package com.invoicesync.invoice;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.invoice.dto.InvoiceResponse;
import com.invoicesync.invoice.dto.InvoiceResponseTable;
import com.invoicesync.shared.common.PageResponse;

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

  @GetMapping("/company/{company-id}")
  public ResponseEntity<PageResponse<InvoiceResponseTable>> findInvoicesByCompanyId(
      @RequestParam(name = "page", defaultValue = "0", required = false) final int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) final int size,
      @PathVariable("company-id") final Long companyId,
      final Authentication connectedUser) {
    return ResponseEntity.ok(
        invoiceService.findInvoicesByCompanyId(page, size, companyId, connectedUser));
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

}
