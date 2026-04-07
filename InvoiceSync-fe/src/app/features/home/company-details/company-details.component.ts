import {Component, OnInit, inject, signal} from '@angular/core';
import {initFlowbite} from 'flowbite';
import {ActivatedRoute, RouterLink} from "@angular/router";
import {CompanyInternalDocumentsAccountsComponent} from "../internal-documents-accounts/company-internal-documents-accounts.component";
import {CompanySettingsComponent} from "../company-settings/company-settings.component";
import {CompanyCashDocumentsAccountsComponent} from "../cash-documents-accounts/company-cash-documents-accounts.component";
import {CompanyService} from "../../../core/services/company.service";
import {StatsService} from "../../../core/services/stats.service";
import {StatsResponse} from "../../../core/models/stats-response";
import {TranslateModule} from "@ngx-translate/core";

@Component({
  selector: 'app-company-details',
  imports: [
    RouterLink,
    CompanyCashDocumentsAccountsComponent,
    CompanySettingsComponent,
    CompanyInternalDocumentsAccountsComponent,
    TranslateModule
  ],
  templateUrl: './company-details.component.html',
  styleUrl: './company-details.component.css'
})
export class CompanyDetailsComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly companyService = inject(CompanyService);
  private readonly statsService = inject(StatsService);

  companyId = signal('');
  activeTab = signal<'overview' | 'accounts' | 'settings'>('overview');
  activeAccountsTab = signal<'internal' | 'cash'>('internal');

  company = signal<any>(null);
  stats = signal<StatsResponse | null>(null);
  loadingHeader = signal(true);

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.companyId.set(params.get('id') || '');
      this.loadData();
    });
    initFlowbite();
  }

  getInitials(name: string): string {
    if (!name) return '';
    const words = name.trim().split(' ');
    if (words.length === 1) return words[0].substring(0, 2).toUpperCase();
    return (words[0][0] + words[1][0]).toUpperCase();
  }

  private loadData(): void {
    this.loadingHeader.set(true);
    Promise.all([
      this.companyService.getCompanyById({companyId: +this.companyId()}),
      this.statsService.getBasicStatsForCompany({companyId: +this.companyId()}),
    ]).then(([companyRes, statsRes]) => {
      this.company.set(companyRes.data);
      this.stats.set(statsRes.data);
    }).finally(() => {
      this.loadingHeader.set(false);
    });
  }
}
