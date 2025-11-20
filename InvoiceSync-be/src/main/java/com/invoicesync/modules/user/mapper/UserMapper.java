package com.invoicesync.modules.user.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.invoicesync.core.enums.SubscriptionPlan;
import com.invoicesync.modules.subscription.model.SubscriptionShort;
import com.invoicesync.modules.subscription.repository.SubscriptionRepository;
import com.invoicesync.modules.user.model.User;
import com.invoicesync.modules.user.model.UserDto;

@Service
public class UserMapper {

  private final SubscriptionRepository subscriptionRepository;

  public UserMapper(final SubscriptionRepository subscriptionRepository) {
    this.subscriptionRepository = subscriptionRepository;
  }

  public UserDto toUserInfo(final User user) {
    final var subscription = subscriptionRepository
        .findByCreatedBy(user.getId().toString())
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
            SubscriptionShort.builder()
                .name(plan.name())
                .price(price)
                .features(plan.getFeatures())
                .build()
        )
        .build();
  }

}
