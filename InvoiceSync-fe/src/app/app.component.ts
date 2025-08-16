import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {Router, RouterOutlet} from '@angular/router';
import {KeycloakService} from "./core/keycloak/keycloak.service";

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

  isSideNavCollapsed = false;
  screenWidth = 0;

  onToggleSideNav(data: SideNavToggle) : void {
    this.screenWidth = data.screenWidth;
    this.isSideNavCollapsed = data.collapsed;
  }
}
