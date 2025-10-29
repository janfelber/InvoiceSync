import {SubscriptionShort} from "./subscription.short";

export interface UserInfoResponse {
  uuid: string;
  username: string;
  email: string;
  fullName: string;
  role: string;
  createdOn: string;
  phoneNumber: string;
  subscriptionPlan: SubscriptionShort;
}
