package com.invoicesync.invoice;

import org.springframework.security.core.Authentication;

import com.invoicesync.invoice.dto.InvoiceRequestDTO;
import com.invoicesync.receipt.dto.ReceiptRequest;
import com.invoicesync.receipt.dto.ReceiptRequestDTO;

public interface PohodaXmlService {

  byte[] generatePohodaInvoiceXml (InvoiceRequestDTO invoiceRequestDTO) throws Exception;

  byte[] generateReceiptXml(ReceiptRequestDTO request, Authentication connectedUser) throws Exception;

  byte[] generatePohodaReceiptExcel(ReceiptRequest request, Authentication connectedUser) throws Exception;
}
