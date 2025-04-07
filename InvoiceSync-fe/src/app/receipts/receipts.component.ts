import {Component} from '@angular/core';
import {CurrencyPipe, DatePipe, NgForOf, NgIf} from "@angular/common";
import {HttpErrorResponse} from "@angular/common/http";
import {FileService} from "../file.service";
import {RouterLink} from "@angular/router";
import {initFlowbite} from 'flowbite'
import {FormsModule} from '@angular/forms';
import {ReceiptService} from "../services/receipt.service";
import {CompanyService} from "../services/company.service";

@Component({
  selector: 'app-receipts',
  imports: [
    DatePipe,
    NgForOf,
    RouterLink,
    NgIf,
    FormsModule,
    CurrencyPipe
  ],
  templateUrl: './receipts.component.html',
  styleUrl: './receipts.component.css'
})
export class ReceiptsComponent {

  constructor(
    private fileService: FileService,
    private receiptService: ReceiptService,
    private companyService: CompanyService) {
    this.calculatePages();
  }

  calculatePages() {
    this.pages = [];
    for (let i = 1; i <= this.totalPages; i++) {
      this.pages.push(i);
    }
  }


  changePage(page: number) {
    if (page < 1 || page > this.totalPages) {
      return; // Zabráni zmenám mimo rozsahu
    }
    this.currentPage = page;
  }

  protected companies: any[] = [];
  protected suppliers: any[] = [];
  protected selectedCompanyId: any;
  filteredInvoiceImports: any[] = [];

  currentPage = 1;
  totalPages = 5;
  pages: number[] = [];

  ngOnInit(): void {
    this.onFetchAllReceipts();
    this.onFetchAllCompanies();
    initFlowbite();
  }

  headers = ['ID', 'Dodávateľ', 'Dátum importu', 'Odberateľ', 'IČO', 'DIČ', 'IČ DPH', 'Cena (€)'];

  protected imports: any[] = [];

  selectedCompanyName = 'Vyber spoločnosť';
  selectedSupplier = 'Vyber dodávateľa';
  searchText: string = '';


  onFetchAllReceipts(): void {
    this.receiptService.fetchAllReceipts().then((imports) => {
      this.imports = imports.data;
      this.filteredInvoiceImports = [...this.imports];
      this.getSuppliers();
    });
  }

  onFetchAllCompanies() {
    this.companyService.fetchAllCompanies().then(
      (companies) => {
        this.companies = companies.data
      }
    ).catch(() => {
    });
  }

  // TODO: implement filtering by supplier
  // onChangeSupplier(supplier: any) {
  //   this.selectedSupplier = supplier;
  //   this.filteredInvoiceImports = this.imports.filter(invoice => invoice.partnerName === supplier);
  // }

  getSuppliers() {
    this.suppliers = Array.from(new Set(this.filteredInvoiceImports.map(invoice => invoice.partnerName)));
  }

  searchInvoices() {
    this.filteredInvoiceImports = this.imports.filter(invoice =>
      invoice.partnerName.toLowerCase().includes(this.searchText.toLowerCase()) ||
      invoice.partnerRegistrationNumber.toLowerCase().includes(this.searchText.toLowerCase())
    );
  }

  onFileSelected(): void {
    const inputElement = document.createElement('input');
    inputElement.type = 'file';
    inputElement.accept = '.png';

    inputElement.click();

    inputElement.addEventListener('change', (event: Event) => {
      const target = event.target as HTMLInputElement;
      if (target.files) {
        this.onUploadFiles(Array.from(target.files));
        alert('Files selected');
      }
    });
  }

  public onUploadFiles(files: File[]): void {
    const formData = new FormData();
    for (const file of files) {
      formData.append('file', file, file.name);
    }

    formData.append('companyId', this.selectedCompanyId.toString());

    this.receiptService.uploadReceipt(formData).subscribe({
      next: (event) => {
        console.log(event);
      },
      error: (error: HttpErrorResponse) => {
        console.error(error);
      },
      complete: () => {
        console.log('Upload complete');
      }
    });
  }

  onSelectCompany(company: any) {
    this.selectedCompanyName = company.name;
    this.selectedCompanyId = company.id;
    this.onCompanyChange(this.selectedCompanyId);
  }

  onCompanyChange(companyId: number) {
    this.receiptService.fetchReceiptsByCompany(companyId)
      .then(response => {
        this.filteredInvoiceImports = response.data;
      })
      .catch(error => {
        console.error(error);
      });
  }
}
