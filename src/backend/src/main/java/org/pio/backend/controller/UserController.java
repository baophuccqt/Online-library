package org.pio.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pio.backend.dto.request.UserAddRequest;
import org.pio.backend.dto.request.UserUpdateRequest;
import org.pio.backend.dto.response.ApiResponse;
import org.pio.backend.dto.response.UserResponse;
import org.pio.backend.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // btw this is actually registering, anyone can do it
    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserAddRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(id))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<UserResponse>> getAllUsers(@PageableDefault Pageable pageable) {
        return ApiResponse.<Page<UserResponse>>builder()
                .result(userService.getAllUsers(pageable))
                .build();
    }

    @GetMapping("/myinfo")
    public ApiResponse<UserResponse> getMyInfo(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo(jwt.getSubject()))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void>  deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.<Void>builder()
                .message("User has been deleted")
                .build();
    }
}
