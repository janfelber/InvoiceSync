import { Component } from '@angular/core';
import {HeaderComponent} from "../header/header.component";
import {HeaderCompanyComponent} from "../header-company/header-company.component";
import {SidenavComponent} from "../sidenav/sidenav.component";
import {GridInvoicesComponent} from "../components/grid-invoices/grid-invoices.component";
import {CommonModule} from "@angular/common";
import {RouterOutlet} from "@angular/router";


/**
 * @deprecated DO NOT USE THIS COMPONENT.
 */
@Component({
    selector: 'app-welcome',
    imports: [
        HeaderComponent,
        HeaderCompanyComponent,
        SidenavComponent,
        GridInvoicesComponent,
        CommonModule,
        RouterOutlet,
    ],
    templateUrl: './welcome.component.html',
    styleUrl: './welcome.component.css'
})
export class WelcomeComponent {

}
