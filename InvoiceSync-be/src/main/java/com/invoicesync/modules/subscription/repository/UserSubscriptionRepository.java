package com.invoicesync.modules.subscription.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.invoicesync.modules.subscription.model.UserSubscription;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long>,
                                                    JpaSpecificationExecutor<UserSubscription> {
  Optional<UserSubscription> findByCreatedByAndSubscriptionActive(String createdBy, boolean active);

  Optional<UserSubscription> findByCreatedBy(String createdBy);

  UserSubscription findByStripeSubscriptionId(String stripeSubscriptionId);
}
