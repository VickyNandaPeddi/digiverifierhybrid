import { Injectable } from '@angular/core';
import { HttpClient,  HttpHeaders} from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { AuthenticationService } from './authentication.service';
import { Observable } from 'rxjs';
@Injectable({
  providedIn: 'root'
})
export class OrgadminDashboardService {

  token: any = null;
  constructor( private http:HttpClient, private authService: AuthenticationService) { 
    this.token = this.authService.getToken();
  }
  
  getUploadDetails(data:any){
    return this.http.post(`${environment.apiUrl}/api/candidate/getCandidateStatusAndCount`, data);
  }
  conventionalGetUploadDetails(data:any){
    return this.http.post(`${environment.apiUrl}/api/candidate/ConventionalGetCandidateStatusAndCount`, data);
  }
  getChartDetails(data:any){
    return this.http.post(`${environment.apiUrl}/api/candidate/candidateList`, data);
  }
  // this is for Conventional
  getConventionalChartDetails(data:any){
    return this.http.post(`${environment.apiUrl}/api/candidate/conventionalCandidateList`, data);
  }
  //ADDED BELOW FUNCTION FOR GETTING PENDING LIST TILL NOW
  getPendingChartDetails(data:any){
    return this.http.post(`${environment.apiUrl}/api/candidate/pendingCandidateList`, data);
  }
  saveInvitationSent(data: any){
    return this.http.post(`${environment.apiUrl}/api/candidate/invitationSent`, data);
  }
  saveConvitaionalInvitationSent(data: any){
    return this.http.post(`${environment.apiUrl}/api/candidate/conventionalInvitationSent`, data);
  }
  putAgentStat(referenceNo: any){
    return this.http.put(`${environment.apiUrl}/api/candidate/cancelCandidate/${referenceNo}`, referenceNo);
  }
  conventionalProcessDeclined(referenceNo: any){
    return this.http.put(`${environment.apiUrl}/api/candidate/conventionalCancelCandidate/${referenceNo}`, referenceNo);
  }
  getCandidateDetails(referenceNo: any){
    return this.http.get(`${environment.apiUrl}/api/candidate/getCandidate/${referenceNo}`, referenceNo);
  }
  putCandidateData(referenceNo: any){
    return this.http.put(`${environment.apiUrl}/api/candidate/updateCandidate`, referenceNo);
  }
  conventionalPutCandidateData(referenceNo: any){
    return this.http.put(`${environment.apiUrl}/api/candidate/conventionalUpdateCandidate`, referenceNo);
  }

  public setStatusCode(statCode: string){
    localStorage.setItem('statCode', statCode);
    localStorage.removeItem('reportDeliverystatCode');
    localStorage.removeItem('PendingDetailsStatCode');
    localStorage.removeItem('ConventionalReportDeliverystatCode');
    localStorage.removeItem('conventionalStatCode');
  }
  public getStatusCode(){
    return localStorage.getItem('statCode');
  }

  public setConventionalStatusCode(statCode: string){
    localStorage.setItem('conventionalStatCode', statCode);
    localStorage.removeItem('reportDeliverystatCode');
    localStorage.removeItem('PendingDetailsStatCode');
    localStorage.removeItem('ConventionalReportDeliverystatCode');
    localStorage.removeItem('statCode');
  }
  public getConventionalStatusCode(){
    return localStorage.getItem('conventionalStatCode');
  }


  public setReportDeliveryStatCode(reportDeliverystatCode: string){
    localStorage.setItem('reportDeliverystatCode', reportDeliverystatCode);
    localStorage.removeItem('statCode');
    localStorage.removeItem('PendingDetailsStatCode');
    localStorage.removeItem('ConventionalReportDeliverystatCode');
    localStorage.removeItem('conventionalStatCode');

  }
  public getReportDeliveryStatCode(){
    return localStorage.getItem('reportDeliverystatCode');
  }

  public setConventionalReportDeliveryStatCode(reportDeliverystatCode: string){
    localStorage.setItem('ConventionalReportDeliverystatCode', reportDeliverystatCode);
    localStorage.removeItem('statCode');
    localStorage.removeItem('PendingDetailsStatCode');
    localStorage.removeItem('reportDeliverystatCode');
    localStorage.removeItem('conventionalStatCode');
  }
  public getConventionalReportDeliveryStatCode(){
    return localStorage.getItem('ConventionalReportDeliverystatCode');
  }

  public setPendingDetailsStatCode(PendingDetailsStatCode: string){
    localStorage.setItem('PendingDetailsStatCode', PendingDetailsStatCode);
    localStorage.removeItem('statCode');
    localStorage.removeItem('reportDeliverystatCode');
    localStorage.removeItem('ConventionalReportDeliverystatCode');
    localStorage.removeItem('conventionalStatCode');

  }
  public getPendingDetailsStatCode(){
    return localStorage.getItem('PendingDetailsStatCode');
  }

  getReportDeliveryDetails(data:any){
    return this.http.post(`${environment.apiUrl}/api/candidate/getReportDeliveryDetailsStatusAndCount`, data);
  }

  conventionalGetReportDeliveryDetails(data:any){
    return this.http.post(`${environment.apiUrl}/api/candidate/ConventionalGetReportDeliveryDetailsStatusAndCount`, data);
  }

  getPendingDetailsStatusAndCount(data:any){
    return this.http.post(`${environment.apiUrl}/api/candidate/getPendingDetailsStatusAndCount`, data);
  }

  conventionalGetPendingDetailsStatusAndCount(data:any){
    return this.http.post(`${environment.apiUrl}/api/candidate/ConventionalGetCandidateStatusAndCount`, data);
  }

  getUserByOrganizationIdAndUserId(organizationId:any, userId:any){
    return this.http.get(`${environment.apiUrl}/api/user/getUserByOrganizationIdAndUserId/${organizationId}/${userId}`);
  }

  getUsersByRoleCode(organizationId:any){
    return this.http.get(`${environment.apiUrl}/api/user/getUsersByRoleCode/${organizationId}`);
  }

  getSignedURLForContent(contentId: any) {
    return this.http.get(`${environment.apiUrl}/api/candidate/content?contentId=${contentId}&type=VIEW`);
  }

  getPreSignedUrlByCandidateCode(candidateCode: any,reportStatus:any,isSecondReport: any) { 
    console.log("getPreSignedUrlByCandidateCode::{}",reportStatus);
    return this.http.get(`${environment.apiUrl}/api/report?candidateCode=${candidateCode}&type=INTERIM&overrideReportStatus=${reportStatus}&secondReport=${isSecondReport}`);
  }

  getConventionalReportByCandidateCode(candidateCode:any,reportStatus:any,conventionalReport:any){
    return this.http.get(`${environment.apiUrl}/api/report/getConventionalReport?candidateCode=${candidateCode}&type=CONVENTIONALINTERIM&overrideReportStatus=${reportStatus}&conventionalReport=${conventionalReport}`);
  }

  //TECHM Report INTERIM REPORT
  getConventionalTechMReportByCandidateCode(candidateCode:any,reportStatus:any,conventionalReport:any){
    return this.http.get(`${environment.apiUrl}/api/report/generateTechMConventional?candidateCode=${candidateCode}&type=CONVENTIONALINTERIM&overrideReportStatus=${reportStatus}&conventionalReport=${conventionalReport}`);
  }

  //TECHM conventional FINAL REPORT
  getConventionalTechMReportByCandidateCodeFinalReport(candidateCode:any,reportStatus:any,conventionalReport:any){
    return this.http.get(`${environment.apiUrl}/api/report/generateTechMConventional?candidateCode=${candidateCode}&type=${reportStatus}&overrideReportStatus=${reportStatus}&conventionalReport=${conventionalReport}`);
  }

  //OverAllSearch
  getAllSearchData(searchData:any){
    return this.http.post(`${environment.apiUrl}/api/candidate/searchAllCandidate`, searchData);
  }
  conventionalDashboardSearchData(searchData:any){
    return this.http.post(`${environment.apiUrl}/api/candidate/conventionalSearchAllCandidate`, searchData);
  }

  getPreSignedUrlByCandidateCodeForFinal(candidateCode: any,reportStatus:any) { 
    return this.http.get(`${environment.apiUrl}/api/report?candidateCode=${candidateCode}&type=FINAL&overrideReportStatus=${reportStatus}`);
  }

  //OverAllSearch for Vendor
  getAllSearchDataForVendor(searchData:any){
    return this.http.post(`${environment.apiUrl}/api/user/searchByVendorId`, searchData);
  }

  getPreOfferRegenerationCall(candidateCode: any) { 
    return this.http.get(`${environment.apiUrl}/api/candidate/CandidateCode?CandidateCode=${candidateCode}`);
  }

  downloadUploadResourceFile(uploadFor: any,uploadType: any): Observable<Blob> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': this.token
    });

    return this.http.get(`${environment.apiUrl}/api/user/getFilesFromResource/${uploadFor}/${uploadType}`, { headers, responseType: 'blob' });
  }

  getLOAPdf(candidateCode: any,dashboardStatus:any): Observable<any> {
    return this.http.get(`${environment.apiUrl}/api/candidate/getCandidateLOAFile/${candidateCode}/${dashboardStatus}`);
  }

  refetchUANData(data: any){
    return this.http.post(`${environment.apiUrl}/api/candidate/reFetchUANData`, data);
  }
  
  refetchPanToUANData(data: any){
    return this.http.post(`${environment.apiUrl}/api/candidate/reFetchPanToUANData`, data);
  } 

  downloadAgentUploadedDocument(documentPathKey:any,viewDocument:any){
    return this.http.post(`${environment.apiUrl}/api/user/getAgentUploadedProof/${viewDocument}`,documentPathKey);
  }

  clientApprove(vendorCheckId:any){
    return this.http.post(`${environment.apiUrl}/api/user/clientApprove`,vendorCheckId);
  }

  getServiceConfigForOrg(orgId: any) {
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/getServiceConfigForOrg/${orgId}`,
      orgId
    );
  }

  forwardReport(candidateIds: any, emailIds: any) {
    const formData: FormData = new FormData();
    formData.append('candidateIds', candidateIds);
    formData.append('emailIds', emailIds);
    // const req = new HttpRequest('POST', `${environment.apiUrl}/api/candidate/resumeParser`, formData, {
    //   reportProgress: true,
    //   responseType: 'json'
    // });
    // return this.http.request(req);
    return this.http.post(
      `${environment.apiUrl}/api/candidate/forwardReport`, formData, {
          reportProgress: true,
          responseType: 'json'
        }
    );
  }

  getEcourtProof(searchData: any) {
    return this.http.post(`${environment.apiUrl}/api/user/getECourtProof`, searchData);
  }

  oldCandidatePurge(orgId: any) { 
    return this.http.get(`${environment.apiUrl}/api/candidate/oldCandidatesPurge/${orgId}`);
  }

  conventionalReferenceData(data:any){
    return this.http.post(`${environment.apiUrl}/api/candidate/saveConventionalReferenceData`,data);
  }
 
}
