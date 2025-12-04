package com.invoicesync.modules.subscription.specification;

import org.springframework.data.jpa.domain.Specification;

import com.invoicesync.modules.subscription.model.UserSubscription;

public class UserSubscriptionSpecification {

  public static Specification<UserSubscription> withUserId(final String userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), userId);
  }

}
