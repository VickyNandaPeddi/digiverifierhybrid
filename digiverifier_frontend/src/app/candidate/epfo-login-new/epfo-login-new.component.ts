
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
  transactionid:any;
  constructor(private candidateService: CandidateService,  private router:ActivatedRoute,
    private navRouter: Router) {
      console.log("epfo login New")
      this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
      //EPFO Captcha
      this.candidateService.getepfoCaptcha(this.candidateCode).subscribe((data: any)=>{
        console.log("data.data.captcha", data)
        if(data.outcome === true){
          this.captchaSrc="data:image/png;base64,"+data.data.captcha;
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
    if (this.formEPFOlogin.valid) {
        console.log('this.formEPFOlogin.value',this.formEPFOlogin.value);
        this.candidateService.getEpfodetailNew(this.formEPFOlogin.value).subscribe((result:any)=>{
          //console.log(result);
          if(result.outcome === true){
              const navURL = 'candidate/cUanConfirm/'+this.candidateCode+'/2';
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
