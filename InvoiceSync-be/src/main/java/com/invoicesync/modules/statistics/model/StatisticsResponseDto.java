package com.invoicesync.modules.statistics.model;

public record StatisticsResponseDto(
    long totalUnprocessedInvoices,
    long totalInvoices,
    long totalUnprocessedReceipts,
    long totalReceipts
) {}
