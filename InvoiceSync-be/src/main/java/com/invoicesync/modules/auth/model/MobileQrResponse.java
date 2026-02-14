package com.invoicesync.modules.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MobileQrResponse {

  private String qrData;

  private int expiresIn;

}
