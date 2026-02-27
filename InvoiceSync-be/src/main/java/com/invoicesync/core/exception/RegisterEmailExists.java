package com.invoicesync.core.exception;

public class RegisterEmailExists extends RuntimeException {

  public RegisterEmailExists(String message) {
    super(message);
  }

}
