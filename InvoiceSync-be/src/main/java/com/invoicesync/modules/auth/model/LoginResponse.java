package com.invoicesync.modules.auth.model;

import com.invoicesync.modules.user.model.UserAccessDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
  private String accessToken;
  private String refreshToken;
  private UserAccessDto user;
}
