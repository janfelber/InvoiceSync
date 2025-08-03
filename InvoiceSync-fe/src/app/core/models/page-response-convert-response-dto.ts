import {ConvertResponseDto} from "./convert-response-dto";

export interface PageResponseConvertResponseDto {
  content: Array<ConvertResponseDto>;
  first?: boolean;
  last?: boolean;
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
