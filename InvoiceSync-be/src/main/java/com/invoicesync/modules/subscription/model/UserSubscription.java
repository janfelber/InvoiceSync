package com.invoicesync.modules.subscription.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import com.invoicesync.core.common.BaseEntity;
import com.invoicesync.core.enums.SubscriptionPlan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Subscription entity representing a user's subscription plan.
 * Tracks the subscription plan, status, usage limits, and integration with Stripe
 * for billing and payment processing.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscriptions", schema = "invoice_sync")
public class UserSubscription extends BaseEntity {

  /**
   * Type of plan (e.g., Free, Pro, Enterprise)
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "subscription_plan")
  private SubscriptionPlan subscriptionPlan;

  /**
   * Whether the subscription is currently active.
   */
  @Column(name = "subscription_active")
  private Boolean subscriptionActive;

  /**
   * Stripe subscription identifier for billing and payment.
   */
  @Column(name = "stripe_subscription_id")
  private String stripeSubscriptionId;

  @Column(name = "stripe_subscription_item_id")
  private String stripeSubscriptionItemId;

  /**
   * Stripe customer identifier linked to this subscription.
   */
  @Column(name = "stripe_customer_id")
  private String stripeCustomerId;

  /**
   * Start date and time of the subscription.
   */
  @Column(name = "start_date")
  private LocalDateTime startDate;

  /**
   * End date and time of the subscription.
   */
  @Column(name = "end_date")
  private LocalDateTime endDate;

  /**
   * Monthly limit for exporting invoices.
   */
  @Column(name = "monthly_invoice_export_limit")
  private Integer monthlyInvoiceExportLimit;

  /**
   * Number of invoices exported this month.
   */
  @Column(name = "monthly_used_invoice_export")
  private Integer monthlyUsedInvoiceExport;


  /**
   * Monthly limit for exporting receipts.
   */
  @Column(name = "monthly_receipt_export_limit")
  private Integer monthlyReceiptExportLimit;

  /**
   * Number of receipts exported this month.
   */
  @Column(name = "monthly_used_receipt_export_limit")
  private Integer monthlyUsedReceiptExport;

  /**
   * Monthly limit for creating invoices.
   */
  @Column(name = "monthly_invoice_create_limit")
  private Integer monthlyInvoiceCreateLimit;

  /**
   * Number of invoices created this month.
   */
  @Column(name = "monthly_used_invoice_create_limit")
  private Integer monthlyUsedInvoiceCreate;

  /**
   * Price of the subscription plan.
   * */
  @Column(name = "subscription_price")
  private BigDecimal subscriptionPrice;

  /**
   * Total combined limit for all relevant actions, if applicable.
   */
  @Column(name = "total_limit")
  private Integer totalLimit;

}
