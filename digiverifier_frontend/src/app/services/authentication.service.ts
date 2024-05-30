import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpXsrfTokenExtractor } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';
import {CookieService} from "ngx-cookie-service";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  requestHeader = new HttpHeaders(
    {"No-Auth":"True"}
  );

  constructor(private http:HttpClient, private router: Router, private cookieService: CookieService) {}
  
  login(loginData: any){
    return this.http.post(`${environment.apiUrl}/api/login/authenticate`, loginData, {headers: this.requestHeader})
  }
  public logOut(){
    return this.http.post(`${environment.apiUrl}/api/login/sign-off`, {headers: this.requestHeader})
  }
  public setRoles(roles: []){
    this.cookieService.set('roles', JSON.stringify(roles), { expires: 0 });
    // localStorage.setItem('roles', JSON.stringify(roles));
  }
  public getRoles(){ 
    return this.cookieService.get('roles');
    // return localStorage.getItem('roles'); //needs to return as Array
  }

  public setToken(jwtToken: string){
    this.cookieService.set('jwtToken', JSON.stringify(jwtToken), { expires: 0 });
    // localStorage.setItem('jwtToken', JSON.stringify(jwtToken));
  }
  public getToken(){ 
    return JSON.parse(this.cookieService.get('jwtToken') || '{}');
    // return JSON.parse(localStorage.getItem('jwtToken') || '{}'); //localStorage.getItem('jwtToken'); 
  }

  public setuserName(userName: string){
    this.cookieService.set('userName', userName, { expires: 0 });
    // localStorage.setItem('userName', userName);
  }
  public getuserName(){ 
    return this.cookieService.get('userName')
    // return localStorage.getItem('userName'); 
  }

  public setroleName(roleName: string){
    this.cookieService.set('roleName', roleName, { expires: 0 });
    // localStorage.setItem('roleName', roleName);
  }
  public getroleName(){ 
    return this.cookieService.get('roleName');
    // return localStorage.getItem('roleName'); 
  }

  public setOrgID(orgID: string){
    this.cookieService.set('orgID', orgID, { expires: 0 });
    // localStorage.setItem('orgID', orgID);
  }
  public getOrgID(){ 
    return this.cookieService.get('orgID');
    // return localStorage.getItem('orgID'); 
  }

  public setuserId(userId: string){
    this.cookieService.set('userId', userId, { expires: 0 });
    // localStorage.setItem('userId', userId);
  }
  public getuserId(){ 
    return this.cookieService.get('userId');
    // return localStorage.getItem('userId'); 
  }

  public clear(){ 
     this.cookieService.deleteAll();
    return localStorage.clear(); 
  }
  public forceLogout(){
    this.logOut().subscribe(
      (response:any)=>{
        if (response.outcome === true)
        {
          this.clear();
          this.router.navigate(['./']);
        }
      }
    ); 
  }

  public isLoggedIn(){ 
    return this.getRoles() && this.getToken();
  }
  
  public roleMatch(allowedRoles:any[]) :boolean{
    let isMatch= false;
    const roles:any = this.getRoles();
    const userRoles  =[];
    userRoles.push(roles);
    if(userRoles != null){
      for(let i=0; i< allowedRoles.length; i++){
        for(let j=0; j< userRoles.length; j++){
          if(userRoles[j] === '"'+allowedRoles[i]+'"'){
            isMatch = true;
            return isMatch;
          }else{
            break;
          }
        }
      }
    }
    return isMatch;
  }

//  getCsrfToken(): string {
//     const tokenCookie = document.cookie.split(';')
//       .find(cookie => cookie.trim().startsWith('XSRF-TOKEN='));
    
//     if (tokenCookie) {
//       const token = tokenCookie.split('=')[1];
//       console.log("token::{}",token);
//       return token;
//     }
    
//     return '';
//   }
//   private createHeaders(): HttpHeaders {
//     const headers = new HttpHeaders({
//       "No-Auth":"True",
//       "X-XSRF-TOKEN": this.getCsrfToken() || ''
//     });
//     return headers;
//   }




}
