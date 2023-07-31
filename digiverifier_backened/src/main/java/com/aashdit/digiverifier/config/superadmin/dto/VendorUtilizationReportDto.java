package com.aashdit.digiverifier.config.superadmin.dto;

import java.math.BigInteger;
import java.util.List;

import com.aashdit.digiverifier.config.admin.dto.vendorChecksDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorUtilizationReportDto {
	
	private String vendorName;
	
	private BigInteger caseInitiatedBy;
	
	private String refNo;
	
	private String candidateName;
	
	private String caseAssignedDate;
	
	private String reportSubmittedDate;
	
	private int reportColorCode;
	
	private Long organizationId;

}
