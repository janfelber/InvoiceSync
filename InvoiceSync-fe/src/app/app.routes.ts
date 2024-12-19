import { Routes } from '@angular/router';
import { Invoices } from './page/invoices/invoices.component';
import { InvoiceInspect } from './invoice-inspect/invoice-inspect.component';
import { LoginComponent } from './page/login/login.component';
import { RegisterComponent } from './page/register/register.component';
import { authGuard } from './services/auth/auth.guard';
import { LayoutComponent } from './layout/layout.component';
import {HomeComponent} from "./page/home/home.component";
import {XmlPageComponent} from "./xml-page/xml-page.component";
import {AdminComponent} from "./admin/admin.component";
import {AdminLayoutComponent} from "./admin-layout/admin-layout.component";

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'home',
        component: HomeComponent
      },
      {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
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
        path: 'xml-import',
        component: XmlPageComponent
      }
    ]
  },
  //admin layout
  {

    path: 'admin',
    component: AdminLayoutComponent,
    children: [
      {
        path: 'users',
        component: AdminComponent
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'home'
  }
];
