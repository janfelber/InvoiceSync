package com.invoicesync.modules.invoice.service;

import org.springframework.security.core.Authentication;

import com.invoicesync.modules.invoice.model.InvoiceRequestDTO;
import com.invoicesync.modules.receipt.model.ReceiptRequest;

public interface PohodaXmlService {

  byte[] generatePohodaInvoiceXml(InvoiceRequestDTO invoiceRequestDTO) throws Exception;

  byte[] generateReceiptXml(Long receiptId, Authentication connectedUser) throws RuntimeException;

  byte[] generatePohodaReceiptExcel(ReceiptRequest request, Authentication connectedUser) throws Exception;

}
