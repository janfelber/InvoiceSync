package com.invoicesync.subscription.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionResponseDTO {

  private String userId;

  private String subscriptionPlan;

  private boolean subscribed;

  private LocalDateTime startDate;

  private LocalDateTime endDate;

  private int monthlyUsageLimit;

  private BigDecimal subscriptionPrice;

  private List<String> features;

}
