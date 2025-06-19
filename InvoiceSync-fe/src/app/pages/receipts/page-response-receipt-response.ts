/* tslint:disable */
/* eslint-disable */

import {ReceiptResponseDto} from "./receipt-response-dto";

export interface PageResponseReceiptResponse {
  content: Array<ReceiptResponseDto>;
  first?: boolean;
  last?: boolean;
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
