package com.invoicesync.shared.exception;

public class EmailAlreadySubscribedException extends RuntimeException {

  public EmailAlreadySubscribedException(String message) {
    super(message);
  }

}
