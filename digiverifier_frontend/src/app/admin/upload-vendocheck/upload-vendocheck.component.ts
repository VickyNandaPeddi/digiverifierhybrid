import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { CustomerService } from '../../services/customer.service';
import { OrgadminService } from 'src/app/services/orgadmin.service';
import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CandidateService } from 'src/app/services/candidate.service';
import * as XLSX from 'xlsx';
import * as XLSXStyle from 'xlsx';
import {
  FormGroup,
  FormControl,
  FormBuilder,
  Validators,
} from '@angular/forms';
import Swal from 'sweetalert2';
import { NgbCalendar, NgbDate } from '@ng-bootstrap/ng-bootstrap';

import { Router } from '@angular/router';
import { OrgadminDashboardService } from 'src/app/services/orgadmin-dashboard.service';
import { any } from '@amcharts/amcharts4/.internal/core/utils/Array';
@Component({
  selector: 'app-upload-vendocheck',
  templateUrl: './upload-vendocheck.component.html',
  styleUrls: ['./upload-vendocheck.component.scss'],
})
export class UploadVendocheckComponent implements OnInit {
  pageTitle = 'Vendor Management';
  vendorchecksupload: any = [];
  vendoruser: any;
  userID: any;
  getVendorID: any = [];
  candidateId: any;
  candidateCode: any;
  sourceId: any;
  candidateName: any = [];
  userName: any = [];
  sourceName: any = [];
  vendorId: any;
  getColors: any = [];
  colorid: any;
  fromDate: any;
  toDate: any;
  setfromDate: any;
  settoDate: any;
  getToday: NgbDate;
  getMinDate: any;
  start_date = '';
  end_date = '';
  initToday: any;
  proofDocumentNew: any;
  venderAttributeValue: any[] = [];
  venderSourceId: any;
  venderAttributeCheck: any = [];
  vendorAttributeCheckMapped: any[] = [];
  vendorDashboardStatus: any;

  vendorAttributeListForm: any[] = [];
  searchText: string = '';
  showMessage: any;

  // public proofDocumentNew: any = File;
  closeModal: string | undefined;

  // selectedFiles: any;
  tep: any = [1];
  // vendorlist:any;
  tmp: any;
  orgID: any;
  pageNumber: number = 0;
  pageSize: number = 10;
  totalPages: number = 0; // Add this variable
  // dasboardSearch:boolean = false;

  vendorlist = new FormGroup({
    vendorcheckId: new FormControl(''),
    documentname: new FormControl(''),
    colorid: new FormControl(''),
   // value: new FormControl(''),
    vendorCheckStatusMasterId: new FormControl('', Validators.required),
    // fileInput: new FormControl('',Validators.required)
    remarks: new FormControl('', Validators.required)
  });

  utilizationReportFilter = new FormGroup({
    fromDate: new FormControl('', Validators.required),
    toDate: new FormControl('', Validators.required),
    // sourceId: new FormControl('', Validators.required)
  });
  getVenorcheckStatus: any[] = [];
  vendorCheckStatusMasterId: any;
  isButtonDisabled: boolean = false;

  patchUserValues() {
    this.vendorlist.patchValue({
      colorid: 2,
    });
  }

  constructor(
    private candidateService: CandidateService,
    public authService: AuthenticationService,
    calendar: NgbCalendar,
    private customers: CustomerService,
    private _router: Router,
    private modalService: NgbModal,
    private orgadmin: OrgadminDashboardService
  ) {
    this.orgID = this.authService.getuserId();
    this.getToday = calendar.getToday();
    console.log(this.orgID);

    this.getToday = calendar.getToday();
    let inityear = this.getToday.year;
    let initmonth = this.getToday.month <= 9 ? '0' + this.getToday.month : this.getToday.month;;
    let initday = this.getToday.day <= 9 ? '0' + this.getToday.day : this.getToday.day;
    let initfinalDate = initday + "/" + initmonth + "/" + inityear;
    this.initToday = initfinalDate;
    if (localStorage.getItem('dbFromDate') == null && localStorage.getItem('dbToDate') == null) {
      this.customers.setFromDate(this.initToday);
      this.customers.setToDate(this.initToday);
      this.fromDate = this.initToday;
      this.toDate = this.initToday;
    }

    var checkfromDate: any = localStorage.getItem('dbFromDate');
    let getfromDate = checkfromDate.split('/');
    this.setfromDate = { day: +getfromDate[0], month: +getfromDate[1], year: +getfromDate[2] };

    var checktoDate: any = localStorage.getItem('dbToDate');
    let gettoDate = checktoDate.split('/');
    this.settoDate = { day: +gettoDate[0], month: +gettoDate[1], year: +gettoDate[2] };
    this.getMinDate = { day: +gettoDate[0], month: +gettoDate[1], year: +gettoDate[2] };

    const dateSearchFilter = {
      fromDate: customers.getFromDate(),
      toDate: customers.getToDate()
    };

    this.utilizationReportFilter.patchValue({
      fromDate: this.setfromDate,
      toDate: this.settoDate,
    });
    const vendorCheckDashboardStatusCode = this.orgadmin.getPendingDetailsStatCode();
    this.vendorDashboardStatus = vendorCheckDashboardStatusCode;

    this.customers
      .getallVendorCheckDetails(this.orgID, dateSearchFilter, this.pageNumber, this.pageSize, vendorCheckDashboardStatusCode)
      .subscribe((data: any) => {
        console.log(data);
        this.vendorchecksupload = data.data;
  //      console.warn("fkjsbfkjb",this.vendorchecksupload)
        this.totalPages = data.status; // Set the total pages

        const dataArray: { createdOn: string; }[] = this.vendorchecksupload;

        dataArray.forEach((element, index) => {
          const createdOnDate = new Date(element.createdOn);
          // console.log(`Element ${index + 1}: ${createdOnDate.toDateString()}`);
        });

        dataArray.sort((a, b) => {
          const dateA = new Date(a.createdOn);
          const dateB = new Date(b.createdOn);

          return dateB.getTime() - dateA.getTime();
        });

        console.log(dataArray);


        console.warn("VENDORCHECKS::::::::::::::::", this.vendorchecksupload);

        const createdOnDate = data.data[0].createdOn;
        console.warn("CreatedOONDATE:::", createdOnDate)


      });
    this.candidateService.getColors().subscribe((data: any) => {
      this.getColors = data.data;
      console.log(this.getColors);
    });
  }

  ngOnInit(): void {
    this.customers.getVenorcheckStatus().subscribe((data: any) => {
      if (data.data) {
        this.getVenorcheckStatus = data.data.filter((temp: any) => {
          if (temp.checkStatusCode != 'INPROGRESS') {
            return temp;
          }
        });
      }
      console.log(this.getVenorcheckStatus);
    });
  }

  getvendorcheckstatuss(event: any) {
    console.log("control entered with value: ", event.target.value);
    this.vendorCheckStatusMasterId = event.target.value;
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

  // onSubmitFilter() {
  //   this.fromDate = this.fromDate != null ? this.fromDate : '';
  //   this.toDate = this.toDate != null ? this.toDate : '';
  //   this.utilizationReportFilter.patchValue({
  //     fromDate: this.fromDate,
  //     toDate: this.toDate,
  //   });
  //   this.customers
  //     .postallVendorCheckDetails(this.utilizationReportFilter.value)
  //     .subscribe((data: any) => {
  //       if (data.outcome === true) {
  //         this.vendorchecksupload = data.data;
  //         console.log('this.vendorchecksupload', this.vendorchecksupload);
  //         this.start_date =
  //           data.data.fromDate != null
  //             ? data.data.fromDate.split('-').join('/')
  //             : '';
  //         this.end_date =
  //           data.data.toDate != null
  //             ? data.data.toDate.split('-').join('/')
  //             : '';
  //       }
  //     });
  // }

  onSubmitFilter() {
    const inputFromDate: any = $("#inputFromDateVendor").val();
    const finalInputFromDate = inputFromDate;
    const inputToDate: any = $("#inputToDateVendor").val();
    const finalInputToDate = inputToDate;
    if (this.fromDate == null) {
      this.fromDate = finalInputFromDate;
    }
    if (this.toDate == null) {
      this.toDate = finalInputToDate;
    }
    // if (this.utilizationReportFilter.valid) {
    //   this.vendorchecksupload();
    // }
    const vendorCheckDashboardStatusCode = this.orgadmin.getPendingDetailsStatCode();
    const dateSearchFilter = {
      fromDate: this.fromDate,
      toDate: this.toDate,
      // vendorCheckDashboardStatusCode:vendorCheckDashboardStatusCode
    }
    console.warn("dateSearchFilter ================ ", dateSearchFilter);
    console.warn("VENDORID ============= ", this.orgID);
    console.warn("VENDORID ============= ", this.authService.getuserId());
    console.warn("VENDORID ============= ", this.authService.getOrgID());

    this.customers
      .getallVendorCheckDetails(this.orgID, dateSearchFilter, this.pageNumber, this.pageSize, vendorCheckDashboardStatusCode)
      .subscribe((data: any) => {
        console.log(data);
        this.vendorchecksupload = data.data;
        this.totalPages = data.status; // Set the total pages
        console.warn("totalPages::", this.totalPages);
        console.warn("vendorchecksupload:::::", this.vendorchecksupload)

        const dataArray: { createdOn: string; }[] = this.vendorchecksupload;

        dataArray.forEach((element, index) => {
          const createdOnDate = new Date(element.createdOn);
          // console.log(`Element ${index + 1}: ${createdOnDate.toDateString()}`);
        });

        dataArray.sort((a, b) => {
          const dateA = new Date(a.createdOn);
          const dateB = new Date(b.createdOn);

          return dateB.getTime() - dateA.getTime();
        });
        console.log(dataArray);
      })
  }

  onPageChange(newPageNumber: number) {
    console.warn("button clicked!!")
    this.pageNumber = newPageNumber;
    this.onSubmitFilter();

  }


  uploadGlobalCaseDetails(event: any) {
    const fileType = event.target.files[0].name.split('.').pop();
    const file = event.target.files[0];
    if (
      fileType == 'pdf' ||
      fileType == 'PDF' ||
      fileType == 'png' ||
      fileType == 'PNG' ||
      fileType == 'jpg' ||
      fileType == 'JPG'
    ) {
      this.proofDocumentNew = file;
      this.previewFile(file);
    } else {
      this.isButtonDisabled = true;
      event.target.value = null;
      Swal.fire({
        title: 'Please select .jpeg, .jpg, .png file type only.',
        icon: 'warning',
      });
    }
  }

  previewFile(file: File) {
    const previewContainer = document.getElementById('preview-container');
    const lowerCaseFileType = file.type;

    if (previewContainer) {
      if (
        lowerCaseFileType === 'application/pdf' ||
        lowerCaseFileType === 'image/png' ||
        lowerCaseFileType === 'image/jpeg' ||
        lowerCaseFileType === ' '
      ) {
        const previewButton = document.createElement('button');
        previewButton.textContent = 'Preview';
        previewButton.addEventListener('click', () => {
          this.previewFile(file);
        });
        const downloadLink = document.createElement('a');
        downloadLink.href = URL.createObjectURL(file);
        downloadLink.target = '_blank';
        downloadLink.textContent = 'Preview';
        downloadLink.classList.add('btn', 'btn-primary');

        previewContainer.innerHTML = '';
        previewContainer.appendChild(downloadLink);
      } else {
        previewContainer.innerHTML =
          'Preview not available for this file type.';
      }
    }
  }

  getcolor(event: any) {
    console.log(event.target.value);
    this.colorid = event.target.value;
  }

  // patchAddIdValues() {
  //   this.vendorlist.patchValue({
  //     candidateId: this.candidateId,
  //     sourceId: this.sourceId,
  //     vendorId: this.vendorId,
  //   });
  // }

  triggerModal(
    content: any,
    documentname: any,
    vendorcheckId: any,
    sourceName: string,
    i: number
  ) {
    console.warn(' venderChecked=====>', this.vendorchecksupload);

    console.warn('vendorcheckId', vendorcheckId);

    this.venderSourceId = this.vendorchecksupload[i].source.sourceId;

    // this is the code for Fetching the venderAttributesList

    this.customers
      .getAgentAttributes(this.venderSourceId)
      .subscribe((data: any) => {
        this.venderAttributeCheck = data.data;

        console.warn('===============', this.venderAttributeCheck);

        console.warn(
          'VenderCheck:::',
          this.venderAttributeCheck.vendorAttributeList
        );

        this.venderAttributeValue =
          this.venderAttributeCheck.vendorAttributeList.map((ele: any) => {
            return {
              label: ele,

              value: null,
            };
          });

        console.log(
          'this.venderAttributeCheck===========>',
          this.venderAttributeValue
        );
      });

    this.modalService.open(content).result.then(
      (res) => {
        console.log(content, '........................');
        this.closeModal = `Closed with: ${res}`;
      },
      (res) => {
        this.closeModal = `Dismissed ${this.getDismissReason(res)}`;
      }
    );
    console.log(documentname, '........................'),
      this.vendorlist.patchValue({
        documentname: documentname,
        vendorcheckId: vendorcheckId,
        colorid: this.colorid,
      });
  }

  onSubmit(vendorlist: FormGroup) {
    this.patchUserValues();
    console.warn('VENDORLISTPATCH:::>>>>>>>>>', this.vendorlist);
    console.log(this.vendorAttributeListForm);

    this.vendorAttributeListForm = this.venderAttributeValue;

    // const venderAttributeValue = this.vendorAttributeListForm.reduce(
    //   (obj, item) => {

    //     if (item.value === null || item.value.trim() === '') {
    //       return false; // Return false if any item.value is null or empty
    //     }

    //     obj[item.label] = item.value;

    //     return obj;
    //   },
    //   {}
    // );

    // if (venderAttributeValue === false) {
    //   console.error('Please enter values for all attributes');
    //   this.showMessage = "Please enter values for Mandatory Field";
    // } else {
    //   console.log('CrimnalGlobalAttributeValues:', venderAttributeValue);
    // }

    //  delete agentAttributeValues.value

    //  this.vendorAttributeCheckMapped = {...this.vendorlist.value, ...venderAttributeValue}

    // this.vendorAttributeCheckMapped = { ...venderAttributeValue };

    // const finalValues = JSON.stringify(this.educationAgentAttributeCheckMapped);

    // console.log("finalValues",finalValues)

    // console.log(
    //   ' vendorAttributeCheckMapped:::',
    //   this.vendorAttributeCheckMapped
    // );

    // console.warn('vendorAttributeCheckMapped===>', venderAttributeValue);

    // const mergedData = {
    //   ...this.vendorAttributeCheckMapped,
    // };

    //  formData.append('vendorchecks', JSON.stringify(this.forAddressCrimnalGlobal.value ))

    //  formData.append('vendorchecks', JSON.stringify(agentAttributeValues ))

    //  formData.append('vendorchecks', JSON.stringify(this.forAddressCrimnalGlobal.value ))

    const formData = new FormData();
    formData.append('vendorchecks', JSON.stringify(this.vendorlist.value));

    formData.append('vendorAttributesValue', JSON.stringify(this.vendorlist.value));

    // console.warn('mergedData++++++++++++++++++++', mergedData);
    // ----------------------------------------------------------------------------------------------------------

    // formData.append('vendorchecks', JSON.stringify(this.vendorlist.value));
// if (this.vendorlist.valid && venderAttributeValue !== false) {
  if (this.vendorlist.valid) {
      formData.append('file', this.proofDocumentNew);
      // formData.append('vendorchecks', JSON.stringify(this.vendorlist.value));
      // formData.append('file', this.proofDocumentNew);
      return this.customers
        .saveproofuploadVendorChecks(formData)
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
    } else {
      Swal.fire({
        title: 'Please enter the required details.',
        icon: 'warning'
      })
    }
    return undefined;
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

  getvendorid(id: any) {
    this.getvendorid = id;
    let agentIdsArray: any = [];
    agentIdsArray.push(id);
    this.getvendorid = agentIdsArray;
  }

  dashboardRedirect(id: any) {
    this.customers.getVendorList(Number(id)).subscribe((result: any) => {
      console.log(result);
      if (result.outcome === true) {
        localStorage.setItem('orgID', id);
        localStorage.setItem('userId', result.data.userId);
        this._router.navigate(['admin/orgadminDashboard']);
      } else {
        Swal.fire({
          title: result.message,
          icon: 'warning',
        });
      }
    });
  }

  downloadPdf(documentPathKey: any, documentname: any, soureName: any) {
    if(documentPathKey != null){  
    this.orgadmin.downloadAgentUploadedDocument(documentPathKey).subscribe(
      (data: any) => {
        // if (soureName == "Employment" || soureName == "Education") {
          const contentType = this.detectContentType(data.message);
          console.warn("contentType : ",contentType)
          if(contentType == 'pdf'){
            const linkSource = 'data:application/pdf;base64,' + data.message;
            const downloadLink = document.createElement('a');
            downloadLink.href = linkSource;
            downloadLink.download = documentname;
            downloadLink.click();
          }
          else if(contentType == 'image'){
            const linkSource = 'data:application/png;base64,' + data.message;
            const downloadLink = document.createElement('a');
            downloadLink.href = linkSource;
            // downloadLink.download = documentname;
            downloadLink.download = `${documentname}.png`;
            downloadLink.click();
          }
          else{
            const linkSource = 'data:application/zip;base64,' + data.message;
            const downloadLink = document.createElement('a');
            downloadLink.href = linkSource;
            downloadLink.download = documentname;
            downloadLink.click();
          }
        
        // else if (documentPathKey != null) {
        //   const linkSource = 'data:application/pdf;base64,' + data.message;
        //   const downloadLink = document.createElement('a');
        //   downloadLink.href = linkSource;
        //   downloadLink.download = documentname;
        //   downloadLink.click();
        // } else {
          // Swal.fire({
          //   title: 'No Documents Uploaded',
          //   icon: 'warning',
          // });
        // }
        
      },
      (error: any) => {
        console.error('Error occurred while downloading:', error);
      }
    );
      
      // if (soureName == "Employment" && agentUploadedDocument != null) {
      //   const linkSource = 'data:application/zip;base64,' + agentUploadedDocument;
      //   const downloadLink = document.createElement('a');
      //   downloadLink.href = linkSource;
      //   downloadLink.download = documentname;
      //   downloadLink.click();
      // }
      // else if (agentUploadedDocument != null) {
      //   const linkSource = 'data:application/pdf;base64,' + agentUploadedDocument;
      //   const downloadLink = document.createElement('a');
      //   downloadLink.href = linkSource;
      //   downloadLink.download = documentname;
      //   downloadLink.click();
      // } else {
      //   // Swal.fire({
      //   //   title: 'No Documents Uploaded',
      //   //   icon: 'warning',
      //   // });
      // }
    }
  }
  
  filterToday() {
    this.customers.setFromDate(this.initToday);
    this.customers.setToDate(this.initToday);
    window.location.reload();
  }

  filterLast7days() {
    var date = new Date();
    date.setDate(date.getDate() - 7);
    var dateString = date.toISOString().split('T')[0];
    let getInputFromDate: any = dateString.split('-');
    let finalInputFromDate = getInputFromDate[2] + "/" + getInputFromDate[1] + "/" + getInputFromDate[0];
    this.customers.setFromDate(finalInputFromDate);
    this.customers.setToDate(this.initToday);
    window.location.reload();
  }

  filterLast30days() {
    var date = new Date();
    date.setDate(date.getDate() - 30);
    var dateString = date.toISOString().split('T')[0];
    let getInputFromDate: any = dateString.split('-');
    let finalInputFromDate = getInputFromDate[2] + "/" + getInputFromDate[1] + "/" + getInputFromDate[0];
    this.customers.setFromDate(finalInputFromDate);
    this.customers.setToDate(this.initToday);
    window.location.reload();
  }

  filterByYear() {
    var date = new Date();
    date.setFullYear(date.getFullYear() - 1);  // subtract one year instead of 30 days
    var dateString = date.toISOString().split('T')[0];
    let getInputFromDate: any = dateString.split('-');
    let finalInputFromDate = getInputFromDate[2] + "/" + getInputFromDate[1] + "/" + getInputFromDate[0];
    this.customers.setFromDate(finalInputFromDate);
    this.customers.setToDate(this.initToday);
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
    this.customers.setFromDate(finalInputFromDate);
    this.customers.setToDate(finalInputToDate);
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

    this.customers.setFromDate(finalInputFromDate);
    this.customers.setToDate(this.initToday);

    console.warn("FORMDATESTRING::", fromDateString)
    console.warn("toDateString::", toDateString)


    window.location.reload();
  }

  performSearch() {
    console.log('Search Text:', this.searchText);
    // this.pageNumber =0;
    const username = this.authService.getuserName();
    const userID = this.authService.getuserId();
    const orgId = this.authService.getOrgID();
    const role = this.authService.getRoles();
    const userRoleName = this.authService.getroleName();
    const searchData = {
      userSearchInput: this.searchText,
      agentName: username,
      organisationId: orgId,
      roleName: userRoleName,
      vendor_Id: userID,
    };
    console.log('Search Data:', searchData);

    this.orgadmin.getAllSearchDataForVendor(searchData).subscribe((data: any) => {
      this.vendorchecksupload = data.data;
      this.totalPages = 1;
      console.warn("data", data);

      const dataArray: { createdOn: string; }[] = this.vendorchecksupload;

      dataArray.forEach((element, index) => {
        const createdOnDate = new Date(element.createdOn);
        // console.log(`Element ${index + 1}: ${createdOnDate.toDateString()}`);
      });

      dataArray.sort((a, b) => {
        const dateA = new Date(a.createdOn);
        const dateB = new Date(b.createdOn);

        return dateB.getTime() - dateA.getTime();
      });
      console.log(dataArray);

    })

  }


  downloadReferenceExcelData(checkId: any) {
    const foundVendorCheck = this.vendorchecksupload.find((check: any) => check.vendorcheckId === checkId);

    if (foundVendorCheck) {
      console.warn("Vendor check found:", foundVendorCheck);
      // Your additional logic when the vendor check is found
      // Create a new workbook and add a worksheet
      const candidateName = foundVendorCheck.candidate.candidateName;

      const agentAttributeAndValue = foundVendorCheck.agentAttirbuteValue;
      const applicantId = foundVendorCheck.candidate.applicantId;
      const checkName = foundVendorCheck.source.sourceName;
      const status = foundVendorCheck.vendorCheckStatusMaster.checkStatusCode;


      const wb = XLSX.utils.book_new();
      const flattenedAttributes = agentAttributeAndValue.map((attribute: any, index: any) => ({ AgentAttribute: attribute }));
      //   const dataToExport = [
      //     { CandidateId: candidateId, CheckName: checkName, Status: status, AgentAttribute: "" },
      //     ...agentAttributeAndValue.map((attribute: any, index: any) => ({ AgentAttribute: attribute }))
      // ];

      // const dataToExport = [
      //   { ApplicantId: applicantId, CheckName: checkName, Status: status },
      //   ...agentAttributeAndValue.map((attribute: any, index: any) => ({ AgentAttribute: attribute }))
      // ];

      // const headerRow = ['ApplicantId', 'Check Name', 'Status', ...agentAttributeAndValue.map((attribute: any) => attribute.split('=')[0])];
      const headerRow = ['Applicant Id', 'Check Name', 'Status', ...(agentAttributeAndValue.map((attribute: any) => {
        const [key, value] = attribute.split('=');
        if (key === 'dateOfBirth') {
            return 'Date of Birth';
        } else if (key === 'fatherName') {
            return 'Father Name';
        }
        else if (key === 'checkType') {
          return 'Check Type';
      }
         else {
            return key;
        }
    }))];
      const dataRow = [applicantId, checkName, status, ...agentAttributeAndValue.map((attribute: any) => attribute.split('=')[1])];

      const dataToExport = [headerRow, dataRow];

      dataToExport[0] = dataToExport[0].map(header => header.toUpperCase());

      const ws = XLSX.utils.json_to_sheet(dataToExport);

      // Get the range of cells in the sheet
      const range = XLSX.utils.decode_range(ws['!ref'] || '');

      // Remove the first row by shifting the data up
      for (let row = range.s.r; row <= range.e.r; ++row) {
        for (let col = range.s.c; col <= range.e.c; ++col) {
          ws[XLSX.utils.encode_cell({ r: row, c: col })] = ws[XLSX.utils.encode_cell({ r: row + 1, c: col })];
        }
      }
      // Decrease the number of rows in the reference
      ws['!ref'] = XLSX.utils.encode_range({
        s: { c: range.s.c, r: range.s.r },
        e: { c: range.e.c, r: range.e.r - 1 }
      });


      // Set Cell Column Width Auto
      const range1 = ws['!ref'];
      const columnCount = range1 ? XLSX.utils.decode_range(range1).e.c + 1 : 0; // Get the total number of columns
      ws['!cols'] = ws['!cols'] || [];

      const rows = XLSX.utils.sheet_to_json(ws, { header: 1 }) as Array<{ [key: string]: any }>;

      for (let i = 0; i < columnCount; i++) {
        let maxColumnWidth = 0;

        for (const row of rows) {
          const cellValue = (row[i] ?? '').toString(); // Use nullish coalescing to handle undefined
          const cellContentLength = cellValue.length;
          maxColumnWidth = Math.max(maxColumnWidth, cellContentLength);
        }

        ws['!cols'][i] = ws['!cols'][i] || {};
        ws['!cols'][i].wch = maxColumnWidth + 2; // Add some padding
      }


      XLSX.utils.book_append_sheet(wb, ws, 'VendorCheck');

      const dynamicFileName = candidateName + '_' + applicantId + '_' + status + '.xlsx';

      // Write the workbook to a file
      XLSX.writeFile(wb, dynamicFileName);

      console.warn("Excel file created: foundVendorCheck.xlsx");
    } else {
      console.warn("Vendor check not found for vendorId:", foundVendorCheck);
    }

  }

  // openCertificate(modalCertificate: any, certificate: any) {
  //   this.modalService.open(modalCertificate, {
  //     centered: true,
  //     backdrop: 'static',
  //     size: 'lg',
  //   });
  //   if (certificate) {
  //     $('#viewcandidateCertificate').attr(
  //       'src',
  //       'data:application/pdf;base64,' + certificate
  //     );
  //   }
  // }

  openCertificate(modalCertificate: any, certificate: any) {
    this.modalService.open(modalCertificate, {
      centered: true,
      backdrop: 'static',
      size: 'lg',
    });
    var maxFileSize = 1000000; // 1MB
    if (certificate && certificate.length <= maxFileSize) {
      this.loadCertificatePDF(certificate);
    }
  }

  loadCertificatePDF(certificate: any) {
    this.detectContentType(certificate);
    const contentType = this.detectContentType(certificate);

    if (contentType === 'pdf') {
        const pdfUrl = 'data:application/pdf;base64,' + certificate;
        const iframe = document.getElementById('viewcandidateCertificate') as HTMLIFrameElement;
        iframe.src = pdfUrl;
    } else if (contentType === 'image') {
      const imageUrl = 'data:image/png;base64,' + certificate;
      const iframe = document.getElementById('viewcandidateCertificate') as HTMLIFrameElement;
      iframe.src = imageUrl;
      console.log('It is an image!');
    } else {
        // Unsupported content type
        console.error('Unsupported content type');
    }
  }

  viewDocFromS3(modalCertificate: any,pathkey:any){
    // console.warn("pathkey>>>>>",pathkey)
    this.orgadmin.downloadAgentUploadedDocument(pathkey).subscribe((data:any)=>{
      console.warn("data>>>>",data)
      this.modalService.open(modalCertificate, {
        centered: true,
        backdrop: 'static',
        size: 'lg',
      });
      var maxFileSize = 1000000; // 1MB
      if (pathkey && pathkey.length <= maxFileSize) {
        this.loadCertificatePDF(data.message);
      }
    })
   
  }

  detectContentType(base64String: string): string | null {
    const decodedData = atob(base64String);
    const firstByte = decodedData.charCodeAt(0);

    if (firstByte === 0x25 && decodedData.startsWith('%PDF-')) {
      return 'pdf';
    }
    else if (decodedData.startsWith('PK')) {
      return 'zip'; // ZIP file
    }
    return 'image';
  }

  controlTouched: boolean[] = [];
  markControlAsTouched(index: number) {
    this.controlTouched[index] = true;
  }
  isControlTouched(index: number) {
    return this.controlTouched[index];
  }

}
