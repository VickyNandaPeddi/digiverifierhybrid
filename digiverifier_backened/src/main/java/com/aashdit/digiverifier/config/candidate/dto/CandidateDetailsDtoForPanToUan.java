package com.aashdit.digiverifier.config.candidate.dto;

import org.springframework.http.ResponseEntity;

import lombok.Data;

@Data
public class CandidateDetailsDtoForPanToUan {
	private String candidateName;
	private String panNumber;
	private Long candidateId;
	private String dob;
	private String applicantId;
	
	private ResponseDto response;
	
	@Data
	public class ResponseDto {
		private String code;
		private String message;
		private String success;
	}
}
