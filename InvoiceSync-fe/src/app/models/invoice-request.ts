export interface InvoiceRequest {
  invoiceDetails?: {
    invoiceType?: string;
    invoiceNumber?: string;
    variableSymbol?: string;
    pairingSymbol?: string;
    dateInvoice?: string;
    dateTax?: string;
    dateDue?: string;
    dateAccounting?: string;
  };
  partner?: {
    name?: string;
    city?: string;
    street?: string;
    zip?: string;
    vatId?: string;
  };
  items?: Array<{
    quantity?: number;
    unitPrice?: number;
    price?: number;
    priceVAT?: number;
    priceSum?: number;
  }>;
  myIdentity?: {
    name?: string;
    city?: string;
    street?: string;
    streetNumber?: string;
    zip?: string;
    registrationNumber?: string;
    taxId?: string;
    vatId?: string;
  };
}
