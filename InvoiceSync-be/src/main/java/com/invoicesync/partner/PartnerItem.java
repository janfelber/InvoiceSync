package com.invoicesync.partner;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@Setter
@Getter
public class PartnerItem {
  @XmlElement(name = "IC_DPH")
  private String vatId;

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
}
