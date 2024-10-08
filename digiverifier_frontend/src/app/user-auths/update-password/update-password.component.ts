import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { CustomerService } from 'src/app/services/customer.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-update-password',
  templateUrl: './update-password.component.html',
  styleUrls: ['./update-password.component.scss']
})
export class UpdatePasswordComponent implements OnInit {

  pageTitle = 'Update Password';
  userId:any;
  isPasswordVisibleNew = false;
  isPasswordVisibleOld = false;
  isConfirmPasswordVisible = false;
  isPasswordValuesCorrect= false;
  formMyProfile = new FormGroup({
    employeeId: new FormControl('', Validators.required),
    roleId: new FormControl('', Validators.required),
    location: new FormControl('', Validators.required),
    userId: new FormControl('', Validators.required),
    userFirstName: new FormControl('', Validators.required),
    userEmailId: new FormControl('', [Validators.required,Validators.email]),
    userMobileNum: new FormControl('', [Validators.required,Validators.minLength(10), Validators.maxLength(10), Validators.pattern('[6-9]\\d{9}')]),
    password: new FormControl(''),
    oldPassword: new FormControl(''),
    confirmPassword: new FormControl('')
  });

  constructor(private authService: AuthenticationService, private customers:CustomerService) { 
    this.customers.getProfile(this.authService.getuserId()).subscribe((data: any)=>{
      console.log(data);
      this.formMyProfile = new FormGroup({
        employeeId: new FormControl(data.data['employeeId'], Validators.required),
        location: new FormControl(data.data['location'], Validators.required),
        roleId: new FormControl(data.data['roleId'], Validators.required),
        userId: new FormControl(data.data['userId'], Validators.required),
        userFirstName: new FormControl(data.data['userFirstName'], Validators.required),
        userEmailId: new FormControl(data.data['userEmailId'], [Validators.required,Validators.email]),
        userMobileNum: new FormControl(data.data['userMobileNum'], [Validators.required,Validators.minLength(10), Validators.maxLength(10), Validators.pattern('[6-9]\\d{9}')]),
        oldPassword: new FormControl(''),
        password: new FormControl('', [
          // Validators.required,
          Validators.minLength(10),
          Validators.pattern(/^(?=(.*[A-Z]){1,})(?=(.*[a-z]){2,})(?=(.*\d){1,})(?=(.*[!@#$%^&*()_+\-=[\]{}|]){1,})(?!.*(.)\1\1)[A-Za-z\d!@#$%^&*()_+\-=[\]{}|]{10,}$/)
        ]),
        confirmPassword: new FormControl('', [
          // Validators.required,
          Validators.minLength(10),
          Validators.pattern(/^(?=(.*[A-Z]){1,})(?=(.*[a-z]){2,})(?=(.*\d){1,})(?=(.*[!@#$%^&*()_+\-=[\]{}|]){1,})(?!.*(.)\1\1)[A-Za-z\d!@#$%^&*()_+\-=[\]{}|]{10,}$/)
        ]),
      });
    });
  }
  ngOnInit(): void {
  }


  
  get password() {
    return this.formMyProfile.get('password');
  }

  // Check if the password has errors
  isPasswordInvalid() {
    const passwordControl = this.formMyProfile.get('password');
    return passwordControl?.invalid && (passwordControl?.touched || passwordControl?.dirty);
  }

  // Check if the password is valid
  isPasswordValid() {
    const passwordControl = this.formMyProfile.get('password');
    return passwordControl?.valid && (passwordControl?.touched || passwordControl?.dirty);
  }

  isConfirmPasswordInvalid() {
    const passwordControl = this.formMyProfile.get('confirmPassword');
    return passwordControl?.invalid && (passwordControl?.touched || passwordControl?.dirty);
  }

  // Check if the password is valid
  isConfirmPasswordValid() {
    const passwordControl = this.formMyProfile.get('confirmPassword');
    return passwordControl?.valid && (passwordControl?.touched || passwordControl?.dirty);
  }

  togglePasswordVisibilityNew() {
    this.isPasswordVisibleNew = !this.isPasswordVisibleNew;
  }
  togglePasswordVisibilityOld() {
    this.isPasswordVisibleOld = !this.isPasswordVisibleOld;
  }
  toggleConfirmPasswordVisibility() {
    this.isConfirmPasswordVisible = !this.isConfirmPasswordVisible;
  }

  onSubmit(formMyProfile: FormGroup) {
    console.log("Checking passwords values::{}",this.isButtonDisabled());
    if(this.formMyProfile.valid && this.isButtonDisabled()){
      this.customers.updatePassword(this.formMyProfile.value).subscribe((data:any)=>{
         if(data.outcome === true){
           Swal.fire({
             title: data.message,
             icon: 'success'
           }).then((result) => {
             if (result.isConfirmed) {
               //window.location.reload();
               if(data.status == null){
                window.location.reload();
               }else{
                 this.authService.forceLogout();
               }
             }
           });
         }else{
           Swal.fire({
             title: data.message,
             icon: 'warning'
           })
         }
   });
   }else{
     Swal.fire({
       title: "Please enter the required information",
       icon: 'warning'
     })
   }
  }

  isButtonDisabled(): boolean {
    const passwordControl = this.formMyProfile.get('password');
    const oldPasswordControl = this.formMyProfile.get('oldPassword');
  
    // Use non-null assertion operator (!) to indicate that these values are not null
    const passwordValue = passwordControl!.value;
    const oldPasswordValue = oldPasswordControl!.value;
  
    if((passwordValue === '' && oldPasswordValue === '') || (passwordValue !== '' && oldPasswordValue !== '')){
      this.isPasswordValuesCorrect=true;
    }else{
      this.isPasswordValuesCorrect=false;
    }
    return this.isPasswordValuesCorrect;
  }

}
