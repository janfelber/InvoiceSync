package com.invoicesync.dto.subscription;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionResponseDTO {
  private Long userId;
  private String subscriptionPlan;
  private boolean subscribed;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private int monthlyUsageLimit;

}
