package com.invoicesync.receipt.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReceiptItemRequest(
    Long id,
    Long receiptId,
    @JsonProperty("description")
    String name,

    Integer quantity,
    BigDecimal priceWithoutVat,
    Integer vatRate,
    String accountValue,
    BigDecimal priceWithVat,
    String accountText
) {}