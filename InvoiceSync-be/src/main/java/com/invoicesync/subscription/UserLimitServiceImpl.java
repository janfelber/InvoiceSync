package com.invoicesync.subscription;

import static com.invoicesync.subscription.specification.SubscriptionSpecification.withUserId;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.subscription.enums.LimitType;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserLimitServiceImpl implements UserService{

  private final SubscriptionRepository subscriptionRepository;

  @Override
  public int getUsed(final Authentication connectedUser, final LimitType type) {
    final Subscription subscription = getSubscription(connectedUser);
    return switch (type) {
      case INVOICE_EXPORT -> subscription.getMonthlyUsedInvoiceExportLimit();
      case RECEIPT_EXPORT -> subscription.getMonthlyUsedReceiptExportLimit();
      case INVOICE_CREATE -> subscription.getMonthlyUsedInvoiceCreateLimit();
    };
  }

  @Override
  public void incrementUsed(final Authentication connectedUser, final LimitType limitType) {
    final Subscription subscription = getSubscription(connectedUser);
    switch (limitType) {
      case INVOICE_EXPORT -> subscription.setMonthlyUsedInvoiceExportLimit(subscription.getMonthlyUsedInvoiceExportLimit() + 1);
      case RECEIPT_EXPORT -> subscription.setMonthlyUsedReceiptExportLimit(subscription.getMonthlyUsedReceiptExportLimit() + 1);
      case INVOICE_CREATE -> subscription.setMonthlyUsedInvoiceCreateLimit(subscription.getMonthlyUsedInvoiceCreateLimit() + 1);
    };
    subscriptionRepository.save(subscription);
  }

  @Override
  public int getLimit(final Authentication connectedUser, final LimitType limitType) {
    final Subscription subscription = getSubscription(connectedUser);
    return switch (limitType) {
      case INVOICE_EXPORT -> subscription.getMonthlyInvoiceExportLimit();
      case RECEIPT_EXPORT -> subscription.getMonthlyReceiptExportLimit();
      case INVOICE_CREATE -> subscription.getMonthlyInvoiceCreateLimit();
    };
  }

  private Subscription getSubscription(final Authentication connectedUser) {
    return subscriptionRepository
        .findOne(withUserId(connectedUser.getName()))
        .orElseThrow(() -> new IllegalStateException("Subscription not found for user: " + connectedUser.getName()));
  }

}
