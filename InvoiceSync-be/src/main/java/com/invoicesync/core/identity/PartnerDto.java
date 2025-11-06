package com.invoicesync.core.identity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartnerDto {

  private String name;

  private String city;

  private String street;

  private String zip;

  private String registrationNumber;

  private String taxId;

  private String vatId;

}
