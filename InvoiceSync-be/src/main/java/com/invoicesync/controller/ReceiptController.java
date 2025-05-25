package com.invoicesync.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.common.PageResponse;
import com.invoicesync.dto.receipt.reponse.ReceiptDetailDto;
import com.invoicesync.dto.receipt.reponse.ReceiptResponseDto;
import com.invoicesync.dto.record.ReceiptRequest;
import com.invoicesync.module.Receipt;
import com.invoicesync.service.ReceiptService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/receipt")
public class ReceiptController {

  // private final InvoiceXmlService invoiceXmlService;
  // private final CurrentUserService currentUserService;
  private final ReceiptService receiptService;


  //get receipts for current user
  @GetMapping("/user")
  public ResponseEntity<PageResponse<ReceiptResponseDto>> findAllReceiptsByUser(
      @RequestParam(name = "page", defaultValue = "0", required = false) final int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) final int size,
      final Authentication connectedUser
  ) {
    return ResponseEntity.ok(receiptService.findAllReceiptsByUser(page, size, connectedUser));
  }

  //get receipts by company id
  @GetMapping("/company/{company-id}")
  public ResponseEntity<PageResponse<ReceiptResponseDto>> findReceiptsByCompanyId(
      @RequestParam(name = "page", defaultValue = "0", required = false) final int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) final int size,
      @PathVariable("company-id") final Long companyId,
      final Authentication connectedUser) {

    return ResponseEntity.ok(receiptService.findReceiptsByCompanyId(page, size, companyId, connectedUser));
  }

  @GetMapping("/{receipt-id}")
  public ResponseEntity<ReceiptDetailDto> getReceiptById(@PathVariable("receipt-id") final Long receiptId) {
    return ResponseEntity.ok(receiptService.findById(receiptId));
  }

  @PostMapping("/save")
  public ResponseEntity<List<Long>> saveReceipts(
      @RequestParam("file") final MultipartFile[] files,
      @RequestParam("companyId") final Long companyId,
      final Authentication connectedUser
  ) {
    final List<Long> savedIds = Arrays.stream(files)
        .map(file -> receiptService.saveReceipt(file, companyId, connectedUser))
        .toList();

    return ResponseEntity.ok(savedIds);
  }

  @PatchMapping("/update/{receiptId}")
  public ResponseEntity<Receipt> updateReceipt(@PathVariable final Long receiptId,
      @RequestBody final ReceiptRequest receipt) {
    return ResponseEntity.ok(receiptService.updateReceiptById(receiptId, receipt));
  }

  //
  // private final CompanyRepository companyRepository;
  //
  // private final ReceiptService receiptService;
  //
  // @PostMapping("/pohoda/export/receipt")
  // public ResponseEntity<byte[]> createReceipt(@RequestBody final ReceiptRequestDTO receiptRequestDTO) {
  //   try {
  //     final byte[] xmlData = invoiceXmlService.generatePohodaReceiptXml(receiptRequestDTO);
  //     System.out.println(receiptRequestDTO);
  //
  //     final HttpHeaders headers = new HttpHeaders();
  //     headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.xml");
  //     headers.add(HttpHeaders.CONTENT_TYPE, "application/xml; charset=UTF-8");
  //
  //     return new ResponseEntity<>(xmlData, headers, HttpStatus.OK);
  //   } catch (Exception e) {
  //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
  //   }
  // }
  //
}
