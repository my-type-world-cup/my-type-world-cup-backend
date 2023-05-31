package com.mytypeworldcup.mytypeworldcup.global.error;

import com.mytypeworldcup.mytypeworldcup.global.auth.exception.AuthExceptionCode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionAdvice {
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogicException(BusinessLogicException e) {
        final ErrorResponse errorResponse = ErrorResponse.of(e.getExceptionCode());
        return ResponseEntity.status(e.getExceptionCode().getStatus()).body(errorResponse);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException ex) {
        final ErrorResponse errorResponse = ErrorResponse.of(AuthExceptionCode.REFRESH_TOKEN_EXPIRED);
        return ResponseEntity.status(AuthExceptionCode.REFRESH_TOKEN_EXPIRED.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        final ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.BAD_REQUEST, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
