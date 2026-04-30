package org.pio.backend.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.pio.backend.dto.request.BookAddRequest;
import org.pio.backend.dto.request.BookUpdateRequest;
import org.pio.backend.dto.response.BookResponse;
import org.pio.backend.entity.Book;
import org.pio.backend.exception.AppException;
import org.pio.backend.exception.ErrorCode;
import org.pio.backend.mapper.BookMapper;
import org.pio.backend.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookService {
    BookRepository bookRepository;
    BookMapper bookMapper;

    public BookResponse getBookById(Long id) {
        return bookMapper.toBookResponse(bookRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)));
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream().map(book -> bookMapper.toBookResponse(book)).toList();
    }

    public BookResponse addBook(BookAddRequest request) {
        Book newBook = bookMapper.toBook(request);

        boolean exists = bookRepository.existsById(newBook.getId());
        if (exists) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return bookMapper.toBookResponse(bookRepository.save(newBook));
    }

    public BookResponse updateBook(Long id, BookUpdateRequest request) {
        Book currentBook = bookRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        bookMapper.updateBook(currentBook, request);

        return bookMapper.toBookResponse(bookRepository.save(currentBook));
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );
        bookRepository.delete(book);
    }

    // suicide
    public void deleteAllBooks() {
        bookRepository.deleteAll();
    }
}
