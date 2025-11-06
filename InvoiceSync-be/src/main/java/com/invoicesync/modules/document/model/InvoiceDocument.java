package com.invoicesync.modules.document.model;

import com.invoicesync.core.common.BaseEntity;
import com.invoicesync.modules.invoice.model.Invoice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Invoice document entity representing a single document on a {@link Invoice}.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoice_document", schema = "invoice_sync")
public class InvoiceDocument extends BaseEntity {

  /**
   * Invoice to which this document belongs.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "invoice_id", nullable = false)
  private Invoice invoice;

  /**
   * Name of the file including a file type (Upload.pdf).
   */
  @Column(nullable = false)
  private String filename;

  /**
   * Path where the document is stored.
   */
  @Column(name = "document")
  private String document;

  /**
   * Description or note of a document.
   */
  @Column(name = "note")
  private String note;

  /**
   * Name of the file excluding file type (Upload).
   */
  @Column(name = "document_name")
  private String documentName;

  /**
   * Indicates if the document can be deleted.
   */
  @Column(name = "can_delete")
  private boolean canDelete;

}
