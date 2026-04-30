package org.pio.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Books")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
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
