CREATE TABLE invoice_sync.refresh_session
(
  id           UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
  token_hash   VARCHAR(255) UNIQUE NOT NULL,
  user_id      UUID                NOT NULL,
  family_id    UUID                NOT NULL,
  device_info  VARCHAR(512),
  created_at   TIMESTAMP           NOT NULL DEFAULT now(),
  expires_at   TIMESTAMP           NOT NULL,
  last_used_at TIMESTAMP,
  revoked      BOOLEAN             NOT NULL DEFAULT FALSE,
  revoked_at   TIMESTAMP,
  CONSTRAINT fk_refresh_session_user FOREIGN KEY (user_id)
    REFERENCES invoice_sync.app_user (id)
);

CREATE INDEX idx_refresh_session_family_id ON invoice_sync.refresh_session (family_id);
CREATE INDEX idx_refresh_session_user_id ON invoice_sync.refresh_session (user_id);
CREATE INDEX idx_refresh_session_token_hash ON invoice_sync.refresh_session (token_hash);

