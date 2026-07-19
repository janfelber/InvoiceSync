import {AfterViewInit, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {AxiosService} from "../../core/axios.service";
import {DatePipe} from "@angular/common";
import {FormsModule} from '@angular/forms';
import {CommonModule} from "@angular/common";
import {ReactiveFormsModule} from "@angular/forms";
import {Router, RouterLink} from "@angular/router";
import {initDropdowns, initFlowbite} from "flowbite";
import {InvoiceService} from "../../core/services/invoice.service";
import {CompanyService} from "../../core/services/company.service";
import {PageResponseCompanyResponseDto} from "../../core/models/page-response-company-response-dto";
import {toast} from "ngx-sonner";
import {InvoiceBulkChangeService} from "../../core/services/bulk/invoice-bulk-change.service";
import {createEmptyPage, PageResponse} from "../../core/models/page-response";
import {InvoiceResponseTable} from "./invoice-response-table";
import {finalize} from "rxjs";

@Component({
  selector: 'app-test',
  imports: [
    DatePipe,
    ReactiveFormsModule,
    FormsModule,
    CommonModule,
    RouterLink
  ],
  templateUrl: './invoices.component.html',
  styleUrl: './invoices.component.css'
})
export class Invoices implements OnInit {

  public invoiceResponse: PageResponse<InvoiceResponseTable> =
    createEmptyPage<InvoiceResponseTable>(10);

  selectedIds = new Set<number>();
  isBulkView = false;
  selectedAction: string | null = null;
  bulkActionDropdownOpen = false;

  uploadModalOpen = false;
  isUploading = false;
  importFinished = false;
  filterOpen = true;
  public loading = false;
  public error: string | null = null;

  selectedFile: File | null = null;

  // @ts-ignore
  protected selectedCompanyId: 0;
  public page: number = 0;
  public size: number = 10;

  selectedCompanyName = 'Všetky spoločnosti';

  activeTab = 'all';
  tabs = [
    { id: 'all', label: 'All Invoices' },
    { id: 'issued', label: 'Issued Invoices' },
    { id: 'received', label: 'Received Invoices' }
  ];

  public companyResponse: PageResponseCompanyResponseDto = {
    content: []
  };

  selectedStatus = '';
  selectedStatusLabel = 'Všetky';
  statusOptions = [
    { value: '', label: 'Všetky' },
    { value: 'Processed', label: 'Spracované' },
    { value: 'Unprocessed', label: 'Nespracované' },
  ];

  constructor(
    private companyService: CompanyService,
    private invoiceService: InvoiceService,
    private invoiceBulkChangeService: InvoiceBulkChangeService,
    private axiosService: AxiosService,
    private router: Router,
    private readonly cdr: ChangeDetectorRef,
  ) {
  }


  ngOnInit(): void {
    this.onFetchAllInvoices();
    this.onFetchCompanies();
  }

  ngAfterViewInit(): void {
    initDropdowns();
  }

  getStatusClasses(status: string | undefined): string {
    switch (status) {
      case 'Processed':
        return 'bg-green-100 text-green-800';
      case 'Unprocessed':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  getStatusText(status: string | undefined): string {
    switch (status) {
      case 'Processed':
        return 'Spracované';
      case 'Unprocessed':
        return 'Nepracované';
      default:
        return 'Neznámy stav';
    }
  }

  editInvoice(id: number) {
    this.router.navigate(['/invoices/edit', id]);
  }

  previewInvoice(id: number) {
    this.router.navigate(['web/receipts/', id]);
  }

  toggleFilter(): void {
    this.filterOpen = !this.filterOpen;
  }

  onSelectStatus(s: { value: string; label: string }): void {
    this.selectedStatus = s.value;
    this.selectedStatusLabel = s.label;
  }

  onFetchAllInvoices(): void {
    this.loading = true;
    this.error = null;

    this.invoiceService.findAllInvoicesByUser({
      page: this.page,
      size: this.size
      })
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: response => {
          this.invoiceResponse = response;
          this.cdr.detectChanges();
        },
        error: () => {
          this.error = 'There was an error fetching invoices.';
        }
      });
  }

  saveCompany(data: any): void {
    const valuesToSend = data.map((input: { value: any }) => input.value);

    console.log('Posielam údaje na server:', valuesToSend);

    this.axiosService.request(
      "POST",
      `/api/v1/company/add`,
      {name: valuesToSend[0]}
    ).then(response => {
      console.log('Spoločnosť bola úspešne vytvorená:', response);
      this.onFetchCompanies();
    }).catch(error => {
      console.error('Chyba pri vytváraní spoločnosti:', error);
    });
  }

  // TODO IS-225 migrate this to use http client instead of axios
  onFetchCompanies() {
    this.companyService.findAllCompaniesByUser().then(response => {
      this.companyResponse = response.data;
      this.cdr.detectChanges();
    }).catch(error => {
      console.error(error);
    });
  }

  openUploadModal() {
    this.uploadModalOpen = true;
  }

  closeUploadModal() {
    this.uploadModalOpen = false;
  }

  onInvoiceUpload(event: any): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  setActiveTab(tabId: string) {
    this.activeTab = tabId;
  }


  // TODO IS-12 when implementing AI extraction of value from invoice migrate this to use http instead of axios
  onUploadFile(): void {
    if (!this.selectedFile) {
      return;
    }

    this.isUploading = true;
    this.importFinished = false;

    this.invoiceService.uploadInvoice({
      file: this.selectedFile,
      companyId: this.selectedCompanyId
    })
      .pipe(
        finalize(() => {
          this.isUploading = false;
          this.importFinished = true;
        })
      )
      .subscribe({
        next: () => {
          this.closeUploadModal();
          this.onFetchAllInvoices();
        },
        error: error => {
          console.error('Invoice upload failed:', error);
        }
      });
  }

  //select company from dropdown
  onSelectCompany(company: any) {
    this.selectedCompanyName = company.name;
    this.selectedCompanyId = company.id;
    if (company.id === 0) {
      this.onFetchAllInvoices();
    } else {
      this.onCompanyChange(company);
    }
  }

  onCompanyChange(company: any) {
    this.loading = true;
    this.error = null;

    this.invoiceService.findAllInvoicesByCompany({
      page: this.page,
      size: this.size,
      companyId: company.id
      })
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: response => {
          this.invoiceResponse = response;
          this.cdr.detectChanges();
        },
        error: () => {
          this.error = 'There was an error fetching invoices.';
        }
      });
  }

  goToPreviousPage() {
    this.page--;
    this.clearSelection();
    this.onFetchAllInvoices();
  }

  goToPage(page: number) {
    this.page = page;
    this.clearSelection();
    this.onFetchAllInvoices();
  }

  goToNextPage() {
    this.page++;
    this.clearSelection();
    this.onFetchAllInvoices();
  }

  get IsLastPage(): boolean {
    return this.page >= ((this.invoiceResponse.totalPages ?? 1) - 1);
  }

  onRowCheckboxChange(id: number | undefined): void {
    if (id == null) return;
    if (this.selectedIds.has(id)) {
      this.selectedIds.delete(id);
    } else {
      this.selectedIds.add(id);
    }
  }

  get allSelected(): boolean {
    const invoices = this.invoiceResponse.content;
    return invoices.length > 0 &&
      invoices.every(i => i.id != null && this.selectedIds.has(i.id));
  }

  toggleSelectAll(): void {
    const invoices = this.invoiceResponse.content;
    if (this.allSelected) {
      invoices.forEach(i => { if (i.id != null) this.selectedIds.delete(i.id); });
    } else {
      invoices.forEach(i => { if (i.id != null) this.selectedIds.add(i.id); });
    }
  }

  clearSelection(): void {
    this.selectedIds.clear();
  }

  downloadSelectedPdfs() {
    if (this.selectedIds.size === 0) {
      return;
    }

    this.invoiceBulkChangeService.bulkDownloadInvoices([...this.selectedIds])
      .subscribe({
        next: (blob: Blob) => {
          const url = URL.createObjectURL(blob);
          const link = document.createElement('a');

          link.href = url;
          link.download = 'invoices.zip';
          link.click();

          URL.revokeObjectURL(url);
        },
        error: (error) => {
          console.error('Download error:', error);
          toast.error('Faktúry sa nepodarilo stiahnuť.');
        }
      });
  }

  get canConfirmBulk(): boolean {
    return this.selectedAction !== null;
  }

  get selectedInvoices() {
    return this.invoiceResponse.content.filter(r => this.selectedIds.has(r.id));
  }

  selectBulkAction(action: string): void {
    this.selectedAction = action;
    this.bulkActionDropdownOpen = false;
  }

  resetBulkAction(): void {
    this.selectedAction = null;
  }

  goBack(): void {
    this.isBulkView = false;
    this.selectedAction = null;
    this.bulkActionDropdownOpen = false;
  }

  goToBulkView(): void {
    this.selectedAction = null;
    this.bulkActionDropdownOpen = false;
    this.isBulkView = true;
  }

  applyBulkAction(): void {
    if (!this.selectedAction) return;
    const ids = [...this.selectedIds];
    const count = ids.length;
    const action = this.selectedAction;
    this.invoiceBulkChangeService.executeBulkChange(action as any, ids)
      .subscribe({
        next: () => {
          this.selectedIds.clear();
          this.goBack();
          this.onFetchAllInvoices();

          toast.success(
            `Deleted ${count} invoice${count !== 1 ? 's' : ''}`,
            { duration: 3000 }
          );
        },
        error: (error) => {
          console.error('Bulk action failed:', error);
          toast.error('Akcia zlyhala. Skúste znova.');
        }
      });
  }

  protected readonly Math = Math;
}
