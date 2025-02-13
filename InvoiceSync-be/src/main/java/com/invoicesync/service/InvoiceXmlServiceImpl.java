package com.invoicesync.service;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.invoicesync.dto.invoice.pohoda.InvoiceRequestDTO;
import com.invoicesync.dto.receipt.pohoda.ReceiptRequestDTO;
import com.invoicesync.xml.utils.InvoiceXmlHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceXmlServiceImpl implements InvoiceXmlService{

  private final InvoiceXmlHelper xmlHelper;

  @Override
  public byte[] generatePohodaInvoiceXml(final InvoiceRequestDTO invoiceRequestDTO) throws Exception {
    final File xmlFile = new File("src/main/resources/template/invoice_template.xml");
    final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    final Document doc = dBuilder.parse(xmlFile);

    dbFactory.setNamespaceAware(true);
    doc.getDocumentElement().normalize();

    xmlHelper.updateInvoiceDetails(doc, invoiceRequestDTO.getInvoiceDetails());
    xmlHelper.updateMyIdentity(doc, invoiceRequestDTO.getMyIdentity());
    xmlHelper.updatePartner(doc, invoiceRequestDTO.getPartner());
    xmlHelper.updateInvoiceItems(doc, invoiceRequestDTO.getItems());

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(new DOMSource(doc), new StreamResult(outputStream));

    return outputStream.toByteArray();
  }

  @Override
  public byte[] generatePohodaReceiptXml(final ReceiptRequestDTO receiptRequestDTO) throws Exception {
    final File xmlFileReceipt = new File("src/main/resources/template/receipt_template.xml");
    final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    final Document doc = dBuilder.parse(xmlFileReceipt);

    dbFactory.setNamespaceAware(true);
    doc.getDocumentElement().normalize();

    xmlHelper.updateReceiptDetails(doc, receiptRequestDTO.getReceiptDetails());
    xmlHelper.updateReceiptMyIdentity(doc, receiptRequestDTO.getMyIdentity());
    xmlHelper.updateReceiptPartner(doc, receiptRequestDTO.getPartner());
    xmlHelper.updateReceiptItems(doc, receiptRequestDTO.getItems());

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(new DOMSource(doc), new StreamResult(outputStream));

    return outputStream.toByteArray();
  }

}
