package com.invoicesync.modules.invoice.extraction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.invoicesync.core.enums.InvoiceType;
import com.invoicesync.core.identity.PartnerDto;
import com.invoicesync.modules.company.model.Company;
import com.invoicesync.modules.invoice.extraction.model.dto.ExtractInvoiceResponse;
import com.invoicesync.modules.invoice.extraction.model.dto.ExtractedItemDto;
import com.invoicesync.modules.invoice.model.InvoiceCreate;
import com.invoicesync.modules.invoice.model.InvoiceRequestDetailsDTO;
import com.invoicesync.partner.CompaniesRegistry;
import com.invoicesync.shared.AccountingLineItem;
import com.invoicesync.shared.MonetaryAmount;

@Service
public class ExtractionMapper {

  private final CompaniesRegistry companyRepository;

  public ExtractionMapper(CompaniesRegistry companyRepository) {
    this.companyRepository = companyRepository;
  }

  public InvoiceCreate toInvoiceCreate(ExtractInvoiceResponse extractResponse, Company company) {

    final LocalDate issueDate = extractResponse.invoiceData().issueDate();
    final LocalDate dueDate = extractResponse.invoiceData().dueDate();

    InvoiceRequestDetailsDTO invoiceRequestDetailsDTO = InvoiceRequestDetailsDTO.builder()
        .numberRequested(extractResponse.invoiceData().invoiceNumber())
        .variableSymbol(extractResponse.invoiceData().invoiceNumber())
        .issueDate(issueDate == null ? null : issueDate.toString())
        .dueDate(dueDate == null ? null : dueDate.toString())
        .taxDate(issueDate == null ? null : issueDate.toString())
        .accountingDate(issueDate == null ? null : issueDate.toString())
        .invoiceType(InvoiceType.RECEIVED)
        .priceWithoutVAT(extractResponse.invoiceData().priceWithoutVAT())
        .priceWithVAT(extractResponse.invoiceData().priceWithVAT())
        .build();

    PartnerDto partnerDto = new PartnerDto();

    companyRepository.findByIco(extractResponse.invoiceData().supplierRegistrationNumber())
        .ifPresentOrElse(
            foundCompany -> {
              partnerDto.setRegistrationNumber(foundCompany.getRegistrationNumber());
              partnerDto.setTaxId(foundCompany.getTaxId());
              partnerDto.setVatId(foundCompany.getVatId());
              partnerDto.setCity(foundCompany.getCity());
              partnerDto.setStreet(foundCompany.getStreet());
              partnerDto.setZip(foundCompany.getZip());
              partnerDto.setName(foundCompany.getName());
            },
            () -> {
              throw new IllegalArgumentException(
                  "Firma s IČO " + extractResponse.invoiceData().supplierRegistrationNumber()
                      + " nebola nájdená v registri.");
            }
        );

    final List<AccountingLineItem> items = extractResponse.invoiceData().items() == null
        ? null
        : extractResponse.invoiceData().items().stream()
            .map(this::toAccountingLineItem)
            .toList();

    return InvoiceCreate.builder()
        .companyId(company.getId())
        .invoiceDetails(invoiceRequestDetailsDTO)
        .partner(partnerDto)
        .items(items)
        .build();
  }

  // TODO: this VAT math (and the mirrored division in InvoiceMapper.toCreateInvoice) should
  // move into MonetaryAmount itself as MonetaryAmount.ofWithoutVat(price, vatRate) /
  // .ofWithVat(price, vatRate) + a .multiply(quantity) helper, so the calculation and rounding
  // rule live in one place instead of being duplicated across mappers.
  private AccountingLineItem toAccountingLineItem(final ExtractedItemDto item) {
    final BigDecimal unitPriceWithoutVat = item.unitPrice() != null ? item.unitPrice() : BigDecimal.ZERO;
    final BigDecimal unitPriceWithVat = unitPriceWithoutVat
        .multiply(BigDecimal.valueOf(1 + item.VAT() / 100.0));

    final BigDecimal quantity = item.quantity() == null ? BigDecimal.ZERO : item.quantity();
    final BigDecimal totalItemPriceWithoutVat = unitPriceWithoutVat.multiply(quantity);
    final BigDecimal totalItemPriceWithVat = unitPriceWithVat.multiply(quantity);

    return AccountingLineItem.builder()
        .name(item.name())
        .quantity(item.quantity() == null ? 0 : item.quantity().intValue())
        .unit(item.unit())
        .vatRate(item.VAT())
        .unitPrice(MonetaryAmount.builder()
            .priceWithVAT(unitPriceWithVat)
            .priceWithoutVAT(unitPriceWithoutVat)
            .build())
        .totalPrice(MonetaryAmount.builder()
            .priceWithVAT(totalItemPriceWithVat)
            .priceWithoutVAT(totalItemPriceWithoutVat)
            .build())
        .build();
  }

}
