package com.invoicesync.chartaccount.specification;

import org.springframework.data.jpa.domain.Specification;

import com.invoicesync.chartaccount.ChartAccount;

public class ChartAccountSpecification {

  public static Specification<ChartAccount> withCompanyId(final Long companyId) {
    return (root, query, cb) -> cb.or(
        cb.equal(root.get("company").get("id"), companyId),
        cb.isNull(root.get("company"))
    );
  }

}
