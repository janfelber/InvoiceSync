import {AfterViewInit, Component, OnInit} from '@angular/core';
import {FileService} from "../../file.service";
import {AxiosService} from "../../core/axios.service";
import {HttpErrorResponse} from "@angular/common/http";
import {DatePipe} from "@angular/common";
import {FormsModule} from '@angular/forms';
import {CommonModule} from "@angular/common";
import {ReactiveFormsModule} from "@angular/forms";
import {Router, RouterLink} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {initDropdowns, initFlowbite} from "flowbite";
import {InvoiceService} from "../../core/services/invoice.service";
import {PageInvoiceResponse} from "./page-response-receipt-response";

@Component({
  selector: 'app-test',
  imports: [
    DatePipe,
    ReactiveFormsModule,
    FormsModule,
    CommonModule,
    RouterLink,
  ],
  templateUrl: './invoices.component.html',
  styleUrl: './invoices.component.css'
})
export class Invoices implements OnInit, AfterViewInit {

  protected isLoading: boolean = true;

  protected selectedCompanyId: any;

  protected companies: any[] = [];

  selectedCompanyName = 'Vyber spoločnosť';
  filteredInvoiceImports: any[] = [];

  public page: number = 0;
  public size: number = 20;

  public invoiceResponse: PageInvoiceResponse = {
    content: []
  };

  constructor(
    private fileService: FileService,
    private invoiceService: InvoiceService,
    private axiosService: AxiosService,
    private toastr: ToastrService,
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

  onRowCheckboxChange(importItem: any): void {
    console.log(`Checkbox changed for import:`, importItem.id);
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

  public onUploadFiles(files: File[]): void {
    const formData = new FormData();
    for (const file of files) {
      formData.append('file', file, file.name);
      formData.append('companyId', this.selectedCompanyId.toString());
    }
    this.fileService.uploadPdf(formData).subscribe({
      next: (event) => {
        console.log(event);
        this.onCompanyChange({id: this.selectedCompanyId});
      },
      error: (error: HttpErrorResponse) => {
        console.error(error);
      },
      complete: () => {
        console.log('Upload complete');
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

}
