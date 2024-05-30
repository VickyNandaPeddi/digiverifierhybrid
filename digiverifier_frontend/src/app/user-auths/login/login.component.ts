import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { first, map } from 'rxjs/operators';
import Swal from 'sweetalert2';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginPage!: FormGroup;
  
  constructor(
      private formBuilder: FormBuilder,
      private route: ActivatedRoute,
      private authService: AuthenticationService,
      private router: Router
  ) { }

  ngOnInit(): void {
    if(this.authService.isLoggedIn()!==null && this.authService.getRoles() == '"ROLE_CBADMIN"'){
      this.router.navigate(['admin']);
    }else if(this.authService.isLoggedIn()!==null && this.authService.getRoles() !== '"ROLE_CBADMIN"'){
      this.router.navigate(['/admin/orgadminDashboard']);
    }else{
      this.router.navigate(['login']);
    }
    this.loginPage = new FormGroup({
      password: new FormControl('', Validators.required),
      userName: new FormControl('', Validators.required)
    });

  }

  encryptedCredentials: any;
  encryptData(data: string, key: string): string {
    let encryptedData = '';
    for (let i = 0; i < data.length; i++) {
      encryptedData += String.fromCharCode(data.charCodeAt(i) ^ key.charCodeAt(i % key.length));
    }
    return btoa(encryptedData); // Base64 encode the encrypted data for better representation
  }

  private decryptData(encryptedData: string, key: string): string {
    const decodedBytes = atob(encryptedData);
    const keyBytes = key.split('').map(char => char.charCodeAt(0));
    let decryptedData = '';
    for (let i = 0; i < decodedBytes.length; i++) {
      decryptedData += String.fromCharCode(decodedBytes.charCodeAt(i) ^ keyBytes[i % keyBytes.length]);
    }
    return decryptedData;
  }

  onSubmit() {

    const credentials = this.loginPage.getRawValue();
    const key = '12345678901234567890123456789012'; // 32-byte key
    // @ts-ignore
    const username = this.encryptData(this.loginPage.get('userName').value, key).toString();
    // @ts-ignore
    const password = this.encryptData(this.loginPage.get('password').value, key).toString();
    this.encryptedCredentials = {
      userName: username,
      password: password
    } 
    return this.authService.login(this.encryptedCredentials).subscribe(
      (response:any)=>{
        //console.log(response);
        if(response.outcome != true){
          Swal.fire({
            title: response.message,
            icon: 'warning'
          });
        }
        
        
        // @ts-ignore
        this.authService.setRoles(this.decryptData(response.data.roleCode,key));
        this.authService.setToken(this.decryptData(response.data.jwtToken, key));
        this.authService.setuserName(this.decryptData(response.data.userFirstName,key));
        this.authService.setroleName(this.decryptData(response.data.roleName,key));
        this.authService.setuserId(this.decryptData(response.data.userId,key));
        if(this.decryptData(response.data.organizationId,key)){
          this.authService.setOrgID(this.decryptData(response.data.organizationId,key));
        }
        const role = this.decryptData(response.data.roleCode,key);
        if(role === "ROLE_CBADMIN"){
          this.router.navigate(['/admin']);
        }else if(role === "ROLE_ADMIN"){
          this.router.navigate(['/admin/orgadminDashboard']);
        }else if(role === "ROLE_PARTNERADMIN"){
          this.router.navigate(['/admin/orgadminDashboard']);
        }else if(role === "ROLE_AGENTSUPERVISOR"){
          this.router.navigate(['/admin/orgadminDashboard']);
        }else if(role === "ROLE_AGENTHR"){
          this.router.navigate(['/admin/orgadminDashboard']);
        }else if(role === "ROLE_VENDOR"){
          this.router.navigate(['/admin/vendordashboard']);
        }else{
          this.router.navigate(['/login']);
        }

        if(response.message == 'Change your password.')
          window.alert('Change your password.')
        
      },
      (error)=>{
        console.log(error);
      }
    )
    
  }

}
