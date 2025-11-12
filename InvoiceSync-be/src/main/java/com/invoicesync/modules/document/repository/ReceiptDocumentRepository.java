package com.invoicesync.modules.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.modules.document.model.ReceiptDocument;

public interface ReceiptDocumentRepository extends JpaRepository<ReceiptDocument, Long> {

  List<ReceiptDocument> findByReceiptId(Long receiptId);

}
