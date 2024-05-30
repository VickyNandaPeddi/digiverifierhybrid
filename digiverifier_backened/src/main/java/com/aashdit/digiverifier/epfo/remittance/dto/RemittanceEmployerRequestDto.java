package com.aashdit.digiverifier.epfo.remittance.dto;

import java.util.Date;

import lombok.Data;

@Data
public class RemittanceEmployerRequestDto {

	private String candidateCode;
	private String company;
	
	private String doe;
	private String doj;
	
	private String memberId;
	private String name;
	
	private String remittanceDates;
	private String uan;
	
	private String remittanceCaptchaText;
}
