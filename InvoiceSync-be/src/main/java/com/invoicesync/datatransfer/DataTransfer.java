package com.invoicesync.datatransfer;

import java.util.ArrayList;
import java.util.List;

import com.invoicesync.shared.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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
@Table(name = "data_transfer", schema = "invoice_sync")
public class DataTransfer extends BaseEntity {

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "data_json", columnDefinition = "TEXT")
  private String data;

  @OneToMany(mappedBy = "dataTransfer", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("createdDate ASC")
  private List<DataTransferHeader> mappings = new ArrayList<>();

  private String status;

}
