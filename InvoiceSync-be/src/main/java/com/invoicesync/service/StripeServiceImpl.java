package com.invoicesync.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService{

  @Override
  public String createCheckoutSession(final String priceId, final Authentication connectedUser) throws StripeException {
    SessionCreateParams params = SessionCreateParams.builder()
        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
        .setSuccessUrl("http://localhost:4200/web/limiter")
        .setCancelUrl("http://localhost:4200/home")
        .addLineItem(SessionCreateParams.LineItem.builder()
            .setPrice(priceId)
            .setQuantity(1L)
            .build())
        .setSubscriptionData(SessionCreateParams.SubscriptionData.builder()
            .putMetadata("userId", connectedUser.getName())
            .build())
        .build();

    Session session = Session.create(params);
    return session.getId(); // <== TOTO
  }

}
