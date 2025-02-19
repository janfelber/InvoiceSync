import { Component, ViewChild } from '@angular/core';
import { FileService } from './file.service';
import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { DownloadListComponentComponent } from './components/download-list-component/download-list-component.component';
import { HeaderComponentComponent } from './components/header-component/header-component.component';
import { GridInvoicesComponent } from './components/grid-invoices/grid-invoices.component';
import {MatSidenavModule} from '@angular/material/sidenav';
import { MatNavList } from '@angular/material/list';
import { AppbarComponent } from './components/appbar/appbar.component';
import {MatButtonModule} from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatListModule } from '@angular/material/list';
import { CustomButtonComponent } from './components/custom-button/custom-button.component';
import { InvoiceService } from './page/invoices/invoice.service';
import { SidenavComponent } from './sidenav/sidenav.component';
import { BodyComponent } from './body/body.component';
import { HeaderComponent } from './header/header.component';
import { Invoices } from './page/invoices/invoices.component';
import { ContentComponent } from './content/content.component';
import { HeaderCompanyComponent } from "./header-company/header-company.component";
import {SideNavRedesignComponent} from "../redesign/side-nav-redesign/side-nav-redesign.component";
import {HlmButtonDirective} from "@spartan-ng/ui-button-helm";

interface SideNavToggle {
  screenWidth: number;
  collapsed: boolean;
}


@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css'],
  imports: [
    SidenavComponent,
    BodyComponent,
    HeaderComponent,
    Invoices,
    ContentComponent,
    CommonModule,
    RouterOutlet,
    GridInvoicesComponent,
    HeaderCompanyComponent,
    SideNavRedesignComponent,
    HlmButtonDirective,
  ]
})

export class AppComponent  {

  @ViewChild(GridInvoicesComponent) gridInvoicesComponent!: GridInvoicesComponent;

  isRexDisabled: boolean = true;

  isSideNavCollapsed = false;
  screenWidth = 0;

  onToggleSideNav(data: SideNavToggle) : void {
    this.screenWidth = data.screenWidth;
    this.isSideNavCollapsed = data.collapsed;
  }
}
