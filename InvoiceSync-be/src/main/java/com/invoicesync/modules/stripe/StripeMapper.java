package com.invoicesync.modules.stripe;

import org.springframework.stereotype.Service;

import com.invoicesync.modules.stripe.model.UserBillingHistory;
import com.invoicesync.modules.stripe.model.UserDefaultCard;
import com.stripe.model.Invoice;
import com.stripe.model.PaymentMethod;

@Service
public class StripeMapper {

  public UserDefaultCard toUserDefaultCard(PaymentMethod paymentMethod) {
    return new UserDefaultCard(
        paymentMethod.getCard().getBrand(),
        paymentMethod.getCard().getLast4(),
        paymentMethod.getCard().getExpMonth(),
        paymentMethod.getCard().getExpYear()
    );
  }

  public UserBillingHistory toUserBillingHistory(Invoice invoice) {
    return new UserBillingHistory(
        invoice.getNumber(),
        invoice.getCreated().toString(),
        invoice.getBillingReason(),
        invoice.getAmountPaid() / 100.0,
        invoice.getStatus(),
        invoice.getInvoicePdf()
    );
  }

}
