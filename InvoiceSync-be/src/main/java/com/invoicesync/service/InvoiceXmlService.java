package com.invoicesync.service;

import com.invoicesync.dto.invoice.pohoda.InvoiceRequestDTO;
import com.invoicesync.dto.record.ReceiptRequest;

public interface InvoiceXmlService {

  byte[] generatePohodaInvoiceXml (InvoiceRequestDTO invoiceRequestDTO) throws Exception;

  byte[] generatePohodaReceiptExcel(ReceiptRequest request) throws Exception;
}
