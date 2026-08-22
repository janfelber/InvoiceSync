CREATE TABLE invoice_sync.email_verification_token
(
  id         UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
  token_hash VARCHAR(255) UNIQUE NOT NULL,
  user_id    UUID                NOT NULL,
  expires_at TIMESTAMP           NOT NULL,
  used       BOOLEAN             NOT NULL DEFAULT FALSE,

  CONSTRAINT fk_email_verification_token_user FOREIGN KEY (user_id)
    REFERENCES invoice_sync.app_user (id)
);

ALTER TABLE invoice_sync.app_user
  ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE;
