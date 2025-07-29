import {AfterViewInit, Component, OnInit} from '@angular/core';
import {initDropdowns, initFlowbite} from "flowbite";
import {RouterLink} from "@angular/router";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {Invoice} from "../invoices/invoices.component";
import {ConvertService} from "../../core/services/convert.service";
import {PageResponseConvertResponseDto} from "../../core/models/page-response-convert-response-dto";
import {MatDialogWindowComponent} from "../../shared/mat-dialog-window/mat-dialog-window.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpErrorResponse, HttpEventType} from "@angular/common/http";

@Component({
  selector: 'app-data-transfer',
  imports: [
    RouterLink,
    NgForOf,
    MatDialogWindowComponent,
    ReactiveFormsModule,
    NgIf,
    FormsModule
  ],
  templateUrl: './data-transfer.component.html',
  styleUrl: './data-transfer.component.css'
})
export class DataTransferComponent implements OnInit, AfterViewInit {

  constructor(
    private convertService: ConvertService,
  ) {
  }

  ngAfterViewInit(): void {
    initDropdowns();
  }

  ngOnInit(): void {
    initFlowbite()
    this.onFetchAllConvertImports();
  }

  uploadModalOpen = false

  page: number = 0;
  size: number = 5;
  fileName: string = '';
  selectedFile: File | null = null;
  isUploading = false;
  importFinished = false;

  convertResponse: PageResponseConvertResponseDto = {
    content: []
  }

  onFetchAllConvertImports(): void {
    this.convertService.findAllConvertsByUser({
      page: this.page,
      size: this.size,
    })
      .then(response => {
        this.convertResponse = response.data;
        converting: for (const convert of this.convertResponse.content) {
          console.log(convert.id);
        }
        console.log(response);
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


  openUploadModal() {
    this.uploadModalOpen = true;
  }

  closeUploadModal() {
    this.uploadModalOpen = false;
  }

}
