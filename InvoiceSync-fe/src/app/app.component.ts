import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {Router, RouterOutlet} from '@angular/router';
import {AuthService} from "./core/auth/auth.service";
import {TranslateService} from "@ngx-translate/core";

interface SideNavToggle {
  screenWidth: number;
  collapsed: boolean;
}


@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css'],
  imports: [
    CommonModule,
    RouterOutlet,
  ]
})

export class AppComponent  {

  isRexDisabled: boolean = true;

  constructor(
    private authService: AuthService,
    private translate: TranslateService
  ) {
    this.authService.init().subscribe();
    const saved = localStorage.getItem('lang') || 'en';
    translate.setDefaultLang(saved);
    translate.use(saved);
  }

  isSideNavCollapsed = false;
  screenWidth = 0;

  onToggleSideNav(data: SideNavToggle) : void {
    this.screenWidth = data.screenWidth;
    this.isSideNavCollapsed = data.collapsed;
  }
}
