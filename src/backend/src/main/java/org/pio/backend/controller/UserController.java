package org.pio.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pio.backend.dto.request.UserAddRequest;
import org.pio.backend.dto.request.UserUpdateRequest;
import org.pio.backend.dto.response.ApiResponse;
import org.pio.backend.dto.response.UserResponse;
import org.pio.backend.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(id))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/myinfo")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
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
                .message("successfully deleted")
                .build();
    }
}
