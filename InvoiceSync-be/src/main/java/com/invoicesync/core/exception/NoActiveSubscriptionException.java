package com.invoicesync.core.exception;

public class NoActiveSubscriptionException extends RuntimeException {

  public NoActiveSubscriptionException(String message) {
    super(message);
  }

}
