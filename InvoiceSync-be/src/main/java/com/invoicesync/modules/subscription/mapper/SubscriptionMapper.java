package com.invoicesync.modules.subscription.mapper;

import static com.invoicesync.core.enums.SubscriptionPlan.FREE;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Service;

import com.invoicesync.core.enums.SubscriptionPlan;
import com.invoicesync.modules.subscription.model.Subscription;
import com.stripe.model.Invoice;

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
        .monthlyUsedInvoiceExport(0)
        .monthlyReceiptExportLimit(FREE.getMonthlyReceiptExportLimit())
        .monthlyUsedReceiptExport(0)
        .monthlyInvoiceCreateLimit(FREE.getMonthlyInvoiceCreateLimit())
        .monthlyUsedInvoiceCreate(0)
        .totalLimit(FREE.getMonthlyInvoiceExportLimit() + FREE.getMonthlyInvoiceCreateLimit()
            + FREE.getMonthlyReceiptExportLimit())
        .subscriptionPrice(BigDecimal.valueOf(FREE.getMonthlyPrice()))
        .build();
  }

  public Subscription fromStripeInvoice(final Invoice invoice, final String userIdStr) {
    final var line = invoice.getLines().getData().getFirst();

    final Instant start = Instant.ofEpochSecond(line.getPeriod().getStart());
    final Instant end = Instant.ofEpochSecond(line.getPeriod().getEnd());

    final LocalDateTime startDate = LocalDateTime.ofInstant(start, ZoneId.systemDefault());
    final LocalDateTime endDate = LocalDateTime.ofInstant(end, ZoneId.systemDefault());

    final String priceId = line.getPricing().getPriceDetails().getPrice();
    final SubscriptionPlan plan = SubscriptionPlan.fromStripePriceId(priceId);

    final Subscription subscription = new Subscription();
    subscription.setSubscriptionPlan(plan);
    subscription.setStartDate(startDate);
    subscription.setEndDate(endDate);
    subscription.setSubscriptionActive(true);
    subscription.setStripeSubscriptionId(invoice.getParent().getSubscriptionDetails().getSubscription());
    subscription.setCreatedBy(userIdStr);

    subscription.setMonthlyInvoiceExportLimit(plan.getMonthlyInvoiceExportLimit());
    subscription.setMonthlyInvoiceCreateLimit(plan.getMonthlyInvoiceCreateLimit());
    subscription.setMonthlyReceiptExportLimit(plan.getMonthlyReceiptExportLimit());
    subscription.setMonthlyUsedInvoiceCreate(0);
    subscription.setMonthlyUsedInvoiceExport(0);
    subscription.setMonthlyUsedReceiptExport(0);

    return subscription;
  }

}
