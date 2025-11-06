package com.invoicesync.modules.user.service;

import static com.invoicesync.modules.subscription.specification.SubscriptionSpecification.withUserId;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.modules.user.mapper.UserMapper;
import com.invoicesync.modules.user.model.User;
import com.invoicesync.modules.user.model.UserDto;
import com.invoicesync.modules.user.repository.UserRepository;
import com.invoicesync.modules.subscription.model.Subscription;
import com.invoicesync.modules.subscription.repository.SubscriptionRepository;
import com.invoicesync.core.enums.LimitType;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

  private final SubscriptionRepository subscriptionRepository;

  private final UserRepository userRepository;

  private final UserMapper userMapper;

  @Override
  public int getUsed(final Authentication connectedUser, final LimitType type) {
    final Subscription subscription = getSubscription(connectedUser);
    return switch (type) {
      case INVOICE_PROCESS -> subscription.getMonthlyUsedInvoiceExport();
      case RECEIPT_EXPORT -> subscription.getMonthlyUsedReceiptExport();
      case INVOICE_CREATE -> subscription.getMonthlyUsedInvoiceCreate();
    };
  }

  @Override
  public void incrementUsed(final Authentication connectedUser, final LimitType limitType) {
    final Subscription subscription = getSubscription(connectedUser);
    switch (limitType) {
      case INVOICE_PROCESS ->
          subscription.setMonthlyUsedInvoiceExport(subscription.getMonthlyUsedInvoiceExport() + 1);
      case RECEIPT_EXPORT -> subscription.setMonthlyUsedReceiptExport(subscription.getMonthlyUsedReceiptExport() + 1);
      case INVOICE_CREATE -> subscription.setMonthlyUsedInvoiceCreate(subscription.getMonthlyUsedInvoiceCreate() + 1);
    };
    subscriptionRepository.save(subscription);
  }

  @Override
  public UserDto getUserInfo(final UUID userId) {
    final User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User not found."));
    return userMapper.toUserInfo(user);
  }

  @Override
  public UserDto getCurrentUserInfo(final Authentication connectedUser) {
    final User user = userRepository.findById(UUID.fromString(connectedUser.getName()))
        .orElseThrow(() -> new IllegalStateException("User not found."));
    return userMapper.toUserInfo(user);
  }

  @Override
  public int getLimit(final Authentication connectedUser, final LimitType limitType) {
    final Subscription subscription = getSubscription(connectedUser);
    return switch (limitType) {
      case INVOICE_PROCESS -> subscription.getMonthlyInvoiceExportLimit();
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
