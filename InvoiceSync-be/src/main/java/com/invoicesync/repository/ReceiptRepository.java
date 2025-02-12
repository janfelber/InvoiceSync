package com.invoicesync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoicesync.module.Receipt;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

}
