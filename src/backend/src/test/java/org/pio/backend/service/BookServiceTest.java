package org.pio.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    BookRepository bookRepository;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    BookMapper bookMapper;
    @InjectMocks
    BookService bookService;

    Book book;
    Category cat1;
    Category cat2;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(1L)
                .title("Clean Code")
                .isbn("978-0132350884")
                .availableCopies(3)
                .build();

        cat1 = Category.builder()
                .id(10L)
                .name("Programming")
                .build();

        cat2 = Category.builder()
                .id(20L)
                .name("Software")
                .build();
    }

    // getBookById

    @Test
    void getBookById_notFound_throws() {
        when (bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(99L))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException)e).getErrorCode())
                .isEqualTo(ErrorCode.BOOK_NOT_FOUND);
    }

    // addBook

    @Test
    void addBook_success_setsCategoriesAndSaves() {
        var request = new BookAddRequest();
        request.setCategoryIds(Set.of(10L, 20L));

        Book mapped = Book.builder().isbn("978-0132350884").build();
        when (bookMapper.toBook(request)).thenReturn(mapped);
        when (bookRepository.existsByIsbn("978-0132350884")).thenReturn(Boolean.FALSE);
        when (categoryRepository.findAllByIdIn(Set.of(10L, 20L))).thenReturn(new HashSet<>(Set.of(cat1, cat2)));
        when (bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));
        when (bookMapper.toBookResponse(any())).thenReturn(new BookResponse());

        bookService.addBook(request);

        verify(bookRepository).save(argThat(b -> b.getCategories().size() == 2 && b.getCategories().contains(cat1) && b.getCategories().contains(cat2)));
    }

    @Test
    void addBook_noCategoryIds_savesWithEmptyCategorySet() {
        var req = new BookAddRequest();
        req.setCategoryIds(null);

        Book mapped = Book.builder().isbn("isbn-x").build();
        when(bookMapper.toBook(req)).thenReturn(mapped);
        when(bookRepository.existsByIsbn("isbn-x")).thenReturn(false);
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookMapper.toBookResponse(any())).thenReturn(new BookResponse());

        bookService.addBook(req);

        verify(categoryRepository, never()).findAllByIdIn(any());

        verify(bookRepository).save(argThat(b -> b.getCategories().isEmpty()));
    }

    @Test
    void addBook_duplicateIsbn_throws() {
        var req = new BookAddRequest();
        Book mapped = Book.builder().isbn("dup").build();
        when(bookMapper.toBook(req)).thenReturn(mapped);
        when(bookRepository.existsByIsbn("dup")).thenReturn(true);

        assertThatThrownBy(() -> bookService.addBook(req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.BOOK_ALREADY_EXISTS);

        verify(bookRepository, never()).save(any());
        verify(categoryRepository, never()).findAllByIdIn(any());
    }

    // ---------- updateBook ----------

    @Test
    void updateBook_notFound_throws() {
        var req = new BookUpdateRequest();
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(99L, req))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.BOOK_NOT_FOUND);

        verify(bookMapper, never()).updateBook(any(), any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void updateBook_success_callsMapperAndSaves() {
        var req = new BookUpdateRequest();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toBookResponse(book)).thenReturn(new BookResponse());

        bookService.updateBook(1L, req);

        verify(bookMapper).updateBook(book, req);
        verify(bookRepository).save(book);
    }

    // ---------- deleteBook ----------

    @Test
    void deleteBook_notFound_throws() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteBook(99L))
                .isInstanceOf(AppException.class)
                .extracting(e -> ((AppException) e).getErrorCode())
                .isEqualTo(ErrorCode.BOOK_NOT_FOUND);

        verify(bookRepository, never()).delete(any());
    }

    @Test
    void deleteBook_success_callsDelete() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        bookService.deleteBook(1L);
        verify(bookRepository).delete(book);
    }
}
