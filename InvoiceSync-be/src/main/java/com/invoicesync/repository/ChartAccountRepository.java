package com.invoicesync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoicesync.module.ChartAccount;

@Repository
public interface ChartAccountRepository extends JpaRepository<ChartAccount, Long> {

}
