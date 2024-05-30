import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CandidateService } from 'src/app/services/candidate.service';
import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import {
  FormGroup,
  FormControl,
  FormBuilder,
  Validators,
  FormArray,
} from '@angular/forms';
import Swal from 'sweetalert2';
import { OrgadminDashboardService } from 'src/app/services/orgadmin-dashboard.service';
import { NgbCalendar, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { formatDate } from '@angular/common';
import { number, string } from '@amcharts/amcharts4/core';
import { ReportDeliveryDetailsComponent } from 'src/app/charts/report-delivery-details/report-delivery-details.component';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { ElementRef, ViewChild } from '@angular/core';
import { CustomerService } from 'src/app/services/customer.service';
import { validate } from 'json-schema';
import { data } from 'jquery';

@Component({
  selector: 'app-admin-c-report-approval',
  templateUrl: './admin-c-report-approval.component.html',
  styleUrls: ['./admin-c-report-approval.component.scss'],
})
export class AdminCReportApprovalComponent implements OnInit {
  @ViewChild('inputDate') inputDateRef!: ElementRef<HTMLInputElement>;
  pageTitle = 'Pending Approval';
  appId: any;
  candidateCode: any;
  candidateName: any;
  candidateId: any;
  candidateAddressData: any = [];
  candidateEduData: any = [];
  candidateEXPData: any = [];

  candidateEXPDataFromITRAndEPFO: any = [];
  candidateEXPDataFromResume: any = [];
  splitEmployment: boolean = false;

  cApplicationFormDetails: any = [];
  getColors: any = [];
  panNumber: any;
  candidateUan: any;
  qualification: any;
  getEducationqualificationName: any;
  getAddressRemarkType: any = [];
  getEmploymentRemarkType: any = [];
  getEducationRemarkType: any = [];
  getEducationDegree: any = [];
  QualificationList: any = [];
  closeModal: string | undefined;
  education: any;
  employment: any;
  address: any;
  candidateResume: any;
  isFresher: any;
  transactionid: any;
  degree: any;
  uanVal: any;
  casedetails: any;
  global: any;
  employ: any = [];
  Dateofjoin: any;
  Dateofexit: any;
  pickerToDate: any;
  getMinDate: any;
  getToday: NgbDate;
  viewLogo: any;
  public CaseDetailsDoc: any = File;
  public globalCaseDoc: any = File;
  getServiceConfigCodes: any = [];
  vendorChecks: any;
  comment: any;
  candidateEPFOData: any;
  candidateRemittanceData: any = [];
  candidateGSTData: any = [];
  candidateUniqueGSTData: any = [];

  public stat_NEWUPLOAD = true;

  epfoSkipped: boolean = false;
  remittanceFound: boolean = false;
  gstFound: boolean = false;
  candidateEXPData_stat: any;
  candidateITRData: any = [];
  remittanceCaptchaEnabled: boolean = false;
  remittanceCaptchaImage:any;
  outPutDOEEnabled: boolean = false;

  orgScopeData: any = {};
  editClickedFor: any;
  reportStatus:any;
  reportStatusHexCode:any;
  caseReinitiationDate: string = '';
  model: any;
  proofDocumentNew: any;
  isButtonDisabled: boolean = false;
  venderAttributeValue: any[] = [];
  vendorchecksupload: any = [];
  venderSourceId: any;
  venderAttributeCheck: any = [];
  colorid: any;
  
  vendorAttributeListForm: any[] = [];
  vendorAttributeCheckMapped: any[] = [];
  showMessage: any;
  vendorCheckStatusMasterId: any;
  getVenorcheckStatus: any[] = [];
  remarks:any
  checkStatus:any
  idItemsCheckType:boolean = false;
  idItemsPanORAadharORPassport:any;
  idItemsProofName:any;
  idItemsDateOfBirth:any;
  idItemsFatherName:any;
  defaultValue:any;
  selectedGlobalAttributeValue: { label: string; value: string }[] = [];
  globaldataBaseCheck: any;
  globalCheckType: string = 'GLOBAL';
  globalAttributeValue: any[] = [];
  indiaAttributeValue: any[] = [];
  selectedIndiaAttributeValue: any[] = [];
  selectedTab: any;
  selectedStatus: any;
  attributeMap: Map<string, any[]> = new Map<string, any[]>();
  isVendorAttributeForm: boolean | undefined;
  civilProceedingsCount: number = 0;
  drugCheck:boolean = false;
  selectedPanel: string = '';
  selectedLabels: { label: string, value: string }[] = [];
  drugCheckSubmitDisable:boolean = false;
  drugtestCheckValidation:boolean = false;
  showGlobalCheck: boolean = true

  formEditEdu = new FormGroup({
    colorId: new FormControl('', Validators.required),
    remarkId: new FormControl(''),
    customRemark: new FormControl('', [Validators.required, this.customRemarkValidator]),
    // qualificationName: new FormControl('', Validators.required),
    qualificationId: new FormControl('', Validators.required),
    schoolOrCollegeName: new FormControl('', [Validators.required, this.specialCharacterValidator]),
    boardOrUniversityName: new FormControl('', [Validators.required, this.specialCharacterValidator]),
    yearOfPassing: new FormControl('', [Validators.required, this.specialCharacterValidator]),
    percentage: new FormControl('', [Validators.required, this.percentageValidator]),
    id: new FormControl(''),
    candidateCode: new FormControl(''),
  });
  
  myForm = new FormGroup({
    caseReinitiationDate: new FormControl('')
  });

  specialCharacterValidator(control: any) {
    const specialCharacterPattern = /[^\w\s]/;
    const hasSpecialCharacter = specialCharacterPattern.test(control.value);
  
    return hasSpecialCharacter ? { containsSpecialCharacter: true } : null;
  }

  percentageValidator(control: any) {
    const specialCharacterPattern = /[!@#$%^&*(),?":{}|<>\/]/;
    const hasSpecialCharacter = specialCharacterPattern.test(control.value);
  
    return hasSpecialCharacter ? { containsSpecialCharacter: true } : null;
  }

  customRemarkValidator(control: any) {
    const specialCharacterPattern = /[!@#$%^&*()?":{}|<>\/]/;
    const hasSpecialCharacter = specialCharacterPattern.test(control.value);
  
    return hasSpecialCharacter ? { containsSpecialCharacter: true } : null;
  }

  formAddcomment = new FormGroup({
    addComments: new FormControl('', Validators.required),
    id: new FormControl(''),
    candidateCode: new FormControl(''),
  });

  formFetchGST = new FormGroup({
    panNumber: new FormControl('', Validators.required),
    id: new FormControl(''),
    candidateCode: new FormControl(''),
  });

  formEditEmp = new FormGroup({
    colorId: new FormControl('', Validators.required),
    remarkId: new FormControl('', Validators.required),
    id: new FormControl(''),
    // candidateCode: new FormControl(''),
  });

  formEditReportStatus = new FormGroup({
    colorId: new FormControl('')
  });

  formEditEmploymentResult = new FormGroup({
    colorId: new FormControl('')
  });

  formEditEXP = new FormGroup({
    organizationid: new FormControl(''),
    colorId: new FormControl('', Validators.required),
    remarkId: new FormControl(''),
    customRemark: new FormControl('', Validators.required),
    candidateEmployerName: new FormControl('', Validators.required),
    inputDateOfJoining: new FormControl(''),
    inputDateOfExit: new FormControl(''),
    candidateCode: new FormControl(''),
    id: new FormControl(''),
    undisclosed: new FormControl(''),
    outputDateOfExit: new FormControl(''),
    outputDateOfExitProofName: new FormControl(''),
  });

  formEditEXPResult = new FormGroup({
    candidateCafExperienceId: new FormControl('', Validators.required),
    colorId: new FormControl('', Validators.required)
  });

  formRemittance = new FormGroup({
    memberId: new FormControl('', Validators.required),
    company: new FormControl('', Validators.required),
    remittanceDates:new FormControl('', Validators.required),
    doj: new FormControl(''),
    doe: new FormControl(''),
    name: new FormControl('', Validators.required),
    uan: new FormControl('', Validators.required),
    candidateCode: new FormControl(''),
    // remittanceCaptchaEnabled: new FormControl(''),
    remittanceCaptchaImage: new FormControl(''),
    remittanceCaptchaText:new FormControl(''),
  });

  colorIdControl = new FormControl();

  formEditDOC = new FormGroup({
    colorId: new FormControl(''),
    customRemark: new FormControl(''),
  });

  formEditScope = new FormGroup({
    colorId: new FormControl(''),
    customRemark: new FormControl(''),
  });

  formEditADRS = new FormGroup({
    colorId: new FormControl('', Validators.required),
    remarkId: new FormControl(''),
    customRemark: new FormControl(''),
    isAssetDeliveryAddress: new FormControl('', Validators.required),
    isPermanentAddress: new FormControl('', Validators.required),
    isPresentAddress: new FormControl('', Validators.required),
    id: new FormControl(''),
  });

  formReportApproval = new FormGroup({
    criminalVerificationColorId: new FormControl(''),
    globalDatabseCaseDetailsColorId: new FormControl(''),
  });
  drugCheckPanel = new FormGroup({
    drugPanelValue: new FormControl('')
  })
  
  
   vendorlist = new FormGroup({
    vendorcheckId: new FormControl(''),
    documentname: new FormControl(''),
    colorid: new FormControl(''),
    value: new FormControl(''),
    vendorCheckStatusMasterId: new FormControl(''),
    roleAdmin: new FormControl(''),
    remarks: new FormControl('',[Validators.required]),
    // fileInput: new FormControl('',Validators.required)
    legalProcedings: new FormGroup({
      civilProceedingsList: new FormArray([]),
      criminalProceedingsList: new FormArray([]),
    }),
    // drugCheckPanel: new FormControl('')
  });
  
  
    get civilProceedingsList() {
    return (
      this.vendorlist.get('legalProcedings.civilProceedingsList') as FormArray
    ).controls;
  }

  get criminalProceedingsList() {
    return (
      this.vendorlist.get(
        'legalProcedings.criminalProceedingsList'
      ) as FormArray
    ).controls;
  }

  addCivilProceeding() {
    const civilProceedingsArray = this.vendorlist.get(
      'legalProcedings.civilProceedingsList'
    ) as FormArray;

    civilProceedingsArray.push(
      this.createProceedingFormGroup(
        'High Court ',
        'All High Courts of India',
        'All High Courts of India '
      )
    );
    civilProceedingsArray.push(
      this.createProceedingFormGroup(
        'Civil Court',
        'All Civil Court',
        'All Civil Court '
      )
    );

    this.civilProceedingsCount++;

    console.log("fshggfgbshdghfvh",this.vendorlist.value);
  }

  addCriminalProceeding() {
    const criminalProceedingsArray = this.vendorlist.get(
      'legalProcedings.criminalProceedingsList'
    ) as FormArray;

    criminalProceedingsArray.push(
      this.createCriminalProceedingFormGroup(
        'Session Court',
        'All Session Courts',
        'All Session Courts'
      )
    );
    criminalProceedingsArray.push(
      this.createCriminalProceedingFormGroup(
        'Magistrate Court',
        'All Magistrate Courts',
        'All Magistrate courts'
      )
    );

    this.civilProceedingsCount++;

    console.log(this.vendorlist.value);
  }

  createProceedingFormGroup(
    staticCourt: string,
    staticJurisdiction: string,
    staticNameOfTheCourt: string
  ): FormGroup {
    return new FormGroup({
      dateOfSearch: new FormControl('', [Validators.required]),
      court: new FormControl(staticCourt, [Validators.required]),
      jurisdiction: new FormControl(staticJurisdiction, [Validators.required]),
      nameOfTheCourt: new FormControl(staticNameOfTheCourt, [
        Validators.required,
      ]),
      result: new FormControl('', [Validators.required]),
    });
  }

  createCriminalProceedingFormGroup(
    staticCourt: string,
    staticJurisdiction: string,
    staticNameOfTheCourt: string
  ): FormGroup {
    return new FormGroup({
      dateOfSearch: new FormControl('', [Validators.required]),
      court: new FormControl(staticCourt, [Validators.required]),
      jurisdiction: new FormControl(staticJurisdiction, [Validators.required]),
      nameOfTheCourt: new FormControl(staticNameOfTheCourt, [
        Validators.required,
      ]),
      result: new FormControl('', [Validators.required]),
    });
  }


  patchUserValues() {
    this.vendorlist.patchValue({
      colorid: 2,
    });
  }

  private updateLegalProcedings() {
    if (this.isVendorAttributeForm) {
      // @ts-ignore
      this.vendorlist.get('legalProcedings').enable();
      // @ts-ignore
      this.vendorlist.get('value').disable();
    } else {
      // @ts-ignore
      this.vendorlist.get('legalProcedings').disable();
      // @ts-ignore
      this.vendorlist.get('value').enable();
    }
    if (this.selectedStatus == '3') {
      // @ts-ignore
      this.vendorlist.get('legalProcedings').disable();
      // @ts-ignore
      this.vendorlist.get('value').disable();
    }
  }

  
  orgid: string | null;
  isCommentAdded: boolean = true;
  suspectEmpCheckResponse: any;
  getAllColor: any;
  enterUanInQcPending: any;
  checked: any;
  undisclosedbtn: any = false;
  orgName: string = '';
  form: FormGroup;

  constructor(
    private candidateService: CandidateService,
    private router: ActivatedRoute,
    private modalService: NgbModal,
    private navRouter: Router,
    private reportDeliveryDetailsComponent:ReportDeliveryDetailsComponent,
    calendar: NgbCalendar,
    public authService: AuthenticationService,
    private formBuilder: FormBuilder,
    private customers:CustomerService,
    private orgAdmin:OrgadminDashboardService
  ) {
    // this.orgid = localStorage.getItem('orgID');
        this.form = this.formBuilder.group({});
    this.orgid = this.authService.getOrgID();
    this.candidateId = this.router.snapshot.paramMap.get('candidateId');
    this.getToday = calendar.getToday();
    this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
    // this.candidateCode='47CF5AF631QI';
    this.candidateService
      .getCandidateFormData_admin(this.candidateCode)
      .subscribe((data: any) => {
        this.cApplicationFormDetails = data.data;
        if (this.cApplicationFormDetails.organisationScope)
          this.orgScopeData = this.cApplicationFormDetails.organisationScope;
        console.log(
          this.cApplicationFormDetails,
          '------------candidate-----------'
        );
        this.orgName = this.cApplicationFormDetails.candidate?.organization?.organizationName;
        this.appId=this.cApplicationFormDetails.candidate.applicantId;
        this.candidateName =
          this.cApplicationFormDetails.candidate.candidateName;
        this.candidateId = this.cApplicationFormDetails.candidate.candidateId;
        this.degree = this.cApplicationFormDetails.candidate.degree;
        // console.log(this.candidate,"-----------------------------------------------");
        this.panNumber = this.cApplicationFormDetails.candidate.panNumber;
      //  this.panNumber = this.cApplicationFormDetails.candidate.itrPanNumber;
       if(this.getServiceConfigCodes.includes('ITR') && this.cApplicationFormDetails.candidate.itrPanNumber!=null){
        this.panNumber = this.cApplicationFormDetails.candidate.itrPanNumber;
       }
        this.candidateUan = this.cApplicationFormDetails.candidateUan;
        console.log(this.candidateUan, '*********************');
        this.candidateAddressData =
          this.cApplicationFormDetails.candidateCafAddressDto;
        this.candidateEduData =
          this.cApplicationFormDetails.candidateCafEducationDto;
        console.log(
          this.candidateEduData,
          '__________candidateEduData__________________'
        );
        this.candidateEXPData =
          this.cApplicationFormDetails.candidateCafExperienceDto;
        console.log(
          this.candidateEXPData,
          '.......................candidateEXPData.........................'
        );

        if(this.getServiceConfigCodes != null && this.getServiceConfigCodes.includes("EPFO") && this.getServiceConfigCodes.includes("DNHDB")
        && !this.getServiceConfigCodes.includes("DIGILOCKER") && !this.getServiceConfigCodes.includes("ITR")) {

          this.splitEmployment = true;
          for(let exp in this.candidateEXPData) {
            if(this.candidateEXPData[exp].serviceName =="EPFO" || (this.candidateEXPData[exp].serviceName =="DNHDB" && this.candidateEXPData[exp].uan) || this.candidateEXPData[exp].serviceName =="ITR")
              this.candidateEXPDataFromITRAndEPFO.push(this.candidateEXPData[exp]);
            else 
              this.candidateEXPDataFromResume.push(this.candidateEXPData[exp]);
          }
        }

        this.candidateEPFOData=this.cApplicationFormDetails.epfoDataFromApiDto;
        console.log(
          this.candidateEPFOData,
          '.......................candidateEPFOData.........................'
        );
          if(this.cApplicationFormDetails.isRemittancePresent){
            this.remittanceFound=true;

          }
        //below lines for remittance dto
        this.candidateRemittanceData = this.cApplicationFormDetails.remittanceProofImagesData;
        console.log(
          this.candidateRemittanceData,
          '.......................candidateRemittanceData.........................'
        );
        if(this.candidateRemittanceData){
          this.remittanceFound=true;
        }
        this.candidateGSTData=this.cApplicationFormDetails.gstImagesData;
        if(this.candidateGSTData){
          this.gstFound=true;
          const uniqueGSTNumbers = new Set<string>();
          this.candidateGSTData.forEach((gst :any)=> {
            if (gst.gstNumber && !uniqueGSTNumbers.has(gst.gstNumber)) {
              // If gstNumber is defined and not already in the set, add to uniqueRecords
              uniqueGSTNumbers.add(gst.gstNumber);
              this.candidateUniqueGSTData.push(gst);
            }
          });
        }

        this.isFresher = this.cApplicationFormDetails.candidate.isFresher;
        this.candidateITRData = this.cApplicationFormDetails.itrdataFromApiDto;
        this.casedetails = this.cApplicationFormDetails.caseDetails;
        this.global = this.cApplicationFormDetails.globalDatabaseCaseDetails;
        this.employ = this.cApplicationFormDetails.vendorProofDetails;
        console.warn("this.Employee>>>>>>>",this.employ)
        this.comment = this.cApplicationFormDetails.candidateAddComments;
        console.log(this.comment);
        if (this.cApplicationFormDetails.candidateResume) {
          this.candidateResume =
            'data:application/pdf;base64,' +
            this.cApplicationFormDetails.candidateResume.document;
        }

        console.log('candidateEXPData', this.candidateEXPData);

        if (this.candidateEXPData) {
          var colorArray = [];
          for (let index = 0; index < this.candidateEXPData.length; index++) {
            colorArray.push(this.candidateEXPData[index].colorColorName);
          }
          if (colorArray.includes('Red')) {
            this.candidateEXPData_stat = 'Red';
          } else if (colorArray.includes('Amber')) {
            this.candidateEXPData_stat = 'Amber';
          } else {
            this.candidateEXPData_stat = 'Green';
          }
        }

        if (this.cApplicationFormDetails.candidate.isUanSkipped == true) {
          this.epfoSkipped = true;
        } else {
          this.epfoSkipped = false;
        }

        if (this.cApplicationFormDetails.candidateAddComments?.comments) {
          this.isCommentAdded = false;
        }

        if(this.cApplicationFormDetails.candidateReinitiatedDate){
          this.caseReinitiationDate=this.cApplicationFormDetails.candidateReinitiatedDate;
          // var partsDoj = this.caseReinitiationDate.split('-');
          // this.caseReinitiationDate= partsDoj[2]+'-'+partsDoj[1]+'-'+partsDoj[0];
          console.log('this.caseReinitiationDate', this.caseReinitiationDate);
          const inputDate: HTMLInputElement | null = this.inputDateRef?.nativeElement;

              // Check if the input element exists before assigning its value  
              if (inputDate) {
                inputDate.value = this.caseReinitiationDate; // Patching the value into the input field
              }
        }
      });

    this.candidateService.getColors().subscribe((data: any) => {
      if(data.data) {
        this.getAllColor = data.data;
        this.getColors = this.getAllColor.filter((temp: any) => {
          if(temp.colorName != 'Moonlighting' && temp.colorName != 'Out of Scope') {
            return temp;
          }
        });
        console.log(this.getColors, this.getAllColor);
      }
    });

    this.candidateService.getCandidateReportStatus(this.candidateCode).subscribe((data: any) => {
        console.log("Candidate Report Status::{}",data.data);
        this.reportStatus=data.data.colorCode;
        this.reportStatusHexCode=data.data.colorHexCode;
    });
    this.qualification = 'qualification';
    this.candidateService.getQualificationList().subscribe((data: any) => {
      this.QualificationList = data.data;
      // this.candidateService.getqualificationName(this.qualification).subscribe((data: any)=>{
      //   this.getEducationqualificationName=data.data;
      console.log(
        this.getEducationqualificationName,
        '*******************EducationqualificationName'
      );
    });
    this.education = 'education';
    this.candidateService
      .getremarkType(this.education)
      .subscribe((data: any) => {
        this.getEducationRemarkType = data.data;
        console.log(
          this.getEducationRemarkType,
          '*******************Education'
        );
      });
    this.employment = 'employment';
    this.candidateService
      .getremarkType(this.employment)
      .subscribe((data: any) => {
        this.getEmploymentRemarkType = data.data;
      });
    this.address = 'address';
    this.candidateService.getremarkType(this.address).subscribe((data: any) => {
      this.getAddressRemarkType = data.data;
    });
    this.candidateService
      .getServiceConfigCodes(this.candidateCode)
      .subscribe((result: any) => {
        this.getServiceConfigCodes = result.data;
        console.log(this.getServiceConfigCodes);
      });
  }

  undisclosedClick() {
    this.formEditEXP.patchValue({
      undisclosed: !this.formEditEXP.get('undisclosed')?.value
    })

    this.undisclosedbtn = this.formEditEXP.get('undisclosed')?.value;
  }

  patchAddeduValues() {
    this.formEditEdu.patchValue({
      candidateCode: this.candidateCode,
    });
  }
  fetchjoinDateSelected() {
    console.log('=================', this.getMinDate);
  }
  fetchexitDateSelected() {
    console.log('=================', this.Dateofexit);
  }

  ngOnInit(): void {
	   this.customers.getVenorcheckStatus().subscribe((data: any) => {
      if (data.data) {
        this.getVenorcheckStatus = data.data.filter((temp: any) => {
          if (temp.checkStatusCode != 'INPROGRESS') {
            return temp;
          }
        });
      }
      console.log("this.getVenorcheckStatus",this.getVenorcheckStatus);
    });
  }

  openOrgScopeModal(content: any, type: any) {
    this.modalService
      .open(content, { ariaLabelledBy: 'modal-basic-title' })
      .result.then(
        (res) => {
          this.closeModal = `Closed with: ${res}`;
        },
        (res) => {
          this.closeModal = `Dismissed ${this.getDismissReason(res)}`;
        }
      );
    console.log(content, type, this.orgScopeData);
    this.editClickedFor = type;
    if (this.orgScopeData) this.patchScopeValues();
    else this.formEditScope.reset();
  }

  openAddEducationModal(content: any) {
    this.formEditEdu.reset();
    this.patchAddeduValues();
    this.modalService
      .open(content, { ariaLabelledBy: 'modal-basic-title' })
      .result.then(
        (res) => {
          this.closeModal = `Closed with: ${res}`;
        },
        (res) => {
          this.closeModal = `Dismissed ${this.getDismissReason(res)}`;
        }
      );
  }

  suspectEmpCheck(){
    let employer: string = this.formEditEXP.get('candidateEmployerName')?.value;
    const textWithoutSlashes = employer.replace(/\//g, ' '); 
    this.candidateService.suspectEmpCheck(textWithoutSlashes, this.orgid)
    .subscribe((result: any) => {
      if (result.outcome === true) {
        this.suspectEmpCheckResponse = result.message + ", Result: " + result.data;
      }
    });
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }
  //Edit_Education_Modal///
  openEducationModal(
    modalEducation: any,
    candidateCafEducationId: any,
    qualificationId: any,
    qualificationName: any,
    schoolOrCollegeName: any,
    boardOrUniversityName: any,
    yearOfPassing: any,
    percentage: any,
    colorName: any,
    customRemark: any
  ) {
    this.modalService.open(modalEducation, {
      centered: true,
      backdrop: 'static',
    });

    let colorObj = this.getColors.find(
      (temp: any) => temp.colorCode == colorName
    );

    this.formEditEdu.patchValue({
      id: candidateCafEducationId,
      qualificationId: qualificationId,
      qualificationName: qualificationName,
      schoolOrCollegeName: schoolOrCollegeName,
      boardOrUniversityName: boardOrUniversityName,
      yearOfPassing: yearOfPassing,
      percentage: percentage,
      colorId: colorObj != null ? colorObj.colorId : null,
      customRemark: customRemark,
    });
  }

  submitEditEdu(formEditEdu: FormGroup) {
    console.log('......formedu..........', this.formEditEdu.value);
    if (this.formEditEdu.valid) {
      this.candidateService
        .updateCandidateEducationStatusAndRemark(this.formEditEdu.value)
        .subscribe((result: any) => {
          if (result.outcome === true) {
            Swal.fire({
              title: result.message,
              icon: 'success',
            }).then((result) => {
              if (result.isConfirmed) {
                window.location.reload();
              }
            });
          } else {
            Swal.fire({
              title: result.message,
              icon: 'warning',
            });
          }
        });
    } else {
      Swal.fire({
        title: 'Please enter the required details.',
        icon: 'warning',
      });
    }
  }

  inactiveCustEducation(id: any) {
    $(this).hide();

    Swal.fire({
      title: 'Are You Sure to Delete Education Details?',
      icon: 'warning',
    }).then((result) => {
      if (result.isConfirmed) {
        this.candidateService
          .deletecandidateEducationById(id)
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

  //Edit_Experience_Modal///

  patchAddexpValues() {
    this.formEditEXP.patchValue({
      candidateCode: this.candidateCode,
      organizationid: this.orgid,
    });
  }

  patchAdddocValues() {
    this.formEditDOC.patchValue({
      vendorChecks: this.vendorChecks,
    });
  }

  openExperienceModal(
    modalExperience: any,
    candidateCafExperienceId: any,
    candidateEmployerName: any,
    inputDateOfJoining: any,
    inputDateOfExit: any,
    colorName: any,
    customRemark: any, undisclosed: any,
    outputDateOfExit: any,
    outputDateOfExitProofName: any
  ) {
    this.formEditEXP.reset();
    this.modalService.open(modalExperience, {
      centered: true,
      backdrop: 'static',
    });

    if (inputDateOfJoining) {
      var partsDoj = inputDateOfJoining.split('/');
      var dojDate = new Date(partsDoj[2], partsDoj[1] - 1, partsDoj[0]);
      let doj = formatDate(dojDate, 'yyyy-MM-dd', 'en-US');
      this.formEditEXP
        .get('inputDateOfJoining')
        ?.setValue(this.formatDate(doj));
    }

    if(inputDateOfExit){
       console.log("Date of exit available..");
       this.outPutDOEEnabled=false;
    }else{
      console.log("Date of exit not available..");
      this.formEditEXP.get('inputDateOfExit')?.setValue(null);
      this.outPutDOEEnabled=true;
    }

    if (inputDateOfExit && !this.outPutDOEEnabled) {
      var partsDoe = inputDateOfExit.split('/');
      var doeDate = new Date(partsDoe[2], partsDoe[1] - 1, partsDoe[0]);
      let doe = formatDate(doeDate, 'yyyy-MM-dd', 'en-US');
      this.formEditEXP.get('inputDateOfExit')?.setValue(this.formatDate(doe));
      this.outPutDOEEnabled=false;
    }

    if (outputDateOfExit && this.outPutDOEEnabled) {
      var partsOutputDoe = outputDateOfExit.split('/');
      var outputDoeDate = new Date(partsOutputDoe[2], partsOutputDoe[1] - 1, partsOutputDoe[0]);
      let outputDoe = formatDate(outputDoeDate, 'yyyy-MM-dd', 'en-US');
      this.formEditEXP.get('outputDateOfExit')?.setValue(this.formatDate(outputDoe));
     // this.outPutDOEEnabled=true;
    }

    let colorObj = this.getAllColor.find(
      (temp: any) => temp.colorCode == colorName
    );
    this.formEditEXP.patchValue({
      id: candidateCafExperienceId,
      candidateEmployerName: candidateEmployerName,
      colorId: colorObj.colorId != null ? colorObj.colorId : false,
      customRemark: customRemark,
      undisclosed: undisclosed,
      outputDateOfExitProofName: outputDateOfExitProofName
    });

    this.undisclosedbtn = undisclosed;
  }

  private formatDate(date: any) {
    const d = new Date(date);
    let month = '' + (d.getMonth() + 1);
    let day = '' + d.getDate();
    const year = d.getFullYear();
    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;
    return [year, month, day].join('-');
  }

  openVendorModal(modalExperience: any, vendorChecks: any) {
    console.log(vendorChecks);
    this.modalService.open(modalExperience, {
      centered: true,
      backdrop: 'static',
    });
    this.vendorChecks = vendorChecks;
  }

  openAddExperienceModal(content: any) {
    this.outPutDOEEnabled= false;
    this.formEditEXP.reset();
    this.patchAddexpValues();
    this.modalService
      .open(content, { ariaLabelledBy: 'modal-basic-title' })
      .result.then(
        (res) => {
          this.closeModal = `Closed with: ${res}`;
        },
        (res) => {
          this.closeModal = `Dismissed ${this.getDismissReason(res)}`;
        }
      );
  }

  submitEditEXP() {
    if (this.formEditEXP.valid) {
      console.log(
        '..........................employeeeeee..........',
        this.formEditEXP.value
      );
      this.candidateService
        .updateCandidateExperienceStatusAndRemark(this.formEditEXP.value)
        .subscribe((result: any) => {
          if (result.outcome === true) {
            Swal.fire({
              title: result.message,
              icon: 'success',
            }).then((result) => {
              if (result.isConfirmed) {
                window.location.reload();
              }
            });
          } else {
            Swal.fire({
              title: result.message,
              icon: 'warning',
            });
          }
        });
    } else {
      Swal.fire({
        title: 'Please enter the required details.',
        icon: 'warning',
      });
    }
  }

  submitEditDOC() {
    this.patchAdddocValues();
    if (this.formEditDOC.valid) {
      console.log(
        '..........................employeeeeee..........',
        this.formEditDOC.value
      );
      this.candidateService
        .updateCandidateVendorProofColor(this.formEditDOC.value)
        .subscribe((result: any) => {
          if (result.outcome === true) {
            Swal.fire({
              title: result.message,
              icon: 'success',
            }).then((result) => {
              if (result.isConfirmed) {
                window.location.reload();
              }
            });
          } else {
            Swal.fire({
              title: result.message,
              icon: 'warning',
            });
          }
        });
    } else {
      Swal.fire({
        title: 'Please enter the required details.',
        icon: 'warning',
      });
    }
  }
  patchScopeValues() {
    this.formEditScope.reset();
    switch (this.editClickedFor) {
      case 'Dual Employment': {
        this.formEditScope
          .get('customRemark')
          ?.setValue(this.orgScopeData.dualEmployment);
        this.formEditScope
          .get('colorId')
          ?.setValue(this.orgScopeData.dualEmploymentColorId);
        break;
      }
      case 'Undisclosed': {
        this.formEditScope
          .get('customRemark')
          ?.setValue(this.orgScopeData.undisclosed);
        this.formEditScope
          .get('colorId')
          ?.setValue(this.orgScopeData.undisclosedColorId);
        break;
      }
      case 'Data Not Found': {
        this.formEditScope
          .get('customRemark')
          ?.setValue(this.orgScopeData.dataNotFound);
        this.formEditScope
          .get('colorId')
          ?.setValue(this.orgScopeData.dataNotFoundColorId);
        break;
      }
      case 'DNH DB': {
        this.formEditScope
          .get('customRemark')
          ?.setValue(this.orgScopeData.dnhdb);
        this.formEditScope
          .get('colorId')
          ?.setValue(this.orgScopeData.dnhdbcolorId);
        break;
      }
      case 'Tenure Mismatch': {
        this.formEditScope
          .get('customRemark')
          ?.setValue(this.orgScopeData.tenureMismatch);
        this.formEditScope
          .get('colorId')
          ?.setValue(this.orgScopeData.tenureMismatchColorId);
        break;
      }
      case 'Overseas Employment': {
        this.formEditScope
          .get('customRemark')
          ?.setValue(this.orgScopeData.overseasEmployment);
        this.formEditScope
          .get('colorId')
          ?.setValue(this.orgScopeData.overseasEmploymentColorId);
        break;
      }
      case 'Others': {
        this.formEditScope
          .get('customRemark')
          ?.setValue(this.orgScopeData.others);
        this.formEditScope
          .get('colorId')
          ?.setValue(this.orgScopeData.othersColorId);
        break;
      }
      default: {
        //statements;
        break;
      }
    }
  }
  submitEditScope() {
    if(this.orgName == 'CAPGEMINI TECHNOLOGY SERVICES INDIA LIMITED'){
      let defaultColorId = this.getColors.find((temp: any) => temp.colorName == 'Green').colorId;
      this.orgScopeData.othersColorId = defaultColorId;
      this.formEditScope.get('colorId')?.setValue(defaultColorId);
    }
    if (this.formEditScope.valid) {
      console.log(this.candidateId, this.orgScopeData);
      if (this.candidateId) this.orgScopeData.candidateId = this.candidateId;
      switch (this.editClickedFor) {
        case 'Dual Employment': {
          this.orgScopeData.dualEmployment =
            this.formEditScope.get('customRemark')?.value;
          this.orgScopeData.dualEmploymentColorId =
            this.formEditScope.get('colorId')?.value;
          break;
        }
        case 'Undisclosed': {
          this.orgScopeData.undisclosed =
            this.formEditScope.get('customRemark')?.value;
          this.orgScopeData.undisclosedColorId =
            this.formEditScope.get('colorId')?.value;
          break;
        }
        case 'Data Not Found': {
          this.orgScopeData.dataNotFound =
            this.formEditScope.get('customRemark')?.value;
          this.orgScopeData.dataNotFoundColorId =
            this.formEditScope.get('colorId')?.value;
          break;
        }
        case 'DNH DB': {
          this.orgScopeData.dnhdb =
            this.formEditScope.get('customRemark')?.value;
          this.orgScopeData.dnhdbcolorId =
            this.formEditScope.get('colorId')?.value;
          break;
        }
        case 'Tenure Mismatch': {
          this.orgScopeData.tenureMismatch =
            this.formEditScope.get('customRemark')?.value;
          this.orgScopeData.tenureMismatchColorId =
            this.formEditScope.get('colorId')?.value;
          break;
        }
        case 'Overseas Employment': {
          this.orgScopeData.overseasEmployment =
            this.formEditScope.get('customRemark')?.value;
          this.orgScopeData.overseasEmploymentColorId =
            this.formEditScope.get('colorId')?.value;
          break;
        }
        case 'Others': {
          this.orgScopeData.others =
            this.formEditScope.get('customRemark')?.value;
          this.orgScopeData.othersColorId =
            this.formEditScope.get('colorId')?.value;
          break;
        }
        default: {
          //statements;
          break;
        }
      }

      this.candidateService
        .updateOrgScopeColor(this.orgScopeData)
        .subscribe((result: any) => {
          if (result.outcome === true) {
            Swal.fire({
              title: result.message,
              icon: 'success',
            }).then((result) => {
              if (result.isConfirmed) {
                window.location.reload();
              }
            });
          } else {
            Swal.fire({
              title: result.message,
              icon: 'warning',
            });
          }
        });
    } else {
      Swal.fire({
        title: 'Please enter the required details.',
        icon: 'warning',
      });
    }
  }

  inactiveCust(id: any) {
    $(this).hide();
    Swal.fire({
      title: 'Are You Sure to Delete Experience Details?',
      icon: 'warning',
    }).then((result) => {
      if (result.isConfirmed) {
        this.candidateService
          .deletecandidateExpById(id)
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

  //Edit_Address_Modal///

  tmp: any = [];
  roleCboxes(e: any) {
    var sid = e.target.id;
    console.log('checked======================', sid);
    if (e.target.checked) {
      // console.log("value************",value)
      this.tmp.push(sid);
    } else {
      this.tmp.splice($.inArray(sid, this.tmp), 1);
    }
    console.log('checked==============================', this.tmp);
  }

  openModalAddress(
    modalAddress: any,
    candidateCafAddressId: any,
    isAssetDeliveryAddress: any,
    isPermanentAddress: any,
    isPresentAddress: any,
    colorName: any,
    customRemark: any
  ) {
    this.modalService.open(modalAddress, {
      centered: true,
      backdrop: 'static',
    });
    let colorObj = this.getColors.find(
      (temp: any) => temp.colorCode == colorName
    );
    console.log(colorObj);
    this.formEditADRS.patchValue({
      id: candidateCafAddressId,
      isAssetDeliveryAddress:
        isAssetDeliveryAddress != null ? isAssetDeliveryAddress : false,
      isPermanentAddress:
        isPermanentAddress != null ? isPermanentAddress : false,
      isPresentAddress: isPresentAddress != null ? isPresentAddress : false,
      colorId: colorObj != null ? colorObj.colorId : null,
      customRemark: customRemark,
    });
  }
  submitEditADRS() {
    // this.patchAddressValues();
    if (this.formEditADRS.valid) {
      console.log(
        '.......................%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%.formEditADRS.',
        this.formEditADRS.value
      );
      this.candidateService
        .updateCandidateAddressStatusAndRemark(this.formEditADRS.value)
        .subscribe((result: any) => {
          if (result.outcome === true) {
            Swal.fire({
              title: result.message,
              icon: 'success',
            }).then((result) => {
              if (result.isConfirmed) {
                window.location.reload();
              }
            });
          } else {
            Swal.fire({
              title: result.message,
              icon: 'warning',
            });
          }
        });
    } else {
      Swal.fire({
        title: 'Please enter the required details.',
        icon: 'warning',
      });
    }
  }

  //Report_Approval_Form
  uploadCaseDetails(event: any) {
    const file = event.target.files[0];
    const fileType = event.target.files[0].name.split('.').pop();
    if (fileType == 'pdf' || fileType == 'PDF') {
      this.CaseDetailsDoc = file;
      
      // if(this.getServiceConfigCodes != null && this.getServiceConfigCodes.includes("EPFO") && this.getServiceConfigCodes.includes("DNHDB")
      // && !this.getServiceConfigCodes.includes("DIGILOCKER") && !this.getServiceConfigCodes.includes("ITR")) {
      //     this.candidateService
      //   .resumeParser(file, this.candidateCode)
      //   .subscribe((result: any) => {
      //     if (result.outcome === true) {
      //       Swal.fire({
      //         title: result.message,
      //         icon: 'success',
      //       }).then((result) => {
      //         if (result.isConfirmed) {
      //           window.location.reload();
      //         }
      //       });
      //     } else {
      //       Swal.fire({
      //         title: result.message,
      //         icon: 'warning',
      //       });
      //     }
      //   });
      // }
    } else {
      event.target.value = null;
      Swal.fire({
        title: 'Please select .pdf file type only.',
        icon: 'warning',
      });
    }
  }
  uploadGlobalCaseDetails(event: any) {
    const globalfile = event.target.files[0];
    const fileType = event.target.files[0].name.split('.').pop();
    if (fileType == 'pdf' || fileType == 'PDF') {
      this.globalCaseDoc = globalfile;
    } else {
      event.target.value = null;
      Swal.fire({
        title: 'Please select .pdf file type only.',
        icon: 'warning',
      });
    }
  }

  submitReportApproval(formReportApproval: FormGroup, reportType: string) {
    if (this.getServiceConfigCodes.includes('CRIMINAL')) {
      this.formReportApproval.controls[
        'criminalVerificationColorId'
      ].clearValidators();
      this.formReportApproval.controls[
        'criminalVerificationColorId'
      ].setValidators(Validators.required);
      this.formReportApproval.controls[
        'criminalVerificationColorId'
      ].updateValueAndValidity();
      if (this.CaseDetailsDoc.size == null) {
        formReportApproval.setErrors({ invalid: true });
      }
    }

    if (this.getServiceConfigCodes.includes('GLOBAL')) {
      this.formReportApproval.controls[
        'globalDatabseCaseDetailsColorId'
      ].clearValidators();
      this.formReportApproval.controls[
        'globalDatabseCaseDetailsColorId'
      ].setValidators(Validators.required);
      this.formReportApproval.controls[
        'globalDatabseCaseDetailsColorId'
      ].updateValueAndValidity();
      if (this.globalCaseDoc.size == null) {
        formReportApproval.setErrors({ invalid: true });
      }
    }

    const candidateReportApproval = formReportApproval.value;
    const formData = new FormData();
    formData.append(
      'candidateReportApproval',
      JSON.stringify(candidateReportApproval)
    );
    formData.append('criminalVerificationDocument', this.CaseDetailsDoc);
    formData.append('globalDatabseCaseDetailsDocument', this.globalCaseDoc);
    formData.append('candidateCode', this.candidateCode);
    formData.append('reportType', reportType);
    this.candidateService
      .candidateApplicationFormApproved(formData)
      .subscribe((result: any) => {
        if (result.outcome === true) {
          Swal.fire({
            title: result.message,
            icon: 'success',
          }).then((result) => {
            if (result.isConfirmed) {
              if(reportType== 'INTERIMREPORT'){
                console.log("showing the INTERIM report ::");
                this.reportDeliveryDetailsComponent.downloadInterimReport(this.cApplicationFormDetails.candidate,this.reportStatus);

              }else{
                console.log("showing the FINAL report ::");
                this.reportDeliveryDetailsComponent.downloadFinalReportDirectFromQC(this.cApplicationFormDetails.candidate,this.reportStatus);
              // const navURL = 'admin/cFinalReport/' + this.candidateCode;
              // this.navRouter.navigate([navURL]);
              }
            }
          });
        } else {
          Swal.fire({
            title: result.message,
            icon: 'warning',
          });
        }
      });
  }

  //Document View
  openResume(modalResume: any) {
    this.modalService.open(modalResume, {
      centered: true,
      backdrop: 'static',
      size: 'lg',
    });
    if (this.candidateResume) {
      $('#viewcandidateResume').attr('src', this.candidateResume);
    }
  }

  openLandlordAgreement(modalLandlordAgreement: any, document: any) {
    this.modalService.open(modalLandlordAgreement, {
      centered: true,
      backdrop: 'static',
      size: 'lg',
    });
    if (document) {
      $('#viewLandlordAgreement').attr(
        'src',
        'data:application/pdf;base64,' + document
      );
    }
  } 

  enteruan() { 
    this.enterUanInQcPending = true; 
    const navURL = 'candidate/epfologin/' + this.candidateCode; 
    this.navRouter.navigate([navURL], { queryParams: { enterUanInQcPending: this.enterUanInQcPending } }); 
  }
  // initiatevendor(){
  //   const navURL = 'admin/vendorinitiaste/'+this.candidateCode;
  //   this.navRouter.navigate([navURL]);
  // }

  initiatevendor() {
    console.log(
      this.candidateId,
      '-----------------------------------------------'
    );
    const navURL = 'admin/vendorinitiaste/' + this.candidateId + '/'+ this.candidateCode;
    this.navRouter.navigate([navURL]);
  }
  // openCertificate(modalCertificate: any, certificate: any) {
  //   this.modalService.open(modalCertificate, {
  //     centered: true,
  //     backdrop: 'static',
  //     size: 'lg',
  //   });
  //   if (certificate) {
  //     $('#viewcandidateCertificate').attr(
  //       'src',
  //       'data:application/pdf;base64,' + certificate
  //     );
  //   }
  // }

  openCertificate(modalCertificate: any, certificate: any) {
    this.modalService.open(modalCertificate, {
      centered: true,
      backdrop: 'static',
      size: 'lg',
    });
    var maxFileSize = 1000000; // 1MB
    if (certificate && certificate.length <= maxFileSize) {
      this.loadCertificatePDF(certificate);
    }
  }

  loadCertificatePDF(certificate: any) {
    // console.warn("certi",certificate)

    this.detectContentType(certificate);

    // const pdfUrl = 'data:application/pdf;base64,' + certificate;
    // const iframe = document.getElementById(
    //   'viewcandidateCertificate'
    // ) as HTMLIFrameElement;
    // iframe.src = pdfUrl;

    const contentType = this.detectContentType(certificate);

    if (contentType === 'pdf') {
        const pdfUrl = 'data:application/pdf;base64,' + certificate;
        const iframe = document.getElementById('viewcandidateCertificate') as HTMLIFrameElement;
        iframe.src = pdfUrl;
    } else if (contentType === 'image') {
      const imageUrl = 'data:image/png;base64,' + certificate;
      const iframe = document.getElementById('viewcandidateCertificate') as HTMLIFrameElement;
      iframe.src = imageUrl;
      console.log('It is an image!');
    } else {
        // Unsupported content type
        console.error('Unsupported content type');
    }
  }

  viewDocFromS3(modalCertificate: any,pathkey:any){
    // console.warn("pathkey>>>>>",pathkey)
    this.orgAdmin.downloadAgentUploadedDocument(pathkey).subscribe((data:any)=>{
      console.warn("data>>>>",data)
      this.modalService.open(modalCertificate, {
        centered: true,
        backdrop: 'static',
        size: 'lg',
      });
      var maxFileSize = 1000000; // 1MB
      if (pathkey && pathkey.length <= maxFileSize) {
        this.loadCertificatePDF(data.message);
      }
    })
   
  }

  detectContentType(base64String: string): string | null {
    const decodedData = atob(base64String);
    const firstByte = decodedData.charCodeAt(0);

    if (firstByte === 0x25 && decodedData.startsWith('%PDF-')) {
      return 'pdf';
    }
    return 'image';
  }

  patchAddcomentValues() {
    this.formAddcomment.patchValue({
      candidateCode: this.candidateCode,
    });
  }

  openAddcommentModal(content: any) {
    this.formAddcomment.reset();
    this.patchAddcomentValues();
    this.modalService
      .open(content, { ariaLabelledBy: 'modal-basic-title' })
      .result.then(
        (res) => {
          this.closeModal = `Closed with: ${res}`;
        },
        (res) => {
          this.closeModal = `Dismissed ${this.getDismissReason(res)}`;
        }
      );
  }

  patchFetchGSTValues() {
    this.formFetchGST.patchValue({
      panNumber: this.panNumber,
      candidateCode: this.candidateCode,
    });
  }

  openFetchGSTModal(content: any) {
   // this.formFetchGST.reset();
    this.patchFetchGSTValues();
    this.modalService
      .open(content, { ariaLabelledBy: 'modal-basic-title' })
      .result.then(
        (res) => {
          this.closeModal = `Closed with: ${res}`;
        },
        (res) => {
          this.closeModal = `Dismissed ${this.getDismissReason(res)}`;
        }
      );
  }

  submitAddcomment(formAddcomment: FormGroup) {
    console.log(
      '================================ ***** formAddcomment',
      this.formAddcomment.value
    );
    if (this.formAddcomment.valid) {
      this.candidateService
        .AddCommentsReports(this.formAddcomment.value)
        .subscribe((result: any) => {
          if (result.outcome === true) {
            Swal.fire({
              title: result.message,
              icon: 'success',
            }).then((result) => {
              if (result.isConfirmed) {
                window.location.reload();
              }
            });

            this.isCommentAdded = false;
          } else {
            Swal.fire({
              title: result.message,
              icon: 'warning',
            });
          }
        });
    } else {
      Swal.fire({
        title: 'Please enter the required details.',
        icon: 'warning',
      });
    }
  }

  openReportStatusModal(modalReportStatus:any){
    this.model=this.modalService.open(modalReportStatus, {
      centered: true,
      backdrop: 'static',
    });

  }

  openExperienceResultModal(
    modalExperienceResult: any,
    candidateCafExperienceId: any,
    colorName: any){
    this.model=this.modalService.open(modalExperienceResult, {
      centered: true,
      backdrop: 'static',
    });

    this.formEditEXPResult.patchValue({
      candidateCafExperienceId: candidateCafExperienceId
    })    
  }

  submitEditReportStatus(){
    if (this.formEditReportStatus.valid) {
      console.log(
        '.................REPORT STATUS..........',
        this.formEditReportStatus.value.colorId
      );
      const formData = new FormData();
    formData.append('candidateCode', this.candidateCode);
    if(this.formEditReportStatus.value.colorId!==null){

      formData.append('candidateStatusColorId', this.formEditReportStatus.value.colorId);
    }

   // this.reportStatus==this.formEditReportStatus.value.colorId;

   if(this.formEditReportStatus.value.colorId==null){
        this.reportStatus =null;
        this.reportStatusHexCode=null;
   }
    this.getAllColor.filter((temp: any) => {
      if(this.formEditReportStatus.value.colorId==temp.colorId) {
        this.reportStatus =temp.colorCode;
        this.reportStatusHexCode=temp.colorHexCode;
      }
    });
    Swal.fire({
              title: "Status Updated Successfully..",
              icon: 'success',
            }).then((result) => {
              if (result.isConfirmed) {
                this.model.close();
              }
            });
    
      // this.candidateService
      //   .updateCandidateReportStatus(formData)
      //   .subscribe((result: any) => {
      //     if (result.outcome === true) {
      //       Swal.fire({
      //         title: result.message,
      //         icon: 'success',
      //       }).then((result) => {
      //         if (result.isConfirmed) {
      //           window.location.reload();
      //         }
      //       });
      //     } else {
      //       Swal.fire({
      //         title: result.message,
      //         icon: 'warning',
      //       });
      //     }
      //   });
    } else {
      Swal.fire({
        title: 'Please enter the required details.',
        icon: 'warning',
      });
    }

  }

  submitEditEmploymentResult(){
    if (this.formEditEmploymentResult.valid) {

      this.formEditEXPResult.patchValue({
        colorId: this.formEditEmploymentResult.get('colorId')?.value
      })
      console.log(this.formEditEXPResult.value)
      this.candidateService
      .updateCandidateExperienceResult(this.formEditEXPResult.value)
      .subscribe((result: any) => {
        if (result.outcome === true) {
          Swal.fire({
            title: result.message,
            icon: 'success',
          }).then((result) => {
            if (result.isConfirmed) {
              window.location.reload();
            }
          });
        } else {
          Swal.fire({
            title: result.message,
            icon: 'warning',
          });
        }
      });

    } else {
      Swal.fire({
        title: 'Please enter the required details.',
        icon: 'warning',
      });
    }

  }

  getRemittanceRecords() {
      this.candidateService
        .getRemittanceRecordsForAllEmployers(this.candidateCode)
        .subscribe((result: any) => {
          if (result.outcome === true) {
            Swal.fire({
              title: result.message,
              icon: 'success',
            }).then((result) => {
              if (result.isConfirmed) {
                window.location.reload();
              }
            });
          } else {
            Swal.fire({
              title: result.message,
              icon: 'warning',
            });
          }
        });
  }

  openFetchRemittanceModal(
    modalRemittance: any,
    memberId: any,
    company: any,
    doj: any,
    doe: any,
    name: any,
    uan: any
  ) {
    this.modalService.open(modalRemittance, {
      centered: true,
      backdrop: 'static',
    });

    this.formRemittance.patchValue({
      memberId: memberId,
      company: company,
      doj: doj,
      doe: doe,
      name: name,
      uan: uan,
      candidateCode: this.candidateCode,
      remittanceDates:''
    });

  }

  submitRemittance() {
    if (this.formRemittance.valid) {
      console.log(
        '..........................formRemittance..........',
        this.formRemittance.value
      );
      this.candidateService
        .fetchRemittanceRecordsForEmployer(this.formRemittance.value)
        .subscribe((result: any) => {
          if (result.outcome === true) {
              Swal.fire({
                title: result.message,
                icon: 'success',
              }).then((result) => {
                if (result.isConfirmed) {
                  window.location.reload();
                }
              });
           } else if(result.outcome === false && result.message=="498"){
              Swal.fire({
                title: 'Remittance Failed, Please try again by entering captcha',
                icon: 'warning',
              })
              .then((result) => {
                if (result.isConfirmed) {

                  this.remittanceCaptchaEnabled=true;

                  this.candidateService
                     .getRemittanceCaptcha(this.candidateCode)
                     .subscribe((response: any) => {
                      if (response.outcome === true) {
                         console.log("GET The Captcha Image::{}");
                        this.remittanceCaptchaImage = 'data:image/png;base64,' + response.data;
                     }else {
                      this.remittanceCaptchaEnabled=false;
                        Swal.fire({
                          title: response.message,
                          icon: 'warning',
                        });
                      }
                    });
                }
              });
              ;
           }else {
              Swal.fire({
                title: result.message,
                icon: 'warning',
              });
           }
        });
    } else {
      Swal.fire({
        title: 'Please enter the required details.',
        icon: 'warning',
      });
    }
  }

  inactiveRemittance(company: any,
    memberId : any,year: any) {
      console.log("DELETE REMITTANCE RECORD FOR ::{}",company +" MEMBERID::{}"+memberId +"YEAR ::{}"+ year);
    $(this).hide();
    Swal.fire({
      title: 'Are You Sure to Delete Remittance Record?',
      icon: 'warning',
    }).then((result) => {
      if (result.isConfirmed) {
        this.candidateService
          .deleteRemittanceRecord(this.candidateCode,memberId,year)
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

  submitFetchGST(formAddcomment: FormGroup) {
    console.log(
      '================================ ***** formFetchGST',
      this.formFetchGST.value
    );
    if (this.formFetchGST.valid) {
      this.candidateService
        .getGSTRecordsForAllEmployers (this.candidateCode)
        .subscribe((result: any) => {
          if (result.outcome === true) {
            Swal.fire({
              title: result.message,
              icon: 'success',
            }).then((result) => {
              if (result.isConfirmed) {
                window.location.reload();
              }
            });
          } else {
            Swal.fire({
              title: result.message,
              icon: 'warning',
            });
          }
        });
    } else {
      Swal.fire({
        title: 'Please enter the required details.',
        icon: 'warning',
      });
    }
  }

  inactiveGST(gstId: any) {
      console.log("DELETE GST RECORD FOR ::{}",gstId);
    $(this).hide();
    Swal.fire({
      title: 'Are You Sure To Delete GST Record?',
      icon: 'warning',
    }).then((result) => {
      if (result.isConfirmed) {
        this.candidateService
          .deleteGSTRecord(gstId)
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
  
  
    
    selectTab(tabName: string): void {
    this.selectedTab = tabName;
    // Add any additional logic you want to perform when a tab is selected
  }
  addSelectedIndiaAttribute(selectedIndiaAttr: any) {
    if (selectedIndiaAttr) {
      // Add the selected value to the array
      this.selectedIndiaAttributeValue.push({
        label: selectedIndiaAttr,
        value: '',
      });
    }
    console.log(
      'indian attrlist' + JSON.stringify(this.selectedIndiaAttributeValue)
    );
  }
  isGlobalValueSelected(label: string): boolean {
    // Check if the value is already selected
    return this.selectedGlobalAttributeValue.some(
      (attribute) => attribute.label === label
    );
  }
  isIndianValueSelected(label: string): boolean {
    // Check if the value is already selected
    return this.selectedIndiaAttributeValue.some(
      (attribute) => attribute.label === label
    );
  }
  addSelectedGlobalAttribute(value: string) {
    const selectedAttribute = this.globalAttributeValue.find(
      (attr) => attr.label === value
    );

    if (selectedAttribute) {
      // Remove the selected attribute from the globalAttributeValue array
      const index = this.globalAttributeValue.indexOf(selectedAttribute);
      if (index !== -1) {
        this.globalAttributeValue.splice(index, 1);
      }
    }
    this.selectedGlobalAttributeValue.push({ label: value, value: '' });
  }
  addSelectedIndianAttribute(value: string) {
    console.warn("addSelectedIndianAttribute>>",value)
    const selectedIndAttribute = this.indiaAttributeValue.find(
      (attr) => attr.label === value
    );
    console.warn("addSelectedIndianAttribute>> selectedIndAttribute>>",selectedIndAttribute)

    if (selectedIndAttribute) {
      // Remove the selected attribute from the globalAttributeValue array
      const index = this.indiaAttributeValue.indexOf(selectedIndAttribute);
      if (index !== -1) {
        this.indiaAttributeValue.splice(index, 1);
      }
    }
    this.selectedIndiaAttributeValue.push({ label: value, value: '' });
    console.warn("addSelectedIndianAttribute>> selectedIndiaAttributeValue>>",this.selectedIndiaAttributeValue)

  }

  onKeyUpGlobal(value: string, label: string): void {
    // debugger;
    // Find the attribute in the array based on the label
    const foundGlobalAttribute = this.selectedGlobalAttributeValue.find(
      (attr) => attr.label === label
    );
    console.warn("onKeyUpGlobal ONCLICKUP BEFORE>>>",JSON.stringify(foundGlobalAttribute));
    // Update the value if the attribute is found
    if (foundGlobalAttribute) {
      foundGlobalAttribute.value = value;
    }
    console.log("onKeyUpGlobal ONCLICKUP After>>>",JSON.stringify(foundGlobalAttribute));
  }
  onKeyUpIndian(value: string, label: string): void {
    // debugger;
    // Find the attribute in the array based on the label
    const foundIndianAttribute = this.selectedIndiaAttributeValue.find(
      (attr) => attr.label === label
    );
    console.warn("ONCLICKUP INDIAN BEFORE>>>",JSON.stringify(foundIndianAttribute));

    // Update the value if the attribute is found
    if (foundIndianAttribute) {
      foundIndianAttribute.value = value;
    }
    console.warn("ONCLICKUP INDIAN AFTER>>>",JSON.stringify(foundIndianAttribute));

    // console.log(JSON.stringify(foundIndianAttribute));
  }

  // In your component class
  disableIndianOptionButton: any;
  disableGlobalOptionButton: any;
  isOptionSelectedIndian(label: string): boolean {
    this.disableIndianOptionButton = this.selectedIndiaAttributeValue.some(
      (attribute) => attribute.label === label
    );
    return this.disableIndianOptionButton;
  }
  isOptionSelectedGlobal(label: string): boolean {
    this.disableGlobalOptionButton = this.selectedGlobalAttributeValue.some(
      (attribute) => attribute.label === label
    );
    return this.disableGlobalOptionButton;
  }

  

  triggerModal(
    content: any,
    documentname: any,
    vendorcheckId: number,
    sourceName: string,
    sourceId:number,
    checkType:any,
    i: number,
  ) {
    this.idItemsCheckType = false;
    this.drugCheck = false
    this.isVendorAttributeForm = sourceName.includes('Criminal');
    // console.warn("isVendorAttributeForm>>>>>>>>>",this.isVendorAttributeForm)
    // console.warn(' venderChecked=====>', this.vendorchecksupload);

    // console.warn('vendorcheckId', vendorcheckId);

    // console.warn("number>>>>>",sourceId)

    // console.warn("number>>>>>>> IIII",i)

    // console.warn("CheckType>>>>>",checkType)

    // console.warn("SOURCENAME>>>>>>>",sourceName)

    if(sourceId == 5){
      this.idItemsCheckType = true;
      this.vendorlist.addControl('nameAsPerProof', new FormControl('',[Validators.required]));
      this.vendorlist.addControl('proofName', new FormControl('',[Validators.required]));
      this.vendorlist.addControl('dateOfBirth', new FormControl('',[Validators.required]));
      this.vendorlist.addControl('fatherName', new FormControl('',[Validators.required]));
      if (checkType && checkType.includes('PAN')) {
          this.idItemsPanORAadharORPassport = "Name as per Pan";
          this.idItemsProofName = "PAN";
          this.idItemsDateOfBirth = "Date of Birth";
          this.idItemsFatherName = "Father Name";

          this.vendorlist.patchValue({
            'nameAsPerProof': this.cApplicationFormDetails.candidate.panName,
            'proofName':this.cApplicationFormDetails.candidate.itrPanNumber,
            'dateOfBirth':this.cApplicationFormDetails.candidate.panDob,
            'fatherName':this.cApplicationFormDetails.candidate.aadharFatherName

        });
      }
      if (checkType && checkType.includes('Aadhar')) {
        this.idItemsPanORAadharORPassport = "Name as per Aadhar";
        this.idItemsProofName = "Aadhar"
        this.idItemsDateOfBirth = "Date of Birth";
        this.idItemsFatherName = "Father Name";
        this.vendorlist.patchValue({
          'nameAsPerProof': this.cApplicationFormDetails.candidate.aadharName,
          'proofName':this.cApplicationFormDetails.candidate.aadharNumber,
          'dateOfBirth':this.cApplicationFormDetails.candidate.aadharDob,
          'fatherName':this.cApplicationFormDetails.candidate.aadharFatherName

      });
    }
    if (checkType && checkType.includes('Passport')) {
      this.idItemsPanORAadharORPassport = "Name as per Passport";
      this.idItemsProofName = "Passport"
      this.idItemsDateOfBirth = "Date of Birth";
      this.idItemsFatherName = "Father Name";
  }
    }

    if(sourceId == 10){
      this.drugCheck = true
      // console.warn("DRUG CHECK >>>",this.drugCheck);
    }

    this.globaldataBaseCheck = sourceName.includes(
      'Global Database check'
    );

    if (this.globaldataBaseCheck) {
      if (this.globalCheckType != null) {
        this.globalCheckType = 'INDIA';
        this.customers
          .getVendorReportAttributes(sourceId, this.globalCheckType)
          .subscribe((data:any) => {
            // @ts-ignore
            this.globalAttributeValue = data.data.vendorAttributeList.map(
              (attr: any) => {
                return {
                  label: attr,
                  value: 'No Records Found',
                };
              }
            );
            // console.log(
            //   'globalAttributeValue :' +
            //     JSON.stringify(this.globalAttributeValue)
            // );
          });
      }
      if (this.globalCheckType != null) {
        this.globalCheckType = 'GLOBAL';
        this.customers
          .getVendorReportAttributes(sourceId, this.globalCheckType)
          .subscribe((data:any) => {
            // @ts-ignore
            this.indiaAttributeValue = data.data.vendorAttributeList.map(
              (attr: any) => {
                return {
                  label: attr,
                  value: 'No Records Found',
                };
              }
            );
            // console.log(
            //   'indiaAttributeValue :' + JSON.stringify(this.indiaAttributeValue)
            // );
          });
      }
    }

    this.updateLegalProcedings();
    if (this.globaldataBaseCheck === false) {
      this.customers
        .getVendorReportAttributes(sourceId, this.globalCheckType)
        .subscribe((data:any) => {
          // @ts-ignore
          this.venderAttributeValue = data.data.vendorAttributeList.map(
            (attr: any) => {
              return {
                label: attr,
                value: '',
              };
            }
          );
        });
    }

    // console.warn("Tgdkjbkjsbkj>",this.employ[i])

    const vendorValue = this.employ[i].vendorAttribute

     // console.warn("vendorValue>>>>>>>>>>>"+vendorValue)

    


    for (const attribute of vendorValue) {
      // console.warn("ATTRIBUTE>>>>>>>",attribute)
      if(this.globaldataBaseCheck){
      //  console.warn("ATTRIBUTE>>>>>>>222", attribute);

        let isValidJSON = false;
try {
  const parsedData = JSON.parse(attribute);
  isValidJSON = true;
  // If parsing succeeds, you can further process the parsed data here
} catch (error) {
  console.error("The attribute is not a valid JSON string:", error);
}

    if (isValidJSON) {
   
          const parsedData = JSON.parse(attribute); // Parse the JSON string

          const vendorCheckStatusId = parsedData.vendorCheckStatusMasterId[0].vendorCheckStatusMasterId;
          const remarks = parsedData.remarks[0].remarks;
  
          console.log("Vendor Check Status Master ID:", vendorCheckStatusId);
      console.log("Remarks:", remarks);
  
      this.remarks = remarks// Store the value of "remarks"
  
      const status = vendorCheckStatusId; // Parse the status value to integer
  
      if(status == 1){
        this.checkStatus = "Clear"
      }
      if(status == 2){
        this.checkStatus = "Inprogress"
      }
      if(status == 3){
        this.checkStatus = "In Sufficiency"
      }
      if(status == 4){
        this.checkStatus = "Major Discrepancy"
      }
      if(status == 5){
        this.checkStatus = "Minor Discrepancy"
      }
      if(status == 6){
        this.checkStatus = "Unable To Verify"
      }
   }
   else{
    console.warn("else>>>>>>>>>>")
   // console.warn("ATTRIBUTE>>>>>>>33", attribute);
  const [key, value] = attribute.split('='); // Split each attribute by '='
  if ((key === 'remarks') || key === 'Remarks') {
      this.remarks = value; // Store the value of "remarks"
      break; // Exit the loop once "remarks" is found
  }
  if(key === 'vendorCheckStatusMasterId'){
    let status = value
    if(status == 1){
      this.checkStatus = "Clear"
    }
    if(status == 2){
      this.checkStatus = "Inprogress"
    }
    if(status == 3){
      this.checkStatus = "In Sufficiency"
    }
    if(status == 4){
      this.checkStatus = "Major Discrepancy"
    }
    if(status == 5){
      this.checkStatus = "Minor Discrepancy"
    }
    if(status == 6){
      this.checkStatus = "Unable To Verify"
    }
  }
   }
     
}
else{
  console.warn("ATTRIBUTE>>>>>>>33", attribute);
  const [key, value] = attribute.split('='); // Split each attribute by '='
  if ((key === 'remarks') || key === 'Remarks') {
      this.remarks = value; // Store the value of "remarks"
      break; // Exit the loop once "remarks" is found
  }
  if(key === 'vendorCheckStatusMasterId'){
    let status = value
    if(status == 1){
      this.checkStatus = "Clear"
    }
    if(status == 2){
      this.checkStatus = "Inprogress"
    }
    if(status == 3){
      this.checkStatus = "In Sufficiency"
    }
    if(status == 4){
      this.checkStatus = "Major Discrepancy"
    }
    if(status == 5){
      this.checkStatus = "Minor Discrepancy"
    }
    if(status == 6){
      this.checkStatus = "Unable To Verify"
    }
  }
}
      
  }

  // console.log("fsjkgkfgskgfk",this.remarks); // Output: SuccessVendot
  // console.log("checkStatus:::::",this.checkStatus)

  

  if(this.isVendorAttributeForm){
    this.vendorlist.patchValue({
      remarks:this.remarks,
      vendorCheckStatusMasterId:this.checkStatus,
      legalProcedings: {
        civilProceedingsList: [
          this.addCivilProceeding()
        ],
        criminalProceedingsList: [
          // Patch values for criminal proceedings if needed
          // Example: { dateOfSearch: 'value', court: 'value', jurisdiction: 'value', nameOfTheCourt: 'value', result: 'value' }
          this.addCriminalProceeding()
        ]
      }
    })
  }else{
    console.warn("CHECKSTATSUS>>>>>>>>>>>",this.checkStatus)
    this.vendorlist.patchValue({
      remarks:this.remarks,
      vendorCheckStatusMasterId:this.checkStatus,
    })
  }


  // console.warn("his.vendorlist.patchValu>>>>>>",this.vendorlist.value)


    

    // this.remarks = 

    // this.venderSourceId = this.vendorchecksupload[i].source.sourceId;

    // console.warn("this.venderSourceId>>>>>>>>>"+this.venderSourceId)


    // this is the code for Fetching the venderAttributesList
    if (this.globaldataBaseCheck === false) {

    this.customers
      .getAgentAttributes(sourceId)
      .subscribe((data: any) => {
        this.venderAttributeCheck = data.data;

        // console.warn('===============', this.venderAttributeCheck);

        console.warn(
          'VenderCheck:::',
          this.venderAttributeCheck.vendorAttributeList
        );
        const labelValueList: any[] = [];


        this.venderAttributeValue =
          this.venderAttributeCheck.vendorAttributeList.map((ele: any) => {
            let defaultValue = null;

            if (ele === 'Date of Birth') {
                defaultValue = this.cApplicationFormDetails.candidate.aadharDob;
                console.log("defaultValue for Date of Birth >>>>", defaultValue);
            } else if (ele === 'Father Name') {
                defaultValue = this.cApplicationFormDetails.candidate.aadharFatherName;
                console.log("defaultValue for Father Name >>>>", defaultValue);
            }
        
            return {
                label: ele,
                value: null,
            };
          });
//   this.venderAttributeCheck.vendorAttributeList.forEach((ele: any) => {
//   const defaultValues:any = {};
//     if (ele === 'Date of Birth') {
//         defaultValues[ele] = this.cApplicationFormDetails.candidate.aadharDob;
//     } else if (ele === 'Father Name') {
//         defaultValues[ele] = this.cApplicationFormDetails.candidate.aadharFatherName;
//     } else {
//         defaultValues[ele] = null;
//     }
// });

// console.warn("defaultValues >>>>", defaultValues);

// const defaultValueList = this.venderAttributeCheck.vendorAttributeList.map((ele: any) => {
//     return {
//         label: ele,
//         value: defaultValues[ele],
//     };
// });

// console.log("defaultValueList >>>>", defaultValueList);
// this.venderAttributeValue = defaultValueList; // Assigning defaultValueList to venderAttributeValue


        console.log(
          'this.venderAttributeCheck===========>',
          this.venderAttributeValue
        );
      });
    }

    this.modalService.open(content).result.then(
      (res) => {
        console.log(content, '........................');
        this.closeModal = `Closed with: ${res}`;
      },
      (res) => {
        console.warn("kjsgfkg")
        this.closeModal = `Dismissed ${this.getDismissReason(res)}`;
        window.location.reload();
      }
    );
    console.log(documentname, '........................'),
      this.vendorlist.patchValue({
        documentname: documentname,
        vendorcheckId: vendorcheckId,
        colorid: this.colorid,
        roleAdmin: true
        
      });
  }

  close(modal:any) {
    modal.dismiss('Close button clicked');
  }


  isFieldEmpty(attribute: any): boolean {
    return attribute.value === null || attribute.value === '' || attribute.value === undefined;
  }

  onSubmit(vendorlist: FormGroup) {
    this.showGlobalCheck = false;
    this.patchUserValues();
    let rawValue = vendorlist.getRawValue();
    // console.log('Form data:', this.form.getRawValue());
    // console.warn("RAWVALUE>>>>>>>>>",rawValue)
    // console.warn('VENDORLISTPATCH:::>>>>>>>>>', this.vendorlist.value);
    // console.log(this.vendorAttributeListForm);

    if(this.vendorlist.get('vendorCheckStatusMasterId')?.value === 'Clear'){
      // console.warn("================TRUE==================")
      this.vendorlist.patchValue({
        vendorCheckStatusMasterId:1,
      })
    }
    else if(this.vendorlist.get('vendorCheckStatusMasterId')?.value === 'In Sufficiency'){
      this.vendorlist.patchValue({
        vendorCheckStatusMasterId:3,
      })
    }else if(this.vendorlist.get('vendorCheckStatusMasterId')?.value === 'Major Discrepancy'){
      this.vendorlist.patchValue({
        vendorCheckStatusMasterId:4,
      })
    }else if(this.vendorlist.get('vendorCheckStatusMasterId')?.value === 'Minor Discrepancy'){
      this.vendorlist.patchValue({
        vendorCheckStatusMasterId:5,
      })
    }else if(this.vendorlist.get('vendorCheckStatusMasterId')?.value === 'Unable To Verify'){
      this.vendorlist.patchValue({
        vendorCheckStatusMasterId:6,
      })
    }

    // tem
    // this.onPanelChange({ target: { value: this.selectedLabels } });
    // console.log("Updated Selected Labels:", this.selectedLabels);

    const formData1 = {
      panel: this.selectedPanel,
      labels: this.selectedLabels
    };
    // console.log("NEW APPROACH>>>",formData1);
    // Check if drugtest is true and add the vendorlist form group
    const drugtest = true; // Example condition, replace with your actual condition
     const combinedData = { ...this.form.value, ...vendorlist.value };
    //  console.log('Combined data:', combinedData);

    this.selectedGlobalAttributeValue.push(...this.globalAttributeValue);
    // console.log('global1' + JSON.stringify(this.selectedGlobalAttributeValue));
    this.attributeMap.set('GLOBAL', this.selectedGlobalAttributeValue);
    // console.log('INDIAN' + JSON.stringify(this.selectedIndiaAttributeValue));
    this.selectedIndiaAttributeValue.push(...this.indiaAttributeValue);
    this.attributeMap.set('INDIA', this.selectedIndiaAttributeValue);
    // console.log(JSON.stringify(this.attributeMap));
    this.patchUserValues();
    console.log(
      this.vendorlist.value,
      '----------------------------------------'
    );
    const formData = new FormData();
    if (this.globaldataBaseCheck) {
      const mergedData = {
        ...this.vendorAttributeCheckMapped,
      };
      const attributeMapObject: { [key: string]: any[] } = {};
      for (const [key, value] of this.attributeMap.entries()) {
        attributeMapObject[key] = value;
      }
      if (rawValue.status === '3') {
        // @ts-ignore
        formData.append('vendorRemarksReport', null);
      } else {
        formData.append('vendorchecks', JSON.stringify(this.vendorlist.value));

        formData.append(
          'vendorAttributesValue',
          JSON.stringify(attributeMapObject)
        );
      }
    //   formData.append('vendorchecks', JSON.stringify(this.vendorlist.value));

    // formData.append('vendorAttributesValue', JSON.stringify(mergedData));

      return this.customers
      .saveproofuploadVendorChecks(formData)
      .subscribe((result: any) => {
        if (result.outcome === true) {
          Swal.fire({
            title: result.message,
            icon: 'success',
          }).then((result) => {
            if (result.isConfirmed) {
              window.location.reload();
            }
          });
        } else {
          Swal.fire({
            title: result.message,
            icon: 'warning',
          });
        }
      });
    }

else{
    this.vendorAttributeListForm = this.venderAttributeValue;

    const venderAttributeValue = this.vendorAttributeListForm.reduce(
      (obj, item) => {

        if (item.value === null || item.value.trim() === '') {
          return false; // Return false if any item.value is null or empty
        }

        obj[item.label] = item.value;

        return obj;
      },
      {}
    );

    if (venderAttributeValue === false) {
      // console.error('Please enter values for all attributes');
      this.showMessage = "Please enter values for Mandatory Field";
    } else {
      // console.log('CrimnalGlobalAttributeValues:', venderAttributeValue);
    }

    this.vendorAttributeCheckMapped = { ...venderAttributeValue };
    // console.log(
    //   ' vendorAttributeCheckMapped:::',
    //   this.vendorAttributeCheckMapped
    // );

  //  console.warn('vendorAttributeCheckMapped===>', venderAttributeValue);
    let mergedData:any = null;

    if(this.isVendorAttributeForm){
      const {vendorCheckStatusMasterId,remarks,nameAsPerProof,proofName,legalProcedings } = this.vendorlist.value;
       mergedData = {
        ...this.vendorAttributeCheckMapped,
        vendorCheckStatusMasterId,
        remarks,
        nameAsPerProof,
        proofName,
        legalProcedings
      };
    }
    else{

      
    //temp

    // console.log("Updated Selected Labels:", this.selectedLabels);

    const formData1 = {
      panel: this.selectedPanel,
      labels: this.selectedLabels
    };
    // console.log("NEW APPROACH>>>",formData1);


    const fetchedData = {
      panel: formData1.panel,
      labels: formData1.labels.map(label => {
        return { [label.label]: label.value };
      })
    };

          const {vendorCheckStatusMasterId,remarks,nameAsPerProof,proofName,dateOfBirth,fatherName } = this.vendorlist.value; 
           mergedData = {
            ...this.vendorAttributeCheckMapped,
            vendorCheckStatusMasterId,
            remarks,
            nameAsPerProof,
            proofName,
            dateOfBirth,
            fatherName
          };

          if(this.drugCheck){
            mergedData.panel = formData1.panel;
  
            // fetchedData.labels.forEach(labelObject => {
            //   const label = Object.keys(labelObject)[0]; // Get the label
            //   const value = labelObject[label]; // Get the value
            //   mergedData[label] = value; // Append label as key and value to mergedData
            // });


            fetchedData.labels.forEach(labelObject => {
              const label = Object.keys(labelObject)[0]; // Get the label
              const value = labelObject[label]; // Get the value
            
              // Check if value is empty
              if (value.trim() === '') {
                // console.warn("khsfhbhsvfvhj false")
              //  const drugtestCheck = false;
              //  mergedData[label] = false;  Set venderAttributeValue to false
              } else {
                this.drugtestCheckValidation = true;
                mergedData[label] = value; // Append label as key and value to mergedData
              }
            });

            // console.log("Fetched Data:", fetchedData);
          }

    }

    //  formData.append('vendorchecks', JSON.stringify(this.forAddressCrimnalGlobal.value ))

    //  formData.append('vendorchecks', JSON.stringify(agentAttributeValues ))

    //  formData.append('vendorchecks', JSON.stringify(this.forAddressCrimnalGlobal.value ))


    const formData = new FormData();
    formData.append('vendorchecks', JSON.stringify(this.vendorlist.value));

    formData.append('vendorAttributesValue', JSON.stringify(mergedData));

    // console.warn('mergedData++++++++++++++++++++', mergedData);
  
    // formData.append('vendorchecks', JSON.stringify(this.vendorlist.value));
    //  if (this.vendorlist.valid && venderAttributeValue !== false) {
      if(this.drugCheck !== true){
      if (this.vendorlist.valid && venderAttributeValue !== false) {
    //  if (this.vendorlist.valid) {
      // formData.append('file', this.proofDocumentNew);
      // formData.append('vendorchecks', JSON.stringify(this.vendorlist.value));
      // formData.append('file', this.proofDocumentNew);
      return this.customers
        .saveproofuploadVendorChecks(formData)
        .subscribe((result: any) => {
          if (result.outcome === true) {
            Swal.fire({
              title: result.message,
              icon: 'success',
            }).then((result) => {
              if (result.isConfirmed) {
                window.location.reload();
              }
            });
          } else {
            Swal.fire({
              title: result.message,
              icon: 'warning',
            });
          }
        });
    } else {
      console.warn("Triggred!!!!!!!!!!!")
      Swal.fire({
        title: 'Please enter the required details.',
        icon: 'warning'
      })
    }
  }
    // Start On Submit For Drug Check with Validation
    if( this.drugCheck && this.drugtestCheckValidation !== false && this.vendorlist.valid && venderAttributeValue !== false){
      return this.customers
      .saveproofuploadVendorChecks(formData)
      .subscribe((result: any) => {
        if (result.outcome === true) {
          Swal.fire({
            title: result.message,
            icon: 'success',
          }).then((result) => {
            if (result.isConfirmed) {
              window.location.reload();
            }
          });
        } else {
          Swal.fire({
            title: result.message,
            icon: 'warning',
          });
        }
      });
    }else {
      Swal.fire({
        title: 'Please enter the required details.',
        icon: 'warning'
      })
    }
  }
      // Start On Submit For Drug Check with Validation
  return undefined;
  }
  
  uploadGlobalCaseDetails2(event: any) {
    const fileType = event.target.files[0].name.split('.').pop();
    const file = event.target.files[0];
    if (
      fileType == 'pdf' ||
      fileType == 'PDF' ||
      fileType == 'png' ||
      fileType == 'PNG' ||
      fileType == 'jpg' ||
      fileType == 'JPG'
    ) {
      this.proofDocumentNew = file;
      this.previewFile(file);
    } else {
      this.isButtonDisabled = true;
      event.target.value = null;
      Swal.fire({
        title: 'Please select .jpeg, .jpg, .png file type only.',
        icon: 'warning',
      });
    }
  }

  previewFile(file: File) {
    const previewContainer = document.getElementById('preview-container');
    const lowerCaseFileType = file.type;

    if (previewContainer) {
      if (
        lowerCaseFileType === 'application/pdf' ||
        lowerCaseFileType === 'image/png' ||
        lowerCaseFileType === 'image/jpeg' ||
        lowerCaseFileType === ' '
      ) {
        const previewButton = document.createElement('button');
        previewButton.textContent = 'Preview';
        previewButton.addEventListener('click', () => {
          this.previewFile(file);
        });
        const downloadLink = document.createElement('a');
        downloadLink.href = URL.createObjectURL(file);
        downloadLink.target = '_blank';
        downloadLink.textContent = 'Preview';
        downloadLink.classList.add('btn', 'btn-primary');

        previewContainer.innerHTML = '';
        previewContainer.appendChild(downloadLink);
      } else {
        previewContainer.innerHTML =
          'Preview not available for this file type.';
      }
    }
  }

  getvendorcheckstatuss(event: any) {
    console.log("control entered with value: ", event.target.value);
    this.vendorCheckStatusMasterId = event.target.value;
  }

  panelLabels: { [key: string]: string[] } = {
    'Panel 5': ['Amphetamine (AMP)', 'Cocaine (COC)', 'Marijuana / Cannabinoids', 'Phencyclidine (PCP)', 'Opiates'],
    'Panel 6': ['Amphetamine (AMP)', 'Cocaine (COC)', 'Marijuana / Cannabinoids', 'Phencyclidine (PCP)', 'Opiates', 'Propoxyphene'],
    'Panel 7': ['Amphetamine (AMP)', 'Cocaine (COC)', 'Marijuana / Cannabinoids', 'Phencyclidine (PCP)', 'Opiates', 'Benzodiazepine (BZD)','Barbiturate (BAR)'],
    'Panel 8': ['Amphetamine (AMP)', 'Cocaine (COC)', 'Marijuana / Cannabinoids', 'Phencyclidine (PCP)', 'Opiates', 'Benzodiazepine (BZD)','Barbiturate (BAR)','Propoxyphene'],
    'Panel 9': ['Amphetamine (AMP)', 'Cocaine (COC)', 'Marijuana / Cannabinoids', 'Phencyclidine (PCP)', 'Opiates', 'Benzodiazepine (BZD)','Barbiturate (BAR)','Methadone','Propoxyphene'],
    'Panel 10': ['Amphetamine (AMP)', 'Cocaine (COC)', 'Marijuana / Cannabinoids', 'Phencyclidine (PCP)', 'Opiates', 'Benzodiazepine (BZD)','Barbiturate (BAR)','Methadone','Propoxyphene','Methaqualone'],
    'Panel 11': ['Amphetamine (AMP)', 'Cocaine (COC)', 'Marijuana / Cannabinoids', 'Phencyclidine (PCP)', 'Opiates', 'Benzodiazepine (BZD)','Barbiturate (BAR)','Methadone','Propoxyphene','Methaqualone','Oxycodone'],
    'Panel 12': ['Marijuana / Cannabinoids', 'Amphetamine (AMP)', 'Cocaine (COC)', 'Phencyclidine (PCP)', 'Barbiturate (BAR)', 'Benzodiazepine (BZD)','Methadone','Propoxyphene','Methaqualone','MDMA(ecstasy)','TCA(Tricyclic Antidepressants)','Oxycodone'],
  };

  
  onPanelChange(event: any) {
    // this.drugCheckSubmitDisable = true;
    const selectedPanelValue = event.target.value;
    this.selectedPanel = selectedPanelValue;
    const labels = this.panelLabels[selectedPanelValue];
    this.selectedLabels = labels.map(label => ({ label, value:''}));
  }

  updateLabelValue(event:any, label:any) {
    label.value = event.target.value;
  }



  submitDate(date: any) {
    if (date) {
      console.log('Date submitted successfully:', date);
      this.candidateService
        .saveCaseReinitInfo (this.candidateCode,date)
        .subscribe((result: any) => {
          if (result.outcome === true) {
            Swal.fire({
              title: result.message,
              icon: 'success',
            }).then((result) => {
              if (result.isConfirmed) {
                window.location.reload();
              }
            });
          } else {
            Swal.fire({
              title: result.message,
              icon: 'warning',
            });
          }
        });
      
    }
  }

}
