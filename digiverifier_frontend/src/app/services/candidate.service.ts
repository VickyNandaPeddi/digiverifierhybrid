import {
  HttpClient,
  HttpRequest,
  HttpHeaders,
  HttpEvent,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CandidateService {
  constructor(private http: HttpClient) {}

  enterUanDataInQcPending(candidateCode: number, enterUanInQcPending: any) {
    console.warn('EnterUan in canService::', enterUanInQcPending);

    return this.http.get(
      `${environment.apiUrl}/api/allowAll/candidateApplicationFormDetails/${candidateCode}?enterUanInQcPending=${enterUanInQcPending}`
    );
  }

  suspectEmpCheck(companyName: any, orgId: any) {
    return this.http.get(
      `${environment.apiUrl}/api/candidate/suspectEmpMasterCheck/${companyName}/${orgId}`
    );
  }
  saveLtrAccept(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/createAccessCodeUriForSelf`,
      data
    );
  }
  conventionalSaveLtrAccept(data:any){
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/conventional/conventionalCreateAccessCodeUriForSelf`,
      data
    );
  }
  saveLtrDecline(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/declineAuthLetter`,
      data
    );
  }
  getCandidateFormData(candidateCode: number) {
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/candidateApplicationFormDetails/${candidateCode}`
    );
  }

  getCandidateApplicationFormSubmit(candidateCode: number) {
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/candidateApplicationFormSubmit/${candidateCode}`
    );
  }
  
  getAllSuspectClgList() {
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/getAllSuspectClgList`
    );
  }
  getQualificationList() {
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/getQualificationList`
    );
  }
  saveNUpdateEducation(formData: FormData): Observable<any> {
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/saveNUpdateEducation`,
      formData
    );
  }

  saveCandidateApplicationForm(mainformData: FormData) {
    console.log(mainformData, '-------------------');
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/saveCandidateApplicationForm`,
      mainformData
    );
  }

  getITRDetailsFromITRSite(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/getITRDetailsFromITRSite`,
      data
    );
  }

  getepfoCaptcha(candidateCode: any) {
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/epfoCaptcha/${candidateCode}`,
      candidateCode
    );
  }

  getEpfodetail(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/getEpfodetail`,
      data
    );
  }

  getEpfodetailNew(data: any) {
    console.log('Callling new API');
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/getEpfodetailNew`,
      data
    );
  }

  postIsFresher(data: any) {
    return this.http.post(`${environment.apiUrl}/api/allowAll/isFresher`, data);
  }

  getAllSuspectEmpList() {
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/getAllSuspectEmpList`
    );
  }

  saveNUpdateCandidateExperience(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/saveNUpdateCandidateExperience`,
      data
    );
  }

  relationshipAddressVerification(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/relationshipAddressVerification`,
      data
    );
  }

  verifyRelation(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/verifyRelation`,
      data
    );
  }
  getServiceConfigCodes(candidateCode: any) {
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/getServiceConfigCodes/${candidateCode}`,
      candidateCode
    );
  }
  //admin_services//
  getColors() {
    return this.http.get(`${environment.apiUrl}/api/organization/getAllColor`);
  }

  getremarkType(remarkType: any) {
    return this.http.get(
      `${environment.apiUrl}/api/candidate/getAllRemark/${remarkType}`,
      remarkType
    );
  }

  updateCandidateEducationStatusAndRemark(data: any) {
    return this.http.put(
      `${environment.apiUrl}/api/candidate/updateCandidateEducationStatusAndRemark`,
      data
    );
  }

  updateCandidateExperienceStatusAndRemark(data: any) {
    return this.http.put(
      `${environment.apiUrl}/api/candidate/updateCandidateExperienceStatusAndRemark`,
      data
    );
  }

  updateCandidateExperienceResult(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/candidate/updateCandidateExperienceResult`,
      data
    );
  }

  updateCandidateAddressStatusAndRemark(data: any) {
    return this.http.put(
      `${environment.apiUrl}/api/candidate/updateCandidateAddressStatusAndRemark`,
      data
    );
  }

  resumeParser(file: any, candidateCode: any) {
    const formData: FormData = new FormData();
    formData.append('file', file);
    formData.append('candidateCode', candidateCode);
    // const req = new HttpRequest('POST', `${environment.apiUrl}/api/candidate/resumeParser`, formData, {
    //   reportProgress: true,
    //   responseType: 'json'
    // });
    // return this.http.request(req);
    return this.http.post(
      `${environment.apiUrl}/api/candidate/resumeParser`, formData, {
          reportProgress: true,
          responseType: 'json'
        }
    );
  }

  candidateApplicationFormApproved(formData: FormData): Observable<any> {
    return this.http.put(
      `${environment.apiUrl}/api/candidate/candidateApplicationFormApproved`,
      formData
    );
  }

  conventionalCandidateApplicationFormApproved(formData: FormData): Observable<any> {
    return this.http.put(
      `${environment.apiUrl}/api/candidate/conventionalCandidateApplicationFormApproved`,
      formData
    );
  }

  getCandidateFormData_admin(candidateCode: number) {
    return this.http.get(
      `${environment.apiUrl}/api/candidate/candidateApplicationFormDetails/${candidateCode}`
    );
  }

  saveCandidateAddress(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/saveCandidateAddress`,
      data
    );
  }

  updateExperience(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/updateExperience`,
      data
    );
  }

  isUanSkipped(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/isUanSkipped`,
      data
    );
  }

  getDigiTansactionid(candidateCode: any) {
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/getDigiTansactionid/${candidateCode}`,
      candidateCode
    );
  }

  // constructor(private http:HttpClient) { }
  getDigiLockerAlldetail(data: any) {
    console.log('called api');
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/getDigiLockerAlldetail`,
      data
    );
  }

  getDigiLockerdetail(data: any) {
    console.log('called api');
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/getDigiLockerdetail`,
      data
    );
  }

  qcPendingstatus(candidateCode: any) {
    console.log(candidateCode, 'calling ');
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/qcPendingstatus/${candidateCode}`,
      candidateCode
    );
  }

  deletecandidateExpById(id: any) {
    console.log(
      '.......................======================............',
      id
    );
    return this.http.put(
      `${environment.apiUrl}/api/candidate/deletecandidateExp/${id}`,
      id
    );
  }

  deletecandidateEducationById(id: any) {
    console.log(
      '.......................======================............',
      id
    );
    return this.http.put(
      `${environment.apiUrl}/api/candidate/deletecandidateEducationById/${id}`,
      id
    );
  }

  getfinal(data: any) {
    var result = this.http.get(`${environment.flaskurl}/`);
    // return this.http.get(`${environment.flaskurl}/`);
    return this.http.post(`${environment.flaskurl}/`, data);
  }

  getCandidateDLdata(candidateCode: number) {
    console.log('now an candidate service');
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/candidateDLdata/${candidateCode}`
    );
  }

  getuniveristy() {
    return this.http.get(`${environment.digiurl}/digilocker/get-issuers`);
  }

  getdocumenttype(org_id: any) {
    console.log(
      '.......................======================............',
      org_id
    );
    return this.http.get(
      `${environment.digiurl}/digilocker/get-doctype/?orgid=${org_id}`,
      org_id
    );
  }
  getparameters(org_id: any, doctype: any) {
    console.log(
      '.......................',
      org_id,
      '======================............',
      doctype
    );
    return this.http.get(
      `${environment.digiurl}/digilocker/get-parameters/?orgid=${org_id}&doctype=${doctype}`,
      org_id
    );
  }
  getDLEdudocument(data: any) {
    console.log(
      '.......................',
      data,
      '======================............'
    );
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/getDLEdudocument`,
      data
    );
  }

  updateCandidateVendorProofColor(data: any) {
    return this.http.put(
      `${environment.apiUrl}/api/candidate/updateCandidateVendorProofColor`,
      data
    );
  }

  AddCommentsReports(data: any) {
    return this.http.put(
      `${environment.apiUrl}/api/candidate/AddCommentsReports`,
      data
    );
  }

  getAllSuspectEmpListtt(organizationId: any, pageNumber: number, pageSize: number) {
    console.log(organizationId, '======================............');
    return this.http.get(
      `${environment.apiUrl}/api/candidate/getAllSuspectEmpList/${organizationId}?pageNumber=${pageNumber}&pageSize=${pageSize}`,
      organizationId
    );
  }

  deleteSuspectExpById(id: any) {
    console.log(
      '.......................======================............',
      id
    );
    return this.http.put(
      `${environment.apiUrl}/api/candidate/deleteSuspectExpById/${id}`,
      id
    );
  }

  deleteSuspectEmployers(data: any) {
    console.warn('delEmp:::', data);
    return this.http.post(
      `${environment.apiUrl}/api/candidate/deleteSuspectExp`,
      data
    );
  }

  updateSpectEMPloyee(data: any) {
    return this.http.put(
      `${environment.apiUrl}/api/candidate/updateSpectEMPloyee`,
      data
    );
  }

  updateOrgScopeColor(data: any) {
    return this.http.put(
      `${environment.apiUrl}/api/candidate/updateCandidateOrganisationScope`,
      data
    );
  }

  

  getCandidateDetails(candidateCode:any){
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/getCandidateDetails/${candidateCode}`,
      candidateCode
    );
  }

  getCandidateReportStatus(candidateCode:any){
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/getCandidateReportStatus/${candidateCode}`,
      candidateCode
    );
  }

  updateCandidateReportStatus(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/updateReportStatus`,
      data
    );
  }

  getRemittanceRecordsForAllEmployers (candidateCode:any) {
    console.log(candidateCode, 'CALLING REMITTANCE............');
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/remittanceRecords/${candidateCode}?flow=NOTCANDIDATE`,
      candidateCode
    );
  }

  fetchRemittanceRecordsForEmployer(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/remittanceRecordsForEmployer`,
      data
    );
  }

  deleteRemittanceRecord(candidateCode: any, memberId: any, year: any) {
    console.log(candidateCode, '======================............');
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/deleteRemittance/${candidateCode}?memberId=${memberId}&year=${year}`,
      candidateCode
    );
  }

  //new function to add candidate experience by candidate in Cform
  saveCandidateExperienceInCForm(data: any) {
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/updateCandidateExperienceInCForm`,
      data
    );
  }

  deletecandidateExpByIdInCForm(id: any) {
    console.log(
      '.......................======================............',
      id
    );
    return this.http.put(
      `${environment.apiUrl}/api/allowAll/deletecandidateExpInCForm/${id}`,
      id
    );
  }

  getRemittanceCaptcha(candidateCode: any) {
    console.log(candidateCode, '======================............');
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/getRemittanceCaptcha/${candidateCode}`,
      candidateCode
    );
  }


  removeAllSuspectEmpByOrgId(orgId:any){
    console.warn("ORGID=========",orgId)
    return this.http.put(
      `${environment.apiUrl}/api/candidate/removeAllSuspectEmployerByOrgId/${orgId}`,orgId
    )

  }

  authLtrDecline(candidateCode: any) {
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/declineAuthLetter/${candidateCode}`,
      candidateCode
    );
  }

  getLoaContentData(candidateCode: any) {
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/getAuthLetterContent/${candidateCode}`
    );
  }

  getGSTRecordsForAllEmployers (candidateCode:any) {
    console.log(candidateCode, 'CALLING GST............');
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/gstRecords/${candidateCode}?flow=NOTCANDIDATE`,
      candidateCode
    );
  }

  deleteGSTRecord(gstId: any) {
    return this.http.get(
      `${environment.apiUrl}/api/allowAll/deleteGstRecord/${gstId}`,
      gstId
    );
  }

  saveCaseReinitInfo (candidateCode:any, date : any) {
    return this.http.get(
      `${environment.apiUrl}/api/candidate/saveCaseReinitDetails/${candidateCode}?caseReinitDate=${date}`,
      candidateCode
    );
  }

  conventionalCandidate(data:any){
    return this.http.post(
      `${environment.apiUrl}/api/allowAll/conventionalcandidate`,
      data
    )
  }
}
