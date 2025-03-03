package com.invoicesync.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.invoicesync.dto.receipt.pohoda.ReceiptDTO;
import com.invoicesync.dto.receipt.pohoda.ReceiptItemDTO;

public final class ParseUtils {

  private ParseUtils() {
  }

  public static ReceiptDTO parseReceipt(final JsonNode rootNode) {
    final JsonNode orgNode = rootNode.path("receipt").path("organization");
    final JsonNode receiptNode = rootNode.path("receipt");

    final String street =
        orgNode.path("streetName").asText() + " " + orgNode.path("propertyRegistrationNumber").asText();

    final List<ReceiptItemDTO> items = new ArrayList<>();
    final JsonNode itemsNode = receiptNode.path("items");
    for (final JsonNode itemNode : itemsNode) {
      final ReceiptItemDTO item = new ReceiptItemDTO();
      item.setName(itemNode.path("name").asText());
      item.setQuantity(itemNode.path("quantity").asInt());
      item.setUnitPrice(new BigDecimal(itemNode.path("price").asText()));
      item.setVatRate(itemNode.path("vatRate").asInt());
      items.add(item);
    }

    return new ReceiptDTO(
        orgNode.path("name").asText(),
        orgNode.path("municipality").asText(),
        street,
        orgNode.path("postalCode").asText(),
        parseDateOnly(receiptNode.path("createDate").asText()),
        parseDateOnly(receiptNode.path("issueDate").asText()),
        parseDateOnly(receiptNode.path("issueDate").asText()),
        orgNode.path("ico").asText(),
        orgNode.path("dic").asText(),
        orgNode.path("icDph").asText(),
        receiptNode.path("totalPrice").asText(),
        items
    );
  }

  private static String parseDateOnly(final String dateTimeStr) {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    return LocalDate.parse(dateTimeStr, formatter).toString();
  }

}