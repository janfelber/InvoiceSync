package com.invoicesync.modules.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.modules.document.model.InvoiceDocument;

public interface InvoiceDocumentRepository extends JpaRepository<InvoiceDocument, Long> {
  List<InvoiceDocument> findByInvoiceId(Long invoiceId);

}
