package org.pio.backend.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.pio.backend.dto.request.BorrowRecordAddRequest;
import org.pio.backend.dto.response.ApiResponse;
import org.pio.backend.dto.response.BorrowRecordResponse;
import org.pio.backend.service.BorrowRecordService;
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
    public ApiResponse<List<BorrowRecordResponse>> getAllBorrowRecords() {
        return ApiResponse.<List<BorrowRecordResponse>>builder()
                .result(borrowRecordService.getAllBorrowRecords())
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<List<BorrowRecordResponse>> getMyBorrowRecords(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<List<BorrowRecordResponse>>builder()
                .result(borrowRecordService.getMyBorrowRecords(jwt.getSubject()))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<BorrowRecordResponse> getBorrowRecordById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<BorrowRecordResponse>builder()
                .result(borrowRecordService.getBorrowRecordById(id, jwt.getSubject()))
                .build();
    }

    @PostMapping
    public ApiResponse<BorrowRecordResponse> borrow(@RequestBody BorrowRecordAddRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<BorrowRecordResponse>builder()
                .result(borrowRecordService.borrow(request, jwt.getSubject()))
                .build();
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BorrowRecordResponse> returnBook(@PathVariable Long id) {
        return ApiResponse.<BorrowRecordResponse>builder()
                .result(borrowRecordService.returnBook(id))
                .build();
    }
}
