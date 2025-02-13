package com.invoicesync.service;

import com.invoicesync.dto.invoice.pohoda.InvoiceRequestDTO;
import com.invoicesync.dto.receipt.pohoda.ReceiptRequestDTO;

public interface InvoiceXmlService {

  byte[] generatePohodaInvoiceXml (InvoiceRequestDTO invoiceRequestDTO) throws Exception;

  byte[] generatePohodaReceiptXml(ReceiptRequestDTO invoiceRequestDTO) throws Exception;
}
