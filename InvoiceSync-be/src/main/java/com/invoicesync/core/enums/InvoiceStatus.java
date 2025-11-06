package com.invoicesync.core.enums;

import lombok.Getter;

@Getter
public enum InvoiceStatus {

  UNPROCESSED("Unprocessed"),
  PROCESSED("Processed"),
  ERROR("Error");

  private final String label;

  InvoiceStatus(final String label) {
    this.label = label;
  }

}
