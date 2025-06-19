package com.invoicesync.subscription;

import static com.invoicesync.subscription.enums.SubscriptionPlan.NONE;
import static com.invoicesync.subscription.specification.SubscriptionSpecification.withUserId;

import java.math.BigDecimal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.subscription.dto.SubscriptionResponseDTO;
import com.invoicesync.subscription.enums.SubscriptionPlan;
import com.invoicesync.subscription.guard.LimitResponseDTO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

  private SubscriptionRepository subscriptionRepository;

  private SubscriptionMapper subscriptionMapper;

  @Override
  public SubscriptionResponseDTO getSubscriptionPlanByUserId(final Authentication connectedUser) {
    final String userId = connectedUser.getName();

    final Subscription subscription = subscriptionRepository
        .findOne(withUserId(userId))
        .orElseGet(() -> {
          final Subscription empty = new Subscription();
          empty.setCreatedBy(userId);
          empty.setSubscriptionPlan(NONE);
          empty.setSubscriptionActive(false);
          empty.setStartDate(null);
          empty.setEndDate(null);
          empty.setSubscriptionPrice(BigDecimal.ZERO);
          empty.setMonthlyInvoiceExportLimit(NONE.getMonthlyInvoiceExportLimit());
          empty.setMonthlyInvoiceCreateLimit(NONE.getMonthlyInvoiceCreateLimit());
          empty.setMonthlyReceiptExportLimit(NONE.getMonthlyReceiptExportLimit());
          empty.setMonthlyUsedInvoiceExportLimit(0);
          empty.setMonthlyUsedInvoiceCreateLimit(0);
          empty.setMonthlyUsedReceiptExportLimit(0);
          return empty;
        });

    final SubscriptionPlan plan = subscription.getSubscriptionPlan() != null
        ? subscription.getSubscriptionPlan()
        : NONE;

    return new SubscriptionResponseDTO(
        subscription.getCreatedBy(),
        subscription.getSubscriptionPlan() != null ? subscription.getSubscriptionPlan().name() : null,
        Boolean.TRUE.equals(subscription.getSubscriptionActive()),
        subscription.getStartDate(),
        subscription.getEndDate(),
        subscription.getMonthlyUsedReceiptExportLimit(),
        subscription.getSubscriptionPrice(),
        plan.getFeatures()
    );
  }

  @Override
  public void createFreeSubscriptionForUser(final Authentication connectedUser) {
    final Subscription subscription = subscriptionMapper.toFreeSubscription();
    subscriptionRepository.save(subscription);
  }

  @Override
  public LimitResponseDTO getUserLimits(final Authentication connectedUser) {
    final Subscription subscription = subscriptionRepository
        .findOne(withUserId(connectedUser.getName()))
        .orElseThrow(() -> new IllegalStateException("Subscription not found for user: " + connectedUser.getName()));

    final int totalLimit = subscription.getMonthlyInvoiceExportLimit() + subscription.getMonthlyInvoiceCreateLimit()
        + subscription.getMonthlyReceiptExportLimit();
    final int usedLimit =
        subscription.getMonthlyUsedInvoiceExportLimit() + subscription.getMonthlyUsedInvoiceCreateLimit()
            + subscription.getMonthlyUsedReceiptExportLimit();

    final int invoiceExportLimit = subscription.getMonthlyInvoiceExportLimit();
    final int invoiceCreateLimit = subscription.getMonthlyInvoiceCreateLimit();
    final int receiptExportLimit = subscription.getMonthlyReceiptExportLimit();

    final int invoiceExportUsedLimit = subscription.getMonthlyUsedInvoiceExportLimit();
    final int invoiceCreateUsedLimit = subscription.getMonthlyUsedInvoiceCreateLimit();
    final int receiptExportUsedLimit = subscription.getMonthlyUsedReceiptExportLimit();

    return new LimitResponseDTO(
        usedLimit,
        totalLimit,
        invoiceExportLimit,
        invoiceExportUsedLimit,
        receiptExportLimit,
        receiptExportUsedLimit,
        invoiceCreateLimit,
        invoiceCreateUsedLimit
    );
  }

}
