CREATE TABLE invoice_sync.audit_log_user_activity
(
  id          BIGSERIAL PRIMARY KEY,
  "user"      VARCHAR   NOT NULL,
  type        VARCHAR   NOT NULL,
  description VARCHAR,
  timestamp   TIMESTAMP NOT NULL
);