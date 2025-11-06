package com.invoicesync.modules.receipt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.invoicesync.modules.receipt.model.Receipt;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long>, JpaSpecificationExecutor<Receipt> {

  // List<Receipt> findByUserId(Long id);
  //
  // List<Receipt> findByCompanyId(Long companyId);

}
