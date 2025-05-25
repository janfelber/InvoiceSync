export interface ReceiptRequest {
  date?:String;
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

  items?: Array<{
    accountText?: string;
    name?: string;
    quantity?: number;
    priceWithoutVAT: number;
    vatRate: number;
    priceWithVAT: number;
    accountValue?: string
  }>;
}
