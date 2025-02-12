package com.invoicesync.service;

import org.springframework.web.multipart.MultipartFile;

public interface ReceiptService {

   void saveReceipt(MultipartFile qrCodeImage, Long userId, Long companyId);

}
