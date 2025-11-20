package com.invoicesync.modules.stripe.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.config.StripeConfig;
import com.invoicesync.core.enums.SubscriptionPlan;
import com.invoicesync.core.utils.SubscriptionUtil;
import com.invoicesync.modules.subscription.mapper.SubscriptionMapper;
import com.invoicesync.modules.subscription.model.Subscription;
import com.invoicesync.modules.subscription.repository.SubscriptionRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {

  @Value("${app.frontend.url}")
  private String frontendUrl;

  private final SubscriptionRepository subscriptionRepository;

  private final SubscriptionMapper subscriptionMapper;

  private final StripeConfig stripePriceConfig;

  @Override
  public Map<String, Object> createCheckoutSession(final String planName, final Authentication connectedUser)
      throws StripeException {
    final SubscriptionPlan subscriptionPlan;
    try {
      subscriptionPlan = SubscriptionPlan.valueOf(planName.toUpperCase());
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new IllegalArgumentException("Invalid subscription plan: " + planName);
    }

    final String priceId = stripePriceConfig.getPriceIdForPlan(subscriptionPlan);

    final List<SessionCreateParams.LineItem> lineItems = List.of(
        SessionCreateParams.LineItem.builder().setPrice(priceId).setQuantity(1L).build());

    final SessionCreateParams params = SessionCreateParams.builder()
        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
        .setSuccessUrl(frontendUrl + "/web/limiter")
        .setCancelUrl(frontendUrl + "/home")
        .addAllLineItem(lineItems)
        .setSubscriptionData(
            SessionCreateParams.SubscriptionData.builder().putMetadata("userId", connectedUser.getName()).build())
        .build();

    final Session session = Session.create(params);
    return Map.of("id", session.getId());
  }

  @Transactional
  public void handleSubscriptionPayment(final Event event) {
    final Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().orElseThrow();
    final var line = invoice.getLines().getData().getFirst();
    final String userIdStr = line.getMetadata().get("userId");
    final String priceId = line.getPricing().getPriceDetails().getPrice();
    final SubscriptionPlan plan = SubscriptionUtil.fromStripePriceId(priceId);

    final LocalDateTime startDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(line.getPeriod().getStart()),
        ZoneId.systemDefault());
    final LocalDateTime endDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(line.getPeriod().getEnd()),
        ZoneId.systemDefault());

    final String stripeSubscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();

    final Optional<Subscription> existingSubscription = subscriptionRepository.findByCreatedByAndSubscriptionActive(
        userIdStr, true);

    if (existingSubscription.isPresent()) {
      updateExistingSubscription(existingSubscription.get(), plan, startDate, endDate,
          BigDecimal.valueOf(plan.getMonthlyPrice()),stripeSubscriptionId);
    } else {
      createNewSubscription(invoice, userIdStr);
    }
  }

  private void updateExistingSubscription(final Subscription existing, final SubscriptionPlan newPlan,
      final LocalDateTime startDate, final LocalDateTime endDate, final BigDecimal subscriptionPrice, final String stripeSubscriptionId) {

    final Integer usedInvoices = existing.getMonthlyUsedInvoiceCreate();
    final Integer usedReceipts = existing.getMonthlyUsedReceiptExport();
    final Integer usedExports = existing.getMonthlyUsedInvoiceExport();

    existing.setSubscriptionPlan(newPlan);
    existing.setStartDate(startDate);
    existing.setEndDate(endDate);
    existing.setStripeSubscriptionId(stripeSubscriptionId);
    existing.setSubscriptionPrice(subscriptionPrice);

    existing.setMonthlyInvoiceCreateLimit(newPlan.getMonthlyInvoiceCreateLimit());
    existing.setMonthlyInvoiceExportLimit(newPlan.getMonthlyInvoiceExportLimit());
    existing.setMonthlyReceiptExportLimit(newPlan.getMonthlyReceiptExportLimit());

    existing.setMonthlyUsedInvoiceCreate(usedInvoices);
    existing.setMonthlyUsedInvoiceExport(usedExports);
    existing.setMonthlyUsedReceiptExport(usedReceipts);

    subscriptionRepository.save(existing);
  }

  private void createNewSubscription(final Invoice invoice, final String userIdStr) {
    final Subscription subscription = subscriptionMapper.fromStripeInvoice(invoice, userIdStr);
    subscriptionRepository.save(subscription);
  }

}
