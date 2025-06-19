package com.invoicesync.company;

import com.invoicesync.shared.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "company", schema = "invoice_sync")
public class Company extends BaseEntity {

  private String name;

  private String city;

  private String street;

  @Column(name = "street_number")
  private String streetNumber;

  private String zip;

  @Column(name = "registration_number")
  private String registrationNumber;

  @Column(name = "tax_id")
  private String taxId;

  @Column(name = "vat_id")
  private String vatId;

  // @ManyToOne
  // @JoinColumn(name = "\"user_id\"")
  // private UserDemo user;

}
