import {Injectable} from "@angular/core";
import {environment} from "../../../environments/environment";
import {ApiPaths} from "./api-paths";
import {AxiosService} from "../axios.service";
import {HttpHeaders} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})

export class ConvertService {

  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.convert.BASE;

  constructor(
    private axiosService: AxiosService
  ) { }

  findAllConvertsByUser(params?: { page?: number; size?: number }): Promise<any> {
    const queryParams = [];
    if (params?.page !== undefined) queryParams.push(`page=${params.page}`);
    if (params?.size !== undefined) queryParams.push(`size=${params.size}`);
    const queryString = queryParams.length > 0 ? `?${queryParams.join('&')}` : '';

    return this.axiosService.request(
      'GET',
      `${this.baseUrl}${ApiPaths.convert.FIND_ALL_BY_USER}${queryString}`,
      null
    )
  }

  getConvertById(params: {convertId: number}): Promise<any> {
    return this.axiosService.request(
      'GET', `${this.baseUrl}${ApiPaths.convert.BY_ID(params.convertId)}`, null
    )
  }


  saveConvert(params: { files: File[] }): Promise<any> {
    const formData = new FormData();
    for (const file of params.files) {
      formData.append('file', file, file.name);
    }

    return this.axiosService.request(
      'POST',
      `${this.baseUrl}${ApiPaths.convert.UPLOAD}`,
      formData
    );
  }
}
