package com.invoicesync.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.dto.receipt.pohoda.ReceiptRequestDTO;
import com.invoicesync.dto.receipt.reponse.ReceiptDetailsDTO;
import com.invoicesync.dto.receipt.reponse.ReceiptListDTO;
import com.invoicesync.module.Receipt;

public interface ReceiptService {

   void saveReceipt(MultipartFile qrCodeImage, Long userId, Long companyId);

   List<ReceiptListDTO> getReceiptsByUserId(Long userId);

   List<ReceiptListDTO> getReceiptsByCompanyId(Long companyId);

   ReceiptDetailsDTO getReceiptById(Long id);

   Receipt updateReceiptById(Long id, Long userId, ReceiptRequestDTO requestDTO);

}
