import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CandidateService } from 'src/app/services/candidate.service';
import Swal from 'sweetalert2';
import { CustomerService } from '../../services/customer.service';
import { Location } from '@angular/common';
import { AuthenticationService } from 'src/app/services/authentication.service';
@Component({
  selector: 'app-uan-confirmation',
  templateUrl: './uan-confirmation.component.html',
  styleUrls: ['./uan-confirmation.component.scss']
})
export class UanConfirmationComponent implements OnInit {
  pageTitle = 'UAN Confirmation';
  candidateCode: any;
  uanVal:any;
  uanStat:boolean=false;
  showvalidation:any=false;
  orgid:any;
  getServiceConfigCodes: any=[];
  constructor(private router: ActivatedRoute, private navRouter: Router, private candidateService: CandidateService 
    ,private customers:CustomerService, private location: Location,public authService: AuthenticationService) {
    history.pushState(null, "", document.URL);
    window.addEventListener('popstate', function () {
      history.pushState(null, "", document.URL);
    });
    this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
    this.orgid= this.authService.getOrgID();
    this.candidateService.getServiceConfigCodes(this.candidateCode).subscribe((result:any)=>{
      this.getServiceConfigCodes = result.data;
      //console.log(this.getServiceConfigCodes);
    });
    // this.customers.getShowvalidation(this.orgid).subscribe((data:any)=>{
    //   this.showvalidation=data.outcome;
    //   console.log(this.showvalidation,"result")
    // })

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
  }
  checkHistoryLength(): boolean {
    return this.location.getState() === null;
  }
  ngOnInit(): void {
    
    this.uanVal = this.router.snapshot.paramMap.get('epfoStat');
    if(this.uanVal){
      if(this.uanVal == 2){
        // this.uanStat = true;
      }else if(this.uanVal == 1){
        this.uanStat = false;
      }
    }

    window.addEventListener('popstate', () => {
      if (!this.checkHistoryLength()) {
        this.location.forward();
      }
    });
  }
  radioCheck(event:any){
    const formData = new FormData();
    formData.append('candidateCode', this.candidateCode);
    formData.append('isUanSkipped', event.target.value);

    if(this.uanVal == 1){
      this.candidateService.isUanSkipped(formData).subscribe((result:any)=>{
        if(result.outcome){
          if(event.target.value=="yes"){
            // const navURL = 'candidate/epfologinnew/'+this.candidateCode;
            const navURL = 'candidate/epfologin/'+this.candidateCode;
            this.navRouter.navigate([navURL]);
          }else{
            if(this.getServiceConfigCodes){
              if(this.getServiceConfigCodes.includes('RELBILLTRUE')){
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
    }else if(this.uanVal == 2){
      // if(event.target.value=="yes"){
      //   // const navURL = 'candidate/epfologinnew/'+this.candidateCode;
      //   const navURL = 'candidate/epfologin/'+this.candidateCode;
      //   this.navRouter.navigate([navURL]);
      // }else{
        // if(this.getServiceConfigCodes){
        //   if(this.getServiceConfigCodes.includes('RELBILLTRUE')){
        //     const navURL = 'candidate/cAddressVerify/'+this.candidateCode;
        //     this.navRouter.navigate([navURL]);
        //   }else if(this.getServiceConfigCodes.includes('RELBILLFALSE')&& this.showvalidation==true){              
        //     const navURL = 'candidate/cForm/'+this.candidateCode;
        //     this.navRouter.navigate([navURL]);
        //   }
        //   else if(this.showvalidation == true){
        //     const navURL = 'candidate/cForm/'+this.candidateCode;
        //     this.navRouter.navigate([navURL]);
        //   }
        //   else{
        //     const navURL = 'candidate/cThankYou/'+this.candidateCode;
        //     this.navRouter.navigate([navURL]);
        //   }
        // }
      // }

      // added 
      const navURL = 'candidate/cThankYou/'+this.candidateCode;
      this.navRouter.navigate([navURL]);
    }



  }
  

}
