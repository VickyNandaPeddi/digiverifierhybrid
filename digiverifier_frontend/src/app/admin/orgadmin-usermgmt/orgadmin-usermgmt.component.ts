import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { OrgadminService } from 'src/app/services/orgadmin.service';
import {ModalDismissReasons, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
@Component({
  selector: 'app-orgadmin-usermgmt',
  templateUrl: './orgadmin-usermgmt.component.html',
  styleUrls: ['./orgadmin-usermgmt.component.scss']
})
export class OrgadminUsermgmtComponent implements OnInit {
  pageTitle = 'User Management';
  orgID:any;
  getOrgUsers: any=[];
  getOrgroles: any=[];
  getSupervisor: any=[];
  closeModal: string | undefined;
  getUserData: any=[];
  stat_roleId:boolean=false;
  getRolePerMissionCodes:any=[];
  ACTIVEANDINACTIVEAGENT_stat:boolean=false;
  EDITAGENT_stat:boolean=false;
  ACTIVEANDINACTIVEAGENTSUPERVISOR_stat:boolean=false;
  EDITAGENTSUPERVISOR_stat:boolean=false;
  admin_active:boolean=false;
  isPasswordVisible = false;
  AddOrUpdateUser = "";
  editMode:boolean=false;
  addOrgUser = new FormGroup({
    employeeId: new FormControl('', Validators.required),
    userFirstName: new FormControl('', [Validators.required, this.specialCharacterValidator]),
    userLastName: new FormControl('', [Validators.required, this.specialCharacterValidator]),
    userEmailId: new FormControl('', [Validators.required,Validators.email]),
    userMobileNum: new FormControl('', [ Validators.required, Validators.minLength(10), Validators.maxLength(10), Validators.pattern('[6-9]\\d{9}')]),
    userLandlineNum: new FormControl('', [Validators.minLength(8), Validators.maxLength(8)]),
    location: new FormControl('', Validators.required),
    roleId: new FormControl('', Validators.required),
    oldPassword: new FormControl(''),
    password: new FormControl('', [
      // Validators.required,
      Validators.minLength(10),
      Validators.pattern(/^(?=(.*[A-Z]){1,})(?=(.*[a-z]){2,})(?=(.*\d){1,})(?=(.*[!@#$%^&*()_+\-=[\]{}|]){2,})(?!.*(.)\1\1)[A-Za-z\d!@#$%^&*()_+\-=[\]{}|]{10,}$/)
    ]),
    organizationId: new FormControl('', Validators.required),
    userId: new FormControl(''),
    agentSupervisorId: new FormControl('')
  });
  specialCharacterValidator(control: any) {
    const specialCharacterPattern = /[^\w\s]/;
    const hasSpecialCharacter = specialCharacterPattern.test(control.value);
  
    return hasSpecialCharacter ? { containsSpecialCharacter: true } : null;
  }
  patchUserValues() {
		this.addOrgUser.patchValue({
			organizationId: this.orgID
		});
	}
  constructor(private orgadmin:OrgadminService, public authService: AuthenticationService, private modalService: NgbModal) {
    this.orgID = this.authService.getOrgID();
    this.orgadmin.getOrgusers(this.orgID).subscribe((data: any)=>{
      this.getOrgUsers=data.data;
      if(this.authService.roleMatch(['ROLE_ADMIN', "ROLE_CBADMIN"])){
        this.admin_active = true;
      }
    });
    this.orgadmin.getOrgroles().subscribe((data: any)=>{
      this.getOrgroles=data.data;
    });
    this.orgadmin.getSupervisor(this.orgID).subscribe((supervisorList: any)=>{
      this.getSupervisor=supervisorList.data;
      console.log(this.getSupervisor);
    });
    
   }

  ngOnInit(): void {
    this.orgadmin.getRolePerMissionCodes(this.authService.getRoles()).subscribe(
      (result:any) => {
      this.getRolePerMissionCodes = result.data;
        console.log(this.getRolePerMissionCodes);
        if(this.getRolePerMissionCodes){
          for (let index = 0; index < this.getOrgUsers.length; index++) {
            if((this.getOrgUsers[index].roleName === 'Agent' && this.getRolePerMissionCodes.includes('EDITAGENT')) || this.authService.roleMatch(['ROLE_ADMIN', "ROLE_CBADMIN"])){
              $("#edit"+index).removeClass('d-none');
             }
            if(this.getOrgUsers[index].roleName === 'Agent Supervisor' && this.getRolePerMissionCodes.includes('EDITAGENTSUPERVISOR') || this.authService.roleMatch(["ROLE_ADMIN", "ROLE_CBADMIN"])){
              $("#edit"+index).removeClass('d-none');
            }

            if(this.getOrgUsers[index].roleName === 'Agent' && this.getRolePerMissionCodes.includes('ACTIVEANDINACTIVEAGENT') || this.authService.roleMatch(["ROLE_ADMIN", "ROLE_CBADMIN"])){
              $("#inactiveCust_d"+index).removeClass('d-none');
              $("#inactiveCust_p"+index).removeClass('d-none');
             }
             if(this.getOrgUsers[index].roleName === 'Agent Supervisor' && this.getRolePerMissionCodes.includes('ACTIVEANDINACTIVEAGENTSUPERVISOR') || this.authService.roleMatch(["ROLE_ADMIN", "ROLE_CBADMIN"])){
              $("#inactiveCust_d"+index).removeClass('d-none');
              $("#inactiveCust_p"+index).removeClass('d-none');
             }

            
          }

        }
    });
    
  }

  get password() {
    return this.addOrgUser.get('password');
  }

  // Check if the password has errors
  isPasswordInvalid() {
    const passwordControl = this.addOrgUser.get('password');
    return passwordControl?.invalid && (passwordControl?.touched || passwordControl?.dirty);
  }

  // Check if the password is valid
  isPasswordValid() {
    const passwordControl = this.addOrgUser.get('password');
    return passwordControl?.valid && (passwordControl?.touched || passwordControl?.dirty);
  }
  
  togglePasswordVisibility() {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  onSubmit() {
    if(this.addOrgUser.valid){
      this.orgadmin.saveOrgusers(this.addOrgUser.value).subscribe((data:any)=>{
       if(data.outcome === true){
         Swal.fire({
           title: data.message,
           icon: 'success'
         }).then((data) => {
           if (data.isConfirmed) {
             window.location.reload();
           }
         });
       }else{
         Swal.fire({
           title: data.message,
           icon: 'warning'
         })
       } 
     });
   }
   else {
     Swal.fire({
       title: 'Please enter the required details.',
       icon: 'warning',
     });
   }
    
  }


  triggerModal(content: any) {
    this.editMode=false;
    this.AddOrUpdateUser = "Add User";
    this.addOrgUser.reset();
    this.patchUserValues();
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((res) => {
      this.closeModal = `Closed with: ${res}`;
    }, (res) => {
      this.closeModal = `Dismissed ${this.getDismissReason(res)}`;
    });
  }
  
  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return  `with: ${reason}`;
    }
  }

  inactiveCust(userId: any, isActive: any){
    $(this).hide();
     this.orgadmin.orguserStat(userId, !isActive).subscribe((data: any)=>{
       for (let index = 0; index < this.getOrgUsers.length; index++) {
        if(this.getOrgUsers[index].userId === data.data.userId){
          this.getOrgUsers[index] = data.data;
         }
       }
       if(data.outcome === true){
        Swal.fire({
          title: data.message,
          icon: 'success'
        }).then((data) => {
          if (data.isConfirmed) {
            window.location.reload();
          }
        });
      }else{
        Swal.fire({
          title: data.message,
          icon: 'warning'
        })
      } 
       
     })
  }
  selectroleId(event:any){
    if(event.target.value == 3){
      this.stat_roleId = true;
      this.addOrgUser.controls["agentSupervisorId"].clearValidators();
      this.addOrgUser.controls["agentSupervisorId"].setValidators(Validators.required);
      this.addOrgUser.controls["agentSupervisorId"].updateValueAndValidity();
    }else{
      this.stat_roleId = false;
      this.addOrgUser.controls["agentSupervisorId"].clearValidators();
      this.addOrgUser.controls["agentSupervisorId"].updateValueAndValidity();
    }
  }
  openModal(modalData:any, userId:any){
    this.modalService.open(modalData, {
      centered: true,
      backdrop: 'static'
     });
     $("#employeeId").attr("readonly", "readonly");
     
     this.editMode=true;
     this.AddOrUpdateUser = "Update User";
     this.orgadmin.getUserbyId(userId).subscribe((result: any)=>{
      this.getUserData=result.data;
      console.log(this.getUserData);
      this.addOrgUser.patchValue({
        employeeId: this.getUserData.employeeId,
        userFirstName: this.getUserData.userFirstName,
        userLastName: this.getUserData.userLastName,
        userEmailId: this.getUserData.userEmailId,
        userMobileNum: this.getUserData.userMobileNum,
        userLandlineNum: this.getUserData.userLandlineNum,
        location: this.getUserData.location,
        roleId: this.getUserData.roleId,
        userId: this.getUserData.userId,
        agentSupervisorId: this.getUserData.agentSupervisorId,
        organizationId: this.getUserData.organizationId
       });
       if(userId){
        this.addOrgUser.controls["password"];
        this.addOrgUser.controls["password"];
        $(".required_toggle").removeClass("required");
        if(this.getUserData.roleId == 3){
          this.stat_roleId = true;
        }else{
          this.stat_roleId = false;
        }
      }
    });
    
  }

}
