package com.invoicesync.modules.postingaccount.model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.invoicesync.modules.company.model.Company;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseDocumentAccountEntity {

  /**
   * Unique ID of this account.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

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
