import { Routes } from '@angular/router';
import { Invoices } from './invoices/invoices.component';
import { InvoiceInspect } from './invoice-inspect/invoice-inspect.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { authGuard } from './services/auth/auth.guard';
import { LayoutComponent } from './layout/layout.component';
import {HomeComponent} from "./home/home.component";

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
