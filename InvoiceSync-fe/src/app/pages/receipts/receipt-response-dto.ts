export interface ReceiptResponseDto {
  id: number;
  orderNumber: number;
  createdAt: any;
  companyName: string;
  receiptDetails: {
    totalPriceWithVat: string;
  };
  partner: {
    name: string;
    city: string;
    street: string;
    zip: string;
    registrationNumber: string;
    taxId: string;
    vatId: string;
  };
}
