import { HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { result } from 'lodash';
import { BehaviorSubject } from 'rxjs';
import { CandidateService } from 'src/app/services/candidate.service';
import { OrgadminService } from 'src/app/services/orgadmin.service';
import Swal from 'sweetalert2';
import { LoaderService } from 'src/app/services/loader.service';
import { AuthenticationService } from 'src/app/services/authentication.service';
import * as XLSX from 'xlsx';
import * as XLSXStyle from 'xlsx';
import { DateFormatPipe } from 'src/app/pipes/date-format.pipe';
import { read, utils } from 'xlsx';
import { data } from 'jquery';






@Component({
  selector: 'app-uan-search',
  templateUrl: './uan-search.component.html',
  styleUrls: ['./uan-search.component.scss']
})
export class UanSearchComponent implements OnInit {

  closeModal: string | undefined;

  constructor(private modalService: NgbModal, private orgadmin: OrgadminService, private candidateService: CandidateService, public loaderService: LoaderService, private auth: AuthenticationService) { }

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
  bulkUanSearch: any;
  // retriveBulkUanData:any = {};
  bulkUanId:any;
  



  // private candidateCodeSubject = new BehaviorSubject<string>('');

  // candidateCode$ = this.candidateCodeSubject.asObservable();



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
    uanusername: new FormControl('', Validators.required),
    candidateCode: new FormControl(''),
    transactionid: new FormControl(''),
    uanSearch: new FormControl('')
  });

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
   

    

  }

  randomId(){
    const min = 100000; // Minimum value for a six-digit number
    const max = 999999; // Maximum value for a six-digit number
    const randomId = Math.floor(Math.random() * (max - min + 1)) + min;
    console.log("Random_ID",randomId); // Output: a random six-digit number between 100000 and 999999
    
  }

   bulkUANSearch() {
    // Logic for bulk UAN search
    this.bulkUanSearch = true;
    if (this.selectedFiles) {
      const file = this.selectedFiles[0];
      const reader = new FileReader();
  
      reader.onload = (e: any) => {
        const data = new Uint8Array(e.target.result);
        const workbook = read(data, { type: 'array' });
        const worksheet = workbook.Sheets[workbook.SheetNames[0]];
        const jsonData: unknown[] = utils.sheet_to_json(worksheet, { header: 1, range: 1 });

        // Count the number of data or rows
      const rowCount = jsonData.length;
      console.warn("TOTAL DATA:::",rowCount)
      const totalRecordUploaded = rowCount;
      const bulkUanSearch = true;
      const uploadedBy = this.auth.getuserName();

      const min = 100000; 
    const max = 999999; 
    const randomId = Math.floor(Math.random() * (max - min + 1)) + min;
    console.log("Random_ID",randomId); 
    
    // let dto:any = {};
    // let getEpfoForBulk:any = {};
    // let getUanData:any = {};

    let dto: any[] = [];
    let getEpfoForBulk: any[] = [];
    let getUanData: any[] = [];

    const extractedData: { applicantId: any; uan: any,randomId:any,totalRecordUploaded:any,bulkUanSearch:boolean,uploadedBy:any }[] = [];

    let applicantId:any;
    let uan:any
        // Process each data entry one by one
        for (const row of jsonData) {
         
           applicantId = (row as any[])[0];
           uan = (row as any[])[1];

          extractedData.push({ applicantId, uan,randomId,totalRecordUploaded,bulkUanSearch,uploadedBy });
          console.log("ExtractedData::>>>",extractedData);

          this.applicantIdAndUan[applicantId] = {
            uan: uan
          };
          console.warn("APPLICANTID==AND==UAN==",this.applicantIdAndUan);
        }

        extractedData.forEach((data) => {
          console.log('Applicant ID:', data.applicantId);
          console.log('UAN:', data.uan);
          console.log('BulkUanId',data.randomId)
          this.bulkUanId = data.randomId;
          console.log('---'); // Just a separator for better visibility
        });
          
        // this.retriveBulkUanData = extractedData;
              console.warn("CandidateCode ===>>>",this.candidateCode);
      
              console.warn("UANDATA>>>>",getUanData)
              this.retriveUanDataPatchValue();
                 this.orgadmin.getBulkUanSearch(extractedData).subscribe((data: any) => {
                        this.uanData = data.data;  
                        console.warn("DATA:::::::", data);
                        console.warn("UANDATA::::",this.uanData);

                        if (data.outcome === true) {
                          const msg = "success";
                          console.warn("RESULT OUTCOME::", data.outcome);
                          // this.updateUandataAfterFetchingPatchValues();
                          // this.orgadmin.updateData(this.updateUandataAfterFetching.value).subscribe((data: any) => {
                          //   console.warn("Update Data::", data);
                          // })
          
                          Swal.fire({
                            //  title: result,
                            icon: 'success'
          
                          }).then((data) => {
                            if (data.isConfirmed) {
                                // window.location.reload();
                               this.modalService.dismissAll();
                              //  this.refreshData();
                            }
                          });;
                        } else {
                          Swal.fire({
                            title: data.message,
                            icon: 'warning'
                          });
                        }
        
                      })

          
      };
  
      reader.readAsArrayBuffer(file);
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

      if(this.candidateCode == null ){
        console.warn("CandidateCode ===>>>",this.candidateCode);

        this.retriveUanDataPatchValue();
                this.orgadmin.getUanSearchData(this.retriveUanData.value).subscribe((data: any) => {
                  this.uanData = data.data;
                  console.warn("DATA:::::::", data);
  
                })

                if (data.outcome === true) {
                  const msg = "success";
                  console.warn("RESULT OUTCOME::", data.outcome);
                  this.updateUandataAfterFetchingPatchValues();
                  this.orgadmin.updateData(this.updateUandataAfterFetching.value).subscribe((data: any) => {
                    console.warn("Update Data::", data);
                  })
  
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
  
      }

      else{

        this.candidateService.getepfoCaptcha(this.candidateCode).subscribe((data2: any) => {
          this.transactionId = data2.data.transactionid;
          console.warn("TRANSACTION_ID::", this.transactionId);
  
  
          this.patchUserValues();
          if (this.singleUanData.valid) {
            console.log('this.SingleUANDATA.value', this.singleUanData.value);
  
            setTimeout(() => {
              this.candidateService.getEpfodetail(this.singleUanData.value).subscribe((result: any) => {
  
                this.retriveUanDataPatchValue();
                this.orgadmin.getUanSearchData(this.retriveUanData.value).subscribe((data: any) => {
                  this.uanData = data.data;
                  console.warn("DATA:::::::", data);
  
                })
                console.warn("FINALRESULT:::", result);
                if (result.outcome === true) {
                  const msg = "success";
                  console.warn("RESULT OUTCOME::", result.outcome);
                  this.updateUandataAfterFetchingPatchValues();
                  this.orgadmin.updateData(this.updateUandataAfterFetching.value).subscribe((data: any) => {
                    console.warn("Update Data::", data);
                  })
  
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
                    title: result.message,
                    icon: 'warning'
                  });
                }
  
                this.loaderService.hide(); // Hide the loader inside the final response callback
              },
  
                (error: any) => {
                  console.error("An error occurred while fetching data:", error);
                  this.loaderService.hide(); // Hide the loader in case of an error
                });
  
            }, 100); // Adjust the delay as needed
  
  
          }
          else {
            Swal.fire({
              title: "Please enter the required information",
              icon: 'warning'
            });
            this.loaderService.hide();
          }
  
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
      const dateFormat = 'yyyy-MM-dd'; // Specify your desired date format

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
const rearrangedData = this.EpfoData.map((item:any) => {
  const { applicantId, ...rest } = item;
  return { applicantId, ...rest };
});

console.warn("REARRANGE DATA::::",rearrangedData);


const mergedData:any = [];

rearrangedData.forEach((current:any) => {
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

console.warn("MERGED DATA::::",mergedData);

      // Generate Excel file
      const worksheet = XLSX.utils.json_to_sheet(mergedData);
  

      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, 'Sheet1');
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
    this.downloadXlsFilePatchValue();
    console.warn("DownloadXlsPatchValue::", this.downloadXlsFile.value);
    this.orgadmin.getDownloadFile(this.downloadXlsFile.value).subscribe((data: any) => {
      console.warn("Output Data in Download Function::", data)
      this.EpfoData = data.data;
      console.warn("EPFODATA:::", this.EpfoData);

      // Add additional column
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
      
        return modifiedData;

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
  

      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, 'Sheet1');
      const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
      const excelBlob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });

      // Trigger file download
      const downloadLink = document.createElement('a');
      downloadLink.href = URL.createObjectURL(excelBlob);
      downloadLink.download = 'UAN-Search-report.xlsx';
      downloadLink.click();
    })
  }
    console.warn("EPFODATA:::22", this.EpfoData);



  }





  ngOnInit(): void {
   

  }


}
