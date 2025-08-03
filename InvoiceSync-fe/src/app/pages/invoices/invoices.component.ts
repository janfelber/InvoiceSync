import {AfterViewInit, Component, OnInit} from '@angular/core';
import {FileService} from "../../file.service";
import {InvoiceService} from "./invoice.service";
import {AxiosService} from "../../core/axios.service";
import { HttpErrorResponse } from "@angular/common/http";
import {DatePipe} from "@angular/common";
import { FormsModule } from '@angular/forms';
import { CommonModule } from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {ReactiveFormsModule} from "@angular/forms";
import {MatCheckbox} from "@angular/material/checkbox";
import {Router, RouterLink} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {MatDialogWindowComponent} from "../../shared/mat-dialog-window/mat-dialog-window.component";
import {ToastrService} from "ngx-toastr";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MatTooltip} from "@angular/material/tooltip";
import {initDropdowns, initFlowbite} from "flowbite";

//TODO: filter state
interface ImportItem {
  invoice_status: string;
  company_id: number;
}

export interface Invoice {
  id: number;
  name: string;           // napr. „Faktúra č. 10/2024/123“
  date: string;           // ISO alebo „DD.MM.YYYY“
  dueDate: string;
  amount: number;         // v € – pre demo stačí number
  status: 'PAID' | 'UNPAID' | 'PARTIAL';
  var: string;
  supplier: string;
  price: string;
  type: string;
}

@Component({
    selector: 'app-test',
  imports: [
    ReactiveFormsModule,
    FormsModule,
    CommonModule,
    RouterLink,
  ],
    templateUrl: './invoices.component.html',
    styleUrl: './invoices.component.css'
})
export class Invoices implements OnInit, AfterViewInit{

  protected filenames: string[] = [];

  protected fileStatus = { status: '', requestType: '', percentage: 0 };

  protected imports: ImportItem[] = [];

  protected isLoading: boolean = true;

  protected selectedCompanyId: any;

  protected schema_name: string = '';

  protected companies: any[] = [];

  protected hasInvoicesDashboardFeature: boolean = false;

  //TODO: filter state
  activeTab: string = 'UNPROCESSED';

  protected demo: boolean = false;

  selectedCompanyName = 'Vyber spoločnosť';

  headers = ['Status', 'Cislo Faktury', 'Var. Symbol', 'Importovane', 'Dat. Vystavenia /Splat.', 'Suma total'];
  headers1 = [
    { name: 'Status', width: '2.5%' },
    { name: 'Firma', width: '7%'},
    { name: 'Cislo Faktury', width: '5%' },
    { name: 'Var. Symbol', width: '3%' },
    { name: 'Importovane', width: '3%' },
    { name: 'Dat. Vystavenia /Splat.', width: '4%' },
    // { name: 'Suma total', width: '3%'}

  ];
  filteredInvoiceImports: any[] = [];
  currentPage = 1;
  rowsPerPage = 100;
  currentPageInput = 1;
  pageSizes = [20, 50];

  constructor(
    private fileService: FileService,
    private invoiceService: InvoiceService,
    private axiosService: AxiosService,
    private toastr: ToastrService,
    private dialog: MatDialog,
    private router: Router,
  ) {
  }


  ngOnInit(): void {
    this.onFetchAllImports();
    this.onFetchCompanies();
    initFlowbite();
  }

  ngAfterViewInit() {
    // prvý render
    initDropdowns();
  }

  editInvoice(id: number) {
    this.router.navigate(['/invoices/edit', id]);
  }

  previewInvoice(id: number) {
    this.router.navigate(['web/receipts/', id]);
  }

  invoices: Invoice[] = [
    {
      "id": 78,
      "name": "Faktúra č. 10/2024/123",
      "date": "2024-05-15",
      "dueDate": "2024-06-14",
      "amount": 281.02,
      "status": "UNPAID",
      "var": "231000602",
      "supplier": "CANIS SAFETY a.s.",
      "price": "281.02",
      "type": "EXPENSE"
    },
    {
      "id": 77,
      "name": "Faktúra č. 11/2024/567",
      "date": "2024-05-18",
      "dueDate": "2024-06-17",
      "amount": 950.00,
      "status": "PAID",
      "var": "241100567",
      "supplier": "MONMARKO s.r.o.",
      "price": "950.00",
      "type": "EXPENSE"
    },
    {
      "id": 76,
      "name": "Faktúra č. 12/2024/890",
      "date": "2024-05-20",
      "dueDate": "2024-06-19",
      "amount": 1200.75,
      "status": "PARTIAL",
      "var": "250890001",
      "supplier": "PA-MI s.r.o.",
      "price": "1200.75",
      "type": "REVENUE"
    }
  ]

  toggleSelectAll(event: any): void {
    const isChecked = event.checked;
    this.paginatedImports.forEach((importItem) => {
      importItem.selected = isChecked;
    });
  }

  //TODO: filter state
  filterImports(status: string): void {
    if (this.selectedCompanyId) {
      this.filteredInvoiceImports = this.imports.filter(
        (importItem: ImportItem) =>
          importItem.invoice_status === status &&
          importItem.company_id === this.selectedCompanyId
      );
    } else {
      this.filteredInvoiceImports = this.imports.filter(
        (importItem: ImportItem) => importItem.invoice_status === status
      );
    }
    this.activeTab = status;
  }

  //TODO: filter state
  allImports(): void {
    if (this.selectedCompanyId) {
      this.filteredInvoiceImports = this.imports.filter(
        (importItem: ImportItem) => importItem.company_id === this.selectedCompanyId
      );
    } else {
      this.filteredInvoiceImports = this.imports;
    }
    this.activeTab = 'all';
  }

  getInvoiceStatus(status: string): string {
    switch (status) {
      case 'UNPROCESSED' :
        return 'Nespracovaná';
      case 'PROCESSED':
        return 'Spracovaná';
      default:
        return 'Neznama'
    }
  }

  onRowCheckboxChange(importItem: any): void {
    console.log(`Checkbox changed for import:`, importItem.id);
  }

  onFetchAllImports(): void {
    this.axiosService.request(
      "GET",
      "/api/v1/import/user",
      null
    ).then(
      (imports) => {
        this.imports = imports.data;
        this.filteredInvoiceImports = [...this.imports];
        console.log(imports.data);
        setTimeout(() => {
          this.isLoading = false;
        }, 3000);
      }
    ).catch((error) => {
      console.error("Error fetching imports:", error);
      this.isLoading = false;
    });
  }

  get paginatedImports(): any[] {
    const start = (this.currentPage - 1) * this.rowsPerPage;
    const end = start + this.rowsPerPage;
    return this.filteredInvoiceImports.slice(start, end);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredInvoiceImports.length / this.rowsPerPage);
  }

  get recordRange(): string {
    const startRecord = (this.currentPage - 1) * this.rowsPerPage + 1;
    const endRecord = Math.min(this.currentPage * this.rowsPerPage, this.filteredInvoiceImports.length);
    return `${startRecord} - ${endRecord} z ${this.filteredInvoiceImports.length}`;
  }

  goToPage(page: number): void {
    if (page > 0 && page <= this.totalPages) {
      this.currentPage = page;
      this.currentPageInput = page;
    }
  }

  jumpToPage(): void {
    if (this.currentPageInput > 0 && this.currentPageInput <= this.totalPages) {
      this.goToPage(this.currentPageInput);
    }
  }

  openDialog() {
    const dialogRef = this.dialog.open(MatDialogWindowComponent, {
      width: '400px',
      panelClass: 'custom-dialog-container',
      data: {
        title: 'Nova spolocnost',
        inputs: [
          { label: 'Názov', type: 'text', placeholder: '', value: '', required: true },
        ],
        confirmText: 'Uložiť',
        cancelText: 'Zrušiť'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Dialóg zatvorený s výsledkom:', result);
        this.saveCompany(result);
      } else {
        console.log('Dialóg bol zrušený');
      }
    });
  }

  saveCompany(data: any): void {
    const valuesToSend = data.map((input: { value: any }) => input.value);

    console.log('Posielam údaje na server:', valuesToSend);

    this.axiosService.request(
      "POST",
      `/api/v1/company/add`,
      { name: valuesToSend[0] }
    ).then(response => {
      console.log('Spoločnosť bola úspešne vytvorená:', response);
      this.onFetchCompanies();
    }).catch(error => {
      console.error('Chyba pri vytváraní spoločnosti:', error);
    });
  }

  /**
   * @deprecated This method is deprecated and will be removed in future versions this should use new api approach
   */
  onFetchImports(): void {
    this.isLoading = true;
    this.invoiceService.fetchImports(this.selectedCompanyId).subscribe(
      imports => {
        this.imports = imports;
        this.isLoading = false;
        console.table(imports);
      },
      error => {
        console.log(error);
        this.isLoading = false;
      }
    );
  }

  onFetchCompanies() {
    this.axiosService.request(
      "GET",
      `/api/v1/company/user`,
      null
    ).then(
      (companies) => {
        this.companies = companies.data
        this.isLoading = false;
        console.log(companies.data);

        if (this.companies.length === 0) {
          this.toastr.warning('Nemáte pridané žiadne spoločnosti!', 'Informácia',
            {
              timeOut: 3000,
              progressBar: true,
              progressAnimation: 'increasing',
              closeButton: true,
              positionClass: 'toast-top-right'
            });
        }
      }
    ).catch(() => {
      this.isLoading = false;
    });
  }

  downloadFile(importId: number) {
    this.axiosService.request('GET', `/file/generateZip/${importId}`, null, { responseType: 'blob' })
      .then((response) => {
        const blob = new Blob([response.data]);
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'output.zip';
        a.click();
        window.URL.revokeObjectURL(url);
      })
      .catch(error => {
        console.error('Download error:', error);
      });
  }

  onFileSelected(): void {
    const inputElement = document.createElement('input');
    inputElement.type = 'file';
    inputElement.accept = '.pdf';

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
      formData.append('companyId', this.selectedCompanyId.toString());
    }
    this.fileService.uploadPdf(formData).subscribe({
      next: (event) => {
        console.log(event);
        this.onCompanyChange({ id: this.selectedCompanyId });
      },
      error: (error: HttpErrorResponse) => {
        console.error(error);
      },
      complete: () => {
        console.log('Upload complete');
      }
    });
  }

  private updateStatus(loaded: number, total: number, requestType: string) {
    this.fileStatus.status = 'progress';
    this.fileStatus.requestType = requestType;
    this.fileStatus.percentage = Math.round(100 * loaded / total);
  }

  openFileDialog() {
    const inputElement = document.createElement('input');
    inputElement.type = 'file';
    inputElement.accept = '.xml';

    inputElement.click();

    inputElement.addEventListener('change', (event: Event) => {
      const target = event.target as HTMLInputElement;
      if (target.files) {
        this.onUploadFiles(Array.from(target.files));
        alert('Files selected');
      }
    });
  }

  //select company from dropdown
  onSelectCompany(company: any) {
    this.selectedCompanyName = company.name;
    this.selectedCompanyId = company.id;
    console.log(this.selectedCompanyName)
    this.onCompanyChange(company);
  }

  onCompanyChange(company: any) {
    this.axiosService.request(
      "GET",
      `/api/v1/imports/company/${company.id}/current-user`,
      null,
    ).then(response => {
      //update imports
      this.filteredInvoiceImports = response.data;
    }).catch(error => {
      console.error(error);
    });
  }


  reloadImports() {
    this.onFetchImports();
  }

}
