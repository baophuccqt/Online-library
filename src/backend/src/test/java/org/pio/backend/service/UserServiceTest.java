package org.pio.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pio.backend.dto.request.UserAddRequest;
import org.pio.backend.dto.request.UserUpdateRequest;
import org.pio.backend.dto.response.UserResponse;
import org.pio.backend.entity.User;
import org.pio.backend.exception.AppException;
import org.pio.backend.exception.ErrorCode;
import org.pio.backend.mapper.UserMapper;
import org.pio.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks UserService userService;

    User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = User.builder()
                .id(1L)
                .email("me@test.com")
                .password("hashedOld")
                .role("USER")
                .isActive(true)
                .build();
    }

    // ------------------ createUser --------------------

    @Test
    public void createUser_success_encodesPasswordAndSetsDefaults() {
        var request = new UserAddRequest();
        request.setEmail("new@test.com");
        request.setPassword("plain123");
        request.setFullName("uchiha madara");

        User mapped = User.builder()
                .email("new@test.com")
                .password("plain123")
                .build();

        when (userMapper.toUser(request)).thenReturn(mapped);
        when (userRepository.existsByEmail("new@test.com")).thenReturn(Boolean.FALSE);
        when (passwordEncoder.encode("plain123")).thenReturn("hashedNew");
        when (userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when (userMapper.toUserResponse(any())).thenReturn(new UserResponse());

        userService.createUser(request);

        verify(userRepository).save(argThat(u -> "USER".equals(u.getRole()) && Boolean.TRUE.equals(u.getIsActive()) && "hashedNew".equals(u.getPassword())));
    }

    @Test
    void createUser_duplicateEmail_throws() {
        var request = new UserAddRequest();
        request.setEmail("dup@test.com");
        request.setPassword("x");

        User mapped = User.builder()
                .email("dup@test.com")
                .password("x")
                .build();

        when (userMapper.toUser(request)).thenReturn(mapped);
        when (userRepository.existsByEmail("dup@test.com")).thenReturn(Boolean.TRUE);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException)e).getErrorCode())
                .isEqualTo(ErrorCode.USER_ALREADY_EXISTS);

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    // updateUser
    @Test
    void updateUser_keepingSameEmail_skipsDuplicateCheck() {
        var request = new UserUpdateRequest();
        request.setEmail("me@test.com"); // same as current email

        when (userRepository.findByEmail("me@test.com")).thenReturn(Optional.of(existingUser));
        when (userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when (userMapper.toUserResponse(any())).thenReturn(new UserResponse());

        userService.updateUser("me@test.com", request);

        verify(userRepository, never()).existsByEmail(any());
    }

    @Test
    void updateUser_changingEmailToTaken_throws() {
        var request = new UserUpdateRequest();
        request.setEmail("taken@test.com");

        when (userRepository.findByEmail("me@test.com")).thenReturn(Optional.of(existingUser));
        when (userRepository.existsByEmail("taken@test.com")).thenReturn(Boolean.TRUE);

        assertThatThrownBy(() -> userService.updateUser("me@test.com", request))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException)e).getErrorCode())
                .isEqualTo(ErrorCode.USER_ALREADY_EXISTS);

        verify(userMapper, never()).updateUser(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_passwordNull_doesNotReEncode() {
        var request = new UserUpdateRequest();
        request.setEmail("me@test.com");
        request.setPassword(null);

        when (userRepository.findByEmail("me@test.com")).thenReturn(Optional.of(existingUser));
        when (userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when (userMapper.toUserResponse(any())).thenReturn(new UserResponse());

        userService.updateUser("me@test.com", request);

        verify(passwordEncoder, never()).encode(any());
        assertThat(existingUser.getPassword()).isEqualTo("hashedOld"); // unchanged
    }

    @Test
    void updateUser_passwordProvided_encodesIt() {
        var request = new UserUpdateRequest();
        request.setEmail("me@test.com");
        request.setPassword("newpw");

        when(userRepository.findByEmail("me@test.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newpw")).thenReturn("hashedNew");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toUserResponse(any())).thenReturn(new UserResponse());

        userService.updateUser("me@test.com", request);

        assertThat(existingUser.getPassword()).isEqualTo("hashedNew");
    }
}
