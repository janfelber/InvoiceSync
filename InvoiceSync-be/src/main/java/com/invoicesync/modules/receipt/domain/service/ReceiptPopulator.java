package com.invoicesync.modules.receipt.domain.service;

import org.w3c.dom.Document;

import com.invoicesync.core.enums.PaymentType;
import com.invoicesync.modules.receipt.model.Receipt;

public interface ReceiptPopulator {

  PaymentType supports();

  void populate(Document template, Receipt receipt, String numberRequested);

}
