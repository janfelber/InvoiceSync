import {Component, OnInit} from '@angular/core';
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {InvoiceService} from "../../../core/services/invoice.service";
import {ActivatedRoute} from "@angular/router";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatDialogWindowComponent} from "../../../shared/mat-dialog-window/mat-dialog-window.component";

@Component({
  selector: 'invoice-documents',
  imports: [
    NgForOf,
    DatePipe,
    FormsModule,
    MatDialogWindowComponent,
    ReactiveFormsModule,
    NgIf
  ],
  templateUrl: './invoice-documents.component.html',
  styleUrl: './invoice-documents.component.css'
})
export class InvoiceDocumentsComponent implements OnInit {

  constructor(
    private invoiceService: InvoiceService,
    private route: ActivatedRoute,
  ) { }

  invoiceId: any = null;
  public documents: any[] = [];

  addDocumentModalOpen = false;
  isUploading = false;

  public documentName: string = '';
  public selectedFile: File | null = null;
  public note: string = '';

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.invoiceId = params.get('id') || '';
    });

    this.onFetchDocuments();
  }

  onFetchDocuments() {
    this.invoiceService.getInvoiceDocumentsById({ invoiceId: this.invoiceId })
      .then(documents => {
        this.documents = documents.data;

        console.log(this.documents);
      })
  }

  async uploadDocument() {
    if (!this.selectedFile) return;

    this.isUploading = true;
    try {
      const r = await this.invoiceService.addDocumentToInvoice({
        invoiceId: this.invoiceId,
        file: this.selectedFile,
        additionalDocumentData: {
          documentName:this.documentName,
          note:this.note
        }
      });
      this.closeAddDocumentModal();
      this.onFetchDocuments();
      console.log(r);
    } catch (e) {
      console.error(e);
    } finally {
      this.isUploading = false;
    }
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    } else {
      this.selectedFile = null;
    }
  }

  openAddDocumentModal() {
    this.addDocumentModalOpen = true;
  }

  closeAddDocumentModal() {
    this.addDocumentModalOpen = false;
  }



}
