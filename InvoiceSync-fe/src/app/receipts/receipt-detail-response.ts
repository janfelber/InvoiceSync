import {Partner} from "../servicesss/models/partner";
import {ReceiptItem} from "../servicesss/models/receipt-item";
import {Company} from "../servicesss/models/company";
import {Details} from "./details";

export interface ReceiptDetailResponse {
  id?: number;
  paymentType?: string
  createdAt?: string;
  receiptDetails?: Details;
  partner?: Partner;
  items?: ReceiptItem[];
  company?: Company;
}
