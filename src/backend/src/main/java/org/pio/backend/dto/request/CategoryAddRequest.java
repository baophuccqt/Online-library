package org.pio.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class CategoryAddRequest {
    @NotBlank(message = "Category's name should not be blank")
    String name;

    @NotBlank(message = "Category's description should not be blank")
    @Size(min = 10, message = "Please give some more description")
    String description;
}