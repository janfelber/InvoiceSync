package com.invoicesync.modules.feature.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "features", schema = "invoice_sync")
public class Feature {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long code;

  private String text;
}
