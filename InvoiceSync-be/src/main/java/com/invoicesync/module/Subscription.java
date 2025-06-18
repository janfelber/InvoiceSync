package com.invoicesync.module;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.invoicesync.common.BaseEntity;
import com.invoicesync.subscription.SubscriptionPlan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscriptions", schema = "invoice_sync")
public class Subscription extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // @ManyToOne
  // @JoinColumn(name = "\"user_id\"")
  // private UserDemo user;

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

  @Column(name = "monthly_invoice_export_limit")
  private Integer monthlyInvoiceExportLimit;

  @Column(name = "monthly_used_invoice_export")
  private Integer monthlyUsedInvoiceExportLimit;

  @Column(name = "monthly_receipt_export_limit")
  private Integer monthlyReceiptExportLimit;

  @Column(name = "monthly_used_receipt_export_limit")
  private Integer monthlyUsedReceiptExportLimit;

  @Column(name = "monthly_invoice_create_limit")
  private Integer monthlyInvoiceCreateLimit;

  @Column(name = "monthly_used_invoice_create_limit")
  private Integer monthlyUsedInvoiceCreateLimit;

  @Column(name = "subscription_price")
  private BigDecimal subscriptionPrice;

  @Column(name = "total_limit")
  private Integer totalLimit;

}
