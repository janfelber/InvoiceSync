package com.invoicesync.module;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import com.invoicesync.module.InvoiceImport;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "\"USER_CREDENTIAL\"", schema = "invoice_sync")
public class UserCredential {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ID\"", nullable = false)
  private Long id;

  @NotNull@NotEmpty
  @NotNull
  @Column(name = "\"USERNAME\"", nullable = false)
  private String username;


  @Column(name = "\"PASSWORD\"", nullable = false)
  private String password;

  @Column(name = "\"LOGIN\"", nullable = false)
  private String login;

  @OneToMany(mappedBy = "userCredential", fetch = FetchType.LAZY)
  @JsonIgnoreProperties("userCredential")
  private List<InvoiceImport> invoiceImports;

}