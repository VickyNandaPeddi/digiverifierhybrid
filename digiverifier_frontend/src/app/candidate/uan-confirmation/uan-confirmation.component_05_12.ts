import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CandidateService } from 'src/app/services/candidate.service';
import Swal from 'sweetalert2';
import { CustomerService } from '../../services/customer.service';
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
  showvalidation:any;
  orgid:any;
  show:any;

  getServiceConfigCodes: any=[];
  constructor(private router: ActivatedRoute, private navRouter: Router, private candidateService: CandidateService,
    private customers:CustomerService, public authService: AuthenticationService) {
    this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
    this.orgid= this.authService.getOrgID();
    this.candidateService.getServiceConfigCodes(this.candidateCode).subscribe((result:any)=>{
      this.getServiceConfigCodes = result.data;
      //console.log(this.getServiceConfigCodes);
    });
    this.customers.getShowvalidation(this.orgid).subscribe((data:any)=>{
      this.showvalidation=data.outcome;
//       console.log(this.showvalidation,"----------------------------------------result")
    })
   }

  ngOnInit(): void {
    // this.show=this.showvalidation
    // let show:any;
    this.customers.getShowvalidation(this.orgid).subscribe((data:any)=>{
      this.showvalidation=data.outcome;


//     console.log(this.showvalidation,"----------------------------------------result")
    this.uanVal = this.router.snapshot.paramMap.get('epfoStat');
    if(this.uanVal){
      if(this.uanVal == 2){
        this.uanStat = true;
        if(this.getServiceConfigCodes){
          if(this.getServiceConfigCodes.includes('RELBILLTRUE')){
            const navURL = 'candidate/cAddressVerify/'+this.candidateCode;
            this.navRouter.navigate([navURL]);
          }else if(this.getServiceConfigCodes.includes('RELBILLFALSE') && this.showvalidation==true){
//             console.log("--------------------cform")
            const navURL = 'candidate/cForm/'+this.candidateCode;
            this.navRouter.navigate([navURL]);
          }
          else if(this.showvalidation == true){
//             console.log("--------------------cform1111")
            const navURL = 'candidate/cForm/'+this.candidateCode;
            this.navRouter.navigate([navURL]);
          }
          else{
//             console.log("--------------------thank")
            const navURL = 'candidate/cThankYou';
            this.navRouter.navigate([navURL]);
          }
        }
      }else if(this.uanVal == 1){
        this.uanStat = false;
      }

    }
  })
  }
  radioCheck(event:any){
    const formData = new FormData();
    formData.append('candidateCode', this.candidateCode);
    formData.append('isUanSkipped', event.target.value);

    if(this.uanVal == 1){
      this.candidateService.isUanSkipped(formData).subscribe((result:any)=>{
        result.data = this.candidateService.decryptData(result.data);
        // Parse the decrypted JSON string into an object
        result.data = JSON.parse(result.data);
        if(result.outcome){
          if(event.target.value=="yes"){
            // const navURL = 'candidate/epfologin/'+this.candidateCode;
            const navURL = 'candidate/epfologinnew/'+this.candidateCode;
            this.navRouter.navigate([navURL]);
          }else{
            if(this.getServiceConfigCodes){
              if(this.getServiceConfigCodes.includes('RELBILLTRUE')){
                const navURL = 'candidate/cAddressVerify/'+this.candidateCode;
                this.navRouter.navigate([navURL]);
              }else if(this.getServiceConfigCodes.includes('RELBILLFALSE') && this.showvalidation==true){
                const navURL = 'candidate/cForm/'+this.candidateCode;
                this.navRouter.navigate([navURL]);
              } else if(this.showvalidation == true){
//                 console.log("--------------------cform1111")
                const navURL = 'candidate/cForm/'+this.candidateCode;
                this.navRouter.navigate([navURL]);
              }
              else{
//                 console.log("--------------------thank")
                const navURL = 'candidate/cThankYou';
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
      if(event.target.value=="yes"){
        const navURL = 'candidate/epfologin/'+this.candidateCode;
        this.navRouter.navigate([navURL]);
      }else{
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
    }



  }


}
