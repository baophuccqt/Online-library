package org.pio.backend.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.pio.backend.entity.User;
import org.pio.backend.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByEmail("admin@library.com").isEmpty()) {
                User user = User.builder()
                        .fullName("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .email("admin@library.com")
                        .role("ADMIN")
                        .isActive(true)
                        .build();

                userRepository.save(user);
                log.warn("admin user has been created with default password: admin123, please change it");
            }
        };
    }
}
