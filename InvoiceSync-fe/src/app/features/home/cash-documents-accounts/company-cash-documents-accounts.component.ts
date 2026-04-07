import {Component, Input, OnInit} from '@angular/core';
import {PostingAccountService} from "../../../core/services/posting-account.service";
import {PageResponsePostingAccount} from "../../../pages/home/page-response-posting-account";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {MatDialogWindowComponent} from "../../../shared/mat-dialog-window/mat-dialog-window.component";
import {PostingAccountRequest} from "../../../core/models/PostingAccountRequest";
import {TranslateModule} from "@ngx-translate/core";

@Component({
  selector: 'app-company-cash-documents-accounts',
  imports: [
    NgForOf,
    NgIf,
    FormsModule,
    MatDialogWindowComponent,
    NgClass,
    TranslateModule
  ],
  templateUrl: './company-cash-documents-accounts.component.html'
})
export class CompanyCashDocumentsAccountsComponent implements OnInit{
  @Input() companyId!: any | null;

  isUploading = false;
  importFinished = false;
  accountsImportModalOpen = false;
  isTutorialOpen = false;


  externalFile: File | null = null;

  public page: number = 0;
  public size: number = 15;

  public accountResponse: PageResponsePostingAccount = {
    content: [],
  }

  addedAccounts: { accountId: string; accountName: string }[] = [];

  activeTabValue: 'create' | 'importExternal' = 'create';

  autoClassId = '';
  autoClassName = '';
  autoCategoryId = '';
  autoCategoryName = '';
  newAccount: PostingAccountRequest = {
    companyId: 0,
    accountId: '',
    accountName: '',
    type: 'CASH',
  }

  mapCategoryName(categoryId: string): string {
    const map: Record<string, string> = {
      '501': 'Spotreba materiálu',
      '343': 'Daň z pridanej hodnoty',
      '602': 'Tržby za služby',
      '321': 'Dodávatelia',
    }

    return map[categoryId] || '';
  }

  mapClassName(classId: string): string {
    const map: Record<string, string> = {
      '0': 'Dlhodobý majetok',
      '1': 'Zásoby',
      '2': 'Finančné účty',
      '3': 'Zúčtovacie vzťahy',
      '4': 'Kapitálové účty',
      '5': 'Náklady',
      '6': 'Výnosy',
      '7': 'Uzávierkové účty',
      '8': 'Vnútropodnikové účty',
      '9': 'Ostatné účty'
    }
    return map[classId] || '';
  }

  constructor(
    private accountChartsService: PostingAccountService,
  ) {}

  ngOnInit(): void {
        this.onFetchAccounts()
    }

  onFetchAccounts() {
    this.accountChartsService.findAccountsByCompany({
      page: this.page,
      size: this.size,
      type: 'CASH',
      companyId: this.companyId,
    }).then(response => {
      this.accountResponse = response.data;
      console.log(this.accountResponse)
    })
  }

  async onCreateAccounts(): Promise<void> {
    this.isUploading = true;
    this.importFinished = false;
    try {
      console.log('Active Tab:', this.activeTab);
      if (this.activeTab === 'importExternal') {
        if (this.externalFile) {
          const imported = await this.accountChartsService.importChartOfAccounts({
            files: [this.externalFile],
            companyId: this.companyId
          });

          this.addedAccounts = imported.data ?? [];
          console.log(this.addedAccounts)
        }
      }
      if (this.activeTab === 'create') {
        this.newAccount.companyId = this.companyId;
        await this.accountChartsService.savePostingAccount(this.newAccount)

      }
    } catch (err) {
      console.error(err);
    } finally {
      this.isUploading = false;
      this.importFinished = true;
    }
  }

  onExternalFileUpload(event: any): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.externalFile = input.files[0];
    }
  }

  onAccountIdChange() {
    const id = this.newAccount.accountId;
    if (id?.length >= 3) {
      const prefix = id.substring(0, 3);
      this.autoCategoryId = prefix;
      this.autoCategoryName = this.mapCategoryName(prefix);

      this.autoClassId = prefix.charAt(0);
      this.autoClassName = this.mapClassName(this.autoClassId);
    } else {
      this.autoCategoryId = this.autoCategoryName = this.autoClassId = this.autoClassName = '';
    }
  }

  set activeTab(tab: 'create' | 'importExternal') {
    this.activeTabValue = tab;
    if (tab === 'create') {
      this.externalFile = null
    } else {
      this.externalFile = null;
    }
  }

  get activeTab(): 'create' | 'importExternal' {
    return this.activeTabValue;
  }

  openAccountsImportModal() {
    this.accountsImportModalOpen = true;
  }

  closeAccountsImportModal() {
    this.accountsImportModalOpen = false;
  }

  openTutorialModal() {
    this.accountsImportModalOpen = false
    this.isTutorialOpen = true
  }
}
