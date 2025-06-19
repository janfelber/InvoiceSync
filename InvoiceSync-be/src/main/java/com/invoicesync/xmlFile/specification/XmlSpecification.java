package com.invoicesync.xmlFile.specification;

import org.springframework.data.jpa.domain.Specification;

import com.invoicesync.xmlFile.XmlFile;

public class XmlSpecification {

  public static Specification<XmlFile> withUserId(String userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), userId);
  }

}
