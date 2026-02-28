import {Component, OnInit} from '@angular/core';
import {CommonModule} from "@angular/common";
import {NgOptimizedImage} from "@angular/common";
import {Router, RouterLink, RouterLinkActive} from "@angular/router";
import {MENU_ITEMS} from "./nav-data";
import {MenuItem} from "./MenuItem";
import {IconService} from "./icons/icon.service";
import {AuthService} from "../core/auth/auth.service";
import {initFlowbite} from "flowbite";
import {UserService} from "../core/services/user.service";
import {UserInfoResponse} from "../pages/settings/user-info.response";
import {ThemeService} from "../core/services/theme.service";

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
  public userInfo: any = null;
  isBetaNoticeDismissed = false;

  constructor(
    protected iconService: IconService,
    public authService: AuthService,
    public userService: UserService,
    public themeService: ThemeService,
    private router: Router
  ) {}

  ngOnInit(): void {
    initFlowbite();
    this.isBetaNoticeDismissed = localStorage.getItem('betaNoticeDismissed') === 'true';
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

  isChildActive(item: MenuItem): boolean {
    return item.children?.some(child =>
      child.route ? this.router.isActive(child.route, {
        paths: 'subset', queryParams: 'ignored', fragment: 'ignored', matrixParams: 'ignored'
      }) : false
    ) ?? false;
  }

  logout() {
    this.authService.logout();
  }

  dismissBetaNotice(): void {
    this.isBetaNoticeDismissed = true;
    localStorage.setItem('betaNoticeDismissed', 'true');
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
