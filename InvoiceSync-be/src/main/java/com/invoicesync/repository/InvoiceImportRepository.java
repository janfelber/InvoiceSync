package com.invoicesync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoicesync.module.InvoiceImport;

@Repository
public interface InvoiceImportRepository extends JpaRepository<InvoiceImport, Integer> {
}