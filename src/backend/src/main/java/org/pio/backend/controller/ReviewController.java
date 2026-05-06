package org.pio.backend.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.pio.backend.dto.request.ReviewAddRequest;
import org.pio.backend.dto.request.ReviewUpdateRequest;
import org.pio.backend.dto.response.ApiResponse;
import org.pio.backend.dto.response.ReviewResponse;
import org.pio.backend.entity.Review;
import org.pio.backend.repository.ReviewRepository;
import org.pio.backend.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {
    ReviewService reviewService;

    @GetMapping("/api/books/{bookId}/reviews")
    public ApiResponse<Page<ReviewResponse>> getReviewsByBook(@PathVariable Long bookId,
                                                              @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.<Page<ReviewResponse>>builder()
                .result(reviewService.getReviewsByBook(bookId, pageable))
                .build();
    }

    @PostMapping("/api/reviews")
    public ApiResponse<ReviewResponse> addReview(@RequestBody @Valid ReviewAddRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.addReview(request, jwt.getSubject()))
                .build();
    }

    @PutMapping("/api/reviews/{id}")
    public ApiResponse<ReviewResponse> updateReview(@PathVariable Long id, @RequestBody @Valid ReviewUpdateRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.updateReview(id, request, jwt.getSubject()))
                .build();
    }

    @DeleteMapping("/api/reviews/{id}")
    public ApiResponse<Void> deleteReview(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        reviewService.deleteReview(id, jwt.getSubject());
        return ApiResponse.<Void>builder()
                .message("Review has been deleted")
                .build();
    }
}
