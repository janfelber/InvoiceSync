package com.invoicesync.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.invoicesync.dto.invoice.pohoda.InvoiceRequestDTO;
import com.invoicesync.dto.record.ReceiptRequest;
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
  public byte[] generatePohodaReceiptExcel(final ReceiptRequest request) throws Exception {

    System.out.printf("typ platby%s%n", request.isPaidByCard());

    System.out.println("Všetky položky:");
    request.items().forEach(System.out::println);

    if (request.isPaidByCard()) {
      throw new IllegalArgumentException("Platba kartou – Excel nie je podporovaný.");
    }

    try (InputStream fis = getClass().getClassLoader().getResourceAsStream("template/receipt-expense-template.xlsx");
         Workbook workbook = new XSSFWorkbook(fis);
         ByteArrayOutputStream out = new ByteArrayOutputStream()) {

      // // Filtrovanie a printovanie kladných položiek
      // final List<ReceiptItemRequest> positiveItems = request.items().stream()
      //     .filter(item -> item.priceWithoutVat() != null && item.priceWithoutVat().compareTo(BigDecimal.ZERO) > 0)
      //     .toList();
      //
      // final String positivePrices = request.items().stream()
      //     .map(ReceiptItemRequest::priceWithoutVat)
      //     .filter(price -> price != null && price.compareTo(BigDecimal.ZERO) > 0)
      //     .map(BigDecimal::toPlainString)
      //     .collect(Collectors.joining(" + "));

      final Sheet sheet = workbook.getSheetAt(0);
      final Row row = sheet.createRow(sheet.getLastRowNum() + 1);

      row.createCell(0).setCellValue("TRUE");
      row.createCell(1).setCellValue(request.receiptNumber());
      row.createCell(3).setCellValue(request.date());
      row.createCell(5).setCellValue(request.accounting());
      row.createCell(6).setCellValue(request.description());
      // row.createCell(8).setCellValue(positivePrices);
      row.createCell(9).setCellValue(request.partnerName());

      row.createCell(11).setCellValue("$" + request.totalPrice());
      System.out.println(request.totalPrice());
      row.createCell(13).setCellValue("Výdaj");
      row.createCell(15).setCellValue("HP");

      row.createCell(17).setCellValue(request.classificationVAT());
      row.createCell(21).setCellValue(request.classificationKVVAT());

      workbook.write(out);
      return out.toByteArray();
    }
  }

}
