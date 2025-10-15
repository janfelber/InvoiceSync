CREATE SCHEMA IF NOT EXISTS invoice_sync;

CREATE TABLE IF NOT EXISTS invoice_sync.company
(
  id                  BIGSERIAL PRIMARY KEY,
  created_by          VARCHAR(255) NOT NULL,
  name                VARCHAR(255),
  city                VARCHAR(255),
  street              VARCHAR(255),
  street_number       VARCHAR(255),
  zip                 VARCHAR(20),
  registration_number VARCHAR(50),
  tax_id              VARCHAR(50),
  vat_id              VARCHAR(50),
  cash_receipt_number VARCHAR(50),
  card_receipt_number VARCHAR(50),
  created_date        TIMESTAMP    NOT NULL DEFAULT now(),
  last_modified_date  TIMESTAMP,
  last_modified_by    VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS invoice_sync.data_transfer
(
  id                 BIGSERIAL PRIMARY KEY,
  created_by         VARCHAR(255) NOT NULL,
  file_name          VARCHAR(255),
  data_json          TEXT,
  status             VARCHAR(50),
  created_date       TIMESTAMP    NOT NULL DEFAULT now(),
  last_modified_date TIMESTAMP,
  last_modified_by   VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS invoice_sync.invoice
(
  id                          BIGSERIAL PRIMARY KEY,
  company                     BIGINT,
  created_by                  VARCHAR(255) NOT NULL,
  invoice_number              VARCHAR(255),
  issue_date                  VARCHAR(255),
  tax_date                    VARCHAR(255),
  accounting_date             VARCHAR(255),
  due_date                    VARCHAR(255),
  variable_symbol             VARCHAR(255),
  partner_name                VARCHAR(255),
  partner_city                VARCHAR(255),
  partner_street              VARCHAR(255),
  partner_zip                 VARCHAR(255),
  partner_registration_number VARCHAR(255),
  partner_tax_id              VARCHAR(255),
  partner_vat_id              VARCHAR(255),
  invoice_status              VARCHAR(255),
  invoice_type                VARCHAR(255),
  created_date                TIMESTAMP    NOT NULL DEFAULT now(),
  last_modified_date          TIMESTAMP,
  last_modified_by            VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS invoice_sync.receipt
(
  id                          BIGSERIAL PRIMARY KEY,
  company                     BIGINT,
  created_by                  VARCHAR(255) NOT NULL,
  cash_receipt_number         VARCHAR(255),
  date                        VARCHAR(255),
  date_payment                VARCHAR(255),
  date_tax                    VARCHAR(255),
  partner_name                VARCHAR(255),
  partner_city                VARCHAR(255),
  partner_street              VARCHAR(255),
  partner_zip                 VARCHAR(255),
  partner_registration_number VARCHAR(255),
  partner_tax_id              VARCHAR(255),
  partner_vat_id              VARCHAR(255),
  import_date                 TIMESTAMP,
  total_price                 VARCHAR(255),
  accounting                  VARCHAR(255),
  classification_vat          VARCHAR(255),
  classification_kv_vat       VARCHAR(255),
  description                 TEXT,
  is_paid_by_card             BOOLEAN      NOT NULL DEFAULT FALSE,
  receipt_status              VARCHAR(255),
  created_date                TIMESTAMP    NOT NULL DEFAULT now(),
  last_modified_date          TIMESTAMP,
  last_modified_by            VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS invoice_sync.email_subscribe
(
  id             BIGSERIAL PRIMARY KEY,
  email          VARCHAR(255),
  subscribe_type VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS invoice_sync.subscriptions
(
  id                                BIGSERIAL PRIMARY KEY,
  created_by                        VARCHAR(255) NOT NULL,
  subscription_plan                 VARCHAR(50),
  subscription_active               BOOLEAN,
  stripe_subscription_id            VARCHAR(255),
  stripe_customer_id                VARCHAR(255),
  start_date                        TIMESTAMP,
  end_date                          TIMESTAMP,
  monthly_invoice_export_limit      INT,
  monthly_used_invoice_export       INT,
  monthly_receipt_export_limit      INT,
  monthly_used_receipt_export_limit INT,
  monthly_invoice_create_limit      INT,
  monthly_used_invoice_create_limit INT,
  subscription_price                NUMERIC(10, 2),
  total_limit                       INT,
  created_date                      TIMESTAMP    NOT NULL DEFAULT now(),
  last_modified_date                TIMESTAMP,
  last_modified_by                  VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS invoice_sync.xml_file
(
  id                 BIGSERIAL PRIMARY KEY,
  created_by         VARCHAR(255) NOT NULL,
  file_name          VARCHAR(255),
  xml_content        XML,
  created_date       TIMESTAMP    NOT NULL DEFAULT now(),
  last_modified_date TIMESTAMP,
  last_modified_by   VARCHAR(255)
);

CREATE TABLE invoice_sync.invoice_item
(
  id                BIGSERIAL PRIMARY KEY,
  invoice_id        BIGINT NOT NULL,
  name              VARCHAR(255),
  quantity          INT    NOT NULL,
  price_without_vat NUMERIC(10, 2),
  vat_rate          INT,
  account_value     VARCHAR(50),
  price_with_vat    NUMERIC(10, 2),
  account_text      TEXT,
  CONSTRAINT fk_invoice_item_invoice
    FOREIGN KEY (invoice_id)
      REFERENCES invoice_sync.invoice (id)
      ON DELETE CASCADE
);

CREATE TABLE invoice_sync.invoice_document
(
  id                 BIGSERIAL PRIMARY KEY,
  invoice_id         BIGINT       NOT NULL,
  created_by         VARCHAR(255) NOT NULL,
  filename           VARCHAR(255) NOT NULL,
  document           TEXT,
  note               TEXT,
  document_name      VARCHAR(255),
  can_delete         BOOLEAN      NOT NULL DEFAULT FALSE,
  created_date       TIMESTAMP    NOT NULL DEFAULT now(),
  last_modified_date TIMESTAMP,
  last_modified_by   VARCHAR(255),
  CONSTRAINT fk_invoice_document_invoice
    FOREIGN KEY (invoice_id)
      REFERENCES invoice_sync.invoice (id)
      ON DELETE CASCADE
);

CREATE TABLE invoice_sync.data_transfer_header
(
  id                 BIGSERIAL PRIMARY KEY,
  convert_import_id  BIGINT       NOT NULL,
  created_by         VARCHAR(255) NOT NULL,
  original_header    VARCHAR(255),
  mapped_header      VARCHAR(255),
  enabled_flag       BOOLEAN      NOT NULL DEFAULT FALSE,
  created_date       TIMESTAMP    NOT NULL DEFAULT now(),
  last_modified_date TIMESTAMP,
  last_modified_by   VARCHAR(255),
  CONSTRAINT fk_data_transfer_header
    FOREIGN KEY (convert_import_id)
      REFERENCES invoice_sync.data_transfer (id)
      ON DELETE CASCADE
);

CREATE TABLE invoice_sync.cash_documents_account
(
  id            BIGSERIAL PRIMARY KEY,
  company       BIGINT,
  class_id      VARCHAR(50),
  class_name    VARCHAR(255),
  account_id    VARCHAR(50),
  account_name  VARCHAR(255),
  category      VARCHAR(50),
  category_name VARCHAR(255),
  is_editable   BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_cash_account_company
    FOREIGN KEY (company)
      REFERENCES invoice_sync.company (id)
      ON DELETE SET NULL
);

CREATE TABLE invoice_sync.internal_documents_account
(
  id            BIGSERIAL PRIMARY KEY,
  company       BIGINT,
  class_id      VARCHAR(50),
  class_name    VARCHAR(255),
  account_id    VARCHAR(50),
  account_name  VARCHAR(255),
  category      VARCHAR(50),
  category_name VARCHAR(255),
  is_editable   BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_internal_account_company
    FOREIGN KEY (company)
      REFERENCES invoice_sync.company (id)
      ON DELETE SET NULL
);

CREATE TABLE invoice_sync.receipt_item
(
  id                BIGSERIAL PRIMARY KEY,
  receipt_id        BIGINT NOT NULL,
  name              VARCHAR(255),
  quantity          INT    NOT NULL,
  price_without_vat NUMERIC(10, 2),
  vat_rate          INT,
  account_value     VARCHAR(50),
  price_with_vat    NUMERIC(10, 2),
  account_text      TEXT,
  CONSTRAINT fk_receipt_item_receipt
    FOREIGN KEY (receipt_id)
      REFERENCES invoice_sync.receipt (id)
      ON DELETE CASCADE
);