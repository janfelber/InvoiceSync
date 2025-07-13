package com.invoicesync.partner;

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
public class RootRegistryXml {

  @XmlElement(name = "DS_DPH_OUD")
  private PartnersList companyList;

}
