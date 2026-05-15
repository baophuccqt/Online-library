package org.pio.backend.service;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pio.backend.dto.request.AuthenticationRequest;
import org.pio.backend.dto.response.AuthenticationResponse;
import org.pio.backend.entity.User;
import org.pio.backend.exception.AppException;
import org.pio.backend.exception.ErrorCode;
import org.pio.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    AuthenticationService authenticationService;

    // HS512 yêu cầu key ≥ 64 bytes (512bits) —phải đủ dài,không random ngắn
    static final String TEST_SIGNER_KEY = "test-signer-key-for-unit-test-only-must-be-at-least-64-bytes-long-xxxxx";

    User user;

    @BeforeEach
    void setUp() {
        // @Value không được Mockito inject→set tay qua reflection
        ReflectionTestUtils.setField(authenticationService, "SIGNER_KEY", TEST_SIGNER_KEY);

        user = User.builder().id(1L).email("user@test.com").password("hashedPw").role("USER").build();
    }

    // ============================================================
    // CASE BẢO MẬT QUAN TRỌNG NHẤT
    // Email không tồn tại phải throwUNAUTHENTICATED(KHÔNG phải USER_NOT_FOUND)
    // để tránh email enumeration attack.
    // ============================================================
    @Test
    void authenticate_emailNotFound_throwsUnauthenticatedNotUserNotFound() {
        var req = new AuthenticationRequest();
        req.setEmail("ghost@test.com");
        req.setPassword("any");

        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.authenticate(req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.UNAUTHENTICATED)
                .isNotEqualTo(ErrorCode.USER_NOT_FOUND); // chống regression

        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void authenticate_wrongPassword_throwsUnauthenticated() {
        var req = new AuthenticationRequest();
        req.setEmail("user@test.com");
        req.setPassword("wrong");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashedPw")).thenReturn(false);

        assertThatThrownBy(() -> authenticationService.authenticate(req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.UNAUTHENTICATED);
    }

    @Test
    void authenticate_success_returnsValidSignedTokenAndAuthenticatedTrue() throws Exception {
        var req = new AuthenticationRequest();
        req.setEmail("user@test.com");
        req.setPassword("correctPw");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correctPw", "hashedPw")).thenReturn(true);

        AuthenticationResponse response = authenticationService.authenticate(req);

        assertThat(response.isAuthenticated()).isTrue();
        assertThat(response.getToken()).isNotBlank();

        // Parse token để verify chữ ký +claims(chứng minh JWT thật, không phải string ngẫu nhiên)
        SignedJWT jwt = SignedJWT.parse(response.getToken());
        JWSVerifier verifier = new MACVerifier(TEST_SIGNER_KEY.getBytes());

        assertThat(jwt.verify(verifier)).isTrue();
        assertThat(jwt.getJWTClaimsSet().getSubject()).isEqualTo("user@test.com");
        assertThat(jwt.getJWTClaimsSet().getStringClaim("role")).isEqualTo("USER");
        assertThat(jwt.getJWTClaimsSet().getIssuer()).isEqualTo("baophuccqt");
        assertThat(jwt.getJWTClaimsSet().getExpirationTime()).isAfter(new java.util.Date());
    }
}