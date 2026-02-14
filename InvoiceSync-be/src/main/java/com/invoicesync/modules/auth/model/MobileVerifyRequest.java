package com.invoicesync.modules.auth.model;

import lombok.Data;

@Data
public class MobileVerifyRequest {

  private String sessionToken;

  private String deviceName;

}
