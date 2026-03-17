package com.invoicesync.modules.invoice.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.invoicesync.core.enums.InvoiceStatus;
import com.invoicesync.core.identity.MyIdentity;
import com.invoicesync.core.identity.PartnerDto;
import com.invoicesync.modules.company.model.Company;
import com.invoicesync.modules.document.model.DocumentTableResponse;
import com.invoicesync.modules.document.model.InvoiceDocument;
import com.invoicesync.modules.invoice.model.Invoice;
import com.invoicesync.modules.invoice.model.InvoiceCreate;
import com.invoicesync.modules.invoice.model.InvoiceDetails;
import com.invoicesync.modules.invoice.model.InvoiceItem;
import com.invoicesync.modules.invoice.model.InvoiceRequest;
import com.invoicesync.modules.invoice.model.InvoiceResponse;
import com.invoicesync.modules.invoice.model.InvoiceResponseTable;
import com.invoicesync.modules.receipt.model.ReceiptItemDto;

@Service
public class InvoiceMapper {

  public Invoice toInvoice(final InvoiceRequest request) {

    final Invoice invoice = Invoice.builder()
        .id(request.id())
        .invoiceNumber(request.numberRequested())
        .variableSymbol(request.variableSymbol())
        .issueDate(request.issueDate())
        .taxDate(request.taxDate())
        .accountingDate(request.accountingDate())
        .dueDate(request.dueDate())
        .partnerRegistrationNumber(request.partnerRegistrationNumber())
        .status(InvoiceStatus.UNPROCESSED)
        .invoiceType("NONE")
        .build();

    final List<InvoiceItem> items = request.items().stream()
        .map(itemRequest -> {
          final InvoiceItem item = new InvoiceItem();
          item.setName(itemRequest.name());
          item.setInvoice(invoice);
          return item;
        })
        .toList();

    invoice.setItems(items);

    return invoice;
  }

  public Invoice toCreateInvoice(final InvoiceCreate request, final Company company) {

    final Invoice invoice = Invoice.builder()
        .invoiceNumber(request.invoiceDetails().numberRequested())
        .variableSymbol(request.invoiceDetails().variableSymbol())
        .issueDate(request.invoiceDetails().issueDate())
        .taxDate(request.invoiceDetails().taxDate())
        .accountingDate(request.invoiceDetails().accountingDate())
        .dueDate(request.invoiceDetails().dueDate())
        .partnerName(request.partner().getName())
        .partnerStreet(request.partner().getStreet())
        .partnerCity(request.partner().getCity())
        .partnerZip(request.partner().getZip())
        .partnerRegistrationNumber(request.partner().getRegistrationNumber())
        .partnerTaxId(request.partner().getTaxId())
        .partnerVatId(request.partner().getVatId())
        .status(InvoiceStatus.UNPROCESSED)
        .invoiceType(request.invoiceDetails().invoiceType().toString())
        .company(company)
        .build();

    final List<InvoiceItem> items = request.items().stream()
        .map(itemRequest -> {
          final BigDecimal unitPriceWithVat = itemRequest.getUnitPriceWithVat() != null
              ? itemRequest.getUnitPriceWithVat()
              : BigDecimal.ZERO;
          final BigDecimal unitPriceWithoutVat = unitPriceWithVat
              .divide(BigDecimal.valueOf(1 + itemRequest.getVatRate() / 100.0), 2, RoundingMode.HALF_UP);
          final BigDecimal qty = BigDecimal.valueOf(itemRequest.getQuantity());

          System.out.println(unitPriceWithVat);

          final InvoiceItem item = new InvoiceItem();
          item.setInvoice(invoice);
          item.setName(itemRequest.getName());
          item.setQuantity(itemRequest.getQuantity());
          item.setUnitType(itemRequest.getUnitType());
          item.setVatRate(itemRequest.getVatRate());
          item.setUnitPriceWithVat(unitPriceWithVat);
          item.setUnitPriceWithoutVat(unitPriceWithoutVat);
          item.setTotalItemPriceWithVat(unitPriceWithVat.multiply(qty));
          item.setTotalItemPriceWithoutVat(unitPriceWithoutVat.multiply(qty));
          return item;
        })
        .toList();

    invoice.setItems(items);

    return invoice;
  }

  public InvoiceResponseTable toInvoiceTableResponse(final Invoice invoice) {
    return InvoiceResponseTable.builder()
        .id(invoice.getId())
        .createAt(invoice.getCreatedDate())
        .invoiceDetails(InvoiceDetails.builder()
            .invoiceNumber(invoice.getInvoiceNumber())
            .variableSymbol(invoice.getVariableSymbol())
            .issueDate(invoice.getIssueDate())
            .dueDate(invoice.getDueDate())
            .status(invoice.getStatus().getLabel())
            .invoiceType(invoice.getInvoiceType())
            .invoiceType(invoice.getInvoiceType())
            .build())
        .partner(PartnerDto.builder()
            .name(invoice.getPartnerName())
            .build())
        .build();
  }

  public InvoiceResponse toInvoiceResponse(final Invoice invoice) {
    final List<ReceiptItemDto> items = invoice.getItems().stream()
        .map(item -> ReceiptItemDto.builder()
            .id(item.getId())
            .accountText(item.getAccountText())
            .name(item.getName())
            .quantity(item.getQuantity())
            .unitType(item.getUnitType())
            .unitPriceWithoutVat(item.getUnitPriceWithoutVat())
            .unitPriceWithVat(item.getUnitPriceWithVat())
            .vatRate(item.getVatRate())
            .totalItemPriceWithoutVat(item.getTotalItemPriceWithoutVat())
            .totalItemPriceWithVat(item.getTotalItemPriceWithVat())
            .accountValue(item.getAccountValue())
            .build())
        .collect(Collectors.toList());

    return InvoiceResponse.builder()
        .id(invoice.getId())
        .createdAt(invoice.getCreatedDate())
        .invoiceDetails(InvoiceDetails.builder()
            .invoiceNumber(invoice.getInvoiceNumber())
            .variableSymbol(invoice.getVariableSymbol())
            .issueDate(invoice.getIssueDate())
            .taxDate(invoice.getTaxDate())
            .accountingDate(invoice.getAccountingDate())
            .dueDate(invoice.getDueDate())
            .status(String.valueOf(invoice.getStatus()))
            .invoiceType(invoice.getInvoiceType())
            .build())
        .partner(PartnerDto.builder()
            .name(invoice.getPartnerName())
            .city(invoice.getPartnerCity())
            .street(invoice.getPartnerStreet())
            .zip(invoice.getPartnerZip())
            .registrationNumber(invoice.getPartnerRegistrationNumber())
            .taxId(invoice.getPartnerTaxId())
            .vatId(invoice.getPartnerVatId())
            .build())
        .items(items)
        .company(MyIdentity.builder()
            .id(invoice.getCompany().getId())
            .name(invoice.getCompany().getName())
            .city(invoice.getCompany().getCity())
            .street(invoice.getCompany().getStreet())
            .streetNumber(invoice.getCompany().getStreetNumber())
            .zip(invoice.getCompany().getZip())
            .registrationNumber(invoice.getCompany().getRegistrationNumber())
            .taxId(invoice.getCompany().getTaxId())
            .vatId(invoice.getCompany().getVatId())
            .build())
        .build();
  }

  public DocumentTableResponse toInvoiceDocumentsTableResponse(final InvoiceDocument invoiceDocument) {
    return DocumentTableResponse.builder()
        .id(invoiceDocument.getId())
        .documentName(invoiceDocument.getDocumentName())
        .fileName(invoiceDocument.getFilename())
        .createdAt(invoiceDocument.getCreatedDate())
        .note(invoiceDocument.getNote())
        .build();
  }

}
