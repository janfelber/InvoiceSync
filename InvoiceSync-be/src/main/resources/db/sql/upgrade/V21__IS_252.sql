ALTER TABLE invoice_sync.invoice
  ADD COLUMN account_value VARCHAR(255);
ALTER TABLE invoice_sync.invoice
  ADD COLUMN classification_vat VARCHAR(255);
ALTER TABLE invoice_sync.invoice
  ADD COLUMN classification_kv_vat VARCHAR(255);
ALTER TABLE invoice_sync.invoice
  ADD COLUMN description VARCHAR(255);
