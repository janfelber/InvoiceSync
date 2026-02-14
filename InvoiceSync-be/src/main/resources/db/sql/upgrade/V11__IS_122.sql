CREATE TABLE invoice_sync.mobile_session
(
  id            UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
  session_token VARCHAR(255) NOT NULL UNIQUE,
  user_id       UUID         NOT NULL,
  expires_at    TIMESTAMP    NOT NULL,
  used          BOOLEAN      NOT NULL DEFAULT FALSE,
  created_at    TIMESTAMP    NOT NULL DEFAULT now(),
  CONSTRAINT fk_mobile_session_user FOREIGN KEY (user_id)
    REFERENCES invoice_sync.app_user (id)
);

CREATE TABLE invoice_sync.mobile_api_key
(
  id           UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
  api_key_hash VARCHAR(255) NOT NULL UNIQUE,
  user_id      UUID         NOT NULL,
  device_name  VARCHAR(255),
  created_at   TIMESTAMP    NOT NULL DEFAULT now(),
  last_used_at TIMESTAMP,
  active       BOOLEAN      NOT NULL DEFAULT TRUE,
  CONSTRAINT fk_mobile_api_key_user FOREIGN KEY (user_id)
    REFERENCES invoice_sync.app_user (id)
);

CREATE INDEX idx_mobile_api_key_hash ON invoice_sync.mobile_api_key (api_key_hash);
CREATE INDEX idx_mobile_api_key_user ON invoice_sync.mobile_api_key (user_id);
CREATE INDEX idx_mobile_session_token ON invoice_sync.mobile_session (session_token);