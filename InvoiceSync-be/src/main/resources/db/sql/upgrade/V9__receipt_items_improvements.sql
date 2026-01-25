DROP TABLE invoice_sync.receipt_item;


CREATE TABLE invoice_sync.receipt_item
(
  id                           BIGSERIAL PRIMARY KEY,
  receipt_id                   BIGINT  NOT NULL,
  name                         VARCHAR(255),
  quantity                     INTEGER NOT NULL,
  vat_rate                     INTEGER,
  unit_price_without_vat       NUMERIC(10, 2),
  unit_price_with_vat          NUMERIC(10, 2),
  total_item_price_without_vat NUMERIC(10, 2),
  total_item_price_with_vat    NUMERIC(10, 2),
  account_value                VARCHAR(50),
  account_text                 TEXT,
  CONSTRAINT fk_receipt_item FOREIGN KEY (receipt_id)
    REFERENCES invoice_sync.receipt (id)
);

ALTER TABLE invoice_sync.receipt
  RENAME COLUMN total_price to total_price_with_vat;

ALTER TABLE invoice_sync.receipt
  ALTER COLUMN total_price_with_vat TYPE NUMERIC(10, 2)
    USING total_price_with_vat::NUMERIC(10, 2);

ALTER TABLE invoice_sync.receipt
  ADD COLUMN total_price_without_vat NUMERIC(10, 2);