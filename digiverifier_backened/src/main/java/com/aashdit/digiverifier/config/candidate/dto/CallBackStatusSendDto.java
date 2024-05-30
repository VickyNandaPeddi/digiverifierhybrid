package com.aashdit.digiverifier.config.candidate.dto;

import java.util.Date;

import lombok.Data;

@Data
public class CallBackStatusSendDto {

	private String candidateCode;
	private String status;
	private Date submittedOn;
}
