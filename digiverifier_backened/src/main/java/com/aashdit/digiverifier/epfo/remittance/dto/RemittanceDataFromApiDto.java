package com.aashdit.digiverifier.epfo.remittance.dto;

import java.util.Date;


import lombok.Data;

@Data
public class RemittanceDataFromApiDto {

	private String candidateCode;
	private String name;
	private String company;
	private String memberId;
	private Date createdOn;
	private String image;
	private String color;
	private String year;
	
	private String remark;
}
