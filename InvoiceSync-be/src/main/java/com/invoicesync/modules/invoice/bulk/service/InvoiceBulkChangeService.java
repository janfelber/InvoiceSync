package com.invoicesync.modules.invoice.bulk.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.invoicesync.modules.invoice.bulk.enums.InvoiceBulkAction;

public interface InvoiceBulkChangeService {

  void executeBulkChange(InvoiceBulkAction action, String bulkChangeRequest, Authentication connectedUser);

  byte[] bulkDownloadInvoices(List<Long> invoiceIds, Authentication connectedUser);

}
