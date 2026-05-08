package org.pio.backend.exception;

import org.pio.backend.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse> handleAppException(AppException e) {
        ApiResponse response = new ApiResponse();
        ErrorCode errorCode = e.getErrorCode();

        response.setMessage(errorCode.getMessage());
        response.setCode(errorCode.getCode());

        return ResponseEntity.status(errorCode.getStatusCode()).body(response);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(
                ApiResponse.builder().code(400).message(message).build()
        );
    }
}
