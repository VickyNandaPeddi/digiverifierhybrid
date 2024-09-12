import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CandidateService } from 'src/app/services/candidate.service';

@Component({
  selector: 'app-candidate-footer',
  templateUrl: './candidate-footer.component.html',
  styleUrls: ['./candidate-footer.component.scss']
})
export class CandidateFooterComponent implements OnInit {

  candidateCode: any;
  cwfCopyRight:any;

  constructor(private candidateService: CandidateService,private router: Router) {

    const url = this.router.routerState.snapshot.url; // Gets the full URL path
    const urlSegments = url.split('/'); // Splits the URL into segments
    this.candidateCode = urlSegments[urlSegments.length - 1];


    this.candidateService.getOrgNameByCandidateCode(this.candidateCode).subscribe((data: any)=>{
      // console.log("DATA :",data)
      // this.organizationName = data.data;
      if(data.data.cwfCopyRight != null && data.data.cwfCopyRight != ''){
        this.cwfCopyRight = data.data.cwfCopyRight;
      }     
   
    })

   }

  ngOnInit(): void {
  }

}
