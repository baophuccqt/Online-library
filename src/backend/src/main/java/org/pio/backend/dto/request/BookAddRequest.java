package org.pio.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.ISBN;
import org.hibernate.validator.constraints.URL;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookAddRequest {
    @NotBlank(message = "ISBN should not be blank")
    @ISBN
    String isbn; // International standard book number

    @NotBlank(message = "Title should not be blank")
    String title;

    @NotBlank(message = "Title should not be blank")
    String author;

    @Size(max = 500, message = "Description should be 500-character max")
    String description;

    @URL(message = "Invalid URL provided")
    @Size(max = 500)
    String coverUrl;

    @NotBlank
    @Size(max = 255)
    String publisher;

    @NotNull(message = "Publish year can not be empty")
    @Min(value = 1450, message = "Invalid publish year")
    @Max(value = 2100, message = "Invalid publish year")
    Integer publishYear;

    @Pattern(regexp = "^[a-z]{2}$", message = "Language must be code ISO 639-1 (2 lowercase characters)")
    String language;

    @Min(value = 0) @Max(value = 10000)
    Integer totalCopies;

    @Min(value = 0) @Max(value = 10000)
    Integer availableCopies;

    @Size(max = 20)
    Set<Long> categoryIds;
}
