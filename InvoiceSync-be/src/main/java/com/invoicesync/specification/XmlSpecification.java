package com.invoicesync.specification;

import org.springframework.data.jpa.domain.Specification;

import com.invoicesync.module.XmlFile;

public class XmlSpecification {

  public static Specification<XmlFile> withUserId(String userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), userId);
  }

}
