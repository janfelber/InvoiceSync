package com.invoicesync.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.JsonNode;
import com.invoicesync.dto.receipt.pohoda.ReceiptDTO;

public final class ParseUtils {

  private ParseUtils() {
  }

  public static ReceiptDTO parseReceipt(final JsonNode rootNode) {
    final JsonNode orgNode = rootNode.path("receipt").path("organization");
    final JsonNode receiptNode = rootNode.path("receipt");

    return new ReceiptDTO(
        orgNode.path("name").asText(),
        orgNode.path("municipality").asText(),
        orgNode.path("streetName").asText(),
        orgNode.path("postalCode").asText(),
        parseDateOnly(receiptNode.path("issueDate").asText()),
        parseDateOnly(receiptNode.path("issueDate").asText()),
        parseDateOnly(receiptNode.path("issueDate").asText()),
        orgNode.path("ico").asText(),
        orgNode.path("dic").asText(),
        orgNode.path("icDph").asText()
    );
  }

  private static String parseDateOnly(final String dateTimeStr) {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    return LocalDate.parse(dateTimeStr, formatter).toString();
  }

}