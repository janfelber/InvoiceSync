import { Component } from '@angular/core';
import {RouterOutlet} from "@angular/router";
import {SideNavComponent} from "../../redesign/side-nav-redesign/side-nav.component";

@Component({
    selector: 'app-admin-layout',
  imports: [
    RouterOutlet,
    SideNavComponent
  ],
    templateUrl: './admin-layout.component.html',
    styleUrl: './admin-layout.component.css'
})
export class AdminLayoutComponent {

}
