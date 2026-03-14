package com.invoicesync.core.exception;

public class DownloadDocumentException extends RuntimeException {

  public DownloadDocumentException(String message) {
    super(message);
  }

  public DownloadDocumentException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
