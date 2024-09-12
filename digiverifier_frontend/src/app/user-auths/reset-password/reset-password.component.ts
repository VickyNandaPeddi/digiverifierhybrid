import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomerService } from 'src/app/services/customer.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {

  isPasswordVisibleNew = false;
  isPasswordVisibleOld = false;
  isConfirmPasswordVisible = false;
  isPasswordValuesCorrect = false;
  userId?: any

  constructor(private customers: CustomerService, private route: ActivatedRoute, private navRouter: Router) {
    this.userId = this.route.snapshot.paramMap.get('userId');
    console.log("userId: " + this.userId)
    this.resetPassword.get('encryptUserId')?.setValue(this.userId); // Update form control value
  }

  resetPassword = new FormGroup({
    password: new FormControl('', [
      Validators.required,
      Validators.minLength(10),
      Validators.pattern(/^(?=(.*[A-Z]){1,})(?=(.*[a-z]){2,})(?=(.*\d){1,})(?=(.*[!@#$%^&*()_+\-=[\]{}|]){1,})(?!.*(.)\1\1)[A-Za-z\d!@#$%^&*()_+\-=[\]{}|]{10,}$/)
    ]),

    confirmPassword: new FormControl('', [
      Validators.required,
      Validators.minLength(10),
      Validators.pattern(/^(?=(.*[A-Z]){1,})(?=(.*[a-z]){2,})(?=(.*\d){1,})(?=(.*[!@#$%^&*()_+\-=[\]{}|]){1,})(?!.*(.)\1\1)[A-Za-z\d!@#$%^&*()_+\-=[\]{}|]{10,}$/)
    ]),
    resetPassword: new FormControl(true),
    encryptUserId: new FormControl(this.userId)
  })

  ngOnInit(): void {
  }

  // Check if the password has errors
  isPasswordInvalid() {
    const passwordControl = this.resetPassword.get('password');
    return passwordControl?.invalid && (passwordControl?.touched || passwordControl?.dirty);
  }

  // Check if the password is valid
  isPasswordValid() {
    const passwordControl = this.resetPassword.get('password');
    return passwordControl?.valid && (passwordControl?.touched || passwordControl?.dirty);
  }

  isConfirmPasswordInvalid() {
    const passwordControl = this.resetPassword.get('confirmPassword');
    return passwordControl?.invalid && (passwordControl?.touched || passwordControl?.dirty);
  }

  // Check if the password is valid
  isConfirmPasswordValid() {
    const passwordControl = this.resetPassword.get('confirmPassword');
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

  isButtonDisabled(): boolean {
    const passwordControl = this.resetPassword.get('password');
    const oldPasswordControl = this.resetPassword.get('confirmPassword');

    // Use non-null assertion operator (!) to indicate that these values are not null
    const passwordValue = passwordControl!.value;
    const oldPasswordValue = oldPasswordControl!.value;

    if ((passwordValue === '' && oldPasswordValue === '') || (passwordValue !== '' && oldPasswordValue !== '')) {
      this.isPasswordValuesCorrect = true;
    } else {
      this.isPasswordValuesCorrect = false;
    }
    return this.isPasswordValuesCorrect;
  }

  onSubmit() {
    if (this.resetPassword.valid) {
      console.log("resetPassword : " + this.resetPassword.get('newPassword')?.value)
      console.log("resetPassword confirmPassword : " + this.resetPassword.get('confirmPassword')?.value)
      console.log("userId : " + this.resetPassword.get('userId')?.value)


      if (this.resetPassword.get('password')?.value !== this.resetPassword.get('confirmPassword')?.value) {
        Swal.fire({
          icon: 'error',
          title: 'Passwords do not match',
          text: 'Please make sure both passwords are the same.'
        });
      } else {
        // Proceed with form submission or further processing
        console.log('Form submitted');

        this.customers.updatePassword(this.resetPassword.value).subscribe((data: any) => {
          if (data.outcome === true) {
            Swal.fire({
              title: data.message,
              icon: 'success'
            }).then((result) => {
              if (result.isConfirmed) {
                this.navRouter.navigate(['login'])
              }
            });
          } else {
            Swal.fire({
              title: data.message,
              icon: 'warning'
            })
          }
        });


      }

    }else{
      Swal.fire({
        title: 'Please enter the required information',
        icon: 'warning'
      })
    }
  }

}
