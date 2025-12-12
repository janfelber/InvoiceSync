import {Routes} from '@angular/router';
import {LayoutComponent} from './layout/layout.component';
import {HomeComponent} from './pages/home/home.component';
import {Invoices} from './pages/invoices/invoices.component';
import {XmlConvertorComponent} from './pages/xml-page/xml-convertor.component';
import {ReceiptsComponent} from './pages/receipts/receipts.component';
import {ReceiptDetailsComponent} from './features/receipt/receipt-details/receipt-details.component';
import {CompanyDetailsComponent} from './features/home/company-details/company-details.component';
import {InvoiceLimiterComponent} from './pages/invoice-limiter/invoice-limiter.component';
import {CreateNewInvoice} from './features/invoice/create-new-invoice/create-new-invoice.component';
import {PricingComponent} from './pages/pricing/pricing.component';
import {MobileAppComponent} from './pages/mobile-app/mobile-app.component';
import {DataTransferTableComponent} from './pages/data-transfer-table/data-transfer-table.component';
import {DataTransferDetailComponent} from './pages/data-trasnfer-detail/data-transfer-detail.component';
import {InvoiceDisplay} from './features/invoice/invoice-inspect/invoice-inspect.component';
import {ErrorComponent} from './shared/error/error.component';
import {PrivacyPolicyComponent} from './pages/privacy-policy/privacy-policy.component';
import {ContactFormComponent} from './contact-form/contact-form.component';
import {AuthGuard} from './core/services/auth/auth.guard';
import {LoginComponent} from './core/auth/login/login.component';
import {RegisterComponent} from './core/auth/register/register.component';
import {RoleGuard} from "./core/services/auth/role.guard";
import {AdminUsersComponent} from "./admin/admin-users/admin-users.component";
import {AdminLayoutComponent} from "./admin/admin-layout/admin-layout.component";
import {AdminUserInfoComponent} from "./admin/admin-user-info/admin-user-info.component";
import {UserSettingsComponent} from "./user-settings/user-settings.component";
import {FeatureGuard} from "./core/services/feature.guard";
import {ReceiptInspectComponent} from "./features/receipt/receipt-inspect/receipt-inspect.component";
import {AiChatComponent} from "./pages/ai-chat/ai-chat.component";

export const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'pricing', component: PricingComponent, canActivate: [AuthGuard]},
  {path: 'contact', component: ContactFormComponent},
  {path: 'web/privacy', component: PrivacyPolicyComponent},
  {path: 'error', component: ErrorComponent},
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: {roles: ['ROLE_ADMIN']},
    children: [
      {path: '', redirectTo: 'users', pathMatch: 'full'},
      {path: 'users', component: AdminUsersComponent},
      {path: 'user/:id', component: AdminUserInfoComponent},
    ]
  },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [AuthGuard],
    children: [
      {path: '', component: HomeComponent, pathMatch: 'full'},
      {path: 'home', component: HomeComponent, pathMatch: 'full'},
      {path: 'invoices', component: Invoices},
      {path: 'invoice/new', component: CreateNewInvoice},
      {
        path: 'xml-convertor',
        component: XmlConvertorComponent,
        canActivate: [FeatureGuard],
        data: {feature: 'EKON_SPECIALTY'}
      },
      {path: 'web/receipts', component: ReceiptsComponent},
      {path: 'web/receipts/:id', component: ReceiptInspectComponent},
      {path: 'web/company/:id', component: CompanyDetailsComponent},
      {path: 'web/invoices/:id', component: InvoiceDisplay},
      {path: 'web/limiter', component: InvoiceLimiterComponent},
      {path: 'web/mobile-app', component: MobileAppComponent},
      {path: 'web/data-transfer', component: DataTransferTableComponent},
      {path: 'web/data-transfer/:id', component: DataTransferDetailComponent},
      {path: 'web/settings', component: UserSettingsComponent},
      {path: 'web/ai-chat', component: AiChatComponent}
    ]
  },

  {path: '**', component: ErrorComponent}
];
