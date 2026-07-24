package com.invoicesync.modules.auth.security;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.function.Function;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Component;

import com.invoicesync.core.common.BaseEntity;
import com.invoicesync.modules.company.repository.CompanyRepository;
import com.invoicesync.modules.document.repository.InvoiceDocumentRepository;
import com.invoicesync.modules.document.repository.ReceiptDocumentRepository;
import com.invoicesync.modules.invoice.repository.InvoiceRepository;
import com.invoicesync.modules.receipt.repository.ReceiptRepository;
import com.invoicesync.modules.xmlFile.repository.XmlFileRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OwnershipLoaderRegistry {

  private final ReceiptRepository receiptRepository;

  private final InvoiceRepository invoiceRepository;

  private final CompanyRepository companyRepository;

  private final XmlFileRepository xmlFileRepository;

  private final ReceiptDocumentRepository receiptDocumentRepository;

  private final InvoiceDocumentRepository invoiceDocumentRepository;

  private Map<Class<? extends Annotation>, Function<Long, BaseEntity>> loaders;

  @jakarta.annotation.PostConstruct
  private void init() {
    loaders = Map.of(
        OwnedReceipt.class, id -> receiptRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Receipt with id " + id + " does not exist")),
        OwnedInvoice.class, id -> invoiceRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Invoice with id " + id + " does not exist")),
        OwnedCompany.class, id -> companyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Company with id " + id + " does not exist")),
        OwnedXML.class, id -> xmlFileRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("XML file with id " + id + " does not exist")),
        OwnedReceiptDocument.class, id -> receiptDocumentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Receipt document with id " + id + " does not exist")),
        OwnedInvoiceDocument.class, id -> invoiceDocumentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Invoice document with id " + id + " does not exist"))
    );
  }

  public Function<Long, BaseEntity> get(Class<? extends Annotation> annotationType) {
    Function<Long, BaseEntity> loader = loaders.get(annotationType);
    if (loader == null) {
      throw new IllegalStateException("No ownership loader registered for " + annotationType);
    }
    return loader;
  }

}