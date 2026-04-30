package org.pio.backend.dto.request;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

// book update request should have the same Id as before
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookUpdateRequest {
    String isbn; // International standard book number
    String title;
    String author;
    String description;
    String cover_url;
    String publisher;
    int publishYear;
    String language;
    int totalCopies;
    int availableCopies;
    LocalDateTime createAt;
    LocalDateTime updateAt;
}
