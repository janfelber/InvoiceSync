import {Component, OnInit} from '@angular/core';
import {initFlowbite} from 'flowbite'
import {ActivatedRoute} from "@angular/router";
import {CompanyInfoComponent} from "../company-info/company-info.component";
import {CompanyInternalDocumentsAccountsComponent} from "../internal-documents-accounts/company-internal-documents-accounts.component";
import {CompanySettingsComponent} from "../company-settings/company-settings.component";
import {CompanyCashDocumentsAccountsComponent} from "../cash-documents-accounts/company-cash-documents-accounts.component";
import {TranslatePipe} from "@ngx-translate/core";


@Component({
  selector: 'app-company-details',
  imports: [
    CompanyInfoComponent,
    CompanyCashDocumentsAccountsComponent,
    CompanySettingsComponent,
    CompanyCashDocumentsAccountsComponent,
    CompanyInternalDocumentsAccountsComponent,
    TranslatePipe
  ],
  templateUrl: './company-details.component.html',
  styleUrl: './company-details.component.css'
})
export class CompanyDetailsComponent implements OnInit {
  companyId: any = null
  companyName: any = null;

  constructor(
    private route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.companyId = params.get('id') || '';
    });
    initFlowbite();
    this.route.queryParamMap.subscribe(params => {
      this.companyName = params.get('name');
    });
  }

}
