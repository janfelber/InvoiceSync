import {Component, OnInit} from '@angular/core';
import {DecimalPipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {initFlowbite} from 'flowbite'
import {CompanyService} from "../../../core/services/company.service";
import {PageResponseCompanyResponseDto} from "../../../core/models/page-response-company-response-dto";
import {toast} from "ngx-sonner";
import {InvoiceService} from "../../../core/services/invoice.service";

interface SupplierDraft {
  name: string;
  city: string;
  street: string;
  zip: string;
  registrationNumber: string;
  taxId: string;
  vatId: string;
}

interface RecipientDraft {
  name: string;
  city: string;
  street: string;
  zip: string;
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
    NgIf,
    NgClass,
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

  selectedSupplierCompany: number = 0;

  supplierDraft: SupplierDraft = {
    name: '',
    city:'',
    street: '',
    zip: '',
    registrationNumber: '',
    taxId: '',
    vatId: ''
  };

  recipientDraft: RecipientDraft = {
    name: '',
    city:'',
    street: '',
    zip: '',
    registrationNumber: '',
    taxId: '',
    vatId: ''
  }

  invoice = {
    invoiceDetails: {
      numberRequested: '',
      variableSymbol: '',
      invoiceType: 'ISSUED',       // enum string
      issueDate: '',
      taxDate: '',
      accountingDate: '',
      dueDate: '',
      note: ''
    },

    partner : this.recipientDraft,

    myIdentity: {
      name: '',
      street: '',
      city: '',
      zip: '',
      registrationNumber: '',
      taxId: '',
      vatId: '',
      iban: '',
      swift: ''
    },

    items: []
  };

  items: {
    name: string;
    quantity: number;
    unitType: string;
    vatRate: number;
    unitPriceWithoutVat: number;
    unitPriceWithVat: number;
    totalItemPriceWithoutVat: number;
    totalItemPriceWithVat: number;
    totalWithoutTax: number;
    taxAmount: number;
    totalWithTax: number;
    accountValue: string;
    accountText: string;
  }[] = [];


  constructor(
    private companyService: CompanyService,
    private invoiceService: InvoiceService,
  ) {
  }

  buildInvoice() {
    return {
      ...this.invoice,

      partner: this.recipientDraft
        ? {
          name: this.recipientDraft.name ?? '',
          city: this.recipientDraft.city ?? '',
          street: this.recipientDraft.street ?? '',
          zip: this.recipientDraft.zip ?? '',
          registrationNumber: this.recipientDraft.registrationNumber ?? '',
          taxId: this.recipientDraft.taxId ?? '',
          vatId: this.recipientDraft.vatId ?? ''
        }
        : null,

      items: [...this.items],

      totalWithoutTax: this.subtotal ?? 0,
      totalTax: this.tax ?? 0,
      totalWithTax: this.total ?? 0
    };
  }

  ngOnInit(): void {
    initFlowbite();
    this.onFetchCompanies();
  }

  onFetchCompanies() {
    this.companyService.findAllCompaniesByUser().then(response => {
      this.companyResponse = response.data;
      if (this.companyResponse.content!.length === 0) {
        toast.info("Nemáte žiadne uložené spoločnosti. Použite vyhľadávanie alebo manuálne zadanie.");
      }
    }).catch(error => {
      console.error(error);
    });
  }

  createInvoice() {
    const payload = this.buildInvoice();
    console.log(this.selectedSupplierCompany);
    this.invoiceService.createInvoice(
      payload, this.selectedSupplierCompany
    ).then(response => {
      toast.success("Faktúra úspešne vytvorená");
    })
  }

  exportPdf() {
    // PDF generation moved to backend
    throw new Error('Not implemented');
  }

  findCompanyByRegistrationNumberSupplier(registrationNumber: string) {
    this.companyService.findByRegistrationNumber({
      registrationNumber: registrationNumber,
    }).then(response => {
      const company = response.data;

      this.supplierDraft = {
        name: company.name,
        city: company.city,
        street: company.street,
        zip: company.zip,
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

      console.log(company.street);

      this.recipientDraft = {
        name: company.name,
        city: company.city,
        street: company.street,
        zip: company.zip,
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

        this.selectedSupplierCompany = company.id;

        this.supplierDraft = {
          name: company.name,
          city: company.city,
          street: company.street,
          zip: company.zip,
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
          city: company.city,
          street: company.street,
          zip: company.zip,
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
      unitType: "",
      vatRate: 23,
      unitPriceWithoutVat: 0,
      unitPriceWithVat: 0,
      totalWithoutTax: 0,
      totalItemPriceWithoutVat: 0,
      totalItemPriceWithVat: 0,
      taxAmount: 0,
      totalWithTax: 0,
      accountValue: "",
      accountText: ""
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

  dueDayPreset: number = 30;
  readonly duePresets = [14, 30, 60, 90];

  onDateChange(field: 'issueDate' | 'dueDate' | 'taxDate', event: Event) {
    this.invoice.invoiceDetails[field] = (event.target as HTMLInputElement).value;
    if (field === 'issueDate') {
      this.setDuePreset(this.dueDayPreset);
    }
  }

  setDuePreset(days: number): void {
    this.dueDayPreset = days;
    if (!this.invoice.invoiceDetails.issueDate) return;
    const parts = this.invoice.invoiceDetails.issueDate.split('.');
    if (parts.length !== 3) return;
    const base = new Date(+parts[2], +parts[1] - 1, +parts[0]);
    base.setDate(base.getDate() + days);
    const dd = String(base.getDate()).padStart(2, '0');
    const mm = String(base.getMonth() + 1).padStart(2, '0');
    this.invoice.invoiceDetails.dueDate = `${dd}.${mm}.${base.getFullYear()}`;
  }

  syncVariableSymbol() {
    this.invoice.invoiceDetails.variableSymbol = this.invoice.invoiceDetails.numberRequested;
  }

  syncDeliveryDate() {
    this.invoice.invoiceDetails.taxDate = this.invoice.invoiceDetails.issueDate;
  }

  updateItemTotals(item: any) {
    const qty = item.quantity || 0;
    const price = item.unitPriceWithoutVat || 0;
    const vatRate = item.vatRate || 0;

    item.unitPriceWithVat = price * (1 + vatRate / 100);
    item.totalWithoutTax = qty * price;
    item.taxAmount = item.totalWithoutTax * (vatRate / 100);
    item.totalWithTax = item.totalWithoutTax + item.taxAmount;

    this.calculateSummary();
  }
}
