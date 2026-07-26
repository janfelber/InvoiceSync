package com.invoicesync.modules.auth.model.dto;

import java.time.LocalDateTime;

import com.invoicesync.modules.user.model.UserAccessDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResult {

  private String accessToken;

  private String refreshToken;

  private LocalDateTime refreshExpiresAt;

  private UserAccessDto user;

}
