import {Component, OnInit} from '@angular/core';
import {CurrencyPipe, DatePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {RouterLink} from "@angular/router";
import {initFlowbite} from 'flowbite'
import {FormsModule} from '@angular/forms';
import {ReceiptService} from "../services/receipt.service";
import {PageResponseReceiptResponse} from "./page-response-receipt-response";
import {CompanyService} from "../services/company.service";
import {PageResponseCompanyResponseDto} from "../servicesss/models/page-response-company-response-dto";
import {MatDialogWindowComponent} from "../../shared/mat-dialog-window/mat-dialog-window.component";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-receipts',
  imports: [
    DatePipe,
    NgForOf,
    RouterLink,
    NgIf,
    FormsModule,
    CurrencyPipe,
    MatDialogWindowComponent,
    NgClass
  ],
  templateUrl: './receipts.component.html',
  styleUrl: './receipts.component.css'
})
export class ReceiptsComponent implements OnInit {

  importModalOpen = false;
  isUploading = false;

  public receiptResponse: PageResponseReceiptResponse = {
    content: []
  };
  public companyResponse: PageResponseCompanyResponseDto = {
    content: []
  };

  public selectedCompanyName: string = 'Spoločnosť';
  public selectedCompanyId: any;
  public lastImportDate?: string;
  public searchText: string = '';

  public page: number = 0;
  public size: number = 20;

  activeTabValue: 'single' | 'multiple' = 'single';
  singleFile: File | null = null;
  multipleFiles: { file: File; preview: string }[] = [];

  set activeTab(tab: 'single' | 'multiple') {
    this.activeTabValue = tab;
    if (tab === 'single') {
      this.multipleFiles = [];
      this.selectedCompanyId = null
    } else {
      this.singleFile = null;
      this.selectedCompanyId = null
    }
  }

  get activeTab(): 'single' | 'multiple' {
    return this.activeTabValue;
  }

  constructor(
    private receiptService: ReceiptService,
    private companyService: CompanyService,
    private toastr: ToastrService
  ) {
  }

  ngOnInit(): void {
    this.onFetchAllReceipts();
    this.onFetchCompanies();
    initFlowbite();
  }

  onFetchAllReceipts(): void {
    this.receiptService.findAllReceiptsByUser({
      page: this.page,
      size: this.size
    })
      .then(response => {
        this.receiptResponse = response.data;
        console.log(this.receiptResponse = response.data)
        const receipts = response.data.content;

        this.lastImportDate = receipts?.length
          ? receipts.reduce(
            (latest: any, curr: any) =>
              new Date(curr.createdAt) > new Date(latest.createdAt) ? curr : latest
          ).createdAt
          : undefined;
      })
      .catch(error => {
        console.error("Chyba pri načítaní firiem:", error);
        alert("Nepodarilo sa načítať firmy.");
      });
  }

  onFetchCompanies() {
    this.companyService.findAllCompaniesByUser().then(response => {
      this.companyResponse = response.data;
    }).catch(error => {
      console.error(error);
    });
  }

  async onUploadFiles(files: File[]): Promise<void> {
    await this.receiptService.saveReceipt({ files, companyId: this.selectedCompanyId });
  }

  onCompanyChange(company: any) {
    this.receiptService.findAllReceiptsByCompany({
        page: this.page,
        size: this.size,
        companyId: company.id
      }
    ).then(response => {
      this.selectedCompanyName = company.name
      this.selectedCompanyId = company.id
      this.receiptResponse = response.data;
    }).catch(error => {
      console.error(error);
    });
  }

  async onSubmit(): Promise<void> {
    if (!this.selectedCompanyId) return;

    this.isUploading = true;
    try {
      if (this.activeTab === 'single' && this.singleFile) {
        await this.onUploadFiles([this.singleFile]);
      } else if (this.activeTab === 'multiple' && this.multipleFiles.length > 0) {
        const files = this.multipleFiles.map(fileObj => fileObj.file);
        await this.onUploadFiles(files);
      } else {
        alert("Prosím vyberte aspoň jeden obrázok.");
        return;
      }

      this.showSuccessToast();
      this.closeImportModal();
      this.onFetchAllReceipts()
    } catch (err) {
      this.showErrorToast?.();
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
        this.multipleFiles.push({ file, preview: e.target.result });
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
    this.selectedCompanyId = null;
  }

  showSuccessToast() {
    this.toastr.success(
      'Import prebehol úspešne!',
      '',
      {
        timeOut: 3000,
        progressBar: true,
        progressAnimation: 'increasing',
        closeButton: true,
        positionClass: 'toast-top-right',
        enableHtml: true,
      }
    );
  }

  showErrorToast() {
    this.toastr.error(
      'Import zlyhal. Skúste znova.',
      '',
      {
        timeOut: 3000,
        progressBar: true,
        progressAnimation: 'increasing',
        closeButton: true,
        positionClass: 'toast-top-right',
        enableHtml: true,
      }
    );
  }
}
