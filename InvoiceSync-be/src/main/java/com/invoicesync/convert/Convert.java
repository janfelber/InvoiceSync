package com.invoicesync.convert;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.invoicesync.shared.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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
@Table(name = "convert_import", schema = "invoice_sync")
public class Convert extends BaseEntity {

  @Column(name = "file_name")
  private String fileName;


  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "data_json")
  private String data;

  @OneToMany(mappedBy = "convert", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ConvertMapping> mappings = new ArrayList<>();

  private String status;

}
