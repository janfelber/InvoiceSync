import { Component } from '@angular/core';
import {RouterOutlet} from "@angular/router";
import {SideNavComponent} from "../../side-nav/side-nav.component";
import {SideNavAdminComponent} from "../side-nav-admin.component";

@Component({
  selector: 'app-admin-layout',
  imports: [
    RouterOutlet,
    SideNavComponent,
    SideNavAdminComponent
  ],
  templateUrl: './admin-layout.component.html',
  styleUrl: './admin-layout.component.css'
})
export class AdminLayoutComponent {

}
