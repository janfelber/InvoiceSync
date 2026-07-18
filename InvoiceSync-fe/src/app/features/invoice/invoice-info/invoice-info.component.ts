import {Component, computed, OnInit, signal} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {InvoiceDetailResponse} from "../../../pages/invoices/invoice-detail-response";
import {InvoiceService} from "../../../core/services/invoice.service";
import {PostingAccountService} from "../../../core/services/posting-account.service";
import {ReceiptType} from "../../../core/enums/receipt-type";
import {TranslatePipe, TranslateService} from "@ngx-translate/core";
import {toast} from "ngx-sonner";

interface Account {
  id: number;
  number: string;
  name: string;
}

@Component({
    selector: 'invoice-info',
  imports: [CommonModule, FormsModule, TranslatePipe],
    templateUrl: './invoice-info.component.html',
    styleUrl: './invoice-info.component.css'
})
export class InvoiceInspect implements OnInit {

  selectedForMerge = new Set<number>();
  currentMode: 'edit' | 'accounting' | 'merge' = 'edit';
  mergedItemName = '';

  drawerOpen = false;
  selectedItemForAccount: any = null;
  selectedItemName = '';
  selectedAccountsPerItem: { [itemId: number]: Account } = {};
  groupedAccounts = signal<any[]>([]);
  searchQuery = signal('');
  openGroups: { [key: string]: boolean } = {};

  filteredGroupedAccounts = computed(() => {
    const q = this.searchQuery().toLowerCase().trim();
    if (!q) return this.groupedAccounts();
    return this.groupedAccounts()
      .map(group => ({
        ...group,
        categories: group.categories
          .map((cat: any) => ({
            ...cat,
            accounts: cat.accounts.filter((acc: any) =>
              acc.number?.toLowerCase().includes(q) ||
              acc.name?.toLowerCase().includes(q)
            )
          }))
          .filter((cat: any) => cat.accounts.length > 0)
      }))
      .filter((group: any) => group.categories.length > 0);
  });

  constructor(
    private route: ActivatedRoute,
    private invoiceService: InvoiceService,
    private postingAccountService: PostingAccountService,
    private translate: TranslateService,
  ) {
  }

  invoiceId: any = null;
  public invoice: InvoiceDetailResponse | null = null;
  mergeConfirmOpen = false;

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
      });
  }

  setMode(mode: 'edit' | 'accounting' | 'merge'): void {
    if (mode !== 'merge') {
      this.selectedForMerge.clear();
    }
    this.currentMode = mode;
  }

  get selectedItemsForMerge() {
    return (this.invoice?.items || []).filter((item) => this.selectedForMerge.has(item.id));
  }

  get mergeTotal(): number {
    return this.selectedItemsForMerge.reduce((sum, item) => sum + (item.totalPrice.priceWithVAT || 0), 0);
  }

  get hasMixedVatRates(): boolean {
    return new Set(this.selectedItemsForMerge.map(item => item.vatRate)).size > 1;
  }

  get dominantVatRate(): number {
    const counts = new Map<number, number>();
    for (const item of this.selectedItemsForMerge) {
      counts.set(item.vatRate, (counts.get(item.vatRate) ?? 0) + 1);
    }
    let dominant = this.selectedItemsForMerge[0]?.vatRate;
    let max = 0;
    for (const [rate, count] of counts) {
      if (count > max) { max = count; dominant = rate; }
    }
    return dominant;
  }

  mergeItems(): void {
    this.mergedItemName = this.selectedItemsForMerge[0]?.name ?? '';
    this.mergeConfirmOpen = true;
  }

  async confirmMerge(): Promise<void> {
    await this.invoiceService.mergeInvoiceItems(
      this.invoiceId,
      { itemIds: Array.from(this.selectedForMerge), description: this.mergedItemName }
    );
    this.mergeConfirmOpen = false;
    this.selectedForMerge.clear();
    this.onFetchInvoice();
    toast.success(this.translate.instant('RECEIPT_DETAIL.TOAST_MERGE_SUCCESS'));
  }

  isSelectedForMerge(itemId: number | undefined): boolean {
    if (itemId == null) return false;
    return this.selectedForMerge.has(itemId);
  }

  toggleMergeSelection(itemId: number | undefined): void {
    if (itemId == null) return;
    if (this.selectedForMerge.has(itemId)) {
      this.selectedForMerge.delete(itemId);
    } else {
      this.selectedForMerge.add(itemId);
    }
  }

  get totalVat(): number {
    return (this.invoice?.items || []).reduce((sum, item) =>
      sum + (item.quantity || 0) * (item.unitPrice.priceWithoutVAT || 0) * ((item.vatRate || 0) / 100), 0);
  }

  get vatBreakdown(): { rate: number; base: number; vat: number }[] {
    const map = new Map<number, { base: number; vat: number }>();
    for (const item of this.invoice?.items || []) {
      const rate = item.vatRate || 0;
      const base = (item.quantity || 0) * (item.unitPrice.priceWithoutVAT || 0);
      const vat = base * (rate / 100);
      const existing = map.get(rate) ?? { base: 0, vat: 0 };
      map.set(rate, { base: existing.base + base, vat: existing.vat + vat });
    }
    return Array.from(map.entries())
      .sort(([a], [b]) => b - a)
      .map(([rate, { base, vat }]) => ({ rate, base, vat }));
  }

  get statusLabel(): string {
    switch (this.invoice?.invoiceDetails.status?.toUpperCase()) {
      case 'PAID': return 'Uhradená';
      case 'UNPAID': return 'Neuhradená';
      case 'OVERDUE': return 'Po splatnosti';
      case 'CANCELLED': return 'Stornovaná';
      default: return this.invoice?.invoiceDetails.status ?? '—';
    }
  }

  get statusClasses(): string {
    switch (this.invoice?.invoiceDetails.status?.toUpperCase()) {
      case 'PAID': return 'bg-green-50 text-green-700 ring-green-600/20 dark:bg-green-900/20 dark:text-green-400';
      case 'UNPAID': return 'bg-yellow-50 text-yellow-700 ring-yellow-600/20 dark:bg-yellow-900/20 dark:text-yellow-400';
      case 'OVERDUE': return 'bg-red-50 text-red-700 ring-red-600/20 dark:bg-red-900/20 dark:text-red-400';
      default: return 'bg-slate-100 text-slate-600 ring-slate-500/20 dark:bg-slate-800 dark:text-slate-400';
    }
  }

  toggleGroup(key: string): void {
    this.openGroups[key] = !this.openGroups[key];
  }

  async onFetchAccounts() {
    const companyId = this.invoice?.targetCompany.id;
    if (!companyId) return;
    const response = await this.postingAccountService.findAvailableAccounts({
      companyId,
      type: ReceiptType.INTERNAL,
    });
    this.groupedAccounts.set(response.data);
  }

  openDrawer(item: any): void {
    this.selectedItemForAccount = item.id;
    this.selectedItemName = item.name;
    this.drawerOpen = true;

    this.onFetchAccounts().then(() => {
      if (!this.selectedAccountsPerItem[item.id] && item.accountValue) {
        const matchingAccount = this.groupedAccounts()
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
    this.searchQuery.set('');
    this.selectedItemForAccount = null;
  }

  onAccountChange(itemId: number, account: Account): void {
    this.selectedAccountsPerItem[itemId] = account;
  }

  assignAccountToItem(): void {
    const selectedAccount = this.selectedAccountsPerItem[this.selectedItemForAccount];
    if (!selectedAccount) return;
    const items = this.invoice?.items || [];
    items.forEach((item: any) => {
      if (item.id === this.selectedItemForAccount) {
        item.accountValue = selectedAccount.number;
      }
    });
    this.closeDrawer();
  }
}
