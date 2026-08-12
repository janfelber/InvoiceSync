package com.invoicesync.supplier.sk;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@XmlRootElement(name = "DS_DPH_OUD")
@XmlAccessorType(XmlAccessType.FIELD)
public class SlovakPartnersList {

  @XmlElement(name = "ITEM")
  private List<SlovakPartner> items;

}
