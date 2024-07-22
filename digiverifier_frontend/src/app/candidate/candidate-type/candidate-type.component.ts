import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { CandidateService } from 'src/app/services/candidate.service';
import { CustomerService } from '../../services/customer.service';
import { Location } from '@angular/common';
import { AuthenticationService } from 'src/app/services/authentication.service';
@Component({
  selector: 'app-candidate-type',
  templateUrl: './candidate-type.component.html',
  styleUrls: ['./candidate-type.component.scss']
})
export class CandidateTypeComponent implements OnInit {
  pageTitle = 'Candidate Type';
  candidateCode: any;
  orgid:any;
  role:any;
  showvalidation:any=false;
  getServiceConfigCodes: any=[];
  formCtype = new FormGroup({
    candidateCode: new FormControl('', Validators.required),
    isFresher: new FormControl('', Validators.required)
  });
  constructor(private navRouter: Router, private router:ActivatedRoute, private candidateService: CandidateService,private customers:CustomerService, private location: Location
    ,public authService: AuthenticationService) {

    history.pushState(null, "", document.URL);
    window.addEventListener('popstate', function () {
      history.pushState(null, "", document.URL);
    });
    
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
    this.orgid= this.authService.getOrgID();
    this.role= this.authService.getroleName();
    console.log(this.orgid,"----------------",this.role)
    this.candidateService.getServiceConfigCodes(this.candidateCode).subscribe((result:any)=>{
      this.getServiceConfigCodes = result.data;
      
    });
    // this.customers.getShowvalidation(this.orgid).subscribe((data:any)=>{
    //   this.showvalidation=data.outcome;
    //   console.log(this.showvalidation,"result")
    // })
  }
  checkHistoryLength(): boolean {
    return this.location.getState() === null;
  }
  ngOnInit(): void {
    window.addEventListener('popstate', () => {
      if (!this.checkHistoryLength()) {
        this.location.forward();
      }
    });
  }
  fresherClick(event:any, isFresher:any){
    console.log(isFresher)
    const btnStat = event.target.id;
    console.log(btnStat);
    const formData = new FormData();
    formData.append('candidateCode', this.candidateCode);
    formData.append('isFresher', isFresher);
    return this.candidateService.postIsFresher(formData).subscribe((result:any)=>{
          if(result.outcome === true){
            if(result.data.isFresher == true){
              if(this.getServiceConfigCodes){
                if(this.getServiceConfigCodes.includes('RELBILLTRUE')){
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
            }
            else if(result.data.isFresher == false){
              if(this.getServiceConfigCodes){
                if(this.getServiceConfigCodes.includes('ITR')){
                  const navURL = 'candidate/itrlogin/'+this.candidateCode;
                  this.navRouter.navigate([navURL]);
                }else if(this.getServiceConfigCodes.includes('EPFO')){                
                  const navURL = 'candidate/cUanConfirm/'+this.candidateCode+'/1';
                  this.navRouter.navigate([navURL]);
                }else if(this.getServiceConfigCodes.includes('RELBILLTRUE')){                
                  const navURL = 'candidate/cAddressVerify/'+this.candidateCode;
                  this.navRouter.navigate([navURL]);
                }else if(this.getServiceConfigCodes.includes('RELBILLFALSE')&& this.showvalidation==true){              
                  const navURL = 'candidate/cForm/'+this.candidateCode;
                  this.navRouter.navigate([navURL]);
                }
                else if(this.showvalidation == true){
                  const navURL = 'candidate/cForm/'+this.candidateCode;
                  this.navRouter.navigate([navURL]);
                }
                else{
                  const navURL = 'candidate/cThankYou/'+this.candidateCode;
                  this.navRouter.navigate([navURL]);
                }
              }
              
            }
            
          }else{
            Swal.fire({
              title: result.message,
              icon: 'warning'
            })
          }
       });
  }

}
