package com.invoicesync.modules.user.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.invoicesync.core.enums.SubscriptionPlan;
import com.invoicesync.modules.subscription.model.UserSubscriptionShort;
import com.invoicesync.modules.subscription.repository.UserSubscriptionRepository;
import com.invoicesync.modules.user.model.User;
import com.invoicesync.modules.user.model.UserDto;

@Service
public class UserMapper {

  private final UserSubscriptionRepository userSubscriptionRepository;

  public UserMapper(final UserSubscriptionRepository userSubscriptionRepository) {
    this.userSubscriptionRepository = userSubscriptionRepository;
  }

  public UserDto toUserInfo(final User user) {
    final var subscription = userSubscriptionRepository
        .findByCreatedByAndSubscriptionActive(user.getId().toString(), true)
        .orElse(null);

    final var plan =
        subscription != null
            ? subscription.getSubscriptionPlan()
            : SubscriptionPlan.NONE;

    final BigDecimal price =
        subscription != null
            ? subscription.getSubscriptionPrice()
            : BigDecimal.ZERO;

    return UserDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .fullName(user.getFullName())
        .role(user.getRole().name())
        .createdOn(user.getCreatedOn())
        .phoneNumber(user.getPhoneNumber())
        .subscriptionPlan(
            UserSubscriptionShort.builder()
                .name(plan.name())
                .price(price)
                .features(plan.getFeatures())
                .build()
        )
        .build();
  }

}
