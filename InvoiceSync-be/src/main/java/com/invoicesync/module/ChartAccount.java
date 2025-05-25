package com.invoicesync.module;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "chart_accounts", schema = "invoice_sync")
public class ChartAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // @ManyToOne
  // @JoinColumn(name = "\"user_id\"")
  // private UserDemo user;

  @Column(name = "class_id")
  private String classId;

  @Column(name = "class_name")
  private String className;

  @Column(name = "account_id")
  private String accountId;

  @Column(name = "account_name")
  private String accountName;

  @Column(name = "category")
  private String category;

  @Column(name = "category_name")
  private String categoryName;

  @Column(name = "is_editable")
  private boolean isEditable;

}
