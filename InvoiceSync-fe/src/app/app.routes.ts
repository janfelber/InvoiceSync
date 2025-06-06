import { Routes } from '@angular/router';
import { Invoices } from './page/invoices/invoices.component';
import { InvoiceInspect } from './invoice-inspect/invoice-inspect.component';
import { LoginComponent } from './page/login/login.component';
import { RegisterComponent } from './page/register/register.component';
import { authGuard } from './services/auth/auth.guard';
import { LayoutComponent } from './layout/layout.component';
import {HomeComponent} from "./page/home/home.component";
import {XmlConvertorComponent} from "./xml-page/xml-convertor.component";
import {AdminComponent} from "./admin/admin.component";
import {AdminLayoutComponent} from "./admin-layout/admin-layout.component";
import {ReceiptsComponent} from "./receipts/receipts.component";
import {ReceiptDetailsComponent} from "./receipt-details/receipt-details.component";
import {CompanyDetailsComponent} from "./company-details/company-details.component";
import {InvoiceLimiterComponent} from "./invoice-limiter/invoice-limiter.component";
import {CreateNewInvoice} from "./create-new-invoice/create-new-invoice.component";

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'home',
        component: HomeComponent,
        pathMatch: "full"
      },
      {
        path: '',
        redirectTo: 'xml-convertor',
        pathMatch: 'full'
      },
      {
        path: 'web/receipts',
        component: ReceiptsComponent
      },
      {
        path: 'web/receipts/:id',
        component: ReceiptDetailsComponent
      },
      {
        path: 'web/company/:id',
        component: CompanyDetailsComponent
      },
      {
        path: 'invoices',
        component: Invoices,
      },
      {
        path: 'invoices/:id',
        component: InvoiceInspect
      },
      {
        path: 'invoice/new',
        component: CreateNewInvoice
      },
      {
        path: 'xml-convertor',
        component: XmlConvertorComponent
      },
      {
        path: 'web/limiter',
        component: InvoiceLimiterComponent
      }
    ]
  },
  //admin layout
  // {
  //
  //   path: 'admin',
  //   component: AdminLayoutComponent,
  //   children: [
  //     {
  //       path: 'users',
  //       component: AdminComponent
  //     }
  //   ]
  // },
  {
    path: '**',
    redirectTo: 'home'
  }
];
