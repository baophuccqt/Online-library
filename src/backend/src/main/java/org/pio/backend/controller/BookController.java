package org.pio.backend.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.pio.backend.dto.request.BookAddRequest;
import org.pio.backend.dto.request.BookUpdateRequest;
import org.pio.backend.dto.response.ApiResponse;
import org.pio.backend.dto.response.BookResponse;
import org.pio.backend.service.BookService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BookResponse> addBook(@RequestBody BookAddRequest request) {
        return ApiResponse.<BookResponse>builder()
                .result(bookService.addBook(request))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<BookResponse> getBook(@PathVariable Long id) {
        return ApiResponse.<BookResponse>builder()
                .result(bookService.getBook(id))
                .build();
    }

    @GetMapping
    public ApiResponse<List<BookResponse>> getAllBooks() {
        return ApiResponse.<List<BookResponse>>builder()
                .result(bookService.getAllBooks())
                .build();
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BookResponse> updateBook(@PathVariable Long id, @RequestBody BookUpdateRequest request) {
        return ApiResponse.<BookResponse>builder()
                .result(bookService.updateBook(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ApiResponse.<Void>builder().build();
    }
}
