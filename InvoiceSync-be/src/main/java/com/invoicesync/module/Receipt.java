package com.invoicesync.module;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.invoicesync.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "receipt", schema = "invoice_sync")
public class Receipt extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "receipt_number")
  private String receiptNumber;

  // @ManyToOne
  // @JoinColumn(name = "\"user_id\"")
  // private UserDemo user;

  @ManyToOne
  @JoinColumn(name = "\"company\"")
  private Company company;

  @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ReceiptItem> items = new ArrayList<>();

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

  @Column(name = "import_date")
  private Date importDate;

  @Column(name = "total_price")
  private String totalPrice;

  @Column(name = "accounting")
  private String accounting;

  @Column(name = "classification_vat")
  private String classificationVAT;

  @Column(name = "classification_kv_vat")
  private String classificationKVVAT;

  @Column(name = "description")
  private String description;

  @Column(name = "is_paid_by_card")
  private boolean isPaidByCard;
}
