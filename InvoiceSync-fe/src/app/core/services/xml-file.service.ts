import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {AxiosService} from "../axios.service";
import {ApiService} from "../auth/api";

@Injectable({
  providedIn: 'root'
})

export class XmlFileService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.xmlFile.BASE;

  constructor(
    private apiService: ApiService
  ) {
  }

  /**
   * Retrieves all xml-file associated with the currently logged-in user.
   *
   * Sends a GET request to: `/xml-file/user?page={page}&size={size}`
   *
   * @param params - Pagination parameters.
   * @param params.page - The current page number (0-based).
   * @param params.size - The number of companies to retrieve per page.
   * @returns A Promise resolving to the server's response containing the list of xml-files.
   */

  findAllXmlFilesByUser(params?: { page?: number; size?: number }): Promise<any> {
    const queryParams = [];
    if (params?.page !== undefined) queryParams.push(`page=${params.page}`);
    if (params?.size !== undefined) queryParams.push(`size=${params.size}`);
    const queryString = queryParams.length > 0 ? `?${queryParams.join('&')}` : '';

    return this.apiService.instance.get(
      `${this.baseUrl}${ApiPaths.xmlFile.FIND_ALL_BY_USER}${queryString}`
    );
  }

  saveXmlFile(params: { files: File[] }): Promise<any> {
    const formData = new FormData();
    for (const file of params.files) {
      formData.append('file', file, file.name);
    }

    return this.apiService.instance.post(
      `${this.baseUrl}${ApiPaths.xmlFile.SAVE}`,
      formData
    )
  }

  // TODO implement export XML content
}
