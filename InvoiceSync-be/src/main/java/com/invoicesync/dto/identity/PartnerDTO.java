package com.invoicesync.dto.identity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartnerDTO {

  private String name;

  private String city;

  private String street;

  private String zip;

  private String vatId;

}
