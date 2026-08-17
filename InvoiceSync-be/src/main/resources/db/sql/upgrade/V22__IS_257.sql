CREATE TABLE invoice_sync.login_attempt
(
  id           BIGSERIAL PRIMARY KEY,
  username     VARCHAR(255) NOT NULL,
  ip           TEXT         NOT NULL,
  attempted_at TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE invoice_sync.ban_login
(
  id        BIGSERIAL PRIMARY KEY,
  username  VARCHAR(255) NOT NULL,
  ban_until TIMESTAMP    NOT NULL
)
