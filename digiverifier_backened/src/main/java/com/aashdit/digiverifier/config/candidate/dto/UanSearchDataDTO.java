package com.aashdit.digiverifier.config.candidate.dto;

import java.util.Date;

import javax.persistence.Entity;

import lombok.Data;

@Data
public class UanSearchDataDTO {
	
	private String applicantId;
	private String uanusername;
	private String uploadedBy;
	private Date uploadedOn;
	private int totalRecordUploaded;
	private int totalRecordFetched;
	private int totalRecordFailed;
	private String epfoResponse;
	
	private String msg;
	private boolean bulkUanSearch;
	private String bulkUanId;

}
