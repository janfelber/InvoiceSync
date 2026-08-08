CREATE TABLE invoice_sync.invoice_vat_breakdown
(
  id              BIGSERIAL PRIMARY KEY,
  invoice_id      BIGINT         NOT NULL,
  vat_rate        INTEGER        NOT NULL,
  sum_without_vat NUMERIC(12, 2) NOT NULL,
  sum_vat         NUMERIC(12, 2) NOT NULL,
  sum_with_vat    NUMERIC(12, 2) NOT NULL,
  CONSTRAINT fk_invoice FOREIGN KEY (invoice_id)
    REFERENCES invoice_sync.invoice (id),
  CONSTRAINT uq_invoice_vat_breakdown_invoice_rate UNIQUE (invoice_id, vat_rate)
);

