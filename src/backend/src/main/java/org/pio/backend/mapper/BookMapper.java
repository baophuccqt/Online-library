package org.pio.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.pio.backend.dto.request.BookAddRequest;
import org.pio.backend.dto.request.BookUpdateRequest;
import org.pio.backend.dto.response.BookResponse;
import org.pio.backend.entity.Book;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "totalCopies", ignore = true)
    BookResponse toBookResponse(Book book);

    Book toBook(BookAddRequest request);

    Book toBook(BookUpdateRequest request);

    void updateBook(@MappingTarget Book book, BookUpdateRequest request);
}
