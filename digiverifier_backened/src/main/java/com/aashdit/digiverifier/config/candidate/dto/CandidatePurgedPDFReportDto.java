package com.aashdit.digiverifier.config.candidate.dto;

import java.util.List;

import com.aashdit.digiverifier.config.superadmin.dto.CandidateDetailsForPurgedReport;
import com.aashdit.digiverifier.config.superadmin.dto.ReportResponseDto;

import lombok.Data;

@Data
public class CandidatePurgedPDFReportDto {
	
	private String orgName;
	
	private String billingAddress;
	
	private List<ReportResponseDto> pwdvMprReportDtoList;
	
	private ReportResponseDto summaryCompanyWiseTotal;
	
	private Integer summaryCompanyWiseGrandTotal;
	
	private List<ReportResponseDto> pwdvMprReportDtoAgentList;
	
	private ReportResponseDto summaryAgentWiseTotal;
	
	private Integer summaryAgentWiseGrandTotal;
	
	private String strToDate;
	
	private String strFromDate;
	
	private List<CandidateDetailsForPurgedReport> reportDeliveredList;
	
	private List<CandidateDetailsForPurgedReport> processDeclinedList;
	
	private List<CandidateDetailsForPurgedReport> invitationExpiredList;
}
