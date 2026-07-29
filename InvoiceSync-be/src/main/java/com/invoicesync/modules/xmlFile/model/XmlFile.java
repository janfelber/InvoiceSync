package com.invoicesync.modules.xmlFile.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.invoicesync.core.common.BaseEntity;

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
@Table(name = "xml_file", schema = "invoice_sync")
public class XmlFile extends BaseEntity {

  @Column(name = "file_name")
  private String fileName;

  @Column(columnDefinition = "XML", name = "xml_content")
  private String xmlContent;

}
