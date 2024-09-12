import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CandidateService } from 'src/app/services/candidate.service';
import Swal from 'sweetalert2';
import { Location } from '@angular/common';


@Component({
  selector: 'app-convention-letter-accept',
  templateUrl: './convention-letter-accept.component.html',
  styleUrls: ['./convention-letter-accept.component.scss']
})
export class ConventionLetterAcceptComponent implements OnInit {

  candidateCode: any;
  conventionalCandidate:boolean = false;
  loaContent:any;



  formLetterAccept = new FormGroup({
    candidateCode: new FormControl('', Validators.required)
  });
  patchUserValues() {
    this.formLetterAccept.patchValue({
      candidateCode: this.candidateCode
    });
  }

  constructor(private candidateService: CandidateService, private router:ActivatedRoute,private navrouter: Router, private location: Location) {
    history.pushState(null, "", document.URL);
    window.addEventListener('popstate', function () {
      history.pushState(null, "", document.URL);
    }); 
    this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');

      this.candidateService.getCandidateDetails(this.candidateCode)
        .subscribe((data: any) => {
        data.data = this.candidateService.decryptData(data.data);
        // Parse the decrypted JSON string into an object
        data.data = JSON.parse(data.data);
          if(data.outcome==true){
            // console.warn("data : ",data)
            this.conventionalCandidate = data.data.conventionalCandidate
          }
        })
    }

    

    checkHistoryLength(): boolean {
      return this.location.getState() === null;
    }

    

    ngOnInit(): void {
      this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
      window.addEventListener('popstate', () => {
        if (!this.checkHistoryLength()) {
          this.location.forward();
        }
      });
    }


  btnLtrAccept() {
    this.patchUserValues();
      return this.candidateService.conventionalSaveLtrAccept(this.formLetterAccept.value).subscribe((result:any)=>{

        if(result.outcome==true){
          console.warn("result :: ",result)
          window.location.href = result.data;
        }else{
          Swal.fire({
            title: result.message,
            icon: 'success'
          });
        }
     
      })
    }  


}
