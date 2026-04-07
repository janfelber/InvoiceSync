package com.invoicesync.modules.company.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CompanyRequest(
    Long id,
    @NotNull(message = "100")
    @NotEmpty(message = "100")
    String name,

    @NotNull(message = "101")
    @NotEmpty(message = "101")
    String city,

    @NotNull(message = "102")
    @NotEmpty(message = "102")
    String street,
    @NotNull(message = "103")
    @NotEmpty(message = "103")
    String streetNumber,
    @NotNull(message = "104")
    @NotEmpty(message = "104")
    String zip,
    @NotNull(message = "105")
    @NotEmpty(message = "105")
    String registrationNumber,
    @NotNull(message = "106")
    @NotEmpty(message = "106")
    String taxId,
    @NotNull(message = "107")
    @NotEmpty(message = "107")
    String vatId,

    String cashReceiptNumber,

    String cardReceiptNumber,

    String recipientEmail
) {

}
