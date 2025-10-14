import {PostingAccountResponseTable} from "./posting-account-response-table";

export interface PageResponsePostingAccount {
  content: Array<PostingAccountResponseTable>;
  first?: boolean;
  last?: boolean;
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
