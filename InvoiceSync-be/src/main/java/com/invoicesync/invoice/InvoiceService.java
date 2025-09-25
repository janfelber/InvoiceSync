package com.invoicesync.invoice;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.document.invoice.dto.AddDocumentData;
import com.invoicesync.document.invoice.dto.InvoiceDocumentsTableResponse;
import com.invoicesync.invoice.dto.InvoiceResponse;
import com.invoicesync.invoice.dto.InvoiceResponseTable;
import com.invoicesync.shared.common.PageResponse;

public interface InvoiceService {

  Long saveInvoice(final MultipartFile file, final Long companyId, final Authentication connectedUser);

  InvoiceResponse findById(Long invoiceId);

  PageResponse<InvoiceResponseTable> findInvoicesByCompanyId(int size, int page, Long companyId,
      Authentication connectedUser);

  PageResponse<InvoiceResponseTable> findAllInvoicesByUser(int size, int page,
      Authentication connectedUser);

  void uploadDocument(MultipartFile document, Boolean canDeleteDocument, Long invoiceId, Authentication connectedUser,
      @Nullable AddDocumentData additionalDocumentData
  );

  void deleteDocument(Long documentId, Authentication connectedUser);

  List<InvoiceDocumentsTableResponse> findDocumentsByInvoiceId(Long invoiceId, Authentication connectedUser);

}
