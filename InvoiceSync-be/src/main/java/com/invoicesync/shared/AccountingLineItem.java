package com.invoicesync.shared;

import lombok.Builder;

@Builder
public record AccountingLineItem(

    Long id,

    String name,

    Integer quantity,

    String unit,

    Integer vatRate,

    MonetaryAmount unitPrice,

    MonetaryAmount totalPrice,

    AccountingAssignment accounting

) {

}
