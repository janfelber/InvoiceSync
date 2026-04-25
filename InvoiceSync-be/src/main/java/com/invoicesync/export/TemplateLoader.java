package com.invoicesync.export;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import com.invoicesync.core.enums.PaymentType;

@Component
public class TemplateLoader {

  public Document load(final PaymentType paymentType) throws Exception {
    final String path = paymentType == PaymentType.CARD
        ? "template/receipt_card.xml"
        : "template/receipt_cash.xml";

    try (InputStream stream = getClass().getClassLoader().getResourceAsStream(path)) {
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(true);
      final Document doc = factory.newDocumentBuilder().parse(stream);
      doc.getDocumentElement().normalize();
      return doc;
    }
  }

}
