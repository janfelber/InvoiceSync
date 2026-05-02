ALTER TABLE invoice_sync.receipt
  DROP COLUMN is_paid_by_card;

ALTER TABLE invoice_sync.receipt
  ADD COLUMN payment_type VARCHAR(255);

ALTER TABLE invoice_sync.receipt
  RENAME COLUMN cash_receipt_number TO receipt_number;

ALTER TABLE invoice_sync.receipt
  RENAME COLUMN date_payment TO payment_date;

ALTER TABLE invoice_sync.receipt
  RENAME COLUMN date_tax TO tax_date;

ALTER TABLE invoice_sync.receipt
  RENAME COLUMN accounting TO account_value;

ALTER TABLE invoice_sync.receipt
  RENAME COLUMN classification_vat TO vat_classification;

ALTER TABLE invoice_sync.receipt
  RENAME COLUMN classification_kv_vat TO kv_vat_classification;

ALTER TABLE invoice_sync.receipt
  RENAME COLUMN partner_name TO supplier_name;

ALTER TABLE invoice_sync.receipt
  RENAME COLUMN partner_city TO supplier_city;

ALTER TABLE invoice_sync.receipt
  RENAME COLUMN partner_street TO supplier_street;

ALTER TABLE invoice_sync.receipt
  RENAME COLUMN partner_zip TO supplier_zip;

ALTER TABLE invoice_sync.receipt
  RENAME COLUMN partner_registration_number TO supplier_registration_number;

ALTER TABLE invoice_sync.receipt
  RENAME COLUMN partner_tax_id TO supplier_tax_id;

ALTER TABLE invoice_sync.receipt
  RENAME COLUMN partner_vat_id TO supplier_vat_id;