/* tslint:disable */
/* eslint-disable */

export interface ReceiptResponseDto {
  id: number;
  createdAt: any;
  companyName: string;
  receiptDetails: {
    totalPrice: string
  }
  partner: {
    name: string;
    city: string;
    street: string;
    zip: string;
    registrationNumber: string;
    taxId: string;
    vatId: string;
  }
}
