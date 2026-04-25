import {ReceiptItem} from "./receipt-item";
import {PaymentType} from "../../pages/receipts/details";

export interface ReceiptRequest {
  date?:String;
  receiptNumber?: String;
  paymentType:PaymentType;
  datePayment?:String;
  dateTax?:String;
  partnerName?:String;
  partnerCity?:String;
  partnerStreet?:String;
  partnerZip?: String;
  partnerRegistrationNumber?:String;
  partnerTaxId?:String;
  partnerVatId?:String;
  accounting?:String;
  classificationVAT?:String;
  classificationKVVAT?:String;
  description?:String;
  totalPrice?:String;

  items: ReceiptItem[]
}
