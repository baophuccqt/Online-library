package org.pio.backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {
    Long id;

    Long userId;
    String userFullName;

    Long bookId;
    String bookTitle;

    int rating;
    String comment;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
