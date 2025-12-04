package com.invoicesync.modules.user.model;

import java.util.Date;
import java.util.UUID;

import com.invoicesync.modules.subscription.model.UserSubscriptionShort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
  private UUID id;
  private String username;
  private String email;
  private String fullName;
  private String role;
  private Date createdOn;
  private String phoneNumber;
  private UserSubscriptionShort subscriptionPlan;
}
