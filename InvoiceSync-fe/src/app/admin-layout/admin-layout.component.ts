import { Component } from '@angular/core';
import {HeaderComponent} from "../header/header.component";
import {RouterOutlet} from "@angular/router";
import {SidenavComponent} from "../sidenav/sidenav.component";

@Component({
    selector: 'app-admin-layout',
    imports: [
        HeaderComponent,
        RouterOutlet,
        SidenavComponent
    ],
    templateUrl: './admin-layout.component.html',
    styleUrl: './admin-layout.component.css'
})
export class AdminLayoutComponent {

}
