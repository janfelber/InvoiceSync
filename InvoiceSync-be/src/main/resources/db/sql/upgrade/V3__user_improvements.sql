ALTER TABLE invoice_sync.app_user ADD COLUMN created_on TIMESTAMP;
ALTER TABLE invoice_sync.app_user ADD COLUMN email VARCHAR(255);
ALTER TABLE invoice_sync.app_user ADD COLUMN phone VARCHAR(255);