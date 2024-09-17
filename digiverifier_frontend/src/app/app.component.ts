import { path } from '@amcharts/amcharts4/core';
import { Component, HostListener } from '@angular/core';
import { AuthenticationService } from './services/authentication.service';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  
  title = 'digiverifier';

  idleTime = 0;
 
  constructor(private router: Router, private authService: AuthenticationService) {
    this.resetTimer();
  }
 
  ngOnInit() {
    this.startIdleTimer();
  }
 
  @HostListener('window:mousemove')
  @HostListener('window:keypress')
  resetTimer() {
    this.idleTime = 0;
  }
 
  startIdleTimer() {
    setInterval(() => {
      this.idleTime++;
      if (this.idleTime > environment.sessionTimeOutIn * 60) { // 5 minutes of inactivity
        this.logout();
      }
    }, 1000); // Every second
  }
 
  logout() {
    // Log out the user and redirect to login
    console.log('Session expired due to inactivity.');
    this.authService.forceLogout();
  }
}
