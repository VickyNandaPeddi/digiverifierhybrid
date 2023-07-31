package com.aashdit.digiverifier.config.admin.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VendorInitiatDto {

	
	private Long vendorId;
	
	private Long sourceId;

	private Long candidateId;

	private String documentname;

	private String candidateName;

	private String dateOfBirth;

	private String contactNo;

	private String fatherName;

	private String address;

	private String alternateContactNo;

	private String typeOfPanel;

	private Long vendorCheckStatusMasterId;
	
	private String value;


	
	
	
   
}
