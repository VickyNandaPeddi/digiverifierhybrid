import { Component, NgZone, OnInit } from '@angular/core';
import { LoaderService } from 'src/app/services/loader.service';
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import am4themes_animated from "@amcharts/amcharts4/themes/animated";
import { NgbCalendar, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { CustomerService } from 'src/app/services/customer.service';
import { SuperadminDashboardService } from 'src/app/services/superadmin-dashboard.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import Swal from 'sweetalert2';
am4core.useTheme(am4themes_animated);
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  pageTitle = 'Dashboard';
  private chart: any=am4charts.XYChart;
  fromDate:any = localStorage.getItem('dbFromDate');
  toDate:any = localStorage.getItem('dbToDate');
  getToday: NgbDate;
  getMinDate: any;
  setfromDate:any;
  settoDate:any;
  initToday:any;
  getCustID:any=[];
  getCust: any;
  selectedUanCount: any;
  filterData: any
  PendingDetailsData:any=[];
  selectedActivitiesData:any=[];
  selectedCustId: number=0;
  selectedCust: any;
  SAduration: number=0;
  selectedUanFilter: any;
  filterForm_valid:boolean=false;
  dashboardFilter = new FormGroup({
    fromDate: new FormControl('', Validators.required),
    toDate: new FormControl('', Validators.required)
  });
  constructor(public loaderService:LoaderService,public calendar: NgbCalendar,private customer: CustomerService,private customers: CustomerService,private zone: NgZone,private superadminDB: SuperadminDashboardService, private http: HttpClient) {
    this.selectedCust = "ALL INCLUDED";
    this.getToday = calendar.getToday(); 
    let inityear = this.getToday.year;
    let initmonth = this.getToday.month <= 9 ? '0' + this.getToday.month : this.getToday.month;;
    let initday = this.getToday.day <= 9 ? '0' + this.getToday.day : this.getToday.day;
    let initfinalDate = initday + "/" + initmonth + "/" + inityear;
    this.initToday = initfinalDate;
    if(localStorage.getItem('dbFromDate')==null && localStorage.getItem('dbToDate')==null){
      this.customer.setFromDate(this.initToday);
      this.customer.setToDate(this.initToday);
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
    this.dashboardFilter.patchValue({
      fromDate: this.setfromDate,
      toDate: this.settoDate
     });
    //Test
    this.customers.getCustomersBill().subscribe((data: any)=>{
      this.getCustID=data.data;
      //console.log(this.getCustID);
    }); 
     
  }
  onfromDate(event:any) {
    let year = event.year;
    let month = event.month <= 9 ? '0' + event.month : event.month;
    let day = event.day <= 9 ? '0' + event.day : event.day;
    let finalDate = day + "/" + month + "/" + year;
    this.fromDate = finalDate;
    this.getMinDate = { day:+day,month:+month,year:+year};
   }
   ontoDate(event:any) {
    let year = event.year;
    let month = event.month <= 9 ? '0' + event.month : event.month;
    let day = event.day <= 9 ? '0' + event.day : event.day;
    let finalDate = day + "/" + month + "/" + year;
    this.toDate = finalDate
   }
   onSubmitFilter(dashboardFilter:FormGroup){
    let inputFromDate:any = $("#inputFromDate").val();
    //let getInputFromDate:any = inputFromDate.split('-');
    let finalInputFromDate = inputFromDate;

    let inputToDate:any = $("#inputToDate").val();
    //let getInputToDate:any = inputToDate.split('-');
    let finalInputToDate = inputToDate;

   if(this.fromDate==null){
       this.fromDate = finalInputFromDate;
   }
   if(this.toDate==null){
     this.toDate = finalInputToDate;
   }
   if (this.dashboardFilter.valid) {
    this.customer.setFromDate(this.fromDate);
    this.customer.setToDate(this.toDate);
    window.location.reload();
   }else{
    Swal.fire({
      title: 'Please select the valid dates.',
      icon: 'warning'
    });
  }

   }

   getCustomerData(custId:any){
    this.selectedCustId = custId;
    this.loadChart();
    this.loadCharts();
    setTimeout(() =>{
      this.loaderService.hide();
    },1500);
  }

saDurationFilter(duration:any){
  this.SAduration = duration;
  this.loadChart();
  this.loadCharts();
  setTimeout(() =>{
    this.loaderService.hide();
  },1500);
}

  filterToday(){
    this.customer.setFromDate(this.initToday);
    this.customer.setToDate(this.initToday);
    window.location.reload();
  }
  
  filterLast7days(){
      var date = new Date();
      date.setDate(date.getDate() - 7);
      var dateString = date.toISOString().split('T')[0];
      let getInputFromDate:any = dateString.split('-');
      let finalInputFromDate = getInputFromDate[2] + "/" + getInputFromDate[1] + "/" + getInputFromDate[0];
      this.customer.setFromDate(finalInputFromDate);
      this.customer.setToDate(this.initToday);
      window.location.reload();
  }

  filterLast30days(){
    var date = new Date();
    date.setDate(date.getDate() - 30);
    var dateString = date.toISOString().split('T')[0];
    let getInputFromDate:any = dateString.split('-');
    let finalInputFromDate = getInputFromDate[2] + "/" + getInputFromDate[1] + "/" + getInputFromDate[0];
    this.customer.setFromDate(finalInputFromDate);
    this.customer.setToDate(this.initToday);
    window.location.reload();
}
  
filterByYear() {
  var date = new Date();
  date.setFullYear(date.getFullYear() - 1);  // subtract one year instead of 30 days
  var dateString = date.toISOString().split('T')[0];
  let getInputFromDate: any = dateString.split('-');
  let finalInputFromDate = getInputFromDate[2] + "/" + getInputFromDate[1] + "/" + getInputFromDate[0];
  this.customer.setFromDate(finalInputFromDate);
  this.customer.setToDate(this.initToday);
  window.location.reload();
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
  this.customer.setFromDate(finalInputFromDate);
  this.customer.setToDate(finalInputToDate);
  window.location.reload();
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

    this.customer.setFromDate(finalInputFromDate);
    this.customer.setToDate(this.initToday);
    window.location.reload();
}

  ngOnInit(): void {
    setTimeout(() =>{
      this.loaderService.hide();
    },1000);

    this.getMethod();
    const  fromDateSplit = this.fromDate.split('/');
    const  toDateSplit = this.toDate.split('/');
    //console.log(fromDateSplit, toDateSplit);
    const finalFromDate = fromDateSplit[2] + '-' + fromDateSplit[1] + '-' + fromDateSplit[0];
    const finalToDate = toDateSplit[2] + '-' + toDateSplit[1] + '-' + toDateSplit[0];
    //console.log(finalFromDate, finalToDate);
    this.getUanCount("", finalFromDate, finalToDate);
    
  }

  onCustomerSelected($event: any, selectedValue: any) {
    console.log(selectedValue)
    this.selectedCust = selectedValue
    const  fromDateSplit = this.fromDate.split('/');
    const  toDateSplit = this.toDate.split('/');
    //console.log(fromDateSplit, toDateSplit);
    const finalFromDate = fromDateSplit[2] + '-' + fromDateSplit[1] + '-' + fromDateSplit[0];
    const finalToDate = toDateSplit[2] + '-' + toDateSplit[1] + '-' + toDateSplit[0];
    //console.log(finalFromDate, finalToDate);
    //console.log("hgdhsdghsdfshdg", this.selectedCust);
    if (this.selectedCust === "ALL INCLUDED") {
      this.getUanCount("",finalFromDate, finalToDate);
    } else {
      this.getUanCount(this.selectedCust, finalFromDate, finalToDate);
    }
    this.UanloadCharts('success_count', 'fail_count');
  }

  onDownloadInvoiceClick() {
    const fromDateSplit = this.fromDate.split('/');
    const toDateSplit = this.toDate.split('/');
    
    const finalFromDate = `${fromDateSplit[2]}-${fromDateSplit[1]}-${fromDateSplit[0]}`;
    const finalToDate = `${toDateSplit[2]}-${toDateSplit[1]}-${toDateSplit[0]}`;
  
    this.getUanExcel(this.selectedCust, finalFromDate, finalToDate);    
  }

  public getMethod(){
    this.http.get(`${environment.epfoUrl}/epfo/get_uan_customers`).subscribe((data: any) => {
      this.getCust=data.customers;
      console.log(this.getCust);
    });
   }

   getUanExcel(client_name: any, date_range: any, end_date: any){
    const url = `${environment.epfoUrl}/epfo/generate_invoice?client=${client_name}&start_date=${date_range}&end_date=${end_date}`;
    this.http.get(url, { responseType: 'blob' }).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = client_name + "_" + "Invoice.xlsx";
      a.click();
    });
  }
  
  public getUanCount(client_name: any, date_range: any, end_date: any){
    let url: string;
    if(client_name) {
      url = `${environment.epfoUrl}/epfo/get_counts?client=${client_name}&start_date=${date_range}&end_date=${end_date}`;
    } else {
      url = `${environment.epfoUrl}/epfo/get_counts?&start_date=${date_range}&end_date=${end_date}`;
    }
    this.http.get(url).subscribe(data => {
      this.selectedUanCount=data;
      console.log(this.selectedUanCount);

      let client = this.selectedUanCount.client_name;
      console.log(client);
      let success_count = this.selectedUanCount.Success_count;
      console.log(success_count);
      let fail_count = this.selectedUanCount.fail_count;
      console.log(fail_count);
      this.UanloadCharts(success_count, fail_count);
    });
  }
UanloadCharts(success_count: any, fail_count: any){
  this.zone.runOutsideAngular(() => {
    let chart = am4core.create("chartUanDetails", am4charts.PieChart);
    chart.innerRadius = am4core.percent(50);
    chart.legend = new am4charts.Legend();
    chart.legend.maxHeight = 290;
    chart.legend.scrollable = true;
    chart.legend.itemContainers.template.paddingTop = 4;
    chart.legend.itemContainers.template.paddingBottom = 4;
    chart.legend.fontSize = 13;
    chart.legend.useDefaultMarker = true;
    let marker:any = chart.legend.markers.template.children.getIndex(0);
    marker.cornerRadius(12, 12, 12, 12);
    marker.strokeWidth = 3;
    marker.strokeOpacity = 1;
    marker.stroke = am4core.color("#000");
    
    chart.logo.disabled = true;
    chart.legend.position = "right";
    chart.padding(0, 0, 0, 0);
    chart.paddingRight = 0
    
    let data = [
      ({name: "Success", value: success_count}),
      ({name: "Failure", value: fail_count})
    ];      
      chart.data = data;
 
    // Add and configure Series
    let pieSeries = chart.series.push(new am4charts.PieSeries());

    pieSeries.slices.template.tooltipText = "{category}: {value}";
    pieSeries.labels.template.disabled = true;
    pieSeries.dataFields.value = "value";
    pieSeries.dataFields.category = "name";
    pieSeries.slices.template.stroke = am4core.color("#fff");
    pieSeries.slices.template.strokeWidth = 2;
    pieSeries.slices.template.strokeOpacity = 1;
  
    // This creates initial animation
    pieSeries.hiddenState.properties.opacity = 1;
    pieSeries.hiddenState.properties.endAngle = -90;
    pieSeries.hiddenState.properties.startAngle = -90;
    pieSeries.legendSettings.itemValueText = "[bold]{value}[/bold]";
    pieSeries.colors.list = [
      am4core.color("#08e702"),      
      am4core.color("#fd352c"),
      am4core.color("#9c27b0"),
      am4core.color("#021aee"),
      am4core.color("#00bd77"),
      am4core.color("#ff0052"),
    ];

  });
 
}

  loadChart(){
    this.zone.runOutsideAngular(() => {
      let chart = am4core.create("customerActivities", am4charts.PieChart);
      chart.innerRadius = am4core.percent(50);
      chart.legend = new am4charts.Legend();
      chart.legend.maxHeight = 290;
      chart.legend.scrollable = true;
      chart.legend.itemContainers.template.paddingTop = 4;
      chart.legend.itemContainers.template.paddingBottom = 4;
      chart.legend.fontSize = 13;
      chart.legend.useDefaultMarker = true;
      let marker:any = chart.legend.markers.template.children.getIndex(0);
      marker.cornerRadius(12, 12, 12, 12);
      marker.strokeWidth = 3;
      marker.strokeOpacity = 1;
      marker.stroke = am4core.color("#000");
      
      chart.logo.disabled = true;
      chart.legend.position = "right";
      chart.padding(0, 0, 0, 0);
      chart.paddingRight = 0;
      //console.log(this.selectedCustId);
      var fromDate:any = localStorage.getItem('dbFromDate');
      var toDate:any = localStorage.getItem('dbToDate');
      let filterData = {
        'fromDate': fromDate,
        'toDate': toDate,
        'organizationId': this.selectedCustId
      }

      this.superadminDB.getPendingDetails(filterData).subscribe((result: any)=>{
        this.PendingDetailsData=result.data.candidateStatusCountDto;
        //console.log(result);
        let data = [];
        for (let i = 0; i < this.PendingDetailsData.length; i++) {
          // let obj={};
          // obj=this.PendingDetailsData[i].statusName;
          data.push({name: this.PendingDetailsData[i].statusName, value: this.PendingDetailsData[i].count});
        }
        chart.data = data;
      });
      
      // Add and configure Series
      let pieSeries = chart.series.push(new am4charts.PieSeries());
      pieSeries.slices.template.tooltipText = "{category}: {value}";
      pieSeries.labels.template.disabled = true;
      pieSeries.dataFields.value = "value";
      pieSeries.dataFields.category = "name";
      pieSeries.slices.template.stroke = am4core.color("#fff");
      pieSeries.slices.template.strokeWidth = 2;
      pieSeries.slices.template.strokeOpacity = 1;

      // This creates initial animation
      pieSeries.hiddenState.properties.opacity = 1;
      pieSeries.hiddenState.properties.endAngle = -90;
      pieSeries.hiddenState.properties.startAngle = -90;
      pieSeries.legendSettings.itemValueText = "[bold]{value}[/bold]";
      pieSeries.colors.list = [
        am4core.color("#FF8E00"),
        am4core.color("#ffd400"),
        am4core.color("#fd352c"),
        am4core.color("#08e702"),
        am4core.color("#9c27b0"),
        am4core.color("#021aee"),
        am4core.color("#00bd77"),
        am4core.color("#ff0052"),
      ];

    });
   
}
loadCharts(){
  this.zone.runOutsideAngular(() => {
    let chart = am4core.create("selectedActivities", am4charts.PieChart);
    chart.innerRadius = am4core.percent(50);
    chart.legend = new am4charts.Legend();
    chart.legend.maxHeight = 290;
    chart.legend.scrollable = true;
    chart.legend.itemContainers.template.paddingTop = 4;
    chart.legend.itemContainers.template.paddingBottom = 4;
    chart.legend.fontSize = 13;
    chart.legend.useDefaultMarker = true;
    let marker:any = chart.legend.markers.template.children.getIndex(0);
    marker.cornerRadius(12, 12, 12, 12);
    marker.strokeWidth = 3;
    marker.strokeOpacity = 1;
    marker.stroke = am4core.color("#000");
    
    chart.logo.disabled = true;
    chart.legend.position = "right";
    chart.padding(0, 0, 0, 0);
    chart.paddingRight = 0;

    var fromDate:any = localStorage.getItem('dbFromDate');
    var toDate:any = localStorage.getItem('dbToDate');
    let filterData = {
      'fromDate': fromDate,
      'toDate': toDate,
      'organizationId': this.selectedCustId
    }
   
    this.superadminDB.getActivityDetails(filterData).subscribe((uploadinfo: any)=>{
      this.selectedActivitiesData=uploadinfo.data.candidateStatusCountDto;
      let data = [];
      for (let i = 0; i < this.selectedActivitiesData.length; i++) {
        // let obj={};
        // obj=this.selectedActivitiesData[i].statusName;
        data.push({name: this.selectedActivitiesData[i].statusName, value: this.selectedActivitiesData[i].count});
      }
      chart.data = data;
    });
    
    // Add and configure Series
    let pieSeries = chart.series.push(new am4charts.PieSeries());
  
    pieSeries.slices.template.tooltipText = "{category}: {value}";
    pieSeries.labels.template.disabled = true;
    pieSeries.dataFields.value = "value";
    pieSeries.dataFields.category = "name";
    pieSeries.slices.template.stroke = am4core.color("#fff");
    pieSeries.slices.template.strokeWidth = 2;
    pieSeries.slices.template.strokeOpacity = 1;

    // This creates initial animation
    pieSeries.hiddenState.properties.opacity = 1;
    pieSeries.hiddenState.properties.endAngle = -90;
    pieSeries.hiddenState.properties.startAngle = -90;
    pieSeries.legendSettings.itemValueText = "[bold]{value}[/bold]";
    pieSeries.colors.list = [
      am4core.color("#FF8E00"),
      am4core.color("#ffd400"),
      am4core.color("#fd352c"),
      am4core.color("#08e702"),
      am4core.color("#9c27b0"),
      am4core.color("#021aee"),
      am4core.color("#00bd77"),
      am4core.color("#ff0052"),
    ];

  });
 
}
ngOnDestroy() {
  this.zone.runOutsideAngular(() => {
    if (this.chart) {
      // this.chart.dispose();
    }
  });
}

}
