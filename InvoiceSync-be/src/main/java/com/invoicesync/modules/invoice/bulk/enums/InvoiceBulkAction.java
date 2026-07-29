package com.invoicesync.modules.invoice.bulk.enums;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.invoicesync.core.common.BulkActionRequest;
import com.invoicesync.modules.receipt.model.request.BulkDeleteRequest;

public enum InvoiceBulkAction {

  DELETE("Bulk deleted invoices", BulkDeleteRequest.class);

  private final String description;

  private final Class<? extends BulkActionRequest> requestType;

  InvoiceBulkAction(final String description, final Class<? extends BulkActionRequest> requestType) {
    this.description = description;
    this.requestType = requestType;
  }

  public String getDescription() {
    return description;
  }

  public Class<? extends BulkActionRequest> getRequestType() {
    return requestType;
  }

  public <T extends BulkActionRequest> T deserialize(String rawBody, ObjectMapper mapper) {
    try {
      return (T) mapper.readValue(rawBody, requestType);
    } catch (JacksonException e) {
      throw new IllegalArgumentException("Invalid request body for action " + this.name(), e);
    }
  }
}
