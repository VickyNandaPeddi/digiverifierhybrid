import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserAuthsRoutingModule } from './user-auths-routing.module';
import { UserAuthsComponent } from './user-auths.component';
import { LoginComponent } from './login/login.component';
import { ForgotpwdComponent } from './forgotpwd/forgotpwd.component';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { UpdatePasswordComponent } from './update-password/update-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';


@NgModule({
  declarations: [
    UserAuthsComponent,
    LoginComponent,
    ForgotpwdComponent,
    UpdatePasswordComponent,
    ResetPasswordComponent
  ],
  imports: [
    CommonModule,
    UserAuthsRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule
  ],
  exports:[LoginComponent,ForgotpwdComponent]
})
export class UserAuthsModule { }
