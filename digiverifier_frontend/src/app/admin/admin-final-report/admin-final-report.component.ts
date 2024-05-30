import { AmChartsLogo } from '@amcharts/amcharts4/.internal/core/elements/AmChartsLogo';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CandidateService } from 'src/app/services/candidate.service';
import { jsPDF } from "jspdf";
import html2canvas from 'html2canvas';
import { DomSanitizer } from '@angular/platform-browser';
import DOMPurify from 'dompurify';


@Component({
  selector: 'app-admin-final-report',
  templateUrl: './admin-final-report.component.html',
  styleUrls: ['./admin-final-report.component.scss']
})
export class AdminFinalReportComponent implements OnInit {
  pageTitle = 'Candidate Report';
  // companyname = 'Tech Mahindra';
  // companyaddrs = 'Survey No. 602/3, ELCOT Special Economic Zone, 138, Sholinganallur, Chennai - 600119 (Tamil Nadu) India';
  // companylogo: any;
  companyname: any;
  companyaddrs: any;
  candidateCode: any;
  candidateName: any;
  organizationLogo: any;
  organizationLocation: any;
  organizationName:any;
  reportStat:boolean=false;
  isPAN:boolean=false;
  isUAN:boolean=false;
  isDRIVE:boolean=false;
  isDGREE:boolean=false;
  isAAHAR:boolean=false;
  candidateAddressData: any=[];
  candidateEduData: any=[];
  candidateEXPData: any=[];
  candidateITRData: any=[];
  cApplicationFormDetails: any=[];
  candidateIdItems: any=[];
  executiveSummary: any=[];
  employmentDetails: any=[];
  dateOfBirth:any;
  contactNumber:any;
  logoUrl:any;
  ccEmailId:any;
  isFresher:any;
  panNumber:any;
  uanNumber:any;
  executiveSummary_stat: any;
  candidateEXPData_stat:any;
  employmentDetails_stat:any;
  candidateIdItems_stat:any;
  candidateEduData_stat:any;
  candidateAddressData_stat:any;
  candidateFinalReport_stat:any;
  dateOfEmailInvite:any;
  submittedOn:any;
  emailId:any;
  getServiceConfigCodes: any=[];
  caseDetails: any=[];
  Criminal_stat:any;
  globalDatabaseCaseDetails: any=[];
  digiDoc:any;
  vendorproof:any=[];
  globalDatabaseCase_stat:any;
  applicantId:any;
  isCRIMINAL:boolean=false;
  isGLOBAL:boolean=false;
  epfoSkipped:boolean=false;
  result:any;
  isVendor:boolean=false;
  isDOC1:boolean=false;
  isDOC2:boolean=false;
  isDOC3:boolean=false;
  isDOC4:boolean=false;
  isDOC5:boolean=false;
  isDOC6:boolean=false;
  isDOC7:boolean=false;
  isDOC8:boolean=false;
  isDOC9:boolean=false;
  isDOC0:boolean=false;
  docname0:any;
  docname1:any;
  docname2:any;
  docname3:any;
  docname4:any;
  docname5:any;
  docname6:any;
  docname7:any;
  docname8:any;
  docname9:any;
  comment:any;
 
  
  constructor( private candidateService: CandidateService, private router: ActivatedRoute, private modalService: NgbModal,private sanitizer:DomSanitizer) { 
    this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
    this.candidateService.getCandidateFormData_admin(this.candidateCode).subscribe((data: any)=>{
      this.cApplicationFormDetails=data.data;
      console.log(this.cApplicationFormDetails);

      if(this.cApplicationFormDetails?.caseDetails?.document){
        this.isCRIMINAL = true;
      }
      if(this.cApplicationFormDetails?.globalDatabaseCaseDetails?.document){
        this.isGLOBAL = true;
      }

      this.candidateName=this.cApplicationFormDetails.candidate.candidateName;
      this.dateOfBirth=this.cApplicationFormDetails.candidate.dateOfBirth;
      this.contactNumber=this.cApplicationFormDetails.candidate.contactNumber;
      this.logoUrl=this.cApplicationFormDetails.candidate.organization.logoUrl;
      this.ccEmailId=this.cApplicationFormDetails.candidate.ccEmailId;
      this.emailId=this.cApplicationFormDetails.candidate.emailId;
      this.applicantId=this.cApplicationFormDetails.candidate.applicantId;
      this.isFresher=this.cApplicationFormDetails.candidate.isFresher;
      this.panNumber=this.cApplicationFormDetails.candidate.panNumber;
      this.dateOfEmailInvite=this.cApplicationFormDetails.emailStatus.dateOfEmailInvite;
      this.submittedOn=this.cApplicationFormDetails.candidate.submittedOn;
      this.comment=this.cApplicationFormDetails.candidateAddComments;
      console.log(this.comment)
      this.organizationName=this.cApplicationFormDetails.candidate.organization.organizationName;
      this.organizationLogo=this.cApplicationFormDetails.candidate.organization.organizationLogo;
      this.organizationLocation=this.cApplicationFormDetails.candidate.organization.organizationLocation;
      // console.log("================logo=========",this.organizationLogo);
      this.candidateAddressData=this.cApplicationFormDetails.candidateCafAddressDto;
      this.candidateEduData=this.cApplicationFormDetails.candidateCafEducationDto;
      this.candidateEXPData=this.cApplicationFormDetails.candidateCafExperienceDto;
      this.employmentDetails=this.cApplicationFormDetails.employmentDetails;
      this.candidateITRData=this.cApplicationFormDetails.itrdataFromApiDto;
      this.candidateIdItems=this.cApplicationFormDetails.candidateIdItems;
      console.log(this.candidateIdItems,"ids")
      this.executiveSummary=this.cApplicationFormDetails.executiveSummary;
      this.caseDetails=this.cApplicationFormDetails.caseDetails;
      if(this.caseDetails){
        $("#viewcaseDetails").attr("src", 'data:application/pdf;base64,'+this.caseDetails.document);
        this.Criminal_stat = this.caseDetails.colorName;
      }
      this.globalDatabaseCaseDetails=this.cApplicationFormDetails.globalDatabaseCaseDetails;
      if(this.globalDatabaseCaseDetails){
        $("#viewGlobalDatabase").attr("src", 'data:application/pdf;base64,'+this.globalDatabaseCaseDetails.document);
        this.globalDatabaseCase_stat = this.globalDatabaseCaseDetails.colorName;
      }
      this.digiDoc=this.cApplicationFormDetails.document;
      this.vendorproof=this.cApplicationFormDetails.vendorProofDetails;
      // console.log(this.vendorproof.length,"2345678990")
     
      if(this.vendorproof){
        this.isVendor=true
        $("#doc0").attr("src", 'data:application/pdf;base64,'+this.vendorproof[0].document);
        this.isDOC0=true
        this.docname0=this.vendorproof[0].documentname
        if(this.vendorproof[1]){
          this.isDOC1=true
          this.docname1=this.vendorproof[1].documentname
          $("#doc1").attr("src", 'data:application/pdf;base64,'+this.vendorproof[1].document);
        }
        if(this.vendorproof[2]){
          this.isDOC2=true
          this.docname2=this.vendorproof[2].documentname
          $("#doc2").attr("src", 'data:application/pdf;base64,'+this.vendorproof[2].document);
        }
        if(this.vendorproof[3]){
          this.isDOC3=true
          this.docname3=this.vendorproof[3].documentname
          $("#doc3").attr("src", 'data:application/pdf;base64,'+this.vendorproof[3].document);
        }
        if(this.vendorproof[4]){
          this.isDOC4=true
          this.docname4=this.vendorproof[4].documentname
            $("#doc4").attr("src", 'data:application/pdf;base64,'+this.vendorproof[4].document);
        }
        if(this.vendorproof[5]){ 
          this.isDOC5=true
          this.docname5=this.vendorproof[5].documentname
            $("#doc5").attr("src", 'data:application/pdf;base64,'+this.vendorproof[5].document);
        }
        if(this.vendorproof[6]){
          this.isDOC6=true
          this.docname6=this.vendorproof[6].documentname
          $("#doc6").attr("src", 'data:application/pdf;base64,'+this.vendorproof[6].document);
        }
        if(this.vendorproof[7]){
          this.isDOC7=true
          this.docname7=this.vendorproof[7].documentname
         $("#doc7").attr("src", 'data:application/pdf;base64,'+this.vendorproof[7].document);
        }
        if(this.vendorproof[8]){
          this.isDOC8=true
          this.docname8=this.vendorproof[8].documentname
          $("#doc8").attr("src", 'data:application/pdf;base64,'+this.vendorproof[8].document);
        }
        if(this.vendorproof[9]){
          this.isDOC9=true
          this.docname9=this.vendorproof[9].documentname
           $("#doc9").attr("src", 'data:application/pdf;base64,'+this.vendorproof[9].document);
        }
        console.log($( $("#doc3").attr("src", 'data:application/pdf;base64,'))) 
      }
     
    
      
      
     
      
     
      for (let index = 0; index < this.digiDoc.length; index++) {
       
        if(this.digiDoc[index].contentSubCategory=="PAN"){
          this.isPAN=true;
          console.log(this.digiDoc[index].contentSubCategory);
          $("#digiDoc").attr("src", 'data:application/pdf;base64,'+this.digiDoc[index].document);
        }
        if(this.digiDoc[index].contentSubCategory=="UAN"){
          this.isUAN=true;
          console.log(this.digiDoc[index].contentSubCategory);
          $("#uanDoc").attr("src", 'data:application/pdf;base64,'+this.digiDoc[index].document);
        }
        if(this.digiDoc[index].contentSubCategory=="DRIVING_LICENSE"){
          this.isDRIVE=true;
          console.log(this.digiDoc[index].contentSubCategory);
          $("#drivingDoc").attr("src", 'data:application/pdf;base64,'+this.digiDoc[index].document);
        }
        if(this.digiDoc[index].contentSubCategory=="DEGREE_CERTIFICATE"){
          this.isDGREE=true;
          console.log(this.digiDoc[index].contentSubCategory);
          $("#degreeDoc").attr("src", 'data:application/pdf;base64,'+this.digiDoc[index].document);
        }
        
      }
      
      // if(this.digiDoc){
      //   $("#digiDoc").attr("src", 'data:application/pdf;base64,'+this.cApplicationFormDetails.document);
        
      // }

      const reportStat =this.cApplicationFormDetails.candidateStatus.statusMaster.statusCode;
      if(reportStat == "FINALREPORT" || reportStat == "INTERIMREPORT"){
        this.reportStat = true; 
      }else {
        this.reportStat = false; 
      }
      if(this.executiveSummary){
        var colorArray=[];
        for (let index = 0; index < this.executiveSummary.length; index++) {
          colorArray.push(this.executiveSummary[index].result);
        }
        if(colorArray.includes('Red')){
          this.executiveSummary_stat = 'Red';
        }else if(colorArray.includes('Amber')){
          this.executiveSummary_stat = 'Amber';
        }else{
          this.executiveSummary_stat = 'Green';
        }
        
      }
      if(this.candidateEXPData){
        var colorArray=[];
        for (let index = 0; index < this.candidateEXPData.length; index++) {
          colorArray.push(this.candidateEXPData[index].colorColorName);
          this.uanNumber=this.candidateEXPData[index].uan;
         
        }
        if(colorArray.includes('Red')){
          this.candidateEXPData_stat = 'Red';
        }else if(colorArray.includes('Amber')){
          this.candidateEXPData_stat = 'Amber';
        }else{
          this.candidateEXPData_stat = 'Green';
        }
        
      }

      if(this.candidateAddressData){
        var colorArray=[];
        for (let index = 0; index < this.candidateAddressData.length; index++) {
          colorArray.push(this.candidateAddressData[index].colorColorName);
          
        }
        if(colorArray.includes('Red')){
          this.candidateAddressData_stat = 'Red';
        }else if(colorArray.includes('Amber')){
          this.candidateAddressData_stat = 'Amber';
        }else{
          this.candidateAddressData_stat = 'Green';
        }
      }

      if(this.candidateEduData){
        var colorArray=[];
        for (let index = 0; index < this.candidateEduData.length; index++) {
          if(this.candidateEduData[index].colorColorName!=""){
            colorArray.push(this.candidateEduData[index].colorColorName);
          }
          
          
        }
        if(colorArray.includes('Red')){
          this.candidateEduData_stat = 'Red';
        }else if(colorArray.includes('Amber')){
          this.candidateEduData_stat = 'Amber';
        }else{
          this.candidateEduData_stat = 'Green';
        }
        
      }

      if(this.candidateIdItems){
        var colorArray=[];
        for (let index = 0; index < this.candidateIdItems.length; index++) {
          colorArray.push(this.candidateIdItems[index].color.colorName);
          if(this.candidateIdItems[index].serviceSourceMaster.serviceName == "Aadhar"){
              console.log("inside aadhar if");
              this.isAAHAR=true;
          }
          
          
        }
        if(colorArray.includes('Red')){
          this.candidateIdItems_stat = 'Red';
        }else if(colorArray.includes('Amber')){
          this.candidateIdItems_stat = 'Amber';
        }else{
          this.candidateIdItems_stat = 'Green';
        }
        
      }

      if(this.employmentDetails){
        var colorArray=[];
        for (let index = 0; index < this.employmentDetails.length; index++) {
          colorArray.push(this.employmentDetails[index].result);
          
        }
        if(colorArray.includes('Red')){
          this.employmentDetails_stat = 'Red';
        }else if(colorArray.includes('Amber')){
          this.employmentDetails_stat = 'Amber';
        }else{
          this.employmentDetails_stat = 'Green';
        }
      }

      if(this.executiveSummary_stat == 'Red' || this.candidateEXPData_stat == 'Red' || this.candidateAddressData_stat == 'Red' || this.candidateEduData_stat == 'Red' || this.candidateIdItems_stat == 'Red' || this.employmentDetails_stat == 'Red' || this.globalDatabaseCase_stat == 'Red' || this.Criminal_stat == 'Red'){
        this.candidateFinalReport_stat = 'Red';
      }else if(this.executiveSummary_stat == 'Amber' || this.candidateEXPData_stat == 'Amber' || this.candidateAddressData_stat == 'Amber' || this.candidateEduData_stat == 'Amber' || this.candidateIdItems_stat == 'Amber' || this.employmentDetails_stat == 'Amber' || this.globalDatabaseCase_stat == 'Amber' || this.Criminal_stat == 'Amber'){
        this.candidateFinalReport_stat = 'Amber';
      }else if(this.executiveSummary_stat == 'Green' || this.candidateEXPData_stat == 'Green' || this.candidateAddressData_stat == 'Green' || this.candidateEduData_stat == 'Green' || this.candidateIdItems_stat == 'Green' || this.employmentDetails_stat == 'Green' || this.globalDatabaseCase_stat == 'Green' || this.Criminal_stat == 'Green'){
        this.candidateFinalReport_stat = 'Green';
      }

      if(this.cApplicationFormDetails.candidate.isUanSkipped == true){
        this.epfoSkipped =true
      }else{
        this.epfoSkipped =false
      }
      
    });
    

    this.candidateService.getServiceConfigCodes(this.candidateCode).subscribe((result:any)=>{
      this.getServiceConfigCodes = result.data;
      console.log(this.getServiceConfigCodes);
      // if(this.getServiceConfigCodes.includes('CRIMINAL')){
      //   this.isCRIMINAL = true;
      // }
      // if(this.getServiceConfigCodes.includes('GLOBAL')){
      //   this.isGLOBAL = true;
      // }
    });
  }
 

  ngOnInit(): void {
    
  }

// logo(){
    
//     // return this.sanitizer.bypassSecurityTrustResourceUrl(this.organizationLogo);

//     // this.imagePath = this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' 
//     // + toReturnImage.base64string);

//     return this.sanitizer.bypassSecurityTrustResourceUrl('data:image/png;base64,' + this.organizationLogo);
//   // base64Image='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOkAAABeCAIAAABJmqR5AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6MDgyNjUwQTA4MDUyMTFFREJEN0M4Mjc1ODJCRDk3REYiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6MDgyNjUwQTE4MDUyMTFFREJEN0M4Mjc1ODJCRDk3REYiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDowODI2NTA5RTgwNTIxMUVEQkQ3QzgyNzU4MkJEOTdERiIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDowODI2NTA5RjgwNTIxMUVEQkQ3QzgyNzU4MkJEOTdERiIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PhDR9RYAABp1SURBVHja7F0JkBzF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';
// }

logo(){
  return this.sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,'+this.organizationLogo);      
}

  printDiv(){
    window.print();
  }

  htmltoPDF(){
    console.log(this.cApplicationFormDetails,"download");
    this.candidateService.getfinal(this.cApplicationFormDetails).subscribe((data:any)=>{
      
      this.result=data.key;
      
      if(this.result!=null){
        // console.log(this.result,"result")
        const linkSource = 'data:application/pdf;base64,'+this.result;
        const downloadLink = document.createElement("a");
        downloadLink.href = linkSource;
        downloadLink.download = this.candidateName+".pdf"
        downloadLink.click();
  
     
      }
      
     })
    
  }
  

  

  //Document View
  openCertificate(modalCertificate:any, certificate:any){
  this.modalService.open(modalCertificate, {
   centered: true,
   backdrop: 'static',
   size: 'lg'
  });
  if(certificate){
    $("#viewcandidateCertificate").attr("src", 'data:application/pdf;base64,'+certificate);
  }
}

openLandlordAgreement(modalLandlordAgreement:any, document:any){
  this.modalService.open(modalLandlordAgreement, {
   centered: true,
   backdrop: 'static',
   size: 'lg'
  });
  if(document){
    $("#viewLandlordAgreement").attr("src", 'data:application/pdf;base64,'+document);
  }
}

calculateTotalGapsAndTenure(){
  var tenures = $(".outputTenures");
  var gaps = $(".gaps");
  var gap=0;
  //debugger;
  $.each(tenures,function(idx,elem){

  });
  $.each(gaps,function(idx,elem){
    //let userValue = $(elem).val();
    let userValue =elem.textContent;
    let sanitizedValue =DOMPurify.sanitize(String(userValue));
    if(sanitizedValue!=="Not-Available"){
      console.log("value"+sanitizedValue);
      let parsedValue = parseInt(sanitizedValue, 10);

      if (!isNaN(parsedValue)) {
        gap += parsedValue;
      }
      console.log("gaps"+gap);
    }
  });

  console.log("tgaps"+gap);
}

}
