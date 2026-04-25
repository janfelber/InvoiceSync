ALTER TABLE invoice_sync.receipt
  DROP COLUMN is_paid_by_card;

ALTER TABLE invoice_sync.receipt
  ADD COLUMN payment_type VARCHAR(255);

ALTER TABLE invoice_sync.receipt
  RENAME COLUMN cash_receipt_number TO receipt_number;