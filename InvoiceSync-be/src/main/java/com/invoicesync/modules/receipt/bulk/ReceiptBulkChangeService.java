package com.invoicesync.modules.receipt.bulk;

import org.springframework.security.core.Authentication;

public interface ReceiptBulkChangeService {

  void executeBulkChange(ReceiptBulkAction action, String bulkChangeRequest, Authentication connectedUser);

}
