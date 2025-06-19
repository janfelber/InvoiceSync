import {Component} from '@angular/core';
import {Router, RouterOutlet} from "@angular/router";
import {CommonModule} from "@angular/common";
import {SideNavComponent} from "../side-nav/side-nav.component";

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

}
