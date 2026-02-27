package com.invoicesync.core.exception;

public class UserNameExists extends RuntimeException {

  public UserNameExists(String message) {
    super(message);
  }

}
