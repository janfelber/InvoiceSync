import {Component, Input} from '@angular/core';
import { navbarData } from './nav-data';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-sidenav',
    imports: [CommonModule, RouterModule],
    templateUrl: './sidenav.component.html',
    styleUrl: './sidenav.component.css'
})
export class SidenavComponent{
  navData = navbarData;

  @Input() layout: string = 'default';

  get filteredNavBarData() {
    return this.navData.filter(item => item.layout === this.layout);
  }

}
