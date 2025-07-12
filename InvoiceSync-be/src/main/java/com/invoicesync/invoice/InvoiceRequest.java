package com.invoicesync.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InvoiceRequest(
    Long id,

    @JsonProperty("Invoice Number")
    String numberRequested,

    @JsonProperty("Variable Symbol")
    String variableSymbol,

    @JsonProperty("Date of Issue")
    String issueDate,

    @JsonProperty("Date of Tax")
    String taxDate,

    @JsonProperty("Date of Accounting")
    String accountingDate,

    @JsonProperty("Date of Due")
    String dueDate,

    @JsonProperty("Supplier Name")
    String partnerName,

    @JsonProperty("Supplier Street")
    String partnerStreet,

    @JsonProperty("Supplier City")
    String partnerCity,

    @JsonProperty("Supplier Zip")
    String partnerZip,

    @JsonProperty("Supplier Registration Number")
    String partnerRegistrationNumber,

    @JsonProperty("Supplier VAT ID")
    String partnerVatId,

    @JsonProperty("Supplier TAX ID")
    String partnerTaxId




) {

}
