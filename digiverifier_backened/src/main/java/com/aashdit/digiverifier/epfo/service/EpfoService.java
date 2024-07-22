package com.aashdit.digiverifier.epfo.service;

import org.springframework.http.ResponseEntity;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.candidate.dto.BulkUanDTO;
import com.aashdit.digiverifier.epfo.dto.EpfoDetailsDto;

public interface EpfoService {

	ServiceOutcome<EpfoDetailsDto>  getEpfoCaptcha(String candidateId);

	ServiceOutcome<String> getEpfodetail(EpfoDetailsDto epfoDetails);

	ServiceOutcome<String> getEpfodetailNew(EpfoDetailsDto epfoDetails);

	Boolean processEpfoDataForUANCandidate(ResponseEntity<String> response, BulkUanDTO details);

	ServiceOutcome<EpfoDetailsDto> epfoLoginCaptcha(String candidateCode);

	ServiceOutcome<EpfoDetailsDto> epfoOTPScreenCaptcha(EpfoDetailsDto epfoDetails);

	ServiceOutcome<String> epfoOTPCaptchaSubmit(EpfoDetailsDto epfoDetails);
	
}
