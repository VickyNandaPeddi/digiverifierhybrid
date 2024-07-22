package com.aashdit.digiverifier.epfo.remittance.service;

import java.util.List;


import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.epfo.remittance.dto.RemittanceDataFromApiDto;
import com.aashdit.digiverifier.epfo.remittance.dto.RemittanceEmployerRequestDto;

public interface RemittanceService {
	
	ServiceOutcome<List<RemittanceDataFromApiDto>> getremittanceRecords(String candidateCode, String flow);
	
	ServiceOutcome<List<RemittanceDataFromApiDto>> getremittanceRecordsForEmployer(RemittanceEmployerRequestDto remittanceEmployerRequestDto);
	
	ServiceOutcome<String> deletedRemittanceRecord(String candidateCode, String memberId, String year);

	ServiceOutcome<String> getRemittanceCaptcha(String candidateCode);
	
	ServiceOutcome<List<RemittanceDataFromApiDto>> getLTIMRemittanceRecords(String candidateCode, String flow);

}
