package com.invoicesync.auth;

import com.invoicesync.user.UserAccessDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
  private String accessToken;
  private String refreshToken;
  private UserAccessDto user;
}
