package com.invoicesync.modules.invoice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.modules.invoice.model.Invoice;
import com.invoicesync.modules.invoice.model.InvoiceVatBreakdown;

public interface InvoiceVatBreakdownRepository extends JpaRepository<InvoiceVatBreakdown, Long> {

  List<InvoiceVatBreakdown> findByInvoice(Invoice invoice);

}
