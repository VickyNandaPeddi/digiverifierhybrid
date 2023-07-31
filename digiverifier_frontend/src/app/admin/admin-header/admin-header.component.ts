import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { OrgadminService } from 'src/app/services/orgadmin.service';

@Component({
  selector: 'app-admin-header',
  templateUrl: './admin-header.component.html',
  styleUrls: ['./admin-header.component.scss']
})
export class AdminHeaderComponent implements OnInit {
  details:any;
  rolename:any;
  getRolePerMissionCodes:any=[];
  CustUtilizationReport_stat:boolean=false;
  eKycReport_stat:boolean=false;
  constructor(public authService: AuthenticationService,
    private router: Router, private orgadmin:OrgadminService) { }

  ngOnInit(): void {
    this.details = this.authService.getuserName();
    this.rolename = this.authService.getroleName();

    this.orgadmin.getRolePerMissionCodes(localStorage.getItem('roles')).subscribe(
      (result:any) => {
      this.getRolePerMissionCodes = result.data;
        console.log("permissioncodes-->"+this.getRolePerMissionCodes);
        if(this.getRolePerMissionCodes){
          if(this.getRolePerMissionCodes.includes('CUSTOMERUTILIZATIONREPORT')){
            this.CustUtilizationReport_stat = true;
          }

          if(this.getRolePerMissionCodes.includes('E-KYCREPORT')){
            this.eKycReport_stat = true;
          }

        }
    });
  }

  public isLoggedIn(){ 
    return this.authService.isLoggedIn();
  }

  public logout() {
    this.authService.logOut().subscribe(
      (response:any)=>{
        if (response.outcome === true)
        {
          this.authService.clear();
          this.router.navigate(['./']);
        }
      }
    );
  }


}
