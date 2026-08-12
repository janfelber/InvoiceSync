package com.invoicesync.supplier.sk;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import com.invoicesync.core.utils.VatUtils;

import lombok.Getter;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class SlovakPartner {

  @XmlElement(name = "IC_DPH")
  private String vatId;

  private String taxId;

  @XmlElement(name = "ICO")
  private String registrationNumber;

  @XmlElement(name = "IBAN")
  private String iban;

  @XmlElement(name = "NAZOV_SUBJEKTU")
  private String name;

  @XmlElement(name = "OBEC")
  private String city;

  @XmlElement(name = "ULICA_CISLO")
  private String street;

  @XmlElement(name = "PSC")
  private String zip;

  @XmlElement(name = "STAT")
  private String country;

  public void setVatId(final String vatId) {
    this.vatId = vatId;
    this.taxId = VatUtils.convertVatIdToTaxId(vatId);
  }

  public String getTaxId() {
    if (taxId == null && vatId != null) {
      taxId = VatUtils.convertVatIdToTaxId(vatId);
    }
    return taxId;
  }

}
