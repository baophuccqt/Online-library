package org.pio.backend.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookAddRequest {
    String isbn; // International standard book number
    String title;
    String author;
    String description;
    String coverUrl;
    String publisher;
    int publishYear;
    String language;
    int totalCopies;
    int availableCopies;

    Set<Long> categoryIds;

//    LocalDateTime createAt;
//    LocalDateTime updatedAt;
    // user should not be able to manually set these
}
