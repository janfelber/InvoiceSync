package com.invoicesync.modules.invoice.service;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.core.common.PageResponse;
import com.invoicesync.modules.document.model.AddDocumentData;
import com.invoicesync.modules.document.model.DocumentTableResponse;
import com.invoicesync.modules.invoice.model.InvoiceResponse;
import com.invoicesync.modules.invoice.model.InvoiceResponseTable;

public interface InvoiceService {

  /**
   * Creates a new invoice from an uploaded file for a specific company.
   *
   * @param file uploaded invoice file
   * @param companyId ID of the company
   * @param connectedUser currently authenticated user
   * @return ID of the newly created invoice
   */
  Long saveInvoice(final MultipartFile file, final Long companyId, final Authentication connectedUser);

  /**
   * Finds an invoice by its unique ID.
   *
   * @param invoiceId ID of the company
   * @return invoice data as a response DTO
   */
  InvoiceResponse findById(Long invoiceId);

  /**
   * Retrieves a paginated list of invoices by company.
   *
   * @param size number of invoices per page
   * @param page page number (0-based)
   * @param companyId ID of the company
   * @param connectedUser currently authenticated user
   * @return paginated response of invoices
   */
  PageResponse<InvoiceResponseTable> findInvoicesByCompanyId(int size, int page, Long companyId,
      Authentication connectedUser);

  /**
   * Retrieves a paginated list of invoices associated with the connected user.
   *
   * @param size number of invoices per page
   * @param page page number (0-based)
   * @param connectedUser currently authenticated user
   * @return paginated response of invoices
   */
  PageResponse<InvoiceResponseTable> findAllInvoicesByUser(int size, int page,
      Authentication connectedUser);

  /**
   * Uploads a document to a specific invoice.
   *
   * @param document file to be uploaded
   * @param canDeleteDocument whether the document can be deleted later
   * @param invoiceId ID of the invoice to attach the document to
   * @param connectedUser currently authenticated user
   * @param additionalDocumentData optional additional metadata for the document
   */
  // TODO make this that this method can be used for receipts as well
  void uploadDocument(MultipartFile document, Boolean canDeleteDocument, Long invoiceId, Authentication connectedUser,
      @Nullable AddDocumentData additionalDocumentData
  );

  /**
   * Deletes a document by its ID associated with invoice.
   *
   * @param documentId ID of the invoice document to delete
   * @param connectedUser currently authenticated user
   */
  void deleteDocument(Long documentId, Authentication connectedUser);

  /**
   * Deletes an invoice by its ID.
   *
   * @param invoiceId ID of the invoice to delete
   * @param connectedUser currently authenticated user
   */
  void deleteInvoice(Long invoiceId, Authentication connectedUser);

  /**
   * Retrieves a list of invoice documents by ID.
   *
   * @param invoiceId ID of the invoice
   * @param connectedUser currently authenticated user
   * @return list response of invoice documents
   */
  List<DocumentTableResponse> findDocumentsByInvoiceId(Long invoiceId, Authentication connectedUser);

}
