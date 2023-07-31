package com.aashdit.digiverifier.config.superadmin.service;

import java.util.List;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.candidate.dto.CandidateReportDTO;
import com.aashdit.digiverifier.config.superadmin.Enum.ReportType;
import com.aashdit.digiverifier.config.superadmin.dto.DateRange;
import com.aashdit.digiverifier.config.superadmin.dto.ReportSearchDto;
import com.aashdit.digiverifier.config.superadmin.dto.VendorSearchDto;
import com.aashdit.digiverifier.config.superadmin.dto.VendorUtilizationReportDto;

public interface ReportService {

	ServiceOutcome<ReportSearchDto> getCustomerUtilizationReportData(ReportSearchDto reportSearchDto);

	ServiceOutcome<ReportSearchDto> getCustomerUtilizationReportByAgent(ReportSearchDto reportSearchDto);

	ServiceOutcome<ReportSearchDto> getCanididateDetailsByStatus(ReportSearchDto reportSearchDto);

	ServiceOutcome<ReportSearchDto> eKycReportData(ReportSearchDto reportSearchDto);
	
	ServiceOutcome<CandidateReportDTO> generateDocument(String candidateCode,String token, ReportType documentType);
	
	ServiceOutcome<CandidateReportDTO> generateDocumentWipro(String candidateCode,String token, ReportType documentType);
	
    ServiceOutcome<VendorSearchDto> getVendorDetailsByStatus(VendorSearchDto reportSearchDto);
	
	ServiceOutcome<ReportSearchDto> getVendorUtilizationReportData(ReportSearchDto reportSearchDto);

	ServiceOutcome<List<VendorUtilizationReportDto>> getVendorDetailsByDateRange(DateRange dateRange);
	
}
