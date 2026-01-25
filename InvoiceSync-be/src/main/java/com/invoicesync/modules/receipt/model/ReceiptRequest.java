package com.invoicesync.modules.receipt.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ReceiptRequest(
    Long id,

    String receiptNumber,

    boolean isPaidByCard,

    @NotNull(message = "100")
    @NotEmpty(message = "100")
    Long companyId,

    @NotNull(message = "101")
    @NotEmpty(message = "101")
    String date,

    @NotNull(message = "102")
    @NotEmpty(message = "102")
    String datePayment,

    @NotNull(message = "103")
    @NotEmpty(message = "103")
    String dateTax,

    @NotNull(message = "104")
    @NotEmpty(message = "104")
    String partnerName,

    @NotNull(message = "105")
    @NotEmpty(message = "105")
    String partnerCity,

    @NotNull(message = "106")
    @NotEmpty(message = "106")
    String partnerStreet,

    @NotNull(message = "107")
    @NotEmpty(message = "107")
    String partnerZip,

    @NotNull(message = "108")
    @NotEmpty(message = "108")
    String partnerRegistrationNumber,

    @NotNull(message = "109")
    @NotEmpty(message = "109")
    String partnerTaxId,

    @NotNull(message = "110")
    @NotEmpty(message = "110")
    String partnerVatId,

    @NotNull(message = "111")
    @NotEmpty(message = "111")
    BigDecimal totalPriceWithoutVat,

    @NotNull(message = "112")
    @NotEmpty(message = "112")
    BigDecimal totalPriceWithVat,

    @NotNull(message = "113")
    @NotEmpty(message = "113")
    String accounting,

    @NotNull(message = "114")
    @NotEmpty(message = "114")
    String classificationVAT,

    @NotNull(message = "115")
    @NotEmpty(message = "115")
    String classificationKVVAT,

    @NotNull(message = "116")
    @NotEmpty(message = "116")
    String description,

    @NotNull(message = "117")
    @NotEmpty(message = "117")
    List<ReceiptItemRequest> items
) {

}
