package org.pio.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @NotBlank(message = "fullName can not be empty")
    @Size(min = 2, max = 50, message = "full name should be 2-50 character long")
    String fullName;

    @NotBlank(message = "password can not be empty")
    @Size(min = 6, max = 32, message = "password's length should be 4-32 character long")
    String password;

    @NotBlank(message = "email can not be empty")
    @Email(message = "from user add request: invalid email")
    String email;
}
