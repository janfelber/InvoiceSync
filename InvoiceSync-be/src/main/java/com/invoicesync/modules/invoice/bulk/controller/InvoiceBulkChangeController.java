package com.invoicesync.modules.invoice.bulk.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.core.Api;
import com.invoicesync.modules.invoice.bulk.enums.InvoiceBulkAction;
import com.invoicesync.modules.invoice.bulk.service.InvoiceBulkChangeService;

@RestController
@RequestMapping(Api.INVOICE + "/bulk")
public class InvoiceBulkChangeController {

  private final InvoiceBulkChangeService invoiceBulkChangeService;

  public InvoiceBulkChangeController(final InvoiceBulkChangeService invoiceBulkChangeService) {
    this.invoiceBulkChangeService = invoiceBulkChangeService;
  }

  @PostMapping("/bulk-change/execute")
  public ResponseEntity<?> executeBulkChange(@RequestParam InvoiceBulkAction action,
      @RequestBody String bulkChangeRequest, Authentication auth) {
    invoiceBulkChangeService.executeBulkChange(action, bulkChangeRequest, auth);
    return ResponseEntity.noContent().build();
  }

  @PostMapping(Api.INVOICE_BULK_DOWNLOAD)
  public ResponseEntity<byte[]> bulkDownloadInvoices(@RequestBody final List<Long> invoiceIds,
      final Authentication connectedUser) {
    final byte[] zip = invoiceBulkChangeService.bulkDownloadInvoices(invoiceIds, connectedUser);

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType("application/zip"))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"invoices.zip\"")
        .body(zip);
  }

}
