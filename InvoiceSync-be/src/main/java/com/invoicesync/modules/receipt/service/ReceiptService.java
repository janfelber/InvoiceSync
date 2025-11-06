package com.invoicesync.modules.receipt.service;

import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.invoicesync.core.common.PageResponse;
import com.invoicesync.modules.receipt.model.Receipt;
import com.invoicesync.modules.receipt.model.ReceiptDetailDto;
import com.invoicesync.modules.receipt.model.ReceiptRequest;
import com.invoicesync.modules.receipt.model.ReceiptResponseDto;

public interface ReceiptService {

   /**
    * Retrieves a paginated list of receipts associated with the connected user.
    *
    * @param size number of receipts per page
    * @param page page number (0-based)
    * @param connectedUser currently authenticated user
    * @return paginated response of receipts
    */
   PageResponse<ReceiptResponseDto> findAllReceiptsByUser(int page, int size, Authentication connectedUser);

   /**
    * Retrieves a paginated list of receipts by company.
    *
    * @param size number of receipts per page
    * @param page page number (0-based)
    * @param companyId ID of the company
    * @param connectedUser currently authenticated user
    * @return paginated response of receipts
    */
   PageResponse<ReceiptResponseDto> findReceiptsByCompanyId(int page, int size, Long companyId,
       Authentication connectedUser);

   /**
    * Finds an receipt by its unique ID.
    *
    * @param receiptId ID of the company
    * @return receipt data as a response DTO
    */
   ReceiptDetailDto findById(long receiptId);

   /**
    * Creates a new receipt from an uploaded QR code image for a specific company.
    *
    * @param qrCodeImage uploaded QR code image representing the receipt
    * @param companyId ID of the company
    * @param connectedUser currently authenticated user
    * @return ID of the newly created receipt
    */
   Long saveReceipt(final MultipartFile qrCodeImage, final Long companyId, final Authentication connectedUser);

   /**
    * Updates an existing receipt.
    *
    * @param receiptId ID of the receipt to update
    * @param receipt updated receipt data
    * @return updated Receipt entity
    */
   Receipt updateReceiptById(Long receiptId, ReceiptRequest receipt);


   /**
    * Deletes an existing receipt.
    *
    * @param receiptId ID of the receipt to update
    * @param connectedUser currently authenticated user
    */
   void deleteReceiptById(Long receiptId, Authentication connectedUser);

   //
   // ReceiptDetailsDTO getReceiptById(Long id);
   //

}
