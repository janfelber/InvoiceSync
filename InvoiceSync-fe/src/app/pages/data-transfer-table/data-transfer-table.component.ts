import {AfterViewInit, Component, OnInit} from '@angular/core';
import {initDropdowns, initFlowbite} from "flowbite";
import {RouterLink} from "@angular/router";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {DataTransferService} from "../../core/services/data-transfer.service";
import {PageResponseConvertResponseDto} from "../../core/models/page-response-convert-response-dto";
import {MatDialogWindowComponent} from "../../shared/mat-dialog-window/mat-dialog-window.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";

@Component({
  selector: 'data-transfer-table',
  imports: [
    RouterLink,
    NgForOf,
    MatDialogWindowComponent,
    ReactiveFormsModule,
    NgIf,
    FormsModule,
    NgClass
  ],
  templateUrl: './data-transfer-table.component.html',
  styleUrl: './data-transfer-table.component.css'
})
export class DataTransferTableComponent implements OnInit, AfterViewInit {

  constructor(
    private convertService: DataTransferService,
  ) {
  }

  ngOnInit(): void {
    this.onFetchAllConvertImports();
  }

  ngAfterViewInit(): void {
    initDropdowns();
  }


  page: number = 0;
  size: number = 5;
  fileName: string = '';
  selectedFile: File | null = null;
  convertResponse: PageResponseConvertResponseDto = {
    content: []
  }

  uploadModalOpen = false
  isUploading = false;
  importFinished = false;

  onFetchAllConvertImports(): void {
    this.convertService.findAllConvertsByUser({
      page: this.page,
      size: this.size,
    })
      .then(response => {
        this.convertResponse = response.data;
        setTimeout(() => initFlowbite(), 0);
      })
  }

  onConvertFileUpload(event: any): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  async onUploadFile(): Promise<void> {
    this.isUploading = true;
    this.importFinished = false;
    try {
      if (this.selectedFile) {
        await this.convertService.saveConvert({
          files: [this.selectedFile]
        })
      }

      this.closeUploadModal();
      this.onFetchAllConvertImports();
    } catch (err) {
      console.error(err);
    } finally {
      this.isUploading = false;
      this.importFinished = true;
    }
  }

  onDownload(id: number, fileName?: string) {
    this.convertService.downloadConvert(id).then(response => {
      const blob = new Blob([response.data]);

      let name = 'output__mapped.xlsx';

      if (fileName) {
        const dotIndex = fileName.lastIndexOf('.');
        if (dotIndex !== -1) {
          const base = fileName.substring(0, dotIndex);
          const ext = fileName.substring(dotIndex);
          name = `${base}__mapped${ext}`;
        } else {
          name = `${fileName}__mapped.xlsx`;
        }
      }

      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = name;
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }

  getStatusClasses(status: string | undefined): string {
    switch (status) {
      case 'PROCESSED':
        return 'bg-green-100 text-green-800';
      case 'UNPROCESSED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  getStatusText(status: string | undefined): string {
    switch (status) {
      case 'PROCESSED':
        return 'Spracované';
      case 'UNPROCESSED':
        return 'Nespracované';
      default:
        return 'Neznámy stav';
    }
  }

  openUploadModal() {
    this.uploadModalOpen = true;
  }

  closeUploadModal() {
    this.uploadModalOpen = false;
  }

}
