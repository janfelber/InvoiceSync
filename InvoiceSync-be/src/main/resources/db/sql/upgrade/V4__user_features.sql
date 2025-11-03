CREATE TABLE invoice_sync.features (
                                     code BIGINT PRIMARY KEY,
                                     text varchar DEFAULT TRUE
);

CREATE TABLE invoice_sync.user_features
(
  user_id      UUID        NOT NULL,
  code         BIGINT      NOT NULL,
  PRIMARY KEY (user_id, code),
  CONSTRAINT fk_user_features_user FOREIGN KEY (user_id)
    REFERENCES invoice_sync.app_user (id)
);