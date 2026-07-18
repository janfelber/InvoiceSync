-- add new columns to invoice - total price without vat and with vat
ALTER TABLE invoice_sync.invoice
  ADD COLUMN total_price_without_vat NUMERIC(12, 2),
  ADD COLUMN total_price_with_vat    NUMERIC(12, 2);
