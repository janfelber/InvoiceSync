package com.invoicesync.specification;

import org.springframework.data.jpa.domain.Specification;

import com.invoicesync.module.Subscription;

public class SubscriptionSpecification {

  public static Specification<Subscription> withUserId(String userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), userId);
  }

}
