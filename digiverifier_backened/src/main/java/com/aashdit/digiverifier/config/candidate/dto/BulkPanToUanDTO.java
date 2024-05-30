package com.aashdit.digiverifier.config.candidate.dto;

import java.util.Date;

import lombok.Data;

@Data
public class BulkPanToUanDTO {

	private String applicantId;
	private String candidateName;
	private String dob;
	private String pan;
	private String uan;
	private String randomId;
	private int totalRecordUploaded;
	private boolean bulkUanSearch;
	
	
	private String uploadedBy;
	private Date uploadedOn;
	private int totalRecordFetched;
	private int totalRecordFailed;
	private String epfoResponse;
	
	private String msg;
	private String bulkUanId;

	private String candidateCode;
}
