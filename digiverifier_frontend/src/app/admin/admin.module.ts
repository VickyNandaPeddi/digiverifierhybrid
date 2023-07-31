import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AdminRoutingModule } from './admin-routing.module';
import { AdminComponent } from './admin.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AdminHeaderComponent } from './admin-header/admin-header.component';
import { AdminFooterComponent } from './admin-footer/admin-footer.component';
import { AdminSidenavComponent } from './admin-sidenav/admin-sidenav.component';
import { TestComponent } from './test/test.component';
import { AddCustomerComponent } from './add-customer/add-customer.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CustomerListComponent } from './customer-list/customer-list.component';
import { HttpClientModule } from '@angular/common/http';
import { CustomerBillComponent } from './customer-bill/customer-bill.component';
import { AdminSetupComponent } from './admin-setup/admin-setup.component';
import { CustomerEditComponent } from './customer-edit/customer-edit.component';
import { CustomerConfigComponent } from './customer-config/customer-config.component';
import { RouterModule } from '@angular/router';
import { OrgadminUsermgmtComponent } from './orgadmin-usermgmt/orgadmin-usermgmt.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { OrgadminDashboardComponent } from './orgadmin-dashboard/orgadmin-dashboard.component';
import { OrgadminRolemgmtComponent } from './orgadmin-rolemgmt/orgadmin-rolemgmt.component';
import { ChartsModule } from '../charts/charts.module';
import { AdminFinalReportComponent } from './admin-final-report/admin-final-report.component';
import { AdminCReportApprovalComponent } from './admin-c-report-approval/admin-c-report-approval.component';
import { MyprofileComponent } from './myprofile/myprofile.component';
import { CustomerUtilizationComponent } from './customer-utilization/customer-utilization.component';
import { CustomerUtilAgentComponent } from './customer-util-agent/customer-util-agent.component';
import { CustomerUtilCandidatesComponent } from './customer-util-candidates/customer-util-candidates.component';
import { EkycreportComponent } from './ekycreport/ekycreport.component';
import { CandidateMgmtComponent } from './candidate-mgmt/candidate-mgmt.component';
import { AgGridModule } from 'ag-grid-angular';
import { VendorMgmtComponent } from './vendor-mgmt/vendor-mgmt.component';
import { AddVendorComponent } from './add-vendor/add-vendor.component';
import { VendorDashboardComponent } from './vendor-dashboard/vendor-dashboard.component';
import { VendorInitiateComponent } from './vendor-initiate/vendor-initiate.component';
import { UploadVendocheckComponent } from './upload-vendocheck/upload-vendocheck.component';
import { VendorUtilizationReportComponent } from './vendor-utilization-report/vendor-utilization-report.component';
import { DNHComponent } from './dnh/dnh.component';
import { UanSearchComponent } from './uan-search/uan-search.component';
import { DateFormatPipe } from '../pipes/date-format.pipe';


@NgModule({
  declarations: [
    VendorUtilizationReportComponent,
    AdminComponent,
    DashboardComponent,
    AdminHeaderComponent,
    AdminFooterComponent,
    AdminSidenavComponent,
    TestComponent,
    AddCustomerComponent,
    CustomerListComponent,
    CustomerBillComponent,
    AdminSetupComponent,
    CustomerEditComponent,
    CustomerConfigComponent,
    OrgadminUsermgmtComponent,
    OrgadminDashboardComponent,
    OrgadminRolemgmtComponent,
    AdminFinalReportComponent,
    AdminCReportApprovalComponent,
    MyprofileComponent,
    CustomerUtilizationComponent,
    CustomerUtilAgentComponent,
    CustomerUtilCandidatesComponent,
    EkycreportComponent,
    CandidateMgmtComponent,
    VendorMgmtComponent,
    AddVendorComponent,
    VendorDashboardComponent,
    VendorInitiateComponent,
    UploadVendocheckComponent,
    DNHComponent,
    UanSearchComponent,
    DateFormatPipe

  ],
  imports: [
    CommonModule,
    AdminRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    NgbModule,
    ChartsModule,
    AgGridModule.withComponents([])
  ]
})
export class AdminModule { }
