package com.invoicesync.xml.utils;

public class PohodaXmlParentTagNames {

  private PohodaXmlParentTagNames() {

  }

  public static final String NUMBER = "inv:number";

  public static final String MY_IDENTITY = "inv:myIdentity";

  public static final String PARTNER = "inv:partnerIdentity";

  public static final String INVOICE_DETAIL = "inv:invoiceDetail";

  public static final String INVOICE_ITEM = "inv:invoiceItem";

  public static final String HOME_CURRENCY = "inv:homeCurrency";

  public static final String ACCOUNTING = "inv:accounting";

  //receipt
  public static final String RECEIPT_NUMBER = "vch:number";

  public static final String RECEIPT_MY_IDENTITY = "vch:myIdentity";

  public static final String RECEIPT_PARTNER = "vch:partnerIdentity";

  public static final String RECEIPT_DETAIL = "vch:voucherDetail";

  public static final String RECEIPT_ITEM = "vch:voucherItem";

  public static final String RECEIPT_HOME_CURRENCY = "vch:homeCurrency";

}
