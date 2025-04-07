import {Component, OnInit} from '@angular/core';
import {NgClass, NgForOf} from "@angular/common";
import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {DecimalPipe, NgClass, NgForOf, NgStyle} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {AxiosService} from "../axios.service";
import {ReceiptRequest} from "../models/receipt-request";
import {InvoiceService} from "../page/invoices/invoice.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-receipt-details',
  imports: [
    NgForOf,
    ReactiveFormsModule,
    FormsModule,
    NgClass
  ],
  templateUrl: './receipt-details.component.html',
  styleUrl: './receipt-details.component.css'
})
export class ReceiptDetailsComponent implements OnInit {
  constructor(
    private axiosService: AxiosService,
    private route: ActivatedRoute,
    private invoiceService: InvoiceService,
    private toastr: ToastrService,
  ) {
  }

  receiptId: any = null;
  companyId: any = null;
  @ViewChild('successToast') successToast!: ElementRef;


  receipt: any = {};
  items: ReceiptRequest['items'] = [];
  partner: any = {};
  myIdentity: any = {};

  receiptRequest: ReceiptRequest = {};

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.receiptId = params.get('id') || '';
    });
    this.onFetchReceipt();
  }

  async exportReceipt() {
    await this.myIdentityCompany();

    this.receiptRequest = {
      receiptDetails: {
        numberRequested: 'test',
        date: this.receipt.date,
        datePayment: this.receipt.datePayment,
        dateTax: this.receipt.dateTax,
        accountValue: this.receipt.accountValue,
        classificationVAT: this.receipt.classificationVAT,
        classificationKVVAT: this.receipt.classificationKVVAT,
        description: this.receipt.description
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
      myIdentity: this.myIdentity,
      items: this.items
      };

    console.log(this.receiptRequest)

    this.invoiceService.exportPohodaReceipt(this.receiptRequest).subscribe({
      next: (response) => {
        const blob = new Blob([response], {type: 'application/xml'});
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'receipt.xml';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);

        console.log(response)
        this.toastr.success('Export uspesny', 'Informácia',
          {
            timeOut: 1500,
            progressBar: true,
            progressAnimation: 'increasing',
            closeButton: true,
            positionClass: 'toast-top-right',
          });
      },
      error: (error) => {
        console.error('Chyba pri exportovaní faktúry', error);
      }
    });
  }

  async updateReceipt() {
    await this.myIdentityCompany();

    this.receiptRequest = {
      receiptDetails: {
        numberRequested: 'test',
        date: this.receipt.date,
        datePayment: this.receipt.datePayment,
        dateTax: this.receipt.dateTax,
        accountValue: this.receipt.accountValue,
        classificationVAT: this.receipt.classificationVAT,
        classificationKVVAT: this.receipt.classificationKVVAT,
        description: this.receipt.description
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
      items: this.items
      }

    console.log(this.receiptRequest)

    this.invoiceService.updateReceipt(this.receiptRequest, this.receiptId).subscribe({
      next: (response) => {
        console.log(response)
          this.toastr.success('Bloček úspešne upravený', 'Informácia',
            {
              timeOut: 1500,
              progressBar: true,
              progressAnimation: 'increasing',
              closeButton: true,
              positionClass: 'toast-top-right',
            });
      },
      error: (error) => {
        console.error('Chyba pri exportovaní faktúry', error);
      }
    });
  }

  async myIdentityCompany() {
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

  onFetchReceipt() {
    this.axiosService.request(
      'GET',
      `/api/v1/receipt/${this.receiptId}`,
      null,
    ).then(response => {
      const data = response.data;
      console.log(data);
      this.companyId = data.company;
      this.receipt = data.receiptDetails;
      this.partner = data.partner;
      this.items = data.items;
    });
  }
}
