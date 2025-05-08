package com.invoicesync.module;

import java.time.LocalDateTime;

import com.invoicesync.subscription.SubscriptionPlan;
import com.invoicesync.user.UserDemo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscriptions", schema = "invoice_sync")
public class Subscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "\"user_id\"")
  private UserDemo user;

  @Enumerated(EnumType.STRING)
  @Column(name = "subscription_plan")
  private SubscriptionPlan subscriptionPlan;

  @Column(name = "subscription_active")
  private Boolean subscriptionActive;

  @Column(name = "stripe_subscription_id")
  private String stripeSubscriptionId;

  @Column(name = "stripe_customer_id")
  private String stripeCustomerId;

  @Column(name = "start_date")
  private LocalDateTime startDate;

  @Column(name = "end_date")
  private LocalDateTime endDate;

  @Column(name = "monthly_invoice_limit")
  private Integer monthlyInvoiceLimit;

  @Column(name = "used_amount")
  private Integer usedAmount;

}
