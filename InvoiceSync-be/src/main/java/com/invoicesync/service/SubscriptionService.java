package com.invoicesync.service;

import com.invoicesync.dto.subscription.LimitResponseDTO;
import com.invoicesync.dto.subscription.SubscriptionResponseDTO;

public interface SubscriptionService {

  SubscriptionResponseDTO getSubscriptionPlanByUserId(Long userId);

  LimitResponseDTO getUserLimits(Long userId);

}
