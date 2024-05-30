import { Component, OnInit } from '@angular/core';
import { Router ,ActivatedRoute} from '@angular/router';
import Swal from 'sweetalert2';
import { CandidateService } from 'src/app/services/candidate.service';
import { FormGroup, FormControl, FormBuilder, Validators } from '@angular/forms';


@Component({
  selector: 'app-digidocument',
  templateUrl: './digidocument.component.html',
  styleUrls: ['./digidocument.component.scss']
})
export class DigidocumentComponent implements OnInit {
  candidateCode: any;
  CandidateFormData: any=[];
  aadharName:any;
  aadharDob:any;
  result:any;
  school_boards:any=[];
  Univerisity_list:any=[];
  school:boolean=false;
  newschool_boards:any=[];
  org_id:any;
  doc:any;
  documents:any=[];
  doctype:any;
  parameters:any=[];
  accesstoken:any;
  code:any;
  digidocument = new FormGroup({
    aadharName: new FormControl('', Validators.required),
    aadharDob: new FormControl('', Validators.required),
    certificate: new FormControl('', Validators.required),
    document: new FormControl('', Validators.required),
    university: new FormControl('', Validators.required),
    param1:new FormControl('', Validators.required),
    param2:new FormControl('', Validators.required),
    param3:new FormControl('', Validators.required),
    param4:new FormControl('', Validators.required),
    param5:new FormControl('', Validators.required),
    param6:new FormControl('', Validators.required),
    param7:new FormControl('', Validators.required),
    param8:new FormControl('', Validators.required),
    param9:new FormControl('', Validators.required),
    param10:new FormControl('', Validators.required),
    param1key:new FormControl(),
    param2key:new FormControl(),
    param3key:new FormControl(),
    param4key:new FormControl(),
    param5key:new FormControl(),
    param6key:new FormControl(),
    param7key:new FormControl(),
    param8key:new FormControl(),
    param9key:new FormControl(),
    param10key:new FormControl(),
  
  
  });
  constructor(private candidateService: CandidateService,private router:ActivatedRoute, private navRouter: Router) {
    this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');
    this.accesstoken = this.router.snapshot.paramMap.get('accesstoken');
    this.code = this.router.snapshot.paramMap.get('code');

    console.log(this.candidateCode,"------------------------",this.accesstoken,this.code);
    this.candidateService.getCandidateDLdata(this.candidateCode).subscribe((data: any)=>{
      this.CandidateFormData=data.data;
      this.aadharName=this.CandidateFormData.aadharName;
      this.aadharDob=this.CandidateFormData.aadharDob;
      console.log(data,"-----------",this.aadharName,"---------",this.aadharDob)
     
      
      
    });

   }
   onSubmit(digidocument:FormGroup){
    console.log(digidocument.value)
    let dict=digidocument.value    
    dict["orgid"]=dict["university"]
    delete dict["university"]
    dict["orgid"]=this.org_id
    dict["doctype"]=dict["document"]
    delete dict["document"]
    dict["doctype"]=this.doctype   
    let i =0;
    for (let item in this.parameters){
      let no=i+1;
      console.log(this.parameters[item]["paramname"],"param"+no)      
      dict["param"+no+"key"]=this.parameters[item]["paramname"]
      // delete dict["param"+no]
      i++;      
    }
    // delete dict["param2"]
    // delete dict["param3"]
    // delete dict["param4"]
    // delete dict["param5"]
    dict["candidatecode"]=this.candidateCode
    dict["accesstoken"]=this.accesstoken
    dict["code"]=this.code
    dict["aadharName"]=this.aadharName
    dict["aadharDob"]=this.aadharDob
    console.log(dict.value,"--------")
    const formData = new FormData();
    formData.append('digidetails', JSON.stringify(dict));
    this.candidateService.getDLEdudocument(formData).subscribe((data:any)=>{
      
      console.log(data)
      if(data.outcome === true){
        
          const navURL = 'candidate/cType/'+this.candidateCode;
          this.navRouter.navigate([navURL]);
        
      }else{
        var str = data.message
        var splitted = str.split(":", 3); 
        console.log(splitted,"splitted")
        var error= splitted[0]+":"+splitted[1]+" "+splitted[2]
        Swal.fire({
          title: "Unable to fetch the Document, please chcek your inputs",
          icon: 'warning'
        })
      }
    
      
    })
   }
   cancel(){
    const navURL = 'candidate/cType/'+this.candidateCode;
    this.navRouter.navigate([navURL]);
   }
   onOptionsSelecteduniversity(value:string){
    console.log("the selected value is " + value);
    this.org_id="";
    for (let item in this.school_boards){
     let str= Object.keys(this.school_boards[item]);
     
      if(value === str[0]){
        console.log(str[0])
         console.log(this.school_boards[item][value],"orgid")
         this.org_id=this.school_boards[item][value]
      }
     
    }
    for (let item in this.Univerisity_list){
      let str= Object.keys(this.Univerisity_list[item]);
      
       if(value === str[0]){
         console.log(str[0])
          console.log(this.Univerisity_list[item][value],"0rgid")
          this.org_id=this.Univerisity_list[item][value]
       }
      
     }
     this.candidateService.getdocumenttype(this.org_id).subscribe((data:any)=>{
      
      this.doc=data;
      this.documents=[];
      for (let item in this.doc){
        console.log(item,"=====docitem========")
        
        this.documents.push(item)
      }
      
    })

   }
   onOptionsSelecteddoc(value:string){
    console.log("the selected value is " + value);
    this.doctype="";
    for (let item in this.doc){
      if (value === item){
        console.log(this.doc[value],"======doc=======")
        this.doctype=this.doc[value];
      
      }
    }
    this.candidateService.getparameters(this.org_id,this.doctype).subscribe((data:any)=>{
      
      console.log(data)
      this.parameters=data;
      console.log(this.parameters[0].label)
      
     
     
    
     

    })
   }
  onOptionsSelected(value:string){
    this.newschool_boards=[];
    console.log("the selected value is " + value);
    if (value==="Class X Marksheet"){
      this.school_boards=this.result.School_Boards;
      for (let item in this.school_boards){
        
        this.newschool_boards.push(Object.keys(this.school_boards[item]))
        
      }
    }
    else if(value==="Class XII Marksheet"){
      this.school_boards=this.result.School_Boards;
      for (let item in this.school_boards){
        
        
        this.newschool_boards.push(Object.keys(this.school_boards[item]))
      }
    }
    else if(value==="Under Graduate/Dgree/Diploma Certificate"){
      this.Univerisity_list=this.result.Univerisity_list;
      for (let item in this.Univerisity_list){
        
        
        this.newschool_boards.push(Object.keys(this.Univerisity_list[item]))
      }
    }
    else if(value==="Post Graduation Certificate"){
      this.Univerisity_list=this.result.Univerisity_list;
      for (let item in this.Univerisity_list){
        
        
        this.newschool_boards.push(Object.keys(this.Univerisity_list[item]))
      }
    }
    
   
    console.log(this.newschool_boards)
  }

  ngOnInit(): void {
    this.candidateService.getuniveristy().subscribe((data:any)=>{
      
      this.result=data;
     
     
      console.log(this.result,"=============")
    })

    //  this.candidateService.getCandidateDLdata(this.candidateCode).subscribe((data: any)=>{
    //   this.CandidateFormData=data.data;
    //   this.aadharName=this.CandidateFormData.aadharName;
    //   this.aadharDob=this.CandidateFormData.aadharDob;
    //   console.log(data,"-----------",this.aadharName,"---------",this.aadharDob)
     
      
      
    // });
  }

}
