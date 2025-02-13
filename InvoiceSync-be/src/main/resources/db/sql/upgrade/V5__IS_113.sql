CREATE TABLE invoice_sync.receipt (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT REFERENCES invoice_sync.user_credential(id),
  company BIGINT REFERENCES invoice_sync.company(id),
  date varchar,
  date_payment varchar,
  date_tax varchar,
  partner_name varchar,
  partner_city varchar,
  partner_street varchar,
  partner_zip varchar,
  partner_registration_number varchar,
  partner_tax_id varchar,
  partner_vat_id varchar
)