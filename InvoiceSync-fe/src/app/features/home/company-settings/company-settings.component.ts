import {Component, Input, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {CompanyService} from "../../../core/services/company.service";
import {StatsService} from "../../../core/services/stats.service";

@Component({
  selector: 'app-company-settings',
  imports: [
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './company-settings.component.html',
  styleUrl: './company-settings.component.css'
})
export class CompanySettingsComponent implements OnInit {
  @Input() companyId!: any;

  constructor(
    private companyService: CompanyService,
  ) {
  }

  company: any = {};

    ngOnInit(): void {
      this.onFetchCompany();
    }

  onSave() {
    const payload = {
      cashReceiptNumber: this.company.cashReceiptNumber,
      cardReceiptNumber: this.company.cardReceiptNumber
    };

    this.companyService.updateCompany({
      companyId: this.company.id,
      company: payload
    }).then(() => {
      console.log("Uložené čísla bločkov:", payload);
    });
  }

  onFetchCompany() {
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
        console.log(this.company)
      })
      .finally(() => {
        console.log("done")
      });
  }

}
