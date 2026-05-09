package org.pio.backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.pio.backend.entity.BorrowStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowRecordResponse {
    Long id;

    Long userId;
    Long bookId;

    LocalDateTime borrowDate;
    LocalDateTime dueDate;
    LocalDateTime returnDate;

    BorrowStatus status;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
