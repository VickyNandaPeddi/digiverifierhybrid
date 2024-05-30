import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoaderComponent } from '../shared/loader/loader.component';
import { AddressVerificationComponent } from './address-verification/address-verification.component';
import { CandidateFormComponent } from './candidate-form/candidate-form.component';
import { CandidateTypeComponent } from './candidate-type/candidate-type.component';
import { CandidateComponent } from './candidate.component';
import { EpfoLoginComponent } from './epfo-login/epfo-login.component';
import { EpfoLoginNewComponent } from './epfo-login-new/epfo-login-new.component';
import { ItrLoginComponent } from './itr-login/itr-login.component';
import { LetteracceptComponent } from './letteraccept/letteraccept.component';
import { StatusMessageComponent } from './status-message/status-message.component';
import { ThankyouComponent } from './thankyou/thankyou.component';
import { UanConfirmationComponent } from './uan-confirmation/uan-confirmation.component';
import { VerifyRelationAddressComponent } from './verify-relation-address/verify-relation-address.component';
import { DigiLockerComponent } from './digi-locker/digi-locker.component';
import { DigiOtpComponent } from './digi-otp/digi-otp.component';
import { DigidocumentComponent } from './digidocument/digidocument.component';
import { AadharMessageComponent } from './aadhar-message/aadhar-message.component';
import { ConventionalCandidateUploadComponent } from './conventional-candidate-upload/conventional-candidate-upload.component';
import { ConventionLetterAcceptComponent } from './convention-letter-accept/convention-letter-accept.component';
const routes: Routes = [
  { path: '', component: CandidateComponent, 
  children:[
    { path: 'cType/:candidateCode', component: CandidateTypeComponent},
    { path: 'cAddressVerify/:candidateCode', component: AddressVerificationComponent},
    { path: 'cRelationVerify/:candidateCode', component: VerifyRelationAddressComponent},
    { path: 'cUanConfirm/:candidateCode/:epfoStat', component: UanConfirmationComponent},
    { path: 'cThankYou', component: ThankyouComponent},
    { path: 'cThankYou/:candidateCode', component: ThankyouComponent},
    { path: 'cForm/:candidateCode', component: CandidateFormComponent, pathMatch: 'full'},
    { path: 'letterAccept/:candidateCode', component: LetteracceptComponent},
    { path: 'itrlogin/:candidateCode', component: ItrLoginComponent},
    { path: 'epfologin/:candidateCode', component: EpfoLoginComponent},
    { path: 'epfologinnew/:candidateCode', component: EpfoLoginNewComponent},
    { path: 'cStatusMessage/:statcode', component: StatusMessageComponent},
    { path: 'loader', component: LoaderComponent},
    { path: 'digiLocker/:candidateCode', component: DigiLockerComponent},
    { path: 'digiOtp/:transactionid/:candidateCode', component: DigiOtpComponent},
    { path: 'digiDoc/:candidateCode/:accesstoken/:code', component: DigidocumentComponent},
    { path: 'digiAadhar/:candidateCode', component: AadharMessageComponent},
    { path: 'conventionalCandidate/:candidateCode', component: ConventionalCandidateUploadComponent},
    { path: 'conventionalLetterAccept/:candidateCode', component: ConventionLetterAcceptComponent}

  ]  
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CandidateRoutingModule { }
