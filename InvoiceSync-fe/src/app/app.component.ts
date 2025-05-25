import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import {PreLaunchPageComponent} from "./pre-lanuch-page/pre-launch-page.component";

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
    PreLaunchPageComponent,
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
