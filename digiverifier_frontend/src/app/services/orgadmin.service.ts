import { HttpClient, HttpRequest, HttpHeaders, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { data } from 'jquery';

@Injectable({
  providedIn: 'root'
})
export class OrgadminService {

  constructor(private http:HttpClient) { }
  
  getOrgusers(organizationId:number){
    return this.http.get(`${environment.apiUrl}/api/user/getUserByOrganizationId/` + organizationId);
  }
  getOrgroles(){
    
    return this.http.get(`${environment.apiUrl}/api/role/getRoleDropDownByUser`);
  }
  getOrgVendor(organizationId:number){

    return this.http.get(`${environment.apiUrl}/api/user/getVendorList/` + organizationId);

  }
  Addrole(data:any){
    console.log("______________________calling api_________________________")
    return this.http.post(`${environment.apiUrl}/api/role/saveNUpdateRole`,data);
  }
  getSupervisor(organizationId:number){
    return this.http.get(`${environment.apiUrl}/api/user/getAgentSupervisorList/` + organizationId);
  }
  saveOrgusers(data: any){
    return this.http.post(`${environment.apiUrl}/api/user/saveNUpdateUser`, data);
  }
  orguserStat(userId : any, isActive: any){
    return this.http.put(`${environment.apiUrl}/api/user/activeNInAtiveUser/${userId}/${isActive}`, userId );
  }
  getUserbyId(userId:number){
    return this.http.get(`${environment.apiUrl}/api/user/getUserById/` + userId);
  }

  // Api to Search by UAN number
  searchByUan(data:any){
    console.warn("SERVICE::",data);
    return this.http.post(`${environment.apiUrl}/api/allowAll/getEpfodetail`, data)
  }

  getCandidateCodeByApplicantId(saveUan:any){
    console.warn("Service_Applicant_ID::",saveUan);
    return this.http.post(`${environment.apiUrl}/api/candidate/getCandidateCode/`,saveUan)
  }

  updateData(updateUandataAfterFetching:any){
    console.warn("Service_Applicant_ID::",updateUandataAfterFetching);
    return this.http.post(`${environment.apiUrl}/api/candidate/getCandidateCode`,updateUandataAfterFetching)

  }

  getUanSearchData(Data:any){
    console.warn("ApplicantId:: In Service",Data);
    return this.http.post(`${environment.apiUrl}/api/candidate/uanSearchData`,Data);
  }


  getBulkUanSearch(Data:any){
    console.warn("ApplicantId:: In ServiceFor Bulk",Data);

    return this.http.post(`${environment.apiUrl}/api/candidate/bulkUanSearch`,Data);
  }

  retriveBulkUanData(Data:any){
    console.warn("ReBulkUan::>>",data)
    return this.http.post(`${environment.apiUrl}/api/candidate/getBulkUanData`,Data);
  }

  getDownloadFile(Data:any){
    console.warn("DownloadFile::Data",Data);
    return this.http.post(`${environment.apiUrl}/api/candidate/getEpfoData`,Data);

  }

  uploadAgent(file: File): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    const req = new HttpRequest('POST', `${environment.apiUrl}/api/user/uploadAgent`, formData, {
      reportProgress: true,
      responseType: 'json'
    });
    return this.http.request(req);
  }

  uploadCandidate(file: File): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    const req = new HttpRequest('POST', `${environment.apiUrl}/api/candidate/uploadCandidate`, formData, {
      reportProgress: true,
      responseType: 'json'
    });
    return this.http.request(req);
  }

  uploadClientscope(file: File): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    const req = new HttpRequest('POST', `${environment.apiUrl}/api/organization/uploadClientscope`, formData, {
      reportProgress: true,
      responseType: 'json'
    });
    return this.http.request(req);
  }

  uploadFakeCompanyDetails(file: File,id:any): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    formData.append('organizationId', id);
    const req = new HttpRequest('POST', `${environment.apiUrl}/api/candidate/uploadFakeCompanyDetails`, formData, {
      reportProgress: true,
      responseType: 'json'
    });
    return this.http.request(req);
  }

  getRoleDropdown(){
    return this.http.get(`${environment.apiUrl}/api/role/getRoleDropDownByUser`);
  }

  getAllRolePermission(){
    return this.http.get(`${environment.apiUrl}/api/role/getAllRolePermission`);
  }
  saveRoleMgmt(data: any){
    return this.http.post(`${environment.apiUrl}/api/role/rolePermission`, data);
  }

  getRoleMgmtStat(roleId:number){
    return this.http.get(`${environment.apiUrl}/api/role/getAllUserRolePerMissionMap/${roleId}`);
  }

  getRolePerMissionCodes(roleCode:any){
    return this.http.get(`${environment.apiUrl}/api/role/getRolePerMissionCodes/${roleCode}`);
  }

}
