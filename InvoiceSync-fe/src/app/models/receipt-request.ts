export interface ReceiptRequest {
  receiptDetails?: {
    numberRequested?: string;
    date?: string;
    datePayment?: string;
    dateTax?: string;
    accountValue?: string;
    classificationVAT?: string;
    classificationKVVAT?: string;
    description?: string;
  }
  partner?: {
    name?: string;
    city?: string;
    street?: string;
    zip?: string;
    registrationNumber?: string;
    taxId?: string;
    vatId?: string;
  }
  myIdentity?: {
    name?: string;
    city?: string;
    street?: string;
    zip?: string;
    registrationNumber?: string;
    taxId?: string;
    vatId?: string;
  },
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
