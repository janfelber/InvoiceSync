package com.invoicesync.stripe;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.invoicesync.subscription.SubscriptionRepository;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.net.Webhook;

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

}
