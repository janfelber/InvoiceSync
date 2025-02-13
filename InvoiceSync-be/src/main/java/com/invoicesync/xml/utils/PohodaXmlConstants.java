package com.invoicesync.xml.utils;

public final class PohodaXmlConstants {

  private PohodaXmlConstants() {
  }

  // General invoice keys
  public static final String INVOICE_TYPE = "inv:invoiceType";

  public static final String INVOICE_NUMBER = "typ:numberRequested";

  public static final String VARIABLE_SYMBOL = "inv:symVar";

  public static final String PAIRING_SYMBOL = "inv:symPar";

  public static final String DATE = "inv:date";

  public static final String DATE_TAX = "inv:dateTax";

  public static final String DATE_ACCOUNTING = "inv:dateAccounting";

  public static final String DATE_DUE = "inv:dateDue";

  public static final String CITY = "typ:city";

  public static final String STREET = "typ:street";

  public static final String ZIP = "typ:zip";

  public static final String VAT_ID = "typ:icDph";

  public static final String NAME = "typ:name";

  public static final String COMPANY = "typ:company";

  public static final String STREET_NUMBER = "typ:number";

  public static final String REGISTRATION_NUMBER = "typ:ico";

  public static final String TAX_ID = "typ:dic";

  // Invoice item
  public static final String TEXT = "inv:text";

  public static final String QUANTITY = "inv:quantity";

  public static final String COEFFICIENT = "inv:coefficient";

  public static final String PAY_VAT = "inv:payVAT";

  public static final String RATE_VAT = "inv:rateVAT";

  public static final String DISCOUNT_PERCENTAGE = "inv:discountPercentage";

  public static final String UNIT_PRICE = "typ:unitPrice";

  public static final String PRICE = "typ:price";

  public static final String PRICE_VAT = "typ:priceVAT";

  public static final String PRICE_SUM = "typ:priceSum";

  public static final String ACCOUNT_VALUE = "typ:ids";

  // receipt
  public static final String RECEIPT_DATE = "vch:date";

  public static final String RECEIPT_DATE_PAYMENT = "vch:datePayment";

  public static final String RECEIPT_TEXT = "vch:text";

  public static final String RECEIPT_DATE_TAX = "vch:dateTax";

  public static final String RECEIPT_QUANTITY = "vch:quantity";

}
