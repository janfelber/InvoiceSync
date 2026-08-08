package com.invoicesync.modules.invoice.extraction.model.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExtractedVatBreakdownDto(

    @JsonProperty("rate")
    Integer vatRate,

    @JsonProperty("sum_without_vat")
    BigDecimal sumWithoutVAT,

    @JsonProperty("sum_with_vat")
    BigDecimal sumWithVAT,

    @JsonProperty("sum_vat")
    BigDecimal sumVAT

) {

}
