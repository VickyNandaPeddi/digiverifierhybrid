package com.aashdit.digiverifier.config.candidate.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.aashdit.digiverifier.common.enums.ContentViewType;
import com.aashdit.digiverifier.common.service.ContentService;
import com.aashdit.digiverifier.config.superadmin.Enum.ReportType;
import com.aashdit.digiverifier.config.superadmin.dto.ReportSearchDto;
import com.aashdit.digiverifier.config.superadmin.model.Color;
import com.aashdit.digiverifier.config.superadmin.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.candidate.dto.ApprovalStatusRemarkDto;
import com.aashdit.digiverifier.config.candidate.dto.CallBackStatusSendDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateCafAddressDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateCafEducationDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateCafExperienceDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidationApplicationFormDto;
import com.aashdit.digiverifier.config.candidate.dto.ConventionalCandidateDTO;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.CandidateCafAddress;
import com.aashdit.digiverifier.config.candidate.model.CandidateCafExperience;
import com.aashdit.digiverifier.config.candidate.model.CandidateVerificationState;
import com.aashdit.digiverifier.config.candidate.model.QualificationMaster;
import com.aashdit.digiverifier.config.candidate.model.SuspectClgMaster;
import com.aashdit.digiverifier.config.candidate.model.SuspectEmpMaster;
import com.aashdit.digiverifier.config.candidate.service.CandidateService;
import com.aashdit.digiverifier.config.candidate.service.CandidateServiceImpl;
import com.aashdit.digiverifier.config.candidate.dto.CandidateDetailsDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateReportDTO;
import com.aashdit.digiverifier.config.admin.dto.UserDto;
import com.aashdit.digiverifier.config.admin.model.VendorChecks;
import com.aashdit.digiverifier.config.admin.service.UserService;

//import io.swagger.annotations.ApiOperation;

@Slf4j
@RestController
@RequestMapping(value = "/api/allowAll")
public class CandidateApplicationFormController {
	
	@Autowired
	private CandidateService candidateService;
	
	@Autowired
	private ReportService reportService;
	
	@Autowired
	private UserService userService;
	
	
	@Autowired
	private ContentService contentService;
	
	@Operation(summary ="Get Qualification List")
	@GetMapping("/getQualificationList")
	public ResponseEntity<ServiceOutcome<List<QualificationMaster>>> getQualificationList() {
		ServiceOutcome<List<QualificationMaster>> svcSearchResult = candidateService.getQualificationList();
		return new ResponseEntity<ServiceOutcome<List<QualificationMaster>>>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="Password updation")
	@PostMapping(path = "/agentCreatepasswrd")
	public ResponseEntity<ServiceOutcome<UserDto>> agentCreatepasswrd(@RequestBody UserDto user,@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<UserDto> svcSearchResult = candidateService.agentCreatepasswrd(user);
		return new ResponseEntity<ServiceOutcome<UserDto>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Get all Candidate Application form details")
	@GetMapping("/candidateApplicationFormDetails/{candidateCode}")
	public ResponseEntity<ServiceOutcome<?>> candidateApplicationFormDetails(@PathVariable("candidateCode")String candidateCode) {
		ServiceOutcome<CandidationApplicationFormDto> svcSearchResult= candidateService.candidateApplicationFormDetails(candidateCode);
		return new ResponseEntity<ServiceOutcome<?>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Save And Update Education Details")
	@PostMapping(value = "/saveNUpdateEducation",consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<ServiceOutcome<Boolean>> saveNUpdateCandidateEducation(@RequestParam String candidateCafEducation
			,@RequestParam(value = "file", required = false)  MultipartFile certificate) {
		ServiceOutcome<Boolean> svcSearchResult=candidateService.saveNUpdateCandidateEducation(candidateCafEducation,certificate);
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);

	}
	
	@Operation(summary ="Get Education By specific Candidate Education Id")
	@GetMapping("/getCandidateEducationById/{candidateCafEducationId}")
	public ResponseEntity<ServiceOutcome<CandidateCafEducationDto>> getCandidateEducationById(@PathVariable Long candidateCafEducationId) {
		ServiceOutcome<CandidateCafEducationDto> svcSearchResult = candidateService.getCandidateEducationById(candidateCafEducationId);
		return new ResponseEntity<ServiceOutcome<CandidateCafEducationDto>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Save And Update Candidate Experience Details")
	@PostMapping(path = "/saveNUpdateCandidateExperience")
	public ResponseEntity<ServiceOutcome<Boolean>> saveNUpdateCandidateExperience(@RequestParam String candidateCafExperience,
			   @RequestParam(value = "file", required = false)  MultipartFile certificate) {
		ServiceOutcome<Boolean> svcSearchResult=candidateService.saveNUpdateCandidateExperience(candidateCafExperience,certificate);
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);

	}
	
	@Operation(summary ="Get Experience By specific Candidate Experience Id")
	@GetMapping("/getCandidateExperienceById/{candidateCafExperienceId}")
	public ResponseEntity<ServiceOutcome<CandidateCafExperienceDto>> getCandidateExperienceById(@PathVariable Long candidateCafExperienceId) {
		ServiceOutcome<CandidateCafExperienceDto> svcSearchResult = candidateService.getCandidateExperienceById(candidateCafExperienceId);
		return new ResponseEntity<ServiceOutcome<CandidateCafExperienceDto>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Save Candidate Application form")
	@PostMapping(path = "/saveCandidateApplicationForm")
	public ResponseEntity<ServiceOutcome<Boolean>> saveCandidateApplicationForm(@RequestParam(value = "candidateCafEducationDto", required = false) String candidateCafEducation,
			@RequestParam(value = "candidateCafAddressDto", required = false) JSONArray candidateCafAddress,@RequestParam(value = "resume", required = false)  MultipartFile resume,@RequestParam String candidateCode) {
		ServiceOutcome<Boolean> svcSearchResult=candidateService.saveCandidateApplicationForm(candidateCafEducation,candidateCafAddress,resume,candidateCode);
		System.out.println(candidateCode+"---------------");
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);

	}
	
	@Operation(summary =" Get All Suspect College List")
	@GetMapping("/getAllSuspectClgList")
	public ResponseEntity<ServiceOutcome<List<SuspectClgMaster>>> getAllSuspectClgList() {
		ServiceOutcome<List<SuspectClgMaster>> svcSearchResult = candidateService.getAllSuspectClgList();
		return new ResponseEntity<ServiceOutcome<List<SuspectClgMaster>>>(svcSearchResult, HttpStatus.OK);
	}
	
	// @Operation(summary =" Get All Suspect College List")
	// @GetMapping("/getAllSuspectEmpList")
	// public ResponseEntity<ServiceOutcome<List<SuspectEmpMaster>>> getAllSuspectEmpList() {
	// 	ServiceOutcome<List<SuspectEmpMaster>> svcSearchResult = candidateService.getAllSuspectEmpList();
	// 	return new ResponseEntity<ServiceOutcome<List<SuspectEmpMaster>>>(svcSearchResult, HttpStatus.OK);
	// }
	
	@Operation(summary ="Candidate Address verification")
	@PostMapping("/relationshipAddressVerification")
	public ResponseEntity<ServiceOutcome<Boolean>> relationshipAddressVerification(
			@RequestParam String candidateCafRelation,
			@RequestParam(value="file", required = false) MultipartFile  document) {
		
		ServiceOutcome<Boolean> svcSearchResult = candidateService.relationshipAddressVerification(candidateCafRelation,document);
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Set A Candidate is fresher or experienced")
	@PostMapping("/isFresher")
	public ResponseEntity<ServiceOutcome<Candidate>> isFresher(@RequestParam String  candidateCode,@RequestParam Boolean isFresher) {
		ServiceOutcome<Candidate> svcSearchResult = candidateService.saveIsFresher(candidateCode,isFresher);
		return new ResponseEntity<ServiceOutcome<Candidate>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Update Experience Data")
	@PostMapping("/updateExperience")
	public ResponseEntity<ServiceOutcome<CandidateCafExperience>> updateExperience(@RequestHeader("Authorization") String authorization,@RequestBody CandidateCafExperienceDto candidateCafExperienceDto) {
		ServiceOutcome<CandidateCafExperience> svcSearchResult=  candidateService.updateCandidateExperience(candidateCafExperienceDto);
		return new ResponseEntity<ServiceOutcome<CandidateCafExperience>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Save candidate address")
	@PostMapping("/saveCandidateAddress")
	public ResponseEntity<ServiceOutcome<CandidateCafAddress>> saveCandidateAddress(@RequestBody CandidateCafAddressDto candidateCafAddressDto,@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<CandidateCafAddress> svcSearchResult = candidateService.saveCandidateAddress(candidateCafAddressDto);
		return new ResponseEntity<ServiceOutcome<CandidateCafAddress>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Get Service Config from candidate for customer")
	@GetMapping("/getServiceConfigCodes/{candidateCode}")
	public ResponseEntity<ServiceOutcome<?>> getServiceConfigForCandidate(@PathVariable("candidateCode")String candidateCode) {
		ServiceOutcome<List<String>> svcSearchResult= candidateService.getServiceConfigCodes(candidateCode,null);
		return new ResponseEntity<ServiceOutcome<?>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Generate interim report")
	@GetMapping("/generateInterimReport/{candidateCode}")
	public ResponseEntity<ServiceOutcome<?>> generateInterimReport(@PathVariable("candidateCode")String candidateCode) throws FileNotFoundException, IOException {
		ServiceOutcome<String> svcSearchResult= candidateService.generateInterimReport(candidateCode);
		return new ResponseEntity<ServiceOutcome<?>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Set A Candidate if uan skipped or not")
	@PostMapping("/isUanSkipped")
	public ResponseEntity<ServiceOutcome<Candidate>> isUanSkipped(@RequestParam String  candidateCode,@RequestParam String isUanSkipped) {
		ServiceOutcome<Candidate> svcSearchResult = candidateService.saveIsUanSkipped(candidateCode,isUanSkipped);
		return new ResponseEntity<ServiceOutcome<Candidate>>(svcSearchResult, HttpStatus.OK);
	}
	
	@GetMapping(value = "/report")
	public ResponseEntity getReport(@RequestParam("Authorization") String authorization,@RequestParam String candidateCode,@RequestParam
		ReportType type,@RequestParam String overrideReportStatus) {
		ServiceOutcome svcSearchResult = new ServiceOutcome();
		ServiceOutcome<CandidateReportDTO> data = reportService.generateDocument(candidateCode, authorization, type,overrideReportStatus);
		svcSearchResult.setData(data.getMessage());
		svcSearchResult.setStatus(data.getStatus());
		svcSearchResult.setOutcome(data.getOutcome());
		svcSearchResult.setMessage(String.valueOf(data.getData().getCandidate_reportType()));
		return new ResponseEntity<ServiceOutcome<ReportSearchDto>>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="qcpending status set")
	@PostMapping(path = "/qcPendingstatus/{candidateCode}")
	public ResponseEntity<ServiceOutcome<Boolean>> qcPendingstatus(@PathVariable("candidateCode")String candidateCode) {
		ServiceOutcome<Boolean> svcSearchResult=candidateService.qcPendingstatus(candidateCode);
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);

	}

	@Operation(summary ="Get all Candidate digilocker details")
	@GetMapping("/candidateDLdata/{candidateCode}")
	public ResponseEntity<ServiceOutcome<?>> candidateDLdata(@RequestHeader("Authorization") String authorization,@PathVariable("candidateCode")String candidateCode) {
		System.out.println("entry for dl data============================"+authorization);
		ServiceOutcome<CandidateDetailsDto> svcSearchResult= candidateService.candidateDLdata(candidateCode);
		return new ResponseEntity<ServiceOutcome<?>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Get Candidate by Reference No")
	@GetMapping("/getCandidateDetails/{referenceNo}")
	public ResponseEntity<ServiceOutcome<CandidateDetailsDto>> getCandidateByCandidateCode(@PathVariable("referenceNo") String referenceNo) {
		ServiceOutcome<CandidateDetailsDto> svcSearchResult = candidateService.getCandidateByCandidateCode(referenceNo);
		return new ResponseEntity<ServiceOutcome<CandidateDetailsDto>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Get Candidate Report Status")
	@GetMapping("/getCandidateReportStatus/{candidateCode}")
	public ResponseEntity<ServiceOutcome<?>> getCandidateReportStatus(@RequestHeader("Authorization") String authorization,@PathVariable("candidateCode")String candidateCode) {
		System.out.println("entry for dl data============================"+authorization);
		ServiceOutcome<Color> serviceOutcome= candidateService.getCandidateReportStatus(candidateCode);
		return new ResponseEntity<ServiceOutcome<?>>(serviceOutcome, HttpStatus.OK);
		}
	
	@Operation(summary ="Update candidate Report Status")
	@PostMapping(path = "/updateReportStatus")
	public ResponseEntity<ServiceOutcome<String>> updateCandidateReportStatus(@RequestHeader("Authorization") String authorization,@RequestParam("candidateCode")String candidateCode
			,@RequestParam(name="candidateStatusColorId", required = false)Long candidateStatusColorId) {
		System.out.println("entry for candidateStatusColorId============================"+candidateStatusColorId);
		ServiceOutcome<String> serviceOutcome= candidateService.updateCandidateReportStatus(candidateCode,candidateStatusColorId);
		return new ResponseEntity<ServiceOutcome<String>>(serviceOutcome, HttpStatus.OK);
		}
	
//	@Operation(summary ="To purge old candidates")
//	@GetMapping("/oldCandidatesPurge/{orgId}")
//	public ResponseEntity<ServiceOutcome<List<Long>>> oldCandidatesPurge(@RequestHeader("Authorization") String authorization,
//			@PathVariable("orgId")Long orgId) {
//		ServiceOutcome<List<Long>> purgedCandidatesList =candidateService.oldCandidatesPurge(orgId);
//		return new ResponseEntity<ServiceOutcome<List<Long>>>(purgedCandidatesList, HttpStatus.OK);
//	}
	
	@Operation(summary ="Save And Update Candidate Experience Details IN candidate form CWF")
	@PostMapping(path = "/updateCandidateExperienceInCForm")
	public ResponseEntity<ServiceOutcome<Boolean>> updateCandidateExperienceInCForm(@RequestParam String candidateCafExperience,
			   @RequestParam(value = "file", required = false)  MultipartFile certificate) {
		ServiceOutcome<Boolean> svcSearchResult =candidateService.updateCandidateExperienceInCForm(candidateCafExperience, certificate);
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="candidate deleteexp Id")
	@PutMapping("/deletecandidateExpInCForm/{id}")
	public ResponseEntity<ServiceOutcome<CandidateCafExperience>> deletecandidateExpInCForm(@PathVariable("id") Long id) {
		
		ServiceOutcome<CandidateCafExperience> svcSearchResult = candidateService.deletecandidateExpById(id);
		return new ResponseEntity<ServiceOutcome<CandidateCafExperience>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="To Decline Letter of authorization")
	@GetMapping("/declineAuthLetter/{candidateCode}")
	public ResponseEntity<ServiceOutcome<Boolean>> declineAuthLetter(@PathVariable("candidateCode") String candidateCode) {
		ServiceOutcome<Boolean> declineAuthLetterRes =candidateService.declineAuthLetter(candidateCode);
		return new ResponseEntity<ServiceOutcome<Boolean>>(declineAuthLetterRes, HttpStatus.OK);
	}
	
	@Operation(summary ="Get Content Letter of authorization")
	@GetMapping("/getAuthLetterContent/{candidateCode}")
	public ResponseEntity<ServiceOutcome<String>> getAuthLetterContent(@PathVariable("candidateCode") String candidateCode) {
		ServiceOutcome<String> declineAuthLetterRes =candidateService.getAuthLetterContent(candidateCode);
		return new ResponseEntity<ServiceOutcome<String>>(declineAuthLetterRes, HttpStatus.OK);
	}
	
	@Operation(summary ="Dummy CALL BACK Api for sending the candidate status to client")
	@PostMapping(path = "/callBackStatusToClient")
	public ResponseEntity<ServiceOutcome<Boolean>> callBackStatusToClient(@RequestBody CallBackStatusSendDto callBackStatusSendDto) {
		ServiceOutcome<Boolean> svcSearchResult =new ServiceOutcome<>();
		
		if(callBackStatusSendDto!=null && callBackStatusSendDto.getStatus()!=null && !callBackStatusSendDto.getStatus().isEmpty()) {
			log.info("CAll BACK STATUS OF THE CANDIDATE::{}",callBackStatusSendDto.toString());
			svcSearchResult.setData(true);
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage("Status Recieved And Stored Successfully..");
			svcSearchResult.setStatus("Success");
		}
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Perform Adjudication after Application form submit")
	@GetMapping("/candidateApplicationFormSubmit/{candidateCode}")
	public ResponseEntity<ServiceOutcome<?>> candidateApplicationFormSubmit(@PathVariable("candidateCode")String candidateCode) {
		ServiceOutcome<String> svcSearchResult= candidateService.candidateApplicationFormSubmit(candidateCode);
		return new ResponseEntity<ServiceOutcome<?>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Get Service Config from OrgId for customer")
	@GetMapping("/getServiceConfigForOrg/{orgId}")
	public ResponseEntity<ServiceOutcome<?>> getServiceConfigForOrg(@PathVariable("orgId")Long orgId) {
		ServiceOutcome<List<String>> svcSearchResult= candidateService.getServiceConfigCodes(null,orgId);
		return new ResponseEntity<ServiceOutcome<?>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Save And Update vendordashboard Information")
	@PostMapping(path = "/conventionalcandidate")
	public ResponseEntity<ServiceOutcome<VendorChecks>> saveConventionalCandidateChecks(@RequestParam String vendorchecks,
			@RequestParam(value = "file", required = false) String proofDocumentNew, @RequestParam String addressCheck) {
		System.out.println(vendorchecks + "***************************************************");

		
			if(proofDocumentNew != null) {
//				for (Map.Entry<String, String> entry : proofDocumentNew.entrySet()) {
//		            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
//		        }
//				System.out.println("proofDocumentNew>>"+proofDocumentNew);
			}
			 else {
//			    System.out.println("No proof documents uploaded");
			}
		
		
		ServiceOutcome<VendorChecks> svcSearchResult = userService.saveConventionalCandidateChecks(vendorchecks,
				proofDocumentNew,addressCheck);
		
		return new ResponseEntity<ServiceOutcome<VendorChecks>>(svcSearchResult, HttpStatus.OK);

	}
}
