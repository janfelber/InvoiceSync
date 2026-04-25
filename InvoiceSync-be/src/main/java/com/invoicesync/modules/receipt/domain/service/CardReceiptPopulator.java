package com.invoicesync.modules.receipt.domain.service;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import com.invoicesync.core.enums.PaymentType;
import com.invoicesync.modules.receipt.model.Receipt;
import com.invoicesync.modules.xml.utils.XmlHelper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CardReceiptPopulator implements ReceiptPopulator {

  private final XmlHelper xmlHelper;

  @Override
  public PaymentType supports() {
    return PaymentType.CARD;
  }

  @Override
  public void populate(final Document template, final Receipt receipt, final String numberRequested) {
    xmlHelper.updateCardReceiptDetails(template, receipt, numberRequested);
    xmlHelper.updateCardReceiptMyIdentity(template, receipt.getCompany());
    xmlHelper.updateCardReceiptPartner(template, receipt);
    xmlHelper.updateCardReceiptItems(template, receipt.getItems());
  }

}
