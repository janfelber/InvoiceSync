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

  private String email;

  private String username;

  private String fullName;

  private String password;

}
