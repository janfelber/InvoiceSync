package com.invoicesync.core.filestorage.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

  /**
   * Saves a file associated with a specific invoice.
   *
   * @param document file to be saved
   * @param invoiceId invoiceId id of the invoice to attach the file to
   * @param connectedUserId ID of the user performing the upload
   * @return path of the saved file
   */
  String saveInvoiceDocument(MultipartFile document, Long invoiceId, String connectedUserId);

  String saveReceiptDocument(MultipartFile document, Long receiptId, String connectedUserId);

  void deleteDocument(String key);

  void deleteBatchDocuments(List<String> keys);

}
