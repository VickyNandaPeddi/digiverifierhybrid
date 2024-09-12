import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { CustomerService } from '../../services/customer.service';
import Swal from 'sweetalert2';
import { ActivatedRoute, Router } from '@angular/router';
import { Customer } from 'src/app/models/customer';


@Component({
  selector: 'app-add-customer',
  templateUrl: './add-customer.component.html',
  styleUrls: ['./add-customer.component.scss']
})
export class AddCustomerComponent implements OnInit {
  pageTitle = 'Add Customer';
  QualificationData:any;
  showValidation:any;
  // showvalid:any
  public custmodel: Customer = new Customer;
  public logoFile: any = File;
  public showvalid: any;
  public passwordPolicy: any;
  orgId:any;
  CustomersData: any=[];
  passwordPolicyDay:any;
  addCustomers = new FormGroup({
    organizationName: new FormControl('', Validators.required),
    gstNumber: new FormControl('', [Validators.minLength(15), Validators.maxLength(15)]),
    panNumber: new FormControl('', [Validators.minLength(10), Validators.maxLength(10)]),
    saacCode : new FormControl('', [Validators.minLength(10), Validators.maxLength(10)]),
    billingAddress: new FormControl(),
    pocName: new FormControl(),
    customerPhoneNumber: new FormControl('', [Validators.minLength(10), Validators.maxLength(10), Validators.pattern('[6-9]\\d{9}')]),
    accountsPocPhoneNumber: new FormControl('', [Validators.minLength(10), Validators.maxLength(10), Validators.pattern('[6-9]\\d{9}')]),
    accountsPoc: new FormControl(),
    accountPocEmail: new FormControl('', Validators.email),
    organizationWebsite: new FormControl(),
    organizationEmailId: new FormControl('', [Validators.required,Validators.email]),
    callBackUrl: new FormControl(),
    tokenUrlForCallBack: new FormControl(),
    callBackUrlUser: new FormControl(),
    callBackUrlPassword: new FormControl(),
    emailConfig:new FormControl(),
    emailTemplate:new FormControl(),
    daysToPurge:new FormControl(),
    reportBackupEmail:new FormControl(),
    noYearsToBeVerified:new FormControl(),
    shipmentAddress: new FormControl(),
    showValidation:new FormControl(),
    passwordPolicy:new FormControl('',Validators.required),
    lastPasswords:new FormControl('',Validators.required)
  });
  constructor( private customers:CustomerService, private router: Router) {}

  ngOnInit(): void {
    this.customers.getPasswordPolicyDayFromConfig().subscribe((result: any)=>{
      console.log("result : ",result)
      this.passwordPolicyDay = result.data;      
      console.log(this.passwordPolicyDay)
    })
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
  onSubmit(addCustomers: FormGroup) {
    console.log(this.showvalid,"----------")
    console.log("passwordPolicy : ",this.passwordPolicy)
    
    const formData = new FormData();
    formData.append('organization', JSON.stringify(addCustomers.value));
    formData.append('file', this.logoFile);
    formData.append('showValidation',this.showvalid);
   
    return this.customers.saveCustomers(formData).subscribe((result:any)=>{
      if(result.outcome){
        const billUrl = 'admin/custbill/'+result.data['organizationId'];
        this.addCustomers.reset();
        Swal.fire({
          title: result.message,
          icon: 'success'
        });
        this.router.navigate([billUrl]);
      } else {
        Swal.fire({
          title: result.message,
          icon: 'warning'
        });
      }

    });
    
  }

}
