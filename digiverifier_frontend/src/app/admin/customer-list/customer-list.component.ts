import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CustomerService } from '../../services/customer.service';
import Swal from 'sweetalert2';
@Component({
  selector: 'app-customer-list',
  templateUrl: './customer-list.component.html',
  styleUrls: ['./customer-list.component.scss']
})
export class CustomerListComponent implements OnInit {
  pageTitle = 'View Customers';
  allPost: any=[];
  filteredPosts: any[] = []; 
  searchTerm: string = '';
  constructor( private customers:CustomerService, private _router: Router) { 
    this.customers.getCustomers().subscribe((data: any)=>{
      this.allPost=data.data;
      console.log(this.allPost);
      this.filteredPosts = this.allPost;
    })
  }

  ngOnInit(): void {
  }

  filterPosts() {
    const searchTermUpper = this.searchTerm.toUpperCase();
    this.filteredPosts = this.allPost.filter((post: any) => {
      return post.organizationName.toUpperCase().includes(searchTermUpper);
    });
  }
  
  getCustID(custID: any){
    this._router.navigate(['admin/custedit',custID]);
  }

  inactiveCust(organizationId: any, isActive: any){
    // $(this).hide();
    const toggle = !isActive;
     this.customers.customerStat(organizationId, toggle).subscribe((data: any)=>{
       for (let index = 0; index < this.allPost.length; index++) {
        if(this.allPost[index].organizationId === data.data.organizationId){
          this.allPost[index] = data.data;
          if(data.outcome === true){
              Swal.fire({
                title: data.message,
                icon: 'success'
              })
          }else{
            Swal.fire({
              title: data.message,
              icon: 'warning'
            })
          }
         }
       } 
     })
  }

  dashboardRedirect(custID: any){
    this.customers.getCustAdminDetails(Number(custID)).subscribe((result: any)=>{
      console.log(result);
      if(result.outcome === true){
        localStorage.setItem('orgID', custID);
        localStorage.setItem('userId', result.data.userId);
        this._router.navigate(['admin/orgadminDashboard']);
      }else{
        Swal.fire({
          title: result.message,
          icon: 'warning'
        })
      }
    });
  }

  deleteCust(custID: any){
    this.customers.deleteCust(custID).subscribe((result: any)=>{
      console.log(result);
      if(result.outcome === true){ 
        Swal.fire({
          title: result.message,
          icon: 'success',
        }).then((result) => {
          if (result.isConfirmed) {
            window.location.reload();
          }
        });
      }else{
        Swal.fire({
          title: result.message,
          icon: 'warning'
        })
      }
    });
  }

}
