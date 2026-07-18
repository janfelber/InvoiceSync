package com.invoicesync.shared;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record MonetaryAmount(

    BigDecimal priceWithoutVAT,

    BigDecimal priceWithVAT

) {

}
