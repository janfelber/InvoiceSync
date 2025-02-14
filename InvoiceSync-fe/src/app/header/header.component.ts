import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { navbarData } from '../sidenav/nav-data';
import { Router } from '@angular/router';

@Component({
    selector: 'app-header',
    imports: [CommonModule, MatToolbarModule, MatIconModule, MatInputModule, MatButtonModule],
    templateUrl: './header.component.html',
    styleUrl: './header.component.css'
})
export class HeaderComponent {

  navData = navbarData;
  isDropdownOpen = false;
  currentRoute = '';

  constructor(private router: Router) {
    this.router.events.subscribe(() => {
      const currentUrl = this.router.url.slice(1);
      const currentNav = this.navData.find(nav => nav.routerLink === currentUrl);
      this.currentRoute = currentNav ? currentNav.label : 'Unknown';
    });
  }

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
    console.log('Dropdown is now', this.isDropdownOpen ? 'open' : 'closed');
  }

  closeDropdown() {
    this.isDropdownOpen = false;
  }


  // @Input() collapsed = true;
  // @Input() screenWidth = 0;


  // getHeadClass() : string {
  //   let styleClass = '';
  //   if(this.collapsed && this.screenWidth > 768) {
  //     styleClass = 'head-trimmed'
  //   }
  //   else {
  //     styleClass = 'head-md-screen'
  //   }
  //   return styleClass;
  // }
}
