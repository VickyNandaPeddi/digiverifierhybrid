import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CandidateService } from 'src/app/services/candidate.service';
import Swal from 'sweetalert2';
@Component({
  selector: 'app-digi-locker',
  templateUrl: './digi-locker.component.html',
  styleUrls: ['./digi-locker.component.scss']
})
export class DigiLockerComponent implements OnInit {
  candidateCode: any;
  //captchaSrc:any;
  transactionid:any;
  constructor(private candidateService: CandidateService,  private router:ActivatedRoute,
    private navRouter: Router) {
      this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
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
      console.log(this.candidateCode,"candidatecode");
      this.candidateService.getDigiTansactionid(this.candidateCode).subscribe((data: any)=>{
        if(data.outcome === true){
          //this.captchaSrc="data:image/png;base64,"+data.data.captcha;
          this.transactionid=data.data.transactionid;
          console.log(this.transactionid,"transactionid");
        }else{
        Swal.fire({
          title: data.message,
          icon: 'warning'
        })
      }

      })
      
    }
  formEPFOlogin = new FormGroup({
    
    aadhaar: new FormControl('', [Validators.required, Validators.minLength(12), Validators.maxLength(12)]),
    candidateCode: new FormControl(''),
    transactionid:new FormControl(''),
    // securitypin: new FormControl('',[Validators.required, Validators.minLength(6), Validators.maxLength(6)]),
    //captcha: new FormControl('', Validators.required),
   
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
    console.log(this.formEPFOlogin.value);
    if (this.formEPFOlogin.valid) {
        this.candidateService.getDigiLockerdetail(this.formEPFOlogin.value).subscribe((result:any)=>{
          console.log(result);
          if(result.outcome === true){
              console.log("sucess")
              // const navURL = 'candidate/digiOtp/'+this.transactionid+"/"+this.candidateCode;
              // this.navRouter.navigate([navURL]);
              // console.log(this.transactionid,"tasn")
              window.location.href = result.data;
          }else{
            Swal.fire({
              title: result.message,
              icon: 'warning'
            })
          }
      });
    }else{
      Swal.fire({
        title: "Please enter 12 digit number",
        icon: 'warning'
      })
    }
    // }else{
    //   Swal.fire({
    //     title: "Please enter the required information",
    //     icon: 'warning'
    //   })
    // }
  }
  redirect(){
    const redirectURL = 'candidate/cUanConfirm/'+this.candidateCode+'/1';
    this.navRouter.navigate([redirectURL]);
  }
}
