package com.invoicesync.module;

import java.util.Date;

import com.invoicesync.user.UserDemo;

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
@Table(name = "xml_file", schema = "invoice_sync")
public class XmlFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String file_name;

    @Column(columnDefinition = "XML")
    private String xml_content;

    private Date created_at;

    @ManyToOne
    @JoinColumn(name = "\"user_id\"")
    private UserDemo user;
}
