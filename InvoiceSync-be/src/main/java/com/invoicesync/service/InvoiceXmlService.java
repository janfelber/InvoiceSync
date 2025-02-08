package com.invoicesync.service;

import com.invoicesync.dto.invoice.InvoiceRequestDTO;

public interface InvoiceXmlService {

  byte[] generatePohodaInvoiceXml (InvoiceRequestDTO invoiceRequestDTO) throws Exception;

}
