import {Component, OnInit} from '@angular/core';
import {AxiosService} from "../../../core/axios.service";
import {ActivatedRoute} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {InvoiceDetailResponse} from "../../../pages/invoices/invoice-detail-response";
import {InvoiceService} from "../../../core/services/invoice.service";
import {PostingAccountService} from "../../../core/services/posting-account.service";
import {ReceiptType} from "../../../core/enums/receipt-type";

interface Account {
  id: number;
  number: string;
  name: string;
}

@Component({
    selector: 'invoice-info',
    imports: [FormsModule, CommonModule],
    templateUrl: './invoice-info.component.html',
    styleUrl: './invoice-info.component.css'
})
export class InvoiceInspect implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private invoiceService: InvoiceService,
    private postingAccountService: PostingAccountService,
  ) {
  }

  invoiceId: any = null;
  public invoice: InvoiceDetailResponse = {};

  drawerOpen = false;
  selectedItemForAccount: any = null;
  selectedItemName: string = '';
  groupedAccounts: any[] = [];
  selectedAccountsPerItem: { [itemId: number]: Account } = {};

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.invoiceId = params.get('id') || '';
    });
    this.onFetchInvoice();
  }

  onFetchInvoice() {
    this.invoiceService.getInvoiceById({ invoiceId: this.invoiceId })
      .then(invoice => {
        this.invoice = invoice.data;
        console.log(this.invoice);
      });
  }

  async onFetchAccounts() {
    const companyId = this.invoice.company?.id ?? null;
    if (!companyId) return [];

    const response = await this.postingAccountService.findAvailableAccounts({
      companyId: companyId,
      type: ReceiptType.INTERNAL,
    });
    this.groupedAccounts = response.data;
    return response.data;
  }

  openDrawer(item: any): void {
    this.selectedItemForAccount = item.id;
    this.selectedItemName = item.name;
    this.drawerOpen = true;

    this.onFetchAccounts().then(() => {
      if (!this.selectedAccountsPerItem[item.id] && item.accountValue) {
        const matchingAccount = this.groupedAccounts
          .flatMap((cls: any) => cls.categories)
          .flatMap((cat: any) => cat.accounts)
          .find((acc: any) => acc.number === item.accountValue);

        if (matchingAccount) {
          this.selectedAccountsPerItem[item.id] = matchingAccount;
        }
      }
    });
  }

  closeDrawer(): void {
    this.drawerOpen = false;
  }

  onAccountChange(itemId: number, account: Account): void {
    this.selectedAccountsPerItem[itemId] = account;
  }

  assignAccountToItem(): void {
    const selectedAccount = this.selectedAccountsPerItem[this.selectedItemForAccount];
    if (!selectedAccount) return;

    this.invoice.items?.forEach((item: any) => {
      if (item.id === this.selectedItemForAccount) {
        item.accountValue = selectedAccount.number;
      }
    });

    this.closeDrawer();
    this.selectedItemForAccount = null;
  }
}
