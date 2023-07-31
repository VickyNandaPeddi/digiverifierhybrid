package com.aashdit.digiverifier.config.candidate.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aashdit.digiverifier.common.enums.ContentViewType;
import com.aashdit.digiverifier.common.service.ContentService;
import com.aashdit.digiverifier.config.superadmin.Enum.ReportType;
import com.aashdit.digiverifier.config.superadmin.dto.ReportSearchDto;
import com.aashdit.digiverifier.config.superadmin.service.ReportService;
import com.aashdit.digiverifier.epfo.dto.EpfoDataFromDetailsDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.CandidateCafEducation;
import com.aashdit.digiverifier.config.candidate.model.CandidateCafExperience;
import com.aashdit.digiverifier.config.candidate.model.OrganisationScope;
import com.aashdit.digiverifier.config.candidate.dto.DigidocDetailsDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateCaseDetailsDTO;
import com.aashdit.digiverifier.config.candidate.model.SuspectEmpMaster;
import com.aashdit.digiverifier.config.candidate.model.UanSearchData;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.UanSearchDataRepository;
import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.candidate.dto.ApprovalStatusRemarkDto;
import com.aashdit.digiverifier.config.candidate.dto.BulkUanDTO;
import com.aashdit.digiverifier.config.candidate.dto.CandidateApprovalDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateDetailsDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateInvitationSentDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidationApplicationFormDto;
import com.aashdit.digiverifier.config.candidate.dto.DeleteRequestDTO;
import com.aashdit.digiverifier.config.admin.dto.VendorUploadChecksDto;
import com.aashdit.digiverifier.config.candidate.model.RemarkMaster;
import com.aashdit.digiverifier.config.candidate.model.StatusMaster;
import com.aashdit.digiverifier.config.candidate.service.CandidateService;
import com.aashdit.digiverifier.config.superadmin.dto.DashboardDto;
import com.aashdit.digiverifier.config.candidate.dto.SuspectEmpMasterDto;
import com.aashdit.digiverifier.config.candidate.dto.UanSearchDataDTO;
import com.aashdit.digiverifier.config.candidate.dto.UanSearchEpfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.aashdit.digiverifier.common.model.Content;



@RestController
@RequestMapping(value = "/api/candidate")
@Slf4j
public class CandidateController {
	
	@Autowired
	private CandidateService candidateService;
	
	@Autowired
	private ReportService reportService;
	
	@Autowired @Lazy
	private ContentService contentService;
	
	@Autowired
	private CandidateRepository candidateRepository;
	
	@Autowired
	private UanSearchDataRepository uanSearchDataRepository;
	
	// @ApiOperation("Upload Candidate Information file CSV Or XLS")
	// @PostMapping("/uploadCandidate")
	// public ResponseEntity<ServiceOutcome<List>> uploadCandidateFile(@RequestParam("file")MultipartFile file,@RequestHeader("Authorization") String authorization){
	// 	ServiceOutcome<List> svcSearchResult = candidateService.saveCandidateInformation(file);
	// 	return new ResponseEntity<ServiceOutcome<List>>(svcSearchResult, HttpStatus.OK);
	// }
	
	@ApiOperation("suspect Emp Master check by company Name")
	@GetMapping("/suspectEmpMasterCheck/{companyName}/{oganizationId}")
	public ResponseEntity<ServiceOutcome<String>> suspectEmpMasterCheck(@PathVariable("companyName") String companyName, @PathVariable("oganizationId")  Long oganizationId, @RequestHeader("Authorization") String authorization) {
		ServiceOutcome<String> svcSearchResult = candidateService.suspectEmpMasterCheck(companyName, oganizationId);
		return new ResponseEntity<ServiceOutcome<String>>(svcSearchResult, HttpStatus.OK);
	}

	@ApiOperation("Upload Candidate Information file CSV Or XLS")
	@PostMapping("/uploadCandidate")
	public ResponseEntity<ServiceOutcome<List>> uploadCandidateFile(@RequestParam("file")MultipartFile file,@RequestHeader("Authorization") String authorization){
		ServiceOutcome<List> svcSearchResult = candidateService.saveCandidateInformation(file);
		return new ResponseEntity<ServiceOutcome<List>>(svcSearchResult, HttpStatus.OK);
	}

	
	
	@ApiOperation("Get all Candidate Information")
	@RequestMapping(value = "/candidateList", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<ServiceOutcome<DashboardDto>> getCandidateList(@RequestHeader("Authorization") String authorization,@RequestBody DashboardDto dashboardDto) {
		ServiceOutcome<DashboardDto> svcSearchResult= candidateService.getAllCandidateList(dashboardDto);
		return new ResponseEntity<ServiceOutcome<DashboardDto>>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation("Get CandidateList Status And Count")
	@RequestMapping(value = "/getCandidateStatusAndCount", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<ServiceOutcome<DashboardDto>> getCandidateStatusAndCount(@RequestHeader("Authorization") String authorization,@RequestBody DashboardDto dashboardDto) {
		ServiceOutcome<DashboardDto> svcSearchResult=  candidateService.getCandidateStatusAndCount(dashboardDto);
		return new ResponseEntity<ServiceOutcome<DashboardDto>>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation("Send Email For Candidate")
	@PostMapping("/invitationSent")
	public ResponseEntity<ServiceOutcome<Boolean>> invitationSent(@RequestBody CandidateInvitationSentDto candidateInvitationSentDto,@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<Boolean> svcSearchResult = candidateService.invitationSent(candidateInvitationSentDto);
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation("Get Candidate by Reference No")
	@GetMapping("/getCandidate/{referenceNo}")
	public ResponseEntity<ServiceOutcome<CandidateDetailsDto>> getCandidate(@PathVariable("referenceNo") String referenceNo,@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<CandidateDetailsDto> svcSearchResult = candidateService.getCandidateByCandidateCode(referenceNo);
		return new ResponseEntity<ServiceOutcome<CandidateDetailsDto>>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation("Candidate UPDATE by Reference No")
	@PutMapping("/updateCandidate")
	public ResponseEntity<ServiceOutcome<CandidateDetailsDto>> updateCandidate(@RequestBody CandidateDetailsDto candidateDetails,@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<CandidateDetailsDto> svcSearchResult = candidateService.updateCandidate(candidateDetails);
		return new ResponseEntity<ServiceOutcome<CandidateDetailsDto>>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation("Candidate Cancel by Reference No")
	@PutMapping("/cancelCandidate/{referenceNo}")
	public ResponseEntity<ServiceOutcome<Boolean>> cancelCandidate(@PathVariable("referenceNo") String referenceNo,@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<Boolean> svcSearchResult = candidateService.cancelCandidate(referenceNo);
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation("Get Report Delivery Details Status And Count")
	@RequestMapping(value = "/getReportDeliveryDetailsStatusAndCount", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<ServiceOutcome<DashboardDto>> getReportDeliveryDetailsStatusAndCount(@RequestHeader("Authorization") String authorization,@RequestBody DashboardDto dashboardDto) {
		ServiceOutcome<DashboardDto> svcSearchResult = candidateService.getReportDeliveryDetailsStatusAndCount(dashboardDto);
		return new ResponseEntity<ServiceOutcome<DashboardDto>>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation("Get all Candidate Application form details")
	@GetMapping("/candidateApplicationFormDetails/{candidateCode}")
	public ResponseEntity<ServiceOutcome<?>> candidateApplicationFormDetails(@PathVariable("candidateCode")String candidateCode) {
		ServiceOutcome<CandidationApplicationFormDto> svcSearchResult= candidateService.candidateApplicationFormDetailsExceptCandidate(candidateCode);
		return new ResponseEntity<ServiceOutcome<?>>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation(" Get All Remark")
	@GetMapping("/getAllRemark/{remarkType}")
	public ResponseEntity<?> getAllRemark(@RequestHeader("Authorization") String authorization,@PathVariable("remarkType")String remarkType) {
		ServiceOutcome<List<RemarkMaster>> svcSearchResult = candidateService.getAllRemark(remarkType);
		return new ResponseEntity<>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation("Candidate education UPDATE")
	@PutMapping("/updateCandidateEducationStatusAndRemark")
	public ResponseEntity<ServiceOutcome<Boolean>> updateCandidateEducationStatusAndRemark(@RequestBody ApprovalStatusRemarkDto approvalStatusRemarkDto,@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<Boolean> svcSearchResult = candidateService.updateCandidateEducationStatusAndRemark(approvalStatusRemarkDto);
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation("Candidate Experience Status And Remark")
	@PutMapping("/updateCandidateExperienceStatusAndRemark")
	public ResponseEntity<ServiceOutcome<Boolean>> updateCandidateExperienceStatusAndRemark(@RequestBody ApprovalStatusRemarkDto approvalStatusRemarkDto,@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<Boolean> svcSearchResult = candidateService.updateCandidateExperienceStatusAndRemark(approvalStatusRemarkDto);
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation("Candidate Experience Status And Remark")
	@PutMapping("/updateCandidateAddressStatusAndRemark")
	public ResponseEntity<ServiceOutcome<Boolean>> updateCandidateAddressStatusAndRemark(@RequestBody ApprovalStatusRemarkDto approvalStatusRemarkDto,@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<Boolean> svcSearchResult = candidateService.updateCandidateAddressStatusAndRemark(approvalStatusRemarkDto);
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation("Candidate Application form Approved")
	@PutMapping(value="/candidateApplicationFormApproved", consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<ServiceOutcome<?>> candidateApplicationFormApproved(@RequestParam String candidateReportApproval,
			@RequestParam String candidateCode,
			@RequestParam(value="criminalVerificationDocument",required = false)  MultipartFile criminalVerificationDocument,
			@RequestParam(value="globalDatabseCaseDetailsDocument",required = false)  MultipartFile globalDatabseCaseDetailsDocument,
			@RequestHeader("Authorization") String authorization,
			@RequestParam String reportType) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		try {
			CandidateApprovalDto candidateApprovalDto  = new ObjectMapper().readValue(candidateReportApproval, CandidateApprovalDto.class);
			svcSearchResult = candidateService.candidateApplicationFormApproved(candidateCode,criminalVerificationDocument,candidateApprovalDto.getCriminalVerificationColorId(),globalDatabseCaseDetailsDocument,candidateApprovalDto.getGlobalDatabseCaseDetailsColorId(), reportType);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return new ResponseEntity<ServiceOutcome<?>>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation("Upload Candidate Fake Company List Xls")
	@PostMapping("/uploadFakeCompanyDetails")
	public ResponseEntity<ServiceOutcome<Boolean>> uploadFakeCompanyDetails(@RequestParam("file")MultipartFile file,@RequestParam Long organizationId,@RequestHeader("Authorization") String authorization){
		ServiceOutcome<Boolean> svcSearchResult=new ServiceOutcome<Boolean>();
		try {
    		  svcSearchResult=candidateService.saveFakeCompanyDetails(file,organizationId);
    		  return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
	      } catch (Exception e) {
	    	log.error("Exception occured in uploadFakeCompanyDetails method in CandidateController-->" , e);
	    	 return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.EXPECTATION_FAILED);
	      }
	}
	
	@ApiOperation("Upload Candidate Fake College List Xls")
	@PostMapping("/uploadFakeCollegeDetails")
	public ResponseEntity<ServiceOutcome<Boolean>> uploadFakeCollegeDetails(@RequestParam("file")MultipartFile file,@RequestHeader("Authorization") String authorization){
		ServiceOutcome<Boolean> svcSearchResult=new ServiceOutcome<Boolean>();
		try {
    		  svcSearchResult=candidateService.saveFakeCollegeDetails(file);
    		  return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
	      } catch (Exception e) {
	    	log.error("Exception occured in uploadFakeCollegeDetails method in CandidateController-->" , e);
	    	 return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.EXPECTATION_FAILED);
	      }
	}
	
	@ApiOperation("View Pending Details Status And Count For Dashboard")
	@RequestMapping(value = "/getPendingDetailsStatusAndCount", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<ServiceOutcome<DashboardDto>> getPendingDetailsStatusAndCount(@RequestHeader("Authorization") String authorization,@RequestBody DashboardDto dashboardDto) {
		ServiceOutcome<DashboardDto> svcSearchResult=  candidateService.getPendingDetailsStatusAndCount(dashboardDto);
		return new ResponseEntity<ServiceOutcome<DashboardDto>>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation(" Get All Status List")
	@GetMapping("/getAllStatus")
	public ResponseEntity<?> getAllStatus(@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<List<StatusMaster>> svcSearchResult = candidateService.getAllStatus();
		return new ResponseEntity<>(svcSearchResult, HttpStatus.OK);
	}
	
	@GetMapping(value = "content")
	public ResponseEntity getContent(@RequestParam Long contentId,@RequestParam
		ContentViewType type) {
		ServiceOutcome svcSearchResult = new ServiceOutcome();
		String url = contentService.getContentById(contentId, type);
		svcSearchResult.setData(url);
		return new ResponseEntity<ServiceOutcome<ReportSearchDto>>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation("candidate deleteexp Id")
	@PutMapping("/deletecandidateExp/{id}")
	public ResponseEntity<ServiceOutcome<CandidateCafExperience>> deletecandidateExpById(@PathVariable("id") Long id,@RequestHeader("Authorization") String authorization) {
		// System.out.println("------------------------exp_id"+id);
		ServiceOutcome<CandidateCafExperience> svcSearchResult = candidateService.deletecandidateExpById(id);
		return new ResponseEntity<ServiceOutcome<CandidateCafExperience>>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation("candidate deleteEducation Id")
	@PutMapping("/deletecandidateEducationById/{id}")
	public ResponseEntity<ServiceOutcome<CandidateCafEducation>> deletecandidateEducationById(@PathVariable("id") Long id,@RequestHeader("Authorization") String authorization) {
		System.out.println("-----------------------education_id"+id);
		ServiceOutcome<CandidateCafEducation> svcSearchResult = candidateService.deletecandidateEducationById(id);
		return new ResponseEntity<ServiceOutcome<CandidateCafEducation>>(svcSearchResult, HttpStatus.OK);
	}

	// update the vendor proof color by agent ///
	@ApiOperation("Candidate vendor proof Status")
	@PutMapping("/updateCandidateVendorProofColor")
	public ResponseEntity<ServiceOutcome<Boolean>> updateCandidateVendorProofColor(@RequestBody VendorUploadChecksDto vendorUploadChecksDto,@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<Boolean> svcSearchResult = candidateService.updateCandidateVendorProofColor(vendorUploadChecksDto);
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation("Candidate organisation scope")
	@PutMapping("/updateCandidateOrganisationScope")
	public ResponseEntity<ServiceOutcome<Boolean>> updateCandidateOrganisationScope(@RequestBody OrganisationScope organisationScope,@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<Boolean> svcSearchResult = candidateService.updateCandidateOrganisationScope(organisationScope);
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
	}

	// @ApiOperation("get Candidate pre approval content id ")
	// @GetMapping(value = "/CandidateCode")
	// public ResponseEntity getContentId(@RequestParam String CandidateCode) {
	// 	ServiceOutcome svcSearchResult = new ServiceOutcome();
	// 	ServiceOutcome<Content> svcSearchResult = contentService.getApplicantById(CandidateCode);
	// 	svcSearchResult.setData(content_Id);
	// 	return new ResponseEntity<ServiceOutcome<Content>>(svcSearchResult, HttpStatus.OK);
	// 	}

	@ApiOperation(" get Candidate pre approval content id ")
	@GetMapping("/CandidateCode")
	public ResponseEntity<?> getContentById(@RequestHeader("Authorization") String authorization,@RequestParam String CandidateCode) {
		ServiceOutcome<Long> svcSearchResult = candidateService.getContentById(CandidateCode);
		return new ResponseEntity<>(svcSearchResult, HttpStatus.OK);
	}

	@ApiOperation("AddCommentsReports details")
	@PutMapping("/AddCommentsReports")
	public ResponseEntity<ServiceOutcome<Boolean>> AddCommentsReports(@RequestBody CandidateCaseDetailsDTO candidateCaseDetailsDTO,@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<Boolean> svcSearchResult = candidateService.AddCommentsReports(candidateCaseDetailsDTO);
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
	}

	@ApiOperation(" Get All Suspect List")
	@GetMapping("/getAllSuspectEmpList/{organizationId}")
	public ResponseEntity<?> getAllSuspectEmpList(@RequestHeader("Authorization") String authorization,@PathVariable("organizationId") Long organizationId) {
		System.out.println("-----------------------organizationId"+organizationId);
		ServiceOutcome<List<SuspectEmpMaster>> svcSearchResult = candidateService.getAllSuspectEmpList(organizationId);
		return new ResponseEntity<ServiceOutcome<List<SuspectEmpMaster>>>(svcSearchResult, HttpStatus.OK);
	}

	@ApiOperation("SuspectEmpMaster deleteexp Id")
	@PutMapping("/deleteSuspectExpById/{id}")
	public ResponseEntity<ServiceOutcome<SuspectEmpMaster>> deleteSuspectExpById(@PathVariable("id") Long id,@RequestHeader("Authorization") String authorization) {
		System.out.println("------------------------SuspectEmpMaster_id"+id);

		ServiceOutcome<SuspectEmpMaster> svcSearchResult = candidateService.deleteSuspectExpById(id);
		return new ResponseEntity<ServiceOutcome<SuspectEmpMaster>>(svcSearchResult, HttpStatus.OK);
	}
	
	@ApiOperation("SuspectEmpMaster deleteexp Mulitple")
	@PostMapping("/deleteSuspectExp")
	public ResponseEntity<ServiceOutcome<SuspectEmpMaster>> deleteSuspectExpByIds(@RequestBody DeleteRequestDTO request,@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<SuspectEmpMaster> svcSearchResult = new ServiceOutcome<>();

		System.out.println("------------------------SuspectEmpMaster_idssss"+request);
	    List<Long> ids = request.getSuspectEmpMasterId();
		System.out.println("------------------------SuspectEmp"+ids);
		for(Long id : ids) {
			System.out.println("IDSSS1"+id);
			 svcSearchResult = candidateService.deleteSuspectExpById(id);
		}

		return new ResponseEntity<ServiceOutcome<SuspectEmpMaster>>(svcSearchResult,HttpStatus.OK);
		
	}



	@ApiOperation("SuspectEmpMaster editeexp Id")
	@PutMapping("/updateSpectEMPloyee")
	public ResponseEntity<ServiceOutcome<Boolean>> updateSpectEMPloyee(@RequestBody SuspectEmpMasterDto suspectEmpMasterDto,@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<Boolean> svcSearchResult = candidateService.updateSpectEMPloyee(suspectEmpMasterDto);
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
	}

	@ApiOperation(value = "Get CandidateCode/ With Or Without ApplicantId")
	@PostMapping("/getCandidateCode")
	public ResponseEntity<ServiceOutcome<UanSearchData>> saveUan(@RequestBody UanSearchDataDTO uanSave){
		System.out.println("msg::"+uanSave.getMsg());
		System.out.println("APPLICANT_ID:::>>>>>>>"+uanSave.getApplicantId());
		System.out.println("BULKUANSEARCH::>>>"+uanSave.isBulkUanSearch());
		System.out.println("BulkUAN_ID_> Controller>>>=============================================="+uanSave.getBulkUanId());
		ServiceOutcome<UanSearchData> svcSearchResult = candidateService.saveUan(uanSave);
		
//		System.out.println("applicantID::"+uanSave.getApplicantId());
//		System.out.println("UploadedBy::"+uanSave.getUploadedBy());
//		Candidate findByapplicantId = candidateRepository.findByapplicantId(uanSave.getApplicantId());
//		System.out.println("FindByApplicantId::"+findByapplicantId.getCandidateCode());
//		
//		String candidateCode = findByapplicantId.getCandidateCode();
//		
//		svcSearchResult.setData(candidateCode);
		
//		return svcSearchResult;
		return new ResponseEntity<ServiceOutcome<UanSearchData>>(svcSearchResult,HttpStatus.OK);

		
	}
	
	@ApiOperation(value = "Api for Retrive Uan Data")
	@PostMapping("/uanSearchData")
	public ServiceOutcome<List<UanSearchData>> uanSearchData(@RequestBody UanSearchDataDTO uanSearch){
		
		ServiceOutcome<List<UanSearchData>> svcSearchResult = new ServiceOutcome<>();

		
		System.out.println("ApplicantID:: Re"+uanSearch.getApplicantId());
		String applicantId = uanSearch.getApplicantId();
		String uanNumber = uanSearch.getUanusername();
		System.out.println("UanNumber:: Re"+uanSearch.getUanusername());
		
		List<UanSearchData> findByApplicantIdAndUan = uanSearchDataRepository.findByApplicantIdAndUan(applicantId, uanNumber);
		
		
		System.out.println("FINDBYAPPLICANTIDANDUAN In Controller uanSearchData.."+findByApplicantIdAndUan.toString());
		svcSearchResult.setData(findByApplicantIdAndUan);
		
		
		return svcSearchResult;
	}
	
	@ApiOperation(value = "Bulk Uan Search")
	@PostMapping("/bulkUanSearch")
	public ResponseEntity<ServiceOutcome<List<UanSearchData>>> bulkUanSearch(@RequestBody List<BulkUanDTO> bulkUan){
		
//		ServiceOutcome<List<UanSearchData>> svcSearchResult = new ServiceOutcome<>();
		
//		String applicantId = uanSearch.getApplicantId();
//		String uanNumber = uanSearch.getUanusername();
		
		System.out.println("DATA>>>>"+bulkUan);
		
		String bulkUanId = null;
		
		for (BulkUanDTO dto : bulkUan) {
//	        System.out.println("ApplicantiD::>"+dto.getApplicantId());
//	        System.out.println("UANNUMBER::>"+dto.getUan());
//	        System.out.println("RandomID::>"+dto.getRandomId());
	        bulkUanId = dto.getRandomId();
	        System.out.println("TotalRecords:::>"+dto.getTotalRecordUploaded());
	        // Add any other processing logic for each UanSearchDataDTO element here.
	    }
		
		ServiceOutcome<List<UanSearchData>> svcSearchResult = candidateService.bulkUan(bulkUan);

		
		
//		List<UanSearchData> findByApplicantIdAndUan = uanSearchDataRepository.findByApplicantIdAndUan(applicantId, uanNumber);
//		List<UanSearchData> findByBulkUanId = uanSearchDataRepository.findByBulkUanId(bulkUanId);
//		System.out.println("FINDBYBULKUANID:::"+findByBulkUanId);
//		return new ResponseEntity<ServiceOutcome<UanSearchData>>(svcSearchResult,HttpStatus.OK);
		
//		svcSearchResult.setData(findByBulkUanId);
		
		
//		svcSearchResult.setMessage("SUCCESS");)

		return new ResponseEntity<ServiceOutcome<List<UanSearchData>>>(svcSearchResult,HttpStatus.OK);
	//	return svcSearchResult;

	}
	
	@ApiOperation("Retrive BulkUan Data")
	@PostMapping("getBulkUanData")
	public ResponseEntity<ServiceOutcome<List<UanSearchData>>> retriveBulkUanData(@RequestBody String bulkUanId){
		
		System.out.println("BulkUanID is >>>"+bulkUanId);
		
		ServiceOutcome<List<UanSearchData>> svcSearchResult = candidateService.retriveBulkUanData(bulkUanId);

		
		return new ResponseEntity<ServiceOutcome<List<UanSearchData>>>(svcSearchResult,HttpStatus.OK);
	}
	
	@ApiOperation("Get EPFO Data For Download::")
	@PostMapping("getEpfoData")
	public ResponseEntity<ServiceOutcome<List<UanSearchEpfoDTO>>> getEpfoData(@RequestBody Map<String, String> data){
		
		ServiceOutcome<List<UanSearchEpfoDTO>> svcSearchResult = candidateService.getEpfoData(data);

		
		System.out.println("UAN::"+data.get("uan"));
		System.out.println("CANDIDATE CODE::"+data.get("candidateCode"));
		System.out.println("BulkUANID>>>>>>>>GEtEPFODATA===="+data.get("bulkUanId"));
		
//		ServiceOutcome<List<EpfoDataFromDetailsDto>> epfoData = candidateService.getEpfoData(data);
		
		
		return new ResponseEntity<ServiceOutcome<List<UanSearchEpfoDTO>>>(svcSearchResult,HttpStatus.OK);
	}
	
	
	
	
	

	
}
