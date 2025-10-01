import {Component, Input, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatDialogTutorialComponent} from "../../../shared/mat-dialog-tutorial/mat-dialog-tutorial.component";
import {MatDialogWindowComponent} from "../../../shared/mat-dialog-window/mat-dialog-window.component";
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {accountsTutorialsByApp} from "../../../shared/tutorials/ExportAccoutsTutorial";
import {ActivatedRoute, Router} from "@angular/router";
import {CompanyService} from "../../../core/services/company.service";
import {AccountChartService} from "../../../core/services/account-chart.service";
import {ChartAccountRequest} from "../../../core/models/ChartAccountRequest";

@Component({
  selector: 'app-company-account-chart',
  imports: [
    FormsModule,
    MatDialogTutorialComponent,
    MatDialogWindowComponent,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    NgClass
  ],
  templateUrl: './company-account-chart.component.html',
  styleUrl: './company-account-chart.component.css'
})
export class CompanyAccountChartComponent implements OnInit {
  @Input() companyId!: any | null;

  isUploading = false;
  importFinished = false;
  accountsImportModalOpen = false;
  showDetails = false;
  isTutorialOpen = false;

  selectedAccount: any = null;
  externalFile: File | null = null;

  accounts: any = []
  addedAccounts: { accountId: string; accountName: string }[] = [];

  activeTabValue: 'create' | 'importExternal' = 'create';

  autoClassId = '';
  autoClassName = '';
  autoCategoryId = '';
  autoCategoryName = '';
  newAccount: ChartAccountRequest = {
    companyId: 0,
    accountId: '',
    accountName: '',
  }

  accountsTutorialsByApp = accountsTutorialsByApp;
  availableApps = Object.keys(this.accountsTutorialsByApp);

  constructor(
    private accountChartsService: AccountChartService
  ) {
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

  ngOnInit(): void {
        this.onFetchAccounts()
    }

  onFetchAccounts() {
    this.accountChartsService.findAccountsByCompany({
      companyId: this.companyId,
    }).then(response => {
      this.accounts = response.data;
      console.log(this.accounts)
    })
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

  mapCategoryName(categoryId: string): string {
    const map: Record<string, string> = {
      '501': 'Spotreba materiálu',
      '343': 'Daň z pridanej hodnoty',
      '602': 'Tržby za služby',
      '321': 'Dodávatelia',
    }

    return map[categoryId] || '';
  }

  openClasses: { [classNumber: number]: boolean } = {};
  openCategories: { [categoryId: string]: boolean } = {};

  toggleClass(classNumber: number): void {
    this.openClasses[classNumber] = !this.openClasses[classNumber];
  }

  isClassOpen(classNumber: number): boolean {
    return !!this.openClasses[classNumber];
  }

  toggleCategory(categoryId: string): void {
    this.openCategories[categoryId] = !this.openCategories[categoryId];
  }

  isCategoryOpen(categoryId: string): boolean {
    return !!this.openCategories[categoryId];
  }

  selectAccount(account: any): void {
    this.selectedAccount = {...account};
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

  closeTutorialModal() {
    this.isTutorialOpen = false
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
        await this.accountChartsService.saveChartAccount(this.newAccount)

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

}
