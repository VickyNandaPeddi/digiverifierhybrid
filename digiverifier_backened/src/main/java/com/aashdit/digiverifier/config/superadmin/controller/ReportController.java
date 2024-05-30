package com.aashdit.digiverifier.config.superadmin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.candidate.dto.CandidateReportDTO;
import com.aashdit.digiverifier.config.superadmin.Enum.ReportType;
import com.aashdit.digiverifier.config.superadmin.dto.DateRange;
import com.aashdit.digiverifier.config.superadmin.dto.ReportSearchDto;
import com.aashdit.digiverifier.config.superadmin.dto.VendorSearchDto;
import com.aashdit.digiverifier.config.superadmin.dto.VendorUtilizationReportDto;
import com.aashdit.digiverifier.config.superadmin.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;

//import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/api/report")
public class ReportController {
	
	@Autowired
	private ReportService reportService;
	
	@Operation(summary ="Customer Utilization Report")
	@RequestMapping(value = "/getCustomerUtilizationReport", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<ServiceOutcome<ReportSearchDto>> getCustomerUtilizationReport(@RequestHeader("Authorization") String authorization,@RequestBody(required=false) ReportSearchDto reportSearchDto) {
		ServiceOutcome<ReportSearchDto> svcSearchResult=  reportService.getCustomerUtilizationReportData(reportSearchDto);
		return new ResponseEntity<ServiceOutcome<ReportSearchDto>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Customer Utilization Report")
	@PostMapping("/getCustomerUtilizationReportByAgent")
	public ResponseEntity<ServiceOutcome<ReportSearchDto>> getCustomerUtilizationReportByAgent(@RequestHeader("Authorization") String authorization,@RequestBody ReportSearchDto reportSearchDto) {
		ServiceOutcome<ReportSearchDto> svcSearchResult=  reportService.getCustomerUtilizationReportByAgent(reportSearchDto);
		return new ResponseEntity<ServiceOutcome<ReportSearchDto>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Candidate Details By Status")
	@PostMapping("/getCanididateDetailsByStatus")
	public ResponseEntity<ServiceOutcome<ReportSearchDto>> getCanididateDetailsByStatus(@RequestHeader("Authorization") String authorization,@RequestBody ReportSearchDto reportSearchDto) {
		ServiceOutcome<ReportSearchDto> svcSearchResult=  reportService.getCanididateDetailsByStatus(reportSearchDto);
		return new ResponseEntity<ServiceOutcome<ReportSearchDto>>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="E-KYC Report")
	@RequestMapping(value = "/eKycReport", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<ServiceOutcome<ReportSearchDto>> eKycReport(@RequestHeader("Authorization") String authorization,@RequestBody(required=false) ReportSearchDto reportSearchDto) {
		ServiceOutcome<ReportSearchDto> svcSearchResult=  reportService.eKycReportData(reportSearchDto);
		return new ResponseEntity<ServiceOutcome<ReportSearchDto>>(svcSearchResult, HttpStatus.OK);
	}
	
	@GetMapping(value = "")
	public ResponseEntity getReport(@RequestHeader("Authorization") String authorization,@RequestParam String candidateCode,@RequestParam
		ReportType type,@RequestParam(name = "overrideReportStatus", required = false) String overrideReportStatus) {
		ServiceOutcome svcSearchResult = new ServiceOutcome();
		ServiceOutcome<CandidateReportDTO> data = reportService.generateDocument(candidateCode, authorization, type,overrideReportStatus);
		svcSearchResult.setData(data.getMessage());
		svcSearchResult.setStatus(data.getStatus());
		svcSearchResult.setOutcome(data.getOutcome());
		svcSearchResult.setMessage(String.valueOf(data.getData().getCandidate_reportType()));
		return new ResponseEntity<ServiceOutcome<ReportSearchDto>>(svcSearchResult, HttpStatus.OK);
	}
	
	@GetMapping(value = "/getConventionalReport")
	public ResponseEntity getConventionalReport(@RequestHeader("Authorization") String authorization,@RequestParam String candidateCode,@RequestParam
		ReportType type,@RequestParam(name = "overrideReportStatus", required = false) String overrideReportStatus,@RequestParam("conventionalReport") boolean conventionalReport) {
		ServiceOutcome svcSearchResult = new ServiceOutcome();
		ServiceOutcome<CandidateReportDTO> data = reportService.generateConventionalDocument(candidateCode, authorization, type,overrideReportStatus,conventionalReport);
		svcSearchResult.setData(data.getMessage());
		svcSearchResult.setStatus(data.getStatus());
		svcSearchResult.setOutcome(data.getOutcome());
		svcSearchResult.setMessage(String.valueOf(data.getData().getCandidate_reportType()));
		return new ResponseEntity<ServiceOutcome<ReportSearchDto>>(svcSearchResult, HttpStatus.OK);
	}
	
	    @Operation(summary ="Vendor Utilization Report")
		@RequestMapping(value = "/getVendorUtilizationReport", method = { RequestMethod.GET, RequestMethod.POST })
		public ResponseEntity<ServiceOutcome<ReportSearchDto>> getVendorUtilizationReport(@RequestHeader("Authorization") String authorization,@RequestBody(required=false) ReportSearchDto reportSearchDto) {
			ServiceOutcome<ReportSearchDto> svcSearchResult=  reportService.getVendorUtilizationReportData(reportSearchDto);
			return new ResponseEntity<ServiceOutcome<ReportSearchDto>>(svcSearchResult, HttpStatus.OK);
		}
		
		@Operation(summary ="Vendor Details By Status")
		@PostMapping("/getVendorDetailsByStatus")
		public ResponseEntity<ServiceOutcome<VendorSearchDto>> getVendorDetailsByStatus(@RequestHeader("Authorization") String authorization,@RequestBody VendorSearchDto reportSearchDto) {
			ServiceOutcome<VendorSearchDto> svcSearchResult=  reportService.getVendorDetailsByStatus(reportSearchDto);
			return new ResponseEntity<ServiceOutcome<VendorSearchDto>>(svcSearchResult, HttpStatus.OK);
		}
	
		@Operation(summary ="Vendor Details By Date Range")
		@PostMapping("/getVendorDetailsByDateRange")
		public ResponseEntity<ServiceOutcome<List<VendorUtilizationReportDto>>> getVendorDetailsByDateRange(@RequestHeader("Authorization") String authorization,@RequestBody DateRange dateRange) {
			ServiceOutcome<List<VendorUtilizationReportDto>> svcSearchResult=  reportService.getVendorDetailsByDateRange(dateRange);
			return new ResponseEntity<ServiceOutcome<List<VendorUtilizationReportDto>>>(svcSearchResult, HttpStatus.OK);
		}
		
		@Operation(summary ="Download The Candidate Status Tracker Report")
		@PostMapping("/download/candidateStatusTrackerReport")
	    public ResponseEntity<byte[]> downloadCandidateStatusTrackerReport(@RequestHeader("Authorization") String authorization,@RequestBody ReportSearchDto reportSearchDto) {
	    	return reportService.downloadCandidateStatusTrackerReport(reportSearchDto);
	    }
	    
	    @Operation(summary ="Download The Candidate Employment Tracker Report")
		@PostMapping("/download/downloadCandidateEmploymentReport")
	    public ResponseEntity<byte[]> downloadCandidateEmploymentReport(@RequestHeader("Authorization") String authorization,@RequestBody ReportSearchDto reportSearchDto) {
	    	return reportService.downloadCandidateEmploymentReport(reportSearchDto);
	    }
}
