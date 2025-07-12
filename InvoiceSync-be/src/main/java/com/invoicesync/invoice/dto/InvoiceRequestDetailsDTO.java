package com.invoicesync.invoice.dto;

import com.invoicesync.invoice.InvoiceType;

public record InvoiceRequestDetailsDTO(
    String numberRequested,

    String variableSymbol,

    String originalDocument,

    String issueDate,

    String taxDate,

    String accountingDate,

    String dueDate,

    String accountValue,

    String classificationVAT,

    String classificationKVVAT,

    String description,

    String status,

    InvoiceType invoiceType
) {

}
