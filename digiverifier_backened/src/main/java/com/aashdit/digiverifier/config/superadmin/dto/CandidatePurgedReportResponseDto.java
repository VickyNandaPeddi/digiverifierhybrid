package com.aashdit.digiverifier.config.superadmin.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidatePurgedReportResponseDto {

	private String fromDate;
	
	private String toDate;
	
	private List<Long> organizationIds;
	
	private List<ReportResponseDto> reportResponseDtoList;
	
	private List<Long> agentIds;
	
	private String statusCode;
	
	private List<CandidateDetailsForPurgedReport> candidateDetailsDto;
	
	private String organizationName;
 
	private Long userId;

	public CandidatePurgedReportResponseDto(String fromDate, String toDate, List<Long> organizationIds,
			List<ReportResponseDto> reportResponseDtoList, List<Long> agentIds) {
		super();
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.organizationIds = organizationIds;
		this.reportResponseDtoList = reportResponseDtoList;
		this.agentIds = agentIds;
	}
}
