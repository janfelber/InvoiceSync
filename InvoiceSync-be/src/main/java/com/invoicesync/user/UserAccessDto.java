package com.invoicesync.user;

import java.util.List;

import com.invoicesync.user.sidenav.SidenavItemDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAccessDto {
  private String role;
  private List<SidenavItemDto> sidenav;
}