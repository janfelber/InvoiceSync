package com.invoicesync.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.repository.SubscriptionRepository;
import com.invoicesync.subscription.SubscriptionPlan;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class StripeController {

  // private UserCredentialRepository userRepository;
  private SubscriptionRepository subscriptionRepository;

  private static final String STRIPE_WEBHOOK_SECRET = "whsec_P8k8tcVJUsoepfujhP4nVloiuBnX4hVr";

  @PostMapping("/stripe/webhook")
  public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
      @RequestHeader("Stripe-Signature") String sigHeader) {
    Event event;

    try {
      event = Webhook.constructEvent(payload, sigHeader, STRIPE_WEBHOOK_SECRET);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Invalid signature");
    }

    if ("invoice.payment_succeeded".equals(event.getType())) {
      System.out.println("Invoice Payment Succeeded");
      handlePaymentSucceeded(event);
    }

    return ResponseEntity.ok("Webhook processed");
  }

  private void handlePaymentSucceeded(Event event) {
    final Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().orElseThrow();

    // System.out.println("Invoice: " + invoice);
    //
    // final String price = invoice.getLines().getData().getFirst().getPricing().getPriceDetails().getPrice();
    // final String userIdStr = invoice.getLines().getData().getFirst().getMetadata().get("userId");
    //
    // UserDemo user = new UserDemo();
    //
    // Long periodEndUnix = invoice.getLines().getData().getFirst().getPeriod().getEnd();
    // Long periodStartUnix = invoice.getLines().getData().getFirst().getPeriod().getStart();
    //
    // Instant periodStart = Instant.ofEpochSecond(periodStartUnix);
    // Instant periodEnd = Instant.ofEpochSecond(periodEndUnix);
    // String customerId = invoice.getCustomer();
    // LocalDateTime endDate = LocalDateTime.ofInstant(periodEnd, ZoneId.systemDefault());
    // LocalDateTime startDate = LocalDateTime.ofInstant(periodStart, ZoneId.systemDefault());
    // SubscriptionPlan plan = SubscriptionPlan.fromStripePriceId(price);
    // String subscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();
    //
    // Subscription subscription = new Subscription();
    // subscription.setSubscriptionPlan(plan);
    // subscription.setStartDate(startDate);
    // subscription.setEndDate(endDate);
    // subscription.setMonthlyInvoiceLimit(plan.getMonthlyInvoiceLimit());
    // subscription.setSubscriptionActive(true);
    // subscription.setStripeSubscriptionId(subscriptionId);
    // user.setId(Long.parseLong(userIdStr));
    // subscription.setCreatedBy(user);

    // subscriptionRepository.save(subscription);
  }


  @PostMapping("/stripe/premium")
  public ResponseEntity<Map<String, Object>> createPremiumCheckout(@RequestBody final Map<String, String> request)
      throws StripeException {

    System.out.println("Stripe create-checkout-session");
    final Authentication connectedUser = SecurityContextHolder.getContext().getAuthentication();
    System.out.println("Connected user: " + connectedUser.getName());

    final String planName = request.get("plan"); // napr. "PRO", "ESSENTIALS", atď.

    final SubscriptionPlan subscriptionPlan;
    try {
      subscriptionPlan = SubscriptionPlan.valueOf(planName.toUpperCase());
    } catch (IllegalArgumentException | NullPointerException e) {
      return ResponseEntity.badRequest().body(Map.of("error", "Invalid subscription plan: " + planName));
    }

    final String priceId = subscriptionPlan.getPriceId();
    System.out.println("Price ID: " + priceId);

    List<SessionCreateParams.LineItem> lineItems = List.of(
        SessionCreateParams.LineItem.builder()
            .setPrice(priceId)
            .setQuantity(1L)
            .build()
    );

    SessionCreateParams params = SessionCreateParams.builder()
        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
        .setSuccessUrl("http://localhost:4200/web/limiter")
        .setCancelUrl("http://localhost:4200/home")
        .addAllLineItem(lineItems)
        .setSubscriptionData(
            SessionCreateParams.SubscriptionData.builder()
                .putMetadata("userId", connectedUser.getName())
                .build()
        )
        .build();

    Session session = Session.create(params);

    Map<String, Object> response = new HashMap<>();
    response.put("id", session.getId());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/stripe/create-checkout-session")
  public ResponseEntity<Map<String, Object>> createCheckoutSession(@RequestBody Map<String, String> request) throws
                                                                    StripeException {
    System.out.println("Stripe create-checkout-session");

    List<SessionCreateParams.LineItem> lineItems = List.of(
        SessionCreateParams.LineItem.builder()
            .setPrice("price_1RLNmZLJ07OMo5e7zw4IHUEW") // Vytvor v Stripe > Products > Prices
            .setQuantity(1L)
            .build()
    );

    SessionCreateParams params = SessionCreateParams.builder()
        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
        .setSuccessUrl("http://localhost:4200/web/limiter")
        .setCancelUrl("http://localhost:4200/home")
        .addAllLineItem(lineItems)
        .build();

    Session session = Session.create(params);

    Map<String, Object> response = new HashMap<>();
    response.put("id", session.getId());
    return ResponseEntity.ok(response);
  }

  // @PostMapping("/stripe/create-checkout-session")
  // public ResponseEntity<String> createCheckoutSession() throws
  //                                                                                                            StripeException {
  //   return ResponseEntity.ok("test");
  // }

}
