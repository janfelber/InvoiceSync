package com.invoicesync.modules.receipt.bulk;

public enum ReceiptBulkAction {

  DELETE("Bulk deleted receipts");

  private final String description;

  ReceiptBulkAction(final String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

}
