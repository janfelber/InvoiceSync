import { Component } from '@angular/core';
import {DatePipe, NgClass, NgForOf} from "@angular/common";
import {MatCheckbox} from "@angular/material/checkbox";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {HttpErrorResponse} from "@angular/common/http";
import {FileService} from "../file.service";
import {AxiosService} from "../axios.service";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-receipts',
  imports: [
    DatePipe,
    MatCheckbox,
    MatIcon,
    MatIconButton,
    NgForOf,
    NgClass,
    RouterLink
  ],
  templateUrl: './receipts.component.html',
  styleUrl: './receipts.component.css'
})
export class ReceiptsComponent {

  constructor(
    private fileService: FileService,
    private axiosService: AxiosService) {
  }

  ngOnInit(): void {
    this.onFetchAllImports();
  }

  headers = ['Status', 'Nazov', 'Pridane', 'Stiahnut'];

  protected imports: string[] = [];

  onFetchAllImports(): void {
    this.axiosService.request(
      "GET",
      `/api/v1/receipt/current-user`,
      null
    ).then(
      (imports) => {
        this.imports = imports.data
        console.log(imports.data);
      }
    )
  }

  onFileSelected(): void {
    const inputElement = document.createElement('input');
    inputElement.type = 'file';
    inputElement.accept = '.png';

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
    formData.append('companyId', '2');

    this.fileService.uploadReceipt(formData).subscribe({
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
