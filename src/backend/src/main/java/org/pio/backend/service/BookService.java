package org.pio.backend.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.pio.backend.dto.request.BookAddRequest;
import org.pio.backend.dto.request.BookUpdateRequest;
import org.pio.backend.dto.response.BookResponse;
import org.pio.backend.entity.Book;
import org.pio.backend.entity.Category;
import org.pio.backend.exception.AppException;
import org.pio.backend.exception.ErrorCode;
import org.pio.backend.mapper.BookMapper;
import org.pio.backend.repository.BookRepository;
import org.pio.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookService {
    BookRepository bookRepository;
    CategoryRepository categoryRepository;
    BookMapper bookMapper;

    public BookResponse getBook(Long id) {
        return bookMapper.toBookResponse(bookRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.BOOK_NOT_EXIST)));
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream().map(book -> bookMapper.toBookResponse(book)).toList();
    }

    @Transactional
    public BookResponse addBook(BookAddRequest request) {
        Book newBook = bookMapper.toBook(request);

        boolean exists = bookRepository.existsByIsbn(newBook.getIsbn());
        if (exists) {
            throw new AppException(ErrorCode.BOOK_EXISTED);
        }

        Set<Category> categories = new HashSet<>();
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            categories = categoryRepository.findAllByIdIn(request.getCategoryIds());
        }

        newBook.setCategories(categories);

        return bookMapper.toBookResponse(bookRepository.save(newBook));
    }

    public BookResponse updateBook(Long id, BookUpdateRequest request) {
        Book currentBook = bookRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.BOOK_NOT_EXIST));

        bookMapper.updateBook(currentBook, request);
        return bookMapper.toBookResponse(bookRepository.save(currentBook));
    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.BOOK_NOT_EXIST)
        );
        bookRepository.delete(book);
    }

    // suicide
    public void deleteAllBooks() {
        bookRepository.deleteAll();
    }
}
