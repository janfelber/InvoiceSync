package com.invoicesync.module;

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
@Table(name = "company", schema = "invoice_sync")
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

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

  @ManyToOne
  @JoinColumn(name = "\"user_id\"")
  private UserDemo user;

}
