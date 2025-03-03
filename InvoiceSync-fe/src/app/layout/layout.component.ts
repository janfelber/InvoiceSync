import {Component} from '@angular/core';
import {HeaderComponent} from "../header/header.component";
import {HeaderCompanyComponent} from "../header-company/header-company.component";
import {SidenavComponent} from "../sidenav/sidenav.component";
import {Router, RouterOutlet} from "@angular/router";
import {GridInvoicesComponent} from "../components/grid-invoices/grid-invoices.component";
import {CommonModule} from "@angular/common";
import {environment} from "../enviroments/enviroments";
import {SideNavRedesignComponent} from "../../redesign/side-nav-redesign/side-nav-redesign.component";

@Component({
    selector: 'app-layout',
    imports: [
        HeaderComponent,
        HeaderCompanyComponent,
        SidenavComponent,
        RouterOutlet,
        GridInvoicesComponent,
        CommonModule,
        SideNavRedesignComponent
    ],
    templateUrl: './layout.component.html',
    styleUrl: './layout.component.css'
})
export class LayoutComponent{

  constructor(private router: Router) {}

  version = environment.version;

}
