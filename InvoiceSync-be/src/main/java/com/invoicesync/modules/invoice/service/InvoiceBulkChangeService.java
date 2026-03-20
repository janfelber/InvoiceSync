package com.invoicesync.modules.invoice.service;

import java.util.List;

import org.springframework.security.core.Authentication;

public interface InvoiceBulkChangeService {

  byte[] bulkDownloadInvoices(List<Long> invoiceIds, Authentication connectedUser);

}
