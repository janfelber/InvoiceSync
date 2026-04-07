import {Component, Input, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CompanyService} from "../../../core/services/company.service";
import {TranslateModule} from "@ngx-translate/core";

@Component({
  selector: 'app-company-settings',
  imports: [
    ReactiveFormsModule,
    FormsModule,
    TranslateModule
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
      cardReceiptNumber: this.company.cardReceiptNumber,
      recipientEmail: this.company.recipientEmail,
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
