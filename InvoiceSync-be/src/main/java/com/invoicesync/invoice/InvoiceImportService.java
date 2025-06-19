package com.invoicesync.invoice;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.invoice.dto.InvoiceImportResponseDto;

public interface InvoiceImportService {

  List<InvoiceImportResponseDto> getInoivceImportsByUserId(Long userId);

  InvoiceImport saveInvoice(MultipartFile file, Long companyId) throws IOException;

  InvoiceImportResponseDto getInvoiceById(Long id);

  List<InvoiceImportResponseDto> getInvoiceImportsByCompanyIdCurrentUser(Long companyId, Long currentUserId);
}
