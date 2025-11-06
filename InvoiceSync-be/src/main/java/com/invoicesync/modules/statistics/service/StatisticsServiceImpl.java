package com.invoicesync.modules.statistics.service;

import static com.invoicesync.core.enums.InvoiceStatus.UNPROCESSED;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.modules.invoice.InvoiceSpecification;
import com.invoicesync.modules.invoice.repository.InvoiceRepository;
import com.invoicesync.modules.receipt.dto.specification.ReceiptSpecification;
import com.invoicesync.modules.receipt.repository.ReceiptRepository;
import com.invoicesync.modules.statistics.model.StatisticsResponseDto;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StatisticsServiceImpl implements StatisticsService{

  private final InvoiceRepository invoiceRepository;
  private final ReceiptRepository receiptRepository;

  @Override
  public StatisticsResponseDto getStatistics(final Authentication connectedUser, final Long companyId) {
    final long totalInvoices = invoiceRepository.count(InvoiceSpecification.withCompanyId(companyId));
    final long totalReceipts = receiptRepository.count(ReceiptSpecification.withCompanyId(companyId));

    final long totalUnprocessedInvoices = invoiceRepository
        .count(InvoiceSpecification.withStatusAndCompany(UNPROCESSED, companyId));
    final long totalUnprocessedReceipts = receiptRepository
        .count(ReceiptSpecification.withStatusAndCompany(UNPROCESSED, companyId));

    return new StatisticsResponseDto(
        totalUnprocessedInvoices, totalInvoices,
        totalUnprocessedReceipts, totalReceipts );
  }

}
