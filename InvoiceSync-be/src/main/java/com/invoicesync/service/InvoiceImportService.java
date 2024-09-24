package com.invoicesync.service;

import com.invoicesync.module.InvoiceImport;

public interface InvoiceImportService {

  InvoiceImport saveInvoice(InvoiceImport invoice);
}
