package com.invoicesync.modules.invoice.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.invoicesync.core.identity.MyIdentity;
import com.invoicesync.core.identity.PartnerDto;
import com.invoicesync.modules.document.model.InvoiceDocument;
import com.invoicesync.modules.document.model.InvoiceDocumentsTableResponse;
import com.invoicesync.core.enums.InvoiceStatus;
import com.invoicesync.modules.invoice.model.InvoiceDetails;
import com.invoicesync.modules.invoice.model.InvoiceRequest;
import com.invoicesync.modules.invoice.model.InvoiceResponse;
import com.invoicesync.modules.invoice.model.InvoiceResponseTable;
import com.invoicesync.modules.invoice.model.Invoice;
import com.invoicesync.modules.invoice.model.InvoiceItem;
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

  public InvoiceResponseTable toInvoiceTableResponse(final Invoice invoice) {
    return InvoiceResponseTable.builder()
        .id(invoice.getId())
        .createAt(invoice.getCreatedDate())
        .invoiceDetails(InvoiceDetails.builder()
            .invoiceNumber(invoice.getInvoiceNumber())
            .variableSymbol(invoice.getVariableSymbol( ))
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
            .priceWithoutVAT(item.getPriceWithoutVAT())
            .vatRate(item.getVatRate())
            .priceWithVAT(item.getPriceWithVAT())
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

  public InvoiceDocumentsTableResponse toInvoiceDocumentsTableResponse(final InvoiceDocument invoiceDocument) {
    return InvoiceDocumentsTableResponse.builder()
        .id(invoiceDocument.getId())
        .documentName(invoiceDocument.getDocumentName())
        .fileName(invoiceDocument.getFilename())
        .createdAt(invoiceDocument.getCreatedDate())
        .note(invoiceDocument.getNote())
        .build();
  }

}
