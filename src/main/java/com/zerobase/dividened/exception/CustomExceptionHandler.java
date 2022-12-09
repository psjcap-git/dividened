package com.zerobase.dividened.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
    
    @ExceptionHandler(AbstractException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(AbstractException ex) {
        ErrorResponse response = ErrorResponse.builder()
                                                .code(ex.getStatusCode())
                                                .message(ex.getMessage())
                                                .build();
        return new ResponseEntity<>(response, HttpStatus.resolve(ex.getStatusCode()));
    }
}
