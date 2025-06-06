import {XmlFileResponseDto} from "../servicesss/models/xml-file-response-dto";

export interface PageResponseXmlFileResponse {
  content: Array<XmlFileResponseDto>;
  first?: boolean;
  last?: boolean;
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
