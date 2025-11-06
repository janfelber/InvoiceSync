package com.invoicesync.modules.invoice.model;

import com.invoicesync.core.enums.InvoiceType;

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
