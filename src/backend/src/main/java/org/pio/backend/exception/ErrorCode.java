package org.pio.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

// chỉ dùng được getter vì chúng là enums, không thay đổi giá trị được để mà setter
@Getter
public enum ErrorCode {
    // 1xxx — general / system
    UNCATEGORIZED_EXCEPTION(1000, "An unexpected error has occurred", HttpStatus.INTERNAL_SERVER_ERROR),

    // 11xx — authentication / authorization
    UNAUTHENTICATED(1101, "Authentication failed: invalid email or password", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1102, "You do not have permission to perform this action", HttpStatus.FORBIDDEN),

    // 12xx — user
    USER_NOT_FOUND(1201, "User does not exist", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS(1202, "A user with this email already exists", HttpStatus.CONFLICT),

    // 13xx — book
    BOOK_NOT_FOUND(1301, "Book does not exist", HttpStatus.NOT_FOUND),
    BOOK_ALREADY_EXISTS(1302, "A book with this ISBN already exists", HttpStatus.CONFLICT),
    BOOK_NOT_AVAILABLE(1303, "This book has no available copies", HttpStatus.CONFLICT),

    // 14xx — category
    CATEGORY_NOT_FOUND(1401, "Category does not exist", HttpStatus.NOT_FOUND),

    // 15xx — borrow records
    BORROW_RECORD_NOT_FOUND(1501, "Borrow record does not exist", HttpStatus.NOT_FOUND),
    BOOK_ALREADY_BORROWED(1502, "You have already borrowed this book", HttpStatus.CONFLICT),
    BOOK_ALREADY_RETURNED(1503, "This book has already been returned", HttpStatus.CONFLICT),

    // 16xx — reviews
    REVIEW_NOT_FOUND(1601, "Review does not exist", HttpStatus.NOT_FOUND),
    REVIEW_ALREADY_EXISTS(1602, "You have already reviewed this book", HttpStatus.CONFLICT),
    ;

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
