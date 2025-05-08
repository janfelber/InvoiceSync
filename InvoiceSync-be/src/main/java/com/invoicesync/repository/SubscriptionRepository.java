package com.invoicesync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoicesync.module.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

  Subscription findByUserId(Long id);

}
