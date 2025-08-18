import {Component, Input, OnInit} from '@angular/core';
import { CommonModule } from "@angular/common";
import {NgComponentOutlet, NgForOf, NgOptimizedImage} from "@angular/common";
import {RouterLink, RouterLinkActive} from "@angular/router";
import {MENU_ITEMS, navbarDataRedesign} from "./nav-data-redesign";
import {MenuItem} from "./MenuItem";
import {KeycloakService} from "../core/keycloak/keycloak.service";
import {IconService} from "./icons/icon.service";

@Component({
    selector: 'app-side-nav',
  imports: [
    RouterLink,
    NgOptimizedImage,
    CommonModule,
    RouterLinkActive
  ],
    templateUrl: './side-nav.component.html',
    styleUrl: './side-nav.component.css'
})
export class SideNavComponent implements OnInit {
  menu: MenuItem[] = [];
  dropdownStates: { [key: string]: boolean } = {};

  constructor(private keycloakService: KeycloakService, protected iconService: IconService) {}

  ngOnInit(): void {
    const userRoles = this.keycloakService.getUserRoles();
    this.menu = this.filterMenu(MENU_ITEMS, userRoles);
  }

  private filterMenu(items: MenuItem[], roles: string[]): MenuItem[] {
    return items
      .filter(item => item.roles.some(role => roles.includes(role)))
      .map(item => ({
        ...item,
        children: item.children ? this.filterMenu(item.children, roles) : undefined
      }))
      .filter(item => item.children === undefined || item.children.length > 0); // odstráni prázdne dropdowny
  }

  toggleDropdown(label: string) {
    this.dropdownStates[label] = !this.dropdownStates[label];
  }

  logout() {
    this.keycloakService.logout();
  }
}
