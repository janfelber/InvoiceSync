package com.invoicesync.modules.xmlFile.specification;

import org.springframework.data.jpa.domain.Specification;

import com.invoicesync.modules.xmlFile.model.XmlFile;

public class XmlSpecification {

  public static Specification<XmlFile> withUserId(String userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), userId);
  }

}
