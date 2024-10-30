import {Component, OnInit} from '@angular/core';
import {HeaderComponent} from "../header/header.component";
import {HeaderCompanyComponent} from "../header-company/header-company.component";
import {SidenavComponent} from "../sidenav/sidenav.component";
import {NavigationEnd, Router, RouterOutlet} from "@angular/router";
import {GridInvoicesComponent} from "../components/grid-invoices/grid-invoices.component";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    HeaderComponent,
    HeaderCompanyComponent,
    SidenavComponent,
    RouterOutlet,
    GridInvoicesComponent,
    CommonModule
  ],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css'
})
export class LayoutComponent implements OnInit{
  showHeaderCompany: boolean = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Check the current URL when the component initializes
    this.updateHeaderVisibility();

    // Listen for navigation changes to dynamically update the flag
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.updateHeaderVisibility();
      }
    });
  }

  private updateHeaderVisibility(): void {
    // Set `showHeaderCompany` to true if the URL includes 'invoices'
    this.showHeaderCompany = this.router.url.includes('/invoices');
  }
}
