import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ForgotpwdComponent } from './forgotpwd/forgotpwd.component';
import { LoginComponent } from './login/login.component';
import { UserAuthsComponent } from './user-auths.component';
import { UpdatePasswordComponent } from './update-password/update-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';

const routes: Routes = [
  { path: '', component: UserAuthsComponent,
  children:[
    { path: '', component: LoginComponent},
    { path: 'forgotpwd', component: ForgotpwdComponent},
    { path: 'updatepassword', component:UpdatePasswordComponent},
    { path: 'resetPassword/:userId', component:ResetPasswordComponent}
    ] 
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserAuthsRoutingModule { }
