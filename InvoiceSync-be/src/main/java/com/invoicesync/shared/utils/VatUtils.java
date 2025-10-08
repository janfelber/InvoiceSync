package com.invoicesync.shared.utils;

import java.util.regex.Pattern;

public final class VatUtils {

  private static final Pattern NON_ALPHANUMERIC_PATTERN = Pattern.compile("[^0-9A-Za-z]");

  private static final Pattern PREFIX_PATTERN = Pattern.compile("^[A-Za-z]{1,2}");

  /**
   * Converts VAT id (e.g. "SK1234567890", "SK 123 456 7890", "CZ12345678") to tax id:
   * removes any leading letter-prefix (country code) and non-alphanumeric chars,
   * returns only the numeric part or null if none found.
   */
  public static String convertVatIdToTaxId(final String vatId) {
    if (vatId == null) {
      return null;
    }
    return PREFIX_PATTERN.matcher(NON_ALPHANUMERIC_PATTERN.matcher(vatId)
        .replaceAll(""))
        .replaceAll("");
  }
}
