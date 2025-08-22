package com.invoicesync.statistics.dto;

public record StatisticsResponseDto(
    long totalUnprocessedInvoices,
    long totalInvoices,
    long totalUnprocessedReceipts,
    long totalReceipts
) {}
