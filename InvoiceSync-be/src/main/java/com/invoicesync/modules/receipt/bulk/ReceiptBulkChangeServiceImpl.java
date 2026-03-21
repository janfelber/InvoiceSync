package com.invoicesync.modules.receipt.bulk;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.modules.activity.service.UserActivityService;
import com.invoicesync.modules.receipt.service.ReceiptService;

@Service
public class ReceiptBulkChangeServiceImpl implements ReceiptBulkChangeService {

  private final ReceiptService receiptService;

  private final UserActivityService userActivityService;

  public ReceiptBulkChangeServiceImpl(final ReceiptService receiptService,
      final UserActivityService userActivityService) {
    this.receiptService = receiptService;
    this.userActivityService = userActivityService;
  }

  @Override
  public void executeBulkChange(final ReceiptBulkAction action, final List<Long> receiptIds,
      final Authentication connectedUser) {
    switch (action) {
      case DELETE -> deleteReceipts(receiptIds, connectedUser);
    }
  }

  private void deleteReceipts(final List<Long> ids, final Authentication connectedUser) {
    ids.forEach(id -> receiptService.deleteReceiptById(id, connectedUser));
  }

}
