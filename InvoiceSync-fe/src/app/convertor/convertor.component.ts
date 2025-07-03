import {Component, EventEmitter, Input} from '@angular/core';
import {NgIf} from "@angular/common";
import {HttpErrorResponse} from "@angular/common/http";
import {FileService} from "../file.service";
import {FormsModule} from "@angular/forms";
import {DialogTutorialComponent} from "../../shared/mat-dialog-tutorial/dialog-tutorial.component";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-convertor',
  imports: [
    NgIf,
    FormsModule,
    DialogTutorialComponent,
    RouterLink
  ],
  templateUrl: './convertor.component.html',
  styleUrl: './convertor.component.css'
})
export class ConvertorComponent {

  constructor(
    private fileService: FileService) {
  }

  fileName: string = '';
  selectedFile: File | null = null;
  modalOpen = false;
  myTutorials = [
    'Krok 1: Otvorte menu',
    'Krok 2: Kliknite na "Nový projekt"',
    'Krok 3: Vyplňte formulár a uložte'
  ];

  openModal() {
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
  }

  onFileSelected(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      const file = target.files[0];
      this.selectedFile = file;  // ← uložíš súbor
      this.fileName = file.name.replace(/\.[^/.]+$/, '');
    } else {
      this.fileName = '';
      this.selectedFile = null;
    }
  }

  startConversion(): void {
    if (!this.selectedFile) {
      alert('Prosím najprv vyber súbor!');
      return;
    }

    this.onUploadFile([this.selectedFile]);
  }

  public onUploadFile(files: File[]): void {
    const formData = new FormData();
    for (const file of files) {
      formData.append('file', file, file.name);
    }

    this.fileService.uploadTest(formData).subscribe({
      next: (response) => {
        const blob = new Blob([response], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'});
        const downloadUrl = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.download = `${this.fileName}-pohoda-converted.xlsx`;
        link.click();
        window.URL.revokeObjectURL(downloadUrl);
      },
      error: (error: HttpErrorResponse) => {
        console.error(error);
      },
      complete: () => {
        console.log('Upload complete');
      }
    });
  }

  removeFile(): void {
    this.fileName = '';
  }

}
