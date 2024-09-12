/**
 * 
 */
package com.aashdit.digiverifier.config.candidate.dto;

import lombok.Data;

/**
 * Nambi
 */
@Data
public class ConventionalEmploymentVerificationDto {
	
	private String EmploymentCheck;
	private String tenureFrom;
	private String tenureTo;
	private String PeriodVerified;
	private String status;
	private String totalYearsOfExperience;

}
