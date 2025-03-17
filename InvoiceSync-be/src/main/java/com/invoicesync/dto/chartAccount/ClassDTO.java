package com.invoicesync.dto.chartAccount;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassDTO {
  private String className;
  private Map<String, CategoryDTO> categories = new HashMap<>();
}
