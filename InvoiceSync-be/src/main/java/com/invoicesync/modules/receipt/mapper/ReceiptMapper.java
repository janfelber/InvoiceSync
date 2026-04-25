package com.invoicesync.modules.receipt.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.invoicesync.core.identity.MyIdentity;
import com.invoicesync.core.identity.PartnerDto;
import com.invoicesync.modules.company.model.Company;
import com.invoicesync.modules.document.model.DocumentTableResponse;
import com.invoicesync.modules.document.model.ReceiptDocument;
import com.invoicesync.modules.receipt.model.DetailDto;
import com.invoicesync.modules.receipt.model.Receipt;
import com.invoicesync.modules.receipt.model.ReceiptDetailDto;
import com.invoicesync.modules.receipt.model.ReceiptItem;
import com.invoicesync.modules.receipt.model.ReceiptItemDto;
import com.invoicesync.modules.receipt.model.ReceiptRequest;
import com.invoicesync.modules.receipt.model.ReceiptResponseDto;

@Service
public class ReceiptMapper {

  public Receipt toReceipt(final ReceiptRequest request) {
    final Company company = new Company();
    company.setId(request.companyId());

    System.out.println(company);

    final Receipt receipt = Receipt.builder()
        .id(request.id())
        .company(company)
        .date(request.date())
        .paymentDate(request.datePayment())
        .taxDate(request.dateTax())
        .supplierName(request.partnerName())
        .supplierCity(request.partnerCity())
        .supplierStreet(request.partnerStreet())
        .supplierZip(request.partnerZip())
        .supplierRegistrationNumber(request.partnerRegistrationNumber())
        .supplierTaxId(request.partnerTaxId())
        .supplierVatId(request.partnerVatId())
        .totalPriceWithVat(request.totalPriceWithVat())
        .totalPriceWithoutVat(request.totalPriceWithoutVat())
        .accountValue(request.accounting())
        .build();

    final List<ReceiptItem> items = request.items().stream()
        .map(itemRequest -> {
          ReceiptItem item = new ReceiptItem();
          item.setName(itemRequest.name());
          item.setQuantity(itemRequest.quantity());
          item.setVatRate(itemRequest.vatRate());
          item.setUnitPriceWithoutVat(itemRequest.unitPriceWithoutVat());
          item.setUnitPriceWithVat(itemRequest.unitPriceWithVat());
          item.setTotalItemPriceWithoutVat(itemRequest.totalItemPriceWithoutVat());
          item.setTotalItemPriceWithVat(itemRequest.totalItemPriceWithVat());
          item.setAccountValue(itemRequest.accountValue());
          item.setAccountText(itemRequest.accountText());
          item.setReceipt(receipt);
          return item;
        })
        .toList();

    receipt.setItems(items);

    return receipt;
  }

  public ReceiptDetailDto toReceiptResponse(final Receipt receipt) {
    final List<ReceiptItemDto> itemDtos = receipt.getItems().stream()
        .map(item -> ReceiptItemDto.builder()
            .id(item.getId())
            .name(item.getName())
            .quantity(item.getQuantity())
            .vatRate(item.getVatRate())
            .unitPriceWithoutVat(item.getUnitPriceWithoutVat())
            .unitPriceWithVat(item.getUnitPriceWithVat())
            .totalItemPriceWithVat(item.getTotalItemPriceWithVat())
            .accountValue(item.getAccountValue())
            .accountText(item.getAccountText())
            .build())
        .collect(Collectors.toList());

    return ReceiptDetailDto.builder()
        .id(receipt.getId())
        .orderNumber(receipt.getReceiptOrder())
        .createdAt(receipt.getCreatedDate())
        .receiptDetails(DetailDto.builder()
            .date(receipt.getDate())
            .datePayment(receipt.getPaymentDate())
            .dateTax(receipt.getTaxDate())
            .totalPriceWithVat(String.valueOf(receipt.getTotalPriceWithVat()))
            .totalPriceWithoutVat(String.valueOf(receipt.getTotalPriceWithoutVat()))
            .accountValue(receipt.getAccountValue())
            .paymentType(receipt.getPaymentType())
            .classificationVAT(receipt.getVatClassification())
            .classificationKVVAT(receipt.getKvVatClassification())
            .description(receipt.getDescription())
            .build())
        .partner(PartnerDto.builder()
            .name(receipt.getSupplierName())
            .city(receipt.getSupplierCity())
            .street(receipt.getSupplierStreet())
            .zip(receipt.getSupplierZip())
            .registrationNumber(receipt.getSupplierRegistrationNumber())
            .taxId(receipt.getSupplierTaxId())
            .vatId(receipt.getSupplierVatId())
            .build())
        .items(itemDtos)
        .company(MyIdentity.builder()
            .id(receipt.getCompany().getId())
            .name(receipt.getCompany().getName())
            .city(receipt.getCompany().getCity())
            .street(receipt.getCompany().getStreet())
            .streetNumber(receipt.getCompany().getStreetNumber())
            .zip(receipt.getCompany().getZip())
            .registrationNumber(receipt.getCompany().getRegistrationNumber())
            .taxId(receipt.getCompany().getTaxId())
            .vatId(receipt.getCompany().getVatId())
            .build())
        .build();
  }

  public ReceiptResponseDto toReceiptTableResponse(final Receipt receipt) {
    return ReceiptResponseDto.builder()
        .id(receipt.getId())
        .orderNumber(receipt.getReceiptOrder())
        .createdAt(receipt.getCreatedDate())
        .companyName(receipt.getCompany().getName())
        .receiptDetails(DetailDto.builder()
            .totalPriceWithVat(String.valueOf(receipt.getTotalPriceWithVat()))
            .build())
        .partner(PartnerDto.builder()
            .name(receipt.getSupplierName())
            .city(receipt.getSupplierCity())
            .street(receipt.getSupplierStreet())
            .zip(receipt.getSupplierZip())
            .registrationNumber(receipt.getSupplierRegistrationNumber())
            .taxId(receipt.getSupplierTaxId())
            .vatId(receipt.getSupplierVatId())
            .build())
        .build();
  }

  public DocumentTableResponse receiptDocumentTableResponse(final ReceiptDocument document) {
    return DocumentTableResponse.builder()
        .id(document.getId())
        .documentName(document.getDocumentName())
        .fileName(document.getFilename())
        .createdAt(document.getCreatedDate())
        .note(document.getNote())
        .build();
  }

}
