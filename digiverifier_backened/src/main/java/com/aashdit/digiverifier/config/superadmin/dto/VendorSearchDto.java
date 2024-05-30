package com.aashdit.digiverifier.config.superadmin.dto;

import java.util.List;

import com.aashdit.digiverifier.config.admin.dto.vendorChecksDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class VendorSearchDto {

private String fromDate;
	
	private String toDate;
	
	private List<Long> organizationIds;
	
	private List<ReportResponseDto> reportResponseDtoList;
	
	private List<Long> agentIds;
	
	private String statusCode;
	
	private List<vendorChecksDto> candidateDetailsDto;
	
	private String organizationName;
 
	private Long userId;
	
	private Long vendorStatusmasterId;
	
//	private List<vendorChecksDto> vendorChecksDto;
	
	public VendorSearchDto(String fromDate, String toDate, List<Long> organizationIds,
			List<ReportResponseDto> reportResponseDtoList, List<Long> agentIds) {
		super();
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.organizationIds = organizationIds;
		this.reportResponseDtoList = reportResponseDtoList;
		this.agentIds = agentIds;
	}
}
