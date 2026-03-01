ALTER TABLE invoice_sync."receipt"
  ADD COLUMN "receipt_order" BIGINT;

CREATE TABLE invoice_sync.user_receipt_counter
(
  username   VARCHAR NOT NULL PRIMARY KEY,
  next_order BIGINT  NOT NULL DEFAULT 1
);