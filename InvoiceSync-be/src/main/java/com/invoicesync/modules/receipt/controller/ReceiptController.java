package com.invoicesync.modules.receipt.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.core.common.PageResponse;
import com.invoicesync.core.exception.LimitExceededException;
import com.invoicesync.modules.invoice.service.PohodaXmlService;
import com.invoicesync.modules.receipt.model.Receipt;
import com.invoicesync.modules.receipt.model.ReceiptDetailDto;
import com.invoicesync.modules.receipt.model.ReceiptRequest;
import com.invoicesync.modules.receipt.model.ReceiptRequestDTO;
import com.invoicesync.modules.receipt.model.ReceiptResponseDto;
import com.invoicesync.modules.receipt.service.ReceiptService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/receipt")
public class ReceiptController {

  private final PohodaXmlService pohodaXmlService;
  private final ReceiptService receiptService;

  @PostMapping(value = "/export/pohoda", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
  public ResponseEntity<byte[]> createReceipt(@RequestBody final ReceiptRequest request,
      final Authentication connectedUser) {
    try {
      final byte[] excel = pohodaXmlService.generatePohodaReceiptExcel(request, connectedUser);

      final HttpHeaders headers = new HttpHeaders();
      headers.setContentType(
          MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
      headers.setContentDisposition(
          ContentDisposition.attachment().filename(request.receiptNumber() + ".xlsx").build());

      return new ResponseEntity<>(excel, headers, HttpStatus.OK);
    } catch (LimitExceededException e) {
      return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping("/export/receipt")
  public ResponseEntity<byte[]> createReceipt(@RequestBody final ReceiptRequestDTO receiptRequestDTO,
      final Authentication connectedUser) {
      final byte[] xmlData = pohodaXmlService.generateReceiptXml(receiptRequestDTO, connectedUser);

      final HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.xml");
      headers.add(HttpHeaders.CONTENT_TYPE, "application/xml; charset=UTF-8");

      return new ResponseEntity<>(xmlData, headers, HttpStatus.OK);
  }

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
    System.out.println(connectedUser.getName());
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

  @DeleteMapping("/{receiptId}")
  public void deleteReceipt(@PathVariable final Long receiptId, final Authentication connectedUser) {
    receiptService.deleteReceiptById(receiptId, connectedUser);
  }
}
