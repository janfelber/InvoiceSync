package com.invoicesync.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.invoicesync.module.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>,
                                                JpaSpecificationExecutor<Subscription> {

}
