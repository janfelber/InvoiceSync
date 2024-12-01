package com.invoicesync.xml.processing;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class XMLProcessor {
    public static void processFile(String xmlContent, File fileTemplate, OutputStream outputStream) throws Exception {
        try {
            // Path to the XML file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            InputStream xmlInputStream = new ByteArrayInputStream(xmlContent.getBytes());
            Document doc = dBuilder.parse(xmlInputStream);
            doc.getDocumentElement().normalize();

            String uhrada_eng;

            try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
                NodeList nList = doc.getElementsByTagName("doklad");

                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);

                    Document templateDoc = dBuilder.parse(fileTemplate);
                    templateDoc.getDocumentElement().normalize();

                    NodeList paymentTypeList = templateDoc.getElementsByTagName("inv:paymentType");

                    Element element = (Element) nNode;


                    NodeList polozky = element.getElementsByTagName("polozka");
                    String invNamespace = "http://www.stormware.cz/schema/version_2/invoice.xsd"; // Correct namespace for invoice-related elements
                    NodeList invoiceItems = templateDoc.getElementsByTagNameNS(invNamespace, "invoiceItem");

                    if (polozky.getLength() == 1) {
                        System.out.println(polozky.getLength());
                        // If there is only one "polozka", remove the second and third invoiceItems from the template virtually not modify the template
                        //remove redundant invoiceItems and save the xml file
                        Element invoiceItem2 = (Element) invoiceItems.item(1);
                        invoiceItem2.getParentNode().removeChild(invoiceItem2);// Remove the second invoiceItem

                        //remove the third invoiceItem
                        Element invoiceItem3 = (Element) invoiceItems.item(1);
                        invoiceItem3.getParentNode().removeChild(invoiceItem3);

                        NodeList nodeList = templateDoc.getElementsByTagNameNS("http://www.stormware.cz/schema/version_2/invoice.xsd", "invoiceSummary");
                        Node invoiceSummaryNode = nodeList.item(0);
                        invoiceSummaryNode.getParentNode().removeChild(invoiceSummaryNode);

                        NodeList unitPirce = templateDoc.getElementsByTagName("typ:unitPrice");
                        NodeList price = templateDoc.getElementsByTagName("typ:price");
                        NodeList priceVat = templateDoc.getElementsByTagName("typ:priceVAT");
                        NodeList priceSum = templateDoc.getElementsByTagName("typ:priceSum");

                        processInvoiceItem((Element) polozky.item(0), unitPirce, price, priceVat, priceSum, 0);
                    }


                    if (polozky.getLength() == 2) {
                        Element invoiceItem3 = (Element) invoiceItems.item(2);
                        invoiceItem3.getParentNode().removeChild(invoiceItem3);

                        Element firstPolozka = (Element) polozky.item(0);
                        Element secondPolozka = (Element) polozky.item(1);

                        NodeList unitPirce = templateDoc.getElementsByTagName("typ:unitPrice");
                        NodeList price = templateDoc.getElementsByTagName("typ:price");
                        NodeList priceVat = templateDoc.getElementsByTagName("typ:priceVAT");
                        NodeList priceSum = templateDoc.getElementsByTagName("typ:priceSum");

                        String cena_cenik = getElementText(element, "cena_cenik");
                        String casta_dph = getElementText(element, "castka_dph");
                        String cena_celkem = getElementText(element, "cena_celkem");

                        BigDecimal cenaCelkem = new BigDecimal(cena_celkem);
                        BigDecimal castkaDph = new BigDecimal(casta_dph);
                        BigDecimal sum = cenaCelkem.add(castkaDph);

                        Element unitPirceElement = (Element) unitPirce.item(0);
                        Element priceElement = (Element) price.item(0);
                        Element priceVatElement = (Element) priceVat.item(0);
                        Element priceSumElement = (Element) priceSum.item(0);

                        unitPirceElement.setTextContent(cena_cenik);
                        priceElement.setTextContent(cena_cenik);
                        priceVatElement.setTextContent(casta_dph);
                        priceSumElement.setTextContent(sum.toString());

                        String cena_cenik2 = getElementText(secondPolozka, "cena_cenik");
                        String casta_dph2 = getElementText(secondPolozka, "castka_dph");
                        String cena_celkem2 = getElementText(secondPolozka, "cena_celkem");

                        Element unitPirceElement2 = (Element) unitPirce.item(1);
                        Element priceElement2 = (Element) price.item(1);
                        Element priceVatElement2 = (Element) priceVat.item(1);
                        Element priceSumElement2 = (Element) priceSum.item(1);

                        BigDecimal cenaCelkem2 = new BigDecimal(cena_celkem2);
                        BigDecimal castkaDph2 = new BigDecimal(casta_dph2);
                        BigDecimal sum2 = cenaCelkem2.add(castkaDph2);

                        unitPirceElement2.setTextContent(cena_cenik2);
                        priceElement2.setTextContent(cena_cenik2);
                        priceVatElement2.setTextContent(casta_dph2);
                        priceSumElement2.setTextContent(sum2.toString());

                        processInvoiceItem((Element) polozky.item(0), unitPirce, price, priceVat, priceSum, 0);
                        processInvoiceItem((Element) polozky.item(1), unitPirce, price, priceVat, priceSum, 1);
                    }

                    if (polozky.getLength() == 3) {

                        NodeList unitPirce = templateDoc.getElementsByTagName("typ:unitPrice");
                        NodeList price = templateDoc.getElementsByTagName("typ:price");
                        NodeList priceVat = templateDoc.getElementsByTagName("typ:priceVAT");
                        NodeList priceSum = templateDoc.getElementsByTagName("typ:priceSum");

//                        Element firstPolozka = (Element) polozky.item(0);
//                        Element secondPolozka = (Element) polozky.item(1);
//                        Element thirdPolozka = (Element) polozky.item(2);
//
//                        String cena_cenik = getElementText(firstPolozka, "cena_cenik");
//                        String casta_dph = getElementText(firstPolozka, "castka_dph");
//                        String cena_celkem = getElementText(firstPolozka, "cena_celkem");
//
//                        BigDecimal cenaCelkem = new BigDecimal(cena_celkem);
//                        BigDecimal castkaDph = new BigDecimal(casta_dph);
//                        BigDecimal sum = cenaCelkem.add(castkaDph);
//
//                        // zaokruhlenie
//                        Element unitPirceElement = (Element) unitPirce.item(2);
//                        Element priceElement = (Element) price.item(2);
//                        Element priceVatElement = (Element) priceVat.item(2);
//                        Element priceSumElement = (Element) priceSum.item(2);
//
//                        unitPirceElement.setTextContent(cena_cenik);
//                        priceElement.setTextContent(cena_cenik);
//                        priceVatElement.setTextContent(casta_dph);
//                        priceSumElement.setTextContent(sum.toString());
//
//                        String cena_cenik2 = getElementText(secondPolozka, "cena_cenik");
//                        String casta_dph2 = getElementText(secondPolozka, "castka_dph");
//                        String cena_celkem2 = getElementText(secondPolozka, "cena_celkem");
//
//                        System.out.println("cena_cenik2: " + cena_cenik2);
//                        System.out.println("casta_dph2: " + casta_dph2);
//                        System.out.println("cena_celkem2: " + cena_celkem2);
//
//                        // tovar price
//                        Element unitPirceElement2 = (Element) unitPirce.item(0);
//                        Element priceElement2 = (Element) price.item(0);
//                        Element priceVatElement2 = (Element) priceVat.item(0);
//                        Element priceSumElement2= (Element) priceSum.item(0);
//
//                        BigDecimal cenaCelkem2 = new BigDecimal(cena_celkem2);
//                        BigDecimal castkaDph2 = new BigDecimal(casta_dph2);
//                        BigDecimal sum2 = cenaCelkem2.add(castkaDph2);
//
//                        unitPirceElement2.setTextContent(cena_cenik2);
//                        priceElement2.setTextContent(cena_cenik2);
//                        priceVatElement2.setTextContent(casta_dph2);
//                        priceSumElement2.setTextContent(sum2.toString());
//
//                        String cena_cenik3 = getElementText(thirdPolozka, "cena_cenik");
//                        String casta_dph3 = getElementText(thirdPolozka, "castka_dph");
//                        String cena_celkem3 = getElementText(thirdPolozka, "cena_celkem");
//
//                        // doprava
//                        Element unitPirceElement3 = (Element) unitPirce.item(1);
//                        Element priceElement3 = (Element) price.item(1);
//                        Element priceVatElement3 = (Element) priceVat.item(1);
//                        Element priceSumElement3 = (Element) priceSum.item(1);
//
//                        BigDecimal cenaCelkem3 = new BigDecimal(cena_celkem3);
//                        BigDecimal castkaDph3 = new BigDecimal(casta_dph3);
//                        BigDecimal sum3 = cenaCelkem3.add(castkaDph3);
//
//                        unitPirceElement3.setTextContent(cena_cenik3);
//                        priceElement3.setTextContent(cena_cenik3);
//                        priceVatElement3.setTextContent(casta_dph3);
//                        priceSumElement3.setTextContent(sum3.toString());

                        processInvoiceItem((Element) polozky.item(0), unitPirce, price, priceVat, priceSum, 2);
                        processInvoiceItem((Element) polozky.item(1), unitPirce, price, priceVat, priceSum, 0);
                        processInvoiceItem((Element) polozky.item(2), unitPirce, price, priceVat, priceSum, 1);
                    }

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        // Extract doklad information
                        String varSymbol = getElementText(element, "var_symbol");
                        String extCislo = getElementText(element, "ext_cislo");
                        String datVyst = getElementText(element, "dat_vyst");
                        String datZdPln = getElementText(element, "dat_zd_pln");
                        String datSpl = getElementText(element, "dat_spl");
                        String forma_uhrady = getElementText(element, "forma_uhrady");
                        String nazev = getElementText(element, "nazev");
                        String ulica = getElementText(element, "ulice");
                        String psc = getElementText(element, "psc");
                        String obec = getElementText(element, "obec");

                        // Format the date
                        String formattedDatVyst = formatDate(datVyst, "dd.MM.yyyy", "yyyy-MM-dd");
                        String formattedDatZdPln = formatDate(datZdPln, "dd.MM.yyyy", "yyyy-MM-dd");
                        String formattedDatSpl = formatDate(datSpl, "dd.MM.yyyy", "yyyy-MM-dd");

                        if (forma_uhrady.equals("PKO")) {
                            forma_uhrady = "Plat.kartou";
                            uhrada_eng = "creditcard";
                        } else {
                            forma_uhrady = "Dobierkou";
                            uhrada_eng = "delivery";
                        }

                        Element paymentTypeElement = (Element) paymentTypeList.item(0);

                        NodeList idsList = paymentTypeElement.getElementsByTagName("typ:ids");
                        NodeList numberRequested = templateDoc.getElementsByTagName("typ:numberRequested");
                        NodeList symVarList = templateDoc.getElementsByTagName("inv:symVar");
                        NodeList symParList = templateDoc.getElementsByTagName("inv:symPar");
                        NodeList date = templateDoc.getElementsByTagName("inv:date");
                        NodeList dateTax = templateDoc.getElementsByTagName("inv:dateTax");
                        NodeList dateDue = templateDoc.getElementsByTagName("inv:dateDue");
                        NodeList dateAccounting = templateDoc.getElementsByTagName("inv:dateAccounting");
                        NodeList name = templateDoc.getElementsByTagName("typ:name");
                        NodeList city = templateDoc.getElementsByTagName("typ:city");
                        NodeList street = templateDoc.getElementsByTagName("typ:street");
                        NodeList zip = templateDoc.getElementsByTagName("typ:zip");
                        NodeList numberOrder = templateDoc.getElementsByTagName("inv:numberOrder");
                        NodeList paymentMethod = templateDoc.getElementsByTagName("typ:paymentType");

                        Element numberRequestedElement = (Element) numberRequested.item(0);
                        Element idsListElement = (Element) idsList.item(0);
                        Element symVarElement = (Element) symVarList.item(0);
                        Element symParElement = (Element) symParList.item(0);
                        Element dateElement = (Element) date.item(0);
                        Element dateTaxElement = (Element) dateTax.item(0);
                        Element dateDueElement = (Element) dateDue.item(0);
                        Element dateAccountingElement = (Element) dateAccounting.item(0);
                        Element nameElement = (Element) name.item(0);
                        Element cityElement = (Element) city.item(0);
                        Element streetElement = (Element) street.item(0);
                        Element zipElement = (Element) zip.item(0);
                        Element numberOrderElement = (Element) numberOrder.item(0);
                        Element paymentMethodElement = (Element) paymentMethod.item(0);

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

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8))) {
                            saveXML(templateDoc, writer);
                        }

                        String uniqueFilename = "generated_file_" + extCislo + ".xml";
                        ZipEntry zipEntry = new ZipEntry(uniqueFilename);
                        zos.putNextEntry(zipEntry);
                        zos.write(byteArrayOutputStream.toByteArray());
                        zos.closeEntry();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processInvoiceItem(Element polozka, NodeList unitPriceList, NodeList priceList, NodeList priceVatList, NodeList priceSumList, int invoiceItemIndex) {
        String cenaCenik = getElementText(polozka, "cena_cenik");
        String castkaDph = getElementText(polozka, "castka_dph");
        String cenaCelkem = getElementText(polozka, "cena_celkem");

        BigDecimal cenaCelkemDecimal = new BigDecimal(cenaCelkem);
        BigDecimal castkaDphDecimal = new BigDecimal(castkaDph);
        BigDecimal sum = cenaCelkemDecimal.add(castkaDphDecimal);

        Element unitPriceElement = (Element) unitPriceList.item(invoiceItemIndex);
        Element priceElement = (Element) priceList.item(invoiceItemIndex);
        Element priceVatElement = (Element) priceVatList.item(invoiceItemIndex);
        Element priceSumElement = (Element) priceSumList.item(invoiceItemIndex);

        unitPriceElement.setTextContent(cenaCenik);
        priceElement.setTextContent(cenaCenik);
        priceVatElement.setTextContent(castkaDph);
        priceSumElement.setTextContent(sum.toString());
    }

    private static void saveXML(Document doc, Writer writer) throws Exception {
        javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(doc);
        javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(writer);
        transformer.transform(source, result);
    }

    private static String getElementText(Element element, String tagName) {
        NodeList list = element.getElementsByTagName(tagName);
        return list.getLength() > 0 ? list.item(0).getTextContent() : null;
    }

    private static String formatDate(String dateStr, String originalFormat, String targetFormat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(originalFormat);
            Date date = sdf.parse(dateStr);
            sdf.applyPattern(targetFormat);
            return sdf.format(date);
        } catch (Exception e) {
            System.out.println("Chyba pri formátovaní dátumu: " + e.getMessage());
            return null;
        }
    }
}
