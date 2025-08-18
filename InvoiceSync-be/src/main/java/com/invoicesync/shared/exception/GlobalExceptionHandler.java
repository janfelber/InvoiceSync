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

  @ExceptionHandler(EmailAlreadySubscribedException.class)
  public ResponseEntity<String> handleEmailAlreadySubscribedException(final EmailAlreadySubscribedException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }

  @ExceptionHandler(FeatureMissingException.class)
  public ResponseEntity<String> handleFeatureMissingException(final FeatureMissingException ex) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(ex.getMessage());
  }
}
