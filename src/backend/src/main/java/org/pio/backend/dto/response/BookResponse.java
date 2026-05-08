package org.pio.backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookResponse {
    private Long id;
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
    LocalDateTime createdAt; // consider to not expose later
    LocalDateTime updatedAt; // consider to not expose later

    Set<String> categories;
}

