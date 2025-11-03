package com.invoicesync.feature;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.invoicesync.user.User;
import com.invoicesync.user.UserRepository;

@Service
public class FeatureServiceImpl implements FeatureService {

  private final UserRepository userRepository;

  public FeatureServiceImpl(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public List<FeatureDto> getAllFeatures(final UUID userId) {
    final User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User not found."));
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

}

