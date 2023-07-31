import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { CandidateService } from 'src/app/services/candidate.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-verify-relation-address',
  templateUrl: './verify-relation-address.component.html',
  styleUrls: ['./verify-relation-address.component.scss']
})
export class VerifyRelationAddressComponent implements OnInit {
  candidateCode: any;
  formRelationAccept = new FormGroup({
    candidateCode: new FormControl('', Validators.required)
  });
  patchUserValues() {
    this.formRelationAccept.patchValue({
      candidateCode: this.candidateCode
    });
  }
  constructor(private candidateService: CandidateService, private router:ActivatedRoute) { 
    
  }

  ngOnInit(): void {
    this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
  }
  btnRelationAccept() {
    this.patchUserValues();
    return this.candidateService.verifyRelation(this.formRelationAccept.value).subscribe((result:any)=>{
      if(result.outcome==true){
        const resuldata = result.data.split('#');
        if(resuldata[1] == "EXPIRED"){
          Swal.fire({
            title: result.message,
            icon: 'success'
          });
        }else if(resuldata[1] === "SUBMITTED"){
          Swal.fire({
            title: result.message,
            icon: 'success'
          });
        }else{
          window.location.href = resuldata[2];
        }
      }else{
        Swal.fire({
          title: result.message,
          icon: 'success'
        });
      }

        
    });
    
  }

}
