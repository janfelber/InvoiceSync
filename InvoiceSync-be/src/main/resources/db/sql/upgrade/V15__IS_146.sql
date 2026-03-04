-- drop table wrongly created in V14__IS_145.sql
DROP TABLE invoice_sync.audit_log_user_activity;

-- create table
CREATE TABLE invoice_sync.audit_log_user_activity
(
  id          BIGSERIAL PRIMARY KEY,
  user_id     VARCHAR   NOT NULL,
  type        VARCHAR   NOT NULL,
  description VARCHAR,
  timestamp   TIMESTAMP NOT NULL
);
