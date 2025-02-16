import {Component, Input} from '@angular/core';
import {NgClass, NgForOf} from "@angular/common";
import {RouterLink, RouterLinkActive} from "@angular/router";
import {navbarDataRedesign} from "./nav-data-redesign";

@Component({
    selector: 'app-side-nav-redesign',
  imports: [
    NgForOf,
    RouterLinkActive,
    RouterLink,
    NgClass
  ],
    templateUrl: './side-nav-redesign.component.html',
    styleUrl: './side-nav-redesign.component.css'
})
export class SideNavRedesignComponent {
  navData = navbarDataRedesign;

  @Input() layout: string = 'default';

  token = localStorage.getItem('token');
  userId: number | null = null;

  constructor() {
    const userData = decodeToken(this.token);
    this.userId = userData?.userId ?? null;
  }


  get filteredNavBarData() {
    return this.navData.filter(item =>
      item.layout === this.layout &&
      (this.userId === 0 || !item.hideIfNotZero)
    );
  }
}

function decodeToken(token: string | null) {
  if (!token) return null;
  try {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload));
  } catch (e) {
    console.error('Invalid token', e);
    return null;
  }
}
