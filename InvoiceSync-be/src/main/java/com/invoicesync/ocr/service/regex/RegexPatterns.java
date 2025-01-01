package com.invoicesync.ocr.service.regex;

public final class RegexPatterns {

  private RegexPatterns() {
  }

  // Regular expression for vat id(IC DPH)
  public static final String VAT_ID = "\\s?([A-Z]{2}\\s?\\d{8,12})";

  // Regular expression for iban
  public static final String IBAN = "\\s?([A-Z]{2}\\d{2}(?:\\s?\\d{4}){1,7})";;

  // Regular expression for registration Number(ICO)
  public static final String REGISTRATION_NUMBER = "\\s?([A-Z0-9]{8,12})";

  // Regular expression for tax id(DIC)
  public static final String TAX_ID = "\\s?([A-Z0-9]{8,12})";

  // Regular expression for date format DD.MM.YYYY
  public static final String DATE_DD_MM_YYYY = "\\s*(\\d{1,2}\\.\\d{1,2}\\.\\d{4})";

  // Regular expression for variable symbol
  public static final String VARIABLE_SYMBOL = "\\s*(\\d{1,10})";
}
