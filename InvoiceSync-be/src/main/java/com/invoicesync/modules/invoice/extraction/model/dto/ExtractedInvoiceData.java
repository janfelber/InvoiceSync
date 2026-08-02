package com.invoicesync.modules.invoice.extraction.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExtractedInvoiceData(
    @JsonProperty("supplier_registration_number") String supplierRegistrationNumber,
    @JsonProperty("invoice_number") String invoiceNumber,
    @JsonProperty("issue_date") LocalDate issueDate,
    @JsonProperty("due_date") LocalDate dueDate,
    String currency,
    List<ExtractedItemDto> items,
    //TODO this could be covered one record
    @JsonProperty("invoice_total_price_without_vat") BigDecimal priceWithoutVAT,
    @JsonProperty("invoice_total_price_with_vat") BigDecimal priceWithVAT
) {

}
