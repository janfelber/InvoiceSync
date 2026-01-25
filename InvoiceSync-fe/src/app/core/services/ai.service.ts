import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {ApiService} from "../auth/api";

@Injectable({
  providedIn: 'root'
})

export class AiService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.ai.BASE;

  constructor(
    private apiService: ApiService
  ) { }

  getUserConversations():Promise<any> {
    return this.apiService.instance.get(
      `${this.baseUrl}${ApiPaths.ai.ALL_CONVERSATIONS}`
    )
  }

  getMessagesForConversation(conversationId: number):Promise<any> {
    return this.apiService.instance.get(
      `${this.baseUrl}${ApiPaths.ai.CONVERSATION_MESSAGES(conversationId)}`
    )
  }
}
