package com.invoicesync.core.enums;

import lombok.Getter;

@Getter
public enum InvoiceType {

  RECEIVED("Received"),

  ISSUED("Issued");

  private final String label;

  InvoiceType(final String label) {
    this.label = label;
  }
}
