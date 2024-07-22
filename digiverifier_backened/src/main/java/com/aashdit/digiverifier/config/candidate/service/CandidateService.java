package com.aashdit.digiverifier.config.candidate.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.aashdit.digiverifier.config.candidate.dto.*;
import com.aashdit.digiverifier.config.candidate.model.*;
import org.json.JSONArray;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import com.aashdit.digiverifier.config.admin.dto.UserDto;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.candidate.dto.DigidocDetailsDto;
import com.aashdit.digiverifier.config.candidate.dto.EmploymentResultUpdateReqDto;
import com.aashdit.digiverifier.config.candidate.dto.SearchAllCandidateDTO;
import com.aashdit.digiverifier.config.candidate.dto.ApprovalStatusRemarkDto;
import com.aashdit.digiverifier.config.candidate.dto.BulkUanDTO;
import com.aashdit.digiverifier.config.candidate.dto.CandidateCafAddressDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateCafEducationDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateCafExperienceDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateDetailsDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateDetailsDtoForPanToUan;
import com.aashdit.digiverifier.config.candidate.dto.CandidateInvitationSentDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidationApplicationFormDto;
import com.aashdit.digiverifier.config.superadmin.dto.DashboardDto;
import com.aashdit.digiverifier.config.superadmin.model.Color;
import com.aashdit.digiverifier.config.superadmin.model.Organization;
import com.aashdit.digiverifier.epfo.dto.EpfoDataFromDetailsDto;
import com.aashdit.digiverifier.config.admin.dto.VendorUploadChecksDto;
import com.aashdit.digiverifier.common.model.Content;
import com.aashdit.digiverifier.config.candidate.dto.CandidateCaseDetailsDTO;
import com.aashdit.digiverifier.config.candidate.dto.SuspectEmpMasterDto;
import com.aashdit.digiverifier.config.candidate.dto.UanSearchDashboardFilterDTO;
import com.aashdit.digiverifier.config.candidate.dto.UanSearchDataDTO;
import com.aashdit.digiverifier.config.candidate.dto.UanSearchEpfoDTO;
import com.aashdit.digiverifier.itr.model.ITRData;
public interface CandidateService {

	ServiceOutcome<List> saveCandidateInformation(MultipartFile file);

	ServiceOutcome<DashboardDto> getAllCandidateList(DashboardDto dashboardDto);

	ServiceOutcome<Boolean> invitationSent(CandidateInvitationSentDto candidateInvitationSentDto);

	ServiceOutcome<CandidateDetailsDto> updateCandidate(CandidateDetailsDto candidateDetails);

	ServiceOutcome<DashboardDto> getCandidateStatusAndCount(DashboardDto dashboardDto);

	ServiceOutcome<Boolean> cancelCandidate(String referenceNo);

	List<CandidateStatus> expireInvitationForCandidate();

	ServiceOutcome<CandidateStatus> getCandidateStatusByCandidateCode(String code);

	ServiceOutcome<DashboardDto> getReportDeliveryDetailsStatusAndCount(DashboardDto dashboardDto);

	ServiceOutcome<List<RemarkMaster>> getAllRemark(String remarkType);

	List<CandidateStatus> processDeclined();

	ServiceOutcome<CandidateDetailsDto> getCandidateByCandidateCode(String referenceNo);
	
	Candidate findCandidateByCandidateCode(String candidateCode);

	ServiceOutcome<Boolean> declineAuthLetter(String candidateCode);

	ServiceOutcome<Boolean> saveFakeCompanyDetails(MultipartFile file,Long organizationId,String status);

	ServiceOutcome<Boolean> saveFakeCollegeDetails(MultipartFile file);
	
	ServiceOutcome<List<QualificationMaster>> getQualificationList();

	ServiceOutcome<Boolean> saveNUpdateCandidateEducation(String candidateCafEducation, MultipartFile certificate);

	ServiceOutcome<CandidateCafEducationDto> getCandidateEducationById(Long candidateCafEducationId);

	ServiceOutcome<Boolean> saveNUpdateCandidateExperience(String candidateCafExperience, MultipartFile certificate);

	ServiceOutcome<CandidateCafExperienceDto> getCandidateExperienceById(Long candidateCafExperienceId);

	ServiceOutcome<CandidationApplicationFormDto> candidateApplicationFormDetails(String candidateCode);

	ServiceOutcome<Boolean> saveCandidateApplicationForm(String candidateCafEducation,
			JSONArray candidateCafAddress, MultipartFile resume, String candidateCode);

	ServiceOutcome<Boolean> updateCandidateEducationStatusAndRemark(ApprovalStatusRemarkDto approvalStatusRemarkDto);

	ServiceOutcome<Boolean> updateCandidateExperienceStatusAndRemark(ApprovalStatusRemarkDto approvalStatusRemarkDto);

	ServiceOutcome<Boolean> updateCandidateAddressStatusAndRemark(ApprovalStatusRemarkDto approvalStatusRemarkDto);

	ServiceOutcome<Boolean> candidateApplicationFormApproved(String candidateCode, MultipartFile criminalVerificationDocument,Long criminalVerificationColorId, MultipartFile globalDatabseCaseDetailsDocument, Long globalDatabseCaseDetailsColorId, String reportType);

	ServiceOutcome<List<SuspectClgMaster>> getAllSuspectClgList();

	ServiceOutcome<List<SuspectEmpMaster>> getAllSuspectEmpList(Long organizationId, int pageNumber, int pageSize);

	ServiceOutcome<Boolean> relationshipAddressVerification(String candidateCafRealation, MultipartFile document);

	ServiceOutcome<DashboardDto> getPendingDetailsStatusAndCount(DashboardDto dashboardDto);

	ServiceOutcome<Candidate> saveIsFresher(String candidateCode, Boolean isFresher);

	ServiceOutcome<CandidationApplicationFormDto> candidateApplicationFormDetailsExceptCandidate(String candidateCode);

	ServiceOutcome<CandidateCafExperience> updateCandidateExperience(CandidateCafExperienceDto candidateCafExperienceDto);

	ServiceOutcome<CandidateCafAddress> saveCandidateAddress(CandidateCafAddressDto candidateCafAddressDto);

	ServiceOutcome<List<String>> getServiceConfigCodes(String candidateCode, Long orgId);

	ServiceOutcome<Candidate> setIsLoaAccepted(String candidateCode);
	
	CandidateStatusHistory createCandidateStatusHistory(CandidateStatus candidateStatus,String who);

	ServiceOutcome<List<StatusMaster>> getAllStatus();

	ServiceOutcome<String> generateInterimReport(String candidateCode) throws FileNotFoundException, IOException;

	ServiceOutcome<Candidate> saveIsUanSkipped(String candidateCode, String isUanSkipped);
	
	List<CandidateCafExperience> getCandidateExperienceFromItrAndEpfoByCandidateId(Long candidateId, Boolean formatEpfoDate);

	List<CandidateCafExperience> getCandidateExperienceFromItrAndEpfoByCandidateId_old(Long candidateId, Boolean formatEpfoDate);
	
	List<CandidateCafExperience> getCandidateExperienceByCandidateId(Long candidateId);
	
	CandidateVerificationState getCandidateVerificationStateByCandidateId(Long candidateId);
	
	CandidateVerificationState addOrUpdateCandidateVerificationStateByCandidateId(Long candidateId,CandidateVerificationState candidateVerificationState);
	
	List<CandidateCafEducationDto> getAllCandidateEducationByCandidateId(Long candidateId);
	
	List<CandidateCafAddressDto> getCandidateAddress(Candidate candidate);

	ServiceOutcome<Boolean> qcPendingstatus(String candidateCode);

	ServiceOutcome<CandidateCafExperience> deletecandidateExpById(Long id);

	ServiceOutcome<CandidateCafEducation> deletecandidateEducationById(Long id);

	ServiceOutcome<CandidateDetailsDto> candidateDLdata(String candidateCode);

	ServiceOutcome<Boolean> updateCandidateVendorProofColor(VendorUploadChecksDto vendorUploadChecksDto);
	
	ServiceOutcome<Boolean> updateCandidateOrganisationScope(OrganisationScope organisationScope);

	ServiceOutcome<Long> getContentById(String CandidateCode);

	ServiceOutcome<Boolean> AddCommentsReports(CandidateCaseDetailsDTO candidateCaseDetailsDTO);

	ServiceOutcome<SuspectEmpMaster> deleteSuspectExpById(Long id);

	ServiceOutcome<Boolean> updateSpectEMPloyee(SuspectEmpMasterDto suspectEmpMasterDto);


	ServiceOutcome<UserDto> agentCreatepasswrd(UserDto user);
	
	
	
	ServiceOutcome<UanSearchData> saveUan(UanSearchDataDTO uanSave);
	//ServiceOutcome<List<UanSearchData>> bulkUan(List<BulkUanDTO> bulkUan);
	ServiceOutcome<List<UanSearchData>> retriveBulkUanData(String bulkUanId);

	ServiceOutcome<List<UanSearchEpfoDTO>> getEpfoData(Map<String, String> data);

	ServiceOutcome<String> suspectEmpMasterCheck(String companyName, Long oganizationId);

	void updateCandidateExperienceDetails(String candidateCode);
	
	ServiceOutcome<DashboardDto> searchAllCandidate(SearchAllCandidateDTO searchAllcandidate);
	
	ServiceOutcome<SuspectEmpMaster> deleteAllSuspectById(List<Long> id);
	ServiceOutcome<Organization> findOrganization(String candidateCode);
	ServiceOutcome<List<SuspectEmpMaster>> searchDnh(String searchData);
	
	ServiceOutcome<List<Long>> oldCandidatesPurge(Long orgId);
	ServiceOutcome<List<UanSearchData>> uanSearchDashboardFilter(UanSearchDashboardFilterDTO uanSearchDashboardFilter);
	
	ServiceOutcome<Color> getCandidateReportStatus(String candidateCode);
	ServiceOutcome<String> updateCandidateReportStatus(String candidateCode, Long colorId);
	
	ServiceOutcome<DashboardDto> getAllPendingCandidateList(DashboardDto dashboardDto);
	
	ServiceOutcome<List> bulkUanNew(MultipartFile file);
	
	ServiceOutcome<SuspectEmpMaster> removeAllSuspectEmployerByOrgId(Long orgId);

	ServiceOutcome<Boolean> updateCandidateExperienceInCForm(String candidateCafExperience, MultipartFile certificate);

	ServiceOutcome<String> getAuthLetterContent(String candidateCode);

	void postStatusToOrganization(String candidateCode);

	ServiceOutcome<String> getCandidateLOAFile(String candidateCode,String dashboardStatus);

	ServiceOutcome<String> candidateApplicationFormSubmit(String candidateCode);

	ServiceOutcome<String> updateCandidateExperienceResult(EmploymentResultUpdateReqDto employmentResultUpdateReqDto);

	ServiceOutcome<String> saveCaseReinitDetails(String candidateCode, String caseReinitDate);

	ServiceOutcome<Boolean> reFetchUANData(CandidateInvitationSentDto candidateInvitationSentDto);

	ServiceOutcome<String> experienceFromResume(MultipartFile base64, String candidateCode);

	ServiceOutcome<List> bulkPanToUan(MultipartFile file);

	ServiceOutcome<Boolean> reFetchPanToUANData(CandidateInvitationSentDto candidateInvitationSentDto);

	ServiceOutcome<String> forwardReport(List<Long> candidateIds, String emailIds);

	ServiceOutcome<List<CandidateDetailsDtoForPanToUan>> getFailedPanToUanCandidateList(DashboardDto dashboardDto);

	ServiceOutcome<String> setPanToUanResponse(CandidateDetailsDtoForPanToUan candidateDetails);

	ServiceOutcome<Boolean> reFetchPANTOUANDataForAvailableUANs();

	//method to calculate tenuer gap
		long calculateTenuerGap(CandidateCafExperience candidateCafExperience,List<CandidateCafExperience> candidateCafExperiences);

		//method to calculate ITR filing gap
		long calculateITRFilingGap(List<ITRData> itrList);

	ServiceOutcome<QcRemarksDto> addUpdateQcRemarks(QcRemarksDto requestBody);

	public ServiceOutcome<List<QcRemarksDto>> getQcRemarks(String candidateCode);

	ServiceOutcome<String> deleteQcRemarkByQcRemarksId(Long qcRemarksID);
}