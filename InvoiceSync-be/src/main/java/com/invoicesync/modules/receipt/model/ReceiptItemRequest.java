package com.invoicesync.modules.receipt.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

//TODO rename record name and fields to something more meaningful and generic
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