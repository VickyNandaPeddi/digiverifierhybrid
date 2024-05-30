import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CandidateService } from 'src/app/services/candidate.service';
import Swal from 'sweetalert2';
import { CustomerService } from '../../services/customer.service';
import { AuthenticationService } from 'src/app/services/authentication.service';
@Component({
  selector: 'app-address-verification',
  templateUrl: './address-verification.component.html',
  styleUrls: ['./address-verification.component.scss']
})
export class AddressVerificationComponent implements OnInit {
  pageTitle = 'Address Verification';
  candidateCode: any;
  stat_LANDLORD: boolean=false;
  showvalidation:any=false;
  orgid:any;
  public currentresumeFile: any = File;
  candidateAddress = new FormGroup({
    candidateCode: new FormControl('', Validators.required),
    candidateRelationship: new FormControl('', Validators.required),
    candidateRelationshipEmail: new FormControl('', [Validators.email]),
    rentType: new FormControl(''),
    uploadFile: new FormControl(''),
  });
  patchCandidateCode() {
    this.candidateAddress.patchValue({
      candidateCode: this.candidateCode
    });
  }
  constructor(private candidateService: CandidateService, private navrouter: Router, private router:ActivatedRoute,
    private customers:CustomerService, public authService: AuthenticationService) { 
    this.candidateCode = this.router.snapshot.paramMap.get('candidateCode'); 
    this.orgid= this.authService.getOrgID();
    // this.customers.getShowvalidation(this.orgid).subscribe((data:any)=>{
    //   this.showvalidation=data.outcome;
    //   console.log(this.showvalidation,"result")
    // })
  }

  ngOnInit(): void {
  }
  selectRelationship(event:any){
    if(event.target.value == 'LANDLORD'){
      this.stat_LANDLORD = true;
      this.candidateAddress.controls["uploadFile"].setValidators(Validators.required);
      this.candidateAddress.controls["uploadFile"].updateValueAndValidity();
      this.candidateAddress.controls["rentType"].setValidators(Validators.required);
      this.candidateAddress.controls["rentType"].updateValueAndValidity();
      $(".required_toggle").addClass("required");
    }else{
      this.stat_LANDLORD = false;
      this.candidateAddress.controls["uploadFile"].clearValidators();
      this.candidateAddress.controls["uploadFile"].updateValueAndValidity();
      this.candidateAddress.controls["rentType"].clearValidators();
      this.candidateAddress.controls["rentType"].updateValueAndValidity();
      $(".required_toggle").removeClass("required");
    }
  }
  uploadAttachment(event:any) {
    const resumefile = event.target.files[0];
    const fileType = event.target.files[0].name.split('.').pop();
    if(fileType == 'pdf' || fileType == 'PDF'){
      this.currentresumeFile = resumefile;
    }else{
      event.target.value = null;
      Swal.fire({
        title: 'Please select .pdf file type only.',
        icon: 'warning'
      });
    }
  }
  onSubmit(candidateAddress: FormGroup){
    this.patchCandidateCode();
    const formData = new FormData();
    formData.append('candidateCafRelation', JSON.stringify(candidateAddress.value));
    formData.append('file', this.currentresumeFile);

    if (this.candidateAddress.valid) {
      this.candidateService.relationshipAddressVerification(formData).subscribe((result:any)=>{
        if(result.outcome === true){
          if(this.showvalidation==true){
          const navURL = 'candidate/cForm/'+this.candidateCode;
          this.navrouter.navigate([navURL]);
          }
          else{
            const navURL = 'candidate/cThankYou/'+this.candidateCode;
            this.navrouter.navigate([navURL]);
          }
        }
        else{
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
