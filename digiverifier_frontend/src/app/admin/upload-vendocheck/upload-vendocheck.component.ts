import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { CustomerService } from '../../services/customer.service';
import { OrgadminService } from 'src/app/services/orgadmin.service';
import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CandidateService } from 'src/app/services/candidate.service';
import {
  FormGroup,
  FormControl,
  FormBuilder,
  Validators,
} from '@angular/forms';
import Swal from 'sweetalert2';
import { NgbCalendar, NgbDate } from '@ng-bootstrap/ng-bootstrap';

import { Router } from '@angular/router';
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
  proofDocumentNew: any;
  venderAttributeValue: any[] = [];
  venderSourceId: any;
  venderAttributeCheck: any = [];
  vendorAttributeCheckMapped: any[] = [];

  vendorAttributeListForm: any[] = [];

  // public proofDocumentNew: any = File;
  closeModal: string | undefined;

  // selectedFiles: any;
  tep: any = [1];
  // vendorlist:any;
  tmp: any;
  orgID: any;

  vendorlist = new FormGroup({
    vendorcheckId: new FormControl(''),
    documentname: new FormControl(''),
    colorid: new FormControl(''),
    value: new FormControl(''),
    vendorCheckStatusMasterId: new FormControl('')
  });

  utilizationReportFilter = new FormGroup({
    fromDate: new FormControl('', Validators.required),
    toDate: new FormControl('', Validators.required),
    // sourceId: new FormControl('', Validators.required)
  });
  getVenorcheckStatus: any[] = [];
  vendorCheckStatusMasterId: any;

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
    private modalService: NgbModal
  ) {
    this.orgID = this.authService.getuserId();
    this.getToday = calendar.getToday();
    console.log(this.orgID);
    this.customers
      .getallVendorCheckDetails(this.orgID)
      .subscribe((data: any) => {
        console.log(data);
        this.vendorchecksupload = data.data;
        // this.proofDocumentNew=this.vendorchecksupload.proofDocumentNew;
        let getfromDate = data.data.fromDate.split('/');
        this.setfromDate = {
          day: +getfromDate[0],
          month: +getfromDate[1],
          year: +getfromDate[2],
        };
        this.getMinDate = this.setfromDate;

        let gettoDate = data.data.toDate.split('/');
        this.settoDate = {
          day: +gettoDate[0],
          month: +gettoDate[1],
          year: +gettoDate[2],
        };
        console.log(
          'getfromDate, gettoDate',
          this.getMinDate,
          this.settoDate,
          this.fromDate,
          this.toDate
        );

        this.start_date = 'No Date Filter'; //data.data.fromDate!=null?data.data.fromDate.split('-').join('/'):''
        this.end_date = 'No Date Filter'; //data.data.toDate!=null?data.data.toDate.split('-').join('/'):''

        console.log('vendorchecksupload', this.vendorchecksupload);
      });
    this.candidateService.getColors().subscribe((data: any) => {
      this.getColors = data.data;
      console.log(this.getColors);
    });
  }

  ngOnInit(): void {
    this.customers.getVenorcheckStatus().subscribe((data: any) => {
      if(data.data) {
        this.getVenorcheckStatus = data.data.filter((temp: any)=> {
          if(temp.checkStatusCode != 'INPROGRESS') {
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

  onSubmitFilter() {
    this.fromDate = this.fromDate != null ? this.fromDate : '';
    this.toDate = this.toDate != null ? this.toDate : '';
    this.utilizationReportFilter.patchValue({
      fromDate: this.fromDate,
      toDate: this.toDate,
    });
    this.customers
      .postallVendorCheckDetails(this.utilizationReportFilter.value)
      .subscribe((data: any) => {
        if (data.outcome === true) {
          this.vendorchecksupload = data.data;
          console.log('this.vendorchecksupload', this.vendorchecksupload);
          this.start_date =
            data.data.fromDate != null
              ? data.data.fromDate.split('-').join('/')
              : '';
          this.end_date =
            data.data.toDate != null
              ? data.data.toDate.split('-').join('/')
              : '';
        }
      });
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
    } else {
      event.target.value = null;
      Swal.fire({
        title: 'Please select .jpeg, .jpg, .png file type only.',
        icon: 'warning',
      });
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

    const venderAttributeValue = this.vendorAttributeListForm.reduce(
      (obj, item) => {
        obj[item.label] = item.value;

        return obj;
      },
      {}
    );

    //  delete agentAttributeValues.value

    //  this.vendorAttributeCheckMapped = {...this.vendorlist.value, ...venderAttributeValue}

    this.vendorAttributeCheckMapped = { ...venderAttributeValue };

    // const finalValues = JSON.stringify(this.educationAgentAttributeCheckMapped);

    // console.log("finalValues",finalValues)

    console.log(
      ' vendorAttributeCheckMapped:::',
      this.vendorAttributeCheckMapped
    );

    console.warn('vendorAttributeCheckMapped===>', venderAttributeValue);

    const mergedData = {
      ...this.vendorAttributeCheckMapped,
    };

    //  formData.append('vendorchecks', JSON.stringify(this.forAddressCrimnalGlobal.value ))

    //  formData.append('vendorchecks', JSON.stringify(agentAttributeValues ))

    //  formData.append('vendorchecks', JSON.stringify(this.forAddressCrimnalGlobal.value ))

    const formData = new FormData();
    formData.append('vendorchecks', JSON.stringify(this.vendorlist.value));

    formData.append('vendorAttributesValue', JSON.stringify(mergedData));

    console.warn('mergedData++++++++++++++++++++', mergedData);
    // ----------------------------------------------------------------------------------------------------------

    // formData.append('vendorchecks', JSON.stringify(this.vendorlist.value));

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

  downloadPdf(agentUploadedDocument: any) {
    console.log(agentUploadedDocument, '******************************');
    if (agentUploadedDocument != null) {
      const linkSource = 'data:application/pdf;base64,' + agentUploadedDocument;
      const downloadLink = document.createElement('a');
      downloadLink.href = linkSource;
      downloadLink.download = 'Download.pdf';
      downloadLink.click();
    } else {
      Swal.fire({
        title: 'No Documents Uploaded',
        icon: 'warning',
      });
    }
  }
}
