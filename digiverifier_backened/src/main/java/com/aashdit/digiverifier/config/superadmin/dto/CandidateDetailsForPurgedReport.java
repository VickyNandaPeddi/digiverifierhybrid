package com.aashdit.digiverifier.config.superadmin.dto;

import lombok.Data;

@Data
public class CandidateDetailsForPurgedReport {

	private String createdByUserFirstName;
	
	private String createdByUserLastName;
	
	private String applicantId;

	private String candidateName;
	
	private String dateOfBirth;
	
	private String organizationName;
	
	private String uploadedDate;
	
	private String inviteSentDate;

	private String qcCreatedOn;

	private String interimDate;

	private String processDeclinedDate;

	private String invitationExpiredDate;

	private String purgedDate;
	
}
