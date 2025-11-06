package com.invoicesync.modules.invoice.model;

import java.util.List;

import com.invoicesync.core.identity.MyIdentity;
import com.invoicesync.core.identity.PartnerDto;
import com.invoicesync.core.enums.InvoiceType;
import com.invoicesync.modules.receipt.model.ReceiptItemDto;

public record InvoiceRequestDTO(

    InvoiceRequestDetailsDTO invoiceDetails,

    PartnerDto partner,

    MyIdentity myIdentity,

    List<ReceiptItemDto> items

) {

  public InvoiceType invoiceType() {
    return invoiceDetails.invoiceType();
  }
}
