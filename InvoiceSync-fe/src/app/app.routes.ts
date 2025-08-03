import { Routes } from '@angular/router';
import { Invoices } from './pages/invoices/invoices.component';
import { InvoiceInspect } from './features/invoice/invoice-inspect/invoice-inspect.component';
import { LoginComponent } from './core/auth/login/login.component';
import { RegisterComponent } from './core/auth/register/register.component';
import { authGuard } from './core/services/auth/auth.guard';
import { LayoutComponent } from './layout/layout.component';
import {HomeComponent} from "./pages/home/home.component";
import {XmlConvertorComponent} from "./pages/xml-page/xml-convertor.component";
import {ReceiptsComponent} from "./pages/receipts/receipts.component";
import {ReceiptDetailsComponent} from "./features/receipt/receipt-details/receipt-details.component";
import {CompanyDetailsComponent} from "./features/home/company-details/company-details.component";
import {InvoiceLimiterComponent} from "./pages/invoice-limiter/invoice-limiter.component";
import {CreateNewInvoice} from "./features/invoice/create-new-invoice/create-new-invoice.component";
import {PricingComponent} from "./pages/pricing/pricing.component";
import {MobileAppComponent} from "./pages/mobile-app/mobile-app.component";
import {DataTransferMappingComponent} from "./pages/data-transfer-mapping/data-transfer-mapping.component";
import {ErrorComponent} from "./shared/error/error.component";
import {PrivacyPolicyComponent} from "./pages/privacy-policy/privacy-policy.component";
import {DataTransferTableComponent} from "./pages/data-transfer-table/data-transfer-table.component";
import {ContactFormComponent} from "./contact-form/contact-form.component";
import {DataTransferDetailComponent} from "./pages/data-trasnfer-detail/data-transfer-detail.component";

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: '',
    component: LayoutComponent,
    children: [
      {
        path: 'home',
        component: HomeComponent,
        pathMatch: "full"
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
      },
      {
        path: 'web/mobile-app',
        component: MobileAppComponent
      },
      {
        path: 'web/data-transfer',
        component: DataTransferTableComponent
      },
      {
        path: 'web/data-transfer/:id',
        component: DataTransferDetailComponent
      },
    ]
  },
  {
    path: 'pricing',
    component: PricingComponent
  },
  {
    path: 'contact',
    component: ContactFormComponent
  },
  {
    path: 'web/privacy',
    component: PrivacyPolicyComponent
  },
  { path: 'error', component: ErrorComponent },
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
