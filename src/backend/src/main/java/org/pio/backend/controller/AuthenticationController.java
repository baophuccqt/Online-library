package org.pio.backend.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.pio.backend.dto.request.AuthenticationRequest;
import org.pio.backend.dto.response.ApiResponse;
import org.pio.backend.dto.response.AuthenticationResponse;
import org.pio.backend.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.authenticate(authenticationRequest))
                .build();
    }
}
