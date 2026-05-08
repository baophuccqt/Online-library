package org.pio.backend.controller;

import lombok.RequiredArgsConstructor;
import org.pio.backend.dto.request.BookAddRequest;
import org.pio.backend.dto.request.BookUpdateRequest;
import org.pio.backend.dto.response.ApiResponse;
import org.pio.backend.dto.response.BookResponse;
import org.pio.backend.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
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
    public ApiResponse<BookResponse> getBookById(@PathVariable Long id) {
        return ApiResponse.<BookResponse>builder()
                .result(bookService.getBookById(id))
                .build();
    }

    @GetMapping
    public ApiResponse<Page<BookResponse>> getAllBooks(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.<Page<BookResponse>>builder()
                .result(bookService.getAllBooks(pageable))
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
        return ApiResponse.<Void>builder()
                .message("Book has been deleted")
                .build();
    }
}
