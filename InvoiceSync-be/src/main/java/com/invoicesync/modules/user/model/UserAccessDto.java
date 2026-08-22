package com.invoicesync.modules.user.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAccessDto {
  private String role;
  private Set<String> features;
  private boolean emailVerified;
}