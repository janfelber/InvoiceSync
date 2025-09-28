package com.invoicesync.filestorage;

import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.invoice.Invoice;

public interface FileStorageService {

  /**
   * Saves a file associated with a specific invoice.
   *
   * @param document file to be saved
   * @param invoice invoice entity to attach the file to
   * @param connectedUserId ID of the user performing the upload
   * @return path of the saved file
   */
  String saveFile(MultipartFile document, Invoice invoice, String connectedUserId);

}
