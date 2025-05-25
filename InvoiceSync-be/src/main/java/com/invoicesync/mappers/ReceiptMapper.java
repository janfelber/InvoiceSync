package com.invoicesync.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.invoicesync.dto.identity.MyIdentityDTO;
import com.invoicesync.dto.identity.PartnerDto;
import com.invoicesync.dto.receipt.pohoda.ReceiptItemDto;
import com.invoicesync.dto.receipt.reponse.DetailDto;
import com.invoicesync.dto.receipt.reponse.ReceiptDetailDto;
import com.invoicesync.dto.receipt.reponse.ReceiptResponseDto;
import com.invoicesync.dto.record.ReceiptRequest;
import com.invoicesync.module.Company;
import com.invoicesync.module.Receipt;
import com.invoicesync.module.ReceiptItem;

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
        .datePayment(request.datePayment())
        .dateTax(request.dateTax())
        .partnerName(request.partnerName())
        .partnerCity(request.partnerCity())
        .partnerStreet(request.partnerStreet())
        .partnerZip(request.partnerZip())
        .partnerRegistrationNumber(request.partnerRegistrationNumber())
        .partnerTaxId(request.partnerTaxId())
        .partnerVatId(request.partnerVatId())
        .totalPrice(request.totalPrice())
        .accounting(request.accounting())
        .build();

    final List<ReceiptItem> items = request.items().stream()
        .map(itemRequest -> {
          ReceiptItem item = new ReceiptItem();
          item.setName(itemRequest.name());
          item.setQuantity(itemRequest.quantity());
          item.setPriceWithoutVAT(itemRequest.priceWithoutVat());
          item.setVatRate(itemRequest.vatRate());
          item.setAccountValue(itemRequest.accountValue());
          item.setPriceWithVAT(itemRequest.priceWithVat());
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
            .accountText(item.getAccountText())
            .name(item.getName())
            .quantity(item.getQuantity())
            .priceWithoutVAT(item.getPriceWithoutVAT())
            .vatRate(item.getVatRate())
            .priceWithVAT(item.getPriceWithVAT())
            .accountValue(item.getAccountValue())
            .build())
        .collect(Collectors.toList());

    return ReceiptDetailDto.builder()
        .id(receipt.getId())
        .createdAt(receipt.getCreatedDate())
        .receiptDetails(DetailDto.builder()
            .date(receipt.getDate())
            .datePayment(receipt.getDatePayment())
            .dateTax(receipt.getDateTax())
            .totalPrice(receipt.getTotalPrice())
            .accountValue(receipt.getAccounting())
            .classificationVAT(receipt.getClassificationVAT())
            .classificationKVVAT(receipt.getClassificationKVVAT())
            .description(receipt.getDescription())
            .build())
        .partner(PartnerDto.builder()
            .name(receipt.getPartnerName())
            .city(receipt.getPartnerCity())
            .street(receipt.getPartnerStreet())
            .zip(receipt.getPartnerZip())
            .registrationNumber(receipt.getPartnerRegistrationNumber())
            .taxId(receipt.getPartnerTaxId())
            .vatId(receipt.getPartnerVatId())
            .build())
        .items(itemDtos)
        .company(MyIdentityDTO.builder()
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
        .createdAt(receipt.getCreatedDate())
        .companyName(receipt.getCompany().getName())
        .receiptDetails(DetailDto.builder()
            .totalPrice(receipt.getTotalPrice())
            .build())
        .partner(PartnerDto.builder()
            .name(receipt.getPartnerName())
            .city(receipt.getPartnerCity())
            .street(receipt.getPartnerStreet())
            .zip(receipt.getPartnerZip())
            .registrationNumber(receipt.getPartnerRegistrationNumber())
            .taxId(receipt.getPartnerTaxId())
            .vatId(receipt.getPartnerVatId())
            .build())
        .build();
  }
}
