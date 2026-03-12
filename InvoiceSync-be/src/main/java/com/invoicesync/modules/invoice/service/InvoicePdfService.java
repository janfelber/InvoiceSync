package com.invoicesync.modules.invoice.service;

import com.invoicesync.modules.invoice.model.InvoiceResponse;

public interface InvoicePdfService {

  byte[] generateInvoicePdf(InvoiceResponse invoiceId);

}
