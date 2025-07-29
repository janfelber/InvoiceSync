package com.invoicesync.convert;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "convert_mapping", schema = "invoice_sync")
public class ConvertMapping {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "convert_import_id", nullable = false)
  private Convert convert;

  @Column(name = "original_header")
  private String originalHeader;

  @Column(name = "mapped_header")
  private String mappedHeader;

  @Column(name = "enabled_flag")
  private boolean isIncluded;

}
