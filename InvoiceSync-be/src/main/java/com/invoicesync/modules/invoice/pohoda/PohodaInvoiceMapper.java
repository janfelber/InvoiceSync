package com.invoicesync.modules.invoice.pohoda;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoicesync.integration.pohoda.generated.invoice.InvoiceHeaderType;
import com.invoicesync.integration.pohoda.generated.invoice.InvoiceSummaryType;
import com.invoicesync.integration.pohoda.generated.invoice.InvoiceType;
import com.invoicesync.integration.pohoda.generated.invoice.InvoiceTypeType;
import com.invoicesync.integration.pohoda.generated.type.AccountingType;
import com.invoicesync.integration.pohoda.generated.type.Address;
import com.invoicesync.integration.pohoda.generated.type.AddressInternetType;
import com.invoicesync.integration.pohoda.generated.type.AddressType;
import com.invoicesync.integration.pohoda.generated.type.ClassificationVATType;
import com.invoicesync.integration.pohoda.generated.type.CurrencyVAT;
import com.invoicesync.integration.pohoda.generated.type.MyAddress;
import com.invoicesync.integration.pohoda.generated.type.NumberType;
import com.invoicesync.integration.pohoda.generated.type.RefType;
import com.invoicesync.integration.pohoda.generated.type.TypeCurrencyHome;
import com.invoicesync.integration.pohoda.generated.type.TypeRound;
import com.invoicesync.integration.pohoda.generated.type.TypeRoundingDocument;
import com.invoicesync.integration.pohoda.generated.type.VatRateEnum;
import com.invoicesync.modules.company.model.Company;
import com.invoicesync.modules.invoice.model.Invoice;
import com.invoicesync.modules.invoice.model.InvoiceVatBreakdown;
import com.invoicesync.modules.invoice.repository.InvoiceVatBreakdownRepository;

@Service
public class PohodaInvoiceMapper {

  private final InvoiceVatBreakdownRepository invoiceVatBreakdownRepository;

  @Autowired
  public PohodaInvoiceMapper(
      InvoiceVatBreakdownRepository invoiceVatBreakdownRepository
  ) {
    this.invoiceVatBreakdownRepository = invoiceVatBreakdownRepository;
  }

  public InvoiceType toInvoiceType(Invoice invoice) {
    Company company = invoice.getCompany();
    List<InvoiceVatBreakdown> vatBreakdowns = invoiceVatBreakdownRepository.findByInvoice(invoice);

    final Map<Integer, VatRateEnum> vatRateEnumMap =
        Map.of(0, VatRateEnum.NONE, 5, VatRateEnum.THIRD, 19, VatRateEnum.LOW, 23, VatRateEnum.HIGH);

    Map<VatRateEnum, InvoiceVatBreakdown> breakdownByCategory = new HashMap<>();

    for (InvoiceVatBreakdown breakdown : vatBreakdowns) {
      VatRateEnum rateCategory = vatRateEnumMap.get(breakdown.getVatRate());
      breakdownByCategory.put(rateCategory, breakdown);
    }

    InvoiceVatBreakdown noneRow = breakdownByCategory.get(VatRateEnum.NONE);
    InvoiceVatBreakdown lowRow = breakdownByCategory.get(VatRateEnum.LOW);
    InvoiceVatBreakdown thirdRow = breakdownByCategory.get(VatRateEnum.THIRD);
    InvoiceVatBreakdown highRow = breakdownByCategory.get(VatRateEnum.HIGH);

    InvoiceHeaderType invoiceHeaderType = new InvoiceHeaderType();

    // <invoiceHeader><invoiceType>
    invoiceHeaderType.setInvoiceType(InvoiceTypeType.RECEIVED_INVOICE);

    // <invoiceHeader><number><numberRequested>
    NumberType.NumberRequested numberRequested = new NumberType.NumberRequested();
    numberRequested.setValue(invoice.getInvoiceNumber());
    NumberType numberType = new NumberType();
    numberType.setNumberRequested(numberRequested);
    invoiceHeaderType.setNumber(numberType);

    // <invoiceHeader><symVar> / <originalDocument> / <symPar>
    invoiceHeaderType.setSymVar(invoice.getVariableSymbol());
    invoiceHeaderType.setOriginalDocument(invoice.getVariableSymbol());
    invoiceHeaderType.setSymPar(invoice.getVariableSymbol());

    // <invoiceHeader><date> / <dateTax> / <dateAccounting> / <dateDue>
    invoiceHeaderType.setDate(toXmlDate(invoice.getIssueDate()));
    invoiceHeaderType.setDateTax(toXmlDate(invoice.getTaxDate()));
    invoiceHeaderType.setDateAccounting(toXmlDate(invoice.getAccountingDate()));
    invoiceHeaderType.setDateDue(toXmlDate(invoice.getDueDate()));

    // <invoiceHeader><accounting><ids>
    if (invoice.getAccountValue() != null) {
      AccountingType accounting = new AccountingType();
      accounting.setIds(invoice.getAccountValue());
      invoiceHeaderType.setAccounting(accounting);
    }

    // <invoiceHeader><classificationVAT><ids>
    if (invoice.getClassificationVAT() != null) {
      ClassificationVATType classificationVAT = new ClassificationVATType();
      classificationVAT.setIds(invoice.getClassificationVAT());
      invoiceHeaderType.setClassificationVAT(classificationVAT);
    }

    // <invoiceHeader><classificationKVDPH><ids>
    if (invoice.getClassificationKVVAT() != null) {
      RefType classificationKVDPH = new RefType();
      classificationKVDPH.setIds(invoice.getClassificationKVVAT());
      invoiceHeaderType.setClassificationKVDPH(classificationKVDPH);
    }

    // <invoiceHeader><text>
    invoiceHeaderType.setText(invoice.getDescription());

    // <invoiceHeader><partnerIdentity> / <myIdentity>
    invoiceHeaderType.setPartnerIdentity(getPartnerAddress(invoice));
    invoiceHeaderType.setMyIdentity(getMyAddress(company));

    // TODO: <invoiceHeader><paymentAccount> - supplier IBAN (Invoice has no IBAN field yet)

    // TODO: <invoiceDetail><invoiceItem>* - items are not built yet; this mapper currently only
    // produces <invoiceHeader> + <invoiceSummary>, for invoices extracted without items.

    TypeCurrencyHome typeCurrencyHome = new TypeCurrencyHome();

    CurrencyVAT lowVAT = toCurrencyVAT(lowRow == null ? 0.0 : lowRow.getSumVAT().doubleValue());
    CurrencyVAT highVAT = toCurrencyVAT(highRow == null ? 0.0 : highRow.getSumVAT().doubleValue());
    CurrencyVAT thirdVAT = toCurrencyVAT(thirdRow == null ? 0.0 : thirdRow.getSumVAT().doubleValue());

    typeCurrencyHome.setPriceNone(noneRow == null ? 0.0 : noneRow.getSumWithoutVAT().doubleValue());

    typeCurrencyHome.setPriceLow(lowRow == null ? 0.0 : lowRow.getSumWithoutVAT().doubleValue());
    typeCurrencyHome.setPriceLowSum(lowRow == null ? 0.0 : lowRow.getSumWithVAT().doubleValue());
    typeCurrencyHome.setPriceLowVAT(lowVAT);

    typeCurrencyHome.setPriceHigh(highRow == null ? 0.0 : highRow.getSumWithoutVAT().doubleValue());
    typeCurrencyHome.setPriceHighSum(highRow == null ? 0.0 : highRow.getSumWithVAT().doubleValue());
    typeCurrencyHome.setPriceHighVAT(highVAT);

    typeCurrencyHome.setPrice3(thirdRow == null ? 0.0 : thirdRow.getSumWithoutVAT().doubleValue());
    typeCurrencyHome.setPrice3Sum(thirdRow == null ? 0.0 : thirdRow.getSumWithVAT().doubleValue());
    typeCurrencyHome.setPrice3VAT(thirdVAT);

    TypeRound typeRound = new TypeRound();
    typeRound.setPriceRound(0.0);
    typeCurrencyHome.setRound(typeRound);

    // <invoiceSummary>
    InvoiceSummaryType invoiceSummaryType = new InvoiceSummaryType();
    invoiceSummaryType.setRoundingDocument(TypeRoundingDocument.NONE);
    invoiceSummaryType.setRoundingVAT("none");
    invoiceSummaryType.setHomeCurrency(typeCurrencyHome);

    // <invoice> - root element, wraps header + summary
    InvoiceType invoiceType = new InvoiceType();
    invoiceType.setVersion("2.0");
    invoiceType.setInvoiceHeader(invoiceHeaderType);
    invoiceType.setInvoiceSummary(invoiceSummaryType);

    return invoiceType;
  }

  // <invoiceHeader><partnerIdentity><address> - the supplier on a received invoice
  @NonNull
  private static Address getPartnerAddress(final Invoice invoice) {
    AddressType partnerAddressType = new AddressType();
    partnerAddressType.setCompany(invoice.getPartnerName());
    partnerAddressType.setStreet(invoice.getPartnerStreet());
    partnerAddressType.setCity(invoice.getPartnerCity());
    partnerAddressType.setZip(invoice.getPartnerZip());
    partnerAddressType.setIco(invoice.getPartnerRegistrationNumber());
    partnerAddressType.setDic(invoice.getPartnerTaxId());
    partnerAddressType.setIcDph(invoice.getPartnerVatId());

    Address partnerAddress = new Address();
    partnerAddress.setAddress(partnerAddressType);
    return partnerAddress;
  }

  // <invoiceHeader><myIdentity><address> - our own company, not the partner
  @NonNull
  private static MyAddress getMyAddress(final Company company) {
    AddressInternetType myAddressType = new AddressInternetType();
    myAddressType.setCompany(company.getName());
    myAddressType.setStreet(company.getStreet());
    myAddressType.setNumber(company.getStreetNumber());
    myAddressType.setCity(company.getCity());
    myAddressType.setZip(company.getZip());
    myAddressType.setIco(company.getRegistrationNumber());
    myAddressType.setDic(company.getTaxId());
    myAddressType.setIcDph(company.getVatId());

    MyAddress myAddress = new MyAddress();
    myAddress.setAddress(myAddressType);
    return myAddress;
  }

  private XMLGregorianCalendar toXmlDate(String isoDate) {
    if (isoDate == null) {
      return null;
    }
    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(isoDate);
    } catch (DatatypeConfigurationException e) {
      throw new IllegalStateException(e);
    }
  }

  private CurrencyVAT toCurrencyVAT(double priceVAT) {
    CurrencyVAT currencyVAT = new CurrencyVAT();
    currencyVAT.setValue(priceVAT);

    return currencyVAT;
  }

}
