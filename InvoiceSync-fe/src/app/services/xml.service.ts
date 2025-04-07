import {environment} from "../../environments/environment";
import {Injectable} from "@angular/core";
import {AxiosService} from "../axios.service";

@Injectable({
  providedIn: 'root'
})

export class XmlService {

  private baseUrl = environment.apiUrl + "/xml-file";

  constructor(
    private axiosService: AxiosService
  ) {
  }

  fetchAllXmlImports() :Promise<any> {
    return this.axiosService.request("GET", `${this.baseUrl}/imports/current-user`, null);
  }

}
