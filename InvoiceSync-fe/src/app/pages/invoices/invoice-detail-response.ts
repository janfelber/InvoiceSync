import {InvoiceDetails} from "./invoice-details";
import {Partner} from "../../core/models/partner";
import {Company} from "../../core/models/company";
import {ReceiptItem} from "../../core/models/receipt-item";


export interface InvoiceDetailResponse {
  id?: number;
  createdAt?: string;
  invoiceDetails?: InvoiceDetails;
  partner?: Partner;
  items?: ReceiptItem[];
  company?: Company;
}
