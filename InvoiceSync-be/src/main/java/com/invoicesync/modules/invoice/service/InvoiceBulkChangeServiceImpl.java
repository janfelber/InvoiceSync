package com.invoicesync.modules.invoice.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.invoicesync.core.exception.DownloadDocumentException;
import com.invoicesync.modules.activity.model.UserActivityType;
import com.invoicesync.modules.activity.service.UserActivityRecord;
import com.invoicesync.modules.activity.service.UserActivityService;
import com.invoicesync.modules.invoice.model.Invoice;
import com.invoicesync.modules.invoice.repository.InvoiceRepository;

@Service
@Transactional
public class InvoiceBulkChangeServiceImpl implements InvoiceBulkChangeService {

  private final InvoiceRepository invoiceRepository;

  private final InvoiceService invoiceService;

  private final UserActivityService userActivityService;

  public InvoiceBulkChangeServiceImpl(final InvoiceRepository invoiceRepository,
      final InvoiceService invoiceService,
      final UserActivityService userActivityService) {
    this.invoiceRepository = invoiceRepository;
    this.invoiceService = invoiceService;
    this.userActivityService = userActivityService;
  }

  @Override
  public byte[] bulkDownloadInvoices(final List<Long> invoiceIds, final Authentication connectedUser) {
    final List<Invoice> invoices = invoiceRepository.findAllById(invoiceIds);

    if (invoices.size() != invoiceIds.size()) {
      final Set<Long> foundIds = invoices.stream().map(Invoice::getId).collect(Collectors.toSet());
      final List<Long> missing = invoiceIds.stream().filter(id -> !foundIds.contains(id)).toList();
      throw new EntityNotFoundException("Invoices not found: " + missing);
    }

    invoices.forEach(invoice -> {
      if (!invoice.getCompany().getCreatedBy().equals(connectedUser.getName())) {
        throw new AccessDeniedException("You do not have access to invoice " + invoice.getId());
      }
    });

    try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ZipOutputStream zip = new ZipOutputStream(baos)) {

      for (final Invoice invoice : invoices) {
        final byte[] pdf = invoiceService.generateInvoicePdf(invoice.getId());
        zip.putNextEntry(new ZipEntry("invoice-" + invoice.getInvoiceNumber() + ".pdf"));
        zip.write(pdf);
        zip.closeEntry();
      }

      zip.finish();

      userActivityService.save(
          UserActivityRecord.forType(connectedUser.getName(), UserActivityType.BULK_INVOICE_DOWNLOAD,
              "User Generated Invoices with ids" + invoiceIds));

      return baos.toByteArray();
    } catch (IOException e) {
      throw new DownloadDocumentException("Failed to create ZIP archive", e);
    }
  }

}
