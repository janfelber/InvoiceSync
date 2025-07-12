package com.invoicesync.invoice;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.invoicesync.invoice.dto.InvoiceImportResponseDto;
import com.invoicesync.invoice.dto.InvoiceResponseDTO;
import com.invoicesync.shared.common.PageResponse;

public interface InvoiceService {

  List<InvoiceImportResponseDto> getInoivceImportsByUserId(Long userId);

  // Long saveInvoice(final MultipartFile file, final Authentication connectedUser);

  InvoiceImportResponseDto getInvoiceById(Long id);

  PageResponse<InvoiceResponseDTO> findInvoicesByCompanyId(int size, int page, Long companyId,
      Authentication connectedUser);
}
