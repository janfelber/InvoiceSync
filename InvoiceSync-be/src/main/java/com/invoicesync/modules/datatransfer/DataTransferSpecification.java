package com.invoicesync.modules.datatransfer;

import org.springframework.data.jpa.domain.Specification;

import com.invoicesync.modules.datatransfer.model.DataTransfer;

public class DataTransferSpecification {

  public static Specification<DataTransfer> withUserId(final String userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), userId);
  }

}
