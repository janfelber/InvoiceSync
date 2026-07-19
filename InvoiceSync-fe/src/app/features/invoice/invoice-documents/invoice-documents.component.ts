import {Component, OnInit} from '@angular/core';
import {DatePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {InvoiceService} from "../../../core/services/invoice.service";
import {ActivatedRoute} from "@angular/router";
import {FormBuilder, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatDialogWindowComponent} from "../../../shared/mat-dialog-window/mat-dialog-window.component";
import {ToastrService} from "ngx-toastr";
import {initFlowbite} from "flowbite";
import {finalize} from "rxjs";
import {HttpErrorResponse} from "@angular/common/http";
import {InvoiceDocumentResponse} from "../../../core/models/invoice-document-response";

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

  invoiceId!: number;
  public documents: InvoiceDocumentResponse[] = [];

  addDocumentModalOpen = false;
  isUploading = false;

  public documentName: string = '';
  public selectedFile: File | null = null;
  public note: string = '';

  ngOnInit(): void {
    const invoiceId = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(invoiceId)) {
      console.error('Invalid invoice ID');
      return;
    }

    this.invoiceId = invoiceId;
    initFlowbite();
    this.onFetchDocuments();
  }

  onFetchDocuments(): void {
    this.invoiceService.getInvoiceDocumentsById(this.invoiceId)
      .subscribe(documents => {
        this.documents = documents;
      })
  }

  uploadDocument(): void {
    if (!this.selectedFile) return;

    this.isUploading = true;

    this.invoiceService.addDocumentToInvoice({
        invoiceId: this.invoiceId,
        file: this.selectedFile,
        additionalDocumentData: {
          documentName:this.documentName,
          note:this.note
        }
      })
      .pipe(
        finalize(() => {
          this.isUploading = false;
        })
      )
      .subscribe({
        next: () => {
          this.closeAddDocumentModal();
          this.onFetchDocuments();
        },
        error: error => {
          console.error('Document upload failed:', error);
        }
      });
  }

  deleteDocumentFromInvoice(documentId: number): void {
    this.invoiceService.deleteDocumentFromInvoice(documentId)
      .subscribe({
        next: () => {
          this.onFetchDocuments(); // refresh až po úspechu
          this.toastr.success('Dokument bol úspešne odstránený.', '', {
            timeOut: 3000,
            progressBar: true,
            progressAnimation: 'increasing',
            closeButton: true,
            positionClass: 'toast-top-right',
          });
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 409) {
            this.toastr.warning('Je nám ľúto, tento dokument nemožno vymazať, pretože je zdrojom údajov pre faktúru.', '', {
              timeOut: 6000,
              progressBar: true,
              progressAnimation: 'increasing',
              closeButton: true,
              positionClass: 'toast-top-right',
            });
            return;
          }

          console.error('Chyba pri mazaní:', error);
          this.toastr.error('Chyba pri mazaní, skúste neskôr alebo kontaktuje podporu', '', {
            timeOut: 3000,
            progressBar: true,
            progressAnimation: 'increasing',
            closeButton: true,
            positionClass: 'toast-top-right',
          });
        }
      });
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
