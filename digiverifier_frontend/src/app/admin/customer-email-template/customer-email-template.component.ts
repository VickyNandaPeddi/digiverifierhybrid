import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { CustomerService } from '../../services/customer.service';
import Swal from 'sweetalert2';
import { ActivatedRoute, Router } from '@angular/router';
import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-customer-email-template',
  templateUrl: './customer-email-template.component.html',
  styleUrls: ['./customer-email-template.component.scss']
})
export class CustomerEmailTemplateComponent implements OnInit {
  animationState: string = 'start';
  public logoFile: any = File;
  viewLogo:any;
  pageTitle = 'Customer Email Templates';
  customersData: any = [];
  orgID: any;
  emailTemplateId: any;
  CustomerEmailTemplateData: any = [];
  // loaMail: boolean = false; // Initial value
  invitationMail: boolean = false
  MailTemplate: any = [];


  addEmailTemplate = new FormGroup({
    inviteMailSub: new FormControl(),
    inviteMailContent: new FormControl(),
    loaMailSub: new FormControl(),
    loaMailContent: new FormControl(),
    cwfCopyright:new FormControl(),
    cwflogo: new FormControl()
  });

  constructor(private customers: CustomerService,
    private router: ActivatedRoute,
    private routernav: Router, private modalService: NgbModal, private navRouter: Router) {
    this.orgID = this.router.snapshot.paramMap.get('organizationId');
    this.customers.getCustomersData(this.router.snapshot.params.organizationId).subscribe((result: any) => {
      this.customersData = result.data;
    });
  }

  ngOnInit(): void {
    this.animationState = 'end';

    this.customers.getCustomerEmailTemplates(this.router.snapshot.params.organizationId).subscribe((result: any) => {
      this.CustomerEmailTemplateData = result.data;
      this.emailTemplateId = result.data.emailTemplateId;
      this.viewLogo = this.CustomerEmailTemplateData.cwflogo;
      console.log("TEMPLATES DATA GET::{}", this.CustomerEmailTemplateData);
      this.addEmailTemplate = new FormGroup({
        inviteMailSub: new FormControl(result.data['inviteMailSub']),
        inviteMailContent: new FormControl(result.data['inviteMailContent']),
        loaMailSub: new FormControl(result.data['loaMailSub']),
        loaMailContent: new FormControl(result.data['loaMailContent']),
        cwfCopyright: new FormControl(result.data['cwfCopyright']),
        cwflogo: new FormControl(result.data['cwflogo'])
      });
    });
  }

  submit(addEmailTemplate: FormGroup) {

    console.log("submiting template::{}", addEmailTemplate.value);

    return this.customers.saveCustomersEmailTemplates(addEmailTemplate.value, this.orgID, 0).subscribe((result: any) => {
      console.log(result);
      if (result.outcome === true) {
        Swal.fire({
          title: result.message,
          icon: 'success',
          showCancelButton: false,
          showConfirmButton: true,
          showCloseButton: true,
          footer: `
    <button class="btn btn-primary load-mail loa-mail-button">LOA Mail</button>
    <button class="btn btn-primary load-invitation mail-invitation-button">Mail Invitation</button>
  `,
          customClass: {
            closeButton: 'custom-close-class',
            confirmButton: 'btn btn-primary',
            cancelButton: 'custom-cancel-class',
          },
          buttonsStyling: false,
        }).then((result) => {
          if (result.isConfirmed) {
            const navURL = 'admin/admindashboard';
            this.navRouter.navigate([navURL]);
          }
        });

        document.querySelector('.loa-mail-button')?.addEventListener('click', () => {
          // loaMail: boolean = false;

          const loaMail = true;
          const invitationMail = false

          console.warn("ORGID::::", this.orgID);

          this.customers.getCustmerEmailTemplatesForReview(this.orgID, loaMail, invitationMail).subscribe((result) => {
            console.warn("RESULT::::::>>>", result)
            this.MailTemplate = result;
            // Open SweetAlert2 modal with the result data
            Swal.fire({
              title: 'LOA Mail Templates',
              html: `
              ${this.MailTemplate.message}
                `,
              icon: 'info',
              showCloseButton: true,
              showCancelButton: true,
              showConfirmButton: true,
              cancelButtonText: 'Edit',
              customClass: {
                closeButton: 'custom-close-class',
                cancelButton: 'btn btn-primary',
                confirmButton: 'btn btn-primary',
              },
              buttonsStyling: false,
              allowOutsideClick: false,
            }).then((result) => {
              if (result.isConfirmed) {
                const navURL = 'admin/admindashboard';
                this.navRouter.navigate([navURL]);
              } else if (result.isDismissed || result.isDenied) {
                // Handle cancel button click
                const editUrl = `admin/custemailtemp/${this.orgID}`;
                this.navRouter.navigate([editUrl]);
                // location.reload();
              }
            })

          })
          console.log('Load Mail clicked');
        });

        document.querySelector('.mail-invitation-button')?.addEventListener('click', () => {
          // this.invitationMail = true

          const loaMail = false;
          const invitationMail = true;
          this.customers.getCustmerEmailTemplatesForReview(this.orgID, loaMail, invitationMail).subscribe((result) => {
            console.warn("RESULT::::::>>>", result)
            this.MailTemplate = result;
            Swal.fire({
              title: 'Mail Invitation Templates',
              html: `
              ${this.MailTemplate.message}
                `,
              icon: 'info',
              showCloseButton: true,
              showCancelButton: true,
              showConfirmButton: true,
              cancelButtonText: 'Edit',
              customClass: {
                closeButton: 'custom-close-class',
                cancelButton: 'btn btn-primary',
                confirmButton: 'btn btn-primary',
              },
              buttonsStyling: false,
              allowOutsideClick: false,
            }).then((result) => {
              if (result.isConfirmed) {
                const navURL = 'admin/admindashboard';
                this.navRouter.navigate([navURL]);
              } else if (result.isDismissed || result.isDenied) {
                // Handle cancel button click
                const editUrl = `admin/custemailtemp/${this.orgID}`;
                this.navRouter.navigate([editUrl]);
                // location.reload();
              }
            })

          })

          console.log('Mail Invitation clicked');
        });

      } else {
        Swal.fire({
          title: result.message,
          icon: 'warning'
        })
      }
    });
  }

  Update(addEmailTemplate: FormGroup) {
    console.log("Update templates::{}", addEmailTemplate.value);

    // console.warn("this.logoFile : ",this.logoFile)
  //   this.addEmailTemplate.patchValue({
  //     cwflogo: this.logoFile
  // });

      return this.customers.saveCustomersEmailTemplates1(addEmailTemplate.value, this.orgID, this.emailTemplateId).subscribe((result: any) => {
      console.log(result);
      if (result.outcome === true) {
        Swal.fire({
          title: result.message,
          icon: 'success',
          showCancelButton: false,
          showConfirmButton: true,
          showCloseButton: true,
          footer: `
    <button class="btn btn-primary load-mail loa-mail-button">Review LOA Mail</button>
    <button class="btn btn-primary load-invitation mail-invitation-button">Review Mail Invitation</button>
  `,
          customClass: {
            closeButton: 'custom-close-class',
            confirmButton: 'btn btn-primary',
            cancelButton: 'custom-cancel-class',
          },
          buttonsStyling: false,
        }).then((result) => {
          if (result.isConfirmed) {
            const navURL = 'admin/admindashboard';
            this.navRouter.navigate([navURL]);
          }
        });

        document.querySelector('.loa-mail-button')?.addEventListener('click', () => {
          // this.loaMail = true;

          const loaMail = true;
          const invitationMail = false;

          console.warn("ORGID::::", this.orgID);

          this.customers.getCustmerEmailTemplatesForReview(this.orgID, loaMail, invitationMail).subscribe((result) => {
            console.warn("RESULT::::::>>>", result)
            this.MailTemplate = result;
            // Open SweetAlert2 modal with the result data
            Swal.fire({
              title: 'LOA Mail Templates',
              html: `
                  ${this.MailTemplate.message}
                `,
              icon: 'info',
              showCloseButton: true,
              showCancelButton: true,
              showConfirmButton: true,
              cancelButtonText: 'Edit',
              customClass: {
                closeButton: 'custom-close-class',
                confirmButton: 'btn btn-primary',
                cancelButton: 'btn btn-primary'
              },
              buttonsStyling: false,
              allowOutsideClick: false,
            }).then((result) => {
              if (result.isConfirmed) {
                const navURL = 'admin/admindashboard';
                this.navRouter.navigate([navURL]);
              } else if (result.isDismissed || result.isDenied) {
                // Handle cancel button click
                const editUrl = `admin/custemailtemp/${this.orgID}`;
                this.navRouter.navigate([editUrl]);
                // location.reload();
              }
            })

          })
          console.log('Load Mail clicked');
        });

        document.querySelector('.mail-invitation-button')?.addEventListener('click', () => {
          // this.invitationMail = true

          const loaMail = false;
          const invitationMail = true;

          this.customers.getCustmerEmailTemplatesForReview(this.orgID, loaMail, invitationMail).subscribe((result) => {
            console.warn("RESULT::::::>>>", result)
            this.MailTemplate = result;
            Swal.fire({
              title: 'Mail Invitation Templates',
              html: `
              ${this.MailTemplate.message}
                `,
              icon: 'info',
              showCloseButton: true,
              showCancelButton: true,
              showConfirmButton: true,
              cancelButtonText: 'Edit',
              customClass: {
                closeButton: 'custom-close-class',
                confirmButton: 'btn btn-primary',
                cancelButton: 'btn btn-primary'
              },
              buttonsStyling: false,
              allowOutsideClick: false,
            }).then((result) => {
              if (result.isConfirmed) {
                const navURL = 'admin/admindashboard';
                this.navRouter.navigate([navURL]);
              } else if (result.isDismissed || result.isDenied) {
                // Handle cancel button click
                const editUrl = `admin/custemailtemp/${this.orgID}`;
                this.navRouter.navigate([editUrl]);
                // location.reload();
              }
            })

          })

          console.log('Mail Invitation clicked');
        });
      } else {
        Swal.fire({
          title: result.message,
          icon: 'warning'
        })
      }
    });
  }

  onEditCustInfo() {
    const onEditCustInfoUrl = 'admin/custedit/' + this.orgID;
    console.log("onEditCustInfo URL::{}", onEditCustInfoUrl)
    this.routernav.navigate([onEditCustInfoUrl]);
  }

  onEditCustBill() {
    const onEditCustBillUrl = 'admin/custbill/' + this.orgID;
    console.log("onEditCustInfo URL::{}", onEditCustBillUrl)
    this.routernav.navigate([onEditCustBillUrl]);
  }

  onEditCustEmailTemp() {
    const onEditCustEmailTempUrl = 'admin/custemailtemp/' + this.orgID;
    console.log("onEditCustInfo URL::{}", onEditCustEmailTempUrl)
    this.routernav.navigate([onEditCustEmailTempUrl]);
  }

  isSubmitDisabled(): boolean {
    return Object.values(this.addEmailTemplate.value).every(value => !value) || this.CustomerEmailTemplateData.emailTemplateId !== null;
  }

  onEditCustScope(){
    const onEditCustScopeUrl = 'admin/custscopeconfig/'+this.orgID;
    console.log("onEditCustInfo URL::{}",onEditCustScopeUrl)
    this.routernav.navigate([onEditCustScopeUrl]);
   }

   selectLogo(event:any) {
    const fileType = event.target.files[0].name.split('.').pop();
    const file = event.target.files[0];
    if(fileType == 'jpeg' || fileType == 'JPEG' || fileType == 'png' || fileType == 'PNG' || fileType == 'jpg' || fileType == 'JPG'){
      // this.logoFile = file;
      if (file) {
        this.convertFileToBase64(file).then((base64) => {
          this.logoFile = base64;
          this.addEmailTemplate.patchValue({
            cwflogo: this.logoFile
          });
          // console.log(this.logoFile); // For debugging
        }).catch(error => {
          console.error('Error converting file to Base64:', error);
        });
      }
    }else{
      event.target.value = null;
      Swal.fire({
        title: 'Please select .jpeg, .jpg, .png file type only.',
        icon: 'warning'
      });
    }  
  }

  openModal(modalData:any){
    this.modalService.open(modalData, {
     centered: true,
     backdrop: 'static'
    });
 }

 private convertFileToBase64(file: File): Promise<string | null> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    
    reader.onloadend = () => {
      const base64 = reader.result as string;
      resolve(base64);
    };
    
    reader.onerror = (error) => {
      reject(error);
    };
    
    reader.readAsDataURL(file);
  });
}
 
}