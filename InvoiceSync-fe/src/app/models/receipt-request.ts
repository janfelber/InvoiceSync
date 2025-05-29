import {ReceiptItem} from "../servicesss/models/receipt-item";

export interface ReceiptRequest {
  date?:String;
  receiptNumber?: String;
  isPaidByCard?:boolean;
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
