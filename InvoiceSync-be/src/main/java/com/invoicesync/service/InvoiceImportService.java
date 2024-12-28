package com.invoicesync.service;

import java.util.List;

import com.invoicesync.dto.InvoiceImportResponseDto;
import com.invoicesync.module.InvoiceImport;

public interface InvoiceImportService {

  List<InvoiceImportResponseDto> getInoivceImportsByUserId(Long userId);

  InvoiceImport saveInvoice(InvoiceImport invoice);

  InvoiceImportResponseDto getInvoiceById(Long id);

  List<InvoiceImportResponseDto> getInvoiceImportsByCompanyIdCurrentUser(Long companyId, Long currentUserId);
}
