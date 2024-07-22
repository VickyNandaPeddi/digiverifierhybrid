import { Component, NgZone, AfterViewInit, OnDestroy, OnInit, Injectable } from '@angular/core';
import * as am4core from "@amcharts/amcharts4/core";
import { CustomerService } from '../../services/customer.service';
import * as am4charts from "@amcharts/amcharts4/charts";
import am4themes_animated from "@amcharts/amcharts4/themes/animated";
import { OrgadminDashboardService } from 'src/app/services/orgadmin-dashboard.service';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';
import * as  _ from 'lodash';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormGroup, FormControl, FormBuilder, Validators, AbstractControl, ValidatorFn } from '@angular/forms';

am4core.useTheme(am4themes_animated);

@Injectable({
  providedIn: 'root',
})
@Component({
  selector: 'app-report-delivery-details',
  templateUrl: './report-delivery-details.component.html',
  styleUrls: ['./report-delivery-details.component.scss']
})

export class ReportDeliveryDetailsComponent implements OnInit {
  private chart: am4charts.XYChart | undefined;
  getReportDeliveryStatCodes: any;
  getConventionalReportDeliveryStatCodes: any;
  searchText: string = '';
  CharReportDelivery: any = [];
  CharReportDeliveryData: any = [];
  CharConventionalReportDeliveryData: any = [];
  containerStat: boolean = false;
  stat_linkAdminApproval: boolean = false;
  stat_linkCandidateReport: boolean = false;
  finalreport: boolean = false;
  interimreport: boolean = false;
  disablePreOfferForConventional: boolean = false;
  startdownload: boolean = false;
  Action: boolean = false;
  startpredownload: boolean = false;
  isCBadmin: boolean = false;
  getCandidate: any = [];
  getServiceConfigCodes: any = [];
  currentPage = 1;
  pageSize: number = 10;
  currentPageIndex: number = 0;
  getConventionalStatCodes:any;
  updateCandidate = new FormGroup({
    applicantId: new FormControl(''),
    candidateName: new FormControl('', Validators.required),
    createdByUserFirstName: new FormControl('', Validators.required),
    candidateCode: new FormControl('', Validators.required),
    contactNumber:  new FormControl('', [Validators.minLength(10), Validators.maxLength(10), Validators.pattern('[6-9]\\d{9}')]),
    emailId: new FormControl('', [Validators.required,Validators.email])
  });

  emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
  PANTOUAN: boolean = false;
  CAPGSCOPE: boolean = false;

  emailValidator(control: AbstractControl): { [key: string]: any } | null {
    if (control.value) {
      const emails = control.value.split(',');
      const invalidEmails = emails.filter((email: any) => !this.emailPattern.test(email.trim()));
      console.log(invalidEmails.length)
      return invalidEmails.length === 0 ? null : { 'invalidEmails': true };
    }
    return null;
  }

  forwardReportForm = new FormGroup({
    emailIds: new FormControl('', [Validators.required, this.emailValidator.bind(this)])
  });
  tmp: any=[];

  constructor(private zone: NgZone, private orgadmin:OrgadminDashboardService,private customers:CustomerService,private modalService: NgbModal,
    private router: Router,public authService: AuthenticationService) {
    this.getReportDeliveryStatCodes = this.orgadmin.getReportDeliveryStatCode();
    this.getConventionalReportDeliveryStatCodes = this.orgadmin.getConventionalReportDeliveryStatCode();
    this.getConventionalStatCodes = this.orgadmin.getConventionalStatusCode();
    console.warn("getConventionalReportDeliveryStatCodes>>",this.getConventionalReportDeliveryStatCodes)
    if (this.getReportDeliveryStatCodes) {
      var userId: any = this.authService.getuserId();
      var fromDate: any = localStorage.getItem('dbFromDate');
      var toDate: any = localStorage.getItem('dbToDate');
      let filterData = {
       // 'userId': userId,
        'fromDate': fromDate,
        'toDate': toDate,
        'status': this.getReportDeliveryStatCodes,
        //adding below parameter to get the backend pagination list
        'pageNumber':this.currentPageIndex
      }
      this.orgadmin.getChartDetails(filterData).subscribe((data: any)=>{
        this.CharReportDelivery=data.data.candidateDtoList;
        // this.CharReportDelivery=data.data.candidateDtoList.reverse();

        for(let i=0; i<this.CharReportDelivery.length; i++) {
          // let index = _.findIndex(this.CharReportDelivery[i].contentDTOList, {contentSubCategory: 'PRE_APPROVAL'});
          // this.CharReportDelivery[i].pre_approval_content_id = (index != -1) ? this.CharReportDelivery[i].contentDTOList[index].contentId : -1;

          let final = this.CharReportDelivery[i].contentDTOList;
          let interim = this.CharReportDelivery[i].candidateStatusName;

          for (let i=0; i<final.length; i++){
            if(final[i].contentSubCategory=="FINAL" && filterData.status == 'FINALREPORT'){
              this.finalreport = true
            }
          }

          if(interim == 'Interim Report' && filterData.status == 'INTERIMREPORT') {
            this.interimreport = true;
          }

        }

        // this.CharReportDelivery.sort((a: any, b: any) => {
        //   const dateA = new Date(a.lastUploadedOn);
        //   const dateB = new Date(b.lastUploadedOn);

        //   return dateB.getTime() - dateA.getTime();
        // });

        console.warn("fiterData:::",this.CharReportDelivery);
        console.log("After : ", this.CharReportDelivery)
        //console.log(data);
        const startIndex = this.currentPageIndex * this.pageSize;
        const endIndex = startIndex + this.pageSize;
        return this.CharReportDelivery.slice(startIndex, endIndex);
      });

      this.orgadmin
        .getServiceConfigForOrg(authService.getOrgID())
        .subscribe((result: any) => {
          this.getServiceConfigCodes = result.data;
          console.log("ORG SERVICES::{}", this.getServiceConfigCodes);
        });
    }

    // Conventional

    if (this.getConventionalReportDeliveryStatCodes) {
      var userId: any = this.authService.getuserId();
      var fromDate: any = localStorage.getItem('dbFromDate');
      var toDate: any = localStorage.getItem('dbToDate');
      let filterData = {
        // 'userId': userId,
        'fromDate': fromDate,
        'toDate': toDate,
        'status': this.getConventionalReportDeliveryStatCodes,
        //adding below parameter to get the backend pagination list
        'pageNumber': this.currentPageIndex
      }
      this.orgadmin.getConventionalChartDetails(filterData).subscribe((data: any) => {
        this.CharReportDelivery = data.data.candidateDtoList;
        this.disablePreOfferForConventional = true;
        console.warn("disablePreOfferForConventional:",this.disablePreOfferForConventional)
        // this.CharReportDelivery=data.data.candidateDtoList.reverse();

        for (let i = 0; i < this.CharReportDelivery.length; i++) {
          // let index = _.findIndex(this.CharReportDelivery[i].contentDTOList, {contentSubCategory: 'PRE_APPROVAL'});
          // this.CharReportDelivery[i].pre_approval_content_id = (index != -1) ? this.CharReportDelivery[i].contentDTOList[index].contentId : -1;

          let final = this.CharReportDelivery[i].contentDTOList;
          let interim = this.CharReportDelivery[i].candidateStatusName;

          for (let i = 0; i < final.length; i++) {
            if (final[i].contentSubCategory == "FINAL" && filterData.status == 'FINALREPORT') {
              this.finalreport = true
            }
          }

          if (interim == 'Interim Report' && filterData.status == 'INTERIMREPORT') {
            this.interimreport = true;
          }

        }

        // this.CharReportDelivery.sort((a: any, b: any) => {
        //   const dateA = new Date(a.lastUploadedOn);
        //   const dateB = new Date(b.lastUploadedOn);

        //   return dateB.getTime() - dateA.getTime();
        // });

        console.warn("fiterData:::", this.CharReportDelivery);
        console.log("After : ", this.CharReportDelivery)
        //console.log(data);
        const startIndex = this.currentPageIndex * this.pageSize;
        const endIndex = startIndex + this.pageSize;
        return this.CharReportDelivery.slice(startIndex, endIndex);
      });
    }
  }

  ngAfterViewInit() {
    setTimeout(() =>{
      this.ngOnDestroy();
      this.loadCharts();
      this.ConventionalLoadCharts();
    },50);
  }

  loadCharts(){
    this.zone.runOutsideAngular(() => {
      let chart = am4core.create("chartReportDelivery", am4charts.PieChart);
      chart.innerRadius = am4core.percent(50);
      chart.legend = new am4charts.Legend();

      chart.legend.itemContainers.template.paddingTop = 4;
      chart.legend.itemContainers.template.paddingBottom = 4;
      chart.legend.fontSize = 13;
      chart.legend.useDefaultMarker = true;
      let marker:any = chart.legend.markers.template.children.getIndex(0);
      marker.cornerRadius(12, 12, 12, 12);
      marker.strokeWidth = 3;
      marker.strokeOpacity = 1;
      marker.stroke = am4core.color("#000");

      chart.legend.maxHeight = 210;
      chart.legend.scrollable = true;
      chart.legend.position = "right";
      chart.logo.disabled = true;
      chart.padding(10, 0, 0, 0);
      chart.radius = am4core.percent(95);
      chart.paddingRight = 0;

      var userId:any = this.authService.getuserId();
      var fromDate:any = localStorage.getItem('dbFromDate');
      var toDate:any = localStorage.getItem('dbToDate');
      let filterData = {
        'userId': userId,
        'fromDate': fromDate,
        'toDate': toDate
      }

      this.orgadmin
      .getServiceConfigForOrg(this.authService.getOrgID())
      .subscribe((result: any) => {
        if(result.data.includes('PANTOUAN'))
          this.PANTOUAN = true;

          if (result.data.includes('EPFO') && !result.data.includes('ITR') && !result.data.includes('DIGILOCKER'))
            this.CAPGSCOPE = true;


        this.orgadmin.getReportDeliveryDetails(filterData).subscribe((uploadinfo: any)=>{
          this.CharReportDeliveryData=uploadinfo.data.candidateStatusCountDto;
          //console.log(this.CharReportDeliveryData);
          let data = [];
          for (let i = 0; i < this.CharReportDeliveryData.length; i++) {
            // let obj={};
            // obj=this.CharReportDeliveryData[i].statusName;
            if(this.PANTOUAN) {
              if(this.CharReportDeliveryData[i].statusName != 'Interim Report' && this.CharReportDeliveryData[i].statusName != 'Final Report' && this.CharReportDeliveryData[i].statusName != 'Process Declined') {
                if(this.CharReportDeliveryData[i].statusName == 'QC Pending')
                  this.CharReportDeliveryData[i].statusName = 'Reports'
                  data.push({
                    name: this.CharReportDeliveryData[i].statusName,
                    value: this.CharReportDeliveryData[i].count,
                    statcode: this.CharReportDeliveryData[i].statusCode
                  });
                }
              } else if (this.PANTOUAN === false && this.CAPGSCOPE === true) {
                if (this.CharReportDeliveryData[i].statusName !== 'Process Declined') {
                  data.push({
                    name: this.CharReportDeliveryData[i].statusName,
                    value: this.CharReportDeliveryData[i].count,
                    statcode: this.CharReportDeliveryData[i].statusCode
                  });

              }
            } else {
              data.push({name: this.CharReportDeliveryData[i].statusName, value: this.CharReportDeliveryData[i].count, statcode: this.CharReportDeliveryData[i].statusCode });
            }
          }
          chart.data = data;
        });
      });



// Add and configure Series
let pieSeries = chart.series.push(new am4charts.PieSeries());
pieSeries.slices.template.stroke = am4core.color("#fff0");
pieSeries.slices.template.strokeWidth = 0;
pieSeries.slices.template.strokeOpacity = 0;

pieSeries.slices.template.tooltipText = "{category}: {value}";
pieSeries.labels.template.disabled = true;
pieSeries.dataFields.value = "value";
pieSeries.dataFields.category = "name";0

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
];

// var rgm = new am4core.RadialGradientModifier();
// rgm.brightnesses.push(-0.2, -0.2, -0.1, 0, - 0.1);
// pieSeries.slices.template.fillModifier = rgm;
// pieSeries.slices.template.strokeModifier = rgm;
// pieSeries.slices.template.strokeWidth = 0;


//pieSeries.slices.template.events.on("hit", myFunction, this);
pieSeries.slices.template.events.on('hit', (e) => {
  const getchartData = e.target._dataItem as any;
  const statuscodes = getchartData._dataContext.statcode;
  //console.log(statuscodes);
  this.orgadmin.setReportDeliveryStatCode(statuscodes);
  window.location.reload();
});
chart.legend.itemContainers.template.events.on("hit", (ev) => {
  const getchartData = ev.target._dataItem as any;
  const statuscodes = getchartData._label._dataItem._dataContext.statcode;
  this.orgadmin.setReportDeliveryStatCode(statuscodes);
  window.location.reload();
});
pieSeries.slices.template.cursorOverStyle = am4core.MouseCursorStyle.pointer;
    });

}

ConventionalLoadCharts() {
  this.zone.runOutsideAngular(() => {
    let chart = am4core.create("ConventionalChartReportDelivery", am4charts.PieChart);
    chart.innerRadius = am4core.percent(50);
    chart.legend = new am4charts.Legend();

    chart.legend.itemContainers.template.paddingTop = 4;
    chart.legend.itemContainers.template.paddingBottom = 4;
    chart.legend.fontSize = 13;
    chart.legend.useDefaultMarker = true;
    let marker: any = chart.legend.markers.template.children.getIndex(0);
    marker.cornerRadius(12, 12, 12, 12);
    marker.strokeWidth = 3;
    marker.strokeOpacity = 1;
    marker.stroke = am4core.color("#000");

    chart.legend.maxHeight = 210;
    chart.legend.scrollable = true;
    chart.legend.position = "right";
    chart.logo.disabled = true;
    chart.padding(10, 0, 0, 0);
    chart.radius = am4core.percent(95);
    chart.paddingRight = 0;

    var userId: any = this.authService.getuserId();
    var fromDate: any = localStorage.getItem('dbFromDate');
    var toDate: any = localStorage.getItem('dbToDate');
    let filterData = {
      'userId': userId,
      'fromDate': fromDate,
      'toDate': toDate
    }

    // this.orgadmin.getReportDeliveryDetails(filterData).subscribe((uploadinfo: any)=>{
    //   this.CharReportDeliveryData=uploadinfo.data.candidateStatusCountDto;
    //   console.log(this.CharReportDeliveryData);
    //   let data = [];
    //   for (let i = 0; i < this.CharReportDeliveryData.length; i++) {
    //     // let obj={};
    //     // obj=this.CharReportDeliveryData[i].statusName;
    //     data.push({name: this.CharReportDeliveryData[i].statusName, value: this.CharReportDeliveryData[i].count, statcode: this.CharReportDeliveryData[i].statusCode });
    //   }
    //   chart.data = data;
    // });

    this.orgadmin.conventionalGetReportDeliveryDetails(filterData).subscribe((uploadinfo: any) => {
      this.CharConventionalReportDeliveryData = uploadinfo.data.candidateStatusCountDto;
      console.log(this.CharConventionalReportDeliveryData);
      let data = [];
      for (let i = 0; i < this.CharConventionalReportDeliveryData.length; i++) {
        // let obj={};
        // obj=this.CharReportDeliveryData[i].statusName;
        data.push({ name: this.CharConventionalReportDeliveryData[i].statusName, value: this.CharConventionalReportDeliveryData[i].count, statcode: this.CharConventionalReportDeliveryData[i].statusCode });
      }
      chart.data = data;
    });

    // Add and configure Series
    let pieSeries = chart.series.push(new am4charts.PieSeries());
    pieSeries.slices.template.stroke = am4core.color("#fff0");
    pieSeries.slices.template.strokeWidth = 0;
    pieSeries.slices.template.strokeOpacity = 0;

    pieSeries.slices.template.tooltipText = "{category}: {value}";
    pieSeries.labels.template.disabled = true;
    pieSeries.dataFields.value = "value";
    pieSeries.dataFields.category = "name"; 0

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
    ];

    // var rgm = new am4core.RadialGradientModifier();
    // rgm.brightnesses.push(-0.2, -0.2, -0.1, 0, - 0.1);
    // pieSeries.slices.template.fillModifier = rgm;
    // pieSeries.slices.template.strokeModifier = rgm;
    // pieSeries.slices.template.strokeWidth = 0;

    //pieSeries.slices.template.events.on("hit", myFunction, this);
    pieSeries.slices.template.events.on('hit', (e) => {
      const getchartData = e.target._dataItem as any;
      console.warn("getchartData>>",getchartData)
      const statuscodes = getchartData._dataContext.statcode;
      console.log("dsgksghj", statuscodes);
      // this.orgadmin.setReportDeliveryStatCode(statuscodes);
      this.orgadmin.setConventionalReportDeliveryStatCode(statuscodes)
      window.location.reload();
    });
    chart.legend.itemContainers.template.events.on("hit", (ev) => {
      const getchartData = ev.target._dataItem as any;
      const statuscodes = getchartData._label._dataItem._dataContext.statcode;
      // this.orgadmin.setReportDeliveryStatCode(statuscodes);
      this.orgadmin.setConventionalReportDeliveryStatCode(statuscodes)
      window.location.reload();
    });
    pieSeries.slices.template.cursorOverStyle = am4core.MouseCursorStyle.pointer;
  });

}




ngOnDestroy() {
  this.zone.runOutsideAngular(() => {
    if (this.chart) {
      this.chart.dispose();
    }
  });
}

childCheck(e:any, applicantId: any) {
  var sid = e.target.id;
  // let obj = {candidateCode: sid, applicantId: applicantId};
  if (e.target.checked) {
    this.tmp.push(sid);
  } else {
    this.tmp.splice($.inArray(sid, this.tmp),1);
  }

  console.log(this.tmp)
}

selectAll(e:any){
  console.log(e.target.checked)
  if (e.target.checked) {
    $(".childCheck").prop('checked', true);
    var  cboxRolesinput = $('.childCheck');
    var arrNumber:any = [];
    $.each(cboxRolesinput,function(idx,elem) {
      arrNumber.push($(this).val());
    });

    this.tmp = arrNumber;
    console.log(this.tmp)
  } else {
    $(".childCheck").prop('checked', false);
    this.tmp = [];
  }

}

formSendInvitation = new FormGroup({
  candidateReferenceNo: new FormControl('', Validators.required),
  statuscode: new FormControl('', Validators.required)
});

reInvite(){
  this.reInvitePatchValues();
  return this.orgadmin.saveInvitationSent(this.formSendInvitation.value).subscribe((result:any)=>{
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

reInvitePatchValues() {
  this.formSendInvitation.patchValue({
    candidateReferenceNo: this.tmp,
    statuscode: "REINVITE",
  });
}

performSearch(){
  console.log('Search Text:', this.searchText);
  const username = this.authService.getuserName();
  const userID = this.authService.getuserId();
  const orgId = this.authService.getOrgID();
  const role = this.authService.getRoles();
  const userRoleName = this.authService.getroleName();
  console.log("ROLE:::",role);
  console.log("roleName:::",userRoleName);
  console.warn("username:::",username);
  console.warn("ORG_ID::",orgId);
  const searchData = {
    userSearchInput: this.searchText,
    agentName: username,
    organisationId:orgId,
    roleName:userRoleName,
    userId:userID

  };
  console.log('Search Data:', searchData);

  if (this.getConventionalStatCodes || this.getConventionalReportDeliveryStatCodes) {
    this.orgadmin.conventionalDashboardSearchData(searchData).subscribe((data: any) => {
      this.CharReportDelivery = data.data.candidateDtoList;
      console.warn("data", data);
    })
  }
  else{
        this.orgadmin.getAllSearchData(searchData).subscribe((data: any) => {
          this.CharReportDelivery = data.data.candidateDtoList;
          console.warn("data", data);
          console.warn("chartreport::>>>", this.CharReportDelivery);
        })

  }

}

conventionalSearch(){
  const username = this.authService.getuserName();
  const userID = this.authService.getuserId();
  const orgId = this.authService.getOrgID();
  const role = this.authService.getRoles();
  const userRoleName = this.authService.getroleName();
  console.log("ROLE:::",role);
  console.log("roleName:::",userRoleName);
  console.warn("username:::",username);
  console.warn("ORG_ID::",orgId);
  const searchData = {
    userSearchInput: this.searchText,
    agentName: username,
    organisationId:orgId,
    roleName:userRoleName,
    userId:userID

  };
  console.log('Search Data:', searchData);

  // if (this.getConventionalStatCodes || this.getConventionalReportDeliveryStatCodes) {
    this.orgadmin.conventionalDashboardSearchData(searchData).subscribe((data: any) => {
      this.CharReportDelivery = data.data.candidateDtoList;
      console.warn("data", data);
    })
  // }

}

  ngOnInit(): void {
    const isCBadminVal = this.authService.getRoles();
    this.orgadmin
    .getServiceConfigForOrg(this.authService.getOrgID())
    .subscribe((result: any) => {
      if(result.data.includes('PANTOUAN'))
        this.PANTOUAN = true;

      if(this.getReportDeliveryStatCodes){
        if(this.getReportDeliveryStatCodes === "PENDINGAPPROVAL"){
          $(".dbtabheading").text("Pre offer Report");
          if(this.PANTOUAN)
            $(".dbtabheading").text("Reports");
          this.stat_linkAdminApproval = true;
          this.stat_linkCandidateReport = false;
          this.Action = true;
          this.finalreport = false;
        }else if(this.getReportDeliveryStatCodes === "INTERIMREPORT"){
          $(".dbtabheading").text("Interim Report");
          this.stat_linkAdminApproval = false;
          this.stat_linkCandidateReport = true;
          this.Action = true;
          this.finalreport = false;
        }else if(this.getReportDeliveryStatCodes === "FINALREPORT"){
          $(".dbtabheading").text("Final Report");
          this.stat_linkAdminApproval = false;
          this.stat_linkCandidateReport = false;
          this.Action = false;
          this.finalreport = true;
        }else if(this.getReportDeliveryStatCodes === "PROCESSDECLINED"){
          $(".dbtabheading").text("Process Declined");
          this.stat_linkAdminApproval = false;
          this.stat_linkCandidateReport = false;
          this.Action = false;
          this.finalreport = false;
        } else if (this.getConventionalReportDeliveryStatCodes === "CONVENTIONALPENDINGAPPROVAL") {
          $(".dbtabheading").text("Conventional CWF Completed");
          this.stat_linkAdminApproval = true;
          this.stat_linkCandidateReport = false;
          this.Action = true;
          this.finalreport = false;
        }
        this.containerStat = true;
        //isCBadmin required for drilldown dashboard at Superadmin
        if(isCBadminVal == '"ROLE_CBADMIN"'){
          this.isCBadmin = true;
          this.stat_linkAdminApproval = false;
        }else{
          this.isCBadmin = false;
        }

      }

      if (this.getConventionalReportDeliveryStatCodes) {
        if (this.getConventionalReportDeliveryStatCodes === "CONVENTIONALPENDINGAPPROVAL") {
          $(".dbtabheading").text("Conventional CWF Completed");
          this.stat_linkAdminApproval = true;
          this.stat_linkCandidateReport = false;
          this.Action = true;
          this.finalreport = false;
          this.interimreport = false;
          this.disablePreOfferForConventional = true;
        } else if (this.getConventionalReportDeliveryStatCodes === "CONVENTIONALINTERIMREPORT") {
          $(".dbtabheading").text("Conventional Interim Report");
          this.stat_linkAdminApproval = false;
          this.stat_linkCandidateReport = true;
          this.Action = true;
          this.finalreport = false;
          this.interimreport = true
        } else if (this.getConventionalReportDeliveryStatCodes === "CONVENTIONALFINALREPORT") {
          $(".dbtabheading").text("Conventional Final Report");
          this.stat_linkAdminApproval = false;
          this.stat_linkCandidateReport = false;
          this.Action = false;
          this.finalreport = true;
        } else if (this.getConventionalReportDeliveryStatCodes === "CONVENTIONALPROCESSDECLINED") {
          $(".dbtabheading").text("Conventional Process Declined");
          this.stat_linkAdminApproval = false;
          this.stat_linkCandidateReport = false;
          this.Action = false;
          this.finalreport = false;
        }else if (this.getConventionalReportDeliveryStatCodes === "CONVENTIONALCANDIDATEAPPROVE") {
          $(".dbtabheading").text("Conventional Candidate Approval");
          this.stat_linkAdminApproval = true;
          this.stat_linkCandidateReport = false;
          this.Action = true;
          this.finalreport = false;
          this.interimreport = false;
          this.disablePreOfferForConventional = true;
        }
        this.containerStat = true;
        //isCBadmin required for drilldown dashboard at Superadmin
        if (isCBadminVal == '"ROLE_CBADMIN"') {
          this.isCBadmin = true;
          this.stat_linkAdminApproval = false;
        } else {
          this.isCBadmin = false;
        }
      }



    });
  }

  linkAdminApproval(candidateCode:any){
    const billUrl = 'admin/cReportApproval/'+[candidateCode];
    this.router.navigate([billUrl]);
  }
  linkCandidateReport(candidateCode:any){
    const billUrl = 'admin/cFinalReport/'+[candidateCode];
    this.router.navigate([billUrl]);
  }

  downloadPreApprovalReport(candidate: any) {
    console.log(candidate);
    for(let i=0; i<this.CharReportDelivery.length; i++) {
      let index = _.findIndex(this.CharReportDelivery[i].contentDTOList, {contentSubCategory: 'PRE_APPROVAL'});
      this.CharReportDelivery[i].pre_approval_content_id = (index != -1) ? this.CharReportDelivery[i].contentDTOList[index].contentId : -1;
      // console.log('Lavanyafinal',index)
      this.startpredownload=true

    }

    if(this.startpredownload==true){
      if(candidate.pre_approval_content_id != -1) {
        console.log(candidate,"-----if--------");
        this.orgadmin.getSignedURLForContent(candidate.pre_approval_content_id).subscribe((url: any)=>{
          // window.location.href = url;
          window.open(url.data, '_blank');
        });
      }
    }

    //this block added if pre offer generation failed and we want to regenerate it from eye button
    if(candidate.pre_approval_content_id == -1) {

      this.startpredownload==true;
      this.orgadmin.getPreOfferRegenerationCall(candidate.candidateCode).subscribe((url: any)=>{
        if(url.outcome){

          let pre_approval_content_id= url.data;
          console.log(pre_approval_content_id,"Content Id for ::");
          this.orgadmin.getSignedURLForContent(pre_approval_content_id).subscribe((url: any)=>{

            window.open(url.data, '_blank');
          });
        }

      });
    }
  }

  downloadFinalReport(candidate: any, reportType: any) {
    console.log(reportType,"-----if--------");
    if(candidate.candidateStatusName == 'Final Report') {
      for(let i=0; i<this.CharReportDelivery.length; i++) {
        let index = _.findIndex(this.CharReportDelivery[i].contentDTOList, {contentSubCategory: 'FINAL'});
        this.CharReportDelivery[i].pre_approval_content_id = (index != -1) ? this.CharReportDelivery[i].contentDTOList[index].contentId : -1;
        console.log('Lavanyafinal',index)
        this.startdownload=true

      }
    } else if(candidate.candidateStatusName == 'Interim Report') {
      for(let i=0; i<this.CharReportDelivery.length; i++) {
        let index = _.findIndex(this.CharReportDelivery[i].contentDTOList, {contentSubCategory: 'INTERIM'});
        // let index = this.CharReportDelivery[i].contentDTOList.findIndex((item: any)=> item.contentSubCategory.includes('INTERIM'));
        this.CharReportDelivery[i].pre_approval_content_id = (index != -1) ? this.CharReportDelivery[i].contentDTOList[index].contentId : -1;
        console.log('interim',index)
        this.startdownload=true

      }
    }else if (candidate.candidateStatusName == 'Conventional Interim Report') {
      for (let i = 0; i < this.CharReportDelivery.length; i++) {
        let index = _.findIndex(this.CharReportDelivery[i].contentDTOList, { contentSubCategory: 'CONVENTIONALINTERIM' });
        // let index = this.CharReportDelivery[i].contentDTOList.findIndex((item: any)=> item.contentSubCategory.includes('INTERIM'));
        this.CharReportDelivery[i].pre_approval_content_id = (index != -1) ? this.CharReportDelivery[i].contentDTOList[index].contentId : -1;
        console.log('CONVENTIONALINTERIM', index)
        this.startdownload = true

      }
    }

    if(this.startdownload==true){
      if(candidate.pre_approval_content_id != -1) {
        console.log(candidate,"-----if--------");
        this.orgadmin.getSignedURLForContent(candidate.pre_approval_content_id).subscribe((url: any)=>{
          // window.location.href = url;
          window.open(url.data, '_blank');
        });
      }
    }
  }

  downloadInterimReport(candidate: any,reportStatus: any) {
    // let interimContentDto = candidate.contentDTOList.find((dto: any) => dto.path.includes('INTERIM.pdf'));

    if(candidate.candidateCode) {
      this.orgadmin.getPreSignedUrlByCandidateCode(candidate.candidateCode,reportStatus).subscribe((url: any)=>{
        window.open(url.data, '_blank');
      });
    }
  }
  CharReportDeliverypagination(): any[] {
    const filteredItems = this.CharReportDelivery.filter((item: any) => this.searchFilter(item));
    const startIndex = this.currentPageIndex * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    return filteredItems.slice(startIndex, endIndex);
  }

  onPageChange(page: number): void {
    this.currentPageIndex = page;

    if (this.getReportDeliveryStatCodes) {
        var userId: any = localStorage.getItem('userId');
        var fromDate: any = localStorage.getItem('dbFromDate');
        var toDate: any = localStorage.getItem('dbToDate');

        let filterData = {
            'fromDate': fromDate,
            'toDate': toDate,
            'status': this.getReportDeliveryStatCodes,
            'pageNumber': this.currentPageIndex
        };

        this.orgadmin.getChartDetails(filterData).subscribe((data: any) => {
            this.CharReportDelivery = data.data.candidateDtoList;
            console.log("After : ", this.CharReportDelivery);
        });
    }

    if(this.getConventionalReportDeliveryStatCodes){
      var userId: any = localStorage.getItem('userId');
      var fromDate: any = localStorage.getItem('dbFromDate');
      var toDate: any = localStorage.getItem('dbToDate');

      let filterData = {
        'fromDate': fromDate,
        'toDate': toDate,
        'status': this.getConventionalReportDeliveryStatCodes,
        'pageNumber': this.currentPageIndex
      };

      this.orgadmin.getConventionalChartDetails(filterData).subscribe((data: any) => {
        this.CharReportDelivery = data.data.candidateDtoList;
        console.log("After : ", this.CharReportDelivery);
      });
    }
  }

  // Modify your goToNextPage and goToPrevPage methods like this
  goToNextPage(): void {
    if (this.currentPageIndex < this.totalPages - 1) {
        this.onPageChange(this.currentPageIndex + 1);
    }
  }

  goToPrevPage(): void {
    if (this.currentPageIndex > 0) {
        this.onPageChange(this.currentPageIndex - 1);
    }
  }

    getDisplayedPages(): number[] {
      const displayedPages: number[] = [];
      const startPage = Math.max(0, this.currentPageIndex - 2);
      const endPage = Math.min(this.totalPages - 1, startPage + 4);

      for (let i = startPage; i <= endPage; i++) {
          displayedPages.push(i);
      }

      return displayedPages;
  }

  // goToNextPage(): void {
  //       if (this.currentPageIndex < this.totalPages - 1) {
  //       this.currentPageIndex++;
  //       }
  //   //adding below lines to call the next page records
  //       if(this.getReportDeliveryStatCodes){
  //         var userId:any = this.authService.getuserId();
  //         var fromDate:any = localStorage.getItem('dbFromDate');
  //         var toDate:any = localStorage.getItem('dbToDate');
  //         let filterData = {
  //          // 'userId': userId,
  //           'fromDate': fromDate,
  //           'toDate': toDate,
  //           'status': this.getReportDeliveryStatCodes,
  //           //adding below parameter to get the backend pagination list
  //           'pageNumber':this.currentPageIndex
  //         }
  //         this.orgadmin.getChartDetails(filterData).subscribe((data: any)=>{
  //           this.CharReportDelivery=data.data.candidateDtoList;
  //           // this.CharReportDelivery=data.data.candidateDtoList.reverse();

  //           for(let i=0; i<this.CharReportDelivery.length; i++) {

  //             let final = this.CharReportDelivery[i].contentDTOList;
  //             let interim = this.CharReportDelivery[i].candidateStatusName;

  //             for (let i=0; i<final.length; i++){
  //               if(final[i].contentSubCategory=="FINAL" && filterData.status == 'FINALREPORT'){
  //                 this.finalreport = true
  //               }
  //             }

  //             if(interim == 'Interim Report' && filterData.status == 'INTERIMREPORT') {
  //               this.interimreport = true;
  //             }

  //           }

  //           // this.CharReportDelivery.sort((a: any, b: any) => {
  //           //   const dateA = new Date(a.lastUploadedOn);
  //           //   const dateB = new Date(b.lastUploadedOn);

  //           //   return dateB.getTime() - dateA.getTime();
  //           // });

  //           console.warn("fiterData:::",this.CharReportDelivery);
  //           console.log("After : ", this.CharReportDelivery)
  //           //console.log(data);
  //           const startIndex = this.currentPageIndex * this.pageSize;
  //           const endIndex = startIndex + this.pageSize;
  //           return this.CharReportDelivery.slice(startIndex, endIndex);
  //         });

  //       }
  //   }

  // goToPrevPage(): void {
  //   // this.idvalue=idvalue;
  //       if (this.currentPageIndex > 0) {
  //       this.currentPageIndex--;
  //       }
  //       //adding below lines to call the previous page records
  //       if(this.getReportDeliveryStatCodes){
  //         var userId:any = this.authService.getuserId();
  //         var fromDate:any = localStorage.getItem('dbFromDate');
  //         var toDate:any = localStorage.getItem('dbToDate');
  //         let filterData = {
  //         //  'userId': userId,
  //           'fromDate': fromDate,
  //           'toDate': toDate,
  //           'status': this.getReportDeliveryStatCodes,
  //           //adding below parameter to get the backend pagination list
  //           'pageNumber':this.currentPageIndex
  //         }
  //         this.orgadmin.getChartDetails(filterData).subscribe((data: any)=>{
  //           this.CharReportDelivery=data.data.candidateDtoList;
  //           // this.CharReportDelivery=data.data.candidateDtoList.reverse();

  //           for(let i=0; i<this.CharReportDelivery.length; i++) {

  //             let final = this.CharReportDelivery[i].contentDTOList;
  //             let interim = this.CharReportDelivery[i].candidateStatusName;

  //             for (let i=0; i<final.length; i++){
  //               if(final[i].contentSubCategory=="FINAL" && filterData.status == 'FINALREPORT'){
  //                 this.finalreport = true
  //               }
  //             }

  //             if(interim == 'Interim Report' && filterData.status == 'INTERIMREPORT') {
  //               this.interimreport = true;
  //             }

  //           }

  //           // this.CharReportDelivery.sort((a: any, b: any) => {
  //           //   const dateA = new Date(a.lastUploadedOn);
  //           //   const dateB = new Date(b.lastUploadedOn);

  //           //   return dateB.getTime() - dateA.getTime();
  //           // });

  //           console.warn("fiterData:::",this.CharReportDelivery);
  //           console.log("After : ", this.CharReportDelivery)
  //           //console.log(data);
  //           const startIndex = this.currentPageIndex * this.pageSize;
  //           const endIndex = startIndex + this.pageSize;
  //           return this.CharReportDelivery.slice(startIndex, endIndex);
  //         });

  //       }

  //   }

  // get totalPages(): number {
  //   const filteredItems = this.CharReportDelivery.filter((item: any) => this.searchFilter(item));
  //   return Math.ceil(filteredItems.length / this.pageSize);
  //   }

  get totalPages(): number {
    if(this.getReportDeliveryStatCodes){
      for (let i = 0; i < this.CharReportDeliveryData.length; i++) {
        if(this.CharReportDeliveryData[i].statusCode==this.getReportDeliveryStatCodes){
          // console.log("Total PAges::{}",Math.ceil(this.CharReportDeliveryData[i].count / this.pageSize));
          return Math.ceil(this.CharReportDeliveryData[i].count / this.pageSize);
        }
      }
    }
      else if(this.getConventionalReportDeliveryStatCodes){
        for (let i = 0; i < this.CharConventionalReportDeliveryData.length; i++) {
          if(this.CharConventionalReportDeliveryData[i].statusCode==this.getConventionalReportDeliveryStatCodes){
            console.log("Total Conventional PAges::{}",Math.ceil(this.CharConventionalReportDeliveryData[i].count / this.pageSize));
            return Math.ceil(this.CharConventionalReportDeliveryData[i].count / this.pageSize);
          }
        }
    }
    return 0;
  }

    searchFilter(item: any): boolean {
      const searchText = this.searchText.toLowerCase();
      const candidateName = item.candidateName?.toLowerCase();
      const emailId = item.emailId?.toLowerCase();
      const contactNumber = item.contactNumber?.toLowerCase();
      const applicantId = item.applicantId?.toLowerCase();

      return candidateName?.includes(searchText.toLowerCase()) ||
             emailId?.includes(searchText.toLowerCase()) ||
             contactNumber?.includes(searchText.toLowerCase()) ||
             applicantId?.includes(searchText.toLowerCase());
  }

  downloadFinalReportDirectFromQC(candidate: any,reportStatus: any) {

    if(candidate.candidateCode) {
      this.orgadmin.getPreSignedUrlByCandidateCodeForFinal(candidate.candidateCode,reportStatus).subscribe((url: any)=>{
        window.open(url.data, '_blank');
      });
    }
  }

  activeInactive(referenceNo:any){
    return this.orgadmin.putAgentStat(referenceNo).subscribe((result:any)=>{
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
  openModal(modalData:any, userId:any){
    this.modalService.open(modalData, {
     centered: true,
     backdrop: 'static'
    });
    this.orgadmin.getCandidateDetails(userId).subscribe((result: any)=>{
     this.getCandidate=result.data;
     this.updateCandidate.patchValue({
       candidateName: this.getCandidate.candidateName,
       applicantId: this.getCandidate.applicantId,
       createdByUserFirstName: this.getCandidate.createdByUserFirstName,
       candidateCode: this.getCandidate.candidateCode,
       contactNumber: this.getCandidate.contactNumber,
       emailId: this.getCandidate.emailId
      });
   });
 }

 openForwardReportModal(modalData:any){
    this.modalService.open(modalData, {
      centered: true,
      backdrop: 'static'
    });
    // this.orgadmin.getCandidateDetails(userId).subscribe((result: any)=>{
    //   this.getCandidate=result.data;
    //   this.updateCandidate.patchValue({
    //     candidateName: this.getCandidate.candidateName,
    //     applicantId: this.getCandidate.applicantId,
    //     createdByUserFirstName: this.getCandidate.createdByUserFirstName,
    //     candidateCode: this.getCandidate.candidateCode,
    //     contactNumber: this.getCandidate.contactNumber,
    //     emailId: this.getCandidate.emailId
    //   });
    // });
  }
 onSubmit(updateCandidate:FormGroup) {
  if(this.updateCandidate.valid){
   this.orgadmin.putCandidateData(this.updateCandidate.value).subscribe((result:any)=>{
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

  onForwardReportSubmit(forwardReportForm: any) {

    // console.log(this.tmp, this.tmp.length)
    if(this.tmp.length <= 0) {
      Swal.fire({
        title: 'Please select reports before forwarding',
        icon: 'warning',
      });

      return
    }

    if(forwardReportForm.valid) {
      this.orgadmin
        .forwardReport(this.tmp, forwardReportForm.get('emailIds')?.value)
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
    } else{
      Swal.fire({
        title: "Please enter the correct information",
        icon: 'warning'
      })
    }
  }

  initiatevendor(candidateId: any, candidateCode: any) {
    const navURL = 'admin/vendorinitiaste/' + candidateId + '/' + candidateCode;
    this.router.navigate([navURL]);
  }
}
