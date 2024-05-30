import { Component, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators, FormArray, Form } from '@angular/forms';
import { CustomerService } from '../../services/customer.service';
import Swal from 'sweetalert2';
import { ActivatedRoute, Router } from '@angular/router';
import {ModalDismissReasons, NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { CandidateService } from 'src/app/services/candidate.service';
import { AdminCReportApprovalComponent } from '../admin-c-report-approval/admin-c-report-approval.component';
import { OrgadminDashboardService } from 'src/app/services/orgadmin-dashboard.service';
import { ReportDeliveryDetailsComponent } from 'src/app/charts/report-delivery-details/report-delivery-details.component';
import JSZip from 'jszip';

@Component({
  selector: 'app-vendor-initiate',
  templateUrl: './vendor-initiate.component.html',
  styleUrls: ['./vendor-initiate.component.scss']
})


export class VendorInitiateComponent implements OnInit {
  @ViewChild(AdminCReportApprovalComponent) adminComponent!: AdminCReportApprovalComponent;
    pageTitle = 'Initiate Vendor Check';
    vendoruser:any
    appId: any;
    candidateName: any;
    candidateCode: any;
    candidateDetails: any=[];
    userID: any;
    getbgv: any=[];
    getBillValues: any=[];
    VendorData_stat:boolean=false;
    getVendorID: any=[];
    candidateId: any;
    sourceid:any;
    sourceName:any;
    vendorid:any;
    getCustomerBillData:any;
    closeModal: string | undefined;
    Employments: Boolean=false;
    education: Boolean=false;
    GlobalDatabasecheck: Boolean=false;
    Address: Boolean=false;
    IDItems: Boolean=false;
    crimnal: Boolean=false;
    DrugTest: Boolean=false;
    PhysicalVisit: Boolean=false;
    ofac:Boolean=false;
    public proofDocumentNew: any = File;
    public empexirdocument: any = File;
    vendorchecksupload: any=[];
    agentAttributeListForm: any[] = [];
    AgentAttributeCheck: any=[];
    crimnalGlobalAgentAttributeCheckMapped:any[]=[];
    educationAgentAttributeCheckMapped:any[]=[];
    addressCheckAgentAttributeCheckMapped:any[]=[];
    ofacAgentAttributeCheckMapped:any[]=[];
    showMessage:any
    idItemsAgentAttributeCheckMapped:any[]=[];
    getVenorcheckStatus: any[] = [];
    vendorCheckStatusMasterId: any;
    insuffRemarks: any;
    @ViewChild('insuffmodal') insuffModal: any;
    modalRef: NgbModalRef | undefined;
    inSuffCandidateDetail: any=[];
    employmentAgentAttributeCheckMapped: any[] = [];
    checkName:any;
    candidateAddress:any;
    candidateDOB:any;
    candidateFatherName:any;
    candidateGender:any;
    updatedValue: string | null = null;
    globalCheckDefaultType:string = " ";
    form: FormGroup = new FormGroup({});
    vendorProof:any;

    //Vendor related
    
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
  remarks:any
  checkStatus:any
  venderAttributeCheck: any = [];
  venderAttributeValue: any[] = [];
  colorid: any;
  vendorAttributeCheckMapped: any[] = [];
  vendorAttributeListForm: any[] = [];

  //Report Generation
  getServiceConfigCodes: any = [];
  reportStatus:any;
  reportStatusHexCode:any;
  conventionalReport:boolean = false;
  conventionalCandidate:boolean = false;
  vendorProofDetails:boolean = false;


    vendorlist = new FormGroup({
      // organizationIds: new FormControl('', Validators.required),
      vendorId: new FormControl(''),
      userId: new FormControl('', Validators.required),
      sourceId: new FormControl('', Validators.required),
      candidateId: new FormControl(''),
      documentName: new FormControl('', Validators.required),
      document: new FormControl('', Validators.required),
    });
    patchUserValues() {
      this.vendorlist.patchValue({
        sourceId: this.tmp,
        candidateId: this.candidateId,
      });  
    }

    vendorProofForm = new FormGroup({
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

    formReportApproval = new FormGroup({
      criminalVerificationColorId: new FormControl(''),
      globalDatabseCaseDetailsColorId: new FormControl(''),
    });

    formEditEdu = new FormGroup({
      documentname: new FormControl('', Validators.required),
      vendorId: new FormControl(''),
      sourceId: new FormControl('', Validators.required),
      candidateId: new FormControl(''), 
      value: new FormControl(''),
      // fileInput: new FormControl('',Validators.required),
      vendorCheckStatusMasterId: new FormControl(''),
      type: new FormControl('',Validators.required),
      checkType: new FormControl('')
    });
    patcheduValues() {
      this.formEditEdu.patchValue({
        sourceId: this.sourceid,
        candidateId: this.candidateId,
        vendorId:this.vendorid
      });
      
    }

    foremployements = new FormGroup({
      // candidateName: new FormControl('', Validators.required),
      documentname: new FormControl('', Validators.required),
      vendorId: new FormControl(''),
      sourceId: new FormControl(''),
      candidateId: new FormControl(''),
      value: new FormControl(''),
      // fileInput: new FormControl('',Validators.required),
      vendorCheckStatusMasterId: new FormControl(''),
      type: new FormControl('',Validators.required),
      checkType: new FormControl('')
    });
    patcheduValuesemp() {
      this.foremployements.patchValue({
        sourceId: this.sourceid,
        candidateId: this.candidateId,
        vendorId:this.vendorid
      });
      
    }
    
    forAddressCrimnalGlobal = new FormGroup({
      // candidateName: new FormControl('', Validators.required),
      // dateOfBirth: new FormControl('', Validators.required),
      // contactNo: new FormControl('', Validators.required),
      // fatherName: new FormControl('', Validators.required),
      // address: new FormControl('', Validators.required),
      vendorId: new FormControl(''),
      sourceId: new FormControl(''),
      candidateId: new FormControl(''),
      value: new FormControl(""),
      vendorCheckStatusMasterId: new FormControl(''),
      address: new FormControl(""),
       dateOfBirth: new FormControl(""),
       fatherName: new FormControl(""),
       gender: new FormControl(""),
       type: new FormControl('',Validators.required),
      checkType: new FormControl('')
    });
    patcheduValuesAddress() {
      this.forAddressCrimnalGlobal.patchValue({
        sourceId: this.sourceid,
        candidateId: this.candidateId,
        vendorId:this.vendorid
      });
      
    }

    formAddressCheck = new FormGroup({
      vendorId: new FormControl(''),
      sourceId: new FormControl(''),
      candidateId: new FormControl(''),
      value: new FormControl(""),
      // CandidateName: new FormControl(""), 
      vendorCheckStatusMasterId: new FormControl(''),
      type: new FormControl('',Validators.required),
      checkType: new FormControl('')
    })
    patchedForAddressCheck() {
      this.formAddressCheck.patchValue({
        sourceId: this.sourceid,
        candidateId: this.candidateId,
        vendorId:this.vendorid
      });
      
    }

    forDrugTest = new FormGroup({
      // candidateName: new FormControl('', Validators.required),
      // documentname: new FormControl('', Validators.required),
      // dateOfBirth: new FormControl('', Validators.required),
      // contactNo: new FormControl('', Validators.required),
      // fatherName: new FormControl('', Validators.required),
      // address: new FormControl('', Validators.required),
      // alternateContactNo: new FormControl('', Validators.required),
      // typeOfPanel: new FormControl('', Validators.required),
      vendorId: new FormControl(''),
      sourceId: new FormControl(''),
      candidateId: new FormControl(''),
      // fileInput: new FormControl('',Validators.required),
      vendorCheckStatusMasterId: new FormControl(''),
      address: new FormControl("",Validators.required),
      dateOfBirth: new FormControl("",Validators.required),
      fatherName: new FormControl("",Validators.required),
      gender: new FormControl("",Validators.required),
      value: new FormControl(''),
      checkType: new FormControl('')
    });
    patcheduValuesDrugTest() {
      this.forDrugTest.patchValue({
        sourceId: this.sourceid,
        candidateId: this.candidateId,
        vendorId:this.vendorid
      });
      
    }

    formpassport = new FormGroup({
      candidateName: new FormControl('', Validators.required),
      documentname: new FormControl('', Validators.required),
      vendorId: new FormControl(''),
      sourceId: new FormControl('', Validators.required),
      candidateId: new FormControl(''),
      value: new FormControl(""),
      vendorCheckStatusMasterId: new FormControl('') 
     
    });
    patchpassport() {
      this.formpassport.patchValue({
        sourceId: this.sourceid,
        candidateId: this.candidateId,
        vendorId:this.vendorid
      });
      
    }

    identityCheckForm = new FormGroup({
      candidateName: new FormControl('', Validators.required),
      documentname: new FormControl('', Validators.required),
      vendorId: new FormControl(''),
      sourceId: new FormControl('', Validators.required),
      candidateId: new FormControl(''),
      value: new FormControl(""),
      vendorCheckStatusMasterId: new FormControl(''),
       type: new FormControl('',Validators.required),
      checkType: new FormControl('')
     
    });
    patchIdentityCheck() {
      this.identityCheckForm.patchValue({
        sourceId: this.sourceid,
        candidateId: this.candidateId,
        vendorId:this.vendorid
      });
      
    }

    ofacForm = new FormGroup({
      candidateId: new FormControl(''),
      vendorId: new FormControl(''),
      sourceId: new FormControl('', Validators.required),
      vendorCheckStatusMasterId: new FormControl(''),
      value: new FormControl('')
    });
    
     patchOfacCheck() {
      this.ofacForm.patchValue({
        sourceId: this.sourceid,
        candidateId: this.candidateId,
        vendorId:this.vendorid,
      });
    }

    updateVendorForm = new FormGroup({
      vendorId: new FormControl(''),
      vendorcheckId: new FormControl('')
    })

    

    constructor( private customers:CustomerService, private router:ActivatedRoute, private fb: FormBuilder,public authService: AuthenticationService, 
      private modalService: NgbModal, private navRouter: Router,private candidateService:CandidateService, private orgAdmin:OrgadminDashboardService,    private reportDeliveryDetailsComponent:ReportDeliveryDetailsComponent,
      ) {
      this.userID = this.router.snapshot.paramMap.get('userId');
      this.candidateId = this.router.snapshot.paramMap.get('candidateId');
      this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');

      

      console.log(this.candidateId,"-----------------------------------")
      this.customers.getVendorCheckDetails(this.candidateId).subscribe((data: any)=>{
        
        this.vendorchecksupload=data.data;
        this.candidateName = this.vendorchecksupload[0].candidate.candidateName;
        this.appId = this.vendorchecksupload[0].candidate.applicantId;
        console.log(this.vendorchecksupload[0])
        if(this.getVendorID){
        for(var index in this.vendorchecksupload){
          for (var index1 in this.getVendorID){
          if(this.vendorchecksupload[index]["vendorId"]==this.getVendorID[index1]["userId"]){
            console.log(this.getVendorID[index1]["userFirstName"],"conuttt")
            this.vendorchecksupload[index]['username']=this.getVendorID[index1]["userFirstName"]
          }
          }
        }
      }
      console.log(this.vendorchecksupload)
      })

      this.candidateService
      .getCandidateFormData_admin(this.candidateCode)
      .subscribe((data: any) => {
        this.candidateDetails=data.data;
        this.conventionalCandidate = this.candidateDetails.candidate.conventionalCandidate;
        // this.vendorProofDetails = this.candidateDetails.vendorProofDetails.length > 0;
        this.vendorProofDetails = this.candidateDetails.vendorProofDetails && this.candidateDetails.vendorProofDetails.length > 0 ? true : false;
        // console.warn("vendorProofDetails : ",this.vendorProofDetails)
        this.candidateName = this.candidateDetails.candidate.candidateName;
        this.appId = this.candidateDetails.candidate.applicantId;
        // console.log(this.candidateDetails)
        this.candidateDOB = this.candidateDetails.candidate.aadharDob;
        this.candidateFatherName = this.candidateDetails.candidate.aadharFatherName 
        this.candidateGender = this.candidateDetails.candidate.aadharGender 
        this.vendorProof = this.candidateDetails.vendorProofDetails  
        // this.candidateAddress = this.candidateDetails.candidateCafAddressDto[0].candidateAddress 
        if (this.candidateDetails && 
          this.candidateDetails.candidateCafAddressDto && 
          this.candidateDetails.candidateCafAddressDto.length > 0 && 
          this.candidateDetails.candidateCafAddressDto[0].candidateAddress !== null && 
          this.candidateDetails.candidateCafAddressDto[0].candidateAddress !== undefined) {
          
          this.candidateAddress = this.candidateDetails.candidateCafAddressDto[0].candidateAddress;
      }                  
        this.candidateName = this.candidateDetails.candidate.candidateName;
        this.appId = this.candidateDetails.candidate.applicantId;
        console.log(this.candidateDetails)
        // console.warn("CandidateDOB:::",this.candidateDOB)
        // console.warn("candidateAddress:::",this.candidateAddress)
        // console.warn("candidateFatherName:::",this.candidateFatherName)
        // console.warn("candidateGender:::",this.candidateGender)
      
      });

      this.candidateService.getCandidateReportStatus(this.candidateCode).subscribe((data: any) => {
        // console.log("Candidate Report Status::{}",data.data);
        this.reportStatus=data.data.colorCode;
        this.reportStatusHexCode=data.data.colorHexCode;
        // console.warn("THIS REPORT STATUS>>",this.reportStatus)
    });

      if(authService.roleMatch(['ROLE_ADMIN']) || authService.roleMatch(['ROLE_AGENTHR']) || authService.roleMatch(['ROLE_AGENTSUPERVISOR'])){
        // console.log(localStorage.getItem('orgID'),"------------------org id")
        this.customers.getVendorList(0).subscribe((data: any)=>{
          this.getVendorID=data.data;
          console.log(this.getVendorID,"-------------vendoy----------------");
          if(this.userID){ 
            for (var index in this.getVendorID){
                console.log(this.getVendorID[index]["userId"],"indexxxxxxxxxxxxxxxxxxxx");
                if(this.userID==this.getVendorID[index]["userId"]){
                  console.log(this.userID,"final")
                  this.vendoruser=this.getVendorID[index]["userFirstName"]
                  console.log(this.vendoruser,"finaluser")
                }

            }
        }
          // if(this.userID){
          //     if (this.userID== item.)
          // }
        });
        console.log(this.vendorlist.value,"-------------vend----------------");
       
      }
      let rportData = {
        'userId': this.authService.getuserId()
      }
      
      this.customers.getSources().subscribe((data: any)=>{
        this.getbgv=data.data;
        console.log(this.getbgv,"-------------getbgv----------------");
        this.getbgv.forEach((element:any) => {
          element.serviceId = '';
          element.ratePerItem = '';
         // element.ratePerItem = '';

        });
      });
    if(this.userID){

      this.customers.getAllVendorServices(this.userID).subscribe((data: any)=>{
        console.log("--------------------calling service--------------")
        this.getBillValues=data.data;
        console.log(this.getBillValues,"--------------------")
        if(this.getBillValues){
          this.getBillValues.forEach((element:any) => {
            // $(".billrpp"+element.source.sourceId).val(element.ratePerItem);
            // $(".billrpi"+element.source.sourceId).val(element.tatPerItem);
            // $(".billServiceId"+element.source.sourceId).val(element.userId);
            const billrpp = document.querySelector(".billrpp" + element.source?.sourceId) as HTMLInputElement;

            const billrpi = document.querySelector(".billrpi" + element.source?.sourceId) as HTMLInputElement;

            const billServiceId = document.querySelector(".billServiceId" + element.source?.sourceId) as HTMLInputElement;
            if (billrpp) {

              billrpp.value = element.ratePerItem;

              console.log("Report:", billrpp.value);

            }

            if (billrpi) {

              billrpi.value = element.tatPerItem;

              console.log("Item:", billrpi.value);

            }

            if (billServiceId) {

              billServiceId.value = element.userId;

              console.log("Service:", billServiceId.value);

            }
            
          });
        }
  
      });
    }
   
    }

    uploadGlobalCaseDetails(event:any) {
      const fileType = event.target.files[0].name.split('.').pop();
      const file = event.target.files[0];
      if(fileType == 'pdf' || fileType == 'PDF' || fileType == 'png' || fileType == 'PNG' || fileType == 'jpg' || fileType == 'JPG' || fileType == 'zip'){
        this.proofDocumentNew = file;
        this.empexirdocument = file;
      }else{
        event.target.value = null;
        Swal.fire({
          title: 'Please select .jpeg, .jpg, .png file type only.',
          icon: 'warning'
        });
      }
      }

    tmp: any=[];
    roleCboxes(e:any){
      var sid = e.target.id;
      console.log("checked======================",sid)
      if (e.target.checked) {
        // console.log("value************",value)
        this.tmp.push(sid);
      } else {
        this.tmp.splice($.inArray(sid, this.tmp),1);
      }
      console.log("checked==============================",this.tmp)
    }

    selectAll(e:any){
      if (e.target.checked) {
       // $(e.target).parent().siblings().find(".billServiceId").prop('checked', true);
        const checkboxes = e.target.parentNode?.parentNode?.querySelectorAll('.item input');
      if (checkboxes) {
        checkboxes.forEach((checkbox: any) => {
          checkbox.checked = true;
        });
      }
       var  iteminput = $('.item input');
        var arrNumber:any = [];
        $.each(iteminput,function(idx,elem){
          // var inputValues:any  = $(elem).val();
          // console.log(inputValues);
          arrNumber.push($(this).val());
        });
        
        this.tmp = arrNumber;
        console.log(this.tmp);
      } else {
       // $(e.target).parent().siblings().find(".billServiceId").prop('checked', false);
       const checkboxes = e.target.parentNode?.parentNode?.querySelectorAll('.item input');
      if (checkboxes) {
        checkboxes.forEach((checkbox: any) => {
          checkbox.checked = false;
        });
      }
      }
      
    }

    getvendorid(id:any){
      this.vendorid = id;
    }

    onKeyUp(){
     this.VendorData_stat = false;
    }
    ngOnInit(): void {
      this.customers.getVenorcheckStatus().subscribe((data: any) => {
        if(data.data) {
          this.getVenorcheckStatus = data.data.filter((temp: any)=> {
            if(temp.checkStatusCode != 'INPROGRESS') {
              return temp;
            }
          });
        }
        console.log(this.getVenorcheckStatus);
      });
    }

    getvendorcheckstatuss(event: any) {
      console.log("control entered with value: ", event.target.value);
      this.vendorCheckStatusMasterId = event.target.value;
    }
    

    submitEditEdu(formEditEdu: FormGroup) {
     
      this.patcheduValues()
      const checkType = this.checkName +" "+this.formEditEdu.get('type')?.value;;
    //  console.warn("checkType>>>>>>"+checkType)
    //  console.warn("This sourcename:::",this.checkName)
     this.formEditEdu.patchValue({
       checkType:checkType
     })
      // console.log("....................",this.formEditEdu.value)
      const formData = new FormData();
      const educationAttributeValues = this.agentAttributeListForm.reduce((obj, item) => {

        if (item.value === null || item.value.trim() === '') {
          return false; // Return false if any item.value is null or empty
        }

        obj[item.label] = item.value;

        return obj;

      }, {});

      if (educationAttributeValues === false) {
        // console.error('Please enter values for all attributes');
        this.showMessage = "Please enter values for Mandatory Field";
      } else {
        // console.log('CrimnalGlobalAttributeValues:', educationAttributeValues);
      }

      // this.educationAgentAttributeCheckMapped = {...this.formEditEdu.value, ...educationAttributeValues}
      const mergedData = {
        ...this.formEditEdu.value,
        ...educationAttributeValues, 
      };
      formData.append('vendorchecks', JSON.stringify(mergedData));
      formData.append('file', this.proofDocumentNew);
      // console.log(".........formData...........",formData)
      if(this.formEditEdu.valid && educationAttributeValues !== false){
        console.log(".........valid...........")
        this.customers.saveInitiateVendorChecks(formData).subscribe((result:any)=>{

          // console.log(result,"=========result");
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
        title: 'Please enter the required details.',
        icon: 'warning'
      })
     }
    }

    isFieldEmpty(attribute: any): boolean {
      return attribute.value === null || attribute.value === '' || attribute.value === undefined;
    }

    submitEmploye(foremployements: FormGroup) {
      this.patcheduValuesemp()
      // console.log("....................", this.foremployements.value)
      const formData = new FormData();
      // console.log("this.agentAttributeListForm>>>>>>>",this.agentAttributeListForm);
      const checkType = this.checkName +" "+this.foremployements.get('type')?.value;;
      // console.warn("checkType>>>>>>"+checkType)
      // console.warn("This sourcename:::",this.checkName)
      this.foremployements.patchValue({
        checkType:checkType
      })
      const employmentAttributeValue = this.agentAttributeListForm.reduce((obj, item) => {
        if (item.value === null || item.value.trim() === '') {
          return false;
        }
        obj[item.label] = item.value;
        return obj;
   
      }, {});
   
      if (employmentAttributeValue === false) {
        // console.error('Please enter values for all attributes');
        this.showMessage = "Please enter values for Mandatory Field";
      } else {
        // console.log('employmentAttributeValue:', employmentAttributeValue);
      }
      //  delete agentAttributeValues.value
      this.employmentAgentAttributeCheckMapped = { ...this.foremployements.value, ...employmentAttributeValue }
      // console.log(" employmentAgentAttributeCheckMapped:::", this.employmentAgentAttributeCheckMapped);
   
      // console.warn("employmentAttributeValue===>", employmentAttributeValue);
   
      const mergedData = {
        ...this.foremployements.value,
        ...this.employmentAgentAttributeCheckMapped,
      };
   
      formData.append('vendorchecks', JSON.stringify(mergedData));
      // console.warn("mergedData++++++++++++++++++++", mergedData)
     
      formData.append('file', this.proofDocumentNew);
      if (this.foremployements.valid && employmentAttributeValue !== false) {
        if (this.foremployements.valid) {
          this.customers.saveInitiateVendorChecks(formData).subscribe((result: any) => {
            // console.log(result);
            if (result.outcome === true) {
              Swal.fire({
                title: result.message,
                icon: 'success'
              }).then((result) => {
                if (result.isConfirmed) {
                  window.location.reload();
                }
              });
            } else {
              Swal.fire({
                title: result.message,
                icon: 'warning'
              })
            }
          });
        }
      } else {
        Swal.fire({
          title: 'Please enter the required details.',
          icon: 'warning'
        })
      }
    }

    submitAgentAttributes(forAddressCrimnalGlobal: FormGroup) {
      this.patcheduValuesAddress()
      const checkType = this.checkName +" "+this.forAddressCrimnalGlobal.get('type')?.value;;
      // console.warn("checkType>>>>>>"+checkType)
      // console.warn("This sourcename:::",this.checkName)
      this.forAddressCrimnalGlobal.patchValue({
        checkType:checkType
      })

      // console.log("....................",this.forAddressCrimnalGlobal.value)
      const formData = new FormData();

      // console.log(this.agentAttributeListForm);
      const CrimnalGlobalAttributeValues = this.agentAttributeListForm.reduce((obj, item) => {

        if (item.value === null || item.value === undefined || item.value.trim() === '') {
          console.warn("False return")
          return false;
        }

         obj[item.label] = item.value;      
        return obj;

      }, {});

      console.warn("CrimnalGlobalAttributeValues>>",CrimnalGlobalAttributeValues)

      if (CrimnalGlobalAttributeValues === false) {
        // console.error('Please enter values for all attributes');
        this.showMessage = "Please enter values for Mandatory Field";
      } else {
        // console.log('CrimnalGlobalAttributeValues:', CrimnalGlobalAttributeValues);
      }

      //  delete agentAttributeValues.value
       this.crimnalGlobalAgentAttributeCheckMapped = {...this.forAddressCrimnalGlobal.value, ...CrimnalGlobalAttributeValues}

      // const finalValues = JSON.stringify(this.educationAgentAttributeCheckMapped);

      // console.log("finalValues",finalValues)

      // console.log(" CrimnalGlobalAttributeValues:::", this.crimnalGlobalAgentAttributeCheckMapped);

      // console.warn("CrimnalGlobalAttributeValues===>",CrimnalGlobalAttributeValues);

        const mergedData = {

            ...this.forAddressCrimnalGlobal.value,

          // ...this.crimnalGlobalAgentAttributeCheckMapped, 

        };

          //  formData.append('vendorchecks', JSON.stringify(this.forAddressCrimnalGlobal.value ))

          //  formData.append('vendorchecks', JSON.stringify(agentAttributeValues ))




        //  formData.append('vendorchecks', JSON.stringify(this.forAddressCrimnalGlobal.value ))
           formData.append('vendorchecks', JSON.stringify(mergedData));

        //  console.warn("mergedData++++++++++++++++++++",mergedData)

      // formData.append('vendorchecks', JSON.stringify(this.forAddressCrimnalGlobal.value));
      formData.append('file', this.proofDocumentNew);

      if(this.forAddressCrimnalGlobal.valid && CrimnalGlobalAttributeValues !== false){
        console.log(".........valid...........")
        this.customers.saveInitiateVendorChecks(formData).subscribe((result:any)=>{

          console.log(result);
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
          title: 'Please enter the required details.',
          icon: 'warning'
        })
       }
    }

            // AddressCheck Submit Form

            submitForAddressCheck(formAddressCheck:FormGroup){
              this.patchedForAddressCheck()
        
              const checkType = this.checkName +" "+this.formAddressCheck.get('type')?.value;;
              // console.warn("checkType>>>>>>"+checkType)
              // console.warn("This sourcename:::",this.checkName)
              this.formAddressCheck.patchValue({
                checkType:checkType
              })
        
              // console.log("....................",this.formEditEdu.value)
        
              // console.log("....................",this.formAddressCheck.value)
              const formData = new FormData();
        
              // console.log(this.agentAttributeListForm);
              // const CrimnalGlobalAttributeValues = this.agentAttributeListForm.reduce((obj, item) => {
                const addressAttributeValues = this.agentAttributeListForm.reduce((obj, item) => {
        
                  if (item.value === null || item.value.trim() === '') {
                    return false;
                }
        
                 obj[item.label] = item.value;      
                return obj;
        
              }, {});
        
              if (addressAttributeValues === false) {
                // console.error('Please enter values for all attributes');
                this.showMessage = "Please enter values for Mandatory Field";
              } else {
                // console.log('CrimnalGlobalAttributeValues:', addressAttributeValues);
              }
        
              //  delete agentAttributeValues.value
               this.addressCheckAgentAttributeCheckMapped = {...this.formAddressCheck.value, ...addressAttributeValues}
        
              // const finalValues = JSON.stringify(this.educationAgentAttributeCheckMapped);
        
              // console.log("finalValues",finalValues)
        
              // console.log(" CrimnalGlobalAttributeValues:::", this.addressCheckAgentAttributeCheckMapped);
        
              // console.warn("CrimnalGlobalAttributeValues===>",addressAttributeValues);
        
                const mergedData = {
        
                    ...this.formAddressCheck.value,
        
                  ...this.addressCheckAgentAttributeCheckMapped, 
        
                };
        
                  //  formData.append('vendorchecks', JSON.stringify(this.forAddressCrimnalGlobal.value ))
        
                  //  formData.append('vendorchecks', JSON.stringify(agentAttributeValues ))
        
        
        
        
                //  formData.append('vendorchecks', JSON.stringify(this.forAddressCrimnalGlobal.value ))
                   formData.append('vendorchecks', JSON.stringify(mergedData));
        
                //  console.warn("mergedData++++++++++++++++++++",mergedData)
        
              // formData.append('vendorchecks', JSON.stringify(this.forAddressCrimnalGlobal.value));
              formData.append('file', this.proofDocumentNew);
        
              if(this.formAddressCheck.valid && addressAttributeValues !== false){
                console.log(".........valid...........")
                this.customers.saveInitiateVendorChecks(formData).subscribe((result:any)=>{
        
                  // console.log(result);
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
                  title: 'Please enter the required details.',
                  icon: 'warning'
                })
               }
            }
        

    submitDrugTest(forDrugTest: FormGroup) {
      this.patcheduValuesDrugTest()
      const checkType = this.checkName
      // console.warn("checkType>>>>>>"+checkType)
      // console.warn("This sourcename:::",this.checkName)
      this.forDrugTest.patchValue({
        checkType:checkType
      })
      // console.log("....................",this.forDrugTest.value)
      const formData = new FormData();
      formData.append('vendorchecks', JSON.stringify(this.forDrugTest.value));
      formData.append('file', this.proofDocumentNew);
      return this.customers.saveInitiateVendorChecks(formData).subscribe((result:any)=>{

        // console.log(result);
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
    }

    submitIdentityCheck(identityCheckForm: FormGroup) {
      this.patchIdentityCheck();
      const candidateName = this.candidateName;
      const checkName = this.checkName;
      const checkType = this.checkName +" "+this.identityCheckForm.get('type')?.value;;
      //  console.warn("checkType>>>>>>"+checkType)
      // console.warn("This sourcename:::",this.checkName)
      identityCheckForm.patchValue({
        candidateName: candidateName,
        documentname:checkType,
        checkType:checkType
      })
      // console.log("....................",this.identityCheckForm.value)

      // console.warn("checkType>>>>>>"+checkType)
      // console.warn("This sourcename:::",this.checkName)
      // this.formAddressCheck.patchValue({
      // })
      
      const formData = new FormData();
      // console.log(this.agentAttributeListForm);
      const idItemsChecks = this.agentAttributeListForm.reduce((obj, item) => {

        if (item.value === null || item.value.trim() === '') {
          return false;
        }

         obj[item.label] = item.value;      
        return obj;

      }, {});

      if (idItemsChecks === false) {
        // console.error('Please enter values for all attributes');
        this.showMessage = "Please enter values for Mandatory Field";
      } else {
        // console.log('CrimnalGlobalAttributeValues:', idItemsChecks);
      }
      
      //  delete agentAttributeValues.value
      this.idItemsAgentAttributeCheckMapped = {...this.identityCheckForm.value, ...idItemsChecks}

      // const finalValues = JSON.stringify(this.educationAgentAttributeCheckMapped);

      // console.log("finalValues",finalValues)

      // console.log(" idItemsAgentAttributeCheckMapped:::", this.crimnalGlobalAgentAttributeCheckMapped);

      // console.warn("CrimnalGlobalAttributeValues===>",idItemsChecks);

        const mergedData = {

            ...this.identityCheckForm.value,

          ...this.idItemsAgentAttributeCheckMapped, 

        };

          //  formData.append('vendorchecks', JSON.stringify(this.forAddressCrimnalGlobal.value ))

          //  formData.append('vendorchecks', JSON.stringify(agentAttributeValues ))




        //  formData.append('vendorchecks', JSON.stringify(this.forAddressCrimnalGlobal.value ))
           formData.append('vendorchecks', JSON.stringify(mergedData));

        //  console.warn("mergedData++++++++++++++++++++",mergedData)

    
      if (identityCheckForm.valid && idItemsChecks !== false) {
        // console.log("....................", identityCheckForm.value);
    
        // const formData = new FormData();
        formData.append('vendorchecks', JSON.stringify(identityCheckForm.value));
        formData.append('file', this.proofDocumentNew);
    
        return this.customers.saveInitiateVendorChecks(formData).subscribe((result: any) => {
          // console.log(result);
          if (result.outcome === true) {
            Swal.fire({
              title: result.message,
              icon: 'success'
            }).then((result) => {
              if (result.isConfirmed) {
                 window.location.reload();
              }
            });
          } else {
            Swal.fire({
              title: result.message,
              icon: 'warning'
            });
          }
        });
      } else {
        Swal.fire({
          title: 'Please enter the required details.',
          icon: 'warning'
        });
    
        // Add the return statement here to satisfy TypeScript
        return undefined;
      }
    }

    submitOfacForm(ofacForm: FormGroup) {
      this.patchOfacCheck();
      // const candidateName = this.candidateName;
      // const checkName = this.checkName;
      // identityCheckForm.patchValue({
      //   candidateName: candidateName,
      //   documentname:checkName
      // })
      
      const formData = new FormData();
      console.log(this.agentAttributeListForm);
      const ofacCheck = this.agentAttributeListForm.reduce((obj, item) => {

        if (item.value === null || item.value.trim() === '') {
          return false;
        }

         obj[item.label] = item.value;      
        return obj;

      }, {});

      if (ofacCheck === false) {
        // console.error('Please enter values for all attributes');
        this.showMessage = "Please enter values for Mandatory Field";
      } else {
        // console.log('CrimnalGlobalAttributeValues:', ofacCheck);
      }
      
      //  delete agentAttributeValues.value
      this.ofacAgentAttributeCheckMapped = {...this.ofacForm.value, ...ofacCheck}

      // const finalValues = JSON.stringify(this.educationAgentAttributeCheckMapped);

      // console.log("finalValues",finalValues)

      // console.log(" ofacAgentAttributeCheckMapped:::", this.ofacAgentAttributeCheckMapped);

      // console.warn("ofacAgentAttributeCheckMapped===>",ofacCheck);

        const mergedData = {

            ...this.ofacForm.value,

          ...this.ofacAgentAttributeCheckMapped, 

        };

          //  formData.append('vendorchecks', JSON.stringify(this.forAddressCrimnalGlobal.value ))

          //  formData.append('vendorchecks', JSON.stringify(agentAttributeValues ))




        //  formData.append('vendorchecks', JSON.stringify(this.forAddressCrimnalGlobal.value ))
           formData.append('vendorchecks', JSON.stringify(mergedData));

        //  console.warn("mergedData++++++++++++++++++++",mergedData)

    
      if (ofacForm.valid && ofacCheck !== false) {
        // console.log("....................", ofacForm.value);
    
        // const formData = new FormData();
        formData.append('vendorchecks', JSON.stringify(ofacForm.value));
        formData.append('file', this.proofDocumentNew);
    
        return this.customers.saveInitiateVendorChecks(formData).subscribe((result: any) => {
          // console.log(result);
          if (result.outcome === true) {
            Swal.fire({
              title: result.message,
              icon: 'success'
            }).then((result) => {
              if (result.isConfirmed) {
                 window.location.reload();
              }
            });
          } else {
            Swal.fire({
              title: result.message,
              icon: 'warning'
            });
          }
        });
      } else {
        Swal.fire({
          title: 'Please enter the required details.',
          icon: 'warning'
        });
    
        // Add the return statement here to satisfy TypeScript
        return undefined;
      }
    }
    


    getsourceid(id:any){
      this.sourceid=id;

      const sourceIdString = this.sourceid;
      const sourceIdInt = parseInt(sourceIdString, 10);

      console.log(this.sourceid,"**********************")
      // const sourceName = this.getbgv.map((item: any) => item.sourceName);
      // console.warn("ALL SOURCE NAMES:", sourceName);

      const foundItem = this.getbgv.find((item: any) => item.sourceId === sourceIdInt);

      if (foundItem) {
        const correspondingSourceName = foundItem.sourceName;

        if(( correspondingSourceName && correspondingSourceName.toLowerCase().trim().includes("employment") || this.sourceid == "1")){
          this.Employments=true;
          this.education=false;
          this.GlobalDatabasecheck=false;
          this.Address=false;
          this.IDItems=false;
          this.crimnal=false;
          this.DrugTest=false;
          this.ofac=false; 
        }
        if(( correspondingSourceName && correspondingSourceName.toLowerCase().trim().includes("education")) || (this.sourceid == "2")){
          this.education=true;
          this.Employments=false;
          this.GlobalDatabasecheck=false;
          this.Address=false;
          this.IDItems=false;
          this.crimnal=false;
          this.DrugTest=false; 
          this.ofac=false;

        }
        if(( correspondingSourceName && correspondingSourceName.toLowerCase().trim().includes("global")) || (this.sourceid == "3")){
          this.GlobalDatabasecheck=true;
          this.Employments=false;
          this.education=false;
          this.Address=false;
          this.IDItems=false;
          this.crimnal=false;
          this.DrugTest=false;
          this.ofac=false;
 
        }
        if(( correspondingSourceName && correspondingSourceName.toLowerCase().trim().includes("address")) || (this.sourceid == "4")){
          console.warn("ADDRESS TRIGGERD::")
          this.Address=true;
          this.Employments=false;
          this.education=false;
          this.GlobalDatabasecheck=false;
          this.IDItems=false;
          this.crimnal=false;
          this.DrugTest=false;
          this.ofac=false;
 
        }
        if(( correspondingSourceName && correspondingSourceName.toLowerCase().trim().includes("id")) || (this.sourceid == "5")){
          this.IDItems=true;
          this.Employments=false;
          this.education=false;
          this.GlobalDatabasecheck=false;
          this.Address=false;
          this.crimnal=false;
          this.DrugTest=false;
          this.ofac=false;
 
        }
        if(( correspondingSourceName && correspondingSourceName.toLowerCase().trim().includes("criminal")) || (this.sourceid == "6")){
          this.crimnal=true;
          this.Employments=false;
          this.education=false;
          this.GlobalDatabasecheck=false;
          this.Address=false;
          this.IDItems=false;
          this.DrugTest=false; 
          this.ofac=false;

        }
        if(( correspondingSourceName && correspondingSourceName.toLowerCase().trim().includes("drug")) || (this.sourceid == "10")){
          this.DrugTest=true;
          this.Employments=false;
          this.education=false;
          this.GlobalDatabasecheck=false;
          this.Address=false;
          this.IDItems=false;
          this.crimnal=false;
          this.ofac=false;
 
        }
        if(( correspondingSourceName && correspondingSourceName.toLowerCase().trim().includes("physical")) || (this.sourceid == "9")){
          this.PhysicalVisit=true;
          this.DrugTest=false;
          this.Employments=false;
          this.education=false;
          this.GlobalDatabasecheck=false;
          this.Address=false;
          this.IDItems=false;
          this.crimnal=false;
          this.ofac=false;
 
        }
        if(( correspondingSourceName && correspondingSourceName.toLowerCase().trim().includes("ofac") || (this.sourceid == "44"))){
          this.ofac=true;
          this.PhysicalVisit=false;
          this.DrugTest=false;
          this.Employments=false;
          this.education=false;
          this.GlobalDatabasecheck=false;
          this.Address=false;
          this.IDItems=false;
          this.crimnal=false; 
        }
        // if(this.sourceid == "25"){
        //   this.DrugTest=false;
        //   this.Employments=false;
        //   this.education=true;
        //   this.GlobalDatabasecheck=false;
        //   this.Address=false;
        //   this.IDItems=false;
        //   this.crimnal=false; 
        // }
  

      } else {
        console.log("SourceId not found in getbgv array");
      }
      
     
    }
    
    opentemplate(id: any) {

      // console.warn("IDDDDD::",id);
      // console.warn("SourceID:::",this.sourceid);
     // this is the code for Fetching the AgentAttributesList

      this.customers.getAgentAttributes(this.sourceid).subscribe((data:any)=>{
        //console.warn("CheckId::",this.checkId);
        this.AgentAttributeCheck = data.data;
        this.checkName = this.AgentAttributeCheck.source.sourceName;
        console.warn("VendorAttribute::",data)
        console.warn("ATTRIBUTE:::",this.AgentAttributeCheck.agentAttributeList);
        this.agentAttributeListForm = this.AgentAttributeCheck.agentAttributeList.map((ele: any) => {
          let defaultValue;
          if (ele === 'Candidate Name') {
            defaultValue = this.candidateName;
        } else if (ele === 'Contact number') {
            defaultValue = null;
        } else if (ele === 'Alternative contact number') {
            defaultValue = null;
        } else if (ele === 'Address') {
            defaultValue = this.candidateAddress;
        } else if (ele.includes("Father's name")) {
         defaultValue = this.candidateFatherName;
         } 
         else if (ele.includes("Date of birth")) {
           defaultValue = this.candidateDOB;
           } 
           else if (ele.includes("Gender")) {
             defaultValue = this.candidateGender;
             } 
         else {
            defaultValue = null; // or any other default value
        }
          console.log(`Setting default value for "${ele}" to:`, defaultValue);
                      return {

                        label: ele,

                        value: defaultValue

                      };

                    });

        console.log(this.agentAttributeListForm);

                });

      this.modalService.open(id, {ariaLabelledBy: 'modal-basic-title'}).result.then((res) => {
        this.closeModal = `Closed with: ${res}`;
      }, (res) => {
        this.closeModal = `Dismissed ${this.getDismissReason(res)}`;
      });
  
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

      billUpdate() {
        console.log("______________inside button ------------------")
        this.getBillValues.forEach((element:any) => {
          // element.ratePerItem = $(".billrpp"+element.source.sourceId).val();
          // element.tatPerItem = $(".billrpi"+element.source.sourceId).val();
          // element.serviceId = $(".billServiceId"+element.source.userId).val();

          const ratePerItem = document.querySelector(".billrpp" + element.source.sourceId) as HTMLInputElement;

        const tatPerItem = document.querySelector(".billrpi" + element.source.sourceId) as HTMLInputElement;

        const serviceId = document.querySelector(".billServiceId" + element.source.sourceId) as HTMLInputElement;

     

        if (ratePerItem && tatPerItem && serviceId) {

          element.ratePerItem = ratePerItem.value;

          //console.log(element.ratePerReport);

          element.tatPerItem = tatPerItem.value;

          //console.log(element.ratePerItem);

          element.serviceId = serviceId.value;

          //console.log(element.serviceId);

        }
  
        });
        return this.customers.saveVendorChecks(this.getBillValues,this.userID ).subscribe((result:any)=>{
          console.log(result,'--------------------return---------------');
          if(result.outcome === true){
            Swal.fire({
              title: result.message,
              icon: 'success'
            }).then((result) => {
              if (result.isConfirmed) {
                const navURL = 'admin/addvendor';
                this.navRouter.navigate([navURL]);
              }
            });
          }else{
            Swal.fire({
              title: result.message,
              icon: 'warning'
            })
          }
        });
      }



      updateVendorSelectModal(content: any, item: any) {
        const modalRef = this.modalService.open(content);
        let elementById = document.getElementById("updateVendorSubmit");
        if (elementById) {
          const self = this;
          const vendorId = item.vendorId;
          const vendorCheckIds = item.vendorCheckId;
          this.updateVendorForm.patchValue({
            vendorId : vendorId,
            vendorcheckId: item.vendorcheckId
          });
          elementById.addEventListener("click", function () {
            }
          );
        }
      }
    
      closeStatusModal(modal: any) {
        modal.dismiss('Cross click');
        // window.location.reload();
      }

      vendorUpdate(vendorUpdateForm:FormGroup){
        console.warn("vendorUpdateForm:::::::::",vendorUpdateForm.value)
        return this.customers.updateVendor(vendorUpdateForm.value).subscribe((result:any)=>{

          console.log(result);
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
      }


      toggleState(item: any) {

        console.warn("ITEM . ",item.stopCheck)
        console.warn("vendorCheckId===",item.vendorcheckId)

        const requestData = {
          vendorcheckId : item.vendorcheckId,
          stopCheck : item.stopCheck
        }
        this.customers.stopCheck(requestData).subscribe((result:any) => {

          console.log(result);
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
        })

      }

      openInsufficiencyModal(remarks:any,item:any) {
        console.warn("Remarks:::>>>",remarks.value)
        console.warn("ITEMS:::",item)
        this.inSuffCandidateDetail = item;
        this.modalService.open(this.insuffModal, { centered: true }); // open the modal
      }

      inSuffRemarksSubmit(remarks: string) {
        console.warn("inSuffRemarksSubmit>>>>>",this.inSuffCandidateDetail);
        console.warn("Remarks Submit:::>>>", remarks);

        const inSufficiencyRemarks = {
          vendorCheckId: this.inSuffCandidateDetail.vendorcheckId,
          candidateId: this.inSuffCandidateDetail.candidate.candidateId,
          remarks : remarks
        }

        console.warn("inSufficiencyRemarks>>>>>>",inSufficiencyRemarks)

        this.customers.inSuffRemarks(inSufficiencyRemarks).subscribe((result:any) => {
          console.warn("DATA>>>>>>>>>>>",result)
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
        })
    
        
      }

      attribute = {
        value: ''
    };

    updateAttributeValue(input: HTMLInputElement): void {
      this.attribute.value = input.value;
    }

    openVendorModal(modalExperience: any, vendorChecks: any) {
      console.log(vendorChecks);
      this.modalService.open(modalExperience, {
        centered: true,
        backdrop: 'static',
      });
      // this.vendorChecks = vendorChecks;
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

    // detectContentType(base64String: string): string | null {
    //   const decodedData = atob(base64String);
    //   const firstByte = decodedData.charCodeAt(0);
  
    //   if (firstByte === 0x25 && decodedData.startsWith('%PDF-')) {
    //     return 'pdf';
    //   }
    //   return 'image';
    // }
    detectContentType(base64String: string): string | null {
      const decodedData = atob(base64String);
      const firstByte = decodedData.charCodeAt(0);
    
      if (firstByte === 0x25 && decodedData.startsWith('%PDF-')) {
        return 'pdf';
      } else if (decodedData.startsWith('PK')) {
        return 'zip';
      } else {
        // Assuming it's an image if not PDF or zip
        return 'image';
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


    //Vendor Modal ==========

    get civilProceedingsList() {
      return (
        this.vendorProofForm.get('legalProcedings.civilProceedingsList') as FormArray
      ).controls;
    }
  
    get criminalProceedingsList() {
      return (
        this.vendorProofForm.get(
          'legalProcedings.criminalProceedingsList'
        ) as FormArray
      ).controls;
    }
  
    addCivilProceeding() {
      const civilProceedingsArray = this.vendorProofForm.get(
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
  
      console.log("fshggfgbshdghfvh",this.vendorProofForm.value);
    }
  
    addCriminalProceeding() {
      const criminalProceedingsArray = this.vendorProofForm.get(
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
  
      console.log(this.vendorProofForm.value);
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
  

    private updateLegalProcedings() {
      if (this.isVendorAttributeForm) {
        // @ts-ignore
        this.vendorProofForm.get('legalProcedings').enable();
        // @ts-ignore
        this.vendorProofForm.get('value').disable();
      } else {
        // @ts-ignore
        this.vendorProofForm.get('legalProcedings').disable();
        // @ts-ignore
        this.vendorProofForm.get('value').enable();
      }
      if (this.selectedStatus == '3') {
        // @ts-ignore
        this.vendorProofForm.get('legalProcedings').disable();
        // @ts-ignore
        this.vendorProofForm.get('value').disable();
      }
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
        this.vendorProofForm.addControl('nameAsPerProof', new FormControl('',[Validators.required]));
        this.vendorProofForm.addControl('proofName', new FormControl('',[Validators.required]));
        this.vendorProofForm.addControl('dateOfBirth', new FormControl('',[Validators.required]));
        this.vendorProofForm.addControl('fatherName', new FormControl('',[Validators.required]));
        if (checkType && checkType.includes('PAN')) {
            this.idItemsPanORAadharORPassport = "Name as per Pan";
            this.idItemsProofName = "PAN";
            this.idItemsDateOfBirth = "Date of Birth";
            this.idItemsFatherName = "Father Name";
  
            this.vendorProofForm.patchValue({
              'nameAsPerProof': this.candidateDetails.candidate.panName,
              'proofName':this.candidateDetails.candidate.itrPanNumber,
              'dateOfBirth':this.candidateDetails.candidate.panDob,
              'fatherName':this.candidateDetails.candidate.aadharFatherName
  
          });
        }
        if (checkType && checkType.includes('Aadhar')) {
          this.idItemsPanORAadharORPassport = "Name as per Aadhar";
          this.idItemsProofName = "Aadhar"
          this.idItemsDateOfBirth = "Date of Birth";
          this.idItemsFatherName = "Father Name";
          this.vendorProofForm.patchValue({
            'nameAsPerProof': this.candidateDetails.candidate.aadharName,
            'proofName':this.candidateDetails.candidate.aadharNumber,
            'dateOfBirth':this.candidateDetails.candidate.aadharDob,
            'fatherName':this.candidateDetails.candidate.aadharFatherName
  
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
  
      const vendorValue = this.vendorProof[i].vendorAttribute
  
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
      this.vendorProofForm.patchValue({
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
      this.vendorProofForm.patchValue({
        remarks:this.remarks,
        vendorCheckStatusMasterId:this.checkStatus,
      })
    }
  
  
    // console.warn("his.vendorProofForm.patchValu>>>>>>",this.vendorProofForm.value)
  
  
      
  
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
                  defaultValue = this.candidateDetails.candidate.aadharDob;
                  console.log("defaultValue for Date of Birth >>>>", defaultValue);
              } else if (ele === 'Father Name') {
                  defaultValue = this.candidateDetails.candidate.aadharFatherName;
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
        this.vendorProofForm.patchValue({
          documentname: documentname,
          vendorcheckId: vendorcheckId,
          colorid: this.colorid,
          roleAdmin: true
          
        });
    }
    

    // onSubmit(vendorProofForm:FormGroup){

    // }


    // Submit AgentQcForm

    onSubmit(vendorProofForm:FormGroup) {
      this.showGlobalCheck = false;
      this.patchUserValues();
      let rawValue = vendorProofForm.getRawValue();
      // console.log('Form data:', this.form.getRawValue());
      // console.warn("RAWVALUE>>>>>>>>>",rawValue)
      // console.warn('VENDORLISTPATCH:::>>>>>>>>>', this.vendorlist.value);
      // console.log(this.vendorAttributeListForm);
  
      if(this.vendorProofForm.get('vendorCheckStatusMasterId')?.value === 'Clear'){
        // console.warn("================TRUE==================")
        this.vendorProofForm.patchValue({
          vendorCheckStatusMasterId:1,
        })
      }
      else if(this.vendorProofForm.get('vendorCheckStatusMasterId')?.value === 'In Sufficiency'){
        this.vendorProofForm.patchValue({
          vendorCheckStatusMasterId:3,
        })
      }else if(this.vendorProofForm.get('vendorCheckStatusMasterId')?.value === 'Major Discrepancy'){
        this.vendorProofForm.patchValue({
          vendorCheckStatusMasterId:4,
        })
      }else if(this.vendorProofForm.get('vendorCheckStatusMasterId')?.value === 'Minor Discrepancy'){
        this.vendorProofForm.patchValue({
          vendorCheckStatusMasterId:5,
        })
      }else if(this.vendorProofForm.get('vendorCheckStatusMasterId')?.value === 'Unable To Verify'){
        this.vendorProofForm.patchValue({
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
       const combinedData = { ...this.form.value, ...vendorProofForm.value };
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
        this.vendorProofForm.value,
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
          formData.append('vendorchecks', JSON.stringify(this.vendorProofForm.value));
  
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
        const {vendorCheckStatusMasterId,remarks,nameAsPerProof,proofName,legalProcedings } = this.vendorProofForm.value;
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
  
            const {vendorCheckStatusMasterId,remarks,nameAsPerProof,proofName,dateOfBirth,fatherName } = this.vendorProofForm.value; 
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
      formData.append('vendorchecks', JSON.stringify(this.vendorProofForm.value));
  
      formData.append('vendorAttributesValue', JSON.stringify(mergedData));
  
      // console.warn('mergedData++++++++++++++++++++', mergedData);
    
      // formData.append('vendorchecks', JSON.stringify(this.vendorlist.value));
      //  if (this.vendorlist.valid && venderAttributeValue !== false) {
        if(this.drugCheck !== true){
        if (this.vendorProofForm.valid && venderAttributeValue !== false) {
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
      if( this.drugCheck && this.drugtestCheckValidation !== false && this.vendorProofForm.valid && venderAttributeValue !== false){
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


    // REPORT GENERATION

    // submitReportApproval(formReportApproval: FormGroup, reportType: string) {

    // }

    submitReportApproval(formReportApproval: FormGroup, reportType: string) {
      // if (this.getServiceConfigCodes.includes('CRIMINAL')) {
      //   this.formReportApproval.controls[
      //     'criminalVerificationColorId'
      //   ].clearValidators();
      //   this.formReportApproval.controls[
      //     'criminalVerificationColorId'
      //   ].setValidators(Validators.required);
      //   this.formReportApproval.controls[
      //     'criminalVerificationColorId'
      //   ].updateValueAndValidity();
      //   if (this.CaseDetailsDoc.size == null) {
      //     formReportApproval.setErrors({ invalid: true });
      //   }
      // }
  
      // if (this.getServiceConfigCodes.includes('GLOBAL')) {
      //   this.formReportApproval.controls[
      //     'globalDatabseCaseDetailsColorId'
      //   ].clearValidators();
      //   this.formReportApproval.controls[
      //     'globalDatabseCaseDetailsColorId'
      //   ].setValidators(Validators.required);
      //   this.formReportApproval.controls[
      //     'globalDatabseCaseDetailsColorId'
      //   ].updateValueAndValidity();
      //   if (this.globalCaseDoc.size == null) {
      //     formReportApproval.setErrors({ invalid: true });
      //   }
      // }
  
      const candidateReportApproval = formReportApproval.value;
      const formData = new FormData();
      formData.append(
        'candidateReportApproval',
        JSON.stringify(candidateReportApproval)
      );
      console.warn("reportType : "+reportType)
      formData.append('criminalVerificationDocument','');
      formData.append('globalDatabseCaseDetailsDocument', '');
      formData.append('candidateCode', this.candidateCode);
      formData.append('reportType', reportType);
      this.candidateService
        .conventionalCandidateApplicationFormApproved(formData)
        .subscribe((result: any) => {
          if (result.outcome === true) {
            Swal.fire({
              title: result.message,
              icon: 'success',
            }).then((result) => {
              if (result.isConfirmed) {
                if(reportType== 'CONVENTIONALINTERIM'){
                  // this.reportDeliveryDetailsComponent.downloadInterimReport(this.candidateDetails.candidate,this.reportStatus);
                  if(this.candidateDetails.candidate.candidateCode) {
                    console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    this.conventionalReport = true;
                    // console.warn("this.candidateDetails.candidate.candidateCode>>",this.candidateDetails.candidate.candidateCode)
                    this.orgAdmin.getConventionalReportByCandidateCode(this.candidateDetails.candidate.candidateCode,this.reportStatus,this.conventionalReport).subscribe((url: any)=>{ 
                      // console.warn("url : "+url)
                      window.open(url.data, '_blank');
                    });
                  }
  
                }else{
                  console.log("showing the FINAL report ::");
                  this.reportDeliveryDetailsComponent.downloadFinalReportDirectFromQC(this.candidateDetails.candidate,this.reportStatus);
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


    
  downloadPdf(documentPathKey: any, documentname: any, soureName: any) {
  if(documentPathKey != null){  
  this.orgAdmin.downloadAgentUploadedDocument(documentPathKey).subscribe(
    (data: any) => {
      // if (soureName == "Employment" || soureName == "Education") {
        const contentType = this.detectContentType(data.message);
        console.warn("contentType : ",contentType)
        if(contentType == 'pdf'){
          const linkSource = 'data:application/pdf;base64,' + data.message;
          const downloadLink = document.createElement('a');
          downloadLink.href = linkSource;
          downloadLink.download = documentname;
          downloadLink.click();
        }
        else if(contentType == 'image'){
          const linkSource = 'data:application/png;base64,' + data.message;
          const downloadLink = document.createElement('a');
          downloadLink.href = linkSource;
          // downloadLink.download = documentname;
          downloadLink.download = `${documentname}.png`;
          downloadLink.click();
        }
        else{
          const linkSource = 'data:application/zip;base64,' + data.message;
          const downloadLink = document.createElement('a');
          downloadLink.href = linkSource;
          downloadLink.download = documentname;
          downloadLink.click();
        }
      
      // else if (documentPathKey != null) {
      //   const linkSource = 'data:application/pdf;base64,' + data.message;
      //   const downloadLink = document.createElement('a');
      //   downloadLink.href = linkSource;
      //   downloadLink.download = documentname;
      //   downloadLink.click();
      // } else {
        // Swal.fire({
        //   title: 'No Documents Uploaded',
        //   icon: 'warning',
        // });
      // }
      
    },
    (error: any) => {
      console.error('Error occurred while downloading:', error);
    }
  );
    
    // if (soureName == "Employment" && agentUploadedDocument != null) {
    //   const linkSource = 'data:application/zip;base64,' + agentUploadedDocument;
    //   const downloadLink = document.createElement('a');
    //   downloadLink.href = linkSource;
    //   downloadLink.download = documentname;
    //   downloadLink.click();
    // }
    // else if (agentUploadedDocument != null) {
    //   const linkSource = 'data:application/pdf;base64,' + agentUploadedDocument;
    //   const downloadLink = document.createElement('a');
    //   downloadLink.href = linkSource;
    //   downloadLink.download = documentname;
    //   downloadLink.click();
    // } else {
    //   // Swal.fire({
    //   //   title: 'No Documents Uploaded',
    //   //   icon: 'warning',
    //   // });
    // }
  }
}


  downloadAllDocument() {
    console.warn("Document All", this.vendorchecksupload);
    console.warn("CANDATENAME::", this.candidateName);
    // Create a new instance of JSZip
    const zip = new JSZip();
  
    const downloadPromises: Promise<void>[] = [];
  
    // Iterate through each item in vendorchecksupload
    this.vendorchecksupload.forEach((item: any) => {
      const promise = new Promise<void>((resolve, reject) => {
        // Download the file data
        if (item.agentUploadDocumentPathKey != null) {
          this.orgAdmin.downloadAgentUploadedDocument(item.agentUploadDocumentPathKey).subscribe(
            (data: any) => {
              // console.warn("Result>>", data);
  
              // Get the content type
              const contentType = this.detectContentType(data.message);
              console.warn("contentType : ", contentType);
              let fileExtension = '';
  
              // Determine the file extension based on content type
              if (contentType === 'pdf') {
                fileExtension = 'pdf';
              }
              else if(contentType == 'image'){
                fileExtension = 'png';
              }
              else {
                fileExtension = 'zip';
              }
  
              // Add the file to the zip
              zip.file(`${item.documentname}.${fileExtension}`, data.message, { base64: true });
  
              resolve();
            },
            (error) => {
              console.error('Error occurred while downloading:', error);
              reject(error);
            }
          );
        } else {
          // If agentUploadDocumentPathKey is null, resolve the promise immediately
          resolve();
        }
      });
  
      downloadPromises.push(promise);
    });
  
    // Wait for all download promises to resolve
    Promise.all(downloadPromises).then(() => {
      // Generate the zip file
      zip.generateAsync({ type: "blob" }).then((content) => {
        // Create a download link for the zip file
        const downloadLink = document.createElement('a');
        downloadLink.href = URL.createObjectURL(content);
        downloadLink.download = `${this.candidateName}_${this.appId}.zip`;
        downloadLink.click();
      });
    }).catch((error) => {
      console.error('Error occurred while downloading files:', error);
    });
  }
  
  }
  
  