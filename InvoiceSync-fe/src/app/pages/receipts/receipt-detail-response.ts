import { Partner } from '../../core/models/partner';
import { ReceiptItem } from '../../core/models/receipt-item';
import { Company } from '../../core/models/company';
import { Details } from './details';

export interface ReceiptDetailResponse {
  id?: number;
  orderNumber?: number;
  receiptNumber?: string;
  paymentType?: string;
  createdAt?: string;
  receiptDetails?: Details;
  partner: Partner;
  items?: ReceiptItem[];
  company?: Company;
}
