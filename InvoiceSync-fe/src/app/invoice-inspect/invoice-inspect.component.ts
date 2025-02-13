import {Component, OnInit} from '@angular/core';
import {AxiosService} from "../axios.service";
import {ActivatedRoute} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {InvoiceService} from "../page/invoices/invoice.service";
import {InvoiceRequest} from "../models/invoice-request";

@Component({
  selector: 'app-invoice-inspect',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './invoice-inspect.component.html',
  styleUrl: './invoice-inspect.component.css'
})
export class InvoiceInspect implements OnInit {

  constructor(
    private axiosService: AxiosService,
    private route: ActivatedRoute,
    private invoiceService: InvoiceService
  ) {
  }

  importId: string = '';
  companyId: any = null;
  invoice: any = {};
  partner: any = {};
  myIdentity: any = {};
  items: any = [];

  invoiceRequest: InvoiceRequest = {};


  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.importId = params.get('id') || '';
    });
    this.onFetchInvoice();
  }

  async exportIssued() {
    await this.infoCompany();

    this.invoiceRequest = {
      invoiceDetails: {
        invoiceType: 'receivedInvoice',
        invoiceNumber: this.invoice.invoiceNumber,
        variableSymbol: this.invoice.variableSymbol,
        pairingSymbol: this.invoice.variableSymbol,
        dateInvoice: this.invoice.issueDate,
        dateTax: this.invoice.issueDate,
        dateDue: this.invoice.dueDate,
        dateAccounting: this.invoice.issueDate
      },
      partner: {
        name: this.partner.name,
        city: this.partner.city,
        street: this.partner.street,
        zip: this.partner.zip,
        registrationNumber: this.partner.registrationNumber,
        taxId: this.partner.taxId,
        vatId: this.partner.vatId
      },
      items: this.items,
      myIdentity: this.myIdentity
    };

    this.invoiceService.exportPohodaInvoice(this.invoiceRequest).subscribe({
      next: (response) => {
        const blob = new Blob([response], {type: 'application/xml'});
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'invoice.xml';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Chyba pri exportovaní faktúry', error);
      }
    });
  }

  async infoCompany() {
    try {
      const response = await this.axiosService.request(
        "GET",
        `/api/v1/company/info/${this.companyId}`,
        null
      );
      this.myIdentity = response.data;
    } catch (error) {
      console.error('Error fetching company info', error);
    }
  }

  getTotalPrice(): number {
    return this.items.reduce((total: number, item: any) => total + (item.price + item.priceVAT), 0);
  }

  getTotalSumWithoutVat(): number {
    return this.items.reduce((total: number, item: any) => total + item.price, 0);
  }

  onFetchInvoice(): void {
    this.axiosService.request(
      "GET",
      `/api/v1/import/${this.importId}`,
      null
    ).then(response => {
        const data = response.data;
        this.companyId = data.company;
        this.invoice = {
          issueDate: data.invoiceDetails.issueDate,
          deliveryDate: data.invoiceDetails.taxDate,
          dueDate: data.invoiceDetails.dueDate,
          variableSymbol: data.invoiceDetails.variableSymbol,
        }
        this.partner = data.partner;
      }
    )
  }
}
