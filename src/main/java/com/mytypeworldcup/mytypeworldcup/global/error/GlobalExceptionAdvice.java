package com.mytypeworldcup.mytypeworldcup.global.error;

import com.mytypeworldcup.mytypeworldcup.global.auth.exception.AuthExceptionCode;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionAdvice {
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handle(BusinessLogicException e) {
        final ErrorResponse errorResponse = ErrorResponse.of(e.getExceptionCode());
        return new ResponseEntity(errorResponse, HttpStatusCode.valueOf(e.getExceptionCode().getStatus()));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handle(ExpiredJwtException ex) {
        final ErrorResponse errorResponse = ErrorResponse.of(AuthExceptionCode.REFRESH_TOKEN_EXPIRED);
        return ResponseEntity.status(AuthExceptionCode.REFRESH_TOKEN_EXPIRED.getStatus()).body(errorResponse);
    }

}
