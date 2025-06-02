import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {DatePipe, DecimalPipe, NgForOf, NgIf, NgOptimizedImage} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {initFlowbite} from 'flowbite'
import {CompanyService} from "../services/company.service";
import {PageResponseCompanyResponseDto} from "../servicesss/models/page-response-company-response-dto";
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';


@Component({
  selector: 'app-create-new-invoice',
  imports: [
    DecimalPipe,
    FormsModule,
    NgForOf,
    NgIf,
    DatePipe,
    NgOptimizedImage
  ],
  templateUrl: './create-new-invoice.component.html',
  styleUrl: './create-new-invoice.component.css'
})
export class CreateNewInvoice implements OnInit{


  public companyResponse: PageResponseCompanyResponseDto = {};
  loadingCompanyDetails = false;
  selectedCompanyDetails: any = null;

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
    html2canvas(invoiceElement, { scale: 1, useCORS: true }).then(canvas => {
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

  onCompanySelected(companyId: any) {
    this.loadingCompanyDetails = true;

    this.companyService.getCompanyById({ companyId })
      .then(response => {
        // Umelé oneskorenie 2 sekundy
        setTimeout(() => {
          this.selectedCompanyDetails = response.data;
          this.loadingCompanyDetails = false;
          console.log(this.selectedCompanyDetails);
        }, 2000);
      })
      .catch(error => {
        console.error('Chyba pri načítavaní údajov o spoločnosti:', error);
        this.loadingCompanyDetails = false;
      });
  }

  invoice = {
    invoiceNumber: '',
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
    paymentMethod: 'Prevod',
    iban: 'SK12 3456 7890 1234 5678 9012',
    swift: 'UNCRSKBX',
    note: ''
  }


  items: {
    name: string;
    quantity: number;
    unit: string;
    unitPrice: number;     // jednotková cena bez DPH
    taxRate: number;       // % DPH
    totalWithoutTax: number;
    taxAmount: number;
    totalWithTax: number;
  }[] = [];

  newItem = {
    name: '',
    quantity: 1,
    unit: 'ks',
    unitPrice: null as number | null,
    taxRate: 23
  };

  subtotal = 0;
  tax = 0;
  shipping = 0; // nastav napr. 3.50 alebo z form inputu
  total = 0;

  calculateSummary() {
    this.subtotal = this.items.reduce((acc, item) => acc + item.totalWithoutTax, 0);
    this.tax = this.items.reduce((acc, item) => acc + item.taxAmount, 0);
    this.total = this.subtotal + this.tax + this.shipping;
  }

  increaseQuantity() {
    if (this.newItem.quantity == null) this.newItem.quantity = 1;
    else this.newItem.quantity++;
  }

  decreaseQuantity() {
    if (this.newItem.quantity != null && this.newItem.quantity > 1) {
      this.newItem.quantity--;
    }
  }

  addItem() {
    // Skontroluj základné povinné polia
    if (this.newItem.name && this.newItem.unitPrice != null && this.newItem.quantity != null) {

      // Výpočet ceny bez DPH = jednotková cena * množstvo
      const totalWithoutTax = this.newItem.unitPrice * this.newItem.quantity;

      // Výpočet DPH podľa sadzby
      const taxAmount = totalWithoutTax * (this.newItem.taxRate / 100);

      // Celková cena s DPH
      const totalWithTax = totalWithoutTax + taxAmount;

      // Pridaj novú položku do zoznamu
      this.items.push({
        name: this.newItem.name,
        quantity: this.newItem.quantity,
        unit: this.newItem.unit ?? 'ks',       // Ak nie je nastavené, použije sa 'ks'
        unitPrice: this.newItem.unitPrice,
        taxRate: this.newItem.taxRate,
        totalWithoutTax,
        taxAmount,
        totalWithTax
      });

      // Reset formu na prázdny stav (pripravený na ďalší vstup)
      this.newItem = {
        name: '',
        quantity: 1,
        unit: 'ks',             // Prednastavená merná jednotka
        unitPrice: null,
        taxRate: 19             // alebo iná štandardná sadzba podľa tvojej potreby
      };

      this.calculateSummary();
    }
  }
}
