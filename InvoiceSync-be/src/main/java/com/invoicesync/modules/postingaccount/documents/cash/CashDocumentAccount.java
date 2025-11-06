package com.invoicesync.modules.postingaccount.documents.cash;

import com.invoicesync.modules.postingaccount.model.BaseDocumentAccountEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a single account from the company's accounts for cash documents (receipt paid by cash)
 */
@Data
@Entity
@AllArgsConstructor
@Table(name = "cash_documents_account", schema = "invoice_sync")
public class CashDocumentAccount extends BaseDocumentAccountEntity {

}
