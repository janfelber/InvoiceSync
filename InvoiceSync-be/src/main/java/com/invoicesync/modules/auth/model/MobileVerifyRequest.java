package com.invoicesync.modules.auth.model;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class MobileVerifyRequest {

  @NotBlank
  private String sessionToken;

  @NotBlank
  private String deviceName;

}
