package com.invoicesync.modules.invoice.model;

import java.util.List;

import com.invoicesync.core.identity.PartnerDto;
import com.invoicesync.shared.AccountingLineItem;

import lombok.Builder;

@Builder
public record InvoiceCreate(

    Long companyId,

    InvoiceRequestDetailsDTO invoiceDetails,

    PartnerDto partner,

    List<AccountingLineItem> items

) {

}
