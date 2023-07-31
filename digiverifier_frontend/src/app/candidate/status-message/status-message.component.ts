import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-status-message',
  templateUrl: './status-message.component.html',
  styleUrls: ['./status-message.component.scss']
})
export class StatusMessageComponent implements OnInit {
  statcode:any;
  msgStat:any;
  constructor(private router:ActivatedRoute) { }

  ngOnInit(): void {
    this.statcode = this.router.snapshot.paramMap.get('statcode');
    console.warn(this.statcode);
    if(this.statcode){
      if(this.statcode == 'INVITATIONEXPIRED'){
        this.msgStat = 'INVITATIONEXPIRED';
      }else if(this.statcode == 'SUBMITTED'){
        this.msgStat = 'SUBMITTED';
      }else if(this.statcode == 'PROCESSDECLINED'){
        this.msgStat = 'PROCESSDECLINED';
      }else if(this.statcode == 'CANCELLED'){
        this.msgStat = 'CANCELLED';
      }
    }

    console.log(this.msgStat);

  }

}
