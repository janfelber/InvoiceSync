package com.invoicesync.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.invoicesync.module.Subscription;
import com.invoicesync.subscription.SubscriptionPlan;

@Service
public class SubscriptionMapper {

  public static final int DAYS = 30;

  public Subscription toFreeSubscription() {
    final SubscriptionPlan freeSub = SubscriptionPlan.FREE;
    final LocalDateTime start = LocalDateTime.now();

    return Subscription.builder()
        .subscriptionPlan(freeSub)
        .subscriptionActive(true)
        .startDate(start)
        .endDate(start.plusDays(DAYS))
        .monthlyInvoiceLimit(freeSub.getMonthlyInvoiceLimit())
        .subscriptionPrice(BigDecimal.valueOf(freeSub.getMonthlyPrice()))
        .build();
  }

}
