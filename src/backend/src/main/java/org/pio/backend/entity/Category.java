package org.pio.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Data
@Table(name = "categories")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    String name;
    String description;

    @ManyToMany(mappedBy = "categories")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Set<Book> books;
}
