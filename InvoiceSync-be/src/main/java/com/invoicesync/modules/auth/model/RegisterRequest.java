package com.invoicesync.modules.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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

  @NotBlank
  @Email
  private String email;

  @NotBlank
  @Size(min = 8, max = 30)
  private String username;

  @NotBlank
  @Size(min = 8, max = 30)
  private String fullName;

  @NotBlank
  @Size(min = 12, max = 100)
  private String password;

  @NotBlank
  private String phoneNumber;

  @NotBlank
  private String registrationNumber;

}
