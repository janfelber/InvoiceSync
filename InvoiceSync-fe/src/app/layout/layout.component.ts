import {Component} from '@angular/core';
import {Router, RouterOutlet} from "@angular/router";
import {CommonModule} from "@angular/common";
import {environment} from "../enviroments/enviroments";
import {SideNavComponent} from "../../redesign/side-nav-redesign/side-nav.component";

@Component({
    selector: 'app-layout',
    imports: [
        RouterOutlet,
        CommonModule,
        SideNavComponent
    ],
    templateUrl: './layout.component.html',
    styleUrl: './layout.component.css'
})
export class LayoutComponent{

  constructor(private router: Router) {}

  version = environment.version;

}
