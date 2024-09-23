import { any } from '@amcharts/amcharts4/.internal/core/utils/Array';
import { getElement } from '@amcharts/amcharts4/core';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { event } from 'jquery';
import JSZip from 'jszip';
import { forEach, remove } from 'lodash';
import { CandidateService } from 'src/app/services/candidate.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-conventional-candidate-upload',
  templateUrl: './conventional-candidate-upload.component.html',
  styleUrls: ['./conventional-candidate-upload.component.scss']
})
export class ConventionalCandidateUploadComponent implements OnInit {

  // public proofDocumentNew: any = File;
  // public proofDocumentNew: File[] = [];
  // public proofDocumentNew: Map<string, File> = new Map<string, File>();
  public proofDocumentNew: Map<string, String> = new Map<string, String>();
  @ViewChild('fileInputContainer', { static: false }) fileInputContainer!: ElementRef;
  @ViewChild('fileInput') fileInput!: ElementRef;


  base64: any;

  newProof: any;
  newProofList: string[] = [];
  candidateCode: any;
  candidateName: any = '';
  contactNumber: any = '';
  candidateMailId: any = '';
  candidateId: any = '';
  conventionalCandidateCheck: any[] = [];
  accountName: any;
  attributeListForm: any[] = [];
  AgentAttributeCheck: any = [];
  agentAttributeListForm: any[] = [];
  // Checks: any;
  Checks: any[] = [];
  subValue: string = '';
  attribute: { value: string } = { value: '' }; // Define attribute object with a value property
  selectedFiles: File[] = [];
  educationSelectedFiles: { key: string, file: File }[] = [];
  educationUniqueSelectedFiles: { key: string, file: File[] }[] = [];
  educationUGSelectedFiles: { key: string, file: File }[] = [];
  educationUGWithDocSelectedFiles: { key: string, file: File }[] = [];
  educationPGSelectedFiles: { key: string, file: File }[] = [];
  educationDiplomaSelectedFiles: { key: string, file: File }[] = [];
  education10THSelectedFiles: { key: string, file: File }[] = [];
  education12THSelectedFiles: { key: string, file: File }[] = [];
  // checkTypeList: string[] = []
  EducationUGDocTypeList: { key: string, file: File }[] = [];
  EducationPGDocTypeList: { key: string, file: File }[] = [];
  Education10THDocTypeList: { key: string, file: File }[] = [];
  Education12THDocTypeList: { key: string, file: File }[] = [];
  EducationDiplomaTHDocTypeList: { key: string, file: File }[] = [];
  EmploymentEMP1DocTypeList: { key: string, file: File }[] = [];
  EmploymentEMP2DocTypeList: { key: string, file: File }[] = [];
  EmploymentEMP3DocTypeList: { key: string, file: File }[] = [];


  tempSelectedFiles: { key: string, file: File }[] = [];

  educationSelectedFilesPG: { key: string, file: File }[] = [];
  // employmentSelectedFiles: File[] = [];
  employmentSelectedFiles: { key: string, file: File }[] = [];
  employmentEMP1SelectedFiles: { key: string, file: File }[] = [];
  employmentEMP2SelectedFiles: { key: string, file: File }[] = [];
  employmentEMP3SelectedFiles: { key: string, file: File }[] = [];

  criminalSelectedFiles: { key: string, file: File }[] = [];
  criminalPresentSelectedFiles: { key: string, file: File }[] = [];
  criminalPermanentSelectedFiles: { key: string, file: File }[] = [];

  // criminalSelectedFiles: File[] = [];
  // idSelectedFiles: File[] = [];
  idSelectedFiles: { key: string, file: File }[] = [];
  idAadharSelectedFiles: { key: string, file: File }[] = [];
  idPanSelectedFiles: { key: string, file: File }[] = [];
  // databaseSelectedFiles: File[] = [];
  databaseSelectedFiles: { key: string, file: File }[] = [];
  addressSelectedFiles: { key: string, file: File }[] = [];
  addressPresentSelectedFiles: { key: string, file: File }[] = [];
  addressPermanentSelectedFiles: { key: string, file: File }[] = [];
  sub: boolean = false;
  // public proofDocumentNew: { [key: string]: File } = {};
  checks: any[] = [];
  showFileUpload: boolean = false;
  selectedFile: File | null = null;
  private lastIndex: number = 0;




  public empexirdocument: any = File;

  constructor(
    private candidateService: CandidateService,
    private router: ActivatedRoute,
    private navRouter: Router,
    private fb: FormBuilder,
  ) {


    this.candidateCode = this.router.snapshot.paramMap.get('candidateCode');

    //getting candidate details
    this.candidateService.getCandidateDetails(this.candidateCode)
      .subscribe((data: any) => {
        data.data = this.candidateService.decryptData(data.data);
        // Parse the decrypted JSON string into an object
        data.data = JSON.parse(data.data);
        if (data.outcome === true) {
//           console.warn("DATA>>>", data)
          this.candidateName = data.data.candidateName;
          this.contactNumber = data.data.contactNumber;
          this.candidateMailId = data.data.emailId;
          this.candidateId = data.data.candidateId;
          this.accountName = data.data.accountName;
          this.conventionalCandidateCheck = data.data.conventionalCandidateCheck.split(',');

//           console.warn("checks>>", this.conventionalCandidateCheck)
          this.Checks = this.conventionalCandidateCheck;


          // this.candidateForm.controls['candidateCode'].setValue(this.candidateCode);
          this.candidateForm.controls['candidateId'].setValue(this.candidateId);
          this.candidateForm.controls['candidateName'].setValue(this.candidateName);
          this.candidateForm.controls['contactNo'].setValue(this.contactNumber);
          this.candidateForm.controls['email'].setValue(this.candidateMailId);

          this.agentAttributeListForm = this.conventionalCandidateCheck.map((ele: any) => {
            // let defaultValue;
            let defaultValue = ele === "Database" ? "NA" : ''; // Check if ele is "database", set defaultValue to "NA", otherwise empty string
            return {

              label: ele,

              value: defaultValue

            };

          });

          console.warn("this.agentAttributeListForm>>>", this.agentAttributeListForm)
          /////////////////////////////////////////

          console.warn(this.candidateName)
        } else {
          Swal.fire({
            title: data.message,
            icon: 'warning',
          });
        }
      });

  }

  ngOnInit(): void {
    this.addressCheck.push(
      { addressType: 'present', address: '' },
      { addressType: 'permanent', address: '' }
    );

    this.idChecks.push(
      {id: 'Aadhar'},
      {id: 'PAN'}
    );

    this.criminalCheck.push(
      {criminal: 'present', criminalAddress: ''},
      {criminal: 'permanent', criminalAddress: ''}
    )
  }

  candidateForm = new FormGroup({
    candidateId: new FormControl(''),
    candidateName: new FormControl(this.candidateName, Validators.required),
    contactNo: new FormControl('', Validators.required),
    email: new FormControl('', Validators.required),
    addressType: new FormControl(''),
    // education: new FormControl('', Validators.required),
    // employment: new FormControl('',Validators.required),
    // fileInput: new FormControl('',Validators.required),
    // fileInputForCriminalAndDatabase: new FormControl('',Validators.required),
    value: new FormControl(""),
    employmentSubType: new FormControl('')
  });

  //EDUCATION
  // educationLevels: string[] = ['10th Marksheet', '12th Marksheet', 'UG Marksheet', 'UG Degree/Provisional', 'PG Marksheet', 'PG Degree/Provisional', 'Diploma'];
  educationLevels: string[] = ['10TH', '12TH', 'UG', 'PG', 'Diploma'];
  educationDocument: string[] = ['Degree Certificate', 'Provisional Certificate', 'Marksheet', 'Consolidated Marksheet']
  Education: string = '';

  employmentLevels: string[] = ['EMP1', 'EMP2', 'EMP3'];


  addCheck() {
    this.checks.push({ Education: '' });
    // console.warn("educatio.push: ", this.educationSelectedFiles)
    // console.warn(" this.checks.push : ", this.checks)
  }

  // addEducationDoc: any[] = [];
  addEducationDoc: { EducationDoc: string }[] = [];
  EducationDoc: string = '';
  // addDocumnentForEducation(){
  //   console.warn("add Document Clicked !..")
  //   this.addEducationDoc.push({ EducationDoc: '' });

  //   console.warn("this.addEducationDoc !..",this.addEducationDoc)
  // }

  addDocumnentForEducation(check: any) {
    // console.warn("check : ", check)
    if (!check.documents) {
      check.documents = []; // Initialize documents array if it doesn't exist
    }
    check.documents.push({ EducationDoc: '' }); // Add a new document for the check
  }

  // removeEducationDoc(index: number,educationDoc:string) {
  //   console.warn("Education DOc : ",educationDoc)
  //   console.warn("INDEX: "+index)
  //   this.addEducationDoc.splice(index, 1);
  //   if(educationDoc == 'UG'){
  //     console.warn("UG is true")
  //     this.educationUGSelectedFiles.splice(index,1);
  //   }else if(educationDoc == 'PG'){
  //     this.educationPGSelectedFiles.splice(index,1);
  //   }else if(educationDoc == 'Diploma'){
  //     this.educationDiplomaSelectedFiles.splice(index,1);
  //   }else if(educationDoc == '10TH'){
  //     this.education10THSelectedFiles.splice(index,1);
  //   }else if(educationDoc == '12TH'){
  //     this.education12THSelectedFiles.splice(index,1);
  //   }
  //   console.warn("this.addEducationDoc Remove : ",this.educationUGSelectedFiles)
  // }

  removeEducationDoc(check: any, fullcheckName: any, index: number, checkNameAndType: any, type: any) {
    // console.warn("check : ", fullcheckName)
    // console.warn("INDEX : ", index)
    // console.warn("checkNameAndType : ", checkNameAndType)
    // console.warn("Before educationSelectedFiles : ", this.educationSelectedFiles)
    const checkName = "education";
    // console.warn("TYPE:: ", type)

    switch (checkNameAndType) {
      case 'Education UG':
        // console.warn("Handle Education UG case");
        // Your logic for Education UG
        this.educationUGSelectedFiles = this.educationSelectedFiles.filter(item => {
          return item.key !== fullcheckName;
        });
        // console.warn("After educationUGSelectedFiles : ", this.educationUGSelectedFiles)

        this.educationUGSelectedFiles = this.educationUGSelectedFiles.filter(item => {
          return item.key.includes(checkNameAndType);
        });

        let indexEduUG2Doc;
        while ((indexEduUG2Doc = this.EducationUGDocTypeList.findIndex(item => item.key && item.key === fullcheckName)) !== -1) {
            this.EducationUGDocTypeList.splice(indexEduUG2Doc, 1);
            console.warn("List UG after removal:", this.EducationUGDocTypeList);
        }
        
        let indexEduUG;
        while ((indexEduUG = this.educationSelectedFiles.findIndex(item => item.key && item.key === fullcheckName)) !== -1) {
          this.educationSelectedFiles.splice(indexEduUG, 1);
          console.warn("List educationSelectedFiles after removal:", this.educationSelectedFiles);
        }
        // console.warn("After filtering by checkNameAndType : ", this.educationUGSelectedFiles);
        this.removeTheCheckByDoc(checkName, fullcheckName, checkNameAndType, this.educationUGSelectedFiles, type);
        // console.warn("check.documents.length : ", check.documents.length)
        break;

      case 'Education PG':
        // console.warn("Handle Education PG case",this.educationPGSelectedFiles);

        this.educationPGSelectedFiles = this.educationSelectedFiles.filter(item => {
          return item.key !== fullcheckName;
        });
        // console.warn("After educationPGSelectedFiles : ", this.educationPGSelectedFiles)

        this.educationPGSelectedFiles = this.educationPGSelectedFiles.filter(item => {
          return item.key.includes(checkNameAndType);
        });

        let indexEduPGDoc;
        while ((indexEduPGDoc = this.EducationPGDocTypeList.findIndex(item => item.key && item.key === fullcheckName)) !== -1) {
            this.EducationPGDocTypeList.splice(indexEduPGDoc, 1);
            console.warn("List UG after removal:", this.EducationPGDocTypeList);
        }
        
        let indexEduPG;
        while ((indexEduPG = this.educationSelectedFiles.findIndex(item => item.key && item.key === fullcheckName)) !== -1) {
          this.educationSelectedFiles.splice(indexEduPG, 1);
          console.warn("List educationSelectedFiles after removal:", this.educationSelectedFiles);
        }

        // console.warn("After filtering by checkNameAndType : ", this.educationPGSelectedFiles);
        this.removeTheCheckByDoc(checkName, fullcheckName, checkNameAndType, this.educationPGSelectedFiles, type);
        // console.warn("check.documents.length : ", check.documents.length) 

        break;

      case 'Education Diploma':
        // console.warn("Education Diploma");
        // Your logic for Work Experience
        this.educationDiplomaSelectedFiles = this.educationSelectedFiles.filter(item => {
          return item.key !== fullcheckName;
        });
        // console.warn("After educationDiplomaSelectedFiles : ", this.educationDiplomaSelectedFiles)

        this.educationDiplomaSelectedFiles = this.educationDiplomaSelectedFiles.filter(item => {
          return item.key.includes(checkNameAndType);
        });
        // console.warn("After filtering by checkNameAndType : ", this.educationDiplomaSelectedFiles);

        let indexEduDiplomaDoc;
        while ((indexEduDiplomaDoc = this.EducationDiplomaTHDocTypeList.findIndex(item => item.key && item.key === fullcheckName)) !== -1) {
            this.EducationDiplomaTHDocTypeList.splice(indexEduDiplomaDoc, 1);
            console.warn("List Diploma after removal:", this.EducationDiplomaTHDocTypeList);
        }
        
        let indexEduDiploma;
        while ((indexEduDiploma = this.educationSelectedFiles.findIndex(item => item.key && item.key === fullcheckName)) !== -1) {
          this.educationSelectedFiles.splice(indexEduDiploma, 1);
          console.warn("List educationSelectedFiles after removal:", this.educationSelectedFiles);
        }

        this.removeTheCheckByDoc(checkName, fullcheckName, checkNameAndType, this.educationDiplomaSelectedFiles, type);
        // console.warn("check.documents.length : ", check.documents.length) 

        break;

      case 'Education 10TH':
        // console.warn("Education 10TH");
        // Your logic for Work Experience
        this.education10THSelectedFiles = this.educationSelectedFiles.filter(item => {
          return item.key !== fullcheckName;
        });
        // console.warn("After education10THSelectedFiles : ", this.education10THSelectedFiles)

        this.education10THSelectedFiles = this.education10THSelectedFiles.filter(item => {
          return item.key.includes(checkNameAndType);
        });
        // console.warn("After filtering by checkNameAndType : ", this.education10THSelectedFiles);
        let indexEdu10THDoc;
        while ((indexEdu10THDoc = this.Education10THDocTypeList.findIndex(item => item.key && item.key === fullcheckName)) !== -1) {
            this.Education10THDocTypeList.splice(indexEdu10THDoc, 1);
            console.warn("List 10TH after removal:", this.Education10THDocTypeList);
        }
        
        let indexEdu10TH;
        while ((indexEdu10TH = this.educationSelectedFiles.findIndex(item => item.key && item.key === fullcheckName)) !== -1) {
          this.educationSelectedFiles.splice(indexEdu10TH, 1);
          console.warn("List educationSelectedFiles after removal:", this.educationSelectedFiles);
        }
        this.removeTheCheckByDoc(checkName, fullcheckName, checkNameAndType, this.education10THSelectedFiles, type);
        // console.warn("check.documents.length : ", check.documents.length) 

        break;


      case 'Education 12TH':
        // console.warn("Education 12TH");
        // Your logic for Work Experience
        this.education12THSelectedFiles = this.educationSelectedFiles.filter(item => {
          return item.key !== fullcheckName;
        });
        // console.warn("After education12THSelectedFiles : ", this.education12THSelectedFiles)

        this.education12THSelectedFiles = this.education12THSelectedFiles.filter(item => {
          return item.key.includes(checkNameAndType);
        });
        // console.warn("After filtering by checkNameAndType : ", this.education12THSelectedFiles);
        let indexEdu12THDoc;
        while ((indexEdu12THDoc = this.Education12THDocTypeList.findIndex(item => item.key && item.key === fullcheckName)) !== -1) {
            this.Education12THDocTypeList.splice(indexEdu12THDoc, 1);
            console.warn("List Diploma after removal:", this.Education12THDocTypeList);
        }
        
        let indexEdu12TH;
        while ((indexEdu12TH = this.educationSelectedFiles.findIndex(item => item.key && item.key === fullcheckName)) !== -1) {
          this.educationSelectedFiles.splice(indexEdu12TH, 1);
          console.warn("List educationSelectedFiles after removal:", this.educationSelectedFiles);
        }
        this.removeTheCheckByDoc(checkName, fullcheckName, checkNameAndType, this.education12THSelectedFiles, type);
        // console.warn("check.documents.length : ", check.documents.length) 

        break;

      // default:
      //   console.warn("Handle default case");
      //   // Your default logic
      //   break;

    }
    // this.educationSelectedFiles = this.educationSelectedFiles.filter(item => {
    //   return item.key !== fullcheckName;
    // });

    // console.warn("After educationSelectedFiles : ", this.educationSelectedFiles)

    // this.educationSelectedFiles = this.educationSelectedFiles.filter(item => {
    //   return item.key.includes(checkNameAndType);
    // });

    // console.warn("After filtering by checkNameAndType : ", this.educationSelectedFiles);

    // this.removeTheCheckByDoc(checkName, fullcheckName, checkNameAndType, this.educationSelectedFiles, type);


    // console.warn("check.documents.length : ", check.documents.length)

    if (check.documents && check.documents.length > index) {
      check.documents.splice(index, 1);
      // console.warn("this.checks : ", check.documents)
    }
  }


  removeEmploymentDoc(employmentCheck: any, fullcheckName: any, index: number, checkNameAndType: any, type: any) {
    console.warn("check : ", fullcheckName)
    console.warn("INDEX : ", index)
    console.warn("checkNameAndType : ", checkNameAndType)
    console.warn("Type : ",type)
    // console.warn("Before educationSelectedFiles : ", this.employmentSelectedFiles)
    const checkName = "education";
    // console.warn("TYPE:: ", type)

    switch (checkNameAndType) {
      case 'Employment EMP1':
        // console.warn("Handle Employment EMP1");
        // Your logic for Education UG
        console.warn("employmentSelectedFiles : ",this.employmentSelectedFiles)
        this.employmentEMP1SelectedFiles = this.employmentSelectedFiles.filter(item => {
          return item.key !== fullcheckName;
        });
        console.warn("After employmentEMP1SelectedFiles : ", this.employmentEMP1SelectedFiles)

        this.employmentEMP1SelectedFiles = this.employmentEMP1SelectedFiles.filter(item => {
          return item.key.includes(checkNameAndType);
        });
        // console.warn("After filtering by checkNameAndType : ", this.employmentEMP1SelectedFiles);
        console.log("EmploymentEMP1DocTypeList : ", this.EmploymentEMP1DocTypeList);
        console.log("EmploymentEMP1DocTypeList : ");

        this.EmploymentEMP1DocTypeList.forEach((item, index) => {
      });
        console.warn("Remove File EmploymentEMP2DocTypeList : ",this.EmploymentEMP2DocTypeList)
        console.warn("Remove File EmploymentEMP3DocTypeList : ",this.EmploymentEMP3DocTypeList)

//         let index;
// while ((index = this.EmploymentEMP1DocTypeList.findIndex(item => item.key.includes("Offer Letter"))) !== -1) {
//     this.EmploymentEMP1DocTypeList.splice(index, 1);
// }    

console.log("fullcheckName : ",fullcheckName)

let index;
while ((index = this.EmploymentEMP1DocTypeList.findIndex(item => item.key && item.key === fullcheckName)) !== -1) {
    this.EmploymentEMP1DocTypeList.splice(index, 1);
    console.warn("List after removal:", this.EmploymentEMP1DocTypeList);
}

    this.EmploymentEMP1DocTypeList.forEach((item, index) => {
    });
          console.log("Remove AFter============ File EmploymentEMP1DocTypeList : ",this.EmploymentEMP1DocTypeList)
          
          let index2;
          while ((index2 = this.employmentSelectedFiles.findIndex(item => item.key && item.key === fullcheckName)) !== -1) {
            this.employmentSelectedFiles.splice(index2, 1);
            console.warn("List employmentEMP1SelectedFiles after removal:", this.employmentSelectedFiles);
          }
          
          console.log("Remove AFter============ File employmentEMP1SelectedFiles : ",this.employmentEMP1SelectedFiles)
          console.log("Remove AFter============ File employmentSelectedFiles : ",this.employmentSelectedFiles)

        this.removeTheCheckByDoc(checkName, fullcheckName, checkNameAndType, this.employmentEMP1SelectedFiles, type);
        // console.warn("check.documents.length : ", employmentCheck.documents.length)
        break;

      case 'Employment EMP2':
        // console.warn("Handle Employment EMP2");
        // Your logic for Education UG
        this.employmentEMP2SelectedFiles = this.employmentSelectedFiles.filter(item => {
          return item.key !== fullcheckName;
        });
        // console.warn("After employmentEMP2SelectedFiles : ", this.employmentEMP2SelectedFiles)

        this.employmentEMP2SelectedFiles = this.employmentEMP2SelectedFiles.filter(item => {
          return item.key.includes(checkNameAndType);
        });

        let indexEmp2Doc;
while ((indexEmp2Doc = this.EmploymentEMP2DocTypeList.findIndex(item => item.key && item.key === fullcheckName)) !== -1) {
    this.EmploymentEMP2DocTypeList.splice(indexEmp2Doc, 1);
    console.warn("List EMP2 after removal:", this.EmploymentEMP2DocTypeList);
}

let indexEmp2;
while ((indexEmp2 = this.employmentSelectedFiles.findIndex(item => item.key && item.key === fullcheckName)) !== -1) {
  this.employmentSelectedFiles.splice(indexEmp2, 1);
  console.warn("List employmentEMP2SelectedFiles after removal:", this.employmentSelectedFiles);
}

        // console.warn("After filtering by checkNameAndType : ", this.employmentEMP2SelectedFiles);
        this.removeTheCheckByDoc(checkName, fullcheckName, checkNameAndType, this.employmentEMP2SelectedFiles, type);
        // console.warn("check.documents.length : ", employmentCheck.documents.length)

        break;

      case 'Employment EMP3':
        // console.warn("Handle Employment EMP3");
        // Your logic for Education UG
        this.employmentEMP3SelectedFiles = this.employmentSelectedFiles.filter(item => {
          return item.key !== fullcheckName;
        });
        // console.warn("After employmentEMP3SelectedFiles : ", this.employmentEMP3SelectedFiles)

        this.employmentEMP3SelectedFiles = this.employmentEMP3SelectedFiles.filter(item => {
          return item.key.includes(checkNameAndType);
        });

        let indexEmp3Doc;
        while ((indexEmp3Doc = this.EmploymentEMP3DocTypeList.findIndex(item => item.key && item.key === fullcheckName)) !== -1) {
            this.EmploymentEMP3DocTypeList.splice(indexEmp3Doc, 1);
            console.warn("List EMP3 after removal:", this.EmploymentEMP3DocTypeList);
        }
        
        let indexEmp3;
        while ((indexEmp3 = this.employmentSelectedFiles.findIndex(item => item.key && item.key === fullcheckName)) !== -1) {
          this.employmentSelectedFiles.splice(indexEmp3, 1);
          console.warn("List employmentEMP3SelectedFiles after removal:", this.employmentSelectedFiles);
        }

        // console.warn("After filtering by checkNameAndType : ", this.employmentEMP3SelectedFiles);
        this.removeTheCheckByDoc(checkName, fullcheckName, checkNameAndType, this.employmentEMP3SelectedFiles, type);
        // console.warn("check.documents.length : ", employmentCheck.documents.length)

        break;

    }

    // this.employmentSelectedFiles = this.employmentSelectedFiles.filter(item => {
    //   return item.key !== fullcheckName;
    // });

    // console.warn("After employmentSelectedFiles : ", this.employmentSelectedFiles)

    // this.employmentSelectedFiles = this.employmentSelectedFiles.filter(item => {
    //   return item.key.includes(checkNameAndType);
    // });

    // console.warn("After filtering by checkNameAndType : ", this.employmentSelectedFiles);

    // this.removeTheCheckByDoc(checkName, fullcheckName, checkNameAndType, this.employmentSelectedFiles, type);


    // console.warn("check.documents.length : ", employmentCheck.documents.length)
    // console.warn("check.documents.length : ", employmentCheck.documents.EmploymentDoc)
    // console.log("INDEX NUMBER : ",index)
    if (employmentCheck.documents && employmentCheck.documents.length > index) {
      employmentCheck.documents.splice(index, 1); // Remove the document at the specified index
    }
  }


  // Define the documents for each education level
  // Example documents for each education level
  allEducationDocuments: { [key: string]: string[] } = {
    '10TH': ['Marksheet'],
    '12TH': ['Marksheet'],
    'UG': ['Degree Certificate', 'Provisional Certificate', 'Marksheet', 'Consolidated Marksheet'],
    'PG': ['Degree Certificate', 'Provisional Certificate', 'Marksheet', 'Consolidated Marksheet'],
    'Diploma': ['Degree Certificate', 'Provisional Certificate', 'Marksheet', 'Consolidated Marksheet']
  };

  filteredEducationDocuments: string[] = [];

  // updateEducationDocuments(selectedEducation: string) {
  //   this.filteredEducationDocuments = this.allEducationDocuments[selectedEducation] || [];
  // }

  previousEducationLevel: string = '';

  // docEducation: any;
  // docEmployment:any;

  // // Inside the method or wherever appropriate
  // setDocEducation(doc: any) {
  //   if (!this.docEducation && doc.EducationDoc) {
  //     this.docEducation = doc.EducationDoc;
  //   }
  // }
  // setDocEmployment(doc: any) {
  //   if (!this.docEmployment && doc.EmploymentDoc) {
  //     this.docEmployment = doc.EmploymentDoc;
  //   }
  // }
  [key: string]: any;
  setDocProperty(doc: any, propertyName: string, propertyValue: any) {
    if (!this[propertyName] && doc[propertyValue]) {
        this[propertyName] = doc[propertyValue];
    }
}

// onEducationDocChange(newValue: string, doc: any): void {
//   console.log("newValue : ",newValue)
//   doc.EducationDoc = newValue;
//   // Perform any additional logic if needed
// }

handleEducationChange(newValue: string, check: any, docEducation: any): void {
  // console.log("newValue handleEducationChange : ",newValue)
  check.Education = newValue;
  this.updateEducationDocuments(newValue, check, docEducation);
}

handleEducationDocChange(newValue: string, doc: any, check: any): void {
  // console.log("newValue handleEducationDocChange : ",newValue)
  doc.EducationDoc = newValue;
  // Perform any additional logic if needed
}



  updateEducationDocuments(selectedEducation: string, check: any, doc: any) {
    // Log the previous and current education levels
    // console.log('Previous Education Level:', this.previousEducationLevel);
    // console.log('Current Education Level:', selectedEducation);
    // console.log('DOC : ', doc);
    // console.log('CheckS : ',check)
    // console.log("check.documents : ",check.documents)

    // Remove files associated with the previous education level
    // if (this.previousEducationLevel) {
    //   this.educationSelectedFiles = this.educationSelectedFiles.filter(fileObj => 
    //     !fileObj.key.startsWith(`Education ${this.previousEducationLevel}`)
    //   );
    // }

    // if(this.previousEducationLevel == 'UG'){
    //   console.warn("EDUCATION UG TRUE::")
    //   this.educationUGSelectedFiles = [];
    // console.log('Before filtering:', this.newProofList);

    //   this.newProofList = this.newProofList.filter((item: any) => item.key === 'Education UG');

    // console.log('After filtering:', this.newProofList);
    // }
    if(this.previousEducationLevel !== ''  && this.previousEducationLevel !== selectedEducation){

    if (this.previousEducationLevel == 'UG') {
      // console.log('Previous Education Level:', this.previousEducationLevel);
      // console.log('Current Education Level:', selectedEducation);
      // console.warn("EDUCATION UG TRUE::")
      // this.educationUGSelectedFiles = [];
      // console.warn("Before educationUGSelectedFiles : ", this.educationSelectedFiles)
      // console.log('Before filtering:', this.newProofList);

    //  console.log("UG OLD DOCUMENT : ",check.documents[0].EducationDoc) 

    const documentType = check.documents[0].EducationDoc;
    // Do something with documentType if needed
    let documentTypes:any = [];
    for (let i = 0; i < check.documents.length; i++) {
      // console.log("UG OLD DOCUMENT : ", check.documents[i].EducationDoc);
      // const documentType = check.documents[0].EducationDoc
      documentTypes.push(check.documents[i].EducationDoc);
  }
  // this.educationUGSelectedFiles.forEach(file => {
  //   console.log('File:', file.file);
  //   this.uploadFile(null, "Education", selectedEducation, documentType, file.file);
  // });

  

  // console.log("this.educationUGSelectedFiles : ",this.educationUGSelectedFiles)
  // console.log("this.educationUniqueSelectedFiles : ",this.educationUniqueSelectedFiles)
  // console.log("educationList Before: ", this.educationSelectedFiles)
  const selectedFilesLength = this.educationUGSelectedFiles.length;
  // console.warn("selectedFilesLength before : ",selectedFilesLength)
  // console.log("DocumentTypes : ",documentTypes)
//   this.educationUGSelectedFiles.forEach(file => {
//     console.log('File:', file.file);
//     documentTypes.forEach((documentType:any) => {
//       if(documentType !== null){
//         console.warn("DOCUMENT TYPE : ",documentType)
//           this.uploadFile(null, "Education", selectedEducation, documentType, file.file);
//       }
//     });
// });
// console.warn("CheckTypeList: ",this.checkTypeList)
// console.warn("EucationWith Doc: ",this.educationUGWithDocSelectedFiles)
//   this.educationUGSelectedFiles.forEach(file => {
//     console.log('File:', file.file);
//     this.checkTypeList.forEach((documentType:any) => {
//       if(documentType !== null){
//         console.warn("DOCUMENT TYPE : ",documentType)
//           this.uploadFile(null, "Education", selectedEducation, documentType, file.file);
//       }
//     });
// });

// this.checkTypeList.forEach(file => {
//   console.log('File:', file.file);
//         this.uploadFile(null, "Education", selectedEducation, documentType, file.file);
//     }
// });

// console.log("EducationUGDocTypeList : ",this.EducationUGDocTypeList)

Object.entries(this.EducationUGDocTypeList).forEach(([key, file]) => {
  // console.log('Key:', file.key);
  // console.log('File:', file.file);
  this.uploadFile(null, "Education", selectedEducation, file.key, file.file);
  this.EducationUGDocTypeList = [];
});

      // removeCheck(index: number, checkName: any) {
      const checkName = this.previousEducationLevel
      const indexNumber = this.newProofList.findIndex(item => {
        // console.log("Item:", item);
        // console.log("Expected Value:", "Education " + this.previousEducationLevel);
        return item.includes("Education " + this.previousEducationLevel);
      });
      console.warn("IndexNumber of previousEducationLevel : ", indexNumber)
      if (indexNumber !== -1) {
        // this.removeCheck(indexNumber,checkName)
        this.newProofList.splice(indexNumber, 1);
        this.educationUGSelectedFiles = [];
        // console.log("educationList BEfore: ", this.educationSelectedFiles)
      }
      // const educationFileIndex = this.educationSelectedFiles.findIndex(item => {
      //   const expectedValue = "Education " + this.previousEducationLevel + ' ' + doc;
      //   console.log("item.key :", item.key);
      //   console.log("Expected Value:", expectedValue);
      //   return item.key.includes(expectedValue);
      // });
      // console.log("educationFileIndex :", educationFileIndex)

      // if (educationFileIndex !== -1) {
      //   this.educationSelectedFiles.splice(educationFileIndex, 1);
      //   console.log("After educationList : ", this.educationSelectedFiles)

      // }

    //   this.educationSelectedFiles.forEach((item, index) => {
    //     const expectedValue = "Education " + this.previousEducationLevel + ' ' + doc;
    //     console.log("item.key :", item.key);
    //     console.log("Expected Value:", expectedValue);
    //     if (item.key.includes(expectedValue)) {
    //       console.log("INDEX : ",index)
    //       if(index !== -1){
    //         this.educationSelectedFiles.splice(index, 1);
    //       }
    //     }
    // });
  //   for (let index = this.educationSelectedFiles.length - 1; index >= 0; index--) {
  //     const item = this.educationSelectedFiles[index]; 
  //     documentTypes.forEach((documentType:any) => {
  //       // this.uploadFile(null, "Education", selectedEducation, documentType, file.file);
  //       if(documentType !== null){
  //         const expectedValue = "Education " + this.previousEducationLevel + ' ' + documentType;
  //         console.log("item.key :", item.key);
  //         console.log("Expected Value:", expectedValue);
  //         if (item.key.includes(expectedValue)) {
  //             console.log("INDEX : ", index);
  //             this.educationSelectedFiles.splice(index, 1);
  //         }
  //       }
  //   }); 
  // }

  for (let index = this.educationSelectedFiles.length - 1; index >= 0; index--) {
    const item = this.educationSelectedFiles[index];
    // const expectedValue = "Education " + this.previousEducationLevel + ' ' + documentType;
    const expectedValue = "Education " + this.previousEducationLevel;
    // console.log("item.key :", item.key);
    // console.log("Expected Value:", expectedValue);
    if (item.key.includes(expectedValue)) {
        // console.log("INDEX : ", index);
        this.educationSelectedFiles.splice(index, 1);
    }
}

//   const uniqueFilenames: { [key: string]: boolean } = {};
//   this.educationSelectedFiles = this.educationSelectedFiles.filter((file) => {
//     // Extract the filename from the file object
//     const filename = file.file.name;
    
//     // Check if the filename already exists in the uniqueFilenames object
//     if (!uniqueFilenames[filename]) {
//         // If the filename doesn't exist, mark it as seen in the uniqueFilenames object
//         uniqueFilenames[filename] = true;
//         // Keep this file in the filtered array
//         return true;
//     }
//     // If the filename already exists, discard this file
//     return false;
// });
    // console.log("educationList After: ", this.educationSelectedFiles)

      // this.educationSelectedFiles.forEach((fileObj: any) => {
      //   if (fileObj.key.includes('Education UG')) {
      //     // Modify the key to replace 'Education UG' with 'Education PG'
      //     fileObj.key = fileObj.key.replace('Education UG', 'Education ' + selectedEducation);
      //   }
      // });
      // // console.log('After modification: educationSelectedFiles', this.educationSelectedFiles);
      // this.newProofList = this.newProofList.map((item: string) => {
      //   // console.warn("ITEM:", item);
      //   if (item.includes('Education UG')) {
      //     // console.warn("Education Current:", 'Education ' + selectedEducation);
      //     // Modify the string to replace 'Education UG' with the new education level
      //     return item.replace('Education UG', 'Education ' + selectedEducation);
      //   }
      //   return item; // Return the unmodified string if it doesn't contain 'Education UG'
      // });
      // console.log('After modification:', this.newProofList);
    }
    else if (this.previousEducationLevel == 'PG') {
      // console.log('Previous Education Level:', this.previousEducationLevel);
      // console.log('Current Education Level:', selectedEducation);
      // console.warn("EDUCATION PG TRUE::")
      // this.educationUGSelectedFiles = [];
      // console.warn("Before educationUGSelectedFiles : ", this.educationSelectedFiles)
      // console.log('Before filtering:', this.newProofList);
      // console.log("UG OLD DOCUMENT PG : ",check.documents[0].EducationDoc) 
      const documentType = check.documents[0].EducationDoc;
      // this.educationPGSelectedFiles.forEach(file => {
      //   console.log('File:', file.file);
      //   this.uploadFile(null, "Education", selectedEducation, documentType, file.file);
      // });
      // console.log("EducationPGDocTypeList : ",this.EducationPGDocTypeList)
      Object.entries(this.EducationPGDocTypeList).forEach(([key, file]) => {
        // console.log('Key:', file.key);
        // console.log('File:', file.file);
        this.uploadFile(null, "Education", selectedEducation, file.key, file.file);
        this.EducationPGDocTypeList = [];
      });
      // removeCheck(index: number, checkName: any) {
      const checkName = this.previousEducationLevel
      const indexNumber = this.newProofList.findIndex(item => {
        // console.log("Item:", item);
        // console.log("Expected Value:", "Education " + this.previousEducationLevel);
        return item.includes("Education " + this.previousEducationLevel);
      });
      console.warn("IndexNumber of previousEducationLevel : ", indexNumber)
      if (indexNumber !== -1) {
        // this.removeCheck(indexNumber,checkName)
        this.newProofList.splice(indexNumber, 1);
        this.educationPGSelectedFiles = [];
        console.log("educationList BEfore: ", this.educationSelectedFiles)
      }
      // const educationFileIndex = this.educationSelectedFiles.findIndex(item => {
      //   const expectedValue = "Education " + this.previousEducationLevel + ' ' + doc;
      //   console.log("item.key :", item.key);
      //   console.log("Expected Value:", expectedValue);
      //   return item.key.includes(expectedValue);
      // });
      // console.log("educationFileIndex :", educationFileIndex)

      // if (educationFileIndex !== -1) {
      //   this.educationSelectedFiles.splice(educationFileIndex, 1);
      //   console.log("After educationList : ", this.educationSelectedFiles)

      // }

    //   this.educationSelectedFiles.forEach((item, index) => {
    //     const expectedValue = "Education " + this.previousEducationLevel + ' ' + doc;
    //     console.log("item.key :", item.key);
    //     console.log("Expected Value:", expectedValue);
    //     if (item.key.includes(expectedValue)) {
    //       console.log("INDEX : ",index)
    //       if(index !== -1){
    //         this.educationSelectedFiles.splice(index, 1);
    //       }
    //     }
    // });

    for (let index = this.educationSelectedFiles.length - 1; index >= 0; index--) {
      const item = this.educationSelectedFiles[index];
      // const expectedValue = "Education " + this.previousEducationLevel + ' ' + documentType;
      const expectedValue = "Education " + this.previousEducationLevel;
      // console.log("item.key :", item.key);
      // console.log("Expected Value:", expectedValue);
      if (item.key.includes(expectedValue)) {
          // console.log("INDEX : ", index);
          this.educationSelectedFiles.splice(index, 1);
      }
  }

    // console.log("educationList After: ", this.educationSelectedFiles)



      // this.educationSelectedFiles.forEach((fileObj: any) => {
      //   if (fileObj.key.includes('Education PG')) {
      //     // Modify the key to replace 'Education UG' with 'Education PG'
      //     fileObj.key = fileObj.key.replace('Education PG', 'Education ' + selectedEducation);
      //   }
      // });
      // // console.log('After modification: educationSelectedFiles', this.educationSelectedFiles);
      // this.newProofList = this.newProofList.map((item: string) => {
      //   // console.warn("ITEM:", item);
      //   if (item.includes('Education PG')) {
      //     // console.warn("Education Current:", 'Education ' + selectedEducation);
      //     // Modify the string to replace 'Education UG' with the new education level
      //     return item.replace('Education PG', 'Education ' + selectedEducation);
      //   }
      //   return item; // Return the unmodified string if it doesn't contain 'Education UG'
      // });
      // console.log('After modification:', this.newProofList);
    }
    else if (this.previousEducationLevel == '10TH') {
      // console.log('Previous Education Level:', this.previousEducationLevel);
      // console.log('Current Education Level:', selectedEducation);
      // console.warn("EDUCATION 10TH TRUE::")
      // this.education10THSelectedFiles = [];
      // console.warn("Before educationUGSelectedFiles : ", this.educationSelectedFiles)
      // console.log('Before filtering:', this.newProofList);

      // console.warn("selectedEducation in 10th : ", selectedEducation)
      // console.log("UG OLD DOCUMENT 10TH : ",check.documents[0].EducationDoc) 
      const documentType = check.documents[0].EducationDoc;
      // console.log('After modification: education10THSelectedFiles', this.education10THSelectedFiles);
      // if (selectedEducation == '12TH') {
        // this.education10THSelectedFiles.forEach(file => {
        //   console.log('File:', file.file);
        //   this.uploadFile(null, "Education", selectedEducation, documentType, file.file);
        // });
        // console.log("")
        
        console.warn("============== Education10THDocTypeList ================ ",this.Education10THDocTypeList)
        Object.entries(this.Education10THDocTypeList).forEach(([key, file]) => {
          console.log('Key:', file.key);
          console.log('File:', file.file);
          console.log("selectedEducation : 10TH :",selectedEducation)
          this.uploadFile(null, "Education", selectedEducation, file.key, file.file);
          // this.education10THSelectedFiles = [];
          this.Education10THDocTypeList = [];
        });
        // removeCheck(index: number, checkName: any) {
        const checkName = this.previousEducationLevel
        const indexNumber = this.newProofList.findIndex(item => {
          // console.log("Item:", item);
          // console.log("Expected Value:", "Education " + this.previousEducationLevel);
          return item.includes("Education " + this.previousEducationLevel);
        });
        // console.warn("IndexNumber of previousEducationLevel : ", indexNumber)
        if (indexNumber !== -1) {
          // this.removeCheck(indexNumber,checkName)
          this.newProofList.splice(indexNumber, 1);
          this.education10THSelectedFiles = [];
          // this.Education10THDocTypeList = [];
          // console.log("educationList : ", this.educationSelectedFiles)
        }
        console.warn("============== education10THSelectedFiles ================ ",this.education10THSelectedFiles)

        // const educationFileIndex = this.educationSelectedFiles.findIndex(item => {
        //   const expectedValue = "Education " + this.previousEducationLevel + ' ' + doc;
        //   console.log("item.key :", item.key);
        //   console.log("Expected Value:", expectedValue);
        //   return item.key.includes(expectedValue);
        // });
        // console.log("educationFileIndex :", educationFileIndex)

        // if (educationFileIndex !== -1) {
        //   this.educationSelectedFiles.splice(educationFileIndex, 1);
        //   console.log("After educationList : ", this.educationSelectedFiles)

        // }

        for (let index = this.educationSelectedFiles.length - 1; index >= 0; index--) {
          const item = this.educationSelectedFiles[index];
          const expectedValue = "Education " + this.previousEducationLevel;
          // console.log("item.key :", item.key);
          // console.log("Expected Value:", expectedValue);
          if (item.key.includes(expectedValue)) {
              // console.log("INDEX : ", index);
              this.educationSelectedFiles.splice(index, 1);
          }
      }
        // console.warn("FIles for 12TH : ",this.education10THSelectedFiles.file)
        // if (this.education10THSelectedFiles.length > 0) {
        //   console.log('First file:', this.education10THSelectedFiles[0]);
        // }
      // }

      // this.education10THSelectedFiles = [];
      // this.educationSelectedFiles.forEach((fileObj: any) => {
      //   if (fileObj.key.includes('Education 10TH')) {
      //     // Modify the key to replace 'Education UG' with 'Education PG'
      //     fileObj.key = fileObj.key.replace('Education 10TH', 'Education ' + selectedEducation);
      //   }
      // });
      // this.newProofList = this.newProofList.map((item: string) => {
      //   // console.warn("ITEM:", item);
      //   if (item.includes('Education 10TH')) {
      //     console.warn("Education Current:", 'Education ' + selectedEducation);
      //     // Modify the string to replace 'Education UG' with the new education level
      //     return item.replace('Education 10TH', 'Education ' + selectedEducation);
      //   }
      //   return item; // Return the unmodified string if it doesn't contain 'Education UG'
      // });
      // console.log('After modification:', this.newProofList);
    }
    else if (this.previousEducationLevel == '12TH') {
      // console.log('Previous Education Level:', this.previousEducationLevel);
      // console.log('Current Education Level:', selectedEducation);
      // console.warn("EDUCATION 12TH TRUE::")
      // this.educationUGSelectedFiles = [];
      // console.warn("Before educationUGSelectedFiles : ", this.educationSelectedFiles)
      // console.warn("selectedEducation in 12th : ", selectedEducation)
      // console.log('Before filtering:', this.newProofList);
      // this.education12THSelectedFiles = [];
      // console.log("Before : education12THSelectedFiles ", this.education12THSelectedFiles);
      // console.log("UG OLD DOCUMENT 12TH : ",check.documents[0].EducationDoc) 
      const documentType = check.documents[0].EducationDoc;
      // this.education12THSelectedFiles.forEach(file => {
      //   console.log('File:', file.file);
      //   this.uploadFile(null, "Education", selectedEducation, documentType, file.file);
      // });
      Object.entries(this.Education12THDocTypeList).forEach(([key, file]) => {
        console.log('Key:', file.key);
        console.log('File:', file.file);
        this.uploadFile(null, "Education", selectedEducation, file.key, file.file);
        this.Education12THDocTypeList = [];
        // this.education12THSelectedFiles = [];
      });
      // removeCheck(index: number, checkName: any) {
      const checkName = this.previousEducationLevel
      const indexNumber = this.newProofList.findIndex(item => {
        // console.log("Item:", item);
        // console.log("Expected Value:", "Education " + this.previousEducationLevel);
        return item.includes("Education " + this.previousEducationLevel);
      });
      // console.warn("IndexNumber of previousEducationLevel : ", indexNumber)
      if (indexNumber !== -1) {
        // this.removeCheck(indexNumber,checkName)
        this.newProofList.splice(indexNumber, 1);
        this.education12THSelectedFiles = [];
        // this.Education12THDocTypeList = [];
        // console.log("educationList : ", this.educationSelectedFiles)
      }
      // const educationFileIndex = this.educationSelectedFiles.findIndex(item => {
      //   const expectedValue = "Education " + this.previousEducationLevel + ' ' + doc;
      //   console.log("item.key :", item.key);
      //   console.log("Expected Value:", expectedValue);
      //   return item.key.includes(expectedValue);
      // });
      // console.log("educationFileIndex :", educationFileIndex)

      // if (educationFileIndex !== -1) {
      //   this.educationSelectedFiles.splice(educationFileIndex, 1);
      //   console.log("After educationList : ", this.educationSelectedFiles)

      // }

      for (let index = this.educationSelectedFiles.length - 1; index >= 0; index--) {
        const item = this.educationSelectedFiles[index];
        const expectedValue = "Education " + this.previousEducationLevel;
        console.log("item.key :", item.key);
        console.log("Expected Value:", expectedValue);
        if (item.key.includes(expectedValue)) {
            console.log("INDEX : ", index);
            this.educationSelectedFiles.splice(index, 1);
        }
    }


      // this.educationSelectedFiles.forEach((fileObj: any) => {
      //   if (fileObj.key.includes('Education 12TH')) {
      //     // Modify the key to replace 'Education UG' with 'Education PG'
      //     fileObj.key = fileObj.key.replace('Education 12TH', 'Education ' + selectedEducation);
      //   }
      // });
      // console.log("After : educationSelectedFiles ", this.education12THSelectedFiles);
      // console.log('After modification: educationSelectedFiles', this.education12THSelectedFiles);
      // this.newProofList = this.newProofList.map((item: string) => {
      //   // console.warn("ITEM:", item);
      //   if (item.includes('Education 12TH')) {
      //     console.warn("Education Current:", 'Education ' + selectedEducation);
      //     // Modify the string to replace 'Education UG' with the new education level
      //     return item.replace('Education 12TH', 'Education ' + selectedEducation);
      //   }
      //   return item; // Return the unmodified string if it doesn't contain 'Education UG'
      // });
      // console.log('After modification:', this.newProofList);
    }
    else if (this.previousEducationLevel == 'Diploma') {
      // console.log('Previous Education Level:', this.previousEducationLevel);
      // console.log('Current Education Level:', selectedEducation);
      // console.warn("EDUCATION Diploma TRUE::")
      // this.educationUGSelectedFiles = [];
      // console.warn("Before educationUGSelectedFiles : ", this.educationSelectedFiles)
      // console.log('Before filtering:', this.newProofList);
      // console.log("UG OLD DOCUMENT Diploma : ",check.documents[0].EducationDoc) 
      const documentType = check.documents[0].EducationDoc;
      // this.educationDiplomaSelectedFiles.forEach(file => {
      //   console.log('File:', file.file);
      //   this.uploadFile(null, "Education", selectedEducation, documentType, file.file);
      // });
      Object.entries(this.EducationDiplomaTHDocTypeList).forEach(([key, file]) => {
        // console.log('Key:', file.key);
        // console.log('File:', file.file);
        this.uploadFile(null, "Education", selectedEducation, file.key, file.file);
        this.EducationDiplomaTHDocTypeList = [];
      });
      // removeCheck(index: number, checkName: any) {
      const checkName = this.previousEducationLevel
      const indexNumber = this.newProofList.findIndex(item => {
        // console.log("Item:", item);
        // console.log("Expected Value:", "Education " + this.previousEducationLevel);
        return item.includes("Education " + this.previousEducationLevel);
      });
      // console.warn("IndexNumber of previousEducationLevel : ", indexNumber)
      if (indexNumber !== -1) {
        // this.removeCheck(indexNumber,checkName)
        this.newProofList.splice(indexNumber, 1);
        this.educationDiplomaSelectedFiles = [];
        // console.log("educationList : ", this.educationSelectedFiles)
      }
      // const educationFileIndex = this.educationSelectedFiles.findIndex(item => {
      //   const expectedValue = "Education " + this.previousEducationLevel + ' ' + doc;
      //   console.log("item.key :", item.key);
      //   console.log("Expected Value:", expectedValue);
      //   return item.key.includes(expectedValue);
      // });
      // console.log("educationFileIndex :", educationFileIndex)

      // if (educationFileIndex !== -1) {
      //   this.educationSelectedFiles.splice(educationFileIndex, 1);
      //   console.log("After educationList : ", this.educationSelectedFiles)

      // }

      for (let index = this.educationSelectedFiles.length - 1; index >= 0; index--) {
        const item = this.educationSelectedFiles[index];
        const expectedValue = "Education " + this.previousEducationLevel;
        // console.log("item.key :", item.key);
        // console.log("Expected Value:", expectedValue);
        if (item.key.includes(expectedValue)) {
            // console.log("INDEX : ", index);
            this.educationSelectedFiles.splice(index, 1);
        }
    }

      // this.educationSelectedFiles.forEach((fileObj: any) => {
      //   if (fileObj.key.includes('Education Diploma')) {
      //     // Modify the key to replace 'Education UG' with 'Education PG'
      //     fileObj.key = fileObj.key.replace('Education Diploma', 'Education ' + selectedEducation);
      //   }
      // });
      // // console.log('After modification: educationSelectedFiles', this.educationSelectedFiles);
      // this.newProofList = this.newProofList.map((item: string) => {
      //   // console.warn("ITEM:", item);
      //   if (item.includes('Education Diploma')) {
      //     // console.warn("Education Current:", 'Education ' + selectedEducation);
      //     // Modify the string to replace 'Education UG' with the new education level
      //     return item.replace('Education Diploma', 'Education ' + selectedEducation);
      //   }
      //   return item; // Return the unmodified string if it doesn't contain 'Education UG'
      // });
      // console.log('After modification:', this.newProofList);
    }

    // // Update the check's education and reset documents
    // check.Education = selectedEducation;
    // check.documents = [];

    // // Update filtered documents for new education level
    // this.filteredEducationDocuments = this.allEducationDocuments[selectedEducation] || [];

    // // Update the previous education level
    // this.previousEducationLevel = selectedEducation;
    this.disableDropdownTemporarily();
  }
}

  previousEmploymentLevel: string = '';

  updateEmploymentDocuments(selectedEmployment: string, check: any,doc:any) {
    // Log the previous and current education levels
    // console.log('Previous Employment Level:', this.previousEmploymentLevel);
    // console.log('Current Employment Level:', selectedEmployment);
    // console.log("Emp Doc : ",doc)
    if(this.previousEmploymentLevel !== ''  && this.previousEmploymentLevel !== selectedEmployment){
    if (this.previousEmploymentLevel == 'EMP1') {
      // this.educationUGSelectedFiles = [];
      // console.warn("Before EmploymentEMP1SelectedFiles : ", this.employmentSelectedFiles)
      // console.log('Before filtering:', this.newProofList);
      // console.log("EMP1 OLD DOCUMENT EMP1 : ",check.documents[0].EmploymentDoc) 
      const documentType = check.documents[0].EmploymentDoc;
      // this.employmentEMP1SelectedFiles.forEach(file => {
      //   console.log('File:', file.file);
      //   this.uploadFile(null, "Employment", selectedEmployment, documentType, file.file);
      // });
      Object.entries(this.EmploymentEMP1DocTypeList).forEach(([key, file]) => {
        // console.log('Key:', file.key);
        // console.log('File:', file.file);
        this.uploadFile(null, "Employment", selectedEmployment, file.key, file.file);
        this.EmploymentEMP1DocTypeList = [];
      });
      // removeCheck(index: number, checkName: any) {
      const checkName = this.previousEmploymentLevel
      const indexNumber = this.newProofList.findIndex(item => {
        // console.log("Item:", item);
        // console.log("Expected Value:", "Employment " + this.previousEmploymentLevel);
        return item.includes("Employment " + this.previousEmploymentLevel);
      });
      // console.warn("IndexNumber of previousEmploymentLevel : ", indexNumber)
      if (indexNumber !== -1) {
        // this.removeCheck(indexNumber,checkName)
        this.newProofList.splice(indexNumber, 1);
        this.employmentEMP1SelectedFiles = [];
        // console.log("employmentList Before: ", this.employmentSelectedFiles)
      }

      for (let index = this.employmentSelectedFiles.length - 1; index >= 0; index--) {
        const item = this.employmentSelectedFiles[index];
        const expectedValue = "Employment " + this.previousEmploymentLevel;
        // console.log("item.key :", item.key);
        // console.log("Expected Value:", expectedValue);
        if (item.key.includes(expectedValue)) {
            // console.log("INDEX : ", index);
            this.employmentSelectedFiles.splice(index, 1);
        }
    }

    console.log("employmentList After: ", this.employmentSelectedFiles)


      // this.employmentSelectedFiles.forEach((fileObj: any) => {
      //   if (fileObj.key.includes('Employment EMP1')) {
      //     // Modify the key to replace 'Education UG' with 'Education PG'
      //     fileObj.key = fileObj.key.replace('Employment EMP1', 'Employment ' + selectedEmployment);
      //   }
      // });
      // // console.log('After modification: employmentSelectedFiles', this.employmentSelectedFiles);
      // this.newProofList = this.newProofList.map((item: string) => {
      //   // console.warn("ITEM:", item);
      //   if (item.includes('Employment EMP1')) {
      //     // console.warn("Employment Current:", 'Employment ' + selectedEmployment);
      //     // Modify the string to replace 'Education UG' with the new education level
      //     return item.replace('Employment EMP1', 'Employment ' + selectedEmployment);
      //   }
      //   return item; // Return the unmodified string if it doesn't contain 'Education UG'
      // });
      // console.log('After modification:', this.newProofList);
    }
    else if (this.previousEmploymentLevel == 'EMP2') {
      // console.warn("Before EmploymentEMP1SelectedFiles : ", this.employmentSelectedFiles)
      // console.log('Before filtering:', this.newProofList);
      // console.log("EMP1 OLD DOCUMENT EMP2 : ",check.documents[0].EmploymentDoc) 
      const documentType = check.documents[0].EmploymentDoc;

      // this.employmentEMP2SelectedFiles.forEach(file => {
      //   console.log('File:', file.file);
      //   this.uploadFile(null, "Employment", selectedEmployment, documentType, file.file);
      // });
      Object.entries(this.EmploymentEMP2DocTypeList).forEach(([key, file]) => {
        // console.log('Key:', file.key);
        // console.log('File:', file.file);
        this.uploadFile(null, "Employment", selectedEmployment, file.key, file.file);
        this.EmploymentEMP2DocTypeList = [];
      });
      // removeCheck(index: number, checkName: any) {
      const checkName = this.previousEmploymentLevel
      const indexNumber = this.newProofList.findIndex(item => {
        // console.log("Item:", item);
        // console.log("Expected Value:", "Employment " + this.previousEmploymentLevel);
        return item.includes("Employment " + this.previousEmploymentLevel);
      });
      // console.warn("IndexNumber of previousEmploymentLevel : ", indexNumber)
      if (indexNumber !== -1) {
        // this.removeCheck(indexNumber,checkName)
        this.newProofList.splice(indexNumber, 1);
        this.employmentEMP2SelectedFiles = [];
        // console.log("employmentList Before: ", this.employmentSelectedFiles)
      }

      for (let index = this.employmentSelectedFiles.length - 1; index >= 0; index--) {
        const item = this.employmentSelectedFiles[index];
        const expectedValue = "Employment " + this.previousEmploymentLevel;
        // console.log("item.key :", item.key);
        // console.log("Expected Value:", expectedValue);
        if (item.key.includes(expectedValue)) {
            // console.log("INDEX : ", index);
            this.employmentSelectedFiles.splice(index, 1);
        }
    }

    console.log("employmentList After: ", this.employmentSelectedFiles)


      // this.employmentSelectedFiles.forEach((fileObj: any) => {
      //   if (fileObj.key.includes('Employment EMP2')) {
      //     // Modify the key to replace 'Education UG' with 'Education PG'
      //     fileObj.key = fileObj.key.replace('Employment EMP2', 'Employment ' + selectedEmployment);
      //   }
      // });
      // // console.log('After modification: employmentSelectedFiles', this.employmentSelectedFiles);
      // this.newProofList = this.newProofList.map((item: string) => {
      //   console.warn("ITEM:", item);
      //   if (item.includes('Employment EMP2')) {
      //     // console.warn("Employment Current:", 'Employment ' + selectedEmployment);
      //     // Modify the string to replace 'Education UG' with the new education level
      //     return item.replace('Employment EMP2', 'Employment ' + selectedEmployment);
      //   }
      //   return item; // Return the unmodified string if it doesn't contain 'Education UG'
      // });
      // console.log('After modification:', this.newProofList);
    }

    else if (this.previousEmploymentLevel == 'EMP3') {
      // console.warn("Before EmploymentEMP1SelectedFiles : ", this.employmentSelectedFiles)
      // console.log('Before filtering:', this.newProofList);

      // this.employmentSelectedFiles.forEach((fileObj: any) => {
      //   if (fileObj.key.includes('Employment EMP3')) {
      //     // Modify the key to replace 'Education UG' with 'Education PG'
      //     fileObj.key = fileObj.key.replace('Employment EMP3', 'Employment ' + selectedEmployment);
      //   }
      // });
      // // console.log('After modification: employmentSelectedFiles', this.employmentSelectedFiles);
      // this.newProofList = this.newProofList.map((item: string) => {
      //   // console.warn("ITEM:", item);
      //   if (item.includes('Employment EMP3')) {
      //     // console.warn("Employment Current:", 'Employment ' + selectedEmployment);
      //     // Modify the string to replace 'Education UG' with the new education level
      //     return item.replace('Employment EMP3', 'Employment ' + selectedEmployment);
      //   }
      //   return item; // Return the unmodified string if it doesn't contain 'Education UG'
      // });
      // console.log("EMP1 OLD DOCUMENT EMP3 : ",check.documents[0].EmploymentDoc) 
      const documentType = check.documents[0].EmploymentDoc;

      // this.employmentEMP3SelectedFiles.forEach(file => {
      //   console.log('File:', file.file);
      //   this.uploadFile(null, "Employment", selectedEmployment, documentType, file.file);
      // });
      Object.entries(this.EmploymentEMP3DocTypeList).forEach(([key, file]) => {
        // console.log('Key:', file.key);
        // console.log('File:', file.file);
        this.uploadFile(null, "Employment", selectedEmployment, file.key, file.file);
        this.EmploymentEMP3DocTypeList = [];
      });
      // removeCheck(index: number, checkName: any) {
      const checkName = this.previousEducationLevel
      const indexNumber = this.newProofList.findIndex(item => {
        // console.log("Item:", item);
        // console.log("Expected Value:", "Employment " + this.previousEmploymentLevel);
        return item.includes("Employment " + this.previousEmploymentLevel);
      });
      // console.warn("IndexNumber of previousEmploymentLevel : ", indexNumber)
      if (indexNumber !== -1) {
        // this.removeCheck(indexNumber,checkName)
        this.newProofList.splice(indexNumber, 1);
        this.employmentEMP3SelectedFiles = [];
        // console.log("employmentList Before: ", this.employmentSelectedFiles)
      }

      for (let index = this.employmentSelectedFiles.length - 1; index >= 0; index--) {
        const item = this.employmentSelectedFiles[index];
        const expectedValue = "Employment " + this.previousEmploymentLevel;
        // console.log("item.key :", item.key);
        // console.log("Expected Value:", expectedValue);
        if (item.key.includes(expectedValue)) {
            // console.log("INDEX : ", index);
            this.employmentSelectedFiles.splice(index, 1);
        }
    }

    // console.log("employmentList After: ", this.employmentSelectedFiles)


    //   console.log('After modification:', this.newProofList);
    }
    this.disableDropdownTemporarily();
  }
  }

  previousIDLevel: string = '';

  updateIDDocuments(selectedIDItems: string, check: any) {
    // Log the previous and current education levels
    // console.log('Previous ID Level:', this.previousIDLevel);
    // console.log('Current ID Level:', selectedIDItems);

    if(this.previousIDLevel !== ''  && this.previousIDLevel !== selectedIDItems){
       if (this.previousIDLevel == 'Aadhar') {
      // this.educationUGSelectedFiles = [];
      // this.idSelectedFiles.forEach((fileObj: any) => {
      //   if (fileObj.key.includes('ID Aadhar')) {
      //     // Modify the key to replace 'Education UG' with 'Education PG'
      //     fileObj.key = fileObj.key.replace('ID Aadhar', 'ID ' + selectedIDItems);
      //   }
      // });
      // console.log('After modification: employmentSelectedFiles', this.idSelectedFiles);
      // this.newProofList = this.newProofList.map((item: string) => {
      //   // console.warn("ITEM:", item);
      //   if (item.includes('ID Aadhar')) {
      //     // Modify the string to replace 'Education UG' with the new education level
      //     return item.replace('ID Aadhar', 'ID ' + selectedIDItems);
      //   }
      //   return item; // Return the unmodified string if it doesn't contain 'Education UG'
      // });

      
      this.idAadharSelectedFiles.forEach(file => {
        // console.log('File:', file.file);
        this.uploadFile(null, "ID", selectedIDItems, '', file.file);
      });
      // removeCheck(index: number, checkName: any) {
      const checkName = this.previousIDLevel
      const indexNumber = this.newProofList.findIndex(item => {
        // console.log("Item:", item);
        // console.log("Expected Value:", "ID " + this.previousIDLevel);
        return item.includes("ID " + this.previousIDLevel);
      });
      // console.warn("IndexNumber of previousIDLevel : ", indexNumber)
      if (indexNumber !== -1) {
        // this.removeCheck(indexNumber,checkName)
        this.newProofList.splice(indexNumber, 1);
        this.idAadharSelectedFiles = [];
        // console.log("idList Before: ", this.idSelectedFiles)
      }

      for (let index = this.idSelectedFiles.length - 1; index >= 0; index--) {
        const item = this.idSelectedFiles[index];
        const expectedValue = "ID " + this.previousIDLevel;
        // console.log("item.key :", item.key);
        // console.log("Expected Value:", expectedValue);
        if (item.key.includes(expectedValue)) {
            // console.log("INDEX : ", index);
            this.idSelectedFiles.splice(index, 1);
        }
    }

    // console.log("idList After: ", this.idSelectedFiles)


      // console.log('After modification:', this.newProofList);

    }
    else if (this.previousIDLevel == 'PAN') {
      // this.idSelectedFiles.forEach((fileObj: any) => {
      //   if (fileObj.key.includes('ID PAN')) {
      //     // Modify the key to replace 'Education UG' with 'Education PG'
      //     fileObj.key = fileObj.key.replace('ID PAN', 'ID ' + selectedIDItems);
      //   }
      // });
      // console.log('After modification: employmentSelectedFiles', this.idSelectedFiles);
      // this.newProofList = this.newProofList.map((item: string) => {
      //   // console.warn("ITEM:", item);
      //   if (item.includes('ID PAN')) {
      //     // Modify the string to replace 'Education UG' with the new education level
      //     return item.replace('ID PAN', 'ID ' + selectedIDItems);
      //   }
      //   return item; // Return the unmodified string if it doesn't contain 'Education UG'
      // });
      this.idPanSelectedFiles.forEach(file => {
        // console.log('File:', file.file);
        this.uploadFile(null, "ID", selectedIDItems, '', file.file);
      });
      // removeCheck(index: number, checkName: any) {
      const checkName = this.previousIDLevel
      const indexNumber = this.newProofList.findIndex(item => {
        // console.log("Item:", item);
        // console.log("Expected Value:", "ID " + this.previousIDLevel);
        return item.includes("ID " + this.previousIDLevel);
      });
      // console.warn("IndexNumber of previousIDLevel : ", indexNumber)
      if (indexNumber !== -1) {
        // this.removeCheck(indexNumber,checkName)
        this.newProofList.splice(indexNumber, 1);
        this.idPanSelectedFiles = [];
        // console.log("idList Before: ", this.idSelectedFiles)
      }

      for (let index = this.idSelectedFiles.length - 1; index >= 0; index--) {
        const item = this.idSelectedFiles[index];
        const expectedValue = "ID " + this.previousIDLevel;
        // console.log("item.key :", item.key);
        // console.log("Expected Value:", expectedValue);
        if (item.key.includes(expectedValue)) {
            // console.log("INDEX : ", index);
            this.idSelectedFiles.splice(index, 1);
        }
    }

    // console.log("idList After: ", this.idSelectedFiles)



      // console.log('After modification:', this.newProofList);
    }
    this.disableDropdownTemporarily();
  }
  }

  previousCriminalLevel: any = '';
  isDropdownDisabled: boolean = false;


  updateCriminalDocuments(selectedCriminalItems: string, check: any) {
    // Log the previous and current education levels
    // console.log("criminal Check : ", this.criminalCheck)
    // console.log('Previous ID Level:', this.previousCriminalLevel);
    // console.log('Current ID Level:', selectedCriminalItems);

    if(this.previousCriminalLevel !== ''  && this.previousCriminalLevel !== selectedCriminalItems){

    if (this.previousCriminalLevel == 'present') {
      // this.educationUGSelectedFiles = [];
      // this.criminalSelectedFiles.forEach((fileObj: any) => {
      //   if (fileObj.key.includes('Criminal present')) {
      //     // Modify the key to replace 'Education UG' with 'Education PG'
      //     fileObj.key = fileObj.key.replace('Criminal present', 'Criminal ' + selectedCriminalItems);
      //   }
      // });
      // // console.log('After modification: criminalSelectedFiles', this.criminalSelectedFiles);
      // this.newProofList = this.newProofList.map((item: string) => {
      //   // console.warn("ITEM:", item);
      //   if (item.includes('Criminal present')) {
      //     // Modify the string to replace 'Education UG' with the new education level
      //     return item.replace('Criminal present', 'Criminal ' + selectedCriminalItems);
      //   }
      //   return item; // Return the unmodified string if it doesn't contain 'Education UG'
      // });

      this.criminalPresentSelectedFiles.forEach(file => {
        // console.log('File:', file.file);
        this.uploadFile(null, "Criminal", selectedCriminalItems, '', file.file);
      });
      // removeCheck(index: number, checkName: any) {
      const checkName = this.previousCriminalLevel
      const indexNumber = this.newProofList.findIndex(item => {
        // console.log("Item:", item);
        // console.log("Expected Value:", "Criminal " + this.previousCriminalLevel);
        return item.includes("Criminal " + this.previousCriminalLevel);
      });
      // console.warn("IndexNumber of previousCriminalLevel : ", indexNumber)
      if (indexNumber !== -1) {
        // this.removeCheck(indexNumber,checkName)
        this.newProofList.splice(indexNumber, 1);
        this.criminalPresentSelectedFiles = [];
        // console.log("criminalList Before: ", this.criminalSelectedFiles)
      }

      for (let index = this.criminalSelectedFiles.length - 1; index >= 0; index--) {
        const item = this.criminalSelectedFiles[index];
        const expectedValue = "Criminal " + this.previousCriminalLevel;
        // console.log("item.key :", item.key);
        // console.log("Expected Value:", expectedValue);
        if (item.key.includes(expectedValue)) {
            // console.log("INDEX : ", index);
            this.criminalSelectedFiles.splice(index, 1);
        }
    }

    // console.log("CriminalList After: ", this.criminalSelectedFiles)

    //   console.log('After modification:', this.newProofList);
    }
    else if (this.previousCriminalLevel == 'permanent') {
      // this.criminalSelectedFiles.forEach((fileObj: any) => {
      //   if (fileObj.key.includes('Criminal permanent')) {
      //     // Modify the key to replace 'Education UG' with 'Education PG'
      //     fileObj.key = fileObj.key.replace('Criminal permanent', 'Criminal ' + selectedCriminalItems);
      //   }
      // });
      // console.log('After modification: criminalSelectedFiles', this.criminalSelectedFiles);
      // this.newProofList = this.newProofList.map((item: string) => {
      //   // console.warn("ITEM:", item);
      //   if (item.includes('Criminal permanent')) {
      //     // Modify the string to replace 'Education UG' with the new education level
      //     return item.replace('Criminal permanent', 'Criminal ' + selectedCriminalItems);
      //   }
      //   return item; // Return the unmodified string if it doesn't contain 'Education UG'
      // });

      this.criminalPermanentSelectedFiles.forEach(file => {
        // console.log('File:', file.file);
        this.uploadFile(null, "Criminal", selectedCriminalItems, '', file.file);
      });
      // removeCheck(index: number, checkName: any) {
      const checkName = this.previousCriminalLevel
      const indexNumber = this.newProofList.findIndex(item => {
        // console.log("Item:", item);
        // console.log("Expected Value:", "Criminal " + this.previousCriminalLevel);
        return item.includes("Criminal " + this.previousCriminalLevel);
      });
      // console.warn("IndexNumber of previousCriminalLevel : ", indexNumber)
      if (indexNumber !== -1) {
        // this.removeCheck(indexNumber,checkName)
        this.newProofList.splice(indexNumber, 1);
        this.criminalPermanentSelectedFiles = [];
        // console.log("criminalList Before: ", this.criminalSelectedFiles)
      }

      for (let index = this.criminalSelectedFiles.length - 1; index >= 0; index--) {
        const item = this.criminalSelectedFiles[index];
        const expectedValue = "Criminal " + this.previousCriminalLevel;
        // console.log("item.key :", item.key);
        // console.log("Expected Value:", expectedValue);
        if (item.key.includes(expectedValue)) {
            // console.log("INDEX : ", index);
            this.criminalSelectedFiles.splice(index, 1);
        }
    }

      // console.log('After modification:', this.newProofList);

    }
    this.disableDropdownTemporarily();
  }

  }

  disableDropdownTemporarily() {
    this.isDropdownDisabled = true;
    setTimeout(() => {
      this.isDropdownDisabled = false;
    }, 1000); // 2000 milliseconds = 2 seconds
  }



  removeCheck(index: number, checkName: any) {
    // console.warn("checkName: ", checkName)
    // console.warn("INdex : ", index)
    this.checks.splice(index, 1);
    if (checkName != '') {
      const entireCheckName = "Education " + checkName;
      this.educationSelectedFiles = this.educationSelectedFiles.filter(file => !file.key.includes(checkName));
      this.removeEntireCheck(entireCheckName)
    }
    // this.educationSelectedFiles = [];
  }

  onEducationLevelChange() {
    this.showFileUpload = !!this.Education;
    this.selectedFile = null;
  }

  employmentChecks: any[] = [];
  employmentUploadedDocuments: any[] = [];


  //EMPLOYMENT
  addEmploymentCheck() {
    this.employmentChecks.push({
      Employment: '',
      // selectedEmploymentSubType: ''
    });
    // console.warn("employmentChecks::", this.employmentChecks)
  }

  addDocumentForEmployment(employmentCheck: any) {
    if (!employmentCheck.documents) {
      employmentCheck.documents = []; // Initialize documents array if it's not already initialized
    }
    employmentCheck.documents.push({
      EmploymentDoc: '', // Initialize employment document value
      selectedFiles: [] // Initialize an empty array to store selected files for this document
    });

    // console.log("employmentCheck.documents : ", employmentCheck.documents)
  }

  employmentDocument: string[] = ['Offer Letter', 'Relieving Letter', 'Pay Slip', 'Experience Letter']


  addEmploymentDoc: { EmploymentDoc: string }[] = [];
  EmploymentDoc: string = '';
  addDocumnentForEmployment() {
    // console.warn("add Document Clicked !..")
    this.addEmploymentDoc.push({ EmploymentDoc: '' });
  }

  removeEmploymentCheck(index: number, checkName: any) {
    this.employmentChecks.splice(index, 1);
    if (checkName != '') {
      const entireCheckName = "Employment " + checkName;
      this.employmentSelectedFiles = this.employmentSelectedFiles.filter(file => !file.key.includes(checkName));
      this.removeEntireCheck(entireCheckName)
    }
    // this.employmentSelectedFiles = [];
  }
  removeIDCheck(index: number, checkName: any) {
    this.idChecks.splice(index, 1);
    if (checkName != '') {
      const entireCheckName = "ID " + checkName;
      this.idSelectedFiles = this.idSelectedFiles.filter(file => !file.key.includes(checkName));
      this.removeEntireCheck(entireCheckName)
    }
    // this.idSelectedFiles = [];
  }
  removeCriminalCheck(index: number, checkName: any) {
    this.criminalCheck.splice(index, 1);
    if (checkName != '') {
      const entireCheckName = "Criminal " + checkName;
      this.criminalSelectedFiles = this.criminalSelectedFiles.filter(file => !file.key.includes(checkName));
      this.removeEntireCheck(entireCheckName)

    }
    // this.criminalSelectedFiles = [];
  }
  removeAddressCheck(index: number) {
    this.addressCheck.splice(index, 1);
  }
  removeDatabaseCheck(index: number, checkName: any) {
    this.databaseCheck.splice(index, 1);
    // console.warn("c=database : ", checkName)
    // if(checkName != ''){
    const entireCheckName = "Database ";
    this.databaseSelectedFiles = this.databaseSelectedFiles.filter(file => !file.key.includes(checkName));
    this.removeEntireCheck(entireCheckName)
    // }
    // this.databaseSelectedFiles = [];
  }


  uploadEmploymentFile(event: any, type: string, selectedEmployment: string) {
    const files = event.target.files;
    for (let i = 0; i < files.length; i++) {
      this.employmentUploadedDocuments.push({
        employment: selectedEmployment,
        fileName: files[i].name
      });
    }
  }


  removeEmploymentFile(employment: string, doc: any) {
    const index = this.employmentUploadedDocuments.indexOf(doc);
    if (index > -1) {
      this.employmentUploadedDocuments.splice(index, 1);
    }
  }

  //ID
  idChecks: any[] = [];
  idLevels: string[] = ['Aadhar', 'PAN'];
  id: string = '';

  addIdCheck() {
    // this.idChecks.push({
    //   id: '',
    // });
  }

  //Criminal
  criminalCheck: any[] = [];
  criminalLevels: string[] = ['present', 'permanent']
  criminal: string = '';

  //   filterCriminalLevels(): string[] {
  //     // Extract keys from newProofList
  //     return this.criminalLevels.filter(level => !this.newProofList.some(entry => level.includes(Object.keys(entry)[0])));
  //   }
  //   isDisabled(level: string, criminalCheckIndex: number): boolean {
  //     const criminalCheck = this.criminalCheck[criminalCheckIndex];

  //     console.warn('criminalCheck : ',criminalCheck)
  //     // Check if any key in newProofList is partially matched by the level
  //     return this.newProofList.some(entry => level.includes(Object.keys(  entry)[0]));
  // }


  addCriminalCheck() {
    this.criminalCheck.push({
      criminal: ''
    })
  }


  // isLevelIncluded(level: string): boolean {
  //   return this.newProofList.some(item => item.split(':')[0].includes(level));
  // }

  // Start of  Disable funcationality for Dropdown.
  getSelectedCriminalLevels(): string[] {
    return this.criminalCheck.map(check => check.criminal).filter(criminal => criminal);
  }

  // isLevelIncluded(level: string): boolean {
  //   return this.criminalCheck.includes(level);
  // }

  isLevelCriminalIncluded(level: string): boolean {
    return this.getSelectedCriminalLevels().includes(level);
  }

  getSelectedIdLevels(): string[] {
    return this.idChecks.map(check => check.id).filter(id => id);
  }

  isLevelIdIncluded(level: string): boolean {
    return this.getSelectedIdLevels().includes(level);
  }

  getSelectedEmploymentLevels(): string[] {
    return this.employmentChecks.map(check => check.Employment).filter(Employment => Employment);
  }

  isLevelEmploymentIncluded(level: string): boolean {
    return this.getSelectedEmploymentLevels().includes(level);
  }
  //Employemnt Doc
  getSelectedEmploymentDocLevels(): string[] {
    // console.log("this.employmentDoc : ", this.addEmploymentDoc)
    return this.addEmploymentDoc.map(check => check.EmploymentDoc).filter(Doc => Doc);
  }

  // isLevelEmploymentDocIncluded(level: string): boolean {
  //   return this.getSelectedEmploymentDocLevels().includes(level);
  // }

  isLevelEmploymentDocIncluded(level: string, employmentCheck: any): boolean {
    if (!employmentCheck.documents) {
      return false;
    }
    return employmentCheck.documents.some((doc: any) => doc.EmploymentDoc === level);
  }

  getSelectedEducationLevels(): string[] {
    return this.checks.map(check => check.Education).filter(Education => Education);
  }

  isLevelEducationIncluded(level: string): boolean {
    return this.getSelectedEducationLevels().includes(level);
  }

  isLevelEducationDocIncluded(level: string, EducationCheck: any): boolean {
    if (!EducationCheck.documents) {
      return false;
    }
    return EducationCheck.documents.some((doc: any) => doc.EducationDoc === level);
  }

  // End of  Disable funcationality for Dropdown.


  // getSelectedAddressLevels(): string[] {
  //   return this.addressCheck.map(check => check.addressType).filter(addressType => addressType);
  // }

  // isLevelAddressIncluded(level: string): boolean {
  //   return this.getSelectedAddressLevels().includes(level);
  // }


  //Address

  // addressCheck: any[] = [];
  addressCheck: { addressType: string; address: string; }[] = [];

  addressLevels: string[] = ['present', 'permanent']
  address: string = '';

  // addAddressCheck() {
  //   this.addressCheck.push({
  //     addressType: '',
  //     address: '',
  //   })

  // }

  isAddressTypeUsed(level: string): boolean {
    return this.addressCheck.some(check => check.addressType === level);
  }


  //Database
  databaseCheck: any[] = [];
  databaseLevels: string[] = ['present', 'permanent']
  database: string = '';

  addDatabaseCheck() {
    this.databaseCheck.push({
      database: '',
    })
  }


  uploadFile(event: any, field: string, value: string, checkType: string, file: any) {
    // this.uploadFile(null, "Education", selectedEducation, file.key, file.file);

    console.warn("ENVENT : ", event)
    console.warn("value : ", value)
    console.warn("CheckType : ", checkType)
    let files = null;
    if (event != null) {
      files = event.target.files;
    } else {
      files = [file];
    }
    // console.warn("files : ", files)
    // console.warn("File Length : ", files.length)
    const checkName: string = field;
    console.log("CHECKNAME>>>>>>", checkName)
    let checkNameAndType = checkName + ' ' + value
    // console.warn("checkNameAndType : ", checkNameAndType)
    const fileCount = files.length;
    let multipleFileCount: number = 0;
    let multipleFileCount2: number = 0;

    // console.log("event : ", event)
    // console.log("field : ", field)
    // console.log("value : ", value)
    // console.log("checkType : ", checkType)

    // console.log("Number of files selected:", fileCount);
    this.selectedFiles = [];
    // this.educationSelectedFiles = [];
    // this.employmentSelectedFiles = [];
    const zip = new JSZip();
    for (let i = 0; i < files.length; i++) {

      // if(files.length > 1){

      // console.warn("fileCount : ", files.length)
      const file = files[i];
      const fileType = file.name.split('.').pop()?.toLowerCase();
      console.log("File Extension : ", fileType)
      if (fileType && ['pdf', 'png', 'jpg', 'jpeg', 'zip'].includes(fileType)) {

        if (checkName == 'Education') {
          let selectedFilesArray: { key: string, file: File }[] = [];;
          // console.warn("education selected files")
          // multipleFileCount = this.educationSelectedFiles.push(file);
          if (value.includes('UG')) {
            // console.warn("UG IS TRUE :::::::::::::::::::::")
            //  const educationUGSelectedFiles: { key: string, file: File }[] = [];
            multipleFileCount = this.educationUGSelectedFiles.push({ key: checkNameAndType, file });
            this.educationUGWithDocSelectedFiles.push({ key: checkNameAndType + ' ' + checkType, file });
            this.EducationUGDocTypeList.push({ key:checkType, file })
            selectedFilesArray = this.educationUGSelectedFiles;
          } else if (value.includes('PG')) {
            // console.warn("PG IS TRUE :::::::::::::::::::::")
            multipleFileCount = this.educationPGSelectedFiles.push({ key: checkNameAndType, file });
            this.EducationPGDocTypeList.push({ key:checkType, file })
            selectedFilesArray = this.educationPGSelectedFiles;
          } else if (value.includes('Diploma')) {
            // console.warn("DIPLOMA IS TRUE :::::::::::::::::::::")
            multipleFileCount = this.educationDiplomaSelectedFiles.push({ key: checkNameAndType, file });
            this.EducationDiplomaTHDocTypeList.push({ key:checkType, file })
            selectedFilesArray = this.educationDiplomaSelectedFiles;
          } else if (value.includes('10TH')) {
            // console.warn("10TH IS TRUE :::::::::::::::::::::")
            console.warn("this.education10THSelectedFiles : ", this.education10THSelectedFiles)
            multipleFileCount = this.education10THSelectedFiles.push({ key: checkNameAndType, file });
            this.Education10THDocTypeList.push({ key:checkType, file })
            selectedFilesArray = this.education10THSelectedFiles;
          } else if (value.includes('12TH')) {
            // console.warn("10TH IS TRUE :::::::::::::::::::::")
            console.warn("this.education12THSelectedFiles : ", this.education12THSelectedFiles)
            multipleFileCount = this.education12THSelectedFiles.push({ key: checkNameAndType, file });
            this.Education12THDocTypeList.push({ key:checkType, file })
            selectedFilesArray = this.education12THSelectedFiles;
          }
          multipleFileCount = this.educationSelectedFiles.push({ key: checkNameAndType + ' ' + checkType, file });
          // this.educationUniqueSelectedFiles.push({key: checkNameAndType + ' ' + checkType, file: [file]})
          // multipleFileCount2 = this.tempSelectedFiles.push({key: checkNameAndType,file})
          if (multipleFileCount > 1) {
            // console.warn("this : ", this.educationUGSelectedFiles)
            for (let i = 0; i < selectedFilesArray.length; i++) {
              const file = selectedFilesArray[i];
              const fileType = file.file.name.split('.').pop()?.toLowerCase();
              // console.warn("fileType ::", fileType)
              // Validate the file type
              if (fileType && ['pdf', 'png', 'jpg', 'jpeg', 'zip'].includes(fileType)) {
                // Add the file to the zip
                const educationInput = document.querySelector('input[name="educationFiles"]');
                if (educationInput instanceof HTMLInputElement) {
                  const remainingEducationFiles = new DataTransfer();
                  for (const f of selectedFilesArray) {
                    remainingEducationFiles.items.add(f.file);
                  }
                  // Add the new files to the existing files in the input element
                  const existingFilesCount = educationInput.files ? educationInput.files.length : 0;
                  const newFilesCount = remainingEducationFiles.files.length;
                  const totalCount = existingFilesCount + newFilesCount;

                  // Set the input element's files to the combined list
                  educationInput.files = totalCount > 0 ? remainingEducationFiles.files : null;
                }
                // console.warn("zip file converted : ")
                zip.file(file.file.name, file.file);
              } else {
                // If the file type is not valid, show a warning message and skip this file
                Swal.fire({
                  title: 'Please select .pdf, .png, .jpg, .jpeg, or .zip file types only.',
                  icon: 'warning'
                });
              }
            }
          }
          // console.warn("this.educationSelectedFiles : ", this.educationSelectedFiles)
        }
        else if (checkName == 'Employment') {
          let selectedFilesArray: { key: string, file: File }[] = [];;
          // console.warn("education selected files")
          // multipleFileCount = this.educationSelectedFiles.push(file);
          if (value.includes('EMP1')) {
            // console.warn("EMP1 IS TRUE :::::::::::::::::::::")
            //  const educationUGSelectedFiles: { key: string, file: File }[] = [];
            multipleFileCount = this.employmentEMP1SelectedFiles.push({ key: checkNameAndType, file });
            this.EmploymentEMP1DocTypeList.push({ key:checkType, file })
            // console.log("EmploymentEMP1DocTypeList IN UploadFile : ",this.EmploymentEMP1DocTypeList)
            selectedFilesArray = this.employmentEMP1SelectedFiles;
          } else if (value.includes('EMP2')) {
            // console.warn("EMP2 IS TRUE :::::::::::::::::::::")
            multipleFileCount = this.employmentEMP2SelectedFiles.push({ key: checkNameAndType, file });
            // this.EmploymentEMP2DocTypeList.push({ key:checkNameAndType +' '+ checkType, file })
            this.EmploymentEMP2DocTypeList.push({ key:checkType, file })
            selectedFilesArray = this.employmentEMP2SelectedFiles;
          } else if (value.includes('EMP3')) {
            // console.warn("EMP3 IS TRUE :::::::::::::::::::::")
            multipleFileCount = this.employmentEMP3SelectedFiles.push({ key: checkNameAndType, file });
            this.EmploymentEMP3DocTypeList.push({ key:checkType, file })
            selectedFilesArray = this.employmentEMP3SelectedFiles;
          }
          multipleFileCount = this.employmentSelectedFiles.push({ key: checkNameAndType + ' ' + checkType, file });
          if (multipleFileCount > 1) {
            // const zip = new JSZip();
            for (let i = 0; i < selectedFilesArray.length; i++) {
              const file = selectedFilesArray[i];
              const fileType = file.file.name.split('.').pop()?.toLowerCase();
              // console.warn("fileType ::", fileType)
              // Validate the file type
              if (fileType && ['pdf', 'png', 'jpg', 'jpeg', 'zip'].includes(fileType)) {
                // Add the file to the zip
                const employmentInput = document.querySelector('input[name="employmentFiles"]');
                if (employmentInput instanceof HTMLInputElement) {
                  const remainingEmploymentFiles = new DataTransfer();
                  for (const f of selectedFilesArray) {
                    remainingEmploymentFiles.items.add(f.file);
                  }
                  // Add the new files to the existing files in the input element
                  const existingFilesCount = employmentInput.files ? employmentInput.files.length : 0;
                  const newFilesCount = remainingEmploymentFiles.files.length;
                  const totalCount = existingFilesCount + newFilesCount;

                  // Set the input element's files to the combined list
                  employmentInput.files = totalCount > 0 ? remainingEmploymentFiles.files : null;
                }
                console.warn("zip file converted : ")
                zip.file(file.file.name, file.file);
              } else {
                // If the file type is not valid, show a warning message and skip this file
                Swal.fire({
                  title: 'Please select .pdf, .png, .jpg, .jpeg, or .zip file types only.',
                  icon: 'warning'
                });
              }
            }
          }
        }
        else if (checkName == 'Criminal') {
          // multipleFileCount = this.criminalSelectedFiles.push(file);
          multipleFileCount = this.criminalSelectedFiles.push({ key: checkNameAndType, file });
          let selectedFilesArray: { key: string, file: File }[] = [];
          if (value.includes('present')) {
            // console.warn("present IS TRUE :::::::::::::::::::::")
            //  const educationUGSelectedFiles: { key: string, file: File }[] = [];
            multipleFileCount = this.criminalPresentSelectedFiles.push({ key: checkNameAndType, file });
            // console.log("multipleCount for Criminal Present : ", multipleFileCount)
            selectedFilesArray = this.criminalPresentSelectedFiles;
          } else if (value.includes('permanent')) {
            // console.warn("permanent IS TRUE :::::::::::::::::::::")
            multipleFileCount = this.criminalPermanentSelectedFiles.push({ key: checkNameAndType, file });
            selectedFilesArray = this.criminalPermanentSelectedFiles;
          }
          if (multipleFileCount > 1) {
            for (let i = 0; i < selectedFilesArray.length; i++) {
              const file = selectedFilesArray[i];
              const fileType = file.file.name.split('.').pop()?.toLowerCase();
              // console.warn("fileType ::", fileType)
              // Validate the file type
              if (fileType && ['pdf', 'png', 'jpg', 'jpeg', 'zip'].includes(fileType)) {
                // Add the file to the zip
                const criminalInput = document.querySelector('input[name="criminalFiles"]');
                console.log("criminalInput : "+criminalInput)
                if (criminalInput instanceof HTMLInputElement) {
                  const remainingCriminalFiles = new DataTransfer();
                  for (const f of selectedFilesArray) {
                    remainingCriminalFiles.items.add(f.file);
                  }
                  // Add the new files to the existing files in the input element
                  const existingFilesCount = criminalInput.files ? criminalInput.files.length : 0;
                  const newFilesCount = remainingCriminalFiles.files.length;
                  const totalCount = existingFilesCount + newFilesCount;

                  // Set the input element's files to the combined list
                  criminalInput.files = totalCount > 0 ? remainingCriminalFiles.files : null;
                }
                // console.warn("zip file converted : ")
                zip.file(file.file.name, file.file);
              } else {
                // If the file type is not valid, show a warning message and skip this file
                Swal.fire({
                  title: 'Please select .pdf, .png, .jpg, .jpeg, or .zip file types only.',
                  icon: 'warning'
                });
              }
            }
          }
        }
        else if (checkName == 'ID') {
          // multipleFileCount = this.idSelectedFiles.push(file);
          multipleFileCount = this.idSelectedFiles.push({ key: checkNameAndType, file });
          let selectedFilesArray: { key: string, file: File }[] = [];
          if (value.includes('Aadhar')) {
            // console.warn("Aadhar IS TRUE :::::::::::::::::::::")
            //  const educationUGSelectedFiles: { key: string, file: File }[] = [];
            multipleFileCount = this.idAadharSelectedFiles.push({ key: checkNameAndType, file });
            selectedFilesArray = this.idAadharSelectedFiles;
          } else if (value.includes('PAN')) {
            // console.warn("PAN IS TRUE :::::::::::::::::::::")
            multipleFileCount = this.idPanSelectedFiles.push({ key: checkNameAndType, file });
            selectedFilesArray = this.idPanSelectedFiles;
          }
          if (multipleFileCount > 1) {
            for (let i = 0; i < selectedFilesArray.length; i++) {
              const file = selectedFilesArray[i];
              const fileType = file.file.name.split('.').pop()?.toLowerCase();
              // console.warn("fileType ::", fileType)
              // Validate the file type
              if (fileType && ['pdf', 'png', 'jpg', 'jpeg', 'zip'].includes(fileType)) {
                // Add the file to the zip
                const idInput = document.querySelector('input[name="idFiles"]');
                if (idInput instanceof HTMLInputElement) {
                  const remainingIdFiles = new DataTransfer();
                  for (const f of selectedFilesArray) {
                    remainingIdFiles.items.add(f.file);
                  }
                  // Add the new files to the existing files in the input element
                  const existingFilesCount = idInput.files ? idInput.files.length : 0;
                  const newFilesCount = remainingIdFiles.files.length;
                  const totalCount = existingFilesCount + newFilesCount;

                  // Set the input element's files to the combined list
                  idInput.files = totalCount > 0 ? remainingIdFiles.files : null;
                }
                console.warn("zip file converted : ")
                zip.file(file.file.name, file.file);
              } else {
                // If the file type is not valid, show a warning message and skip this file
                Swal.fire({
                  title: 'Please select .pdf, .png, .jpg, .jpeg, or .zip file types only.',
                  icon: 'warning'
                });
              }
            }
          }
        }
        else if (checkName == 'Database') {
          // multipleFileCount = this.databaseSelectedFiles.push(file);
          // console.log("Global Database ::")
          let selectedFilesArray: { key: string, file: File }[] = [];
          multipleFileCount = this.databaseSelectedFiles.push({ key: checkNameAndType, file });
          selectedFilesArray = this.databaseSelectedFiles
          if (multipleFileCount > 1) {
            for (let i = 0; i < selectedFilesArray.length; i++) {
              // console.log("Global Database :: 222")
              const file = selectedFilesArray[i];
              const fileType = file.file.name.split('.').pop()?.toLowerCase();
              // console.warn("fileType ::", fileType)
              // Validate the file type
              if (fileType && ['pdf', 'png', 'jpg', 'jpeg', 'zip'].includes(fileType)) {
                // Add the file to the zip
                const databaseInput = document.querySelector('input[name="databaseFiles"]');
                if (databaseInput instanceof HTMLInputElement) {
                  const remainingDatabaseFiles = new DataTransfer();
                  for (const f of selectedFilesArray) {
                    remainingDatabaseFiles.items.add(f.file);
                  }
                  // Add the new files to the existing files in the input element
                  const existingFilesCount = databaseInput.files ? databaseInput.files.length : 0;
                  const newFilesCount = remainingDatabaseFiles.files.length;
                  const totalCount = existingFilesCount + newFilesCount;

                  // Set the input element's files to the combined list
                  databaseInput.files = totalCount > 0 ? remainingDatabaseFiles.files : null;
                }
                console.warn("zip file converted : ")
                zip.file(file.file.name, file.file);
              } else {
                // If the file type is not valid, show a warning message and skip this file
                Swal.fire({
                  title: 'Please select .pdf, .png, .jpg, .jpeg, or .zip file types only.',
                  icon: 'warning'
                });
              }
            }
          }
        }
        else if (checkName == 'Address') {
          // multipleFileCount = this.idSelectedFiles.push(file);
          multipleFileCount = this.addressSelectedFiles.push({ key: checkNameAndType, file });
          let selectedFilesArray: { key: string, file: File }[] = [];
          if (value.includes('present')) {
            // console.warn("Aadhar IS TRUE :::::::::::::::::::::")
            //  const educationUGSelectedFiles: { key: string, file: File }[] = [];
            multipleFileCount = this.addressPresentSelectedFiles.push({ key: checkNameAndType, file });
            selectedFilesArray = this.addressPresentSelectedFiles;
          } else if (value.includes('permanent')) {
            // console.warn("PAN IS TRUE :::::::::::::::::::::")
            multipleFileCount = this.addressPermanentSelectedFiles.push({ key: checkNameAndType, file });
            selectedFilesArray = this.addressPermanentSelectedFiles;
          }
          if (multipleFileCount > 1) {
            for (let i = 0; i < selectedFilesArray.length; i++) {
              const file = selectedFilesArray[i];
              const fileType = file.file.name.split('.').pop()?.toLowerCase();
              // console.warn("fileType ::", fileType)
              // Validate the file type
              if (fileType && ['pdf', 'png', 'jpg', 'jpeg', 'zip'].includes(fileType)) {
                // Add the file to the zip
                const addressInput = document.querySelector('input[name="addressFiles"]');
                if (addressInput instanceof HTMLInputElement) {
                  const remainingIdFiles = new DataTransfer();
                  for (const f of selectedFilesArray) {
                    remainingIdFiles.items.add(f.file);
                  }
                  // Add the new files to the existing files in the input element
                  const existingFilesCount = addressInput.files ? addressInput.files.length : 0;
                  const newFilesCount = remainingIdFiles.files.length;
                  const totalCount = existingFilesCount + newFilesCount;

                  // Set the input element's files to the combined list
                  addressInput.files = totalCount > 0 ? remainingIdFiles.files : null;
                }
                console.warn("zip file converted : ")
                zip.file(file.file.name, file.file);
              } else {
                // If the file type is not valid, show a warning message and skip this file
                Swal.fire({
                  title: 'Please select .pdf, .png, .jpg, .jpeg, or .zip file types only.',
                  icon: 'warning'
                });
              }
            }
          }
        }

        // this.selectedFiles.push(file);
      }
      else {
        // If the file type is not valid, show a warning message and skip this file
        Swal.fire({
          title: 'Please select .pdf, .png, .jpg, .jpeg, or .zip file types only.',
          icon: 'warning'
        });
      }
      // }
    }

    if (multipleFileCount > 1) {
      // const zip = new JSZip();

      // Generate the zip file

      zip.generateAsync({ type: "blob" }).then((blob: any) => {
        // Create a File object for the zip blob
        const zipFile = new File([blob], "files.zip", { type: "application/zip" });

        // Assign the zip file to the appropriate form control
        this.candidateForm.get(`${field}FileInput`)?.setValue(zipFile);

        // Optionally, you can convert the zip file to base64 if needed
        const reader = new FileReader();
        reader.onload = (e: any) => {
          const base64 = e.target.result;
          // console.log("Base64 string of the zip file:", base64);

          const byteArray = new Uint8Array(e.target.result);

          // Convert Uint8Array to regular array
          const byteArrayAsArray = Array.from(byteArray);

          // Convert byte array to Base64 string
          this.base64 = this.arrayBufferToBase64(byteArray);

          const keyToRemove = `${checkName + ' ' + value}`;

          // Remove items with the key "Education UG"
          // this.newProofList = this.newProofList.filter(proof => {
          //   const key = proof.split(':')[0];
          //   console.warn("remove is true form Education UG:")
          //   return key !== keyToRemove;
          // });

          this.newProofList = this.newProofList.filter(proof => {
            const key = proof.split(':')[0];
            // console.warn("Key :: ", key)
            if (key === keyToRemove) {
              // console.warn("remove is true for Education UG:", proof);
              return false; // This will filter out the item
            }
            return true; // This will keep the item in the list
          });

          // Optionally, if you want to store each proof separately as a string
          const newProof = `${checkName + ' ' + value}:${this.base64}`;
          let combinedProofs = ''; // Initialize a variable to store combined proofs

          // Iterate over proofs and combine them into a single string
          for (const proof of newProof) {
            combinedProofs += `${proof}`; // Concatenate base64-encoded proof strings
          }

          // console.warn("combinedProofs : ", combinedProofs)
          // console.log("Combined proofs:", combinedProofs);
          this.newProofList.push(combinedProofs); // Add newProof string to the list

          // If all files have been processed, assign newProofList to this.newProof
          // console.warn("this.newProofList.length xyz: ", this.newProofList.length)
          // console.warn("files.length xyz: ", files.length)
          if (this.newProofList.length === files.length) {
            // console.warn("new file length match")
            this.newProofList = this.newProofList;
          }

          // console.warn("this.newProofList", this.newProofList)


        };
        reader.readAsArrayBuffer(zipFile);
      });
    }

    else {
      // console.warn("File is not more than 1")
      for (let i = 0; i < files.length; i++) {
        // console.warn("File length is 1::::")
        const file = files[i];
        const fileType = file.name.split('.').pop()?.toLowerCase();
        if (fileType && ['pdf', 'png', 'jpg', 'jpeg', 'zip'].includes(fileType)) {

          // newly addeD

          const reader = new FileReader();

          // When the file is loaded, convert it to a byte array
          reader.onload = (e: any) => {
            // console.log("File contents:", e.target.result);
            const byteArray = new Uint8Array(e.target.result);

            // Convert Uint8Array to regular array
            const byteArrayAsArray = Array.from(byteArray);

            // Convert byte array to Base64 string
            this.base64 = this.arrayBufferToBase64(byteArray);

            const keyToRemove = `${checkName + ' ' + value}`;

            this.newProofList = this.newProofList.filter(proof => {
              const key = proof.split(':')[0];
              return key !== keyToRemove;
            });

            // Optionally, if you want to store each proof separately as a string
            // const newProof = `${checkName}:${this.base64}`;
            const newProof = `${checkName + ' ' + value}:${this.base64}`;
            this.newProofList.push(newProof); // Add newProof string to the list

            // If all files have been processed, assign newProofList to this.newProof
            if (this.newProofList.length === files.length) {
              this.newProofList = this.newProofList;
            }

            // console.warn("this.newProofList", this.newProofList)


            // Print the Base64 string
            // console.log("Base64 string:", this.base64);
          };

          reader.readAsArrayBuffer(file);


          // Validate the file type
          if (fileType && ['pdf', 'png', 'jpg', 'jpeg', 'zip'].includes(fileType)) {
            // If the file type is valid, assign the file to the appropriate form control
            this.candidateForm.get(`${field}FileInput`)?.setValue(file);

            this.proofDocumentNew.set(checkName, this.base64);
          } else {
            // If the file type is not valid, skip this file and show a warning message
            Swal.fire({
              title: 'Please select .pdf, .png, .jpg, .jpeg, or .zip file types only.',
              icon: 'warning'
            });
          }
        }
        else {
          // If the file type is not valid, skip this file and show a warning message
          Swal.fire({
            title: 'Please select .pdf, .png, .jpg, .jpeg, or .zip file types only.',
            icon: 'warning'
          });
        }
      }
    }

  }


  removeFile(fileType: string, checkType: any, file: File, checkIndex: number, docIndex: number, fileIndex: any, removeCheckbyDoc: number,removeLastFileEntire:any) {
          //  this.removeFile(checkname, type, file, 0, 0, removeIndex, 1,null)

    let selectedFilesArray: { key: string, file: File }[] = [];
    let selectedEMPFilesArray: { key: string, file: File }[] = [];
    let selectedCriminalFilesArray: { key: string, file: File }[] = [];
    let selectedIdFilesArray: { key: string, file: File }[] = [];
    let selectedAddressFilesArray: { key: string, file: File }[] = [];
    // console.warn("checkIndex : ", checkIndex)
    // console.warn("docIndex : ", docIndex)
    // console.warn("fileIndex : ", fileIndex)
    // console.warn("removeLastFileEntire:number : ",removeLastFileEntire)
    // console.log("checkType : ",checkType)
    // 'education', check.Education, fileObj.file,i, j, k,0,i
    if (fileType === 'education') {
      const checkName = "Education";
      const checkNameAndCheckType = checkName + ' ' + checkType
      console.warn("checkNameAndCheckType : ", checkNameAndCheckType)
      // const index = this.educationSelectedFiles.indexOf({key:checkNameAndCheckType,file});
      // const index = this.educationSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
      let index = null;
      // console.warn("fileName : ", file.name)

      if (checkType.includes('UG')) {
        // console.warn("UG IS TRUE :::::::::::::::::::::")
        index = this.educationUGSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        //  index = this.educationUGSelectedFiles.findIndex(item => item.file.name !== file.name);
        // console.warn("filename from html : ", file.name)
        this.educationUGSelectedFiles.forEach(item => {
          // console.log("Filename: ", item.file.name);
        });
        // index = this.educationUGSelectedFiles.filter(item => item.file.name !== file.name);
        // console.warn("INDEX: ", index)
        // selectedFilesArray = this.educationUGSelectedFiles;
        // selectedFilesArray = this.educationUGSelectedFiles.filter(item => item.file.name !== file.name);
        selectedFilesArray = this.educationUGSelectedFiles.filter(item => item.file.name !== file.name);
        this.educationUGSelectedFiles = selectedFilesArray;

      } else if (checkType.includes('PG')) {
        // console.warn("PG IS TRUE :::::::::::::::::::::")
        index = this.educationPGSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedFilesArray = this.educationPGSelectedFiles.filter(item => item.file.name !== file.name);
        // selectedFilesArray = this.educationPGSelectedFiles;
        this.educationPGSelectedFiles = selectedFilesArray;
      } else if (checkType.includes('Diploma')) {
        // console.warn("DIPLOMA IS TRUE :::::::::::::::::::::")
        index = this.educationDiplomaSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedFilesArray = this.educationDiplomaSelectedFiles.filter(item => item.file.name !== file.name);
        // selectedFilesArray = this.educationDiplomaSelectedFiles;
        this.educationDiplomaSelectedFiles = selectedFilesArray;
      } else if (checkType.includes('10TH')) {
        // console.warn("10TH IS TRUE :::::::::::::::::::::")
        index = this.education10THSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedFilesArray = this.education10THSelectedFiles.filter(item => item.file.name !== file.name);
        // selectedFilesArray = this.education10THSelectedFiles;
        this.education10THSelectedFiles = selectedFilesArray;

      } else if (checkType.includes('12TH')) {
        // console.warn("10TH IS TRUE :::::::::::::::::::::")
        index = this.education12THSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedFilesArray = this.education12THSelectedFiles.filter(item => item.file.name !== file.name);
        // selectedFilesArray = this.education12THSelectedFiles;
        this.education12THSelectedFiles = selectedFilesArray;
      }

      if (index != null) {
        // console.warn("fileIndex : ", fileIndex)
        // console.warn("index : ", index)
        // console.warn("docIndex : ", docIndex)
        if (removeCheckbyDoc != 1) {
          // console.warn("Remove File Education10THDocTypeList : ",this.Education10THDocTypeList)
          // console.warn("Remove File Education12THDocTypeList : ",this.Education12THDocTypeList)
          // console.warn("Remove File EducationUGDocTypeList : ",this.EducationUGDocTypeList)
          // console.warn("Remove File EducationPGDocTypeList : ",this.EducationPGDocTypeList)
          // console.warn("Remove File EducationDiplomaTHDocTypeList : ",this.EducationDiplomaTHDocTypeList)
          // console.warn("Remove File FILEINDEX : ",fileIndex)


          if(checkType.includes('10TH') && this.Education10THDocTypeList != null){
            // console.log('checkNameAndCheckType:', checkNameAndCheckType);
            // console.log('file:', file);
            // console.log("education10THSelectedFiles : ",this.education10THSelectedFiles)
            // console.log("Education10THDocTypeList BEFORE : ",this.Education10THDocTypeList)

            const  index2 = this.Education10THDocTypeList.findIndex(item => item.file === file);
            // console.log("INDEX : ",index2)
            this.Education10THDocTypeList.splice(index2,1)
            // console.warn("Remove File Education10THDocTypeList After : ",this.Education10THDocTypeList)
          }
          if( checkType.includes('12TH') && this.Education12THDocTypeList != null){
            // console.log("education12THSelectedFiles : ",this.education12THSelectedFiles)
            const  index2 = this.Education12THDocTypeList.findIndex(item => item.file === file);
            // console.log("INDEX : ",index2)
            this.Education12THDocTypeList.splice(index2,1)
            // console.warn("Remove File Education12THDocTypeList After: ",this.Education12THDocTypeList)
          }
          if(checkType.includes('UG') && this.EducationUGDocTypeList != null){
            // console.log("PG ================== ")
            // index = this.educationUGSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file != file);
            const  index2 = this.EducationUGDocTypeList.findIndex(item => item.file === file);
            // console.log("INDEX : ",index2)
            this.EducationUGDocTypeList.splice(index2,1) 
          }
          if(checkType.includes('PG') && this.EducationPGDocTypeList != null){
            // index = this.educationPGSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file != file);
            const  index2 = this.EducationPGDocTypeList.findIndex(item => item.file === file);
            // console.log("INDEX : ",index2)
            this.EducationPGDocTypeList.splice(index2,1) 
          }
          if(checkType.includes('Diploma') && this.EducationDiplomaTHDocTypeList != null){
            // index = this.educationDiplomaSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file != file);
            const  index2 = this.EducationDiplomaTHDocTypeList.findIndex(item => item.file === file);
            // console.log("INDEX : ",index2)
            this.EducationDiplomaTHDocTypeList.splice(index2,1) 
          }


          this.educationSelectedFiles.splice(fileIndex, 1);
        }
        // selectedFilesArray.splice(index, 1);

        // console.warn(" this.educationSelectedFiles : ", this.educationSelectedFiles)
        // console.warn(" selectedFilesArray : ", selectedFilesArray)


        const educationInput = document.querySelector('input[name="educationFiles"]');
        if (educationInput instanceof HTMLInputElement) {
          const remainingEducationFiles = new DataTransfer();
          for (const f of this.educationSelectedFiles) {
            remainingEducationFiles.items.add(f.file);
          }
          educationInput.files = remainingEducationFiles.files;
        }

        // this.removeAndUpdateDocument(this.educationSelectedFiles(), checkName)
        // const matchingFile = this.educationSelectedFiles.find(item => item.key === checkNameAndCheckType);
        // const matchingFile = this.educationSelectedFiles.find(item => {
        //   console.log(item.key); // This will print the key property of each item
        //   return item.key === checkNameAndCheckType;
        // });
        // if (matchingFile) {
        //   this.removeAndUpdateDocument([matchingFile.file], checkName);
        // }
        // console.log("Number of files in selectedFilesArray:", selectedFilesArray.map(item => item.file).length);
        this.removeAndUpdateDocument(selectedFilesArray.map(item => item.file), checkNameAndCheckType);
      }
    } else if (fileType === 'employment') {
      const checkName = "Employment";
      const checkNameAndCheckType = checkName + ' ' + checkType.trim()
      // console.warn("checkNameAndCheckType : ", checkNameAndCheckType)
      let index = null;
      if (checkType.includes('EMP1')) {
        // console.warn("EMP1 IS TRUE :::::::::::::::::::::")
        // console.warn("employmentEMP1SelectedFiles : ",this.employmentEMP1SelectedFiles)
        index = this.employmentEMP1SelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedEMPFilesArray = this.employmentEMP1SelectedFiles.filter(item => item.file.name !== file.name);
        // selectedEMPFilesArray = this.employmentEMP1SelectedFiles;
        this.employmentEMP1SelectedFiles = selectedEMPFilesArray;
        // const employmentInput = document.querySelector(`input[name="employmentFiles${removeLastFileEntire}"]`);
        // console.log("employmentInput:: ",employmentInput)
        // if (employmentInput instanceof HTMLInputElement) {
        //   const remainingEmploymentFiles = new DataTransfer();
        //   for (const f of this.employmentEMP1SelectedFiles) {
        //     remainingEmploymentFiles.items.add(f.file);
        //   }
        //   employmentInput.files = remainingEmploymentFiles.files;
        // }
      } else if (checkType.includes('EMP2')) {
        // console.warn("EMP2 IS TRUE :::::::::::::::::::::")
        index = this.employmentEMP2SelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedEMPFilesArray = this.employmentEMP2SelectedFiles.filter(item => item.file.name !== file.name);
        // selectedEMPFilesArray = this.employmentEMP2SelectedFiles;
        this.employmentEMP2SelectedFiles = selectedEMPFilesArray;
        // const employmentInput = document.querySelector(`input[name="employmentFiles${removeLastFileEntire}"]`);
        // if (employmentInput instanceof HTMLInputElement) {
        //   const remainingEmploymentFiles = new DataTransfer();
        //   for (const f of this.employmentEMP2SelectedFiles) {
        //     remainingEmploymentFiles.items.add(f.file);
        //   }
        //   employmentInput.files = remainingEmploymentFiles.files;
        // }
      } else if (checkType.includes('EMP3')) {
        // console.warn("EMP3 IS TRUE :::::::::::::::::::::")
        index = this.employmentEMP3SelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedEMPFilesArray = this.employmentEMP3SelectedFiles.filter(item => item.file.name !== file.name);
        // selectedEMPFilesArray = this.employmentEMP3SelectedFiles;
        this.employmentEMP3SelectedFiles = selectedEMPFilesArray;
        // const employmentInput = document.querySelector(`input[name="employmentFiles${removeLastFileEntire}"]`);
        // if (employmentInput instanceof HTMLInputElement) {
        //   const remainingEmploymentFiles = new DataTransfer();
        //   for (const f of this.employmentEMP3SelectedFiles) {
        //     remainingEmploymentFiles.items.add(f.file);
        //   }
        //   employmentInput.files = remainingEmploymentFiles.files;
        // }
      }
      if (index != null) {
        // console.warn("docIndex : ", docIndex)
        if (removeCheckbyDoc != 1) {
          // console.warn("Remove File EmploymentEMP1DocTypeList : ",this.EmploymentEMP1DocTypeList)
          // console.warn("Remove File EmploymentEMP2DocTypeList : ",this.EmploymentEMP2DocTypeList)
          // console.warn("Remove File EmploymentEMP3DocTypeList : ",this.EmploymentEMP3DocTypeList)

          if(checkType.includes('EMP1') && this.EmploymentEMP1DocTypeList != null){
            const  index2 = this.EmploymentEMP1DocTypeList.findIndex(item => item.file === file);
            this.EmploymentEMP1DocTypeList.splice(index2,1)
          }
          if(checkType.includes('EMP2') && this.EmploymentEMP2DocTypeList != null){
            const  index2 = this.EmploymentEMP2DocTypeList.findIndex(item => item.file === file);
            this.EmploymentEMP2DocTypeList.splice(index2,1)
          }
          if(checkType.includes('EMP3') && this.EmploymentEMP3DocTypeList != null){
            const  index2 = this.EmploymentEMP3DocTypeList.findIndex(item => item.file === file);
            this.EmploymentEMP3DocTypeList.splice(index2,1) 
          }

          this.employmentSelectedFiles.splice(fileIndex, 1);
        }
        // selectedEMPFilesArray.splice(index, 1);
        // const remainingFiles = new DataTransfer();
        // for (const f of this.employmentSelectedFiles) {
        //   remainingFiles.items.add(f);
        // }
        // // Get the input element
        // const input: HTMLInputElement | null = document.querySelector('input[type="file"]');
        // // Update the input's files property with the new FileList
        // if (input !== null) {
        //   input.files = remainingFiles.files;
        // }
        const employmentInput = document.querySelector('input[name="employmentFiles"]');
        // const employmentInput = document.querySelector(`input[name="employmentFiles${removeLastFileEntire}"]`);
        if (employmentInput instanceof HTMLInputElement) {
          const remainingEmploymentFiles = new DataTransfer();
          for (const f of this.employmentSelectedFiles) {
            remainingEmploymentFiles.items.add(f.file);
          }
          employmentInput.files = remainingEmploymentFiles.files;
        }
        // this.removeAndUpdateDocument(this.employmentSelectedFiles, checkName)
        // const matchingFile = this.employmentSelectedFiles.find(item => {
        //   console.log(item.key); // This will print the key property of each item
        //   return item.key === checkNameAndCheckType;
        // });
        // if (matchingFile) {
        //   this.removeAndUpdateDocument([matchingFile.file], checkName);
        // }
        this.removeAndUpdateDocument(selectedEMPFilesArray.map(item => item.file), checkNameAndCheckType);
      }
    } else if (fileType === 'criminal') {
      const checkName = "Criminal";
      const checkNameAndCheckType = checkName + ' ' + checkType
      let index = null;
      // console.warn("checkType : ", checkType)
      // console.warn("fileIndex : ", fileIndex)
      if (checkType == 'present') {
        index = this.criminalPresentSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedCriminalFilesArray = this.criminalPresentSelectedFiles.filter(item => item.file.name !== file.name);
        this.criminalPresentSelectedFiles = selectedCriminalFilesArray;
        const employmentInput = document.querySelector(`input[name="criminalFiles${removeLastFileEntire}"]`);
        // const employmentInput = document.getElementsByName(`criminalFiles${removeLastFileEntire}`);
        console.log("CRIMINAL COUNT :",employmentInput)
        if (employmentInput instanceof HTMLInputElement) {
          // employmentInput.setAttribute('name', `criminalFiles${removeLastFileEntire}`);
          const remainingEmploymentFiles = new DataTransfer();
          // console.log("remainingEmploymentFiles : ",remainingEmploymentFiles)
          // console.log("this.criminalSelectedFiles : ",this.criminalSelectedFiles)
          // console.log("this.criminalSelectedFiles.length : ",this.criminalSelectedFiles.length)
          for (const f of this.criminalPresentSelectedFiles) {
            // console.log("inside::")
            remainingEmploymentFiles.items.add(f.file);
          }
          employmentInput.files = remainingEmploymentFiles.files;
          // console.log("outside:::",employmentInput.files)


        }
      }
      else if (checkType == 'permanent') {
        index = this.criminalPermanentSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedCriminalFilesArray = this.criminalPermanentSelectedFiles.filter(item => item.file.name !== file.name);
        this.criminalPermanentSelectedFiles = selectedCriminalFilesArray;
        const employmentInput = document.querySelector(`input[name="criminalFiles${removeLastFileEntire}"]`);
        // const employmentInput = document.getElementsByName(`criminalFiles${removeLastFileEntire}`);
        // console.log("CRIMINAL COUNT :",employmentInput)
        if (employmentInput instanceof HTMLInputElement) {
          // employmentInput.setAttribute('name', `criminalFiles${removeLastFileEntire}`);
          const remainingEmploymentFiles = new DataTransfer();
          // console.log("remainingEmploymentFiles : ",remainingEmploymentFiles)
          // console.log("this.criminalSelectedFiles : ",this.criminalSelectedFiles)
          // console.log("this.criminalSelectedFiles.length : ",this.criminalSelectedFiles.length)
          for (const f of this.criminalPermanentSelectedFiles) {
            // console.log("inside::")
            remainingEmploymentFiles.items.add(f.file);
          }
          employmentInput.files = remainingEmploymentFiles.files;
          // console.log("outside:::",employmentInput.files)


        }
      }
      // const index = this.criminalSelectedFiles.indexOf(file);
      // const index = this.criminalSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
      if (index != null) {
        // console.warn("FileIndex : ", fileIndex)
        // console.warn("index : ", index)
        this.criminalSelectedFiles.splice(fileIndex, 1);
        // const employmentInput = document.querySelector('input[name="criminalFiles${removeLastFileEntire}"]');
        // const employmentInput = document.querySelector(`input[name="criminalFiles${removeLastFileEntire}"]`);
        // // const employmentInput = document.getElementsByName(`criminalFiles${removeLastFileEntire}`);
        // console.log("CRIMINAL COUNT :",employmentInput)
        // if (employmentInput instanceof HTMLInputElement) {
        //   // employmentInput.setAttribute('name', `criminalFiles${removeLastFileEntire}`);
        //   const remainingEmploymentFiles = new DataTransfer();
        //   console.log("remainingEmploymentFiles : ",remainingEmploymentFiles)
        //   console.log("this.criminalSelectedFiles : ",this.criminalSelectedFiles)
        //   console.log("this.criminalSelectedFiles.length : ",this.criminalSelectedFiles.length)
        //   for (const f of this.criminalSelectedFiles) {
        //     console.log("inside::")
        //     remainingEmploymentFiles.items.add(f.file);
        //   }
        //   employmentInput.files = remainingEmploymentFiles.files;
        //   console.log("outside:::",employmentInput.files)


        // }
        // this.removeAndUpdateDocument(this.criminalSelectedFiles, checkName)
        // this.removeAndUpdateDocument(this.selectedCriminalFilesArray.map(item => item.file), checkNameAndCheckType);
        this.removeAndUpdateDocument(selectedCriminalFilesArray.map(item => item.file), checkNameAndCheckType);
        // const matchingFile = this.criminalSelectedFiles.find(item => {
        //   console.log(item.key); // This will print the key property of each item
        //   return item.key === checkNameAndCheckType;
        // });
        // if (matchingFile) {
        //   this.removeAndUpdateDocument([matchingFile.file], checkName);
        // }
      }
    }
    else if (fileType === 'ID') {
      const checkName = "ID";
      const checkNameAndCheckType = checkName + ' ' + checkType
      let index = null;
      // console.warn("checkType : ", checkType)
      // console.warn("checkNameAndCheckType : ", checkNameAndCheckType)
      // const index = this.idSelectedFiles.indexOf(file);
      if (checkType == 'Aadhar') {
        index = this.idAadharSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        // console.warn("Aadhar index : ", index)
        selectedIdFilesArray = this.idAadharSelectedFiles.filter(item => item.file.name !== file.name);
        this.idAadharSelectedFiles = selectedIdFilesArray;
        const employmentInput = document.querySelector(`input[name="idFiles${removeLastFileEntire}"]`);
        // console.warn("inside Index ::", index)
        if (employmentInput instanceof HTMLInputElement) {
          const remainingEmploymentFiles = new DataTransfer();
          for (const f of this.idAadharSelectedFiles) {
            remainingEmploymentFiles.items.add(f.file);
          }
          employmentInput.files = remainingEmploymentFiles.files;
        }
      }
      else if (checkType == 'PAN') {
        index = this.idPanSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedIdFilesArray = this.idPanSelectedFiles.filter(item => item.file.name !== file.name);
        this.idPanSelectedFiles = selectedIdFilesArray;
        const employmentInput = document.querySelector(`input[name="idFiles${removeLastFileEntire}"]`);
        // console.warn("inside Index ::", index)
        if (employmentInput instanceof HTMLInputElement) {
          const remainingEmploymentFiles = new DataTransfer();
          for (const f of this.idPanSelectedFiles) {
            remainingEmploymentFiles.items.add(f.file);
          }
          employmentInput.files = remainingEmploymentFiles.files;
        }
      }
      // const index = this.idSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
      // console.warn("outside Index ::", index)
      if (index != null) {
        this.idSelectedFiles.splice(fileIndex, 1);
        // console.warn("this.idSelectedFiles : ", this.idSelectedFiles.length)
        // const employmentInput = document.querySelector('input[name="idFiles"]');
        // const employmentInput = document.querySelector(`input[name="idFiles${removeLastFileEntire}"]`);
        // // console.warn("inside Index ::", index)
        // if (employmentInput instanceof HTMLInputElement) {
        //   const remainingEmploymentFiles = new DataTransfer();
        //   for (const f of this.idSelectedFiles) {
        //     remainingEmploymentFiles.items.add(f.file);
        //   }
        //   employmentInput.files = remainingEmploymentFiles.files;
        // }

        // console.log("selectedIdFilesArray : ", selectedIdFilesArray)
        // this.removeAndUpdateDocument(this.idSelectedFiles, checkName)
        // this.removeAndUpdateDocument(this.idSelectedFiles.map(item => item.file), checkNameAndCheckType);
        this.removeAndUpdateDocument(selectedIdFilesArray.map(item => item.file), checkNameAndCheckType);
        // const matchingFile = this.idSelectedFiles.find(item => {
        //   console.log("id items:::::: ",item.key); // This will print the key property of each item
        //   return item.key === checkNameAndCheckType;
        // });
        // if (matchingFile) {
        //   console.warn("matchingFile true")
        //   this.removeAndUpdateDocument([matchingFile.file], checkName);
        // }
      }
    }
    else if (fileType === 'database') {

      const checkName = "Database";
      const checkNameAndCheckType = checkName + ' ' + checkType

      // const index = this.databaseSelectedFiles.indexOf(file);
      const index = this.databaseSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
      if (index != null) {
        this.databaseSelectedFiles.splice(fileIndex, 1);
        // const employmentInput = document.querySelector('input[name="databaseFiles"]');
        const employmentInput = document.querySelector(`input[name="databaseFiles${removeLastFileEntire}"]`);
        if (employmentInput instanceof HTMLInputElement) {
          const remainingEmploymentFiles = new DataTransfer();
          for (const f of this.databaseSelectedFiles) {
            remainingEmploymentFiles.items.add(f.file);
          }
          employmentInput.files = remainingEmploymentFiles.files;
        }
        // this.removeAndUpdateDocument(this.databaseSelectedFiles, checkName)
        this.removeAndUpdateDocument(this.databaseSelectedFiles.map(item => item.file), checkNameAndCheckType);
        // const matchingFile = this.databaseSelectedFiles.find(item => {
        //   console.log(item.key); // This will print the key property of each item
        //   return item.key === checkNameAndCheckType;
        // });
        // if (matchingFile) {
        //   this.removeAndUpdateDocument([matchingFile.file], checkName);
        // }
      }
    }
    else if (fileType === 'address') {
      const checkName = "Address";
      const checkNameAndCheckType = checkName + ' ' + checkType
      let index = null;
      // console.warn("checkType : ", checkType)
      // console.warn("checkNameAndCheckType : ", checkNameAndCheckType)
      // const index = this.idSelectedFiles.indexOf(file);
      if (checkType == 'present') {
        console.log("this.addressPresentSelectedFiles : ",this.addressPresentSelectedFiles)
        index = this.addressPresentSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        console.warn("Address index : ", index)
        selectedAddressFilesArray = this.addressPresentSelectedFiles.filter(item => item.file.name !== file.name);
        this.addressPresentSelectedFiles = selectedAddressFilesArray;
        const employmentInput = document.querySelector(`input[name="addressFiles${removeLastFileEntire}"]`);
        // console.warn("inside Index ::", index)
        if (employmentInput instanceof HTMLInputElement) {
          const remainingEmploymentFiles = new DataTransfer();
          for (const f of this.addressPresentSelectedFiles) {
            remainingEmploymentFiles.items.add(f.file);
          }
          employmentInput.files = remainingEmploymentFiles.files;
        }
      }
      else if (checkType == 'permanent') {
        index = this.addressPermanentSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedAddressFilesArray = this.addressPermanentSelectedFiles.filter(item => item.file.name !== file.name);
        this.addressPermanentSelectedFiles = selectedAddressFilesArray;
        const employmentInput = document.querySelector(`input[name="addressFiles${removeLastFileEntire}"]`);
        // console.warn("inside Index ::", index)
        if (employmentInput instanceof HTMLInputElement) {
          const remainingEmploymentFiles = new DataTransfer();
          for (const f of this.addressPermanentSelectedFiles) {
            remainingEmploymentFiles.items.add(f.file);
          }
          employmentInput.files = remainingEmploymentFiles.files;
        }
      }
      // const index = this.idSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
      // console.warn("outside Index ::", index)
      if (index != null) {
        this.addressSelectedFiles.splice(fileIndex, 1);
        // console.warn("this.idSelectedFiles : ", this.idSelectedFiles.length)
        // const employmentInput = document.querySelector('input[name="idFiles"]');
        // const employmentInput = document.querySelector(`input[name="idFiles${removeLastFileEntire}"]`);
        // // console.warn("inside Index ::", index)
        // if (employmentInput instanceof HTMLInputElement) {
        //   const remainingEmploymentFiles = new DataTransfer();
        //   for (const f of this.idSelectedFiles) {
        //     remainingEmploymentFiles.items.add(f.file);
        //   }
        //   employmentInput.files = remainingEmploymentFiles.files;
        // }

        // console.log("selectedIdFilesArray : ", selectedIdFilesArray)
        // this.removeAndUpdateDocument(this.idSelectedFiles, checkName)
        // this.removeAndUpdateDocument(this.idSelectedFiles.map(item => item.file), checkNameAndCheckType);
        this.removeAndUpdateDocument(selectedAddressFilesArray.map(item => item.file), checkNameAndCheckType);
        // const matchingFile = this.idSelectedFiles.find(item => {
        //   console.log("id items:::::: ",item.key); // This will print the key property of each item
        //   return item.key === checkNameAndCheckType;
        // });
        // if (matchingFile) {
        //   console.warn("matchingFile true")
        //   this.removeAndUpdateDocument([matchingFile.file], checkName);
        // }
      }
    }
  }

  arrayBufferToBase64(buffer: Uint8Array) {
    let binary = '';
    for (let i = 0; i < buffer.length; i++) {
      binary += String.fromCharCode(buffer[i]);
    }
    return btoa(binary);
  }

  removeAndUpdateDocument(file: File[], checkName: any) {
    // console.warn("checkName removeAndUpdateDocument : ", checkName)
    // console.warn("File length: ", file.length)
    // if (file.length >= 1) {
    const zip = new JSZip();
    const files = file;

    // console.log("file count : ",file.length)
    // console.warn("removeAndUpdateDocument : checkName : ", checkName)

    // console.warn("fileLength removeAndUpdateDocument : ", files.length)
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      // console.warn("removeAndUpdateDocument file.name : ", file.name)
      const fileType = file.name.split('.').pop()?.toLowerCase();
      // console.warn("fileType ::", fileType)
      // Validate the file type
      if (fileType && ['pdf', 'png', 'jpg', 'jpeg', 'zip'].includes(fileType)) {
        // Add the file to the zip
        // console.warn("zip file converted : ")
        zip.file(file.name, file);
      } else {
        // If the file type is not valid, show a warning message and skip this file
        Swal.fire({
          title: 'Please select .pdf, .png, .jpg, .jpeg, or .zip file types only.',
          icon: 'warning'
        });
      }
    }

    // Generate the zip file
if(file.length >= 1){
    zip.generateAsync({ type: "blob" }).then((blob: any) => {
      // Create a File object for the zip blob
      const zipFile = new File([blob], "files.zip", { type: "application/zip" });

      // Assign the zip file to the appropriate form control
      this.candidateForm.get(`${checkName}FileInput`)?.setValue(zipFile);

      // Optionally, you can convert the zip file to base64 if needed
      const reader = new FileReader();
      reader.onload = (e: any) => {
        const base64 = e.target.result;
        // console.log("Base64 string of the zip file:", base64);

        const byteArray = new Uint8Array(e.target.result);

        // Convert Uint8Array to regular array
        const byteArrayAsArray = Array.from(byteArray);

        // Convert byte array to Base64 string
        this.base64 = this.arrayBufferToBase64(byteArray);
        // console.log("base 64 : ", this.base64)
        // Optionally, if you want to store each proof separately as a string
        const newProof = `${checkName}:${this.base64}`;
        // this.newProofList.push(newProof); // Add newProof string to the list
        // const newProof = `${checkName}:${this.base64}`;
        // console.warn("checkName: ", checkName)
        this.newProofList.forEach(entry => {
          // console.log("Entry: ", entry);
        });
        const index = this.newProofList.findIndex(entry => entry.startsWith(`${checkName}:`));
        // const index = this.newProofList.filter(entry => !entry.startsWith(`${checkName}:${file.name}`));
        // console.warn("index: ", index)

        if (index !== -1) {
          // If an entry with the same checkName already exists, overwrite it
          // console.log("Found entry: ", this.newProofList[index]);
          this.newProofList[index] = newProof;
        } else {
          // Otherwise, add the newProof string to the list
          // console.log("Entry not found.");
          this.newProofList.push(newProof);
        }

        // console.log("After remove and update newProofList : ",this.newProofList)
        // If all files have been processed, assign newProofList to this.newProof
        // console.warn("this.newProofList.length : ", this.newProofList.length)
        this.newProofList.length === files.length;
        // console.warn("files.length : ", files.length)
        if (this.newProofList.length === files.length) {
          this.newProofList = this.newProofList;
        }

        if (this.newProofList.length !== files.length) {
          // If files.length is reduced, update this.newProofList accordingly
          // this.newProofList = this.newProofList.slice(0, files.length);
        }

        // console.warn("Updated this.newProofList:", this.newProofList);
        // console.warn("Emove functional this.newProofList", this.newProofList)


      };
      reader.readAsArrayBuffer(zipFile);
    });

  }
  else if(file.length == 0){
    // console.log("CheckName : ",checkName)
    // if(checkName == 'Criminal present'){
    //   console.warn("File in remove : ",file)
    // }
    this.removeEntireCheck(checkName)

  }

    // }
  }

  resetFileInput() {
    this.fileInput.nativeElement.value = '';
  }

  removeEntireCheck(checkName: any) {
    // Example: Log the parameters
    // console.log('removeEntireCheck called with:', checkName);

    // Example: Perform some action based on idSelectedFiles and checkName
    // (You can replace this with actual logic as needed)
    // For instance, remove specific files from a server or update the UI
    // console.warn("BEfore Remove : ", this.newProofList)
    // const index = this.newProofList.indexOf('ID Aadhar');

    const index = this.newProofList.findIndex(item => {
      const [key] = item.split(':'); // Splitting the string into key-value pair
      return key === checkName;
    });

    // console.warn("INDEX : ", index)
    if (index !== -1) {
      this.newProofList.splice(index, 1);
      // console.log('ID Aadhar removed from newProofList');
      // console.warn("After Remove : ", this.newProofList)
    } else {
      // console.log('ID Aadhar not found in newProofList');
    }

  }

  // It is only Education and Employment
  removeTheCheckByDoc(checkName: any, check: any, checkType: string, files: any[], type: any) {

    // console.warn("check : ", check);
    // console.warn("checkType : ", checkType);
    // console.warn("educationSelectedFiles After removal: ", files);
    // console.warn("checkName : ", checkName)
    // console.warn("type : ", type)
    let educationTemSelectedFiles: { key: string; file: File; }[] = [];
    let employmentTemSelectedFiles: { key: string; file: File; }[] = [];

    if (checkType == 'Education UG') {
      // console.warn("this.educationUG  :: ", this.educationUGSelectedFiles)
      const checkname = 'education';
      const filenames = files.map(file => file.file.name.trim());
      // const filteredFiles = this.educationUGSelectedFiles.filter(item => filenames.includes(item.file.name.trim()));


      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        // console.warn("file ::::::: ",file)
        const filename = file.file.name.trim()
        const index = 0; // Get the index of the file in the files array
        // console.warn("Processing file with index: ", index);
        // console.warn("FILE.fileindex : ",file.file.index)
        // Assuming file.filename is the property you want to check against checkName
        // if (file.filename === checkName) {
        // Remove the file from this.educationUGSelectedFiles
        this.educationUGSelectedFiles.forEach(item => {
          // console.log("Comparing item.file.name: ", item.file.name, " with file.filename: ", filename);
        });

        const removeIndex = this.educationUGSelectedFiles.findIndex(item => {
          const itemFilename = item.file.name.trim();
          // console.log("Comparing:", itemFilename, "with", filename);
          // console.log("Result:", itemFilename === filename);
          return itemFilename === filename;
        });

        // console.warn("removeIndex :: ", removeIndex)

        // console.warn("Before Selected Files : ", this.educationUGSelectedFiles)
        educationTemSelectedFiles.push(this.educationUGSelectedFiles[removeIndex]);

        // this.removeFile(checkname, type, file, 0, 1, removeIndex)
        this.removeFile(checkname, type, file, 0, 0, removeIndex, 1,null)


        // console.warn("this.educationUGSelectedFiles after removal: ", this.educationUGSelectedFiles);
        // console.warn("file.index : ", file.file.index)
        // removeFile(fileType: string, checkType: any, file: File, checkIndex: number, docIndex: number, fileIndex: number) {
      }

      // console.warn("this.educationTemSelectedFiles after removal: ", educationTemSelectedFiles);
      // console.warn("this.educationTemSelectedFiles after removal: ",educationTemSelectedFiles);
      if(files.length == 0){
        for (let i = 0; i <= files.length; i++) {
          // console.log("FILE IS 0000000000")
          const file = files[i];
          this.EducationUGDocTypeList=[];
          // console.warn("Remove File EducationUGDocTypeList : ",this.EducationUGDocTypeList)
          this.removeFile(checkname, type, file, 0, 0, null, 1,null)
        }

      }

      this.educationUGSelectedFiles = educationTemSelectedFiles;


    }

    else if (checkType == 'Education PG') {
      // console.warn("this.educationPG  :: ", this.educationPGSelectedFiles)
      const checkname = 'education';
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const filename = file.file.name.trim()
        const index = i; // Get the index of the file in the files array
        // console.warn("Processing file with index: ", index);
        // Assuming file.filename is the property you want to check against checkName
        // if (file.filename === checkName) {
        // Remove the file from this.educationUGSelectedFiles
        this.educationPGSelectedFiles.forEach(item => {
          // console.log("Comparing item.file.name: ", item.file.name, " with file.filename: ", filename);
        });

        const removeIndex = this.educationPGSelectedFiles.findIndex(item => {
          const itemFilename = item.file.name.trim();
          // console.log("Comparing:", itemFilename, "with", filename);
          // console.log("Result:", itemFilename === filename);
          return itemFilename === filename;
        });

        // console.warn("removeIndex :: ", removeIndex)

        // console.warn("Before Selected Files : ", this.educationPGSelectedFiles)
        educationTemSelectedFiles.push(this.educationPGSelectedFiles[removeIndex]);

        // this.removeFile(checkname, type, file, 0, 1, removeIndex)
        this.removeFile(checkname, type, file, 0, 0, removeIndex, 1,null)


        // console.warn("this.educationPGSelectedFiles after removal: ", this.educationPGSelectedFiles);
        // console.warn("file.index : ", file.index)
        // removeFile(fileType: string, checkType: any, file: File, checkIndex: number, docIndex: number, fileIndex: number) {
        // this.removeFile(type, checkType, file, 0, 0, i)
      }

      // console.warn("this.educationTemSelectedFiles after removal: ", educationTemSelectedFiles);
      // console.warn("this.educationTemSelectedFiles after removal: ",educationTemSelectedFiles);
      if(files.length == 0){
        for (let i = 0; i <= files.length; i++) {
          // console.log("FILE IS 0000000000")
          const file = files[i];
          this.EducationPGDocTypeList=[];
          // console.warn("Remove File EducationPGDocTypeList : ",this.EducationPGDocTypeList)
          this.removeFile(checkname, type, file, 0, 0, null, 1,null)
        }

      }

      this.educationPGSelectedFiles = educationTemSelectedFiles;

    }

    else if (checkType == 'Education Diploma') {
      // console.warn("this.educationDiplomaSelectedFiles  :: ", this.educationDiplomaSelectedFiles)
      const checkname = 'education';
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const filename = file.file.name.trim()
        const index = i; // Get the index of the file in the files array
        // console.warn("Processing file with index: ", index);
        // Assuming file.filename is the property you want to check against checkName
        // if (file.filename === checkName) {
        // Remove the file from this.educationUGSelectedFiles
        this.educationDiplomaSelectedFiles.forEach(item => {
          // console.log("Comparing item.file.name: ", item.file.name, " with file.filename: ", filename);
        });

        const removeIndex = this.educationDiplomaSelectedFiles.findIndex(item => {
          const itemFilename = item.file.name.trim();
          // console.log("Comparing:", itemFilename, "with", filename);
          // console.log("Result:", itemFilename === filename);
          return itemFilename === filename;
        });

        // console.warn("removeIndex :: ",removeIndex)

        // console.warn("Before Selected Files : ", this.educationDiplomaSelectedFiles)
        educationTemSelectedFiles.push(this.educationDiplomaSelectedFiles[removeIndex]);

        // this.removeFile(checkname, type, file, 0, 1, removeIndex)
        this.removeFile(checkname, type, file, 0, 0, removeIndex, 1,null)


        // console.warn("this.educationDiplomaSelectedFiles after removal: ", this.educationDiplomaSelectedFiles);
        // console.warn("file.index : ", file.index)
        // removeFile(fileType: string, checkType: any, file: File, checkIndex: number, docIndex: number, fileIndex: number) {
      }

      // console.warn("this.educationTemSelectedFiles after removal: ", educationTemSelectedFiles);

      console.warn("this.educationTemSelectedFiles after removal: ",educationTemSelectedFiles);
      if(files.length == 0){
        for (let i = 0; i <= files.length; i++) {
          // console.log("FILE IS 0000000000")
          const file = files[i];
          this.EducationDiplomaTHDocTypeList=[];
          // console.warn("Remove File EducationDiplomaDocTypeList : ",this.EducationDiplomaTHDocTypeList)
          this.removeFile(checkname, type, file, 0, 0, null, 1,null)
        }

      }

      this.educationDiplomaSelectedFiles = educationTemSelectedFiles;

    }

    else if (checkType == 'Education 10TH') {

      // console.warn("this.education10THSelectedFiles  :: ", this.education10THSelectedFiles)
      const checkname = 'education';
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const filename = file.file.name.trim()
        const index = i; // Get the index of the file in the files array
        // console.warn("Processing file with index: ", index);
        // Assuming file.filename is the property you want to check against checkName
        // if (file.filename === checkName) {
        // Remove the file from this.educationUGSelectedFiles
        this.education10THSelectedFiles.forEach(item => {
          // console.log("Comparing item.file.name: ", item.file.name, " with file.filename: ", filename);
        });

        const removeIndex = this.education10THSelectedFiles.findIndex(item => {
          const itemFilename = item.file.name.trim();
          // console.log("Comparing:", itemFilename, "with", filename);
          // console.log("Result:", itemFilename === filename);
          return itemFilename === filename;
        });

        // console.warn("removeIndex :: ",removeIndex)

        // console.warn("Before Selected Files : ", this.education10THSelectedFiles)
        educationTemSelectedFiles.push(this.education10THSelectedFiles[removeIndex]);

        // this.removeFile(checkname, type, file, 0, 1, removeIndex)
        this.removeFile(checkname, type, file, 0, 0, removeIndex, 1,null)


        // console.warn("this.education10THSelectedFiles after removal: ", this.education10THSelectedFiles);
        // console.warn("file.index : ", file.index)
        // removeFile(fileType: string, checkType: any, file: File, checkIndex: number, docIndex: number, fileIndex: number) {
      }

      // console.warn("this.educationTemSelectedFiles after removal: ", educationTemSelectedFiles);

      // console.warn("this.educationTemSelectedFiles after removal: ",educationTemSelectedFiles);
      if(files.length == 0){
        for (let i = 0; i <= files.length; i++) {
          // console.log("FILE IS 0000000000")
          const file = files[i];
          this.Education10THDocTypeList=[];
          // console.warn("Remove File Education10THDocTypeList : ",this.Education10THDocTypeList)
          this.removeFile(checkname, type, file, 0, 0, null, 1,null)
        }

      }
      this.education10THSelectedFiles = educationTemSelectedFiles;


    }

    else if (checkType == 'Education 12TH') {

      // console.warn("this.education12THSelectedFiles  :: ", this.education12THSelectedFiles)
      const checkname = 'education';
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const filename = file.file.name.trim()
        const index = i; // Get the index of the file in the files array
        // console.warn("Processing file with index: ", index);
        // Assuming file.filename is the property you want to check against checkName
        // if (file.filename === checkName) {
        // Remove the file from this.educationUGSelectedFiles
        this.education12THSelectedFiles.forEach(item => {
          // console.log("Comparing item.file.name: ", item.file.name, " with file.filename: ", filename);
        });

        const removeIndex = this.education12THSelectedFiles.findIndex(item => {
          const itemFilename = item.file.name.trim();
          // console.log("Comparing:", itemFilename, "with", filename);
          // console.log("Result:", itemFilename === filename);
          return itemFilename === filename;
        });

        // console.warn("removeIndex :: ",removeIndex)

        // console.warn("Before Selected Files : ", this.education12THSelectedFiles)
        educationTemSelectedFiles.push(this.education12THSelectedFiles[removeIndex]);

        // this.removeFile(checkname, type, file, 0, 1, removeIndex)
        this.removeFile(checkname, type, file, 0, 0, removeIndex, 1,null)


        // console.warn("this.education10THSelectedFiles after removal: ", this.education12THSelectedFiles);
        // console.warn("file.index : ", file.index)
        // removeFile(fileType: string, checkType: any, file: File, checkIndex: number, docIndex: number, fileIndex: number) {
      }

      // console.warn("this.educationTemSelectedFiles after removal: ", educationTemSelectedFiles);
      // console.warn("this.educationTemSelectedFiles after removal: ",educationTemSelectedFiles);
      if(files.length == 0){
        for (let i = 0; i <= files.length; i++) {
          // console.log("FILE IS 0000000000")
          const file = files[i];
          this.Education12THDocTypeList=[];
          // console.warn("Remove File Education12THDocTypeList : ",this.Education12THDocTypeList)
          this.removeFile(checkname, type, file, 0, 0, null, 1,null)
        }

      }
      this.education12THSelectedFiles = educationTemSelectedFiles;


    }
    // this.removeAndUpdateDocument(files.map(item => item.file), checkType);

    else if (checkType == 'Employment EMP1') {
// console.log("==============================")
      const checkname = 'employment';
      // console.warn("this.employmentEMP1SelectedFiles  :: ", this.employmentEMP1SelectedFiles)
      //       console.warn("this.employment Temp file  :: ", employmentTemSelectedFiles)
      // console.log('files.length : ',files.length)
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const filename = file.file.name.trim()
        const index = i; // Get the index of the file in the files array
        console.warn("Processing file with index: ", index);
        // Assuming file.filename is the property you want to check against checkName
        // if (file.filename === checkName) {
        // Remove the file from this.educationUGSelectedFiles
        this.employmentEMP1SelectedFiles.forEach(item => {
          // console.log("Comparing item.file.name: ", item.file.name, " with file.filename: ", filename);
        });

        const removeIndex = this.employmentEMP1SelectedFiles.findIndex(item => {
          const itemFilename = item.file.name.trim();
          // console.log("Comparing:", itemFilename, "with", filename);
          // console.log("Result:", itemFilename === filename);
          return itemFilename === filename;
        });

        // console.warn("removeIndex :: ",removeIndex)

        // console.warn("Before Selected Files : ",this.employmentEMP1SelectedFiles)
        employmentTemSelectedFiles.push(this.employmentEMP1SelectedFiles[removeIndex]);

        // this.removeFile(checkname, type, file, 0, 1, removeIndex)
        this.removeFile(checkname, type, file, 0, 0, removeIndex, 1,null)


        // console.warn("this.employmentEMP1SelectedFiles after removal: ", this.employmentEMP1SelectedFiles);
        // console.warn("file.index : ", file.index)
        // removeFile(fileType: string, checkType: any, file: File, checkIndex: number, docIndex: number, fileIndex: number) {
      }

      // console.warn("this.employmentTemSelectedFiles after removal: ",employmentTemSelectedFiles);
      if(files.length == 0){
        for (let i = 0; i <= files.length; i++) {
          // console.log("FILE IS 0000000000")
          const file = files[i];
          this.EmploymentEMP1DocTypeList = [];
          // console.warn("Remove File EmploymentEMP1DocTypeList : ",this.EmploymentEMP1DocTypeList)
          this.removeFile(checkname, type, file, 0, 0, null, 1,null)
        }

      }

      this.employmentEMP1SelectedFiles = employmentTemSelectedFiles;


    }

    else if (checkType == 'Employment EMP2') {

      // console.warn("this.employmentEMP2SelectedFiles  :: ", this.employmentEMP2SelectedFiles)
      const checkname = 'employment';
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const filename = file.file.name.trim()
        const index = i; // Get the index of the file in the files array
        // console.warn("Processing file with index: ", index);
        // Assuming file.filename is the property you want to check against checkName
        // if (file.filename === checkName) {
        // Remove the file from this.educationUGSelectedFiles
        this.employmentEMP2SelectedFiles.forEach(item => {
          // console.log("Comparing item.file.name: ", item.file.name, " with file.filename: ", filename);
        });

        const removeIndex = this.employmentEMP2SelectedFiles.findIndex(item => {
          const itemFilename = item.file.name.trim();
          // console.log("Comparing:", itemFilename, "with", filename);
          // console.log("Result:", itemFilename === filename);
          return itemFilename === filename;
        });

        // console.warn("removeIndex :: ",removeIndex)

        //   console.warn("Before Selected Files : ",this.employmentEMP2SelectedFiles)
        employmentTemSelectedFiles.push(this.employmentEMP2SelectedFiles[removeIndex]);

        // this.removeFile(checkname, type, file, 0, 1, removeIndex)
        this.removeFile(checkname, type, file, 0, 0, removeIndex, 1,null)


        // console.warn("this.employmentEMP2SelectedFiles after removal: ", this.employmentEMP2SelectedFiles);
        // console.warn("file.index : ", file.index)
        // removeFile(fileType: string, checkType: any, file: File, checkIndex: number, docIndex: number, fileIndex: number) {
      }

      // console.warn("this.employmentTemSelectedFiles after removal: ",employmentTemSelectedFiles);

      // console.warn("this.employmentTemSelectedFiles after removal: ",employmentTemSelectedFiles);
      if(files.length == 0){
        for (let i = 0; i <= files.length; i++) {
          // console.log("FILE IS 0000000000")
          const file = files[i];
          this.EmploymentEMP2DocTypeList = [];
          // console.warn("Remove File EmploymentEMP2DocTypeList : ",this.EmploymentEMP2DocTypeList)
          this.removeFile(checkname, type, file, 0, 0, null, 1,null)
        }

      }

      this.employmentEMP2SelectedFiles = employmentTemSelectedFiles;


    }


    else if (checkType == 'Employment EMP3') {

      // console.warn("this.employmentEMP3SelectedFiles  :: ", this.employmentEMP3SelectedFiles)
      const checkname = 'employment';
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const filename = file.file.name.trim()
        const index = i; // Get the index of the file in the files array
        // console.warn("Processing file with index: ", index);
        // Assuming file.filename is the property you want to check against checkName
        // if (file.filename === checkName) {
        // Remove the file from this.educationUGSelectedFiles
        this.employmentEMP3SelectedFiles.forEach(item => {
          // console.log("Comparing item.file.name: ", item.file.name, " with file.filename: ", filename);
        });

        const removeIndex = this.employmentEMP3SelectedFiles.findIndex(item => {
          const itemFilename = item.file.name.trim();
          // console.log("Comparing:", itemFilename, "with", filename);
          // console.log("Result:", itemFilename === filename);
          return itemFilename === filename;
        });

        // console.warn("removeIndex :: ",removeIndex)

        // console.warn("Before Selected Files : ",this.employmentEMP3SelectedFiles)
        employmentTemSelectedFiles.push(this.employmentEMP3SelectedFiles[removeIndex]);

        // this.removeFile(checkname, type, file, 0, 1, removeIndex)
        this.removeFile(checkname, type, file, 0, 0, removeIndex, 1,null)


        // console.warn("this.employmentEMP3SelectedFiles after removal: ", this.employmentEMP3SelectedFiles);
        // console.warn("file.index : ", file.index)
        // removeFile(fileType: string, checkType: any, file: File, checkIndex: number, docIndex: number, fileIndex: number) {
      }

      // console.warn("this.employmentTemSelectedFiles after removal: ",employmentTemSelectedFiles);
      // console.warn("this.employmentTemSelectedFiles after removal: ",employmentTemSelectedFiles);
      if(files.length == 0){
        for (let i = 0; i <= files.length; i++) {
          // console.log("FILE IS 0000000000")
          const file = files[i];
          this.EmploymentEMP3DocTypeList = [];
          // console.warn("Remove File EmploymentEMP3DocTypeList : ",this.EmploymentEMP3DocTypeList)
          this.removeFile(checkname, type, file, 0, 0, null, 1,null)
        }

      }

      this.employmentEMP3SelectedFiles = employmentTemSelectedFiles;


    }

  }


  submitConventionalCandidateForm() {
    // console.log("FORM>>>", this.candidateForm.value)

    // console.warn(" this.checks.push : ", this.checks)
    // this.employmentChecks.forEach(item => {
    //   // Add selectedEmploymentSubType value to Employment property
    //   item.Employment += ` ${item.selectedEmploymentSubType}`;

    //   // Remove selectedEmploymentSubType property
    //   delete item.selectedEmploymentSubType;
    // });
    // console.warn(" this.employment.push : ", this.employmentChecks)
    // console.warn(" this.id.push : ", this.idChecks)
    console.warn(" this.criminalCheck.push : ", this.criminalCheck)
    // console.warn(" this.addressCheck.push : ", this.addressCheck)

    const combinedChecks: {
      employment?: any[];
      id?: any[];
      criminal?: any[];
      address?: any[];
    } = {};

    combinedChecks.employment = this.employmentChecks.map(item => {
      const newItem = { ...item }; // Clone the object to avoid modifying the original
      newItem.Employment += ` (${newItem.selectedEmploymentSubType})`; // Modify Employment property
      delete newItem.selectedEmploymentSubType; // Remove selectedEmploymentSubType property
      return newItem;
    });
    combinedChecks.id = this.idChecks;
    combinedChecks.criminal = this.criminalCheck;
    combinedChecks.address = this.addressCheck;



    // console.warn("Combined checks:", combinedChecks);



    const mergedObject: { [key: string]: string } = {};

    for (const obj of this.employmentChecks) {
      for (const [key, value] of Object.entries(obj)) {
        if (!mergedObject[key]) {
          mergedObject[key] = `"${key}": "${value}"`; // Initialize the value with key
        } else {
          mergedObject[key] += `, "${key}": "${value}"`; // Append the new key-value pair to the existing value
        }
      }
    }

    for (const obj of this.checks) {
      for (const [key, value] of Object.entries(obj)) {
        if (!mergedObject[key]) {
          mergedObject[key] = `"${key}": "${value}"`; // Initialize the value with key
        } else {
          mergedObject[key] += `, "${key}": "${value}"`; // Append the new key-value pair to the existing value
        }
      }
    }

    for (const obj of this.idChecks) {
      for (const [key, value] of Object.entries(obj)) {
        if (!mergedObject[key]) {
          // mergedObject[key] = `${key}: '${value}'`; // Initialize the value with key
          mergedObject[key] = `"${key}": "${value}"`; // Initialize the value with key
        } else {
          mergedObject[key] += `, "${key}": "${value}"`; // Append the new key-value pair to the existing value
        }
      }
    }

    for (const obj of this.criminalCheck) {
      for (const [key, value] of Object.entries(obj)) {
        if (!mergedObject[key]) {
          mergedObject[key] = `"${key}": "${value}"`; // Initialize the value with key
        } else {
          mergedObject[key] += `, "${key}": "${value}"`; // Append the new key-value pair to the existing value
        }
      }
    }

    // for (const obj of this.addressCheck) {
    //   for (const [key, value] of Object.entries(obj)) {
    //     if (!mergedObject[key]) {
    //       mergedObject[key] = `"${key}": "${value}"`; // Initialize the value with key
    //     } else {
    //       mergedObject[key] += `, "${key}": "${value}"`; // Append the new key-value pair to the existing value
    //     }
    //   }
    // }

    // let jsonArray: any[] = [];

    // for (const obj of this.addressCheck) {
    //     let jsonObject: { [key: string]: any } = {};
    //     for (const [key, value] of Object.entries(obj)) {
    //         jsonObject[key] = value;
    //     }
    //     jsonArray.push(jsonObject);
    // }

    let jsonArray: any[] = [];

    for (const obj of this.addressCheck) {
      let jsonObject: { [key: string]: any } = {};
      for (const [key, value] of Object.entries(obj)) {
        jsonObject[key] = value;
      }
      jsonArray.push(jsonObject);
    }

    for (const obj of this.criminalCheck) {
      let jsonObject: { [key: string]: any } = {};
      for (const [key, value] of Object.entries(obj)) {
        jsonObject[key] = value;
      }
      jsonArray.push(jsonObject);
    }


    const result = { jsonArray }; // Wrapping jsonArray inside an object
    // console.log("RESULT: ", result);

    // console.log(JSON.stringify(jsonArray));

    // Convert the object into a string representation
    const mergedString = Object.values(mergedObject).join(', ');
    // const mergedString = `{${Object.values(mergedObject).join(', ')}}`;

    // console.log(`{${mergedString}}`);
    // console.log(mergedObject);

    const jsonArrayString = JSON.stringify(jsonArray);

    console.log("jsonStriong", jsonArrayString);

    // const mergedArray = [jsonArrayString, mergedString];

    // const finalString = JSON.stringify(mergedArray);
    // console.log("final String",finalString);


    const mergedString2 = JSON.stringify(mergedString);

    // console.warn("mergedObject2 : ", mergedString2)

    const attributeaAndValues = this.agentAttributeListForm.filter(item => item.value !== null && item.value.trim() !== '').reduce((obj, item) => {


      if (item.value === null || item.value.trim() === '') {
        // console.warn("ITEMS >>>>>>", item.value)
        return false; // Return false if any item.value is null or empty
      }

      obj[item.label] = item.value;

      // console.log("ITEM >>>>>>", item);
      // console.log("OBJ >>>>>>", obj);

      return obj;

    }, {});

    // console.warn("educationAttributeValues?>>", attributeaAndValues)

    const mergedData = {
      ...this.candidateForm.value,
      // ...attributeaAndValues,
      mergedString2
    };

    // console.warn("MERGED DATA>>", mergedData)

    const formData = new FormData();
    formData.append('vendorchecks', JSON.stringify(mergedData));
    formData.append('addressCheck', JSON.stringify(jsonArrayString));


    // console.log(this.proofDocumentNew);
    // console.warn(this.newProofList)
    const newProofListObjects = this.newProofList.map((label: string) => ({ label, value: null as string | null }));


    // console.log("Before deletion:", this.Checks);

    const addressIndex = this.Checks.indexOf("Address");

    // If "Address" exists in the array, remove it
    if (addressIndex !== -1) {
      this.Checks.splice(addressIndex, 1);
      // console.log("Address removed from Checks array.");
    } else {
      // console.log("Address not found in Checks array.");
    }

    //   console.log("Keys (Labels):");
    this.newProofList.forEach(item => {
      const key = item.split(':')[0]; // Split the string and get the first part (the key)
      // console.log(key);
    });


    const keysFromNewProofList = this.newProofList.map(item => item.split(':')[0]);

    // Log keys from this.newProofList
    console.log("Keys (Labels) from newProofList:");
    keysFromNewProofList.forEach(key => {
      // console.log(key);
    });

    // console.log("Matching elements from Checks:");
    // console.warn("this.checks::", this.checks)
    // this.Checks.forEach((check: any) => {
    //   // if (keysFromNewProofList.keys.includes(check)) {
    //   //   console.log("checks ::: ", check);
    //   // }
    //   console.warn("inside the check:::")
    //   if (Object.keys(keysFromNewProofList).includes(check)) {
    //     console.log("checks ::: ", check);
    // }
    // });

    Object.keys(this.conventionalCandidateCheck).forEach((check) => {
      // console.warn("inside the check:::");
      if (Object.keys(keysFromNewProofList).includes(check)) {
        // console.log("checks ::: ", check);
      }
    });


    const allMatched = this.conventionalCandidateCheck.every((check: any) => keysFromNewProofList.includes(check));
    // console.log("All matched:", allMatched);

    // if (allMatched) {
    //   console.log("All matched: TRUE ================");
    //   return true;
    // } else {
    //   console.log("NOT  matched: FALSE ================");
    //   return false;
    // }


    formData.append("file", JSON.stringify(this.newProofList));

    // if (this.candidateForm.valid && allMatched != false) {
    this.candidateService.conventionalCandidate(formData).subscribe((data: any) => {
      // console.log("data>>>",data)

      if (data.outcome === true) {
        const navURL = 'candidate/cThankYou/' + this.candidateCode;
        this.navRouter.navigate([navURL]);
      } else {
        Swal.fire({
          title: data.message,
          icon: 'warning'
        })
      }


    })

    // }
    // else {
    //   Swal.fire({
    //     title: 'Please enter the required details.',
    //     icon: 'warning'
    //   })
    // }
  }

  // test(eventvalue:any,address:any){

  //   console.warn("eventValue : ",eventvalue.target.value)
  //   console.warn("addressValue : ",address)

  //   address.address = eventvalue.target.value;
  // }

}
