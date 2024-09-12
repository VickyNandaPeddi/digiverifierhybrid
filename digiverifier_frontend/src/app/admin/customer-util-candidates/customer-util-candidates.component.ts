import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';
import { CustomerService } from 'src/app/services/customer.service';
import * as XLSX from 'xlsx';
@Component({
  selector: 'app-customer-util-candidates',
  templateUrl: './customer-util-candidates.component.html',
  styleUrls: ['./customer-util-candidates.component.scss']
})
export class CustomerUtilCandidatesComponent implements OnInit {
  pageTitle = 'Customer Utilization Report (Candidate)';
  getCandidateUtilizationReport: any=[];
  utilizationReportClick = new FormGroup({
    fromDate: new FormControl('', Validators.required),
    toDate: new FormControl('', Validators.required),
    organizationIds: new FormControl('', Validators.required),
    statusCode: new FormControl('', Validators.required),
    agentIds: new FormControl('')
  });
  /*name of the excel-file which will be downloaded. */ 
  fileName= 'export.xlsx';  
  exportexcel(): void 
    {
       /* table id is passed over here */   
       let element = document.getElementById('excel-table'); 
       const ws: XLSX.WorkSheet =XLSX.utils.table_to_sheet(element);
       /* generate workbook and add the worksheet */
       const wb: XLSX.WorkBook = XLSX.utils.book_new();
       XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');
       /* save to file */
       XLSX.writeFile(wb, this.fileName);
  } 
  constructor(private route: ActivatedRoute, private customers: CustomerService) { }

  ngOnInit(): void {
    let fromDate = this.route.snapshot.queryParamMap.get('fromDate');
    let toDate = this.route.snapshot.queryParamMap.get('toDate');
    const organizationIds = this.route.snapshot.queryParamMap.get('organizationIds');
    const statusCode = this.route.snapshot.queryParamMap.get('statusCode');
    const agentIds = this.route.snapshot.queryParamMap.get('agentIds');
    const isAgent = this.route.snapshot.queryParamMap.get('isAgent');
    console.log("Customer-utils",organizationIds, statusCode, agentIds, isAgent);
    let organizationIdArray: any=[];
    organizationIdArray.push(organizationIds);

    let agentIdsArray: any=[];
    agentIdsArray.push(agentIds);

    fromDate = fromDate!=null?fromDate.split('-').join('/'):'';
    toDate = toDate!=null?toDate.split('-').join('/'):'';
    if(isAgent == 'true'){
      this.utilizationReportClick.patchValue({
        fromDate: fromDate,
        toDate: toDate,
        organizationIds: organizationIdArray,
        statusCode: statusCode,
        agentIds: agentIdsArray,
       });
    }else{
      this.utilizationReportClick.patchValue({
        fromDate: fromDate,
        toDate: toDate,
        organizationIds: organizationIdArray,
        statusCode: statusCode,
        agentIds:[]
       });
    }
    

    const key = '12345678901234567890123456789012'; // 32-byte key
    this.customers.getCanididateDetailsByStatus(this.utilizationReportClick.value).subscribe((result: any)=>{
      console.log(JSON.stringify(result.data.candidateDetailsDto));
      this.getCandidateUtilizationReport=result.data.candidateDetailsDto;
      this.getCandidateUtilizationReport=this.getCandidateUtilizationReport.map((item:any)=>{
        console.log( item.panNumber);
        return {
              ...item,
              panNumber: this.decryptData(item.panNumber, key),
              aadharName: this.decryptData(item.aadharName, key),
              aadharNumber: this.decryptData(item.aadharNumber, key),
              aadharDob: this.decryptData(item.aadharDob, key),
              aadharGender:this.decryptData(item.aadharGender, key),
            };
      });
    });

  }

  private decryptData(encryptedData: string, key: string): string {
    const decodedBytes = atob(encryptedData);
    const keyBytes = key.split('').map(char => char.charCodeAt(0));
    let decryptedData = '';
    for (let i = 0; i < decodedBytes.length; i++) {
      decryptedData += String.fromCharCode(decodedBytes.charCodeAt(i) ^ keyBytes[i % keyBytes.length]);
    }
    // console.log("decdsafdfas  _"+decryptedData)
    return decryptedData;
  }

}
