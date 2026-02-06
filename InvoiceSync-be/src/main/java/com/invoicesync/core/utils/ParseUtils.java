package com.invoicesync.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.invoicesync.modules.receipt.model.ReceiptItemRequest;
import com.invoicesync.modules.receipt.model.ReceiptRequest;

public final class ParseUtils {

  private ParseUtils() {
  }

  public static ReceiptRequest parseReceiptRequest(final JsonNode rootNode, final Long companyId) {
    final JsonNode receiptNode = rootNode.path("receipt");
    final JsonNode orgNode = receiptNode.path("organization");
    final String street =
        orgNode.path("streetName").asText() + " " + orgNode.path("propertyRegistrationNumber").asText();

    BigDecimal totalPriceWithoutVat = BigDecimal.ZERO;

    final List<ReceiptItemRequest> items = new ArrayList<>();
    for (final JsonNode itemNode : receiptNode.path("items")) {
      final BigDecimal totalItemPriceWithVat = itemNode.path("price").decimalValue();
      final int vatRate = itemNode.path("vatRate").asInt();
      final int quantity = itemNode.path("quantity").asInt();

      final BigDecimal unitPriceWithVat =
          totalItemPriceWithVat.divide(BigDecimal.valueOf(quantity), 2, RoundingMode.HALF_UP);

      final BigDecimal unitPriceWithoutVat =
          unitPriceWithVat.divide(BigDecimal.ONE.add(BigDecimal.valueOf(vatRate).divide(BigDecimal.valueOf(100))), 2,
              RoundingMode.HALF_UP);

      final BigDecimal totalItemPriceWithoutVat = unitPriceWithoutVat.multiply(BigDecimal.valueOf(quantity));

      totalPriceWithoutVat = totalPriceWithoutVat.add(totalItemPriceWithoutVat);

      items.add(new ReceiptItemRequest(
          null, // id (autogenerované)
          null, // receiptId (nastaví sa až po uložení Receipt)
          itemNode.path("name").asText(),
          null,
          quantity,
          vatRate,
          unitPriceWithoutVat,
          unitPriceWithVat,
          totalItemPriceWithoutVat,
          totalItemPriceWithVat,
          null,
          null
      ));
    }

    return new ReceiptRequest(
        null,
        null,
        false,
        companyId,
        parseDateOnly(receiptNode.path("createDate").asText()),     // date
        parseDateOnly(receiptNode.path("issueDate").asText()),      // datePayment
        parseDateOnly(receiptNode.path("issueDate").asText()),      // dateTax
        orgNode.path("name").asText(),                              // partnerName
        orgNode.path("municipality").asText(),                      // partnerCity
        street,                                                        // partnerStreet
        orgNode.path("postalCode").asText(),                        // partnerZip
        orgNode.path("ico").asText(),                               // partnerRegistrationNumber
        orgNode.path("dic").asText(),                               // partnerTaxId
        orgNode.path("icDph").asText(),                             // partnerVatId
        totalPriceWithoutVat,
        receiptNode.path("totalPrice").decimalValue(), // totalPrice,
        null,
        null,
        null,
        null,
        items
    );
  }

  private static String parseDateOnly(final String dateTimeStr) {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    return LocalDate.parse(dateTimeStr, formatter).toString();
  }

}