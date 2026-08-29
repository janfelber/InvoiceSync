package com.invoicesync.modules.stripe.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "stripe_processed_event", schema = "invoice_sync")
public class StripeProcessedEvent {

  @Id
  @Column(name = "event_id")
  private String eventId;

  @CreationTimestamp
  @Column(name = "processed_at", updatable = false)
  private LocalDateTime processedAt;

  public StripeProcessedEvent(final String eventId) {
    this.eventId = eventId;
  }

}
