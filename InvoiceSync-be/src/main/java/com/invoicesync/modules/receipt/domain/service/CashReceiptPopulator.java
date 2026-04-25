package com.invoicesync.modules.receipt.domain.service;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import com.invoicesync.core.enums.PaymentType;
import com.invoicesync.modules.receipt.model.Receipt;
import com.invoicesync.modules.xml.utils.XmlHelper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CashReceiptPopulator implements ReceiptPopulator {

  private final XmlHelper xmlHelper;

  @Override
  public PaymentType supports() {
    return PaymentType.CASH;
  }

  @Override
  public void populate(final Document template, final Receipt receipt, final String numberRequested) {
    xmlHelper.updateCashReceiptDetails(template, receipt, numberRequested);
    xmlHelper.updateCashReceiptMyIdentity(template, receipt.getCompany());
    xmlHelper.updateCashReceiptPartner(template, receipt);
    xmlHelper.updateCashReceiptItems(template, receipt.getItems());
  }

}
