package com.invoicesync.modules.invoice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.modules.invoice.model.InvoiceVatBreakdown;

public interface InvoiceVatBreakdownRepository extends JpaRepository<InvoiceVatBreakdown, Long> {

}
