package com.invoicesync.user.sidenav;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SidenavItemDto {
  private String label;
  private String icon;
  private String route;
  private List<SidenavItemDto> children;
}
