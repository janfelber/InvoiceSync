package com.invoicesync.feature;

import lombok.Getter;

@Getter
public enum Feature {
  EKON_SPECIALTY("ekon_specialty"),
  OTHER_USER_ROLE("OTHER_USER_ROLE");

  private final String roleName;

  Feature(String roleName) {
    this.roleName = roleName;
  }

  public String getRoleName() {
    return roleName;
  }
}
