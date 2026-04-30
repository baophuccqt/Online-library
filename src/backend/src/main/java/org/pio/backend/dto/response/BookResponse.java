package org.pio.backend.dto.response;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Table(name = "Books")
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
    String cover_url;
    String publisher;
    int publishYear;
    String language;
    int totalCopies; // consider to not expose
    int availableCopies;
    LocalDateTime createAt; // consider to not expose
    LocalDateTime updateAt; // consider to not expose
}

