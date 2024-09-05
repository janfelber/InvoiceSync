package com.invoicesync.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoicesync.module.InvoiceImport;
import com.invoicesync.repository.InvoiceImportRepository;

@Service
public class InvoiceImportServiceImpl implements InvoiceImportService {

  private final InvoiceImportRepository invoiceImportRepository;

  @Autowired
  public InvoiceImportServiceImpl(final InvoiceImportRepository invoiceImportRepository) {
    this.invoiceImportRepository = invoiceImportRepository;
  }

  public InvoiceImport saveInvoice(InvoiceImport invoice) {
    return invoiceImportRepository.save(invoice);
  }
}
