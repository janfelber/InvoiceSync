package com.invoicesync.modules.invoice.service;

import static com.invoicesync.core.enums.InvoiceType.RECEIVED;

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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.invoicesync.core.enums.LimitType;
import com.invoicesync.modules.company.service.CompanyService;
import com.invoicesync.modules.invoice.model.InvoiceRequestDTO;
import com.invoicesync.modules.receipt.model.ReceiptRequest;
import com.invoicesync.modules.receipt.model.ReceiptRequestDTO;
import com.invoicesync.modules.subscription.guard.service.LimitGuardService;
import com.invoicesync.modules.subscription.service.SubscriptionService;
import com.invoicesync.modules.user.service.UserService;
import com.invoicesync.modules.xml.utils.XmlHelper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class InvoiceXmlServiceImpl implements PohodaXmlService {

  private final XmlHelper xmlHelper;

  private final SubscriptionService subscriptionService;

  private final LimitGuardService limitGuardService;

  private final UserService userService;

  private final CompanyService companyService;

  @Override
  public byte[] generatePohodaInvoiceXml(final InvoiceRequestDTO request) throws Exception {
    final File xmlFile = new File("src/main/resources/template/PriFaktury.xml");
    final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    final Document doc = dBuilder.parse(xmlFile);

    dbFactory.setNamespaceAware(true);
    doc.getDocumentElement().normalize();

    if (request.invoiceType() == RECEIVED) {
      xmlHelper.updateInvoiceDetails(doc, request.invoiceDetails());
      xmlHelper.updateInvoiceMyIdentity(doc, request.myIdentity());
      xmlHelper.updatePartner(doc, request.partner());
      xmlHelper.updateInvoiceItems(doc, request.items());
    }

    removeWhitespaceNodes(doc);

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(new DOMSource(doc), new StreamResult(outputStream));

    return outputStream.toByteArray();
  }

  @SneakyThrows
  @Override
  public byte[] generateReceiptXml(final ReceiptRequestDTO request, final Authentication connectedUser)
      throws RuntimeException {

    limitGuardService.checkLimit(connectedUser, LimitType.RECEIPT_EXPORT);

    final String templatePath = resolveTemplatePath(request);

    final File xmlFile = new File(templatePath);
    final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    dbFactory.setNamespaceAware(true);
    final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    final Document doc = dBuilder.parse(xmlFile);
    doc.getDocumentElement().normalize();

    final String numberRequested = companyService.getReceiptNumber(request.myIdentity().getId(), request.isPaidByCard(),
        connectedUser);
    request.receiptDetails().setNumberRequested(numberRequested);


    if (!request.isPaidByCard()) {
      xmlHelper.updateCashReceiptDetails(doc, request.receiptDetails());
      xmlHelper.updateCashReceiptMyIdentity(doc, request.myIdentity());
      xmlHelper.updateCashReceiptPartner(doc, request.partner());
      xmlHelper.updateCashReceiptItems(doc, request.items());
    } else {
      xmlHelper.updateCardReceiptDetails(doc, request.receiptDetails());
      xmlHelper.updateCardReceiptMyIdentity(doc, request.myIdentity());
      xmlHelper.updateCardReceiptPartner(doc, request.partner());
      xmlHelper.updateCardReceiptItems(doc, request.items());
    }

    removeWhitespaceNodes(doc);

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.transform(new DOMSource(doc), new StreamResult(outputStream));

    userService.incrementUsed(connectedUser, LimitType.RECEIPT_EXPORT);

    return outputStream.toByteArray();
  }

  private void removeWhitespaceNodes(final Node node) {
    final NodeList children = node.getChildNodes();
    for (int i = children.getLength() - 1; i >= 0; i--) {
      final Node child = children.item(i);
      if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().trim().isEmpty()) {
        node.removeChild(child);
      } else if (child.hasChildNodes()) {
        removeWhitespaceNodes(child);
      }
    }
  }

  private String resolveTemplatePath(final ReceiptRequestDTO receipt) {
    if (receipt.isPaidByCard()) {
      return "src/main/resources/template/receipt_card.xml";
    } else {
      return "src/main/resources/template/receipt_cash.xml";
    }
  }

  @Override
  public byte[] generatePohodaReceiptExcel(final ReceiptRequest request, final Authentication connectedUser)
      throws Exception {

    limitGuardService.checkLimit(connectedUser, LimitType.RECEIPT_EXPORT);

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

      userService.incrementUsed(connectedUser, LimitType.RECEIPT_EXPORT);
      return out.toByteArray();
    }
  }

}
