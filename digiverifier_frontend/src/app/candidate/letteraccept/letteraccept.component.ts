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
  isDigiLocker=true;
  organizationData: any=[];
  organizationId:any;
  organizationName:any;
  loaContent:any;
  formLetterAccept = new FormGroup({
    candidateCode: new FormControl('', Validators.required)
  });
  showDeclineBtn: boolean = false;
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
    this.candidateService.getCurrentStatusByCandidateCode(this.candidateCode).subscribe((result:any)=>{
      if(result.outcome==true){
        console.log(result.data)
        const navURL = result.data.split('#/')[1];
        this.navrouter.navigate([navURL]);
      } else {
        Swal.fire({
          title: result.message,
          icon: 'warning',
        });
      }
    });

    this.candidateService.getLoaContentData(this.candidateCode).subscribe((result:any)=>{
      if(result.outcome==true){
        this.loaContent=result.data;
      }
    });

    
    this.candidateService.getOrgNameByCandidateCode(this.candidateCode).subscribe((data: any)=>{
      // console.log("DATA :",data)
      this.organizationName = data.data;
      
      // console.log("organizationName : ",this.organizationName)
      if (this.organizationName && this.organizationName == 'Ernst & Young Pvt. Ltd.') {
        this.showDeclineBtn = true;
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
    return this.candidateService.saveLtrAccept(this.formLetterAccept.value).subscribe((result:any)=>{
      console.warn("CandidateCode::",this.candidateCode);
     // this.candidateService.getOrgDetails(this.candidateCode).subscribe((data:any)=>{
        // this.organizationData = data;
        // this.organizationName = data.data.organizationName;
        // console.warn("orgName"+this.organizationName);
        // this.organizationId = data.data.organizationId; 

        // if(this.organizationName.toLowerCase() === "kpmg" || this.organizationName.toUpperCase() === "KPMG"){
        //   this.isDigiLocker = false;
        //   console.warn("isDigilocker::",this.isDigiLocker);
        // } 

      if(result.outcome==true){
        //if(this.isDigiLocker){
 //         const navURL = 'candidate/digiLocker/'+this.candidateCode;
 //         this.navrouter.navigate([navURL]);
           window.location.href = result.data;
        // }else{
        //   const navURL = 'candidate/itrlogin/'+this.candidateCode;
        //   this.navrouter.navigate([navURL]);
        // }
      }else{
        Swal.fire({
          title: result.message,
          icon: 'success'
        });
      }
   // })   
    });
  }

  btnLtrDecline(){
    // this.patchUserValues();
    // return this.candidateService.saveLtrDecline(this.formLetterAccept.value).subscribe((result:any)=>{
    //   if(result.outcome==true){
    //       Swal.fire({
    //         title: result.message,
    //         icon: 'success'
    //       });
    //   }else{
    //     Swal.fire({
    //       title: result.message,
    //       icon: 'success'
    //     });
    //   }

        
    // });
    $(this).hide();
    Swal.fire({
      title: 'Your data is mandatory to avail the services from DigiVerifier',
      icon: 'warning',
      showCancelButton: true, // Add this line to show the cancel button
      confirmButtonText: 'Confirm', // Specify the text for the confirmation button
      cancelButtonText: 'Cancel', 
    }).then((result) => {
      if (result.isConfirmed) {
        this.candidateService
          .authLtrDecline(this.candidateCode)
          .subscribe((data: any) => {
            if (data.outcome === true) {
              Swal.fire({
                title: data.message,
                icon: 'success',
              }).then((data) => {
                if (data.isConfirmed) {
                    const navURL = 'candidate/cStatusMessage/PROCESSDECLINED';
                    this.navrouter.navigate([navURL]);
                  //window.location.reload();
                }
              });
            } else {
              Swal.fire({
                title: data.message,
                icon: 'warning',
              });
            }
          });
      }else if (result.dismiss === Swal.DismissReason.cancel) {
        // User clicked the cancel button
        window.location.reload();
      }
    });
  }

}
