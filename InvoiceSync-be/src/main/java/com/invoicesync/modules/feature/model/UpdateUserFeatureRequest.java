package com.invoicesync.modules.feature.model;

import java.util.Set;

import lombok.Data;

@Data
public class UpdateUserFeatureRequest {
  private Set<Long> featureIds;
}
