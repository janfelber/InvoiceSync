package com.invoicesync.modules.invoice.extraction.model.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExtractedItemDto(
    String name,
    BigDecimal quantity,
    String unit,
    @JsonProperty("vat") int VAT,
    @JsonProperty("unit_price") BigDecimal unitPrice,
    @JsonProperty("total_price") BigDecimal totalPrice
) {

}
