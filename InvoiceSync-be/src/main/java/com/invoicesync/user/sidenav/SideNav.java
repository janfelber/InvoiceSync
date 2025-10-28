package com.invoicesync.user.sidenav;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "side_nav", schema = "invoice_sync")
public class SideNav {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private SideNav parent;

  @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
  private Set<SideNav> children = new HashSet<>();

  private String label;
  private String icon;
  private String route;

  public Long getId() {
    return id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public SideNav getParent() {
    return parent;
  }

  public void setParent(final SideNav parent) {
    this.parent = parent;
  }

  public Set<SideNav> getChildren() {
    return children;
  }

  public void setChildren(final Set<SideNav> children) {
    this.children = children;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(final String icon) {
    this.icon = icon;
  }

  public String getRoute() {
    return route;
  }

  public void setRoute(final String route) {
    this.route = route;
  }

}
