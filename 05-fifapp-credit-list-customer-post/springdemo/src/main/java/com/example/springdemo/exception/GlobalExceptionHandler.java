package com.example.springdemo.exception;

import com.example.springdemo.utils.ApiResponse;
import com.example.springdemo.utils.FieldErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
            List<FieldErrorResponse> errors = ex.getBindingResult().getFieldErrors().stream()
                    .map(e -> FieldErrorResponse.builder()
                            .field(e.getField())
                            .message(e.getDefaultMessage())
                            .build())
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("VALIDATION_ERROR", "Invalid request", errors));
        }
}
