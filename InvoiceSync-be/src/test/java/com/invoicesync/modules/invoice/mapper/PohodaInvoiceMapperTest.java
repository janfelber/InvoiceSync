package com.invoicesync.modules.invoice.mapper;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.transform.dom.DOMResult;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.invoicesync.integration.pohoda.generated.datapack.DataPackItemType;
import com.invoicesync.integration.pohoda.generated.datapack.DataPackType;
import com.invoicesync.integration.pohoda.generated.invoice.InvoiceType;
import com.invoicesync.integration.pohoda.generated.invoice.ObjectFactory;
import com.invoicesync.modules.company.model.Company;
import com.invoicesync.modules.invoice.model.Invoice;
import com.invoicesync.modules.invoice.model.InvoiceItem;
import com.invoicesync.modules.invoice.pohoda.PohodaInvoiceMapper;
import com.invoicesync.modules.invoice.repository.InvoiceVatBreakdownRepository;

class PohodaInvoiceMapperTest {

  @Test
  void printsMappedInvoiceAsXml() throws Exception {
    Company company = Company.builder()
        .name("Moja Firma s.r.o.")
        .street("Hlavná")
        .streetNumber("1")
        .city("Bratislava")
        .zip("81101")
        .registrationNumber("12345678")
        .taxId("2023456789")
        .vatId("SK2023456789")
        .build();

    InvoiceItem item = InvoiceItem.builder()
        .name("Telefónne poplatky")
        .quantity(1)
        .unitType("ks")
        .vatRate(20)
        .unitPriceWithoutVat(BigDecimal.valueOf(100))
        .totalItemPriceWithoutVat(BigDecimal.valueOf(100))
        .totalItemPriceWithVat(BigDecimal.valueOf(120))
        .accountValue("326tel.popl")
        .build();

    Invoice invoice = Invoice.builder()
        .invoiceNumber("2026-00123")
        .variableSymbol("2026123")
        .issueDate("2026-08-01")
        .taxDate("2026-08-01")
        .accountingDate("2026-08-01")
        .dueDate("2026-08-15")
        .partnerName("Dodávateľ s.r.o.")
        .partnerStreet("Vedľajšia 5")
        .partnerCity("Košice")
        .partnerZip("04001")
        .partnerRegistrationNumber("87654321")
        .partnerTaxId("2098765432")
        .partnerVatId("SK2098765432")
        .company(company)
        .items(List.of(item))
        .build();

    InvoiceType invoiceType =
        new PohodaInvoiceMapper(Mockito.mock(InvoiceVatBreakdownRepository.class)).toInvoiceType(invoice);

    ObjectFactory objectFactory = new ObjectFactory();
    JAXBElement<InvoiceType> jaxbElement = objectFactory.createInvoice(invoiceType);

    JAXBContext invoiceContext = JAXBContext.newInstance(InvoiceType.class);
    Marshaller invoiceMarshaller = invoiceContext.createMarshaller();

    DOMResult domResult = new DOMResult();
    invoiceMarshaller.marshal(jaxbElement, domResult);
    Element invoiceElement = ((Document) domResult.getNode()).getDocumentElement();

    DataPackItemType dataPackItemType = new DataPackItemType();
    dataPackItemType.setId("1");
    dataPackItemType.setVersion("2.0");
    dataPackItemType.setAny(invoiceElement);

    DataPackType dataPackType = new DataPackType();
    dataPackType.setId("1");
    dataPackType.setIco(invoice.getCompany().getRegistrationNumber());
    dataPackType.setApplication("InvoiceSync");
    dataPackType.setNote("Export faktúry " + invoice.getInvoiceNumber());
    dataPackType.setVersion("2.0");
    dataPackType.getDataPackItem().add(dataPackItemType);

    com.invoicesync.integration.pohoda.generated.datapack.ObjectFactory dataPackObjectFactory =
        new com.invoicesync.integration.pohoda.generated.datapack.ObjectFactory();

    JAXBContext dataPackContext = JAXBContext.newInstance(DataPackType.class);
    Marshaller dataPackMarshaller = dataPackContext.createMarshaller();
    dataPackMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

    StringWriter writer = new StringWriter();
    dataPackMarshaller.marshal(dataPackObjectFactory.createDataPack(dataPackType), writer);

    System.out.println(writer);
  }

}