package org.pio.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "full_name")
    String fullName;
    @Column(name = "password_hash")
    String password;

    @Column(unique = true)
    String email;
    String role;

    @Column(name = "is_active")
    @ColumnDefault("true")
    Boolean isActive;

    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;
    @Column(name = "updated_at")
    LocalDateTime updateAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}
