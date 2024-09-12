import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { first, map } from 'rxjs/operators';
import Swal from 'sweetalert2';
import { data } from 'jquery';
import { any } from '@amcharts/amcharts4/.internal/core/utils/Array';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginPage!: FormGroup;
  private timerInterval: any;

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private authService: AuthenticationService,
    private router: Router
  ) { }

  ngOnInit(): void {
    if (this.authService.isLoggedIn() !== null && this.authService.getRoles() == '"ROLE_CBADMIN"') {
      this.router.navigate(['admin']);
    } else if (this.authService.isLoggedIn() !== null && this.authService.getRoles() !== '"ROLE_CBADMIN"') {
      this.router.navigate(['/admin/orgadminDashboard']);
    } else {
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
      (response: any) => {
        //console.log(response);
        if (response.outcome != true) {
          Swal.fire({
            title: response.message,
            icon: 'warning'
          });
        }


        // @ts-ignore
        this.authService.setRoles(this.decryptData(response.data.roleCode, key));
        this.authService.setToken(this.decryptData(response.data.jwtToken, key));
        this.authService.setuserName(this.decryptData(response.data.userFirstName, key));
        this.authService.setroleName(this.decryptData(response.data.roleName, key));
        this.authService.setuserId(this.decryptData(response.data.userId, key));
        if (this.decryptData(response.data.organizationId, key)) {
          this.authService.setOrgID(this.decryptData(response.data.organizationId, key));
        }
        const role = this.decryptData(response.data.roleCode, key);
        let lastPasswordUpdated: Date = new Date(response.data.lastPasswordUpdated);
        const passwordPolicyDay = response.data.passwordPolicyDay;
        // let lastPasswordUpdated: Date | null = response.data.lastPasswordUpdated ? new Date(response.data.lastPasswordUpdated) : null;
        if (lastPasswordUpdated !== null) {
          // let currentDate: Date = new Date();
          // let timeDifference: number = currentDate.getTime() - lastPasswordUpdated.getTime();
          // let daysDifference: number = timeDifference / (1000 * 3600 * 24);
          // console.log("response.data.orgPassPolicy : " + response.data.orgPassPolicy)
          // console.log("lastPasswordUpdated : " + lastPasswordUpdated)
          // console.log("response.data.isMFA : " + response.data.mfa)
        }

        if (role === "ROLE_CBADMIN") {
          this.router.navigate(['/admin']);
        }
        else if (response.data.orgPassPolicy === true && lastPasswordUpdated !== null) {
          let currentDate: Date = new Date();
          let timeDifference: number = currentDate.getTime() - lastPasswordUpdated.getTime();
          let daysDifference: number = timeDifference / (1000 * 3600 * 24);
          // console.log("daysDifference : " + daysDifference)
          if (response.data.mfa) {
            this.showOTPModal(username, password);
          }
          else{
            this.checkPasswordStatusOrPassowordPolicy(response, lastPasswordUpdated, passwordPolicyDay, role, username, password);
          }
         
        }
        else if (response.data.orgPassPolicy === false || response.data.orgPassPolicy == null) {
          if (response.data.mfa == true) {
            // console.log("============================================")
            this.showOTPModal(username, password);


          } else {
            if (role === "ROLE_ADMIN") {
              this.router.navigate(['/admin/orgadminDashboard']);
            } else if (role === "ROLE_PARTNERADMIN") {
              this.router.navigate(['/admin/orgadminDashboard']);
            } else if (role === "ROLE_AGENTSUPERVISOR") {
              this.router.navigate(['/admin/orgadminDashboard']);
            } else if (role === "ROLE_AGENTHR") {
              this.router.navigate(['/admin/orgadminDashboard']);
            } else if (role === "ROLE_VENDOR") {
              this.router.navigate(['/admin/vendordashboard']);
            } else if (role === "ROLE_CLIENTAGENT") {
              this.router.navigate(['/admin/orgadminDashboard']);
            } else if (role === "ROLE_CLIENTSUPERVISOR") {
              this.router.navigate(['/admin/orgadminDashboard']);
            }
            else {
              this.router.navigate(['/login']);
            }
          }
        }

        if (response.message == 'Change your password.')
          window.alert('Change your password.')

      },
      (error) => {
        console.log(error);
      }
    )

  }


  navigateBasedOnRole(role: string): void {
    console.warn("ROLE ::::::::", role)
    const roleRoutes: { [key: string]: string } = {
      'ROLE_ADMIN': '/admin/orgadminDashboard',
      'ROLE_PARTNERADMIN': '/admin/orgadminDashboard',
      'ROLE_AGENTSUPERVISOR': '/admin/orgadminDashboard',
      'ROLE_AGENTHR': '/admin/orgadminDashboard',
      'ROLE_VENDOR': '/admin/vendordashboard',
      'ROLE_CLIENTAGENT': '/admin/orgadminDashboard',
      'ROLE_CLIENTSUPERVISOR': '/admin/orgadminDashboard'
    };

    const route = roleRoutes[role] || '/login';
    this.router.navigate([route]);
  }


  resendOtp(username: string, password: string) {
    // Replace with your actual API call to resend OTP
    this.encryptedCredentials = {
      userName: username,
      password: password,
    }
    this.authService.login(this.encryptedCredentials).subscribe((data: any) => {
      // console.log("DATA : ", data);
      this.showOTPModal(username, password);
    });
  }


  showOTPModal(username: string, password: string) {
    const key = '12345678901234567890123456789012'; // 32-byte key
    Swal.fire({
      title: 'Enter OTP',
      html: `
                  <input id="otp" type="text" pattern="[0-9]*" inputmode="numeric" maxlength="6" class="otp-input" placeholder="Enter OTP" />
                   <div id="timer" style="font-size: 1.2em; margin-top: 1em; color: red;">2:00</div>
                    <button id="resendOtp" style="display: none; margin-top: 1em;">Resend OTP</button>
                  <style>
                      .otp-input {
                          width: 100%;
                          font-size: 2em;
                          text-align: center;
                          padding: 0.5em;
                          border: 1px solid #ced4da;
                          border-radius: 0.25rem;
                          box-sizing: border-box; /* Ensure padding is included in width */
                      }
                  #resendOtp {
                    padding: 0.25em 0.5em; /* Reduce padding */
                    font-size: 0.875em; /* Reduce font size */
                    background-color: #007bff;
                    color: white;
                    border: none;
                    border-radius: 0.25rem;
                    cursor: pointer;
                    display: block;
                    margin: 1em auto 0 auto; /* Center the button horizontally */
                }
                #resendOtp:disabled {
                    background-color: #ccc;
                    cursor: not-allowed;
                }
                  </style>
              `,
      confirmButtonText: 'Submit',
      showCancelButton: true,
      cancelButtonText: 'Cancel',
      preConfirm: () => {
        const otp = (document.getElementById('otp') as HTMLInputElement).value;
        if (!/^\d{6}$/.test(otp)) {
          Swal.showValidationMessage('Please enter a valid 6-digit OTP');
          return false;
        }
        return otp;
      },
      didOpen: (popup) => {
        const submitButton = popup.querySelector('.swal2-confirm') as HTMLButtonElement;
        const cancelButton = popup.querySelector('.swal2-cancel') as HTMLButtonElement;
        const otpInput = popup.querySelector('#otp') as HTMLInputElement;
        const timerDisplay = popup.querySelector('#timer') as HTMLDivElement;
        const resendOtpButton = popup.querySelector('#resendOtp') as HTMLButtonElement;

        let timeRemaining = 2 * 60; // 2 minutes in seconds
        // let timeRemaining = 20; // 5 seconds
        const intervalId = setInterval(() => {
          const minutes = Math.floor(timeRemaining / 60);
          const seconds = timeRemaining % 60;
          timerDisplay.textContent = `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;

          if (timeRemaining <= 0) {
            clearInterval(intervalId);
            otpInput.disabled = true;
            // submitButton.disabled = true;
            submitButton.style.display = 'none'; // Hide the "Submit" button
            cancelButton.style.display = 'none'; // Hide the "Cancel" button
            resendOtpButton.disabled = false; // Ensure "Resend OTP" is enabled
            resendOtpButton.style.display = 'block'; // Show the "Resend OTP" button
            Swal.showValidationMessage('Time has expired. Please request a new OTP.');
          } else {
            timeRemaining--;
          }
        }, 1000); // Update every second

        popup.addEventListener('submit', () => {
          clearInterval(intervalId); // Clear timer if user submits within the allowed time
        });

        // Handle Resend OTP button click
        resendOtpButton.addEventListener('click', () => {
          clearInterval(intervalId);
          // Swal.close();
          // Add your logic here to resend the OTP, e.g., make an API call
          console.log("Resending OTP...");
          // Re-open the Swal to prompt the user to enter the new OTP
          // (you might want to refactor this to avoid duplicating code)

          this.encryptedCredentials = {
            userName: username,
            password: password,
          }
          this.resendOtp(username, password);
          // });

        });
      }
    }).then((result) => {
      if (result.isConfirmed) {
        const otp = result.value as string;
        // Handle OTP submission here
        // console.log('Entered OTP:', otp);
        this.encryptedCredentials = {
          userName: username,
          password: password,
          otp: otp
        }
        this.authService.login(this.encryptedCredentials).subscribe(((response: any) => {
          // console.log("DATA : ", response)
          if (response.outcome != true) {
            Swal.fire({
              title: response.message,
              icon: 'warning'
            });
          }
          if (response.data != null) {
            this.authService.setToken(this.decryptData(response.data.jwtToken, key));
            let lastPasswordUpdated: Date = new Date(response.data.lastPasswordUpdated);
            const passwordPolicyDay = response.data.passwordPolicyDay;
            const role = this.decryptData(response.data.roleCode, key);
            if (response.data.orgPassPolicy === true && lastPasswordUpdated !== null) {
              this.checkPasswordStatusOrPassowordPolicy(response, lastPasswordUpdated, passwordPolicyDay, role, username, password);
            }else{
               this.navigateBasedOnRole(this.decryptData(response.data.roleCode, key));     
            }
          } else {
            Swal.fire({
              title: 'Invalid OTP.',
              icon: 'warning'
            });
          }
        }))
      }
    });
  }



  checkPasswordStatusOrPassowordPolicy(response: any, lastPasswordUpdated: Date, passwordPolicyDay: number, role: string, username: string, password: string): void {
    const key = '12345678901234567890123456789012'; // 32-byte key
    if (response.data.orgPassPolicy === true && lastPasswordUpdated !== null) {
      const currentDate: Date = new Date();
      const timeDifference: number = currentDate.getTime() - lastPasswordUpdated.getTime();
      const daysDifference: number = timeDifference / (1000 * 3600 * 24);
      // console.log("daysDifference : " + daysDifference);

      // if (response.data.mfa) {
      //   this.showOTPModal(username, password);
      // }
      if (daysDifference >= passwordPolicyDay) {
        Swal.fire({
          title: 'Expired Password',
          text: 'Your password has expired. Please update your password.',
          icon: 'error',
          confirmButtonText: 'Change Password'
        }).then((result) => {
          if (result.isConfirmed) {
            this.router.navigate(['/updatepassword']);
          } else if (result.dismiss === Swal.DismissReason.cancel) {
            console.log("User chose to skip updating the password.");
          }
        });
      } else if (daysDifference > (passwordPolicyDay - 7)) {
        Swal.fire({
          title: 'Password Expiring Soon',
          text: 'Your password will expire soon. Please update your password.',
          icon: 'warning',
          showCancelButton: true,
          confirmButtonText: 'Change Password',
          cancelButtonText: 'Skip'
        }).then((result) => {
          if (result.isConfirmed) {
            this.router.navigate(['/admin/myProfile']);
          } else if (result.dismiss === Swal.DismissReason.cancel) {
            if (role === "ROLE_VENDOR") {
              this.router.navigate(['/admin/vendordashboard']);
            } else {
              this.router.navigate(['/admin/orgadminDashboard']);
              console.log("User chose to skip updating the password.");
            }
          }
        });
      }else{
        this.navigateBasedOnRole(this.decryptData(response.data.roleCode, key));     
      }
    }
  }


}
