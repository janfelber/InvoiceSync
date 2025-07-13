package com.invoicesync.invoice;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.invoice.dto.InvoiceResponse;
import com.invoicesync.invoice.dto.InvoiceResponseTable;
import com.invoicesync.shared.common.PageResponse;

public interface InvoiceService {

  Long saveInvoice(final MultipartFile file, final Long companyId, final Authentication connectedUser);

  InvoiceResponse findById(Long invoiceId);

  PageResponse<InvoiceResponseTable> findInvoicesByCompanyId(int size, int page, Long companyId,
      Authentication connectedUser);
}
