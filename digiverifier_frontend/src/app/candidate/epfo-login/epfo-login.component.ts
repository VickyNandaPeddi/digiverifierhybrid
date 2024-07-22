import { Component, OnInit } from '@angular/core';
import {
  FormGroup,
  FormControl,
  FormBuilder,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CandidateService } from 'src/app/services/candidate.service';
import Swal from 'sweetalert2';
import { Location } from '@angular/common';
@Component({
  selector: 'app-epfo-login',
  templateUrl: './epfo-login.component.html',
  styleUrls: ['./epfo-login.component.scss'],
})
export class EpfoLoginComponent implements OnInit {
  candidateCode: any;
  //captchaSrc:any;
  transactionid: any;
  enterUanInQcPending: any;
  candidateEXPData: any = [];
  CandidateFormData: any = [];

  showvalidation:any=false;
  constructor(
    private candidateService: CandidateService,
    private router: ActivatedRoute,
    private navRouter: Router,
    private location: Location
  ) {
    history.pushState(null, "", document.URL);
    window.addEventListener('popstate', function () {
      history.pushState(null, "", document.URL);
    });
    this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
    //EPFO Captcha
    // this.candidateService
    //   .getepfoCaptcha(this.candidateCode)
    //   .subscribe((data: any) => {
    //     if (data.outcome === true) {
    //       //this.captchaSrc="data:image/png;base64,"+data.data.captcha;
    //       this.transactionid = data.data.transactionid;
    //     } else if(data.outcome === false && !this.enterUanInQcPending) {
          
    //       const navURL= 'candidate/epfologinnew/' + this.candidateCode;
    //       Swal.fire({
    //         title: data.message + " Or try using Captcha",
    //         icon: 'warning',
    //       }).then((sresult) => {
    //         if (sresult.isConfirmed) {
    //           this.navRouter.navigate([navURL]);
    //         }
    //       });
    //     }else{
    //           Swal.fire({
    //             title: data.message,
    //             icon: 'warning',
    //           });
    //     }
    //   });

      //getting candidate details
      this.candidateService.getCandidateDetails(this.candidateCode)
      .subscribe((data: any) => {
        if (data.outcome === true) {
          if(data.data.showvalidation){

            this.showvalidation = data.data.showvalidation;
          }
          
          console.log("IS SHOW VALIDATION::{}",this.showvalidation);
        } else {
          Swal.fire({
            title: data.message,
            icon: 'warning',
          });
        }
      });

      const navURL= 'candidate/epfologinnew/' + this.candidateCode;
          Swal.fire({
            title: "EPFO Site is down, Please try another..!",
            icon: 'warning',
          }).then((sresult) => {
            if (sresult.isConfirmed) {
              this.navRouter.navigate([navURL]);
            }
          });

  }
  formEPFOlogin = new FormGroup({
    candidateCode: new FormControl('', Validators.required),
    uanusername: new FormControl('', [
      Validators.required,
      Validators.minLength(12),
      Validators.maxLength(12),
    ]),
    //uanpassword: new FormControl('', [Validators.required, Validators.maxLength(40)]),
    //captcha: new FormControl('', Validators.required),
    transactionid: new FormControl('', Validators.required),
    enterUanInQcPending: new FormControl(''),
  });
  checkHistoryLength(): boolean {
    return this.location.getState() === null;
  }
  patchUserValues() {
    this.formEPFOlogin.patchValue({
      candidateCode: this.candidateCode,
      transactionid: this.transactionid,
      enterUanInQcPending: this.enterUanInQcPending,
    });
  }

  ngOnInit(): void {
    window.addEventListener('popstate', () => {
      if (!this.checkHistoryLength()) {
        this.location.forward();
      }
    });

    this.router.queryParams.subscribe((params) => {
      this.enterUanInQcPending = params.enterUanInQcPending === 'true'; // Assuming the parameter is passed as a string
      // Rest of your code...
      console.log(this.enterUanInQcPending)
    });

    if(!this.enterUanInQcPending) {
      this.candidateService.getCurrentStatusByCandidateCode(this.candidateCode).subscribe((result:any)=>{
        if(result.outcome==true){
          console.log(result.data)
          const navURL = result.data.split('#/')[1];
          this.navRouter.navigate([navURL]);
        } else {
          Swal.fire({
            title: result.message,
            icon: 'warning',
          });
        }
      });
    }

  }

  onSubmit() {
    this.patchUserValues();

    if (this.formEPFOlogin.valid) {
      console.log('this.formEPFOlogin.value', this.formEPFOlogin.value);

      this.candidateService
        .getEpfodetail(this.formEPFOlogin.value)
        .subscribe((result: any) => {
          //console.log(result);

          if (this.enterUanInQcPending == true) {
            this.candidateService
              .enterUanDataInQcPending(
                this.candidateCode,
                this.enterUanInQcPending
              )
              .subscribe((data: any) => {
                this.CandidateFormData = data.data;

                this.candidateEXPData =
                  this.CandidateFormData.candidateCafExperienceDto;

                console.warn('candidateEXPData::>>>>', this.candidateEXPData);
              });
          }

          if (result.outcome === true) {
            // const navURL = 'candidate/cUanConfirm/'+this.candidateCode+'/2';

            // const navURL = 'candidate/cThankYou/'+this.candidateCode;

            if (this.enterUanInQcPending == true) {
              const navURL = 'admin/cReportApproval/' + this.candidateCode;

              this.navRouter.navigate([navURL]).then(() => {
                setTimeout(() => {
                  window.location.reload();
                }, 2000);
              });
            } else if (this.enterUanInQcPending == false && this.showvalidation== false) {
              console.log("GOING TO THANK You PAGE::");
              const navURL = 'candidate/cThankYou/' + this.candidateCode;

              this.navRouter.navigate([navURL]);
            } else if (this.enterUanInQcPending == false && this.showvalidation== true) {
              console.log("GOING TO Candidate FORM::");
              const navURL = 'candidate/cForm/' + this.candidateCode;

              this.navRouter.navigate([navURL]);
            }else {
              Swal.fire({
                title: result.message,

                icon: 'warning',
              });
            }
          } else if(result.outcome === false && !this.enterUanInQcPending) {
          
            const navURL= 'candidate/epfologinnew/' + this.candidateCode;
            Swal.fire({
              title: result.message + " Or try using Captcha",
              icon: 'warning',
            }).then((sresult) => {
              if (sresult.isConfirmed) {
                this.navRouter.navigate([navURL]);
              }
            });
          }else{
                Swal.fire({
                  title: result.message,
                  icon: 'warning',
                });
          }
        });
    } else {
      Swal.fire({
        title: 'Please enter the required information',

        icon: 'warning',
      });
    }
  }
  redirect() {
    if(this.enterUanInQcPending){
      console.warn("EnterUanInQcPending:::",this.enterUanInQcPending)
      const redirectURL = 'admin/cReportApproval/' + this.candidateCode;
      this.navRouter.navigate([redirectURL]);
    }
    else{
      this.candidateService.cancelEpfoLogin(this.candidateCode).subscribe((result:any)=>{
        if(result.outcome) {      
          const redirectURL = 'candidate/cUanConfirm/' + this.candidateCode + '/1';
          this.navRouter.navigate([redirectURL]);
        }else{
          Swal.fire({
            title: result.message,
            icon: 'warning'
          })
        }
      });
    }
  }
}
