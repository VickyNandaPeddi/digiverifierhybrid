package com.aashdit.digiverifier.config.candidate.dto;

import java.util.Date;
import java.util.List;

import com.aashdit.digiverifier.config.superadmin.Enum.ReportType;
import com.aashdit.digiverifier.config.superadmin.Enum.VerificationStatus;

import lombok.Data;

@Data
public class FinalReportDto {
	
	private String name;
	
	private String applicantId;
	
	private String caseInitiationDate;
	
	private String finalReportDate;
	
	private String highestQualification;
	
	private String courseName;
	
	private String universityName;
	
	private String rollNo;
	
	private String yearOfPassing;
	
	private String currentEmployment;
	
	private Date dateOfJoin;
	
	private Date dateOfExit;
	
	private String currentAddress;
	
	private String permanentAddress;
	
	private ReportType reportType;
	
	private String organizationName;
	
	private VerificationStatus verificationStatus;
	
	private List<ExecutiveSummaryDto> executiveSummaryList;
	
    private String IdConsolidatedStatus;
	
	private String educationConsolidatedStatus;
	
	private String employmentConsolidatedStatus;
	
	private String addressConsolidatedStatus;
	
	private String uanVerified;
	
	private String pfVerified;
	
	private String eduCustomRemark;
	
	private String expCustomRemark;
	
    private String caseReinitDate;
	
	private String interimAmendedDate;
}
