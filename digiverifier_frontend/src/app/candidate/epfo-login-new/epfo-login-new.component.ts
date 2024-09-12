
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CandidateService } from 'src/app/services/candidate.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-epfo-login-new',
  templateUrl: './epfo-login-new.component.html',
  styleUrls: ['./epfo-login-new.component.scss']
})
export class EpfoLoginNewComponent implements OnInit {

  candidateCode: any;
  captchaSrc:any;
  transactionid:any = null;
  message: any;
  organizationName: any;
  hideCancelBtn: boolean = false;
  constructor(private candidateService: CandidateService,  private router:ActivatedRoute,
    private navRouter: Router) {
      console.log("epfo login New")
      history.pushState(null, "", document.URL);
      window.addEventListener('popstate', function () {
        history.pushState(null, "", document.URL);
      });
      this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
      //EPFO Captcha
      this.candidateService.getepfoLoginCaptcha(this.candidateCode).subscribe((data: any)=>{
        // console.log("data.data.captcha", data)
        data.data = this.candidateService.decryptData(data.data);
        // Parse the decrypted JSON string into an object
        data.data = JSON.parse(data.data);
        if(data.outcome === true){
          this.captchaSrc="data:image/png;base64,"+data.data.captcha;
          this.transactionid=data.data.transactionid;
        }else{
          this.message = data.message;
        Swal.fire({
          title: data.message,
          icon: 'warning'
        }).then((sresult) => {
          if (sresult.isConfirmed) {
            // window.location.reload();
          }
        });
      }

      })

      this.candidateService.getOrgNameByCandidateCode(this.candidateCode).subscribe((data: any)=>{
        // console.log("DATA :",data)
        this.organizationName = data.data;

        // console.log("organizationName : ",this.organizationName)
        if (this.organizationName && this.organizationName == 'Ernst & Young Pvt. Ltd.') {
          this.hideCancelBtn = true;
        }
      })
    }
  formEPFOlogin = new FormGroup({
    candidateCode: new FormControl('', Validators.required),
    uanusername: new FormControl('', [Validators.required, Validators.minLength(12), Validators.maxLength(12)]),
    uanpassword: new FormControl('', [Validators.required, Validators.maxLength(40)]),
    captcha: new FormControl('', Validators.required),
    transactionid: new FormControl('', Validators.required)
  });
  patchUserValues() {
    this.formEPFOlogin.patchValue({
      candidateCode: this.candidateCode,
      transactionid: this.transactionid
    });
  }

  ngOnInit(): void {
  }

  onSubmit(){
    this.patchUserValues();

    if(!this.transactionid) {
      Swal.fire({
        title: this.message,
        icon: 'warning'
      }).then((sresult) => {
        if (sresult.isConfirmed) {
          window.location.reload();
        }
      });
    } else if(this.formEPFOlogin.valid) {
        // this.candidateService.getEpfodetailNew(this.formEPFOlogin.value).subscribe((result:any)=>{
          this.candidateService.getepfoOTPScreenCaptcha(this.formEPFOlogin.value).subscribe((result:any)=>{
            result.data = this.candidateService.decryptData(result.data);
            // Parse the decrypted JSON string into an object
            result.data = JSON.parse(result.data);
          if(result.outcome === true){
//               console.log("OTP CAPTCHA data.data.captcha", result.data.captcha)
              const hiddenParam = result.data.captcha;
              const navURL = 'candidate/epfootpcaptcha/'+this.candidateCode;
              this.navRouter.navigate([navURL], { state: { hiddenParam: hiddenParam ,tId:this.transactionid} });
              // this.navRouter.navigate([navURL]);
          }else{
            Swal.fire({
              title: result.message,
              icon: 'warning'
            }).then((sresult) => {
            if (sresult.isConfirmed) {
                  window.location.reload();
                }
            });
          }
        });
    }else{
      Swal.fire({
        title: "Please enter the required information",
        icon: 'warning'
      })
    }
  }
  redirect(){
    // const redirectURL = 'candidate/cUanConfirm/'+this.candidateCode+'/1';
    // this.navRouter.navigate([redirectURL]);
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
