package com.invoicesync.modules.invoice.model;

import java.util.List;

import com.invoicesync.core.identity.PartnerDto;
import com.invoicesync.modules.receipt.model.ReceiptItemDto;

public record InvoiceCreate(
    Long companyId,
    InvoiceRequestDetailsDTO invoiceDetails,
    PartnerDto partner,
    List<ReceiptItemDto> items
) {

}
