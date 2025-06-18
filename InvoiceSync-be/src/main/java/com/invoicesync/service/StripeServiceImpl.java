package com.invoicesync.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoicesync.subscription.SubscriptionPlan;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService{

  @Override
  public Map<String, Object> createCheckoutSession(final String planName, final Authentication connectedUser)
      throws StripeException {
    final SubscriptionPlan subscriptionPlan;
    try {
      subscriptionPlan = SubscriptionPlan.valueOf(planName.toUpperCase());
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new IllegalArgumentException("Invalid subscription plan: " + planName);
    }

    final String priceId = subscriptionPlan.getPriceId();

    final List<SessionCreateParams.LineItem> lineItems = List.of(
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

    return Map.of("id", session.getId());
  }

}
