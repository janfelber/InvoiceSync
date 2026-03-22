package com.invoicesync.modules.receipt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.core.Api;
import com.invoicesync.modules.receipt.bulk.ReceiptBulkAction;
import com.invoicesync.modules.receipt.bulk.ReceiptBulkChangeService;

@RestController
@RequestMapping(Api.RECEIPT + "/bulk")
public class ReceiptBulkChangeController {

  private final ReceiptBulkChangeService receiptBulkChangeService;

  public ReceiptBulkChangeController(final ReceiptBulkChangeService receiptBulkChangeService) {
    this.receiptBulkChangeService = receiptBulkChangeService;
  }

  @PostMapping("/bulk-change/execute")
  public ResponseEntity<?> executeBulkChange(@RequestParam ReceiptBulkAction action,
      @RequestBody String bulkChangeRequest, Authentication auth) {
    receiptBulkChangeService.executeBulkChange(action, bulkChangeRequest, auth);
    return ResponseEntity.noContent().build();
  }

}
