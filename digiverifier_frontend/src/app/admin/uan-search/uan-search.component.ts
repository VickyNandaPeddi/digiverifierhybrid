import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ModalDismissReasons, NgbCalendar, NgbDate, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { result } from 'lodash';
import { BehaviorSubject } from 'rxjs';
import { CandidateService } from 'src/app/services/candidate.service';
import { OrgadminService } from 'src/app/services/orgadmin.service';
import { OrgadminDashboardService } from 'src/app/services/orgadmin-dashboard.service';
import Swal from 'sweetalert2';
import { LoaderService } from 'src/app/services/loader.service';
import { AuthenticationService } from 'src/app/services/authentication.service';
import * as XLSX from 'xlsx';
import * as XLSXStyle from 'xlsx';
import { DateFormatPipe } from 'src/app/pipes/date-format.pipe';
import { read, utils } from 'xlsx';
import { data } from 'jquery';
import { Router } from '@angular/router';






@Component({
  selector: 'app-uan-search',
  templateUrl: './uan-search.component.html',
  styleUrls: ['./uan-search.component.scss']
})
export class UanSearchComponent implements OnInit {

  closeModal: string | undefined;

  constructor(private modalService: NgbModal, private orgadmin: OrgadminService, private candidateService: CandidateService,
     public loaderService: LoaderService, private auth: AuthenticationService,public calendar: NgbCalendar,
     private router: Router, private dashboardservice:OrgadminDashboardService) {
   
    this.getToday = calendar.getToday(); 
        let inityear = this.getToday.year;
        let initmonth = this.getToday.month <= 9 ? '0' + this.getToday.month : this.getToday.month;;
        let initday = this.getToday.day <= 9 ? '0' + this.getToday.day : this.getToday.day;
        let initfinalDate = initday + "/" + initmonth + "/" + inityear;
        this.initToday = initfinalDate;
        if(localStorage.getItem('dbFromDate')==null && localStorage.getItem('dbToDate')==null){
        // this.customer.setFromDate(this.initToday);
        // this.customer.setToDate(this.initToday);
        this.fromDate = this.initToday;
        this.toDate = this.initToday;
      }

      console.warn("FROMDATE::",this.initToday);
      console.warn("toDate::",this.initToday)

      console.warn("CONSTRUCT TODAY::",this.uanSearchFilter.value)

      
      this.uanSearchFilterSubmit();

      this.orgadmin
      .getServiceConfigForOrg(auth.getOrgID())
      .subscribe((result: any) => {
        this.getServiceConfigCodes = result.data;
        console.log("ORG SERVICES::{}",this.getServiceConfigCodes);
      });

   }

  sortBy: any;
  selectedDate: any;
  singleUAN: any;

  candidateCode: any;
  transactionId: any;
  uanSearch: boolean = true;
  uanData: any = [];
  uanMessages: any[] = [];
  EpfoData: any = [];
  selectedFiles: any;
  BulkUanApplicantId:any = [];
  bulkApplicantId:any = [];
  bulkUan:any = [];
  applicantIdAndUan:any={};
  getServiceConfigCodes: any = [];
  bulkUanSearch: any;
  // retriveBulkUanData:any = {};
  bulkUanId:any;
  uanSearchId:any;
  

  fromDate:any;
  toDate:any;
  getToday: NgbDate;
  getMinDate: any;
  setfromDate:any;
  settoDate:any;
  initToday:any;

  pageNumber: number = 0;
  pageSize: number = 50;

  currentPage = 1;
  currentPageIndex: number = 1;
  currentFile: any;




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
      console.warn("SELECTED FILE::::",this.selectedFiles)
    }else{
      event.target.value = null;
      Swal.fire({
        title: 'Please select .xlsx, .xls, .csv file type only.',
        icon: 'warning'
      });
    }

  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }

  singleUanData = new FormGroup({
    applicantId: new FormControl('', Validators.required),
    uanusername: new FormControl('', [Validators.required,Validators.pattern(/^\d{12}$/)]),
    candidateCode: new FormControl(''),
    transactionid: new FormControl(''),
    uanSearch: new FormControl('')
  });
  allowInput(event: any) {
    const input = event.target.value;
    if (input.length >= 12) {
      event.preventDefault(); // Prevent further input
    }
  }

  saveUan = new FormGroup({
    applicantId: new FormControl(''),
    uploadedBy: new FormControl(''),
    uanusername: new FormControl('')
  })

  retriveUanData = new FormGroup({
    applicantId: new FormControl(''),
    uanusername: new FormControl('')
  })

  updateUandataAfterFetching = new FormGroup({
    applicantId: new FormControl(''),
    uanusername: new FormControl(''),
    uploadedBy: new FormControl(''),
    msg: new FormControl('')
  })

  downloadXlsFile = new FormGroup({
    uanusername: new FormControl(''),
    candidateCode: new FormControl(''),
    applicantId: new FormControl(''),
    bulkUanId: new FormControl(''),
    uanSearchId:new FormControl(''),

  })

  uanSearchFilter = new FormGroup({
    fromDate: new FormControl(''),
    toDate: new FormControl(''),
    user: new FormControl(this.auth.getuserName()),
    pageSize: new FormControl(this.pageSize),
    pageNumber: new FormControl(this.pageNumber)
  })

  downloadXlsFilePatchValue() {
    this.downloadXlsFile.patchValue({
      candidateCode: this.candidateCode,
      uanusername: this.singleUanData.get('uanusername')?.value,
      applicantId: this.singleUanData.get('applicantId')?.value,
    

    })
  }




  updateUandataAfterFetchingPatchValues() {
    this.updateUandataAfterFetching.patchValue({
      applicantId: this.singleUanData.get('applicantId')?.value,
      uanusername: this.singleUanData.get('uanusername')?.value,
      uploadedBy: this.auth.getuserName(),
      msg: "success",
    })
  }


  saveUanPatchValues() {
    this.saveUan.patchValue({
      uploadedBy: this.auth.getuserName(),
      applicantId: this.singleUanData.get('applicantId')?.value,
      uanusername: this.singleUanData.get('uanusername')?.value
    })
  }

  retriveUanDataPatchValue() {
    this.retriveUanData.patchValue({
      applicantId: this.singleUanData.get('applicantId')?.value,
      uanusername: this.singleUanData.get('uanusername')?.value
    })

  }

  patchUserValues() {
    this.singleUanData.patchValue({
      candidateCode: this.candidateCode,
      transactionid: this.transactionId,
      uanSearch: this.uanSearch,
    });

  }



  refreshData() {
    // Logic for refreshing data

    if(this.bulkUanSearch == true){
      console.warn("BULKUANSEARCH PROPERTY::>>",this.bulkUanSearch);
    // console.warn("RetriveBulkUanData:::>>",this.retriveBulkUanData);
    console.warn("BulkUanId::>>",this.bulkUanId);
    this.orgadmin.retriveBulkUanData(this.bulkUanId).subscribe((data:any) => {
      this.uanData = data.data;
        console.warn("DATA::::::: ", data);
  
    })
    }
    else{
      this.retriveUanDataPatchValue();
      this.orgadmin.getUanSearchData(this.retriveUanData.value).subscribe((data: any) => {
        this.uanData = data.data;
        console.warn("DATA::::::: ", data);
  
      })
    }
   
     // this.uanSearchFilterSubmit();

     this.router.routeReuseStrategy.shouldReuseRoute = () => false;
    this.router.onSameUrlNavigation = 'reload';
    this.router.navigate([this.router.url]);

  }

   bulkUANSearch() {
    // Logic for bulk UAN search
    if (this.selectedFiles) {
              this.currentFile = this.selectedFiles.item(0);
                 this.orgadmin.getBulkUanSearch(this.currentFile).subscribe(
                  (event: any) => {
                       // this.uanData = event.data;  
                        console.warn("DATA:::::::", data);
                        console.warn("UANDATA::::",this.uanData);

                        if(event instanceof HttpResponse){
                          if(event.body.outcome == true){
                            Swal.fire({
                              title: event.body.message,
                              icon: 'success'
                            }).then(function() {
                              window.location.reload();
                          });
                          } 
                          else{
                            Swal.fire({
                              title: event.body.message,
                              icon: 'error'
                            }).then(function() {
                              window.location.reload();
                          });
                          } 
                        }
                      })
    } else {
      // Handle the case when no file is selected
      console.log('No file selected');
    }
}


  
  searchSingleUAN() {
    this.bulkUanSearch = false;
    this.loaderService.show();
    console.warn("getUserId", this.auth.getuserName());
    this.saveUanPatchValues();
    console.warn("PathValue::", this.saveUan.value);
    const applicantId = this.singleUanData.get('applicantId')?.value;
    const uanNumber = this.singleUanData.get('uanusername')?.value;
    console.warn("applicantId::", applicantId);


    this.orgadmin.getCandidateCodeByApplicantId(this.saveUan.value).subscribe((data: any) => {
      this.candidateCode = data.message;
      // this.uanData = data.data;
      console.warn("CANDIDATECODE::", this.candidateCode);
      console.warn("DATA:::", data);

    //  if(this.candidateCode == null ){
        console.warn("CandidateCode ===>>>",this.candidateCode);

        this.retriveUanDataPatchValue();
                this.orgadmin.getUanSearchData(this.retriveUanData.value).subscribe((data: any) => {
                  this.uanData = data.data;
                  this.uanData.reverse();
                  this.uanSearchId = this.uanData?.uanSearchId; // Access uanSearchId from uanData
                  console.warn("UanSearchId::",this.uanData);
                  console.warn("DATA:::::::", data);
  
                })

                if (data.outcome === true) {
                  const msg = "success";
                  console.warn("RESULT OUTCOME::", data.outcome);
                  this.updateUandataAfterFetchingPatchValues();
                  // this.orgadmin.updateData(this.updateUandataAfterFetching.value).subscribe((data: any) => {
                  //   console.warn("Update Data::", data);
                  // })
  
                  Swal.fire({
                    //  title: result,
                    icon: 'success'
  
                  }).then((result) => {
                    if (result.isConfirmed) {
                        // window.location.reload();
                       this.modalService.dismissAll();
                       this.refreshData();
                    }
                  });;
                } else {
                  Swal.fire({
                    title: data.message,
                    icon: 'warning'
                  });
                }
      
    });
    //else
    // console.warn("UANDATA2:::",this.uanData);
  }


  downloadFile(item: any) {
    const applicantId2 = item.applicantId;
    const bulkId = item.bulkUanId;
    const totalRecordUploaded = item.totalRecordUploaded;
    console.warn("TotalRecords:::",totalRecordUploaded);

    console.warn("UANDATA>>>>>>>>>>",this.uanData);
    console.warn("APPLICANTID==AND==UAN==IN DOWNLOAD",this.applicantIdAndUan);
    console.warn("APLICANT>>>>",this.applicantIdAndUan.applicantId);
    // console.log(Object.keys(this.applicantIdAndUan));
    // console.log(Object.values(this.applicantIdAndUan));

    const entries = Object.entries(this.applicantIdAndUan);
console.warn("entries>>>",entries)

const applicantKey = Object.keys(this.applicantIdAndUan);
console.warn("ApplicantKey>>>>>>>>>>",applicantKey);



    this.downloadXlsFile.patchValue({
      bulkUanId: bulkId, // Patching the value of bulkUanId
    });
    if(bulkId != null){
      console.warn("BulkId is not Null")
      console.warn("BulkId:::",bulkId);
    console.warn("ApplicantId For Download::", applicantId2);
    console.warn("Candidate Code in DownloadFunction::", this.candidateCode);
    this.downloadXlsFilePatchValue();
    console.warn("DownloadXlsPatchValue::", this.downloadXlsFile.value);
    this.orgadmin.getDownloadFile(this.downloadXlsFile.value).subscribe((data: any) => {
      console.warn("Output Data in Download Function::", data)
      this.EpfoData = data.data;
      console.warn("EPFODATA:::", this.EpfoData);

    

      // Add additional column
      const dateFormat = 'dd/MM/yyyy'; // Specify your desired date format

      const datePipe = new DateFormatPipe();
  

      console.warn(this.EpfoData);

      this.EpfoData.forEach((data: any) => {
        // Check if doe property is empty
        if (!data.doe) {
          data.doe = 'Not_Available';
        } else {
          data.doe = datePipe.transform(data.doe, dateFormat);
        }
    
        // Check if doj property is empty
        if (!data.doj) {
          data.doj = 'Not_Available';
        } else {
          data.doj = datePipe.transform(data.doj, dateFormat);
        }
      });

      // Assuming this.EpfoData is an array of objects
// Rearrange the properties of each object to bring "applicantId" first
const rearrangedDataSheet1 = this.EpfoData.map((item:any) => {
  const { applicantId, epfoResponse, ...rest } = item; // Exclude 'epfoResponse' here
    return { applicantId, ...rest };
});

const rearrangedDataSheet2 = this.EpfoData.map((item:any) => {
  const { applicantId, ...rest } = item;
  return { applicantId, ...rest };
});

console.warn("REARRANGE DATA::::",rearrangedDataSheet1);



const mergedData:any = [];

rearrangedDataSheet1.forEach((current:any) => {
  const existingIndex = mergedData.findIndex((item:any) => {
    // Check if all cell values in the current item match the existing item
    return Object.keys(item).every(key => item[key] === current[key]);
  });
  
  if (existingIndex !== -1) {
    // Merge the properties of the current item with the existing item
    Object.assign(mergedData[existingIndex], current);
  } else {
    // Add the current item to the mergedData array if it doesn't exist
    mergedData.push(current);
  }
});

const filteredDataSheet1 = rearrangedDataSheet1
  .filter((item: any) => item.name !== null || item.company !== null)
  .map((item: any) => {
    // Rename keys and swap "doj" and "doe" properties
    return {
      "APPLICANT ID": item.applicantId,
      "UAN": item.uan,
      "CANDIDATE NAME": item.name,
      "ESTABLISHMENT NAME": item.company,
      "DATE OF JOIN": item.doj,
      "DATE OF EXIT PF": item.doe,
      "BULK ID": item.bulkId
    };
  });


console.warn("MERGED DATA::::",mergedData);
//const worksheet2Data = mergedData.filter((item:any) => item.epforesponse !== null);
let worksheet2Data:any = [];
console.log("Sheet@ DATA::",worksheet2Data);

rearrangedDataSheet2.forEach((current:any) => {
  if (current.epfoResponse !== null) {
    // Add the current item to worksheet2Data if epforesponse is not null
    console.warn("CURRENT___EPFORESPONSE:::",current.epfoResponse);
    const { name, company, doe, doj, ...remainingData } = current;

    const rearrangedItem = {
      "APPLICANT ID": remainingData.applicantId,
      "UAN": remainingData.uan,
      "EPFO RESPONSE": remainingData.epfoResponse,
      "BULK ID": remainingData.bulkId
      // Add more renamed keys as needed
    };

    worksheet2Data.push(rearrangedItem);
  } 
  // else {
  //   // Add the current item to sheet1Data
  //   worksheet2Data = [];
  //   //sheet1Data.push(current);
  // }
});


      // Generate Excel file
      const worksheet = XLSX.utils.json_to_sheet(filteredDataSheet1);
      const worksheet2 = XLSX.utils.json_to_sheet(worksheet2Data);


      //Set Cell Column Width Auto
      const range = worksheet['!ref'];
      const columnCount = range ? XLSX.utils.decode_range(range).e.c + 1 : 0; // Get the total number of columns
      worksheet['!cols'] = worksheet['!cols'] || [];

      const rows = XLSX.utils.sheet_to_json(worksheet, { header: 1 }) as Array<{ [key: string]: any }>;
      
      for (let i = 0; i < columnCount; i++) {
        let maxColumnWidth = 0;
      
        for (const row of rows) {
          const cellValue = (row[i] ?? '').toString(); // Use nullish coalescing to handle undefined
          const cellContentLength = cellValue.length;
          maxColumnWidth = Math.max(maxColumnWidth, cellContentLength);
        }
      
        worksheet['!cols'][i] = worksheet['!cols'][i] || {};

        // worksheet['!cols'][i] = { wch: maxColumnWidth + 2 }; // Add some padding
        worksheet['!cols'][i].wch = maxColumnWidth + 2;
      }
  

      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, 'SUCCESS');
      XLSX.utils.book_append_sheet(workbook, worksheet2, 'FAIL'); // Add Sheet2


      const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
      const excelBlob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });

      // Trigger file download
      const downloadLink = document.createElement('a');
      downloadLink.href = URL.createObjectURL(excelBlob);
      downloadLink.download = 'UAN-Search-report.xlsx';
      downloadLink.click();
    })
      

    }

    
    else{
    console.warn("Bulk is Null")
    console.warn("BulkId:::",bulkId);
    console.warn("ApplicantId For Download::", applicantId2);
    console.warn("Candidate Code in DownloadFunction::", this.candidateCode);
    const selectedUanSearchId = item.uanSearchId;
    const uanNumber = item.uan;
    this.downloadXlsFilePatchValue();
    this.downloadXlsFile.patchValue({
      uanSearchId: selectedUanSearchId,
      uanusername: item.uan,
      applicantId: applicantId2
    });
    console.warn("DownloadXlsPatchValue::", this.downloadXlsFile.value);
    this.orgadmin.getDownloadFile(this.downloadXlsFile.value).subscribe((data: any) => {
      console.warn("Output Data in Download Function::", data)
      this.EpfoData = data.data;
      const sourceOutCome = data.outcome;
      console.log("SOurce OUTCOME::::",sourceOutCome)
      console.warn("EPFODATA:::", this.EpfoData);

      if(sourceOutCome){
        const dateFormat = 'dd/MM/yyyy'; // Specify your desired date format

      const datePipe = new DateFormatPipe();
      const columnName = 'APPLICANT_ID';
      const columnValue = applicantId2;
      // const columnNameToRemove = 'doe';
      const modifiedEpfoData = this.EpfoData.map((data: any) => {
        // const { [columnNameToRemove]: _, ...restData } = data;
        
        const modifiedData = {
          [columnName]: columnValue,
          ...data,
        };

        delete modifiedData['applicantId'];
        delete modifiedData['bulkId'];
        delete modifiedData['epfoResponse'];
      
        // Check if doe property is empty
        if (!modifiedData.doe) {
          modifiedData.doe = 'Not_Available';
        } else {
          modifiedData.doe = datePipe.transform(modifiedData.doe, dateFormat);
        }
      
        // Check if doj property is empty
        if (!modifiedData.doj) {
          modifiedData.doj = 'Not_Available';
        } else {
          modifiedData.doj = datePipe.transform(modifiedData.doj, dateFormat);
        }
      
        // const temp = modifiedData.doj;
        // modifiedData.doj = modifiedData.doe;
        // modifiedData.doe = temp;

        const rearrangeData = {
          "APPLICANT ID":columnValue,
          "UAN":modifiedData.uan,
          "CANDIDATE NAME":modifiedData.name,
          "ESTABLISHMENT NAME":modifiedData.company,
          "DATE OF JOIN":modifiedData.doj,
          "DATE OF EXIT PF":modifiedData.doe
        }

        console.warn("RRRRDRDYADTGCD::",rearrangeData);

        return rearrangeData;

        // return {
        //   [columnName]: columnValue,
        //   ...data,
        //   doe:datePipe.transform(data.doe,dateFormat),
        //   doj: datePipe.transform(data.doj, dateFormat)
        // };
      });

      const mergedData:any = [];

      modifiedEpfoData.forEach((current:any) => {
  const existingIndex = mergedData.findIndex((item:any) => {
    // Check if all cell values in the current item match the existing item
    return Object.keys(item).every(key => item[key] === current[key]);
  });
  
  if (existingIndex !== -1) {
    // Merge the properties of the current item with the existing item
    Object.assign(mergedData[existingIndex], current);
  } else {
    // Add the current item to the mergedData array if it doesn't exist
    mergedData.push(current);
  }
});

      console.warn(modifiedEpfoData);
      console.warn("MERGED DATA::::",mergedData);
      


      // Generate Excel file
      const worksheet = XLSX.utils.json_to_sheet(mergedData);

      //Set Cell Column Width Auto
      const range = worksheet['!ref'];
      const columnCount = range ? XLSX.utils.decode_range(range).e.c + 1 : 0; // Get the total number of columns
      worksheet['!cols'] = worksheet['!cols'] || [];

      const rows = XLSX.utils.sheet_to_json(worksheet, { header: 1 }) as Array<{ [key: string]: any }>;
      
      for (let i = 0; i < columnCount; i++) {
        let maxColumnWidth = 0;
      
        for (const row of rows) {
          const cellValue = (row[i] ?? '').toString(); // Use nullish coalescing to handle undefined
          const cellContentLength = cellValue.length;
          maxColumnWidth = Math.max(maxColumnWidth, cellContentLength);
        }
      
        worksheet['!cols'][i] = worksheet['!cols'][i] || {};

      //  worksheet['!cols'][i] = { wch: maxColumnWidth + 2 }; // Add some padding
      worksheet['!cols'][i].wch = maxColumnWidth + 2;
      }

      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, 'Sheet1');

      const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
      const excelBlob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });

      // Trigger file download
      const downloadLink = document.createElement('a');
      downloadLink.href = URL.createObjectURL(excelBlob);
      downloadLink.download = 'UAN-Search-report.xlsx';
      downloadLink.click();
    }
      // Add additional column
      //OUTSource block close

      else{
        console.log("ELSE BLOCK EPFO")
        const dateFormat = 'yyyy-MM-dd'; // Specify your desired date format

        const datePipe = new DateFormatPipe();
        const columnName = 'ApplicantId';
        const columnValue = applicantId2;
        // const columnNameToRemove = 'doe';
        const modifiedEpfoData = this.EpfoData.map((data: any) => {
          // const { [columnNameToRemove]: _, ...restData } = data;
          
          const modifiedData = {
            [columnName]: columnValue,
            ...data,
          };
  
          delete modifiedData['applicantId'];
          delete modifiedData['bulkId'];
          delete modifiedData['name'];
          delete modifiedData['company'];
          delete modifiedData['doe'];
          delete modifiedData['doj'];
        
          // Check if doe property is empty
          // if (!modifiedData.doe) {
          //   modifiedData.doe = 'Not_Available';
          // } else {
          //   modifiedData.doe = datePipe.transform(modifiedData.doe, dateFormat);
          // }
        
          // // Check if doj property is empty
          // if (!modifiedData.doj) {
          //   modifiedData.doj = 'Not_Available';
          // } else {
          //   modifiedData.doj = datePipe.transform(modifiedData.doj, dateFormat);
          // }

          const rearrangeData = {
            "APPLICANT ID":columnValue,
            "UAN":modifiedData.uan,
            "EPFO RESPONSE":modifiedData.epfoResponse
          }
        
          return rearrangeData;
  
          // return {
          //   [columnName]: columnValue,
          //   ...data,
          //   doe:datePipe.transform(data.doe,dateFormat),
          //   doj: datePipe.transform(data.doj, dateFormat)
          // };
        });
  
        const mergedData:any = [];
  
        modifiedEpfoData.forEach((current:any) => {
    const existingIndex = mergedData.findIndex((item:any) => {
      // Check if all cell values in the current item match the existing item
      return Object.keys(item).every(key => item[key] === current[key]);
    });
    
    if (existingIndex !== -1) {
      // Merge the properties of the current item with the existing item
      Object.assign(mergedData[existingIndex], current);
    } else {
      // Add the current item to the mergedData array if it doesn't exist
      mergedData.push(current);
    }
  });
  
        console.warn(modifiedEpfoData);
        console.warn("MERGED DATA::::",mergedData);
  
  
        // Generate Excel file
        const worksheet = XLSX.utils.json_to_sheet(mergedData);

        //Set Cell Column Width Auto
        const range = worksheet['!ref'];
      const columnCount = range ? XLSX.utils.decode_range(range).e.c + 1 : 0; // Get the total number of columns
      worksheet['!cols'] = worksheet['!cols'] || [];

      const rows = XLSX.utils.sheet_to_json(worksheet, { header: 1 }) as Array<{ [key: string]: any }>;
      
      for (let i = 0; i < columnCount; i++) {
        let maxColumnWidth = 0;
      
        for (const row of rows) {
          const cellValue = (row[i] ?? '').toString(); // Use nullish coalescing to handle undefined
          const cellContentLength = cellValue.length;
          maxColumnWidth = Math.max(maxColumnWidth, cellContentLength);
        }
      
        worksheet['!cols'][i] = worksheet['!cols'][i] || {};

        // worksheet['!cols'][i] = { wch: maxColumnWidth + 2 }; // Add some padding
        worksheet['!cols'][i].wch = maxColumnWidth + 2;
      }
  

    
  
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, 'Sheet1');
        const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
        const excelBlob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
  
        // Trigger file download
        const downloadLink = document.createElement('a');
        downloadLink.href = URL.createObjectURL(excelBlob);
        downloadLink.download = 'UAN-Search-report.xlsx';
        downloadLink.click();

      }
      
    })
  }
    console.warn("EPFODATA:::22", this.EpfoData);

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


uanSearchFilterSubmit(){

  console.warn("DATES:::",this.uanSearchFilter.value)


  if (this.uanSearchFilter.value.fromDate === '' && this.uanSearchFilter.value.toDate === '') {
    const currentDate = new Date();
    const day = currentDate.getDate().toString().padStart(2, '0'); // Get day and pad with '0' if necessary
    const month = (currentDate.getMonth() + 1).toString().padStart(2, '0'); // Get month (add 1 because months are 0-based) and pad with '0' if necessary
    const year = currentDate.getFullYear();

const currentDateString = `${day}/${month}/${year}`;

    var checkfromDate:any = this.initToday;
      let getfromDate = checkfromDate.split('/');
      this.setfromDate = { day:+getfromDate[0],month:+getfromDate[1],year:+getfromDate[2]};

      var checktoDate:any = this.initToday;
      let gettoDate =checktoDate.split('/');
      this.settoDate = { day:+gettoDate[0],month:+gettoDate[1],year:+gettoDate[2]};
      this.getMinDate = { day:+gettoDate[0],month:+gettoDate[1],year:+gettoDate[2]};

      this.uanSearchFilter.patchValue({
        fromDate: this.setfromDate,
        toDate: this.settoDate
       });


console.log(currentDateString);


    this.uanSearchFilter.value.fromDate = currentDateString;
    this.uanSearchFilter.value.toDate = currentDateString;
    this.uanSearchFilter.value.user = this.auth.getuserName();
    
    console.log("if::",this.uanSearchFilter.value);
   
    this.orgadmin.uanSearchFilter(this.uanSearchFilter.value).subscribe((result:any) => {
      console.log("API OUTSOURCE :::",result);
      this.uanData = result.data;

       this.uanData.sort((a: any, b: any) => {
           const dateA = new Date(a.uploadedOn);
           const dateB = new Date(b.uploadedOn);          
          return dateB.getTime() - dateA.getTime();
       });
  
      this.currentPageIndex = 0;
  

  })
  }
  else{
    console.log("ELSE:");

    let inputFromDate:any = $("#inputFromDate").val();
    //let getInputFromDate:any = inputFromDate.split('-');
    let finalInputFromDate = inputFromDate;

    let inputToDate:any = $("#inputToDate").val();
    //let getInputToDate:any = inputToDate.split('-');
    let finalInputToDate = inputToDate;

    this.uanSearchFilter.value.fromDate = finalInputFromDate;
    this.uanSearchFilter.value.toDate = finalInputToDate;

    console.warn("uanSearchFilter:",this.uanSearchFilter.value);

    this.orgadmin.uanSearchFilter(this.uanSearchFilter.value).subscribe((result:any) => {
    console.log("API OUTSOURCE :::",result);
    this.uanData = result.data;

    this.uanData.sort((a: any, b: any) => {
      const dateA = new Date(a.uploadedOn);
      const dateB = new Date(b.uploadedOn);
      
      return dateB.getTime() - dateA.getTime();
    });

    this.currentPageIndex = 0;

})

}
}

uanSearchFilterPagination(): any[] {
  const startIndex = this.currentPageIndex * this.pageSize;
  const endIndex = startIndex + this.pageSize;
  return this.uanData.slice(startIndex, endIndex);
}

goToNextPage(): void {
  if (this.currentPageIndex < this.totalPages - 1) {
    this.currentPageIndex++;
  }
}

goToPrevPage(): void {
  if (this.currentPageIndex > 0) {
    this.currentPageIndex--;
  }
}

get totalPages(): number {
  return Math.ceil(this.uanData.length / this.pageSize);
}

  ngOnInit(): void {
   

  }

  downloadUploadResourceFile(uploadFor: any,uploadType: any): void {
    this.dashboardservice.downloadUploadResourceFile(uploadFor,uploadType).subscribe((data: Blob) => {
      const blob = new Blob([data], { type: 'application/octet-stream' });
        const url = window.URL.createObjectURL(blob);
  
        // Create a temporary anchor element
        const a = document.createElement('a');
        a.href = url;
        a.download = uploadFor+"."+uploadType; // Set the filename
        document.body.appendChild(a);
  
        // Trigger the click event on the anchor element
        a.click();
  
        // Remove the temporary anchor element
        document.body.removeChild(a);
    });
  }

  uploadBulkPanData() {
    this.currentFile = this.selectedFiles.item(0);
    console.log(this.currentFile)
    this.orgadmin.uploadBulkPanData(this.currentFile).subscribe(
      (event:any) => {
        console.log(event);
        if(event instanceof HttpResponse){
          console.log(event.body)
          Swal.fire({
            title: event.body.message,
            icon: 'success'
          }).then(function() {
            window.location.reload();
          });

          if(event.body.outcome == false) {
            Swal.fire({
              title: event.body.message,
              icon: 'warning'
            }).then(function() {
              window.location.reload();
            });
          }
        }

    }, (err: any) => {
      if(err.error){
        Swal.fire({
          title: err.error.message,
          icon: 'warning'
        }).then(function() {
          window.location.reload();
        });
      }
    });
  }

}
