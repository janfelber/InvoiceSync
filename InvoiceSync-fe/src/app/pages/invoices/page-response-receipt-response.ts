import {InvoiceResponseTable} from "./invoice-response-table";

export interface PageInvoiceResponse {
  content: Array<InvoiceResponseTable>;
  first?: boolean;
  last?: boolean;
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
