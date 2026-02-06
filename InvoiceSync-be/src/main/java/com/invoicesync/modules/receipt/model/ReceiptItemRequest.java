package com.invoicesync.modules.receipt.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReceiptItemRequest(
    Long id,

    Long receiptId,

    @JsonProperty("description")
    String name,

    @JsonProperty("unitType")
    String unitType,

    Integer quantity,

    @JsonProperty("vatRate")
    Integer vatRate,

    @JsonProperty("unitPriceWithoutVat")
    BigDecimal unitPriceWithoutVat,

    @JsonProperty("unitPriceWithVat")
    BigDecimal unitPriceWithVat,

    BigDecimal totalItemPriceWithoutVat,

    @JsonProperty("total")
    BigDecimal totalItemPriceWithVat,

    String accountValue,

    String accountText
) {

}