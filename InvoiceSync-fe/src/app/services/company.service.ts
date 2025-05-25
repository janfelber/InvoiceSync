import {Injectable} from "@angular/core";
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {AxiosService} from "../axios.service";
import {ApiPaths} from "./api-paths";
import {CompanyRequest} from "../servicesss/models/company-request";

@Injectable({
  providedIn: 'root'
})

export class CompanyService {


  private baseUrl: string = environment.apiUrl.replace(/\/$/, '') + ApiPaths.company.BASE;

  constructor(
    private axiosService: AxiosService
  ) { }

  /**
   * Retrieves all companies associated with the currently logged-in user.
   *
   * Sends a GET request to: `/company/user?page={page}&size={size}`
   *
   * @param params - Pagination parameters.
   * @param params.page - The current page number (0-based).
   * @param params.size - The number of companies to retrieve per page.
   * @returns A Promise resolving to the server's response containing the list of companies.
   */
  findAllCompaniesByUser(params?: { page?: number; size?: number }): Promise<any> {
    const queryParams = [];
    if (params?.page !== undefined) queryParams.push(`page=${params.page}`);
    if (params?.size !== undefined) queryParams.push(`size=${params.size}`);
    const queryString = queryParams.length > 0 ? `?${queryParams.join('&')}` : '';

    return this.axiosService.request(
      'GET',
      `${this.baseUrl}${ApiPaths.company.FIND_ALL_BY_USER}${queryString}`,
      null
    );
  }

  /**
   * Get company details by ID
   * @param params Object with companyId property
   * @returns Promise resolving to company details
   */
  getCompanyById(params: { companyId: number }): Promise<any> {
    const url = `${this.baseUrl}${ApiPaths.company.BY_ID(params.companyId)}`;
    return this.axiosService.request('GET', url, null);
  }

  /**
   * Saves a new company to the backend.
   *
   * Sends a POST request to: `/company/save`
   *
   * @param company - The company data to be saved.
   * @returns A Promise resolving to the server's response after creating the company.
   */
  saveCompany(company: CompanyRequest): Promise<any> {
    return this.axiosService.request(
      'POST', `${this.baseUrl}${ApiPaths.company.SAVE}`, company
    );
  }

  /**
   * Updates an existing company in the backend.
   *
   * Sends a POST request to: `/company/update/{companyId}`
   *
   * @param params - An object containing the ID of the company to update and the updated data.
   * @param params.companyId - The ID of the company to be updated.
   * @param params.company - The updated company data.
   * @returns A Promise resolving to the server's response after updating the company.
   */
  updateCompany(params: { companyId: number, company: CompanyRequest }): Promise<any> {
    return this.axiosService.request(
      'POST', `${this.baseUrl}${ApiPaths.company.UPDATE_BY_ID(params.companyId)}`, params.company
    );
  }

  /**
   * Deletes a company by its ID.
   *
   * Sends a DELETE request to: `/company/delete/{companyId}`
   *
   * @param params - An object containing the ID of the company to delete.
   * @param params.companyId - The ID of the company to be deleted.
   * @returns A Promise resolving to the server's response confirming deletion.
   */
  deleteCompany(params: { companyId: number }): Promise<any> {
    return this.axiosService.request(
      'DELETE', `${this.baseUrl}${ApiPaths.company.DELETE_BY_ID(params.companyId)}`, null
    );
  }
}
