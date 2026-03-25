ALTER TABLE invoice_sync.app_user
  ADD COLUMN google_access_token VARCHAR(2048),
  ADD COLUMN google_refresh_token VARCHAR(512),
  ADD COLUMN google_email VARCHAR(255),
  ADD COLUMN google_connected BOOLEAN DEFAULT FALSE;