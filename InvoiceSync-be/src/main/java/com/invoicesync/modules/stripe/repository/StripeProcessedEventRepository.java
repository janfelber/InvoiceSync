package com.invoicesync.modules.stripe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.invoicesync.modules.stripe.model.StripeProcessedEvent;

public interface StripeProcessedEventRepository extends JpaRepository<StripeProcessedEvent, String> {

  @Modifying
  @Query(
      value = "INSERT INTO invoice_sync.stripe_processed_event (event_id) VALUES (:eventId) ON CONFLICT (event_id) DO NOTHING",
      nativeQuery = true)
  int tryInsert(@Param("eventId") String eventId);

}
