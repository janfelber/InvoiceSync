package com.invoicesync.modules.synxie.specification;

import org.springframework.data.jpa.domain.Specification;

import com.invoicesync.modules.synxie.model.Conversation;

public class SynxieSpecification {

  public static Specification<Conversation> withUserId(final String userId) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), userId);
  }

}
