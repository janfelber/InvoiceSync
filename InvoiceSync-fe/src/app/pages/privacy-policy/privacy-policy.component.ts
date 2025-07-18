import {Component, OnInit} from '@angular/core';
import {RouterLink, RouterLinkActive} from "@angular/router";
import {ViewportScroller} from "@angular/common";

@Component({
  selector: 'app-privacy-policy',
  imports: [
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './privacy-policy.component.html',
  styleUrl: './privacy-policy.component.css'
})
export class PrivacyPolicyComponent implements OnInit{

  constructor(private viewportScroller: ViewportScroller) {}

  ngOnInit() {
    this.viewportScroller.setOffset([0, 60]); // ak máš sticky header
  }

  public navigateToSection(section: string) {
    window.location.hash = '';
    window.location.hash = section;
  }

}
