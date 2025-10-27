import {ReceiptResponseDto} from "./receipts/receipt-response-dto";
import {UserResponse} from "./user-response";


export interface PageResponseUsers {
  content: Array<UserResponse>;
  first?: boolean;
  last?: boolean;
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
