import { Component, OnInit } from '@angular/core';
import { CustomerService } from '../../services/customer.service';
import {NgbCalendar, NgbDate} from '@ng-bootstrap/ng-bootstrap';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-employment-tracker-sheet',
  templateUrl: './employment-tracker-sheet.component.html',
  styleUrls: ['./employment-tracker-sheet.component.scss']
})
export class EmploymentTrackerSheetComponent implements OnInit {
  pageTitle = 'Employment Tracker Sheet';
  fromDate: any;
  toDate: any;
  setfromDate: any;
  settoDate: any;
  getToday: NgbDate;
  getMinDate: any;
  initToday: any;
  custId: any=0;
  organizationIds: any = [];
  trackerFilter = new FormGroup({
    fromDate: new FormControl('', Validators.required),
    toDate: new FormControl('', Validators.required),
  });
  constructor(public calendar: NgbCalendar, private customers: CustomerService,public authService: AuthenticationService) {
    this.getToday = calendar.getToday(); 
    let inityear = this.getToday.year;
    let initmonth = this.getToday.month <= 9 ? '0' + this.getToday.month : this.getToday.month;;
    let initday = this.getToday.day <= 9 ? '0' + this.getToday.day : this.getToday.day;
    let initfinalDate = initday + "/" + initmonth + "/" + inityear;
    this.initToday = initfinalDate;
    if(localStorage.getItem('dbFromDate')==null && localStorage.getItem('dbToDate')==null){
    this.customers.setFromDate(this.initToday);
    this.customers.setToDate(this.initToday);
    this.fromDate = this.initToday;
    this.toDate = this.initToday;
  }

  var checkfromDate:any = localStorage.getItem('dbFromDate');
  let getfromDate = checkfromDate.split('/');
  this.setfromDate = { day:+getfromDate[0],month:+getfromDate[1],year:+getfromDate[2]};

  var checktoDate:any = localStorage.getItem('dbToDate');
  let gettoDate =checktoDate.split('/');
  this.settoDate = { day:+gettoDate[0],month:+gettoDate[1],year:+gettoDate[2]};
  this.getMinDate = { day:+gettoDate[0],month:+gettoDate[1],year:+gettoDate[2]};

  this.trackerFilter.patchValue({
    fromDate: this.setfromDate,
    toDate: this.settoDate
   });

   
  this.organizationIds.push(Number(this.authService.getOrgID()));
  console.log('---organizationIds---', this.organizationIds);

   }

  ngOnInit(): void {
  }

  onfromDate(event:any) {
    let year = event.year;
    let month = event.month <= 9 ? '0' + event.month : event.month;;
    let day = event.day <= 9 ? '0' + event.day : event.day;
    let finalDate = day + "/" + month + "/" + year;
    this.fromDate = finalDate;
    this.getMinDate = { day:+day,month:+month,year:+year};
   }
   ontoDate(event:any) {
    let year = event.year;
    let month = event.month <= 9 ? '0' + event.month : event.month;;
    let day = event.day <= 9 ? '0' + event.day : event.day;;
    let finalDate = day + "/" + month + "/" + year;
    this.toDate = finalDate;
   }
   onSubmitFilter(trackerFilter:FormGroup){
     let inputFromDate:any = $("#inputFromDate").val();
     //let getInputFromDate:any = inputFromDate.split('-');
     let finalInputFromDate = inputFromDate;

     let inputToDate:any = $("#inputToDate").val();
     //let getInputToDate:any = inputToDate.split('-');
     let finalInputToDate = inputToDate;

     let organizationIds: any = [];
     organizationIds.push(this.custId);

    if(this.fromDate==null){
        this.fromDate = finalInputFromDate;
    }
    if(this.toDate==null){
      this.toDate = finalInputToDate;
    }
    
    console.log("from dates:{}",this.fromDate);
    console.log("to dates:{}",this.toDate);
    // if (this.trackerFilter.valid) {
      this.customers.setFromDate(this.fromDate);
      this.customers.setToDate(this.toDate);
      
      this.customers.getCandidateEmployentReport({
        fromDate: this.fromDate,
        toDate: this.toDate,
        organizationIds: this.organizationIds,
      }).subscribe(
        (data: any) => {
          const blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
          const link = document.createElement('a');
          link.href = window.URL.createObjectURL(blob);
          link.download = 'customer_employment_Report.xlsx';
          link.click();
        },
        error => {
          console.error('Error downloading Excel file: ', error);
        }
      );
    // }else{
    //   Swal.fire({
    //     title: 'Please select the valid dates.',
    //     icon: 'warning'
    //   });
    // }
   }

  filterToday(){
    this.customers.setFromDate(this.initToday);
    this.customers.setToDate(this.initToday);
    this.fromDate = this.initToday;
    this.toDate = this.initToday;
    
    this.trackerFilter.patchValue({
      fromDate: this.fromDate,
      toDate: this.toDate,
    });
    this.onSubmitFilter(this.trackerFilter);
    
  }
  filterLastMonth() {
    let date = new Date();
    date.setMonth(date.getMonth() - 1);
    let firstDayOfMonth = new Date(date.getFullYear(), date.getMonth(), 2);
    let lastDayOfMonth = new Date(date.getFullYear(), date.getMonth() + 1, 1);
    let fromDateString = firstDayOfMonth.toISOString().split('T')[0];
    let toDateString = lastDayOfMonth.toISOString().split('T')[0];
  
    let getInputFromDate: any = fromDateString.split('-');
    let finalInputFromDate =
      getInputFromDate[2] +
      '/' +
      getInputFromDate[1] +
      '/' +
      getInputFromDate[0];
  
      let getInputToDate: any = toDateString.split('-');
      let finalInputToDate =
        getInputToDate[2] +
        '/' +
        getInputToDate[1] +
        '/' +
        getInputToDate[0];

        this.fromDate = finalInputFromDate;
    this.toDate = finalInputToDate;

    this.customers.setFromDate(finalInputFromDate);
    this.customers.setToDate(finalInputToDate);
    
    
    
    this.trackerFilter.patchValue({
      fromDate: this.fromDate,
      toDate: this.toDate,
      organizationIds: this.organizationIds,
    });
  
    this.onSubmitFilter(this.trackerFilter);
  }
  
  filterMonthToDate() {
    let currentDate = new Date();
    let firstDayOfMonth = new Date(currentDate.getFullYear(), currentDate.getMonth(), 2);
    let fromDateString = firstDayOfMonth.toISOString().split('T')[0];
    let toDateString = currentDate.toISOString().split('T')[0];
  
    let getInputFromDate: any = fromDateString.split('-');
    let finalInputFromDate =
      getInputFromDate[2] +
      '/' +
      getInputFromDate[1] +
      '/' +
      getInputFromDate[0];

      this.fromDate = finalInputFromDate;
    this.toDate = this.initToday;
  
      this.customers.setFromDate(finalInputFromDate);
      this.customers.setToDate(this.initToday);
      
    
    this.trackerFilter.patchValue({
      fromDate: this.fromDate,
      toDate: this.toDate,
      organizationIds: this.organizationIds,
    });
  
    this.onSubmitFilter(this.trackerFilter);
  }
}


