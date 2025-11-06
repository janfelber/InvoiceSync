package com.invoicesync.modules.subscription.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.invoicesync.modules.subscription.model.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>,
                                                JpaSpecificationExecutor<Subscription> {
  Optional<Subscription> findByCreatedByAndSubscriptionActive(String createdBy, boolean active);

  Optional<Subscription> findByCreatedBy(String createdBy);
}
