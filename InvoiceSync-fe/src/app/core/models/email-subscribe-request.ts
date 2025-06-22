import {EmailSubscribeType} from "../enums/email-subscribe-type";

export interface EmailSubscribeRequest {
  email: string;
  type: EmailSubscribeType;
}
