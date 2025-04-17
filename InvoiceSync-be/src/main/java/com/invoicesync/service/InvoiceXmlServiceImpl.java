package com.invoicesync.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
import com.invoicesync.dto.receipt.pohoda.ReceiptRequestDTO;
import com.invoicesync.dto.receipt.pohoda.ReceiptRequestDetailsDTO;
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
    final InputStream fis = getClass().getClassLoader().getResourceAsStream("template/receipt_template.xlsx");
    if (fis == null) {
      throw new FileNotFoundException("Šablóna receipt_template.xlsx sa nenašla v resources/template/");
    }

    final Workbook workbook = new XSSFWorkbook(fis);
    final Sheet sheet = workbook.getSheetAt(0);

    final int newRowIdx = sheet.getLastRowNum() + 1;
    final Row row = sheet.createRow(newRowIdx);

    final ReceiptRequestDetailsDTO d = receiptRequestDTO.getReceiptDetails();

    row.createCell(0).setCellValue(d.getNumberRequested());
    row.createCell(1).setCellValue(d.getDate());
    row.createCell(2).setCellValue(d.getDate());
    row.createCell(3).setCellValue(d.getDateTax());
    row.createCell(4).setCellValue(d.getAccountValue());
    row.createCell(5).setCellValue(d.getClassificationVAT());
    row.createCell(6).setCellValue(d.getClassificationKVVAT());
    row.createCell(7).setCellValue(d.getDescription());

    fis.close();

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    workbook.write(out);
    workbook.close();
    return out.toByteArray();
  }

}
