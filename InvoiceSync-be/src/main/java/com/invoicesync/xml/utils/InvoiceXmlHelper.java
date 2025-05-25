package com.invoicesync.xml.utils;

import static com.invoicesync.xml.utils.PohodaXmlConstants.*;
import static com.invoicesync.xml.utils.PohodaXmlParentTagNames.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.invoicesync.dto.identity.MyIdentityDTO;
import com.invoicesync.dto.identity.PartnerDto;
import com.invoicesync.dto.invoice.pohoda.InvoiceItemDTO;
import com.invoicesync.dto.invoice.pohoda.InvoiceRequestDetailsDTO;
import com.invoicesync.dto.receipt.pohoda.ReceiptItemDto;
import com.invoicesync.dto.receipt.pohoda.ReceiptRequestDetailsDTO;

@Component
public class InvoiceXmlHelper {

  public void updateInvoiceDetails(final Document doc, final InvoiceRequestDetailsDTO invoiceRequestDetailsDTO) {
    replaceTextContent(doc, INVOICE_TYPE, invoiceRequestDetailsDTO.getInvoiceType(), null);
    replaceTextContent(doc, INVOICE_NUMBER, invoiceRequestDetailsDTO.getInvoiceNumber(),
        NUMBER);
    replaceTextContent(doc, VARIABLE_SYMBOL, invoiceRequestDetailsDTO.getVariableSymbol(), null);
    replaceTextContent(doc, PAIRING_SYMBOL, invoiceRequestDetailsDTO.getPairingSymbol(), null);
    replaceTextContent(doc, DATE, invoiceRequestDetailsDTO.getDateIssue(), null);
    replaceTextContent(doc, DATE_TAX, invoiceRequestDetailsDTO.getDateTax(), null);
    replaceTextContent(doc, DATE_ACCOUNTING, invoiceRequestDetailsDTO.getDateAccounting(), null);
    replaceTextContent(doc, DATE_DUE, invoiceRequestDetailsDTO.getDateDue(), null);
  }

  public void updateMyIdentity(final Document doc, final MyIdentityDTO myIdentityDTO) {
    replaceTextContent(doc, COMPANY, myIdentityDTO.getName(), MY_IDENTITY);
    replaceTextContent(doc, CITY, myIdentityDTO.getCity(), MY_IDENTITY);
    replaceTextContent(doc, STREET, myIdentityDTO.getStreet(), MY_IDENTITY);
    replaceTextContent(doc, STREET_NUMBER, myIdentityDTO.getStreetNumber(), MY_IDENTITY);
    replaceTextContent(doc, ZIP, myIdentityDTO.getZip(), MY_IDENTITY);
    replaceTextContent(doc, REGISTRATION_NUMBER, myIdentityDTO.getRegistrationNumber(),
        MY_IDENTITY);
    replaceTextContent(doc, TAX_ID, myIdentityDTO.getTaxId(), MY_IDENTITY);
    replaceTextContent(doc, VAT_ID, myIdentityDTO.getVatId(), MY_IDENTITY);
  }

  public void updatePartner(final Document doc, final PartnerDto partnerDTO) {
    replaceTextContent(doc, NAME, partnerDTO.getName(), PARTNER);
    replaceTextContent(doc, CITY, partnerDTO.getCity(), PARTNER);
    replaceTextContent(doc, STREET, partnerDTO.getStreet(), PARTNER);
    replaceTextContent(doc, ZIP, partnerDTO.getZip(), PARTNER);
    replaceTextContent(doc, REGISTRATION_NUMBER, partnerDTO.getRegistrationNumber(), PARTNER);
    replaceTextContent(doc, TAX_ID, partnerDTO.getTaxId(), PARTNER);
    replaceTextContent(doc, VAT_ID, partnerDTO.getVatId(), PARTNER);
  }

  public void updateInvoiceItems(final Document doc, final List<InvoiceItemDTO> items) {
      final Element invoiceItemsParent = (Element) doc.getElementsByTagName(INVOICE_DETAIL)
          .item(0);
      for (final InvoiceItemDTO item : items) {
        final Element invoiceItem = doc.createElement(INVOICE_ITEM);

        // Set the relevant fields for the invoiceItem
        createElementAndAppend(doc, invoiceItem, TEXT, "test");
        createElementAndAppend(doc, invoiceItem, QUANTITY,
            String.valueOf(item.getQuantity()));
        createElementAndAppend(doc, invoiceItem, COEFFICIENT, "1");
        createElementAndAppend(doc, invoiceItem, PAY_VAT, "false");
        createElementAndAppend(doc, invoiceItem, RATE_VAT, "high");
        createElementAndAppend(doc, invoiceItem, DISCOUNT_PERCENTAGE, "0");

        // Handle homeCurrency and pricing
        final Element homeCurrency = doc.createElement(HOME_CURRENCY);
        createElementAndAppend(doc, homeCurrency, UNIT_PRICE, String.valueOf(item.getUnitPrice()));
        createElementAndAppend(doc, homeCurrency, PRICE, String.valueOf(item.getPrice()));
        createElementAndAppend(doc, homeCurrency, PRICE_VAT, String.valueOf(item.getPriceVAT()));
        createElementAndAppend(doc, homeCurrency, PRICE_SUM, String.valueOf(item.getPriceSum()));
        invoiceItem.appendChild(homeCurrency);

        final Element accounting = doc.createElement(ACCOUNTING);
        createElementAndAppend(doc, accounting, ACCOUNT_VALUE, item.getAccountValue());
        invoiceItem.appendChild(accounting);

        // Append the invoiceItem to the parent node
        invoiceItemsParent.appendChild(invoiceItem);
      }
  }

  public void updateReceiptDetails(final Document doc, final ReceiptRequestDetailsDTO receiptRequestDetailsDTO) {
    replaceTextContent(doc, INVOICE_NUMBER, receiptRequestDetailsDTO.getNumberRequested(), null);
    replaceTextContent(doc, RECEIPT_DATE, receiptRequestDetailsDTO.getDate(), null);
    replaceTextContent(doc, RECEIPT_DATE_PAYMENT, receiptRequestDetailsDTO.getDate(), null);
    replaceTextContent(doc, RECEIPT_DATE_TAX, receiptRequestDetailsDTO.getDateTax(), null);
    replaceTextContent(doc, RECEIPT_ACCOUNTING, receiptRequestDetailsDTO.getAccountValue(), null);
    replaceTextContent(doc, RECEIPT_CLASSIFICATION_VAT, receiptRequestDetailsDTO.getClassificationVAT(), null);
    replaceTextContent(doc, RECEIPT_CLASSIFICATION_KV_VAT, receiptRequestDetailsDTO.getClassificationKVVAT(), null);
    replaceTextContent(doc, RECEIPT_TEXT, receiptRequestDetailsDTO.getDescription(), null);
  }

  public void updateReceiptMyIdentity(final Document doc, final MyIdentityDTO myIdentityDTO) {
    replaceTextContent(doc, COMPANY, myIdentityDTO.getName(), RECEIPT_MY_IDENTITY);
    replaceTextContent(doc, CITY, myIdentityDTO.getCity(), RECEIPT_MY_IDENTITY);
    replaceTextContent(doc, STREET, myIdentityDTO.getStreet(), RECEIPT_MY_IDENTITY);
    replaceTextContent(doc, STREET_NUMBER, myIdentityDTO.getStreetNumber(), RECEIPT_MY_IDENTITY);
    replaceTextContent(doc, ZIP, myIdentityDTO.getZip(), RECEIPT_MY_IDENTITY);
    replaceTextContent(doc, REGISTRATION_NUMBER, myIdentityDTO.getRegistrationNumber(),
        RECEIPT_MY_IDENTITY);
    replaceTextContent(doc, TAX_ID, myIdentityDTO.getTaxId(), RECEIPT_MY_IDENTITY);
    replaceTextContent(doc, VAT_ID, myIdentityDTO.getVatId(), RECEIPT_MY_IDENTITY);
  }

  public void updateReceiptPartner(final Document doc, final PartnerDto partnerDTO) {
    replaceTextContent(doc, COMPANY, partnerDTO.getName(), RECEIPT_PARTNER);
    replaceTextContent(doc, CITY, partnerDTO.getCity(), RECEIPT_PARTNER);
    replaceTextContent(doc, STREET, partnerDTO.getStreet(), RECEIPT_PARTNER);
    replaceTextContent(doc, ZIP, partnerDTO.getZip(), RECEIPT_PARTNER);
    replaceTextContent(doc, REGISTRATION_NUMBER, partnerDTO.getRegistrationNumber(), RECEIPT_PARTNER);
    replaceTextContent(doc, TAX_ID, partnerDTO.getTaxId(), RECEIPT_PARTNER);
    replaceTextContent(doc, VAT_ID, partnerDTO.getVatId(), RECEIPT_PARTNER);
  }

  public void updateReceiptItems(final Document doc, final List<ReceiptItemDto> items) {
    final Element receiptItemsParent = (Element) doc.getElementsByTagName(RECEIPT_DETAIL)
        .item(0);
    for (final ReceiptItemDto item : items) {
      final Element receiptItem = doc.createElement(RECEIPT_ITEM);

      createElementAndAppend(doc, receiptItem, TEXT, item.getAccountText());
      createElementAndAppend(doc, receiptItem, QUANTITY,
          String.valueOf(item.getQuantity()));
      createElementAndAppend(doc, receiptItem, COEFFICIENT, "1");
      createElementAndAppend(doc, receiptItem, PAY_VAT, "false");
      createElementAndAppend(doc, receiptItem, RATE_VAT, "high");
      createElementAndAppend(doc, receiptItem, DISCOUNT_PERCENTAGE, "0");

      final BigDecimal vatRate = BigDecimal.valueOf(item.getVatRate());  // Sadza DPH, napr. 19

      // Cena bez DPH
      final BigDecimal priceWithoutVAT = item.getPriceWithoutVAT();

      // Vypočítať DPH ako čiastku (t.j. podiel z ceny bez DPH)
      final BigDecimal vatAmount = priceWithoutVAT.multiply(vatRate)
          .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
      
      final Element homeCurrency = doc.createElement(RECEIPT_HOME_CURRENCY);
      createElementAndAppend(doc, homeCurrency, UNIT_PRICE, String.valueOf(item.getPriceWithoutVAT()));
      createElementAndAppend(doc, homeCurrency, PRICE, String.valueOf(item.getPriceWithoutVAT()));
      createElementAndAppend(doc, homeCurrency, PRICE_VAT, String.valueOf(vatAmount));
      createElementAndAppend(doc, homeCurrency, PRICE_SUM, String.valueOf(item.getPriceWithVAT()));
      receiptItem.appendChild(homeCurrency);

      final Element accounting = doc.createElement(ACCOUNTING);
      createElementAndAppend(doc, accounting, ACCOUNT_VALUE, item.getAccountValue());
      receiptItem.appendChild(accounting);

      // Append the receiptItem to the parent node
      receiptItemsParent.appendChild(receiptItem);
    }
  }

  private void createElementAndAppend(final Document doc, final Element parent, final String tagName,
      final String value) {
    if (value != null) {
      final Element element = doc.createElement(tagName);
      element.appendChild(doc.createTextNode(value));
      parent.appendChild(element);
    }
  }

  private void replaceTextContent(final Document doc, final String tagName, final String newValue,
      final String parentTagName) {
    final NodeList nodeList;
    if (parentTagName != null && !parentTagName.isEmpty()) {
      final NodeList parentNodeList = doc.getElementsByTagName(parentTagName);
      if (parentNodeList.getLength() > 0) {
        final Node parentNode = parentNodeList.item(0);
        nodeList = ((Element) parentNode).getElementsByTagName(tagName);
      } else {
        return;
      }
    } else {
      nodeList = doc.getElementsByTagName(tagName);
    }

    if (nodeList.getLength() > 0) {
      nodeList.item(0).setTextContent(newValue);
    }
  }

}
