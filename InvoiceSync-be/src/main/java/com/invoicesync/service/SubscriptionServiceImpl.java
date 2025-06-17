package com.invoicesync.service;

import static com.invoicesync.specification.SubscriptionSpecification.withUserId;

import java.math.BigDecimal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.dto.subscription.LimitResponseDTO;
import com.invoicesync.dto.subscription.SubscriptionResponseDTO;
import com.invoicesync.module.Subscription;
import com.invoicesync.repository.SubscriptionRepository;
import com.invoicesync.subscription.SubscriptionPlan;

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
          empty.setSubscriptionPlan(SubscriptionPlan.NONE);
          empty.setSubscriptionActive(false);
          empty.setStartDate(null);
          empty.setEndDate(null);
          empty.setSubscriptionPrice(BigDecimal.ZERO);
          empty.setMonthlyInvoiceLimit(0);
          return empty;
        });

    final SubscriptionPlan plan = subscription.getSubscriptionPlan() != null
        ? subscription.getSubscriptionPlan()
        : SubscriptionPlan.NONE;

    return new SubscriptionResponseDTO(
        subscription.getCreatedBy(),
        subscription.getSubscriptionPlan() != null ? subscription.getSubscriptionPlan().name() : null,
        Boolean.TRUE.equals(subscription.getSubscriptionActive()),
        subscription.getStartDate(),
        subscription.getEndDate(),
        subscription.getMonthlyInvoiceLimit(),
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

    final int totalLimit = subscription.getMonthlyInvoiceLimit();
    final int usedLimit = subscription.getUsedAmount();

    return new LimitResponseDTO(usedLimit, totalLimit);
  }

}
