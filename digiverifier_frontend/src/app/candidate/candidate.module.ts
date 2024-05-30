import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CandidateRoutingModule } from './candidate-routing.module';
import { CandidateComponent } from './candidate.component';
import { CandidateHeaderComponent } from './candidate-header/candidate-header.component';
import { CandidateFooterComponent } from './candidate-footer/candidate-footer.component';
import { CandidateTypeComponent } from './candidate-type/candidate-type.component';
import { CandidateFormComponent } from './candidate-form/candidate-form.component';
import { LetteracceptComponent } from './letteraccept/letteraccept.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AddressVerificationComponent } from './address-verification/address-verification.component';
import { UanConfirmationComponent } from './uan-confirmation/uan-confirmation.component';
import { ThankyouComponent } from './thankyou/thankyou.component';
import { ItrLoginComponent } from './itr-login/itr-login.component';
import { EpfoLoginComponent } from './epfo-login/epfo-login.component';
import { NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { VerifyRelationAddressComponent } from './verify-relation-address/verify-relation-address.component';
import { StatusMessageComponent } from './status-message/status-message.component';
import {AutocompleteLibModule} from 'angular-ng-autocomplete';
import { EpfoLoginNewComponent } from './epfo-login-new/epfo-login-new.component';
import { DigiLockerComponent } from './digi-locker/digi-locker.component';
import { DigiOtpComponent } from './digi-otp/digi-otp.component';
import { DigidocumentComponent } from './digidocument/digidocument.component';
import { AadharMessageComponent } from './aadhar-message/aadhar-message.component';
import { ConventionalCandidateUploadComponent } from './conventional-candidate-upload/conventional-candidate-upload.component';
import { ConventionLetterAcceptComponent } from './convention-letter-accept/convention-letter-accept.component';

@NgModule({
  declarations: [
    CandidateComponent,
    CandidateHeaderComponent,
    CandidateFooterComponent,
    CandidateTypeComponent,
    CandidateFormComponent,
    LetteracceptComponent,
    AddressVerificationComponent,
    UanConfirmationComponent,
    ThankyouComponent,
    ItrLoginComponent,
    EpfoLoginComponent,
    VerifyRelationAddressComponent,
    StatusMessageComponent,
    EpfoLoginNewComponent,
    DigiLockerComponent,
    DigiOtpComponent,
    DigidocumentComponent,
    AadharMessageComponent,
    ConventionalCandidateUploadComponent,
    ConventionLetterAcceptComponent
  ],
  imports: [
    CommonModule,
    CandidateRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    NgbDatepickerModule,
    AutocompleteLibModule
  ]
})
export class CandidateModule { }
