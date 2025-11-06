package com.invoicesync.modules.xml.utils;

public final class PohodaXmlConstants {

  private PohodaXmlConstants() {
  }

  public static class General {

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

    public static final String SURNAME = "typ:surname";

    public static final String STREET_NUMBER = "typ:number";

    public static final String REGISTRATION_NUMBER = "typ:ico";

    public static final String TAX_ID = "typ:dic";

    public static final String UNIT_PRICE = "typ:unitPrice";

    public static final String PRICE = "typ:price";

    public static final String PRICE_VAT = "typ:priceVAT";

    public static final String PRICE_SUM = "typ:priceSum";

    public static final String ACCOUNT_VALUE = "typ:ids";

  }
  // General invoice keys

  // Invoice item
  public static final String TEXT = "inv:text";

  public static final String QUANTITY = "inv:quantity";

  public static final String COEFFICIENT = "inv:coefficient";

  public static final String PAY_VAT = "inv:payVAT";

  public static final String RATE_VAT = "inv:rateVAT";

  public static final String DISCOUNT_PERCENTAGE = "inv:discountPercentage";

  public static class ReceivedInvoice {

    public static final String ACCOUNTING = "inv:accounting";

    public static final String CLASSIFICATION_VAT = "inv:classificationVAT";

    public static final String CLASSIFICATION_KV_VAT = "inv:classificationKVVAT";

    public static final String TEXT = "inv:text";

    public static final String MY_IDENTITY = "inv:myIdentity";

    public static final String PARTNER = "inv:partnerIdentity";

    public static final String DETAIL = "inv:invoiceDetail";

    public static final String ITEM = "inv:invoiceItem";

    public static final String HOME_CURRENCY = "inv:homeCurrency";

  }

  // receipt

  public static class ReceiptCash {

    public static final String NUMBER = "vch:number";

    public static final String MY_IDENTITY = "vch:myIdentity";

    public static final String PARTNER = "vch:partnerIdentity";

    public static final String DETAIL = "vch:voucherDetail";

    public static final String ITEM = "vch:voucherItem";

    public static final String HOME_CURRENCY = "vch:homeCurrency";

    public static final String DATE = "vch:date";

    public static final String DATE_PAYMENT = "vch:datePayment";

    public static final String TEXT = "vch:text";

    public static final String DATE_TAX = "vch:dateTax";

    public static final String ACCOUNTING = "vch:accounting";

    public static final String CLASSIFICATION_VAT = "vch:classificationVAT";

    public static final String CLASSIFICATION_KV_VAT = "vch:classificationKVDPH";

    public static final String QUANTITY = "vch:quantity";

    public static final String COEFFICIENT = "vch:coefficient";

    public static final String PAY_VAT = "vch:payVAT";

    public static final String RATE_VAT = "vch:rateVAT";

    public static final String DISCOUNT_PERCENTAGE = "vch:discountPercentage";

  }

  // receipt paid by card
  public static class ReceiptCard {

    public static final String DATE = "int:date";

    public static final String DATE_PAYMENT = "int:datePayment";

    public static final String TEXT = "int:text";

    public static final String DATE_TAX = "int:dateTax";

    public static final String ACCOUNTING = "int:accounting";

    public static final String CLASSIFICATION_VAT = "int:classificationVAT";

    public static final String CLASSIFICATION_KV_VAT = "int:classificationKVDPH";

    public static final String QUANTITY = "int:quantity";

    public static final String MY_IDENTITY = "int:myIdentity";

    public static final String PARTNER = "int:partnerIdentity";

    public static final String DETAIL = "int:intDocDetail";

    public static final String ITEM = "int:intDocItem";

    public static final String HOME_CURRENCY = "int:homeCurrency";

    public static final String COEFFICIENT = "int:coefficient";

    public static final String PAY_VAT = "int:payVAT";

    public static final String RATE_VAT = "int:rateVAT";

    public static final String DISCOUNT_PERCENTAGE = "int:discountPercentage";

  }
}
