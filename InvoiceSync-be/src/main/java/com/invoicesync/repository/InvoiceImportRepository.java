package com.invoicesync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoicesync.module.InvoiceImport;

import java.util.List;

@Repository
public interface InvoiceImportRepository extends JpaRepository<InvoiceImport, Long> {

    List<InvoiceImport> findByUserId(Long id);
}