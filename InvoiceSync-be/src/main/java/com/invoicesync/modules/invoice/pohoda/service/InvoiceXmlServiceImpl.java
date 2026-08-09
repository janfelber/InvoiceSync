package com.invoicesync.modules.invoice.pohoda.service;

import static com.invoicesync.core.enums.InvoiceType.RECEIVED;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

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

import com.invoicesync.core.enums.LimitType;
import com.invoicesync.export.TemplateLoader;
import com.invoicesync.export.XmlSerializer;
import com.invoicesync.integration.pohoda.generated.invoice.InvoiceType;
import com.invoicesync.modules.activity.model.UserActivityType;
import com.invoicesync.modules.activity.service.UserActivityRecord;
import com.invoicesync.modules.activity.service.UserActivityService;
import com.invoicesync.modules.auth.security.OwnedInvoice;
import com.invoicesync.modules.auth.security.RequiresOwnership;
import com.invoicesync.modules.company.service.CompanyService;
import com.invoicesync.modules.invoice.model.Invoice;
import com.invoicesync.modules.invoice.model.InvoiceRequestDTO;
import com.invoicesync.modules.invoice.pohoda.PohodaInvoiceMapper;
import com.invoicesync.modules.invoice.pohoda.PohodaXmlBuilder;
import com.invoicesync.modules.invoice.repository.InvoiceRepository;
import com.invoicesync.modules.receipt.domain.service.ReceiptPopulator;
import com.invoicesync.modules.receipt.model.Receipt;
import com.invoicesync.modules.receipt.model.ReceiptRequest;
import com.invoicesync.modules.receipt.repository.ReceiptRepository;
import com.invoicesync.modules.subscription.guard.service.LimitGuardService;
import com.invoicesync.modules.user.service.UserService;
import com.invoicesync.modules.xml.utils.XmlHelper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class InvoiceXmlServiceImpl implements PohodaXmlService {

  private final XmlHelper xmlHelper;

  private final LimitGuardService limitGuardService;

  private final UserService userService;

  private final UserActivityService userActivityService;

  private final ReceiptRepository receiptRepository;

  private final TemplateLoader templateLoader;

  private final List<ReceiptPopulator> populators;

  private final XmlSerializer xmlSerializer;

  private final CompanyService companyService;

  private final InvoiceRepository invoiceRepository;

  private final PohodaInvoiceMapper pohodaInvoiceMapper;

  private final PohodaXmlBuilder pohodaXmlBuilder;

  //TODO refactor to use cleaner approach with populators and template loader, also for invoices
  @Override
  public byte[] generatePohodaInvoiceXml(final InvoiceRequestDTO request) throws Exception {
    final File xmlFile = new File("src/main/resources/template/PriFaktury.xml");
    final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    final Document doc = dBuilder.parse(xmlFile);

    dbFactory.setNamespaceAware(true);
    doc.getDocumentElement().normalize();

    //TODO create populator for invoices
    if (request.invoiceType() == RECEIVED) {
      xmlHelper.updateInvoiceDetails(doc, request.invoiceDetails());
      xmlHelper.updateInvoiceMyIdentity(doc, request.myIdentity());
      xmlHelper.updatePartner(doc, request.partner());
      xmlHelper.updateInvoiceItems(doc, request.items());
    }

    xmlSerializer.removeWhitespaceNodes(doc);

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(new DOMSource(doc), new StreamResult(outputStream));

    return outputStream.toByteArray();
  }

  @SneakyThrows
  @Override
  public byte[] generateReceiptXml(final Long receiptId, final Authentication connectedUser)
      throws RuntimeException {
    limitGuardService.checkLimit(connectedUser, LimitType.RECEIPT_EXPORT);

    //TODO validate with custom validator
    final Receipt receipt = receiptRepository.findByIdAndCompany_CreatedBy(receiptId, connectedUser.getName())
        .orElseThrow(() -> new IllegalArgumentException("Receipt not found or access denied"));

    final String number =
        companyService.allocateReceiptNumber(receipt.getCompany(), receipt.getPaymentType(), connectedUser);

    final Document document = templateLoader.load(receipt.getPaymentType());
    resolvePopulator(receipt).populate(document, receipt, number);

    userService.incrementUsed(connectedUser, LimitType.RECEIPT_EXPORT);
    userActivityService.save(UserActivityRecord.forType(connectedUser.getName(), UserActivityType.EXPORT_RECEIPT,
        "User generated receipt for company " + receipt.getCompany().getId()));

    return xmlSerializer.serialize(document);
  }

  //TODO clean up
  @Override
  public byte[] generatePohodaReceiptExcel(final ReceiptRequest request, final Authentication connectedUser)
      throws Exception {

    limitGuardService.checkLimit(connectedUser, LimitType.RECEIPT_EXPORT);

    // System.out.printf("typ platby%s%n", request.isPaidByCard());

    System.out.println("Všetky položky:");
    request.items().forEach(System.out::println);

    // if (request.isPaidByCard()) {
    //   throw new IllegalArgumentException("Platba kartou – Excel nie je podporovaný.");
    // }

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

      row.createCell(11).setCellValue("$" + request.totalPriceWithVat());
      row.createCell(13).setCellValue("Výdaj");
      row.createCell(15).setCellValue("HP");

      row.createCell(17).setCellValue(request.classificationVAT());
      row.createCell(21).setCellValue(request.classificationKVVAT());

      workbook.write(out);

      userService.incrementUsed(connectedUser, LimitType.RECEIPT_EXPORT);
      return out.toByteArray();
    }
  }

  @Override
  @RequiresOwnership
  public byte[] generateInvoiceXml(final @OwnedInvoice Long invoiceId, final Authentication connectedUser)
      throws Exception {

    final Invoice invoice =
        invoiceRepository.findById(invoiceId).orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
    final InvoiceType invoiceToGenerate = pohodaInvoiceMapper.toInvoiceType(invoice);

    return pohodaXmlBuilder.buildInvoiceXml(invoiceToGenerate, invoice.getCompany().getRegistrationNumber(),
        invoice.getInvoiceNumber());
  }

  private ReceiptPopulator resolvePopulator(final Receipt receipt) {
    return populators.stream()
        .filter(p -> p.supports() == receipt.getPaymentType())
        .findFirst()
        .orElseThrow();
  }

}
