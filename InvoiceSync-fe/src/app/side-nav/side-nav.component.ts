import {Component, OnInit} from '@angular/core';
import {CommonModule} from "@angular/common";
import {NgOptimizedImage} from "@angular/common";
import {RouterLink, RouterLinkActive} from "@angular/router";
import {MENU_ITEMS} from "./nav-data";
import {MenuItem} from "./MenuItem";
import {IconService} from "./icons/icon.service";
import {AuthService} from "../core/auth/auth.service";
import {initFlowbite} from "flowbite";
import {UserService} from "../core/services/user.service";
import {UserInfoResponse} from "../pages/settings/user-info.response";
import {TranslatePipe} from "@ngx-translate/core";

@Component({
  selector: 'app-side-nav',
  imports: [
    RouterLink,
    NgOptimizedImage,
    CommonModule,
    RouterLinkActive,
    TranslatePipe
  ],
  templateUrl: './side-nav.component.html',
  styleUrl: './side-nav.component.css'
})
export class SideNavComponent implements OnInit {
  menu: MenuItem[] = [];
  dropdownStates: { [key: string]: boolean } = {};
  public userInfo: any = null;

  constructor(protected iconService: IconService, public authService: AuthService, public userService: UserService) {
  }

  ngOnInit(): void {
    initFlowbite()
    this.getUserName();
    this.menu = this.filterMenu(MENU_ITEMS, this.authService.userFeatures);
  }

  getUserName(): void {
    this.userService.getCurrentUserFullName().then(response => {
      this.userInfo = response.data;
    })
  }

  private filterMenu(items: MenuItem[], userFeatures: string[]): MenuItem[] {
    return items
      .filter(item =>
        !item.features || item.features.some(f => userFeatures.includes(f))
      )
      .map(item => ({
        ...item,
        children: item.children ? this.filterMenu(item.children, userFeatures) : undefined
      }))
      .filter(item => !item.children || item.children.length > 0);
  }

  toggleDropdown(label: string) {
    this.dropdownStates[label] = !this.dropdownStates[label];
  }

  logout() {
    this.authService.logout();
  }

  getInitials(name: string): string {
    if (!name) return '';
    const words = name.trim().split(' ');
    if (words.length === 1) {
      return words[0].substring(0, 2).toUpperCase();
    }
    return (words[0][0] + words[1][0]).toUpperCase();
  }
}
