package org.pio.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.pio.backend.dto.request.BookAddRequest;
import org.pio.backend.dto.request.BookUpdateRequest;
import org.pio.backend.dto.response.BookResponse;
import org.pio.backend.entity.Book;
import org.pio.backend.entity.Category;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookResponse toBookResponse(Book book);

    @Mapping(target = "categories", ignore = true)
    Book toBook(BookAddRequest request);

    void updateBook(@MappingTarget Book book, BookUpdateRequest request);

    default Set<String> categoriesToNames(Set<Category> categories) {
        if (categories == null) return null;
        return categories.stream().map(category -> category.getName()).collect(Collectors.toSet());
    }
}
