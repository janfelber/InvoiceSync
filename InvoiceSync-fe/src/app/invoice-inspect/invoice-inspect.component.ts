import {Component, OnInit} from '@angular/core';
import {InvoiceDisplay} from '../invoice-inspect-display/invoice-inspect-display.component';
import {InvoiceInspectOcrWarning} from '../invoice-inspect-ocr-warning/invoice-inspect-ocr-warning.component';
import {AxiosService} from "../axios.service";
import {ActivatedRoute} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-invoice-inspect',
  standalone: true,
  imports: [InvoiceDisplay, InvoiceInspectOcrWarning, FormsModule, CommonModule],
  templateUrl: './invoice-inspect.component.html',
  styleUrl: './invoice-inspect.component.css'
})
export class InvoiceInspect implements OnInit {

  constructor(private axiosService: AxiosService, private route: ActivatedRoute) {
  }

  importId: string = '';
  invoice: any = {};

  showAddress: boolean = true;
  showCompanyData: boolean = true;
  showInformationSupplier: boolean = true;

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.importId = params.get('id') || '';
    });
    this.onFetchInvoice();
  }

  onFetchInvoice(): void {
    this.axiosService.request(
      "GET",
      `/api/v1/import/${this.importId}`,
      null
    ).then(response => {
        const data = response.data;
        this.invoice = {
          companyName: data.invoice_company_name,
          companyCity: data.invoice_company_city,
          companyStreet: data.invoice_company_address,
          companyZip: data.invoice_company_zip,
          companyVat: data.invoice_company_vat_number,
          companyIban: data.invoice_company_iban,
          companyRegistration: data.invoice_company_registration_number,
          companyTax: data.invoice_tax_number,
          issueDate: data.invoice_issue_date,
          deliveryDate: data.invoice_delivery_date,
          dueDate: data.invoice_due_date,
          variableSymbol: data.invoice_variable_symbol
        }
      }
    )
  }
}
