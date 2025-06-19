package com.invoicesync.service;

import static com.invoicesync.subscription.SubscriptionPlan.FREE;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.invoicesync.module.Subscription;

@Service
public class SubscriptionMapper {

  public static final int DAYS = 30;

  public Subscription toFreeSubscription() {
    final LocalDateTime start = LocalDateTime.now();

    return Subscription.builder()
        .subscriptionPlan(FREE)
        .subscriptionActive(true)
        .startDate(start)
        .endDate(start.plusDays(DAYS))
        .monthlyInvoiceExportLimit(FREE.getMonthlyInvoiceExportLimit())
        .monthlyUsedInvoiceExportLimit(0)
        .monthlyReceiptExportLimit(FREE.getMonthlyReceiptExportLimit())
        .monthlyUsedReceiptExportLimit(0)
        .monthlyInvoiceCreateLimit(FREE.getMonthlyInvoiceCreateLimit())
        .monthlyUsedInvoiceCreateLimit(0)
        .totalLimit(FREE.getMonthlyInvoiceExportLimit() + FREE.getMonthlyInvoiceCreateLimit()
            + FREE.getMonthlyReceiptExportLimit())
        .subscriptionPrice(BigDecimal.valueOf(FREE.getMonthlyPrice()))
        .build();
  }

}
