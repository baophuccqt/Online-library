package org.pio.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pio.backend.dto.request.ReviewAddRequest;
import org.pio.backend.dto.request.ReviewUpdateRequest;
import org.pio.backend.entity.Book;
import org.pio.backend.entity.Review;
import org.pio.backend.entity.User;
import org.pio.backend.exception.AppException;
import org.pio.backend.exception.ErrorCode;
import org.pio.backend.mapper.ReviewMapper;
import org.pio.backend.repository.BookRepository;
import org.pio.backend.repository.ReviewRepository;
import org.pio.backend.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    ReviewRepository reviewRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookRepository bookRepository;
    @Mock
    ReviewMapper reviewMapper;
    @InjectMocks
    ReviewService reviewService;

    User owner;     // người tạo review
    User admin;
    User otherUser; // không phải owner, không phải admin
    Book book;
    Review review;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).email
                ("owner@test.com").role("USER").build();
        admin = User.builder().id(2L).email
                ("admin@test.com").role("ADMIN").build();
        otherUser = User.builder().id(3L).email("other@test.com ").role("USER").build();
        book = Book.builder().id(10L).title("CleanCode").build();
        review = Review.builder().id(100L).user(owner).book(book).rating(5).comment("good").build();
    }

    // --- addReview duplicate ---
    @Test
    void addReview_duplicate_throws() {
        var req = new ReviewAddRequest();
        req.setBookId(10L);
        req.setRating(5); req.setComment("x");

        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(owner));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(reviewRepository.existsByUserAndBook(owner, book)).thenReturn(true);

        assertThatThrownBy(() ->
                reviewService.addReview(req, "owner@test.com"))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.REVIEW_ALREADY_EXISTS);
    }

    // --- updateReview not owner ---
    @Test
    void
    updateReview_notOwner_throwsUnauthorized() {
        when(reviewRepository.findById(100L
        )).thenReturn(Optional.of(review));

        assertThatThrownBy(() ->
                reviewService.updateReview(100L, new ReviewUpdateRequest(), "other@test.com"))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.UNAUTHORIZED);

        Mockito.verify(reviewMapper, never()).updateReview(any(), any());
        Mockito.verify(reviewRepository, never()).save(any());
    }

    // --- deleteReview admin deletes other's review ---
    @Test
    void deleteReview_adminDeletesOthersReview_success() {
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review)); // owner=owner@
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));

        reviewService.deleteReview(100L, "admin@test.com");

        Mockito.verify(reviewRepository).delete(review);
    }

    // --- deleteReview normal user deletes other's review → 401 ---
    @Test
    void
    deleteReview_notOwnerNotAdmin_throws() {
        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));

        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherUser));

        assertThatThrownBy(() -> reviewService.deleteReview(100L, "other@test.com"))
                        .isInstanceOf(AppException.class)
                        .extracting(e -> ((AppException) e).getErrorCode())
                        .isEqualTo(ErrorCode.UNAUTHORIZED);

        Mockito.verify(reviewRepository, never()).delete(any());
    }
}
