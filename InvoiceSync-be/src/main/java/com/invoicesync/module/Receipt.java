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
@Table(name = "receipt", schema = "invoice_sync")
public class Receipt {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "\"user_id\"")
  private UserDemo user;

  @ManyToOne
  @JoinColumn(name = "\"company\"")
  private Company company;

  private String date;

  @Column(name = "date_payment")
  private String datePayment;

  @Column(name = "date_tax")
  private String dateTax;

  @Column(name = "partner_name")
  private String partnerName;

  @Column(name = "partner_city")
  private String partnerCity;

  @Column(name = "partner_street")
  private String partnerStreet;

  @Column(name = "partner_zip")
  private String partnerZip;

  @Column(name = "partner_registration_number")
  private String partnerRegistrationNumber;

  @Column(name = "partner_tax_id")
  private String partnerTaxId;

  @Column(name = "partner_vat_id")
  private String partnerVatId;

}
