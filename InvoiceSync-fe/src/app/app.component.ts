import { Component, ViewChild } from '@angular/core';
import { FileService } from './file.service';
import { saveAs } from 'file-saver';
import { HttpClientModule, HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
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
import { InvoiceService } from './components/grid-invoices/invoice.service';
import { SidenavComponent } from './sidenav/sidenav.component';
import { BodyComponent } from './body/body.component';
import { HeaderComponent } from './header/header.component';
import { Invoices } from './invoices/invoices.component';
import { ContentComponent } from './content/content.component';

interface SideNavToggle {
  screenWidth: number;
  collapsed: boolean;
}


@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  imports:  [
  SidenavComponent,
  BodyComponent,
  HeaderComponent,
  Invoices,
ContentComponent],
})

export class AppComponent  {

  @ViewChild(GridInvoicesComponent) gridInvoicesComponent!: GridInvoicesComponent;
  
  isRexDisabled: boolean = true;

  filenames: string[] = [];
  fileStatus = { status: '', requestType: '', percentage: 0 };


  constructor(private fileService: FileService) {}

  public onUploadFiles(files: File[]): void {
    const formData = new FormData();
    for (const file of files) { formData.append('file', file, file.name); }
    this.fileService.upload(formData).subscribe(
      event => {
        console.log(event);
        this.gridInvoicesComponent.onFetchAllImports();
        
      },
      (error: HttpErrorResponse) => {
        console.error(error);
      }
    );
  }

  openFileDialog() {
    const inputElement = document.createElement('input');
    inputElement.type = 'file';
    inputElement.accept = '.xml';

    inputElement.click();

    inputElement.addEventListener('change', (event: Event) => {
      const target = event.target as HTMLInputElement;
      if (target.files) {
        this.onUploadFiles(Array.from(target.files));
        alert('Files selected');
      }
    });
  }

  isSideNavCollapsed = false;
  screenWidth = 0;

  onToggleSideNav(data: SideNavToggle) : void {
    this.screenWidth = data.screenWidth;
    this.isSideNavCollapsed = data.collapsed;
  }
}
