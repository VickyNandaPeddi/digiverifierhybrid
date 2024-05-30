import { Component, NgModule, OnInit } from '@angular/core';
import { OrgadminService } from 'src/app/services/orgadmin.service';
import Swal from 'sweetalert2';
import { FormGroup, FormControl,ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import {ModalDismissReasons, NgbModal, NgbCalendar, NgbDate} from '@ng-bootstrap/ng-bootstrap';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { CustomerService } from '../../services/customer.service';
import { CandidateService } from 'src/app/services/candidate.service';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { OrgadminDashboardService } from 'src/app/services/orgadmin-dashboard.service';
import { result } from 'lodash';


@Component({
  selector: 'dnh.component',
  templateUrl: './dnh.component.html',
  styleUrls: ['./dnh.component.scss']
})


export class DNHComponent implements OnInit {
  pageTitle = 'DNH DB';
  closeModal: string | undefined;
  selectedFiles: any;
  currentFile: any;
  getCustID: any=[];
  organizationId:any;
  cusname:any;
  AllSuspectEmpList: any=[];
  orgid: any;
  organization:any=[];
  organizationame:any;
  admin:boolean=false;
  tmp: any=[];

  pageNumber: number = 0;
  pageSize: number = 100;
  totalPages: number = 0; // Add this variable
  filteredData: any[] = [];



  formSuSpectEMP = new FormGroup({
    suspectCompanyName:new FormControl(''),
    address: new FormControl(''),
    id: new FormControl(''),
    isActive:new FormControl(''),
    // organizationId: new FormControl('', Validators.required),
  });

  
  
  formToDelete = new FormGroup({
    suspectEmpMasterId: new FormControl('', Validators.required),
  });

  searchForm = new FormGroup({
    searchText: new FormControl('',Validators.required) // Initialize with an empty string

  })
 
  constructor(private orgadmin:OrgadminService,private customers:CustomerService,private candidateService: CandidateService,
      private modalService: NgbModal, public authService: AuthenticationService,private dashboardservice:OrgadminDashboardService) {
    this.orgid= this.authService.getOrgID();
    this.customers.getCustomersBill().subscribe((data: any)=>{
      if(this.orgid == 6){
        this.getCustID=data.data;
        console.log(data.data)
      }
      else{
        this.admin=true
        this.getCustomerData(this.orgid);
        this.organization =data.data; 
        for (let item in this.organization){
          console.log(this.organization[item].organizationId);
          if(this.organization[item].organizationId==this.orgid){
              this.organizationame=this.organization[item].organizationName
          }

        }
        
      }
    })

    console.log(this.getCustID)

   }

  ngOnInit(): void {

  }

  deletePatchValues() {
    this.formToDelete.patchValue({
      suspectEmpMasterId: this.tmp,
    });
  }
 
  getCustomerData(organizationId:any){
    this.organizationId=organizationId;
   
    console.log(organizationId,"organizationId")
    this.candidateService.getAllSuspectEmpListtt(organizationId, this.pageNumber, this.pageSize).subscribe((data: any)=>{

      console.log(organizationId,",,,,,,,,,,,,,,,,,,")
      this.AllSuspectEmpList=data.data;
    
      this.totalPages = data.status; // Set the total pages
      console.warn("totalPages::",this.totalPages);
      console.log(this.AllSuspectEmpList,"*******************AllSuspectEmpList")
    });
  }

  onPageChange(newPageNumber: number) {
    console.warn("button clicked!!")
    this.pageNumber = newPageNumber;
    this.getCustomerData(this.organizationId);
    }

 performSearch(){
  const searchTextControl = this.searchForm.get('searchText');
  if (searchTextControl !== null) {
    const searchText = searchTextControl.value; // Get the value
    console.log('Search Text:', searchText);
    const username = this.authService.getuserName();
       console.warn("username:::",username);
       const searchData = {
               userSearchInput: searchText,
               orgId: this.orgid
             };
             console.log('Search Data:', searchData);
    // You can use searchText in your code or pass it to a service.
     this.orgadmin.searchDnh(searchData).subscribe((data:any)=>{
         this.AllSuspectEmpList=data.data;
         console.warn("jfsbkjhsj::",this.AllSuspectEmpList)
         console.warn("data",data);
      })
  }
  
}

  triggerModal(content: any) {
    this.modalService.open(content).result.then((res) => {
      this.closeModal = `Closed with: ${res}`;
    }, (res) => {
      this.closeModal = `Dismissed ${this.getDismissReason(res)}`;
    });
  }

  selectFile(event:any) {
    const fileType = event.target.files[0].name.split('.').pop();
    if(fileType == 'xlsx' || fileType == 'XLSX' || fileType == 'xls' || fileType == 'XLS' || fileType == 'csv' || fileType == 'CSV'){
      this.selectedFiles = event.target.files;
    }else{
      event.target.value = null;
      Swal.fire({
        title: 'Please select .xlsx, .xls, .csv file type only.',
        icon: 'warning'
      });
    }

  }

  // select single by single
  childCheck(e:any){
    var sid = e.target.id;
    console.warn("SID:::",sid);
    if (e.target.checked) {
      this.tmp.push(sid);
    } else {
      this.tmp.splice($.inArray(sid, this.tmp),1);
      console.warn("SLICE::",this.tmp);
    }
  }
  childCheckselected(sid:any){
    this.tmp.push(sid);
    console.warn("SELECTED CHECKS 3::",this.tmp);
  }

  // select all 
  selectAll(e:any){
    console.warn("selectAll::"+e.id)
    console.warn("SELECT ALL:: 5:",e);
    if (e.target.checked) {
      $(".childCheck").prop('checked', true);
      var  cboxRolesinput = $('.childCheck');
      var arrNumber:any = [];
      $.each(cboxRolesinput,function(idx,elem){
   //     var inputValues:any  = $(elem).val();
        // console.log(inputValues);
        arrNumber.push($(this).val());
      });
      
      this.tmp = arrNumber;
      console.warn("SELECT ALL TEMP 4::",this.tmp);
    } else {
      $(".childCheck").prop('checked', false);
    }
    
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

  proceed() {
    this.currentFile = this.selectedFiles.item(0);
    console.warn("currentFile::",this.currentFile)
    const status = "proceed";
    this.orgadmin.uploadFakeCompanyDetailsStatus(this.currentFile,this.organizationId,status).subscribe(
      (event:any) => {
        console.log(event);
        if(event instanceof HttpResponse){
          if(event.body.outcome){
            Swal.fire({
              title: event.body.message,
              icon: 'success'
            }).then(function() {
              window.location.reload();
          });
          } 
          else{
            Swal.fire({
              title: event.body.message,
              icon: 'error',
              showCancelButton: true,
              confirmButtonText: 'Proceed',
              cancelButtonText: 'Skip',
             // cancelButtonText: 'Skip',
            }).then(function() {
              window.location.reload();
          });
          } 
        }
       });
  }


  uploadFakeCompanyDetails() {
    this.currentFile = this.selectedFiles.item(0);
    console.warn("currentFile::",this.currentFile)

    this.orgadmin.uploadFakeCompanyDetails(this.currentFile,this.organizationId).subscribe(
      (event:any) => {
        console.log(event);
        if(event instanceof HttpResponse){
          if(event.body.outcome){
            Swal.fire({
              title: event.body.message,
              icon: 'success'
            }).then(function() {
              window.location.reload();
          });
          } 
          else {
            Swal.fire({
              title: event.body.message,
              icon: 'error',
              showCancelButton: true,
              confirmButtonText: 'Proceed',
              cancelButtonText: 'Skip',
            }).then((result) => {
              if (result.isConfirmed) {
                // The "Proceed" button was clicked
                // Call your function here
                this.proceed();
              } else if (result.dismiss === Swal.DismissReason.cancel) {
                window.location.reload();
              }
            });
          }
          
        }
       });
  }

 
  openSuspectEmployeeModal(modalSuSpectEmploye:any, suspectEmpMasterId:any,
    suspectCompanyName: any,
    address:any,
    ){
    this.modalService.open(modalSuSpectEmploye, {
     centered: true,
     backdrop: 'static'
    });
    this.formSuSpectEMP.patchValue({
      id: suspectEmpMasterId,
      suspectCompanyName: suspectCompanyName,
      address: address,

     });
  }

  deleteSuspectEmp(id: any){
    $(this).hide();
    Swal.fire({
      title: "Are You Sure to Delete DNHDB Details?",
      icon: 'warning'
    }).then((result) => {
      if (result.isConfirmed) {
        this.candidateService.deleteSuspectExpById(id).subscribe((data: any)=>{
             if(data.outcome === true){
              Swal.fire({
                title: data.message,
                icon: 'success'
              }).then((data) => {
                if (data.isConfirmed) {
                  window.location.reload();
                }
              });
            }else{
              Swal.fire({
                title: data.message,
                icon: 'warning'
              })
            } 
           })
      }
    });
  }


  //
  deleteSuspectedEmployers(){
    this.deletePatchValues();
    console.warn("Patch Value::",this.deletePatchValues);
    $(this).hide();
    Swal.fire({
      title: "Are You Sure to Delete DHNDB Details?",
      icon: 'warning'
    }).then((result) => {
      if (result.isConfirmed) {
        console.warn("FORMTODELETE::",this.formToDelete.value);
        this.candidateService.deleteSuspectEmployers(this.formToDelete.value).subscribe((data: any)=>{
             if(data.outcome === true){
              Swal.fire({
                title: data.message,
                icon: 'success'
              }).then((data) => {
                if (data.isConfirmed) {
                  window.location.reload();
                }
              });
            }else{
              Swal.fire({
                title: data.message,
                icon: 'warning'
              })
            } 
           })
      }
    });
    
  }

  submitSuspectEmploye(){
    if(this.formSuSpectEMP.valid){
      console.log("..........................employeeeeee..........",this.formSuSpectEMP.value)
     this.candidateService.updateSpectEMPloyee(this.formSuSpectEMP.value).subscribe((result:any)=>{
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


removeDNHDB(){
    const orgId = this.authService.getOrgID();
    console.warn("orgId ======================== ",orgId);

Swal.fire({
  title: 'Are you sure?',
  text: `This will remove all suspect employer DNHDB for ${this.organizationame}. Do you want to proceed?`,
  icon: 'warning',
  showCancelButton: true,
  confirmButtonColor: '#3085d6',
  cancelButtonColor: '#d33',
  confirmButtonText: 'Yes, proceed!'
}).then((confirmationResult) => {
  if (confirmationResult.isConfirmed) {
    this.candidateService.removeAllSuspectEmpByOrgId(orgId).subscribe((result: any) => {
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
  }
});

}

downloadUploadResourceFile(uploadFor: any,uploadType: any): void {
  this.dashboardservice.downloadUploadResourceFile(uploadFor,uploadType).subscribe((data: Blob) => {
    const blob = new Blob([data], { type: 'application/octet-stream' });
      const url = window.URL.createObjectURL(blob);

      // Create a temporary anchor element
      const a = document.createElement('a');
      a.href = url;
      a.download = uploadFor+"."+uploadType; // Set the filename
      document.body.appendChild(a);

      // Trigger the click event on the anchor element
      a.click();

      // Remove the temporary anchor element
      document.body.removeChild(a);
  });
}

 getDisplayedPages(): number[] {
    const maxPages = 5;
    const startPage = Math.max(0, this.pageNumber - Math.floor(maxPages / 2));
    const endPage = Math.min(this.totalPages - 1, startPage + maxPages - 1);

    const pages: number[] = [];
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    return pages;
  }

}


