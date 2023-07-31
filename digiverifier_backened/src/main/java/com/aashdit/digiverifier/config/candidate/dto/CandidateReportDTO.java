package com.aashdit.digiverifier.config.candidate.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.aashdit.digiverifier.config.admin.dto.VendorUploadChecksDto;
import com.aashdit.digiverifier.config.candidate.model.CandidateCafExperience;
import com.aashdit.digiverifier.config.candidate.model.OrganisationScope;
import com.aashdit.digiverifier.config.superadmin.Enum.ReportType;
import com.aashdit.digiverifier.config.superadmin.Enum.VerificationStatus;

import lombok.Data;

@Data
public class CandidateReportDTO {
	private String name;

	private String comments;
	
	private String applicantId;
	
	private String dob;
	
	private String contactNo;
	
	private String emailId;
	
	private String referenceId;
	
	private String experience;
	
	private String URNno;
	
	private String organizationName;
	
	private String organizationLocation;
	
	private String organizationLogo;
	
	private String organizationDOJ;
	
	private String jobLocation;
	
	private String project;
	
	private ReportType reportType;
	
	private String caseInitiationDate;
	
	private String interimReportDate;
	
	private String finalReportDate;
	
	private String SrReportDate;
	
	private VerificationStatus verificationStatus;
	
	private List<ExecutiveSummaryDto> executiveSummaryList;
	
	private List<IDVerificationDTO> idVerificationDTOList;

	private List<CandidateCafExperienceDto> employementDetailsDTOlist;
	
	private PanCardVerificationDto panCardVerification;
	
	private AadharVerificationDTO aadharCardVerification;
	
	private List<AddressVerificationDto> addressVerificationDtoList;
	
	private List<EmploymentVerificationDto> employmentVerificationDtoList;
	
	private List<EmploymentTenureVerificationDto> employmentTenureVerificationDtoList;
	
	private List<EducationVerificationDTO> educationVerificationDTOList;
	
	private String experienceCalculationResult;
	
//	private int noOfYearsToBeVerified;
	
	private List<CandidateCafExperience> inputExperienceList;
	
	private EPFODataDto epfoData;
	
	private ITRDataDto itrData;
	
    private String IdConsolidatedStatus;
	
	private String educationConsolidatedStatus;
	
	private String employmentConsolidatedStatus;
	
	private String addressConsolidatedStatus;
	
	private List<VendorUploadChecksDto> vendorProofDetails;
	
	private String candidate_reportType;
	
	private ItrEpfoHeaderDetails itrEpfoHeaderDetails;
	
	// added for executive summary
	private String currentEmployment;
	
	private List<CandidateCafExperienceDto> serviceHistory;
	
	private String accountName;
	
	private OrganisationScope organisationScope;
	
	private String totalTenure;
	
	private String noOfYearsToBeVerified;
	
	private Map<String, String> globalDatabaseCheck;

	private Map<String, String> criminalCheck;
	
	private Map<String, String> educationCheck;
	
	// fields for wipro final report
	private String highestQualification;
	
	private String courseName;
	
	private String universityName;
	
	private String rollNo;
	
	private String yearOfPassing;
	
	private String eduCustomRemark;
	
	private String pfVerified;
	
	private String uanVerified; 
	
	private Date dateOfJoin;
	
	private Date dateOfExit;
}
