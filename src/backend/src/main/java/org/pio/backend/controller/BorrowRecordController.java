package org.pio.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.pio.backend.dto.request.BorrowRecordAddRequest;
import org.pio.backend.dto.response.ApiResponse;
import org.pio.backend.dto.response.BorrowRecordResponse;
import org.pio.backend.service.BorrowRecordService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow-records")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BorrowRecordController {
    BorrowRecordService borrowRecordService;

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<BorrowRecordResponse>> getAllBorrowRecords(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ApiResponse.<Page<BorrowRecordResponse>>builder()
                .result(borrowRecordService.getAllBorrowRecords(pageable))
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<Page<BorrowRecordResponse>> getMyBorrowRecords(@AuthenticationPrincipal Jwt jwt,
                                                                      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.<Page<BorrowRecordResponse>>builder()
                .result(borrowRecordService.getMyBorrowRecords(jwt.getSubject(), pageable))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<BorrowRecordResponse> getBorrowRecordById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<BorrowRecordResponse>builder()
                .result(borrowRecordService.getBorrowRecordById(id, jwt.getSubject()))
                .build();
    }

    @PostMapping
    public ApiResponse<BorrowRecordResponse> borrow(@RequestBody @Valid BorrowRecordAddRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<BorrowRecordResponse>builder()
                .result(borrowRecordService.borrow(request, jwt.getSubject()))
                .build();
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BorrowRecordResponse> returnBook(@PathVariable @NotNull(message = "Invalid bookId") Long id) {
        return ApiResponse.<BorrowRecordResponse>builder()
                .result(borrowRecordService.returnBook(id))
                .build();
    }
}
