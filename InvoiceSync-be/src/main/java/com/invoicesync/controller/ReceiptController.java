package com.invoicesync.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.dto.receipt.pohoda.ReceiptRequestDTO;
import com.invoicesync.dto.receipt.reponse.ReceiptDetailsDTO;
import com.invoicesync.dto.receipt.reponse.ReceiptListDTO;
import com.invoicesync.service.InvoiceXmlService;
import com.invoicesync.service.ReceiptService;
import com.invoicesync.user.CurrentUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/receipt")
public class ReceiptController {

  private final InvoiceXmlService invoiceXmlService;
  private final CurrentUserService currentUserService;

  private final ReceiptService receiptService;

  @PostMapping("/pohoda/export/receipt")
  public ResponseEntity<byte[]> createReceipt(@RequestBody final ReceiptRequestDTO receiptRequestDTO) {
    try {
      final byte[] xmlData = invoiceXmlService.generatePohodaReceiptXml(receiptRequestDTO);

      final HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.xml");
      headers.add(HttpHeaders.CONTENT_TYPE, "application/xml; charset=UTF-8");

      return new ResponseEntity<>(xmlData, headers, HttpStatus.OK);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  //get receipts for current user
  @GetMapping("/current-user")
  public List<ReceiptListDTO> getReceiptsCurrentUser() {
    final Long currentUserId = currentUserService.getCurrentUserId();
    return receiptService.getReceiptsByUserId(currentUserId);
  }

  @GetMapping("/{id}")
  public ReceiptDetailsDTO getReceiptById(@PathVariable("id") final Long id) {
    return receiptService.getReceiptById(id);
  }

  @PostMapping("/save")
  public void saveReceipt(
      @RequestParam("file") final MultipartFile file,
      @RequestParam("companyId") final Long companyId) {
    final Long currentUserId = currentUserService.getCurrentUserId();
    receiptService.saveReceipt(file, currentUserId, companyId);
  }
}
