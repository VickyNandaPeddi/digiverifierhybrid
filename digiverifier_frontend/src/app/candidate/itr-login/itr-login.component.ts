import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CandidateService } from 'src/app/services/candidate.service';
import { Location } from '@angular/common';
import Swal from 'sweetalert2';
@Component({
  selector: 'app-itr-login',
  templateUrl: './itr-login.component.html',
  styleUrls: ['./itr-login.component.scss']
})
export class ItrLoginComponent implements OnInit {
  candidateCode: any;
  getServiceConfigCodes: any=[];
  itrPanNumber: any;
  uan:any;
  transactionid: any;
  constructor(private candidateService: CandidateService,  private router:ActivatedRoute,private navRouter: Router, private location: Location) {
    history.pushState(null, "", document.URL);
    window.addEventListener('popstate', function () {
      history.pushState(null, "", document.URL);
    }); 
    this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
    this.candidateService.getCurrentStatusByCandidateCode(this.candidateCode).subscribe((result:any)=>{
      if(result.outcome==true){
//         console.log(result.data)
        const navURL = result.data.split('#/')[1];
        this.navRouter.navigate([navURL]);
      } else {
        Swal.fire({
          title: result.message,
          icon: 'warning',
        });
      }
    });
    
    this.candidateService.getServiceConfigCodes(this.candidateCode).subscribe((result:any)=>{
      this.getServiceConfigCodes = result.data;
    });

    this.updateITRPanNumber();

  }

    checkHistoryLength(): boolean {
      return this.location.getState() === null;
    }

  formITRlogin = new FormGroup({
    candidateCode: new FormControl('', Validators.required),
    userName: new FormControl("",[Validators.required, Validators.minLength(10), Validators.maxLength(10)]),
    password: new FormControl('',[Validators.required, Validators.maxLength(40)])
  });

  updateITRPanNumber() {
    this.candidateService.getCandidateDetails(this.candidateCode).subscribe((result: any) => {

      result.data = this.candidateService.decryptData(result.data);
      result.data = JSON.parse(result.data);
      // Parse the decrypted JSON string into an object
      //@ts-ignore
      this.itrPanNumber = result.data.itrPanNumber;
//       console.warn("ITRPANNUMBER::: ", this.itrPanNumber);
      this.uan = result.data.uan;
      this.updateITRPanNumberOrUserName();
    });
  }

  updateITRPanNumberOrUserName(){
//     console.warn("ITRPan::",this.itrPanNumber)
//     console.warn("this is calling::::");
    const userNameControl = this.formITRlogin.get('userName');
    if (userNameControl) {
      userNameControl.setValue(this.itrPanNumber);
    }
  }

  patchUserValues() {
    this.formITRlogin.patchValue({
      candidateCode: this.candidateCode
    });
  }

 

  ngOnInit(): void {
    window.addEventListener('popstate', () => {
      if (!this.checkHistoryLength()) {
        this.location.forward();
      }
    });

    //this.updateITRPanNumber();
  }

  

  onSubmit(){
    this.patchUserValues();
    if (this.formITRlogin.valid) {
	const userNameUpperCase = (this.formITRlogin.get('userName')?.value || '').toUpperCase();
      this.formITRlogin.patchValue({
        userName:userNameUpperCase
      })
      this.candidateService.getITRDetailsFromITRSite(this.formITRlogin.value).subscribe((result:any)=>{
        if(result.outcome === true){
          if(this.getServiceConfigCodes){
            if(this.getServiceConfigCodes.includes('EPFO')){
                const navURL = 'candidate/cUanConfirm/'+this.candidateCode+'/1';
                this.navRouter.navigate([navURL]); 
            }else if(this.getServiceConfigCodes.includes('RELBILLTRUE')){
              const navURL = 'candidate/cAddressVerify/'+this.candidateCode;
              this.navRouter.navigate([navURL]);
            }else if(this.getServiceConfigCodes.includes('RELBILLFALSE')){
              const navURL = 'candidate/cForm/'+this.candidateCode;
              this.navRouter.navigate([navURL]);
            }else{
              const navURL = 'candidate/cForm/'+this.candidateCode;
              this.navRouter.navigate([navURL]);
            }
          }
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
}
