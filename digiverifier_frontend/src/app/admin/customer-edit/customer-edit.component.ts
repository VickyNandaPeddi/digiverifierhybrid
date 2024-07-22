import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { CustomerService } from '../../services/customer.service';
import Swal from 'sweetalert2';
import { ActivatedRoute, Router } from '@angular/router';
import {ModalDismissReasons, NgbModal} from '@ng-bootstrap/ng-bootstrap';
@Component({
  selector: 'app-customer-edit',
  templateUrl: './customer-edit.component.html',
  styleUrls: ['./customer-edit.component.scss']
})
export class CustomerEditComponent implements OnInit {
  pageTitle = 'Edit Customer';
  public logoFile: any = File;
  viewLogo:any;
  orgId:any;
  CustomersData: any=[];
  public showvalid: any;
  public passwordPolicy: any;
  updateCustomers = new FormGroup({
    organizationId: new FormControl('', Validators.required),
    organizationName: new FormControl('', Validators.required),
    organizationEmailId: new FormControl('', [Validators.required,Validators.email]),
    customerPhoneNumber: new FormControl('', [Validators.minLength(10), Validators.maxLength(10), Validators.pattern('[6-9]\\d{9}')]),
    gstNumber: new FormControl('', [Validators.minLength(15), Validators.maxLength(15)]),
    pocName: new FormControl(),
    shipmentAddress: new FormControl(),
    organizationWebsite: new FormControl(),
    organizationLogo: new FormControl(),
    accountPocEmail: new FormControl('', Validators.email),
    accountsPoc: new FormControl(),
    accountsPocPhoneNumber: new FormControl('', [Validators.minLength(10), Validators.maxLength(10), Validators.pattern('[6-9]\\d{9}')]),
    billingAddress: new FormControl(),
    callBackUrl: new FormControl(),
    showValidation:new FormControl(),
    passwordPolicy:new FormControl('',Validators.required),
    lastPasswords:new FormControl('',Validators.required)
  });
  constructor( private customers:CustomerService, 
    private router:ActivatedRoute,
    private routernav: Router,private modalService: NgbModal) {}
  readonlyStat = true;
  ngOnInit(): void {
    this.customers.getCustomersData(this.router.snapshot.params.organizationId).subscribe((result: any)=>{
      this.CustomersData = result.data;
      this.orgId=result.data.organizationId;
      console.log(this.CustomersData)
      this.viewLogo = "data:image/png;base64,"+this.CustomersData.organizationLogo;
      this.updateCustomers = new FormGroup({
        organizationId: new FormControl(result.data['organizationId'], Validators.required),
        organizationName: new FormControl(result.data['organizationName'], Validators.required),
        organizationEmailId: new FormControl(result.data['organizationEmailId'], [Validators.required,Validators.email]),
        customerPhoneNumber: new FormControl(result.data['customerPhoneNumber'], [Validators.minLength(10), Validators.maxLength(10), Validators.pattern('[6-9]\\d{9}')]),
        gstNumber: new FormControl(result.data['gstNumber'], [Validators.minLength(15), Validators.maxLength(15)]),
        pocName: new FormControl(result.data['pocName']),
        shipmentAddress: new FormControl(result.data['shipmentAddress']),
        organizationWebsite: new FormControl(result.data['organizationWebsite']),
        accountPocEmail: new FormControl(result.data['accountPocEmail'], Validators.email),
        accountsPoc: new FormControl(result.data['accountsPoc']),
        accountsPocPhoneNumber: new FormControl(result.data['accountsPocPhoneNumber'], [Validators.minLength(10), Validators.maxLength(10), Validators.pattern('[6-9]\\d{9}')]),
        billingAddress: new FormControl(result.data['billingAddress']),
        callBackUrl: new FormControl(result.data['callBackUrl']),
        // showValidation: new FormControl(result.data['showValidation'], Validators.required),
        showValidation:new FormControl('',Validators.required),
        passwordPolicy:new FormControl('',Validators.required),
        lastPasswords:new FormControl('',Validators.required)
      })
    });
  }
  selectLogo(event:any) {
    const fileType = event.target.files[0].name.split('.').pop();
    const file = event.target.files[0];
    if(fileType == 'jpeg' || fileType == 'JPEG' || fileType == 'png' || fileType == 'PNG' || fileType == 'jpg' || fileType == 'JPG'){
      this.logoFile = file;
    }else{
      event.target.value = null;
      Swal.fire({
        title: 'Please select .jpeg, .jpg, .png file type only.',
        icon: 'warning'
      });
    }  
  }

  selectshowValidation(event:any){
    this.showvalid=event.target.value
    console.log( this.showvalid,"-----------------------kkk")
    
  }
  selectPasswordPolicy(event:any){
    this.passwordPolicy=event.target.value
    console.log( this.passwordPolicy,"-----------------------kkk")
    
  }
  onSubmit(updateCustomers: FormGroup) {
    const formData = new FormData();
    formData.append('organization', JSON.stringify(updateCustomers.value));
    formData.append('file', this.logoFile);
    formData.append('showValidation',this.showvalid);
    if (this.updateCustomers.valid) {
      this.customers.saveCustomers(formData).subscribe((result:any)=>{
          console.log(result);
        if(result.outcome){
          const billUrl = 'admin/custbill/'+result.data['organizationId'];
          Swal.fire({
            title: result.message,
            icon: 'success'
          });
          this.routernav.navigate([billUrl]);
        } else {
          Swal.fire({
            title: result.message,
            icon: 'warning'
          });
        }
      });
    }else{
      const invalidFields = Object.keys(updateCustomers.controls).filter(key => updateCustomers.get(key)?.invalid);
      const errorMessage = `Please enter the required information: ${invalidFields.join(', ')}`;
      Swal.fire({
        title: errorMessage,
        icon: 'warning'
      })
    }
    
  }

  openModal(modalData:any){
    this.modalService.open(modalData, {
     centered: true,
     backdrop: 'static'
    });
 }

 onEditCustInfo(){
  const onEditCustInfoUrl = 'admin/custedit/'+this.orgId;
  console.log("onEditCustInfo URL::{}",onEditCustInfoUrl)
  this.routernav.navigate([onEditCustInfoUrl]);
 }

 onEditCustBill(){
  const onEditCustBillUrl = 'admin/custbill/'+this.orgId;
  console.log("onEditCustInfo URL::{}",onEditCustBillUrl)
  this.routernav.navigate([onEditCustBillUrl]);
 }

 onEditCustEmailTemp(){
  const onEditCustEmailTempUrl = 'admin/custemailtemp/'+this.orgId;
  console.log("onEditCustInfo URL::{}",onEditCustEmailTempUrl)
  this.routernav.navigate([onEditCustEmailTempUrl]);
 }
 onEditCustScope(){
  const onEditCustScopeUrl = 'admin/custscopeconfig/'+this.orgId;
  console.log("onEditCustInfo URL::{}",onEditCustScopeUrl)
  this.routernav.navigate([onEditCustScopeUrl]);
 }

}
