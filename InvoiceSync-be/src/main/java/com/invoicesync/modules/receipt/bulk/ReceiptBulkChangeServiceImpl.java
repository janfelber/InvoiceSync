package com.invoicesync.modules.receipt.bulk;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoicesync.core.common.BulkActionRequest;
import com.invoicesync.modules.activity.service.UserActivityService;
import com.invoicesync.modules.receipt.model.request.BulkDeleteRequest;
import com.invoicesync.modules.receipt.model.request.BulkReassignRequest;
import com.invoicesync.modules.receipt.service.ReceiptService;

@Service
public class ReceiptBulkChangeServiceImpl implements ReceiptBulkChangeService {

  private final ObjectMapper objectMapper;

  private final ReceiptService receiptService;

  private final UserActivityService userActivityService;

  public ReceiptBulkChangeServiceImpl(final ObjectMapper objectMapper,
      final ReceiptService receiptService,
      final UserActivityService userActivityService) {
    this.objectMapper = objectMapper;
    this.receiptService = receiptService;
    this.userActivityService = userActivityService;
  }

  @Override
  public void executeBulkChange(final ReceiptBulkAction action, String bulkChangeRequest,
      final Authentication connectedUser) {
    BulkActionRequest request = action.deserialize(bulkChangeRequest, objectMapper);
    switch (action) {
      case DELETE -> deleteReceipts((BulkDeleteRequest) request, connectedUser);
      case COMPANY_REASSIGN -> reassignCompanyReceipts((BulkReassignRequest) request, connectedUser);
      default -> throw new IllegalArgumentException("Unsupported bulk action: " + action);
    }
  }

  private void deleteReceipts(BulkDeleteRequest deleteRequest, final Authentication connectedUser) {
    deleteRequest.ids().forEach(id -> receiptService.deleteReceiptById(id, connectedUser));
  }

  private void reassignCompanyReceipts(BulkReassignRequest reassignRequest, final Authentication connectedUser) {
    reassignRequest.ids().forEach(id -> receiptService.reassignReceipt(id, reassignRequest.companyId(), connectedUser));
  }

}
