import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminGuard } from '../services/admin.guard';
import { ErrorComponent } from '../shared/error/error.component';
import { AddCustomerComponent } from './add-customer/add-customer.component';
import { AdminCReportApprovalComponent } from './admin-c-report-approval/admin-c-report-approval.component';
import { AdminFinalReportComponent } from './admin-final-report/admin-final-report.component';
import { AdminSetupComponent } from './admin-setup/admin-setup.component';
import { AdminComponent } from './admin.component';
import { CandidateMgmtComponent } from './candidate-mgmt/candidate-mgmt.component';
import { CustomerBillComponent } from './customer-bill/customer-bill.component';
import { CustomerConfigComponent } from './customer-config/customer-config.component';
import { CustomerEditComponent } from './customer-edit/customer-edit.component';
import { CustomerListComponent } from './customer-list/customer-list.component';
import { CustomerUtilAgentComponent } from './customer-util-agent/customer-util-agent.component';
import { CustomerUtilCandidatesComponent } from './customer-util-candidates/customer-util-candidates.component';
import { CustomerUtilizationComponent } from './customer-utilization/customer-utilization.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { EkycreportComponent } from './ekycreport/ekycreport.component';
import { MyprofileComponent } from './myprofile/myprofile.component';
import { OrgadminDashboardComponent } from './orgadmin-dashboard/orgadmin-dashboard.component';
import { OrgadminRolemgmtComponent } from './orgadmin-rolemgmt/orgadmin-rolemgmt.component';
import { OrgadminUsermgmtComponent } from './orgadmin-usermgmt/orgadmin-usermgmt.component';
import { TestComponent } from './test/test.component';
import { VendorMgmtComponent } from './vendor-mgmt/vendor-mgmt.component';
import { AddVendorComponent } from './add-vendor/add-vendor.component';
import { VendorDashboardComponent } from './vendor-dashboard/vendor-dashboard.component';
import { VendorInitiateComponent } from './vendor-initiate/vendor-initiate.component';
import { UploadVendocheckComponent } from './upload-vendocheck/upload-vendocheck.component';
import { VendorUtilizationReportComponent } from './vendor-utilization-report/vendor-utilization-report.component';
import { DNHComponent } from './dnh/dnh.component';
import { UanSearchComponent } from './uan-search/uan-search.component';


const routes: Routes = [
  { path: 'vendorUtilization', component: VendorUtilizationReportComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN','ROLE_ADMIN','ROLE_PARTNERADMIN','ROLE_AGENTSUPERVISOR','ROLE_AGENTHR']}},
  { path: '', component: AdminComponent,
  children:[
    { path: '', redirectTo: 'admindashboard', pathMatch:"full", component: DashboardComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN']}},
    { path: 'admindashboard', component: DashboardComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN']}},
    { path: 'addcust', component: AddCustomerComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN']}},
    { path: 'vendormgmt/:userId', component: VendorMgmtComponent, canActivate: [AdminGuard], data:{roles:['ROLE_ADMIN']}},
    { path: 'vendormgmt', component: VendorMgmtComponent, canActivate: [AdminGuard], data:{roles:['ROLE_ADMIN']}},
    { path: 'addvendor', component: AddVendorComponent, canActivate: [AdminGuard], data:{roles:['ROLE_ADMIN']}},
    { path: 'customerUtilization', component: CustomerUtilizationComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN','ROLE_ADMIN','ROLE_PARTNERADMIN','ROLE_AGENTSUPERVISOR','ROLE_AGENTHR']}},
    { path: 'customerUtilizationAgent', component: CustomerUtilAgentComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN','ROLE_ADMIN','ROLE_PARTNERADMIN','ROLE_AGENTSUPERVISOR','ROLE_AGENTHR']}},
    { path: 'customerUtilizationCandidate', component: CustomerUtilCandidatesComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN','ROLE_ADMIN','ROLE_PARTNERADMIN','ROLE_AGENTSUPERVISOR','ROLE_AGENTHR']}},
    { path: 'ekycReport', component: EkycreportComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN','ROLE_ADMIN','ROLE_PARTNERADMIN','ROLE_AGENTSUPERVISOR','ROLE_AGENTHR']}},
    { path: 'custlist', component: CustomerListComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN']}},
    { path: 'custbill/:organizationId', component: CustomerBillComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN']}},
    { path: 'custedit/:organizationId', component: CustomerEditComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN']}},
    { path: 'adminsetup', component: AdminSetupComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN']}},
    { path: 'custconfig/:organizationId', component: CustomerConfigComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN']}},
    { path: 'test', component: TestComponent},
    { path: 'orgadminDashboard', component: OrgadminDashboardComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN','ROLE_ADMIN','ROLE_PARTNERADMIN','ROLE_AGENTSUPERVISOR','ROLE_AGENTHR','ROLE_VENDOR']}},
    { path: 'usermgmt', component: OrgadminUsermgmtComponent, canActivate: [AdminGuard], data:{roles:['ROLE_ADMIN','ROLE_PARTNERADMIN','ROLE_AGENTSUPERVISOR','ROLE_CBADMIN']}},
    { path: 'rolemgmt', component: OrgadminRolemgmtComponent, canActivate: [AdminGuard], data:{roles:['ROLE_ADMIN','ROLE_PARTNERADMIN','ROLE_AGENTSUPERVISOR']}},
    { path: 'candidateMgmt', component: CandidateMgmtComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN','ROLE_ADMIN','ROLE_PARTNERADMIN','ROLE_AGENTSUPERVISOR','ROLE_AGENTHR']}},
    { path: 'cFinalReport/:candidateCode', component: AdminFinalReportComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN','ROLE_PARTNERADMIN','ROLE_ADMIN','ROLE_AGENTSUPERVISOR','ROLE_AGENTHR']}},
    { path: 'cReportApproval/:candidateCode', component: AdminCReportApprovalComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN','ROLE_PARTNERADMIN','ROLE_ADMIN','ROLE_AGENTSUPERVISOR','ROLE_AGENTHR']}},
    { path: 'myProfile', component: MyprofileComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN','ROLE_PARTNERADMIN','ROLE_ADMIN','ROLE_AGENTSUPERVISOR','ROLE_AGENTHR']}},
    { path: 'vendordashboard', component: VendorDashboardComponent, canActivate: [AdminGuard], data:{roles:['ROLE_VENDOR']}},
    { path: 'vendorinitiaste/:candidateId', component: VendorInitiateComponent, canActivate: [AdminGuard], data:{roles:['ROLE_VENDOR','ROLE_ADMIN']}},
    { path: 'uploadvendorcheck', component: UploadVendocheckComponent, canActivate: [AdminGuard], data:{roles:['ROLE_VENDOR']}},
    { path: 'dnh', component: DNHComponent, canActivate: [AdminGuard], data:{roles:['ROLE_CBADMIN','ROLE_ADMIN']}},
    { path: 'uanSearch',component:UanSearchComponent,canActivate:[AdminGuard],data:{roles:['ROLE_CBADMIN','ROLE_PARTNERADMIN','ROLE_ADMIN','ROLE_AGENTSUPERVISOR','ROLE_AGENTHR']}},
    { path: 'error', component: ErrorComponent},

    {path: '**', redirectTo: 'error'}
  ] 
  
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
