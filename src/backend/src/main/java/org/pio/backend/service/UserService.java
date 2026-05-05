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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        Long id = Long.parseLong(context.getAuthentication().getName());

        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        ));
    }

    public UserResponse getUserById(Long id) {
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        ));
    }

    public UserResponse getUserByEmail(String email) {
        return userMapper.toUserResponse(userRepository.findByEmail(email).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        ));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(user -> userMapper.toUserResponse(user)).toList();
    }

    public UserResponse createUser(UserAddRequest request) {
        User newUser = userMapper.toUser(request);
        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // set these values by default like this bc it makes sense
        newUser.setRole("USER");
        newUser.setIsActive(true);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        return userMapper.toUserResponse(userRepository.save(newUser));
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User currentUser = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        );

        userMapper.updateUser(currentUser, request);
        return userMapper.toUserResponse(userRepository.save(currentUser));
    }

    public void deleteUser(Long id) {
        User user =  userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        );
        userRepository.delete(user);
    }

}
