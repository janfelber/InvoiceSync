package com.invoicesync.modules.feature.service;

import java.util.List;
import java.util.UUID;

import com.invoicesync.modules.feature.model.FeatureDto;
import com.invoicesync.modules.feature.model.UpdateUserFeatureRequest;

public interface FeatureService {

  List<FeatureDto> getAllFeatures(UUID userId);

  List<FeatureDto> updateUserFeatures(UUID userId, UpdateUserFeatureRequest request);
}
