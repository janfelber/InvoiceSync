

export interface Details {
  accountValue: string,
  paidByCard?: boolean,
  classificationKVVAT?: string,
  classificationVAT?: string,
  date?: string;
  datePayment?: string;
  dateTax?: string
  description?: string;
  totalPriceWithVat?: string;
  totalPriceWithoutVat?: string;
}
