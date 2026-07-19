import {InvoiceDetails} from "./invoice-details";
import {Partner} from "./partner";


export interface InvoiceResponseTable {
  id: number;
  createdAt: any;
  invoiceDetails: InvoiceDetails
  partner: Partner;
  companyName: string;
}
