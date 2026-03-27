import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {Router, RouterOutlet} from '@angular/router';
import {AuthService} from "./core/auth/auth.service";
import { toast, NgxSonnerToaster } from 'ngx-sonner';
import { LanguageService } from './core/services/language.service';

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
    NgxSonnerToaster,
  ]
})

export class AppComponent  {

  isRexDisabled: boolean = true;

  protected readonly toast = toast;

  constructor(private authService: AuthService, private languageService: LanguageService) {
    this.authService.init().subscribe();
    this.languageService.init();
  }

  isSideNavCollapsed = false;
  screenWidth = 0;

  onToggleSideNav(data: SideNavToggle) : void {
    this.screenWidth = data.screenWidth;
    this.isSideNavCollapsed = data.collapsed;
  }
}
