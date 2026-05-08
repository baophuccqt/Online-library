package org.pio.backend.mapper;

import org.mapstruct.*;
import org.pio.backend.dto.request.ReviewUpdateRequest;
import org.pio.backend.dto.response.ReviewResponse;
import org.pio.backend.entity.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userFullName")
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    ReviewResponse toReviewResponse(Review review);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateReview(@MappingTarget Review review, ReviewUpdateRequest request);
}
