import { Routes } from '@angular/router';
import { Invoices } from './invoices/invoices.component';
import { InvoiceDisplay } from './invoice-inspect-display/invoice-inspect-display.component';
import { InvoiceInspect } from './invoice-inspect/invoice-inspect.component';
import {LoginComponent} from "./login/login.component";
import {WelcomeComponent} from "./welcome/welcome.component";
import {RegisterComponent} from "./register/register.component";

export const routes: Routes = [
    // {path:'', redirectTo:'invoices', pathMatch: 'full'},
    // {path: 'invoices', component: Invoices},
    // {path: 'test', component: InvoiceInspect},
  {
    path: 'login', component: LoginComponent
  },
  {
    path: 'register', component: RegisterComponent
  },
  {
    path: 'welcome', component: WelcomeComponent
  }

];
