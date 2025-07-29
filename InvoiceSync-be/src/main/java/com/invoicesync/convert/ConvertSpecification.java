package com.invoicesync.convert;

import org.springframework.data.jpa.domain.Specification;

public class ConvertSpecification {

  public static Specification<Convert> withUserId(String userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), userId);
  }

}
