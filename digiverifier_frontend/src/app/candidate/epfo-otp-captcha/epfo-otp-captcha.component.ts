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
  selector: 'app-epfo-otp-captcha',
  templateUrl: './epfo-otp-captcha.component.html',
  styleUrls: ['./epfo-otp-captcha.component.scss']
})
export class EpfoOtpCaptchaComponent implements OnInit {
  hiddenCaptcha: any;
  title = 'OTP Verification';
  candidateCode: any;
  showvalidation:any=false;
  transactionid: any;
  uanusername: any;


  constructor(private candidateService: CandidateService,private router: ActivatedRoute,private navRouter: Router,
    private location: Location) { 
    //   history.pushState(null, "", document.URL);
    // window.addEventListener('popstate', function () {
    //   history.pushState(null, "", document.URL);
    // });
    this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
    const navigation = this.navRouter.getCurrentNavigation();
    if (navigation?.extras.state) {
      const hiddenParam = navigation.extras.state.hiddenParam;
      this.hiddenCaptcha="data:image/png;base64,"+hiddenParam;

      this.transactionid = navigation.extras.state.tId;
      // console.log("CAPCHA FOR OTP::{}",hiddenParam)
      // Use hiddenParam as needed
    }

    // this.candidateService
    //   .getepfoCaptcha(this.candidateCode)
    //   .subscribe((data: any) => {
    //     if (data.outcome === true) {
    //       //this.captchaSrc="data:image/png;base64,"+data.data.captcha;
    //       this.transactionid = data.data.transactionid;
    //     } else{
    //           Swal.fire({
    //             title: data.message,
    //             icon: 'warning',
    //           });
    //     }
    //   });

    //   //getting candidate details
      this.candidateService.getCandidateDetails(this.candidateCode)
      .subscribe((data: any) => {
        if (data.outcome === true) {
          if(data.data.showvalidation){

            this.showvalidation = data.data.showvalidation;
          }
          this.uanusername= data.data.uan;
          console.log("uanusername::{}",this.uanusername);
          console.log("IS SHOW VALIDATION::{}",this.showvalidation);
        } else {
          Swal.fire({
            title: data.message,
            icon: 'warning',
          });
        }
      });
  }

  ngOnInit(): void {
  }

  formEPFOlogin = new FormGroup({
    candidateCode: new FormControl('', Validators.required),
    otp: new FormControl('', [Validators.required,Validators.minLength(6),Validators.maxLength(6)]),
    // captcha: new FormControl('', [Validators.required, Validators.maxLength(40)]),
    uanusername: new FormControl('', Validators.required),
    captcha: new FormControl('', Validators.required),
    transactionid: new FormControl('', Validators.required),
  });

  patchUserValues() {
    this.formEPFOlogin.patchValue({
      candidateCode: this.candidateCode,
      transactionid: this.transactionid,
      uanusername: this.uanusername,
    });
  }

  onSubmit() {
    this.patchUserValues();
    console.log('this.formEPFOlogin.value', this.formEPFOlogin.value);
    if (this.formEPFOlogin.valid) {

      this.candidateService
        .getEpfodetailByOTPAndCaptcha(this.formEPFOlogin.value)
        .subscribe((result: any) => {

          if (result.outcome === true) {

           if (this.showvalidation== false) {
              console.log("GOING TO THANK You PAGE::");
              const navURL = 'candidate/cThankYou/' + this.candidateCode;

              this.navRouter.navigate([navURL]);
            } else if (this.showvalidation== true) {
              console.log("GOING TO Candidate FORM::");
              const navURL = 'candidate/cForm/' + this.candidateCode;

              this.navRouter.navigate([navURL]);
            }else {
              Swal.fire({
                title: result.message,

                icon: 'warning',
              });
            }
          } else{
                Swal.fire({
                  title: result.message,
                  icon: 'warning',
                }).then((sresult) => {
                  if (sresult.isConfirmed) {
                        const navURL = 'candidate/epfologinnew/' + this.candidateCode;

                        this.navRouter.navigate([navURL]);
                      }
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

  redirect(){
    const redirectURL = 'candidate/epfologinnew/'+this.candidateCode;
    this.navRouter.navigate([redirectURL]);
  }

}
