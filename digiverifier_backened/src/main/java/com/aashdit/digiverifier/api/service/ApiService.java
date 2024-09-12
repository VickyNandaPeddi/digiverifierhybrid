package com.aashdit.digiverifier.api.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.aashdit.digiverifier.api.model.ApiCandidate;
import com.aashdit.digiverifier.common.dto.EPFOResponseDto;
import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.candidate.dto.CandidateReportDTO;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatusHistory;
import com.aashdit.digiverifier.common.dto.EPFOResponseDto;


public interface ApiService {

	ServiceOutcome<List> saveCandidateInformation(List<ApiCandidate> candidateList);

	CandidateStatusHistory createCandidateStatusHistory(CandidateStatus candidateStatus, String who);

	ServiceOutcome<String> getContentByCandidateCode(String candidateCode);

	ResponseEntity<byte[]> downloadCandidateStatusTrackerReport(String candidateCode);

	ServiceOutcome<EPFOResponseDto> getEPFODataAPI(String candidateCode);

	ServiceOutcome<List> getInvitaionLink(List<ApiCandidate> candidateList);
}
