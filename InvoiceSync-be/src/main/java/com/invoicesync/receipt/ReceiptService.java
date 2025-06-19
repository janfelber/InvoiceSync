package com.invoicesync.receipt;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.receipt.dto.ReceiptDetailDto;
import com.invoicesync.receipt.dto.ReceiptRequest;
import com.invoicesync.receipt.dto.ReceiptResponseDto;
import com.invoicesync.shared.common.PageResponse;

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
