import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators, FormArray } from '@angular/forms';
import { CustomerService } from '../../services/customer.service';
import Swal from 'sweetalert2';
import { ActivatedRoute, Router } from '@angular/router';
import DOMPurify from 'dompurify';

@Component({
  selector: 'app-customer-bill',
  templateUrl: './customer-bill.component.html',
  styleUrls: ['./customer-bill.component.scss']
})

export class CustomerBillComponent implements OnInit {
  pageTitle = 'Customers Bill';
  
  orgID: any;
  getbgv: any=[];
  getBillValues: any=[];
  getCustomerBillData:any;
  BillData_stat:boolean=false;
  constructor( private customers:CustomerService, private router:ActivatedRoute, private fb: FormBuilder, private navRouter: Router, private routernav: Router) {
    this.orgID = this.router.snapshot.paramMap.get('organizationId');
    this.customers.getSources().subscribe((data: any)=>{
      this.getbgv=data.data;
      this.getbgv.forEach((element:any) => {
        element.serviceId = '';
        element.ratePerReport = '';
        element.ratePerItem = '';
      });
    });

    this.customers.getAllServices(this.orgID).subscribe((data: any)=>{
      this.getBillValues=data.data;
      if(this.getBillValues){
        this.getBillValues.forEach((element:any) => {
          // $(".billrpp"+element.source.sourceId).val(element.ratePerReport);
          // $(".billrpi"+element.source.sourceId).val(element.ratePerItem);
          // $(".billServiceId"+element.source.sourceId).val(element.serviceId);

          const billrpp = document.querySelector(".billrpp" + element.source?.sourceId) as HTMLInputElement;

            const billrpi = document.querySelector(".billrpi" + element.source?.sourceId) as HTMLInputElement;

            const billServiceId = document.querySelector(".billServiceId" + element.source?.sourceId) as HTMLInputElement;      

            if (billrpp) {

              billrpp.value = element.ratePerReport;

              console.log("Report:", billrpp.value);

            }

            if (billrpi) {

              billrpi.value = element.ratePerItem;

              console.log("Item:", billrpi.value);

            }

            if (billServiceId) {

              billServiceId.value = element.serviceId;

              console.log("Service:", billServiceId.value);

            }

          //  console.log("Element source ID:", element.ratePerReport);
          // $(".billrpp").filter(function() {
          //   return $(this).attr("data-sourceId") === element.source.sourceId;
          // }).val(element.ratePerReport);
          
          // $(".billrpi").filter(function() {
          //   return $(this).attr("data-sourceId") === element.source.sourceId;
          // }).val(element.ratePerItem);
          
          // $(".billServiceId").filter(function() {
          //   return $(this).attr("data-sourceId") === element.source.sourceId;
          // }).val(element.serviceId);
        });
      }

    });
    this.customers.getCustomersData(this.orgID).subscribe((data: any)=>{
      console.log(data.data);
      this.getCustomerBillData=data.data.total;
      if(this.getCustomerBillData){
        this.BillData_stat = true;
      }
    });
    
  }
  onKeyUp(){
   this.BillData_stat = false;
  }
  ngOnInit(): void {
     
  }
  billsubmit(){
    var billValue = $(".x-billcomponents");
    var i=0;
    $.each(billValue,function(idx,elem){
      var sanitizedValue = DOMPurify.sanitize(String(DOMPurify.sanitize(elem)));
      if(sanitizedValue!=""){
        i++;
      }
    })
    if(i>0){
      this.onSubmit()
    }
  }
  onSubmit() {
    return this.customers.saveCustomersBill(this.getbgv,this.orgID ).subscribe((result:any)=>{
      console.log(result);
      if(result.outcome === true){
        Swal.fire({
          title: result.message,
          icon: 'success'
        }).then((result) => {
          if (result.isConfirmed) {
            // const navURL = 'admin/admindashboard';
            const navURL = 'admin/custemailtemp/'+this.orgID;
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
    billUpdate() {
      this.getBillValues.forEach((element:any) => {
        // element.ratePerReport = $(".billrpp"+element.source.sourceId).val();
        // element.ratePerItem = $(".billrpi"+element.source.sourceId).val();
        // element.serviceId = $(".billServiceId"+element.source.sourceId).val();
        const ratePerReport = document.querySelector(".billrpp" + element.source.sourceId) as HTMLInputElement;

        const ratePerItem = document.querySelector(".billrpi" + element.source.sourceId) as HTMLInputElement;

        const serviceId = document.querySelector(".billServiceId" + element.source.sourceId) as HTMLInputElement;

     

        if (ratePerReport && ratePerItem && serviceId) {

          element.ratePerReport = ratePerReport.value;

          //console.log(element.ratePerReport);

          element.ratePerItem = ratePerItem.value;

          //console.log(element.ratePerItem);

          element.serviceId = serviceId.value;

          //console.log(element.serviceId);

        }

      });
      return this.customers.saveCustomersBill(this.getBillValues,this.orgID ).subscribe((result:any)=>{
        console.log(result);
        if(result.outcome === true){
          Swal.fire({
            title: result.message,
            icon: 'success'
          }).then((result) => {
            if (result.isConfirmed) {
              // const navURL = 'admin/admindashboard';
              const navURL = 'admin/custemailtemp/'+this.orgID;
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

      // Function to sanitize and validate the sourceId
      sanitizeUserInput(input:any){
  // Perform the necessary sanitization and validation here
  // For example, you can use a regular expression to ensure that sourceId is alphanumeric

  // If the sourceId is valid, return it; otherwise, throw an error or handle the invalid input appropriately
  if (/^[0-9a-zA-Z]+$/.test(input)) {
    return input;
  } else {
    throw new Error('Invalid sourceId');
  }
}

cancel(){
  const navURL = 'admin/custemailtemp/'+this.orgID;
   this.navRouter.navigate([navURL]);
}

onEditCustInfo(){
  const onEditCustInfoUrl = 'admin/custedit/'+this.orgID;
  console.log("onEditCustInfo URL::{}",onEditCustInfoUrl)
  this.routernav.navigate([onEditCustInfoUrl]);
 }

 onEditCustBill(){
  const onEditCustBillUrl = 'admin/custbill/'+this.orgID;
  console.log("onEditCustInfo URL::{}",onEditCustBillUrl)
  this.routernav.navigate([onEditCustBillUrl]);
 }

 onEditCustEmailTemp(){
  const onEditCustEmailTempUrl = 'admin/custemailtemp/'+this.orgID;
  console.log("onEditCustInfo URL::{}",onEditCustEmailTempUrl)
  this.routernav.navigate([onEditCustEmailTempUrl]);
 }

 onEditCustScope(){
  const onEditCustScopeUrl = 'admin/custscopeconfig/'+this.orgID;
  console.log("onEditCustInfo URL::{}",onEditCustScopeUrl)
  this.routernav.navigate([onEditCustScopeUrl]);
 }
}

