import { Component, OnInit } from '@angular/core';
import {
  FormGroup,
  FormControl,
  FormBuilder,
  Validators,
} from '@angular/forms';
import { CustomerService } from '../../services/customer.service';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';
import { timer } from 'rxjs';
import { ActivatedRoute } from '@angular/router';

// import * as FileSaver from 'file-saver';

import { NgbCalendar, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import * as XLSX from 'xlsx';
import * as XLSXStyle from 'xlsx';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { data } from 'jquery';
import { any } from '@amcharts/amcharts4/.internal/core/utils/Array';
import { LoaderService } from 'src/app/services/loader.service';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import { OrgadminDashboardService } from 'src/app/services/orgadmin-dashboard.service';

const EXCEL_TYPE =
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';

@Component({
  selector: 'app-purged-report',
  templateUrl: './purged-report.component.html',
  styleUrls: ['./purged-report.component.scss']
})
export class PurgedReportComponent implements OnInit {
  pageTitle = 'Customer Purged Report';
  getCustomerUtilizationReport: any = [];
  //merge excel start
  getCandidateUtilizationReport: any = [];
  company = new Map<string, {}>();
  company_name: any = [];
  excel_data: any = [];
  geteKycReport: any = [];
  orgKycReport: any = [];
  getAgentUtilizationReport: any = [];
  init_agent_details: any = [];
  agent_details: any = [];
  start_date = '';
  end_date = '';
  //merge excel end
  getCustomerUtilizationReportByAgent: any = [];
  getCanididateDetailsByStatus: any = [];
  getCustID: any = [];
  custId: any = 0;
  getAgentList: any = [];
  fromDate: any;
  toDate: any;
  setfromDate: any;
  settoDate: any;
  getToday: NgbDate;
  getMinDate: any;
  kyc: Boolean = false;
  //date filtration:
  initToday: any;

  statusList: string[] = [
    'PURGED',
    // 'INVITATIONEXPIRED',
    // 'PENDINGAPPROVAL',
    // 'PENDINGNOW',
    // 'FINALREPORT',
    // 'NEWUPLOAD',
    // 'REPORTDELIVERED',
    // 'FINALREPORTTOTAL',
    // 'PENDINGAPPROVALTOTAL',
    // 'INVITATIONEXPIREDTOTAL',
    // 'NEWUPLOADTOTAL',
    // 'PENDINGNOWTOTAL',
    // 'PROCESSDECLINEDTOTAL',
    // 'REINVITETOTAL',
    // 'LOA',
    // 'GST',
  ];
  responseCheck = new Map();
  dashboardFilter = new FormGroup({
    fromDate: new FormControl('', Validators.required),
    toDate: new FormControl('', Validators.required),
  });

  utilizationReportClick = new FormGroup({
    fromDate: new FormControl('', Validators.required),
    toDate: new FormControl('', Validators.required),
    organizationIds: new FormControl('', Validators.required),
    statusCode: new FormControl('', Validators.required),
  });
  utilizationReportFilter = new FormGroup({
    fromDate: new FormControl('', Validators.required),
    toDate: new FormControl('', Validators.required),
    organizationIds: new FormControl('', Validators.required),
  });

  excelBuffer: any;
  fileName = 'export.xlsx';
  hideLoadingBtn: boolean = false;
  reportDeliveredList: any[] = [];
  processDeclinedList: any[] = [];
  invitationExpiredList: any[] = [];
  orgName: any;
  billingAddress: any;
  hideExtra: boolean = true;
  pdfUrl: string = '';

  exportexcel(): void {
    const dKey = '12345678901234567890123456789012'; // 32-byte key
    /* table id is passed over here */
    //  let element = document.getElementById('excel-table');
    //  const ws: XLSX.WorkSheet =XLSX.utils.table_to_sheet(element);
    //  /* generate workbook and add the worksheet */
    //  const wb: XLSX.WorkBook = XLSX.utils.book_new();
    //  XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');
    //  /* save to file */
    //  XLSX.writeFile(wb, this.fileName);
    console.log('Inside excel', this.company);

    const fileName = 'Candidate Purged Report.xlsx';
    // const sheetName = ['sheet1', 'sheet2', 'sheet3'];

    //   let wb = XLSX.utils.book_new();
    //   for (var i = 0; i < sheetName.length; i++) {
    //     let ws = XLSX.utils.json_to_sheet(arr[i]);
    //     XLSX.utils.book_append_sheet(wb, ws, sheetName[i]);
    //   }
    //   XLSX.writeFile(wb, fileName);

    let wb = XLSX.utils.book_new();
    let ws_newupload: any = {};
    let ws_PENDINGNOW: any = {};
    let ws_FINALREPORT: any = {};
    let ws_PENDINGAPPROVAL: any = {};
    let ws_INVITATIONEXPIRED: any = {};
    let ws_REINVITE: any = {};
    let ws_ReportDelivered: any = {};
    let ws_Loa: any = {};
    let ws_Gst: any ={}
    let REPORTDELIVERED = 0;
    let OLD_REPORTDELIVERED_LEN = 0;
    let NEWUPLOAD = 0;
    let PENDINGNOW = 0;
    let FINALREPORT = 0;
    let PENDINGAPPROVAL = 0;
    let INVITATIONEXPIRED = 0;
    let REINVITE = 0;
    let LOA = 0;
    let GST = 0;
    let OLD_NEWUPLOAD_LEN = 0;
    let OLD_PENDINGNOW_LEN = 0;
    let OLD_FINALREPORT_LEN = 0;
    let OLD_PENDINGAPPROVAL_LEN = 0;
    let OLD_INVITATIONEXPIRED_LEN = 0;
    let OLD_REINVITE_LEN = 0;
    let OLD_LOA_LEN=0;
    let OLD_GST_LEN=0;

    // let ws_OVERALLSummary = XLSX.utils.json_to_sheet(this.getCustomerUtilizationReport);
    let element = document.getElementById('excel-table');
    let ws_OVERALLSummary = XLSX.utils.table_to_sheet(element);

    // Convert the table to an Excel sheet
    // Iterate through the cells in the sheet and set the cell format to "General"
    for (let cellAddress in ws_OVERALLSummary) {   
      if (ws_OVERALLSummary.hasOwnProperty(cellAddress)) {
             let cell = ws_OVERALLSummary[cellAddress]; // Check if the cell contains a date value (you may need to adjust this condition)    
            //  if (cellAddress == 'A4') {       // Set the cell format to "General"      
            //     cell.t = 'n'; // 'n' stands for number format (General)    
            //     cell.z = 'dd/mm/yy';
            //     cell.v = 'Start Date : ' + this.start_date;
            //     console.log('cell a1', cell, cellAddress)

            //   if (!cell.s) {
            //     cell.s = {}; // Initialize the style object if it doesn't exist
            //   }
            //   cell.s.alignment = { horizontal: 'center' };
            //   cell.s.font = { bold: true };

            // }   

            // if (cellAddress == 'B4') {       // Set the cell format to "General"      
            //   cell.t = 'n'; // 'n' stands for number format (General)    
            //   cell.z = 'dd/mm/yy';
            //   cell.v = 'End Date : ' + this.end_date;
            //   console.log('cell a1', cell, cellAddress)
            // }
      } 
    } // Create a new workbook and add the sheet to it

    // let filteredList = this.filteredItems();
    // console.log(this.geteKycReport, filteredList)
    // for (let item in filteredList) {
    //   delete filteredList[item]['applicantId'];
    // }
    let ws_ekycReport = XLSX.utils.json_to_sheet(this.geteKycReport);

    this.company.forEach((value: any = [], key: string) => {
      const filter_key = key.slice(0, 30);

      for (let val in value) {
        delete value[val].dateOfEmailInvite;
        delete value[val].numberofexpiredCount;
        delete value[val].reinviteCount;
        delete value[val].clearCount;
        delete value[val].inProgressCount;
        delete value[val].inSufficiencyCount;
        delete value[val].majorDiscrepancyCount;
        delete value[val].numberofexpiredCount;
        delete value[val].minorDiscrepancyCount;
        delete value[val].unableToVerifyCount;
        delete value[val].candidateUan;
        delete value[val].dateOfBirth;
        delete value[val].address;
        delete value[val].aadharFatherName;
        delete value[val].relationName;
        // delete value[val].colorName;
        delete value[val].aadharNumber;
        delete value[val].aadharName;
        delete value[val].aadharDob;
        delete value[val].aadharGender;
        delete value[val].panDob;
        delete value[val].panName;
        delete value[val].candidateUanName;
        delete value[val].relationship;
       
        if(value[val].preOfferReportColor || value[val].interimReportColor){
          value[val].preOfferReportColor=value[val].preOfferReportColor;
          value[val].interimReportColor=value[val].interimReportColor;
        }else{
          delete value[val].preOfferReportColor;
          delete value[val].interimReportColor;
        }

        if (value[val].panNumber){
            value[val].panNumber=this.decryptData(value[val].panNumber, dKey);
        }else{
          delete value[val].panNumber;
        }

        if(value[val].createdByUserFirstName || value[val].createdByUserLastName){
          value[val].createdByUserFirstName=value[val].createdByUserFirstName;
          value[val].createdByUserLastName=value[val].createdByUserLastName;
        }else{
          delete value[val].createdByUserFirstName;
          delete value[val].createdByUserLastName;
        }

        
        
        if(value[val].contactNumber){
          value[val].contactNumber=value[val].contactNumber;
        }else{
          delete value[val].contactNumber;
        }
        if(value[val].emailId){
          value[val].emailId=value[val].emailId;
        }else{
          delete value[val].emailId;
        }
        if(value[val].experience){
          value[val].experience=value[val].experience;
        }else{
          delete value[val].experience;
        }

        if (value[val].organizationOrganizationName)
          value[val].organizationName = value[val].organizationOrganizationName;
        else value[val].organizationName = key.substring(0, key.indexOf(','));
        delete value[val].organizationOrganizationName;

        delete value[val].candidateId;
        delete value[val].currentStatusDate;
        delete value[val].candidateCode;
        delete value[val].organizationName;

        if(value[val].statusName){
          value[val].Status = value[val].statusName;
        }else{
          delete value[val].Status ;
        }
        
        // value[val].Date = value[val].statusDate

        if(value[val].qcCreatedOn){
          value[val].PreOfferDate = value[val].qcCreatedOn;
        }else{
          delete value[val].PreOfferDate;

        }
        if(value[val].interimCreatedOn){
          value[val].InterimDate = value[val].interimCreatedOn;
        }else{
          delete value[val].InterimDate;
          
        }
        
        delete value[val].qcCreatedOn;
        delete value[val].interimCreatedOn;

        delete value[val].statusName;
        if (!key.includes('REINVITE') && !key.includes('INVITATIONEXPIRED') && !key.includes('GST')){
          delete value[val].statusDate;
        }

        if(!key.includes('GST')){
          delete value[val].gstNumber;
          delete value[val].colorName;
        }
        
      }

      // Set column widths
      const columnWidths = [
        { wpx: 150 }, // Column 1 width is set to 100 pixels
        { wpx: 150 }, // Column 2 width is set to 150 pixels
        { wpx: 100 }, // Column 3 width is set to 120 pixels
        { wpx: 100 },
        { wpx: 200 },
        { wpx: 80 },
        { wpx: 50 },
        { wpx: 150 },
        { wpx: 100 },
        { wpx: 100 },
        { wpx: 150 },
        { wpx: 150 },
        { wpx: 150 },
        { wpx: 150 },
        { wpx: 100 }
      ];

      if (key.includes('PURGED')) {
        // console.log("Outside",key, value.length, value)
        if (REPORTDELIVERED == 0) {
          console.log('Inside zero..', key, value.length, value);
          ws_ReportDelivered = XLSX.utils.json_to_sheet(value);
          REPORTDELIVERED = 1;
          OLD_REPORTDELIVERED_LEN = OLD_REPORTDELIVERED_LEN + value.length;
        } else {
          console.log('Inside Non zero', key, OLD_REPORTDELIVERED_LEN, value);
          XLSX.utils.sheet_add_json(ws_ReportDelivered, value, {
            skipHeader: false,
            origin: `A${OLD_REPORTDELIVERED_LEN + 2}`,
          });
          OLD_REPORTDELIVERED_LEN = OLD_REPORTDELIVERED_LEN + value.length;
          console.log('OLD_REINVITE_LEN', OLD_REPORTDELIVERED_LEN);
        }
        ws_ReportDelivered['!cols'] = columnWidths;
      } else if (key.includes('LOA')) {
         // console.log("Outside",key, value.length, value)
         if (LOA == 0) {
          console.log('Inside zero..', key, value.length, value);
          // Assuming value is your JSON data
          value.forEach((item:any) => {
            item['Initiated On'] = item['createdOn'];
            item['Accepted On'] = item['PreOfferDate'];
            item['Loa Stored'] = 'Yes';
            delete item['createdOn'];
            delete item['PreOfferDate'];
          });
          ws_Loa = XLSX.utils.json_to_sheet(value);
          LOA = 1;
          OLD_LOA_LEN = OLD_LOA_LEN + value.length;
        } else {
          console.log('Inside Non zero', key, OLD_LOA_LEN, value);
          XLSX.utils.sheet_add_json(ws_Loa, value, {
            skipHeader: false,
            origin: `A${OLD_LOA_LEN + 2}`,
          });
          OLD_LOA_LEN = OLD_LOA_LEN + value.length;
          console.log('OLD_LOA_LEN', OLD_LOA_LEN);
        }
        ws_Loa['!cols'] = columnWidths;

      }else if (key.includes('GST')) {
        // console.log("Outside",key, value.length, value)
        if (GST == 0) {
         console.log('Inside zero..', key, value.length, value);
         // Assuming value is your JSON data
         value.forEach((item:any) => {
          item['Triggered Spoc'] = item['createdByUserFirstName'];
           item['BGC Initiated Date'] = item['createdOn'];
           item['BGC close Date'] = item['statusDate'];
           item['Remarks'] = item['gstNumber'];
           item['CID'] = item['applicantId'];
           item['GST status'] = item['colorName'];
           item['PAN Number'] = item['panNumber'];
           delete item['createdOn'];
           delete item['gstNumber'];
           delete item['createdByUserLastName'];
           delete item['createdByUserFirstName'];
           delete item['applicantId'];
           delete item['statusDate'];
           delete item['experience'];
           delete item['colorName'];
           delete item['panNumber'];
         });
         ws_Gst = XLSX.utils.json_to_sheet(value);
         GST = 1;
         OLD_GST_LEN = OLD_GST_LEN + value.length;
       } else {
         console.log('Inside Non zero', key, OLD_GST_LEN, value);
         XLSX.utils.sheet_add_json(ws_Gst, value, {
           skipHeader: false,
           origin: `A${OLD_GST_LEN + 2}`,
         });
         OLD_GST_LEN = OLD_GST_LEN + value.length;
         console.log('OLD_GST_LEN', OLD_GST_LEN);
       }
       const widths = [
        { wpx: 100 }, // Column 1 width is set to 100 pixels
        { wpx: 100 }, // Column 2 width is set to 150 pixels
        { wpx: 100 }, // Column 3 width is set to 120 pixels
        { wpx: 200 },
        { wpx: 100 },
        { wpx: 150 },
        { wpx: 150 },
        { wpx: 150 },
        { wpx: 150 },
        { wpx: 100 },
        { wpx: 100 }
      ];
       ws_Gst['!cols'] = widths;
       const columnOrder = ['CID','candidateName','contactNumber','emailId','PAN Number','Status','Triggered Spoc','BGC Initiated Date','BGC close Date','Remarks','GST status'];
       columnOrder.forEach((colName, index) => {
        const cell = XLSX.utils.encode_cell({ c: index, r: 0 }); // Assuming header row is at index 0
        ws_Gst[cell] = { v: colName, t: 's', s: { font: { bold: true } } }; // Bold header
        value.forEach((item: any, rowIndex: number) => {
          ws_Gst[XLSX.utils.encode_cell({ c: index, r: rowIndex + 1 })] = { v: item[colName] };
        });
      });

     }else if (key.includes('AGENT')) {
        // Set column widths
        const ws_OVERALLSummarycolumnWidths = [
          { wpx: 150 }, // Column 1 width is set to 100 pixels
          { wpx: 150 }, // Column 2 width is set to 150 pixels
          { wpx: 100 }, // Column 3 width is set to 120 pixels
          { wpx: 100 },
          { wpx: 100 },
          { wpx: 100 },
          { wpx: 100 },
          { wpx: 100 },
          { wpx: 100 },
          { wpx: 100 },
        ];
        ws_OVERALLSummary['!cols'] = ws_OVERALLSummarycolumnWidths;
      }
    });

    // Define the default font size
    const defaultFontSize = 10;

    // Set column widths
    const eKycReportcolumnWidths = [
      { wpx: 50 }, // Column 1 width is set to 100 pixels
      { wpx: 100 }, // Column 2 width is set to 150 pixels
      { wpx: 150 }, // Column 3 width is set to 120 pixels
      { wpx: 80 },
      { wpx: 200 },
      { wpx: 70 },
      { wpx: 150 },
      { wpx: 200 },
      { wpx: 100 },
      { wpx: 200 },
      { wpx: 70 },
      { wpx: 50 },
      { wpx: 100 },
      { wpx: 100 },
      { wpx: 250 },
    ];

    ws_ekycReport['!cols'] = eKycReportcolumnWidths;

    // Set font size for the cells
    const fontSize = { sz: 10 }; // Font size set to 12 points
    const style = XLSX.utils.encode_cell({ r: 0, c: 0 }); // Choose a cell to apply the font size
    if (ws_ekycReport[style]) ws_ekycReport[style].s = { font: fontSize };

    // Set header style and color
    //  const headerStyle = {
    //   fill: {
    //     fgColor: { rgb: 'FF0000' } // Red background color
    //   },
    //   font: {
    //     bold: true,
    //     color: {
    //       rgb: 'FFFFFF' // White font color
    //     }
    //   }
    // };
    // const headerStyle = {
    //   fill: {
    //     patternType: 'solid',
    //     fgColor: { rgb: 'FF0000' }, // Red background color
    //   },
    //   font: {
    //     bold: true,
    //     color: { rgb: 'FFFFFF' }, // White font color
    //   },
    // };
    // // Create a custom cell style for the header row
    // // const headerCellStyle = XLSX.utils.book_new().SS.createStyle(headerStyle);
    // const range = XLSX.utils.decode_range(ws_ekycReport['!ref'] || 'A1:A1'); // Get the range of cells
    // for (let col = range.s.c; col <= range.e.c; col++) {
    //   const cellAddress = XLSX.utils.encode_cell({ r: 0, c: col });
    //   ws_ekycReport[cellAddress].s = headerStyle;
    //   ws_ekycReport[cellAddress].fill = {
    //     fgColor: { argb: 'FF0000' },
    //   };
    //   // XLSX.utils.book_new().SS.setCellStyle(0, XLSX.utils.decode_cell(cellAddress), headerCellStyle);
    // }

    // // Set text wrap for all cells
    // for (let row = range.s.r; row <= range.e.r; row++) {
    //   for (let col = range.s.c; col <= range.e.c; col++) {
    //     const cellAddress = XLSX.utils.encode_cell({ r: row, c: col });
    //     const cell = ws_ekycReport[cellAddress];
    //     if (cell) {
    //       if (!cell.s) cell.s = {}; // Create style object if it doesn't exist
    //       cell.s.alignment = { wrapText: true };
    //     }
    //     console.log(ws_ekycReport[cellAddress]);
    //   }
    // }

    XLSX.utils.book_append_sheet(wb, ws_OVERALLSummary, 'CandidatePurged');
    // XLSX.utils.book_append_sheet(wb, ws_newupload, 'NewUpload');
    // XLSX.utils.book_append_sheet(wb, ws_FINALREPORT, 'FinalReport');
    // XLSX.utils.book_append_sheet(wb, ws_PENDINGNOW, 'PendingB4QC_UntillNow');
    // XLSX.utils.book_append_sheet(wb, ws_PENDINGAPPROVAL, 'PendingApproval');
    // XLSX.utils.book_append_sheet(wb, ws_INVITATIONEXPIRED, 'InvitationExpired');
    // XLSX.utils.book_append_sheet(wb, ws_REINVITE, 'Reinvite');
    // XLSX.utils.book_append_sheet(wb, ws_ekycReport, 'EkycReport');
    // XLSX.utils.book_append_sheet(wb, ws_ReportDelivered, 'CandidatePurged');
    // XLSX.utils.book_append_sheet(wb, ws_Loa, 'Loa');
    // XLSX.utils.book_append_sheet(wb, ws_Gst, 'Gst');

    XLSX.writeFile(wb, fileName);

    //   const ws: XLSX.WorkSheet =XLSX.utils.json_to_sheet(this.getCustomerUtilizationReport);
    //   const wb: XLSX.WorkBook = XLSX.utils.book_new();
    //   XLSX.utils.book_append_sheet(wb, ws, 'Overallsummary');
    //   var count = 0;

    //   this.company.forEach((value: any=[], key: string) => {
    //     const filter_key = key.slice(0,30);
    //     this.company_name.push(filter_key);
    //     console.log("#########################");
    //     if (key.includes('NEWUPLOAD')){
    //       console.log(key, typeof(key), this.company_name, value);
    //     //   const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(value);
    //     //   const workbook: XLSX.WorkBook = { Sheets: { filter_key : worksheet }, SheetNames: this.company_name };
    //     //   const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    //     //   const data: Blob = new Blob([excelBuffer], {type: EXCEL_TYPE});
    //     //   FileSaver.saveAs(data, this.fileName);

    //     //   this.exportAsExcelFile(value, key);
    //       const Newuploads_worksheet = XLSX.utils.json_to_sheet(value,{origin: -1});
    //       console.log("Newuploads_worksheet",Newuploads_worksheet);
    //       // XLSX.utils.sheet_add_json(Newuploads_worksheet,value, {skipHeader: false,origin: -1} );
    //       XLSX.utils.book_append_sheet(wb, Newuploads_worksheet,key);
    //     }

    // );

    // XLSX.writeFile(wb, this.fileName);
  }

  onfromDate(event: any) {
    let year = event.year;
    let month = event.month <= 9 ? '0' + event.month : event.month;
    let day = event.day <= 9 ? '0' + event.day : event.day;
    let finalDate = day + '/' + month + '/' + year;
    this.fromDate = finalDate;
    this.getMinDate = { day: +day, month: +month, year: +year };
  }
  ontoDate(event: any) {
    let year = event.year;
    let month = event.month <= 9 ? '0' + event.month : event.month;
    let day = event.day <= 9 ? '0' + event.day : event.day;
    let finalDate = day + '/' + month + '/' + year;
    this.toDate = finalDate;
  }

  filteredItems() {
    let uniqueIds : string[] = [];

    return this.geteKycReport.filter((item: any) => {
      if (uniqueIds.includes(item.applicantId)) {
        return false; // Skip duplicate items
      } else {
        uniqueIds.push(item.applicantId);
        return true; // Include unique items
      }
    });
  }

  totalCalculation() {
    let number = 0;
    for ( let item in this.getCustomerUtilizationReport ){
      if(this.getCustomerUtilizationReport[item]['name'] = 'TOTAL'){
        number = number + Number(this.getCustomerUtilizationReport[item]['eKYC']);
      }
    }
    return number;
  }
  constructor(
    private customers: CustomerService,
    private navrouter: Router,
    calendar: NgbCalendar,
    private route: ActivatedRoute,
    public authService: AuthenticationService,
    private loaderService: LoaderService,
    private orgadmin: OrgadminDashboardService
  ) {
    this.getToday = calendar.getToday();
    this.customers.getCustomersBill().subscribe((data: any) => {
      this.getCustID = data.data;
    });

    //date filteration
    this.getToday = calendar.getToday();
    let inityear = this.getToday.year;
    let initmonth =
      this.getToday.month <= 9
        ? '0' + this.getToday.month
        : this.getToday.month;
    let initday =
      this.getToday.day <= 9 ? '0' + this.getToday.day : this.getToday.day;
    let initfinalDate = initday + '/' + initmonth + '/' + inityear;
    this.initToday = initfinalDate;
    if (
      localStorage.getItem('dbFromDate') == null &&
      localStorage.getItem('dbToDate') == null
    ) {
      this.customers.setFromDate(this.initToday);
      this.customers.setToDate(this.initToday);
      this.fromDate = this.initToday;
      this.toDate = this.initToday;
      console.warn('INSIDE GET FROM::', this.fromDate);
      console.warn('INSIDE GET TO::', this.toDate);
    }

    var checkfromDate: any = localStorage.getItem('dbFromDate');
    let getfromDate = checkfromDate.split('/');
    this.setfromDate = {
      day: +getfromDate[0],
      month: +getfromDate[1],
      year: +getfromDate[2],
    };

    var checktoDate: any = localStorage.getItem('dbToDate');
    let gettoDate = checktoDate.split('/');
    this.settoDate = {
      day: +gettoDate[0],
      month: +gettoDate[1],
      year: +gettoDate[2],
    };
    this.getMinDate = {
      day: +gettoDate[0],
      month: +gettoDate[1],
      year: +gettoDate[2],
    };

    this.dashboardFilter.patchValue({
      fromDate: this.setfromDate,
      toDate: this.settoDate,
    });

    let rportData = {
      // userId: localStorage.getItem('userId'),
      userId: this.authService.getuserId(),
      fromDate: localStorage.getItem('dbFromDate'),
      toDate: localStorage.getItem('dbToDate'),
    };
    console.log('ekycReport request body', rportData);

    if (authService.roleMatch(['ROLE_CBADMIN'])) {
      let getfromDate = localStorage.getItem('dbFromDate')?.split('/');
      if (getfromDate)
        this.setfromDate = {
          day: +getfromDate[0],
          month: +getfromDate[1],
          year: +getfromDate[2],
        };
      this.getMinDate = this.setfromDate;

      let gettoDate = localStorage.getItem('dbToDate')?.split('/');
      if (gettoDate)
        this.settoDate = {
          day: +gettoDate[0],
          month: +gettoDate[1],
          year: +gettoDate[2],
        };

      this.utilizationReportFilter.patchValue({
        fromDate: this.setfromDate,
        toDate: this.settoDate,
      });

      this.fromDate = localStorage.getItem('dbFromDate');
      this.toDate = localStorage.getItem('dbToDate');

      Swal.fire({
        title: 'Select Customer',
        icon: 'info',
      });

      this.hideLoadingBtn = true;
    } else {
      this.resetMap();
      let organizationIds: any = [];
            this.kyc = true;

      let orgID = this.authService.getOrgID();
      let fromDate = localStorage.getItem('dbFromDate');
      let toDate = localStorage.getItem('dbToDate');

      this.statusList.forEach((status: any) => {
        var features: any = {};
        const statusCode = status;

        const agentIds = this.route.snapshot.queryParamMap.get('agentIds');
        const isAgent = this.route.snapshot.queryParamMap.get('isAgent');

        let agentIdsArray: any = [];
        agentIdsArray.push(agentIds);

        if (isAgent == 'true') {
          this.utilizationReportClick.patchValue({
            fromDate: fromDate != null ? fromDate.split('-').join('/') : '',
            toDate: toDate != null ? toDate.split('-').join('/') : '',
            organizationIds: [this.authService.getOrgID()],
            statusCode: statusCode,
            agentIds: agentIdsArray,
          });
        } else {
          this.utilizationReportClick.patchValue({
            fromDate: fromDate != null ? fromDate.split('-').join('/') : '',
            toDate: toDate != null ? toDate.split('-').join('/') : '',
            organizationIds: [orgID],
            statusCode: statusCode,
            agentIds: [],
          });
        }
              this.kyc = true;
      });

      const statusCode = 'agent';
      // console.log("statusCode type *****",typeof(statusCode), statusCode);
      const agentIds = this.route.snapshot.queryParamMap.get('agentIds');
      const isAgent = 'true';
      // console.log("agentIds isAgent",agentIds,isAgent);
      let agentIdsArray: any = [];
      agentIdsArray.push(agentIds);

      this.utilizationReportClick.patchValue({
        fromDate: fromDate != null ? fromDate.split('-').join('/') : '',
        toDate: toDate != null ? toDate.split('-').join('/') : '',
        organizationIds: [orgID],
        statusCode: statusCode,
        agentIds: agentIdsArray,
      });

      // this.customers
      //   .getCustomerUtilizationReportByAgent(this.utilizationReportClick.value)
      //   .subscribe((data: any) => {
      //     if (data.data) {
      //       if (data.data.reportResponseDtoList != null) {
      //         // console.log("Agent result",data);
      //         this.getAgentUtilizationReport = data.data.reportResponseDtoList;
      //         let index = this.getCustomerUtilizationReport.find(
      //           (temp: any) => {
      //             temp.id == orgID;
      //           }
      //         );
      //         this.company.set(
      //           this.getCustomerUtilizationReport[index]?.name + ', ' + 'AGENT',
      //           data.data.reportResponseDtoList
      //         );
      //         this.fromDate =
      //           data.data.fromDate != null ? data.data.fromDate : '';
      //         this.toDate = data.data.toDate != null ? data.data.toDate : '';
      //       }
      //     }

      //     this.responseCheck.set('agent', true);
      //     let allResponseReceived = true;
      //     for (let entry of this.responseCheck.entries()) {
      //       if (entry[1] == false) {
      //         // console.log('response not received yet', entry[0]);
      //         allResponseReceived = false;
      //       }
      //     }

      //     if (allResponseReceived) {
            this.kyc = true;
      //     }
      //   });

      this.utilizationReportFilter.patchValue({
        fromDate: this.setfromDate,
        toDate: this.settoDate,
      });

      if (authService.roleMatch(['ROLE_AGENTHR'])) {
        const navURL = 'admin/customerUtilizationAgent/';
        this.navrouter.navigate([navURL], {
          queryParams: {
            fromDate: this.fromDate,
            toDate: this.toDate,
            organizationIds: this.authService.getOrgID(),
            statusCode: 'agent',
          },
        });
      }
    }
  }

  getData(custId: any, statusCode: any) {
    let organizationIds: any = [];
    organizationIds.push(custId);
    console.log('this.fromDate, this.toDate', this.fromDate, this.toDate);
    this.utilizationReportClick.patchValue({
      fromDate: this.fromDate,
      toDate: this.toDate,
      organizationIds: organizationIds,
      statusCode: statusCode,
    });
    if (statusCode == 'agent') {
      const navURL = 'admin/customerUtilizationAgent/';
      this.navrouter.navigate([navURL], {
        queryParams: {
          fromDate: this.fromDate,
          toDate: this.toDate,
          organizationIds: organizationIds,
          statusCode: statusCode,
        },
      });
    } else {
      const navURL = 'admin/customerUtilizationCandidate/';
      this.navrouter.navigate([navURL], {
        queryParams: {
          fromDate: this.fromDate,
          toDate: this.toDate,
          organizationIds: organizationIds,
          statusCode: statusCode,
        },
      });
    }
  }

  getcustId(id: any) {
    this.custId = id;
  }
  onSubmitFilter(utilizationReportFilter: FormGroup) {
    this.hideLoadingBtn = false;
    
    this.resetMap();
    this.fromDate = this.fromDate != null ? this.fromDate : '';
    this.toDate = this.toDate != null ? this.toDate : '';

    var checkfromDate: any = this.fromDate;
    let getfromDate = checkfromDate.split('/');
    this.setfromDate = {
      day: +getfromDate[0],
      month: +getfromDate[1],
      year: +getfromDate[2],
    };

    var checktoDate: any = this.toDate;
    let gettoDate = checktoDate.split('/');
    this.settoDate = {
      day: +gettoDate[0],
      month: +gettoDate[1],
      year: +gettoDate[2],
    };
    this.getMinDate = {
      day: +gettoDate[0],
      month: +gettoDate[1],
      year: +gettoDate[2],
    };

    let organizationIds: any = [];
    organizationIds.push(this.custId);
    this.utilizationReportFilter.patchValue({
      fromDate: this.setfromDate,
      toDate: this.settoDate,
      organizationIds: organizationIds.includes(0)
        ? [Number(this.authService.getOrgID())]
        : organizationIds,
    });
    console.warn('FROMDATE xyz:::', this.fromDate);
    console.warn('TODATE xyz:::', this.toDate);

    this.customers
      .getCandidatePurgedReport({
        fromDate: this.fromDate,
        toDate: this.toDate,
        organizationIds: organizationIds.includes(0)
          ? [Number(this.authService.getOrgID())]
          : organizationIds,
      })
      .subscribe((data: any) => {
        this.responseCheck.set('utilizationReport', true);
        let allResponseReceived = true;
        for (let entry of this.responseCheck.entries()) {
          if (entry[1] == false) {
            // console.log('response not received yet', entry[0]);
            allResponseReceived = false;
          }
        }

        if (allResponseReceived) {
          this.kyc = true;
        }
        if (data.outcome === true) {
          this.getCustomerUtilizationReport = data.data?.reportResponseDtoList;
          this.orgName = this.getCustomerUtilizationReport[0].name;
          this.billingAddress = this.getCustomerUtilizationReport[0].newuploadStatusCode;
          for ( let item in this.getCustomerUtilizationReport ){
            this.getCustomerUtilizationReport[item]['eKYC'] = this.geteKycReport.length;
          }
          this.start_date =
            data.data.fromDate != null
              ? data.data.fromDate.split('-').join('/')
              : '';
          this.end_date =
            data.data.toDate != null
              ? data.data.toDate.split('-').join('/')
              : '';

        } else {
          Swal.fire({
            title: data.message,
            icon: 'warning',
          });
        }
      });

    let rportData = {
      userId: this.authService.getuserId(),
      fromDate: this.fromDate,
      toDate: this.toDate,
      organizationIds: organizationIds.includes(0)
        ? [Number(this.authService.getOrgID())]
        : organizationIds,
    };

    console.log('ekycReport request body', rportData);
    this.responseCheck.set('ekycReport', true);

    let orgID = this.authService.getOrgID();
    let fromDate = localStorage.getItem('dbFromDate');
    let toDate = localStorage.getItem('dbToDate');
    
    this.company = new Map<string, {}>();
    this.statusList.forEach((status: any) => {
      var features: any = {};
      const statusCode = status;

      const agentIds = this.route.snapshot.queryParamMap.get('agentIds');
      const isAgent = this.route.snapshot.queryParamMap.get('isAgent');

      let agentIdsArray: any = [];
      agentIdsArray.push(agentIds);

      if (isAgent == 'true') {
        this.utilizationReportClick.patchValue({
          // fromDate: fromDate != null ? fromDate.split('-').join('/') : '',
          // toDate: toDate != null ? toDate.split('-').join('/') : '',
          // organizationIds: [localStorage.getItem('orgID')],
          fromDate: this.fromDate,
          toDate: this.toDate,
          organizationIds: organizationIds.includes(0)
            ? [Number(this.authService.getOrgID())]
            : organizationIds,
          statusCode: statusCode,
          agentIds: agentIdsArray,
        });
      } else {
        this.utilizationReportClick.patchValue({
          fromDate: this.fromDate,
          toDate: this.toDate,
          organizationIds: organizationIds.includes(0)
            ? [Number(this.authService.getOrgID())]
            : organizationIds,
          statusCode: statusCode,
          agentIds: [],
        });
      }

      this.customers
        .getPurgedCanididateDetailsByStatus(this.utilizationReportClick.value)
        .subscribe((result: any) => {

          if(result.outcome)
            this.pdfUrl =  result.message;         

          // console.log(
          //   result['data']['organizationName'],
          //   result.data.candidateDetailsDto.length
          // );

          this.responseCheck.set(status, true); 
          if (
            result['data']['organizationName'] != null &&
            result.data?.candidateDetailsDto?.length > 0
          ) {
            this.getCandidateUtilizationReport =
              result.data.candidateDetailsDto;
            features[result['data']['statusCode']] =
              result.data.candidateDetailsDto;

              this.reportDeliveredList = [];
              this.processDeclinedList = [];
              this.invitationExpiredList = [];
              
              // Iterate over the candidate details and classify them into the appropriate lists
              this.getCandidateUtilizationReport.forEach((candidate: any) => {
                if (candidate.qcCreatedOn) {
                  this.reportDeliveredList.push(candidate);
                } else if (candidate.processDeclinedDate) {
                  this.processDeclinedList.push(candidate);
                } else if (candidate.invitationExpiredDate) {
                  this.invitationExpiredList.push(candidate);
                }
              });

            this.company.set(
              result['data']['organizationName'] +
                ', ' +
                result['data']['statusCode'],
              result.data.candidateDetailsDto
            );
          }

          let allResponseReceived = true;
          for (let entry of this.responseCheck.entries()) {
            if (entry[1] == false) {
              // console.log('response not received yet', entry[0]);
              allResponseReceived = false;
            }
          }

          if (allResponseReceived) {
            this.kyc = true;
          }
        });
    });

    const statusCode = 'agent';
    // console.log("statusCode type *****",typeof(statusCode), statusCode);
    const agentIds = this.route.snapshot.queryParamMap.get('agentIds');
    const isAgent = 'true';
    // console.log("agentIds isAgent",agentIds,isAgent);
    let agentIdsArray: any = [];
    agentIdsArray.push(agentIds);

    this.utilizationReportClick.patchValue({
      fromDate: this.fromDate,
      toDate: this.toDate,
      organizationIds: organizationIds.includes(0)
        ? [Number(this.authService.getOrgID())]
        : organizationIds,
      statusCode: statusCode,
      agentIds: agentIdsArray,
    });

    this.customers
      .getCandidatePurgedReportByAgent(this.utilizationReportClick.value)
      .subscribe((data: any) => {
        this.responseCheck.set('agent', true);

        if (data.data)
          if (data.data.reportResponseDtoList != null) {
            // console.log("Agent result",data);
            this.getAgentUtilizationReport = data.data.reportResponseDtoList;
            let index = this.getCustomerUtilizationReport.find((temp: any) => {
              temp.id == orgID;
            });
            this.company.set(
              this.getCustomerUtilizationReport[index]?.name + ', ' + 'AGENT',
              data.data.reportResponseDtoList
            );
            this.fromDate =
              data.data.fromDate != null ? data.data.fromDate : '';
            this.toDate = data.data.toDate != null ? data.data.toDate : '';
          }


          let allResponseReceived = true;
        for (let entry of this.responseCheck.entries()) {
          if (entry[1] == false) {
            // console.log('response not received yet', entry[0]);
            allResponseReceived = false;
          }
        }

        if (allResponseReceived) {
          this.kyc = true;
        }
      });

    this.utilizationReportFilter.patchValue({
      fromDate: this.setfromDate,
      toDate: this.settoDate,
    });

    if (this.authService.roleMatch(['ROLE_AGENTHR'])) {
      const navURL = 'admin/customerUtilizationAgent/';
      this.navrouter.navigate([navURL], {
        queryParams: {
          fromDate: this.fromDate,
          toDate: this.toDate,
          organizationIds: this.authService.getOrgID(),
          statusCode: 'agent',
        },
      });
    }

    // timer(3000).subscribe(x => {
    //   this.agent_details = [];
    //   this.company.forEach((value: any = [], key: string) => {
    //     var agent_dict: any = {};
    //     // if (key.includes('AGENT')) {
    //       agent_dict['key'] = key;
    //       agent_dict['value'] = value;
    //       this.agent_details.push(agent_dict);
    //     // }
    //   });
    // });
  }

  resetMap() {
    this.kyc = false;
    this.responseCheck.set('ekycReport', false);
    this.responseCheck.set('utilizationReport', false);
    this.responseCheck.set('agent', false);

    this.statusList.forEach((status) => {
      this.responseCheck.set(status, false);
    });
  }

  filterToday() {
    this.customers.setFromDate(this.initToday);
    this.customers.setToDate(this.initToday);
    this.fromDate = this.initToday;
    this.toDate = this.initToday;

    let organizationIds: any = [];
    organizationIds.push(this.custId);
    this.utilizationReportFilter.patchValue({
      fromDate: this.fromDate,
      toDate: this.toDate,
      organizationIds: organizationIds,
    });
    this.onSubmitFilter(this.utilizationReportFilter);
  }

  filterLast7days() {
    var date = new Date();
    date.setDate(date.getDate() - 7);
    var dateString = date.toISOString().split('T')[0];
    let getInputFromDate: any = dateString.split('-');
    let finalInputFromDate =
      getInputFromDate[2] +
      '/' +
      getInputFromDate[1] +
      '/' +
      getInputFromDate[0];
    this.customers.setFromDate(finalInputFromDate);
    this.customers.setToDate(this.initToday);
    this.fromDate = finalInputFromDate;
    this.toDate = this.initToday;

    let organizationIds: any = [];
    organizationIds.push(this.custId);
    this.utilizationReportFilter.patchValue({
      fromDate: this.fromDate,
      toDate: this.toDate,
      organizationIds: organizationIds.includes(0)
        ? [this.authService.getOrgID()]
        : organizationIds,
    });

    this.onSubmitFilter(this.utilizationReportFilter);
  }

  filterLast30days() {
    var date = new Date();
    date.setDate(date.getDate() - 30);
    var dateString = date.toISOString().split('T')[0];
    let getInputFromDate: any = dateString.split('-');
    let finalInputFromDate =
      getInputFromDate[2] +
      '/' +
      getInputFromDate[1] +
      '/' +
      getInputFromDate[0];
    this.customers.setFromDate(finalInputFromDate);
    this.customers.setToDate(this.initToday);

    this.fromDate = finalInputFromDate;
    this.toDate = this.initToday;

    let organizationIds: any = [];
    organizationIds.push(this.custId);
    this.utilizationReportFilter.patchValue({
      fromDate: this.fromDate,
      toDate: this.toDate,
      organizationIds: organizationIds,
    });

    this.onSubmitFilter(this.utilizationReportFilter);
  }

  filterByYear() {
    var date = new Date();
    date.setFullYear(date.getFullYear() - 1); // subtract one year instead of 30 days
    var dateString = date.toISOString().split('T')[0];
    let getInputFromDate: any = dateString.split('-');
    let finalInputFromDate =
      getInputFromDate[2] +
      '/' +
      getInputFromDate[1] +
      '/' +
      getInputFromDate[0];
    this.customers.setFromDate(finalInputFromDate);
    this.customers.setToDate(this.initToday);

    this.fromDate = finalInputFromDate;
    this.toDate = this.initToday;

    let organizationIds: any = [];
    organizationIds.push(this.custId);
    this.utilizationReportFilter.patchValue({
      fromDate: this.fromDate,
      toDate: this.toDate,
      organizationIds: organizationIds,
    });

    this.onSubmitFilter(this.utilizationReportFilter);
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

    let organizationIds: any = [];
    organizationIds.push(this.custId);
    this.utilizationReportFilter.patchValue({
      fromDate: this.fromDate,
      toDate: this.toDate,
      organizationIds: organizationIds,
    });
  
    this.onSubmitFilter(this.utilizationReportFilter);
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

    let organizationIds: any = [];
    organizationIds.push(this.custId);
    this.utilizationReportFilter.patchValue({
      fromDate: this.fromDate,
      toDate: this.toDate,
      organizationIds: organizationIds,
    });
  
    this.onSubmitFilter(this.utilizationReportFilter);
  }
  
  
  

  ngOnInit(): void {}

  private decryptData(encryptedData: string, key: string): string {
    const decodedBytes = atob(encryptedData);
    const keyBytes = key.split('').map(char => char.charCodeAt(0));
    let decryptedData = '';
    for (let i = 0; i < decodedBytes.length; i++) {
      decryptedData += String.fromCharCode(decodedBytes.charCodeAt(i) ^ keyBytes[i % keyBytes.length]);
    }
    console.log("decdsafdfas  _"+decryptedData)
    return decryptedData;
  }

  exportTableToPDF() {
    // this.hideExtra = false;
    // const tableContainer = document.getElementById('excel-table');
    if (this.pdfUrl.length > 0) {
    //   html2canvas(tableContainer).then(canvas => {
    //     const imgData = canvas.toDataURL('image/png');
    //     const pdf = new jsPDF('p', 'mm', 'a4');
    //     const imgProps = pdf.getImageProperties(imgData);
    //     const pdfWidth = pdf.internal.pageSize.getWidth();
    //     const pdfHeight = (imgProps.height * pdfWidth) / imgProps.width;

    //     pdf.addImage(imgData, 'PNG', 0, 0, pdfWidth, pdfHeight);
    //     pdf.save('Candidate Purged Report.pdf');

    //     // this.hideExtra = true;
    //   });

      window.open(this.pdfUrl, '_blank');
    } else {
      Swal.fire({
        title: 'data not found',
        icon: 'warning',
      });
      console.error('Table container not found');
      // this.hideExtra = true;
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
}
