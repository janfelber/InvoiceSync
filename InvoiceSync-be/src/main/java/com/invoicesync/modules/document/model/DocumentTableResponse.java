package com.invoicesync.modules.document.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentTableResponse {

  private Long id;

  private String documentName;

  private String fileName;

  private LocalDateTime createdAt;

  private String note;

}
