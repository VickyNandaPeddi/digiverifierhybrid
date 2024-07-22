import { Component, OnInit } from '@angular/core';
import { Router ,ActivatedRoute} from '@angular/router';
import { CandidateService } from 'src/app/services/candidate.service';
import { CustomerService } from '../../services/customer.service';
import { Location } from '@angular/common';

@Component({
  selector: 'app-thankyou',
  templateUrl: './thankyou.component.html',
  styleUrls: ['./thankyou.component.scss']
})
export class ThankyouComponent implements OnInit {
  candidateCode: any;
  showvalidation:any=false;
  orgid:any;
  result:any;
  CandidateFormData: any=[];
  conventionalCandidate: any;
  constructor(private candidateService: CandidateService,private router:ActivatedRoute,private customers:CustomerService, private location: Location) {
    history.pushState(null, "", document.URL);
    window.addEventListener('popstate', function () {
      history.pushState(null, "", document.URL);
    });
    // this.orgid= localStorage.getItem('orgID');
    // this.customers.getShowvalidation(this.orgid).subscribe((data:any)=>{
    //   this.showvalidation=data.outcome;
    //   console.log(this.showvalidation,"result")
    
    console.log(this.showvalidation,"result")
    if(this.showvalidation == false){
      console.log("inisde ");
      this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
      console.log(this.candidateCode);
      console.log("before getCandidateFormData THANKYOU FROM>>>>>::{}",new Date().toLocaleTimeString());
      this.candidateService.getCandidateFormData(this.candidateCode).subscribe((data: any)=>{
        this.CandidateFormData=data.data;
        this.conventionalCandidate = this.CandidateFormData.candidate.conventionalCandidate
        console.log("conventionalCandidate>>",this.conventionalCandidate)
        console.log("after getCandidateFormData THANKYOU FROM>>>>>::{}",new Date().toLocaleTimeString());
        console.log(this.CandidateFormData);

        // if(data.outcome === true){
          // console.log("before qcPendingstatus THANKYOU FROM>>>>>::{}",new Date().toLocaleTimeString());
          // this.candidateService.qcPendingstatus(this.candidateCode).subscribe((data:any)=>{
          //  this.result=data.outcome;
          //  console.log("after qcPendingstatus THANKYOU FROM>>>>>::{}",new Date().toLocaleTimeString());
          //  console.log(this.result,"----------------------------------------result")
          // })
        // }
        console.log("before qc || this.conventionalCandidate == nullPendingstatus THANKYOU FROM>>>>>::{}",new Date().toLocaleTimeString());
        console.log("this.conventionalCandidate : ",this.conventionalCandidate)
        if(this.conventionalCandidate === false || this.conventionalCandidate == null){  
        this.candidateService.qcPendingstatus(this.candidateCode).subscribe((data:any)=>{
          this.result=data.outcome;
          console.log("after qcPendingstatus THANKYOU FROM>>>>>::{}",new Date().toLocaleTimeString());
          console.log(this.result,"----------------------------------------result")
         }) 
        }
      });
       
    }
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
}
