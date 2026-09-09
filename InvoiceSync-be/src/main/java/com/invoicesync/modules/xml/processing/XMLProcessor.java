package com.invoicesync.modules.xml.processing;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XMLProcessor {

  public static void processFile(final String xmlContent, final File fileTemplate, final OutputStream outputStream) throws Exception {
    try {
      // Path to the XML file
      final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      dbFactory.setNamespaceAware(true);
      final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

      final InputStream xmlInputStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
      final Document doc = dBuilder.parse(xmlInputStream);
      doc.getDocumentElement().normalize();

      String uhrada_eng;

      try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
        final NodeList nList = doc.getElementsByTagName("doklad");

        for (int temp = 0; temp < nList.getLength(); temp++) {
          final Node nNode = nList.item(temp);

          final Document templateDoc = dBuilder.parse(fileTemplate);
          templateDoc.getDocumentElement().normalize();

          final NodeList paymentTypeList = templateDoc.getElementsByTagName("inv:paymentType");

          final Element element = (Element) nNode;

          final NodeList polozky = element.getElementsByTagName("polozka");
          final String invNamespace = "http://www.stormware.cz/schema/version_2/invoice.xsd"; // Correct namespace for invoice-related elements
          final NodeList invoiceItems = templateDoc.getElementsByTagNameNS(invNamespace, "invoiceItem");

          if (polozky.getLength() == 1) {
            System.out.println(polozky.getLength());
            // If there is only one "polozka", remove the second and third invoiceItems from the template virtually not modify the template
            //remove redundant invoiceItems and save the xml file
            final Element invoiceItem2 = (Element) invoiceItems.item(1);
            invoiceItem2.getParentNode().removeChild(invoiceItem2);// Remove the second invoiceItem

            //remove the third invoiceItem
            final Element invoiceItem3 = (Element) invoiceItems.item(1);
            invoiceItem3.getParentNode().removeChild(invoiceItem3);

            final NodeList nodeList = templateDoc.getElementsByTagNameNS(
                "http://www.stormware.cz/schema/version_2/invoice.xsd", "invoiceSummary");
            final Node invoiceSummaryNode = nodeList.item(0);
            invoiceSummaryNode.getParentNode().removeChild(invoiceSummaryNode);

            final NodeList unitPirce = templateDoc.getElementsByTagName("typ:unitPrice");
            final NodeList price = templateDoc.getElementsByTagName("typ:price");
            final NodeList priceVat = templateDoc.getElementsByTagName("typ:priceVAT");
            final NodeList priceSum = templateDoc.getElementsByTagName("typ:priceSum");

            processInvoiceItem((Element) polozky.item(0), unitPirce, price, priceVat, priceSum, 0);
          }

          if (polozky.getLength() == 2) {
            final Element invoiceItem3 = (Element) invoiceItems.item(2);
            invoiceItem3.getParentNode().removeChild(invoiceItem3);

            final Element firstPolozka = (Element) polozky.item(0);
            final Element secondPolozka = (Element) polozky.item(1);

            final NodeList unitPirce = templateDoc.getElementsByTagName("typ:unitPrice");
            final NodeList price = templateDoc.getElementsByTagName("typ:price");
            final NodeList priceVat = templateDoc.getElementsByTagName("typ:priceVAT");
            final NodeList priceSum = templateDoc.getElementsByTagName("typ:priceSum");

            final String cena_cenik = getElementText(element, "cena_cenik");
            final String casta_dph = getElementText(element, "castka_dph");
            final String cena_celkem = getElementText(element, "cena_celkem");

            final BigDecimal cenaCelkem = new BigDecimal(cena_celkem);
            final BigDecimal castkaDph = new BigDecimal(casta_dph);
            final BigDecimal sum = cenaCelkem.add(castkaDph);

            final Element unitPirceElement = (Element) unitPirce.item(0);
            final Element priceElement = (Element) price.item(0);
            final Element priceVatElement = (Element) priceVat.item(0);
            final Element priceSumElement = (Element) priceSum.item(0);

            unitPirceElement.setTextContent(cena_cenik);
            priceElement.setTextContent(cena_cenik);
            priceVatElement.setTextContent(casta_dph);
            priceSumElement.setTextContent(sum.toString());

            final String cena_cenik2 = getElementText(secondPolozka, "cena_cenik");
            final String casta_dph2 = getElementText(secondPolozka, "castka_dph");
            final String cena_celkem2 = getElementText(secondPolozka, "cena_celkem");

            final Element unitPirceElement2 = (Element) unitPirce.item(1);
            final Element priceElement2 = (Element) price.item(1);
            final Element priceVatElement2 = (Element) priceVat.item(1);
            final Element priceSumElement2 = (Element) priceSum.item(1);

            final BigDecimal cenaCelkem2 = new BigDecimal(cena_celkem2);
            final BigDecimal castkaDph2 = new BigDecimal(casta_dph2);
            final BigDecimal sum2 = cenaCelkem2.add(castkaDph2);

            unitPirceElement2.setTextContent(cena_cenik2);
            priceElement2.setTextContent(cena_cenik2);
            priceVatElement2.setTextContent(casta_dph2);
            priceSumElement2.setTextContent(sum2.toString());

            processInvoiceItem((Element) polozky.item(0), unitPirce, price, priceVat, priceSum, 0);
            processInvoiceItem((Element) polozky.item(1), unitPirce, price, priceVat, priceSum, 1);
          }

          if (polozky.getLength() == 3) {

            final NodeList unitPirce = templateDoc.getElementsByTagName("typ:unitPrice");
            final NodeList price = templateDoc.getElementsByTagName("typ:price");
            final NodeList priceVat = templateDoc.getElementsByTagName("typ:priceVAT");
            final NodeList priceSum = templateDoc.getElementsByTagName("typ:priceSum");

            processInvoiceItem((Element) polozky.item(0), unitPirce, price, priceVat, priceSum, 2);
            processInvoiceItem((Element) polozky.item(1), unitPirce, price, priceVat, priceSum, 0);
            processInvoiceItem((Element) polozky.item(2), unitPirce, price, priceVat, priceSum, 1);
          }

          if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            // Extract doklad information
            final String varSymbol = getElementText(element, "var_symbol");
            final String extCislo = getElementText(element, "ext_cislo");
            final String datVyst = getElementText(element, "dat_vyst");
            final String datZdPln = getElementText(element, "dat_zd_pln");
            final String datSpl = getElementText(element, "dat_spl");
            String forma_uhrady = getElementText(element, "forma_uhrady");
            final String nazev = getElementText(element, "nazev");
            final String ulica = getElementText(element, "ulice");
            final String psc = getElementText(element, "psc");
            final String obec = getElementText(element, "obec");

            // Format the date
            final String formattedDatVyst = formatDate(datVyst, "dd.MM.yyyy", "yyyy-MM-dd");
            final String formattedDatZdPln = formatDate(datZdPln, "dd.MM.yyyy", "yyyy-MM-dd");
            final String formattedDatSpl = formatDate(datSpl, "dd.MM.yyyy", "yyyy-MM-dd");

            if ("PKO".equals(forma_uhrady)) {
              forma_uhrady = "Plat.kartou";
              uhrada_eng = "creditcard";
            } else {
              forma_uhrady = "Dobierkou";
              uhrada_eng = "delivery";
            }

            final Element paymentTypeElement = (Element) paymentTypeList.item(0);

            final NodeList idsList = paymentTypeElement.getElementsByTagName("typ:ids");
            final NodeList numberRequested = templateDoc.getElementsByTagName("typ:numberRequested");
            final NodeList symVarList = templateDoc.getElementsByTagName("inv:symVar");
            final NodeList symParList = templateDoc.getElementsByTagName("inv:symPar");
            final NodeList date = templateDoc.getElementsByTagName("inv:date");
            final NodeList dateTax = templateDoc.getElementsByTagName("inv:dateTax");
            final NodeList dateDue = templateDoc.getElementsByTagName("inv:dateDue");
            final NodeList dateAccounting = templateDoc.getElementsByTagName("inv:dateAccounting");
            final NodeList name = templateDoc.getElementsByTagName("typ:name");
            final NodeList city = templateDoc.getElementsByTagName("typ:city");
            final NodeList street = templateDoc.getElementsByTagName("typ:street");
            final NodeList zip = templateDoc.getElementsByTagName("typ:zip");
            final NodeList numberOrder = templateDoc.getElementsByTagName("inv:numberOrder");
            final NodeList paymentMethod = templateDoc.getElementsByTagName("typ:paymentType");

            final Element numberRequestedElement = (Element) numberRequested.item(0);
            final Element idsListElement = (Element) idsList.item(0);
            final Element symVarElement = (Element) symVarList.item(0);
            final Element symParElement = (Element) symParList.item(0);
            final Element dateElement = (Element) date.item(0);
            final Element dateTaxElement = (Element) dateTax.item(0);
            final Element dateDueElement = (Element) dateDue.item(0);
            final Element dateAccountingElement = (Element) dateAccounting.item(0);
            final Element nameElement = (Element) name.item(0);
            final Element cityElement = (Element) city.item(0);
            final Element streetElement = (Element) street.item(0);
            final Element zipElement = (Element) zip.item(0);
            final Element numberOrderElement = (Element) numberOrder.item(0);
            final Element paymentMethodElement = (Element) paymentMethod.item(0);

            numberRequestedElement.setTextContent(extCislo);
            numberOrderElement.setTextContent(varSymbol);
            symVarElement.setTextContent(varSymbol);
            symParElement.setTextContent(varSymbol);
            dateElement.setTextContent(formattedDatVyst);
            dateTaxElement.setTextContent(formattedDatZdPln);
            dateDueElement.setTextContent(formattedDatSpl);
            dateAccountingElement.setTextContent(formattedDatZdPln);
            nameElement.setTextContent(nazev);
            cityElement.setTextContent(obec);
            streetElement.setTextContent(ulica);
            zipElement.setTextContent(psc);
            paymentMethodElement.setTextContent(uhrada_eng);
            idsListElement.setTextContent(forma_uhrady);

            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8))) {
              saveXML(templateDoc, writer);
            }

            final String uniqueFilename = "generated_file_" + extCislo + ".xml";
            final ZipEntry zipEntry = new ZipEntry(uniqueFilename);
            zos.putNextEntry(zipEntry);
            zos.write(byteArrayOutputStream.toByteArray());
            zos.closeEntry();
          }

        }
      }
    } catch (Exception e) {
      log.error("[XML] Failed to process XML for Pohoda export", e);
      throw e;
    }
  }

  private static void processInvoiceItem(final Element polozka, final NodeList unitPriceList, final NodeList priceList,
      final NodeList priceVatList, final NodeList priceSumList, final int invoiceItemIndex) {
    final String cenaCenik = getElementText(polozka, "cena_cenik");
    final String castkaDph = getElementText(polozka, "castka_dph");
    final String cenaCelkem = getElementText(polozka, "cena_celkem");

    final BigDecimal cenaCelkemDecimal = new BigDecimal(cenaCelkem);
    final BigDecimal castkaDphDecimal = new BigDecimal(castkaDph);
    final BigDecimal sum = cenaCelkemDecimal.add(castkaDphDecimal);

    final Element unitPriceElement = (Element) unitPriceList.item(invoiceItemIndex);
    final Element priceElement = (Element) priceList.item(invoiceItemIndex);
    final Element priceVatElement = (Element) priceVatList.item(invoiceItemIndex);
    final Element priceSumElement = (Element) priceSumList.item(invoiceItemIndex);

    unitPriceElement.setTextContent(cenaCenik);
    priceElement.setTextContent(cenaCenik);
    priceVatElement.setTextContent(castkaDph);
    priceSumElement.setTextContent(sum.toString());
  }

  private static void saveXML(final Document doc, final Writer writer) throws Exception {
    final javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
    final javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
    final javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(doc);
    final javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(writer);
    transformer.transform(source, result);
  }

  private static String getElementText(final Element element, final String tagName) {
    final NodeList list = element.getElementsByTagName(tagName);
    return list.getLength() > 0 ? list.item(0).getTextContent() : null;
  }

  private static String formatDate(final String dateStr, final String originalFormat, final String targetFormat) {
    try {
      final SimpleDateFormat sdf = new SimpleDateFormat(originalFormat);
      final Date date = sdf.parse(dateStr);
      sdf.applyPattern(targetFormat);
      return sdf.format(date);
    } catch (Exception e) {
      log.warn("[XML] Failed to parse date '{}' with format '{}'", dateStr, originalFormat, e);
      return null;
    }
  }

}
