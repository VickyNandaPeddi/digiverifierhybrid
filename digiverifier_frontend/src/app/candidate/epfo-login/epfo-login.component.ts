import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CandidateService } from 'src/app/services/candidate.service';
import Swal from 'sweetalert2';
import { Location } from '@angular/common';
@Component({
  selector: 'app-epfo-login',
  templateUrl: './epfo-login.component.html',
  styleUrls: ['./epfo-login.component.scss']
})
export class EpfoLoginComponent implements OnInit {
  candidateCode: any;
  //captchaSrc:any;
  transactionid:any;
  constructor(private candidateService: CandidateService,  private router:ActivatedRoute,
    private navRouter: Router, private location: Location) {
      this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
      //EPFO Captcha
      this.candidateService.getepfoCaptcha(this.candidateCode).subscribe((data: any)=>{
        if(data.outcome === true){
          //this.captchaSrc="data:image/png;base64,"+data.data.captcha;
          this.transactionid=data.data.transactionid;
        }else{
        Swal.fire({
          title: data.message,
          icon: 'warning'
        })
      }

      })
    }
  formEPFOlogin = new FormGroup({
    candidateCode: new FormControl('', Validators.required),
    uanusername: new FormControl('', [Validators.required, Validators.minLength(12), Validators.maxLength(12)]),
    //uanpassword: new FormControl('', [Validators.required, Validators.maxLength(40)]),
    //captcha: new FormControl('', Validators.required),
    transactionid: new FormControl('', Validators.required)
  });
  checkHistoryLength(): boolean {
    return this.location.getState() === null;
  }
  patchUserValues() {
    this.formEPFOlogin.patchValue({
      candidateCode: this.candidateCode,
      transactionid: this.transactionid
    });
  }

  ngOnInit(): void {
    window.addEventListener('popstate', () => {
      if (!this.checkHistoryLength()) {
        this.location.forward();
      }
    });
  }

  onSubmit(){
    this.patchUserValues();
    if (this.formEPFOlogin.valid) {
        console.log('this.formEPFOlogin.value',this.formEPFOlogin.value);
        this.candidateService.getEpfodetail(this.formEPFOlogin.value).subscribe((result:any)=>{
          //console.log(result);
          if(result.outcome === true){
              // const navURL = 'candidate/cUanConfirm/'+this.candidateCode+'/2';
              const navURL = 'candidate/cThankYou/'+this.candidateCode;
              this.navRouter.navigate([navURL]);
          }else{
            Swal.fire({
              title: result.message,
              icon: 'warning'
            })
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
    const redirectURL = 'candidate/cUanConfirm/'+this.candidateCode+'/1';
    this.navRouter.navigate([redirectURL]);
  }
}
