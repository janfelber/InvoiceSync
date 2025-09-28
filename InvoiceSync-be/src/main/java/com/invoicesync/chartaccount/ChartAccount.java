package com.invoicesync.chartaccount;

import com.invoicesync.company.Company;

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

/**
 * Represents a single account from the company's chart of accounts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chart_accounts", schema = "invoice_sync")
public class ChartAccount {

  /**
   * Unique ID of this account.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // @ManyToOne
  // @JoinColumn(name = "\"user_id\"")
  // private UserDemo user;

  /**
   * Company to which the account belongs.
   */
  @ManyToOne
  @JoinColumn(name = "\"company\"")
  private Company company;


  /**
   * Class ID of the account (e.g., 0–9) according to the accounting chart.
   */
  @Column(name = "class_id")
  private String classId;

  /**
   * Name of the class the account belongs to.
   */
  @Column(name = "class_name")
  private String className;

  /**
   * Unique account ID within the class.
   */
  @Column(name = "account_id")
  private String accountId;

  /**
   * Name of the account.
   */
  @Column(name = "account_name")
  private String accountName;

  /**
   * Category code for grouping accounts (e.g., assets, liabilities).
   */
  @Column(name = "category")
  private String category;

  /**
   * Name of the category.
   */
  @Column(name = "category_name")
  private String categoryName;

  /**
   * Indicates if this account can be edited by users.
   */
  @Column(name = "is_editable")
  private boolean isEditable;

}
