import { Component } from '@angular/core';
import { GridInvoicesComponent } from "../components/grid-invoices/grid-invoices.component";
import {HeaderCompanyComponent} from "../header-company/header-company.component";

@Component({
  selector: 'app-test',
  standalone: true,
  imports: [GridInvoicesComponent, HeaderCompanyComponent],
  templateUrl: './invoices.component.html',
  styleUrl: './invoices.component.css'
})
export class Invoices {

}
