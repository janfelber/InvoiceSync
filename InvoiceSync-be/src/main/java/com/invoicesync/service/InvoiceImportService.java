package com.invoicesync.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.dto.invoice.response.InvoiceImportResponseDto;
import com.invoicesync.module.InvoiceImport;

public interface InvoiceImportService {

  List<InvoiceImportResponseDto> getInoivceImportsByUserId(Long userId);

  InvoiceImport saveInvoice(MultipartFile file, Long companyId) throws IOException;

  InvoiceImportResponseDto getInvoiceById(Long id);

  List<InvoiceImportResponseDto> getInvoiceImportsByCompanyIdCurrentUser(Long companyId, Long currentUserId);
}
