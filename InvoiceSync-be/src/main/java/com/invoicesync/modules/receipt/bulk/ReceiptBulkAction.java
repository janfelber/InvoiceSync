package com.invoicesync.modules.receipt.bulk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoicesync.core.common.BulkActionRequest;
import com.invoicesync.modules.receipt.model.request.BulkDeleteRequest;
import com.invoicesync.modules.receipt.model.request.BulkReassignRequest;

public enum ReceiptBulkAction {

  DELETE("Bulk deleted receipts", BulkDeleteRequest.class),
  COMPANY_REASSIGN("Bulk reassigned receipts to company", BulkReassignRequest.class);

  private final String description;

  private final Class<? extends BulkActionRequest> requestType;

  ReceiptBulkAction(final String description, final Class<? extends BulkActionRequest> requestType) {
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
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Invalid request body for action " + this.name(), e);
    }
  }

}
