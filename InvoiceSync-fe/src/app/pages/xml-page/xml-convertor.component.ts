import {Component, OnInit} from '@angular/core';
import {CommonModule} from "@angular/common";
import {FileService} from "../../file.service";
import {InvoiceService} from "../invoices/invoice.service";
import {AxiosService} from "../../core/axios.service";
import {MatTableModule} from '@angular/material/table';
import {MatIcon} from "@angular/material/icon";
import {MatButtonModule, MatIconButton} from '@angular/material/button';
import {HttpErrorResponse} from "@angular/common/http";
import {ReactiveFormsModule} from "@angular/forms";
import {FormsModule} from '@angular/forms';
import {MatCheckbox} from "@angular/material/checkbox";
import {XmlFileService} from "../../core/services/xml-file.service";
import {PageResponseXmlFileResponse} from "../receipts/page-response-xml-file-response";
import {MatDialogWindowComponent} from "../../shared/mat-dialog-window/mat-dialog-window.component";
import {initDropdowns, initFlowbite} from "flowbite";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-xml-page',
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    ReactiveFormsModule,
    FormsModule,
    MatDialogWindowComponent,
    RouterLink
  ],
  templateUrl: './xml-convertor.component.html',
  styleUrl: './xml-convertor.component.css'
})
export class XmlConvertorComponent implements OnInit {

  importModalOpen = false;
  isUploading = false;

  public xmlFileResponse: PageResponseXmlFileResponse = {
    content: []
  };

  activeTabValue: 'single' | 'multiple' = 'single';
  singleFile: File | null = null;
  multipleFiles: { file: File; preview: string }[] = [];

  page: number = 0;
  size: number = 10;

  set activeTab(tab: 'single' | 'multiple') {
    this.activeTabValue = tab;
    if (tab === 'single') {
      this.multipleFiles = [];
    } else {
      this.singleFile = null;
    }
  }

  get activeTab(): 'single' | 'multiple' {
    return this.activeTabValue;
  }

  constructor(
    private xmlFileService: XmlFileService,
    private axiosService: AxiosService) {
  }

  ngOnInit(): void {
    initFlowbite();
    initDropdowns();
    this.onFetchAllImports();
  }

  onFetchAllImports(): void {
    this.xmlFileService.findAllXmlFilesByUser({
      page: this.page,
      size: this.size
    })
      .then(response => {
        this.xmlFileResponse = response.data
        console.log(this.xmlFileResponse.content)
      })
      .catch(error => {
        console.log("Error with fetching xml-files:", error)
      });
  }

  downloadFile(importId: number, fileName: string) {
    this.axiosService.request('POST', `/xml-file/generate-zip/${importId}`, null, {responseType: 'blob'})
      .then((response) => {
        const blob = new Blob([response.data]);
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName + '.zip';
        a.click();
        window.URL.revokeObjectURL(url);
      })
      .catch(error => {
        console.error('Download error:', error);
      });
  }


  async onUploadFiles(files: File[]): Promise<void> {
    await this.xmlFileService.saveXmlFile({files});
  }

  async onSubmit(): Promise<void> {
    this.isUploading = true;
    try {
      if (this.activeTab === 'single' && this.singleFile) {
        await this.onUploadFiles([this.singleFile]);
      } else if (this.activeTab === 'multiple' && this.multipleFiles.length > 0) {
        const files = this.multipleFiles.map(fileObj => fileObj.file);
        await this.onUploadFiles(files);
      } else {
        alert("Prosím vyberte aspoň jeden XML súbor.");
        return;
      }
      this.closeImportModal();
      this.onFetchAllImports()
    } catch (err) {
    } finally {
      this.isUploading = false;
    }
  }

  onSingleFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.singleFile = input.files[0];
    }
  }

  onMultipleFilesSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files) return;

    this.multipleFiles = [];
    Array.from(input.files).forEach((file) => {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.multipleFiles.push({file, preview: e.target.result});
      };
      reader.readAsDataURL(file);
    });
  }

  resetAll() {
    this.singleFile = null;
    this.multipleFiles = [];
  }

  openImportModal() {
    this.importModalOpen = true;
  }

  closeImportModal() {
    this.importModalOpen = false;
    this.resetAll();
  }
}
