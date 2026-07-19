export interface InvoiceDocumentResponse {
  id: number;
  documentName: string | null;
  fileName: string;
  createdAt: string;
  note: string | null;
}
