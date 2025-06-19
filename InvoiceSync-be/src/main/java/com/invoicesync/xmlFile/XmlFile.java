package com.invoicesync.xmlFile;

import com.invoicesync.shared.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "xml_file", schema = "invoice_sync")
public class XmlFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String file_name;

    @Column(columnDefinition = "XML")
    private String xml_content;

    // @ManyToOne
    // @JoinColumn(name = "\"user_id\"")
    // private UserDemo user;
}
