package com.invoicesync.modules.feature.service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import com.invoicesync.core.enums.FeatureEnum;
import com.invoicesync.modules.feature.model.FeatureDto;
import com.invoicesync.modules.feature.model.UpdateUserFeatureRequest;
import com.invoicesync.modules.subscription.service.UserSubscriptionService;
import com.invoicesync.modules.user.model.User;
import com.invoicesync.modules.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FeatureServiceImpl implements FeatureService {

  private final UserRepository userRepository;

  private final UserSubscriptionService userSubscriptionService;

  public FeatureServiceImpl(final UserRepository userRepository, final UserSubscriptionService userSubscriptionService) {
    this.userRepository = userRepository;
    this.userSubscriptionService = userSubscriptionService;
  }

  @Override
  public List<FeatureDto> getAllFeatures(final UUID userId) {
    final User user = userRepository.findById(userId).orElseThrow(() -> {
      log.warn("[USER] User not found for userId={}", userId);
      return new EntityNotFoundException("User not found: " + userId);
    });
    final Set<Long> userFeatureCodes = user.getFeatureIds();

    return Arrays.stream(FeatureEnum.values())
        .map(feature -> FeatureDto.builder()
            .code(feature.getId())
            .name(feature.name())
            .description(feature.getDescription())
            .enabled(userFeatureCodes.contains(feature.getId()))
            .build())
        .collect(Collectors.toList());
  }

  @Override
  public List<FeatureDto> updateUserFeatures(final UUID userId, final UpdateUserFeatureRequest request) {
    final User user = userRepository.findById(userId).orElseThrow(() -> {
      log.warn("[USER] User not found for userId={}", userId);
      return new EntityNotFoundException("User not found: " + userId);
    });


    if (request.getFeatureIds().contains(FeatureEnum.EKON_SPECIALTY.getId())) {
        userSubscriptionService.setEnterpriseSubscriptionForUser(user);
    }

    user.setFeatureIds(request.getFeatureIds());
    userRepository.save(user);
    return getAllFeatures(userId);
  }

}

