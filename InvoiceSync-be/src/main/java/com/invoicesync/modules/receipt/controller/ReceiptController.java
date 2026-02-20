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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.core.Api;
import com.invoicesync.core.common.PageResponse;
import com.invoicesync.core.exception.LimitExceededException;
import com.invoicesync.modules.document.model.AddDocumentData;
import com.invoicesync.modules.document.model.DocumentTableResponse;
import com.invoicesync.modules.invoice.service.PohodaXmlService;
import com.invoicesync.modules.receipt.model.MergeItemsRequest;
import com.invoicesync.modules.receipt.model.Receipt;
import com.invoicesync.modules.receipt.model.ReceiptDetailDto;
import com.invoicesync.modules.receipt.model.ReceiptRequest;
import com.invoicesync.modules.receipt.model.ReceiptRequestDTO;
import com.invoicesync.modules.receipt.model.ReceiptResponseDto;
import com.invoicesync.modules.receipt.service.ReceiptService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(Api.RECEIPT)
public class ReceiptController {

  private final PohodaXmlService pohodaXmlService;

  private final ReceiptService receiptService;

  @PostMapping(value = Api.RECEIPT_EXPORT_POHODA, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
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

  @PostMapping(Api.RECEIPT_EXPORT_RECEIPT)
  public ResponseEntity<byte[]> createReceipt(@RequestBody final ReceiptRequestDTO receiptRequestDTO,
      final Authentication connectedUser) {
    final byte[] xmlData = pohodaXmlService.generateReceiptXml(receiptRequestDTO, connectedUser);

    final HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.xml");
    headers.add(HttpHeaders.CONTENT_TYPE, "application/xml; charset=UTF-8");

    return new ResponseEntity<>(xmlData, headers, HttpStatus.OK);
  }

  //get receipts for current user
  @GetMapping(Api.GET_BY_USER)
  public ResponseEntity<PageResponse<ReceiptResponseDto>> findAllReceiptsByUser(
      @RequestParam(name = "page", defaultValue = "0", required = false) final int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) final int size,
      final Authentication connectedUser
  ) {
    return ResponseEntity.ok(receiptService.findAllReceiptsByUser(page, size, connectedUser));
  }

  //get receipts by company id
  @GetMapping(Api.RECEIPT_GET_BY_COMPANY)
  public ResponseEntity<PageResponse<ReceiptResponseDto>> findReceiptsByCompanyId(
      @RequestParam(name = "page", defaultValue = "0", required = false) final int page,
      @RequestParam(name = "size", defaultValue = "10", required = false) final int size,
      @PathVariable("company-id") final Long companyId,
      final Authentication connectedUser) {

    return ResponseEntity.ok(receiptService.findReceiptsByCompanyId(page, size, companyId, connectedUser));
  }

  @GetMapping(Api.RECEIPT_GET_BY_ID)
  public ResponseEntity<ReceiptDetailDto> getReceiptById(@PathVariable("receipt-id") final Long receiptId) {
    return ResponseEntity.ok(receiptService.findById(receiptId));
  }

  @GetMapping(Api.RECEIPT_GET_DOCUMENTS)
  public ResponseEntity<List<DocumentTableResponse>> findDocumentsByReceiptId(
      @PathVariable("receipt-id") final Long receiptId,
      final Authentication connectedUser) {
    return ResponseEntity.ok(
        receiptService.findDocumentsByReceipt(receiptId, connectedUser)
    );
  }

  @PostMapping(Api.RECEIPT_SAVE)
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

  @PostMapping(Api.RECEIPT_MERGE_ITEMS)
  public void mergeReceiptItems(
      @PathVariable("receipt-id") final Long receiptId,
      @RequestBody final MergeItemsRequest request,
      Authentication connectedUser
  ) {
    receiptService.mergeReceiptItems(receiptId, request, connectedUser);
  }

  @PostMapping(value = Api.RECEIPT_UPLOAD_DOCUMENT, consumes = "multipart/form-data")
  public ResponseEntity<?> uploadDocument(
      @RequestPart("file") final MultipartFile document,
      @PathVariable("receipt-id") final Long receiptId,
      @RequestPart("data") final AddDocumentData addDocumentData,
      final Authentication connectedUser
  ) {
    receiptService.uploadReceiptDocument(document, true, receiptId, connectedUser, addDocumentData);
    return ResponseEntity.ok().build();
  }

  @PatchMapping(Api.RECEIPT_UPDATE)
  public ResponseEntity<Receipt> updateReceipt(@PathVariable final Long receiptId,
      @RequestBody final ReceiptRequest receipt) {
    return ResponseEntity.ok(receiptService.updateReceiptById(receiptId, receipt));
  }

  @DeleteMapping(Api.RECEIPT_DELETE)
  public void deleteReceipt(@PathVariable final Long receiptId, final Authentication connectedUser) {
    receiptService.deleteReceiptById(receiptId, connectedUser);
  }

  @DeleteMapping(Api.RECEIPT_DELETE_DOCUMENT)
  public ResponseEntity<Long> deleteDocument(
      @PathVariable("document-id") final Long documentId,
      final Authentication connectedUser
  ) {
    receiptService.deleteReceiptDocument(documentId, connectedUser);
    return ResponseEntity.ok(documentId);
  }

}
