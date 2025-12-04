package com.invoicesync.modules.subscription.model;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSubscriptionShort {
  private String name;
  private BigDecimal price;
  private List<String> features;

}
