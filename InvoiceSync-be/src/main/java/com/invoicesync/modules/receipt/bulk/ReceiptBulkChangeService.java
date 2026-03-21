package com.invoicesync.modules.receipt.bulk;

import java.util.List;

import org.springframework.security.core.Authentication;

public interface ReceiptBulkChangeService {

  void executeBulkChange(ReceiptBulkAction action, List<Long> receiptIds, Authentication connectedUser);

}
