package com.invoicesync.document.invoice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceDocumentRepository extends JpaRepository<InvoiceDocument, Long> {
  List<InvoiceDocument> findByInvoiceId(Long invoiceId);

}
