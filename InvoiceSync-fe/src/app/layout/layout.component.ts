import { Component } from '@angular/core';
import {HeaderComponent} from "../header/header.component";
import {HeaderCompanyComponent} from "../header-company/header-company.component";
import {SidenavComponent} from "../sidenav/sidenav.component";
import {RouterOutlet} from "@angular/router";
import {GridInvoicesComponent} from "../components/grid-invoices/grid-invoices.component";

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    HeaderComponent,
    HeaderCompanyComponent,
    SidenavComponent,
    RouterOutlet,
    GridInvoicesComponent
  ],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css'
})
export class LayoutComponent {

}
