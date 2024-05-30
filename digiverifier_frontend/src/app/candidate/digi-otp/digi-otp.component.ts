import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CandidateService } from 'src/app/services/candidate.service';
import Swal from 'sweetalert2';
@Component({
  selector: 'app-digi-otp',
  templateUrl: './digi-otp.component.html',
  styleUrls: ['./digi-otp.component.scss']
})
export class DigiOtpComponent implements OnInit {
  candidateCode: any;
  //captchaSrc:any;
  transactionid:any;
  accesstoken:any;
  code:any;
  constructor(private candidateService: CandidateService,  private router:ActivatedRoute,
    private navRouter: Router) {
      this.transactionid = this.router.snapshot.paramMap.get('transactionid');
      this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
      console.log(this.transactionid,"transactionid");
      // this.candidateService.getDigiTansactionid(this.candidateCode).subscribe((data: any)=>{
      //   if(data.outcome === true){
      //     //this.captchaSrc="data:image/png;base64,"+data.data.captcha;
      //     this.transactionid=data.data.transactionid;
      //     console.log(this.transactionid,"transactionid");
      //   }else{
      //   Swal.fire({
      //     title: data.message,
      //     icon: 'warning'
      //   })
      // }

      // })
      
    }
  formEPFOlogin = new FormGroup({
    
    otp: new FormControl('', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]),
   
    transactionid:new FormControl(''),
    candidateCode:new FormControl(''),
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
        this.candidateService.getDigiLockerAlldetail(this.formEPFOlogin.value).subscribe((result:any)=>{
          console.log(result);
          this.accesstoken=result.message
          this.code=result.status
          if(result.outcome === true){
            // if(result.data === "Issued documents Information retrieved successfully, dgree not in Issued documents"){
            //     console.log("sucess")
                
            //     // const navURL = 'candidate/cType/'+this.candidateCode;
            //     const navURL = 'candidate/digiDoc/'+this.candidateCode+"/"+this.accesstoken+"/"+this.code;
            //     this.navRouter.navigate([navURL]);
            //     // window.location.href = result.data;
            // }else{
            //   const navURL = 'candidate/cType/'+this.candidateCode;
            //   this.navRouter.navigate([navURL]);
            // }

              const navURL = 'candidate/cType/'+this.candidateCode;
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
    // else{
    //   Swal.fire({
    //     title: "Please enter the required information",
    //     icon: 'warning'
    //   })
    // }
  
  redirect(){
    const redirectURL = 'candidate/cUanConfirm/'+this.candidateCode+'/1';
    this.navRouter.navigate([redirectURL]);
  }
}

