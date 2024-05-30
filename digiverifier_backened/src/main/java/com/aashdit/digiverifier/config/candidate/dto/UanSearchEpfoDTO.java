package com.aashdit.digiverifier.config.candidate.dto;

import java.util.Date;

import lombok.Data;

@Data
public class UanSearchEpfoDTO {
	
	String uan;
	String name;
	String company;
	Date doe;
	Date doj;
	String applicantId;
	String bulkId;
	String epfoResponse;
	

}
