import {Component, OnInit} from '@angular/core';
import {CommonModule, CurrencyPipe, DatePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {Router, RouterLink} from "@angular/router";
import {initDropdowns, initFlowbite} from 'flowbite'
import {FormsModule} from '@angular/forms';
import {ReceiptService} from "../../core/services/receipt.service";
import {PageResponseReceiptResponse} from "./page-response-receipt-response";
import {CompanyService} from "../../core/services/company.service";
import {PageResponseCompanyResponseDto} from "../../core/models/page-response-company-response-dto";
import {MatDialogWindowComponent} from "../../shared/mat-dialog-window/mat-dialog-window.component";
import {ToastrService} from "ngx-toastr";
import {toast} from "ngx-sonner";
import {ReceiptBulkChangeService} from "../../core/services/bulk/ReceiptBulkChangeService";

@Component({
  selector: 'app-receipts',
  imports: [
    CommonModule,
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
  filterOpen = true;

  selectedIds = new Set<number>();
  isBulkView = false;
  selectedAction: string | null = null;
  bulkActionDropdownOpen = false;

  public receiptResponse: PageResponseReceiptResponse = {
    content: []
  };
  public companyResponse: PageResponseCompanyResponseDto = {
    content: []
  };

  public selectedCompanyName: string = 'Spoločnosť';
  public selectedCompanyId: any;
  public modalCompanyId: any = null;
  public lastImportDate?: string;
  public searchText: string = '';

  public page: number = 0;
  public size: number = 10;

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
    private toastr: ToastrService,
    private router: Router,
    private receiptBulkChangeService: ReceiptBulkChangeService,
  ) {
  }

  ngOnInit(): void {
    this.onFetchAllReceipts();
    this.onFetchCompanies();
  }

  onFetchAllReceipts(): void {
    this.receiptService.findAllReceiptsByUser({
      page: this.page,
      size: this.size
    })
      .then(response => {
        this.receiptResponse = response.data;
        const receipts = response.data.content;
        this.lastImportDate = receipts?.length
          ? receipts.reduce(
            (latest: any, curr: any) =>
              new Date(curr.createdAt) > new Date(latest.createdAt) ? curr : latest
          ).createdAt
          : undefined;
        setTimeout(() => initFlowbite(), 0);
      })
      .catch(error => {
        console.error("Chyba pri načítaní firiem:", error);
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
    await this.receiptService.saveReceipt({ files, companyId: this.modalCompanyId });
  }

  onSelectCompany(company: any) {
    this.selectedCompanyName = company.name;
    this.selectedCompanyId = company.id;
    if (company.id === 0) {
      this.onFetchCompanies();
    } else {
      this.onCompanyChange(company);
    }
  }


  toggleFilter(): void {
    this.filterOpen = !this.filterOpen;
  }

  onRowCheckboxChange(id: number): void {
    if (this.selectedIds.has(id)) {
      this.selectedIds.delete(id);
    } else {
      this.selectedIds.add(id);
    }
    this.selectedIds = new Set(this.selectedIds);
  }

  get allSelected(): boolean {
    return this.receiptResponse.content.length > 0 &&
      this.receiptResponse.content.every(r => this.selectedIds.has(r.id));
  }

  toggleSelectAll(): void {
    if (this.allSelected) {
      this.receiptResponse.content.forEach(r => this.selectedIds.delete(r.id));
    } else {
      this.receiptResponse.content.forEach(r => this.selectedIds.add(r.id));
    }
    this.selectedIds = new Set(this.selectedIds);
  }

  goToBulkView(): void {
    this.selectedAction = null;
    this.bulkActionDropdownOpen = false;
    this.isBulkView = true;
  }

  goBack(): void {
    this.isBulkView = false;
    this.selectedAction = null;
    this.bulkActionDropdownOpen = false;
  }

  get selectedReceipts() {
    return this.receiptResponse.content.filter(r => this.selectedIds.has(r.id));
  }

  selectBulkAction(action: string): void {
    this.selectedAction = action;
    this.bulkActionDropdownOpen = false;
  }

  resetBulkAction(): void {
    this.selectedAction = null;
  }

  applyBulkAction(): void {
    if (!this.selectedAction) return;
    const ids = Array.from(this.selectedIds);
    const count = ids.length;
    const action = this.selectedAction;
    this.receiptBulkChangeService.executeBulkChange(action as any, ids)
      .then(() => {
        this.selectedIds.clear();
        this.goBack();
        this.onFetchAllReceipts();
        if (action === 'DELETE') {
          toast.success(`Deleted ${count} receipt${count !== 1 ? 's' : ''}`, { duration: 3000 });
        }
      })
      .catch(error => {
        toast.error('Akcia zlyhala. Skúste znova.');
      });
  }

  onCompanyChange(company: any) {
    this.receiptService.findAllReceiptsByCompany({
        page: this.page,
        size: this.size,
        companyId: company.id
      }
    ).then(response => {
      this.receiptResponse = response.data;
    }).catch(error => {
      console.error(error);
    });
  }

  async onSubmit(): Promise<void> {
    if (!this.modalCompanyId) return;

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
    this.modalCompanyId = null;
  }

  editReceipt(id: number) {
    this.router.navigate(['web/receipts/', id]);
  }

  deleteReceipt(id: number) {
    this.receiptService.deleteReceipt(id).then(response => {
      this.onFetchAllReceipts();
    }).catch(error => {
      this.showErrorToast();
    });
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

  goToPreviousPage() {
    this.page--;
    this.onFetchAllReceipts();
  }

  goToPage(page: number) {
    this.page = page;
    this.onFetchAllReceipts();
  }

  goToNextPage() {
    this.page++;
    this.onFetchAllReceipts();
  }

  get IsLastPage(): boolean {
    return this.page >= ((this.receiptResponse.totalPages ?? 1) - 1);
  }

  readonly Math = Math;
}
