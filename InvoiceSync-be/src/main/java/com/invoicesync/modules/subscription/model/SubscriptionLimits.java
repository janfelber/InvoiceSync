package com.invoicesync.modules.subscription.model;

public final class SubscriptionLimits {

  public static final int NONE_INVOICE_EXPORT_LIMIT = 0;
  public static final int NONE_RECEIPT_EXPORT_LIMIT = 0;
  public static final int NONE_INVOICE_CREATE_LIMIT = 0;

  public static final int FREE_INVOICE_EXPORT_LIMIT = 20;
  public static final int FREE_RECEIPT_EXPORT_LIMIT = 20;
  public static final int FREE_INVOICE_CREATE_LIMIT = 10;

  public static final int ESSENTIALS_INVOICE_EXPORT_LIMIT = 150;
  public static final int ESSENTIALS_RECEIPT_EXPORT_LIMIT = 50;
  public static final int ESSENTIALS_INVOICE_CREATE_LIMIT = 50;

  public static final int PRO_INVOICE_EXPORT_LIMIT = 500;
  public static final int PRO_RECEIPT_EXPORT_LIMIT = 150;
  public static final int PRO_INVOICE_CREATE_LIMIT = 150;

  public static final int ENTERPRISE_INVOICE_EXPORT_LIMIT = 2000;
  public static final int ENTERPRISE_RECEIPT_EXPORT_LIMIT = 1000;
  public static final int ENTERPRISE_INVOICE_CREATE_LIMIT = Integer.MAX_VALUE;

  private SubscriptionLimits() {}

}
