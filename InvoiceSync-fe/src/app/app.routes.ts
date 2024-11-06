import { Routes } from '@angular/router';
import { Invoices } from './page/invoices/invoices.component';
import { InvoiceInspect } from './invoice-inspect/invoice-inspect.component';
import { LoginComponent } from './page/login/login.component';
import { RegisterComponent } from './page/register/register.component';
import { authGuard } from './services/auth/auth.guard';
import { LayoutComponent } from './layout/layout.component';
import {HomeComponent} from "./page/home/home.component";

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
        path: 'rex',
        component: InvoiceInspect
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'home'
  }
];
