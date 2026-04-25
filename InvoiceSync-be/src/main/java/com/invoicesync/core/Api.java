package com.invoicesync.core;

public final class Api {

  // General
  public static final String GET_BY_USER = "/user";

  public static final String SAVE = "/save";

  // Auth
  public static final String AUTH = "/auth";

  public static final String REGISTER = "/register";

  public static final String LOGIN = "/login";

  public static final String REFRESH_TOKEN = "/refresh-token";

  public static final String LOGOUT = "/logout";

  public static final String AUTH_MOBILE = "/auth/mobile";

  public static final String AUTH_MOBILE_QR = "/qr";

  public static final String AUTH_MOBILE_VERIFY = "/verify";

  // Receipt
  public static final String RECEIPT = "/receipt";

  public static final String RECEIPT_GET_BY_COMPANY = "/company/{company-id}";

  public static final String RECEIPT_GET_BY_ID = "/{receipt-id}";

  public static final String RECEIPT_GET_DOCUMENTS = "/{receipt-id}/documents";

  public static final String RECEIPT_SAVE = "/save";

  public static final String RECEIPT_UPDATE = "/update/{receiptId}";

  public static final String RECEIPT_DELETE = "/{receiptId}";

  public static final String RECEIPT_MERGE_ITEMS = "/{receipt-id}/items/merge";

  public static final String RECEIPT_UPLOAD_DOCUMENT = "/upload/document/{receipt-id}";

  public static final String RECEIPT_DELETE_DOCUMENT = "/delete/document/{document-id}";

  public static final String RECEIPT_EXPORT_POHODA = "/export/pohoda";

  public static final String RECEIPT_EXPORT_RECEIPT = "/export/{receiptId}";

  // Invoice
  public static final String INVOICE = "/invoice";

  public static final String INVOICE_GET_BY_COMPANY = "/{company-id}/invoices";

  public static final String INVOICE_GET_BY_ID = "/{invoice-id}";

  public static final String INVOICE_GET_DOCUMENTS = "/{invoice-id}/documents";

  public static final String INVOICE_CREATE = "/create-invoice";

  public static final String INVOICE_EXPORT_PDF = "/{invoice-id}/pdf";

  public static final String INVOICE_UPLOAD_DOCUMENT = "/upload/document/{invoice-id}";

  public static final String INVOICE_DELETE_DOCUMENT = "/delete/document/{document-id}";

  public static final String INVOICE_DELETE = "/delete/{invoice-id}";

  public static final String INVOICE_BULK_DOWNLOAD = "/bulk-download";

  public static final String INVOICE_SEND_EMAIL = "/send-email";

  // Company
  public static final String COMPANY = "/company";

  public static final String COMPANY_GET_BY_ID = "/{company-id}";

  public static final String COMPANY_GET_BY_REGISTRATION_NUMBER = "/by-registration-number/{registrationNumber}";

  public static final String COMPANY_SAVE_BY_REGISTRATION_NUMBER = "/save/{registrationNumber}";

  public static final String COMPANY_UPDATE = "/update/{companyId}";

  public static final String COMPANY_DELETE = "/delete/{companyId}";

  public static final String POST_ACCOUNT = "/post-account";

  // Post Accounting

  public static final String POST_ACCOUNT_GET_BY_COMPANY = "/{companyId}/accounts";

  public static final String POST_ACCOUNT_GET_CLASSES = "/{companyId}/classes";

  public static final String POST_ACCOUNT_IMPORT = "/import";

  public static final String POST_ACCOUNT_DELETE = "/{accountId}";

  // User
  public static final String USER = "/user";

  public static final String USER_ME = "/me";

  public static final String USER_ME_NAME = "/me/name";

  // Admin
  public static final String ADMIN = "/admin";

  public static final String ADMIN_GET_USERS = "/users";

  public static final String ADMIN_GET_USER = "/user/{userId}";

  public static final String ADMIN_UPDATE_USER_FEATURES = "/user/{userId}/features";

  public static final String ADMIN_USER_ACTIVITY = ADMIN + "/user-activity";

  public static final String ADMIN_USER_ACTIVITY_TABLE = "/table";

  // Subscription
  public static final String SUBSCRIPTION = "/subscription";

  public static final String SUBSCRIPTION_GET_USER_PLAN = "/user/plan";

  public static final String SUBSCRIPTION_SUBSCRIBE = "/subscribe";

  public static final String SUBSCRIPTION_GET_USER_LIMIT = "/user/limit";

  public static final String SUBSCRIPTION_CANCEL = "/cancel";

  // Stripe
  public static final String STRIPE_WEBHOOK = "/stripe/webhook";

  public static final String STRIPE_GET_USER_DEFAULT_CARD = "/stripe/user/default-card";

  public static final String STRIPE_GET_USER_BILLING_HISTORY = "/stripe/user/billing-history";

  // Statistics
  public static final String STATS = "/stats";

  public static final String STATS_BASIC = "/basic-stats/{companyId}";

  // Convert
  public static final String CONVERT = "/convert";

  public static final String CONVERT_GET_IMPORTS = "/imports";

  public static final String CONVERT_GET_BY_ID = "/{convert-id}";

  public static final String CONVERT_SAVE_MAPPING = "/{convertId}/mapping";

  public static final String CONVERT_DOWNLOAD = "/{convertId}/download";

  // XmlFile
  public static final String XML_FILE = "/xml-file";

  public static final String XML_FILE_GENERATE_ZIP = "/generate-zip/{importId}";

  // Email Subscribe
  public static final String EMAIL_SUBSCRIBE = "/email-subscribe";

  public static final String EMAIL_SUBSCRIBE_SUBSCRIBE = "/subscribe";

  // Google Integration
  public static final String GOOGLE_INTEGRATION = "/integrations/google";

  public static final String GOOGLE_INTEGRATION_URL = "/url";

  public static final String GOOGLE_INTEGRATION_CALLBACK = "/callback";

  public static final String GOOGLE_INTEGRATION_DISCONNECT = "/disconnect";

  public static final String GOOGLE_INTEGRATION_STATUS = "/status";

  // Feature
  public static final String FEATURE = "/feature";

  public static final String FEATURE_GET_BY_USER_ID = "/{user-id}";

  // Pohoda
  public static final String POHODA = "/pohoda";

  public static final String POHODA_EXPORT_RECEIVED = "/export/received";

  private Api() {
  }

}
