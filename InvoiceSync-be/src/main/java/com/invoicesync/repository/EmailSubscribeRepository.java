package com.invoicesync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.invoicesync.module.EmailSubscribe;

@Repository
public interface EmailSubscribeRepository extends JpaRepository<EmailSubscribe, Long> {

}
