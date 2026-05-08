package org.pio.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.pio.backend.dto.request.BorrowRecordAddRequest;
import org.pio.backend.dto.response.BorrowRecordResponse;
import org.pio.backend.entity.BorrowRecord;

@Mapper(componentModel = "spring")
public interface BorrowRecordMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "book.id", target = "bookId")
    BorrowRecordResponse toBorrowRecordResponse(BorrowRecord borrowRecord);
}
