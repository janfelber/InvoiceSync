package com.invoicesync.service;

import org.springframework.stereotype.Service;

import com.invoicesync.dto.subscription.LimitResponseDTO;
import com.invoicesync.dto.subscription.SubscriptionResponseDTO;
import com.invoicesync.module.Subscription;
import com.invoicesync.repository.SubscriptionRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

  private SubscriptionRepository subscriptionRepository;

  @Override
  public SubscriptionResponseDTO getSubscriptionPlanByUserId(final Long userId) {
    final Subscription subscription = subscriptionRepository.findByUserId(userId);

    return new SubscriptionResponseDTO(
        subscription.getUser().getId(),
        subscription.getSubscriptionPlan().name(),
        Boolean.TRUE.equals(subscription.getSubscriptionActive()),
        subscription.getStartDate(),
        subscription.getEndDate(),
        subscription.getMonthlyInvoiceLimit()
    );
  }

  @Override
  public LimitResponseDTO getUserLimits(final Long userId) {
    final Subscription subscription = subscriptionRepository.findByUserId(userId);

    final int totalLimit = subscription.getMonthlyInvoiceLimit();
    final int usedAmount = subscription.getUsedAmount();
    final int remainingLimit = totalLimit - usedAmount;

    return new LimitResponseDTO(totalLimit, remainingLimit);
  }

}
