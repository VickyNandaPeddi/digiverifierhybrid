import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders ,HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs';

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
      `${environment.apiUrl}/api/user/saveInitiateVendorChecks`,
      data
    );
  }
  getCustomersBill() {
    return this.http.get(
      `${environment.apiUrl}/api/organization/getOrganizationListAfterBilling`
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

  getCandidatePurgedReport(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/report/getCandidatePurgedReport`,
      data
    );
  }

  getCustomerUtilizationReportByAgent(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/report/getCustomerUtilizationReportByAgent`,
      data
    );
  }

  getCandidatePurgedReportByAgent(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/report/getCandidatePurgedReportByAgent`,
      data
    );
  }

  getCanididateDetailsByStatus(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/report/getCanididateDetailsByStatus`,
      data
    );
  }

  getPurgedCanididateDetailsByStatus(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/report/getPurgedCanididateDetailsByStatus`,
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
    return this.http.get(`${environment.apiUrl}/api/report/eKycReport`, data);
  }

  posteKycReport(data: any) {
    return this.http.post(`${environment.apiUrl}/api/report/eKycReport`, data);
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
      `${environment.apiUrl}/api/user/saveproofuploadVendorChecks`,
      data
    );
  }

  getallVendorCheckDetails(vendorId: any,dateSearchFilter:any,pageNumber:any,pageSize:any,vendorCheckDashboardStatusCode:any) {
    console.log(vendorId, 'vendorId');

    const requestBody = { dateSearchFilter, pageNumber, pageSize,vendorCheckDashboardStatusCode };
    return this.http.post(
      `${environment.apiUrl}/api/user/getVendorCheck/${vendorId}`,requestBody);
  }

  postallVendorCheckDetails(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/user/getallVendorCheckDetails`,
      data
    );
  }

  getAgentAttributes(checkId: any) {
    // const params = new HttpParams().set('type', 'TECHM');
    console.log("SOURCE ID : ",checkId)
    return this.http.get(
      `${environment.apiUrl}/api/user/getConventionalAttributesMaster/${checkId}`
    );
  }

  getAgentAttributesForTECHM(checkId: any) {
    const params = new HttpParams().set('type', 'TECHM');
    return this.http.get(
      `${environment.apiUrl}/api/user/getConventionalAttributesMaster/${checkId}`, { params: params }
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

  deleteCust(custId: number) {
    return this.http.get(
      `${environment.apiUrl}/api/organization/deleteOrg/${custId}`
    );  }

    saveCustomersEmailTemplates(data: any, custId: any, emailTemplateId:any) {
      return this.http.post(
        `${environment.apiUrl}/api/organization/saveAndUpdateOrgEmailTemplates/${custId}/${emailTemplateId}`,
        data
      );
    }

    saveCustomersEmailTemplates1(data: any, custId: any, emailTemplateId:any) {
      return this.http.post(
        `${environment.apiUrl}/api/organization/saveAndUpdateOrgEmailTemplates/${custId}/${emailTemplateId}`,
        data
      );
    }
    

    getCustomerEmailTemplates(custId: any) {
      return this.http.get(
        `${environment.apiUrl}/api/organization/getOrgEmailTemplates/${custId}`
      );
    }

    getCustmerEmailTemplatesForReview(custId:any,loaMail:any,invitationMail:any){

      const data = {
        orgId:custId,
        invitationMail: invitationMail,
        loaMail:loaMail
      }

      console.warn("DATA::::",data)

      return this.http.get(
        `${environment.apiUrl}/api/organization/getOrgEmailTemplateReview/${custId}/${invitationMail}/${loaMail}`
      );
    }

    updateVendor(data:any){
      return this.http.post(`${environment.apiUrl}/api/user/updateVendor`,data)
    }

    stopCheck(data:any){
      return this.http.post(`${environment.apiUrl}/api/user/stopCheck`,data)
    }

    addCheckByAdmin(data:any){
      return this.http.post(`${environment.apiUrl}/api/user/addChecks`,data)

    }

    getAllSources(){
      return this.http.get(`${environment.apiUrl}/api/user/getAllSource`)

    }

    deleteCheckBySourceId(sourceId:any){
      return this.http.post(`${environment.apiUrl}/api/user/deleteCheckBySourceId`,sourceId)
    }

    getCustomerStatusTrackerReport(data: any): Observable<any> {
      const headers = new HttpHeaders({
        'Content-Type': 'application/json',
        'Accept': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      });
      return this.http.post(`${environment.apiUrl}/api/report/download/candidateStatusTrackerReport`,data, {
        headers: headers,
        responseType: 'blob' as 'json'
      });
    }

    getCandidateEmployentReport(data: any): Observable<any> {
      const headers = new HttpHeaders({
        'Content-Type': 'application/json',
        'Accept': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      });
      return this.http.post(`${environment.apiUrl}/api/report/download/downloadCandidateEmploymentReport`,data, {
        headers: headers,
        responseType: 'blob' as 'json'
      });
    }

    inSuffRemarks(data:any){
      return this.http.post(`${environment.apiUrl}/api/user/raiseInsufficiency`,data)

    }

    getVendorReportAttributes(sourceID: any,type:any) {
      return this.http.get(`${environment.apiUrl}/api/user/getConventionalAttributesMaster/${sourceID}?type=${type}`);
      }

      getVendorReportAttributesForTECHM(sourceID: any,type:any) {
        return this.http.get(`${environment.apiUrl}/api/user/getConventionalAttributesMaster/${sourceID}?type=${'TECHM'}`);
        }

      getProfile(userId:any) {
        return this.http.get(`${environment.apiUrl}/api/allowAll/getProfile/${userId}`);
      }

      updatePassword(data: any) {
        return this.http.post(
          `${environment.apiUrl}/api/allowAll/updatePassword`,
          data
        );
      }

      resetPassword(data:any){
        return this.http.post(
          `${environment.apiUrl}/api/allowAll/forgotPassword`,
          data
        )
      }

      getPasswordPolicyDayFromConfig() {
        return this.http.get(
          `${environment.apiUrl}/api/organization/getPasswordPolicyDayFromConfig`,
        );
      }

      getCustomerUtilizationDBCounts(data: any) {
        return this.http.post(
          `${environment.apiUrl}/api/report/getCustomerUtilizationDashboardCounts`,
          data
        );
      }
}
