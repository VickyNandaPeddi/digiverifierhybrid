import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  constructor(private http: HttpClient) {}
  getVendorUtilizationReport() {
    return this.http.get(
      `${environment.apiUrl}/api/report/getVendorUtilizationReport`
    );
  }
  postVendorUtilizationReport(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/report/getVendorUtilizationReport`,
      data
    );
  }
  getVendorDetailsByStatus(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/report/getVendorDetailsByStatus`,
      data
    );
  }
  getCustomers() {
    return this.http.get(
      `${environment.apiUrl}/api/organization/getAllOrganization`
    );
  }
  saveCustomers(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/organization/saveOrganization`,
      data
    );
  }
  getCustomersData(organizationId: any) {
    return this.http.get(
      `${environment.apiUrl}/api/organization/getOrganizationById/${organizationId}`,
      organizationId
    );
  }
  customerStat(organizationId: any, isActive: any) {
    return this.http.put(
      `${environment.apiUrl}/api/organization/activeNInAtiveOrganization/${organizationId}/${isActive}`,
      organizationId
    );
  }
  getSources() {
    return this.http.get(`${environment.apiUrl}/api/organization/getAllSource`);
  }
  saveCustomersBill(data: any, organizationId: any) {
    return this.http.post(
      `${environment.apiUrl}/api/organization/saveOrganizationBilling/` +
        organizationId,
      data
    );
  }
  saveVendorChecks(data: any, userId: any) {
    return this.http.post(
      `${environment.apiUrl}/api/organization/saveVendorChecks/` + userId,
      data
    );
  }
  getAllVendorServices(userId: any) {
    return this.http.get(
      `${environment.apiUrl}/api/organization/getAllVendorServices/${userId}`,
      userId
    );
  }
  saveInitiateVendorChecks(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/user/saveInitiateVendorChecks/`,
      data
    );
  }
  getCustomersBill() {
    return this.http.get(
      `${environment.apiUrl}/api/organization/getOrganizationListAfterBilling/`
    );
  }
  getCustAdminDetails(organizationId: number) {
    return this.http.get(
      `${environment.apiUrl}/api/user/getAdminDetailsForOrganization/` +
        organizationId
    );
  }
  saveAdminSetup(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/user/saveNUpdateUser`,
      data
    );
  }
  getColors() {
    return this.http.get(`${environment.apiUrl}/api/organization/getAllColor`);
  }
  getCustConfigs(organizationId: number) {
    return this.http.get(
      `${environment.apiUrl}/api/organization/getAllServicesForConfiguration/` +
        organizationId
    );
  }
  saveCustServiceConfig(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/organization/saveOrganizationServiceConfiguration`,
      data
    );
  }
  getCustconfigDetails(organizationId: number) {
    return this.http.get(
      `${environment.apiUrl}/api/organization/getServiceTypeConfigByOrgId/` +
        organizationId
    );
  }
  getAllServices(organizationId: any) {
    return this.http.get(
      `${environment.apiUrl}/api/organization/getAllServices/${organizationId}`,
      organizationId
    );
  }

  getUserById() {
    return this.http.get(`${environment.apiUrl}/api/user/getUserProfile`);
  }

  getCustomerUtilizationReport() {
    return this.http.get(
      `${environment.apiUrl}/api/report/getCustomerUtilizationReport`
    );
  }

  postCustomerUtilizationReport(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/report/getCustomerUtilizationReport`,
      data
    );
  }

  getCustomerUtilizationReportByAgent(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/report/getCustomerUtilizationReportByAgent`,
      data
    );
  }

  getCanididateDetailsByStatus(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/report/getCanididateDetailsByStatus`,
      data
    );
  }

  getAgentList(organizationId: any) {
    return this.http.get(
      `${environment.apiUrl}/api/user/getAgentList/${organizationId}`
    );
  }
  getVendorList(organizationId: any) {
    return this.http.get(
      `${environment.apiUrl}/api/user/getVendorList/${organizationId}`
    );
  }
  eKycReport(data: any) {
    return this.http.get(`${environment.apiUrl}/api/report/eKycReport/`, data);
  }

  posteKycReport(data: any) {
    return this.http.post(`${environment.apiUrl}/api/report/eKycReport/`, data);
  }

  getAllStatus() {
    return this.http.get(`${environment.apiUrl}/api/candidate/getAllStatus`);
  }
  getShowvalidation(organizationId: any) {
    console.log('---------------calling api-------------', organizationId);
    return this.http.get(
      `${environment.apiUrl}/api/organization/getShowvalid/${organizationId}`
    );
  }
  getVendorCheckDetails(candidateId: any) {
    return this.http.get(
      `${environment.apiUrl}/api/user/getVendorCheckDetails/${candidateId}`
    );
  }

  saveproofuploadVendorChecks(data: any) {
    console.warn('Save Proof:::', data);
    return this.http.post(
      `${environment.apiUrl}/api/user/saveproofuploadVendorChecks/`,
      data
    );
  }

  getallVendorCheckDetails(vendorId: any) {
    console.log(vendorId, 'vendorId');
    return this.http.get(
      `${environment.apiUrl}/api/user/getVendorCheck/${vendorId}`
    );
  }

  postallVendorCheckDetails(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/user/getallVendorCheckDetails`,
      data
    );
  }

  getAgentAttributes(checkId: any) {
    return this.http.get(
      `${environment.apiUrl}/api/user/getConventionalAttributesMaster/${checkId}`
    );
  }

  public setFromDate(statCode: string) {
    localStorage.setItem('dbFromDate', statCode);
  }
  public getFromDate() {
    return localStorage.getItem('dbFromDate');
  }

  public setToDate(statCode: string) {
    localStorage.setItem('dbToDate', statCode);
  }
  public getToDate() {
    return localStorage.getItem('dbToDate');
  }

  getVenorcheckStatus() {
    return this.http.get(
      `${environment.apiUrl}/api/organization/getAllVenorcheckStatus`
    );
  }
}
