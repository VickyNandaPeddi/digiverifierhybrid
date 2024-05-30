import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { AuthenticationService } from './authentication.service';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthenticationService, private router: Router, private cookieService: CookieService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    req = this.addToken(req, token);
    if (req.headers.get("No-Auth") === 'True'){
      return next.handle(req.clone());
    }
    const secureReq = req.clone({
      headers: req.headers
        .set('Content-Security-Policy', "default-src 'self';")
    });
    return next.handle(req).pipe(
      catchError(
        (err:HttpErrorResponse)=>{
          console.log(err.status)
          if(err.status === 401){
            this.router.navigate(['/'])
          }else if(err.status === 403){
            this.router.navigate(['/']);
            this.authService.forceLogout();
            const allCookies: {} = this.cookieService.getAll();
            for (const cookieName of Object.keys(allCookies)) {
              this.cookieService.delete(cookieName);
            }
            // localStorage.clear();
            window.location.reload();
          }
          return throwError("Something Went Wrong!");
        }
      )
    );
  }

  private addToken(request:HttpRequest<any>, token:string){
    return request.clone(
      {
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      }
    );
  }


}
