import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { result } from 'lodash';
import { CustomerService } from 'src/app/services/customer.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-add-check',
  templateUrl: './add-check.component.html',
  styleUrls: ['./add-check.component.scss']
})
export class AddCheckComponent implements OnInit {

  closeModal: string | undefined;
  checks: any=[];
  sourceNameValue: any = '';

  addChecksForm  = new FormGroup({
    sourceId : new FormControl(''),
    sourceName : new FormControl('',Validators.required),
    sourceCode : new FormControl('',Validators.required),
    isActive: new FormControl(''),
    changeActiveStatus: new FormControl('')
  })

  constructor(private modalService: NgbModal,private customers:CustomerService) {

    this.customers.getAllSources().subscribe((data: any)=>{
      this.checks=data.data;
      console.warn(this.checks)
      // this.getbgv.forEach((element:any) => {
      //   element.serviceId = '';
      //   element.ratePerItem = '';
      // //  element.ratePerItem = '';

      // });
    });

   

   }

  ngOnInit(): void {
    this.addChecksForm.get('sourceName')?.valueChanges.subscribe((value) => {
      this.addChecksForm.get('sourceCode')?.setValue(value.toUpperCase());
    });
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return  `with: ${reason}`;
    }
  }

  triggerModal(content: any) {
    this.addChecksForm.patchValue({
      sourceId: "",
      sourceName: "",
      sourceCode: "",
     })
    this.modalService.open(content).result.then((res) => {
      this.closeModal = `Closed with: ${res}`;
    }, (res) => {
      this.closeModal = `Dismissed ${this.getDismissReason(res)}`;
    });
  }

  submitAddCheck(){
    console.warn("CHVhdvhjsvhdjv",this.addChecksForm.value)

    this.customers.addCheckByAdmin(this.addChecksForm.value).subscribe((result:any) => {
      if(result.outcome === true){
        Swal.fire({
          title: result.message,
          icon: 'success'
        }).then((result) => {
          if (result.isConfirmed) {
            window.location.reload();
          }
        });
      }else{
        Swal.fire({
          title: result.message,
          icon: 'warning'
        }).then((result) => {
          if (result.isConfirmed) {
            window.location.reload();
          }
        });
      }

    })

  }

  openModal(modalData:any,item:any){
    this.modalService.open(modalData, {
      centered: true,
      backdrop: 'static'
     });

     console.warn("ITEM ==========",item)

     this.addChecksForm.patchValue({
      sourceId: item.sourceId,
      sourceName: item.sourceName,
      sourceCode: item.sourceCode,
      isActive: item.isActive
     })

  }

  inactiveCust(sourceId:any,status:any){

    console.warn("SOURCE_ID:::::",sourceId);
    console.warn("status:::",status)

    this.addChecksForm.patchValue({
      sourceId: sourceId,
      isActive: status,
      changeActiveStatus: true
    })

    this.submitAddCheck();

  }

  isDisabled(sourceId: number): boolean {
    return sourceId >= 1 && sourceId <= 10;
  }

  deleteCheck(sourceId:any){
    console.warn("DELETECHECK:::SOURCEID::",sourceId)

    Swal.fire({
      title: 'Are you sure?',
      text: 'You won\'t be able to revert this!',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Yes, delete it!'
    }).then((result)=>{
      if (result.isConfirmed) {
        this.customers.deleteCheckBySourceId(sourceId).subscribe((result:any)=>{
          if(result.outcome === true){
            Swal.fire({
              title: result.message,
              icon: 'success'
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
        })
      }
    })

  }

}
