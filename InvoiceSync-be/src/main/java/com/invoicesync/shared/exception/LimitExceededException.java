package com.invoicesync.shared.exception;

public class LimitExceededException extends RuntimeException {
  public LimitExceededException(String message) {
    super(message);
  }
}
