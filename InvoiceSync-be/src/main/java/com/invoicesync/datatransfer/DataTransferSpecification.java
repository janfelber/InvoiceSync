package com.invoicesync.datatransfer;

import org.springframework.data.jpa.domain.Specification;

public class DataTransferSpecification {

  public static Specification<DataTransfer> withUserId(final String userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), userId);
  }

}
