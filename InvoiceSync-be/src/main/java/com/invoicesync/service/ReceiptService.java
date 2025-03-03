package com.invoicesync.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.dto.receipt.reponse.ReceiptDetailsDTO;
import com.invoicesync.dto.receipt.reponse.ReceiptListDTO;

public interface ReceiptService {

   void saveReceipt(MultipartFile qrCodeImage, Long userId, Long companyId);

   List<ReceiptListDTO> getReceiptsByUserId(Long userId);

   ReceiptDetailsDTO getReceiptById(Long id);

}
