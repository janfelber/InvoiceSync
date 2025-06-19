package com.invoicesync.invoice;

import org.springframework.security.core.Authentication;

import com.invoicesync.invoice.dto.InvoiceRequestDTO;
import com.invoicesync.receipt.dto.ReceiptRequest;

public interface InvoiceXmlService {

  byte[] generatePohodaInvoiceXml (InvoiceRequestDTO invoiceRequestDTO) throws Exception;

  byte[] generatePohodaReceiptExcel(ReceiptRequest request, Authentication connectedUser) throws Exception;
}
