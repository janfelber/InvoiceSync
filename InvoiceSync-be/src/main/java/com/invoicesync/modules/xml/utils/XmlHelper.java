package com.invoicesync.modules.xml.utils;

import static com.invoicesync.modules.xml.utils.PohodaXmlConstants.*;
import static com.invoicesync.modules.xml.utils.PohodaXmlConstants.General.*;
import static com.invoicesync.modules.xml.utils.PohodaXmlParentTagNames.NUMBER;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.invoicesync.core.identity.MyIdentity;
import com.invoicesync.core.identity.PartnerDto;
import com.invoicesync.modules.company.model.Company;
import com.invoicesync.modules.invoice.model.InvoiceRequestDetailsDTO;
import com.invoicesync.modules.receipt.model.Receipt;
import com.invoicesync.modules.receipt.model.ReceiptItem;
import com.invoicesync.modules.receipt.model.ReceiptItemDto;

@Component
public class XmlHelper {

  public void updateInvoiceDetails(final Document doc, final InvoiceRequestDetailsDTO invoice) {
    replaceTextContent(doc, INVOICE_NUMBER, invoice.numberRequested(),
        NUMBER);
    replaceTextContent(doc, VARIABLE_SYMBOL, invoice.variableSymbol(), null);
    replaceTextContent(doc, PAIRING_SYMBOL, invoice.originalDocument(), null);
    replaceTextContent(doc, DATE, invoice.issueDate(), null);
    replaceTextContent(doc, DATE_TAX, invoice.taxDate(), null);
    replaceTextContent(doc, DATE_ACCOUNTING, invoice.accountingDate(), null);
    replaceTextContent(doc, DATE_DUE, invoice.dueDate(), null);
    replaceTextContent(doc, ACCOUNT_VALUE, invoice.accountValue(), ReceivedInvoice.ACCOUNTING);
    replaceTextContent(doc, ACCOUNT_VALUE, invoice.classificationVAT(),
        ReceivedInvoice.CLASSIFICATION_VAT);
    replaceTextContent(doc, ACCOUNT_VALUE, invoice.classificationKVVAT(),
        ReceivedInvoice.CLASSIFICATION_KV_VAT);
    replaceTextContent(doc, ReceivedInvoice.TEXT, invoice.description(), null);
  }

  public void updateInvoiceMyIdentity(final Document doc, final MyIdentity myIdentity) {
    replaceTextContent(doc, COMPANY, myIdentity.getName(), ReceivedInvoice.MY_IDENTITY);
    replaceTextContent(doc, CITY, myIdentity.getCity(), ReceivedInvoice.MY_IDENTITY);
    replaceTextContent(doc, STREET, myIdentity.getStreet(), ReceivedInvoice.MY_IDENTITY);
    replaceTextContent(doc, STREET_NUMBER, myIdentity.getStreetNumber(), ReceivedInvoice.MY_IDENTITY);
    replaceTextContent(doc, ZIP, myIdentity.getZip(), ReceivedInvoice.MY_IDENTITY);
    replaceTextContent(doc, REGISTRATION_NUMBER, myIdentity.getRegistrationNumber(),
        ReceivedInvoice.MY_IDENTITY);
    replaceTextContent(doc, TAX_ID, myIdentity.getTaxId(), ReceivedInvoice.MY_IDENTITY);
    replaceTextContent(doc, VAT_ID, myIdentity.getVatId(), ReceivedInvoice.MY_IDENTITY);
  }

  public void updatePartner(final Document doc, final PartnerDto partnerDTO) {
    replaceTextContent(doc, COMPANY, partnerDTO.getName(), ReceivedInvoice.PARTNER);
    replaceTextContent(doc, CITY, partnerDTO.getCity(), ReceivedInvoice.PARTNER);
    replaceTextContent(doc, STREET, partnerDTO.getStreet(), ReceivedInvoice.PARTNER);
    replaceTextContent(doc, ZIP, partnerDTO.getZip(), ReceivedInvoice.PARTNER);
    replaceTextContent(doc, REGISTRATION_NUMBER, partnerDTO.getRegistrationNumber(), ReceivedInvoice.PARTNER);
    replaceTextContent(doc, TAX_ID, partnerDTO.getTaxId(), ReceivedInvoice.PARTNER);
    replaceTextContent(doc, VAT_ID, partnerDTO.getVatId(), ReceivedInvoice.PARTNER);
  }

  public void updateInvoiceItems(final Document doc, final List<ReceiptItemDto> items) {
    final Element invoiceItemsParent = (Element) doc.getElementsByTagName(ReceivedInvoice.DETAIL)
        .item(0);
    for (final ReceiptItemDto item : items) {
      final Element invoiceItem = doc.createElement(ReceivedInvoice.ITEM);

      // Set the relevant fields for the invoiceItem
      createElementAndAppend(doc, invoiceItem, TEXT, item.getName());
      createElementAndAppend(doc, invoiceItem, QUANTITY,
          String.valueOf(item.getQuantity()));
      createElementAndAppend(doc, invoiceItem, COEFFICIENT, "1");
      createElementAndAppend(doc, invoiceItem, PAY_VAT, "false");
      createElementAndAppend(doc, invoiceItem, RATE_VAT, "high");
      createElementAndAppend(doc, invoiceItem, DISCOUNT_PERCENTAGE, "0");

      final BigDecimal vatRate = BigDecimal.valueOf(item.getVatRate());  // Sadza DPH, napr. 19

      // Cena bez DPH
      final BigDecimal priceWithoutVAT = item.getUnitPriceWithoutVat();

      // Vypočítať DPH ako čiastku (t.j. podiel z ceny bez DPH)
      final BigDecimal vatAmount = priceWithoutVAT.multiply(vatRate)
          .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

      // Handle homeCurrency and pricing
      final Element homeCurrency = doc.createElement(ReceivedInvoice.HOME_CURRENCY);
      createElementAndAppend(doc, homeCurrency, UNIT_PRICE, String.valueOf(item.getUnitPriceWithoutVat()));
      createElementAndAppend(doc, homeCurrency, PRICE, String.valueOf(item.getUnitPriceWithoutVat()));
      createElementAndAppend(doc, homeCurrency, PRICE_VAT, String.valueOf(vatAmount));
      createElementAndAppend(doc, homeCurrency, PRICE_SUM, String.valueOf(item.getUnitPriceWithVat()));
      invoiceItem.appendChild(homeCurrency);

      final Element accounting = doc.createElement(ReceivedInvoice.ACCOUNTING);
      createElementAndAppend(doc, accounting, ACCOUNT_VALUE, item.getAccountValue());
      invoiceItem.appendChild(accounting);

      // Append the invoiceItem to the parent node
      invoiceItemsParent.appendChild(invoiceItem);
    }
  }

  public void updateCashReceiptDetails(final Document template, final Receipt receipt, String numberRequested) {
    replaceTextContent(template, INVOICE_NUMBER, numberRequested, null);
    replaceTextContent(template, ReceiptCash.DATE, receipt.getDate(), null);
    replaceTextContent(template, ReceiptCash.DATE_PAYMENT, receipt.getPaymentDate(), null);
    replaceTextContent(template, ReceiptCash.DATE_TAX, receipt.getTaxDate(), null);
    replaceTextContent(template, ACCOUNT_VALUE, receipt.getAccountValue(), ReceiptCash.ACCOUNTING);
    replaceTextContent(template, ACCOUNT_VALUE, receipt.getVatClassification(),
        ReceiptCash.CLASSIFICATION_VAT);
    replaceTextContent(template, ACCOUNT_VALUE, receipt.getKvVatClassification(),
        ReceiptCash.CLASSIFICATION_KV_VAT);
    replaceTextContent(template, ReceiptCash.TEXT, receipt.getDescription(), null);
  }

  public void updateCardReceiptDetails(final Document template, final Receipt receipt, String numberRequested) {
    replaceTextContent(template, INVOICE_NUMBER, numberRequested, null);
    replaceTextContent(template, ReceiptCard.VARIABLE_SYMBOL, numberRequested, null);
    replaceTextContent(template, ReceiptCard.DATE, receipt.getDate(), null);
    replaceTextContent(template, ReceiptCard.DATE_PAYMENT, receipt.getPaymentDate(), null);
    replaceTextContent(template, ReceiptCard.DATE_TAX, receipt.getTaxDate(), null);
    replaceTextContent(template, ReceiptCard.DATE_ACCOUNTING, receipt.getDate(), null);
    replaceTextContent(template, ReceiptCard.DATE_KV_VAT, receipt.getDate(), null);
    replaceTextContent(template, ACCOUNT_VALUE, receipt.getAccountValue(), ReceiptCard.ACCOUNTING);
    replaceTextContent(template, ACCOUNT_VALUE, receipt.getVatClassification(),
        ReceiptCard.CLASSIFICATION_VAT);
    replaceTextContent(template, ACCOUNT_VALUE, receipt.getKvVatClassification(),
        ReceiptCard.CLASSIFICATION_KV_VAT);
    replaceTextContent(template, ReceiptCard.TEXT, receipt.getDescription(), null);
  }

  public void updateCashReceiptMyIdentity(final Document template, final Company company) {
    // Pohoda supports company (legal entity) or surname (sole trader). We only support legal entities, so surname is always empty.
    // TODO: add surname support for sole traders
    replaceTextContent(template, COMPANY, company.getName(), ReceiptCash.MY_IDENTITY);
    replaceTextContent(template, SURNAME, "", ReceiptCash.MY_IDENTITY);

    replaceTextContent(template, CITY, company.getCity(), ReceiptCash.MY_IDENTITY);
    replaceTextContent(template, STREET, company.getStreet(), ReceiptCash.MY_IDENTITY);
    replaceTextContent(template, STREET_NUMBER, company.getStreetNumber(), ReceiptCash.MY_IDENTITY);
    replaceTextContent(template, ZIP, company.getZip(), ReceiptCash.MY_IDENTITY);
    replaceTextContent(template, REGISTRATION_NUMBER, company.getRegistrationNumber(),
        ReceiptCash.MY_IDENTITY);
    replaceTextContent(template, TAX_ID, company.getTaxId(), ReceiptCash.MY_IDENTITY);
    replaceTextContent(template, VAT_ID, company.getVatId(), ReceiptCash.MY_IDENTITY);
  }

  // Pohoda XML uses "my identity" to refer to the recipient
  public void updateCardReceiptMyIdentity(final Document template, final Company company) {
    // Pohoda supports company (legal entity) or surname (sole trader). We only support legal entities, so surname is always empty.
    // TODO: add surname support for sole traders
    replaceTextContent(template, COMPANY, company.getName(), ReceiptCard.MY_IDENTITY);
    replaceTextContent(template, SURNAME, "", ReceiptCard.MY_IDENTITY);

    replaceTextContent(template, CITY, company.getCity(), ReceiptCard.MY_IDENTITY);
    replaceTextContent(template, STREET, company.getStreet(), ReceiptCard.MY_IDENTITY);
    replaceTextContent(template, STREET_NUMBER, company.getStreetNumber(), ReceiptCard.MY_IDENTITY);
    replaceTextContent(template, ZIP, company.getZip(), ReceiptCard.MY_IDENTITY);
    replaceTextContent(template, REGISTRATION_NUMBER, company.getRegistrationNumber(),
        ReceiptCard.MY_IDENTITY);
    replaceTextContent(template, TAX_ID, company.getTaxId(), ReceiptCard.MY_IDENTITY);
    replaceTextContent(template, VAT_ID, company.getVatId(), ReceiptCard.MY_IDENTITY);
  }

  // Pohoda XML uses "partner" to refer to the supplier
  public void updateCashReceiptPartner(final Document template, final Receipt receipt) {
    replaceTextContent(template, COMPANY, receipt.getSupplierName(), ReceiptCash.PARTNER);
    replaceTextContent(template, CITY, receipt.getSupplierCity(), ReceiptCash.PARTNER);
    replaceTextContent(template, STREET, receipt.getSupplierStreet(), ReceiptCash.PARTNER);
    replaceTextContent(template, ZIP, receipt.getSupplierZip(), ReceiptCash.PARTNER);
    replaceTextContent(template, REGISTRATION_NUMBER, receipt.getSupplierRegistrationNumber(), ReceiptCash.PARTNER);
    replaceTextContent(template, TAX_ID, receipt.getSupplierTaxId(), ReceiptCash.PARTNER);
    replaceTextContent(template, VAT_ID, receipt.getSupplierVatId(), ReceiptCash.PARTNER);
  }

  // Pohoda XML uses "my identity" to refer to the recipient
  public void updateCardReceiptPartner(final Document template, final Receipt receipt) {
    replaceTextContent(template, COMPANY, receipt.getSupplierName(), ReceiptCard.PARTNER);
    replaceTextContent(template, CITY, receipt.getSupplierCity(), ReceiptCard.PARTNER);
    replaceTextContent(template, STREET, receipt.getSupplierStreet(), ReceiptCard.PARTNER);
    replaceTextContent(template, ZIP, receipt.getSupplierZip(), ReceiptCard.PARTNER);
    replaceTextContent(template, REGISTRATION_NUMBER, receipt.getSupplierRegistrationNumber(), ReceiptCard.PARTNER);
    replaceTextContent(template, TAX_ID, receipt.getSupplierTaxId(), ReceiptCard.PARTNER);
    replaceTextContent(template, VAT_ID, receipt.getSupplierVatId(), ReceiptCard.PARTNER);
  }

  public void updateCashReceiptItems(final Document template, final List<ReceiptItem> items) {
    final Element receiptItemsParent = (Element) template.getElementsByTagName(ReceiptCash.DETAIL)
        .item(0);
    for (final ReceiptItem item : items) {
      final Element receiptItem = template.createElement(ReceiptCash.ITEM);

      createElementAndAppend(template, receiptItem, ReceiptCash.TEXT, item.getName());
      createElementAndAppend(template, receiptItem, ReceiptCash.QUANTITY,
          String.valueOf(item.getQuantity()));
      createElementAndAppend(template, receiptItem, ReceiptCash.COEFFICIENT, "1");
      createElementAndAppend(template, receiptItem, ReceiptCash.PAY_VAT, "false");
      createElementAndAppend(template, receiptItem, ReceiptCash.RATE_VAT, "high");
      createElementAndAppend(template, receiptItem, ReceiptCash.DISCOUNT_PERCENTAGE, "0");

      final BigDecimal vatRate = BigDecimal.valueOf(item.getVatRate());

      // price w/o VAT
      final BigDecimal priceWithoutVAT = item.getUnitPriceWithoutVat();

      final BigDecimal vatAmount = priceWithoutVAT.multiply(vatRate)
          .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

      final Element homeCurrency = template.createElement(ReceiptCash.HOME_CURRENCY);
      createElementAndAppend(template, homeCurrency, UNIT_PRICE, String.valueOf(item.getUnitPriceWithoutVat()));
      createElementAndAppend(template, homeCurrency, PRICE, String.valueOf(item.getUnitPriceWithoutVat()));
      createElementAndAppend(template, homeCurrency, PRICE_VAT, String.valueOf(vatAmount));
      createElementAndAppend(template, homeCurrency, PRICE_SUM, String.valueOf(item.getTotalItemPriceWithVat()));
      receiptItem.appendChild(homeCurrency);

      final Element accounting = template.createElement(ReceiptCash.ACCOUNTING);
      createElementAndAppend(template, accounting, ACCOUNT_VALUE, item.getAccountValue());
      receiptItem.appendChild(accounting);

      // Append the receiptItem to the parent node
      receiptItemsParent.appendChild(receiptItem);
    }
  }

  public void updateCardReceiptItems(final Document template, final List<ReceiptItem> items) {
    final Element receiptItemsParent = (Element) template.getElementsByTagName(ReceiptCard.DETAIL)
        .item(0);
    for (final ReceiptItem item : items) {
      final Element receiptItem = template.createElement(ReceiptCard.ITEM);

      createElementAndAppend(template, receiptItem, ReceiptCard.TEXT, item.getName());
      createElementAndAppend(template, receiptItem, ReceiptCard.QUANTITY,
          String.valueOf(item.getQuantity()));
      createElementAndAppend(template, receiptItem, ReceiptCard.COEFFICIENT, "1");
      createElementAndAppend(template, receiptItem, ReceiptCard.PAY_VAT, "false");
      createElementAndAppend(template, receiptItem, ReceiptCard.RATE_VAT, "high");
      createElementAndAppend(template, receiptItem, ReceiptCard.DISCOUNT_PERCENTAGE, "0");

      final BigDecimal vatRate = BigDecimal.valueOf(item.getVatRate());

      // price w/o VAT
      final BigDecimal priceWithoutVAT = item.getUnitPriceWithoutVat();

      final BigDecimal vatAmount = priceWithoutVAT.multiply(vatRate)
          .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

      //TODO it seems to be duplicate to the updateCashReceiptItems
      final Element homeCurrency = template.createElement(ReceiptCard.HOME_CURRENCY);
      createElementAndAppend(template, homeCurrency, UNIT_PRICE, String.valueOf(item.getUnitPriceWithoutVat()));
      createElementAndAppend(template, homeCurrency, PRICE, String.valueOf(item.getUnitPriceWithoutVat()));
      createElementAndAppend(template, homeCurrency, PRICE_VAT, String.valueOf(vatAmount));
      createElementAndAppend(template, homeCurrency, PRICE_SUM, String.valueOf(item.getUnitPriceWithVat()));
      receiptItem.appendChild(homeCurrency);

      final Element accounting = template.createElement(ReceiptCard.ACCOUNTING);
      createElementAndAppend(template, accounting, ACCOUNT_VALUE, item.getAccountValue());
      receiptItem.appendChild(accounting);

      // Append the receiptItem to the parent node
      receiptItemsParent.appendChild(receiptItem);
    }
  }

  private void createElementAndAppend(final Document template, final Element parent, final String tagName,
      final String value) {
    if (value != null) {
      final Element element = template.createElement(tagName);
      element.appendChild(template.createTextNode(value));
      parent.appendChild(element);
    }
  }

  private void replaceTextContent(final Document template, final String tagName, final String newValue,
      final String parentTagName) {
    final NodeList nodeList;
    if (parentTagName != null && !parentTagName.isEmpty()) {
      final NodeList parentNodeList = template.getElementsByTagName(parentTagName);
      if (parentNodeList.getLength() > 0) {
        final Node parentNode = parentNodeList.item(0);
        nodeList = ((Element) parentNode).getElementsByTagName(tagName);
      } else {
        return;
      }
    } else {
      nodeList = template.getElementsByTagName(tagName);
    }

    if (nodeList.getLength() > 0) {
      nodeList.item(0).setTextContent(newValue);
    }
  }

}
