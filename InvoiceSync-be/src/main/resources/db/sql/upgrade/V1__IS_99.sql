ALTER TABLE invoice_sync.invoice
  ADD COLUMN company BIGINT,
  ADD CONSTRAINT company_fk_company FOREIGN KEY (company) REFERENCES invoice_sync.company(id)

