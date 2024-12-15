package com.invoicesync.service;

import com.invoicesync.dto.InvoiceImportResponseDto;
import com.invoicesync.module.InvoiceImport;

import java.util.List;

public interface InvoiceImportService {

  List<InvoiceImportResponseDto> getInoivceImportsByUserId(Long userId);

  InvoiceImport saveInvoice(InvoiceImport invoice);
}
