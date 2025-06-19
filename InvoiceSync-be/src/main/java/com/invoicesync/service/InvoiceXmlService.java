package com.invoicesync.service;

import org.springframework.security.core.Authentication;

import com.invoicesync.dto.invoice.pohoda.InvoiceRequestDTO;
import com.invoicesync.dto.record.ReceiptRequest;

public interface InvoiceXmlService {

  byte[] generatePohodaInvoiceXml (InvoiceRequestDTO invoiceRequestDTO) throws Exception;

  byte[] generatePohodaReceiptExcel(ReceiptRequest request, Authentication connectedUser) throws Exception;
}
