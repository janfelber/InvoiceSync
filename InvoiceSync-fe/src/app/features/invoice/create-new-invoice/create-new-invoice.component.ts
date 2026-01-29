import {Component, OnInit} from '@angular/core';
import {DecimalPipe, NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {initFlowbite} from 'flowbite'
import {CompanyService} from "../../../core/services/company.service";
import {PageResponseCompanyResponseDto} from "../../../core/models/page-response-company-response-dto";
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';
import {toast} from "ngx-sonner";

interface SupplierDraft {
  name: string;
  address: string;
  registrationNumber: string;
  taxId: string;
  vatId: string;
}

interface RecipientDraft {
  name: string;
  address: string;
  registrationNumber: string;
  taxId: string;
  vatId: string;
}

@Component({
  selector: 'app-create-new-invoice',
  imports: [
    DecimalPipe,
    FormsModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './create-new-invoice.component.html',
  styleUrl: './create-new-invoice.component.css'
})
export class CreateNewInvoice implements OnInit {

  public companyResponse: PageResponseCompanyResponseDto = {
    content: []
  };

  subtotal = 0;
  tax = 0;
  shipping = 0;
  total = 0;

  supplierDraft: SupplierDraft = {
    name: '',
    address: '',
    registrationNumber: '',
    taxId: '',
    vatId: ''
  };

  recipientDraft: RecipientDraft = {
    name: '',
    address: '',
    registrationNumber: '',
    taxId: '',
    vatId: ''
  }

  invoice = {
    invoiceNumber: '',
    variableSymbol: '',
    dateIssued: '',
    dateDue: '',
    dateTax: '',
    supplier: {
      name: '',
      address: '',
      ico: '',
      dic: '',
      icDph: ''
    },
    customer: {
      name: '',
      address: '',
      registrationNumber: '',
      taxId: '',
      vatId: ''
    },
    items: [],
    totalWithoutTax: 500,
    totalTax: 100,
    totalWithTax: 600,
    note: ''
  }

  items: {
    name: string;
    quantity: number;
    unit: string;
    unitPrice: number;
    taxRate: number;
    totalWithoutTax: number;
    taxAmount: number;
    totalWithTax: number;
  }[] = [];


  constructor(
    private companyService: CompanyService
  ) {
  }

  ngOnInit(): void {
    initFlowbite();
    this.onFetchCompanies();
  }

  onFetchCompanies() {
    this.companyService.findAllCompaniesByUser().then(response => {
      this.companyResponse = response.data;
      console.log(this.companyResponse.content)
    }).catch(error => {
      console.error(error);
    });
  }

  exportPdf() {
    const invoiceElement = document.getElementById('invoice');
    if (!invoiceElement) {
      console.error('Element s id "invoice" nebol nájdený');
      return;
    }

    // @ts-ignore
    html2canvas(invoiceElement, {scale: 1, useCORS: true}).then(canvas => {
      const imgData = canvas.toDataURL('image/png');
      const pdf = new jsPDF('p', 'mm', 'a4');
      const pdfWidth = pdf.internal.pageSize.getWidth();
      const pdfHeight = pdf.internal.pageSize.getHeight();

      const imgPropsWidth = canvas.width;
      const imgPropsHeight = canvas.height;

      const margin = 0;
      const usableWidth = pdfWidth;
      let imgHeight = (imgPropsHeight * usableWidth) / imgPropsWidth;
      let imgWidth = usableWidth;

      if (imgHeight > pdfHeight) {
        imgHeight = pdfHeight;
        imgWidth = (imgPropsWidth * imgHeight) / imgPropsHeight;
      }

      pdf.addImage(imgData, 'PNG', margin, margin, imgWidth, imgHeight);
      pdf.save('invoice.pdf');
    }).catch(err => {
      console.error('Chyba pri generovaní PDF', err);
    });
  }

  findCompanyByRegistrationNumberSupplier(registrationNumber: string) {
    this.companyService.findByRegistrationNumber({
      registrationNumber: registrationNumber,
    }).then(response => {
      const company = response.data;

      this.supplierDraft = {
        name: company.name,
        address: company.address,
        registrationNumber: company.registrationNumber,
        taxId: company.taxId,
        vatId: company.vatId
      };
    }).catch(error => {
      if (error.response?.status === 403) {
        toast.info("Spoločnosť s týmto IČO nebola nájdená. Prosím, vyplňte údaje manuálne.");
      }
    })
  }

  findCompanyByRegistrationNumberRecipient(registrationNumber: string) {
    this.companyService.findByRegistrationNumber({
      registrationNumber: registrationNumber,
    }).then(response => {
      const company = response.data;

      this.recipientDraft = {
        name: company.name,
        address: company.address,
        registrationNumber: company.registrationNumber,
        taxId: company.taxId,
        vatId: company.vatId
      };
    }).catch(error => {
      if (error.response?.status === 403) {
        toast.info("Spoločnosť s týmto IČO nebola nájdená. Prosím, vyplňte údaje manuálne.");
      }
    })
  }


  onCompanySelected(companyId: any) {

    this.companyService.getCompanyById({companyId})
      .then(response => {
        const company = response.data;

        this.supplierDraft = {
          name: company.name,
          address: `${company.street}, ${company.zip} ${company.city}`,
          registrationNumber: company.registrationNumber,
          taxId: company.taxId,
          vatId: company.vatId
        };
      })
      .catch(error => {
        console.error('Chyba pri načítavaní údajov o spoločnosti:', error);
      });
  }

  onRecipientSelected(companyId: any) {

    this.companyService.getCompanyById({companyId})
      .then(response => {
        const company = response.data;

        this.recipientDraft = {
          name: company.name,
          address: `${company.street}, ${company.zip} ${company.city}`,
          registrationNumber: company.registrationNumber,
          taxId: company.taxId,
          vatId: company.vatId
        };
      })
      .catch(error => {
        console.error('Chyba pri načítavaní údajov o spoločnosti:', error);
      });
  }

  addItem() {
    this.items.push({
      name: "",
      quantity: 0,
      unit: "",
      unitPrice: 0,
      taxRate: 23,
      totalWithoutTax: 0,
      taxAmount: 0,
      totalWithTax: 0
    });
  }

  calculateSummary() {
    this.subtotal = this.items.reduce((acc, item) => acc + item.totalWithoutTax, 0);
    this.tax = this.items.reduce((acc, item) => acc + item.taxAmount, 0);
    this.total = this.subtotal + this.tax + this.shipping;
  }

  removeItem(index: number) {
    this.items.splice(index, 1);
  }

  updateItemTotals(item: any) {
    const qty = item.quantity || 0;
    const price = item.unitPrice || 0;
    const taxRate = item.taxRate || 0;

    item.totalWithoutTax = qty * price;
    item.taxAmount = item.totalWithoutTax * (taxRate / 100);
    item.totalWithTax = item.totalWithoutTax + item.taxAmount;

    this.calculateSummary();
  }
}
