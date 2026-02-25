package com.invoicesync.modules.stripe.model;

public record UserDefaultCard(

    // Card brand which can be one of the following: "visa", "mastercard", "amex".
    String cardBrand,
    // Last 4 digits of the card.
    String cardLastDigits,
    // Card expiration month.
    Long expMonth,
    // Card expiration year.
    Long expYear
) {

}
