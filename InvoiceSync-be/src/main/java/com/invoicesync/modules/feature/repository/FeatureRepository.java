package com.invoicesync.modules.feature.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.modules.feature.model.Feature;

public interface FeatureRepository extends JpaRepository<Feature, Long> {

}
