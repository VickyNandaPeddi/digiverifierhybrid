import { Component, NgZone, OnInit } from '@angular/core';
import { OrgadminDashboardService } from 'src/app/services/orgadmin-dashboard.service';
import { OrgadminService } from 'src/app/services/orgadmin.service';
import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-vendor-dashboard-pending-details',
  templateUrl: './vendor-dashboard-pending-details.component.html',
  styleUrls: ['./vendor-dashboard-pending-details.component.scss']
})
export class VendorDashboardPendingDetailsComponent implements OnInit {
  private chart: am4charts.XYChart | undefined;
  getPendingDetailsStatCode:any;
  CharPendingDetails: any=[];
  currentPageIndex: number = 0;
  pageSize: number = 10;
  ChartPendingDetails: any=[];



  constructor(private zone: NgZone,private orgadminservice: OrgadminService,private adminDashboard:OrgadminDashboardService
    ,public authService: AuthenticationService) {

    this.getPendingDetailsStatCode = this.adminDashboard.getPendingDetailsStatCode();

    // if(this.getPendingDetailsStatCode){
      var userId:any = this.authService.getuserId();
      var fromDate:any = localStorage.getItem('dbFromDate');
      var toDate:any = localStorage.getItem('dbToDate');
      let filterData = {
        'userId': userId,
        'fromDate': fromDate,
        'toDate': toDate,
        // 'status': this.getPendingDetailsStatCode,
        //adding below parameter to get the backend pagination list
        // 'pageNumber':this.currentPageIndex
      }
  
    //   this.adminDashboard.getPendingChartDetails(filterData).subscribe((data: any)=>{
    //     this.CharPendingDetails=data.data.candidateDtoList;
    //     //console.log(this.CharPendingDetails);
    //     console.log("After : ", this.CharPendingDetails)
    //     //console.log(data);
    //     const startIndex = this.currentPageIndex * this.pageSize;
    //     const endIndex = startIndex + this.pageSize;
    //     return this.CharPendingDetails.slice(startIndex, endIndex);
    //   });
    //  }

    // this.orgadminservice.getVendorCheckStatusAndCount(filterData).subscribe((data:any)=>{
    //     console.warn("DATA ==================== ",data);
    //     this.CharPendingDetails=data.data.vendorCheckStatusAndCount;
    //     console.warn("CharPendingDetails ============= ",this.CharPendingDetails)

    // })
    // }

    }

    ngAfterViewInit() {
      setTimeout(() =>{
        this.ngOnDestroy();
        this.loadCharts();
      },50);
    }

    
  ngOnInit(): void {
    
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
      //chart.legend.maxHeight = 210;
      chart.legend.scrollable = true;

      chart.legend.position = "right";
      chart.logo.disabled = true;
      chart.padding(0, 0, 0, 0);
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

      this.orgadminservice.getVendorCheckStatusAndCount(filterData).subscribe((uploadinfo: any)=>{
        this.ChartPendingDetails=uploadinfo.data.vendorCheckStatusAndCount;
        console.log(this.ChartPendingDetails);
        let data = [];
        for (let i = 0; i < this.ChartPendingDetails.length; i++) {
          // let obj={};
          // obj=this.ChartPendingDetails[i].statusName;
          data.push({name: this.ChartPendingDetails[i].statusName, value: this.ChartPendingDetails[i].count, statcode: this.ChartPendingDetails[i].statusCode });
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

var rgm = new am4core.RadialGradientModifier();
rgm.brightnesses.push(-0.3, -0.3, -0.1, 0, - 0.1);
pieSeries.slices.template.fillModifier = rgm;
pieSeries.slices.template.strokeModifier = rgm;
pieSeries.slices.template.strokeWidth = 0;

pieSeries.slices.template.events.on('hit', (e) => {
  const getchartData = e.target._dataItem as any;
  const statuscodes = getchartData._dataContext.statcode;
  this.adminDashboard.setPendingDetailsStatCode(statuscodes);
  window.location.reload();
});
chart.legend.itemContainers.template.events.on("hit", (ev) => {
  const getchartData = ev.target._dataItem as any;
  const statuscodes = getchartData._label._dataItem._dataContext.statcode;
  this.adminDashboard.setPendingDetailsStatCode(statuscodes);
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


}
