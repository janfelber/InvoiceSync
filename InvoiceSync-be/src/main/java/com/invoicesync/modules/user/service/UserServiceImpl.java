package com.invoicesync.modules.user.service;

import static com.invoicesync.modules.subscription.specification.UserSubscriptionSpecification.withUserId;

import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.core.enums.LimitType;
import com.invoicesync.core.exception.NoActiveSubscriptionException;
import com.invoicesync.modules.subscription.model.UserSubscription;
import com.invoicesync.modules.subscription.repository.UserSubscriptionRepository;
import com.invoicesync.modules.user.mapper.UserMapper;
import com.invoicesync.modules.user.model.User;
import com.invoicesync.modules.user.model.UserDto;
import com.invoicesync.modules.user.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

  private final UserSubscriptionRepository userSubscriptionRepository;

  private final UserRepository userRepository;

  private final UserMapper userMapper;

  @Override
  public int getUsed(final Authentication connectedUser, final LimitType type) {
    final UserSubscription subscription = getSubscription(connectedUser);
    return switch (type) {
      case INVOICE_PROCESS -> subscription.getMonthlyUsedInvoiceExport();
      case RECEIPT_EXPORT -> subscription.getMonthlyUsedReceiptExport();
      case INVOICE_CREATE -> subscription.getMonthlyUsedInvoiceCreate();
    };
  }

  @Override
  public void incrementUsed(final Authentication connectedUser, final LimitType limitType) {
    final UserSubscription subscription = getSubscription(connectedUser);
    switch (limitType) {
      case INVOICE_PROCESS ->
          subscription.setMonthlyUsedInvoiceExport(subscription.getMonthlyUsedInvoiceExport() + 1);
      case RECEIPT_EXPORT -> subscription.setMonthlyUsedReceiptExport(subscription.getMonthlyUsedReceiptExport() + 1);
      case INVOICE_CREATE -> subscription.setMonthlyUsedInvoiceCreate(subscription.getMonthlyUsedInvoiceCreate() + 1);
    };
    userSubscriptionRepository.save(subscription);
  }

  @Override
  public UserDto getUserInfo(final UUID userId) {
    final User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("[USER] User not found for userId={}", userId);
          return new EntityNotFoundException("User not found: " + userId);
        });
    return userMapper.toUserInfo(user);
  }

  @Override
  public String getUserName(final Authentication connectedUser) {
    final User user = userRepository.findById(UUID.fromString(connectedUser.getName()))
        .orElseThrow(() -> {
          log.warn("[USER] User not found for userId={}", connectedUser.getName());
          return new EntityNotFoundException("User not found: " + connectedUser.getName());
        });
    return user.getFullName();
  }

  @Override
  public UserDto getCurrentUserInfo(final Authentication connectedUser) {
    final User user = userRepository.findById(UUID.fromString(connectedUser.getName()))
        .orElseThrow(() -> {
          log.warn("[USER] User not found for userId={}", connectedUser.getName());
          return new EntityNotFoundException("User not found: " + connectedUser.getName());
        });
    return userMapper.toUserInfo(user);
  }

  @Override
  public int getLimit(final Authentication connectedUser, final LimitType limitType) {
    final UserSubscription subscription = getSubscription(connectedUser);
    return switch (limitType) {
      case INVOICE_PROCESS -> subscription.getMonthlyInvoiceExportLimit();
      case RECEIPT_EXPORT -> subscription.getMonthlyReceiptExportLimit();
      case INVOICE_CREATE -> subscription.getMonthlyInvoiceCreateLimit();
    };
  }

  private UserSubscription getSubscription(final Authentication connectedUser) {
    return userSubscriptionRepository
        .findOne(withUserId(connectedUser.getName()))
        .orElseThrow(() -> {
          log.warn("[SUBSCRIPTION] No subscription record exists for userId={}", connectedUser.getName());
          return new NoActiveSubscriptionException("Subscription not found for user: " + connectedUser.getName());
        });
  }

}
