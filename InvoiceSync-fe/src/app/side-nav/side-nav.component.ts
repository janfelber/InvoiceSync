import {Component, Input, OnInit} from '@angular/core';
import { CommonModule } from "@angular/common";
import {NgComponentOutlet, NgForOf, NgOptimizedImage} from "@angular/common";
import {RouterLink, RouterLinkActive} from "@angular/router";
import {MENU_ITEMS, navbarDataRedesign} from "./nav-data-redesign";
import {MenuItem} from "./MenuItem";
import {IconService} from "./icons/icon.service";
import {AuthService} from "../core/auth/auth.service";
import {initFlowbite} from "flowbite";

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
export class SideNavComponent implements OnInit{
  menu: MenuItem[] = [];
  dropdownStates: { [key: string]: boolean } = {};

  constructor( protected iconService: IconService, public authService: AuthService) {}

  ngOnInit(): void {
    initFlowbite()
    this.getMenu();
  }

  getMenu() {
    return this.authService.currentUser?.sidenav || [];
  }

  toggleDropdown(label: string) {
    this.dropdownStates[label] = !this.dropdownStates[label];
  }

  logout() {
    this.authService.logout();
  }
}
