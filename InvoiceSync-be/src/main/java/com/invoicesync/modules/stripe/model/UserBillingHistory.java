package com.invoicesync.modules.stripe.model;

public record UserBillingHistory(

    // Invoice number – unique identifier of the invoice from Stripe.
    String invoiceNumber,
    // Date the invoice was sent.
    String dateInvoiced,
    // Reason for the invoice being created.
    String billingReason,
    // Amount charged (in cents).
    Double amount,
    // Invoice status (e.g., "paid", "open").
    String status,
    // URL to the invoice PDF from Stripe.
    String invoicePdfUrl
) {

}
