package com.invoicesync.modules.subscription.service;

import static com.invoicesync.core.enums.SubscriptionPlan.ENTERPRISE;
import static com.invoicesync.core.enums.SubscriptionPlan.NONE;
import static com.invoicesync.modules.subscription.specification.SubscriptionSpecification.withUserId;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.core.enums.FeatureEnum;
import com.invoicesync.core.enums.SubscriptionPlan;
import com.invoicesync.modules.subscription.guard.model.LimitResponseDTO;
import com.invoicesync.modules.subscription.mapper.SubscriptionMapper;
import com.invoicesync.modules.subscription.model.Subscription;
import com.invoicesync.modules.subscription.model.SubscriptionResponseDTO;
import com.invoicesync.modules.subscription.repository.SubscriptionRepository;
import com.invoicesync.modules.user.model.User;
import com.invoicesync.modules.user.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

  private SubscriptionRepository subscriptionRepository;

  private UserRepository userRepository;

  private SubscriptionMapper subscriptionMapper;

  @Override
  public SubscriptionResponseDTO getSubscriptionPlanByUserId(final Authentication connectedUser) {
    final String userId = connectedUser.getName();
    final User user = userRepository.findById(UUID.fromString(userId))
        .orElseThrow(() -> new IllegalStateException("User not found."));

    final Subscription subscription = subscriptionRepository
        .findOne(withUserId(userId))
        .orElseGet(() -> {
          final Subscription empty = new Subscription();
          empty.setSubscriptionPlan(NONE);
          empty.setSubscriptionActive(false);
          empty.setStartDate(null);
          empty.setEndDate(null);
          empty.setSubscriptionPrice(BigDecimal.ZERO);
          empty.setMonthlyInvoiceExportLimit(NONE.getMonthlyInvoiceExportLimit());
          empty.setMonthlyInvoiceCreateLimit(NONE.getMonthlyInvoiceCreateLimit());
          empty.setMonthlyReceiptExportLimit(NONE.getMonthlyReceiptExportLimit());
          empty.setMonthlyUsedInvoiceExport(0);
          empty.setMonthlyUsedInvoiceCreate(0);
          empty.setMonthlyUsedReceiptExport(0);
          return empty;
        });

    final SubscriptionPlan plan = subscription.getSubscriptionPlan() != null
        ? subscription.getSubscriptionPlan()
        : NONE;

    if (user.hasFeature(FeatureEnum.EKON_SPECIALTY)) {
      subscription.setSubscriptionPlan(ENTERPRISE);
      subscription.setSubscriptionPrice(BigDecimal.ZERO);
      subscription.setEndDate(null);
      subscription.setMonthlyInvoiceExportLimit(ENTERPRISE.getMonthlyInvoiceExportLimit());
      subscription.setMonthlyInvoiceCreateLimit(ENTERPRISE.getMonthlyInvoiceCreateLimit());
      subscription.setMonthlyReceiptExportLimit(ENTERPRISE.getMonthlyReceiptExportLimit());
    }

    return new SubscriptionResponseDTO(
        subscription.getCreatedBy(),
        subscription.getSubscriptionPlan() != null ? subscription.getSubscriptionPlan().name() : null,
        Boolean.TRUE.equals(subscription.getSubscriptionActive()),
        subscription.getStartDate(),
        subscription.getEndDate(),
        subscription.getMonthlyUsedReceiptExport(),
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

    final User user = userRepository.findById(UUID.fromString(connectedUser.getName()))
        .orElseThrow(() -> new IllegalStateException("User not found."));

    final int totalLimit = subscription.getMonthlyInvoiceExportLimit() + subscription.getMonthlyInvoiceCreateLimit()
        + subscription.getMonthlyReceiptExportLimit();
    final int usedLimit =
        subscription.getMonthlyUsedInvoiceExport() + subscription.getMonthlyUsedInvoiceCreate()
            + subscription.getMonthlyUsedReceiptExport();

    final int invoiceExportLimit = subscription.getMonthlyInvoiceExportLimit();
    final int invoiceCreateLimit = subscription.getMonthlyInvoiceCreateLimit();
    final int receiptExportLimit = subscription.getMonthlyReceiptExportLimit();

    final int invoiceExportUsedLimit = subscription.getMonthlyUsedInvoiceExport();
    final int invoiceCreateUsedLimit = subscription.getMonthlyUsedInvoiceCreate();
    final int receiptExportUsedLimit = subscription.getMonthlyUsedReceiptExport();

    if (user.hasFeature(FeatureEnum.EKON_SPECIALTY)) {
      return new LimitResponseDTO(
          usedLimit,
          Integer.MAX_VALUE,
          Integer.MAX_VALUE,
          invoiceExportUsedLimit,
          Integer.MAX_VALUE,
          receiptExportUsedLimit,
          Integer.MAX_VALUE,
          invoiceCreateUsedLimit
      );
    }

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
