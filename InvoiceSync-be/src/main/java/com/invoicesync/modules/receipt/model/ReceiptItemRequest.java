package com.invoicesync.modules.receipt.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReceiptItemRequest(
    Long id,

    Long receiptId,

    @JsonProperty("description")
    String name,

    Integer quantity,

    Integer vatRate,

    @JsonProperty("unitPriceWithoutVat")
    BigDecimal unitPriceWithoutVat,

    @JsonProperty("unitPriceWithVat")
    BigDecimal unitPriceWithVat,

    BigDecimal totalItemPriceWithoutVat,

    BigDecimal totalItemPriceWithVat,

    String accountValue,

    String accountText
) {

}