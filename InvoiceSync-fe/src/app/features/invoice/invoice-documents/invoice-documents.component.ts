import {Component, OnInit} from '@angular/core';
import {DatePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {InvoiceService} from "../../../core/services/invoice.service";
import {ActivatedRoute} from "@angular/router";
import {FormBuilder, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatDialogWindowComponent} from "../../../shared/mat-dialog-window/mat-dialog-window.component";
import {ToastrService} from "ngx-toastr";
import {initFlowbite} from "flowbite";

@Component({
  selector: 'invoice-documents',
  imports: [
    NgForOf,
    DatePipe,
    FormsModule,
    MatDialogWindowComponent,
    ReactiveFormsModule,
    NgIf,
    NgClass
  ],
  templateUrl: './invoice-documents.component.html',
  styleUrl: './invoice-documents.component.css'
})
export class InvoiceDocumentsComponent implements OnInit {

  constructor(
    private invoiceService: InvoiceService,
    private route: ActivatedRoute,
    private toastr: ToastrService,
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
    initFlowbite();
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

  async deleteDocumentFromInvoice(documentId: number) {

    try {
      await this.invoiceService.deleteDocumentFromInvoice({
        documentId: documentId
      }).then(() => {
        this.onFetchDocuments(); // refresh až po úspechu
        this.toastr.success('Dokument bol úspešne odstránený.', '', {
          timeOut: 3000,
          progressBar: true,
          progressAnimation: 'increasing',
          closeButton: true,
          positionClass: 'toast-top-right',
        });
      });
    } catch (error: any) {
      if (error.response?.status === 409) {
        this.toastr.warning('Je nám ľúto, tento dokument nemožno vymazať, pretože je zdrojom údajov pre faktúru.', '', {
          timeOut: 6000,
          progressBar: true,
          progressAnimation: 'increasing',
          closeButton: true,
          positionClass: 'toast-top-right',
        });
      } else {
        console.error('Chyba pri mazaní:', error);
        this.toastr.error('Chyba pri mazaní, skúste neskôr alebo kontaktuje podporu', '', {
          timeOut: 3000,
          progressBar: true,
          progressAnimation: 'increasing',
          closeButton: true,
          positionClass: 'toast-top-right',
        });
      }
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
