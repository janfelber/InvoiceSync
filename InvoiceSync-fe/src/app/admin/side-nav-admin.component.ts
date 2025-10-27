import {Component, Input, OnInit} from '@angular/core';
import { CommonModule } from "@angular/common";
import {NgComponentOutlet, NgForOf, NgOptimizedImage} from "@angular/common";
import {RouterLink, RouterLinkActive} from "@angular/router";
import {AuthService} from "../core/auth/auth.service";
import {initFlowbite} from "flowbite";

@Component({
    selector: 'app-side-nav-admin',
  imports: [
    RouterLink,
    NgOptimizedImage,
    CommonModule,
    RouterLinkActive
  ],
    templateUrl: './side-nav-admin.component.html',
    styleUrl: './side-nav-admin.component.css'
})
export class SideNavAdminComponent implements OnInit{
  dropdownStates: { [key: string]: boolean } = {};

  constructor(public authService: AuthService) {}

  ngOnInit(): void {
    initFlowbite()
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
