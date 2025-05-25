package com.invoicesync.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.common.PageResponse;
import com.invoicesync.dto.receipt.reponse.ReceiptDetailDto;
import com.invoicesync.dto.receipt.reponse.ReceiptResponseDto;
import com.invoicesync.dto.record.ReceiptRequest;
import com.invoicesync.module.Receipt;

public interface ReceiptService {

   PageResponse<ReceiptResponseDto> findAllReceiptsByUser(int size, int page, Authentication connectedUser);

   PageResponse<ReceiptResponseDto> findReceiptsByCompanyId(int size, int page, Long companyId,
       Authentication connectedUser);

   ReceiptDetailDto findById(long receiptId);

   Long saveReceipt(final MultipartFile qrCodeImage, final Long companyId, final Authentication connectedUser);

   Receipt updateReceiptById(Long id, ReceiptRequest receipt);

   //
   // ReceiptDetailsDTO getReceiptById(Long id);
   //

}
