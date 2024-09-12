import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CandidateService } from 'src/app/services/candidate.service';

@Component({
  selector: 'app-candidate-header',
  templateUrl: './candidate-header.component.html',
  styleUrls: ['./candidate-header.component.scss']
})
export class CandidateHeaderComponent implements OnInit {

  candidateCode: any;
  organizationName:any;
  enableEYLogo:any= false;
  cwfLogo:any


  constructor(private candidateService: CandidateService,private route:ActivatedRoute,private router: Router) {
    const url = this.router.routerState.snapshot.url; // Gets the full URL path
    const urlSegments = url.split('/'); // Splits the URL into segments
    this.candidateCode = urlSegments[urlSegments.length - 1];

    // console.log("this.candidateCode : ",this.candidateCode)
    this.candidateService.getOrgNameByCandidateCode(this.candidateCode).subscribe((data: any)=>{
      // console.log("DATA :",data)
      this.organizationName = data.data;

      if(data.data.cwfLogo != null && data.data.cwfLogo != ''){
        this.cwfLogo = data.data.cwfLogo;
      }     
      // console.log("organizationName : ",this.organizationName)
      // if (this.organizationName && this.organizationName == 'Ernst & Young Pvt. Ltd.') {
      //   this.enableEYLogo = true;
      //   // console.log('The organizationName contains "EY".');
      // } else {
      //   this.enableEYLogo = false;
      //   // console.log('The organizationName does not contain "EY".');
      // }
    })
   }

  ngOnInit(): void {
  }

}
