import {MonetaryAmountDto} from "./monetary-amount-dto";
import {AccountingAssignment} from "./accounting-assignment";

export interface AccountingLineItem {

  id: number;
  name: string;
  quantity: number;
  unit: string;
  vatRate: number;
  unitPrice: MonetaryAmountDto;
  totalPrice: MonetaryAmountDto;
  accounting: AccountingAssignment;
}
