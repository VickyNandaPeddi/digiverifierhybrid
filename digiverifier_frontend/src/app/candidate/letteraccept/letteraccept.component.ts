import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute,Router } from '@angular/router';
import { CandidateService } from 'src/app/services/candidate.service';
import Swal from 'sweetalert2';
import { Location } from '@angular/common';
@Component({
  selector: 'app-letteraccept',
  templateUrl: './letteraccept.component.html',
  styleUrls: ['./letteraccept.component.scss']
})
export class LetteracceptComponent implements OnInit {
  candidateCode: any;
  formLetterAccept = new FormGroup({
    candidateCode: new FormControl('', Validators.required)
  });
  patchUserValues() {
    this.formLetterAccept.patchValue({
      candidateCode: this.candidateCode
    });
  }
 
  constructor(private candidateService: CandidateService, private router:ActivatedRoute,private navrouter: Router, private location: Location) { 
    
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
    return this.candidateService.saveLtrAccept(this.formLetterAccept.value).subscribe((result:any)=>{
      if(result.outcome==true){
        const navURL = 'candidate/digiLocker/'+this.candidateCode;
        this.navrouter.navigate([navURL]);
        // window.location.href = result.data;
      }else{
        Swal.fire({
          title: result.message,
          icon: 'success'
        });
      }

        
    });
    
  }
  btnLtrDecline(){
    this.patchUserValues();
    return this.candidateService.saveLtrDecline(this.formLetterAccept.value).subscribe((result:any)=>{
      if(result.outcome==true){
          Swal.fire({
            title: result.message,
            icon: 'success'
          });
      }else{
        Swal.fire({
          title: result.message,
          icon: 'success'
        });
      }

        
    });
  }

}
