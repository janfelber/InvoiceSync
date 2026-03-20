import {AfterViewInit, Component, OnInit} from '@angular/core';
import {AxiosService} from "../../core/axios.service";
import {DatePipe} from "@angular/common";
import {FormsModule} from '@angular/forms';
import {CommonModule} from "@angular/common";
import {ReactiveFormsModule} from "@angular/forms";
import {Router, RouterLink} from "@angular/router";
import {initDropdowns, initFlowbite} from "flowbite";
import {InvoiceService} from "../../core/services/invoice.service";
import {PageInvoiceResponse} from "./page-response-receipt-response";
import {CompanyService} from "../../core/services/company.service";
import {PageResponseCompanyResponseDto} from "../../core/models/page-response-company-response-dto";

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
export class Invoices implements OnInit, AfterViewInit {

  selectedIds = new Set<number>();

  uploadModalOpen = false;
  isUploading = false;
  importFinished = false;
  filterOpen = true;

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

  public invoiceResponse: PageInvoiceResponse = {
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
    private axiosService: AxiosService,
    private router: Router,
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
    this.invoiceService.findAllInvoicesByUser({
      page: this.page,
      size: this.size
    })
      .then(response => {
        this.invoiceResponse = response.data;
        console.log(this.invoiceResponse);
        setTimeout(() => initFlowbite(), 0);
      })
      .catch(error => {
        console.error("Chyba: ", error);
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

  onFetchCompanies() {
    this.companyService.findAllCompaniesByUser().then(response => {
      this.companyResponse = response.data;
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

  async onUploadFile(): Promise<void> {

    console.log(this.selectedCompanyId)

    this.isUploading = true;
    this.importFinished = false;
    try {
      if (this.selectedFile) {
        await this.invoiceService.uploadInvoice({
          file: this.selectedFile,
          companyId: this.selectedCompanyId
        })
      }
      this.closeUploadModal();
      this.onFetchAllInvoices();
    } catch (err) {
      console.error(err);
    } finally {
      this.isUploading = false;
      this.importFinished = true;
    }
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
    this.invoiceService.findAllInvoicesByCompany({
      page: this.page,
      size: this.size,
      companyId: company.id
    }).then(response => {
      this.invoiceResponse = response.data;
    }).catch(error => {
      console.error(error);
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
    return this.page >= ((this.invoiceResponse?.totalPages ?? 1) - 1);
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
    return this.invoiceResponse.content.length > 0 &&
      this.invoiceResponse.content.every(i => i.id != null && this.selectedIds.has(i.id));
  }

  toggleSelectAll(): void {
    if (this.allSelected) {
      this.invoiceResponse.content.forEach(i => { if (i.id != null) this.selectedIds.delete(i.id); });
    } else {
      this.invoiceResponse.content.forEach(i => { if (i.id != null) this.selectedIds.add(i.id); });
    }
  }

  clearSelection(): void {
    this.selectedIds.clear();
  }

  downloadSelectedPdfs() {
    this.invoiceService.bulkDownloadInvoices(this.selectedIds)
      .then((response) => {
        const blob = new Blob([response.data]);
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'invoices.zip';
        a.click();
        window.URL.revokeObjectURL(url);
      })
      .catch(error => {
        console.error('Download error:', error);
      });
  }

  protected readonly Math = Math;
}
