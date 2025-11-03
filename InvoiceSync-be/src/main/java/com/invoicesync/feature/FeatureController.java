package com.invoicesync.feature;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feature")
public class FeatureController {

  private final FeatureService featureService;

  public FeatureController(final FeatureService featureService) {
    this.featureService = featureService;
  }

  @GetMapping("/{user-id}")
  public List<FeatureDto> getAllFeatures(@PathVariable("user-id") final UUID userId) {
    return featureService.getAllFeatures(userId);
  }
}
