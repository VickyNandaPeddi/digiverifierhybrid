import { Injectable } from '@angular/core';
import { HttpClient} from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { AuthenticationService } from './authentication.service';
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
  getChartDetails(data:any){
    return this.http.post(`${environment.apiUrl}/api/candidate/candidateList`, data);
  }
  saveInvitationSent(data: any){
    return this.http.post(`${environment.apiUrl}/api/candidate/invitationSent`, data);
  }
  putAgentStat(referenceNo: any){
    return this.http.put(`${environment.apiUrl}/api/candidate/cancelCandidate/${referenceNo}`, referenceNo);
  }
  getCandidateDetails(referenceNo: any){
    return this.http.get(`${environment.apiUrl}/api/candidate/getCandidate/${referenceNo}`, referenceNo);
  }
  putCandidateData(referenceNo: any){
    return this.http.put(`${environment.apiUrl}/api/candidate/updateCandidate`, referenceNo);
  }

  public setStatusCode(statCode: string){
    localStorage.setItem('statCode', statCode);
    localStorage.removeItem('reportDeliverystatCode');
    localStorage.removeItem('PendingDetailsStatCode');
  }
  public getStatusCode(){
    return localStorage.getItem('statCode');
  }

  public setReportDeliveryStatCode(reportDeliverystatCode: string){
    localStorage.setItem('reportDeliverystatCode', reportDeliverystatCode);
    localStorage.removeItem('statCode');
    localStorage.removeItem('PendingDetailsStatCode');
  }
  public getReportDeliveryStatCode(){
    return localStorage.getItem('reportDeliverystatCode');
  }

  public setPendingDetailsStatCode(PendingDetailsStatCode: string){
    localStorage.setItem('PendingDetailsStatCode', PendingDetailsStatCode);
    localStorage.removeItem('statCode');
    localStorage.removeItem('reportDeliverystatCode');
  }
  public getPendingDetailsStatCode(){
    return localStorage.getItem('PendingDetailsStatCode');
  }

  getReportDeliveryDetails(data:any){
    return this.http.post(`${environment.apiUrl}/api/candidate/getReportDeliveryDetailsStatusAndCount`, data);
  }

  getPendingDetailsStatusAndCount(data:any){
    return this.http.post(`${environment.apiUrl}/api/candidate/getPendingDetailsStatusAndCount`, data);
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

  getPreSignedUrlByCandidateCode(candidateCode: any) { 
    return this.http.get(`${environment.apiUrl}/api/report?candidateCode=${candidateCode}&type=INTERIM&Authorization=${this.token}`);
  }
}
