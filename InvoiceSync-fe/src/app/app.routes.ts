import { Routes } from '@angular/router';
import { Invoices } from './invoices/invoices.component';

export const routes: Routes = [
    {path:'', redirectTo:'invoices', pathMatch: 'full'},
    {path: 'invoices', component: Invoices},
    {path: 'test', component: Invoices},
];
