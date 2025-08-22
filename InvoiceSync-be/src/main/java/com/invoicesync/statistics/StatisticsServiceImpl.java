package com.invoicesync.statistics;

import static com.invoicesync.invoice.InvoiceStatus.UNPROCESSED;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.invoice.InvoiceRepository;
import com.invoicesync.invoice.InvoiceSpecification;
import com.invoicesync.receipt.ReceiptRepository;
import com.invoicesync.receipt.dto.specification.ReceiptSpecification;
import com.invoicesync.statistics.dto.StatisticsResponseDto;

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
