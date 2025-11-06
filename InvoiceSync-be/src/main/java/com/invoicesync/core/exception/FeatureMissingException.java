package com.invoicesync.core.exception;

public class FeatureMissingException extends RuntimeException {
  public FeatureMissingException(String message) {
    super(message);
  }
}
