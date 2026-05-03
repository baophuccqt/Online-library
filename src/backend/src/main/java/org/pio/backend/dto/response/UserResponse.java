package org.pio.backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String fullName;
//    String password;
    String email;
    String role;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updateAt;
}