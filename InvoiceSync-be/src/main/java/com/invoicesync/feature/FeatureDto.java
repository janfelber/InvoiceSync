package com.invoicesync.feature;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeatureDto {
  private long code;
  private String name;
  private String description;
  private boolean enabled;
}