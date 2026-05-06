package org.pio.backend.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.pio.backend.dto.request.UserAddRequest;
import org.pio.backend.dto.request.UserUpdateRequest;
import org.pio.backend.dto.response.UserResponse;
import org.pio.backend.entity.User;
import org.pio.backend.exception.AppException;
import org.pio.backend.exception.ErrorCode;
import org.pio.backend.mapper.UserMapper;
import org.pio.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse getMe(String userEmail) {
        return userMapper.toUserResponse(userRepository.findByEmail(userEmail).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND)
        ));
    }

    public UserResponse getUserById(Long id) {
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND)
        ));
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(user -> userMapper.toUserResponse(user));
    }

    @Transactional
    public UserResponse createUser(UserAddRequest request) {
        User newUser = userMapper.toUser(request);
        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // set these values by default like this bc it makes sense
        newUser.setRole("USER");
        newUser.setIsActive(true);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        return userMapper.toUserResponse(userRepository.save(newUser));
    }

    @Transactional
    public UserResponse updateUser(String userEmail, UserUpdateRequest request) {
        User currentUser = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );

        if (request.getEmail() != null
                && !request.getEmail().equals(currentUser.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        userMapper.updateUser(currentUser, request);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userMapper.toUserResponse(userRepository.save(currentUser));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user =  userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );
        userRepository.delete(user);
    }

}
