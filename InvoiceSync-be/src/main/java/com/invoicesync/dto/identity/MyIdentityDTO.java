package com.invoicesync.dto.identity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyIdentityDTO {

  private String name;

  private String surname;

  private String city;

  private String street;

  private String streetNumber;

  private String zip;

  private String registrationNumber;

  private String taxId;

  private String vatId;

}
