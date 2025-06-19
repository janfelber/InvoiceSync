package com.invoicesync.chartaccount.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassDTO {

  private int classNumber;
  private String className;

  private List<CategoryDTO> categories = new ArrayList<>();
}
