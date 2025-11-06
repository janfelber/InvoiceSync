package com.invoicesync.modules.user.mapper;

import org.springframework.stereotype.Service;

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
    final var subscription = subscriptionRepository.findByCreatedBy(user.getId().toString()).orElse(null);

    return UserDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .fullName(user.getFullName())
        .role(user.getRole().name())
        .createdOn(user.getCreatedOn())
        .phoneNumber(user.getPhoneNumber())
        .subscriptionPlan(SubscriptionShort.builder()
            .name(subscription.getSubscriptionPlan().name())
            .price(subscription.getSubscriptionPrice())
            .features(subscription.getSubscriptionPlan().getFeatures())
            .build())
        .build();
  }

}
