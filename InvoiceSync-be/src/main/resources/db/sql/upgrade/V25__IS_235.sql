-- Email: enforce presence and case-insensitive uniqueness (app-level checks alone are race-prone)
ALTER TABLE invoice_sync.app_user
  ALTER COLUMN email SET NOT NULL;

CREATE UNIQUE INDEX uq_app_user_email_lower ON invoice_sync.app_user (LOWER(email));

-- Username: exact-case UNIQUE already exists on the column, add case-insensitive uniqueness too
CREATE UNIQUE INDEX uq_app_user_username_lower ON invoice_sync.app_user (LOWER(username));

-- Invoice/receipt must always belong to a company: enforce referential integrity at DB level
ALTER TABLE invoice_sync.invoice
  ALTER COLUMN company SET NOT NULL;

ALTER TABLE invoice_sync.invoice
  ADD CONSTRAINT fk_invoice_company FOREIGN KEY (company)
    REFERENCES invoice_sync.company (id);

ALTER TABLE invoice_sync.receipt
  ALTER COLUMN company SET NOT NULL;

ALTER TABLE invoice_sync.receipt
  ADD CONSTRAINT fk_receipt_company FOREIGN KEY (company)
    REFERENCES invoice_sync.company (id);

-- Owner/tenant filter columns used on every list/detail query
CREATE INDEX idx_invoice_created_by ON invoice_sync.invoice (created_by);
CREATE INDEX idx_invoice_company ON invoice_sync.invoice (company);
CREATE INDEX idx_receipt_created_by ON invoice_sync.receipt (created_by);
CREATE INDEX idx_receipt_company ON invoice_sync.receipt (company);
CREATE INDEX idx_company_created_by ON invoice_sync.company (created_by);

-- Remaining foreign key columns with no supporting index
CREATE INDEX idx_invoice_item_invoice_id ON invoice_sync.invoice_item (invoice_id);
CREATE INDEX idx_invoice_document_invoice_id ON invoice_sync.invoice_document (invoice_id);
CREATE INDEX idx_receipt_item_receipt_id ON invoice_sync.receipt_item (receipt_id);
CREATE INDEX idx_receipt_document_receipt_id ON invoice_sync.receipt_document (receipt_id);
CREATE INDEX idx_data_transfer_header_convert_import_id ON invoice_sync.data_transfer_header (convert_import_id);
CREATE INDEX idx_cash_documents_account_company ON invoice_sync.cash_documents_account (company);
CREATE INDEX idx_internal_documents_account_company ON invoice_sync.internal_documents_account (company);
CREATE INDEX idx_email_verification_token_user_id ON invoice_sync.email_verification_token (user_id);
CREATE INDEX idx_password_reset_token_user_id ON invoice_sync.password_reset_token (user_id);
CREATE INDEX idx_mobile_session_user_id ON invoice_sync.mobile_session (user_id);
CREATE INDEX idx_user_features_user_id ON invoice_sync.user_features (user_id);

-- Stripe webhook idempotency: dedup on event id so retried/duplicate deliveries are no-ops
CREATE TABLE invoice_sync.stripe_processed_event
(
  event_id     VARCHAR(255) PRIMARY KEY,
  processed_at TIMESTAMP NOT NULL DEFAULT now()
);
