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
  constructor(private candidateService: CandidateService,  private router:ActivatedRoute,private navRouter: Router, private location: Location) { 
    this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
    this.candidateService.getServiceConfigCodes(this.candidateCode).subscribe((result:any)=>{
      this.getServiceConfigCodes = result.data;
    });
  }

    checkHistoryLength(): boolean {
      return this.location.getState() === null;
    }

  formITRlogin = new FormGroup({
    candidateCode: new FormControl('', Validators.required),
    userName: new FormControl('',[Validators.required, Validators.minLength(10), Validators.maxLength(10)]),
    password: new FormControl('',[Validators.required, Validators.maxLength(40)])
  });
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
  }

  onSubmit(){
    this.patchUserValues();
    if (this.formITRlogin.valid) {
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
