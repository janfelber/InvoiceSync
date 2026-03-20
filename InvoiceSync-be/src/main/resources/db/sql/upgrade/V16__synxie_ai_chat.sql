CREATE TABLE invoice_sync.conversation
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    VARCHAR(255) NOT NULL,
    title      VARCHAR(255),
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE invoice_sync.message
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID        NOT NULL,
    role            VARCHAR(20) NOT NULL,
    content         TEXT        NOT NULL,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_message_conversation FOREIGN KEY (conversation_id)
        REFERENCES invoice_sync.conversation (id) ON DELETE CASCADE
);

CREATE INDEX idx_message_conversation_id ON invoice_sync.message (conversation_id);
CREATE INDEX idx_conversation_user_id ON invoice_sync.conversation (user_id);
