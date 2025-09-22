package com.invoicesync.filestorage;

import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.invoice.Invoice;

public interface FileStorageService {

  String saveFile(MultipartFile document, Invoice invoice, String connectedUserId);

}
