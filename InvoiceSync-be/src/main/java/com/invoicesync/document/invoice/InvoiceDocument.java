package com.invoicesync.document.invoice;

import com.invoicesync.invoice.Invoice;
import com.invoicesync.shared.common.BaseEntity;

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

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoice_document", schema = "invoice_sync")
public class InvoiceDocument extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "invoice_id", nullable = false)
  private Invoice invoice;

  @Column(nullable = false)
  private String filename;

  @Column(name = "document")
  private String document;

  @Column(name = "note")
  private String note;

  @Column(name = "document_name")
  private String documentName;

  @Column(name = "can_delete")
  private boolean canDelete;

}
