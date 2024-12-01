import { Component } from '@angular/core';
import { CommonModule } from "@angular/common";
import {FileService} from "../file.service";
import {InvoiceService} from "../page/invoices/invoice.service";
import {AxiosService} from "../axios.service";
import {MatTableModule} from '@angular/material/table';
import {MatIcon} from "@angular/material/icon";
import {MatButtonModule, MatIconButton} from '@angular/material/button';
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-xml-page',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatIcon,
    MatIconButton,
    MatButtonModule
  ],
  templateUrl: './xml-page.component.html',
  styleUrl: './xml-page.component.css'
})
export class XmlPageComponent {
  private server = 'http://localhost:8080'

  constructor(private fileService: FileService, private invoiceService: InvoiceService, private axiosService: AxiosService) {
  }

  protected imports: string[] = [];
  protected dataSource: any[] = [];
  protected displayedColumns: string[] = ['filename', 'created_at', "download"];

  ngOnInit(): void {
    this.onFetchAllImports();
  }

  onFetchAllImports(): void {
    this.axiosService.request(
      "GET",
      `/xml-file/imports/current-user`,
      null
    ).then(
      (imports) => {
        this.imports = imports.data
        this.dataSource = imports.data;
        console.log(imports.data);
      }
    )
  }

  downloadFile(importId: number) {
    this.axiosService.request('POST', `/xml-file/generate-zip/${importId}`, null, { responseType: 'blob' })
      .then((response) => {
        console.log(importId);
        console.log(response);
        const blob = new Blob([response.data]);
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'output.zip';
        a.click();
        window.URL.revokeObjectURL(url);
      })
      .catch(error => {
        console.error('Download error:', error);
      });
  }

  onFileSelected(): void {
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

  public onUploadFiles(files: File[]): void {
    const formData = new FormData();
    for (const file of files) {
      formData.append('file', file, file.name);
    }
    this.fileService.upload(formData).subscribe({
      next: (event) => {
        console.log(event);
      },
      error: (error: HttpErrorResponse) => {
        console.error(error);
      },
      complete: () => {
        console.log('Upload complete');
      }
    });
  }

}
