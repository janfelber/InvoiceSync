package com.invoicesync.modules.postingaccount.documents.internal;

import com.invoicesync.modules.postingaccount.model.BaseDocumentAccountEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a single account from the company's accounts for internal documents (receipt paid by card).
 */
@Data
@Entity
@AllArgsConstructor
@Table(name = "internal_documents_account", schema = "invoice_sync")
public class InternalDocumentAccount extends BaseDocumentAccountEntity {

}
