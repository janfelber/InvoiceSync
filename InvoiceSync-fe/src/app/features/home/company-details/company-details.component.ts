import {AfterViewInit, Component, OnInit} from '@angular/core';
import {initFlowbite} from 'flowbite'
import {NgClass, NgForOf, NgIf} from "@angular/common";
import ApexCharts from 'apexcharts';
import {MatDialogWindowComponent} from "../../../shared/mat-dialog-window/mat-dialog-window.component";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CompanyService} from "../../../core/services/company.service";
import {AccountChartService} from "../../../core/services/account-chart.service";
import {MatDialogTutorialComponent} from "../../../shared/mat-dialog-tutorial/mat-dialog-tutorial.component";
import {accountsTutorialsByApp} from "../../../shared/tutorials/ExportAccoutsTutorial";
import {ChartAccountRequest} from "../../../core/models/ChartAccountRequest";
import {StatsService} from "../../../core/services/stats.service";
import {StatsResponse} from "../../../core/models/stats-response";


@Component({
  selector: 'app-company-details',
  imports: [
    NgForOf,
    NgIf,
    FormsModule,
    MatDialogWindowComponent,
    ReactiveFormsModule,
    RouterLink,
    NgClass,
    MatDialogTutorialComponent
  ],
  templateUrl: './company-details.component.html',
  styleUrl: './company-details.component.css'
})
export class CompanyDetailsComponent implements OnInit, AfterViewInit {

  loadingCompany = false;
  editCompanyModalOpen = false
  deleteCompanyModalOpen = false
  accountsImportModalOpen = false
  isUploading = false;
  isTutorialOpen = false;
  showDetails = false;
  importFinished = false;

  addedAccounts: { accountId: string; accountName: string }[] = [];

  accounts: any = []
  company: any = {};
  editedCompany: any = {};

  searchTerm: string = '';
  selectedAccount: any = null;
  companyId: any = null
  companyName: any = null;
  private chart: ApexCharts | undefined;
  stats: StatsResponse | null = null;

  autoClassId = '';
  autoClassName = '';
  autoCategoryId = '';
  autoCategoryName = '';

  accountsTutorialsByApp = accountsTutorialsByApp;
  availableApps = Object.keys(this.accountsTutorialsByApp);
  activeTabValue: 'create' | 'importExternal' = 'create';
  externalFile: File | null = null;

  newAccount: ChartAccountRequest = {
    companyId: 0,
    accountId: '',
    accountName: '',
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

  constructor(
    private route: ActivatedRoute,
    private companyService: CompanyService,
    private accountChartsService: AccountChartService,
    private statsService: StatsService,
    private router: Router
  ) {
  }

  ngAfterViewInit(): void {
    const options = {
      chart: {
        height: 420,
        type: 'area',
        fontFamily: 'Inter, sans-serif',
        foreColor: '#6b7280',
        toolbar: {
          show: false
        },
        animations: {
          enabled: true,
          easing: 'easeOutCubic',       // svižný, dynamický efekt "výstrelu"
          speed: 1200,                  // celková dĺžka animácie v ms (rýchla)
          animateGradually: {
            enabled: true,
            delay: 150                // malé oneskorenie medzi jednotlivými bodmi pre plynulosť
          },
          dynamicAnimation: {
            enabled: true,
            speed: 500                  // rýchlejšia dynamická animácia pre zmeny dát
          }
        }
      },
      fill: {
        type: 'gradient',
        gradient: {
          shadeIntensity: 1,
          opacityFrom: 0.4,
          opacityTo: 0.05,
          stops: [0, 90, 100]
        }
      },
      dataLabels: {
        enabled: false
      },
      tooltip: {
        style: {
          fontSize: '14px',
          fontFamily: 'Inter, sans-serif',
        },
      },
      grid: {
        show: false,
      },
      series: [
        {
          name: 'Revenue',
          data: [6356, 6218, 6156, 6526, 6356, 6256, 6056],
          color: '#1a56db'
        },
        {
          name: 'Revenue (previous period)',
          data: [6556, 6725, 6424, 6356, 6586, 6756, 6616],
          color: '#fdba8c'
        }
      ],
      xaxis: {
        categories: ['01 Feb', '02 Feb', '03 Feb', '04 Feb', '05 Feb', '06 Feb', '07 Feb'],
        labels: {
          show: false
        },
        axisBorder: {
          show: false
        },
        axisTicks: {
          show: false
        },
        crosshairs: {
          show: false
        }
      },
      yaxis: {
        show: true,
        labels: {
          style: {
            colors: ['#6b7280'],
            fontSize: '14px',
            fontWeight: 500,
          },
        },
      },
      stroke: {
        width: 6,
        curve: 'smooth'
      },
      legend: {
        fontSize: '14px',
        fontWeight: 500,
        fontFamily: 'Inter, sans-serif',
        labels: {
          colors: ['#6b7280']
        },
        itemMargin: {
          horizontal: 10
        }
      },
      responsive: [
        {
          breakpoint: 1024,
          options: {
            xaxis: {
              labels: {
                show: false
              }
            }
          }
        }
      ]
    };

    if (document.getElementById("area-chart") && typeof ApexCharts !== 'undefined') {
      const chart = new ApexCharts(document.getElementById("area-chart"), options);
      chart.render();
    }
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
    this.selectedAccount = {...account}; // kopírovanie pre editáciu bez ovplyvnenia pôvodného objektu
  }

  ngOnInit(): void {
    initFlowbite();

    this.route.queryParamMap.subscribe(params => {
      this.companyName = params.get('name');
    });
    this.route.paramMap.subscribe(params => {
      console.log(params.get('id'))
      this.companyId = params.get('id') || '';
      if (this.companyId) {
        this.onFetchStats();
        this.onFetchCompany();
      } else {
        console.error('Company ID is missing!');
      }
    });
  }

  onFetchStats() {
    this.statsService.getBasicStatsForCompany({ companyId: this.companyId })
      .then(response => {
        this.stats = response.data;
        console.log(this.stats)
      });
  }

  onFetchCompany() {
    this.loadingCompany = true;
    const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

    Promise.all([
      this.companyService.getCompanyById(
        {
          companyId: this.companyId
        }
      ),
      delay(250)
    ])
      .then(([response]) => {
        this.company = response.data;
      })
      .finally(() => {
        this.loadingCompany = false;
      });
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

  openEditModal() {
    this.editedCompany = {...this.company};
    this.editCompanyModalOpen = true
  }

  closeEditModal() {
    this.editCompanyModalOpen = false
  }

  openDeleteModal() {
    this.deleteCompanyModalOpen = true
  }

  closeDeleteModal() {
    this.deleteCompanyModalOpen = false
  }

  openAccountsImportModal() {
    this.accountsImportModalOpen = true
  }

  closeAccountsImportModal() {
    this.accountsImportModalOpen = false
  }

  openTutorialModal() {
    this.accountsImportModalOpen = false
    this.isTutorialOpen = true
  }

  closeTutorialModal() {
    this.isTutorialOpen = false
  }

  editCompany(): void {
    if (!this.company?.id) return;

    this.companyService.updateCompany(
      {
        companyId: this.company.id,
        company: this.editedCompany
      }
    )
      .then((updatedCompany) => {
        this.company = updatedCompany;
        console.log(this.editedCompany)
        this.editCompanyModalOpen = false;
        this.onFetchCompany()
      })
      .catch((error) => {
        console.error("Chyba pri aktualizácii spoločnosti", error);
      });
  }

  deleteCompany(): void {
    this.companyService.deleteCompany(
      {
        companyId: this.companyId
      }
    )
      .then(() => {
        console.log('Zmazané, idem navigovať...');
        this.deleteCompanyModalOpen = false;
        this.router.navigate(['home']);
      })
      .catch(err => {
        console.error('Chyba pri mazaní:', err);
      });
  }

}
