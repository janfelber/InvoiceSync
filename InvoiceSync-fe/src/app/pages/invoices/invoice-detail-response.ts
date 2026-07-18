import {InvoiceDetails} from "./invoice-details";
import {Partner} from "../../core/models/partner";
import {Company} from "../../core/models/company";
import {ReceiptItem} from "../../core/models/receipt-item";
import {OrganizationDto} from "../../core/models/organization-dto";
import {MonetaryAmountDto} from "../../core/models/monetary-amount-dto";
import {AccountingLineItem} from "../../core/models/accounting-line-item";

export interface InvoiceDetailResponse {
  id: number;
  createdAt: string;
  invoiceDetails: InvoiceDetails;
  totalAmount : MonetaryAmountDto;
  supplier: OrganizationDto;
  items: AccountingLineItem[];
  targetCompany: OrganizationDto;
}
