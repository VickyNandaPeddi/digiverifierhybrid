import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { CustomerService } from 'src/app/services/customer.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-forgotpwd',
  templateUrl: './forgotpwd.component.html',
  styleUrls: ['./forgotpwd.component.scss']
})
export class ForgotpwdComponent implements OnInit {

  forgotPassword = new FormGroup({
    emailId : new FormControl('',[Validators.required, Validators.email])
  })
  constructor(private customers:CustomerService) { }

  ngOnInit(): void {
  }

  onSubmit(){
    console.log(this.forgotPassword.value)
    if(this.forgotPassword.valid){
      this.customers.resetPassword(this.forgotPassword.get("emailId")?.value).subscribe((response:any)=>{
        if(response.outcome === true){
          Swal.fire({
            title: response.message,
            icon: 'success'
          }).then((result) => {
            if (result.isConfirmed) {
              window.location.reload();
            }
          });
        }else{
          Swal.fire({
            title: response.message,
            icon: 'warning'
          })
        }
      })
    }else{
      Swal.fire({
        title: 'Enter Valid Email',
        icon: 'warning'
      })
    }
  }

}
