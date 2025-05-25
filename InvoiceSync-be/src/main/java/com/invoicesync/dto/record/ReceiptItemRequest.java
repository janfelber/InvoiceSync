package com.invoicesync.dto.record;

import java.math.BigDecimal;

public record ReceiptItemRequest(
    Long id,
    Long receiptId,
    String name,
    Integer quantity,
    BigDecimal priceWithoutVat,
    Integer vatRate,
    String accountValue,
    BigDecimal priceWithVat,
    String accountText
) {}