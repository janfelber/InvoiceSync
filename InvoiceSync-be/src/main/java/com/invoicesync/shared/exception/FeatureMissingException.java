package com.invoicesync.shared.exception;

public class FeatureMissingException extends RuntimeException {
  public FeatureMissingException(String message) {
    super(message);
  }
}
