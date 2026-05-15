package org.pio.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pio.backend.dto.request.BorrowRecordAddRequest;
import org.pio.backend.dto.response.BorrowRecordResponse;
import org.pio.backend.entity.Book;
import org.pio.backend.entity.BorrowRecord;
import org.pio.backend.entity.BorrowStatus;
import org.pio.backend.entity.User;
import org.pio.backend.exception.AppException;
import org.pio.backend.exception.ErrorCode;
import org.pio.backend.mapper.BorrowRecordMapper;
import org.pio.backend.repository.BookRepository;
import org.pio.backend.repository.BorrowRecordRepository;
import org.pio.backend.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BorrowRecordServiceTest {
    @Mock BorrowRecordRepository borrowRecordRepository;
    @Mock BookRepository bookRepository;
    @Mock UserRepository userRepository;
    @Mock BorrowRecordMapper borrowRecordMapper;
    @InjectMocks BorrowRecordService borrowRecordService;

    User user;
    Book book;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .email("u@test.com")
                .role("USER")
                .build();

        book = Book.builder()
                .id(10L)
                .title("Clean Code")
                .availableCopies(2)
                .build();
    }

    // 4 tests for borrow

    @Test
    public void borrow_success_decrementsCopiesAndSaves() {
        var request = new BorrowRecordAddRequest();
        request.setBookId(10L);

        when(bookRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(book));
        when(userRepository.findByEmail("u@test.com")).thenReturn(Optional.of(user));
        when(borrowRecordRepository.existsByUserAndBookAndStatus(user, book, BorrowStatus.BORROWED)).thenReturn(Boolean.FALSE);
        when(borrowRecordRepository.save(any(BorrowRecord.class))).thenAnswer(inv -> inv.getArgument(0));
        when(borrowRecordMapper.toBorrowRecordResponse(any())).thenReturn(new BorrowRecordResponse());

        borrowRecordService.borrow(request, "u@test.com");

        assertThat(book.getAvailableCopies()).isEqualTo(1);
        verify(borrowRecordRepository).save(argThat(r ->
                r.getStatus() == BorrowStatus.BORROWED && r.getDueDate().isAfter(r.getBorrowDate()
                )));
    }

    @Test
    public void borrow_bookNotFound_throws() {
        var request = new BorrowRecordAddRequest();
        request.setBookId(99L);
        when(bookRepository.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowRecordService.borrow(request, "u@test.com"))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.BOOK_NOT_FOUND);
    }

    @Test
    public void borrow_noCopiesLeft_throws() {
        book.setAvailableCopies(0);
        var request = new BorrowRecordAddRequest();
        request.setBookId(10L);

        when(bookRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(book));
        when(userRepository.findByEmail("u@test.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> borrowRecordService.borrow(request, "u@test.com"))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException)e).getErrorCode())
                .isEqualTo(ErrorCode.BOOK_NOT_AVAILABLE);
    }

    @Test
    public void borrow_alreadyBorrowed_throws() {
        var request = new BorrowRecordAddRequest();
        request.setBookId(10L);

        when(bookRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(book));
        when(userRepository.findByEmail("u@test.com")).thenReturn(Optional.of(user));
        when(borrowRecordRepository.existsByUserAndBookAndStatus(user, book, BorrowStatus.BORROWED)).thenReturn(Boolean.TRUE);

        assertThatThrownBy(() -> borrowRecordService.borrow(request, "u@test.com"))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException)e).getErrorCode())
                .isEqualTo(ErrorCode.BOOK_ALREADY_BORROWED);
    }


    // test for admin/owner
    @Test
    public void getById_notOwnerNotAdmin_throwsUnauthorized() {
        var record = BorrowRecord.builder()
                .id(5L)
                .user(User.builder().id(2L).email("other@test.com").build())
                .build();

        when(borrowRecordRepository.findById(5L)).thenReturn(Optional.of(record));
        when(userRepository.findByEmail("u@test.com")).thenReturn(Optional.of(user)); // role USER

        assertThatThrownBy(() -> borrowRecordService.getBorrowRecordById(5L, "u@test.com"))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException)e).getErrorCode())
                .isEqualTo(ErrorCode.UNAUTHORIZED);
    }

    // tests for returnBook()
    @Test
    public void returnBook_alreadyReturned_throws() {
        var record  = BorrowRecord.builder()
                .id(5L)
                .status(BorrowStatus.RETURNED)
                .book(book)
                .build();

        when(borrowRecordRepository.findById(5L)).thenReturn(Optional.of(record));

        assertThatThrownBy(() -> borrowRecordService.returnBook(5L))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException)e).getErrorCode())
                .isEqualTo(ErrorCode.BOOK_ALREADY_RETURNED);
    }

    @Test
    public void returnBook_success_incrementsCopiesAndSetsReturned() {
        book.setAvailableCopies(1);
        var record =  BorrowRecord.builder()
                .id(5L)
                .status(BorrowStatus.BORROWED)
                .book(book)
                .build();

        when (borrowRecordRepository.findById(5L)).thenReturn(Optional.of(record));
        when (bookRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(book));

        when (borrowRecordRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when (borrowRecordMapper.toBorrowRecordResponse(any())).thenReturn(new BorrowRecordResponse());

        borrowRecordService.returnBook(5L);

        assertThat(record.getStatus()).isEqualTo(BorrowStatus.RETURNED);
        assertThat(record.getReturnDate()).isNotNull();
        assertThat(book.getAvailableCopies()).isEqualTo(2);
    }
}
