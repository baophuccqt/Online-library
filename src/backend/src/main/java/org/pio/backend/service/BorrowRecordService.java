package org.pio.backend.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class BorrowRecordService {
    BorrowRecordRepository borrowRecordRepository;
    BookRepository bookRepository;
    UserRepository userRepository;
    BorrowRecordMapper borrowRecordMapper;

    public Page<BorrowRecordResponse> getAllBorrowRecords(Pageable pageable) {
        return borrowRecordRepository.findAll(pageable).map(borrowRecord -> borrowRecordMapper.toBorrowRecordResponse(borrowRecord));
    }

    public Page<BorrowRecordResponse> getMyBorrowRecords(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        );

        return borrowRecordRepository.findAllByUser(user, pageable).map(borrowRecord -> borrowRecordMapper.toBorrowRecordResponse(borrowRecord));
    }

    public BorrowRecordResponse getBorrowRecordById(Long id, String userEmail) {
        BorrowRecord record = borrowRecordRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RECORD_NOT_EXIST)
        );

        User user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        );

        boolean isAdmin = "ADMIN".equals(user.getRole());
        boolean isOwner = record.getUser().getEmail().equals(userEmail);

        if (!isAdmin && !isOwner) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return borrowRecordMapper.toBorrowRecordResponse(record);
    }

    @Transactional
    public BorrowRecordResponse borrow(BorrowRecordAddRequest request, String userEmail) {
        Book book = bookRepository.findById(request.getBookId()).orElseThrow(
                () -> new AppException(ErrorCode.BOOK_NOT_EXIST)
        );

        User user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        );

        if (book.getAvailableCopies() <= 0) {
            throw new AppException(ErrorCode.BOOK_NOT_AVAILABLE);
        }

        if (borrowRecordRepository.existsByUserAndBookAndStatus(
                user, book, BorrowStatus.BORROWED)) {
            throw new AppException(ErrorCode.ALREADY_BORROWED);
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);

        LocalDateTime now = LocalDateTime.now();
        BorrowRecord record = BorrowRecord.builder()
                .user(user)
                .book(book)
                .borrowDate(now)
                .dueDate(now.plusDays(14))
                .status(BorrowStatus.BORROWED)
                .build();

        return borrowRecordMapper.toBorrowRecordResponse(borrowRecordRepository.save(record));
    }

    @Transactional
    public BorrowRecordResponse returnBook(Long id) {
        BorrowRecord record = borrowRecordRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.RECORD_NOT_EXIST)
        );

        if (record.getStatus() == BorrowStatus.RETURNED) {
            throw new AppException(ErrorCode.BOOK_ALREADY_RETURNED);
        }

        record.setReturnDate(LocalDateTime.now());
        record.setStatus(BorrowStatus.RETURNED);
        record.getBook().setAvailableCopies(record.getBook().getAvailableCopies() + 1);

        return borrowRecordMapper.toBorrowRecordResponse(borrowRecordRepository.save(record));
    }
}
