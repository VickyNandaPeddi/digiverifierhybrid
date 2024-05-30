import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-aadhar-message',
  templateUrl: './aadhar-message.component.html',
  styleUrls: ['./aadhar-message.component.scss']
})
export class AadharMessageComponent implements OnInit {
  candidateCode: any;
  
  constructor(private navrouter: Router, private router:ActivatedRoute) {
    this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
  }

  ngOnInit(): void {
  }

  linkAadharRelogin (){
          const navURL = 'candidate/digiLocker/'+this.candidateCode;
          this.navrouter.navigate([navURL]);
      }
}
