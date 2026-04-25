
export type PaymentType = 'CASH' | 'CARD';


export interface Details {
  accountValue: string,
  paymentType: PaymentType,
  classificationKVVAT?: string,
  classificationVAT?: string,
  date?: string;
  datePayment?: string;
  dateTax?: string
  description?: string;
  totalPriceWithVat?: string;
  totalPriceWithoutVat?: string;
}
