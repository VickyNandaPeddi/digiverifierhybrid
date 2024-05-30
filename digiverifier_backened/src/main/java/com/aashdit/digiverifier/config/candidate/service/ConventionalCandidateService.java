/**
 * 
 */
package com.aashdit.digiverifier.config.candidate.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.candidate.dto.CandidateDetailsDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateInvitationSentDto;
import com.aashdit.digiverifier.config.candidate.dto.SearchAllCandidateDTO;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatusHistory;
import com.aashdit.digiverifier.config.candidate.model.CandidateVerificationState;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateStatusHistory;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateVerificationState;
import com.aashdit.digiverifier.config.superadmin.dto.DashboardDto;

/**
 * Nambi
 */
public interface ConventionalCandidateService {
	
	ServiceOutcome<List> saveConventionalCandidateInformation(MultipartFile file,String candidateCode, boolean hybridToConventionalCandidateFlow,String accountName);

	ServiceOutcome<DashboardDto> conventionalGetReportDeliveryDetailsStatusAndCount(DashboardDto dashboardDto);

	ServiceOutcome<DashboardDto> conventionalGetCandidateStatusAndCount(DashboardDto dashboardDto);
	
	void postStatusToOrganization(String candidateCode);

	ConventionalCandidateStatusHistory createConventionalCandidateStatusHistory(ConventionalCandidateStatus candidateStatus,String who);

	ServiceOutcome<Boolean> conventionalInvitationSent(CandidateInvitationSentDto candidateInvitationSentDto);
	
	ServiceOutcome<ConventionalCandidateStatus> conventionalGetCandidateStatusByCandidateCode(String code);
	
	ServiceOutcome<DashboardDto> getAllConventionalCandidateList(DashboardDto dashboardDto);

	ServiceOutcome<Boolean> conventionalCandidateApplicationFormApproved(String candidateCode, MultipartFile criminalVerificationDocument,Long criminalVerificationColorId, MultipartFile globalDatabseCaseDetailsDocument, Long globalDatabseCaseDetailsColorId, String reportType);

	ServiceOutcome<Boolean> conventionalCancelCandidate(String referenceNo);
	
	ServiceOutcome<CandidateDetailsDto> conventionalUpdateCandidate(CandidateDetailsDto candidateDetails);

	ConventionalCandidateVerificationState getConventionalCandidateVerificationStateByCandidateId(Long candidateId);

	ConventionalCandidateVerificationState addOrUpdateConventionalCandidateVerificationStateByCandidateId(Long candidateId,ConventionalCandidateVerificationState candidateVerificationState);

	ServiceOutcome<DashboardDto> conventionalSearchAllCandidate(SearchAllCandidateDTO searchAllcandidate);



}
