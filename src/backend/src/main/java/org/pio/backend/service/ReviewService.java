package org.pio.backend.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.pio.backend.dto.request.ReviewAddRequest;
import org.pio.backend.dto.request.ReviewUpdateRequest;
import org.pio.backend.dto.response.ReviewResponse;
import org.pio.backend.entity.Book;
import org.pio.backend.entity.Review;
import org.pio.backend.entity.User;
import org.pio.backend.exception.AppException;
import org.pio.backend.exception.ErrorCode;
import org.pio.backend.mapper.ReviewMapper;
import org.pio.backend.repository.BookRepository;
import org.pio.backend.repository.ReviewRepository;
import org.pio.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class ReviewService {
    ReviewRepository reviewRepository;
    UserRepository userRepository;
    BookRepository bookRepository;
    ReviewMapper reviewMapper;

    public Page<ReviewResponse> getReviewsByBook(Long bookId, Pageable pageable) {
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new AppException(ErrorCode.BOOK_NOT_FOUND)
        );

        return reviewRepository.findAllByBook(book, pageable).map(review -> reviewMapper.toReviewResponse(review));
    }

    @Transactional
    public ReviewResponse addReview(ReviewAddRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );

        Book book = bookRepository.findById(request.getBookId()).orElseThrow(
                () -> new AppException(ErrorCode.BOOK_NOT_FOUND)
        );

        if (reviewRepository.existsByUserAndBook(user, book)) {
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return reviewMapper.toReviewResponse(reviewRepository.save(review));
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest request, String userEmail) {
        Review  review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new AppException(ErrorCode.REVIEW_NOT_FOUND)
        );

        if (!review.getUser().getEmail().equals(userEmail)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        reviewMapper.updateReview(review, request);
        return  reviewMapper.toReviewResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(Long reviewId, String userEmail) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new AppException(ErrorCode.REVIEW_NOT_FOUND)
        );

        User user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND)
        );

        boolean isAdmin = "ADMIN".equals(user.getRole());
        boolean isOwner = review.getUser().getEmail().equals(userEmail);

        if (!isAdmin && !isOwner) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        reviewRepository.delete(review);
    }
}
