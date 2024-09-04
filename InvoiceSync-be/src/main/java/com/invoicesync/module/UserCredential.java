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

@Entity
@Table(name = "USER_CREADENTIAL", schema = "invoice_sync")
public class UserCredential {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private int id;

  @NotNull
  @NotEmpty
  @Column(name = "USERNAME")
  private String username;

  @OneToMany(mappedBy = "userCredential", fetch = FetchType.LAZY)
  @JsonIgnoreProperties("userCredential")
  private List<InvoiceImport> invoiceImports;

  public List<InvoiceImport> getInvoiceImports() {
    return invoiceImports;
  }

  public void setInvoiceImports(final List<InvoiceImport> invoiceImports) {
    this.invoiceImports = invoiceImports;
  }

  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public @NotNull @NotEmpty String getUsername() {
    return username;
  }

  public void setUsername(
      final @NotNull @NotEmpty String username) {
    this.username = username;
  }

}