package com.invoicesync.invoice;

import org.springframework.stereotype.Service;

import com.invoicesync.invoice.dto.InvoiceDetailsDTO;
import com.invoicesync.invoice.dto.InvoiceResponseDTO;

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
        .partnerName(request.partnerName())
        .partnerStreet(request.partnerStreet())
        .partnerCity(request.partnerCity())
        .partnerZip(request.partnerZip())
        .partnerRegistrationNumber(request.partnerRegistrationNumber())
        .partnerVatId(request.partnerVatId())
        .partnerTaxId(request.partnerTaxId())
        .build();

    return invoice;
  }

  public InvoiceResponseDTO toInvoiceTableResponse(final Invoice invoice) {
    return InvoiceResponseDTO.builder()
        .id(invoice.getId())
        .createAt(invoice.getCreatedDate())
        .invoiceDetails(InvoiceDetailsDTO.builder()
            .invoiceNumber(invoice.getInvoiceNumber())
            .variableSymbol(invoice.getVariableSymbol( ))
            .issueDate(invoice.getIssueDate())
            .status(invoice.getStatus())
            .invoiceType(invoice.getInvoiceType())
            .build())
        .build();
  }

}
