package com.invoicesync.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
public class RegisterRequest {
  private String fullName;

  private String username;

  private String password;
}
