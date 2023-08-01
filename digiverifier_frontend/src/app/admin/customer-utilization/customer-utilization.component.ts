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

const EXCEL_TYPE =
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';

@Component({
  selector: 'app-customer-utilization',
  templateUrl: './customer-utilization.component.html',
  styleUrls: ['./customer-utilization.component.scss'],
})
export class CustomerUtilizationComponent implements OnInit {
  pageTitle = 'Customer Utilization Report';
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
    'REINVITE',
    'INVITATIONEXPIRED',
    'PENDINGAPPROVAL',
    'PENDINGNOW',
    'FINALREPORT',
    'NEWUPLOAD',
    'FINALREPORTTOTAL',
    'PENDINGAPPROVALTOTAL',
    'INVITATIONEXPIREDTOTAL',
    'NEWUPLOADTOTAL',
    'PENDINGNOWTOTAL',
    'PROCESSDECLINEDTOTAL',
    'REINVITETOTAL',
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

  /*name of the excel-file which will be downloaded. */

  // public exportAsExcelFile(json: any[], excelFileName: string): void {
  //   const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(json);
  //   const workbook: XLSX.WorkBook = { Sheets: { 'data': worksheet }, SheetNames: ['data'] };
  //   const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
  //   this.saveAsExcelFile(excelBuffer, excelFileName);
  // }
  // private saveAsExcelFile(buffer: any, fileName: string): void {
  //    const data: Blob = new Blob([buffer], {type: EXCEL_TYPE});
  //    FileSaver.saveAs(data, fileName + ".xlsx");
  // }
  excelBuffer: any;
  fileName = 'export.xlsx';

  exportexcel(): void {
    /* table id is passed over here */
    //  let element = document.getElementById('excel-table');
    //  const ws: XLSX.WorkSheet =XLSX.utils.table_to_sheet(element);
    //  /* generate workbook and add the worksheet */
    //  const wb: XLSX.WorkBook = XLSX.utils.book_new();
    //  XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');
    //  /* save to file */
    //  XLSX.writeFile(wb, this.fileName);
    console.log('Inside excel', this.company);

    const fileName = 'Customer Utilization Report.xlsx';
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
    let REPORTDELIVERED = 0;
    let OLD_REPORTDELIVERED_LEN = 0;
    let NEWUPLOAD = 0;
    let PENDINGNOW = 0;
    let FINALREPORT = 0;
    let PENDINGAPPROVAL = 0;
    let INVITATIONEXPIRED = 0;
    let REINVITE = 0;
    let OLD_NEWUPLOAD_LEN = 0;
    let OLD_PENDINGNOW_LEN = 0;
    let OLD_FINALREPORT_LEN = 0;
    let OLD_PENDINGAPPROVAL_LEN = 0;
    let OLD_INVITATIONEXPIRED_LEN = 0;
    let OLD_REINVITE_LEN = 0;

    // let ws_OVERALLSummary = XLSX.utils.json_to_sheet(this.getCustomerUtilizationReport);
    let element = document.getElementById('excel-table');
    let ws_OVERALLSummary = XLSX.utils.table_to_sheet(element);

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
        delete value[val].colorName;
        delete value[val].aadharNumber;
        delete value[val].aadharName;
        delete value[val].aadharDob;
        delete value[val].aadharGender;
        delete value[val].panDob;
        delete value[val].panName;
        delete value[val].candidateUanName;
        delete value[val].relationship;

        if (value[val].organizationOrganizationName)
          value[val].organizationName = value[val].organizationOrganizationName;
        else value[val].organizationName = key.substring(0, key.indexOf(','));
        delete value[val].organizationOrganizationName;
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
        { wpx: 60 },
        { wpx: 120 },
        { wpx: 110 },
        { wpx: 150 },
        { wpx: 150 },
        { wpx: 100 },
      ];

      if (key.includes('NEWUPLOAD')) {
        console.log('Outside', key, value.length, value);
        if (NEWUPLOAD == 0) {
          console.log('Inside zero', key, value.length, value);
          ws_newupload = XLSX.utils.json_to_sheet(value);

          NEWUPLOAD = 1;
          OLD_NEWUPLOAD_LEN = OLD_NEWUPLOAD_LEN + value.length;

          // new block start
          let tempValueList = value.filter((temp: any) => {
            if(temp.statusName == "QC Pending" || temp.statusName == "Interim Report" || temp.statusName == "Final Report") {
              return temp;
            } 
          })
          console.log('Report delivered', tempValueList)
          ws_ReportDelivered = XLSX.utils.json_to_sheet(tempValueList);

          REPORTDELIVERED = 1;
          OLD_REPORTDELIVERED_LEN = OLD_REPORTDELIVERED_LEN + tempValueList.length;
          // end

        } else {
          console.log('Inside Non zero', key, OLD_NEWUPLOAD_LEN, value);
          XLSX.utils.sheet_add_json(ws_newupload, value, {
            skipHeader: false,
            origin: `A${OLD_NEWUPLOAD_LEN + 2}`,
          });

          OLD_NEWUPLOAD_LEN = OLD_NEWUPLOAD_LEN + value.length;
          console.log('OLD_NEWUPLOAD_LEN', OLD_NEWUPLOAD_LEN);

          // new block start
          let tempValueList = value.filter((temp: any) => {
            if(temp.statusName == 'PENDINGAPPROVAL' || temp.statusName == 'INTERIMREPORT' || temp.statusName == 'FINALREPORT')
              return temp;
          })

          console.log('Report delivered ELSE', tempValueList)
          ws_ReportDelivered = XLSX.utils.sheet_add_json(ws_ReportDelivered, tempValueList);
          OLD_REPORTDELIVERED_LEN = OLD_REPORTDELIVERED_LEN + tempValueList.length;
          // end
        }

        ws_newupload['!cols'] = columnWidths;
        ws_ReportDelivered['!cols'] = columnWidths;
      } else if (key.includes('FINALREPORT')) {
        // console.log("Outside",key, value.length, value)
        if (FINALREPORT == 0) {
          console.log('Inside zero', key, value.length, value);
          ws_FINALREPORT = XLSX.utils.json_to_sheet(value);
          FINALREPORT = 1;
          OLD_FINALREPORT_LEN = OLD_FINALREPORT_LEN + value.length;
        } else {
          console.log('Inside Non zero', key, OLD_FINALREPORT_LEN, value);
          XLSX.utils.sheet_add_json(ws_FINALREPORT, value, {
            skipHeader: false,
            origin: `A${OLD_FINALREPORT_LEN + 2}`,
          });
          OLD_FINALREPORT_LEN = OLD_FINALREPORT_LEN + value.length;
          // console.log("OLD_FINALREPORT_LEN",OLD_FINALREPORT_LEN);
        }
        ws_FINALREPORT['!cols'] = columnWidths;
      } else if (key.includes('PENDINGNOW')) {
        // console.log("Outside",key, value.length, value)
        if (PENDINGNOW == 0) {
          console.log('Inside zero', key, value.length, value);
          ws_PENDINGNOW = XLSX.utils.json_to_sheet(value);
          PENDINGNOW = 1;
          OLD_PENDINGNOW_LEN = OLD_PENDINGNOW_LEN + value.length;
        } else {
          console.log('Inside Non zero', key, OLD_PENDINGNOW_LEN, value);
          XLSX.utils.sheet_add_json(ws_PENDINGNOW, value, {
            skipHeader: false,
            origin: `A${OLD_PENDINGNOW_LEN + 2}`,
          });
          OLD_PENDINGNOW_LEN = OLD_PENDINGNOW_LEN + value.length;
          console.log('OLD_PENDINGNOW_LEN', OLD_PENDINGNOW_LEN);
        }
        ws_PENDINGNOW['!cols'] = columnWidths;
      } else if (key.includes('PENDINGAPPROVAL')) {
        // console.log("Outside",key, value.length, value)
        if (PENDINGAPPROVAL == 0) {
          console.log('Inside zero', key, value.length, value);
          ws_PENDINGAPPROVAL = XLSX.utils.json_to_sheet(value);
          PENDINGAPPROVAL = 1;
          OLD_PENDINGAPPROVAL_LEN = OLD_PENDINGAPPROVAL_LEN + value.length;
        } else {
          console.log('Inside Non zero', key, OLD_PENDINGAPPROVAL_LEN, value);
          XLSX.utils.sheet_add_json(ws_PENDINGAPPROVAL, value, {
            skipHeader: false,
            origin: `A${OLD_PENDINGAPPROVAL_LEN + 2}`,
          });
          OLD_PENDINGAPPROVAL_LEN = OLD_PENDINGAPPROVAL_LEN + value.length;
          console.log('OLD_PENDINGAPPROVAL_LEN', OLD_PENDINGAPPROVAL_LEN);
        }
        ws_PENDINGAPPROVAL['!cols'] = columnWidths;
      } else if (key.includes('INVITATIONEXPIRED')) {
        // console.log("Outside",key, value.length, value)
        if (INVITATIONEXPIRED == 0) {
          console.log('Inside zero', key, value.length, value);
          ws_INVITATIONEXPIRED = XLSX.utils.json_to_sheet(value);
          INVITATIONEXPIRED = 1;
          OLD_INVITATIONEXPIRED_LEN = OLD_INVITATIONEXPIRED_LEN + value.length;
        } else {
          console.log('Inside Non zero', key, OLD_INVITATIONEXPIRED_LEN, value);
          XLSX.utils.sheet_add_json(ws_INVITATIONEXPIRED, value, {
            skipHeader: false,
            origin: `A${OLD_INVITATIONEXPIRED_LEN + 2}`,
          });
          OLD_INVITATIONEXPIRED_LEN = OLD_INVITATIONEXPIRED_LEN + value.length;
          console.log('OLD_INVITATIONEXPIRED_LEN', OLD_INVITATIONEXPIRED_LEN);
        }

        ws_INVITATIONEXPIRED['!cols'] = columnWidths;
      } else if (key.includes('REINVITE')) {
        // console.log("Outside",key, value.length, value)
        if (REINVITE == 0) {
          console.log('Inside zero', key, value.length, value);
          ws_REINVITE = XLSX.utils.json_to_sheet(value);
          REINVITE = 1;
          OLD_REINVITE_LEN = OLD_REINVITE_LEN + value.length;
        } else {
          console.log('Inside Non zero', key, OLD_REINVITE_LEN, value);
          XLSX.utils.sheet_add_json(ws_REINVITE, value, {
            skipHeader: false,
            origin: `A${OLD_REINVITE_LEN + 2}`,
          });
          OLD_REINVITE_LEN = OLD_REINVITE_LEN + value.length;
          console.log('OLD_REINVITE_LEN', OLD_REINVITE_LEN);
        }
        ws_REINVITE['!cols'] = columnWidths;
      } else if (key.includes('AGENT')) {
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
        ];
        ws_OVERALLSummary['!cols'] = ws_OVERALLSummarycolumnWidths;
      }
    });

    // Define the default font size
    const defaultFontSize = 10;

    // Set the font size for all cells
    // Object.keys(ws_OVERALLSummary).forEach((cell) => {
    //   if (cell !== '!ref') {
    //     ws_OVERALLSummary[cell].s = { font: { sz: defaultFontSize } };
    //   }
    // });
    // Object.keys(ws_newupload).forEach((cell) => {
    //   if (cell !== '!ref') {
    //     ws_newupload[cell].s = { font: { sz: defaultFontSize } };
    //   }
    // });
    // Object.keys(ws_FINALREPORT).forEach((cell) => {
    //   if (cell !== '!ref') {
    //     ws_FINALREPORT[cell].s = { font: { sz: defaultFontSize } };
    //   }
    // });
    // Object.keys(ws_PENDINGNOW).forEach((cell) => {
    //   if (cell !== '!ref') {
    //     ws_PENDINGNOW[cell].s = { font: { sz: defaultFontSize } };
    //   }
    // });
    // Object.keys(ws_PENDINGAPPROVAL).forEach((cell) => {
    //   if (cell !== '!ref') {
    //     ws_PENDINGAPPROVAL[cell].s = { font: { sz: defaultFontSize } };
    //   }
    // });
    // Object.keys(ws_INVITATIONEXPIRED).forEach((cell) => {
    //   if (cell !== '!ref') {
    //     ws_INVITATIONEXPIRED[cell].s = { font: { sz: defaultFontSize } };
    //   }
    // });
    // Object.keys(ws_REINVITE).forEach((cell) => {
    //   if (cell !== '!ref') {
    //     ws_REINVITE[cell].s = { font: { sz: defaultFontSize } };
    //   }
    // });
    // Object.keys(ws_ekycReport).forEach((cell) => {
    //   if (cell !== '!ref') {
    //     ws_ekycReport[cell].s = { font: { sz: defaultFontSize } };
    //   }
    // });

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

    XLSX.utils.book_append_sheet(wb, ws_OVERALLSummary, 'OverallSummary');
    XLSX.utils.book_append_sheet(wb, ws_newupload, 'NewUpload');
    XLSX.utils.book_append_sheet(wb, ws_FINALREPORT, 'FinalReport');
    XLSX.utils.book_append_sheet(wb, ws_PENDINGNOW, 'PendingB4QC');
    XLSX.utils.book_append_sheet(wb, ws_PENDINGAPPROVAL, 'PendingApproval');
    XLSX.utils.book_append_sheet(wb, ws_INVITATIONEXPIRED, 'InvitationExpired');
    XLSX.utils.book_append_sheet(wb, ws_REINVITE, 'Reinvite');
    XLSX.utils.book_append_sheet(wb, ws_ekycReport, 'EkycReport');
    XLSX.utils.book_append_sheet(wb, ws_ReportDelivered, 'ReportDelivered');

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

  constructor(
    private customers: CustomerService,
    private navrouter: Router,
    calendar: NgbCalendar,
    private route: ActivatedRoute,
    public authService: AuthenticationService,
    private loaderService: LoaderService
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
      userId: localStorage.getItem('userId'),
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
    } else {
      this.resetMap();

      this.customers.posteKycReport(rportData).subscribe((data: any) => {
        if (data.data) {
          this.geteKycReport = data.data.candidateDetailsDto;
          this.orgKycReport = data.data.candidateDetailsDto;

          let no = 1;
          for (let item in this.geteKycReport) {
            delete this.geteKycReport[item]['candidateCode'];
            delete this.geteKycReport[item]['createdByUserFirstName'];
            delete this.geteKycReport[item]['candidateId'];
            delete this.geteKycReport[item]['createdByUserLastName'];
            delete this.geteKycReport[item]['candidateName'];
            delete this.geteKycReport[item]['dateOfBirth'];
            delete this.geteKycReport[item]['contactNumber'];
            delete this.geteKycReport[item]['emailId'];
            this.geteKycReport[item]['SNo'] = no;
            no++;
            this.geteKycReport[item]['Applicant ID'] =
              this.geteKycReport[item]['applicantId'];
            delete this.geteKycReport[item]['applicantId'];
            delete this.geteKycReport[item]['experienceInMonth'];
            delete this.geteKycReport[item]['statusName'];
            this.geteKycReport[item]['Status Date'] =
              this.geteKycReport[item]['statusDate'];
            delete this.geteKycReport[item]['statusDate'];
            delete this.geteKycReport[item]['createdOn'];
            this.geteKycReport[item]['PAN'] =
              this.geteKycReport[item]['panNumber'];
            delete this.geteKycReport[item]['panNumber'];
            this.geteKycReport[item]['Name as per PAN'] =
              this.geteKycReport[item]['panName'];
            delete this.geteKycReport[item]['panName'];
            this.geteKycReport[item]['PAN DOB'] =
              this.geteKycReport[item]['panDob'];
            delete this.geteKycReport[item]['panDob'];
            delete this.geteKycReport[item]['statusDate'];
            delete this.geteKycReport[item]['currentStatusDate'];
            delete this.geteKycReport[item]['colorName'];

            delete this.geteKycReport[item]['dateOfEmailInvite'];
            delete this.geteKycReport[item]['numberofexpiredCount'];
            delete this.geteKycReport[item]['reinviteCount'];
            delete this.geteKycReport[item]['clearCount'];
            delete this.geteKycReport[item]['inProgressCount'];
            delete this.geteKycReport[item]['inSufficiencyCount'];
            delete this.geteKycReport[item]['majorDiscrepancyCount'];
            delete this.geteKycReport[item]['numberofexpiredCount'];
            delete this.geteKycReport[item]['minorDiscrepancyCount'];
            delete this.geteKycReport[item]['unableToVerifyCount'];

            this.geteKycReport[item].UAN =
              this.geteKycReport[item]['candidateUan'];
            delete this.geteKycReport[item]['candidateUan'];
            delete this.geteKycReport[item]['organizationOrganizationName'];
            this.geteKycReport[item]['Name as per UAN'] =
              this.geteKycReport[item]['candidateUanName'];
            delete this.geteKycReport[item]['candidateUanName'];
            this.geteKycReport[item]['Full Aadhaar No'] =
              this.geteKycReport[item]['aadharNumber'];
            delete this.geteKycReport[item]['aadharNumber'];
            this.geteKycReport[item]['Name as per Aadhaar'] =
              this.geteKycReport[item]['aadharName'];
            delete this.geteKycReport[item]['aadharName'];
            this.geteKycReport[item]['Aadhar DOB'] =
              this.geteKycReport[item]['aadharDob'];
            delete this.geteKycReport[item]['aadharDob'];
            delete this.geteKycReport[item]['relationName'];
            delete this.geteKycReport[item]['relationship'];
            if (this.geteKycReport[item]['aadharFatherName'] != null) {
              var str = this.geteKycReport[item]['aadharFatherName'];
              var splitted = str.split(' ', 3);
              console.log(splitted.length, 'length');
              if (splitted.length == 3) {
                this.geteKycReport[item]['Relative'] = splitted[2];
                delete this.geteKycReport[item]['aadharFatherName'];
                if (splitted[0] === 'S/O') {
                  this.geteKycReport[item]['Relationship'] = 'Father';
                } else if (splitted[0] === 'W/O') {
                  this.geteKycReport[item]['Relationship'] = 'Husband';
                } else if (splitted[0] === 'D/O:') {
                  this.geteKycReport[item]['Relationship'] = 'Father';
                } else if (splitted[0] === 'S/O:') {
                  this.geteKycReport[item]['Relationship'] = 'Father';
                } else if (splitted[0] === 'D/O') {
                  this.geteKycReport[item]['Relationship'] = 'Father';
                } else {
                  this.geteKycReport[item]['Relationship'] = '';
                }
              } else {
                if (splitted[1]?.length != 1) {
                  this.geteKycReport[item]['Relative'] = splitted[1];
                  delete this.geteKycReport[item]['aadharFatherName'];
                  if (splitted[0] === 'S/O') {
                    this.geteKycReport[item]['Relationship'] = 'Father';
                  } else if (splitted[0] === 'W/O') {
                    this.geteKycReport[item]['Relationship'] = 'Husband';
                  } else if (splitted[0] === 'D/O:') {
                    this.geteKycReport[item]['Relationship'] = 'Father';
                  } else if (splitted[0] === 'D/O') {
                    this.geteKycReport[item]['Relationship'] = 'Father';
                  } else if (splitted[0] === 'S/O:') {
                    this.geteKycReport[item]['Relationship'] = 'Father';
                  } else {
                    this.geteKycReport[item]['Relationship'] = '';
                  }
                } else {
                  this.geteKycReport[item]['Relative'] = splitted[0];
                  delete this.geteKycReport[item]['aadharFatherName'];
                  this.geteKycReport[item]['Relationship'] = '';
                }
              }
            } else {
              delete this.geteKycReport[item]['aadharFatherName'];
            }

            if (this.geteKycReport[item]['aadharGender'] != null) {
              var str = this.geteKycReport[item]['aadharGender'];
              if (str === 'F') {
                this.geteKycReport[item].Gender = 'Female';
                delete this.geteKycReport[item]['aadharGender'];
              } else if (str === 'M') {
                this.geteKycReport[item].Gender = 'Male';
                delete this.geteKycReport[item]['aadharGender'];
              } else {
                this.geteKycReport[item].Gender =
                  this.geteKycReport[item]['aadharGender'];
                delete this.geteKycReport[item]['aadharGender'];
              }
            } else {
              this.geteKycReport[item].Gender = '';
              delete this.geteKycReport[item]['aadharGender'];
            }
            this.geteKycReport[item]['Address'] =
              this.geteKycReport[item]['address'];
            delete this.geteKycReport[item]['address'];
            // this.kyc = true;
          }
        }

        this.responseCheck.set('ekycReport', true);
        // let allResponseReceived = true;
        // for (let entry of this.responseCheck.entries()) {
        //   if(entry[1] == false) {
        //     console.log('response not received yet', entry[0])
        //     allResponseReceived = false;
        //   }
        // }

        // if(allResponseReceived) {
        //   this.kyc = true;
        //   this.loaderService.removeQueue();
        // }
      });
      let organizationIds: any = [];
      this.customers
        .postCustomerUtilizationReport({
          fromDate: this.fromDate,
          toDate: this.toDate,
          organizationIds: organizationIds.includes(0)
            ? [Number(localStorage.getItem('orgID'))]
            : organizationIds,
        })
        .subscribe((data: any) => {
          if (data.data) {
            this.getCustomerUtilizationReport =
              data.data?.reportResponseDtoList;

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
            //   console.log(
            //     'getfromDate, gettoDate',
            //     this.getMinDate,
            //     this.settoDate,
            //     this.fromDate,
            //     this.toDate
            //   );

            this.start_date =
              data.data.fromDate != null
                ? data.data.fromDate.split('-').join('/')
                : '';
            this.end_date =
              data.data.toDate != null
                ? data.data.toDate.split('-').join('/')
                : '';
          }

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
          // this.loaderService.hide();
          // this.navrouter.navigate([loader]);

          //   console.log(
          //     'getCustomerUtilizationReport',
          //     this.getCustomerUtilizationReport
          //   );
          //   let company = new Map<string, {}>();
          //   for (let item in this.getCustomerUtilizationReport) {
          //     var features: any = {};
          //     console.log('item', this.getCustomerUtilizationReport[item].name);

          //     Object.keys(this.getCustomerUtilizationReport[item]).find((key) => {
          //       if (key.includes('Code')) {
          //         const statusCode = this.getCustomerUtilizationReport[item][key];

          //         const agentIds = this.route.snapshot.queryParamMap.get('agentIds');
          //         const isAgent = this.route.snapshot.queryParamMap.get('isAgent');

          //         let agentIdsArray: any = [];
          //         agentIdsArray.push(agentIds);

          //         if (isAgent == 'true') {
          //           this.utilizationReportClick.patchValue({
          //             fromDate:
          //               data.data.fromDate != null
          //                 ? data.data.fromDate.split('-').join('/')
          //                 : '',
          //             toDate:
          //               data.data.toDate != null
          //                 ? data.data.toDate.split('-').join('/')
          //                 : '',
          //             organizationIds: [this.getCustomerUtilizationReport[item].id],
          //             statusCode: statusCode,
          //             agentIds: agentIdsArray,
          //           });
          //         } else {
          //           this.utilizationReportClick.patchValue({
          //             fromDate:
          //               data.data.fromDate != null
          //                 ? data.data.fromDate.split('-').join('/')
          //                 : '',
          //             toDate:
          //               data.data.toDate != null
          //                 ? data.data.toDate.split('-').join('/')
          //                 : '',
          //             organizationIds: [this.getCustomerUtilizationReport[item].id],
          //             statusCode: statusCode,
          //             agentIds: [],
          //           });
          //         }

          //         this.customers
          //           .getCanididateDetailsByStatus(this.utilizationReportClick.value)
          //           .subscribe((result: any) => {
          //             if (
          //               result['data']['organizationName'] != null &&
          //               result.data.candidateDetailsDto.length > 1
          //             ) {

          //               this.getCandidateUtilizationReport =
          //                 result.data.candidateDetailsDto;
          //               features[result['data']['statusCode']] =
          //                 result.data.candidateDetailsDto;
          //               this.company.set(
          //                 result['data']['organizationName'] +
          //                   ', ' +
          //                   result['data']['statusCode'],
          //                 result.data.candidateDetailsDto
          //               );
          //             }
          //           });
          //       } else if (key.includes('agentCount')) {
          //         const statusCode = 'agent';
          //         console.log('statusCode type *****', typeof statusCode, statusCode);
          //         const agentIds = this.route.snapshot.queryParamMap.get('agentIds');
          //         const isAgent = 'true';
          //         console.log('agentIds isAgent', agentIds, isAgent);
          //         let agentIdsArray: any = [];
          //         agentIdsArray.push(agentIds);

          //         if (isAgent == 'true') {
          //           this.utilizationReportClick.patchValue({
          //             fromDate:
          //               data.data.fromDate != null
          //                 ? data.data.fromDate.split('-').join('/')
          //                 : '',
          //             toDate:
          //               data.data.toDate != null
          //                 ? data.data.toDate.split('-').join('/')
          //                 : '',
          //             organizationIds: [this.getCustomerUtilizationReport[item].id],
          //             statusCode: statusCode,
          //             agentIds: agentIdsArray,
          //           });
          //         } else {
          //           this.utilizationReportClick.patchValue({
          //             fromDate:
          //               data.data.fromDate != null
          //                 ? data.data.fromDate.split('-').join('/')
          //                 : '',
          //             toDate:
          //               data.data.toDate != null
          //                 ? data.data.toDate.split('-').join('/')
          //                 : '',
          //             organizationIds: [this.getCustomerUtilizationReport[item].id],
          //             statusCode: statusCode,
          //             agentIds: [],
          //           });
          //         }

          //         this.customers
          //           .getCustomerUtilizationReportByAgent(
          //             this.utilizationReportClick.value
          //           )
          //           .subscribe((data: any) => {
          //             if (data.data.reportResponseDtoList != null) {
          //               console.log('Agent result', data);
          //               this.getAgentUtilizationReport =
          //                 data.data.reportResponseDtoList;
          //               this.company.set(
          //                 this.getCustomerUtilizationReport[item].name +
          //                   ', ' +
          //                   'AGENT',
          //                 data.data.reportResponseDtoList
          //               );

          //             }
          //           });
          //       }
          //     });
          //   }
          // timer(3000).subscribe(x => { console.log("Initial",this.company);
          // timer().subscribe((x) => {
          //   console.log('Initial', this.company);
          //   this.agent_details = [];
          //   this.company.forEach((value: any = [], key: string) => {
          //     var agent_dict: any = {};
          //     if (key.includes('AGENT')) {
          //       agent_dict['key'] = key;
          //       agent_dict['value'] = value;
          //       this.agent_details.push(agent_dict);
          //     }
          //   });
          //   console.log('this.agent_details', this.agent_details);
          // });
          //Excel end

          this.utilizationReportFilter.patchValue({
            fromDate: this.setfromDate,
            toDate: this.settoDate,
          });

          // this.fromDate = data.data.fromDate != null ? data.data.fromDate : '';
          // this.toDate = data.data.toDate != null ? data.data.toDate : '';

          // if (authService.roleMatch(['ROLE_AGENTHR'])) {
          //   const navURL = 'admin/customerUtilizationAgent/';
          //   this.navrouter.navigate([navURL], {
          //     queryParams: {
          //       fromDate: this.fromDate,
          //       toDate: this.toDate,
          //       organizationIds: localStorage.getItem('orgID'),
          //       statusCode: 'agent',
          //     },
          //   });
          // }
        });

      let orgID = localStorage.getItem('orgID');
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
            organizationIds: [localStorage.getItem('orgID')],
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

        this.customers
          .getCanididateDetailsByStatus(this.utilizationReportClick.value)
          .subscribe((result: any) => {
            this.responseCheck.set(status, true);
            let allResponseReceived = true;
            for (let entry of this.responseCheck.entries()) {
              if (entry[1] == false) {
                console.log('response not received yet', entry[0], result);
                allResponseReceived = false;
              }
            }

            if (allResponseReceived) {
              this.kyc = true;
            }

            // console.log(
            //   result['data']['organizationName'],
            //   result.data.candidateDetailsDto.length
            // );
            if (
              result['data']['organizationName'] != null &&
              result.data.candidateDetailsDto.length > 1
            ) {
              this.getCandidateUtilizationReport =
                result.data.candidateDetailsDto;
              features[result['data']['statusCode']] =
                result.data.candidateDetailsDto;
              this.company.set(
                result['data']['organizationName'] +
                  ', ' +
                  result['data']['statusCode'],
                result.data.candidateDetailsDto
              );
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
        fromDate: fromDate != null ? fromDate.split('-').join('/') : '',
        toDate: toDate != null ? toDate.split('-').join('/') : '',
        organizationIds: [orgID],
        statusCode: statusCode,
        agentIds: agentIdsArray,
      });

      this.customers
        .getCustomerUtilizationReportByAgent(this.utilizationReportClick.value)
        .subscribe((data: any) => {
          if (data.data) {
            if (data.data.reportResponseDtoList != null) {
              // console.log("Agent result",data);
              this.getAgentUtilizationReport = data.data.reportResponseDtoList;
              let index = this.getCustomerUtilizationReport.find(
                (temp: any) => {
                  temp.id == orgID;
                }
              );
              this.company.set(
                this.getCustomerUtilizationReport[index]?.name + ', ' + 'AGENT',
                data.data.reportResponseDtoList
              );
              this.fromDate =
                data.data.fromDate != null ? data.data.fromDate : '';
              this.toDate = data.data.toDate != null ? data.data.toDate : '';
            }
          }

          this.responseCheck.set('agent', true);
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

      if (authService.roleMatch(['ROLE_AGENTHR'])) {
        const navURL = 'admin/customerUtilizationAgent/';
        this.navrouter.navigate([navURL], {
          queryParams: {
            fromDate: this.fromDate,
            toDate: this.toDate,
            organizationIds: localStorage.getItem('orgID'),
            statusCode: 'agent',
          },
        });
      }
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
        ? [Number(localStorage.getItem('orgID'))]
        : organizationIds,
    });
    console.warn('FROMDATE xyz:::', this.fromDate);
    console.warn('TODATE xyz:::', this.toDate);

    this.customers
      .postCustomerUtilizationReport({
        fromDate: this.fromDate,
        toDate: this.toDate,
        organizationIds: organizationIds.includes(0)
          ? [Number(localStorage.getItem('orgID'))]
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
          // console.log(
          //   'this.getCustomerUtilizationReport',
          //   this.getCustomerUtilizationReport
          // );
          this.start_date =
            data.data.fromDate != null
              ? data.data.fromDate.split('-').join('/')
              : '';
          this.end_date =
            data.data.toDate != null
              ? data.data.toDate.split('-').join('/')
              : '';

          // this.loaderService.hide();
          // this.loaderService.isLoading
          // const loader = 'loader';
          // this.navrouter.navigate([loader]);
          // //merge excel start
          // this.company = new Map<string, {}>();
          // for (let item in this.getCustomerUtilizationReport) {
          //   var features: any = {};
          //   // console.log("item",this.getCustomerUtilizationReport[item].name);

          //   Object.keys(this.getCustomerUtilizationReport[item]).find((key) => {
          //     if (key.includes('Code')) {
          //       const statusCode = this.getCustomerUtilizationReport[item][key];
          //       // console.log("statusCode type *****",typeof(statusCode), statusCode);
          //       const agentIds =
          //         this.route.snapshot.queryParamMap.get('agentIds');
          //       const isAgent =
          //         this.route.snapshot.queryParamMap.get('isAgent');
          //       // console.log("agentIds isAgent",agentIds,isAgent);
          //       let agentIdsArray: any = [];
          //       agentIdsArray.push(agentIds);

          //       if (isAgent == 'true') {
          //         this.utilizationReportClick.patchValue({
          //           fromDate:
          //             this.fromDate != null
          //               ? this.fromDate.split('-').join('/')
          //               : '',
          //           toDate:
          //             this.toDate != null
          //               ? this.toDate.split('-').join('/')
          //               : '',
          //           organizationIds: [
          //             this.getCustomerUtilizationReport[item].id,
          //           ],
          //           statusCode: statusCode,
          //           agentIds: agentIdsArray,
          //         });
          //       } else {
          //         this.utilizationReportClick.patchValue({
          //           fromDate:
          //             this.fromDate != null
          //               ? this.fromDate.split('-').join('/')
          //               : '',
          //           toDate:
          //             this.toDate != null
          //               ? this.toDate.split('-').join('/')
          //               : '',
          //           organizationIds: [
          //             this.getCustomerUtilizationReport[item].id,
          //           ],
          //           statusCode: statusCode,
          //           agentIds: [],
          //         });
          //       }
          //       // console.log("this.customers.getCanididateDetailsByStatus",this.utilizationReportClick);
          //       this.customers
          //         .getCanididateDetailsByStatus(
          //           this.utilizationReportClick.value
          //         )
          //         .subscribe((result: any) => {
          //           if (
          //             result['data']['organizationName'] != null &&
          //             result.data.candidateDetailsDto.length > 1
          //           ) {
          //             // console.log("*************************************");
          //             // console.log("organizationName",result['data']['organizationName']);
          //             // console.log("Fetaure name",result['data']['statusCode']);
          //             // console.log("Data",result, result.data.candidateDetailsDto.length);

          //             this.getCandidateUtilizationReport =
          //               result.data.candidateDetailsDto;
          //             features[result['data']['statusCode']] =
          //               result.data.candidateDetailsDto;
          //             // console.log("features",features);
          //             this.company.set(
          //               result['data']['organizationName'] +
          //                 ', ' +
          //                 result['data']['statusCode'],
          //               result.data.candidateDetailsDto
          //             );
          //           }
          //         });
          //     } else if (key.includes('agentCount')) {
          //       const statusCode = 'agent';
          //       // console.log("statusCode type *****",typeof(statusCode), statusCode);
          //       const agentIds =
          //         this.route.snapshot.queryParamMap.get('agentIds');
          //       const isAgent = 'true';
          //       // console.log("agentIds isAgent",agentIds,isAgent);
          //       let agentIdsArray: any = [];
          //       agentIdsArray.push(agentIds);

          //       if (isAgent == 'true') {
          //         this.utilizationReportClick.patchValue({
          //           fromDate:
          //             this.fromDate != null
          //               ? this.fromDate.split('-').join('/')
          //               : '',
          //           toDate:
          //             this.toDate != null
          //               ? this.toDate.split('-').join('/')
          //               : '',
          //           organizationIds: [
          //             this.getCustomerUtilizationReport[item].id,
          //           ],
          //           statusCode: statusCode,
          //           agentIds: agentIdsArray,
          //         });
          //       } else {
          //         this.utilizationReportClick.patchValue({
          //           fromDate:
          //             this.fromDate != null
          //               ? this.fromDate.split('-').join('/')
          //               : '',
          //           toDate:
          //             this.toDate != null
          //               ? this.toDate.split('-').join('/')
          //               : '',
          //           organizationIds: [
          //             this.getCustomerUtilizationReport[item].id,
          //           ],
          //           statusCode: statusCode,
          //           agentIds: [],
          //         });
          //       }

          //       this.customers
          //         .getCustomerUtilizationReportByAgent(
          //           this.utilizationReportClick.value
          //         )
          //         .subscribe((data: any) => {
          //           if (data.data.reportResponseDtoList != null) {
          //             // console.log("Agent result",data);
          //             this.getAgentUtilizationReport =
          //               data.data.reportResponseDtoList;
          //             this.company.set(
          //               this.getCustomerUtilizationReport[item].name +
          //                 ', ' +
          //                 'AGENT',
          //               data.data.reportResponseDtoList
          //             );
          //           }
          //         });
          //     }
          //   });
          //   // console.log("this.company name",this.getCustomerUtilizationReport[item].name);
          //   // timer(10000).subscribe(x => { console.log("your_action_code_here",this.company) })
          //   // break;
          // }

          // console.log('filter this.company', this.company);
          // // timer(5000).subscribe(x => { console.log("your_action_code_here",this.company);
          // timer().subscribe((x) => {
          //   console.log('your_action_code_here', this.company);
          //   let count = 0;
          //   this.company.forEach((value: any = [], key: string) => {
          //     var agent_dict: any = {};
          //     if (key.includes('AGENT') && count != 0) {
          //       agent_dict['key'] = key;
          //       agent_dict['value'] = value;
          //       this.agent_details.push(agent_dict);
          //     }
          //     count += 1;
          //   });
          //   console.log('this.agent_details', this.agent_details);
          // });

          // //merge excel end

          // let getfromDate = data.data.fromDate.split('/');
          // this.setfromDate = {
          //   day: +getfromDate[0],
          //   month: +getfromDate[1],
          //   year: +getfromDate[2],
          // };
          // let gettoDate = data.data.toDate.split('/');
          // this.settoDate = {
          //   day: +gettoDate[0],
          //   month: +gettoDate[1],
          //   year: +gettoDate[2],
          // };
          // this.utilizationReportFilter.patchValue({
          //   fromDate: this.setfromDate,
          //   toDate: this.settoDate,
          // });
        } else {
          Swal.fire({
            title: data.message,
            icon: 'warning',
          });
        }
      });

    let rportData = {
      userId: localStorage.getItem('userId'),
      fromDate: this.fromDate,
      toDate: this.toDate,
      organizationIds: organizationIds.includes(0)
        ? [Number(localStorage.getItem('orgID'))]
        : organizationIds,
    };

    console.log('ekycReport request body', rportData);
    this.customers.posteKycReport(rportData).subscribe((data: any) => {

      this.responseCheck.set('ekycReport', true);

      if (data.data) {
        this.geteKycReport = data.data.candidateDetailsDto;
        this.orgKycReport = data.data.candidateDetailsDto;
        console.log(data, '------------------------------------');
        let no = 1;
        for (let item in this.geteKycReport) {
          delete this.geteKycReport[item]['candidateCode'];
          delete this.geteKycReport[item]['createdByUserFirstName'];
          delete this.geteKycReport[item]['candidateId'];
          delete this.geteKycReport[item]['createdByUserLastName'];
          delete this.geteKycReport[item]['candidateName'];
          delete this.geteKycReport[item]['dateOfBirth'];
          delete this.geteKycReport[item]['contactNumber'];
          delete this.geteKycReport[item]['emailId'];
          this.geteKycReport[item]['SNo'] = no;
          no++;
          this.geteKycReport[item]['Applicant ID'] =
            this.geteKycReport[item]['applicantId'];
          delete this.geteKycReport[item]['applicantId'];
          delete this.geteKycReport[item]['experienceInMonth'];
          delete this.geteKycReport[item]['statusName'];
          this.geteKycReport[item]['Status Date'] =
            this.geteKycReport[item]['statusDate'];
          delete this.geteKycReport[item]['statusDate'];
          delete this.geteKycReport[item]['createdOn'];
          this.geteKycReport[item]['PAN'] =
            this.geteKycReport[item]['panNumber'];
          delete this.geteKycReport[item]['panNumber'];
          this.geteKycReport[item]['Name as per PAN'] =
            this.geteKycReport[item]['panName'];
          delete this.geteKycReport[item]['panName'];
          this.geteKycReport[item]['PAN DOB'] =
            this.geteKycReport[item]['panDob'];
          delete this.geteKycReport[item]['panDob'];
          delete this.geteKycReport[item]['statusDate'];
          delete this.geteKycReport[item]['currentStatusDate'];
          delete this.geteKycReport[item]['colorName'];

          delete this.geteKycReport[item]['dateOfEmailInvite'];
          delete this.geteKycReport[item]['numberofexpiredCount'];
          delete this.geteKycReport[item]['reinviteCount'];
          delete this.geteKycReport[item]['clearCount'];
          delete this.geteKycReport[item]['inProgressCount'];
          delete this.geteKycReport[item]['inSufficiencyCount'];
          delete this.geteKycReport[item]['majorDiscrepancyCount'];
          delete this.geteKycReport[item]['numberofexpiredCount'];
          delete this.geteKycReport[item]['minorDiscrepancyCount'];
          delete this.geteKycReport[item]['unableToVerifyCount'];

          this.geteKycReport[item].UAN =
            this.geteKycReport[item]['candidateUan'];
          delete this.geteKycReport[item]['candidateUan'];
          delete this.geteKycReport[item]['organizationOrganizationName'];
          this.geteKycReport[item]['Name as per UAN'] =
            this.geteKycReport[item]['candidateUanName'];
          delete this.geteKycReport[item]['candidateUanName'];
          this.geteKycReport[item]['Full Aadhaar No'] =
            this.geteKycReport[item]['aadharNumber'];
          delete this.geteKycReport[item]['aadharNumber'];
          this.geteKycReport[item]['Name as per Aadhaar'] =
            this.geteKycReport[item]['aadharName'];
          delete this.geteKycReport[item]['aadharName'];
          this.geteKycReport[item]['Aadhar DOB'] =
            this.geteKycReport[item]['aadharDob'];
          delete this.geteKycReport[item]['aadharDob'];
          delete this.geteKycReport[item]['relationName'];
          delete this.geteKycReport[item]['relationship'];
          if (this.geteKycReport[item]['aadharFatherName'] != null) {
            var str = this.geteKycReport[item]['aadharFatherName'];
            var splitted = str.split(' ', 3);

            if (splitted.length == 3) {
              this.geteKycReport[item]['Relative'] = splitted[2];
              delete this.geteKycReport[item]['aadharFatherName'];
              if (splitted[0] === 'S/O') {
                this.geteKycReport[item]['Relationship'] = 'Father';
              } else if (splitted[0] === 'W/O') {
                this.geteKycReport[item]['Relationship'] = 'Husband';
              } else if (splitted[0] === 'D/O:') {
                this.geteKycReport[item]['Relationship'] = 'Father';
              } else if (splitted[0] === 'S/O:') {
                this.geteKycReport[item]['Relationship'] = 'Father';
              } else if (splitted[0] === 'D/O') {
                this.geteKycReport[item]['Relationship'] = 'Father';
              } else {
                this.geteKycReport[item]['Relationship'] = '';
              }
            } else {
              if (splitted[1]?.length != 1) {
                this.geteKycReport[item]['Relative'] = splitted[1];
                delete this.geteKycReport[item]['aadharFatherName'];
                if (splitted[0] === 'S/O') {
                  this.geteKycReport[item]['Relationship'] = 'Father';
                } else if (splitted[0] === 'W/O') {
                  this.geteKycReport[item]['Relationship'] = 'Husband';
                } else if (splitted[0] === 'D/O:') {
                  this.geteKycReport[item]['Relationship'] = 'Father';
                } else if (splitted[0] === 'D/O') {
                  this.geteKycReport[item]['Relationship'] = 'Father';
                } else if (splitted[0] === 'S/O:') {
                  this.geteKycReport[item]['Relationship'] = 'Father';
                } else {
                  this.geteKycReport[item]['Relationship'] = '';
                }
              } else {
                this.geteKycReport[item]['Relative'] = splitted[0];
                delete this.geteKycReport[item]['aadharFatherName'];
                this.geteKycReport[item]['Relationship'] = '';
              }
            }
          } else {
            delete this.geteKycReport[item]['aadharFatherName'];
          }

          if (this.geteKycReport[item]['aadharGender'] != null) {
            var str = this.geteKycReport[item]['aadharGender'];
            if (str === 'F') {
              this.geteKycReport[item].Gender = 'Female';
              delete this.geteKycReport[item]['aadharGender'];
            } else if (str === 'M') {
              this.geteKycReport[item].Gender = 'Male';
              delete this.geteKycReport[item]['aadharGender'];
            } else {
              this.geteKycReport[item].Gender =
                this.geteKycReport[item]['aadharGender'];
              delete this.geteKycReport[item]['aadharGender'];
            }
          } else {
            this.geteKycReport[item].Gender = '';
            delete this.geteKycReport[item]['aadharGender'];
          }
          this.geteKycReport[item]['Address'] =
            this.geteKycReport[item]['address'];
          delete this.geteKycReport[item]['address'];
          // this.kyc = true;
        }
      }

      // let allResponseReceived = true;
      // for (let entry of this.responseCheck.entries()) {
      //   if(entry[1] == false) {
      //     allResponseReceived = false;
      //   }
      // }

      // if(allResponseReceived) {
      //   this.kyc = true;
      //   this.loaderService.removeQueue();
      // }
    });

    let orgID = localStorage.getItem('orgID');
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
            ? [Number(localStorage.getItem('orgID'))]
            : organizationIds,
          statusCode: statusCode,
          agentIds: agentIdsArray,
        });
      } else {
        this.utilizationReportClick.patchValue({
          fromDate: this.fromDate,
          toDate: this.toDate,
          organizationIds: organizationIds.includes(0)
            ? [Number(localStorage.getItem('orgID'))]
            : organizationIds,
          statusCode: statusCode,
          agentIds: [],
        });
      }

      this.customers
        .getCanididateDetailsByStatus(this.utilizationReportClick.value)
        .subscribe((result: any) => {
          // console.log(
          //   result['data']['organizationName'],
          //   result.data.candidateDetailsDto.length
          // );

          this.responseCheck.set(status, true); 
          if (
            result['data']['organizationName'] != null &&
            result.data?.candidateDetailsDto?.length > 1
          ) {
            this.getCandidateUtilizationReport =
              result.data.candidateDetailsDto;
            features[result['data']['statusCode']] =
              result.data.candidateDetailsDto;
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
        ? [Number(localStorage.getItem('orgID'))]
        : organizationIds,
      statusCode: statusCode,
      agentIds: agentIdsArray,
    });

    this.customers
      .getCustomerUtilizationReportByAgent(this.utilizationReportClick.value)
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
          organizationIds: localStorage.getItem('orgID'),
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
        ? [localStorage.getItem('orgID')]
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
}
