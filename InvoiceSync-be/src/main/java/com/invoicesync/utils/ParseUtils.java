package com.invoicesync.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.invoicesync.dto.record.ReceiptItemRequest;
import com.invoicesync.dto.record.ReceiptRequest;

public final class ParseUtils {

  private ParseUtils() {
  }

  public static ReceiptRequest parseReceiptRequest(final JsonNode rootNode, final Long companyId) {
    final JsonNode receiptNode = rootNode.path("receipt");
    final JsonNode orgNode = receiptNode.path("organization");
    final String street =
        orgNode.path("streetName").asText() + " " + orgNode.path("propertyRegistrationNumber").asText();

    final List<ReceiptItemRequest> items = new ArrayList<>();
    for (final JsonNode itemNode : receiptNode.path("items")) {
      final double price = itemNode.path("price").asDouble();
      final int vatRate = itemNode.path("vatRate").asInt();

      final BigDecimal priceWithoutVAT = new BigDecimal(price).divide(BigDecimal.valueOf(1 + (double) vatRate / 100), 2,
          RoundingMode.HALF_UP);

      items.add(new ReceiptItemRequest(
          null, // id (autogenerované)
          null, // receiptId (nastaví sa až po uložení Receipt)
          itemNode.path("name").asText(),
          itemNode.path("quantity").asInt(),
          priceWithoutVAT,
          itemNode.path("vatRate").asInt(),
          null,
          new BigDecimal(itemNode.path("price").asText()),
          null
      ));
    }

    return new ReceiptRequest(
        null,
        companyId,
        parseDateOnly(receiptNode.path("createDate").asText()),     // date
        parseDateOnly(receiptNode.path("issueDate").asText()),      // datePayment
        parseDateOnly(receiptNode.path("issueDate").asText()),      // dateTax
        orgNode.path("name").asText(),                               // partnerName
        orgNode.path("municipality").asText(),                      // partnerCity
        street,                                                     // partnerStreet
        orgNode.path("postalCode").asText(),                        // partnerZip
        orgNode.path("ico").asText(),                               // partnerRegistrationNumber
        orgNode.path("dic").asText(),                               // partnerTaxId
        orgNode.path("icDph").asText(),                             // partnerVatId
        receiptNode.path("totalPrice").asText(),                    // totalPrice
        null, null, null, null, // accounting, classificationVAT, classificationKVVAT, description
        items
    );
  }

  private static String parseDateOnly(final String dateTimeStr) {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    return LocalDate.parse(dateTimeStr, formatter).toString();
  }

}