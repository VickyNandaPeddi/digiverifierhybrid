import { Component, NgModule, OnInit } from '@angular/core';
import { OrgadminService } from 'src/app/services/orgadmin.service';
import Swal from 'sweetalert2';
import { FormGroup, FormControl,ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import {ModalDismissReasons, NgbModal, NgbCalendar, NgbDate} from '@ng-bootstrap/ng-bootstrap';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { CustomerService } from '../../services/customer.service';
import { CandidateService } from 'src/app/services/candidate.service';


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
 
  constructor(private orgadmin:OrgadminService,private customers:CustomerService,private candidateService: CandidateService,  private modalService: NgbModal) {
    this.orgid= localStorage.getItem('orgID');
    this.customers.getCustomersBill().subscribe((data: any)=>{
      if(this.orgid == 6){
        this.getCustID=data.data;
        console.log(data.data)
      }
      else{
        this.admin=true
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
    this.candidateService.getAllSuspectEmpListtt(organizationId).subscribe((data: any)=>{
      console.log(organizationId,",,,,,,,,,,,,,,,,,,")
      this.AllSuspectEmpList=data.data;
      console.log(this.AllSuspectEmpList,"*******************AllSuspectEmpList")
    });
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
    if (e.target.checked) {
      this.tmp.push(sid);
      console.warn("1::",e);
      console.warn("2::",sid);
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
    console.warn("SELECT ALL:: 5:",e);
    if (e.target.checked) {
      $(".childCheck").prop('checked', true);
      var  cboxRolesinput = $('.childCheck');
      var arrNumber:any = [];
      $.each(cboxRolesinput,function(idx,elem){
        var inputValues:any  = $(elem).val();
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

  uploadFakeCompanyDetails() {
    this.currentFile = this.selectedFiles.item(0);
    this.orgadmin.uploadFakeCompanyDetails(this.currentFile,this.organizationId).subscribe(
      (event:any) => {
        //console.log(event);
        if(event instanceof HttpResponse){
          Swal.fire({
            title: event.body.message,
            icon: 'success'
          }).then(function() {
            window.location.reload();
        });
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
      title: "Are You Sure to Delete Experience Details?",
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
      title: "Are You Sure to Delete Experience Details?",
      icon: 'warning'
    }).then((result) => {
      if (result.isConfirmed) {
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

}


