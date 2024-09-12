package com.aashdit.digiverifier.config.candidate.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UpdateITRDto {
	private String candidateCode;
	
	private String form26AsResponse;
}
