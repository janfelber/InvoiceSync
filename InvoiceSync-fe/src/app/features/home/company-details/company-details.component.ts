import {Component, OnInit} from '@angular/core';
import {initFlowbite} from 'flowbite'
import {ActivatedRoute} from "@angular/router";
import {CompanyInfoComponent} from "../company-info/company-info.component";
import {CompanyAccountChartComponent} from "../account-chart/company-account-chart.component";
import {CompanySettingsComponent} from "../company-settings/company-settings.component";


@Component({
  selector: 'app-company-details',
  imports: [
    CompanyInfoComponent,
    CompanyAccountChartComponent,
    CompanySettingsComponent
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
