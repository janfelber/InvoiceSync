package com.invoicesync.modules.datatransfer.model;

import com.invoicesync.core.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "data_transfer_header", schema = "invoice_sync")
public class DataTransferHeader extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "convert_import_id", nullable = false)
  private DataTransfer dataTransfer;

  @Column(name = "original_header")
  private String originalHeader;

  @Column(name = "mapped_header")
  private String mappedHeader;

  @Column(name = "enabled_flag")
  private boolean isIncluded;

}
