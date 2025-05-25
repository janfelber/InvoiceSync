package com.invoicesync.module;

import org.springframework.data.jpa.domain.Specification;

public class CompanySpecification {

  public static Specification<Company> withUserId(String userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), userId);
  }

}
