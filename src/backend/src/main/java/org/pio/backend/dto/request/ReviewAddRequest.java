package org.pio.backend.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewAddRequest {
    @NotNull
    Long bookId;

    @NotNull
    @Min(value = 0, message = "rating must be at least 0")
    @Max(value = 5, message = "rating must be at most 5")
    Integer rating;

    @Size(max = 2000)
    String comment;
}
