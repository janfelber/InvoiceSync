import { Routes } from '@angular/router';
import { Invoices } from './invoices/invoices.component';
import { InvoiceDisplay } from './invoice-inspect-display/invoice-inspect-display.component';
import { InvoiceInspect } from './invoice-inspect/invoice-inspect.component';

export const routes: Routes = [
    {path:'', redirectTo:'invoices', pathMatch: 'full'},
    {path: 'invoices', component: Invoices},
    {path: 'test', component: InvoiceInspect},
];
