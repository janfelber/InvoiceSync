package com.invoicesync.feature;

import java.util.List;
import java.util.UUID;

public interface FeatureService {

  List<FeatureDto> getAllFeatures(UUID userId);

  List<FeatureDto> updateUserFeatures(UUID userId, UpdateUserFeatureRequest request);
}
