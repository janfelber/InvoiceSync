package com.invoicesync.invoice.dto;

import java.util.List;

import com.invoicesync.invoice.InvoiceType;
import com.invoicesync.receipt.dto.ReceiptItemDto;
import com.invoicesync.shared.dto.identity.MyIdentityDTO;
import com.invoicesync.shared.dto.identity.PartnerDto;

public record InvoiceRequestDTO(

    InvoiceRequestDetailsDTO invoiceDetails,

    PartnerDto partner,

    MyIdentityDTO myIdentity,

    List<ReceiptItemDto> items

) {

  public InvoiceType invoiceType() {
    return invoiceDetails.invoiceType();
  }
}
