CREATE TABLE invoice_sync.password_reset_token
(
  id         UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
  token_hash VARCHAR(255) UNIQUE NOT NULL,
  user_id    UUID                NOT NULL,
  expires_at TIMESTAMP           NOT NULL,
  used       BOOLEAN             NOT NULL DEFAULT FALSE,

  CONSTRAINT fk_password_reset_token FOREIGN KEY (user_id)
    REFERENCES invoice_sync.app_user (id)
);
