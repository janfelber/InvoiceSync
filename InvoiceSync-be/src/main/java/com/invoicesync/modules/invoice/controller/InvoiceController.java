package com.invoicesync.modules.invoice.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.core.Api;
import com.invoicesync.core.common.PageResponse;
import com.invoicesync.core.common.PageableFactory;
import com.invoicesync.modules.document.model.AddDocumentData;
import com.invoicesync.modules.document.model.DocumentTableResponse;
import com.invoicesync.modules.invoice.model.InvoiceCreate;
import com.invoicesync.modules.invoice.model.InvoiceResponse;
import com.invoicesync.modules.invoice.model.InvoiceResponseTable;
import com.invoicesync.modules.invoice.service.InvoiceService;

@RestController
@RequestMapping(Api.INVOICE)
public class InvoiceController {

  private final InvoiceService invoiceService;

  @Autowired
  public InvoiceController(
      final InvoiceService invoiceService
  ) {
    this.invoiceService = invoiceService;
  }

  @GetMapping(Api.GET_BY_USER)
  public ResponseEntity<PageResponse<InvoiceResponseTable>> findAllInvoicesByUser(
      @RequestParam(name = "page", defaultValue = "" + PageableFactory.DEFAULT_PAGE, required = false) final int page,
      @RequestParam(name = "size", defaultValue = "" + PageableFactory.DEFAULT_SIZE, required = false) final int size,
      final Authentication connectedUser) {
    return ResponseEntity.ok(invoiceService.findAllInvoicesByUser(page, size, connectedUser));
  }

  @GetMapping(Api.INVOICE_GET_BY_COMPANY)
  public ResponseEntity<PageResponse<InvoiceResponseTable>> findInvoicesByCompanyId(
      @RequestParam(name = "page", defaultValue = "" + PageableFactory.DEFAULT_PAGE, required = false) final int page,
      @RequestParam(name = "size", defaultValue = "" + PageableFactory.DEFAULT_SIZE, required = false) final int size,
      @PathVariable("company-id") final Long companyId,
      final Authentication connectedUser) {
    return ResponseEntity.ok(
        invoiceService.findInvoicesByCompanyId(page, size, companyId, connectedUser));
  }

  @GetMapping(Api.INVOICE_GET_DOCUMENTS)
  public ResponseEntity<List<DocumentTableResponse>> findDocumentsByInvoiceId(
      @PathVariable("invoice-id") final Long invoiceId,
      final Authentication connectedUser) {
    return ResponseEntity.ok(
        invoiceService.findDocumentsByInvoiceId(invoiceId, connectedUser)
    );
  }

  @PostMapping(value = Api.SAVE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Long> saveInvoice(
      @RequestParam("file") final MultipartFile pdfFile,
      @RequestParam("companyId") final Long companyId,
      final Authentication connectedUser) throws IOException {
    return ResponseEntity.ok(invoiceService.saveInvoice(pdfFile, companyId, connectedUser));
  }

  @PostMapping(Api.INVOICE_CREATE)
  public ResponseEntity<Long> createInvoice(
      @RequestParam Long companyId,
      @RequestBody InvoiceCreate invoiceCreateRequest,
      Authentication connectedUser) {

    return ResponseEntity.ok(
        invoiceService.createInvoice(invoiceCreateRequest, companyId, connectedUser)
    );
  }

  @GetMapping(Api.INVOICE_EXPORT_PDF)
  public ResponseEntity<byte[]> downloadInvoicePdf(
      @PathVariable("invoice-id") Long invoiceId) {

    byte[] pdf = invoiceService.generateInvoicePdf(invoiceId);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDisposition(
        ContentDisposition.attachment()
            .filename("invoice-" + invoiceId + ".pdf")
            .build());

    return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
  }

  // //get import by id
  @GetMapping(Api.INVOICE_GET_BY_ID)
  public InvoiceResponse getInvoiceById(@PathVariable("invoice-id") final Long invoiceId) {
    return invoiceService.findById(invoiceId);
  }

  @PostMapping(value = Api.INVOICE_UPLOAD_DOCUMENT, consumes = "multipart/form-data")
  public ResponseEntity<?> uploadDocument(
      @RequestPart("file") final MultipartFile document,
      @PathVariable("invoice-id") final Long invoiceId,
      @RequestPart("data") final AddDocumentData addDocumentData,
      final Authentication connectedUser
  ) throws IOException {
    invoiceService.uploadDocument(document, true, invoiceId, connectedUser, addDocumentData);
    return ResponseEntity.accepted().build();
  }

  @DeleteMapping(Api.INVOICE_DELETE_DOCUMENT)
  public ResponseEntity<Long> deleteDocument(
      @PathVariable("document-id") final Long documentId,
      final Authentication connectedUser
  ) {
    invoiceService.deleteDocument(documentId, connectedUser);
    return ResponseEntity.ok(documentId);
  }

  @DeleteMapping(Api.INVOICE_DELETE)
  public void deleteInvoice(
      @PathVariable("invoice-id") final Long invoiceId,
      final Authentication connectedUser
  ) {
    invoiceService.deleteInvoice(invoiceId, connectedUser);
  }

}
