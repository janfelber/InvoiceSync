package com.invoicesync.supplier.sk;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@XmlRootElement(name = "ZoznamPlatitDPHsOUD")
@XmlAccessorType(XmlAccessType.FIELD)
public class SlovakRootRegistryXml {

  @XmlElement(name = "DS_DPH_OUD")
  private SlovakPartnersList companyList;

}
