import { Component, ViewChild } from '@angular/core';
import { FileService } from './file.service';
import { HttpErrorResponse } from '@angular/common/http';
import { GridInvoicesComponent } from './components/grid-invoices/grid-invoices.component';
import { SidenavComponent } from './sidenav/sidenav.component';
import { BodyComponent } from './body/body.component';
import { HeaderComponent } from './header/header.component';
import { Invoices } from './invoices/invoices.component';

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
  Invoices],
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
