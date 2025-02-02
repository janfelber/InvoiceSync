import {Component, OnInit} from '@angular/core';
import {InvoiceDisplay} from '../invoice-inspect-display/invoice-inspect-display.component';
import {InvoiceInspectOcrWarning} from '../invoice-inspect-ocr-warning/invoice-inspect-ocr-warning.component';
import {AxiosService} from "../axios.service";
import {ActivatedRoute} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {InvoiceService} from "../page/invoices/invoice.service";
import {AuthenticationService} from "../services/authentication.service";
import {InvoiceRequest} from "../models/invoice-request";

@Component({
  selector: 'app-invoice-inspect',
  standalone: true,
  imports: [InvoiceDisplay, InvoiceInspectOcrWarning, FormsModule, CommonModule],
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

  showAddress: boolean = true;
  showCompanyData: boolean = true;
  showInformationSupplier: boolean = true;
  invoiceRequest: InvoiceRequest = {};


  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.importId = params.get('id') || '';
    });
    this.route.queryParams.subscribe(params => {
      this.companyId = params['companyId'];
      console.log("Company ID:", this.companyId);
    });
    this.onFetchInvoice();
    this.infoCompany();
  }

  exportIssued() {

    this.invoiceRequest = {
      invoiceDetails: {
        invoiceType: 'issuedInvoice',
        invoiceNumber: this.invoice.invoiceNumber,
        variableSymbol: this.invoice.variableSymbol,
        pairingSymbol: this.invoice.variableSymbol,
        dateInvoice: this.invoice.issueDate,
        dateTax: this.invoice.issueDate,
        dateDue: this.invoice.dueDate,
        dateAccounting: this.invoice.issueDate
      },
      partner: this.partner,
      items: this.invoice.items || [],
      myIdentity: this.myIdentity
    };

    console.log(this.invoiceRequest);

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

  infoCompany() {
    this.axiosService.request(
      "GET",
      `/api/v1/company/info/${this.companyId}`,
      null
    ).then(response => {
      this.myIdentity = response.data;
    })
  }

  onFetchInvoice(): void {
    this.axiosService.request(
      "GET",
      `/api/v1/import/${this.importId}`,
      null
    ).then(response => {
        const data = response.data;
        this.invoice = {
          partnerName: data.partner.name,
          partnerCity: data.partner.city,
          partnerStreet: data.partner.street,
          partnerZip: data.partner.zip,
          partnerVatId: data.partner.vatId,
          issueDate: data.invoiceDetails.issueDate,
          dueDate: data.invoiceDetails.dueDate,
          variableSymbol: data.invoiceDetails.variableSymbol
        }
        this.partner = data.partner
      }
    )
  }
}
