import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {CommonModule, CurrencyPipe, DatePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {Router, RouterLink} from "@angular/router";
import {initFlowbite} from 'flowbite'
import {FormsModule} from '@angular/forms';
import {ReceiptDTO, ReceiptService} from "../../core/services/receipt.service";
import {PageResponseReceiptResponse} from "./page-response-receipt-response";
import {CompanyService} from "../../core/services/company.service";
import {PageResponseCompanyResponseDto} from "../../core/models/page-response-company-response-dto";
import {ToastrService} from "ngx-toastr";
import {toast} from "ngx-sonner";
import {ReceiptBulkChangeService} from "../../core/services/bulk/ReceiptBulkChangeService";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {SafeHtmlPipe} from "../../shared/pipes/safe-html.pipe";
import {SimpleSelectComponent, SelectOption} from "../../shared/simple-select/simple-select.component";

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
    TranslateModule,
    SafeHtmlPipe,
    NgClass,
    SimpleSelectComponent
  ],
  templateUrl: './receipts.component.html',
  styleUrl: './receipts.component.css'
})
export class ReceiptsComponent implements OnInit {
  private readonly receiptService = inject(ReceiptService)

  receipts = signal<ReceiptDTO[]>([]);
  totalPages = signal(0);
  totalElements = signal(0);

  loading = signal(true);
  isUploading = signal(false);
  isModalOpen = signal(false);
  isFilterOpen = signal(true);
  isBulkView= signal(false);
  isBulkActionDropdownOpen= signal(false);
  isBulkCompanyDropdownOpen= signal(false);
  searchQuery  = signal('');

  // TODO: implement server-side filtering — pass search params to findAllReceiptByUser() and reload
  filtered = computed(() => {
    return this.receipts();
  })

  selectedIds = signal<number[]>([]);
  selectedAction = signal<string | null>(null);
  bulkCompanyId = signal<number | null>(null);
  bulkCompanyName = signal<string | null>(null);

  public receiptResponse: PageResponseReceiptResponse = {
    content: []
  };
  public companyResponse: PageResponseCompanyResponseDto = {
    content: []
  };

  selectedCompanyId = signal<number | null>(null);
  modalCompanyId = signal<number | null>(null);
  searchText = signal('');

  public page: number = 0;
  public size: number = 10;

  public lastImportDate?: string;
  activeTabValue: 'single' | 'multiple' = 'single';
  singleFile: File | null = null;
  multipleFiles: { file: File; preview: string }[] = [];

  set activeTab(tab: 'single' | 'multiple') {
    this.activeTabValue = tab;
    if (tab === 'single') {
      this.multipleFiles = [];
      this.selectedCompanyId.set(null)
    } else {
      this.singleFile = null;
      this.selectedCompanyId.set(null)
    }
  }

  get activeTab(): 'single' | 'multiple' {
    return this.activeTabValue;
  }

  constructor(
    private receiptServiceLegacy: ReceiptService,
    private companyService: CompanyService,
    private toastr: ToastrService,
    private router: Router,
    private receiptBulkChangeService: ReceiptBulkChangeService,
    private translate: TranslateService,
  ) {}

  getBulkActionLabel(): string {
    if (this.selectedAction() === 'DELETE') return this.translate.instant('COMMON.DELETE');
    if (this.selectedAction() === 'COMPANY_REASSIGN') return this.translate.instant('BULK.COMPANY_REASSIGN');
    return this.translate.instant('BULK.SELECT_ACTION');
  }

  ngOnInit(): void {
    this.onFetchAllReceipts();
    this.onFetchCompanies();
  }

  onFetchAllReceipts(): void {
    this.loading.set(true);
    this.receiptService.findAllReceiptsByUser(this.page, this.size).subscribe({
      next: page => {
        this.page = page.number;
        this.size = page.size;
        this.receipts.set(page.content);
        this.totalPages.set(page.totalPages);
        this.totalElements.set(page.totalElements);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  onFetchAllReceiptsLegacy(): void {
    this.receiptServiceLegacy.findAllReceiptsByUserLegacy({
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

  // TODO refactor this call when company service will be migrated to use HttpClient
  onFetchCompanies() {
    this.companyService.findAllCompaniesByUser().then(response => {
      this.companyResponse = response.data;
    }).catch(error => {
      console.error(error);
    });
  }

  async onUploadFiles(files: File[]): Promise<void> {
    await this.receiptService.saveReceiptLegacy({ files, companyId: this.modalCompanyId()! });
  }

  get companyOptions(): SelectOption[] {
    return [
      { value: 0, label: this.translate.instant('COMMON.ALL_COMPANIES') },
      ...this.companyResponse.content.map(c => ({ value: c.id ?? 0, label: c.name ?? '' }))
    ];
  }

  get companyOptionsNoAll(): SelectOption[] {
    return this.companyResponse.content.map(c => ({ value: c.id ?? 0, label: c.name ?? '' }));
  }

  onSelectCompany(company: any) {
    this.selectedCompanyId.set(company.id);
    if (company.id === 0) {
      this.onFetchCompanies();
    } else {
      this.onCompanyChange(company);
    }
  }

  onCompanyFilterChange(id: number): void {
    const company = id === 0 ? { id: 0, name: '' } : this.companyResponse.content.find(c => c.id === id);
    if (company) this.onSelectCompany(company);
  }


  toggleFilter(): void {
    this.isFilterOpen.update(current => !current);
  }

  onRowCheckboxChange(id: number): void {
    this.selectedIds.update(ids =>
      ids.includes(id) ? ids.filter(i => i !== id) : [...ids, id]
    );
  }

  get allSelected(): boolean {
    return this.receipts().length > 0 &&
      this.receipts().every(receipt => this.selectedIds().includes(receipt.id));
  }

  toggleSelectAll(): void {
    if (this.allSelected) {
      this.selectedIds.set([]);
    } else {
      this.selectedIds.set(this.receipts().map(r => r.id));
    }
  }

  goToBulkView(): void {
    this.selectedAction.set(null);
    this.isBulkActionDropdownOpen.set(false);
    this.isBulkView.set(true);
  }

  goBack(): void {
    this.isBulkView.set(false);
    this.selectedAction.set(null);
    this.isBulkActionDropdownOpen.set(false);
  }

  get selectedReceipts() {
    return this.receipts().filter(receipt => this.selectedIds().includes(receipt.id));
  }

  selectBulkAction(action: string): void {
    this.selectedAction.set(action);
    this.isBulkActionDropdownOpen.set(false);
  }

  resetBulkAction(): void {
    this.selectedAction.set(null);
    this.bulkCompanyId.set(null);
    this.bulkCompanyName.set(null);
    this.isBulkCompanyDropdownOpen.set(false);
  }

  selectBulkCompany(company: any): void {
    this.bulkCompanyId.set(company.id);
    this.bulkCompanyName.set(company.name);
    this.isBulkCompanyDropdownOpen.set(false);
  }

  get canConfirmBulk(): boolean {
    if (this.selectedAction() === 'COMPANY_REASSIGN') return this.bulkCompanyId() !== null;
    return this.selectedAction() !== null;
  }

  applyBulkAction(): void {
    if (!this.selectedAction()) return;
    const ids = this.selectedIds();
    const count = ids.length;
    const action = this.selectedAction()!;
    const companyId = this.bulkCompanyId() ?? undefined;
    const companyName = this.bulkCompanyName();
    this.receiptBulkChangeService.executeBulkChange(action as any, ids, companyId)
      .subscribe({
        next: () => {
          this.selectedIds.set([]);
          this.goBack();
          this.onFetchAllReceipts();
          this.selectedIds.set([]);
          if (action === 'DELETE') {
            toast.success(this.translate.instant('RECEIPT.TOAST_BULK_DELETE', { count }), { duration: 3000 });
          } else if (action === 'COMPANY_REASSIGN') {
            toast.success(this.translate.instant('RECEIPT.TOAST_BULK_REASSIGN', { count, company: companyName }), { duration: 3000 });
          }
        },
        error: () => toast.error(this.translate.instant('RECEIPT.TOAST_BULK_ERROR'), { duration: 3000 }),
      });
  }

  onCompanyChange(company: any) {
    this.receiptService.findAllReceiptsByCompany(
      this.page, this.size, company.id
    ).subscribe({
      next: page => {
        this.receipts.set(page.content);
        this.totalPages.set(page.totalPages);
        this.totalElements.set(page.totalElements);
      },
      error: () => toast.error('Akcia zlyhala. Skúste znova.'),
    });
  }

  onSubmit(): void {
    if (!this.modalCompanyId()) return;

    this.isUploading.set(true);
    const files = this.multipleFiles.map(f => f.file);
    this.receiptService.save(files, this.modalCompanyId()!).subscribe({
      next: () => {
        this.closeImportModal();
        this.onFetchAllReceipts();
        this.isUploading.set(false);
        toast.success(this.translate.instant('RECEIPT.TOAST_IMPORT_SUCCESS'), { duration: 3000 });
      },
      error: () => {
        this.isUploading.set(false);
        toast.error(this.translate.instant('RECEIPT.TOAST_IMPORT_ERROR'), { duration: 3000 });
      },
    });
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
    this.isModalOpen.set(true);
  }

  closeImportModal() {
    this.isModalOpen.set(false);
    this.resetAll();
    this.modalCompanyId.set(null);
  }

  editReceipt(id: number) {
    this.router.navigate(['web/receipts/', id]);
  }

  deleteReceipt(id: number) {
    this.receiptService.deleteReceipt(id).subscribe({
      next: () => {
        this.onFetchAllReceipts();
        toast.success(this.translate.instant('RECEIPT.TOAST_DELETE_SUCCESS'), { duration: 3000 });
      },
      error: () => toast.error(this.translate.instant('RECEIPT.TOAST_DELETE_ERROR'), { duration: 3000 }),
    })
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
    return this.page >= ((this.totalPages() ?? 1) - 1);
  }

  readonly Math = Math;
}
