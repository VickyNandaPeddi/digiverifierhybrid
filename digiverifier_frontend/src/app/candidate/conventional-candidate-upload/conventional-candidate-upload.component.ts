import { any } from '@amcharts/amcharts4/.internal/core/utils/Array';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import JSZip from 'jszip';
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
  Checks: any;
  subValue: string = '';
  attribute: { value: string } = { value: '' }; // Define attribute object with a value property
  selectedFiles: File[] = [];
  educationSelectedFiles: { key: string, file: File }[] = [];
  educationUGSelectedFiles: { key: string, file: File }[] = [];
  educationPGSelectedFiles: { key: string, file: File }[] = [];
  educationDiplomaSelectedFiles: { key: string, file: File }[] = [];
  education10THSelectedFiles: { key: string, file: File }[] = [];
  education12THSelectedFiles: { key: string, file: File }[] = [];


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
        if (data.outcome === true) {
          console.warn("DATA>>>", data)
          this.candidateName = data.data.candidateName;
          this.contactNumber = data.data.contactNumber;
          this.candidateMailId = data.data.emailId;
          this.candidateId = data.data.candidateId;
          this.accountName = data.data.accountName;
          this.conventionalCandidateCheck = data.data.conventionalCandidateCheck.split(',');

          console.warn("checks>>", this.conventionalCandidateCheck)
          this.Checks = this.conventionalCandidateCheck;


          // this.candidateForm.controls['candidateCode'].setValue(this.candidateCode);
          this.candidateForm.controls['candidateId'].setValue(this.candidateId);
          this.candidateForm.controls['candidateName'].setValue(this.candidateName);
          this.candidateForm.controls['contactNo'].setValue(this.contactNumber);
          this.candidateForm.controls['email'].setValue(this.candidateMailId);

          this.agentAttributeListForm = this.conventionalCandidateCheck.map((ele: any) => {
            let defaultValue = ele === "Database" ? "NA" : ''; // Check if ele is "database", set defaultValue to "NA", otherwise empty string
            console.log(`Setting default value for "${ele}" to: ${defaultValue}`);
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


  addCheck() {
    this.checks.push({ Education: '' });
    console.warn("educatio.push: ", this.educationSelectedFiles)
    console.warn(" this.checks.push : ", this.checks)
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
    console.warn("check : ", check)
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

  removeEducationDoc(check: any, index: number) {
    if (check.documents && check.documents.length > index) {
      check.documents.splice(index, 1); // Remove the document at the specified index
    }
  }

  removeEmploymentDoc(employmentCheck: any, index: number) {
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

  updateEducationDocuments(selectedEducation: string, check: any) {
    // Log the previous and current education levels
    // console.log('Previous Education Level:', this.previousEducationLevel);
    // console.log('Current Education Level:', selectedEducation);

    // Remove files associated with the previous education level
    // if (this.previousEducationLevel) {
    //   this.educationSelectedFiles = this.educationSelectedFiles.filter(fileObj =>
    //     !fileObj.key.startsWith(`Education ${this.previousEducationLevel}`)
    //   );
    // }

    // if(this.previousEducationLevel == 'UG'){
    //   console.warn("EDUCATION UG TRUE::")
    //   this.educationUGSelectedFiles = [];
    //   console.log('Before filtering:', this.newProofList);

    //   this.newProofList = this.newProofList.filter((item: any) => item.key === 'Education UG');

    //   console.log('After filtering:', this.newProofList);
    // }

    if (this.previousEducationLevel == 'UG') {
      // console.log('Previous Education Level:', this.previousEducationLevel);
      // console.log('Current Education Level:', selectedEducation);
      // console.warn("EDUCATION UG TRUE::")
      // this.educationUGSelectedFiles = [];
      // console.warn("Before educationUGSelectedFiles : ", this.educationSelectedFiles)
      // console.log('Before filtering:', this.newProofList);

      this.educationSelectedFiles.forEach((fileObj: any) => {
        if (fileObj.key.includes('Education UG')) {
          // Modify the key to replace 'Education UG' with 'Education PG'
          fileObj.key = fileObj.key.replace('Education UG', 'Education ' + selectedEducation);
        }
      });
      // console.log('After modification: educationSelectedFiles', this.educationSelectedFiles);
      this.newProofList = this.newProofList.map((item: string) => {
        console.warn("ITEM:", item);
        if (item.includes('Education UG')) {
          // console.warn("Education Current:", 'Education ' + selectedEducation);
          // Modify the string to replace 'Education UG' with the new education level
          return item.replace('Education UG', 'Education ' + selectedEducation);
        }
        return item; // Return the unmodified string if it doesn't contain 'Education UG'
      });
      // console.log('After modification:', this.newProofList);
    }
    else if (this.previousEducationLevel == 'PG') {
      // console.log('Previous Education Level:', this.previousEducationLevel);
      // console.log('Current Education Level:', selectedEducation);
      // console.warn("EDUCATION PG TRUE::")
      // this.educationUGSelectedFiles = [];
      // console.warn("Before educationUGSelectedFiles : ", this.educationSelectedFiles)
      // console.log('Before filtering:', this.newProofList);

      this.educationSelectedFiles.forEach((fileObj: any) => {
        if (fileObj.key.includes('Education PG')) {
          // Modify the key to replace 'Education UG' with 'Education PG'
          fileObj.key = fileObj.key.replace('Education PG', 'Education ' + selectedEducation);
        }
      });
      // console.log('After modification: educationSelectedFiles', this.educationSelectedFiles);
      this.newProofList = this.newProofList.map((item: string) => {
        // console.warn("ITEM:", item);
        if (item.includes('Education PG')) {
          // console.warn("Education Current:", 'Education ' + selectedEducation);
          // Modify the string to replace 'Education UG' with the new education level
          return item.replace('Education PG', 'Education ' + selectedEducation);
        }
        return item; // Return the unmodified string if it doesn't contain 'Education UG'
      });
      // console.log('After modification:', this.newProofList);
    }
    else if (this.previousEducationLevel == '10TH') {
      // console.log('Previous Education Level:', this.previousEducationLevel);
      // console.log('Current Education Level:', selectedEducation);
      // console.warn("EDUCATION 10TH TRUE::")
      // this.educationUGSelectedFiles = [];
      // console.warn("Before educationUGSelectedFiles : ", this.educationSelectedFiles)
      // console.log('Before filtering:', this.newProofList);

      this.educationSelectedFiles.forEach((fileObj: any) => {
        if (fileObj.key.includes('Education 10TH')) {
          // Modify the key to replace 'Education UG' with 'Education PG'
          fileObj.key = fileObj.key.replace('Education 10TH', 'Education ' + selectedEducation);
        }
      });
      // console.log('After modification: educationSelectedFiles', this.educationSelectedFiles);
      this.newProofList = this.newProofList.map((item: string) => {
        // console.warn("ITEM:", item);
        if (item.includes('Education 10TH')) {
          console.warn("Education Current:", 'Education ' + selectedEducation);
          // Modify the string to replace 'Education UG' with the new education level
          return item.replace('Education 10TH', 'Education ' + selectedEducation);
        }
        return item; // Return the unmodified string if it doesn't contain 'Education UG'
      });
      // console.log('After modification:', this.newProofList);
    }
    else if (this.previousEducationLevel == '12TH') {
      // console.log('Previous Education Level:', this.previousEducationLevel);
      // console.log('Current Education Level:', selectedEducation);
      // console.warn("EDUCATION 12TH TRUE::")
      // this.educationUGSelectedFiles = [];
      // console.warn("Before educationUGSelectedFiles : ", this.educationSelectedFiles)
      // console.log('Before filtering:', this.newProofList);

      this.educationSelectedFiles.forEach((fileObj: any) => {
        if (fileObj.key.includes('Education 12TH')) {
          // Modify the key to replace 'Education UG' with 'Education PG'
          fileObj.key = fileObj.key.replace('Education 12TH', 'Education ' + selectedEducation);
        }
      });
      // console.log('After modification: educationSelectedFiles', this.educationSelectedFiles);
      this.newProofList = this.newProofList.map((item: string) => {
        // console.warn("ITEM:", item);
        if (item.includes('Education 12TH')) {
          console.warn("Education Current:", 'Education ' + selectedEducation);
          // Modify the string to replace 'Education UG' with the new education level
          return item.replace('Education 12TH', 'Education ' + selectedEducation);
        }
        return item; // Return the unmodified string if it doesn't contain 'Education UG'
      });
      // console.log('After modification:', this.newProofList);
    }
    else if (this.previousEducationLevel == 'Diploma') {
      // console.log('Previous Education Level:', this.previousEducationLevel);
      // console.log('Current Education Level:', selectedEducation);
      // console.warn("EDUCATION Diploma TRUE::")
      // this.educationUGSelectedFiles = [];
      // console.warn("Before educationUGSelectedFiles : ", this.educationSelectedFiles)
      // console.log('Before filtering:', this.newProofList);

      this.educationSelectedFiles.forEach((fileObj: any) => {
        if (fileObj.key.includes('Education Diploma')) {
          // Modify the key to replace 'Education UG' with 'Education PG'
          fileObj.key = fileObj.key.replace('Education Diploma', 'Education ' + selectedEducation);
        }
      });
      // console.log('After modification: educationSelectedFiles', this.educationSelectedFiles);
      this.newProofList = this.newProofList.map((item: string) => {
        // console.warn("ITEM:", item);
        if (item.includes('Education Diploma')) {
          // console.warn("Education Current:", 'Education ' + selectedEducation);
          // Modify the string to replace 'Education UG' with the new education level
          return item.replace('Education Diploma', 'Education ' + selectedEducation);
        }
        return item; // Return the unmodified string if it doesn't contain 'Education UG'
      });
      // console.log('After modification:', this.newProofList);
    }

    // // Update the check's education and reset documents
    // check.Education = selectedEducation;
    // check.documents = [];

    // // Update filtered documents for new education level
    // this.filteredEducationDocuments = this.allEducationDocuments[selectedEducation] || [];

    // // Update the previous education level
    // this.previousEducationLevel = selectedEducation;
  }

  previousEmploymentLevel: string = '';

  updateEmploymentDocuments(selectedEmployment: string, check: any) {
    // Log the previous and current education levels
    // console.log('Previous Employment Level:', this.previousEmploymentLevel);
    // console.log('Current Employment Level:', selectedEmployment);

    if (this.previousEmploymentLevel == 'EMP1') {
      // this.educationUGSelectedFiles = [];
      // console.warn("Before EmploymentEMP1SelectedFiles : ", this.employmentSelectedFiles)
      // console.log('Before filtering:', this.newProofList);

      this.employmentSelectedFiles.forEach((fileObj: any) => {
        if (fileObj.key.includes('Employment EMP1')) {
          // Modify the key to replace 'Education UG' with 'Education PG'
          fileObj.key = fileObj.key.replace('Employment EMP1', 'Employment ' + selectedEmployment);
        }
      });
      // console.log('After modification: employmentSelectedFiles', this.employmentSelectedFiles);
      this.newProofList = this.newProofList.map((item: string) => {
        // console.warn("ITEM:", item);
        if (item.includes('Employment EMP1')) {
          // console.warn("Employment Current:", 'Employment ' + selectedEmployment);
          // Modify the string to replace 'Education UG' with the new education level
          return item.replace('Employment EMP1', 'Employment ' + selectedEmployment);
        }
        return item; // Return the unmodified string if it doesn't contain 'Education UG'
      });
      // console.log('After modification:', this.newProofList);
    }
    else if (this.previousEmploymentLevel == 'EMP2') {
      // console.warn("Before EmploymentEMP1SelectedFiles : ", this.employmentSelectedFiles)
      // console.log('Before filtering:', this.newProofList);

      this.employmentSelectedFiles.forEach((fileObj: any) => {
        if (fileObj.key.includes('Employment EMP2')) {
          // Modify the key to replace 'Education UG' with 'Education PG'
          fileObj.key = fileObj.key.replace('Employment EMP2', 'Employment ' + selectedEmployment);
        }
      });
      // console.log('After modification: employmentSelectedFiles', this.employmentSelectedFiles);
      this.newProofList = this.newProofList.map((item: string) => {
        console.warn("ITEM:", item);
        if (item.includes('Employment EMP2')) {
          // console.warn("Employment Current:", 'Employment ' + selectedEmployment);
          // Modify the string to replace 'Education UG' with the new education level
          return item.replace('Employment EMP2', 'Employment ' + selectedEmployment);
        }
        return item; // Return the unmodified string if it doesn't contain 'Education UG'
      });
      // console.log('After modification:', this.newProofList);
    }

    else if (this.previousEmploymentLevel == 'EMP3') {
      // console.warn("Before EmploymentEMP1SelectedFiles : ", this.employmentSelectedFiles)
      // console.log('Before filtering:', this.newProofList);

      this.employmentSelectedFiles.forEach((fileObj: any) => {
        if (fileObj.key.includes('Employment EMP3')) {
          // Modify the key to replace 'Education UG' with 'Education PG'
          fileObj.key = fileObj.key.replace('Employment EMP3', 'Employment ' + selectedEmployment);
        }
      });
      // console.log('After modification: employmentSelectedFiles', this.employmentSelectedFiles);
      this.newProofList = this.newProofList.map((item: string) => {
        // console.warn("ITEM:", item);
        if (item.includes('Employment EMP3')) {
          // console.warn("Employment Current:", 'Employment ' + selectedEmployment);
          // Modify the string to replace 'Education UG' with the new education level
          return item.replace('Employment EMP3', 'Employment ' + selectedEmployment);
        }
        return item; // Return the unmodified string if it doesn't contain 'Education UG'
      });
      // console.log('After modification:', this.newProofList);
    }

  }

  previousIDLevel: string = '';

  updateIDDocuments(selectedIDItems: string, check: any) {
    // Log the previous and current education levels
    // console.log('Previous ID Level:', this.previousIDLevel);
    // console.log('Current ID Level:', selectedIDItems);

    if (this.previousIDLevel == 'Aadhar') {
      // this.educationUGSelectedFiles = [];
      this.idSelectedFiles.forEach((fileObj: any) => {
        if (fileObj.key.includes('ID Aadhar')) {
          // Modify the key to replace 'Education UG' with 'Education PG'
          fileObj.key = fileObj.key.replace('ID Aadhar', 'ID ' + selectedIDItems);
        }
      });
      // console.log('After modification: employmentSelectedFiles', this.idSelectedFiles);
      this.newProofList = this.newProofList.map((item: string) => {
        // console.warn("ITEM:", item);
        if (item.includes('ID Aadhar')) {
          // Modify the string to replace 'Education UG' with the new education level
          return item.replace('ID Aadhar', 'ID ' + selectedIDItems);
        }
        return item; // Return the unmodified string if it doesn't contain 'Education UG'
      });
      // console.log('After modification:', this.newProofList);
    }
    else if (this.previousIDLevel == 'PAN') {
      this.idSelectedFiles.forEach((fileObj: any) => {
        if (fileObj.key.includes('ID PAN')) {
          // Modify the key to replace 'Education UG' with 'Education PG'
          fileObj.key = fileObj.key.replace('ID PAN', 'ID ' + selectedIDItems);
        }
      });
      // console.log('After modification: employmentSelectedFiles', this.idSelectedFiles);
      this.newProofList = this.newProofList.map((item: string) => {
        // console.warn("ITEM:", item);
        if (item.includes('ID PAN')) {
          // Modify the string to replace 'Education UG' with the new education level
          return item.replace('ID PAN', 'ID ' + selectedIDItems);
        }
        return item; // Return the unmodified string if it doesn't contain 'Education UG'
      });
      // console.log('After modification:', this.newProofList);
    }
  }

  previousCriminalLevel: string = '';

  updateCriminalDocuments(selectedCriminalItems: string, check: any) {
    // Log the previous and current education levels
    // console.log('Previous ID Level:', this.previousCriminalLevel);
    // console.log('Current ID Level:', selectedCriminalItems);

    if (this.previousCriminalLevel == 'present') {
      // this.educationUGSelectedFiles = [];
      this.criminalSelectedFiles.forEach((fileObj: any) => {
        if (fileObj.key.includes('Criminal present')) {
          // Modify the key to replace 'Education UG' with 'Education PG'
          fileObj.key = fileObj.key.replace('Criminal present', 'Criminal ' + selectedCriminalItems);
        }
      });
      // console.log('After modification: criminalSelectedFiles', this.criminalSelectedFiles);
      this.newProofList = this.newProofList.map((item: string) => {
        // console.warn("ITEM:", item);
        if (item.includes('Criminal present')) {
          // Modify the string to replace 'Education UG' with the new education level
          return item.replace('Criminal present', 'Criminal ' + selectedCriminalItems);
        }
        return item; // Return the unmodified string if it doesn't contain 'Education UG'
      });
      // console.log('After modification:', this.newProofList);
    }
    else if(this.previousCriminalLevel == 'permanent'){
      this.criminalSelectedFiles.forEach((fileObj: any) => {
        if (fileObj.key.includes('Criminal permanent')) {
          // Modify the key to replace 'Education UG' with 'Education PG'
          fileObj.key = fileObj.key.replace('Criminal permanent', 'Criminal ' + selectedCriminalItems);
        }
      });
      // console.log('After modification: criminalSelectedFiles', this.criminalSelectedFiles);
      this.newProofList = this.newProofList.map((item: string) => {
        // console.warn("ITEM:", item);
        if (item.includes('Criminal permanent')) {
          // Modify the string to replace 'Education UG' with the new education level
          return item.replace('Criminal permanent', 'Criminal ' + selectedCriminalItems);
        }
        return item; // Return the unmodified string if it doesn't contain 'Education UG'
      });
      // console.log('After modification:', this.newProofList);

    }


  }

  removeCheck(index: number,checkName:any) {
    console.warn("checkName: ",checkName)
    console.warn("INdex : ", index)
    this.checks.splice(index, 1);
    if(checkName != ''){
      this.educationSelectedFiles = this.educationSelectedFiles.filter(file => !file.key.includes(checkName));
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
      selectedEmploymentSubType: ''
    });
  }

  addDocumentForEmployment(employmentCheck: any) {
    if (!employmentCheck.documents) {
      employmentCheck.documents = []; // Initialize documents array if it's not already initialized
    }
    employmentCheck.documents.push({
      EmploymentDoc: '', // Initialize employment document value
      selectedFiles: [] // Initialize an empty array to store selected files for this document
    });
  }

  employmentDocument: string[] = ['Offer Letter', 'Relieving Letter', 'Pay Slip', 'Experience Letter']


  addEmploymentDoc: { EmploymentDoc: string }[] = [];
  EmploymentDoc: string = '';
  addDocumnentForEmployment() {
    // console.warn("add Document Clicked !..")
    this.addEmploymentDoc.push({ EmploymentDoc: '' });
  }

  removeEmploymentCheck(index: number,checkName:any) {
    this.employmentChecks.splice(index, 1);
    if(checkName != ''){
      this.employmentSelectedFiles = this.employmentSelectedFiles.filter(file => !file.key.includes(checkName));
    }
    // this.employmentSelectedFiles = [];
  }
  removeIDCheck(index: number,checkName:any) {
    this.idChecks.splice(index, 1);
    if(checkName != ''){
      this.idSelectedFiles = this.idSelectedFiles.filter(file => !file.key.includes(checkName));
    }
    // this.idSelectedFiles = [];
  }
  removeCriminalCheck(index: number,checkName:any) {
    this.criminalCheck.splice(index, 1);
    if(checkName != ''){
      this.criminalSelectedFiles = this.criminalSelectedFiles.filter(file => !file.key.includes(checkName));
    }
    // this.criminalSelectedFiles = [];
  }
  removeAddressCheck(index: number) {
    this.addressCheck.splice(index, 1);
  }
  removeDatabaseCheck(index: number,checkName:any) {
    this.databaseCheck.splice(index, 1);
    if(checkName != ''){
      this.databaseSelectedFiles = this.databaseSelectedFiles.filter(file => !file.key.includes(checkName));
    }
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
    this.idChecks.push({
      id: '',
    });
  }

  //Criminal
  criminalCheck: any[] = [];
  criminalLevels: string[] = ['present', 'permanent']
  criminal: string = '';

  addCriminalCheck() {
    this.criminalCheck.push({
      criminal: ''
    })
  }

  //Address

  // addressCheck: any[] = [];
  addressCheck: { addressType: string; address: string; }[] = [];

  addressLevels: string[] = ['present', 'permanent']
  address: string = '';

  addAddressCheck() {
    this.addressCheck.push({
      addressType: '',
      address: '',
    })

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

  uploadFile(event: any, field: string, value: string, checkType: string) {
    // console.warn("ENVENT : ", event)
    // console.warn("value : ", value)
    // console.warn("CheckType : ", checkType)
    const files = event.target.files;
    const checkName: string = field;
    // console.log("CHECKNAME>>>>>>", checkName)
    let checkNameAndType = checkName + ' ' + value
    const fileCount = files.length;
    let multipleFileCount: number = 0;
    let multipleFileCount2: number = 0;

    // console.log("Number of files selected:", fileCount);
    this.selectedFiles = [];
    // this.educationSelectedFiles = [];
    // this.employmentSelectedFiles = [];
    const zip = new JSZip();
    for (let i = 0; i < files.length; i++) {
      // console.warn("fileCount : ", files.length)
      const file = files[i];
      if (checkName == 'Education') {
        let selectedFilesArray: { key: string, file: File }[] = [];;
        // console.warn("education selected files")
        // multipleFileCount = this.educationSelectedFiles.push(file);
        if (value.includes('UG')) {
          // console.warn("UG IS TRUE :::::::::::::::::::::")
          //  const educationUGSelectedFiles: { key: string, file: File }[] = [];
          multipleFileCount = this.educationUGSelectedFiles.push({ key: checkNameAndType, file });
          selectedFilesArray = this.educationUGSelectedFiles;
        } else if (value.includes('PG')) {
          // console.warn("PG IS TRUE :::::::::::::::::::::")
          multipleFileCount = this.educationPGSelectedFiles.push({ key: checkNameAndType, file });
          selectedFilesArray = this.educationPGSelectedFiles;
        } else if (value.includes('Diploma')) {
          // console.warn("DIPLOMA IS TRUE :::::::::::::::::::::")
          multipleFileCount = this.educationDiplomaSelectedFiles.push({ key: checkNameAndType, file });
          selectedFilesArray = this.educationDiplomaSelectedFiles;
        } else if (value.includes('10TH')) {
          // console.warn("10TH IS TRUE :::::::::::::::::::::")
          multipleFileCount = this.education10THSelectedFiles.push({ key: checkNameAndType, file });
          selectedFilesArray = this.education10THSelectedFiles;
        } else if (value.includes('12TH')) {
          // console.warn("10TH IS TRUE :::::::::::::::::::::")
          multipleFileCount = this.education12THSelectedFiles.push({ key: checkNameAndType, file });
          selectedFilesArray = this.education12THSelectedFiles;
        }
        multipleFileCount = this.educationSelectedFiles.push({ key: checkNameAndType + ' ' + checkType, file });
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
                // console.log(`Total files count: ${totalCount}`);
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
          selectedFilesArray = this.employmentEMP1SelectedFiles;
        } else if (value.includes('EMP2')) {
          // console.warn("EMP2 IS TRUE :::::::::::::::::::::")
          multipleFileCount = this.employmentEMP2SelectedFiles.push({ key: checkNameAndType, file });
          selectedFilesArray = this.employmentEMP2SelectedFiles;
        } else if (value.includes('EMP3')) {
          // console.warn("EMP3 IS TRUE :::::::::::::::::::::")
          multipleFileCount = this.employmentEMP3SelectedFiles.push({ key: checkNameAndType, file });
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
                console.log(`Total files count: ${totalCount}`);
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
                // console.log(`Total files count: ${totalCount}`);
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
                // console.log(`Total files count: ${totalCount}`);
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
        multipleFileCount = this.databaseSelectedFiles.push({ key: checkNameAndType, file });
        if (multipleFileCount > 1) {
          for (let i = 0; i < files.length; i++) {
            const file = files[i];
            const fileType = file.name.split('.').pop()?.toLowerCase();
            // console.warn("fileType ::", fileType)
            // Validate the file type
            if (fileType && ['pdf', 'png', 'jpg', 'jpeg', 'zip'].includes(fileType)) {
              // Add the file to the zip
              const databaseInput = document.querySelector('input[name="databaseFiles"]');
              if (databaseInput instanceof HTMLInputElement) {
                const remainingDatabaseFiles = new DataTransfer();
                for (const f of this.databaseSelectedFiles) {
                  remainingDatabaseFiles.items.add(f.file);
                }
                // Add the new files to the existing files in the input element
                const existingFilesCount = databaseInput.files ? databaseInput.files.length : 0;
                const newFilesCount = remainingDatabaseFiles.files.length;
                const totalCount = existingFilesCount + newFilesCount;

                // Set the input element's files to the combined list
                databaseInput.files = totalCount > 0 ? remainingDatabaseFiles.files : null;
                // console.log(`Total files count: ${totalCount}`);
              }
              console.warn("zip file converted : ")
              zip.file(file.name, file.file);
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

          console.warn("this.newProofList", this.newProofList)


        };
        reader.readAsArrayBuffer(zipFile);
      });
    }

    else {
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const fileType = file.name.split('.').pop()?.toLowerCase();

        const reader = new FileReader();

        // When the file is loaded, convert it to a byte array
        reader.onload = (e: any) => {
          // console.log("File contents:", e.target.result);
          const byteArray = new Uint8Array(e.target.result);

          // Convert Uint8Array to regular array
          const byteArrayAsArray = Array.from(byteArray);

          // Convert byte array to Base64 string
          this.base64 = this.arrayBufferToBase64(byteArray);

          // Optionally, if you want to store each proof separately as a string
          // const newProof = `${checkName}:${this.base64}`;
          const newProof = `${checkName + ' ' + value}:${this.base64}`;
          this.newProofList.push(newProof); // Add newProof string to the list

          // If all files have been processed, assign newProofList to this.newProof
          if (this.newProofList.length === files.length) {
            this.newProofList = this.newProofList;
          }

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
    }

  }


  removeFile(fileType: string, checkType: any, file: File, checkIndex: number, docIndex: number, fileIndex: number) {
    let selectedFilesArray: { key: string, file: File }[] = [];
    let selectedEMPFilesArray: { key: string, file: File }[] = [];
    // console.warn("checkIndex : ", checkIndex)
    // console.warn("docIndex : ", docIndex)
    // console.warn("fileIndex : ", fileIndex)

    if (fileType === 'education') {
      const checkName = "Education";
      const checkNameAndCheckType = checkName + ' ' + checkType
      // console.warn("checkNameAndCheckType : ", checkNameAndCheckType)
      // const index = this.educationSelectedFiles.indexOf({key:checkNameAndCheckType,file});
      // const index = this.educationSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
      let index = null;

      if (checkType.includes('UG')) {
        // console.warn("UG IS TRUE :::::::::::::::::::::")
        index = this.educationUGSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        // console.warn("INDEX: ", index)
        selectedFilesArray = this.educationUGSelectedFiles;
      } else if (checkType.includes('PG')) {
        // console.warn("PG IS TRUE :::::::::::::::::::::")
        index = this.educationPGSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedFilesArray = this.educationPGSelectedFiles;

      } else if (checkType.includes('Diploma')) {
        // console.warn("DIPLOMA IS TRUE :::::::::::::::::::::")
        index = this.educationDiplomaSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedFilesArray = this.educationDiplomaSelectedFiles;

      } else if (checkType.includes('10TH')) {
        // console.warn("10TH IS TRUE :::::::::::::::::::::")
        index = this.education10THSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedFilesArray = this.education10THSelectedFiles;

      } else if (checkType.includes('12TH')) {
        // console.warn("10TH IS TRUE :::::::::::::::::::::")
        index = this.education12THSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedFilesArray = this.education12THSelectedFiles;

      }

      if (index != null) {
        this.educationSelectedFiles.splice(fileIndex, 1);
        selectedFilesArray.splice(index, 1);

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
        index = this.employmentEMP1SelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedEMPFilesArray = this.employmentEMP1SelectedFiles;
      } else if (checkType.includes('EMP2')) {
        // console.warn("EMP2 IS TRUE :::::::::::::::::::::")
        index = this.employmentEMP2SelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedEMPFilesArray = this.employmentEMP2SelectedFiles;
      } else if (checkType.includes('EMP3')) {
        // console.warn("EMP3 IS TRUE :::::::::::::::::::::")
        index = this.employmentEMP3SelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
        selectedEMPFilesArray = this.employmentEMP3SelectedFiles;
      }
      if (index != null) {
        this.employmentSelectedFiles.splice(fileIndex, 1);
        selectedEMPFilesArray.splice(index, 1);
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
      // const index = this.criminalSelectedFiles.indexOf(file);
      const index = this.criminalSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
      if (index != null) {
        this.criminalSelectedFiles.splice(index, 1);
        const employmentInput = document.querySelector('input[name="criminalFiles"]');
        if (employmentInput instanceof HTMLInputElement) {
          const remainingEmploymentFiles = new DataTransfer();
          for (const f of this.criminalSelectedFiles) {
            remainingEmploymentFiles.items.add(f.file);
          }
          employmentInput.files = remainingEmploymentFiles.files;
        }
        // this.removeAndUpdateDocument(this.criminalSelectedFiles, checkName)
        this.removeAndUpdateDocument(this.criminalSelectedFiles.map(item => item.file), checkNameAndCheckType);
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
      // console.warn("checkNameAndCheckType : ", checkNameAndCheckType)
      // const index = this.idSelectedFiles.indexOf(file);
      const index = this.idSelectedFiles.findIndex(item => item.key === checkNameAndCheckType && item.file === file);
      if (index != null) {
        this.idSelectedFiles.splice(index, 1);
        // console.warn("this.idSelectedFiles : ", this.idSelectedFiles.length)
        const employmentInput = document.querySelector('input[name="idFiles"]');
        if (employmentInput instanceof HTMLInputElement) {
          const remainingEmploymentFiles = new DataTransfer();
          for (const f of this.idSelectedFiles) {
            remainingEmploymentFiles.items.add(f.file);
          }
          employmentInput.files = remainingEmploymentFiles.files;
        }
        // this.removeAndUpdateDocument(this.idSelectedFiles, checkName)
        this.removeAndUpdateDocument(this.idSelectedFiles.map(item => item.file), checkNameAndCheckType);
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
        this.databaseSelectedFiles.splice(index, 1);
        const employmentInput = document.querySelector('input[name="databaseFiles"]');
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
    if (file.length >= 1) {
      const zip = new JSZip();
      const files = file;

      // console.warn("fileLength removeAndUpdateDocument : ", files.length)
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
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
          const index = this.newProofList.findIndex(entry => entry.startsWith(`${checkName}:`));

          if (index !== -1) {
            // If an entry with the same checkName already exists, overwrite it
            this.newProofList[index] = newProof;
          } else {
            // Otherwise, add the newProof string to the list
            this.newProofList.push(newProof);
          }

          // If all files have been processed, assign newProofList to this.newProof
          // console.warn("this.newProofList.length : ", this.newProofList.length)
          this.newProofList.length === files.length;
          // console.warn("files.length : ", files.length)
          if (this.newProofList.length === files.length) {
            this.newProofList = this.newProofList;
          }

          if (this.newProofList.length !== files.length) {
            // If files.length is reduced, update this.newProofList accordingly
            this.newProofList = this.newProofList.slice(0, files.length);
          }

          // console.warn("Updated this.newProofList:", this.newProofList);
          // console.warn("Emove functional this.newProofList", this.newProofList)


        };
        reader.readAsArrayBuffer(zipFile);
      });

    }
  }


  submitConventionalCandidateForm() {
    // console.log("FORM>>>", this.candidateForm.value)

    // console.warn(" this.checks.push : ", this.checks)
    this.employmentChecks.forEach(item => {
      // Add selectedEmploymentSubType value to Employment property
      item.Employment += ` (${item.selectedEmploymentSubType})`;

      // Remove selectedEmploymentSubType property
      delete item.selectedEmploymentSubType;
    });
    // console.warn(" this.employment.push : ", this.employmentChecks)
    // console.warn(" this.id.push : ", this.idChecks)
    // console.warn(" this.criminalCheck.push : ", this.criminalCheck)
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

    const result = { jsonArray }; // Wrapping jsonArray inside an object
    // console.log("RESULT: ", result);

    // console.log(JSON.stringify(jsonArray));

    // Convert the object into a string representation
    const mergedString = Object.values(mergedObject).join(', ');
    // const mergedString = `{${Object.values(mergedObject).join(', ')}}`;

    // console.log(`{${mergedString}}`);
    // console.log(mergedObject);

    const jsonArrayString = JSON.stringify(jsonArray);

    // console.log("jsonStriong", jsonArrayString);

    // const mergedArray = [jsonArrayString, mergedString];

    // const finalString = JSON.stringify(mergedArray);
    // console.log("final String",finalString);


    const mergedString2 = JSON.stringify(mergedString);

    console.warn("mergedObject2 : ", mergedString2)

    const attributeaAndValues = this.agentAttributeListForm.filter(item => item.value !== null && item.value.trim() !== '').reduce((obj, item) => {


      if (item.value === null || item.value.trim() === '') {
        console.warn("ITEMS >>>>>>", item.value)
        return false; // Return false if any item.value is null or empty
      }

      obj[item.label] = item.value;

      console.log("ITEM >>>>>>", item);
      console.log("OBJ >>>>>>", obj);

      return obj;

    }, {});

    console.warn("educationAttributeValues?>>", attributeaAndValues)

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
      console.log(key);
    });


    const keysFromNewProofList = this.newProofList.map(item => item.split(':')[0]);

    // Log keys from this.newProofList
    console.log("Keys (Labels) from newProofList:");
    keysFromNewProofList.forEach(key => {
      console.log(key);
    });

    console.log("Matching elements from Checks:");
    this.Checks.forEach((check: any) => {
      if (keysFromNewProofList.includes(check)) {
        console.log("checks ::: ", check);
      }
    });


    const allMatched = this.Checks.every((check: any) => keysFromNewProofList.includes(check));
    console.log("All matched:", allMatched);

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

}
