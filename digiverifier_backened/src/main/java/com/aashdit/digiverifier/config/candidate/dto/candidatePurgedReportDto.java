package com.aashdit.digiverifier.config.candidate.dto;

public interface candidatePurgedReportDto {
	
	public String getCreatedByUserFirstName();
	
	public String getCreatedByUserLastName();
	
	public String getApplicantId();

	public String getCandidateName();
	
	public String getDateOfBirth();
	
	public String getOrganizationName();
	
	public String getUploadedDate();
	
	public String getInviteSentDate();

	public String getQcCreatedOn();

	public String getInterimDate();

	public String getProcessDeclinedDate();

	public String getInvitationExpiredDate();

	public String getPurgedDate();

}
