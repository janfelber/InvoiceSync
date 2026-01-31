DROP TABLE invoice_sync.invoice_item;


CREATE TABLE invoice_sync.invoice_item
(
  id                           BIGSERIAL PRIMARY KEY,
  invoice_id                   BIGINT  NOT NULL,
  name                         VARCHAR(255),
  quantity                     INTEGER NOT NULL,
  unit_type                    VARCHAR(100),
  vat_rate                     INTEGER,
  unit_price_without_vat       NUMERIC(10, 2),
  unit_price_with_vat          NUMERIC(10, 2),
  total_item_price_without_vat NUMERIC(10, 2),
  total_item_price_with_vat    NUMERIC(10, 2),
  account_value                VARCHAR(50),
  account_text                 TEXT,
  CONSTRAINT fk_invoice_item FOREIGN KEY (invoice_id)
    REFERENCES invoice_sync.invoice (id)
);