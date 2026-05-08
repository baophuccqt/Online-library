package org.pio.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.pio.backend.entity.Book;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class CategoryResponse {
    Long id;
    String name;
    String description;
}