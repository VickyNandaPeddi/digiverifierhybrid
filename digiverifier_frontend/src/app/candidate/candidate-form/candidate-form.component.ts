import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CandidateService } from 'src/app/services/candidate.service';
import Swal from 'sweetalert2';
import {ModalDismissReasons, NgbModal, NgbCalendar, NgbDate} from '@ng-bootstrap/ng-bootstrap';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import DOMPurify from 'dompurify';
import { formatDate } from '@angular/common';
import { Location } from '@angular/common';
import moment from 'moment';

@Component({
  selector: 'app-candidate-form',
  templateUrl: './candidate-form.component.html',
  styleUrls: ['./candidate-form.component.scss']
})
export class CandidateFormComponent implements OnInit {
  pageTitle = 'Application Form';
  candidateCode: any;
  candidateName: any;
  CandidateFormData: any=[];
  candidateAddressData: any=[];
  candidateEduData: any=[];
  AllSuspectClgList: any=[];
  AllSuspectEmpList: any=[];
  QualificationList: any=[];
  candidateEXPData: any=[];
  editableDOJ: boolean=true;
  editableLWD: boolean=true;
  editableCompanyName: boolean=false;
  closeModal: string | undefined;
  selectedFiles: any;
  QualificationData: any;
  AssetDeliveryAddress: any;
  PermanentAddress: any;
  PresentAddress:any;
  joiningDate:any;
  exitDate:any;
  outputjoiningDate:any;
  outputexitDate:any;
  panNumber:any;
  public currentFile: any = File;
  public currentresumeFile: any = File;
  public certificateFile: any = File;
  // candidateAddressData_stat: boolean=false;
  candidateAddressData_stat: boolean=true;
  disableSubmit: boolean=true;

  isFresher:boolean=true;
  getServiceConfigCodes: any=[];
  TenurejoiningDate:any;
  TenureexitDate:any;
  dateOfBirth:any;
  candidateUan:any;
  isUanSkipped:any;
  getToday: NgbDate;
  getMinDate: any;
  Dateofexit: any;
  pickerToDate:any;
  formCandidateEXP_valid:boolean=false;
  serachBoard:any;
  serachBoardOther:boolean=false;
  result:any;
  keyword = 'suspectInstitutionName';
  candidateForm = new FormGroup({
    candidateCode: new FormControl('', Validators.required),
    isHighestQualification: new FormControl('', Validators.required),
    isAssetDeliveryAddress: new FormControl('', Validators.required),
    isPermanentAddress: new FormControl('', Validators.required),
    isPresentAddress: new FormControl('', Validators.required),
    resume: new FormControl('')
  });

  formCandidateEXP = new FormGroup({
    candidateCafExperienceId:new FormControl(''),
    candidateCode: new FormControl(''),
    suspectEmpMasterId: new FormControl('', Validators.required),
    candidateEmployerName: new FormControl(''),
    inputDateOfJoining: new FormControl('', Validators.required),
    inputDateOfExit: new FormControl('', Validators.required),
    outputDateOfJoining: new FormControl(''),
    outputDateOfExit: new FormControl(''),
    serviceName: new FormControl(''),
    certificate: new FormControl('', Validators.required),
    colorColorName:new FormControl('')
  });

  formCandidateEdu = this.fb.group({
    candidateCode: new FormControl('', Validators.required),
    qualificationId: new FormControl('', Validators.required),
    schoolOrCollegeName: new FormControl('', Validators.required),
    suspectClgMasterId: new FormControl('', Validators.required),
    percentage: new FormControl('', [Validators.required,Validators.minLength(1), Validators.maxLength(5)]),
    yearOfPassing: new FormControl('', [Validators.required,Validators.minLength(4), Validators.maxLength(4), Validators.pattern("[1-2]\\d{3}")]),
    //totalMarks: new FormControl('', [Validators.minLength(1), Validators.maxLength(4), Validators.pattern("^[0-9]*$")]),
    file: new FormControl('', Validators.required),
    boardOrUniversityName: new FormControl('')
  });

  formCandidateAddress = new FormGroup({
    candidateCode: new FormControl('', Validators.required),
    name: new FormControl('', Validators.required),
    state: new FormControl('', Validators.required),
    city: new FormControl('', Validators.required),
    pinCode: new FormControl('',[Validators.required,Validators.minLength(6), Validators.maxLength(6)]),
    candidateAddress: new FormControl('', Validators.required)
  });

  formITRedit = new FormGroup({
    candidateCode: new FormControl('', Validators.required),
    candidateCafExperienceId: new FormControl('', Validators.required),
    inputDateOfJoining: new FormControl(''),
    inputDateOfExit: new FormControl('')
  });

  serviceName: any;
  colorName: any;

  patchUserValues() {
    this.formCandidateEdu.patchValue({
      candidateCode: this.candidateCode,
      suspectClgMasterId: this.serachBoard
    });
  }
  patchformEXPccode(){
    this.formCandidateEXP.patchValue({
      candidateCode: this.candidateCode,
      inputDateOfJoining: this.joiningDate,
      inputDateOfExit: this.exitDate,
      outputDateOfJoining: this.outputjoiningDate,
      outputDateOfExit: this.outputexitDate
    });
  }
  patchMainFormValues() {
    this.candidateForm.patchValue({
      candidateCode: this.candidateCode
    });
  }

  patchAddressModalValues() {
    this.formCandidateAddress.patchValue({
      candidateCode: this.candidateCode
    });
  }
  constructor(private candidateService: CandidateService, private router:ActivatedRoute,
    private modalService: NgbModal, private fb : FormBuilder,  private navrouter: Router,
    calendar: NgbCalendar, private location: Location) {

      history.pushState(null, "", document.URL);
      window.addEventListener('popstate', function () {
        history.pushState(null, "", document.URL);
      });

      this.getToday = calendar.getToday();
    this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
    this.candidateService.getServiceConfigCodes(this.candidateCode).subscribe((result:any)=>{
      this.getServiceConfigCodes = result.data;
    });
    this.candidateService.getCandidateFormData(this.candidateCode).subscribe((data: any)=>{
      data.data = this.candidateService.decryptData(data.data);
      // Parse the decrypted JSON string into an object
      data.data = JSON.parse(data.data);
       this.CandidateFormData=data.data;
       this.candidateName=this.CandidateFormData.candidate.candidateName;
       this.dateOfBirth=this.CandidateFormData.candidate.panDob;
       // this.dateOfBirth=this.CandidateFormData.candidate.dateOfBirth;
       this.panNumber=this.CandidateFormData.candidate.itrPanNumber;
       this.candidateUan=this.CandidateFormData.candidateUan;
       this.candidateAddressData=this.CandidateFormData.candidateCafAddressDto;
       this.candidateEduData=this.CandidateFormData.candidateCafEducationDto;
       this.candidateEXPData=this.CandidateFormData.candidateCafExperienceDto;
       this.isFresher=this.CandidateFormData.candidate.isFresher;
       this.isUanSkipped=this.CandidateFormData.candidate.isUanSkipped;
   

      if(this.candidateUan!=null){
        this.isUanSkipped=false;
      }else if(this.candidateEXPData){
        this.candidateUan= this.candidateEXPData[0].uan;
      }

      if(this.candidateEXPData.length != 0){
        this.disableSubmit=false;
        //sorting

        // this.candidateEXPData = this.candidateEXPData.sort((a:any, b:any) => {
        //   const dateA = a.inputDateOfJoining ? moment(a.inputDateOfJoining, 'DD/MM/YYYY') : moment(a.inputDateOfExit, 'DD/MM/YYYY');
        //   const dateB = b.inputDateOfJoining ? moment(b.inputDateOfJoining, 'DD/MM/YYYY') : moment(b.inputDateOfExit, 'DD/MM/YYYY');

        //   const yearA = isNaN(dateA.year()) ? 0 : dateA.year();
        //   const yearB = isNaN(dateB.year()) ? 0 : dateB.year();

        //   return yearB - yearA;
        // });

        this.candidateEXPData = this.candidateEXPData.sort((a:any, b:any) => {
          const dateA = a.inputDateOfJoining ? moment(a.inputDateOfJoining, 'DD/MM/YYYY') : moment(a.inputDateOfExit, 'DD/MM/YYYY');
          const dateB = b.inputDateOfJoining ? moment(b.inputDateOfJoining, 'DD/MM/YYYY') : moment(b.inputDateOfExit, 'DD/MM/YYYY');

          const yearA = isNaN(dateA.year()) ? 0 : dateA.year();
          const yearB = isNaN(dateB.year()) ? 0 : dateB.year();

          const monthA = isNaN(dateA.month()) ? 0 : dateA.month();
          const monthB = isNaN(dateB.month()) ? 0 : dateB.month();

          const dayA = isNaN(dateA.date()) ? 0 : dateA.date();
          const dayB = isNaN(dateB.date()) ? 0 : dateB.date();

          if (yearA !== yearB) {
            return yearB - yearA;
          } else if (monthA !== monthB) {
            return monthB - monthA;
          } else {
            return dayB - dayA;
          }
        });

      }
//       console.log("this.candidateUan::{}",this.candidateUan);
//       console.log("this.disableSubmit::{}",this.disableSubmit);
      const isRelVerify=this.getServiceConfigCodes.includes('RELBILLTRUE');
      const isRelVerifyFalse=this.getServiceConfigCodes.includes('RELBILLFALSE');
      if(this.candidateAddressData){
        // for(let index = 0; index < this.candidateAddressData.length; index++) {
        //   if(this.candidateAddressData[index].addressVerificationCandidateAddressVerificationId != null && isRelVerify){
        //     this.candidateAddressData_stat = true;
        //   }else if(isRelVerifyFalse){
        //     this.candidateAddressData_stat = true;
        //   }
        // }
      }
    });

    this.candidateService.getAllSuspectClgList().subscribe((data: any)=>{
      this.AllSuspectClgList=data.data;
    });
    // this.candidateService.getAllSuspectEmpList().subscribe((data: any)=>{
    //   this.AllSuspectEmpList=data.data;
    //   console.log( this.AllSuspectEmpList)
    // });
    this.candidateService.getQualificationList().subscribe((data: any)=>{
      this.QualificationList=data.data;
    });

   }


  selectFile(event:any) {
    const file = event.target.files[0];

    const fileType = event.target.files[0].name.split('.').pop();
    if(fileType == 'pdf' || fileType == 'PDF'){
      this.currentFile = file;
      }else{
        event.target.value = null;
        Swal.fire({
          title: 'Please select .pdf file type only.',
          icon: 'warning'
        });
      }
  }
  selectEvent(event:any){
    this.serachBoard = event.suspectClgMasterId;
    //console.log(this.serachBoard);
    if(this.serachBoard==0){
      this.formCandidateEdu.controls["boardOrUniversityName"].setValidators(Validators.required);
      this.formCandidateEdu.controls["boardOrUniversityName"].updateValueAndValidity();
      this.serachBoardOther = true;
    }else{
      this.formCandidateEdu.controls["boardOrUniversityName"].clearValidators();
      this.formCandidateEdu.controls["boardOrUniversityName"].updateValueAndValidity();
      this.serachBoardOther = false;
    }
  }
  onSubmitEdu(formCandidateEdu: FormGroup) {
    this.patchUserValues();
    const candidateCafEducation = formCandidateEdu.value;
    const formData = new FormData();
    formData.append('candidateCafEducation', JSON.stringify(candidateCafEducation));
    formData.append('file', this.currentFile);
    if (this.formCandidateEdu.valid) {
      this.candidateService.saveNUpdateEducation(formData).subscribe(
        result => {
            if(result.outcome === true){
              Swal.fire({
                title: result.message,
                icon: 'success'
              }).then((result) => {
                if (result.isConfirmed) {
                  window.location.reload();
                }
              });
            }else{
              Swal.fire({
                title: result.message,
                icon: 'warning'
              })
            }
        });
    }else{
      Swal.fire({
        title: "Please enter the required information",
        icon: 'warning'
      })
    }
  }
  ngOnInit(): void {
    window.addEventListener('popstate', () => {
      if (!this.checkHistoryLength()) {
        this.location.forward();
      }
    });
  }

  checkHistoryLength(): boolean {
    return this.location.getState() === null;
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return  `with: ${reason}`;
    }
  }
  openModal(content: any) {
    this.formCandidateEXP.reset();
    this.editableCompanyName=false;
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((res) => {
      this.closeModal = `Closed with: ${res}`;
    }, (res) => {
      this.closeModal = `Dismissed ${this.getDismissReason(res)}`;
    });

  }
//saveCandidateExperienceForm
onJoiningDate(event:NgbDate) {
  let year = event.year;
  let month = event.month <= 9 ? '0' + event.month : event.month;;
  let day = event.day <= 9 ? '0' + event.day : event.day;;
  let finalDate = day + "/" + month + "/" + year;
  this.joiningDate = finalDate;
  this.getMinDate = { day:+day,month:+month,year:+year};
  if(event.after(this.pickerToDate)){
   this.formCandidateEXP_valid = false;
  }else{
    this.formCandidateEXP_valid = true;
  }
 }
 onExitDate(event:NgbDate) {
   this.pickerToDate = event;
  let year = event.year;
  let month = event.month <= 9 ? '0' + event.month : event.month;;
  let day = event.day <= 9 ? '0' + event.day : event.day;;
  let finalDate = day + "/" + month + "/" + year;
  this.exitDate = finalDate;
 }
selectCertificate(event:any) {
  const file = event.target.files[0];
  const fileType = event.target.files[0].name.split('.').pop();
  if(fileType == 'pdf' || fileType == 'PDF'){
      this.certificateFile = file;
    }else{
      event.target.value = null;
      Swal.fire({
        title: 'Please select .pdf file type only.',
        icon: 'warning'
      });
    }
}

valEmpMaster(event:any){
  if(event.target.value==0){
    this.formCandidateEXP.controls["candidateEmployerName"].setValidators(Validators.required);
    this.formCandidateEXP.controls["candidateEmployerName"].updateValueAndValidity();
  }
}
calculateDiff(doe:any,doj:any){
  if(doe==null || doj==null){
    return null;
  }
  let exit = doe.split("/");
  var exitDate = new Date(parseInt(exit[2]) , parseInt(exit[1]) , parseInt(exit[0]));
  var join = doj.split("/");
  var joinDate = new Date(parseInt(join[2]) , parseInt(join[1]) , parseInt(join[0]));
  let differenceInTime = exitDate.getTime() - joinDate.getTime();
  // To calculate the no. of days between two dates
  let differenceInDays = Math.floor(differenceInTime / (1000 * 3600 * 24));
  return differenceInDays;
}

onSubmitEXP(formCandidateEXP: FormGroup) {

  const inputDateOfJoining = this.formCandidateEXP.get('inputDateOfJoining');
  const inputDateOfExit = this.formCandidateEXP.get('inputDateOfExit');
  const outputDateOfJoining = this.formCandidateEXP.get('outputDateOfJoining');
  const outputDateOfExit = this.formCandidateEXP.get('outputDateOfExit');
//   console.log('..............inputDateOfJoining..........',inputDateOfJoining);
  if (inputDateOfJoining && inputDateOfJoining.value) {
    var partsDoj =String(inputDateOfJoining.value).split('/');
    var dojDate = new Date(inputDateOfJoining.value.year, inputDateOfJoining.value.month-1, inputDateOfJoining.value.day);
    let doj = formatDate(dojDate, 'yyyy-MM-dd', 'en-US');
    this.joiningDate=doj;
  }

  if (inputDateOfExit && inputDateOfExit.value) {
    var partsDoe = String(inputDateOfExit.value).split('/');
    var doeDate = new Date(inputDateOfExit.value.year, inputDateOfExit.value.month-1, inputDateOfExit.value.day);
    let doe = formatDate(doeDate, 'yyyy-MM-dd', 'en-US');
    this.exitDate=doe;
  }

  if (outputDateOfJoining && outputDateOfJoining.value) {
    var partsDoj =String(outputDateOfJoining.value).split('/');
    var dojDate = new Date(outputDateOfJoining.value.year, outputDateOfJoining.value.month-1, outputDateOfJoining.value.day);
    let doj = formatDate(dojDate, 'yyyy-MM-dd', 'en-US');
    this.outputjoiningDate=doj;
  }

  if (outputDateOfExit && outputDateOfExit.value) {
    var partsDoe = String(outputDateOfExit.value).split('/');
    var doeDate = new Date(outputDateOfExit.value.year, outputDateOfExit.value.month-1, outputDateOfExit.value.day);
    let doe = formatDate(doeDate, 'yyyy-MM-dd', 'en-US');
    this.outputexitDate=doe;
  }


  this.patchformEXPccode();
  const candidateCafExperience = formCandidateEXP.value;
  const formData = new FormData();
  formData.append('candidateCafExperience', JSON.stringify(candidateCafExperience));
  formData.append('file', this.certificateFile);
//   console.log('..............CAN_FORM_EXPERIENCE..........',candidateCafExperience);
  //console.log(this.certificateFile);

  // formCandidateEXP.patchValue({
  //   serviceName: this.serviceName,
  //   colorColorName: this.colorName
  // })

  if (this.formCandidateEXP_valid == true) {
    // this.candidateService.saveNUpdateCandidateExperience(formData).subscribe(
    this.candidateService.saveCandidateExperienceInCForm(formData).subscribe(
      (result:any) => {
          if(result.outcome === true){
            Swal.fire({
              title: result.message,
              icon: 'success'
            }).then((result) => {
              if (result.isConfirmed) {
                window.location.reload();
              }
            });
          }else{
            Swal.fire({
              title: result.message,
              icon: 'warning'
            })
          }
      });
  }else{
    Swal.fire({
      title: "Please enter the required information",
      icon: 'warning'
    })
  }
}

//saveCandidateApplicationForm
selectHighestQualification(event:any){
  const selectedQualification = event.target.id;
  this.QualificationData = selectedQualification;
  //console.log(selectedQualification);
}

selectResume(event:any) {
  const resumefile = event.target.files[0];
  const fileType = event.target.files[0].name.split('.').pop();
  if(fileType == 'pdf' || fileType == 'PDF'){
    this.currentresumeFile = resumefile;
    }else{
      event.target.value = null;
      Swal.fire({
        title: 'Please select .pdf file type only.',
        icon: 'warning'
      });
    }
}
onSubmit(candidateForm: FormGroup){
//   console.log(candidateForm,"----------")

  this.candidateService.getCandidateApplicationFormSubmit(this.candidateCode).subscribe(
    (result:any) => {
        // if(result.outcome === true){
        //   Swal.fire({
        //     title: result.message,
        //     icon: 'success'
        //   }).then((result) => {
        //     if (result.isConfirmed) {
        //       window.location.reload();
        //     }
        //   });
        // }else{
        //   Swal.fire({
        //     title: result.message,
        //     icon: 'warning'
        //   })
        // }
    }
  );

  const navURL = 'candidate/cThankYou/'+this.candidateCode;
  this.navrouter.navigate([navURL]);
  // const mainformData = new FormData();
  // this.candidateService.qcPendingstatus(this.candidateCode).subscribe((data:any)=>{
  //   this.result=data.outcome;
  //   console.log(this.result,"----------------------------------------result")
  //   if(this.result === true){
  //     const navURL = 'candidate/cThankYou';
  //     this.navrouter.navigate([navURL]);
  //   }else{
  //     Swal.fire({
  //       title: this.result.message,
  //       icon: 'warning'
  //     })
  //   }

  //     })

}
onSubmit_old(candidateForm: FormGroup) {
  let array : any=[];
  var hdnCandidateIds = $(".hdnCandidateIds");
  $.each(hdnCandidateIds,function(idx,elem){
  let value = {
    "candidateCafAddressId":"",
    "isAssetDeliveryAddress":false,
    "isPermanentAddress":false,
    "isPresentAddress":false
  };
  // var values : String= $(elem).val()!.toString();
  //  value.candidateCafAddressId=values.toString();
  var values =elem.textContent;
  if (values !== null) {
    value.candidateCafAddressId = values.toString();
  //  value.isAssetDeliveryAddress=$('.assetDeliveryAddress'+values.toString()).prop('checked')?true:false;
  // value.isPermanentAddress=$('.permanentAddress'+values.toString()).prop('checked')?true:false;
  // value.isPresentAddress=$('.presentAddress'+values.toString()).prop('checked')?true:false;

var assetDeliveryAddress = document.querySelector('.assetDeliveryAddress'+values.toString()) as HTMLInputElement;
value.isAssetDeliveryAddress = assetDeliveryAddress.checked ? true : false;


var permanentAddress = document.querySelector('.permanentAddress'+values.toString()) as HTMLInputElement;
value.isPermanentAddress = permanentAddress.checked ? true : false;


var presentAddress = document.querySelector('.presentAddress'+values.toString()) as HTMLInputElement;
value.isPresentAddress = presentAddress.checked ? true : false;
  array.push(value);
 }
  });
  const mainformData = new FormData();
  mainformData.append('candidateCafEducationDto', this.QualificationData);
  mainformData.append('candidateCafAddressDto',JSON.stringify(array));
  mainformData.append('resume', this.currentresumeFile);
  mainformData.append('candidateCode', this.candidateCode);
  const assetAddress = $(".assetDeliveryAddress_check");
  const permanentAddress = $(".permanentAddress_check");
  const presentAddress = $(".presentAddress_check");
  const HighestQualification_check = $(".HighestQualification_check");
//   console.log(mainformData)
  var i = 0, j=0, k=0, l=0;
  $.each(assetAddress, function(idx,elem){
    //if($(elem).prop('checked')){
    if(elem instanceof HTMLInputElement && elem.checked){
      i++;
    }
  });
  $.each(permanentAddress, function(idx,elem){
    if(elem instanceof HTMLInputElement && elem.checked){
      j++;
    }
  });
  $.each(presentAddress, function(idx,elem){
    if(elem instanceof HTMLInputElement && elem.checked){
      k++;
    }
  });
  $.each(HighestQualification_check, function(idx,elem){
    if(elem instanceof HTMLInputElement && elem.checked){
      l++;
    }
  });
  if(l==0){
    Swal.fire({
      title: "Please select Highest Qualification",
      icon: 'warning'
    })
  }else if(i==0){
    Swal.fire({
      title: "Please select Communication Address",
      icon: 'warning'
    })
  }else if(j==0){
    Swal.fire({
      title: "Please select Present Address",
      icon: 'warning'
    })
  }else if(k==0){
    Swal.fire({
      title: "Please select Permanent Address",
      icon: 'warning'
    })
  }else if(i>0 && j>0 && k>0 && l>0){
     this.candidateService.saveCandidateApplicationForm(mainformData).subscribe((result:any)=>{
      if(result.outcome === true){
        const navURL = 'candidate/cThankYou';
        this.navrouter.navigate([navURL]);
      }else{
        Swal.fire({
          title: result.message,
          icon: 'warning'
        })
      }
    });
  }else{
    Swal.fire({
      title: "Please enter the required information",
      icon: 'warning'
    })
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
addAddress(ModalAddAddress:any){
  this.modalService.open(ModalAddAddress, {
   centered: true,
   backdrop: 'static'
  });
}

onSubmitAddress(formCandidateAddress: FormGroup) {
  this.patchAddressModalValues();
  if (this.formCandidateAddress.valid) {
    this.candidateService.saveCandidateAddress(this.formCandidateAddress.value).subscribe(
      (result:any) => {
          if(result.outcome === true){
            Swal.fire({
              title: result.message,
              icon: 'success'
            }).then((result) => {
              if (result.isConfirmed) {
                window.location.reload();
              }
            });
          }else{
            Swal.fire({
              title: result.message,
              icon: 'warning'
            })
          }
      });
  }else{
    Swal.fire({
      title: "Please enter the required information",
      icon: 'warning'
    })
  }
}

//ITR/EXP Edit

onTenureDOJ(event:any) {
  let year = event.year;
  let month = event.month <= 9 ? '0' + event.month : event.month;;
  let day = event.day <= 9 ? '0' + event.day : event.day;;
  let finalDate = day + "/" + month + "/" + year;
  this.TenurejoiningDate = finalDate;
  this.formITRedit.patchValue({
    inputDateOfJoining: finalDate
  });
 }
 onTenureLWD(event:any) {
  let year = event.year;
  let month = event.month <= 9 ? '0' + event.month : event.month;;
  let day = event.day <= 9 ? '0' + event.day : event.day;;
  let finalDate = day + "/" + month + "/" + year;
  this.TenureexitDate = finalDate;
  this.formITRedit.patchValue({
    inputDateOfExit: finalDate
  });
 }
ITRedit(EXPmodalData:any,
  candidateEmployerName:any, candidateCafExperienceId:any, dateOfJoining:any, dateOfExit:any,
  outputDateOfJoining:any,outputDateOfExit:any , serviceName:any, row:any, colorName: any){

  this.formCandidateEXP.patchValue({
    candidateEmployerName: candidateEmployerName,
    candidateCode: this.candidateCode,
    candidateCafExperienceId: candidateCafExperienceId,
    inputDateOfJoining: this.formatDate(dateOfJoining),
    inputDateOfExit: this.formatDate(dateOfExit),
    outputDateOfJoining: this.formatDate(outputDateOfJoining),
    outputDateOfExit: this.formatDate(outputDateOfExit),
    serviceName:serviceName,
    colorColorName: colorName
  });
//   console.log('serviceName colorName ' + serviceName + ' ' + colorName)
  this.serviceName = serviceName;
  this.colorName = colorName;

  this.formCandidateEXP.markAsUntouched();

  this.editableDOJ = (serviceName=='EPFO' && dateOfJoining!=null) ? false : true;
  this.editableLWD = ((serviceName=='EPFO' && dateOfExit!=null) || row==0) ? false : true;
  this.editableCompanyName = (serviceName=='EPFO' || serviceName=='ITR');

  if(this.editableDOJ){
    this.formCandidateEXP.controls["inputDateOfJoining"].setValidators(Validators.required);
  }else{
    this.formCandidateEXP.controls['inputDateOfJoining'].clearValidators();
  }
  if(this.editableLWD){
    this.formCandidateEXP.controls['inputDateOfExit'].setValidators(Validators.required);
  }else{
    this.formCandidateEXP.controls['inputDateOfExit'].clearValidators();
  }

  this.formCandidateEXP_valid = true;
  this.modalService.open(EXPmodalData, {
   centered: true,
   backdrop: 'static'
  });

}
onSubmitITRedit(formITRedit:FormGroup, modal:any){
  this.formITRedit.markAllAsTouched();
  if (this.formITRedit.valid) {
    this.formITRedit.patchValue({
      inputDateOfJoining: this.TenurejoiningDate,
      inputDateOfExit: this.TenureexitDate
    });
    this.candidateService.saveCandidateExperienceInCForm(this.formITRedit.value).subscribe((result:any)=>{
      if(result.outcome === true){
        modal.close('Save click');
        Swal.fire({
          title: result.message,
          icon: 'success'
        }).then((result) => {
          if (result.isConfirmed) {
            window.location.reload();
          }
        });
      }else{
        Swal.fire({
          title: result.message,
          icon: 'warning'
        })
      }
    });
    return true;
  }else{
    Swal.fire({
      title: "Please enter the required information",
      icon: 'warning'
    });
    return false;
  }
}
formatDate(date:any) {
//   console.log(date,'date in formatDate');
  if(date==null){
    return null;
  }
  let d = date.split('/');
  return { year:+d[2], month:+d[1], day:+d[0]}
}

fetchjoinDateSelected() {
//   console.log('=================', this.getMinDate);
}
fetchexitDateSelected() {
//   console.log('=================', this.Dateofexit);
}

inactiveCust(id: any) {
  $(this).hide();
  Swal.fire({
    title: 'Are You Sure to Delete Experience Details?',
    icon: 'warning',
  }).then((result) => {
    if (result.isConfirmed) {
      this.candidateService
        .deletecandidateExpByIdInCForm(id)
        .subscribe((data: any) => {
          if (data.outcome === true) {
            Swal.fire({
              title: data.message,
              icon: 'success',
            }).then((data) => {
              if (data.isConfirmed) {
                window.location.reload();
              }
            });
          } else {
            Swal.fire({
              title: data.message,
              icon: 'warning',
            });
          }
        });
    }
  });
}

}
