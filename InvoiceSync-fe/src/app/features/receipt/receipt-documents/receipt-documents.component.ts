import {Component, OnInit} from '@angular/core';
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {MatDialogWindowComponent} from "../../../shared/mat-dialog-window/mat-dialog-window.component";
import {ReceiptService} from "../../../core/services/receipt.service";
import {ActivatedRoute} from "@angular/router";
import {initFlowbite} from "flowbite";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'receipt-documents',
  imports: [
    DatePipe,
    FormsModule,
    MatDialogWindowComponent,
    NgForOf,
    NgIf
  ],
  templateUrl: './receipt-documents.component.html',
  styleUrl: './receipt-documents.component.css'
})
export class ReceiptDocumentsComponent implements OnInit{

  constructor(
    private receiptService: ReceiptService,
    private route: ActivatedRoute,
    private toastr: ToastrService
  ) { }

  receiptId: any = null;
  public documents: any[] = [];

  addDocumentModalOpen = false;
  isUploading = false;

  public documentName: string = '';
  public selectedFile: File | null = null;
  public note: string = '';

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.receiptId = params.get('id') || '';
    });
    initFlowbite();
    this.onFetchDocuments();
  }

  onFetchDocuments() {
    this.receiptService.getReceiptDocumentsById({ receiptId: this.receiptId })
      .then(documents => {
        this.documents = documents.data;
        console.log(this.documents);
      })
  }

  async uploadDocument() {
    if (!this.selectedFile) return;

    this.isUploading = true;
    try {
      const r = await this.receiptService.addDocumentToReceipt({
        receiptId: this.receiptId,
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

  async deleteDocumentFromReceipt(documentId: number) {
    try {
      await this.receiptService.deleteDocumentFromReceipt({
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
        this.toastr.warning('Je nám ľúto, tento dokument nemožno vymazať, pretože je zdrojom údajov pre bloček.', '', {
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
