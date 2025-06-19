package com.invoicesync.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(LimitExceededException.class)
  public ResponseEntity<String> handleLimitExceededException(LimitExceededException ex) {
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ex.getMessage());
  }
}
