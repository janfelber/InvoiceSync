package com.invoicesync;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.stripe.Stripe;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StripeInitializer {

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    Stripe.apiKey = "sk_test_51RLMmHLJ07OMo5e7pmTWQpK37a0lm0llHYxiHSkZrozcN9xkJT8F6h8PjO4Hitcn56ecPpy8go9zd6cTlZao9Oav00b9tg54oq";
    log.info("Stripe initialized!");
  }
}