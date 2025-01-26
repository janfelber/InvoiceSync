import {Component, Input} from '@angular/core';
import {NgClass, NgForOf} from "@angular/common";
import {RouterLink, RouterLinkActive} from "@angular/router";
import {navbarDataRedesign} from "./nav-data-redesign";

@Component({
  selector: 'app-side-nav-redesign',
  standalone: true,
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

  get filteredNavBarData() {
    return this.navData.filter(item => item.layout === this.layout);
  }

}
