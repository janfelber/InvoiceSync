package com.invoicesync.modules.invoice.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.core.common.PageResponse;
import com.invoicesync.modules.document.model.AddDocumentData;
import com.invoicesync.modules.document.model.InvoiceDocumentsTableResponse;
import com.invoicesync.modules.invoice.service.InvoiceService;
import com.invoicesync.modules.invoice.model.InvoiceResponse;
import com.invoicesync.modules.invoice.model.InvoiceResponseTable;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

  private final InvoiceService invoiceService;

  @Autowired
  public InvoiceController(
      final InvoiceService invoiceService
  ) {
    this.invoiceService = invoiceService;
  }

  @GetMapping("/user")
  public ResponseEntity<PageResponse<InvoiceResponseTable>> findAllInvoicesByUser(
      @RequestParam(name = "page", defaultValue = "0", required = false) final int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) final int size,
      final Authentication connectedUser) {
    return ResponseEntity.ok(invoiceService.findAllInvoicesByUser(page, size, connectedUser));
  }

  @GetMapping("/{company-id}/invoices")
  public ResponseEntity<PageResponse<InvoiceResponseTable>> findInvoicesByCompanyId(
      @RequestParam(name = "page", defaultValue = "0", required = false) final int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) final int size,
      @PathVariable("company-id") final Long companyId,
      final Authentication connectedUser) {
    return ResponseEntity.ok(
        invoiceService.findInvoicesByCompanyId(page, size, companyId, connectedUser));
  }

  @GetMapping("/{invoice-id}/documents")
  public ResponseEntity<List<InvoiceDocumentsTableResponse>> findDocumentsByInvoiceId(
      @PathVariable("invoice-id") final Long invoiceId,
      final Authentication connectedUser) {
    return ResponseEntity.ok(
        invoiceService.findDocumentsByInvoiceId(invoiceId, connectedUser)
    );
  }

  @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Long> saveInvoice(
      @RequestParam("file") final MultipartFile pdfFile,
      @RequestParam("companyId") final Long companyId,
      final Authentication connectedUser) throws IOException {
    return ResponseEntity.ok(invoiceService.saveInvoice(pdfFile, companyId, connectedUser));
  }

  // //get import by id
  @GetMapping("/{invoice-id}")
  public InvoiceResponse getInvoiceById(@PathVariable("invoice-id") final Long invoiceId) {
    return invoiceService.findById(invoiceId);
  }

  @PostMapping(value = "/upload/document/{invoice-id}", consumes = "multipart/form-data")
  public ResponseEntity<?> uploadDocument(
      @RequestPart("file") final MultipartFile document,
      @PathVariable("invoice-id") final Long invoiceId,
      @RequestPart("data") final AddDocumentData addDocumentData,
      final Authentication connectedUser
  ) throws IOException {
    invoiceService.uploadDocument(document, true, invoiceId, connectedUser, addDocumentData);
    return ResponseEntity.accepted().build();
  }

  @DeleteMapping("/delete/document/{document-id}")
  public ResponseEntity<Long> deleteDocument(
      @PathVariable("document-id") final Long documentId,
      final Authentication connectedUser
  ) {
    invoiceService.deleteDocument(documentId, connectedUser);
    return ResponseEntity.ok(documentId);
  }

  @DeleteMapping("/delete/{invoice-id}")
  public void deleteInvoice(
      @PathVariable("invoice-id") final Long invoiceId,
      final Authentication connectedUser
  ) {
    invoiceService.deleteInvoice(invoiceId, connectedUser);
  }

}
