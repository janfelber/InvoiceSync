CREATE TABLE invoice_sync.receipt_document (
    id BIGSERIAL PRIMARY KEY,
    receipt_id BIGINT NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    filename VARCHAR(255) NOT NULL,
    document TEXT,
    note TEXT,
    document_name VARCHAR(255),
    can_delete BOOLEAN DEFAULT FALSE NOT NULL,
    created_date TIMESTAMP DEFAULT NOW() NOT NULL,
    last_modified_date TIMESTAMP,
    last_modified_by VARCHAR(255),
    CONSTRAINT fk_receipt_document FOREIGN KEY (receipt_id) REFERENCES invoice_sync.receipt(id)
);