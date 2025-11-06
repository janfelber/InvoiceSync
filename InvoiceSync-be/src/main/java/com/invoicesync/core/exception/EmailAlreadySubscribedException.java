package com.invoicesync.core.exception;

public class EmailAlreadySubscribedException extends RuntimeException {

  public EmailAlreadySubscribedException(String message) {
    super(message);
  }

}
