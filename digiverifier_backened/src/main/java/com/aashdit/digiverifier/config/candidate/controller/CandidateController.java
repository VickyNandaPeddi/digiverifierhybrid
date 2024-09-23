package com.aashdit.digiverifier.config.candidate.controller;

import java.util.List;
import java.util.Map;

import com.aashdit.digiverifier.config.candidate.dto.*;
import com.aashdit.digiverifier.config.candidate.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.aashdit.digiverifier.common.enums.ContentViewType;
import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.common.service.ContentService;
import com.aashdit.digiverifier.config.admin.dto.VendorUploadChecksDto;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.UanSearchDataRepository;
import com.aashdit.digiverifier.config.candidate.service.CandidateService;
import com.aashdit.digiverifier.config.candidate.service.ConventionalCandidateService;
import com.aashdit.digiverifier.config.superadmin.dto.DashboardDto;
import com.aashdit.digiverifier.config.superadmin.dto.ReportSearchDto;
import com.aashdit.digiverifier.config.superadmin.model.Organization;
import com.aashdit.digiverifier.config.superadmin.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping(value = "/api/candidate")
@Slf4j
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private ReportService reportService;

    @Autowired
    @Lazy
    private ContentService contentService;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private UanSearchDataRepository uanSearchDataRepository;

    @Autowired
    private ConventionalCandidateService conventionalConventionalService;

    // @Operation(summary ="Upload Candidate Information file CSV Or XLS")
    // @PostMapping("/uploadCandidate")
    // public ResponseEntity<ServiceOutcome<List>> uploadCandidateFile(@RequestParam("file")MultipartFile file,@RequestHeader("Authorization") String authorization){
    // 	ServiceOutcome<List> svcSearchResult = candidateService.saveCandidateInformation(file);
    // 	return new ResponseEntity<ServiceOutcome<List>>(svcSearchResult, HttpStatus.OK);
    // }

    @Operation(summary = "suspect Emp Master check by company Name")
    @GetMapping("/suspectEmpMasterCheck/{companyName}/{oganizationId}")
    public ResponseEntity<ServiceOutcome<String>> suspectEmpMasterCheck(@PathVariable("companyName") String companyName, @PathVariable("oganizationId") Long oganizationId, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<String> svcSearchResult = candidateService.suspectEmpMasterCheck(companyName, oganizationId);
        return new ResponseEntity<ServiceOutcome<String>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Upload Candidate Information file CSV Or XLS")
    @PostMapping("/uploadCandidate")
    public ResponseEntity<ServiceOutcome<List>> uploadCandidateFile(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<List> svcSearchResult = candidateService.saveCandidateInformation(file);
        return new ResponseEntity<ServiceOutcome<List>>(svcSearchResult, HttpStatus.OK);
    }


    @Operation(summary = "Get all Candidate Information")
    @RequestMapping(value = "/candidateList", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ServiceOutcome<DashboardDto>> getCandidateList(@RequestHeader("Authorization") String authorization, @RequestBody DashboardDto dashboardDto) {
        ServiceOutcome<DashboardDto> svcSearchResult = candidateService.getAllCandidateList(dashboardDto);
        return new ResponseEntity<ServiceOutcome<DashboardDto>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Get CandidateList Status And Count")
    @RequestMapping(value = "/getCandidateStatusAndCount", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ServiceOutcome<DashboardDto>> getCandidateStatusAndCount(@RequestHeader("Authorization") String authorization, @RequestBody DashboardDto dashboardDto) {
        ServiceOutcome<DashboardDto> svcSearchResult = candidateService.getCandidateStatusAndCount(dashboardDto);
        return new ResponseEntity<ServiceOutcome<DashboardDto>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Send Email For Candidate")
    @PostMapping("/invitationSent")
    public ResponseEntity<ServiceOutcome<Boolean>> invitationSent(@RequestBody CandidateInvitationSentDto candidateInvitationSentDto, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<Boolean> svcSearchResult = candidateService.invitationSent(candidateInvitationSentDto);
        return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Get Candidate by Reference No")
    @GetMapping("/getCandidate/{referenceNo}")
    public ResponseEntity<ServiceOutcome<CandidateDetailsDto>> getCandidate(@PathVariable("referenceNo") String referenceNo, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<CandidateDetailsDto> svcSearchResult = candidateService.getCandidateByCandidateCode(referenceNo);
        return new ResponseEntity<ServiceOutcome<CandidateDetailsDto>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Candidate UPDATE by Reference No")
    @PutMapping("/updateCandidate")
    public ResponseEntity<ServiceOutcome<CandidateDetailsDto>> updateCandidate(@RequestBody CandidateDetailsDto candidateDetails, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<CandidateDetailsDto> svcSearchResult = candidateService.updateCandidate(candidateDetails);
        return new ResponseEntity<ServiceOutcome<CandidateDetailsDto>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Candidate Cancel by Reference No")
    @PutMapping("/cancelCandidate/{referenceNo}")
    public ResponseEntity<ServiceOutcome<Boolean>> cancelCandidate(@PathVariable("referenceNo") String referenceNo, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<Boolean> svcSearchResult = candidateService.cancelCandidate(referenceNo);
        return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Get Report Delivery Details Status And Count")
    @RequestMapping(value = "/getReportDeliveryDetailsStatusAndCount", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ServiceOutcome<DashboardDto>> getReportDeliveryDetailsStatusAndCount(@RequestHeader("Authorization") String authorization, @RequestBody DashboardDto dashboardDto) {
        ServiceOutcome<DashboardDto> svcSearchResult = candidateService.getReportDeliveryDetailsStatusAndCount(dashboardDto);
        return new ResponseEntity<ServiceOutcome<DashboardDto>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Get all Candidate Application form details")
    @GetMapping("/candidateApplicationFormDetails/{candidateCode}")
    public ResponseEntity<ServiceOutcome<?>> candidateApplicationFormDetails(@PathVariable("candidateCode") String candidateCode) {
        ServiceOutcome<CandidationApplicationFormDto> svcSearchResult = candidateService.candidateApplicationFormDetailsExceptCandidate(candidateCode);
        return new ResponseEntity<ServiceOutcome<?>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = " Get All Remark")
    @GetMapping("/getAllRemark/{remarkType}")
    public ResponseEntity<?> getAllRemark(@RequestHeader("Authorization") String authorization, @PathVariable("remarkType") String remarkType) {
        ServiceOutcome<List<RemarkMaster>> svcSearchResult = candidateService.getAllRemark(remarkType);
        return new ResponseEntity<>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Candidate education UPDATE")
    @PutMapping("/updateCandidateEducationStatusAndRemark")
    public ResponseEntity<ServiceOutcome<Boolean>> updateCandidateEducationStatusAndRemark(@RequestBody ApprovalStatusRemarkDto approvalStatusRemarkDto, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<Boolean> svcSearchResult = candidateService.updateCandidateEducationStatusAndRemark(approvalStatusRemarkDto);
        return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Candidate Experience Status And Remark")
    @PutMapping("/updateCandidateExperienceStatusAndRemark")
    public ResponseEntity<ServiceOutcome<Boolean>> updateCandidateExperienceStatusAndRemark(@RequestBody ApprovalStatusRemarkDto approvalStatusRemarkDto, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<Boolean> svcSearchResult = candidateService.updateCandidateExperienceStatusAndRemark(approvalStatusRemarkDto);
        return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Candidate Experience Result Edit")
    @PostMapping("/updateCandidateExperienceResult")
    public ResponseEntity<ServiceOutcome<String>> updateCandidateExperienceResult(@RequestBody EmploymentResultUpdateReqDto employmentResultUpdateReqDto, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<String> svcSearchResult = candidateService.updateCandidateExperienceResult(employmentResultUpdateReqDto);
        return new ResponseEntity<ServiceOutcome<String>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Candidate Experience Status And Remark")
    @PutMapping("/updateCandidateAddressStatusAndRemark")
    public ResponseEntity<ServiceOutcome<Boolean>> updateCandidateAddressStatusAndRemark(@RequestBody ApprovalStatusRemarkDto approvalStatusRemarkDto, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<Boolean> svcSearchResult = candidateService.updateCandidateAddressStatusAndRemark(approvalStatusRemarkDto);
        return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Candidate Application form Approved")
    @PutMapping(value = "/candidateApplicationFormApproved", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ServiceOutcome<?>> candidateApplicationFormApproved(@RequestParam String candidateReportApproval,
                                                                              @RequestParam String candidateCode,
                                                                              @RequestParam(value = "criminalVerificationDocument", required = false) MultipartFile[] criminalVerificationDocument,
                                                                              @RequestParam(value = "globalDatabseCaseDetailsDocument", required = false) MultipartFile globalDatabseCaseDetailsDocument,
                                                                              @RequestHeader("Authorization") String authorization,
                                                                              @RequestParam String reportType) {
        ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
        try {
            CandidateApprovalDto candidateApprovalDto = new ObjectMapper().readValue(candidateReportApproval, CandidateApprovalDto.class);
            svcSearchResult = candidateService.candidateApplicationFormApproved(candidateCode, criminalVerificationDocument, candidateApprovalDto.getCriminalVerificationColorId(), globalDatabseCaseDetailsDocument, candidateApprovalDto.getGlobalDatabseCaseDetailsColorId(), reportType);
        } catch (Exception e) {
            log.error("Exception occured in candidateApplicationFormApproved method in CandidateController-->", e);
        }

        return new ResponseEntity<ServiceOutcome<?>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Upload Candidate Fake Company List Xls")
    @PostMapping("/uploadFakeCompanyDetails")
    public ResponseEntity<ServiceOutcome<Boolean>> uploadFakeCompanyDetails(@RequestParam("file") MultipartFile file, @RequestParam Long organizationId, @RequestParam(required = false) String status, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
        try {
            svcSearchResult = candidateService.saveFakeCompanyDetails(file, organizationId, status);
            return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occured in uploadFakeCompanyDetails method in CandidateController-->", e);
            return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @Operation(summary = "Upload Candidate Fake College List Xls")
    @PostMapping("/uploadFakeCollegeDetails")
    public ResponseEntity<ServiceOutcome<Boolean>> uploadFakeCollegeDetails(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
        try {
            svcSearchResult = candidateService.saveFakeCollegeDetails(file);
            return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occured in uploadFakeCollegeDetails method in CandidateController-->", e);
            return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @Operation(summary = "View Pending Details Status And Count For Dashboard")
    @RequestMapping(value = "/getPendingDetailsStatusAndCount", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ServiceOutcome<DashboardDto>> getPendingDetailsStatusAndCount(@RequestHeader("Authorization") String authorization, @RequestBody DashboardDto dashboardDto) {
        ServiceOutcome<DashboardDto> svcSearchResult = candidateService.getPendingDetailsStatusAndCount(dashboardDto);
        return new ResponseEntity<ServiceOutcome<DashboardDto>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = " Get All Status List")
    @GetMapping("/getAllStatus")
    public ResponseEntity<?> getAllStatus(@RequestHeader("Authorization") String authorization) {
        ServiceOutcome<List<StatusMaster>> svcSearchResult = candidateService.getAllStatus();
        return new ResponseEntity<>(svcSearchResult, HttpStatus.OK);
    }

    @GetMapping(value = "content")
    public ResponseEntity getContent(@RequestParam Long contentId, @RequestParam
    ContentViewType type) {
        ServiceOutcome svcSearchResult = new ServiceOutcome();
        String url = contentService.getContentById(contentId, type);
        svcSearchResult.setData(url);
        return new ResponseEntity<ServiceOutcome<ReportSearchDto>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "candidate deleteexp Id")
    @PutMapping("/deletecandidateExp/{id}")
    public ResponseEntity<ServiceOutcome<CandidateCafExperience>> deletecandidateExpById(@PathVariable("id") Long id, @RequestHeader("Authorization") String authorization) {
        // System.out.println("------------------------exp_id"+id);
        ServiceOutcome<CandidateCafExperience> svcSearchResult = candidateService.deletecandidateExpById(id);
        return new ResponseEntity<ServiceOutcome<CandidateCafExperience>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "candidate deleteEducation Id")
    @PutMapping("/deletecandidateEducationById/{id}")
    public ResponseEntity<ServiceOutcome<CandidateCafEducation>> deletecandidateEducationById(@PathVariable("id") Long id, @RequestHeader("Authorization") String authorization) {
        System.out.println("-----------------------education_id" + id);
        ServiceOutcome<CandidateCafEducation> svcSearchResult = candidateService.deletecandidateEducationById(id);
        return new ResponseEntity<ServiceOutcome<CandidateCafEducation>>(svcSearchResult, HttpStatus.OK);
    }

    // update the vendor proof color by agent ///
    @Operation(summary = "Candidate vendor proof Status")
    @PutMapping("/updateCandidateVendorProofColor")
    public ResponseEntity<ServiceOutcome<Boolean>> updateCandidateVendorProofColor(@RequestBody VendorUploadChecksDto vendorUploadChecksDto, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<Boolean> svcSearchResult = candidateService.updateCandidateVendorProofColor(vendorUploadChecksDto);
        return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Candidate organisation scope")
    @PutMapping("/updateCandidateOrganisationScope")
    public ResponseEntity<ServiceOutcome<Boolean>> updateCandidateOrganisationScope(@RequestBody OrganisationScope organisationScope, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<Boolean> svcSearchResult = candidateService.updateCandidateOrganisationScope(organisationScope);
        return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
    }

    // @Operation(summary ="get Candidate pre approval content id ")
    // @GetMapping(value = "/CandidateCode")
    // public ResponseEntity getContentId(@RequestParam String CandidateCode) {
    // 	ServiceOutcome svcSearchResult = new ServiceOutcome();
    // 	ServiceOutcome<Content> svcSearchResult = contentService.getApplicantById(CandidateCode);
    // 	svcSearchResult.setData(content_Id);
    // 	return new ResponseEntity<ServiceOutcome<Content>>(svcSearchResult, HttpStatus.OK);
    // 	}

    @Operation(summary = " get Candidate pre approval content id ")
    @GetMapping("/CandidateCode")
    public ResponseEntity<?> getContentById(@RequestHeader("Authorization") String authorization, @RequestParam String CandidateCode) {
        ServiceOutcome<Long> svcSearchResult = candidateService.getContentById(CandidateCode);
        return new ResponseEntity<>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "AddCommentsReports details")
    @PutMapping("/AddCommentsReports")
    public ResponseEntity<ServiceOutcome<Boolean>> AddCommentsReports(@RequestBody CandidateCaseDetailsDTO candidateCaseDetailsDTO, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<Boolean> svcSearchResult = candidateService.AddCommentsReports(candidateCaseDetailsDTO);
        return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = " Get All Suspect List")
    @GetMapping("/getAllSuspectEmpList/{organizationId}")
    public ResponseEntity<?> getAllSuspectEmpList(@RequestHeader("Authorization") String authorization, @PathVariable("organizationId") Long organizationId, @RequestParam(defaultValue = "0") int pageNumber,
                                                  @RequestParam(defaultValue = "10") int pageSize) {
        log.info("-----------------------organizationId {}" + organizationId);
        log.info("PageNumber In con:: {}" + pageNumber);
        log.info("PageSize:: In con {}" + pageSize);
        ServiceOutcome<List<SuspectEmpMaster>> svcSearchResult = candidateService.getAllSuspectEmpList(organizationId, pageNumber, pageSize);
        return new ResponseEntity<ServiceOutcome<List<SuspectEmpMaster>>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "SuspectEmpMaster deleteexp Id")
    @PutMapping("/deleteSuspectExpById/{id}")
    public ResponseEntity<ServiceOutcome<SuspectEmpMaster>> deleteSuspectExpById(@PathVariable("id") Long id, @RequestHeader("Authorization") String authorization) {
        log.info("------------------------SuspectEmpMaster_id {}" + id);

        ServiceOutcome<SuspectEmpMaster> svcSearchResult = candidateService.deleteSuspectExpById(id);
        return new ResponseEntity<ServiceOutcome<SuspectEmpMaster>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "SuspectEmpMaster deleteexp Mulitple")
    @PostMapping("/deleteSuspectExp")
    public ResponseEntity<ServiceOutcome<SuspectEmpMaster>> deleteSuspectExpByIds(@RequestBody DeleteRequestDTO request, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<SuspectEmpMaster> svcSearchResult;

        log.info("------------------------SuspectEmpMaster_idssss {}" + request);
        List<Long> ids = request.getSuspectEmpMasterId();
        log.info("------------------------SuspectEmp {}" + ids);

        svcSearchResult = candidateService.deleteAllSuspectById(ids);

        return new ResponseEntity<ServiceOutcome<SuspectEmpMaster>>(svcSearchResult, HttpStatus.OK);

    }

    @Operation(summary = "Remove all SuspectEmployer by OrganizationId")
    @PutMapping("/removeAllSuspectEmployerByOrgId/{orgId}")
    public ResponseEntity<ServiceOutcome<SuspectEmpMaster>> removeAllSuspectEmployerByOrgId(@PathVariable("orgId") Long orgId, @RequestHeader("Authorization") String authorization) {
        log.info("------------------------ORGID {}" + orgId);
        ServiceOutcome<SuspectEmpMaster> svcSearchResult = candidateService.removeAllSuspectEmployerByOrgId(orgId);
        return new ResponseEntity<ServiceOutcome<SuspectEmpMaster>>(svcSearchResult, HttpStatus.OK);
    }


    @Operation(summary = "SuspectEmpMaster editeexp Id")
    @PutMapping("/updateSpectEMPloyee")
    public ResponseEntity<ServiceOutcome<Boolean>> updateSpectEMPloyee(@RequestBody SuspectEmpMasterDto suspectEmpMasterDto, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<Boolean> svcSearchResult = candidateService.updateSpectEMPloyee(suspectEmpMasterDto);
        return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Get CandidateCode/ With Or Without ApplicantId")
    @PostMapping("/singleUanSearch")
    public ResponseEntity<ServiceOutcome<UanSearchData>> saveUan(@RequestBody UanSearchDataDTO uanSave) {
        System.out.println("msg::" + uanSave.getMsg());
        System.out.println("APPLICANT_ID:::>>>>>>>" + uanSave.getApplicantId());
        System.out.println("BULKUANSEARCH::>>>" + uanSave.isBulkUanSearch());
        System.out.println("BulkUAN_ID_> Controller>>>==============================================" + uanSave.getBulkUanId());
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
        return new ResponseEntity<ServiceOutcome<UanSearchData>>(svcSearchResult, HttpStatus.OK);


    }

    @Operation(summary = "Api for Retrive Uan Data")
    @PostMapping("/uanSearchData")
    public ServiceOutcome<List<UanSearchData>> uanSearchData(@RequestBody UanSearchDataDTO uanSearch) {

        ServiceOutcome<List<UanSearchData>> svcSearchResult = new ServiceOutcome<>();


        System.out.println("ApplicantID:: Re" + uanSearch.getApplicantId());
        String applicantId = uanSearch.getApplicantId();
        String uanNumber = uanSearch.getUanusername();
        System.out.println("UanNumber:: Re" + uanSearch.getUanusername());

        List<UanSearchData> findByApplicantIdAndUan = uanSearchDataRepository.findByApplicantIdAndUan(applicantId, uanNumber);


        System.out.println("FINDBYAPPLICANTIDANDUAN In Controller uanSearchData.." + findByApplicantIdAndUan.toString());
        svcSearchResult.setData(findByApplicantIdAndUan);


        return svcSearchResult;
    }

//	@Operation(summary = "Bulk Uan Search")
//	@PostMapping("/bulkUanSearch")
//	public ResponseEntity<ServiceOutcome<List<UanSearchData>>> bulkUanSearch(@RequestBody List<BulkUanDTO> bulkUan){
//		
////		ServiceOutcome<List<UanSearchData>> svcSearchResult = new ServiceOutcome<>();
//		
////		String applicantId = uanSearch.getApplicantId();
////		String uanNumber = uanSearch.getUanusername();
//		
//		System.out.println("DATA>>>>"+bulkUan);
//		
//		String bulkUanId = null;
//		
//		for (BulkUanDTO dto : bulkUan) {
////	        System.out.println("ApplicantiD::>"+dto.getApplicantId());
////	        System.out.println("UANNUMBER::>"+dto.getUan());
////	        System.out.println("RandomID::>"+dto.getRandomId());
//	        bulkUanId = dto.getRandomId();
//	        System.out.println("TotalRecords:::>"+dto.getTotalRecordUploaded());
//	        // Add any other processing logic for each UanSearchDataDTO element here.
//	    }
//		
//		ServiceOutcome<List<UanSearchData>> svcSearchResult = candidateService.bulkUan(bulkUan);
//
//		
//		
////		List<UanSearchData> findByApplicantIdAndUan = uanSearchDataRepository.findByApplicantIdAndUan(applicantId, uanNumber);
////		List<UanSearchData> findByBulkUanId = uanSearchDataRepository.findByBulkUanId(bulkUanId);
////		System.out.println("FINDBYBULKUANID:::"+findByBulkUanId);
////		return new ResponseEntity<ServiceOutcome<UanSearchData>>(svcSearchResult,HttpStatus.OK);
//		
////		svcSearchResult.setData(findByBulkUanId);
//		
//		
////		svcSearchResult.setMessage("SUCCESS");)
//
//		return new ResponseEntity<ServiceOutcome<List<UanSearchData>>>(svcSearchResult,HttpStatus.OK);
//	//	return svcSearchResult;
//
//	}

    @Operation(summary = "Retrive BulkUan Data")
    @PostMapping("getBulkUanData")
    public ResponseEntity<ServiceOutcome<List<UanSearchData>>> retriveBulkUanData(@RequestBody String bulkUanId) {

        System.out.println("BulkUanID is >>>" + bulkUanId);

        ServiceOutcome<List<UanSearchData>> svcSearchResult = candidateService.retriveBulkUanData(bulkUanId);


        return new ResponseEntity<ServiceOutcome<List<UanSearchData>>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Get EPFO Data For Download::")
    @PostMapping("getEpfoData")
    public ResponseEntity<ServiceOutcome<List<UanSearchEpfoDTO>>> getEpfoData(@RequestBody Map<String, String> data) {

        ServiceOutcome<List<UanSearchEpfoDTO>> svcSearchResult = candidateService.getEpfoData(data);


        System.out.println("UAN::" + data.get("uan"));
        System.out.println("CANDIDATE CODE::" + data.get("candidateCode"));
        System.out.println("BulkUANID>>>>>>>>GEtEPFODATA====" + data.get("bulkUanId"));

//		ServiceOutcome<List<EpfoDataFromDetailsDto>> epfoData = candidateService.getEpfoData(data);


        return new ResponseEntity<ServiceOutcome<List<UanSearchEpfoDTO>>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Search all Candidate")
    @PostMapping("searchAllCandidate")
    public ResponseEntity<ServiceOutcome<DashboardDto>> searchAllCandidate(@RequestBody SearchAllCandidateDTO searchAllcandidate) {

        System.out.println("AgentName for Search All Candidate::" + searchAllcandidate.getAgentName());
        System.out.println("userSearchInput for Search All Candidate::>>" + searchAllcandidate.getUserSearchInput());

        ServiceOutcome<DashboardDto> svcSearchResult = candidateService.searchAllCandidate(searchAllcandidate);
        return new ResponseEntity<ServiceOutcome<DashboardDto>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Find Organization By candidateCode")
    @PostMapping("findOrgId")
    public ResponseEntity<ServiceOutcome<Organization>> findOrganization(@RequestHeader("Authorization") String authorization, @RequestBody String candidateCode) {

        ServiceOutcome<Organization> svcSearchResult = candidateService.findOrganization(candidateCode);

        return new ResponseEntity<ServiceOutcome<Organization>>(svcSearchResult, HttpStatus.OK);

    }

    @Operation(summary = "DNHB Search")
    @PostMapping("searchDnh")
    public ResponseEntity<ServiceOutcome<List<SuspectEmpMaster>>> searchDnhb(@RequestBody String searchData) {

        ServiceOutcome<List<SuspectEmpMaster>> svcSearchResult = candidateService.searchDnh(searchData);

        return new ResponseEntity<ServiceOutcome<List<SuspectEmpMaster>>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "UANSEARCH dashboard search filter data")
    @PostMapping("uanSearchFilter")
    public ResponseEntity<ServiceOutcome<List<UanSearchData>>> uanFilterSearchDashboard(@RequestBody UanSearchDashboardFilterDTO uanSearchDashboardFilterDto) {

        ServiceOutcome<List<UanSearchData>> svcSearchResult = candidateService.uanSearchDashboardFilter(uanSearchDashboardFilterDto);

        return new ResponseEntity<ServiceOutcome<List<UanSearchData>>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Get all Candidate Information")
    @PostMapping("/pendingCandidateList")
    public ResponseEntity<ServiceOutcome<DashboardDto>> pendingCandidateList(@RequestHeader("Authorization") String authorization, @RequestBody DashboardDto dashboardDto) {
        ServiceOutcome<DashboardDto> svcSearchResult = candidateService.getAllPendingCandidateList(dashboardDto);
        return new ResponseEntity<ServiceOutcome<DashboardDto>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Upload BulkUan Information file XLS")
    @PostMapping("/bulkUanSearch")
    public ResponseEntity<ServiceOutcome<List>> uploadBulkUan(@RequestParam("file") MultipartFile file) {
        ServiceOutcome<List> svcSearchResult = candidateService.bulkUanNew(file);
        return new ResponseEntity<ServiceOutcome<List>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Get LOA Concent File Of candidate")
    @GetMapping("/getCandidateLOAFile/{candidateCode}/{dashboardStatus}")
    public ResponseEntity<ServiceOutcome<String>> getCandidateLOAFile(@RequestHeader("Authorization") String authorization,
                                                                      @PathVariable("candidateCode") String candidateCode, @PathVariable("dashboardStatus") String dashboardStatus) {
        ServiceOutcome<String> svcSearchResult = candidateService.getCandidateLOAFile(candidateCode, dashboardStatus);
        return new ResponseEntity<ServiceOutcome<String>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Save Case Reinitiation info Of candidate")
    @GetMapping("/saveCaseReinitDetails/{candidateCode}")
    public ServiceOutcome<String> saveCaseReinitDetails(@RequestHeader("Authorization") String authorization,
                                                        @PathVariable("candidateCode") String candidateCode, @RequestParam("caseReinitDate") String caseReinitDate) {
        return candidateService.saveCaseReinitDetails(candidateCode, caseReinitDate);
    }

    @Operation(summary = "Refetch The UANS data if BULK Uan get Failed for some candidates")
    @PostMapping("/reFetchUANData")
    public ResponseEntity<ServiceOutcome<Boolean>> reFetchUANData(@RequestBody CandidateInvitationSentDto candidateInvitationSentDto, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<Boolean> svcSearchResult = candidateService.reFetchUANData(candidateInvitationSentDto);
        return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
    }
//	@Operation(summary ="Add Experience From Resume To Employment Table")
//    @PostMapping(value="/resumeParser", consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE })
//	public ResponseEntity<ServiceOutcome<String>> experienceFromResume(@RequestParam("base64")MultipartFile base64){
//		ServiceOutcome<String> svcSearchResult = candidateService.experienceFromResume(base64);
//		return new ResponseEntity<ServiceOutcome<String>>(svcSearchResult, HttpStatus.OK);
//    }

    @Operation(summary = "Upload BulkUan Information file XLS")
    @PostMapping("/resumeParser")
    public ResponseEntity<ServiceOutcome<String>> experienceFromResume(@RequestParam("file") MultipartFile file, @RequestParam("candidateCode") String candidateCode) {
        System.out.println("file for resume parser " + file + candidateCode);
        ServiceOutcome<String> svcSearchResult = candidateService.experienceFromResume(file, candidateCode);
        return new ResponseEntity<ServiceOutcome<String>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Upload BulkUan Information file XLS")
    @PostMapping("/bulkPanToUan")
    public ResponseEntity<ServiceOutcome<List>> bulkPanToUan(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<List> svcSearchResult = candidateService.bulkPanToUan(file);
        return new ResponseEntity<ServiceOutcome<List>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Refetch The UANS data if PanToUANData get Failed for some candidates when upload pan list")
    @PostMapping("/reFetchPanToUANData")
    public ResponseEntity<ServiceOutcome<Boolean>> reFetchPanToUANData(@RequestBody CandidateInvitationSentDto candidateInvitationSentDto, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<Boolean> svcSearchResult = candidateService.reFetchPanToUANData(candidateInvitationSentDto);
        return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "To purge old candidates")
    @GetMapping("/oldCandidatesPurge/{orgId}")
    public ResponseEntity<ServiceOutcome<List<Long>>> oldCandidatesPurge(@RequestHeader("Authorization") String authorization,
                                                                         @PathVariable("orgId") Long orgId) {
        ServiceOutcome<List<Long>> purgedCandidatesList = candidateService.oldCandidatesPurge(orgId);
        return new ResponseEntity<ServiceOutcome<List<Long>>>(purgedCandidatesList, HttpStatus.OK);
    }

    @Operation(summary = "Forward Report throught Email")
    @PostMapping("/forwardReport")
    public ResponseEntity<ServiceOutcome<String>> forwardReport(@RequestParam("candidateIds") List<Long> candidateIds, @RequestParam("emailIds") String emailIds) {
        System.out.println("Forward Report throught Email " + candidateIds + emailIds);
        ServiceOutcome<String> svcSearchResult = candidateService.forwardReport(candidateIds, emailIds);
        return new ResponseEntity<ServiceOutcome<String>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Failed Pan To Uan Candidate List")
    @RequestMapping(value = "/failedPanToUanCandidateList", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ServiceOutcome<List<CandidateDetailsDtoForPanToUan>>> getFailedPanToUanCandidateList(@RequestHeader("Authorization") String authorization, @RequestBody DashboardDto dashboardDto) {
        ServiceOutcome<List<CandidateDetailsDtoForPanToUan>> svcSearchResult = candidateService.getFailedPanToUanCandidateList(dashboardDto);
        return new ResponseEntity<ServiceOutcome<List<CandidateDetailsDtoForPanToUan>>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Set Pan to Uan response in Candidate basic and uan search data table")
    @PostMapping("/setPanToUanResponse")
    public ResponseEntity<ServiceOutcome<String>> setPanToUanResponse(@RequestBody CandidateDetailsDtoForPanToUan candidateDetails, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<String> svcSearchResult = candidateService.setPanToUanResponse(candidateDetails);
        return new ResponseEntity<ServiceOutcome<String>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Upload Conventional Candidate Information file CSV Or XLS")
    @PostMapping("/uploadConventionalCandidate")
    public ResponseEntity<ServiceOutcome<List>> uploadConventionalCandidateFile(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam(value = "candidateCode", required = false) String candidateCode, @RequestParam(value = "hybridToConventionalCandidateFlow", required = false) boolean hybridToConventionalCandidateFlow, @RequestParam(value = "accountName", required = false) String accountName, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<List> svcSearchResult = conventionalConventionalService.saveConventionalCandidateInformation(file, candidateCode, hybridToConventionalCandidateFlow, accountName);
        return new ResponseEntity<ServiceOutcome<List>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Get Report Delivery Details Status And Count")
    @RequestMapping(value = "/ConventionalGetReportDeliveryDetailsStatusAndCount", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ServiceOutcome<DashboardDto>> conventionalGetReportDeliveryDetailsStatusAndCount(@RequestHeader("Authorization") String authorization, @RequestBody DashboardDto dashboardDto) {
        ServiceOutcome<DashboardDto> svcSearchResult = conventionalConventionalService.conventionalGetReportDeliveryDetailsStatusAndCount(dashboardDto);
        return new ResponseEntity<ServiceOutcome<DashboardDto>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Get CandidateList Status And Count")
    @RequestMapping(value = "/ConventionalGetCandidateStatusAndCount", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ServiceOutcome<DashboardDto>> conventionalGetCandidateStatusAndCount(@RequestHeader("Authorization") String authorization, @RequestBody DashboardDto dashboardDto) {
        ServiceOutcome<DashboardDto> svcSearchResult = conventionalConventionalService.conventionalGetCandidateStatusAndCount(dashboardDto);
        return new ResponseEntity<ServiceOutcome<DashboardDto>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Get all Conventional Candidate Information")
    @RequestMapping(value = "/conventionalCandidateList", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ServiceOutcome<DashboardDto>> getConventionalCandidateList(@RequestHeader("Authorization") String authorization, @RequestBody DashboardDto dashboardDto) {
        ServiceOutcome<DashboardDto> svcSearchResult = conventionalConventionalService.getAllConventionalCandidateList(dashboardDto);
        return new ResponseEntity<ServiceOutcome<DashboardDto>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Conventional Candidate Application form Approved")
    @PutMapping(value = "/conventionalCandidateApplicationFormApproved", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ServiceOutcome<?>> conventionalCandidateApplicationFormApproved(@RequestParam String candidateReportApproval,
                                                                                          @RequestParam String candidateCode,
                                                                                          @RequestParam(value = "criminalVerificationDocument", required = false) MultipartFile criminalVerificationDocument,
                                                                                          @RequestParam(value = "globalDatabseCaseDetailsDocument", required = false) MultipartFile globalDatabseCaseDetailsDocument,
                                                                                          @RequestHeader("Authorization") String authorization,
                                                                                          @RequestParam String reportType) {
        ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
        try {
            CandidateApprovalDto candidateApprovalDto = new ObjectMapper().readValue(candidateReportApproval, CandidateApprovalDto.class);
            svcSearchResult = conventionalConventionalService.conventionalCandidateApplicationFormApproved(candidateCode, criminalVerificationDocument, candidateApprovalDto.getCriminalVerificationColorId(), globalDatabseCaseDetailsDocument, candidateApprovalDto.getGlobalDatabseCaseDetailsColorId(), reportType);
        } catch (Exception e) {
            log.error("Exception occured in candidateApplicationFormApproved method in CandidateController-->", e);
        }

        return new ResponseEntity<ServiceOutcome<?>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Send Email For Candidate")
    @PostMapping("/conventionalInvitationSent")
    public ResponseEntity<ServiceOutcome<Boolean>> conventionalInvitationSent(@RequestBody CandidateInvitationSentDto candidateInvitationSentDto, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<Boolean> svcSearchResult = conventionalConventionalService.conventionalInvitationSent(candidateInvitationSentDto);
        return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Candidate Cancel by Reference No")
    @PutMapping("/conventionalCancelCandidate/{referenceNo}")
    public ResponseEntity<ServiceOutcome<Boolean>> conventionalCancelCandidate(@PathVariable("referenceNo") String referenceNo, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<Boolean> svcSearchResult = conventionalConventionalService.conventionalCancelCandidate(referenceNo);
        return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Candidate UPDATE by Reference No")
    @PutMapping("/conventionalUpdateCandidate")
    public ResponseEntity<ServiceOutcome<CandidateDetailsDto>> conventionalUpdateCandidate(@RequestBody CandidateDetailsDto candidateDetails, @RequestHeader("Authorization") String authorization) {
        ServiceOutcome<CandidateDetailsDto> svcSearchResult = conventionalConventionalService.conventionalUpdateCandidate(candidateDetails);
        return new ResponseEntity<ServiceOutcome<CandidateDetailsDto>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Search all Candidate")
    @PostMapping("conventionalSearchAllCandidate")
    public ResponseEntity<ServiceOutcome<DashboardDto>> conventionalSearchAllCandidate(@RequestBody SearchAllCandidateDTO searchAllcandidate) {

        log.info("CONVENTIONAL DASHBOARD SEARCH : ");
        log.info("AgentName for Search All Candidate::" + searchAllcandidate.getAgentName());
        log.info("userSearchInput for Search All Candidate::>>" + searchAllcandidate.getUserSearchInput());

        ServiceOutcome<DashboardDto> svcSearchResult = conventionalConventionalService.conventionalSearchAllCandidate(searchAllcandidate);
        return new ResponseEntity<ServiceOutcome<DashboardDto>>(svcSearchResult, HttpStatus.OK);
    }

    @Operation(summary = "Refetch The UANS data if PanToUANData get Failed and If UANS available")
    @GetMapping("/reFetchPANTOUANDataForAvailableUANs")
    public ResponseEntity<ServiceOutcome<Boolean>> reFetchPANTOUANDataForAvailableUANs(@RequestHeader("Authorization") String authorization) {
        ServiceOutcome<Boolean> svcSearchResult = candidateService.reFetchPANTOUANDataForAvailableUANs();
        return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
    }

    @PostMapping("/addUpdateQcRemarks")
    public ResponseEntity<ServiceOutcome<QcRemarksDto>> addUpdateQcRemarks(@RequestBody QcRemarksDto requestBody) {
        ServiceOutcome<QcRemarksDto> svcOutcome = candidateService.addUpdateQcRemarks(requestBody);
        return new ResponseEntity<>(svcOutcome, HttpStatus.OK);
    }

    @GetMapping("/getQcRemarks/{candidateCode}")
    public ResponseEntity<ServiceOutcome<List<QcRemarksDto>>> getQcRemarks(
            @PathVariable String candidateCode) {
        ServiceOutcome<List<QcRemarksDto>> qcRemarks = new ServiceOutcome<>();
        if (candidateCode.contains("null")==false) {
            qcRemarks = candidateService.getQcRemarks(candidateCode);
        }
        return new ResponseEntity<>(qcRemarks, HttpStatus.OK);
    }

    @GetMapping("/deleteQcRemarks/{qcRemarksID}")
    public ResponseEntity<ServiceOutcome<String>> deleteQcRemarks(
            @PathVariable(name = "qcRemarksID", required = false) String qcRemarksID) {
        if (qcRemarksID == null) {
            return (ResponseEntity<ServiceOutcome<String>>) ResponseEntity.badRequest();
        }
        ServiceOutcome<String> stringServiceOutcome = candidateService.deleteQcRemarkByQcRemarksId(Long.valueOf(qcRemarksID));
        return new ResponseEntity<ServiceOutcome<String>>(stringServiceOutcome, HttpStatus.OK);
    }

    @Operation(summary = "Update ITR details by candidateCode and ITR response")
    @PostMapping("updateITRDetails")
    public ResponseEntity<ServiceOutcome<String>> updateITRDetails(@RequestHeader("Authorization") String authorization, @RequestBody UpdateITRDto updateITRDto) {
        ServiceOutcome<String> svcSearchResult = candidateService.updateITRDetails(updateITRDto);
        return new ResponseEntity<ServiceOutcome<String>>(svcSearchResult, HttpStatus.OK);

    }
    
    @PostMapping("saveConventionalReferenceData")
    public ResponseEntity<ServiceOutcome<ConventionalReferenceDataDTO>> saveConventionalReferenceData(@RequestHeader("Authorization") String authorization,@RequestBody ConventionalReferenceDataDTO conventionalReferenceDto){
    	        ServiceOutcome<ConventionalReferenceDataDTO> svcSearchResult = conventionalConventionalService.saveConventionalRefereneceData(conventionalReferenceDto);

    	return new ResponseEntity<ServiceOutcome<ConventionalReferenceDataDTO>>(svcSearchResult, HttpStatus.OK);
    }

}
