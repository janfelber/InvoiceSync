package com.invoicesync.subscription;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>,
                                                JpaSpecificationExecutor<Subscription> {
  Optional<Subscription> findByCreatedByAndSubscriptionActive(String createdBy, boolean active);
}
