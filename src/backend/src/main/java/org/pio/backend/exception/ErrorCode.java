package org.pio.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

// chỉ dùng được getter vì chúng là enums, không thay đổi giá trị được để mà setter
@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_ERROR_KEY(1001, "Uncategorized exception", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User already exists", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username has to be at least 3-character long", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password should be at least 8-character long", HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission! Fuck off", HttpStatus.FORBIDDEN),
    BAD_TOKEN(1008, "bad token provided",  HttpStatus.BAD_REQUEST),
    BAD_PARSE(1009, "bad parse happened", HttpStatus.BAD_REQUEST),
    ;

    private int code; // by default, 1000 is succesful
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
