package com.aashdit.digiverifier.config.superadmin.service;

import static com.aashdit.digiverifier.digilocker.service.DigilockerServiceImpl.DIGIVERIFIER_DOC_BUCKET_NAME;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.util.TempFile;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import com.aashdit.digiverifier.common.ContentRepository;
import com.aashdit.digiverifier.common.enums.ContentCategory;
import com.aashdit.digiverifier.common.enums.ContentSubCategory;
import com.aashdit.digiverifier.common.enums.ContentType;
import com.aashdit.digiverifier.common.enums.FileType;
import com.aashdit.digiverifier.common.model.Content;
import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.admin.dto.LegalProceedingsDTO;
import com.aashdit.digiverifier.config.admin.dto.VendorUploadChecksDto;
import com.aashdit.digiverifier.config.admin.dto.vendorChecksDto;
import com.aashdit.digiverifier.config.admin.model.CriminalCheck;
import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.admin.model.VendorChecks;
import com.aashdit.digiverifier.config.admin.model.VendorUploadChecks;
import com.aashdit.digiverifier.config.admin.repository.ConventionalAttributesMasterRepository;
import com.aashdit.digiverifier.config.admin.repository.CriminalCheckRepository;
import com.aashdit.digiverifier.config.admin.repository.UserRepository;
import com.aashdit.digiverifier.config.admin.repository.VendorChecksRepository;
import com.aashdit.digiverifier.config.admin.repository.VendorUploadChecksRepository;
import com.aashdit.digiverifier.config.candidate.Enum.CandidateStatusEnum;
import com.aashdit.digiverifier.config.candidate.Enum.IDtype;
import com.aashdit.digiverifier.config.candidate.dto.AadharVerificationDTO;
import com.aashdit.digiverifier.config.candidate.dto.AddressVerificationDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateCafAddressDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateCafEducationDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateCafExperienceDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateFileDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateReportDTO;
import com.aashdit.digiverifier.config.candidate.dto.CandidateStatusDto;
import com.aashdit.digiverifier.config.candidate.dto.EPFODataDto;
import com.aashdit.digiverifier.config.candidate.dto.EducationVerificationDTO;
import com.aashdit.digiverifier.config.candidate.dto.EmploymentTenureVerificationDto;
import com.aashdit.digiverifier.config.candidate.dto.EmploymentVerificationDto;
import com.aashdit.digiverifier.config.candidate.dto.EpfoDataResDTO;
import com.aashdit.digiverifier.config.candidate.dto.ExecutiveSummaryDto;
import com.aashdit.digiverifier.config.candidate.dto.FinalReportDto;
import com.aashdit.digiverifier.config.candidate.dto.IDVerificationDTO;
import com.aashdit.digiverifier.config.candidate.dto.ITRDataDto;
import com.aashdit.digiverifier.config.candidate.dto.ItrEpfoHeaderDetails;
import com.aashdit.digiverifier.config.candidate.dto.PanCardVerificationDto;
import com.aashdit.digiverifier.config.candidate.dto.ServiceHistory;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.CandidateAddComments;
import com.aashdit.digiverifier.config.candidate.model.CandidateCafExperience;
import com.aashdit.digiverifier.config.candidate.model.CandidateCaseDetails;
import com.aashdit.digiverifier.config.candidate.model.CandidateEmailStatus;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatusHistory;
import com.aashdit.digiverifier.config.candidate.model.CandidateVerificationState;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateVerificationState;
import com.aashdit.digiverifier.config.candidate.model.LoaConsentMaster;
import com.aashdit.digiverifier.config.candidate.model.StatusMaster;
import com.aashdit.digiverifier.config.candidate.model.UanSearchData;
import com.aashdit.digiverifier.config.candidate.repository.CandidateAddCommentRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCafAddressRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCafExperienceRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCaseDetailsRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateEmailStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateIdItemsRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusHistoryRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateVerificationStateRepository;
import com.aashdit.digiverifier.config.candidate.repository.ConventionalCandidateStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.ConventionalCandidateVerificationStateRepository;
import com.aashdit.digiverifier.config.candidate.repository.LoaConsentMasterRepository;
import com.aashdit.digiverifier.config.candidate.repository.OrganisationScopeRepository;
import com.aashdit.digiverifier.config.candidate.repository.StatusMasterRepository;
import com.aashdit.digiverifier.config.candidate.repository.UanSearchDataRepository;
import com.aashdit.digiverifier.config.candidate.service.CandidateService;
import com.aashdit.digiverifier.config.candidate.service.ConventionalCandidateService;
import com.aashdit.digiverifier.config.candidate.util.ExcelUtil;
import com.aashdit.digiverifier.config.superadmin.Enum.ExecutiveName;
import com.aashdit.digiverifier.config.superadmin.Enum.ReportType;
import com.aashdit.digiverifier.config.superadmin.Enum.SourceEnum;
import com.aashdit.digiverifier.config.superadmin.Enum.VerificationStatus;
import com.aashdit.digiverifier.config.superadmin.dto.CandidateDetailsForReport;
import com.aashdit.digiverifier.config.superadmin.dto.CheckAttributeAndValueDTO;
import com.aashdit.digiverifier.config.superadmin.dto.DateRange;
import com.aashdit.digiverifier.config.superadmin.dto.OrganizationDto;
import com.aashdit.digiverifier.config.superadmin.dto.ReportResponseDto;
import com.aashdit.digiverifier.config.superadmin.dto.ReportSearchDto;
import com.aashdit.digiverifier.config.superadmin.dto.VendorSearchDto;
import com.aashdit.digiverifier.config.superadmin.dto.VendorUtilizationReportDto;
import com.aashdit.digiverifier.config.superadmin.model.Organization;
import com.aashdit.digiverifier.config.superadmin.model.OrganizationExecutive;
import com.aashdit.digiverifier.config.superadmin.model.Source;
import com.aashdit.digiverifier.config.superadmin.model.ToleranceConfig;
import com.aashdit.digiverifier.config.superadmin.repository.ColorRepository;
import com.aashdit.digiverifier.config.superadmin.repository.OrganizationRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ServiceSourceMasterRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ServiceTypeConfigRepository;
import com.aashdit.digiverifier.config.superadmin.repository.SourceRepository;
import com.aashdit.digiverifier.email.dto.Email;
import com.aashdit.digiverifier.email.dto.EmailProperties;
import com.aashdit.digiverifier.epfo.model.CandidateEPFOResponse;
import com.aashdit.digiverifier.epfo.model.EpfoData;
import com.aashdit.digiverifier.epfo.remittance.dto.RemittanceDataFromApiDto;
import com.aashdit.digiverifier.epfo.remittance.model.RemittanceData;
import com.aashdit.digiverifier.epfo.remittance.repository.RemittanceRepository;
import com.aashdit.digiverifier.epfo.repository.CandidateEPFOResponseRepository;
import com.aashdit.digiverifier.epfo.repository.EpfoDataRepository;
import com.aashdit.digiverifier.gst.dto.GstDataFromApiDto;
import com.aashdit.digiverifier.gst.model.GstData;
import com.aashdit.digiverifier.gst.repository.GstRepository;
import com.aashdit.digiverifier.itr.model.ITRData;
import com.aashdit.digiverifier.itr.repository.CanditateItrEpfoResponseRepository;
import com.aashdit.digiverifier.itr.repository.ITRDataRepository;
import com.aashdit.digiverifier.utils.ApplicationDateUtils;
import com.aashdit.digiverifier.utils.AwsUtils;
import com.aashdit.digiverifier.utils.CommonUtils;
import com.aashdit.digiverifier.utils.DateDifference;
import com.aashdit.digiverifier.utils.DateUtil;
import com.aashdit.digiverifier.utils.EmailSentTask;
import com.aashdit.digiverifier.utils.FileUtil;
import com.aashdit.digiverifier.utils.PdfUtil;
import com.aashdit.digiverifier.utils.SecurityHelper;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

import com.aashdit.digiverifier.epfo.dto.EpfoDataFromDetailsDto;


import org.joda.time.DateTime;
import org.joda.time.Interval;
import java.time.Instant;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);

	public static final String NO_DATA_FOUND = "NO DATA FOUND";
	public static final String FINAL = "FINAL";
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private CandidateStatusRepository candidateStatusRepository;

	@Autowired
	private CandidateEmailStatusRepository candidateEmailStatusRepository;

	@Autowired
	private ColorRepository colorRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private CandidateCafExperienceRepository candidateCafExperienceRepository;

	@Autowired
	private CandidateIdItemsRepository candidateIdItemsRepository;

	@Autowired
	private CandidateCafAddressRepository candidateCafAddressRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrganizationServiceImpl organizationServiceImpl;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private CandidateService candidateService;

	@Autowired
	private CandidateCaseDetailsRepository candidateCaseDetailsRepository;

	@Autowired
	private CandidateStatusHistoryRepository candidateStatusHistoryRepository;

	@Autowired
	private AwsUtils awsUtils;

	@Autowired
	private PdfService pdfService;

	@Autowired
	private EpfoDataRepository epfoDataRepository;

	@Autowired
	private ITRDataRepository itrDataRepository;

	@Autowired
	private CanditateItrEpfoResponseRepository canditateItrEpfoResponseRepository;

	@Autowired
	@Lazy
	private CandidateEPFOResponseRepository candidateEPFOResponseRepository;

	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	@Lazy
	private EmailProperties emailProperties;

	@Autowired
	@Lazy
	private EmailSentTask emailSentTask;

	@Autowired
	private CandidateAddCommentRepository candidateAddCommentRepository;

	@Autowired
	private VendorChecksRepository vendorChecksRepository;

	@Autowired
	private VendorUploadChecksRepository vendorUploadChecksRepository;

	@Autowired
	private OrganisationScopeRepository organisationScopeRepository;

	@Autowired
	private ConventionalAttributesMasterRepository conventionalAttributesMasterRepository;

	@Autowired
	private SourceRepository sourceRepository;
	
	@Autowired
	private ServiceSourceMasterRepository serviceSourceMasterRepository;
	
	@Autowired
	private CandidateRepository candidateRepository;
	
	@Autowired
	private CandidateVerificationStateRepository candidateVerificationStateRepository;
	
	@Autowired
	private RemittanceRepository remittanceRepository;
	
	@Autowired
	private ExcelUtil excelUtil;
	@Autowired
	private StatusMasterRepository statusMasterRepository;
	
	@Autowired
	private CommonUtils commonUtils;
	
	@Autowired
	private GstRepository gstRepository;
	
	@Autowired
	private LoaConsentMasterRepository loaConsentMasterRepository;
	
	@Autowired
	private ServiceTypeConfigRepository serviceTypeConfigRepository;
	
	@Autowired
	 private CriminalCheckRepository criminalCheckRepository;
	
	@Autowired
	private UanSearchDataRepository uanSearchDataRepository;

	@Autowired
	private ConventionalCandidateStatusRepository conventionalCandidateStatusRepository;
	
	@Autowired
	private ConventionalCandidateService conventionalCandidateService;
	
	@Autowired
	private ConventionalCandidateVerificationStateRepository conventionalCandidateVerificationStateRepository;

	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); 

	private static final String emailContent = "<html>\n" + "<head>\n" + "</head>\n" + "<body>\n"
			+ "<p style=\"font-size:12px\">Dear %s, <br><br>Greetings from Team-DigiVerifier <br><br>Please find attached %s report for %s. </p>\n"
			+ "<p style=\"font-size:8px\">\n" + "DISCLAIMER:\n"
			+ "The information contained in this e-mail message and/or attachments to it may contain confidential or privileged information. If you are not the intended recipient, any dissemination, use, review, distribution, printing or copying of the information contained in this e-mail message and/or attachments to it are strictly prohibited. If you have received this communication in error, please notify us by reply e-mail or telephone and immediately and permanently delete the message and any attachments. Thank you.</p>\n"
			+ "<p style=\"font-size:12px\">Regards, <br> Team-DigiVerifier </p>\n" + "\n" + "</body>\n" + "</html>";

	// @Override
	// public ServiceOutcome<List<VendorChecks>> vendorReport(ReportSearchDto
	// reportSearchDto) {
	// ServiceOutcome<List<VendorChecks>> svcSearchResult = new
	// ServiceOutcome<List<VendorChecks>>();
	// System.out.println(reportSearchDto);
	// String strToDate="";
	// String strFromDate="";
	// List<VendorChecks> vendorChecksList=null;
	// try {
	// strToDate=reportSearchDto.getToDate();
	// strFromDate=reportSearchDto.getFromDate();
	// Date startDate = format.parse(strFromDate+" 00:00:00");
	// Date endDate = format.parse(strToDate+" 23:59:59");

	// vendorChecksList=vendorChecksRepository.findAllByCreatedOn(startDate,endDate);
	// System.out.println(vendorChecksList.size()+" newuploades");
	// if(!vendorChecksList.isEmpty()) {
	// svcSearchResult.setData(vendorChecksList);
	// svcSearchResult.setOutcome(true);
	// svcSearchResult.setMessage("SUCCESS");
	// }else {
	// svcSearchResult.setData(null);
	// svcSearchResult.setOutcome(false);
	// svcSearchResult.setMessage("NOT FOUND");
	// }
	// }catch(Exception ex) {
	// log.error("Exception occured in vendorReport method in
	// ReportServiceImpl-->",ex);
	// svcSearchResult.setData(null);
	// svcSearchResult.setOutcome(false);
	// svcSearchResult.setMessage("Something Went Wrong, Please Try After
	// Sometimes.");
	// }
	// return svcSearchResult;

	// }

	@Override
	public ServiceOutcome<ReportSearchDto> getCustomerUtilizationReportData(ReportSearchDto reportSearchDto) {
		ServiceOutcome<ReportSearchDto> svcSearchResult = new ServiceOutcome<ReportSearchDto>();
		List<Object[]> resultList = null;
		User user = SecurityHelper.getCurrentUser();
		String strToDate = "";
		String strFromDate = "";
		List<Long> orgIds = new ArrayList<Long>();
		ReportSearchDto reportSearchDtoObj = null;
		try {
			if (reportSearchDto == null) {
				strToDate = ApplicationDateUtils.getStringTodayAsDDMMYYYY();
				strFromDate = ApplicationDateUtils
						.subtractNoOfDaysFromDateAsDDMMYYYY(new SimpleDateFormat("dd/MM/yyyy").parse(strToDate), 30);
				if (user.getRole().getRoleCode().equals("ROLE_CBADMIN")) {
					orgIds.add(0, 0l);
				} else {
					Long orgIdLong = user.getOrganization().getOrganizationId();
					orgIds.add(orgIdLong);
					reportSearchDto = new ReportSearchDto();
					reportSearchDto.setOrganizationIds(orgIds);
				}

			} else {
				strToDate = reportSearchDto.getToDate();
				strFromDate = reportSearchDto.getFromDate();
				orgIds.addAll(reportSearchDto.getOrganizationIds());
			}
			Date startDate = format.parse(strFromDate + " 00:00:00");
			Date endDate = format.parse(strToDate + " 23:59:59");
			StringBuilder query = new StringBuilder();
			if (reportSearchDto != null && reportSearchDto.getOrganizationIds() != null
					&& reportSearchDto.getOrganizationIds().size() > 0
					&& reportSearchDto.getOrganizationIds().get(0) != 0l) {
				query.append(
						"SELECT t1.orgid,t1.orgname,t1.newuploadcount,t1.reinvitecount,t1.finalreportDeliveredcount,t1.intereimreportDeliveredcount,\n");
				query.append("t2.pendingcount,t1.processDeclinecount,t1.invitationExpiredcount,t1.agentCount\n");
				query.append("FROM\n");
				query.append("(SELECT sm.organization_id AS orgid,organization.organization_name AS orgname,  \n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='NEWUPLOAD' OR statusMaster.status_code ='INVALIDUPLOAD') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS newuploadcount,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='REINVITE') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS reinvitecount,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='FINALREPORT') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS finalreportDeliveredcount,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='PENDINGAPPROVAL') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS intereimreportDeliveredcount,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='PROCESSDECLINED') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS processDeclinecount,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='INVITATIONEXPIRED') AND candidatestatus.last_updated_on BETWEEN ?1 AND ?2 THEN candidatestatus.candidate_id END) AS invitationExpiredcount,\n");
				if (user.getRole().getRoleCode().equals("ROLE_AGENTSUPERVISOR")) {
					query.append(
							"COUNT( DISTINCT CASE WHEN roleMaster.role_code ='ROLE_AGENTHR' AND userMaster.is_user_active =TRUE AND userMaster.user_id IN(?4) THEN userMaster.user_id END) AS agentCount \n");
				} else if (user.getRole().getRoleCode().equals("ROLE_AGENTHR")) {
					query.append("0 AS agentCount \n");
				} else {
					query.append(
							"COUNT( DISTINCT CASE WHEN roleMaster.role_code ='ROLE_AGENTHR' AND userMaster.is_user_active =TRUE THEN userMaster.user_id END) AS agentCount \n");
				}
				query.append("FROM   t_dgv_service_master sm \n");
				query.append(
						"LEFT JOIN t_dgv_organization_master organization ON organization.organization_id = sm.organization_id \n");
				query.append(
						"LEFT JOIN t_dgv_candidate_basic candidatebasic ON candidatebasic.organization_id=organization.organization_id \n");
				query.append(
						"LEFT JOIN t_dgv_candidate_status_history candidatestatushistory ON candidatestatushistory.candidate_id=candidatebasic.candidate_id \n");
				query.append(
						"LEFT JOIN t_dgv_candidate_status candidatestatus ON candidatestatus.candidate_id=candidatebasic.candidate_id \n");
				query.append(
						"LEFT JOIN t_dgv_status_master statusMaster ON statusMaster.status_master_id=candidatestatushistory.status_master_id \n");
				query.append(
						"LEFT JOIN t_dgv_user_master userMaster ON userMaster.orgainzation_id=organization.organization_id \n");
				query.append("LEFT JOIN t_dgv_role_master roleMaster ON roleMaster.role_id =userMaster.role_id  \n");
				query.append("WHERE organization.is_active =TRUE \n");
				if (user.getRole().getRoleCode().equals("ROLE_AGENTSUPERVISOR")
						|| user.getRole().getRoleCode().equals("ROLE_AGENTHR")) {
					query.append("AND candidatebasic.created_by IN (?4) \n");
				}
				query.append("AND organization.organization_id IN(?3)\n");
				query.append(
						"GROUP BY organization.organization_name,sm.organization_id ORDER BY organization.organization_name ASC) t1\n");
				query.append("LEFT JOIN\n");
				query.append("(SELECT sm.organization_id AS orgid,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='INVITATIONSENT' OR statusMaster.status_code='ITR' OR statusMaster.status_code='EPFO' OR statusMaster.status_code='DIGILOCKER' OR statusMaster.status_code='RELATIVEADDRESS') AND candidatestatus.last_updated_on BETWEEN ?1 AND ?2 THEN candidatestatus.candidate_id END) AS pendingcount\n");
				query.append("FROM   t_dgv_service_master sm \n");
				query.append(
						"LEFT JOIN t_dgv_organization_master organization ON organization.organization_id = sm.organization_id \n");
				query.append(
						"LEFT JOIN t_dgv_candidate_basic candidatebasic ON candidatebasic.organization_id=organization.organization_id \n");
				query.append(
						"LEFT JOIN t_dgv_candidate_status candidatestatus ON candidatestatus.candidate_id=candidatebasic.candidate_id\n");
				query.append(
						"LEFT JOIN t_dgv_status_master statusMaster ON statusMaster.status_master_id=candidatestatus.status_master_id \n");
				query.append(
						"LEFT JOIN t_dgv_user_master userMaster ON userMaster.orgainzation_id=organization.organization_id \n");
				query.append("LEFT JOIN t_dgv_role_master roleMaster ON roleMaster.role_id =userMaster.role_id  \n");
				query.append("WHERE organization.is_active =TRUE \n");
				if (user.getRole().getRoleCode().equals("ROLE_AGENTSUPERVISOR")
						|| user.getRole().getRoleCode().equals("ROLE_AGENTHR")) {
					query.append("AND candidatebasic.created_by IN (?4) \n");
				}
				query.append("AND organization.organization_id IN(?3)\n");
				query.append("GROUP BY sm.organization_id ORDER BY organization.organization_name ASC) t2\n");
				query.append("ON t1.orgid=t2.orgid  \n");

				Query squery = entityManager.createNativeQuery(query.toString());
				squery.setParameter(1, startDate);
				squery.setParameter(2, endDate);
				squery.setParameter(3, reportSearchDto.getOrganizationIds());
				if (user.getRole().getRoleCode().equals("ROLE_AGENTSUPERVISOR")) {
					List<User> agentList = userRepository.findAllByAgentSupervisorUserId(user.getUserId());
					if (!agentList.isEmpty()) {
						List<Long> agentIdsList = agentList.parallelStream().map(x -> x.getUserId())
								.collect(Collectors.toList());
						agentIdsList.add(user.getUserId());
						reportSearchDto.setAgentIds(agentIdsList);
						squery.setParameter(4, reportSearchDto.getAgentIds());
					}
				}
				if (user.getRole().getRoleCode().equals("ROLE_AGENTHR")) {
					List<Long> agentIdsList = new ArrayList<>();
					agentIdsList.add(user.getUserId());
					reportSearchDto.setAgentIds(agentIdsList);
					squery.setParameter(4, reportSearchDto.getAgentIds());
				}

				resultList = squery.getResultList();
			} else {
				query.append(
						"SELECT t1.orgid,t1.orgname,t1.newuploadcount,t1.reinvitecount,t1.finalreportDeliveredcount,t1.intereimreportDeliveredcount,\n");
				query.append("t2.pendingcount,t1.processDeclinecount,t1.invitationExpiredcount,t1.agentCount\n");
				query.append("FROM\n");
				query.append("(SELECT sm.organization_id AS orgid,organization.organization_name AS orgname,  \n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='NEWUPLOAD' OR statusMaster.status_code ='INVALIDUPLOAD') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS newuploadcount,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='REINVITE') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS reinvitecount,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='FINALREPORT') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS finalreportDeliveredcount,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='PENDINGAPPROVAL') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS intereimreportDeliveredcount,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='PROCESSDECLINED') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS processDeclinecount,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='INVITATIONEXPIRED') AND candidatestatus.last_updated_on BETWEEN ?1 AND ?2 THEN candidatestatus.candidate_id END) AS invitationExpiredcount,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN roleMaster.role_code ='ROLE_AGENTHR' AND userMaster.is_user_active =TRUE THEN userMaster.user_id END) AS agentCount \n");
				query.append("FROM   t_dgv_service_master sm \n");
				query.append(
						"LEFT JOIN t_dgv_organization_master organization ON organization.organization_id = sm.organization_id \n");
				query.append(
						"LEFT JOIN t_dgv_candidate_basic candidatebasic ON candidatebasic.organization_id=organization.organization_id \n");
				query.append(
						"LEFT JOIN t_dgv_candidate_status_history candidatestatushistory ON candidatestatushistory.candidate_id=candidatebasic.candidate_id \n");
				query.append(
						"LEFT JOIN t_dgv_candidate_status candidatestatus ON candidatestatus.candidate_id=candidatebasic.candidate_id \n");
				query.append(
						"LEFT JOIN t_dgv_status_master statusMaster ON statusMaster.status_master_id=candidatestatushistory.status_master_id \n");
				query.append(
						"LEFT JOIN t_dgv_user_master userMaster ON userMaster.orgainzation_id=organization.organization_id \n");
				query.append("LEFT JOIN t_dgv_role_master roleMaster ON roleMaster.role_id =userMaster.role_id  \n");
				query.append("WHERE organization.is_active =TRUE \n");
				query.append(
						"GROUP BY organization.organization_name,sm.organization_id ORDER BY organization.organization_name ASC) t1\n");
				query.append("LEFT JOIN\n");
				query.append("(SELECT sm.organization_id AS orgid,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='INVITATIONSENT' OR statusMaster.status_code='ITR' OR statusMaster.status_code='EPFO' OR statusMaster.status_code='DIGILOCKER' OR statusMaster.status_code='RELATIVEADDRESS') AND candidatestatus.last_updated_on BETWEEN ?1 AND ?2 THEN candidatestatus.candidate_id END) AS pendingcount\n");
				query.append("FROM   t_dgv_service_master sm \n");
				query.append(
						"LEFT JOIN t_dgv_organization_master organization ON organization.organization_id = sm.organization_id \n");
				query.append(
						"LEFT JOIN t_dgv_candidate_basic candidatebasic ON candidatebasic.organization_id=organization.organization_id \n");
				query.append(
						"LEFT JOIN t_dgv_candidate_status candidatestatus ON candidatestatus.candidate_id=candidatebasic.candidate_id\n");
				query.append(
						"LEFT JOIN t_dgv_status_master statusMaster ON statusMaster.status_master_id=candidatestatus.status_master_id \n");
				query.append(
						"LEFT JOIN t_dgv_user_master userMaster ON userMaster.orgainzation_id=organization.organization_id \n");
				query.append("LEFT JOIN t_dgv_role_master roleMaster ON roleMaster.role_id =userMaster.role_id  \n");
				query.append("WHERE organization.is_active =TRUE \n");
				query.append("GROUP BY sm.organization_id ORDER BY organization.organization_name ASC) t2\n");
				query.append("ON t1.orgid=t2.orgid  \n");

				Query squery = entityManager.createNativeQuery(query.toString());
				squery.setParameter(1, startDate);
				squery.setParameter(2, endDate);
				resultList = squery.getResultList();
			}
			//getting PENDINGNOW (pending count) and INVITATIONEXPIRED count for Untill Now records
			
			StringBuilder query1 = new StringBuilder();
			List<Object[]> resultList1 = null;
			if (reportSearchDto != null && reportSearchDto.getOrganizationIds() != null
					&& reportSearchDto.getOrganizationIds().size() > 0
					&& reportSearchDto.getOrganizationIds().get(0) != 0l) {
				Date startDate1 = user.getOrganization()!=null? user.getOrganization().getCreatedOn() : startDate;
				Date endDate1 = new Date();
				
				query1.append("SELECT pending, inviteExpired\n");
				query1.append("FROM (\n");

				// PENDINGNOW
				query1.append("(SELECT COUNT(tdcsh.candidate_id) AS pending FROM t_dgv_candidate_status tdcsh\n");
				query1.append("JOIN t_dgv_candidate_basic bas ON bas.candidate_id = tdcsh.candidate_id\n");
				query1.append("JOIN t_dgv_organization_master org ON bas.organization_id = org.organization_id\n");
				query1.append("JOIN t_dgv_status_master mas ON mas.status_master_id = tdcsh.status_master_id\n");
				query1.append("WHERE mas.status_code IN ('INVITATIONSENT','REINVITE','ITR','EPFO','DIGILOCKER','RELATIVEADDRESS')\n");
				query1.append("AND org.organization_id IN (:orgId)\n");
				query1.append("AND tdcsh.last_updated_on BETWEEN :startDate AND :endDate) ne,\n");

				// INVITATIONEXPIRED
				query1.append("(SELECT COUNT(tdcsh.candidate_id) AS inviteExpired FROM t_dgv_candidate_status tdcsh\n");
				query1.append("JOIN t_dgv_candidate_basic bas ON bas.candidate_id = tdcsh.candidate_id\n");
				query1.append("JOIN t_dgv_organization_master org ON bas.organization_id = org.organization_id\n");
				query1.append("JOIN t_dgv_status_master mas ON mas.status_master_id = tdcsh.status_master_id\n");
				query1.append("WHERE mas.status_code IN ('INVITATIONEXPIRED')\n");
				query1.append("AND org.organization_id IN (:orgId)\n");
				query1.append("AND tdcsh.last_updated_on BETWEEN :startDate AND :endDate) ie\n");

				query1.append(")");

				
				Query squery = entityManager.createNativeQuery(query1.toString());
				squery.setParameter("startDate", startDate1);
				squery.setParameter("endDate", endDate1);
				squery.setParameter("orgId", reportSearchDto.getOrganizationIds());
				resultList1 = squery.getResultList();
				
				for (Object[] result : resultList1) {
					log.info("PENDINGNOW::{}",result[0]);
					log.info("INVITATIONEXPIRED::{}",result[1]);
				}
			}else {
				Date startDate1 = user.getOrganization()!=null? user.getOrganization().getCreatedOn() : startDate;
				Date endDate1 = new Date();
				
				query1.append("SELECT pending, inviteExpired\n");
				query1.append("FROM (\n");

				// PENDINGNOW
				query1.append("(SELECT COUNT(tdcsh.candidate_id) AS pending FROM t_dgv_candidate_status tdcsh\n");
				query1.append("JOIN t_dgv_candidate_basic bas ON bas.candidate_id = tdcsh.candidate_id\n");
				query1.append("JOIN t_dgv_organization_master org ON bas.organization_id = org.organization_id\n");
				query1.append("JOIN t_dgv_status_master mas ON mas.status_master_id = tdcsh.status_master_id\n");
				query1.append("WHERE mas.status_code IN ('INVITATIONSENT','REINVITE','ITR','EPFO','DIGILOCKER','RELATIVEADDRESS')\n");
				query1.append("AND tdcsh.last_updated_on BETWEEN :startDate AND :endDate) ne,\n");

				// INVITATIONEXPIRED
				query1.append("(SELECT COUNT(tdcsh.candidate_id) AS inviteExpired FROM t_dgv_candidate_status tdcsh\n");
				query1.append("JOIN t_dgv_candidate_basic bas ON bas.candidate_id = tdcsh.candidate_id\n");
				query1.append("JOIN t_dgv_organization_master org ON bas.organization_id = org.organization_id\n");
				query1.append("JOIN t_dgv_status_master mas ON mas.status_master_id = tdcsh.status_master_id\n");
				query1.append("WHERE mas.status_code IN ('INVITATIONEXPIRED')\n");
				query1.append("AND tdcsh.last_updated_on BETWEEN :startDate AND :endDate) ie\n");

				query1.append(")");

				
				Query squery = entityManager.createNativeQuery(query1.toString());
				squery.setParameter("startDate", startDate1);
				squery.setParameter("endDate", endDate1);
				resultList1 = squery.getResultList();
				
				for (Object[] result : resultList1) {
					log.info("else PENDINGNOW::{}",result[0]);
					log.info("else INVITATIONEXPIRED::{}",result[1]);
				}
			}
			
			//end PENDINGNOW , INVITATIONEXPIRED updated counts
			if (resultList != null && !resultList.isEmpty() && resultList1!=null && !resultList1.isEmpty()) {
				List<ReportResponseDto> pwdvMprReportDtoList = new ArrayList<ReportResponseDto>();
//				for (Object[] result : resultList) {
				for (int i=0 ; i<resultList.size() ; i++) {
					Object[] result = resultList.get(i);
				    Object[] result1 = resultList1.get(i); // Retrieve data from the second list
					ReportResponseDto reportResponseDto = new ReportResponseDto(Long.valueOf(String.valueOf(result[0])),
							String.valueOf(result[1]), Integer.valueOf(String.valueOf(result[2])), "NEWUPLOAD",
							Integer.valueOf(String.valueOf(result[3])), "REINVITE",
							Integer.valueOf(String.valueOf(result[4])), "FINALREPORT",
							Integer.valueOf(String.valueOf(result[5])), "PENDINGAPPROVAL",
//							Integer.valueOf(String.valueOf(result[6])), "PENDINGNOW",
							Integer.valueOf(String.valueOf(result1[0])), "PENDINGNOW",      //overrided count untill now
							Integer.valueOf(String.valueOf(result[7])), "PROCESSDECLINED",
//							Integer.valueOf(String.valueOf(result[8])), "INVITATIONEXPIRED",
							Integer.valueOf(String.valueOf(result1[1])), "INVITATIONEXPIRED", //overrided count untill now
							Integer.valueOf(String.valueOf(result[9])));
					pwdvMprReportDtoList.add(reportResponseDto);
				}
				ReportResponseDto reportResponseDtoTotal = new ReportResponseDto(0l, "TOTAL",
						pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getNewuploadcount()).sum(),
						"NEWUPLOADTOTAL", pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getReinvitecount()).sum(),
						"REINVITETOTAL",
						pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getFinalreportCount()).sum(),
						"FINALREPORTTOTAL",
						pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getInterimReportCount()).sum(),
						"PENDINGAPPROVALTOTAL",
						pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getPendingCount()).sum(), "PENDINGNOWTOTAL",
						pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getProcessDeclinedCount()).sum(),
						"PROCESSDECLINEDTOTAL",
						pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getInvitationExpireCount()).sum(),
						"INVITATIONEXPIREDTOTAL",
						pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getAgentCount()).sum());
				pwdvMprReportDtoList.add(reportResponseDtoTotal);
				reportSearchDtoObj = new ReportSearchDto(strFromDate, strToDate, orgIds, pwdvMprReportDtoList, null);
				svcSearchResult.setData(reportSearchDtoObj);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("Customer Utilization Report Data generated...");
			} else {
				reportSearchDtoObj = new ReportSearchDto(strFromDate, strToDate, orgIds, null, null);
				svcSearchResult.setData(reportSearchDtoObj);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NO RECORD FOUND");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getCustomerUtilizationReportData method in ReportServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<ReportSearchDto> getCustomerUtilizationReportByAgent(ReportSearchDto reportSearchDto) {
		ServiceOutcome<ReportSearchDto> svcSearchResult = new ServiceOutcome<ReportSearchDto>();
		List<Object[]> resultList = null;
		ReportSearchDto reportSearchDtoObj = null;
		try {
			if (reportSearchDto != null) {
				Date startDate = format.parse(reportSearchDto.getFromDate() + " 00:00:00");
				Date endDate = format.parse(reportSearchDto.getToDate() + " 23:59:59");
				StringBuilder query = new StringBuilder();
				query.append(
						"select userMaster.user_id ,userMaster.user_first_name , COALESCE(userMaster.user_last_name,'') as lastname , ");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='NEWUPLOAD' OR statusMaster.status_code ='INVALIDUPLOAD') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS newuploadcount,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='REINVITE') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS reinvitecount,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='FINALREPORT') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS finalreportDeliveredcount,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='PENDINGAPPROVAL') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS intereimreportDeliveredcount,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='INVITATIONSENT' OR statusMaster.status_code='ITR' OR statusMaster.status_code='EPFO' OR statusMaster.status_code='DIGILOCKER' OR statusMaster.status_code='RELATIVEADDRESS') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS pendingcount,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='PROCESSDECLINED') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS processDeclinecount,\n");
				query.append(
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='INVITATIONEXPIRED') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS invitationExpiredcount\n");
				query.append("from  t_dgv_user_master userMaster ");
				query.append(
						"left JOIN t_dgv_candidate_basic candidatebasic ON candidatebasic.created_by =userMaster.user_id  ");
				query.append(
						"left JOIN t_dgv_candidate_status_history candidatestatushistory ON candidatestatushistory.candidate_id=candidatebasic.candidate_id ");
				query.append(
						"left JOIN t_dgv_status_master statusMaster ON statusMaster.status_master_id=candidatestatushistory.status_master_id ");
				query.append("left JOIN t_dgv_role_master roleMaster ON roleMaster.role_id =userMaster.role_id  ");
				if (reportSearchDto.getAgentIds() != null && reportSearchDto.getAgentIds().size() > 0
						&& reportSearchDto.getAgentIds().get(0) != 0l) {
					query.append(
							"where userMaster.orgainzation_id in (?3) and userMaster.is_user_active = true and roleMaster.role_code ='ROLE_AGENTHR' and userMaster.user_id in (?4) ");
					query.append("group by userMaster.user_id order by userMaster.user_first_name ASC; ");
					Query squery = entityManager.createNativeQuery(query.toString());
					squery.setParameter(1, startDate);
					squery.setParameter(2, endDate);
					squery.setParameter(3, reportSearchDto.getOrganizationIds());
					squery.setParameter(4, reportSearchDto.getAgentIds());
					resultList = squery.getResultList();
				} else {
					query.append(
							"where userMaster.orgainzation_id in (?3) and userMaster.is_user_active = true and roleMaster.role_code ='ROLE_AGENTHR' ");
					query.append("group by userMaster.user_id order by userMaster.user_first_name ASC; ");
					Query squery = entityManager.createNativeQuery(query.toString());
					squery.setParameter(1, startDate);
					squery.setParameter(2, endDate);
					squery.setParameter(3, reportSearchDto.getOrganizationIds());
					resultList = squery.getResultList();
				}
				if (resultList != null && resultList.size() > 0) {
					List<ReportResponseDto> pwdvMprReportDtoList = new ArrayList<ReportResponseDto>();
					for (Object[] result : resultList) {

						ReportResponseDto reportResponseDto = new ReportResponseDto(
								Long.valueOf(String.valueOf(result[0])),
								String.valueOf(result[1]) + " " + String.valueOf(result[2]),
								Integer.valueOf(String.valueOf(result[3])), "NEWUPLOAD",
								Integer.valueOf(String.valueOf(result[4])), "REINVITE",
								Integer.valueOf(String.valueOf(result[5])), "FINALREPORT",
								Integer.valueOf(String.valueOf(result[6])), "PENDINGAPPROVAL",
								Integer.valueOf(String.valueOf(result[7])), "PENDINGNOW",
								Integer.valueOf(String.valueOf(result[8])), "PROCESSDECLINED",
								Integer.valueOf(String.valueOf(result[9])), "INVITATIONEXPIRED", 0);
						pwdvMprReportDtoList.add(reportResponseDto);
					}
					ReportResponseDto reportResponseDtoTotal = new ReportResponseDto(0l, "TOTAL",
							pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getNewuploadcount()).sum(),
							"NEWUPLOADTOTAL",
							pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getReinvitecount()).sum(),
							"REINVITETOTAL",
							pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getFinalreportCount()).sum(),
							"FINALREPORTTOTAL",
							pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getInterimReportCount()).sum(),
							"PENDINGAPPROVALTOTAL",
							pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getPendingCount()).sum(),
							"PENDINGNOWTOTAL",
							pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getProcessDeclinedCount()).sum(),
							"PROCESSDECLINEDTOTAL",
							pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getInvitationExpireCount()).sum(),
							"INVITATIONEXPIREDTOTAL",
							pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getAgentCount()).sum());
					pwdvMprReportDtoList.add(reportResponseDtoTotal);
					reportSearchDtoObj = new ReportSearchDto(reportSearchDto.getFromDate(), reportSearchDto.getToDate(),
							reportSearchDto.getOrganizationIds(), pwdvMprReportDtoList, reportSearchDto.getAgentIds());
					svcSearchResult.setData(reportSearchDtoObj);
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage("Customer Utilization Report Data By Agent generated...");
				} else {
					reportSearchDtoObj = new ReportSearchDto(reportSearchDto.getFromDate(), reportSearchDto.getToDate(),
							reportSearchDto.getOrganizationIds(), null, reportSearchDto.getAgentIds());
					svcSearchResult.setData(reportSearchDtoObj);
					svcSearchResult.setOutcome(false);
					svcSearchResult.setMessage("NO RECORD FOUND");
				}
			}
		} catch (Exception ex) {
			log.error("Exception occured in getCustomerUtilizationReportByAgent method in ReportServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<ReportSearchDto> getCanididateDetailsByStatus(ReportSearchDto reportSearchDto) {
		ServiceOutcome<ReportSearchDto> svcSearchResult = new ServiceOutcome<ReportSearchDto>();
		List<CandidateDetailsForReport> candidateDetailsDtoList = new ArrayList<CandidateDetailsForReport>();
		CandidateEmailStatus candidateEmailStatus = null;
		List<CandidateStatusDto> candidateStatusList = null;
		CandidateDetailsForReport candidateDto = null;
		List<Object[]> resultList = null;
		StringBuilder query = null;
		Query squery = null;
		try {
			if (StringUtils.isNotBlank(reportSearchDto.getStatusCode())
					&& StringUtils.isNotBlank(reportSearchDto.getFromDate())
					&& StringUtils.isNotBlank(reportSearchDto.getToDate())
					&& reportSearchDto.getOrganizationIds() != null
					&& !reportSearchDto.getOrganizationIds().isEmpty()) {
				User user = SecurityHelper.getCurrentUser();
				Date startDate = format.parse(reportSearchDto.getFromDate() + " 00:00:00");
				Date endDate = format.parse(reportSearchDto.getToDate() + " 23:59:59");
				List<String> statusList = null;
				if (reportSearchDto.getStatusCode().equals("NEWUPLOAD")) {
					statusList = new ArrayList<>();
					Collections.addAll(statusList, "NEWUPLOAD");
					Collections.addAll(statusList, "INVALIDUPLOAD");
					if (reportSearchDto.getAgentIds() != null && !reportSearchDto.getAgentIds().isEmpty()) {
//						candidateStatusList = candidateStatusRepository
//								.findAllByCreatedOnBetweenAndCandidateOrganizationOrganizationIdInAndCreatedByUserIdIn(
//										startDate, endDate, reportSearchDto.getOrganizationIds(),
//										reportSearchDto.getAgentIds());
						candidateStatusList = candidateStatusHistoryRepository
								.findAllByCreatedOnBetweenAndCandidateOrganizationOrganizationIdInAndCreatedByUserIdIn(
								startDate, endDate, reportSearchDto.getOrganizationIds(),
								reportSearchDto.getAgentIds(),statusList);
						
					} else {
//						candidateStatusList = candidateStatusRepository
//								.findAllByCreatedOnBetweenAndCandidateOrganizationOrganizationIdIn(startDate, endDate,
//										reportSearchDto.getOrganizationIds());
						
						candidateStatusList = candidateStatusHistoryRepository.findAllByCreatedOnBetweenAndCandidateOrganizationOrganizationIdIn(startDate, endDate,
								reportSearchDto.getOrganizationIds(),statusList);
					}
					if (!candidateStatusList.isEmpty()) {

//						for (CandidateStatusDto candidateStatus : candidateStatusList) {
//							candidateDto = this.modelMapper.map(candidateStatus.getCandidate(),
//									CandidateDetailsForReport.class);
//							candidateEmailStatus = candidateEmailStatusRepository
//									.findByCandidateCandidateCode(candidateStatus.getCandidate().getCandidateCode());
//							candidateDto.setDateOfEmailInvite(
//									candidateEmailStatus != null && candidateEmailStatus.getDateOfEmailInvite() != null
//											? candidateEmailStatus.getDateOfEmailInvite().toString()
//											: null);
//							candidateDto.setStatusName(candidateStatus.getStatusMaster().getStatusName());
//							candidateDto.setStatusDate(candidateStatus.getLastUpdatedOn() != null
//									? candidateStatus.getLastUpdatedOn().toString()
//									: null);
//							candidateDetailsDtoList.add(candidateDto);
//						}

						Type candidateDetailsListForReport = new TypeToken<List<CandidateDetailsForReport>>() {
						}.getType();
						candidateDetailsDtoList = this.modelMapper.map(candidateStatusList,
								candidateDetailsListForReport);
					}
				} else if(reportSearchDto.getStatusCode().equals("REPORTDELIVERED")) {
					
					statusList = new ArrayList<>();
					Collections.addAll(statusList, "PENDINGAPPROVAL");
					
					if (reportSearchDto.getAgentIds() != null && !reportSearchDto.getAgentIds().isEmpty()) {
						candidateStatusList = candidateStatusRepository
								.findReportDeliveredByUserIdAndStatus(
										startDate, endDate, reportSearchDto.getOrganizationIds(),
										reportSearchDto.getAgentIds(), statusList);
					} else {
						candidateStatusList = candidateStatusRepository
								.findReportDeliveredByOrganizationIdAndStatus(startDate, endDate,
										reportSearchDto.getOrganizationIds(), statusList);
					}
					
					if (!candidateStatusList.isEmpty()) {

//						Type candidateDetailsListForReport = new TypeToken<List<CandidateDetailsForReport>>() {
//						}.getType();
//						candidateDetailsDtoList = this.modelMapper.map(candidateStatusList,
//								candidateDetailsListForReport);
						
						candidateDetailsDtoList = candidateStatusList.stream()
						        .map(candidateStatus -> modelMapper.map(candidateStatus, CandidateDetailsForReport.class))
						        .sorted(Comparator.comparing(CandidateDetailsForReport::getQcCreatedOn))
//						        .sorted(Comparator.comparing(CandidateDetailsForReport::getCreatedOn))
						        .collect(Collectors.toList());
						
					}
				}
				if (reportSearchDto.getStatusCode().equals("REINVITE")
						|| reportSearchDto.getStatusCode().equals("FINALREPORT")
						|| reportSearchDto.getStatusCode().equals("PENDINGAPPROVAL")
						|| reportSearchDto.getStatusCode().equals("PROCESSDECLINED")
						|| reportSearchDto.getStatusCode().equals("INVITATIONEXPIRED")
						|| reportSearchDto.getStatusCode().equals("PENDINGNOW")
						|| reportSearchDto.getStatusCode().equals("GST")) {
					query = new StringBuilder();
					query.append(
							"select distinct cb.candidate_id ,um.user_first_name ,COALESCE(um.user_last_name,'') as lastname , ");
					query.append(
							"cb.candidate_name ,cb.contact_number ,cb.email_id,coalesce(cb.itr_pan_number,'') as pannumber, ");
					query.append("cb.applicant_id ,cb.candidate_code ,es.date_of_email_invite,cb.created_on, ");
					query.append("coalesce(cb.experience_in_month,0) as noe, t.currentstatusdate, ");
					query.append(
							"tdcs.color_id ,sm.status_name ,t.invitationexpiredcount,t.reinvitecount,tdcs.last_updated_on as statusdate ");
					query.append("from  t_dgv_candidate_basic cb, ");
					query.append("t_dgv_candidate_status tdcs,t_dgv_status_master sm,t_dgv_user_master um, ");
					query.append("t_dgv_candidate_email_status es , ");
					query.append("( ");
					query.append(
							"SELECT csh.candidate_id,count(case when sm.status_code ='INVITATIONEXPIRED' then 1  END) as invitationexpiredcount, ");
					query.append("count(case when sm.status_code ='REINVITE' then 1 END) as reinvitecount, ");
					query.append("max(case when sm.status_code in (?4) then created_on END) as currentstatusdate ");
					if (reportSearchDto.getStatusCode().equals("PENDINGAPPROVAL")
							|| reportSearchDto.getStatusCode().equals("PENDINGNOW")
							|| reportSearchDto.getStatusCode().equals("INVITATIONEXPIRED")) {
						query.append("FROM t_dgv_candidate_status csh ");

					} else {
						query.append("FROM t_dgv_candidate_status_history csh ");
					}
					query.append("left JOIN t_dgv_status_master sm ON sm.status_master_id=csh.status_master_id ");
					query.append("where  csh.created_on  between ?1 and ?2 ");
					query.append("and sm.status_code in (?4) ");
					query.append("group by csh.candidate_id ) t ");
					query.append("where ");
					query.append(" cb.candidate_id=t.candidate_id ");
					query.append("and sm.status_master_id=tdcs.status_master_id ");
					query.append("and um.user_id =cb.created_by  ");
					query.append("and es.candidate_id =cb.candidate_id  ");
					query.append("and tdcs.candidate_id=cb.candidate_id ");
					query.append("and cb.organization_id in (?3)  ");
					if (reportSearchDto.getAgentIds() != null && !reportSearchDto.getAgentIds().isEmpty()) {
						query.append("and um.user_id in (?5)  ");
					}
					squery = entityManager.createNativeQuery(query.toString());
					//Override dates for getting Untill now records list
					if(reportSearchDto.getStatusCode().equals("INVITATIONEXPIRED")
							|| reportSearchDto.getStatusCode().equals("PENDINGNOW")) {
						startDate = user.getOrganization()!=null ? user.getOrganization().getCreatedOn() : startDate;
						endDate = new Date();
						squery.setParameter(1, startDate);
						squery.setParameter(2, endDate);
					}else {
						squery.setParameter(1, startDate);
						squery.setParameter(2, endDate);
					}
					
					squery.setParameter(3, reportSearchDto.getOrganizationIds());
					if (reportSearchDto.getStatusCode().equals("PENDINGNOW")) {
						statusList = new ArrayList<>();
						Collections.addAll(statusList, "INVITATIONSENT","REINVITE", "ITR", "EPFO", "DIGILOCKER",
								"RELATIVEADDRESS");
//						Collections.addAll(statusList, "INVITATIONSENT", "ITR", "EPFO", "DIGILOCKER",
//								"RELATIVEADDRESS", "PENDINGAPPROVAL", "INTERIMREPORT");
					} else if (reportSearchDto.getStatusCode().equals("PENDINGAPPROVAL")) {
						statusList = new ArrayList<>();
						Collections.addAll(statusList, "PENDINGAPPROVAL", "INTERIMREPORT");
					} else {
						statusList = new ArrayList<>();
						Collections.addAll(statusList, reportSearchDto.getStatusCode());
					}
					squery.setParameter(4, statusList);
					if (reportSearchDto.getAgentIds() != null && !reportSearchDto.getAgentIds().isEmpty()) {
						squery.setParameter(5, reportSearchDto.getAgentIds());
					}
					resultList = squery.getResultList();
					if (resultList != null && resultList.size() > 0) {
						for (Object[] result : resultList) {
							candidateDto = new CandidateDetailsForReport();
							candidateDto.setCreatedByUserFirstName(String.valueOf(result[1]));
							candidateDto.setCreatedByUserLastName(String.valueOf(result[2]));
							candidateDto.setCandidateName(String.valueOf(result[3]));
							candidateDto.setContactNumber(String.valueOf(result[4]));
							candidateDto.setEmailId(String.valueOf(result[5]));
							candidateDto.setPanNumber(String.valueOf(result[6]));
							candidateDto.setApplicantId(String.valueOf(result[7]));
							candidateDto.setCandidateCode(String.valueOf(result[8]));
							candidateDto.setDateOfEmailInvite(String.valueOf(result[9]));
							candidateDto.setCreatedOn(String.valueOf(result[10]));
							candidateDto.setExperience(Float.valueOf(String.valueOf(result[11])) + " Years");
							candidateDto.setCurrentStatusDate(String.valueOf(result[12]));
							candidateDto.setColorName(
									result[13] != null
											? colorRepository.findById(Long.valueOf(String.valueOf(result[13]))).get()
													.getColorName()
											: "NA");
							if(reportSearchDto.getStatusCode().equals("INVITATIONEXPIRED")) {
								candidateDto.setStatusName("Invitation Expired");
							}else {
								candidateDto.setStatusName(String.valueOf(result[14]));
							}
							
							if(reportSearchDto.getStatusCode().equals("GST")) {
								List<GstData> gstRecords= gstRepository.findAllByCandidate(Long.valueOf(String.valueOf(result[0])));
								String colorName = gstRecords != null && !gstRecords.isEmpty() && gstRecords.get(0).getColor()!=null
										? gstRecords.get(0).getColor().getColorName()
										: "NA";
								candidateDto.setColorName(colorName);
								String remarks= "NA";
								if(colorName.equalsIgnoreCase("Green")) {
									remarks= "No records found";
								}else if(colorName.equalsIgnoreCase("Amber")) {
									remarks= "Inactive in GST";
								}else if(colorName.equalsIgnoreCase("Red")) {
									remarks= "Active in GST";
								}
								candidateDto.setGstNumber(remarks);
//								candidateDto.setGstNumber(
//										gstRecords != null && !gstRecords.isEmpty()? gstRecords.get(0).getGstNumber()
//												: "NA");
							}
							candidateDto.setNumberofexpiredCount(Integer.valueOf(String.valueOf(result[15])));
							candidateDto.setReinviteCount(Integer.valueOf(String.valueOf(result[16])));
							candidateDto.setStatusDate(String.valueOf(result[17]));
							candidateDetailsDtoList.add(candidateDto);
						}
					}
				}
				//LOA SHEET ADDED
				if(reportSearchDto.getStatusCode().equals("LOA")) {
					List<LoaConsentMaster>	candidatesConsents = null;						
					if (reportSearchDto.getAgentIds() != null && !reportSearchDto.getAgentIds().isEmpty()) {
						candidatesConsents = loaConsentMasterRepository.getByAgentAndCreatedOn(reportSearchDto.getAgentIds(),startDate, endDate);

					}else {
						candidatesConsents = loaConsentMasterRepository.getByOrgAndCreatedOn(reportSearchDto.getOrganizationIds(),startDate, endDate);

					}
					log.info("LOA LIST");
					if (candidatesConsents != null && !candidatesConsents.isEmpty()) {
						for (LoaConsentMaster loaConsentMaster : candidatesConsents) {
							candidateDto = new CandidateDetailsForReport();
							candidateDto.setApplicantId(loaConsentMaster.getCandidate().getApplicantId());
							candidateDto.setCandidateName(loaConsentMaster.getCandidate().getCandidateName());
							candidateDto.setCreatedOn(String.valueOf(loaConsentMaster.getCandidate().getCreatedOn()));
							candidateDto.setQcCreatedOn(String.valueOf(loaConsentMaster.getCreatedOn()));							
							candidateDto.setCandidateCode(loaConsentMaster.getCandidate().getCandidateCode());
							
							candidateDetailsDtoList.add(candidateDto);
							
						}
					}
				}
			}
			ReportSearchDto reportSearchDtoObj = new ReportSearchDto();
			reportSearchDtoObj.setFromDate(reportSearchDto.getFromDate());
			reportSearchDtoObj.setToDate(reportSearchDto.getToDate());
			reportSearchDtoObj.setStatusCode(reportSearchDto.getStatusCode());
			reportSearchDtoObj.setOrganizationIds(reportSearchDto.getOrganizationIds());
			if (reportSearchDto.getOrganizationIds() != null && reportSearchDto.getOrganizationIds().get(0) != 0) {
				reportSearchDtoObj.setOrganizationName(organizationRepository
						.findById(reportSearchDto.getOrganizationIds().get(0)).get().getOrganizationName());
			}
//			List<CandidateDetailsForReport> sortedList = candidateDetailsDtoList.stream()
//					.sorted((o1, o2) -> o1.getCandidateName().compareTo(o2.getCandidateName()))
//					.collect(Collectors.toList());
			
			candidateDetailsDtoList.forEach(candidateDtoRef->{
				candidateDtoRef.setPanNumber(candidateDtoRef.getPanNumber() != null ? commonUtils.encryptXOR(candidateDtoRef.getPanNumber()) : null);
				candidateDtoRef.setAadharName(candidateDtoRef.getAadharName() != null ? commonUtils.encryptXOR(candidateDtoRef.getAadharName()) : null);
				candidateDtoRef.setAadharNumber(candidateDtoRef.getAadharNumber() != null ? commonUtils.encryptXOR(candidateDtoRef.getAadharNumber()) : null);
				candidateDtoRef.setAadharDob(candidateDtoRef.getAadharDob() != null ? commonUtils.encryptXOR(candidateDtoRef.getAadharDob()) : null);
				candidateDtoRef.setAadharGender(candidateDtoRef.getAadharGender() != null ? commonUtils.encryptXOR(candidateDtoRef.getAadharGender()) : null);
				//adding report generated colors for the report delivered sheet
				if(candidateDtoRef.getCandidateId()!=null) {
					CandidateVerificationState verificationStatus = candidateVerificationStateRepository
                            .findByCandidateCandidateId(candidateDtoRef.getCandidateId());
					if(Boolean.TRUE.equals(candidateDtoRef.getStatusName().equalsIgnoreCase("QC Pending"))
						       && verificationStatus!=null && verificationStatus.getPreApprovalColorCodeStatus()!=null){
					
						candidateDtoRef.setPreOfferReportColor(verificationStatus.getPreApprovalColorCodeStatus().getColorName());
						if(verificationStatus.getInterimColorCodeStatus()!=null){
						
							candidateDtoRef.setInterimReportColor(verificationStatus.getInterimColorCodeStatus().getColorName());
					    }
				    }
					
				}
			});

			reportSearchDtoObj.setCandidateDetailsDto(candidateDetailsDtoList);
			svcSearchResult.setData(reportSearchDtoObj);
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage("SUCCESS");
		} catch (Exception ex) {
			log.error("Exception occured in getCanididateDetailsByStatus method in ReportServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<ReportSearchDto> eKycReportData(ReportSearchDto reportSearchDto) {
		ServiceOutcome<ReportSearchDto> svcSearchResult = new ServiceOutcome<ReportSearchDto>();
		List<CandidateDetailsForReport> candidateDetailsForReportList = new ArrayList<CandidateDetailsForReport>();
		CandidateDetailsForReport candidateDetailsForReport = null;
		ReportSearchDto reportSearchDtoObj = new ReportSearchDto();
		List<CandidateStatusDto> candidateList = null;
		List<Long> agentIds = new ArrayList<Long>();
		List<Long> orgIds = new ArrayList<Long>();
		List<String> statusList = new ArrayList<>();

		try {
			if (reportSearchDto != null && reportSearchDto.getUserId() != null) {

				Date startDate = format.parse(reportSearchDto.getFromDate() + " 00:00:00");
				Date endDate = format.parse(reportSearchDto.getToDate() + " 23:59:59");

				User user = userRepository.findById(reportSearchDto.getUserId()).get();
//				Collections.addAll(statusList, "PENDINGAPPROVAL", "FINALREPORT");
//				Collections.addAll(statusList, "PENDINGAPPROVAL", "FINALREPORT", "INTERIMREPORT");
				Collections.addAll(statusList, "PENDINGAPPROVAL");
				if (reportSearchDto.getAgentIds() != null && !reportSearchDto.getAgentIds().isEmpty()
						&& reportSearchDto.getAgentIds().get(0) != 0) {
					candidateList = candidateStatusRepository.findAllByCreatedByUserIdInAndStatusMasterStatusCodeIn(
							reportSearchDto.getAgentIds(), statusList, startDate, endDate);
//					candidateList = candidateStatusRepository.findAllByCreatedByUserIdInAndStatusMasterStatusCodeIn(
//							reportSearchDto.getAgentIds(), statusList);
				} else {
					if (user.getRole().getRoleCode().equals("ROLE_CBADMIN")
							|| user.getRole().getRoleCode().equals("ROLE_ADMIN")
							|| user.getRole().getRoleCode().equals("ROLE_PARTNERADMIN")) {
						if (user.getRole().getRoleCode().equals("ROLE_CBADMIN")) {
							if (reportSearchDto.getOrganizationIds() != null
									&& !reportSearchDto.getOrganizationIds().isEmpty()
									&& reportSearchDto.getOrganizationIds().get(0) != 0) {
								orgIds = reportSearchDto.getOrganizationIds();
							} else {
								ServiceOutcome<List<OrganizationDto>> svcoutcome = organizationServiceImpl
										.getOrganizationListAfterBilling();
								orgIds = svcoutcome.getData().parallelStream().map(x -> x.getOrganizationId())
										.collect(Collectors.toList());
							}
						} else {
							orgIds.add(0, user.getOrganization().getOrganizationId());
						}
						candidateList = candidateStatusRepository
								.findAllByCandidateOrganizationOrganizationIdInAndStatusMasterStatusCodeIn(orgIds,
										statusList, startDate, endDate);
//						candidateList = candidateStatusRepository
//								.findAllByCandidateOrganizationOrganizationIdInAndStatusMasterStatusCodeIn(orgIds,
//										statusList);
					}
					if (user.getRole().getRoleCode().equals("ROLE_AGENTSUPERVISOR")
							|| user.getRole().getRoleCode().equals("ROLE_AGENTHR")) {
						List<User> agentList = userRepository.findAllByAgentSupervisorUserId(user.getUserId());
						if (!agentList.isEmpty()) {
							agentIds = agentList.parallelStream().map(x -> x.getUserId()).collect(Collectors.toList());
						}
						agentIds.add(user.getUserId());
						candidateList = candidateStatusRepository.findAllByCreatedByUserIdInAndStatusMasterStatusCodeIn(
								agentIds, statusList, startDate, endDate);
//						candidateList = candidateStatusRepository
//								.findAllByCreatedByUserIdInAndStatusMasterStatusCodeIn(agentIds, statusList);
					}
				}
				if (candidateList != null && !candidateList.isEmpty()) {
//					for (CandidateStatusDto candidate : candidateList) {
//						candidateDetailsForReport = this.modelMapper.map(candidate,
//								CandidateDetailsForReport.class);
//						candidateDetailsForReport = new CandidateDetailsForReport();
//						List<String> uanNUmberList = candidateCafExperienceRepository
//								.getCandidateUan(candidate.getCandidateId());
//						String uanNumber = uanNUmberList.parallelStream().map(uan -> uan.toString())
//								.collect(Collectors.joining("/"));
//						candidateDetailsForReport.setCandidateUan(uanNumber);
//						log.info("Candidate details {} {} {}", candidate.getCandidateUan(), candidate.getCandidateUanName(), candidate.getAddress());
//						IdItemsDto candidateIdItemPan = candidateIdItemsRepository
//								.findByCandidateCandidateCodeAndServiceSourceMasterServiceCode(
//										candidate.getCandidateCode(), "PAN");
//						if (candidateIdItemPan != null && candidateIdItemPan.getIdHolder() != null) {
//							candidateDetailsForReport.setPanName(candidateIdItemPan.getIdHolder());
//							candidateDetailsForReport.setPanDob(candidateIdItemPan.getIdHolderDob());
//						}
//						List<EpfoDto> uanList = epfoDataRepository
//								.findAllByCandidateCandidateId(candidate.getCandidateId());
//						String uanName = uanList.parallelStream().map(name -> name.toString())
//								.collect(Collectors.joining("/"));
//						for (EpfoDto uan : uanList) {
//							candidateDetailsForReport.setCandidateUanName(uan.getName());
//						}
//						CandidateCafAddressInterfaceDto candidateCafAddress = candidateCafAddressRepository
//								.findByCandidateCandidateCodeAndServiceSourceMasterServiceCodeAndAddressVerificationIsNull(
//										candidate.getCandidateCode(), "AADHARADDR");
//						candidateDetailsForReport.setAddress(
//								candidateCafAddress != null ? candidateCafAddress.getCandidateAddress() : "");

//						CandidateCafAddress candidateCafAddressRelation = candidateCafAddressRepository
//								.findByCandidateCandidateCodeAndAddressVerificationIsNotNull(
//										candidate.getCandidateCode());
//						candidateDetailsForReport.setRelationship(candidateCafAddressRelation != null
//								&& candidateCafAddressRelation.getAddressVerification() != null
//										? candidateCafAddressRelation.getAddressVerification()
//												.getCandidateCafRelationship().getCandidateRelationship()
//										: "");
//						candidateDetailsForReport.setRelationName(candidateCafAddressRelation != null
//								&& candidateCafAddressRelation.getAddressVerification() != null
//										? candidateCafAddressRelation.getName()
//										: "");
//						CandidateCafAddressInterfaceDto candidateCafAddressRelation = candidateCafAddressRepository
//								.findByCandidateCandidateCodeAndAddressVerificationIsNotNull(
//										candidate.getCandidateId());
//						candidateDetailsForReport.setRelationship(candidateCafAddressRelation != null
//								&& candidateCafAddressRelation.getCandidateRelationship() != null
//										? candidateCafAddressRelation.getCandidateRelationship()
//										: ""); 
//						candidateDetailsForReport.setRelationName(candidateCafAddressRelation != null
//								&& candidateCafAddressRelation.getAddressVerification() != null
//										? candidateCafAddressRelation.getName()
//										: "");
//						candidateDetailsForReportList.add(candidateDetailsForReport);
//					}
					Type candidateDetailsListForReport = new TypeToken<List<CandidateDetailsForReport>>() {
					}.getType();
					candidateDetailsForReportList = this.modelMapper.map(candidateList, candidateDetailsListForReport);
				}
				List<CandidateDetailsForReport> sortedList = candidateDetailsForReportList.parallelStream()
						.sorted((o1, o2) -> o1.getCandidateName().compareTo(o2.getCandidateName()))
						.collect(Collectors.toList());
				reportSearchDtoObj.setCandidateDetailsDto(sortedList);
				reportSearchDtoObj.setUserId(reportSearchDto.getUserId());
				reportSearchDtoObj.setOrganizationIds(reportSearchDto.getOrganizationIds());
				reportSearchDtoObj.setAgentIds(reportSearchDto.getAgentIds());
				svcSearchResult.setData(reportSearchDtoObj);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("Please specify user.");
			}
		} catch (Exception ex) {
			log.error("Exception occured in eKycReportData method in ReportServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<CandidateReportDTO> generateDocument(String candidateCode, String token,
			ReportType reportType, String overrideReportStatus) {
//		System.out.println("enter to generate doc *******************************");
		entityManager.setFlushMode(FlushModeType.COMMIT);
		ServiceOutcome<CandidateReportDTO> svcSearchResult = new ServiceOutcome<CandidateReportDTO>();
		Candidate candidate = candidateService.findCandidateByCandidateCode(candidateCode);
		CandidateAddComments candidateAddComments = candidateAddCommentRepository
				.findByCandidateCandidateId(candidate.getCandidateId());
//		System.out.println(candidate.getCandidateId() + "*******************************"
//				+ validateCandidateStatus(candidate.getCandidateId()));
		CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(candidateCode);
		Integer report_status_id = 0;
		String report_present_status = "";
		Boolean generatePdfFlag = true;
		if (reportType.equals(ReportType.PRE_OFFER)) {
			report_status_id = 7;
		} else if (reportType.equals(ReportType.FINAL)) {
			report_status_id = 8;
		} else if (reportType.equals(ReportType.INTERIM)) {
			report_status_id = 13;
		}
		if (Integer.valueOf(String.valueOf(candidateStatus.getStatusMaster().getStatusMasterId())) == 7) {
			report_present_status = "QC Pending";
		} else if (Integer.valueOf(String.valueOf(candidateStatus.getStatusMaster().getStatusMasterId())) == 8) {
			report_present_status = String.valueOf(ReportType.FINAL);
		} else if (Integer.valueOf(String.valueOf(candidateStatus.getStatusMaster().getStatusMasterId())) == 13) {
			report_present_status = String.valueOf(ReportType.INTERIM);
		}
		if (candidateStatus.getStatusMaster().getStatusMasterId() != null) {
			if (report_status_id == Integer
					.valueOf(String.valueOf(candidateStatus.getStatusMaster().getStatusMasterId()))) {
				generatePdfFlag = true;
//				log.info("entered line no 870 {}", report_status_id);
			} else {
				generatePdfFlag = false;
				CandidateReportDTO candidateDTOobj = null;
				candidateDTOobj = new CandidateReportDTO();
				candidateDTOobj
						.setCandidate_reportType("candidate in " + report_present_status + " Not in " + reportType);
				svcSearchResult.setData(candidateDTOobj);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage(null);
				svcSearchResult.setStatus(report_present_status);
			}
		}

		if (reportType == ReportType.PRE_OFFER) {
			Optional<Content> contentList = contentRepository
					.findByCandidateIdAndContentTypeAndContentCategoryAndContentSubCategory(candidate.getCandidateId(),
							ContentType.GENERATED, ContentCategory.OTHERS, ContentSubCategory.PRE_APPROVAL);
			if (contentList.isPresent()) {
				generatePdfFlag = false;
				CandidateReportDTO candidateDTOobj = null;
				candidateDTOobj = new CandidateReportDTO();
				candidateDTOobj
						.setCandidate_reportType("candidate in " + report_present_status + " Not in " + reportType);
				svcSearchResult.setData(candidateDTOobj);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage(null);
				svcSearchResult.setStatus(report_present_status);
			}
		}

//		log.info("entered line no 883 {}", report_status_id);
		if (validateCandidateStatus(candidate.getCandidateId()) || candidate.getOrganization().getOrganizationName().equalsIgnoreCase("KPMG")) {
			if (generatePdfFlag) {
//				System.out.println("enter if *******************************");
				List<VendorUploadChecksDto> vendordocDtoList = new ArrayList<VendorUploadChecksDto>();
				VendorUploadChecksDto vendorUploadChecksDto = null;

				List<String> orgServices = serviceTypeConfigRepository.getServiceSourceMasterByOrgId(candidate.getOrganization().getOrganizationId());

				// Added For KPMG
				final boolean isKPMG = candidate.getOrganization().getOrganizationName().equalsIgnoreCase("KPMG");
				final boolean[] isdigilocker = {!isKPMG};
				
				ServiceOutcome<List<String>> configCodes = candidateService.getServiceConfigCodes(candidateCode, null);
				if(Boolean.TRUE.equals(configCodes.getOutcome()) && !configCodes.getData().contains("DIGILOCKER")) {
					isdigilocker[0] =false;
				}
				
				log.info("REPORT FOR DIGILOCKER::{}",isdigilocker[0]);
				// candidate Basic detail
				CandidateReportDTO candidateReportDTO = new CandidateReportDTO();
				candidateReportDTO.setOrgServices(orgServices);

				Set<CandidateStatusEnum> candidateStatusEnums = candidateStatusHistoryRepository
						.findAllByCandidateCandidateId(candidate.getCandidateId()).stream()
						.map(candidateStatusHistory -> CandidateStatusEnum
								.valueOf(candidateStatusHistory.getStatusMaster().getStatusCode()))
						.collect(Collectors.toSet());

				if (candidateStatusEnums.contains(CandidateStatusEnum.EPFO)) {
					candidateReportDTO.setPfVerified("Yes");
				} else {
					candidateReportDTO.setPfVerified("No");
				}

				candidateReportDTO.setName(candidate.getCandidateName());
				candidateReportDTO.setApplicantId(candidate.getApplicantId());
				//candidateReportDTO.setDob(candidate.getDateOfBirth());
				candidateReportDTO.setDob(candidate.getPanDob());
				candidateReportDTO.setContactNo(candidate.getContactNumber());
				candidateReportDTO.setEmailId(candidate.getEmailId());
				if(isdigilocker!=null && isdigilocker[0]) {
					candidateReportDTO.setExperience(candidate.getIsFresher() ? "Fresher" : "Experience");
				}
				candidateReportDTO.setReportType(reportType);
				// ADDED TO DTO
				candidateReportDTO.setCandidateId(candidate.getCandidateId());
				Organization organization = candidate.getOrganization();
				candidateReportDTO.setOrganizationName(organization.getOrganizationName());
				candidateReportDTO.setProject(organization.getOrganizationName());
//				candidateReportDTO.setOrganizationLocation(organization.getOrganizationLocation());
				candidateReportDTO.setOrganizationLocation(organization.getBillingAddress());
//				candidateReportDTO.setOrganizationLogo(organization.getLogoUrl());
				try {
					// Create a temporary file to store the Blob data
					
					if(organization.getOrganizationLogo() != null) {
						File tempDir = new File(System.getProperty("java.io.tmpdir"));
						File tempF = File.createTempFile("data", ".dat", tempDir);
						Path tempFile = tempF.toPath();

						// Write the Blob data to the temporary file
						Files.copy(new ByteArrayInputStream(organization.getOrganizationLogo()), tempFile,
								StandardCopyOption.REPLACE_EXISTING);

						// Create a URL for the temporary file
						URL url = tempFile.toUri().toURL();

						candidateReportDTO.setOrganizationLogo(url.toString());
					}
					
				} catch (IOException e) {
					log.error("Exception occured in generateDocument method in ReportServiceImpl-->", e);
				}
				candidateReportDTO.setAccountName(candidate.getAccountName()!=null ? candidate.getAccountName(): null);
				if (candidateAddComments != null) {
					candidateReportDTO.setComments(candidateAddComments.getComments());
				}

				CandidateVerificationState candidateVerificationState = candidateService
						.getCandidateVerificationStateByCandidateId(candidate.getCandidateId());
				boolean hasCandidateVerificationStateChanged = false;
				if (Objects.isNull(candidateVerificationState)) {
					candidateVerificationState = new CandidateVerificationState();
					candidateVerificationState.setCandidate(candidate);
					final ZoneId id = ZoneId.systemDefault();
					candidateVerificationState
							.setCaseInitiationTime(ZonedDateTime.ofInstant(candidate.getCreatedOn().toInstant(), id));

				}
				switch (reportType) {
				case PRE_OFFER:
					candidateVerificationState.setPreApprovalTime(ZonedDateTime.now());
					break;
				case FINAL:
					candidateVerificationState.setFinalReportTime(ZonedDateTime.now());
					break;
				case INTERIM:
					if(orgServices!=null && orgServices.contains("EPFO") && orgServices.contains("DNHDB")
		 			    && !orgServices.contains("DIGILOCKER") && !orgServices.contains("ITR")) {
						//below condition for storing re initiation case information
						candidateService.saveCaseReinitDetails(candidateCode,null);
						candidateVerificationState.setInterimReportTime(candidateVerificationState!=null  && candidateVerificationState.getInterimReportTime()!=null? 
										candidateVerificationState.getInterimReportTime():ZonedDateTime.now());
					}else {
						candidateVerificationState.setInterimReportTime(ZonedDateTime.now());
					}
					
					break;

				}
				candidateVerificationState = candidateService.addOrUpdateCandidateVerificationStateByCandidateId(
						candidate.getCandidateId(), candidateVerificationState);
				candidateReportDTO.setFinalReportDate(DateUtil.convertToString(ZonedDateTime.now()));
				candidateReportDTO.setInterimReportDate(
						DateUtil.convertToString(candidateVerificationState.getInterimReportTime()));
				
				candidateReportDTO.setCaseReinitDate(candidateVerificationState.getCaseReInitiationTime()!=null ?
						DateUtil.convertToString(candidateVerificationState.getCaseReInitiationTime()) : null);
				candidateReportDTO.setInterimAmendedDate(candidateVerificationState.getInterimReportAmendedTime()!=null ?
						DateUtil.convertToString(candidateVerificationState.getInterimReportAmendedTime()) : null);

				
				
				// Specify the IST time zone (ZoneId.of("Asia/Kolkata"))
		        ZoneId istZone = ZoneId.of("Asia/Kolkata");

		        candidateReportDTO.setPreOfferReportDate(candidateVerificationState.getPreApprovalTime()!=null?
						DateUtil.convertToString(candidateVerificationState.getPreApprovalTime().withZoneSameInstant(istZone)): null);
		        // Convert the ZonedDateTime from GMT to IST
		        ZonedDateTime istZonedDateTime = candidateVerificationState.getCaseInitiationTime().withZoneSameInstant(istZone);
		        candidateReportDTO.setCaseInitiationDate(
						DateUtil.convertToString(istZonedDateTime));
//				candidateReportDTO.setCaseInitiationDate(
//						DateUtil.convertToString(candidateVerificationState.getCaseInitiationTime()));
				// executive summary
				Long organizationId = organization.getOrganizationId();
				List<OrganizationExecutive> organizationExecutiveByOrganizationId = organizationService
						.getOrganizationExecutiveByOrganizationId(organizationId);
				List<ExecutiveSummaryDto> executiveSummaryDtos = new ArrayList<>();
				organizationExecutiveByOrganizationId.stream().forEach(organizationExecutive -> {
					switch (organizationExecutive.getExecutive().getName()) {

					// System.out.println(organizationExecutive.getExecutive());
					case EDUCATION:
						log.info("Education block entered {}", candidate.getCandidateId());
						List<CandidateCafEducationDto> candidateCafEducationDtos = candidateService
								.getAllCandidateEducationByCandidateId(candidate.getCandidateId());
//						log.info("EDUCATION SERVICE SOURCE::{}",candidateCafEducationDtos.toString());

						List<EducationVerificationDTO> educationVerificationDTOS = candidateCafEducationDtos.stream()
								.map(candidateCafEducationDto -> {
									EducationVerificationDTO educationVerificationDTO = new EducationVerificationDTO();
									educationVerificationDTO.setVerificationStatus(
											VerificationStatus.valueOf(candidateCafEducationDto.getColorColorCode()));
									
									if(candidateCafEducationDto.getServiceSourceMasterSourceServiceId() != null) {
										educationVerificationDTO.setSource(SourceEnum.DIGILOCKER);
									}else {
										educationVerificationDTO.setSource(null);
									}
									//educationVerificationDTO.setSource(SourceEnum.DIGILOCKER);
									educationVerificationDTO.setDegree(candidateCafEducationDto.getCourseName());
									educationVerificationDTO
											.setUniversity(candidateCafEducationDto.getBoardOrUniversityName());
									educationVerificationDTO
											.setCustomRemark(candidateCafEducationDto.getCustomRemark());
									return educationVerificationDTO;
								}).collect(Collectors.toList());

						for (CandidateCafEducationDto temp : candidateCafEducationDtos) {
							if (temp.getIsHighestQualification()) {
								candidateReportDTO.setHighestQualification(temp.getSchoolOrCollegeName());
								candidateReportDTO.setCourseName(temp.getCourseName());
								candidateReportDTO.setUniversityName(temp.getBoardOrUniversityName());
								candidateReportDTO.setRollNo(temp.getCandidateRollNumber());
								candidateReportDTO.setYearOfPassing(temp.getYearOfPassing());
								candidateReportDTO.setEduCustomRemark(temp.getCustomRemark());
							}
						}

						candidateReportDTO.setEducationVerificationDTOList(educationVerificationDTOS);
						List<String> redArray = new ArrayList<>();
						;
						List<String> amberArray = new ArrayList<>();
						;
						List<String> greenArray = new ArrayList<>();
						;
						String status = null;
						for (EducationVerificationDTO s : educationVerificationDTOS) {
							if (s.getVerificationStatus().equals(VerificationStatus.RED)) {
								redArray.add("count");
							} else if (s.getVerificationStatus().equals(VerificationStatus.AMBER)) {
								amberArray.add("count");
							} else {
								greenArray.add("count");
							}
						}
						if (redArray.size() > 0) {
							status = VerificationStatus.RED.toString();
						} else if (amberArray.size() > 0) {
							status = VerificationStatus.AMBER.toString();
						} else {
							status = VerificationStatus.GREEN.toString();
						}
						candidateReportDTO.setEducationConsolidatedStatus(status);

						for (EducationVerificationDTO educationVerificationDto : candidateReportDTO
								.getEducationVerificationDTOList()) {

							executiveSummaryDtos.add(new ExecutiveSummaryDto(ExecutiveName.EMPLOYMENT,
									educationVerificationDto.getDegree(),
									educationVerificationDto.getVerificationStatus()));
						}
						break;
					case IDENTITY:
//						System.out.println("inside identity *******************************");
						// verify from digilocker and itr
						List<IDVerificationDTO> idVerificationDTOList = new ArrayList<>();
						IDVerificationDTO aadhaarIdVerificationDTO = new IDVerificationDTO();
						String aadharNumber = candidate.getAadharNumber();
						if(isdigilocker!=null && isdigilocker[0] && aadharNumber != null) {
			
							aadhaarIdVerificationDTO.setName(candidate.getAadharName());
	//						aadhaarIdVerificationDTO.setName(candidate.getCandidateName());
							aadhaarIdVerificationDTO.setIDtype(IDtype.AADHAAR.label);
	//                      aadhaarIdVerificationDTO.setIdNo(candidate.getAadharNumber());

							String maskedAadharNumber = "XXXX XXXX " + aadharNumber.substring(aadharNumber.length() - 4);
							aadhaarIdVerificationDTO.setIdNo(maskedAadharNumber);
							
							aadhaarIdVerificationDTO.setSourceEnum(SourceEnum.DIGILOCKER);
							aadhaarIdVerificationDTO.setVerificationStatus(VerificationStatus.GREEN);
							idVerificationDTOList.add(aadhaarIdVerificationDTO);
							
						}
						
						IDVerificationDTO panIdVerificationDTO = new IDVerificationDTO();
						panIdVerificationDTO.setName(candidate.getPanName());
//						panIdVerificationDTO.setName(candidate.getAadharName());
//						if(orgServices!=null && orgServices.contains("ITR")){
//							panIdVerificationDTO.setName(candidate.getPanName());
//					    }
						panIdVerificationDTO.setIDtype(IDtype.PAN.label);
						panIdVerificationDTO.setIdNo(candidate.getPanNumber());
						panIdVerificationDTO.setSourceEnum(SourceEnum.DIGILOCKER);
						panIdVerificationDTO.setVerificationStatus(VerificationStatus.GREEN);
						idVerificationDTOList.add(panIdVerificationDTO);

						ItrEpfoHeaderDetails itrEpfoHeaderDetails = new ItrEpfoHeaderDetails();
						List<ItrEpfoHeaderDetails> epfoDetailsForMultiUAN= new ArrayList<>();

						List<CandidateEPFOResponse> uanList = candidateEPFOResponseRepository
								.findByCandidateId(candidate.getCandidateId());
						EpfoData epfoDtls = epfoDataRepository
								.findFirstByCandidateCandidateId(candidate.getCandidateId());

						for (CandidateEPFOResponse candidateEPFOResponse : uanList) {
							ItrEpfoHeaderDetails epfoDetailsForSingleUAN = new ItrEpfoHeaderDetails();
							IDVerificationDTO uanIdVerificationDTO = new IDVerificationDTO();

							if (epfoDtls != null) { 
								itrEpfoHeaderDetails.setEpfoName1(epfoDtls.getName());
								epfoDetailsForSingleUAN.setEpfoName1(candidateEPFOResponse.getUanName());
								if (candidateEPFOResponse.getUan() != null) {
									itrEpfoHeaderDetails.setUanNo1(candidateEPFOResponse.getUan());
									epfoDetailsForSingleUAN.setUanNo1(candidateEPFOResponse.getUan());
									candidateReportDTO.setUanVerified("Yes");
								} else {
									candidateReportDTO.setUanVerified("No");
								}
							}

							if(candidateEPFOResponse.getUan() != null && candidateEPFOResponse.getUanName() != null) {
								uanIdVerificationDTO.setName(candidateEPFOResponse.getUanName());
								uanIdVerificationDTO.setIDtype(IDtype.UAN.label);
								uanIdVerificationDTO.setIdNo(candidateEPFOResponse.getUan());
								uanIdVerificationDTO.setSourceEnum(SourceEnum.EPFO);
								uanIdVerificationDTO.setVerificationStatus(VerificationStatus.GREEN);
								idVerificationDTOList.add(uanIdVerificationDTO);
								epfoDetailsForMultiUAN.add(epfoDetailsForSingleUAN);
							}
						}

						if(orgServices!=null && orgServices.contains("ITR")){
							IDVerificationDTO itrPanIdVerificationDTO = new IDVerificationDTO();
							itrPanIdVerificationDTO.setName(candidate.getPanName());
							itrEpfoHeaderDetails.setItrName(candidate.getPanName());
							itrPanIdVerificationDTO.setIDtype(IDtype.PAN.label);
							itrPanIdVerificationDTO.setIdNo(candidate.getItrPanNumber());
							itrEpfoHeaderDetails.setPanNo(candidate.getItrPanNumber());
							itrPanIdVerificationDTO.setSourceEnum(SourceEnum.ITR);
							itrPanIdVerificationDTO.setVerificationStatus(VerificationStatus.GREEN);
							idVerificationDTOList.add(itrPanIdVerificationDTO);
						}
						
						boolean removeDigiPan = false;
						for(IDVerificationDTO idVerification: idVerificationDTOList) {
							if(idVerification.getSourceEnum().equals(SourceEnum.ITR) && idVerification.getName() != null && idVerification.getIdNo() != null) {
								removeDigiPan = true;
							}
						} 
						
						if(removeDigiPan)
							idVerificationDTOList.removeIf(idVerification -> idVerification.getSourceEnum() == SourceEnum.DIGILOCKER && idVerification.getIDtype() == IDtype.PAN.label);
						
						candidateReportDTO.setItrEpfoHeaderDetails(itrEpfoHeaderDetails);
						candidateReportDTO.setEpfoDetailsForMultiUAN(epfoDetailsForMultiUAN);

						List<String> redArray_id = new ArrayList<>();
						;
						List<String> amberArray_id = new ArrayList<>();
						;
						List<String> greenArray_id = new ArrayList<>();
						;
						String status_id = null;
						for (IDVerificationDTO s : idVerificationDTOList) {
							if (s.getVerificationStatus().equals(VerificationStatus.RED)) {
								redArray_id.add("count");
							} else if (s.getVerificationStatus().equals(VerificationStatus.AMBER)) {
								amberArray_id.add("count");
							} else {
								greenArray_id.add("count");
							}
						}
						if (redArray_id.size() > 0) {
							status_id = VerificationStatus.RED.toString();
						} else if (amberArray_id.size() > 0) {
							status_id = VerificationStatus.AMBER.toString();
						} else {
							status_id = VerificationStatus.GREEN.toString();
						}

						candidateReportDTO.setIdVerificationDTOList(idVerificationDTOList);
						candidateReportDTO.setIdConsolidatedStatus(status_id);
						PanCardVerificationDto panCardVerificationDto = new PanCardVerificationDto();
						panCardVerificationDto.setInput(candidate.getPanNumber());
						panCardVerificationDto.setOutput(candidate.getPanNumber());
						panCardVerificationDto.setSource(SourceEnum.DIGILOCKER);
						panCardVerificationDto.setVerificationStatus(VerificationStatus.GREEN);
						candidateReportDTO.setPanCardVerification(panCardVerificationDto);
						executiveSummaryDtos
								.add(new ExecutiveSummaryDto(ExecutiveName.IDENTITY, "Pan", VerificationStatus.GREEN));
//
						AadharVerificationDTO aadharVerification = new AadharVerificationDTO();
						aadharVerification.setAadharNo(candidate.getAadharNumber());
						aadharVerification.setName(candidate.getAadharName());
						aadharVerification.setFatherName(candidate.getAadharFatherName());
						aadharVerification.setDob(candidate.getAadharDob());
						aadharVerification.setSource(SourceEnum.DIGILOCKER);
						candidateReportDTO.setAadharCardVerification(aadharVerification);
						executiveSummaryDtos.add(
								new ExecutiveSummaryDto(ExecutiveName.IDENTITY, "Aadhar", VerificationStatus.GREEN));
						break;
					case EMPLOYMENT:
						// Calendar cal = Calendar.getInstance();
						// cal.setTimeZone(TimeZone.getTimeZone("GMT"));

						List<CandidateCafExperience> candidateCafExperienceList = candidateService
								.getCandidateExperienceByCandidateId(candidate.getCandidateId());
						List<CandidateCafExperienceDto> employementDetailsDTOlist = new ArrayList<>();
						if (!candidateCafExperienceList.isEmpty()) { 
//							Date dateWith1Days = null;
//							Date doee = null;

							for (CandidateCafExperience candidateCafExperience : candidateCafExperienceList) {
								entityManager.detach(candidateCafExperience);
								Date dateWith1Days = null;
								Date doee = null;
								if (candidateCafExperience.getInputDateOfJoining() != null) {
									Date doj = candidateCafExperience.getInputDateOfJoining();
									Calendar cal = Calendar.getInstance();
//									cal.setTimeZone(TimeZone.getTimeZone("IST"));
									cal.setTime(doj);
//									cal.add(Calendar.DATE, 1);
									dateWith1Days = cal.getTime();

								}
								if (candidateCafExperience.getInputDateOfExit() != null) {
									Date doe = candidateCafExperience.getInputDateOfExit();
									Calendar cal = Calendar.getInstance();
									cal.setTime(doe);
//									cal.add(Calendar.DATE, 1);
									doee = cal.getTime();
								}

								if (doee == null) { // added to check and assign current employer
									candidateReportDTO
											.setCurrentEmployment(candidateCafExperience.getCandidateEmployerName());
									candidateReportDTO.setDateOfJoin(candidateCafExperience.getInputDateOfJoining());
									candidateReportDTO.setDateOfExit(candidateCafExperience.getInputDateOfExit());
								}

								String str = "NOT_AVAILABLE";
								CandidateCafExperienceDto candidateCafExperienceDto = this.modelMapper
										.map(candidateCafExperience, CandidateCafExperienceDto.class);
//								candidateCafExperienceDto.setInputDateOfJoining(
//										dateWith1Days != null ? sdf.format(dateWith1Days) : null);
//								candidateCafExperienceDto.setInputDateOfExit(doee != null ? sdf.format(doee) : str);

//								candidateCafExperienceDto.setInputDateOfJoining(
//										dateWith1Days != null ? new SimpleDateFormat("dd-MM-yyyy").format(dateWith1Days)
//												: null);
								candidateCafExperienceDto.setInputDateOfJoining(
										dateWith1Days != null ? new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(dateWith1Days): str);

//								candidateCafExperienceDto.setInputDateOfExit(
//										doee != null ? new SimpleDateFormat("dd-MM-yyyy").format(doee) : str);

								candidateCafExperienceDto.setInputDateOfExit(
										doee != null ? new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(doee): str);
								
								
								candidateCafExperienceDto
										.setCandidateEmployerName(candidateCafExperience.getCandidateEmployerName());
								candidateCafExperienceDto
										.setServiceName(candidateCafExperience.getServiceSourceMaster() != null
												? candidateCafExperience.getServiceSourceMaster().getServiceName()
												: "");
								// System.out.println("inside exp"+employe.getCandidateEmployerName());
								employementDetailsDTOlist.add(candidateCafExperienceDto);
							}
							
							// sorting for employment details	        
					        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);
					        Collections.sort(employementDetailsDTOlist, (s1, s2) -> {
					            try {
					            	LocalDate date1 = null;
					            	if(s1.getInputDateOfJoining() != null && !s1.getInputDateOfJoining().equals("NOT_AVAILABLE"))
					            		date1 = LocalDate.parse(s1.getInputDateOfJoining(), formatter2);
					            	else
					            		date1 = LocalDate.now();
					            	LocalDate date2 = null;
					            	if(s2.getInputDateOfJoining() != null && !s2.getInputDateOfJoining().equals("NOT_AVAILABLE"))
					            		date2 = LocalDate.parse(s2.getInputDateOfJoining(), formatter2);
					            	else
					            		date2 = LocalDate.now();
//					            	LocalDate date1 = s1.getInputDateOfJoining() != null ? LocalDate.parse(s1.getInputDateOfJoining(), formatter2) : LocalDate.now();
//					            	LocalDate date2 = s2.getInputDateOfJoining() != null ? LocalDate.parse(s2.getInputDateOfJoining(), formatter2) : LocalDate.now();
					                return date1.compareTo(date2);
					            } catch (DateTimeParseException e) {
					            	log.error("Exception occured in generateDocument method in ReportServiceImpl-->", e);
					                return 0; // Handle parsing error, e.g., consider them equal
					            }
					        });
					        Collections.reverse(employementDetailsDTOlist);
							candidateReportDTO.setEmployementDetailsDTOlist(employementDetailsDTOlist);

						}
						Collections.sort(candidateCafExperienceList, new Comparator<CandidateCafExperience>() {
							@Override
							public int compare(CandidateCafExperience o1, CandidateCafExperience o2) {
								Date doj1 = null;
				            	if(o1.getInputDateOfJoining() != null && !o1.getInputDateOfJoining().equals("NOT_AVAILABLE"))
				            		doj1 = o1.getInputDateOfJoining();
				            	else
				            		doj1 = new Date();
				            	Date doj2 = null;
				            	if(o2.getInputDateOfJoining() != null && !o2.getInputDateOfJoining().equals("NOT_AVAILABLE"))
				            		doj2 = o2.getInputDateOfJoining();
				            	else
				            		doj2 = new Date();
//								Date doj1 = o1.getInputDateOfJoining() != null ? o1.getInputDateOfJoining() : new Date();
//								Date doj2 = o2.getInputDateOfJoining() != null ? o2.getInputDateOfJoining() : new Date();
//								return o1.getInputDateOfJoining().compareTo(o2.getInputDateOfJoining());
								return doj1.compareTo(doj2);
							}
						});
						Collections.reverse(candidateCafExperienceList);
						cleanDate(candidateCafExperienceList);
						
						List<CandidateCafExperience> candidateExperienceFromItrEpfo = candidateService
								.getCandidateExperienceFromItrAndEpfoByCandidateId(candidate.getCandidateId(), true);
						Collections.sort(candidateExperienceFromItrEpfo, new Comparator<CandidateCafExperience>() {
							@Override
							public int compare(CandidateCafExperience o1, CandidateCafExperience o2) {
								Date doj1 = null;
				            	if(o1.getInputDateOfJoining() != null && !o1.getInputDateOfJoining().equals("NOT_AVAILABLE"))
				            		doj1 = o1.getInputDateOfJoining();
				            	else
				            		doj1 = new Date();
				            	Date doj2 = null;
				            	if(o2.getInputDateOfJoining() != null && !o2.getInputDateOfJoining().equals("NOT_AVAILABLE"))
				            		doj2 = o2.getInputDateOfJoining();
				            	else
				            		doj2 = new Date();
//								Date doj1 = o1.getInputDateOfJoining() != null ? o1.getInputDateOfJoining() : new Date();
//								Date doj2 = o2.getInputDateOfJoining() != null ? o2.getInputDateOfJoining() : new Date();
//								return o1.getInputDateOfJoining().compareTo(o2.getInputDateOfJoining());
								return doj1.compareTo(doj2);
							} 
						});
						Collections.reverse(candidateExperienceFromItrEpfo);
						cleanDate(candidateExperienceFromItrEpfo);
						ServiceOutcome<ToleranceConfig> toleranceConfigByOrgId = organizationService
								.getToleranceConfigByOrgId(organizationId);
						// System.out.println(candidateCafExperienceList+"candidateCafExperienceList");
						if (!candidateCafExperienceList.isEmpty()) {
							// validate experience and tenure
							List<EmploymentVerificationDto> employmentVerificationDtoList = validateAndCompareExperience(
									candidateCafExperienceList, candidateExperienceFromItrEpfo,
									toleranceConfigByOrgId.getData());
//							employmentVerificationDtoList
//									.sort(Comparator.comparing(EmploymentVerificationDto::getDoj).reversed());
							Collections.sort(employmentVerificationDtoList, new Comparator<EmploymentVerificationDto>() {

								@Override
								public int compare(EmploymentVerificationDto o1, EmploymentVerificationDto o2) {
									Date doj1 = null;
					            	if(o1.getDoj() != null && !o1.getDoj().equals("NOT_AVAILABLE"))
					            		doj1 = o1.getDoj();
					            	else
					            		doj1 = new Date();
					            	Date doj2 = null;
					            	if(o2.getDoj() != null && !o2.getDoj().equals("NOT_AVAILABLE"))
					            		doj2 = o2.getDoj();
					            	else
					            		doj2 = new Date();
//									Date doj1 = o1.getDoj() != null ? o1.getDoj() : new Date();
//									Date doj2 = o2.getDoj() != null ? o2.getDoj() : new Date();
									return doj1.compareTo(doj2);
								} 
							});
							Collections.reverse(employmentVerificationDtoList);
							
							candidateReportDTO.setEmploymentVerificationDtoList(employmentVerificationDtoList);

							List<EmploymentTenureVerificationDto> employmentTenureDtoList = validateAndCompareExperienceTenure(
									employmentVerificationDtoList, candidateExperienceFromItrEpfo,
									toleranceConfigByOrgId.getData(), candidateCafExperienceList);

							int totalYears = 0;
							int totalMonths = 0;
							int totalDays = 0;

							for (EmploymentTenureVerificationDto data : employmentTenureDtoList) {
								if(data.getSource() != null && !data.getOutput().contains("Data Not Found")) {
									String[] parts = data.getOutput().split(" ");
//									String[] parts = data.getInput().split(" ");
									int years = Integer.parseInt(parts[0]);
									int months = Integer.parseInt(parts[2]);
									int days = Integer.parseInt(parts[4]);

									totalYears += years;
									totalMonths += months;
									totalDays += days;
								}
							}

							int extraYears = totalMonths / 12;
							int remainingMonths = totalMonths % 12;

							totalYears += extraYears;
							
							int extraMonths = totalDays / 30;
							int remainingDays = totalDays % 30;
							remainingMonths+=extraMonths;

							candidateReportDTO.setTotalTenure(totalYears + " years, " + remainingMonths + " months, " + remainingDays + " days");
							candidateReportDTO.setNoOfYearsToBeVerified(candidate.getExperienceInMonth() + " years");

							candidateCafExperienceList.forEach(candidateCafExperience -> {
								employmentTenureDtoList.forEach(employmentTenureDto -> {

									if (candidateCafExperience.getCandidateCafExperienceId() == employmentTenureDto
											.getCandidateCafExperienceId()) {
										employmentTenureDto.setVerificationStatus(VerificationStatus
												.valueOf(candidateCafExperience.getColor().getColorCode()));
									}
								});
								employmentVerificationDtoList.forEach(employmentVerificationDto -> {
									if (candidateCafExperience
											.getCandidateCafExperienceId() == employmentVerificationDto
													.getCandidateCafExperienceId()) {
										employmentVerificationDto.setVerificationStatus(VerificationStatus
												.valueOf(candidateCafExperience.getColor().getColorCode()));
									}

								});
							});

							//Overriding tenure list color codes for ITR if tolerance configuration is present for ORG
							if(toleranceConfigByOrgId.getData()!=null && toleranceConfigByOrgId.getData().getTenure()!=null 
              && toleranceConfigByOrgId.getData().getTenure()!=0) {
                
                for (EmploymentTenureVerificationDto s : employmentTenureDtoList) {
                  Boolean undisclosed = s.getUndisclosed() != null ? s.getUndisclosed(): false;
									
									if(s.getSource()!=null && s.getSource().equals(SourceEnum.ITR)
											&& !s.getInput().equalsIgnoreCase("Data Not Found") && !s.getOutput().equalsIgnoreCase("Data Not Found")
											&& !s.getVerificationStatus().equals(VerificationStatus.MOONLIGHTING)
											&& !s.getVerificationStatus().equals(VerificationStatus.OUTOFSCOPE)
											&& !undisclosed
											&& s.getSecondarySource() == null) {
										String[] idArr = s.getInput().split(", ");
										String[] odArr = s.getOutput().split(", ");
										
										double idYear=Double.parseDouble(idArr[0].replace(" Y", ""));
										double odYear=Double.parseDouble(odArr[0].replace(" Y", ""));
										double idMonths=Double.parseDouble(idArr[1].replace(" M", ""));
										double odMonths=Double.parseDouble(odArr[1].replace(" M", ""));
										
										int orgtenure = toleranceConfigByOrgId.getData().getTenure();
										double orgTenureInMonths = orgtenure / 30d;

											double idInMonths = idYear*12+idMonths;
											double odInMonths = odYear*12+odMonths;
											double monthsDifference = Math.max(idInMonths, odInMonths) - Math.min(idInMonths, odInMonths);
											s.setVerificationStatus( monthsDifference <= orgTenureInMonths ? VerificationStatus.GREEN : VerificationStatus.AMBER);
										   log.info("Tenure idMonths::{}", idInMonths);
									       log.info("Tenure odMonths::{}", odInMonths);
									       log.info("Tenure orgTenureInMonths::{}", monthsDifference);
								       
									}
							    }
							}
							//end Overriding tenure
//							employmentTenureDtoList
//									.sort(Comparator.comparing(EmploymentTenureVerificationDto::getDoj).reversed());
							Collections.sort(employmentTenureDtoList, new Comparator<EmploymentTenureVerificationDto>() {

								@Override
								public int compare(EmploymentTenureVerificationDto o1, EmploymentTenureVerificationDto o2) {
									Date doj1 = null;
					            	if(o1.getDoj() != null && !o1.getDoj().equals("NOT_AVAILABLE"))
					            		doj1 = o1.getDoj();
					            	else
					            		doj1 = new Date();
					            	Date doj2 = null;
					            	if(o2.getDoj() != null && !o2.getDoj().equals("NOT_AVAILABLE"))
					            		doj2 = o2.getDoj();
					            	else
					            		doj2 = new Date();
//									Date doj1 = o1.getDoj() != null ? o1.getDoj() : new Date();
//									Date doj2 = o2.getDoj() != null ? o2.getDoj() : new Date();
									return doj1.compareTo(doj2);
								} 
							});
							Collections.reverse(employmentTenureDtoList);
							
							candidateReportDTO.setEmploymentTenureVerificationDtoList(employmentTenureDtoList);
							List<String> redArray_emp = new ArrayList<>();
							List<String> amberArray_emp = new ArrayList<>();
							List<String> greenArray_emp = new ArrayList<>();
							String status_emp = null;
							
							// added to check employment verification as per given experience to verify
							float experienceCount = 0; //start
							float maxExperienceToBeVerified = candidate.getExperienceInMonth() !=null ? candidate.getExperienceInMonth() * 12 : 7* 12;
							for (EmploymentTenureVerificationDto s : employmentTenureDtoList) {
								
								if(!s.getInput().equalsIgnoreCase("Data Not Found")) {
									String arr[] = s.getInput().split(", ");

									float tempExpValue = Float.parseFloat(arr[0].replace(" Y", "")) * 12 + Float.parseFloat(arr[1].replace(" M", ""));
									if(experienceCount <=  maxExperienceToBeVerified) {
										if (s.getVerificationStatus().equals(VerificationStatus.RED)) {
											redArray_emp.add("count");
										} else if (s.getVerificationStatus().equals(VerificationStatus.AMBER)
												|| s.getVerificationStatus().equals(VerificationStatus.MOONLIGHTING)) {
											amberArray_emp.add("count");
										} else {
											greenArray_emp.add("count");
										}
										
										experienceCount += tempExpValue;
									}
								}
							} // end
							
							
							if (redArray_emp.size() > 0) {
								status_emp = VerificationStatus.RED.toString();
							} else if (amberArray_emp.size() > 0) {
								status_emp = VerificationStatus.AMBER.toString();
							} else {
								status_emp = VerificationStatus.GREEN.toString();
							}
							candidateReportDTO.setEmploymentConsolidatedStatus(status_emp);
//							candidateCafExperienceList.sort(
//									Comparator.comparing(CandidateCafExperience::getInputDateOfJoining).reversed());
							Collections.sort(candidateCafExperienceList, new Comparator<CandidateCafExperience>() {

								@Override
								public int compare(CandidateCafExperience o1, CandidateCafExperience o2) {
									Date doj1 = null;
					            	if(o1.getInputDateOfJoining() != null && !o1.getInputDateOfJoining().equals("NOT_AVAILABLE"))
					            		doj1 = o1.getInputDateOfJoining();
					            	else
					            		doj1 = new Date();
					            	Date doj2 = null;
					            	if(o2.getInputDateOfJoining() != null && !o2.getInputDateOfJoining().equals("NOT_AVAILABLE"))
					            		doj2 = o2.getInputDateOfJoining();
					            	else
					            		doj2 = new Date();
//									Date doj1 = o1.getInputDateOfJoining() != null ? o1.getInputDateOfJoining() : new Date();
//									Date doj2 = o2.getInputDateOfJoining() != null ? o2.getInputDateOfJoining() : new Date();
									return doj1.compareTo(doj2);
								} 
							});
							Collections.reverse(employmentVerificationDtoList);
							
							candidateReportDTO.setInputExperienceList(candidateCafExperienceList);
							EPFODataDto epfoDataDto = new EPFODataDto();

							List<CandidateEPFOResponse> canditateItrEpfoResponseOptional = candidateEPFOResponseRepository
									.findByCandidateId(candidate.getCandidateId()); // removed '.stream().findFirst();' to fetch multiple uan data

							// added for service history
							List<ServiceHistory> cafExperiences = new ArrayList<>();

							if (canditateItrEpfoResponseOptional.size() > 0) {
								List<EpfoDataResDTO> epfoDatas = new ArrayList<>();
								for(CandidateEPFOResponse canditateItrEpfoResponse: canditateItrEpfoResponseOptional) {
									String epfoResponse = canditateItrEpfoResponse.getEPFOResponse();
									try {
										ObjectMapper objectMapper = new ObjectMapper();
										JsonNode arrNode = objectMapper.readTree(epfoResponse).get("message");
										
										if (arrNode.isArray()) {
											for (final JsonNode objNode : arrNode) {
												EpfoDataResDTO epfoData = new EpfoDataResDTO();
												epfoData.setName(objNode.get("name").asText());
												epfoData.setUan(objNode.get("uan").asText());
												epfoData.setCompany(objNode.get("company").asText());
												epfoData.setDoe(objNode.get("doe").asText());
												epfoData.setDoj(objNode.get("doj").asText());
												if(objNode.has("memberId")) {	
													epfoData.setMemberId(objNode.get("memberId").asText());
												}
//												if(!epfoDatas.stream().anyMatch(o -> epfoData.getCompany().equalsIgnoreCase(o.getCompany()) && epfoData.getDoj().equals(o.getDoj()))) { // condition added to avoid duplicate entry
													epfoDatas.add(epfoData); 
//												}
											}
										}

										epfoDataDto.setCandidateName(epfoDatas.stream().map(EpfoDataResDTO::getName)
												.filter(StringUtils::isNotEmpty).findFirst().orElse(null));
										epfoDataDto.setUANno(canditateItrEpfoResponse.getUan());
										epfoDataDto.setEpfoDataList(epfoDatas);
										candidateReportDTO.setEpfoData(epfoDataDto);



//										int i = 0;
//										for (EpfoDataResDTO experience : epfoDatas) {
//											String inputTenure = "";
//											String outputTenure = "";
//											Date idoj = new Date(experience.getDoj());
//											Date idoe = new Date();
//											if (experience.getDoe() != null
//													&& !experience.getDoe().equalsIgnoreCase("NOT_AVAILABLE")) {
//												idoe = new Date(experience.getDoe());
//											}
	//
//											String gap = "0y 0m";
	//
//											if (idoj == null) {
//												inputTenure = 0 + "y " + 0 + "m";
//											} else {
//												LocalDate inputdoj = idoj.toInstant().atZone(ZoneId.systemDefault())
//														.toLocalDate();
//												LocalDate inputdoe = idoe == null ? LocalDate.now()
//														: idoe.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//												Period inputdiff = Period.between(inputdoj, inputdoe);
	//
//												int years = inputdiff.getYears();
//												int months = inputdiff.getMonths();
	//
//												if (inputdiff.getDays() > 0) {
//													months += 1;
//												}
//												inputTenure = years + "y " + months + "m";
//											}
	//
//											Date odoj = new Date(experience.getDoj());
//											Date odoe = new Date();
//											if (experience.getDoe() != null
//													&& !experience.getDoe().equalsIgnoreCase("NOT_AVAILABLE"))
//												odoe = new Date(experience.getDoe());
	//
//											if (odoj == null) {
//												outputTenure = 0 + "y " + 0 + "m";
//											} else {
//												LocalDate outputdoj = odoj.toInstant().atZone(ZoneId.systemDefault())
//														.toLocalDate();
//												LocalDate outputdoe = odoe == null ? LocalDate.now()
//														: odoe.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//												Period outputdiff = Period.between(outputdoj, outputdoe);
//												int oyears = outputdiff.getYears();
//												int omonths = outputdiff.getMonths();
//												if (outputdiff.getDays() > 0) {
//													omonths += 1;
//												}
//												outputTenure = oyears + "y " + omonths + "m";
	//
//												if (i < epfoDatas.size()) {
//													EpfoDataResDTO experience1 = epfoDatas.get(i);
//													if (i + 1 < epfoDatas.size() && experience1.getDoj() != null) {
//														EpfoDataResDTO experience2 = epfoDatas.get(i + 1);
//														LocalDate experience1Inputdoj = new Date(experience1.getDoj())
//																.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//														LocalDate experience2Outputdoe = new Date(
//																experience2.getDoe()) == null
//																		? LocalDate.now()
//																		: new Date(experience2.getDoe()).toInstant()
//																				.atZone(ZoneId.systemDefault())
//																				.toLocalDate();
//														Period gapPeriod = Period.between(experience2Outputdoe,
//																experience1Inputdoj);
//														int gapYears = gapPeriod.getYears();
//														int gapMonths = gapPeriod.getMonths();
//														if (gapPeriod.getDays() > 0) {
//															gapMonths += 1;
//														}
//														gap = gapYears + "y " + gapMonths + "m";
//													}
//													i++;
//												}
//											}
	//
//											for (EmploymentTenureVerificationDto employementDetails : candidateReportDTO
//													.getEmploymentTenureVerificationDtoList()) {
//												if (employementDetails.getEmployerName()
//														.equalsIgnoreCase(experience.getCompany())) {
//													employementDetails.setInput(inputTenure);
//												}
//											}
	//
//										}


									} catch (JsonProcessingException e) {
										log.error("Exception 2 occured in generateDocument method in ReportServiceImpl-->", e); 
									}
								}
								
								epfoDatas.stream().forEach(temp -> {
									ServiceHistory cafExperience = new ServiceHistory();
									cafExperience.setCandidateEmployerName(temp.getCompany());

									try {
										String tempDoj = temp.getDoj();
										if(temp.getDoj() != null && !temp.getDoj().equalsIgnoreCase("NOT_AVAILABLE")) {
											tempDoj = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(sdf.parse(temp.getDoj()));
											cafExperience.setInputDateOfJoining(tempDoj);
										}
										String tempDoe = temp.getDoe();
										if(temp.getDoe() != null && !temp.getDoe().equals("NOT_AVAILABLE")) {
											tempDoe = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(sdf.parse(temp.getDoe()));
										}
										cafExperience.setInputDateOfExit(tempDoe);
										cafExperience.setInputDateOfJoining(tempDoj);
									} catch (ParseException e) {
										log.error("Exception occured in service history method in ReportServiceImpl-->", e);
									}
									
									if(orgServices!=null && orgServices.contains("EPFO") && orgServices.contains("DNHDB")
							 			    && !orgServices.contains("DIGILOCKER") && !orgServices.contains("ITR")) {
										ServiceOutcome<String> suspectResponse = candidateService.suspectEmpMasterCheck(temp.getCompany(), 
			        			                 candidate.getOrganization().getOrganizationId());
							        	if (Boolean.TRUE.equals(suspectResponse.getOutcome()) && suspectResponse.getData().equalsIgnoreCase("RED")) {
												cafExperience.setServiceName("DNHDB");
										}else {
											cafExperience.setServiceName("EPFO");
										}
									}else {
										cafExperience.setServiceName("EPFO");
									}
									
									cafExperience.setMemberId(temp.getMemberId());
									cafExperience.setUan(temp.getUan());
									cafExperiences.add(cafExperience);
								});
								
							}
							

							List<ITRData> itrDataList = itrDataRepository
									.findAllByCandidateCandidateCodeOrderByFiledDateDesc(candidateCode);
							ITRDataDto itrDataDto = new ITRDataDto();
							// itrDataDto.setItrDataList(itrDataList);
							
							List<ITRData> formattedItrDataList = itrDataList.stream()

							        .map(itrData -> {
							        	ITRData formattedItrData = new ITRData();
							        	try {

							        		formattedItrData.setAmount(itrData.getAmount());
							        		formattedItrData.setAssesmentYear(itrData.getAssesmentYear());
							        		formattedItrData.setCandidate(itrData.getCandidate());
							        		formattedItrData.setDeductor(itrData.getDeductor());
							        		formattedItrData.setFiledDate(itrData.getFiledDate());
							        		formattedItrData.setFinancialYear(itrData.getFinancialYear());
							        		formattedItrData.setItrId(itrData.getItrId());
							        		formattedItrData.setSection(itrData.getSection());
							        		formattedItrData.setServiceSourceMaster(itrData.getServiceSourceMaster());
							        		formattedItrData.setTan(itrData.getTan());
							        		formattedItrData.setTds(itrData.getTds());

							        	SimpleDateFormat sdfItrDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

							        	formattedItrData.setDate(
							            		itrData.getDate() != null ? new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(sdfItrDate.parse(itrData.getDate())): null);

							        	return formattedItrData;
							        	} catch (ParseException e) {
							        		log.error("Exception occured while changing the date format for ITR 26AS data in ReportServiceImpl-->", e);
							            }
							            return formattedItrData;
							        })

							        .collect(Collectors.toList());
							itrDataDto.setItrDataList(formattedItrDataList);
							
							candidateReportDTO.setItrData(itrDataDto);

							// start
							
							List<ITRData> filteredITRList = new ArrayList<>();
							Integer tenureTolerance = toleranceConfigByOrgId.getData().getTenure() != null ? toleranceConfigByOrgId.getData().getTenure() / 30 : 5;
							
					        for (int i = 0; i < itrDataList.size(); i++) {
					            LocalDate formattedDate = LocalDate.parse(itrDataList.get(i).getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
					            YearMonth formattedYearMonth = YearMonth.from(formattedDate);

					            int epfoIndex = -1;
					            int epfoTenureTolerancePeriodIndex = -1;
					            
					            for (int j = 0; j < cafExperiences.size(); j++) {
					            	ServiceHistory epfoData = cafExperiences.get(j);
									
									if(epfoData.getInputDateOfJoining() != null && !epfoData.getInputDateOfJoining().equalsIgnoreCase("NOT_AVAILABLE")) {
						                LocalDate epfoDataDoj = LocalDate.parse(epfoData.getInputDateOfJoining(), DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
						                LocalDate epfoDataDoe = epfoData.getInputDateOfExit() != null ? epfoData.getInputDateOfExit().equalsIgnoreCase("NOT_AVAILABLE") ? LocalDate.now() : LocalDate.parse(epfoData.getInputDateOfExit(), DateTimeFormatter.ofPattern("dd-MMM-yyyy")) : LocalDate.now();
						                
						                if ((formattedYearMonth.isAfter(YearMonth.from(epfoDataDoj)) || formattedYearMonth.equals(YearMonth.from(epfoDataDoj))) && (formattedYearMonth.isBefore(YearMonth.from(epfoDataDoe)) || formattedYearMonth.equals(YearMonth.from(epfoDataDoe))) && CommonUtils.checkStringSimilarity(itrDataList.get(i).getDeductor(), epfoData.getCandidateEmployerName()) > 0.90) {
						                    epfoIndex = j;
						                    break;
						                }
									}
					            }
					            
					            for (int j = 0; j < cafExperiences.size(); j++) {
					            	ServiceHistory epfoData = cafExperiences.get(j);

					                LocalDate epfoDataDoe = epfoData.getInputDateOfExit() != null ? epfoData.getInputDateOfExit().equalsIgnoreCase("NOT_AVAILABLE") ? LocalDate.now() : LocalDate.parse(epfoData.getInputDateOfExit(), DateTimeFormatter.ofPattern("dd-MMM-yyyy")) : LocalDate.now();
					                LocalDate endDateWithTolerance = epfoDataDoe.plusMonths(tenureTolerance);
//					                String employerName = trimEmployerName(form26AsSummaryTableList.get(i).getDeductor());
//					                String epfoCompanyName = trimEmployerName(epfoData.getCompany());
					                
					                if ((formattedYearMonth.isAfter(YearMonth.from(epfoDataDoe)) || formattedYearMonth.equals(YearMonth.from(epfoDataDoe))) && (formattedYearMonth.isBefore(YearMonth.from(endDateWithTolerance)) || formattedYearMonth.equals(YearMonth.from(endDateWithTolerance)))
					                        && CommonUtils.checkStringSimilarity(itrDataList.get(i).getDeductor(), epfoData.getCandidateEmployerName()) > 0.90) {
					                    epfoTenureTolerancePeriodIndex = j;
					                    break;
					                }
					            }
					            
					            if (epfoIndex == -1 && epfoTenureTolerancePeriodIndex == -1) {
					            	filteredITRList.add(itrDataList.get(i));
					            }
					        }
					        
					        
							HashMap<String, List<ITRData>> map = new HashMap<String, List<ITRData>>();
							filteredITRList.forEach(itr -> {
								if (!map.containsKey(itr.getDeductor())) {
									List<ITRData> list = new ArrayList<>();
									filteredITRList.forEach(itrIndivisual -> {
										if (itr.getDeductor().equalsIgnoreCase(itrIndivisual.getDeductor())) {
											list.add(itrIndivisual);
										}
									});
									map.put(itr.getDeductor(), list);
								}
							});

							map.keySet().forEach(itrMapKey -> {
//								if (!containsObj(candidateReportDTO.getEmployementDetailsDTOlist(), itrMapKey)) {

									CandidateCafExperienceDto employementDetail = new CandidateCafExperienceDto();
									List<ITRData> itrList = map.get(itrMapKey);
									if (!itrList.isEmpty()) {
										employementDetail.setCandidateEmployerName(itrList.get(0).getDeductor());
										employementDetail.setServiceName(
												itrList.get(0).getServiceSourceMaster().getServiceName());

										if (itrList.size() == 1) {
											employementDetail.setInputDateOfJoining(itrList.get(0).getDate());
											employementDetail.setInputDateOfExit(itrList.get(0).getDate());
										} else {
											employementDetail
													.setInputDateOfJoining(itrList.get(itrList.size() - 1).getDate());
											employementDetail.setInputDateOfExit(itrList.get(0).getDate());
										}


										if (employementDetail.getServiceName().equals("ITR")) {
											ServiceHistory cafExperience = new ServiceHistory();
											cafExperience.setCandidateEmployerName(employementDetail.getCandidateEmployerName());
											try {
												if(employementDetail.getInputDateOfJoining() != null) {
													String tempDoj = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(sdf.parse(employementDetail.getInputDateOfJoining().replace('-','/')));
													cafExperience.setInputDateOfJoining(tempDoj);
												}
												String tempDoe = employementDetail.getInputDateOfExit();
												if(employementDetail.getInputDateOfExit() != null && !employementDetail.getInputDateOfExit().equals("NOT_AVAILABLE")) {
													tempDoe = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(sdf.parse(employementDetail.getInputDateOfExit().replace('-','/')));
												}
												cafExperience.setInputDateOfExit(tempDoe);
											} catch (Exception e) {
												// TODO: handle exception
											}
											cafExperience.setServiceName("ITR"); 
											
											double similarity = 0.0;
											ServiceHistory matchedServiceHistory = null;
											for(ServiceHistory candidateCafExperice: cafExperiences) {
												if(CommonUtils.checkStringSimilarity(cafExperience.getCandidateEmployerName(),
														candidateCafExperice.getCandidateEmployerName()) > similarity) {
													similarity = CommonUtils.checkStringSimilarity(cafExperience.getCandidateEmployerName(),
															candidateCafExperice.getCandidateEmployerName());
													
													if(similarity > 0.90)
														matchedServiceHistory = candidateCafExperice;
												}	
											}
											if(similarity < 0.90)  
												cafExperiences.add(cafExperience);
											else if(similarity > 0.90 && !isOverllappingTenure(cafExperience, matchedServiceHistory)
													&& !isSameOverllappingTenureDuration(cafExperience, matchedServiceHistory)) {
												cafExperiences.add(cafExperience);
											}
										}
									}
//								}
							});

//							candidateReportDTO.getEmployementDetailsDTOlist().stream().forEach(temp -> {
//								if (temp.getServiceName().equals("ITR") && !temp.getInputDateOfExit().equalsIgnoreCase("NOT_AVAILABLE")) {
//									CandidateCafExperienceDto cafExperience = new CandidateCafExperienceDto();
//									cafExperience = temp;
//
//									cafExperiences.add(cafExperience);
//								}
//							});
							candidateReportDTO.setServiceHistory(cafExperiences);
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
							Collections.sort(candidateReportDTO.getServiceHistory(), (s1, s2) -> {
								try { 
									LocalDate date1 = null;
					            	if(s1.getInputDateOfJoining() != null && !s1.getInputDateOfJoining().equals("NOT_AVAILABLE"))
					            		date1 = LocalDate.parse(s1.getInputDateOfJoining(), formatter);
					            	else
					            		date1 = LocalDate.now();
					            	LocalDate date2 = null;
					            	if(s2.getInputDateOfJoining() != null && !s2.getInputDateOfJoining().equals("NOT_AVAILABLE"))
					            		date2 = LocalDate.parse(s2.getInputDateOfJoining(), formatter);
					            	else
					            		date2 = LocalDate.now();
//									LocalDate date1 = s1.getInputDateOfJoining() != null ? LocalDate.parse(s1.getInputDateOfJoining(), formatter) : LocalDate.now();
//									LocalDate date2 = s1.getInputDateOfJoining() != null ? LocalDate.parse(s1.getInputDateOfJoining(), formatter) : LocalDate.now();
									return date1.compareTo(date2);
								} catch (DateTimeParseException e) {
									log.error("Exception occured in generateDocument method in ReportServiceImpl-->", e);
									return 0; // Handle parsing error, e.g., consider them equal
								}
							});
							Collections.reverse(candidateReportDTO.getServiceHistory());

							// end

							// System.out.println(candidateReportDTO.getEmploymentVerificationDtoList()+"candidateReportDTO");
							
							float experienceCountExecutiveSummary = 0; //start
							
							LocalDate today = LocalDate.now();
							String substringYear = ("" + today.getYear()).substring(2);
					        float todayInMonths = Integer.parseInt(substringYear) * 12 + today.getMonthValue();
						
					        float maxExperienceToBeVerifiedExecutiveSummary =candidate.getExperienceInMonth()!=null ? todayInMonths - candidate.getExperienceInMonth() * 12 : todayInMonths - 7 * 12 ;
					        for (EmploymentTenureVerificationDto employmentVerificationDto : employmentTenureDtoList) {
								if(!employmentVerificationDto.getInput().equalsIgnoreCase("Data Not Found")) {
//									String arr[] = employmentVerificationDto.getInput().split(", ");
//									float tempExpValue = Float.parseFloat(arr[0].replace(" Y", "")) * 12 + Float.parseFloat(arr[1].replace(" M", ""));

									Instant instant = employmentVerificationDto.getDoj().toInstant();
							        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
							        String subYear = ("" + localDate.getYear()).substring(2);
							        VerificationStatus verificationStatus = employmentVerificationDto.getSecondarySource() != null && employmentVerificationDto.getSecondarySource().equals(SourceEnum.DNHDB) ? VerificationStatus.RED : employmentVerificationDto.getVerificationStatus();
							        		
							        float tempExpValue = Integer.parseInt(subYear) * 12 + localDate.getMonthValue();
//									if(experienceCountExecutiveSummary <=  maxExperienceToBeVerifiedExecutiveSummary) {
							        if(tempExpValue >=  maxExperienceToBeVerifiedExecutiveSummary) {
										executiveSummaryDtos.add(new ExecutiveSummaryDto(ExecutiveName.EMPLOYMENT,
												employmentVerificationDto.getInput(),
												verificationStatus));
										
//										experienceCountExecutiveSummary += tempExpValue;
									} else if(employmentVerificationDto.getDoe() != null) {
										log.info("Doe check for no. of years to be verified :: {}", employmentVerificationDto.getDoe());
										instant = employmentVerificationDto.getDoe().toInstant();
								        localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
								        subYear = ("" + localDate.getYear()).substring(2);
								        		
								        tempExpValue = Integer.parseInt(subYear) * 12 + localDate.getMonthValue();
								        if(tempExpValue >=  maxExperienceToBeVerifiedExecutiveSummary) {
											executiveSummaryDtos.add(new ExecutiveSummaryDto(ExecutiveName.EMPLOYMENT,
													employmentVerificationDto.getInput(),
													verificationStatus));
											
										}
									}
								}
							// end

//								executiveSummaryDtos.add(new ExecutiveSummaryDto(ExecutiveName.EMPLOYMENT,
//										employmentVerificationDto.getInput(),
//										employmentVerificationDto.getVerificationStatus()));
							}

						}

						break;
					case ADDRESS:

						List<CandidateCafAddressDto> candidateAddress = candidateService.getCandidateAddress(candidate);
//						System.out.println("ADDRESS**************" + candidateAddress);
						List<AddressVerificationDto> collect = candidateAddress.stream().map(candidateCafAddressDto -> {
							AddressVerificationDto addressVerificationDto = new AddressVerificationDto();
							addressVerificationDto.setType("Address");
							addressVerificationDto.setInput(candidateCafAddressDto.getCandidateAddress());
							addressVerificationDto.setVerificationStatus(
									VerificationStatus.valueOf(candidateCafAddressDto.getColorColorCode()));
							
							String source = serviceSourceMasterRepository.findById(Long.valueOf(candidateCafAddressDto.getServiceSourceMasterSourceServiceId())).get().getServiceCode();
							if(source.equals("AADHARADDR")) {
								addressVerificationDto.setSource(SourceEnum.AADHAR);
							}
							if(source.equals("DLADDR")) {
								addressVerificationDto.setSource(SourceEnum.DrivingLicense);
							}
							if(source.equals("PAN")) {
								addressVerificationDto.setSource(SourceEnum.ITR);
							}
							
							addressVerificationDto.setCustomRemark(candidateCafAddressDto.getCustomRemark());
							List<String> type = new ArrayList<>();
							// if(candidateCafAddressDto.getIsAssetDeliveryAddress()) {
							// type.add("Communication");
							// } if(candidateCafAddressDto.getIsPresentAddress()) {
							// type.add("Present");

							// } if(candidateCafAddressDto.getIsPermanentAddress()) {
							// type.add("Premanent");
							// }
							// addressVerificationDto.setType(String.join(", ", type));
							return addressVerificationDto;
						}).collect(Collectors.toList());
						List<String> redArray_addr = new ArrayList<>();
						;
						List<String> amberArray_addr = new ArrayList<>();
						;
						List<String> greenArray_addr = new ArrayList<>();
						;
						String status_addr = null;
						for (AddressVerificationDto s : collect) {
							if (s.getVerificationStatus().equals(VerificationStatus.RED)) {
								redArray_addr.add("count");
							} else if (s.getVerificationStatus().equals(VerificationStatus.AMBER)) {
								amberArray_addr.add("count");
							} else {
								greenArray_addr.add("count");
							}
						}
						if (redArray_addr.size() > 0) {
							status_addr = VerificationStatus.RED.toString();
						} else if (amberArray_addr.size() > 0) {
							status_addr = VerificationStatus.AMBER.toString();
						} else {
							status_addr = VerificationStatus.GREEN.toString();
						}
						candidateReportDTO.setAddressConsolidatedStatus(status_addr);
						candidateReportDTO.setAddressVerificationDtoList(collect!=null && !collect.isEmpty() ? collect : null);
						// System.out.println("candidateReportDTO**************"+candidateReportDTO);

						if(collect!=null && !collect.isEmpty()) {
							for (AddressVerificationDto addressVerificationDto : candidateReportDTO
									.getAddressVerificationDtoList()) {
								// System.out.println("inside
								// for"+employmentVerificationDto+"emppppp"+candidateReportDTO);
								executiveSummaryDtos.add(new ExecutiveSummaryDto(ExecutiveName.EMPLOYMENT,
										addressVerificationDto.getInput(), addressVerificationDto.getVerificationStatus()));
							}
					    }
						break;
					case CRIMINAL:
						break;
					case REFERENCE_CHECK_1:
						break;
					case REFERENCE_CHECK_2:
						break;
					}

					candidateReportDTO.setExecutiveSummaryList(executiveSummaryDtos);

				});
				// System.out.println("before
				// pdf*******************************"+candidateReportDTO);

//				List<Source> sourceList = sourceRepository.findByIsActiveTrue();
//				ArrayList<CheckAttributeAndValueDTO> vendorAttributeDtos = new ArrayList<>();
//				if (sourceList.size() > 0) {
//					sourceList.forEach(source -> {
//						List<VendorChecks> vendorChecks = vendorChecksRepository
//								.findByCandidateCandidateIdAndSourceSourceId(candidate.getCandidateId(),
//										source.getSourceId());
//
//                        CheckAttributeAndValueDTO checkAttributeAndValueDto = new CheckAttributeAndValueDTO();
//                        
//						if (vendorChecks.size() > 0) {
//							VendorChecks vendorCheck = vendorChecks.get(vendorChecks.size() - 1);
//							
//							System.out.println("VENDORCHECK::::::::::"+vendorCheck.toString());
//							
//							Map<String, String> checkMap = new HashMap<>();
//
//							VendorUploadChecks vendorUploadChecks = vendorUploadChecksRepository
//									.findByVendorChecksVendorcheckId(vendorCheck.getVendorcheckId());
//
//							if (vendorUploadChecks != null) {
//								vendorCheck.getAgentAttirbuteValue()
//										.addAll(vendorUploadChecks.getVendorAttirbuteValue());
//							
//								System.out.println("getAGENTATTR:::>"+vendorCheck.getAgentAttirbuteValue());
//								
//								checkAttributeAndValueDto.setSourceName(vendorCheck.getSource().getSourceName());
//								checkAttributeAndValueDto.setVendorAttirbuteValue(vendorCheck.getAgentAttirbuteValue());
//								vendorAttributeDtos.add(checkAttributeAndValueDto);
//							}
//					        List<Map<String, String>> employmentChecksList = new ArrayList<>();
//
////							vendorCheck.getAgentAttirbuteValue().forEach(temp -> {
////								String[] arr = temp.split("=");
////								checkMap.put(arr[0], arr[1]);
//////					            employmentChecksList.add(checkMap);
////
////							});
//					
//							System.out.println("Contents of checkMap:");
//					        for (Map.Entry<String, String> entry : checkMap.entrySet()) {
//					            System.out.println(entry.getKey() + "=" + entry.getValue());
//					        }
//					        
////					        for (Map<String, String> employmentCheckMap : employmentChecksList) {
////					            System.out.println("Contents of employmentCheckMap:");
////					            for (Map.Entry<String, String> entry : employmentCheckMap.entrySet()) {
////					                System.out.println(entry.getKey() + "=" + entry.getValue());
////					            }
////					            System.out.println("---"); // Separator between maps
////					        }
////							
////					        System.out.println("employmentChecksList::::::"+employmentChecksList);
//					        
//						String checkName =	vendorCheck.getSource().getSourceName();
//						
//						 String sanitizedString = checkName.replaceAll("[^a-zA-Z0-9]", "");
//
//						  sanitizedString = sanitizedString.replaceAll("\\s", "");
//						
//						if(sanitizedString.contains("Education")) {
//							candidateReportDTO.setEducationCheck(checkMap);
//							log.info("MATCH FOUND FOR EDUCATION:::::::>{}");
//						}
//						if(sanitizedString.contains("Employment")) {
//							employmentChecksList.add(checkMap);
//							candidateReportDTO.setEmploymentCheck(employmentChecksList);
//							log.info("MATCJ FOUND FOR EMPLOYMENT::::::>{}");
//						}
//						System.out.println("employmentChecksList==========>"+employmentChecksList);
//						if(sanitizedString.contains("Global")){
//							candidateReportDTO.setGlobalDatabaseCheck(checkMap);
//							log.info("MATCH FOUND FOR 'Global Database check'::::>{}");
//						}
//						if(sanitizedString.contains("Address")){
//							candidateReportDTO.setAddressCheck(checkMap);
//							log.info("MATCH FOUND FOR ''Address''::::>{}");
//						}
//						if(sanitizedString.contains("ID")){
//							candidateReportDTO.setIdItemsCheck(checkMap);
//							log.info("MATCH FOUND FOR '''ID Items'''::::>{}");
//						}
//						if(sanitizedString.contains("Criminal")){
//							candidateReportDTO.setCriminalCheck(checkMap);
//							log.info("MATCH FOUND FOR ''''Criminal''''::::>{}");
//						}
//						if(sanitizedString.contains("Physical")){
////							candidateReportDTO.setPhysicalCheck(checkMap);
//							log.info("MATCH FOUND FOR '''''Physical Visit'''''::::>{}");
//						}
//						if(sanitizedString.contains("Drug")){
////							candidateReportDTO.setDrugTestCheck(checkMap);
//							log.info("MATCH FOUND FOR ''''''Drug Test''''''::::>{}");
//						}
//						
////							if (source.getSourceId() == Long.valueOf(3))
////								candidateReportDTO.setGlobalDatabaseCheck(checkMap);
////							if (source.getSourceId() == Long.valueOf(6))
////								candidateReportDTO.setCriminalCheck(checkMap);
////							if (source.getSourceId() == Long.valueOf(2) || source.getSourceId() == Long.valueOf(21) || source.getSourceId() == Long.valueOf(25))
////								candidateReportDTO.setEducationCheck(checkMap);
////							if (source.getSourceId() == Long.valueOf(1) || source.getSourceId() == Long.valueOf(11) || source.getSourceId() == Long.valueOf(27))
////								candidateReportDTO.setEmploymentCheck(checkMap);
////							if (source.getSourceId() == Long.valueOf(4))
////								candidateReportDTO.setAddressCheck(checkMap);
////							if (source.getSourceId() == Long.valueOf(5))
////								candidateReportDTO.setIdItemsCheck(checkMap);
////							if (source.getSourceId() == Long.valueOf(9))
////								candidateReportDTO.setPhysicalCheck(checkMap);
////							if (source.getSourceId() == Long.valueOf(10))
////								candidateReportDTO.setDrugTestCheck(checkMap);
//						}
//					});
//					
//	                candidateReportDTO.setCheckAttributeAndValue(vendorAttributeDtos);
//	                
//	                System.out.println("candidateReportDTO>>>>>>>>>setCheckAttributeAndValue:::::::"+candidateReportDTO.getCheckAttributeAndValue());
//
//				}
                List<Map<String, List<Map<String, String>>>> dataList = new ArrayList<>();

				List<VendorChecks> vendorList = vendorChecksRepository
						.findAllByCandidateCandidateId(candidate.getCandidateId());
				ArrayList<String> attributeValuesList = new ArrayList<>();
				ArrayList<CheckAttributeAndValueDTO> individualValuesList = new ArrayList<>();
				HashMap<String, LegalProceedingsDTO> criminalCheckListMap = new HashMap<>();
				String checkType = null;
				for (VendorChecks vendorChecks : vendorList) {
					if (vendorChecks != null &&
                            vendorChecks.getVendorCheckStatusMaster() != null &&
                            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode() != null &&
                            (vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("CLEAR") ||
                                    vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("MINORDISCREPANCY") ||
                                    vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("QCPENDING") ||
                                    vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("UNABLETOVERIFY") ||
                                    vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("MAJORDISCREPANCY"))) {
						
					User user = userRepository.findByUserId(vendorChecks.getVendorId());
					VendorUploadChecks vendorChecksss = vendorUploadChecksRepository
							.findByVendorChecksVendorcheckId(vendorChecks.getVendorcheckId());
					if (vendorChecksss != null) {
						CheckAttributeAndValueDTO checkAttributeAndValueDto = new CheckAttributeAndValueDTO();
						vendorUploadChecksDto = new VendorUploadChecksDto(user.getUserFirstName(),
								vendorChecksss.getVendorChecks().getVendorcheckId(),
								vendorChecksss.getVendorUploadedDocument(), vendorChecksss.getDocumentname(),
								vendorChecksss.getAgentColor().getColorName(),
								vendorChecksss.getAgentColor().getColorHexCode(), null,null,vendorChecksss.getCreatedOn(),null,null,null,null,null);

						ArrayList<String> combinedList = new ArrayList<>(vendorChecks.getAgentAttirbuteValue());
						combinedList.addAll(vendorChecksss.getVendorAttirbuteValue());

//						checkAttributeAndValueDto.setSourceName(vendorChecks.getSource().getSourceName());
//						checkAttributeAndValueDto.setAttributeAndValue(combinedList);
//						individualValuesList.add(checkAttributeAndValueDto);
//						vendorUploadChecksDto.setVendorAttirbuteValue(individualValuesList);
						
						if (vendorChecks.getSource().getSourceName().equalsIgnoreCase("GLOBAL DATABASE CHECK")) {
                            ObjectMapper objectMapper = new ObjectMapper();
                            for (String jsonData : vendorChecksss.getVendorAttirbuteValue()) {
//                                Map<String, List<Map<String, String>>> dataMap = null;
                                try {
                                    Map<String, List<Map<String, String>>> dataMap = objectMapper.readValue(jsonData, new TypeReference<Map<String, List<Map<String, String>>>>() {});
                                    System.out.println(dataMap);
                                    dataList.add(dataMap);
                                } catch (Exception e) {
                                    log.warn("An error occurred while parsing JSON data: {} " + e.getMessage());
                                    // Handle the exception here, you can log it, display a user-friendly message, etc.
                                }
                            }
//                            vendorAttributeDto.setSourceName(conventionalVendorliChecksToPerform.getCheckName());
//                            vendorAttributeDtos.add(vendorAttributeDto);
                        }
						if(vendorChecks.getSource().getSourceName().equalsIgnoreCase("Criminal")) {
							 LegalProceedingsDTO legalProceedingsDTO = new LegalProceedingsDTO();

	                            List<CriminalCheck> civilproceding = criminalCheckRepository.findByVendorCheckIdAndProceedingsType(vendorChecks.getVendorcheckId(), "CIVILPROCEDING");
	                            if (civilproceding.isEmpty() == false) {
	                                legalProceedingsDTO.setCivilProceedingList(civilproceding);
	                            }
	                            List<CriminalCheck> criminalproceding = criminalCheckRepository.findByVendorCheckIdAndProceedingsType(vendorChecks.getVendorcheckId(), "CRIMINALPROCEDING");
	                            if (criminalproceding.isEmpty() == false) {
	                                legalProceedingsDTO.setCriminalProceedingList(criminalproceding);
	                            }
	                            if(civilproceding.isEmpty() && criminalproceding.isEmpty()) {
	                            	System.out.println(" CRiminal IS EMPLTY >>"+vendorChecks.getVendorcheckId());
	                            	criminalCheckListMap.put(String.valueOf(vendorChecks.getCheckType()),null);
	                            	System.out.println("criminalCheckListMap>>>"+criminalCheckListMap.toString());
	                            }
	                            else {
	                            	 criminalCheckListMap.put(String.valueOf(vendorChecks.getCheckType()), legalProceedingsDTO);
	 	                            log.info("criminal check data" + criminalCheckListMap);
	                            }
	                           
						}
						
						 String employerName = extractValue(combinedList, "Employers Name");
					        
					        String qualification = extractValue(combinedList,"Qualification attained");
					        
					        String address = extractAddress(combinedList);
					        
					        String idItems = null;
					        
					        if(vendorChecks.getSource().getSourceName().equalsIgnoreCase("Employment")) {
					        	checkAttributeAndValueDto.setCheckDetails(employerName);
					        }else if(vendorChecks.getSource().getSourceName().equalsIgnoreCase("Education")) {
					        	checkAttributeAndValueDto.setCheckDetails(qualification);
					        }else if(vendorChecks.getSource().getSourceName().equalsIgnoreCase("Address") ||
					        		vendorChecks.getSource().getSourceName().equalsIgnoreCase("Criminal") ||
					        		vendorChecks.getSource().getSourceName().equalsIgnoreCase("Global Database check")) {
					        	checkAttributeAndValueDto.setCheckDetails(address);
					        }
					        else if(vendorChecks.getSource().getSourceName().equalsIgnoreCase("ID Items")) {
								VendorUploadChecks byVendorChecksVendorcheckId = vendorUploadChecksRepository.findByVendorChecksVendorcheckId(vendorChecks.getVendorcheckId());
								if(byVendorChecksVendorcheckId != null) {
									String vendorAttributeValue = byVendorChecksVendorcheckId.toString();
							        idItems = extractValue(byVendorChecksVendorcheckId.getVendorAttirbuteValue(), "proofName");
						        	checkAttributeAndValueDto.setCheckDetails(idItems);

								}
					        }

//					        checkAttributeAndValueDto.setSourceName(vendorChecks.getCheckType()!= null
//					        		&& vendorChecks.getCheckType().contains("Drug")?"Drug Test"
//					        				:vendorChecks.getCheckType().contains("Global")?"Global Database Check"
//					        						:vendorChecks.getCheckType());
					        
					        checkAttributeAndValueDto.setSourceName(
					        	    vendorChecks.getCheckType() != null && vendorChecks.getCheckType().contains("Drug")
					        	        ? "Drug Test"
					        	        : vendorChecks.getCheckType() != null && vendorChecks.getCheckType().contains("Global")
					        	            ? "Global Database Check"
					        	            : vendorChecks.getCheckType()
					        	);
					        
					        checkAttributeAndValueDto.setCheckStatus(vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode());
					        String remarks = extractValue(combinedList, "remarks");

						checkAttributeAndValueDto.setCheckRemarks(remarks);
						checkAttributeAndValueDto.setAttributeAndValue(combinedList);				
						individualValuesList.add(checkAttributeAndValueDto);
						vendorUploadChecksDto.setVendorAttirbuteValue(individualValuesList);




						vendordocDtoList.add(vendorUploadChecksDto);
					}
				}
				}

				candidateReportDTO.setVendorProofDetails(vendordocDtoList);
				candidateReportDTO.setDataList(dataList);
                candidateReportDTO.setCriminalCheckList(criminalCheckListMap);

				candidateReportDTO.setOrganisationScope(
						organisationScopeRepository.findByCandidateId(candidate.getCandidateId()));

//				List<Long> vendorCheckIds = vendorList.stream()
//						.map(VendorChecks::getVendorcheckId)
//						.collect(Collectors.toList());
				
				List<Long> vendorCheckIds = vendorList.stream()
					    .filter(vendorChecks ->
					        vendorChecks.getVendorCheckStatusMaster() != null &&
					        vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode() != null &&
					        (
					            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("CLEAR") ||
					            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("MINORDISCREPANCY") ||
					            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("QCPENDING") ||
					            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("UNABLETOVERIFY") ||
					            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("MAJORDISCREPANCY")
					        )
					    )
					    .map(VendorChecks::getVendorcheckId)
					    .collect(Collectors.toList());

//				List<String> checkName = vendorList.stream()
//						.map(vendorChecks -> vendorChecks.getSource().getSourceName())
//						.collect(Collectors.toList());
				
				List<String> checkName = vendorList.stream()
					    .filter(vendorChecks ->
					        vendorChecks.getVendorCheckStatusMaster() != null &&
					        vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode() != null &&
					        (
					            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("CLEAR") ||
					            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("MINORDISCREPANCY") ||
					            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("QCPENDING") ||
					            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("UNABLETOVERIFY") ||
					            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("MAJORDISCREPANCY")
					        )
					    )
					    .map(vendorChecks -> vendorChecks.getCheckType() != null ? vendorChecks.getCheckType().trim() : vendorChecks.getSource().getSourceName())
					    .collect(Collectors.toList());

				// VendorProof Document Starts Here
				List<VendorUploadChecks> result = vendorUploadChecksRepository.findByVendorChecksVendorcheckIds(vendorCheckIds);
				List<Map<String, List<String>>> encodedImagesList = new ArrayList<>();
				
				
				 List<InputStream> collect2 = new ArrayList<>();
	                File report = FileUtil.createUniqueTempFile("report", ".pdf");
	                String conventionalHtmlStr = null;
	                Date createdOn = candidate.getCreatedOn();
	                String htmlStr = pdfService.parseThymeleafTemplate("ConventionalReport_pdf", candidateReportDTO);
	                pdfService.generatePdfFromHtml(htmlStr, report);

	                List<Content> contentList = contentRepository.findAllByCandidateIdAndContentTypeIn(candidate.getCandidateId(), Arrays.asList(ContentType.ISSUED, ContentType.AGENT_UPLOADED));
	                List<File> files = contentList.stream().map(content -> {
	                    File uniqueTempFile = FileUtil.createUniqueTempFile(candidate.getCandidateId() + "_issued_" + content.getContentId().toString(), ".pdf");
	                    awsUtils.getFileFromS3(content.getBucketName(), content.getPath(), uniqueTempFile);
	                    return uniqueTempFile;
	                }).collect(Collectors.toList());
//	                File mergedFile = FileUtil.createUniqueTempFile(String.valueOf(candidate.getCandidateId()), ".pdf");
//
//	                collect.add(FileUtil.convertToInputStream(report));
//	                collect.addAll(files.stream().map(FileUtil::convertToInputStream).collect(Collectors.toList()));


				for (VendorUploadChecks vendorUploadCheck : result) {
					VendorUploadChecks byId = vendorUploadChecksRepository
							.findByVendorChecksVendorcheckId(vendorUploadCheck.getVendorChecks().getVendorcheckId());
					Map<String, List<String>> encodedImageMap = new HashMap<>();

					Long checkId = vendorUploadCheck.getVendorChecks().getVendorcheckId();
					String sourceName = vendorUploadCheck.getVendorChecks().getSource().getSourceName();
					log.info("Vendor sourceName ===== {}"+ sourceName);

					log.info("Size of checkName: {}", checkName.size());
					log.info(" ReportServiceImpl CandidateId : "+vendorUploadCheck.getVendorChecks().getCandidate().getCandidateId()+" | Unique CheckID In upload Vendor proof : "+checkId+" | CheckStatus : "+vendorUploadCheck.getVendorChecks().getVendorCheckStatusMaster().getCheckStatusName());


					if (checkName != null && !checkName.isEmpty()) {

						String nameOfCheck = checkName.isEmpty() ? null : checkName.get(encodedImagesList.size() % checkName.size());
//						log.info("VENDOR CHECK ID ====== {}"+checkId);
//						log.info("Vendor nameOfCheck ===== {}"+ nameOfCheck);
//
//						log.info("Vendor Upload Documents ====== {}" + vendorUploadCheck.getVendorUploadedDocument());

						byte[] documentBytes = vendorUploadCheck.getVendorUploadedDocument();

						ObjectMapper objectMapper = new ObjectMapper();
						String vendorUploadedImages = vendorUploadCheck.getVendorUploadedImage();
						
						Byte[] vendorUploadedImagesByte = null;
						String jsonString = null;
						String documentPresicedUrl = null;
//                      log.info("Vendor Upload Documents ====== {}" + vendorUploadCheck.getVendorUploadedDocument());
                      if (vendorUploadCheck.getVendorUploadDocumentPathKey() != null ||vendorUploadCheck.getVendorUploadDocumentPathKey()!=null) {
                          log.info("inside the aws path key retival for the check    --"+sourceName);
                          try {
							documentBytes = awsUtils.getbyteArrayFromS3(DIGIVERIFIER_DOC_BUCKET_NAME, vendorUploadCheck.getVendorUploadDocumentPathKey());
							ObjectMetadata metadataDocumentContentType = new ObjectMetadata();
		                    metadataDocumentContentType.setContentType(".pdf");
//		                    String documentPresicedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, vendorUploadCheck.getVendorUploadDocumentPathKey(),documentBytes, metadataDocumentContentType);
		                 // Check if the document is not PDF
		                    if (!isPDF(documentBytes)) {
		                        String base64EncodedDocument = Base64.getEncoder().encodeToString(documentBytes);
		                        documentPresicedUrl = base64EncodedDocument;
//		                        System.out.println("Base64 Encoded Document: " + base64EncodedDocument);
		                    } else {
		                        // Use the original document
		                        // For example:
		                    	documentPresicedUrl = vendorUploadCheck.getVendorUploadDocumentPathKey();
		                        log.info("Original Document URL: " + documentPresicedUrl);
		                    }

							vendorUploadedImages = new String(documentPresicedUrl);
//							
							// Convert list to JSONArray
							JSONArray jsonArray = new JSONArray();
//							for (String base64 : imageBase64List) {
							    JSONObject jsonObject = new JSONObject();
							    JSONArray imageArray = new JSONArray();
							    imageArray.put(vendorUploadedImages);
							    jsonObject.put("image", imageArray);
							    jsonArray.put(jsonObject);
//							}
							// Convert the JSON array to a string
							 jsonString = jsonArray.toString();
							
						} catch (IOException e) {
							log.info("Exception in DIGIVERIFIER_DOC_BUCKET_NAME {}"+e);
							}

                      }
						try {
							if (vendorUploadedImages != null) {
								List<Map<String, List<String>>> decodedImageList = objectMapper.readValue(jsonString, new TypeReference
										<List<Map<String, List<String>>>>() {});

								List<String> allEncodedImages = decodedImageList.stream()
										.flatMap(imageMap -> imageMap.values().stream())
										.flatMap(List::stream)
										.collect(Collectors.toList());

								// Loop through each image byte array and encode it to Base64
								List<String> encodedImagesForDocument = new ArrayList<>();

								log.info("encodedImagesForDocument::::: {}"+encodedImagesForDocument.size());
								
								encodedImageMap.put(nameOfCheck, allEncodedImages);
//								 for (Map.Entry<String, List<String>> entry : encodedImageMap.entrySet()) {
//							            String checkName1 = entry.getKey();
//							            System.out.println("checkName1>>>"+checkName1);
//							            List<String> s3Urls = entry.getValue();
////
////							            // Check if the sourceName matches the desired criteria
//							            if (checkName1.equals("Education UG")) {
//							                // Attach each PDF (S3 URL) to the existing PDF document
//							                for (String s3Url : s3Urls) {
//							                    attachPDF(s3Url);
//							                }
//							            }
//							        }
							}
							//	           	    	else if(documentBytes != null) {
							//	           	    		List<byte[]> imageBytes = convertPDFToImage(documentBytes);
							//	           	    		List<String> encodedImagesForDocument = new ArrayList<>();
							//	           	    		for (int j = 0; j < imageBytes.size(); j++) {
							//	           	    			byte[] imageBytess = imageBytes.get(j);
							//	           	    			String encodedImage = Base64.getEncoder().encodeToString(imageBytess); 
							//	           	    			encodedImagesForDocument.add(encodedImage);
							//
							//	           	    		}
							//	           	    		encodedImageMap.put(nameOfCheck, encodedImagesForDocument);
							//
							//	           	    	}
							else {
								log.info("Vendor uploaded document is null {}");
								encodedImageMap.put(nameOfCheck, null);

							}

							encodedImagesList.add(encodedImageMap);
							
	                        try {
	                        	CandidateReportDTO testConventionalCandidateReportDto = null;
	                            if (vendorUploadedImages != null) {
	                                if (isBase64Encoded(documentPresicedUrl)) {
	                                    log.info("BASE64 IMG for " + nameOfCheck + " entry");
	                                    List<Map<String, List<String>>> dynamicEncodedImagesList = new ArrayList<>();
	                                    // Generate table for this education entry
	                                    File allcheckDynamicReport = FileUtil.createUniqueTempFile(byId.getVendorChecks().getCheckType(), ".pdf");
	                                    String templateName;
	                                    if (nameOfCheck.toLowerCase().contains("education".toLowerCase())) {
	                                    	System.out.println("this is true : ");
	                                        templateName = "Conventional/EducationCheck-pdf";
	                                    }
	                                    else if (nameOfCheck.toLowerCase().contains("employment".toLowerCase())) {
	                                        templateName = "Conventional/EmploymentCheck-pdf";
	                                    } else if (nameOfCheck.toLowerCase().contains("criminal".toLowerCase())) {
	                                    	System.out.println("This is for criminal");
	                                        templateName = "Conventional/CriminalCheck-pdf";
	                                    } else if (nameOfCheck.toLowerCase().contains("global".toLowerCase())) {
	                                        templateName = "Conventional/GlobalCheck-pdf";
	                                    } else if (nameOfCheck.toLowerCase().contains("id".toLowerCase())) {
	                                        templateName = "Conventional/IDItemsCheck-pdf";
	                                    } else if (nameOfCheck.contains("LEGAL")) {
	                                        templateName = "Conventional/LegalRigntCheck";
	                                    } else if (nameOfCheck.contains("OFAC")) {
	                                        templateName = "Conventional/OfacCheck";
	                                    } else if (nameOfCheck.toLowerCase().contains("address".toLowerCase())) {
	                                        templateName = "Conventional/AddressCheck-pdf";
	                                    } else {
	                                        templateName = "Conventional/NoTableChecks";
	                                    }
	                                    testConventionalCandidateReportDto = new CandidateReportDTO();
	                                    
//	                                    List<VendorUploadChecksDto> filteredVendorProofs = candidateReportDTO.getVendorProofDetails().stream().filter(p -> p.getVendorChecks().equalsIgnoreCase(String.valueOf(byId.get().getSourceId())) == true).collect(Collectors.toList());
//	                                    List<VendorUploadChecksDto> filteredVendorProofs = candidateReportDTO.getVendorProofDetails().stream()
//	                                    	    .filter(p -> String.valueOf(p.getVendorChecks()).equalsIgnoreCase(String.valueOf(byId.getVendorChecks().getCheckType())))
//	                                    	    .collect(Collectors.toList());
	                                    
										List<VendorUploadChecksDto> filteredVendorProofs = candidateReportDTO
												.getVendorProofDetails().stream()
												.filter(p -> p.getVendorChecks()
														.equals(byId.getVendorChecks().getVendorcheckId()))
												.collect(Collectors.toList());

										System.out.println("nameOfCheck ::: :" + nameOfCheck);

										// Further filter the vendorAttirbuteValue within each filteredVendorProofs
										// element
										List<VendorUploadChecksDto> finalFilteredVendorProofs = filteredVendorProofs
												.stream().map(vendorUpload -> {
													// Filter the vendorAttirbuteValue list within each
													// VendorUploadChecksDto
													ArrayList<CheckAttributeAndValueDTO> filteredAttributes = vendorUpload
															.getVendorAttirbuteValue().stream()
															.filter(attr -> attr.getSourceName()
																	.equals(nameOfCheck))
															.collect(Collectors.toCollection(ArrayList::new));
													// Set the filtered attributes back into the
													// VendorUploadChecksDto
													vendorUpload.setVendorAttirbuteValue(filteredAttributes);
													return vendorUpload;
												}).collect(Collectors.toList());
	                                    
//	                                    testConventionalCandidateReportDto.setVendorProofDetails(filteredVendorProofs);
	                                    testConventionalCandidateReportDto.setVendorProofDetails(finalFilteredVendorProofs);
                               
	                                    Map<String, LegalProceedingsDTO> filteredCriminalCheckList = candidateReportDTO.getCriminalCheckList()
	                                            .entrySet()
	                                            .stream()
	                                            .filter(entry -> "Criminal".contains(entry.getKey()))
	                                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	                                    
	                                    if (nameOfCheck.equalsIgnoreCase("Criminal present")) {
											Map<String, LegalProceedingsDTO> modifiedMap = new HashMap<>();
											for (Map.Entry<String, LegalProceedingsDTO> entry : criminalCheckListMap
													.entrySet()) {
												// Get the key and value from the map entry
												String originalKey = entry.getKey();
												LegalProceedingsDTO value = entry.getValue();

												// Check if the key is "Criminal Permanent", if so, skip it
												if (!originalKey.equals("Criminal permanent")) {
													// Put the key and value into the new map
													modifiedMap.put(originalKey, value);
												}
											}
											testConventionalCandidateReportDto.setCriminalCheckList(modifiedMap);

										} else if (nameOfCheck.equalsIgnoreCase("Criminal permanent")) {
											Map<String, LegalProceedingsDTO> modifiedMap = new HashMap<>();
											for (Map.Entry<String, LegalProceedingsDTO> entry : criminalCheckListMap
													.entrySet()) {
												// Get the key and value from the map entry
												String originalKey = entry.getKey();
												LegalProceedingsDTO value = entry.getValue();

												// Check if the key is "Criminal Permanent", if so, skip it
												if (!originalKey.equals("Criminal present")) {
													// Put the key and value into the new map
													modifiedMap.put(originalKey, value);
												}
											}
											testConventionalCandidateReportDto.setCriminalCheckList(modifiedMap);

										}
	                                    
//	                                    testConventionalCandidateReportDto.setCriminalCheckList(filteredCriminalCheckList);
	                                    testConventionalCandidateReportDto.setDataList(candidateReportDTO.getDataList());
	                                    dynamicEncodedImagesList.add(encodedImageMap);
	                                    testConventionalCandidateReportDto.setPdfByes(dynamicEncodedImagesList);
	                                    String tableHtmlStr = pdfService.parseThymeleafTemplate(templateName, testConventionalCandidateReportDto);
	                                    pdfService.generatePdfFromHtml(tableHtmlStr, allcheckDynamicReport);
	                                    // Collect education proof and table
	                                    List<InputStream> educationProof = new ArrayList<>();
	                                    educationProof.add(FileUtil.convertToInputStream(allcheckDynamicReport));
//	                                    educationProof.add(new FileInputStream(fileFromS3));
	                                    collect2.addAll(educationProof);
	                                } else {
	                                    log.info("Fetching the PDF Proof for :" + nameOfCheck);
	                                    // Fetch the PDF file from S3
	                                    File fileFromS3 = awsUtils.getFileFromS3(DIGIVERIFIER_DOC_BUCKET_NAME, documentPresicedUrl);
	                                    // Generate table for this education entry
	                                    File allcheckDynamicReport = FileUtil.createUniqueTempFile(byId.getVendorChecks().getCheckType(), ".pdf");
	                                    String templateName;
	                                    if (nameOfCheck.toLowerCase().contains("education".toLowerCase())) {
	                                    	System.out.println("this is true : ");
	                                        templateName = "Conventional/EducationCheck-pdf";
	                                    }
	                                    else if (nameOfCheck.toLowerCase().contains("employment".toLowerCase())) {
	                                        templateName = "Conventional/EmploymentCheck-pdf";
	                                    } else if (nameOfCheck.toLowerCase().contains("criminal".toLowerCase())) {
	                                    	System.out.println("This is for criminal");
	                                        templateName = "Conventional/CriminalCheck-pdf";
	                                    } else if (nameOfCheck.toLowerCase().contains("global".toLowerCase())) {
	                                        templateName = "Conventional/GlobalCheck-pdf";
	                                    } else if (nameOfCheck.toLowerCase().contains("id".toLowerCase())) {
	                                        templateName = "Conventional/IDItemsCheck-pdf";
	                                    } else if (nameOfCheck.contains("LEGAL")) {
	                                        templateName = "Conventional/LegalRigntCheck";
	                                    } else if (nameOfCheck.contains("OFAC")) {
	                                        templateName = "Conventional/OfacCheck";
	                                    } else if (nameOfCheck.toLowerCase().contains("address".toLowerCase())) {
	                                        templateName = "Conventional/AddressCheck-pdf";
	                                    } else {
	                                        templateName = "Conventional/NoTableChecks";
	                                    }

	                                    testConventionalCandidateReportDto = new CandidateReportDTO();
	                                    
										List<VendorUploadChecksDto> filteredVendorProofs = candidateReportDTO
												.getVendorProofDetails().stream()
												.filter(p -> p.getVendorChecks()
														.equals(byId.getVendorChecks().getVendorcheckId()))
												.collect(Collectors.toList());

										// Further filter the vendorAttirbuteValue within each filteredVendorProofs
										// element
										List<VendorUploadChecksDto> finalFilteredVendorProofs = filteredVendorProofs
												.stream().map(vendorUpload -> {
													// Filter the vendorAttirbuteValue list within each
													// VendorUploadChecksDto
													ArrayList<CheckAttributeAndValueDTO> filteredAttributes = vendorUpload
															.getVendorAttirbuteValue().stream()
															.filter(attr -> attr.getSourceName()
																	.equals(nameOfCheck))
															.collect(Collectors.toCollection(ArrayList::new));
													// Set the filtered attributes back into the
													// VendorUploadChecksDto
													vendorUpload.setVendorAttirbuteValue(filteredAttributes);
													return vendorUpload;
												}).collect(Collectors.toList());
	                                    
	                                    
	                                    System.out.println("filteredVendorProofs : "+filteredVendorProofs.toString());
	                                    System.out.println("name of the Check : "+nameOfCheck);
	                                    testConventionalCandidateReportDto.setVendorProofDetails(finalFilteredVendorProofs);
	                                    Map<String, LegalProceedingsDTO> filteredCriminalCheckList = candidateReportDTO.getCriminalCheckList()
	                                            .entrySet()
	                                            .stream()
	                                            .filter(entry -> "Criminal".contains(entry.getKey()))
	                                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//	                                    candidateReportDTO.
//	                                    filteredCriminalCheckList.keySet().forEach(System.out::println);
	                                    
	                                    if (nameOfCheck.equalsIgnoreCase("Criminal present")) {
											Map<String, LegalProceedingsDTO> modifiedMap = new HashMap<>();
											for (Map.Entry<String, LegalProceedingsDTO> entry : criminalCheckListMap
													.entrySet()) {
												// Get the key and value from the map entry
												String originalKey = entry.getKey();
												LegalProceedingsDTO value = entry.getValue();

												// Check if the key is "Criminal Permanent", if so, skip it
												if (!originalKey.equals("Criminal permanent")) {
													// Put the key and value into the new map
													modifiedMap.put(originalKey, value);
												}
											}
											testConventionalCandidateReportDto.setCriminalCheckList(modifiedMap);

										} else if (nameOfCheck.equalsIgnoreCase("Criminal permanent")) {
											Map<String, LegalProceedingsDTO> modifiedMap = new HashMap<>();
											for (Map.Entry<String, LegalProceedingsDTO> entry : criminalCheckListMap
													.entrySet()) {
												// Get the key and value from the map entry
												String originalKey = entry.getKey();
												LegalProceedingsDTO value = entry.getValue();

												// Check if the key is "Criminal Permanent", if so, skip it
												if (!originalKey.equals("Criminal present")) {
													// Put the key and value into the new map
													modifiedMap.put(originalKey, value);
												}
											}
											testConventionalCandidateReportDto.setCriminalCheckList(modifiedMap);

										} 
	                                    
//	                                    System.out.println("testConventionalCandidateReportDto.getCriminal : "+testConventionalCandidateReportDto.getCriminalCheckList().toString());

//	                                    testConventionalCandidateReportDto.setCriminalCheckList(criminalCheckListMap);
	                                    testConventionalCandidateReportDto.setDataList(candidateReportDTO.getDataList());
	                                    String tableHtmlStr = pdfService.parseThymeleafTemplate(templateName, testConventionalCandidateReportDto);
	                                    
	                                    pdfService.generatePdfFromHtml(tableHtmlStr, allcheckDynamicReport);
	                                    // Collect education proof and table
	                                    List<InputStream> educationProof = new ArrayList<>();
	                                    educationProof.add(FileUtil.convertToInputStream(allcheckDynamicReport));
	                                    educationProof.add(new FileInputStream(fileFromS3));
	                                    collect2.addAll(educationProof);
	                                    // Additional processing if needed
	                                }
	                            }
	                        } catch (IOException e) {
	                            log.error("Exception occurred: {}", e);
	                        }
							
							

						} catch (JsonProcessingException e) {
							// Handle the exception (e.g., log or throw)
							log.error("Exception 3 occured in generateDocument VendorProof method in ReportServiceImpl-->", e);
						}

					}
				}

				candidateReportDTO.setPdfByes(encodedImagesList);

	             // VendorProof Documnet Ends here
				
				//adding below section for Remittance images proof data
				List<RemittanceData> remittanceRecords = remittanceRepository.findAllByCandidateCandidateCode(candidateCode);
				List<RemittanceDataFromApiDto> dataDTOList = new ArrayList<>();
				for(RemittanceData remittanceData : remittanceRecords) {
					RemittanceDataFromApiDto remittanceDataFromApiDto=new RemittanceDataFromApiDto();
		            remittanceDataFromApiDto.setCandidateCode(candidateCode);
		            remittanceDataFromApiDto.setColor(remittanceData.getColor().getColorCode());
		            remittanceDataFromApiDto.setCompany(remittanceData.getCompany());
		            remittanceDataFromApiDto.setCreatedOn(remittanceData.getCreatedOn());
		            remittanceDataFromApiDto.setImage(Base64.getEncoder().encodeToString(remittanceData.getImage()));
		            remittanceDataFromApiDto.setMemberId(remittanceData.getMemberId());
		            remittanceDataFromApiDto.setName(remittanceData.getName());
		            remittanceDataFromApiDto.setYear(remittanceData.getYear());
		            remittanceDataFromApiDto.setRemark(remittanceData.getRemark());
		            
		            dataDTOList.add(remittanceDataFromApiDto);
				}
        if(dataDTOList!=null && !dataDTOList.isEmpty()) {
					//sorting remittance 
					List<RemittanceDataFromApiDto> sortedList = dataDTOList.stream()
			                .sorted(Comparator.comparing(dto -> {
			                    // Parsing the "year" string to LocalDate
			                    String yearString = dto.getYear(); // Assuming you have a getter method for year in your DTO
			                    return LocalDate.parse("01-" + yearString, DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH));
			                }))
			                .toList();
 
					candidateReportDTO.setRemittanceProofImagesData(sortedList!=null && !sortedList.isEmpty() ? sortedList : null);
					//end
				} else {
					candidateReportDTO.setRemittanceProofImagesData(null);
				}
				//end
				//adding gst images in report
				List<GstData> gstRecords = gstRepository.findAllByCandidateCandidateCode(candidateCode);
				List<GstDataFromApiDto> gstDataDTOList = new ArrayList<>();
				List<ExecutiveSummaryDto> gstExecutiveSummary = new ArrayList<>();
				for(GstData gstData : gstRecords) {
					GstDataFromApiDto gstDataFromApiDto= new GstDataFromApiDto();
					gstDataFromApiDto.setCandidateCode(candidate.getCandidateCode());
		            gstDataFromApiDto.setColor(gstData.getColor()!=null ? gstData.getColor().getColorCode() : "");
		            gstDataFromApiDto.setCompany(gstData.getCompany()!=null || gstData.getCompany().equals("")? gstData.getCompany() : "");
		            gstDataFromApiDto.setCreatedOn(gstData.getCreatedOn());
		            gstDataFromApiDto.setImage(Base64.getEncoder().encodeToString(gstData.getImage()));
		            gstDataFromApiDto.setPanNumber(gstData.getPanNumber());
		            gstDataFromApiDto.setGstNumber(gstData.getGstNumber());
		            gstDataFromApiDto.setStatus(gstData.getStatus()!=null || gstData.getStatus().equals("")? gstData.getStatus() : "");
		            
		            gstDataDTOList.add(gstDataFromApiDto);
				
				}
				//filtering unique gst records
						List<GstDataFromApiDto> gstDataUniqueDTOList = gstDataDTOList.stream()
		                .collect(Collectors.collectingAndThen(
		                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(GstDataFromApiDto::getGstNumber))),
		                        ArrayList::new));
						
				if(orgServices!=null && orgServices.contains("GST")) {
					updateEmploymentVerificationStatus(candidateReportDTO,overrideReportStatus);
					if(gstDataDTOList != null)
						updateGSTVerificationStatus(candidateReportDTO, gstDataDTOList);
				}
				
				candidateReportDTO.setGstImagesData(gstDataDTOList!=null && !gstDataDTOList.isEmpty() ? gstDataDTOList : null);
				candidateReportDTO.setGstImagesUniqueData(gstDataUniqueDTOList!=null && !gstDataUniqueDTOList.isEmpty() ? gstDataUniqueDTOList : null);

				//below condition added for the overriding the report verification status
				if(overrideReportStatus!=null && !overrideReportStatus.equals("") && !overrideReportStatus.equals("undefined") && !overrideReportStatus.equals("null") && !overrideReportStatus.isEmpty()
						&& orgServices!=null && !orgServices.contains("GST")) {
//				  log.info("REPORT IS HAVING OVERRIDED STATUS ::{}",overrideReportStatus);
				  updateCandidateOverridedVerificationStatus(candidateReportDTO,overrideReportStatus);
				}else {
				  updateCandidateVerificationStatus(candidateReportDTO);
				}
//				Date createdOn = candidate.getCreatedOn();

				// System.out.println("candidate Report dto : " +candidateReportDTO);
				
				// HYDRID CONVENTIONAL STARTS HERE	
				ArrayList<String> filePaths = new ArrayList<>();


//				String htmlStr = null;
//				String conventionalHtmlStr = null;
				if (reportType.toString().equalsIgnoreCase(FINAL) && candidateReportDTO.getProject().contains("Wipro")) {
					htmlStr = pdfService.parseThymeleafTemplate("wipro-final", candidateReportDTO);
				} else if(candidateReportDTO.getProject().contains("LTIMindtree")) {
					htmlStr = pdfService.parseThymeleafTemplate("template_LTIMT", candidateReportDTO);
					
				}  else if(candidateReportDTO.getProject().contains("CAPGEMINI TECHNOLOGY SERVICES INDIA LIMITED")
						|| orgServices.contains("PANTOUAN")) {
					htmlStr = pdfService.parseThymeleafTemplate("CG_UAN-Report", candidateReportDTO);
			    }else {
					htmlStr = pdfService.parseThymeleafTemplate("common-pdf", candidateReportDTO);
					
			    }

				pdfService.generatePdfFromHtml(htmlStr, report);
				
						
//				Vendor Uploaded Proof in PDF Format retrive from S3 End
				
//				List<Content> contentList = contentRepository.findAllByCandidateIdAndContentTypeIn(
//						candidate.getCandidateId(), Arrays.asList(ContentType.ISSUED, ContentType.AGENT_UPLOADED));
//
//				List<File> files = contentList.stream().map(content -> {
//					File uniqueTempFile = FileUtil.createUniqueTempFile(
//							candidateCode + "_issued_" + content.getContentId().toString(), ".pdf");
//					awsUtils.getFileFromS3(content.getBucketName(), content.getPath(), uniqueTempFile);
//					return uniqueTempFile;
//				}).collect(Collectors.toList());

//				List<String> vendorFilesURLs_paths = vendordocDtoList.stream().map(vendor -> {
//					byte[] data = vendor.getDocument();
//					String vendorFilesTemp = "Candidate/".concat(createdOn.toString())
//							.concat(candidateCode + "/Generated");
//					awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, vendorFilesTemp, data);
//					return vendorFilesTemp;
//				}).collect(Collectors.toList());
//			
				
//				List<String> vendorFilesURLs_paths = vendordocDtoList.stream().filter(vendor -> vendor.getDocument() != null && vendor.getDocument().length > 0).map(vendor -> {
//					byte[] data = vendor.getDocument();
//					SecureRandom rand = new SecureRandom();
//					long random12Digits = 100000000000L + (long) (rand.nextDouble() * 900000000000L);
//					System.out.println(random12Digits);
//					String vendorFilesTemp = "Candidate/".concat(new Date().toString()).concat(candidate.getCandidateId() + "/Generated" + random12Digits);
//					log.info("vendortemp file Path" + vendorFilesTemp);
//					String s = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, vendorFilesTemp, data);
//					return vendorFilesTemp;
//				}).collect(Collectors.toList());
//
//
//				List<File> vendorfiles = vendorFilesURLs_paths.stream().map(content -> {
//					File uniqueTempFile = FileUtil.createUniqueTempFile(content, ".pdf");
//					awsUtils.getFileFromS3(DIGIVERIFIER_DOC_BUCKET_NAME, content, uniqueTempFile);
//					return uniqueTempFile;
//				}).collect(Collectors.toList());
//			
				try {
					System.out.println("entry to generate try*************************");
					File mergedFile = FileUtil.createUniqueTempFile(candidateCode, ".pdf");
					List<InputStream> collect = new ArrayList<>();

					// added for footer logo fix(separate pdf for thymeleaf and uploaded document)
					List<InputStream> onlyForThymeleafToPdf = new ArrayList<>();
					File thymeleafContentPdfFile = FileUtil.createUniqueTempFile(candidateCode, ".pdf");
					onlyForThymeleafToPdf.add(FileUtil.convertToInputStream(report));
					PdfUtil.mergOnlyForThymeleafToPdf(onlyForThymeleafToPdf, new FileOutputStream(thymeleafContentPdfFile.getPath()));
					collect.add(FileUtil.convertToInputStream(thymeleafContentPdfFile));
					collect.addAll(collect2);
//					// end of merge 2 separate pdf
//					
					collect.addAll(files.stream().map(FileUtil::convertToInputStream).collect(Collectors.toList()));
//					collect.addAll(collect2);
//					collect.addAll(
//							vendorfiles.stream().map(FileUtil::convertToInputStream).collect(Collectors.toList()));

					
//					collect.addAll(files.stream().map(FileUtil::convertToInputStream).collect(Collectors.toList()));
					
					CandidateCaseDetails candidateCaseDetails = candidateCaseDetailsRepository
							.findByCandidateCandidateCode(candidateCode);
					
					
//					CandidateFileDto candidateFileDto = null;
//					File caseDetailsDocument = null;
//					File globalDatabaseDocument = null;
//
//					if (candidateCaseDetails != null) {
//						if (candidateCaseDetails.getCriminalVerificationisExist() != null) {
//							candidateFileDto = new CandidateFileDto(candidateCaseDetails.getCandidateCaseDetailsId(),
//									candidateCaseDetails.getCriminalVerificationDocument(),
//									candidateCaseDetails.getCriminalVerificationisExist().getColorName());
//							try {
//								caseDetailsDocument = FileUtil.createUniqueTempFile(
//										candidateCode + "_issued_" + candidateFileDto.getId().toString(), ".pdf");
//								FileUtils.writeByteArrayToFile(caseDetailsDocument, candidateFileDto.getDocument());
//								collect.add(FileUtil.convertToInputStream(caseDetailsDocument));
//							} catch (IOException e) {
//								System.out.println(e.getMessage());
//							}
//						}
//						if (candidateCaseDetails.getGlobalDatabaseCaseDetailsIsExist() != null) {
//							candidateFileDto = new CandidateFileDto(candidateCaseDetails.getCandidateCaseDetailsId(),
//									candidateCaseDetails.getGlobalDatabaseCaseDetailsDocument(),
//									candidateCaseDetails.getGlobalDatabaseCaseDetailsIsExist().getColorName());
//							try {
//								globalDatabaseDocument = FileUtil.createUniqueTempFile(
//										candidateCode + "_issued_" + candidateFileDto.getId().toString(), ".pdf");
//								FileUtils.writeByteArrayToFile(globalDatabaseDocument, candidateFileDto.getDocument());
//								collect.add(FileUtil.convertToInputStream(globalDatabaseDocument));
//							} catch (IOException e) {
//								System.out.println(e.getMessage());
//							}
//						}
//					}

					List<Content> uploadedDocContentList = new ArrayList<>();
					if(candidateCaseDetails != null && candidateCaseDetails.getCriminalDocContentId() != null) {
						Optional<Content> content = contentRepository.findByContentId(candidateCaseDetails.getCriminalDocContentId());
						if(content.isPresent()) {
							uploadedDocContentList.add(content.get());
						}
					}
					if(candidateCaseDetails != null && candidateCaseDetails.getGlobalDBDocContentId() != null) {
						Optional<Content> content = contentRepository.findByContentId(candidateCaseDetails.getGlobalDBDocContentId());
						if(content.isPresent()) {
							uploadedDocContentList.add(content.get());
						}
					}	
						

					List<File> uploadedDocuments = uploadedDocContentList.stream().map(content -> {
						File uniqueTempFile = FileUtil.createUniqueTempFile(
								candidateCode + "_issued_" + content.getContentId().toString(), ".pdf");
						awsUtils.getFileFromS3(content.getBucketName(), content.getPath(), uniqueTempFile);
						return uniqueTempFile;
					}).collect(Collectors.toList());
					
					collect.addAll(uploadedDocuments.stream().map(FileUtil::convertToInputStream).collect(Collectors.toList()));


			        
					ClassPathResource resource = new ClassPathResource("disclaimer.pdf");
					ClassPathResource resource2 = new ClassPathResource("KPMGBackCover.pdf");
					try (InputStream inputStream = resource.getInputStream()) {
						if (reportType.toString().equalsIgnoreCase(FINAL) && candidateReportDTO.getProject().contains("Wipro")) {
							List<InputStream> onlyReport = new ArrayList<>();
							onlyReport.add(FileUtil.convertToInputStream(report));

							onlyReport.add(resource.getInputStream());
							PdfUtil.mergePdfFiles(onlyReport, new FileOutputStream(mergedFile.getPath()));
						} else if (candidateReportDTO.getProject().contains("KPMG")) {
							List<InputStream> onlyReport = new ArrayList<>();
							onlyReport.add(FileUtil.convertToInputStream(report));

							onlyReport.add(resource2.getInputStream());
							PdfUtil.mergePdfFiles(onlyReport, new FileOutputStream(mergedFile.getPath()));
						} else {
							collect.add(resource.getInputStream());
							PdfUtil.mergePdfFiles(collect, new FileOutputStream(mergedFile.getPath()));
//							PdfUtil.mergePdfFiles(collect2, new FileOutputStream(mergedFile.getPath()));
							}
        			} catch (IOException e) {
						// TODO Auto-generated catch block
        				log.error("Exception 3 occured in generateDocument method in ReportServiceImpl-->", e);
					}
					
//					log.info("CHECKING SUBMITTED DATE ::{}",candidate.getSubmittedOn());
					if (reportType == ReportType.PRE_OFFER) {
						candidate.setSubmittedOn(new Date());
						candidateRepository.save(candidate);
					}

					String path = "Candidate/".concat(candidateCode + "/Generated".concat("/")
							.concat(candidate.getApplicantId() + "_" + candidate.getCandidateName() + "_" + reportType.name()).concat(".pdf"));
					//updatinng path for epfo and dnhdb company
					if(orgServices!=null && orgServices.contains("EPFO") && orgServices.contains("DNHDB")
				 			&& !orgServices.contains("DIGILOCKER") && !orgServices.contains("ITR")) {
						CandidateVerificationState updateVerificationStatus = candidateVerificationStateRepository.findByCandidateCandidateId(candidate.getCandidateId());
						
						String colorCode= updateVerificationStatus!=null &&
						                  updateVerificationStatus.getInterimColorCodeStatus() !=null ?
						                   updateVerificationStatus.getInterimColorCodeStatus().getColorCode() : reportType.name();
						path="Candidate/".concat(candidateCode + "/Generated".concat("/")
								.concat(candidate.getApplicantId() + "_" + candidate.getCandidateName() + "_" + colorCode).concat(".pdf"));
					}
					String pdfUrl = awsUtils.uploadFileAndGetPresignedUrl(DIGIVERIFIER_DOC_BUCKET_NAME, path,
							mergedFile);
					Content content = new Content();
					content.setCandidateId(candidate.getCandidateId());
					content.setContentCategory(ContentCategory.OTHERS);
					content.setContentSubCategory(ContentSubCategory.PRE_APPROVAL);
					// System.out.println(content+"*******************************************content");
					if (reportType.name().equalsIgnoreCase("PRE_OFFER")) {
						content.setContentSubCategory(ContentSubCategory.PRE_APPROVAL);
					} else if (reportType.name().equalsIgnoreCase("INTERIM")) {
						content.setContentSubCategory(ContentSubCategory.INTERIM);
					} else if (reportType.name().equalsIgnoreCase("FINAL")) {
						content.setContentSubCategory(ContentSubCategory.FINAL);
					}
					content.setFileType(FileType.PDF);
					content.setContentType(ContentType.GENERATED);
					content.setBucketName(DIGIVERIFIER_DOC_BUCKET_NAME);
					content.setPath(path);
					
					// block to delete old interim
					if (reportType.name().equalsIgnoreCase("INTERIM")) {
						List<Content> existingInterimList = contentRepository.findByCandidateIdAndContentSubCategory(candidate.getCandidateId(), ContentSubCategory.INTERIM);
						if(existingInterimList.size() > 0) {
							existingInterimList.forEach(temp -> {
								contentRepository.deleteById(temp.getContentId());
							});
						}
					}
					// end of delete old interim
					contentRepository.save(content);
					String reportTypeStr = reportType.label;
					Email email = new Email();
					email.setSender(emailProperties.getDigiverifierEmailSenderId());
					User agent = candidate.getCreatedBy();
					email.setReceiver(agent.getUserEmailId());
					//setting email send to ORG customer(Organization mailid.)
//                    log.info("SENDING GENERAL AND CC EMAIL TO ::{}",agent.getUserEmailId()+"   CC::{}"+candidate.getOrganization().getOrganizationEmailId());
                    email.setCopiedReceiver(candidate.getOrganization().getOrganizationEmailId()+","+agent.getUserEmailId());
                    //end
					email.setTitle("DigiVerifier " + reportTypeStr + " report - " + candidate.getCandidateName());

					String attachmentName=candidate.getApplicantId()+ "_"+candidate.getCandidateName()+ "_" + reportTypeStr + ".pdf";
					email.setAttachmentName(attachmentName);
					//email.setAttachmentName(candidateCode + " " + reportTypeStr + ".pdf");
					email.setAttachmentFile(mergedFile);

					email.setContent(String.format(emailContent, agent.getUserFirstName(), candidate.getCandidateName(),
							reportTypeStr));
					if (!reportType.name().equalsIgnoreCase("INTERIM")) {
					    emailSentTask.send(email);
					}
					//below condition for send interim report in mail to LTIM organization
					if (reportType.name().equalsIgnoreCase("INTERIM") && candidate.getOrganization().getOrganizationName().equalsIgnoreCase("LTIMindtree")) {
					    emailSentTask.send(email);
					}
					if(reportType.name().equalsIgnoreCase("PRE_OFFER")) {
					    emailSentTask.loa(candidateCode);
					    //posting the candidates to API clients
					    candidateService.postStatusToOrganization(candidateCode);
					}
					
					// delete files
					files.stream().forEach(file -> file.delete());
					mergedFile.delete();
					report.delete();
					
					candidateReportDTO.setCandidate_reportType(report_present_status);
					svcSearchResult.setData(candidateReportDTO);
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage(pdfUrl);
					svcSearchResult.setStatus(candidateReportDTO.getVerificationStatus()!=null?String.valueOf(candidateReportDTO.getVerificationStatus()):"");
					return svcSearchResult;
				} catch (MessagingException | IOException e) {
					log.error("Exception 4 occured in generateDocument method in ReportServiceImpl-->", e);
				}
				return svcSearchResult;
			} else {
				return svcSearchResult;
			}

		} else {
			System.out.println("enter else");
			throw new RuntimeException("unable to generate document for this candidate");
		}
	}
	
	@Override
	public ServiceOutcome<CandidateReportDTO> generateConventionalDocument(String candidateCode, String token,
			ReportType reportType, String overrideReportStatus, boolean conventionalReport) {
		
//		try {
			log.info("Calling conventional Generate Document");
			System.out.println("reportType :"+reportType.name());
//			System.out.println("enter to generate doc *******************************");
			entityManager.setFlushMode(FlushModeType.COMMIT);
			ServiceOutcome<CandidateReportDTO> svcSearchResult = new ServiceOutcome<CandidateReportDTO>();
			Candidate candidate = candidateService.findCandidateByCandidateCode(candidateCode);
			CandidateAddComments candidateAddComments = candidateAddCommentRepository
					.findByCandidateCandidateId(candidate.getCandidateId());
//			System.out.println(candidate.getCandidateId() + "*******************************"
//					+ validateCandidateStatus(candidate.getCandidateId()));
			ConventionalCandidateStatus conventionalCandidateStatus = conventionalCandidateStatusRepository.findByCandidateCandidateCode(candidateCode);
			System.out.println("conventionalCandidateStatus.getStatusMaster().getStatusMasterId() : "+conventionalCandidateStatus.getStatusMaster().getStatusMasterId());
			Integer report_status_id = 0;
			String report_present_status = "";
			Boolean generatePdfFlag = true;
			if (reportType.equals(ReportType.PRE_OFFER)) {
				report_status_id = 7;
			} else if (reportType.equals(ReportType.FINAL)) {
				report_status_id = 8;
			} else if (reportType.equals(ReportType.CONVENTIONALINTERIM)) {
				System.out.println("CONVENTIONALINTERIM > ");
				report_status_id = 25;
			}
			if (Integer.valueOf(String.valueOf(conventionalCandidateStatus.getStatusMaster().getStatusMasterId())) == 7) {
				report_present_status = "QC Pending";
			} else if (Integer.valueOf(String.valueOf(conventionalCandidateStatus.getStatusMaster().getStatusMasterId())) == 8) {
				report_present_status = String.valueOf(ReportType.FINAL);
			} else if (Integer.valueOf(String.valueOf(conventionalCandidateStatus.getStatusMaster().getStatusMasterId())) == 25) {
				report_present_status = String.valueOf(ReportType.CONVENTIONALINTERIM);
			}
			if (conventionalCandidateStatus.getStatusMaster().getStatusMasterId() != null) {
				if (report_status_id == Integer
						.valueOf(String.valueOf(conventionalCandidateStatus.getStatusMaster().getStatusMasterId()))) {
					generatePdfFlag = true;
//					log.info("entered line no 870 {}", report_status_id);
				} else {
					generatePdfFlag = false;
					CandidateReportDTO candidateDTOobj = null;
					candidateDTOobj = new CandidateReportDTO();
					candidateDTOobj
							.setCandidate_reportType("candidate in " + report_present_status + " Not in " + reportType);
					svcSearchResult.setData(candidateDTOobj);
					svcSearchResult.setOutcome(false);
					svcSearchResult.setMessage(null);
					svcSearchResult.setStatus(report_present_status);
				}
			}

			if (reportType == ReportType.PRE_OFFER) {
				Optional<Content> contentList = contentRepository
						.findByCandidateIdAndContentTypeAndContentCategoryAndContentSubCategory(candidate.getCandidateId(),
								ContentType.GENERATED, ContentCategory.OTHERS, ContentSubCategory.PRE_APPROVAL);
				if (contentList.isPresent()) {
					generatePdfFlag = false;
					CandidateReportDTO candidateDTOobj = null;
					candidateDTOobj = new CandidateReportDTO();
					candidateDTOobj
							.setCandidate_reportType("candidate in " + report_present_status + " Not in " + reportType);
					svcSearchResult.setData(candidateDTOobj);
					svcSearchResult.setOutcome(false);
					svcSearchResult.setMessage(null);
					svcSearchResult.setStatus(report_present_status);
				}
			}

//			log.info("entered line no 883 {}", report_status_id);
			if (conventionalReport) {
				if (generatePdfFlag) {

//					System.out.println("enter if *******************************");
					List<VendorUploadChecksDto> vendordocDtoList = new ArrayList<VendorUploadChecksDto>();
					VendorUploadChecksDto vendorUploadChecksDto = null;

					List<String> orgServices = serviceTypeConfigRepository.getServiceSourceMasterByOrgId(candidate.getOrganization().getOrganizationId());

					// Added For KPMG
					final boolean isKPMG = candidate.getOrganization().getOrganizationName().equalsIgnoreCase("KPMG");
					final boolean[] isdigilocker = {!isKPMG};
					
//					ServiceOutcome<List<String>> configCodes = candidateService.getServiceConfigCodes(candidateCode, null);
//					if(Boolean.TRUE.equals(configCodes.getOutcome()) && !configCodes.getData().contains("DIGILOCKER")) {
//						isdigilocker[0] =false;
//					}
					
					// candidate Basic detail
					CandidateReportDTO candidateReportDTO = new CandidateReportDTO();
					candidateReportDTO.setOrgServices(orgServices);

					candidateReportDTO.setName(candidate.getCandidateName());
					candidateReportDTO.setApplicantId(candidate.getApplicantId());
					//candidateReportDTO.setDob(candidate.getDateOfBirth());
					candidateReportDTO.setDob(candidate.getPanDob());
					candidateReportDTO.setContactNo(candidate.getContactNumber());
					candidateReportDTO.setEmailId(candidate.getEmailId());
//					if(isdigilocker!=null && isdigilocker[0]) {
//						candidateReportDTO.setExperience(candidate.getIsFresher() ? "Fresher" : "Experience");
//					}
					candidateReportDTO.setReportType(reportType);
					// ADDED TO DTO
					candidateReportDTO.setCandidateId(candidate.getCandidateId());
					Organization organization = candidate.getOrganization();
					candidateReportDTO.setOrganizationName(organization.getOrganizationName());
					candidateReportDTO.setProject(organization.getOrganizationName());
//					candidateReportDTO.setOrganizationLocation(organization.getOrganizationLocation());
					candidateReportDTO.setOrganizationLocation(organization.getBillingAddress());
//					candidateReportDTO.setOrganizationLogo(organization.getLogoUrl());
					try {
						// Create a temporary file to store the Blob data
						
						if(organization.getOrganizationLogo() != null) {
							File tempDir = new File(System.getProperty("java.io.tmpdir"));
							File tempF = File.createTempFile("data", ".dat", tempDir);
							Path tempFile = tempF.toPath();

							// Write the Blob data to the temporary file
							Files.copy(new ByteArrayInputStream(organization.getOrganizationLogo()), tempFile,
									StandardCopyOption.REPLACE_EXISTING);

							// Create a URL for the temporary file
							URL url = tempFile.toUri().toURL();

							candidateReportDTO.setOrganizationLogo(url.toString());
						}
						
					} catch (IOException e) {
						log.error("Exception occured in generateDocument method in ReportServiceImpl-->", e);
					}
					candidateReportDTO.setAccountName(candidate.getAccountName()!=null ? candidate.getAccountName(): null);
					if (candidateAddComments != null) {
						candidateReportDTO.setComments(candidateAddComments.getComments());
					}

					ConventionalCandidateVerificationState candidateVerificationState = conventionalCandidateService
							.getConventionalCandidateVerificationStateByCandidateId(candidate.getCandidateId());
					boolean hasCandidateVerificationStateChanged = false;
					if (Objects.isNull(candidateVerificationState)) {
						candidateVerificationState = new ConventionalCandidateVerificationState();
						candidateVerificationState.setCandidate(candidate);
						final ZoneId id = ZoneId.systemDefault();
						candidateVerificationState
								.setCaseInitiationTime(ZonedDateTime.ofInstant(candidate.getCreatedOn().toInstant(), id));

					}
					switch (reportType) {
					case PRE_OFFER:
						candidateVerificationState.setPreApprovalTime(ZonedDateTime.now());
						break;
					case FINAL:
						candidateVerificationState.setFinalReportTime(ZonedDateTime.now());
						break;
					case CONVENTIONALINTERIM:
//						if(orgServices!=null && orgServices.contains("EPFO") && orgServices.contains("DNHDB")
//			 			    && !orgServices.contains("DIGILOCKER") && !orgServices.contains("ITR")) {
//							//below condition for storing re initiation case information
//							candidateService.saveCaseReinitDetails(candidateCode,null);
//							candidateVerificationState.setInterimReportTime(candidateVerificationState!=null  && candidateVerificationState.getInterimReportTime()!=null? 
//											candidateVerificationState.getInterimReportTime():ZonedDateTime.now());
//						}else {
							candidateVerificationState.setInterimReportTime(ZonedDateTime.now());
//						}
						
						break;

					}
					candidateVerificationState = conventionalCandidateService.addOrUpdateConventionalCandidateVerificationStateByCandidateId(
							candidate.getCandidateId(), candidateVerificationState);
					candidateReportDTO.setFinalReportDate(DateUtil.convertToString(ZonedDateTime.now()));
					candidateReportDTO.setInterimReportDate(
							DateUtil.convertToString(candidateVerificationState.getInterimReportTime()));
					
					candidateReportDTO.setCaseReinitDate(candidateVerificationState.getCaseReInitiationTime()!=null ?
							DateUtil.convertToString(candidateVerificationState.getCaseReInitiationTime()) : null);
					candidateReportDTO.setInterimAmendedDate(candidateVerificationState.getInterimReportAmendedTime()!=null ?
							DateUtil.convertToString(candidateVerificationState.getInterimReportAmendedTime()) : null);

					
					
					// Specify the IST time zone (ZoneId.of("Asia/Kolkata"))
			        ZoneId istZone = ZoneId.of("Asia/Kolkata");

			        candidateReportDTO.setPreOfferReportDate(candidateVerificationState.getPreApprovalTime()!=null?
							DateUtil.convertToString(candidateVerificationState.getPreApprovalTime().withZoneSameInstant(istZone)): null);
			        // Convert the ZonedDateTime from GMT to IST
			        candidateReportDTO.setConventionalCWFCompletedDate(candidate.getSubmittedOn() != null ? 
			        	    DateUtil.convertToString(candidate.getSubmittedOn().toInstant().atZone(ZoneId.systemDefault())) : 
			        	    null);
			        ZonedDateTime istZonedDateTime = candidateVerificationState.getCaseInitiationTime().withZoneSameInstant(istZone);
			        candidateReportDTO.setCaseInitiationDate(
							DateUtil.convertToString(istZonedDateTime));
//					candidateReportDTO.setCaseInitiationDate(
//							DateUtil.convertToString(candidateVerificationState.getCaseInitiationTime()));
					// executive summary
					Long organizationId = organization.getOrganizationId();
					List<OrganizationExecutive> organizationExecutiveByOrganizationId = organizationService
							.getOrganizationExecutiveByOrganizationId(organizationId);
					
	                List<Map<String, List<Map<String, String>>>> dataList = new ArrayList<>();

					List<VendorChecks> vendorList = vendorChecksRepository
							.findAllByCandidateCandidateId(candidate.getCandidateId());
					ArrayList<String> attributeValuesList = new ArrayList<>();
					ArrayList<CheckAttributeAndValueDTO> individualValuesList = new ArrayList<>();
					HashMap<String, LegalProceedingsDTO> criminalCheckListMap = new HashMap<>();
					String checkType = null;
					for (VendorChecks vendorChecks : vendorList) {
						if (vendorChecks != null &&
	                            vendorChecks.getVendorCheckStatusMaster() != null &&
	                            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode() != null &&
	                            (vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("CLEAR") ||
	                                    vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("MINORDISCREPANCY") ||
	                                    vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("QCPENDING") ||
	                                    vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("UNABLETOVERIFY") ||
	                                    vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("MAJORDISCREPANCY"))) {
							
						User user = userRepository.findByUserId(vendorChecks.getVendorId());
						VendorUploadChecks vendorChecksss = vendorUploadChecksRepository
								.findByVendorChecksVendorcheckId(vendorChecks.getVendorcheckId());
						if (vendorChecksss != null) {
							CheckAttributeAndValueDTO checkAttributeAndValueDto = new CheckAttributeAndValueDTO();
							vendorUploadChecksDto = new VendorUploadChecksDto(user.getUserFirstName(),
									vendorChecksss.getVendorChecks().getVendorcheckId(),
									vendorChecksss.getVendorUploadedDocument(), vendorChecksss.getDocumentname(),
									vendorChecksss.getAgentColor().getColorName(),
									vendorChecksss.getAgentColor().getColorHexCode(), null,null,vendorChecksss.getCreatedOn(),null,null,null,null,null);

							ArrayList<String> combinedList = new ArrayList<>(vendorChecks.getAgentAttirbuteValue());
							combinedList.addAll(vendorChecksss.getVendorAttirbuteValue());

//							checkAttributeAndValueDto.setSourceName(vendorChecks.getSource().getSourceName());
//							checkAttributeAndValueDto.setAttributeAndValue(combinedList);
//							individualValuesList.add(checkAttributeAndValueDto);
//							vendorUploadChecksDto.setVendorAttirbuteValue(individualValuesList);
							
							if (vendorChecks.getSource().getSourceName().equalsIgnoreCase("GLOBAL DATABASE CHECK") || vendorChecks.getSource().getSourceName().toLowerCase().contains("database")) {
	                            ObjectMapper objectMapper = new ObjectMapper();
	                            for (String jsonData : vendorChecksss.getVendorAttirbuteValue()) {
//	                                Map<String, List<Map<String, String>>> dataMap = null;
	                                try {
	                                    Map<String, List<Map<String, String>>> dataMap = objectMapper.readValue(jsonData, new TypeReference<Map<String, List<Map<String, String>>>>() {});
	                                    System.out.println(dataMap);
	                                    dataList.add(dataMap);
	                                } catch (Exception e) {
	                                    log.warn("An error occurred while parsing JSON data: {} " + e.getMessage());
	                                    // Handle the exception here, you can log it, display a user-friendly message, etc.
	                                }
	                            }
//	                            vendorAttributeDto.setSourceName(conventionalVendorliChecksToPerform.getCheckName());
//	                            vendorAttributeDtos.add(vendorAttributeDto);
	                        }
							if(vendorChecks.getSource().getSourceName().equalsIgnoreCase("Criminal")) {
								 LegalProceedingsDTO legalProceedingsDTO = new LegalProceedingsDTO();

		                            List<CriminalCheck> civilproceding = criminalCheckRepository.findByVendorCheckIdAndProceedingsType(vendorChecks.getVendorcheckId(), "CIVILPROCEDING");
		                            if (civilproceding.isEmpty() == false) {
		                                legalProceedingsDTO.setCivilProceedingList(civilproceding);
		                            }
		                            List<CriminalCheck> criminalproceding = criminalCheckRepository.findByVendorCheckIdAndProceedingsType(vendorChecks.getVendorcheckId(), "CRIMINALPROCEDING");
		                            if (criminalproceding.isEmpty() == false) {
		                                legalProceedingsDTO.setCriminalProceedingList(criminalproceding);
		                            }
		                            if(civilproceding.isEmpty() && criminalproceding.isEmpty()) {
		                            	System.out.println(" CRiminal IS EMPLTY >>"+vendorChecks.getVendorcheckId());
		                            	criminalCheckListMap.put(String.valueOf(vendorChecks.getCheckType()),null);
		                            	System.out.println("criminalCheckListMap>>>"+criminalCheckListMap.toString());
		                            }
		                            else {
		                            	 criminalCheckListMap.put(String.valueOf(vendorChecks.getCheckType()), legalProceedingsDTO);
		 	                            log.info("criminal check data" + criminalCheckListMap);
		                            }
		                           
							}
							
							 String employerName = extractValue(combinedList, "Employers Name");
						        
						        String qualification = extractValue(combinedList,"Qualification attained");
						        
						        String address = extractAddress(combinedList);
						        
						        String idItems = null;
						        
						        if(vendorChecks.getSource().getSourceName().equalsIgnoreCase("Employment")) {
						        	checkAttributeAndValueDto.setCheckDetails(employerName);
						        }else if(vendorChecks.getSource().getSourceName().equalsIgnoreCase("Education")) {
						        	if(qualification != null) {	
							        	checkAttributeAndValueDto.setCheckDetails(qualification);
									}else {
										String prodaptQualification = extractValue(combinedList,"Complete Name of Qualification/ Degree Attained");
							        	checkAttributeAndValueDto.setCheckDetails(prodaptQualification);
									}
//						        	checkAttributeAndValueDto.setCheckDetails(qualification);
						        }else if(vendorChecks.getSource().getSourceName().equalsIgnoreCase("Address") ||
						        		vendorChecks.getSource().getSourceName().equalsIgnoreCase("Criminal") ||
						        		vendorChecks.getSource().getSourceName().equalsIgnoreCase("Global Database check")) {
						        	checkAttributeAndValueDto.setCheckDetails(address);
						        }
						        else if(vendorChecks.getSource().getSourceName().equalsIgnoreCase("ID Items")) {
									VendorUploadChecks byVendorChecksVendorcheckId = vendorUploadChecksRepository.findByVendorChecksVendorcheckId(vendorChecks.getVendorcheckId());
									if(byVendorChecksVendorcheckId != null) {
										String vendorAttributeValue = byVendorChecksVendorcheckId.toString();
								        idItems = extractValue(byVendorChecksVendorcheckId.getVendorAttirbuteValue(), "proofName");
							        	checkAttributeAndValueDto.setCheckDetails(idItems);

									}
						        }

//						        checkAttributeAndValueDto.setSourceName(vendorChecks.getCheckType()!= null
//						        		&& vendorChecks.getCheckType().contains("Drug")?"Drug Test"
//						        				:vendorChecks.getCheckType().contains("Global")?"Global Database Check"
//						        						:vendorChecks.getCheckType());
						        
						        checkAttributeAndValueDto.setSourceName(
						        	    vendorChecks.getCheckType() != null && vendorChecks.getCheckType().contains("Drug")
						        	        ? "Drug Test"
						        	        : vendorChecks.getCheckType() != null && vendorChecks.getCheckType().contains("Global")
						        	            ? "Global Database Check"
						        	            : vendorChecks.getCheckType()
						        	);
						        
						        checkAttributeAndValueDto.setCheckStatus(vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode());
						        String remarks = extractValue(combinedList, "remarks");

							checkAttributeAndValueDto.setCheckRemarks(remarks);
							checkAttributeAndValueDto.setAttributeAndValue(combinedList);				
							individualValuesList.add(checkAttributeAndValueDto);
							vendorUploadChecksDto.setVendorAttirbuteValue(individualValuesList);
							vendorUploadChecksDto.setCheckStatus(vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode());


							vendordocDtoList.add(vendorUploadChecksDto);
						}
					}
					}

					candidateReportDTO.setVendorProofDetails(vendordocDtoList);
					candidateReportDTO.setExcetiveSummaryDto(vendordocDtoList);
					candidateReportDTO.setDataList(dataList);
	                candidateReportDTO.setCriminalCheckList(criminalCheckListMap);

					candidateReportDTO.setOrganisationScope(
							organisationScopeRepository.findByCandidateId(candidate.getCandidateId()));

//					List<Long> vendorCheckIds = vendorList.stream()
//							.map(VendorChecks::getVendorcheckId)
//							.collect(Collectors.toList());
					
					List<Long> vendorCheckIds = vendorList.stream()
						    .filter(vendorChecks ->
						        vendorChecks.getVendorCheckStatusMaster() != null &&
						        vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode() != null &&
						        (
						            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("CLEAR") ||
						            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("MINORDISCREPANCY") ||
						            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("QCPENDING") ||
						            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("UNABLETOVERIFY") ||
						            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("MAJORDISCREPANCY")
						        )
						    )
						    .map(VendorChecks::getVendorcheckId)
						    .collect(Collectors.toList());

//					List<String> checkName = vendorList.stream()
//							.map(vendorChecks -> vendorChecks.getSource().getSourceName())
//							.collect(Collectors.toList());
					
					List<String> checkName = vendorList.stream()
						    .filter(vendorChecks ->
						        vendorChecks.getVendorCheckStatusMaster() != null &&
						        vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode() != null &&
						        (
						            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("CLEAR") ||
						            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("MINORDISCREPANCY") ||
						            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("QCPENDING") ||
						            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("UNABLETOVERIFY") ||
						            vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode().equals("MAJORDISCREPANCY")
						        )
						    )
						    .map(vendorChecks -> vendorChecks.getCheckType() != null ? vendorChecks.getCheckType().trim() : vendorChecks.getSource().getSourceName())
						    .collect(Collectors.toList());

					// VendorProof Document Starts Here
					List<VendorUploadChecks> result = vendorUploadChecksRepository.findByVendorChecksVendorcheckIds(vendorCheckIds);
					List<Map<String, List<String>>> encodedImagesList = new ArrayList<>();
					
					 List<InputStream> collect = new ArrayList<>();
		                File report = FileUtil.createUniqueTempFile("report", ".pdf");
		                String conventionalHtmlStr = null;
		                Date createdOn = candidate.getCreatedOn();
		                String htmlStr = pdfService.parseThymeleafTemplate("ConventionalReport_pdf", candidateReportDTO);
		                pdfService.generatePdfFromHtml(htmlStr, report);

//		                List<Content> contentList = contentRepository.findAllByCandidateIdAndContentTypeIn(candidate.getCandidateId(), Arrays.asList(ContentType.ISSUED, ContentType.AGENT_UPLOADED));
//		                List<File> files = contentList.stream().map(content -> {
//		                    File uniqueTempFile = FileUtil.createUniqueTempFile(candidate.getCandidateId() + "_issued_" + content.getContentId().toString(), ".pdf");
//		                    awsUtils.getFileFromS3(content.getBucketName(), content.getPath(), uniqueTempFile);
//		                    return uniqueTempFile;
//		                }).collect(Collectors.toList());
		                File mergedFile = FileUtil.createUniqueTempFile(String.valueOf(candidate.getCandidateId()), ".pdf");

		                collect.add(FileUtil.convertToInputStream(report));
//		                collect.addAll(files.stream().map(FileUtil::convertToInputStream).collect(Collectors.toList()));


					for (VendorUploadChecks vendorUploadCheck : result) {
						VendorUploadChecks byId = vendorUploadChecksRepository
								.findByVendorChecksVendorcheckId(vendorUploadCheck.getVendorChecks().getVendorcheckId());
//						byId.getVendorChecks().getCheckType();
//	                    Optional<Source> byId = sourceRepository.findById(vendorUploadCheck.getVendorChecks().getSource().getSourceId());
	                    
	                    

						Map<String, List<String>> encodedImageMap = new HashMap<>();

						Long checkId = vendorUploadCheck.getVendorChecks().getVendorcheckId();
						String sourceName = vendorUploadCheck.getVendorChecks().getSource().getSourceName();
						log.info("Vendor sourceName ===== {}"+ sourceName);

						log.info("Size of checkName: {}", checkName.size());
						log.info(" ReportServiceImpl CandidateId : "+vendorUploadCheck.getVendorChecks().getCandidate().getCandidateId()+" | Unique CheckID In upload Vendor proof : "+checkId+" | CheckStatus : "+vendorUploadCheck.getVendorChecks().getVendorCheckStatusMaster().getCheckStatusName());


						if (checkName != null && !checkName.isEmpty()) {

							String nameOfCheck = checkName.isEmpty() ? null : checkName.get(encodedImagesList.size() % checkName.size());
//							log.info("VENDOR CHECK ID ====== {}"+checkId);
//							log.info("Vendor nameOfCheck ===== {}"+ nameOfCheck);
	//
//							log.info("Vendor Upload Documents ====== {}" + vendorUploadCheck.getVendorUploadedDocument());

							byte[] documentBytes = vendorUploadCheck.getVendorUploadedDocument();

							ObjectMapper objectMapper = new ObjectMapper();
							String vendorUploadedImages = vendorUploadCheck.getVendorUploadedImage();
							String documentPresicedUrl = null;
							
							Byte[] vendorUploadedImagesByte = null;
							String jsonString = null;
//	                      log.info("Vendor Upload Documents ====== {}" + vendorUploadCheck.getVendorUploadedDocument());
	                      if (vendorUploadCheck.getVendorUploadDocumentPathKey() != null ||vendorUploadCheck.getVendorUploadDocumentPathKey()!=null) {
	                          log.info("inside the aws path key retival for the check    --"+sourceName);
	                          try {
								documentBytes = awsUtils.getbyteArrayFromS3(DIGIVERIFIER_DOC_BUCKET_NAME, vendorUploadCheck.getVendorUploadDocumentPathKey());
								ObjectMetadata metadataDocumentContentType = new ObjectMetadata();
			                    metadataDocumentContentType.setContentType(".pdf");
//			                    String documentPresicedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, vendorUploadCheck.getVendorUploadDocumentPathKey(),documentBytes, metadataDocumentContentType);
			                 // Check if the document is not PDF
			                    if (!isPDF(documentBytes)) {
			                        String base64EncodedDocument = Base64.getEncoder().encodeToString(documentBytes);
			                        documentPresicedUrl = base64EncodedDocument;
//			                        System.out.println("Base64 Encoded Document: " + base64EncodedDocument);
			                    } else {
			                        // Use the original document
			                        // For example:
			                    	documentPresicedUrl = vendorUploadCheck.getVendorUploadDocumentPathKey();
			                        log.info("Original Document URL: " + documentPresicedUrl);
			                    }

								vendorUploadedImages = new String(documentPresicedUrl);
//								
								// Convert list to JSONArray
								JSONArray jsonArray = new JSONArray();
//								for (String base64 : imageBase64List) {
								    JSONObject jsonObject = new JSONObject();
								    JSONArray imageArray = new JSONArray();
								    imageArray.put(vendorUploadedImages);
								    jsonObject.put("image", imageArray);
								    jsonArray.put(jsonObject);
//								}
								// Convert the JSON array to a string
								 jsonString = jsonArray.toString();
								
							} catch (IOException e) {
								log.info("Exception in DIGIVERIFIER_DOC_BUCKET_NAME {}"+e);
								}

	                      }
							try {
								if (vendorUploadedImages != null) {
									List<Map<String, List<String>>> decodedImageList = objectMapper.readValue(jsonString, new TypeReference
											<List<Map<String, List<String>>>>() {});

									List<String> allEncodedImages = decodedImageList.stream()
											.flatMap(imageMap -> imageMap.values().stream())
											.flatMap(List::stream)
											.collect(Collectors.toList());

									// Loop through each image byte array and encode it to Base64
									List<String> encodedImagesForDocument = new ArrayList<>();

									log.info("encodedImagesForDocument::::: {}"+encodedImagesForDocument.size());
									
									encodedImageMap.put(nameOfCheck, allEncodedImages);
//									 for (Map.Entry<String, List<String>> entry : encodedImageMap.entrySet()) {
//								            String checkName1 = entry.getKey();
//								            System.out.println("checkName1>>>"+checkName1);
//								            List<String> s3Urls = entry.getValue();
	////
////								            // Check if the sourceName matches the desired criteria
//								            if (checkName1.equals("Education UG")) {
//								                // Attach each PDF (S3 URL) to the existing PDF document
//								                for (String s3Url : s3Urls) {
//								                    attachPDF(s3Url);
//								                }
//								            }
//								        }
								}
								//	           	    	else if(documentBytes != null) {
								//	           	    		List<byte[]> imageBytes = convertPDFToImage(documentBytes);
								//	           	    		List<String> encodedImagesForDocument = new ArrayList<>();
								//	           	    		for (int j = 0; j < imageBytes.size(); j++) {
								//	           	    			byte[] imageBytess = imageBytes.get(j);
								//	           	    			String encodedImage = Base64.getEncoder().encodeToString(imageBytess); 
								//	           	    			encodedImagesForDocument.add(encodedImage);
								//
								//	           	    		}
								//	           	    		encodedImageMap.put(nameOfCheck, encodedImagesForDocument);
								//
								//	           	    	}
								else {
									log.info("Vendor uploaded document is null {}");
									encodedImageMap.put(nameOfCheck, null);

								}

								encodedImagesList.add(encodedImageMap);
								
		                        try {
		                        	CandidateReportDTO testConventionalCandidateReportDto = null;
		                            if (vendorUploadedImages != null) {
		                                if (isBase64Encoded(documentPresicedUrl)) {
		                                    log.info("BASE64 IMG for " + nameOfCheck + " entry");
		                                    List<Map<String, List<String>>> dynamicEncodedImagesList = new ArrayList<>();
		                                    // Generate table for this education entry
		                                    File allcheckDynamicReport = FileUtil.createUniqueTempFile(byId.getVendorChecks().getCheckType(), ".pdf");
		                                    String templateName;
		                                    if (nameOfCheck.toLowerCase().contains("education".toLowerCase())) {
		                                    	System.out.println("this is true : ");
		                                        templateName = "Conventional/EducationCheck-pdf";
		                                    }
		                                    else if (nameOfCheck.toLowerCase().contains("employment".toLowerCase())) {
		                                        templateName = "Conventional/EmploymentCheck-pdf";
		                                    } else if (nameOfCheck.toLowerCase().contains("criminal".toLowerCase())) {
		                                    	System.out.println("This is for criminal");
		                                        templateName = "Conventional/CriminalCheck-pdf";
		                                    } else if (nameOfCheck.toLowerCase().contains("global".toLowerCase())) {
		                                        templateName = "Conventional/GlobalCheck-pdf";
		                                    } else if (nameOfCheck.toLowerCase().contains("id".toLowerCase())) {
		                                        templateName = "Conventional/IDItemsCheck-pdf";
		                                    } else if (nameOfCheck.contains("LEGAL")) {
		                                        templateName = "Conventional/LegalRigntCheck";
		                                    } else if (nameOfCheck.contains("OFAC")) {
		                                        templateName = "Conventional/OfacCheck";
		                                    } else if (nameOfCheck.toLowerCase().contains("address".toLowerCase())) {
		                                        templateName = "Conventional/AddressCheck-pdf";
		                                    } else {
		                                        templateName = "Conventional/NoTableChecks";
		                                    }
		                                    testConventionalCandidateReportDto = new CandidateReportDTO();
		                                    
//		                                    List<VendorUploadChecksDto> filteredVendorProofs = candidateReportDTO.getVendorProofDetails().stream().filter(p -> p.getVendorChecks().equalsIgnoreCase(String.valueOf(byId.get().getSourceId())) == true).collect(Collectors.toList());
//		                                    List<VendorUploadChecksDto> filteredVendorProofs = candidateReportDTO.getVendorProofDetails().stream()
//		                                    	    .filter(p -> String.valueOf(p.getVendorChecks()).equalsIgnoreCase(String.valueOf(byId.getVendorChecks().getCheckType())))
//		                                    	    .collect(Collectors.toList());
		                                    
											List<VendorUploadChecksDto> filteredVendorProofs = candidateReportDTO
													.getVendorProofDetails().stream()
													.filter(p -> p.getVendorChecks()
															.equals(byId.getVendorChecks().getVendorcheckId()))
													.collect(Collectors.toList());

											System.out.println("nameOfCheck ::: :" + nameOfCheck);

											// Further filter the vendorAttirbuteValue within each filteredVendorProofs
											// element
//											List<VendorUploadChecksDto> finalFilteredVendorProofs = filteredVendorProofs
//													.stream().map(vendorUpload -> {
//														// Filter the vendorAttirbuteValue list within each
//														// VendorUploadChecksDto
//														ArrayList<CheckAttributeAndValueDTO> filteredAttributes = vendorUpload
//																.getVendorAttirbuteValue().stream()
//																.filter(attr -> attr.getSourceName()
//																		.equals(nameOfCheck))
//																.collect(Collectors.toCollection(ArrayList::new));
//														// Set the filtered attributes back into the
//														// VendorUploadChecksDto
//														vendorUpload.setVendorAttirbuteValue(filteredAttributes);
//														return vendorUpload;
//													}).collect(Collectors.toList());
											
											List<VendorUploadChecksDto> finalFilteredVendorProofs = filteredVendorProofs
												    .stream()
												    .map(vendorUpload -> {
												        // Print vendorUpload details
//												        System.out.println("Processing VendorUploadChecksDto ID: " + vendorUpload.getId());
												        System.out.println("Original Attributes: " + vendorUpload.getVendorAttirbuteValue());

												        // Filter the vendorAttirbuteValue list within each VendorUploadChecksDto
												        ArrayList<CheckAttributeAndValueDTO> filteredAttributes = vendorUpload
												            .getVendorAttirbuteValue()
												            .stream()
												            .filter(attr -> {
												                String trimmedSourceName = attr.getSourceName().trim();
												                String trimmedNameOfCheck = nameOfCheck.trim();
												                System.out.println("Checking attribute with sourceName: " + trimmedSourceName);
												                return trimmedSourceName.equalsIgnoreCase(trimmedNameOfCheck);
												            })
												            .collect(Collectors.toCollection(ArrayList::new));

												        // Set the filtered attributes back into the VendorUploadChecksDto
												        vendorUpload.setVendorAttirbuteValue(filteredAttributes);

												        // Print filtered attributes
												        System.out.println("Filtered Attributes: " + vendorUpload.getVendorAttirbuteValue());

												        return vendorUpload;
												    })
												    .collect(Collectors.toList());
		                                    
//		                                    testConventionalCandidateReportDto.setVendorProofDetails(filteredVendorProofs);
		                                    testConventionalCandidateReportDto.setVendorProofDetails(finalFilteredVendorProofs);
                                   
		                                    Map<String, LegalProceedingsDTO> filteredCriminalCheckList = candidateReportDTO.getCriminalCheckList()
		                                            .entrySet()
		                                            .stream()
		                                            .filter(entry -> "Criminal".contains(entry.getKey()))
		                                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		                                    
//		                                    testConventionalCandidateReportDto.setCriminalCheckList(filteredCriminalCheckList);
		                                    
											if (nameOfCheck.equals("Criminal present")) {
												Map<String, LegalProceedingsDTO> modifiedMap = new HashMap<>();
												for (Map.Entry<String, LegalProceedingsDTO> entry : criminalCheckListMap
														.entrySet()) {
													// Get the key and value from the map entry
													String originalKey = entry.getKey();
													LegalProceedingsDTO value = entry.getValue();

													// Check if the key is "Criminal Permanent", if so, skip it
													if (!originalKey.equals("Criminal permanent")) {
														// Put the key and value into the new map
														modifiedMap.put(originalKey, value);
													}
												}
												testConventionalCandidateReportDto.setCriminalCheckList(modifiedMap);

											} else if (nameOfCheck.equals("Criminal permanent")) {
												Map<String, LegalProceedingsDTO> modifiedMap = new HashMap<>();
												for (Map.Entry<String, LegalProceedingsDTO> entry : criminalCheckListMap
														.entrySet()) {
													// Get the key and value from the map entry
													String originalKey = entry.getKey();
													LegalProceedingsDTO value = entry.getValue();

													// Check if the key is "Criminal Permanent", if so, skip it
													if (!originalKey.equals("Criminal present")) {
														// Put the key and value into the new map
														modifiedMap.put(originalKey, value);
													}
												}
												testConventionalCandidateReportDto.setCriminalCheckList(modifiedMap);

											}
		                                    
		                                    testConventionalCandidateReportDto.setDataList(candidateReportDTO.getDataList());
		                                    dynamicEncodedImagesList.add(encodedImageMap);
		                                    testConventionalCandidateReportDto.setPdfByes(dynamicEncodedImagesList);
		                                    String tableHtmlStr = pdfService.parseThymeleafTemplate(templateName, testConventionalCandidateReportDto);
		                                    pdfService.generatePdfFromHtml(tableHtmlStr, allcheckDynamicReport);
		                                    // Collect education proof and table
		                                    List<InputStream> educationProof = new ArrayList<>();
		                                    educationProof.add(FileUtil.convertToInputStream(allcheckDynamicReport));
//		                                    educationProof.add(new FileInputStream(fileFromS3));
		                                    collect.addAll(educationProof);
		                                } else {
		                                    log.info("Fetching the PDF Proof for :" + nameOfCheck);
		                                    // Fetch the PDF file from S3
		                                    File fileFromS3 = awsUtils.getFileFromS3(DIGIVERIFIER_DOC_BUCKET_NAME, documentPresicedUrl);
		                                    // Generate table for this education entry
		                                    File allcheckDynamicReport = FileUtil.createUniqueTempFile(byId.getVendorChecks().getCheckType(), ".pdf");
		                                    String templateName;
		                                    if (nameOfCheck.toLowerCase().contains("education".toLowerCase())) {
		                                    	System.out.println("this is true : ");
		                                        templateName = "Conventional/EducationCheck-pdf";
		                                    }
		                                    else if (nameOfCheck.toLowerCase().contains("employment".toLowerCase())) {
		                                        templateName = "Conventional/EmploymentCheck-pdf";
		                                    } else if (nameOfCheck.toLowerCase().contains("criminal".toLowerCase())) {
		                                    	System.out.println("This is for criminal");
		                                        templateName = "Conventional/CriminalCheck-pdf";
		                                    } else if (nameOfCheck.toLowerCase().contains("global".toLowerCase())) {
		                                    	System.out.println("global :::");
		                                        templateName = "Conventional/GlobalCheck-pdf";
		                                    } else if (nameOfCheck.toLowerCase().contains("id".toLowerCase())) {
		                                        templateName = "Conventional/IDItemsCheck-pdf";
		                                    } else if (nameOfCheck.contains("LEGAL")) {
		                                        templateName = "Conventional/LegalRigntCheck";
		                                    } else if (nameOfCheck.contains("OFAC")) {
		                                        templateName = "Conventional/OfacCheck";
		                                    } else if (nameOfCheck.toLowerCase().contains("address".toLowerCase())) {
		                                        templateName = "Conventional/AddressCheck-pdf";
		                                    } else {
		                                        templateName = "Conventional/NoTableChecks";
		                                    }

		                                    testConventionalCandidateReportDto = new CandidateReportDTO();
//		                                    List<VendorUploadChecksDto> filteredVendorProofs = candidateReportDTO.getVendorProofDetails().stream().filter(p -> p.getCheckUniqueId().equalsIgnoreCase(String.valueOf(byId.get().getCheckUniqueId())) == true).collect(Collectors.toList());
//		                                    List<VendorUploadChecksDto> filteredVendorProofs = candidateReportDTO.getVendorProofDetails().stream()
//		                                    	    .filter(p -> String.valueOf(p.getVendorChecks()).equalsIgnoreCase(String.valueOf(byId.getVendorChecks().getVendorcheckId())))
//		                                    	    .collect(Collectors.toList());
		                                    
											List<VendorUploadChecksDto> filteredVendorProofs = candidateReportDTO
													.getVendorProofDetails().stream()
													.filter(p -> p.getVendorChecks()
															.equals(byId.getVendorChecks().getVendorcheckId()))
													.collect(Collectors.toList());

											// Further filter the vendorAttirbuteValue within each filteredVendorProofs
											// element
											System.out.println("filteredVendorProofs : "+filteredVendorProofs);
											System.out.println("vendorProof : "+candidateReportDTO.getVendorProofDetails().toString());
											System.out.println("nameOfCheck : "+nameOfCheck);
//											List<VendorUploadChecksDto> finalFilteredVendorProofs = filteredVendorProofs
//													.stream().map(vendorUpload -> {
//														// Filter the vendorAttirbuteValue list within each
//														// VendorUploadChecksDto
//														ArrayList<CheckAttributeAndValueDTO> filteredAttributes = vendorUpload
//																.getVendorAttirbuteValue().stream()
//																.filter(attr -> attr.getSourceName()
//																		.equals(nameOfCheck))
//																.collect(Collectors.toCollection(ArrayList::new));
//														// Set the filtered attributes back into the
//														// VendorUploadChecksDto
//														vendorUpload.setVendorAttirbuteValue(filteredAttributes);
//														return vendorUpload;
//													}).collect(Collectors.toList());
											
											List<VendorUploadChecksDto> finalFilteredVendorProofs = filteredVendorProofs
												    .stream()
												    .map(vendorUpload -> {
												        // Print vendorUpload details
//												        System.out.println("Processing VendorUploadChecksDto ID: " + vendorUpload.getId());
												        System.out.println("Original Attributes: " + vendorUpload.getVendorAttirbuteValue());

												        // Filter the vendorAttirbuteValue list within each VendorUploadChecksDto
												        ArrayList<CheckAttributeAndValueDTO> filteredAttributes = vendorUpload
												            .getVendorAttirbuteValue()
												            .stream()
												            .filter(attr -> {
												                String trimmedSourceName = attr.getSourceName().trim();
												                String trimmedNameOfCheck = nameOfCheck.trim();
												                System.out.println("Checking attribute with sourceName: " + trimmedSourceName);
												                return trimmedSourceName.equalsIgnoreCase(trimmedNameOfCheck);
												            })
												            .collect(Collectors.toCollection(ArrayList::new));

												        // Set the filtered attributes back into the VendorUploadChecksDto
												        vendorUpload.setVendorAttirbuteValue(filteredAttributes);

												        // Print filtered attributes
												        System.out.println("Filtered Attributes: " + vendorUpload.getVendorAttirbuteValue());

												        return vendorUpload;
												    })
												    .collect(Collectors.toList());
		                                    
		                                    
		                                    System.out.println("finalFilteredVendorProofs : "+finalFilteredVendorProofs.toString());
		                                    testConventionalCandidateReportDto.setVendorProofDetails(finalFilteredVendorProofs);
		                                    Map<String, LegalProceedingsDTO> filteredCriminalCheckList = candidateReportDTO.getCriminalCheckList()
		                                            .entrySet()
		                                            .stream()
		                                            .filter(entry -> "Criminal".contains(entry.getKey()))
		                                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//		                                    candidateReportDTO.
		                                    filteredCriminalCheckList.keySet().forEach(System.out::println);
		                                    
											if (nameOfCheck.equals("Criminal present")) {
												Map<String, LegalProceedingsDTO> modifiedMap = new HashMap<>();
												for (Map.Entry<String, LegalProceedingsDTO> entry : criminalCheckListMap
														.entrySet()) {
													// Get the key and value from the map entry
													String originalKey = entry.getKey();
													LegalProceedingsDTO value = entry.getValue();

													// Check if the key is "Criminal Permanent", if so, skip it
													if (!originalKey.equals("Criminal permanent")) {
														// Put the key and value into the new map
														modifiedMap.put(originalKey, value);
													}
												}
												testConventionalCandidateReportDto.setCriminalCheckList(modifiedMap);

											} else if (nameOfCheck.equals("Criminal permanent")) {
												Map<String, LegalProceedingsDTO> modifiedMap = new HashMap<>();
												for (Map.Entry<String, LegalProceedingsDTO> entry : criminalCheckListMap
														.entrySet()) {
													// Get the key and value from the map entry
													String originalKey = entry.getKey();
													LegalProceedingsDTO value = entry.getValue();

													// Check if the key is "Criminal Permanent", if so, skip it
													if (!originalKey.equals("Criminal present")) {
														// Put the key and value into the new map
														modifiedMap.put(originalKey, value);
													}
												}
												testConventionalCandidateReportDto.setCriminalCheckList(modifiedMap);
											}
//		                                    testConventionalCandidateReportDto.setCriminalCheckList(criminalCheckListMap);
		                                    testConventionalCandidateReportDto.setDataList(candidateReportDTO.getDataList());
		                                    String tableHtmlStr = pdfService.parseThymeleafTemplate(templateName, testConventionalCandidateReportDto);
		                                    
		                                    pdfService.generatePdfFromHtml(tableHtmlStr, allcheckDynamicReport);
		                                    // Collect education proof and table
		                                    List<InputStream> educationProof = new ArrayList<>();
		                                    educationProof.add(FileUtil.convertToInputStream(allcheckDynamicReport));
		                                    educationProof.add(new FileInputStream(fileFromS3));
		                                    collect.addAll(educationProof);
		                                    // Additional processing if needed
		                                }
		                            }
		                        } catch (IOException e) {
		                            log.error("Exception occurred: {}", e);
		                        }

							} catch (JsonProcessingException e) {
								// Handle the exception (e.g., log or throw)
								log.error("Exception 3 occured in generateDocument VendorProof method in ReportServiceImpl-->", e);
							}

						}
					}

					candidateReportDTO.setPdfByes(encodedImagesList);

		             // VendorProof Documnet Ends here
					// System.out.println("candidate Report dto : " +candidateReportDTO);
					pureConventionalReportVerificationStatus(candidateReportDTO);
					// CONVENTIONAL STARTS HERE	
					ArrayList<String> filePaths = new ArrayList<>();

//					File report = FileUtil.createUniqueTempFile("report", ".pdf");


//					String htmlStr = null;
//					String conventionalHtmlStr = null;
					if (reportType.toString().equalsIgnoreCase(FINAL) && candidateReportDTO.getProject().contains("Wipro")) {
						htmlStr = pdfService.parseThymeleafTemplate("wipro-final", candidateReportDTO);
					} else if(candidateReportDTO.getProject().contains("LTIMindtree")) {
						htmlStr = pdfService.parseThymeleafTemplate("template_LTIMT", candidateReportDTO);
						
					}  else if(candidateReportDTO.getProject().contains("CAPGEMINI TECHNOLOGY SERVICES INDIA LIMITED")) {
						htmlStr = pdfService.parseThymeleafTemplate("CG_UAN-Report", candidateReportDTO);
				    }
					else {
						htmlStr = pdfService.parseThymeleafTemplate("ConventionalReport_pdf", candidateReportDTO);						
				    }

					pdfService.generatePdfFromHtml(htmlStr, report);
	


//				
					try {
						System.out.println("entry to generate try*************************");
						log.info("CONVENTIONAL CANDIDATE STATUS IS MOVED TO CONVENTIONALINTERIMREPORT : CANDIDATEID : "+candidate.getCandidateId());
//						File mergedFile = FileUtil.createUniqueTempFile(candidateCode, ".pdf");
//						List<InputStream> collect = new ArrayList<>();

						
						
						CandidateCaseDetails candidateCaseDetails = candidateCaseDetailsRepository
								.findByCandidateCandidateCode(candidateCode);
						

						List<Content> uploadedDocContentList = new ArrayList<>();
						if(candidateCaseDetails != null && candidateCaseDetails.getCriminalDocContentId() != null) {
							Optional<Content> content = contentRepository.findByContentId(candidateCaseDetails.getCriminalDocContentId());
							if(content.isPresent()) {
								uploadedDocContentList.add(content.get());
							}
						}
						if(candidateCaseDetails != null && candidateCaseDetails.getGlobalDBDocContentId() != null) {
							Optional<Content> content = contentRepository.findByContentId(candidateCaseDetails.getGlobalDBDocContentId());
							if(content.isPresent()) {
								uploadedDocContentList.add(content.get());
							}
						}	
							

						List<File> uploadedDocuments = uploadedDocContentList.stream().map(content -> {
							File uniqueTempFile = FileUtil.createUniqueTempFile(
									candidateCode + "_issued_" + content.getContentId().toString(), ".pdf");
							awsUtils.getFileFromS3(content.getBucketName(), content.getPath(), uniqueTempFile);
							return uniqueTempFile;
						}).collect(Collectors.toList());
						

				        
						ClassPathResource resource = new ClassPathResource("disclaimer.pdf");
						ClassPathResource resource2 = new ClassPathResource("KPMGBackCover.pdf");
						try (InputStream inputStream = resource.getInputStream()) {
							if (reportType.toString().equalsIgnoreCase(FINAL) && candidateReportDTO.getProject().contains("Wipro")) {
								List<InputStream> onlyReport = new ArrayList<>();
								onlyReport.add(FileUtil.convertToInputStream(report));

								onlyReport.add(resource.getInputStream());
								PdfUtil.mergePdfFiles(onlyReport, new FileOutputStream(mergedFile.getPath()));
							} 
//							else if (candidateReportDTO.getProject().contains("KPMG")) {
//								List<InputStream> onlyReport = new ArrayList<>();
//								onlyReport.add(FileUtil.convertToInputStream(report));
//
//								onlyReport.add(resource2.getInputStream());
//								PdfUtil.mergePdfFiles(onlyReport, new FileOutputStream(mergedFile.getPath()));
//							} 
							else {
								collect.add(resource.getInputStream());
								PdfUtil.mergePdfFiles(collect, new FileOutputStream(mergedFile.getPath()));
//								PdfUtil.mergePdfFiles(collect2, new FileOutputStream(mergedFile.getPath()));
								}
	        			} catch (IOException e) {
							// TODO Auto-generated catch block
	        				log.error("Exception 3 occured in generateDocument method in ReportServiceImpl-->", e);
						}
						
//						log.info("CHECKING SUBMITTED DATE ::{}",candidate.getSubmittedOn());
						if (reportType == ReportType.PRE_OFFER) {
							candidate.setSubmittedOn(new Date());
							candidateRepository.save(candidate);
						}

						String path = "Candidate/".concat(candidateCode + "/Conventional" + "/Generated".concat("/")
								.concat(candidate.getApplicantId() + "_" + candidate.getCandidateName() + "_" + reportType.name()).concat(".pdf"));
						//updatinng path for epfo and dnhdb company
//						if(orgServices!=null && orgServices.contains("EPFO") && orgServices.contains("DNHDB")
//					 			&& !orgServices.contains("DIGILOCKER") && !orgServices.contains("ITR")) {
//							CandidateVerificationState updateVerificationStatus = candidateVerificationStateRepository.findByCandidateCandidateId(candidate.getCandidateId());
//							
//							String colorCode= updateVerificationStatus!=null &&
//							                  updateVerificationStatus.getInterimColorCodeStatus() !=null ?
//							                   updateVerificationStatus.getInterimColorCodeStatus().getColorCode() : reportType.name();
//							path="Candidate/".concat(candidateCode + "/Generated".concat("/")
//									.concat(candidate.getApplicantId() + "_" + candidate.getCandidateName() + "_" + colorCode).concat(".pdf"));
//						}
						String pdfUrl = awsUtils.uploadFileAndGetPresignedUrl(DIGIVERIFIER_DOC_BUCKET_NAME, path,
								mergedFile);
						System.out.println(pdfUrl);
						Content content = new Content();
						content.setCandidateId(candidate.getCandidateId());
						content.setContentCategory(ContentCategory.OTHERS);
//						content.setContentSubCategory(ContentSubCategory.PRE_APPROVAL);
						// System.out.println(content+"*******************************************content");
						if (reportType.name().equalsIgnoreCase("PRE_OFFER")) {
							content.setContentSubCategory(ContentSubCategory.PRE_APPROVAL);
						} else if (reportType.name().equalsIgnoreCase("CONVENTIONALINTERIM")) {
							content.setContentSubCategory(ContentSubCategory.CONVENTIONALINTERIM);
						} else if (reportType.name().equalsIgnoreCase("FINAL")) {
							content.setContentSubCategory(ContentSubCategory.FINAL);
						}
						content.setFileType(FileType.PDF);
						content.setContentType(ContentType.GENERATED);
						content.setBucketName(DIGIVERIFIER_DOC_BUCKET_NAME);
						content.setPath(path);
						
						// block to delete old interim
						if (reportType.name().equalsIgnoreCase("CONVENTIONALINTERIM")) {
							List<Content> existingInterimList = contentRepository.findByCandidateIdAndContentSubCategory(candidate.getCandidateId(), ContentSubCategory.CONVENTIONALINTERIM);
							if(existingInterimList.size() > 0) {
								existingInterimList.forEach(temp -> {
									contentRepository.deleteById(temp.getContentId());
								});
							}
						}
						// end of delete old interim
						contentRepository.save(content);
						String reportTypeStr = reportType.label;
						Email email = new Email();
						email.setSender(emailProperties.getDigiverifierEmailSenderId());
						User agent = candidate.getCreatedBy();
						email.setReceiver(agent.getUserEmailId());
						//setting email send to ORG customer(Organization mailid.)
//	                    log.info("SENDING GENERAL AND CC EMAIL TO ::{}",agent.getUserEmailId()+"   CC::{}"+candidate.getOrganization().getOrganizationEmailId());
	                    email.setCopiedReceiver(candidate.getOrganization().getOrganizationEmailId()+","+agent.getUserEmailId());
	                    //end
						email.setTitle("DigiVerifier " + reportTypeStr + " report - " + candidate.getCandidateName());

						String attachmentName=candidate.getApplicantId()+ "_"+candidate.getCandidateName()+ "_" + reportTypeStr + ".pdf";
						email.setAttachmentName(attachmentName);
						//email.setAttachmentName(candidateCode + " " + reportTypeStr + ".pdf");
						email.setAttachmentFile(mergedFile);

						email.setContent(String.format(emailContent, agent.getUserFirstName(), candidate.getCandidateName(),
								reportTypeStr));
						if (!reportType.name().equalsIgnoreCase("CONVENTIONALINTERIM")) {
						    emailSentTask.send(email);
						}
						//below condition for send interim report in mail to LTIM organization
//						if (reportType.name().equalsIgnoreCase("INTERIM") && candidate.getOrganization().getOrganizationName().equalsIgnoreCase("LTIMindtree")) {
//						    emailSentTask.send(email);
//						}
						if(reportType.name().equalsIgnoreCase("PRE_OFFER")) {
						    emailSentTask.loa(candidateCode);
						    //posting the candidates to API clients
						    candidateService.postStatusToOrganization(candidateCode);
						}
						
//						for (String filePath : filePaths) {
//						    System.out.println("FILE PATH: " + filePath);
//						}
						
						
//						 for (String filePath : filePaths) {
//					            File file = new File(filePath);
//					            
//					            if (file.exists()) {
//					                // Attempt to delete the file
//					                boolean deleted = file.delete();
//					                
//					                // Check if deletion was successful
//					                if (deleted) {
//					                    log.info("File deleted successfully: " + filePath);
//					                } else {
//					                    log.info("Failed to delete file: " + filePath);
//					                }
//					            } else {
//					                log.info("File does not exist: " + filePath);
//					            }
//					        }
						
						// delete files
//						files.stream().forEach(file -> file.delete());
						mergedFile.delete();
						report.delete();
						
						// Conventional Delete File
//						educationUGReport.delete();
//						educationPGReport.delete();
//						educationDiplomaReport.delete();
//						education10THReport.delete();
//						idItemsReportPAN.delete();
//						idItemsReportAadhar.delete();
//						idItemsReportPassport.delete();
//						criminalPresentReport.delete();
//						criminalPermanentReport.delete();
//						criminalPresentAndPermanentReport.delete();
//						employmentCurrentReport.delete();
//						employmentEmployer2Report.delete();
//						employment5YearsReport.delete();
//						employment7YearsReport.delete();
//						employment20YearsReport.delete();
//						addressPresentReport.delete();
//						addressPermanentReport.delete();
//						addressPresentAndPermanentReport.delete();
//						drugReport.delete();
//						globalReport.delete();
//						employmentEMPReport.delete();
						
						candidateReportDTO.setCandidate_reportType(report_present_status);
						svcSearchResult.setData(candidateReportDTO);
						svcSearchResult.setOutcome(true);
						svcSearchResult.setMessage(pdfUrl);
						svcSearchResult.setStatus(candidateReportDTO.getVerificationStatus()!=null?String.valueOf(candidateReportDTO.getVerificationStatus()):"");
						return svcSearchResult;
					} catch (MessagingException | IOException e) {
						log.error("Exception 4 occured in generateDocument method in ReportServiceImpl-->", e);
					}
					return svcSearchResult;
				} else {
					return svcSearchResult;
				}

			} else {
				System.out.println("enter else");
				throw new RuntimeException("unable to generate document for this candidate");
			}
		
//
//			
//		} catch (Exception e) {
//			log.error("Exception 4 occured in generateConventionalDocument method in ReportServiceImpl-->", e);
//		}
//		
//		return null;
		
	}
	
	// Function to check if the byte array represents a PDF
	private boolean isPDF(byte[] bytes) {
	    // Check if the file magic number matches PDF
	    return bytes.length > 4 &&
	            bytes[0] == '%' &&
	            bytes[1] == 'P' &&
	            bytes[2] == 'D' &&
	            bytes[3] == 'F';
	}
	
//	  public boolean isBase64Encoded(String value) {
//	        try {
//	            Base64.getDecoder().decode(value);
//	            return true;
//	        } catch (IllegalArgumentException e) {
//	            return false;
//	        }
//	    }
	  
	  public boolean isBase64Encoded(String value) {
	        // Regular expression to match Base64 encoded strings
	        String base64Pattern = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$";
	        return value != null && value.matches(base64Pattern);
	    }
	
//	public String convertPdfToHtml(byte[] pdfBytes) throws IOException {
//        try (PDDocument document = PDDocument.load(pdfBytes)) {
//            PDFTextStripper stripper = new PDFTextStripper();
//            String text = stripper.getText(document);
//            
//            System.out.println("text>>>>>"+text);
//            
//            if (text == null || text.isEmpty()) {
//                // If PDFBox fails to extract text, use OCR
//                File tempFile = Files.createTempFile("temp", ".pdf").toFile();
//                try {
//                    document.save(tempFile);
//                    Tesseract tesseract = new Tesseract();
//                    tesseract.setLanguage("eng"); // Set the language for OCR
//                    text = tesseract.doOCR(tempFile);
//                } catch (TesseractException e) {
//                    e.printStackTrace();
//                    return "<html><body>Error performing OCR on the PDF</body></html>";
//                } finally {
//                    Files.deleteIfExists(tempFile.toPath());
//                }
//            }
//            
//            
//            System.out.println("convertPdfToHtml>>>>>>"+text);
//
//            // You can further process the text or convert it to HTML as needed
//            // For simplicity, we'll just return the text as HTML
//            return "<html><body>" + text + "</body></html>";
//        }
//    }
	
	
	 private static String extractValue(ArrayList<String> combinedList, String key) {
	        String patternString = key + "=([^,\\]]+)";
	        Pattern pattern = Pattern.compile(patternString);

	        for (String element : combinedList) {
	            Matcher matcher = pattern.matcher(element);
	            if (matcher.find()) {
	                return matcher.group(1).trim();
	            }
	        }

	        return null;
	    }
	 
	 private static String extractAddress(ArrayList<String> combinedList) {
	        String pattern = "Address=([^=]+)";
	        Pattern p = Pattern.compile(pattern);

	        for (String element : combinedList) {
	            Matcher m = p.matcher(element);
	            if (m.find()) {
	                String addressWithComma = m.group(1).trim();
	                int lastCommaIndex = addressWithComma.lastIndexOf(",");
	                if (lastCommaIndex != -1) {
	                    return addressWithComma.substring(0, lastCommaIndex);
	                }
	                return addressWithComma;
	            }
	        }
	        
	        return null;
	    }
	
	
	
	private boolean isSameOverllappingTenureDuration(ServiceHistory cafExperience,
			ServiceHistory matchedServiceHistory) {
		boolean result = false;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
			ServiceHistory epfoData1 = this.modelMapper.map(cafExperience, ServiceHistory.class);

			ServiceHistory epfoData2 = matchedServiceHistory;
			if (epfoData1.getInputDateOfJoining() != null && epfoData2.getInputDateOfJoining() != null
					&& !epfoData1.getInputDateOfJoining().equalsIgnoreCase("NOT_AVAILABLE") && !epfoData2.getInputDateOfJoining().equalsIgnoreCase("NOT_AVAILABLE")) {
				Date start1 = dateFormat.parse(epfoData1.getInputDateOfJoining());
				Date end1 = null;
				if (epfoData1.getInputDateOfExit() == null || epfoData1.getInputDateOfExit().equalsIgnoreCase("NOT_AVAILABLE")) {
					end1 = new Date();
				} else {
					end1 = dateFormat.parse(epfoData1.getInputDateOfExit());
				}

				Date start2 = dateFormat.parse(epfoData2.getInputDateOfJoining());
				Date end2 = null;
				if (epfoData2.getInputDateOfExit() == null || epfoData2.getInputDateOfExit().equalsIgnoreCase("NOT_AVAILABLE")) {
					end2 = new Date();
				} else {
					end2 = dateFormat.parse(epfoData2.getInputDateOfExit());
				}

				DateDifference dd1 = DateUtil.getPreodDifference(start1, end1);
				DateDifference dd2 = DateUtil.getPreodDifference(start2, end2);
				if (dd1.getYears() == dd2.getYears() && dd1.getMonths() == dd2.getMonths()
						&& dd1.getDays() == dd2.getDays()) {
					result = true;
				}
			}
		} catch (Exception e) {
			log.error("Exception occured in isSameOverllappingTenureDuration method in ReportServiceImpl-->", e);
		}
		return result;
	}

	private boolean isOverllappingTenure(ServiceHistory cafExperience, ServiceHistory matchedServiceHistory) {
		
		boolean result = false;
		org.joda.time.format.DateTimeFormatter formatter = org.joda.time.format.DateTimeFormat.forPattern("dd-MMM-yyyy");
		ServiceHistory epfoData1 = this.modelMapper.map(cafExperience, ServiceHistory.class);

		ServiceHistory epfoData2 = matchedServiceHistory;
		if (epfoData1.getInputDateOfJoining() != null && epfoData2.getInputDateOfJoining() != null
				&& !epfoData1.getInputDateOfJoining().equalsIgnoreCase("NOT_AVAILABLE") && !epfoData2.getInputDateOfJoining().equalsIgnoreCase("NOT_AVAILABLE")) {

			DateTime start1 = formatter.parseDateTime(epfoData1.getInputDateOfJoining());
			DateTime end1 = null;
			if (epfoData1.getInputDateOfExit() == null || epfoData1.getInputDateOfExit().equalsIgnoreCase("NOT_AVAILABLE")) {
				end1 = DateTime.now();
			} else {
				end1 = formatter.parseDateTime(epfoData1.getInputDateOfExit());
			}

			DateTime start2 = formatter.parseDateTime(epfoData2.getInputDateOfJoining());
			DateTime end2 = null;
			if (epfoData2.getInputDateOfExit() == null || epfoData2.getInputDateOfExit().equalsIgnoreCase("NOT_AVAILABLE")) {
				end2 = DateTime.now();
			} else {
				end2 = formatter.parseDateTime(epfoData2.getInputDateOfExit());
			}

			Interval interval = null;
			Interval interval2 = null;
			if (start1.isBefore(end1)) {
				interval = new Interval(start1, end1);
			} else {
				interval = new Interval(end1, start1);
			}
			if (start2.isBefore(end2)) {
				interval2 = new Interval(start2, end2);
			} else {
				interval2 = new Interval(end2, start2);
			}

			if (interval.overlaps(interval2)) {
				result = true;
			}
		} else {
			result = true;
		}
		
		return result;
	}
	
	private  List<Map<String, List<String>>> processKeyValuePair(String pair, List<Map<String, List<String>>> attributeListAndValue) {
	    // Split the pair into key and value
	    String[] keyValue = pair.split("=");

	    // Ensure there are two parts (key and value)
	    if (keyValue.length == 2) {
	        String key = keyValue[0].trim();
	        String value = keyValue[1].trim();

	        // Create a map for each key-value pair
	        Map<String, List<String>> attributeMap = new HashMap<>();
	        attributeMap.put(key, List.of(value));

	        // Add the map to the result list
	        attributeListAndValue.add(attributeMap);
	    }

	    // Return the modified list
	    return attributeListAndValue;
	}


	@Override
	public ServiceOutcome<CandidateReportDTO> generateDocumentWipro(String candidateCode, String token,
			ReportType reportType) {
		System.out.println("enter to generate doc *******************************");
		ServiceOutcome<CandidateReportDTO> svcSearchResult = new ServiceOutcome<CandidateReportDTO>();
		Candidate candidate = candidateService.findCandidateByCandidateCode(candidateCode);
		CandidateAddComments candidateAddComments = candidateAddCommentRepository
				.findByCandidateCandidateId(candidate.getCandidateId());
		System.out.println(candidate.getCandidateId() + "*******************************"
				+ validateCandidateStatus(candidate.getCandidateId()));
		CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(candidateCode);
		Integer report_status_id = 0;
		String report_present_status = "";
		Boolean generatePdfFlag = true;
		if (reportType.equals(ReportType.PRE_OFFER)) {
			report_status_id = 7;
		} else if (reportType.equals(ReportType.FINAL)) {
			report_status_id = 8;
		} else if (reportType.equals(ReportType.INTERIM)) {
			report_status_id = 13;
		}
		if (Integer.valueOf(String.valueOf(candidateStatus.getStatusMaster().getStatusMasterId())) == 7) {
			report_present_status = "QC Pending";
		} else if (Integer.valueOf(String.valueOf(candidateStatus.getStatusMaster().getStatusMasterId())) == 8) {
			report_present_status = String.valueOf(ReportType.FINAL);
		} else if (Integer.valueOf(String.valueOf(candidateStatus.getStatusMaster().getStatusMasterId())) == 13) {
			report_present_status = String.valueOf(ReportType.INTERIM);
		}
		if (candidateStatus.getStatusMaster().getStatusMasterId() != null) {
			if (report_status_id == Integer
					.valueOf(String.valueOf(candidateStatus.getStatusMaster().getStatusMasterId()))) {
				generatePdfFlag = true;
//				log.info("entered line no 870 {}", report_status_id);
			} else {
				generatePdfFlag = false;
				CandidateReportDTO candidateDTOobj = null;
				candidateDTOobj = new CandidateReportDTO();
				candidateDTOobj
						.setCandidate_reportType("candidate in " + report_present_status + " Not in " + reportType);
				svcSearchResult.setData(candidateDTOobj);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage(null);
				svcSearchResult.setStatus(report_present_status);
			}
		}
//		log.info("entered line no 883 {}", report_status_id);
		if (validateCandidateStatus(candidate.getCandidateId())) {
			if (generatePdfFlag) {
				System.out.println("enter if *******************************");
				List<VendorUploadChecksDto> vendordocDtoList = new ArrayList<VendorUploadChecksDto>();
				VendorUploadChecksDto vendorUploadChecksDto = null;

				// candidate Basic detail
				CandidateReportDTO candidateReportDTO = new CandidateReportDTO();
				FinalReportDto candidateFinalReportDTO = new FinalReportDto();

				Set<CandidateStatusEnum> candidateStatusEnums = candidateStatusHistoryRepository
						.findAllByCandidateCandidateId(candidate.getCandidateId()).stream()
						.map(candidateStatusHistory -> CandidateStatusEnum
								.valueOf(candidateStatusHistory.getStatusMaster().getStatusCode()))
						.collect(Collectors.toSet());

				if (candidateStatusEnums.contains(CandidateStatusEnum.EPFO)) {
					candidateFinalReportDTO.setPfVerified("Yes");
				} else {
					candidateFinalReportDTO.setPfVerified("No");
				}

				candidateFinalReportDTO.setName(candidate.getCandidateName());
				candidateFinalReportDTO.setApplicantId(candidate.getApplicantId());
				candidateFinalReportDTO.setReportType(reportType);

				candidateReportDTO.setName(candidate.getCandidateName());
				candidateReportDTO.setApplicantId(candidate.getApplicantId());
				candidateReportDTO.setDob(candidate.getDateOfBirth());
				candidateReportDTO.setContactNo(candidate.getContactNumber());
				candidateReportDTO.setEmailId(candidate.getEmailId());
				candidateReportDTO.setExperience(candidate.getIsFresher() ? "Fresher" : "Experience");
				candidateReportDTO.setReportType(reportType);
				Organization organization = candidate.getOrganization();
				candidateFinalReportDTO.setOrganizationName(organization.getOrganizationName());

				candidateReportDTO.setOrganizationName(organization.getOrganizationName());
				candidateReportDTO.setProject(organization.getOrganizationName());
				candidateReportDTO.setOrganizationLocation(organization.getOrganizationLocation());
				candidateReportDTO.setOrganizationLogo(organization.getLogoUrl());
				if (candidateAddComments != null) {
					candidateReportDTO.setComments(candidateAddComments.getComments());
				}

				CandidateVerificationState candidateVerificationState = candidateService
						.getCandidateVerificationStateByCandidateId(candidate.getCandidateId());
				boolean hasCandidateVerificationStateChanged = false;
				if (Objects.isNull(candidateVerificationState)) {
					candidateVerificationState = new CandidateVerificationState();
					candidateVerificationState.setCandidate(candidate);
					final ZoneId id = ZoneId.systemDefault();
					candidateVerificationState
							.setCaseInitiationTime(ZonedDateTime.ofInstant(candidate.getCreatedOn().toInstant(), id));

				}
				switch (reportType) {
				case PRE_OFFER:
					candidateVerificationState.setPreApprovalTime(ZonedDateTime.now());
					break;
				case FINAL:
					candidateVerificationState.setFinalReportTime(ZonedDateTime.now());
					break;
				case INTERIM:
					candidateVerificationState.setInterimReportTime(ZonedDateTime.now());
					break;

				}
				candidateVerificationState = candidateService.addOrUpdateCandidateVerificationStateByCandidateId(
						candidate.getCandidateId(), candidateVerificationState);
				candidateReportDTO.setFinalReportDate(DateUtil.convertToString(ZonedDateTime.now()));
				candidateReportDTO.setInterimReportDate(
						DateUtil.convertToString(candidateVerificationState.getInterimReportTime()));
				candidateReportDTO.setCaseInitiationDate(
						DateUtil.convertToString(candidateVerificationState.getCaseInitiationTime()));
				
				
				candidateReportDTO.setCaseReinitDate(candidateVerificationState.getCaseReInitiationTime()!=null ?
						DateUtil.convertToString(candidateVerificationState.getCaseReInitiationTime()) : null);
				candidateReportDTO.setInterimAmendedDate(candidateVerificationState.getInterimReportAmendedTime()!=null ?
						DateUtil.convertToString(candidateVerificationState.getInterimReportAmendedTime()) : null);
				candidateFinalReportDTO.setCaseReinitDate(candidateVerificationState.getCaseReInitiationTime()!=null ?
						DateUtil.convertToString(candidateVerificationState.getCaseReInitiationTime()) : null);
				candidateFinalReportDTO.setInterimAmendedDate(candidateVerificationState.getInterimReportAmendedTime()!=null ?
						DateUtil.convertToString(candidateVerificationState.getInterimReportAmendedTime()) : null);

				candidateFinalReportDTO.setFinalReportDate(DateUtil.convertToString(ZonedDateTime.now()));
				candidateFinalReportDTO.setCaseInitiationDate(
						DateUtil.convertToString(candidateVerificationState.getCaseInitiationTime()));

				// executive summary
				Long organizationId = organization.getOrganizationId();
				List<OrganizationExecutive> organizationExecutiveByOrganizationId = organizationService
						.getOrganizationExecutiveByOrganizationId(organizationId);
				List<ExecutiveSummaryDto> executiveSummaryDtos = new ArrayList<>();
				organizationExecutiveByOrganizationId.stream().forEach(organizationExecutive -> {
					switch (organizationExecutive.getExecutive().getName()) {

					// System.out.println(organizationExecutive.getExecutive());
					case EDUCATION:
						System.out.println("inside EDUCATION *******************************");
						List<CandidateCafEducationDto> candidateCafEducationDtos = candidateService
								.getAllCandidateEducationByCandidateId(candidate.getCandidateId());
						List<EducationVerificationDTO> educationVerificationDTOS = candidateCafEducationDtos.stream()
								.map(candidateCafEducationDto -> {
									EducationVerificationDTO educationVerificationDTO = new EducationVerificationDTO();
									educationVerificationDTO.setVerificationStatus(
											VerificationStatus.valueOf(candidateCafEducationDto.getColorColorCode()));
									educationVerificationDTO.setSource(SourceEnum.CANDIDATE);
									educationVerificationDTO.setDegree(candidateCafEducationDto.getCourseName());
									educationVerificationDTO
											.setUniversity(candidateCafEducationDto.getBoardOrUniversityName());

									return educationVerificationDTO;
								}).collect(Collectors.toList());

						for (CandidateCafEducationDto temp : candidateCafEducationDtos) {
							if (temp.getIsHighestQualification()) {
								candidateFinalReportDTO.setHighestQualification(temp.getSchoolOrCollegeName());
								candidateFinalReportDTO.setCourseName(temp.getCourseName());
								candidateFinalReportDTO.setUniversityName(temp.getBoardOrUniversityName());
								candidateFinalReportDTO.setRollNo(temp.getCandidateRollNumber());
								candidateFinalReportDTO.setYearOfPassing(temp.getYearOfPassing());
								candidateFinalReportDTO.setEduCustomRemark(temp.getCustomRemark());
							}
						}

						candidateReportDTO.setEducationVerificationDTOList(educationVerificationDTOS);
						List<String> redArray = new ArrayList<>();
						;
						List<String> amberArray = new ArrayList<>();
						;
						List<String> greenArray = new ArrayList<>();
						;
						String status = null;
						for (EducationVerificationDTO s : educationVerificationDTOS) {
							if (s.getVerificationStatus().equals(VerificationStatus.RED)) {
								redArray.add("count");
							} else if (s.getVerificationStatus().equals(VerificationStatus.AMBER)) {
								amberArray.add("count");
							} else {
								greenArray.add("count");
							}
						}
						if (redArray.size() > 0) {
							status = VerificationStatus.RED.toString();
						} else if (amberArray.size() > 0) {
							status = VerificationStatus.AMBER.toString();
						} else {
							status = VerificationStatus.GREEN.toString();
						}
						candidateReportDTO.setEducationConsolidatedStatus(status);
						candidateFinalReportDTO.setEducationConsolidatedStatus(status);
						break;
					case IDENTITY:
						System.out.println("inside identity *******************************");
						// verify from digilocker and itr
						List<IDVerificationDTO> idVerificationDTOList = new ArrayList<>();
						IDVerificationDTO aadhaarIdVerificationDTO = new IDVerificationDTO();
						aadhaarIdVerificationDTO.setName(candidate.getAadharName());
//						aadhaarIdVerificationDTO.setName(candidate.getCandidateName());
						aadhaarIdVerificationDTO.setIDtype(IDtype.AADHAAR.label);
						aadhaarIdVerificationDTO.setIdNo(candidate.getAadharNumber());
						aadhaarIdVerificationDTO.setSourceEnum(SourceEnum.DIGILOCKER);
						aadhaarIdVerificationDTO.setVerificationStatus(VerificationStatus.GREEN);
						idVerificationDTOList.add(aadhaarIdVerificationDTO);

						IDVerificationDTO panIdVerificationDTO = new IDVerificationDTO();
//						panIdVerificationDTO.setName(candidate.getPanName());
						panIdVerificationDTO.setName(candidate.getAadharName());
						panIdVerificationDTO.setIDtype(IDtype.PAN.label);
						panIdVerificationDTO.setIdNo(candidate.getPanNumber());
						panIdVerificationDTO.setSourceEnum(SourceEnum.DIGILOCKER);
						panIdVerificationDTO.setVerificationStatus(VerificationStatus.GREEN);
						idVerificationDTOList.add(panIdVerificationDTO);

						ItrEpfoHeaderDetails itrEpfoHeaderDetails = new ItrEpfoHeaderDetails();
						List<ItrEpfoHeaderDetails> epfoDetailsForMultiUAN= new ArrayList<>();

						List<CandidateEPFOResponse> uanList = candidateEPFOResponseRepository
								.findByCandidateId(candidate.getCandidateId());
						EpfoData epfoDtls = epfoDataRepository
								.findFirstByCandidateCandidateId(candidate.getCandidateId());

						for (CandidateEPFOResponse candidateEPFOResponse : uanList) {
							ItrEpfoHeaderDetails epfoDetailsForSingleUAN = new ItrEpfoHeaderDetails();
							IDVerificationDTO uanIdVerificationDTO = new IDVerificationDTO();

							if (epfoDtls != null) {
								itrEpfoHeaderDetails.setEpfoName1(epfoDtls.getName());
								epfoDetailsForSingleUAN.setEpfoName1(candidateEPFOResponse.getUanName());
								if (candidateEPFOResponse.getUan() != null) {
									itrEpfoHeaderDetails.setUanNo1(candidateEPFOResponse.getUan());
									epfoDetailsForSingleUAN.setUanNo1(candidateEPFOResponse.getUan());
									candidateFinalReportDTO.setUanVerified("Yes");
								} else {
									candidateFinalReportDTO.setUanVerified("No");
								}
							}

							uanIdVerificationDTO.setIDtype(IDtype.UAN.label);
							uanIdVerificationDTO.setIdNo(candidateEPFOResponse.getUan());
							uanIdVerificationDTO.setSourceEnum(SourceEnum.EPFO);
							uanIdVerificationDTO.setVerificationStatus(VerificationStatus.GREEN);
							idVerificationDTOList.add(uanIdVerificationDTO);
							epfoDetailsForMultiUAN.add(epfoDetailsForSingleUAN);
						}

						IDVerificationDTO itrPanIdVerificationDTO = new IDVerificationDTO();
						itrPanIdVerificationDTO.setName(candidate.getPanName());
						itrEpfoHeaderDetails.setItrName(candidate.getPanName());
						itrPanIdVerificationDTO.setIDtype(IDtype.PAN.label);
						itrPanIdVerificationDTO.setIdNo(candidate.getItrPanNumber());
						itrEpfoHeaderDetails.setPanNo(candidate.getItrPanNumber());
						itrPanIdVerificationDTO.setSourceEnum(SourceEnum.ITR);
						itrPanIdVerificationDTO.setVerificationStatus(VerificationStatus.GREEN);
						idVerificationDTOList.add(itrPanIdVerificationDTO);
						

						candidateReportDTO.setItrEpfoHeaderDetails(itrEpfoHeaderDetails);
						candidateReportDTO.setEpfoDetailsForMultiUAN(epfoDetailsForMultiUAN);

						List<String> redArray_id = new ArrayList<>();
						;
						List<String> amberArray_id = new ArrayList<>();
						;
						List<String> greenArray_id = new ArrayList<>();
						;
						String status_id = null;
						for (IDVerificationDTO s : idVerificationDTOList) {
							if (s.getVerificationStatus().equals(VerificationStatus.RED)) {
								redArray_id.add("count");
							} else if (s.getVerificationStatus().equals(VerificationStatus.AMBER)) {
								amberArray_id.add("count");
							} else {
								greenArray_id.add("count");
							}
						}
						if (redArray_id.size() > 0) {
							status_id = VerificationStatus.RED.toString();
						} else if (amberArray_id.size() > 0) {
							status_id = VerificationStatus.AMBER.toString();
						} else {
							status_id = VerificationStatus.GREEN.toString();
						}

						System.out.println("befor epfo *******************************");
						candidateReportDTO.setIdVerificationDTOList(idVerificationDTOList);
						candidateReportDTO.setIdConsolidatedStatus(status_id);
						candidateFinalReportDTO.setIdConsolidatedStatus(status_id);
						PanCardVerificationDto panCardVerificationDto = new PanCardVerificationDto();
						panCardVerificationDto.setInput(candidate.getPanNumber());
						panCardVerificationDto.setOutput(candidate.getPanNumber());
						panCardVerificationDto.setSource(SourceEnum.DIGILOCKER);
						panCardVerificationDto.setVerificationStatus(VerificationStatus.GREEN);
						candidateReportDTO.setPanCardVerification(panCardVerificationDto);
						executiveSummaryDtos
								.add(new ExecutiveSummaryDto(ExecutiveName.IDENTITY, "Pan", VerificationStatus.GREEN));
//
						AadharVerificationDTO aadharVerification = new AadharVerificationDTO();
						aadharVerification.setAadharNo(candidate.getAadharNumber());
						aadharVerification.setName(candidate.getAadharName());
						aadharVerification.setFatherName(candidate.getAadharFatherName());
						aadharVerification.setDob(candidate.getAadharDob());
						aadharVerification.setSource(SourceEnum.DIGILOCKER);
						candidateReportDTO.setAadharCardVerification(aadharVerification);
						executiveSummaryDtos.add(
								new ExecutiveSummaryDto(ExecutiveName.IDENTITY, "Aadhar", VerificationStatus.GREEN));
						break;
					case EMPLOYMENT:
						// Calendar cal = Calendar.getInstance();
						// cal.setTimeZone(TimeZone.getTimeZone("GMT"));
						System.out.println("empy *******************************");
						List<CandidateCafExperience> candidateCafExperienceList = candidateService
								.getCandidateExperienceByCandidateId(candidate.getCandidateId());
						List<CandidateCafExperienceDto> employementDetailsDTOlist = new ArrayList<>();
						if (!candidateCafExperienceList.isEmpty()) {
							System.out.println("inside exp");
							Date dateWith1Days = null;
							Date doee = null;
							for (CandidateCafExperience candidateCafExperience : candidateCafExperienceList) {
								if (candidateCafExperience.getInputDateOfJoining() != null) {
									Date doj = candidateCafExperience.getInputDateOfJoining();
									Calendar cal = Calendar.getInstance();
//									cal.setTimeZone(TimeZone.getTimeZone("IST"));
									cal.setTime(doj);
//									cal.add(Calendar.DATE, 1);
									dateWith1Days = cal.getTime();
									System.out.println(dateWith1Days + "doj");

								}
								if (candidateCafExperience.getInputDateOfExit() != null) {
									Date doe = candidateCafExperience.getInputDateOfExit();
									Calendar cal = Calendar.getInstance();
									cal.setTime(doe);
//									cal.add(Calendar.DATE, 1);
									doee = cal.getTime();
									System.out.println(doee + "doe");
								}

								if (doee == null) { // added to check and assign current employer
									candidateFinalReportDTO
											.setCurrentEmployment(candidateCafExperience.getCandidateEmployerName());
									candidateFinalReportDTO
											.setDateOfJoin(candidateCafExperience.getInputDateOfJoining());
									candidateFinalReportDTO.setDateOfExit(candidateCafExperience.getInputDateOfExit());
								}

								String str = "NOT_AVAILABLE";
								CandidateCafExperienceDto candidateCafExperienceDto = this.modelMapper
										.map(candidateCafExperience, CandidateCafExperienceDto.class);
//								candidateCafExperienceDto.setInputDateOfJoining(
//										dateWith1Days != null ? sdf.format(dateWith1Days) : null);
//								candidateCafExperienceDto.setInputDateOfExit(doee != null ? sdf.format(doee) : str);

								candidateCafExperienceDto.setInputDateOfJoining(
										dateWith1Days != null ? new SimpleDateFormat("dd-MM-yyyy").format(dateWith1Days)
												: null);
								candidateCafExperienceDto.setInputDateOfExit(
										doee != null ? new SimpleDateFormat("dd-MM-yyyy").format(doee) : str);

								candidateCafExperienceDto
										.setCandidateEmployerName(candidateCafExperience.getCandidateEmployerName());
								candidateCafExperienceDto
										.setServiceName(candidateCafExperience.getServiceSourceMaster() != null
												? candidateCafExperience.getServiceSourceMaster().getServiceName()
												: "Candidate");
								// System.out.println("inside exp"+employe.getCandidateEmployerName());
								employementDetailsDTOlist.add(candidateCafExperienceDto);
							}
							candidateReportDTO.setEmployementDetailsDTOlist(employementDetailsDTOlist);

						}
						Collections.sort(candidateCafExperienceList, new Comparator<CandidateCafExperience>() {
							@Override
							public int compare(CandidateCafExperience o1, CandidateCafExperience o2) {
								return o1.getInputDateOfJoining().compareTo(o2.getInputDateOfJoining());
							}
						});
						Collections.reverse(candidateCafExperienceList);
						cleanDate(candidateCafExperienceList);
						List<CandidateCafExperience> candidateExperienceFromItrEpfo = candidateService
								.getCandidateExperienceFromItrAndEpfoByCandidateId(candidate.getCandidateId(), true);
						cleanDate(candidateExperienceFromItrEpfo);
						ServiceOutcome<ToleranceConfig> toleranceConfigByOrgId = organizationService
								.getToleranceConfigByOrgId(organizationId);
						// System.out.println(candidateCafExperienceList+"candidateCafExperienceList");
						if (!candidateCafExperienceList.isEmpty()) {
							System.out.println("inside exp if");
							// validate experience and tenure
							List<EmploymentVerificationDto> employmentVerificationDtoList = validateAndCompareExperience(
									candidateCafExperienceList, candidateExperienceFromItrEpfo,
									toleranceConfigByOrgId.getData());
							employmentVerificationDtoList
									.sort(Comparator.comparing(EmploymentVerificationDto::getDoj).reversed());
							candidateReportDTO.setEmploymentVerificationDtoList(employmentVerificationDtoList);

							List<EmploymentTenureVerificationDto> employmentTenureDtoList = validateAndCompareExperienceTenure(
									employmentVerificationDtoList, candidateExperienceFromItrEpfo,
									toleranceConfigByOrgId.getData(), candidateCafExperienceList);
							employmentTenureDtoList
									.sort(Comparator.comparing(EmploymentTenureVerificationDto::getDoj).reversed());
							candidateReportDTO.setEmploymentTenureVerificationDtoList(employmentTenureDtoList);
							List<String> redArray_emp = new ArrayList<>();
							List<String> amberArray_emp = new ArrayList<>();
							List<String> greenArray_emp = new ArrayList<>();
							String status_emp = null;
							for (EmploymentTenureVerificationDto s : employmentTenureDtoList) {
								if (s.getVerificationStatus().equals(VerificationStatus.RED)) {
									redArray_emp.add("count");
								} else if (s.getVerificationStatus().equals(VerificationStatus.AMBER)) {
									amberArray_emp.add("count");
								} else {
									greenArray_emp.add("count");
								}
							}
							if (redArray_emp.size() > 0) {
								status_emp = VerificationStatus.RED.toString();
							} else if (amberArray_emp.size() > 0) {
								status_emp = VerificationStatus.AMBER.toString();
							} else {
								status_emp = VerificationStatus.GREEN.toString();
							}
							candidateReportDTO.setEmploymentConsolidatedStatus(status_emp);
							candidateFinalReportDTO.setEmploymentConsolidatedStatus(status_emp);
							candidateCafExperienceList.sort(
									Comparator.comparing(CandidateCafExperience::getInputDateOfJoining).reversed());
							candidateReportDTO.setInputExperienceList(candidateCafExperienceList);
							EPFODataDto epfoDataDto = new EPFODataDto();

							Optional<CandidateEPFOResponse> canditateItrEpfoResponseOptional = candidateEPFOResponseRepository
									.findByCandidateId(candidate.getCandidateId()).stream().findFirst();
							if (canditateItrEpfoResponseOptional.isPresent()) {
								String epfoResponse = canditateItrEpfoResponseOptional.get().getEPFOResponse();
								try {
									ObjectMapper objectMapper = new ObjectMapper();
									JsonNode arrNode = objectMapper.readTree(epfoResponse).get("message");
									List<EpfoDataResDTO> epfoDatas = new ArrayList<>();
									if (arrNode.isArray()) {
										for (final JsonNode objNode : arrNode) {
											EpfoDataResDTO epfoData = new EpfoDataResDTO();
											epfoData.setName(objNode.get("name").asText());
											epfoData.setUan(objNode.get("uan").asText());
											epfoData.setCompany(objNode.get("company").asText());
											epfoData.setDoe(objNode.get("doe").asText());
											epfoData.setDoj(objNode.get("doj").asText());
											epfoDatas.add(epfoData);
										}
									}

									epfoDataDto.setCandidateName(epfoDatas.stream().map(EpfoDataResDTO::getName)
											.filter(StringUtils::isNotEmpty).findFirst().orElse(null));
									epfoDataDto.setUANno(canditateItrEpfoResponseOptional.get().getUan());
									epfoDataDto.setEpfoDataList(epfoDatas);
									candidateReportDTO.setEpfoData(epfoDataDto);
								} catch (JsonProcessingException e) {
									log.error("Exception occured in generateDocumentWipro method in ReportServiceImpl-->", e);
								}
							}

							List<ITRData> itrDataList = itrDataRepository
									.findAllByCandidateCandidateCodeOrderByFiledDateDesc(candidateCode);
							ITRDataDto itrDataDto = new ITRDataDto();
							itrDataDto.setItrDataList(itrDataList);
							candidateReportDTO.setItrData(itrDataDto);

							// System.out.println(candidateReportDTO.getEmploymentVerificationDtoList()+"candidateReportDTO");
							for (EmploymentVerificationDto employmentVerificationDto : candidateReportDTO
									.getEmploymentVerificationDtoList()) {
								// System.out.println("inside
								// for"+employmentVerificationDto+"emppppp"+candidateReportDTO);
								executiveSummaryDtos.add(new ExecutiveSummaryDto(ExecutiveName.EMPLOYMENT,
										employmentVerificationDto.getInput(),
										employmentVerificationDto.getVerificationStatus()));
							}

						}

						break;
					case ADDRESS:

						List<CandidateCafAddressDto> candidateAddress = candidateService.getCandidateAddress(candidate);
						System.out.println("ADDRESS**************" + candidateAddress);
						List<AddressVerificationDto> collect = candidateAddress.stream().map(candidateCafAddressDto -> {
							AddressVerificationDto addressVerificationDto = new AddressVerificationDto();
							addressVerificationDto.setType("Address");
							addressVerificationDto.setInput(candidateCafAddressDto.getCandidateAddress());
							addressVerificationDto.setVerificationStatus(VerificationStatus.GREEN);
							addressVerificationDto.setSource(SourceEnum.AADHAR);
							List<String> type = new ArrayList<>();
							// if(candidateCafAddressDto.getIsAssetDeliveryAddress()) {
							// type.add("Communication");
							// } if(candidateCafAddressDto.getIsPresentAddress()) {
							// type.add("Present");

							// } if(candidateCafAddressDto.getIsPermanentAddress()) {
							// type.add("Premanent");
							// }
							// addressVerificationDto.setType(String.join(", ", type));
							return addressVerificationDto;
						}).collect(Collectors.toList());
						List<String> redArray_addr = new ArrayList<>();
						;
						List<String> amberArray_addr = new ArrayList<>();
						;
						List<String> greenArray_addr = new ArrayList<>();
						;
						String status_addr = null;
						for (AddressVerificationDto s : collect) {
							if (s.getVerificationStatus().equals(VerificationStatus.RED)) {
								redArray_addr.add("count");
							} else if (s.getVerificationStatus().equals(VerificationStatus.AMBER)) {
								amberArray_addr.add("count");
							} else {
								greenArray_addr.add("count");
							}
						}
						if (redArray_addr.size() > 0) {
							status_addr = VerificationStatus.RED.toString();
						} else if (amberArray_addr.size() > 0) {
							status_addr = VerificationStatus.AMBER.toString();
						} else {
							status_addr = VerificationStatus.GREEN.toString();
						}
						candidateReportDTO.setAddressConsolidatedStatus(status_addr);
						candidateFinalReportDTO.setAddressConsolidatedStatus(status_addr);
						candidateReportDTO.setAddressVerificationDtoList(collect);
						// System.out.println("candidateReportDTO**************"+candidateReportDTO);
						break;
					case CRIMINAL:
						break;
					case REFERENCE_CHECK_1:
						break;
					case REFERENCE_CHECK_2:
						break;
					}

					System.out.println("switch  *******************************");
					candidateReportDTO.setExecutiveSummaryList(executiveSummaryDtos);
					candidateFinalReportDTO.setExecutiveSummaryList(executiveSummaryDtos);
					System.out.println("switch end *******************************");

				});
				// System.out.println("before
				// pdf*******************************"+candidateReportDTO);

				List<VendorChecks> vendorList = vendorChecksRepository
						.findAllByCandidateCandidateId(candidate.getCandidateId());
				for (VendorChecks vendorChecks : vendorList) {
					User user = userRepository.findByUserId(vendorChecks.getVendorId());
					VendorUploadChecks vendorChecksss = vendorUploadChecksRepository
							.findByVendorChecksVendorcheckId(vendorChecks.getVendorcheckId());
					if (vendorChecksss != null) {
						vendorUploadChecksDto = new VendorUploadChecksDto(user.getUserFirstName(),
								vendorChecksss.getVendorChecks().getVendorcheckId(),
								vendorChecksss.getVendorUploadedDocument(), vendorChecksss.getDocumentname(),
								vendorChecksss.getAgentColor().getColorName(),
								vendorChecksss.getAgentColor().getColorHexCode(), null,null,vendorChecksss.getCreatedOn(),null,null,null,null,null);
						vendordocDtoList.add(vendorUploadChecksDto);
					}
				}

				candidateReportDTO.setVendorProofDetails(vendordocDtoList);

				updateCandidateVerificationStatus(candidateReportDTO);
				updateCandidateVerificationStatus(candidateFinalReportDTO);

				System.out.println("after*****************update**************");
				Date createdOn = candidate.getCreatedOn();
				System.out.println("after *****************date**************");

				// System.out.println("candidate Report dto : " +candidateReportDTO);
				File report = FileUtil.createUniqueTempFile("report", ".pdf");
				String htmlStr = "";
				if (reportType == ReportType.FINAL) {
					htmlStr = pdfService.parseThymeleafTemplate("finalPdf", candidateFinalReportDTO);
				} else {
					htmlStr = pdfService.parseThymeleafTemplate("pdf_wipro", candidateReportDTO);

				}

				pdfService.generatePdfFromHtml(htmlStr, report);
				List<Content> contentList = contentRepository.findAllByCandidateIdAndContentTypeIn(
						candidate.getCandidateId(), Arrays.asList(ContentType.ISSUED, ContentType.AGENT_UPLOADED));

				List<File> files = contentList.stream().map(content -> {
					System.out.println("**************************");
					File uniqueTempFile = FileUtil.createUniqueTempFile(
							candidateCode + "_issued_" + content.getContentId().toString(), ".pdf");
					awsUtils.getFileFromS3(content.getBucketName(), content.getPath(), uniqueTempFile);
					return uniqueTempFile;
				}).collect(Collectors.toList());

				List<String> vendorFilesURLs_paths = vendordocDtoList.stream().map(vendor -> {
					byte[] data = vendor.getDocument();
					String vendorFilesTemp = "Candidate/".concat(createdOn.toString())
							.concat(candidateCode + "/Generated");
					awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, vendorFilesTemp, data);
					return vendorFilesTemp;
				}).collect(Collectors.toList());

				List<File> vendorfiles = vendorFilesURLs_paths.stream().map(content -> {
					System.out.println("**************************");
					File uniqueTempFile = FileUtil.createUniqueTempFile(content, ".pdf");
					awsUtils.getFileFromS3(DIGIVERIFIER_DOC_BUCKET_NAME, content, uniqueTempFile);
					return uniqueTempFile;
				}).collect(Collectors.toList());

				try {
					System.out.println("entry to generate try*************************");
					File mergedFile = FileUtil.createUniqueTempFile(candidateCode, ".pdf");
					List<InputStream> collect = new ArrayList<>();
					collect.add(FileUtil.convertToInputStream(report));
					collect.addAll(files.stream().map(FileUtil::convertToInputStream).collect(Collectors.toList()));
					collect.addAll(
							vendorfiles.stream().map(FileUtil::convertToInputStream).collect(Collectors.toList()));
					PdfUtil.mergePdfFiles(collect, new FileOutputStream(mergedFile.getPath()));

					String path = "Candidate/".concat(candidateCode + "/Generated".concat("/")
							.concat(candidate.getCandidateName() + "_" + reportType.name()).concat(".pdf"));
					String pdfUrl = awsUtils.uploadFileAndGetPresignedUrl(DIGIVERIFIER_DOC_BUCKET_NAME, path,
							mergedFile);
					Content content = new Content();
					content.setCandidateId(candidate.getCandidateId());
					content.setContentCategory(ContentCategory.OTHERS);
					content.setContentSubCategory(ContentSubCategory.PRE_APPROVAL);
					// System.out.println(content+"*******************************************content");
					if (reportType.name().equalsIgnoreCase("PRE_OFFER")) {
						content.setContentSubCategory(ContentSubCategory.PRE_APPROVAL);
					} else if (reportType.name().equalsIgnoreCase(FINAL)) {
						content.setContentSubCategory(ContentSubCategory.FINAL);
					}
					content.setFileType(FileType.PDF);
					content.setContentType(ContentType.GENERATED);
					content.setBucketName(DIGIVERIFIER_DOC_BUCKET_NAME);
					content.setPath(path);
					contentRepository.save(content);
					String reportTypeStr = reportType.label;
					Email email = new Email();
					email.setSender(emailProperties.getDigiverifierEmailSenderId());
					User agent = candidate.getCreatedBy();
					email.setReceiver(agent.getUserEmailId());
					email.setTitle("DigiVerifier " + reportTypeStr + " report - " + candidate.getCandidateName());

					email.setAttachmentName(candidateCode + " " + reportTypeStr + ".pdf");
					email.setAttachmentFile(mergedFile);

					email.setContent(String.format(emailContent, agent.getUserFirstName(), candidate.getCandidateName(),
							reportTypeStr));
					emailSentTask.send(email);
					// delete files
					files.stream().forEach(file -> file.delete());
					mergedFile.delete();
					report.delete();
					candidateReportDTO.setCandidate_reportType(report_present_status);
					svcSearchResult.setData(candidateReportDTO);
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage(pdfUrl);
					svcSearchResult.setStatus(String.valueOf(candidateReportDTO.getVerificationStatus()));
					return svcSearchResult;
				} catch (FileNotFoundException | MessagingException | UnsupportedEncodingException e) {
					log.error("Exception 2 occured in generateDocumentWipro method in ReportServiceImpl-->", e);
				}
				return svcSearchResult;
			} else {
				return svcSearchResult;
			}

		} else {
			System.out.println("enter else");
			throw new RuntimeException("unable to generate document for this candidate");
		}
	}

	private void cleanDate(List<CandidateCafExperience> candidateCafExperienceList) {
		if (candidateCafExperienceList.size() >= 1
				&& Objects.isNull(candidateCafExperienceList.stream().findFirst().get().getInputDateOfExit())) {
			candidateCafExperienceList.stream().findFirst().get().setInputDateOfExit(new Date());
		}

		for (int i = 1; i < candidateCafExperienceList.size(); i++) {
			CandidateCafExperience candidateCafExperience = candidateCafExperienceList.get(i);
			if (Objects.isNull(candidateCafExperience.getInputDateOfExit())) {
//				Date nextInputDateOfJoining = candidateCafExperienceList.get(i - 1).getInputDateOfJoining();
//				LocalDateTime ldt = LocalDateTime.ofInstant(nextInputDateOfJoining.toInstant(), ZoneId.systemDefault());
//				LocalDateTime exitDate = ldt.minusDays(1);
//				Date out = Date.from(exitDate.atZone(ZoneId.systemDefault()).toInstant());
//				candidateCafExperience.setInputDateOfExit(out);
				candidateCafExperience.setInputDateOfExit(new Date());
			}
		}
	}
	
	private void updateEmploymentVerificationStatus(CandidateReportDTO candidateReportDTO, String overrideReportStatus) {
		if(overrideReportStatus!=null && !overrideReportStatus.equals("") && !overrideReportStatus.equals("undefined") && !overrideReportStatus.equals("null") && !overrideReportStatus.isEmpty()) {
			String status_emp="";
			if (overrideReportStatus.equalsIgnoreCase("RED")) {
				candidateReportDTO.setEmploymentVerificationStatus(VerificationStatus.RED);
				status_emp="RED";
			} else if (overrideReportStatus.equalsIgnoreCase("AMBER") || overrideReportStatus.equalsIgnoreCase("MOONLIGHTING")) {
				candidateReportDTO.setEmploymentVerificationStatus(VerificationStatus.AMBER);
				status_emp="AMBER";
			} else {
				candidateReportDTO.setEmploymentVerificationStatus(VerificationStatus.GREEN);
				status_emp="GREEN";
			}
			List<ExecutiveSummaryDto> executiveSummaryDto = candidateReportDTO.getExecutiveSummaryList();
	        executiveSummaryDto.add(new ExecutiveSummaryDto(ExecutiveName.EMPLOYMENT,
	        		"employment", VerificationStatus.valueOf(status_emp)));
	        candidateReportDTO.setExecutiveSummaryList(executiveSummaryDto);
		}else {
			List<VerificationStatus> collect = candidateReportDTO.getExecutiveSummaryList().stream()
					.map(ExecutiveSummaryDto::getVerificationStatus).collect(Collectors.toList());
			if (collect.contains(VerificationStatus.RED)) {
				candidateReportDTO.setEmploymentVerificationStatus(VerificationStatus.RED);
			} else if (collect.contains(VerificationStatus.AMBER) || collect.contains(VerificationStatus.MOONLIGHTING)) {
				candidateReportDTO.setEmploymentVerificationStatus(VerificationStatus.AMBER);
			} else {
				candidateReportDTO.setEmploymentVerificationStatus(VerificationStatus.GREEN);
			}
		}
	}
	
	private void updateGSTVerificationStatus(CandidateReportDTO candidateReportDTO, List<GstDataFromApiDto> gstDataDTOList) {
		
		List<String> redArray_emp = new ArrayList<>();
		List<String> amberArray_emp = new ArrayList<>();
		List<String> greenArray_emp = new ArrayList<>();
		String status_emp = null;

		for (GstDataFromApiDto s : gstDataDTOList) {
			
			if (s.getColor().equals("RED")) {
				redArray_emp.add("count");
			} else if (s.getColor().equals("AMBER")) {
				amberArray_emp.add("count");
			} else {
				greenArray_emp.add("count");
			}
					
		} // end
		
		
		if (redArray_emp.size() > 0) {
			status_emp = VerificationStatus.RED.toString();
		} else if (amberArray_emp.size() > 0) {
			status_emp = VerificationStatus.AMBER.toString();
		} else {
			status_emp = VerificationStatus.GREEN.toString();
		}
		
        candidateReportDTO.setGstVerificationStatus(VerificationStatus.valueOf(status_emp));
        
        List<ExecutiveSummaryDto> executiveSummaryDto = candidateReportDTO.getExecutiveSummaryList();
        executiveSummaryDto.add(new ExecutiveSummaryDto(ExecutiveName.EMPLOYMENT,
        		"gst", VerificationStatus.valueOf(status_emp)));
        
        candidateReportDTO.setExecutiveSummaryList(executiveSummaryDto);

	}

	private void updateCandidateVerificationStatus(CandidateReportDTO candidateReportDTO) {
//		System.out.println("entry private------------" + candidateReportDTO.getExecutiveSummaryList());
		List<VerificationStatus> collect = candidateReportDTO.getExecutiveSummaryList().stream()
				.map(ExecutiveSummaryDto::getVerificationStatus).collect(Collectors.toList());
//		System.out.println("private----inisde--------" + collect);
//		if (collect.contains(VerificationStatus.RED)) {
//			System.out.println("entry if------------");
//			candidateReportDTO.setVerificationStatus(VerificationStatus.RED);
//		} else if (collect.contains(VerificationStatus.AMBER) || collect.contains(VerificationStatus.MOONLIGHTING)) {
//			System.out.println("entry else------if------");
//			candidateReportDTO.setVerificationStatus(VerificationStatus.AMBER);
//		} else {
//			System.out.println("entry------else------");
//			candidateReportDTO.setVerificationStatus(VerificationStatus.GREEN);
//		}
//		System.out.println("CANIDATEID::::::::::::::::::::::::::"+candidateReportDTO.getCandidateId());

		CandidateVerificationState updateVerificationStatus = candidateVerificationStateRepository.findByCandidateCandidateId(candidateReportDTO.getCandidateId());
		if (collect.contains(VerificationStatus.RED)) {
//			log.info("entry if------------");
//			System.out.println("BLOCK 1:::"+candidateReportDTO.getReportType());
			if(candidateReportDTO.getReportType().label.equals("Pre Offer")) {
//				log.info("==================== PREOFFER ================");
				updateVerificationStatus.setPreApprovalColorCodeStatus(colorRepository.findByColorCode("RED"));
			}
			else if(candidateReportDTO.getReportType().label.equals("Interim")) {
//				log.info("==================== INTERIM ================");
				updateVerificationStatus.setInterimColorCodeStatus(colorRepository.findByColorCode("RED"));
				}
			else {
//				log.info("==================== FINAL ================");	
				updateVerificationStatus.setFinalColorCodeStatus(colorRepository.findByColorCode("RED"));
				}		
				candidateReportDTO.setVerificationStatus(VerificationStatus.RED);
		} else if (collect.contains(VerificationStatus.AMBER) || collect.contains(VerificationStatus.MOONLIGHTING)) {
//			log.info("entry else------if------");
			System.out.println("BLOCK 2:::"+candidateReportDTO.getReportType());
			if(candidateReportDTO.getReportType().label.equals("Pre Offer")) {
//				log.info("==================== PREOFFER ================");
				updateVerificationStatus.setPreApprovalColorCodeStatus(colorRepository.findByColorCode("AMBER"));				
			}
			else if(candidateReportDTO.getReportType().label.equals("Interim")) {
//				log.info("==================== INTERIM ================");
				updateVerificationStatus.setInterimColorCodeStatus(colorRepository.findByColorCode("AMBER"));
				}
			else {
//				log.info("==================== FINAL ================");	
				updateVerificationStatus.setFinalColorCodeStatus(colorRepository.findByColorCode("AMBER"));
			}
			candidateReportDTO.setVerificationStatus(VerificationStatus.AMBER);
		} else {
//			log.info("entry------else------");
//			log.info("BLOCK 3:::"+candidateReportDTO.getReportType());

			if(candidateReportDTO.getReportType().label.equals("Pre Offer")) {
//				log.info("==================== PREOFFER ================");
				updateVerificationStatus.setPreApprovalColorCodeStatus(colorRepository.findByColorCode("GREEN"));				

			}
			else if(candidateReportDTO.getReportType().label.equals("Interim")) {

//				log.info("==================== INTERIM ================");
				updateVerificationStatus.setInterimColorCodeStatus(colorRepository.findByColorCode("GREEN"));
			}
			else {
//				log.info("==================== FINAL ================");

				updateVerificationStatus.setFinalColorCodeStatus(colorRepository.findByColorCode("GREEN"));
			}
			candidateReportDTO.setVerificationStatus(VerificationStatus.GREEN);
		}
		
		if(candidateReportDTO.getOrgServices() !=null && candidateReportDTO.getOrgServices().contains("GST")) {
			List<VerificationStatus> verificationStatusList = new ArrayList<>();
			verificationStatusList.add(candidateReportDTO.getEmploymentVerificationStatus());
			verificationStatusList.add(candidateReportDTO.getGstVerificationStatus());
			
			if (verificationStatusList.contains(VerificationStatus.RED)) {	
				candidateReportDTO.setVerificationStatus(VerificationStatus.RED);
			} else if (verificationStatusList.contains(VerificationStatus.AMBER)) {
				candidateReportDTO.setVerificationStatus(VerificationStatus.AMBER);
			} else {
				candidateReportDTO.setVerificationStatus(VerificationStatus.GREEN);
			}
		}
		
		candidateVerificationStateRepository.save(updateVerificationStatus);
	}
	
	private void conventionalReportVerificationStatus(CandidateReportDTO candidateReportDTO) {
		List<String> collect = candidateReportDTO.getVendorProofDetails().stream()
				.map(VendorUploadChecksDto::getCheckStatus)
				.collect(Collectors.toList());

		for (String status : collect) {
			System.out.println(status);
		}
		CandidateVerificationState updateVerificationStatus = candidateVerificationStateRepository.findByCandidateCandidateId(candidateReportDTO.getCandidateId());
		if (collect.contains("MINORDISCREPANCY") || collect.contains("MAJORDISCREPANCY")) {
			if(candidateReportDTO.getReportType().label.equals("Pre Offer")) {
				//				log.info("==================== PREOFFER ================");
				updateVerificationStatus.setPreApprovalColorCodeStatus(colorRepository.findByColorCode("RED"));
			}
			else if(candidateReportDTO.getReportType().label.equals("Interim")) {
				//				log.info("==================== INTERIM ================");
				updateVerificationStatus.setInterimColorCodeStatus(colorRepository.findByColorCode("RED"));
			}
			else {
				//				log.info("==================== FINAL ================");	
				updateVerificationStatus.setFinalColorCodeStatus(colorRepository.findByColorCode("RED"));
			}		
			candidateReportDTO.setVerificationStatus(VerificationStatus.RED);
		}
		else if (collect.contains("UNABLETOVERIFY")) {
			//			log.info("entry else------if------");
			System.out.println("BLOCK 2:::"+candidateReportDTO.getReportType());
			if(candidateReportDTO.getReportType().label.equals("Pre Offer")) {
				//				log.info("==================== PREOFFER ================");
				updateVerificationStatus.setPreApprovalColorCodeStatus(colorRepository.findByColorCode("AMBER"));				
			}
			else if(candidateReportDTO.getReportType().label.equals("Interim")) {
				//				log.info("==================== INTERIM ================");
				updateVerificationStatus.setInterimColorCodeStatus(colorRepository.findByColorCode("AMBER"));
			}
			else {
				//				log.info("==================== FINAL ================");	
				updateVerificationStatus.setFinalColorCodeStatus(colorRepository.findByColorCode("AMBER"));
			}
			candidateReportDTO.setVerificationStatus(VerificationStatus.AMBER);
		}
		else {
			//		log.info("entry------else------");
			//		log.info("BLOCK 3:::"+candidateReportDTO.getReportType());

			if(candidateReportDTO.getReportType().label.equals("Pre Offer")) {
				//			log.info("==================== PREOFFER ================");
				updateVerificationStatus.setPreApprovalColorCodeStatus(colorRepository.findByColorCode("GREEN"));				

			}
			else if(candidateReportDTO.getReportType().label.equals("Interim")) {

				//			log.info("==================== INTERIM ================");
				updateVerificationStatus.setInterimColorCodeStatus(colorRepository.findByColorCode("GREEN"));
			}
			else {
				//			log.info("==================== FINAL ================");

				updateVerificationStatus.setFinalColorCodeStatus(colorRepository.findByColorCode("GREEN"));
			}
			candidateReportDTO.setVerificationStatus(VerificationStatus.GREEN);
		}
		candidateVerificationStateRepository.save(updateVerificationStatus);


	}
	
	private void pureConventionalReportVerificationStatus(CandidateReportDTO candidateReportDTO) {
		List<String> collect = candidateReportDTO.getVendorProofDetails().stream()
				.map(VendorUploadChecksDto::getCheckStatus)
				.collect(Collectors.toList());

		for (String status : collect) {
			System.out.println(status);
		}
		ConventionalCandidateVerificationState updateVerificationStatus = conventionalCandidateVerificationStateRepository.findByCandidateCandidateId(candidateReportDTO.getCandidateId());
		if (collect.contains("MINORDISCREPANCY") || collect.contains("MAJORDISCREPANCY")) {
			if(candidateReportDTO.getReportType().label.equals("Pre Offer")) {
				//				log.info("==================== PREOFFER ================");
				updateVerificationStatus.setPreApprovalColorCodeStatus(colorRepository.findByColorCode("RED"));
			}
			else if(candidateReportDTO.getReportType().label.equals("Interim")) {
				//				log.info("==================== INTERIM ================");
				updateVerificationStatus.setInterimColorCodeStatus(colorRepository.findByColorCode("RED"));
			}
			else {
				//				log.info("==================== FINAL ================");	
				updateVerificationStatus.setFinalColorCodeStatus(colorRepository.findByColorCode("RED"));
			}		
			candidateReportDTO.setVerificationStatus(VerificationStatus.RED);
		}
		else if (collect.contains("UNABLETOVERIFY")) {
			//			log.info("entry else------if------");
			System.out.println("BLOCK 2:::"+candidateReportDTO.getReportType());
			if(candidateReportDTO.getReportType().label.equals("Pre Offer")) {
				//				log.info("==================== PREOFFER ================");
				updateVerificationStatus.setPreApprovalColorCodeStatus(colorRepository.findByColorCode("AMBER"));				
			}
			else if(candidateReportDTO.getReportType().label.equals("Interim")) {
				//				log.info("==================== INTERIM ================");
				updateVerificationStatus.setInterimColorCodeStatus(colorRepository.findByColorCode("AMBER"));
			}
			else {
				//				log.info("==================== FINAL ================");	
				updateVerificationStatus.setFinalColorCodeStatus(colorRepository.findByColorCode("AMBER"));
			}
			candidateReportDTO.setVerificationStatus(VerificationStatus.AMBER);
		}
		else {
			//		log.info("entry------else------");
			//		log.info("BLOCK 3:::"+candidateReportDTO.getReportType());

			if(candidateReportDTO.getReportType().label.equals("Pre Offer")) {
				//			log.info("==================== PREOFFER ================");
				updateVerificationStatus.setPreApprovalColorCodeStatus(colorRepository.findByColorCode("GREEN"));				

			}
			else if(candidateReportDTO.getReportType().label.equals("Interim")) {

				//			log.info("==================== INTERIM ================");
				updateVerificationStatus.setInterimColorCodeStatus(colorRepository.findByColorCode("GREEN"));
			}
			else {
				//			log.info("==================== FINAL ================");

				updateVerificationStatus.setFinalColorCodeStatus(colorRepository.findByColorCode("GREEN"));
			}
			candidateReportDTO.setVerificationStatus(VerificationStatus.GREEN);
		}
		conventionalCandidateVerificationStateRepository.save(updateVerificationStatus);


	}

	private void updateCandidateVerificationStatus(FinalReportDto candidateReportDTO) {
//		System.out.println("entry private------------" + candidateReportDTO.getExecutiveSummaryList());
		List<VerificationStatus> collect = candidateReportDTO.getExecutiveSummaryList().stream()
				.map(ExecutiveSummaryDto::getVerificationStatus).collect(Collectors.toList());
//		System.out.println("private----inisde--------" + collect);
		if (collect.contains(VerificationStatus.RED)) {
			candidateReportDTO.setVerificationStatus(VerificationStatus.RED);
		} else if (collect.contains(VerificationStatus.AMBER)) {
			candidateReportDTO.setVerificationStatus(VerificationStatus.AMBER);
		} else {
			candidateReportDTO.setVerificationStatus(VerificationStatus.GREEN);
		}
	}

	private List<EmploymentTenureVerificationDto> validateAndCompareExperienceTenure(
			List<EmploymentVerificationDto> candidateCafExperienceList,
			List<CandidateCafExperience> candidateExperienceFromItrEpfo, ToleranceConfig toleranceConfig,
			List<CandidateCafExperience> candidateCafExperienceMainList) {

		return candidateCafExperienceList.stream().map(employmentVerificationDto -> {
			EmploymentTenureVerificationDto employmentTenureVerificationDto = new EmploymentTenureVerificationDto();

			List<CandidateCafExperience> tempcandidateCafExperience = new ArrayList<>();
			tempcandidateCafExperience = candidateCafExperienceMainList.stream()
					.filter(temp -> temp.getCandidateCafExperienceId().equals(employmentVerificationDto.getCandidateCafExperienceId())).collect(Collectors.toList());
			
			long inputDifference1 = Long.valueOf(0);
			if(tempcandidateCafExperience.size() > 0 && tempcandidateCafExperience.get(0).getInputDateOfJoining() != null) {
				
				inputDifference1 = DateUtil.differenceInMonths(tempcandidateCafExperience.get(0).getInputDateOfJoining(),
						tempcandidateCafExperience.get(0).getInputDateOfExit());
			} else {
				inputDifference1 = DateUtil.differenceInMonths(employmentVerificationDto.getDoj() != null ? employmentVerificationDto.getDoj(): new Date(),
						employmentVerificationDto.getDoe());
			}
			long outputDifference = Long.valueOf(0);

			employmentTenureVerificationDto.setDoj(employmentVerificationDto.getDoj());
			employmentTenureVerificationDto.setDoe(employmentVerificationDto.getDoe());

			employmentTenureVerificationDto
					.setInput(inputDifference1 / 12 + " Years, " + inputDifference1 % 12 + " months");
//			employmentTenureVerificationDto
//					.setOutput(inputDifference1 / 12 + " Years, " + inputDifference1 % 12 + " months");
			employmentTenureVerificationDto.setSource(employmentVerificationDto.getSource());
			employmentTenureVerificationDto.setSecondarySource(employmentVerificationDto.getSecondarySource());

			employmentTenureVerificationDto.setInput("Data Not Found");
			employmentTenureVerificationDto.setOutput("Data Not Found");
			employmentTenureVerificationDto.setVerificationStatus(VerificationStatus.AMBER);
			Integer tenure = toleranceConfig.getTenure();
			employmentTenureVerificationDto.setEmployerName(employmentVerificationDto.getInput());

			// added
			employmentTenureVerificationDto.setCustomRemark(employmentVerificationDto.getCustomRemark());
			if(employmentVerificationDto.getUndisclosed() != null)
				employmentTenureVerificationDto.setUndisclosed(employmentVerificationDto.getUndisclosed());
			employmentTenureVerificationDto
					.setCandidateCafExperienceId(employmentVerificationDto.getCandidateCafExperienceId());

			if (employmentVerificationDto.getVerificationStatus().equals(VerificationStatus.GREEN)) {
				CandidateCafExperience candidateCafExperience = candidateExperienceFromItrEpfo
						.get(employmentVerificationDto.getIndex());

				employmentTenureVerificationDto.setVerificationStatus(
						VerificationStatus.valueOf(candidateCafExperience.getColor().getColorCode()));

//				long inputDifference = DateUtil.differenceInMonths(employmentVerificationDto.getDoj(),
//						employmentVerificationDto.getDoe());
				
				long inputDifference = Long.valueOf(0);
				if(tempcandidateCafExperience.size() > 0 && tempcandidateCafExperience.get(0).getInputDateOfJoining() != null) {
					
					inputDifference = DateUtil.differenceInMonths(tempcandidateCafExperience.get(0).getInputDateOfJoining(),
							employmentVerificationDto.getDoe());
				} else {
					inputDifference = DateUtil.differenceInMonths(employmentVerificationDto.getDoj() != null ? employmentVerificationDto.getDoj(): new Date(),
							employmentVerificationDto.getDoe()); 
				}
				employmentTenureVerificationDto
						.setInput(inputDifference / 12 + " Y, " + inputDifference % 12 + " M");

				outputDifference = DateUtil.differenceInMonths(candidateCafExperience.getInputDateOfJoining() != null ? candidateCafExperience.getInputDateOfJoining() : new Date(),
						candidateCafExperience.getInputDateOfExit());
				employmentTenureVerificationDto
						.setOutput(outputDifference / 12 + " Y, " + outputDifference % 12 + "M");

				if (Math.abs(inputDifference - outputDifference) <= tenure) {
					employmentTenureVerificationDto.setSource(employmentVerificationDto.getSource());
					employmentTenureVerificationDto.setVerificationStatus(VerificationStatus.GREEN);
				}

			}
			// overwriting the input tenure to support 11 oct dev issues
			if(tempcandidateCafExperience.size() > 0) {
				employmentTenureVerificationDto.setDoj(tempcandidateCafExperience.get(0).getInputDateOfJoining());
				if(tempcandidateCafExperience.get(0).getInputDateOfJoining() != null) {
					DateDifference dateDifferenceInput= new DateDifference();
					inputDifference1 = DateUtil.differenceInMonths(tempcandidateCafExperience.get(0).getInputDateOfJoining(),
							tempcandidateCafExperience.get(0).getInputDateOfExit() != null ? tempcandidateCafExperience.get(0).getInputDateOfExit() : new Date());
					
					dateDifferenceInput = DateUtil.getPreodDifference(tempcandidateCafExperience.get(0).getInputDateOfJoining(),
							tempcandidateCafExperience.get(0).getInputDateOfExit() != null ? tempcandidateCafExperience.get(0).getInputDateOfExit() : new Date());
					log.info("NEW YEAR MONTH FOR INPUT ::{}",dateDifferenceInput.getYears() +"::"+dateDifferenceInput.getMonths()+"::"+dateDifferenceInput.getDays());
					
					Integer inputTenureMonths = dateDifferenceInput.getMonths();
					if(tempcandidateCafExperience.get(0).getServiceSourceMaster() != null && tempcandidateCafExperience.get(0).getServiceSourceMaster().getServiceCode().equalsIgnoreCase("ITR")) {
						inputTenureMonths += 1;
					}
					employmentTenureVerificationDto
					.setInput(dateDifferenceInput.getYears() + " Y, " + inputTenureMonths + " M, " + dateDifferenceInput.getDays()+ " D");
//					.setInput(inputDifference1 / 12 + " Years, " + inputDifference1 % 12 + " months, " + dateDifferenceInput.getDays()+ " days");
					
					if(tempcandidateCafExperience.get(0).getOutputDateOfJoining() != null) {
						DateDifference dateDifferenceOutPut= new DateDifference();
						
						outputDifference = DateUtil.differenceInMonths(tempcandidateCafExperience.get(0).getOutputDateOfJoining(),
								tempcandidateCafExperience.get(0).getOutputDateOfExit() != null ? tempcandidateCafExperience.get(0).getOutputDateOfExit() : new Date());
						
						dateDifferenceOutPut = DateUtil.getPreodDifference(tempcandidateCafExperience.get(0).getOutputDateOfJoining(),
								tempcandidateCafExperience.get(0).getOutputDateOfExit() != null ? tempcandidateCafExperience.get(0).getOutputDateOfExit() : new Date());
						log.info("NEW YEAR MONTH FOR OUTPUT ::{}",dateDifferenceOutPut.getYears() +"::"+dateDifferenceOutPut.getMonths());
						Integer outPutTenureMonths = dateDifferenceOutPut.getMonths();
						if(tempcandidateCafExperience.get(0).getServiceSourceMaster() != null && tempcandidateCafExperience.get(0).getServiceSourceMaster().getServiceCode().equalsIgnoreCase("ITR")) {
							outPutTenureMonths += 1;
						}
						employmentTenureVerificationDto
						.setOutput(dateDifferenceOutPut.getYears() + " Y, " + outPutTenureMonths + " M, " + dateDifferenceOutPut.getDays()+ " D");
//						.setOutput(outputDifference / 12 + " Years, " + outputDifference % 12 + " months, " + dateDifferenceOutPut.getDays()+ " days");
					} else {
						employmentTenureVerificationDto.setOutput("Data Not Found");
					}
				} else {
					employmentTenureVerificationDto.setInput("Data Not Found");
					employmentTenureVerificationDto.setOutput("Data Not Found");
				}
			}
			
			if(employmentTenureVerificationDto.getInput().equalsIgnoreCase("0 Y, 0 M, 0 D"))
				employmentTenureVerificationDto.setInput("0 Y, 0 M, 0 D");
			
			if(employmentTenureVerificationDto.getOutput().equalsIgnoreCase("0 Y, 0 M, 0 D"))
				employmentTenureVerificationDto.setOutput("0 Y, 0 M, 0 D");

			return employmentTenureVerificationDto;
		}).collect(Collectors.toList());
//			Optional<CandidateCafExperience> experienceOptional = candidateExperienceFromItrEpfo.stream()
//				.filter(candidateCafExperienceItrEpfo -> {
//					int dojDifference = DateUtil.differenceInMonths(employmentVerificationDto.getInputDateOfJoining(),
//						candidateCafExperienceItrEpfo.getInputDateOfJoining());
//					int doeDifference = DateUtil.differenceInMonths(employmentVerificationDto.getInputDateOfExit(),
//						candidateCafExperienceItrEpfo.getInputDateOfExit());
//					return doeDifference <= tenure && dojDifference <= tenure;
//				}).findFirst();
//			if(experienceOptional.isPresent()){
//				String employerNameOp = employmentVerificationDto.getCandidateEmployerName();
//				employmentTenureVerificationDto.setOutput(employerNameOp);
//				employmentTenureVerificationDto.setVerificationStatus(VerificationStatus.GREEN);
//			}else{
//				employmentTenureVerificationDto.setVerificationStatus(VerificationStatus.RED);
//				employmentTenureVerificationDto.setOutput(NO_DATA_FOUND);
//			}
//
//			return employmentTenureVerificationDto;
//		}).collect(Collectors.toList());

	}

	private List<EmploymentVerificationDto> validateAndCompareExperience(
			List<CandidateCafExperience> candidateCafExperienceList,
			List<CandidateCafExperience> candidateExperienceFromItrEpfo, ToleranceConfig toleranceConfig) {

		List<EmploymentVerificationDto> employmentVerificationDtoList = candidateCafExperienceList.stream()
				.map(candidateCafExperience -> {
					EmploymentVerificationDto employmentVerificationDto = new EmploymentVerificationDto();
					employmentVerificationDto.setInput(candidateCafExperience.getCandidateEmployerName());
					employmentVerificationDto.setVerificationStatus(VerificationStatus.AMBER);
					employmentVerificationDto.setOutput("Data Not Found");
					employmentVerificationDto.setDoj(candidateCafExperience.getInputDateOfJoining());
					employmentVerificationDto.setDoe(candidateCafExperience.getInputDateOfExit()); 
					employmentVerificationDto.setOutputDoj(candidateCafExperience.getOutputDateOfJoining());
					if(candidateCafExperience.getServiceSourceMaster() != null)
						if(candidateCafExperience.getServiceSourceMaster().getServiceCode() != null)
							employmentVerificationDto.setSource(SourceEnum.valueOf(candidateCafExperience.getServiceSourceMaster().getServiceCode()));
						else 
							employmentVerificationDto.setSource(null);
					
					if(candidateCafExperience.getSecondaryServiceSourceMaster() != null)
						if(candidateCafExperience.getSecondaryServiceSourceMaster().getServiceCode() != null)
							employmentVerificationDto.setSecondarySource(SourceEnum.valueOf(candidateCafExperience.getSecondaryServiceSourceMaster().getServiceCode()));
					
					employmentVerificationDto.setCustomRemark(candidateCafExperience.getCustomRemark());
					if(candidateCafExperience.getUndisclosed() != null)
						employmentVerificationDto.setUndisclosed(candidateCafExperience.getUndisclosed());
					employmentVerificationDto
							.setCandidateCafExperienceId(candidateCafExperience.getCandidateCafExperienceId());
					return employmentVerificationDto;
				}).collect(Collectors.toList());

		for (EmploymentVerificationDto employmentVerificationDto : employmentVerificationDtoList) {
			int index = 0;
			List<String> outputNameSet = candidateExperienceFromItrEpfo.stream()
					.map(CandidateCafExperience::getCandidateEmployerName).collect(Collectors.toList());
			while (index < outputNameSet.size()
					&& employmentVerificationDto.getVerificationStatus().equals(VerificationStatus.AMBER)) {
				String employerName1 = outputNameSet.get(index);
				double similarity = CommonUtils.checkStringSimilarity(employmentVerificationDto.getInput(),
						employerName1);
				if (similarity >= 0.90 && employmentVerificationDto.getSource() != null) {
					employmentVerificationDto.setVerificationStatus(VerificationStatus.GREEN);
					employmentVerificationDto.setOutput(employerName1);
//					employmentVerificationDto.setSource(SourceEnum.valueOf(
//							candidateExperienceFromItrEpfo.get(index).getServiceSourceMaster().getServiceCode()));
					outputNameSet.remove(index);
					employmentVerificationDto.setIndex(index);
					employmentVerificationDto.setDoj(candidateExperienceFromItrEpfo.get(index).getInputDateOfJoining());
					Date inputDateOfExit = candidateExperienceFromItrEpfo.get(index).getInputDateOfExit();
					if (Objects.isNull(inputDateOfExit) && index == 0) {
						employmentVerificationDto.setDoe(new Date());
					} else {
						employmentVerificationDto.setDoe(inputDateOfExit);
					}
				}
				index++;
			}
		}

		return employmentVerificationDtoList;

	}

	private boolean validateCandidateStatus(Long candidateId) {
		Set<CandidateStatusEnum> candidateStatusEnums = candidateStatusHistoryRepository
				.findAllByCandidateCandidateId(candidateId).stream().map(candidateStatusHistory -> CandidateStatusEnum
						.valueOf(candidateStatusHistory.getStatusMaster().getStatusCode()))
				.collect(Collectors.toSet());
//		log.info("candidateStatusEnums {}", candidateStatusEnums);		
		return (candidateStatusEnums.contains(CandidateStatusEnum.EPFO)
				|| candidateStatusEnums.contains(CandidateStatusEnum.ITR)
				|| candidateStatusEnums.contains(CandidateStatusEnum.DIGILOCKER)
				|| candidateStatusEnums.contains(CandidateStatusEnum.REMITTANCE)
				|| candidateStatusEnums.contains(CandidateStatusEnum.GST));
//		return (candidateStatusEnums.contains(CandidateStatusEnum.ITR));
	}

	@Override
	public ServiceOutcome<VendorSearchDto> getVendorDetailsByStatus(VendorSearchDto reportSearchDto) {
		ServiceOutcome<VendorSearchDto> svcSearchResult = new ServiceOutcome<VendorSearchDto>();
		List<CandidateDetailsForReport> candidateDetailsDtoList = new ArrayList<CandidateDetailsForReport>();
		CandidateEmailStatus candidateEmailStatus = null;
		List<CandidateStatus> candidateStatusList = null;
		CandidateDetailsForReport candidateDto = null;
		List<vendorChecksDto> vendorDetailsDtoList = new ArrayList<vendorChecksDto>();
		vendorChecksDto vendorChecksDto = null;
		List<Object[]> resultList = null;
		StringBuilder query = null;
		Query squery = null;
		Integer VendorStatusmasterId = null;
		try {
			if (StringUtils.isNotBlank(reportSearchDto.getStatusCode())
					&& StringUtils.isNotBlank(reportSearchDto.getFromDate())
					&& StringUtils.isNotBlank(reportSearchDto.getToDate())
					&& reportSearchDto.getOrganizationIds() != null
					&& !reportSearchDto.getOrganizationIds().isEmpty()) {
				Date startDate = format.parse(reportSearchDto.getFromDate() + " 00:00:00");
				Date endDate = format.parse(reportSearchDto.getToDate() + " 23:59:59");
				List<String> statusList = null;
				if (reportSearchDto.getStatusCode().equals("NEWUPLOAD")
						|| reportSearchDto.getStatusCode().equals("REINVITE")
						|| reportSearchDto.getStatusCode().equals("FINALREPORT")
						|| reportSearchDto.getStatusCode().equals("PENDINGAPPROVAL")
						|| reportSearchDto.getStatusCode().equals("PROCESSDECLINED")
						|| reportSearchDto.getStatusCode().equals("INVITATIONEXPIRED")
						|| reportSearchDto.getStatusCode().equals("PENDINGNOW")) {
					query = new StringBuilder();
					if (reportSearchDto.getStatusCode() != null) {
						if (reportSearchDto.getStatusCode().equals("NEWUPLOAD")) {
							VendorStatusmasterId = 1;
						} else if (reportSearchDto.getStatusCode().equals("REINVITE")) {
							VendorStatusmasterId = 2;
						} else if (reportSearchDto.getStatusCode().equals("FINALREPORT")) {
							VendorStatusmasterId = 3;
						} else if (reportSearchDto.getStatusCode().equals("PENDINGNOW")) {
							VendorStatusmasterId = 4;
						} else if (reportSearchDto.getStatusCode().equals("PROCESSDECLINED")) {
							VendorStatusmasterId = 5;
						} else if (reportSearchDto.getStatusCode().equals("INVITATIONEXPIRED")) {
							VendorStatusmasterId = 6;
						}
					}
					query.append(
							"select distinct vc.vendor_check_id, vc.candidate_id, vc.created_by, vc.created_at, vc.email_id, vc.expires_on, vc.tat, vc.vendor_id, vc.source_id, vc.Is_proof_uploaded, vc.agent_Uploaded_Document, vc.address, vc.alternate_contact_no, vc.candidate_name, vc.contact_no, vc.date_of_birth, vc.document_name, vc.father_name, vc.type_of_panel, vc.vendor_checkstatus_master_id from t_dgv_role_master rm, t_dgv_user_master um, t_dgv_vendor_master_new vm, t_dgv_vendor_checks vc, t_dgv_organization_master org where vc.vendor_checkstatus_master_id in (?1) and vc.created_at between ?2 and  ?3 and org.organization_id = um.orgainzation_id and um.user_id = vm.user_id and vm.vendor_id = vc.vendor_id;");
					squery = entityManager.createNativeQuery(query.toString());
					squery.setParameter(1, VendorStatusmasterId);
					squery.setParameter(2, startDate);
					squery.setParameter(3, endDate);
//						squery.setParameter(4, reportSearchDto.getOrganizationIds());

//						if(reportSearchDto.getStatusCode().equals("PENDINGNOW")) {
//							 statusList = new ArrayList<>();
//							Collections.addAll(statusList, "INVITATIONSENT","ITR","EPFO","DIGILOCKER","RELATIVEADDRESS");
//						}else {
//							 statusList = new ArrayList<>();
//							Collections.addAll(statusList, reportSearchDto.getStatusCode());
//						}
//						squery.setParameter(4, statusList);
//						if(reportSearchDto.getAgentIds()!=null && !reportSearchDto.getAgentIds().isEmpty()) {
//							squery.setParameter(5, reportSearchDto.getAgentIds());
//						}
					resultList = squery.getResultList();
					if (resultList != null && resultList.size() > 0) {
						for (Object[] result : resultList) {
							vendorChecksDto = new vendorChecksDto();
							vendorChecksDto.setVendor_check_id(String.valueOf(result[0]));
//								vendorChecksDto.setCandidate_id(result[1]);
//								vendorChecksDto.setVendor_check_id(String.valueOf(result[2]));
							vendorChecksDto.setCreated_at(String.valueOf(result[3]));
							vendorChecksDto.setEmail_id(String.valueOf(result[4]));
//								vendorChecksDto.setExpires_on(String.valueOf(result[5]) ? 1:0);
//								vendorChecksDto.setTat(null);
							vendorChecksDto.setVendor_id(String.valueOf(result[7]));
							vendorChecksDto.setSource_id(String.valueOf(result[8]));
							vendorChecksDto.setIs_proof_uploaded(String.valueOf(result[9]));
//								vendorChecksDto.setVendor_check_id(String.valueOf(result[10]));
							vendorChecksDto.setAddress(String.valueOf(result[11]));
							vendorChecksDto.setAlternate_contact_no(String.valueOf(result[12]));
							vendorChecksDto.setCandidate_name(String.valueOf(result[13]));
							vendorChecksDto.setContact_no(String.valueOf(result[14]));
							vendorChecksDto.setDate_of_birth(String.valueOf(result[15]));
							vendorChecksDto.setDocument_name(String.valueOf(result[16]));
							vendorChecksDto.setFather_name(String.valueOf(result[17]));
							vendorChecksDto.setType_of_panel(String.valueOf(result[18]));
							vendorChecksDto.setVendor_checkstatus_master_id(String.valueOf(result[19]));
							vendorDetailsDtoList.add(vendorChecksDto);

//								candidateDto=new CandidateDetailsForReport();
//								candidateDto.setCreatedByUserFirstName(String.valueOf(result[0]));
//								candidateDto.setCreatedByUserLastName(String.valueOf(result[2]));
//								candidateDto.setCandidateName(String.valueOf(result[3]));
//								candidateDto.setContactNumber(String.valueOf(result[4]));
//								candidateDto.setEmailId(String.valueOf(result[5]));
//								candidateDto.setPanNumber(String.valueOf(result[6]));
//								candidateDto.setApplicantId(String.valueOf(result[7]));
//								candidateDto.setCandidateCode(String.valueOf(result[8]));
//								candidateDto.setDateOfEmailInvite(String.valueOf(result[9]));
//								candidateDto.setCreatedOn(String.valueOf(result[10]));
//								candidateDto.setExperienceInMonth(Integer.valueOf(String.valueOf(result[11])));
//								candidateDto.setCurrentStatusDate(String.valueOf(result[12]));
//								candidateDto.setColorName(result[13]!=null?colorRepository.findById(Long.valueOf(String.valueOf(result[13]))).get().getColorName():"NA");
//								candidateDto.setStatusName(String.valueOf(result[14]));
//								candidateDto.setNumberofexpiredCount(Integer.valueOf(String.valueOf(result[15])));
//								candidateDto.setReinviteCount(Integer.valueOf(String.valueOf(result[16])));
//								candidateDto.setStatusDate(String.valueOf(result[17]));
//								candidateDetailsDtoList.add(candidateDto);
						}
					}
				}
			}
			VendorSearchDto reportSearchDtoObj = new VendorSearchDto();
			reportSearchDtoObj.setFromDate(reportSearchDto.getFromDate());
			reportSearchDtoObj.setToDate(reportSearchDto.getToDate());
			reportSearchDtoObj.setStatusCode(reportSearchDto.getStatusCode());
			reportSearchDtoObj.setOrganizationIds(reportSearchDto.getOrganizationIds());
			if (reportSearchDto.getOrganizationIds() != null && reportSearchDto.getOrganizationIds().get(0) != 0) {
				reportSearchDtoObj.setOrganizationName(organizationRepository
						.findById(reportSearchDto.getOrganizationIds().get(0)).get().getOrganizationName());
			}
			List<vendorChecksDto> sortedList = vendorDetailsDtoList.stream()
					.sorted((o1, o2) -> o1.getVendor_check_id().compareTo(o2.getVendor_check_id()))
					.collect(Collectors.toList());
			reportSearchDtoObj.setCandidateDetailsDto(sortedList);
			svcSearchResult.setData(reportSearchDtoObj);
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage("SUCCESS");
		} catch (Exception ex) {
			log.error("Exception occured in getVendorDetailsByStatus method in ReportServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	boolean containsObj(List<CandidateCafExperienceDto> list, String name) {
		return list.stream().anyMatch(p -> p.getCandidateEmployerName().equalsIgnoreCase(name) && !p.getInputDateOfExit().equalsIgnoreCase("NOT_AVAILABLE"));
	}
	
	boolean containsITR(List<CandidateCafExperienceDto> list, String name) {
		return list.stream().anyMatch(p -> p.getCandidateEmployerName().equalsIgnoreCase(name));
	}

	@Override
	public ServiceOutcome<ReportSearchDto> getVendorUtilizationReportData(ReportSearchDto reportSearchDto) {
		ServiceOutcome<ReportSearchDto> svcSearchResult = new ServiceOutcome<ReportSearchDto>();
		List<Object[]> resultList = null;
		User user = SecurityHelper.getCurrentUser();
		String strToDate = "";
		String strFromDate = "";
		List<Long> orgIds = new ArrayList<Long>();
		List<Long> agentIds = new ArrayList<Long>();
		ReportSearchDto reportSearchDtoObj = null;
		CandidateDetailsForReport candidateDto = null;
		ReportResponseDto reportResponseDto_vendor = null;
		try {
			if (reportSearchDto == null) {
				strToDate = ApplicationDateUtils.getStringTodayAsDDMMYYYY();
				strFromDate = ApplicationDateUtils
						.subtractNoOfDaysFromDateAsDDMMYYYY(new SimpleDateFormat("dd/MM/yyyy").parse(strToDate), 30);
//				if(user.getRole().getRoleCode().equals("ROLE_CBADMIN")) {
//					orgIds.add(0, 0l);
//				}else {
//					Long orgIdLong = user.getOrganization().getOrganizationId();
//					orgIds.add(orgIdLong);
//					reportSearchDto=new ReportSearchDto();
//					reportSearchDto.setOrganizationIds(orgIds);
//				}
			} else {
				strToDate = reportSearchDto.getToDate();
				strFromDate = reportSearchDto.getFromDate();
				orgIds.addAll(reportSearchDto.getOrganizationIds());
				if (reportSearchDto.getAgentIds() != null) {
					agentIds.addAll(reportSearchDto.getAgentIds());
				} else {

				}

			}
			Date startDate = format.parse(strFromDate + " 00:00:00");
			Date endDate = format.parse(strToDate + " 23:59:59");
			StringBuilder query = new StringBuilder();
			if (reportSearchDto != null && reportSearchDto.getOrganizationIds() != null
					&& reportSearchDto.getOrganizationIds().size() > 0
					&& reportSearchDto.getOrganizationIds().get(0) != 0l) {
				query.append(
						"select COUNT( DISTINCT CASE WHEN vc.vendor_checkstatus_master_id = 1 and vc.created_at between ?1 and  ?2 THEN vc.vendor_check_id END) AS clearcount, COUNT( DISTINCT CASE WHEN vc.vendor_checkstatus_master_id = 2 and vc.created_at between ?1 and  ?2 THEN vc.vendor_check_id END) AS inprogresscount, COUNT( DISTINCT CASE WHEN vc.vendor_checkstatus_master_id = 3 and vc.created_at between ?1 and  ?2 THEN vc.vendor_check_id END) AS insufficientstatuscount, COUNT( DISTINCT CASE WHEN vc.vendor_checkstatus_master_id = 4 and vc.created_at between ?1 and  ?2 THEN vc.vendor_check_id END) AS majordiscrepancystatuscount, COUNT( DISTINCT CASE WHEN vc.vendor_checkstatus_master_id = 5 and vc.created_at between ?1 and  ?2 THEN vc.vendor_check_id END) AS minordiscrepencystatuscount, COUNT( DISTINCT CASE WHEN vc.vendor_checkstatus_master_id = 6 and vc.created_at between ?1 and  ?2 THEN vc.vendor_check_id END) AS unabletoverifystatuscount, org.organization_name as name, org.organization_id as orgId from t_dgv_role_master rm, t_dgv_user_master um, t_dgv_vendor_master_new vm, t_dgv_vendor_checks vc, t_dgv_organization_master org where org.is_active = 1 and org.organization_id = um.orgainzation_id and um.user_id = vm.user_id and vm.vendor_id = vc.vendor_id and org.organization_id in (?3);");
//				System.out.println(query);
				Query squery = entityManager.createNativeQuery(query.toString());
				squery.setParameter(1, startDate);
				squery.setParameter(2, endDate);
				squery.setParameter(3, reportSearchDto.getOrganizationIds());
//					if(user.getRole().getRoleCode().equals("ROLE_AGENTSUPERVISOR")) {
//						List<User> agentList=userRepository.findAllByAgentSupervisorUserId(user.getUserId());
//						if(!agentList.isEmpty()) {
//							List<Long> agentIdsList = agentList.parallelStream().map(x -> x.getUserId()).collect(Collectors.toList());
//							agentIdsList.add(user.getUserId());
//							reportSearchDto.setAgentIds(agentIdsList);
//							squery.setParameter(4, reportSearchDto.getAgentIds());
//						}
//					}
//					if(user.getRole().getRoleCode().equals("ROLE_AGENTHR")) {
//							List<Long> agentIdsList = new ArrayList<>();
//							agentIdsList.add(user.getUserId());
//							reportSearchDto.setAgentIds(agentIdsList);
//							squery.setParameter(4, reportSearchDto.getAgentIds());
//					}
				resultList = squery.getResultList();
			} else {
				query.append(
						"select COUNT( DISTINCT CASE WHEN vc.vendor_checkstatus_master_id = 1 and vc.created_at between ?1 and  ?2 THEN vc.vendor_check_id END) AS clearcount, COUNT( DISTINCT CASE WHEN vc.vendor_checkstatus_master_id = 2 and vc.created_at between ?1 and  ?2 THEN vc.vendor_check_id END) AS inprogresscount, COUNT( DISTINCT CASE WHEN vc.vendor_checkstatus_master_id = 3 and vc.created_at between ?1 and  ?2 THEN vc.vendor_check_id END) AS insufficientstatuscount, COUNT( DISTINCT CASE WHEN vc.vendor_checkstatus_master_id = 4 and vc.created_at between ?1 and  ?2 THEN vc.vendor_check_id END) AS majordiscrepancystatuscount, COUNT( DISTINCT CASE WHEN vc.vendor_checkstatus_master_id = 5 and vc.created_at between ?1 and  ?2 THEN vc.vendor_check_id END) AS minordiscrepencystatuscount, COUNT( DISTINCT CASE WHEN vc.vendor_checkstatus_master_id = 6 and vc.created_at between ?1 and  ?2 THEN vc.vendor_check_id END) AS unabletoverifystatuscount, org.organization_name as name, org.organization_id as orgId from t_dgv_role_master rm, t_dgv_user_master um, t_dgv_vendor_master_new vm, t_dgv_vendor_checks vc, t_dgv_organization_master org where org.is_active = 1 and org.organization_id = um.orgainzation_id and um.user_id = vm.user_id and vm.vendor_id = vc.vendor_id;");
				Query squery = entityManager.createNativeQuery(query.toString());
//				squery.setParameter(1, null);
				squery.setParameter(1, startDate);
				squery.setParameter(2, endDate);
				resultList = squery.getResultList();
			}
			if (resultList != null && resultList.size() > 0) {
				List<ReportResponseDto> pwdvMprReportDtoList = new ArrayList<ReportResponseDto>();
				for (Object[] result : resultList) {

					ReportResponseDto reportResponseDto = new ReportResponseDto(Long.valueOf(String.valueOf(result[0])),
							String.valueOf(result[6]), Integer.valueOf(String.valueOf(result[0])), "NEWUPLOAD",
							Integer.valueOf(String.valueOf(result[1])), "REINVITE",
							Integer.valueOf(String.valueOf(result[2])), "FINALREPORT", 0, "PENDINGAPPROVAL",
							Integer.valueOf(String.valueOf(result[3])), "PENDINGNOW",
							Integer.valueOf(String.valueOf(result[4])), "PROCESSDECLINED",
							Integer.valueOf(String.valueOf(result[5])), "INVITATIONEXPIRED", 0);
					pwdvMprReportDtoList.add(reportResponseDto);
				}
				ReportResponseDto reportResponseDtoTotal = new ReportResponseDto(0l, "TOTAL",
						pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getNewuploadcount()).sum(),
						"NEWUPLOADTOTAL", pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getReinvitecount()).sum(),
						"REINVITETOTAL",
						pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getFinalreportCount()).sum(),
						"FINALREPORTTOTAL",
						pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getInterimReportCount()).sum(),
						"PENDINGAPPROVALTOTAL",
						pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getPendingCount()).sum(), "PENDINGNOWTOTAL",
						pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getProcessDeclinedCount()).sum(),
						"PROCESSDECLINEDTOTAL",
						pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getInvitationExpireCount()).sum(),
						"INVITATIONEXPIREDTOTAL",
						pwdvMprReportDtoList.stream().mapToInt(pojo -> pojo.getAgentCount()).sum());
				pwdvMprReportDtoList.add(reportResponseDtoTotal);
				reportSearchDtoObj = new ReportSearchDto(strFromDate, strToDate, orgIds, pwdvMprReportDtoList,
						agentIds);

				svcSearchResult.setData(reportSearchDtoObj);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("Vendor Utilization Report Data generated...");
			} else {
				reportSearchDtoObj = new ReportSearchDto(strFromDate, strToDate, orgIds, null, null);
				svcSearchResult.setData(reportSearchDtoObj);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NO RECORD FOUND");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getVendorUtilizationReportData method in ReportServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	public ServiceOutcome<List<VendorUtilizationReportDto>> getVendorDetailsByDateRange(DateRange dateRange) {
		ServiceOutcome<List<VendorUtilizationReportDto>> svcSearchResult = new ServiceOutcome<List<VendorUtilizationReportDto>>();
		List<VendorUtilizationReportDto> vendorUtilizationReports = new ArrayList<>();
		try {
			String strToDate = "";
			String strFromDate = "";

			strToDate = dateRange.getToDate() != null ? dateRange.getToDate()
					: ApplicationDateUtils.getStringTodayAsDDMMYYYY();
			strFromDate = dateRange.getFromDate() != null ? dateRange.getFromDate()
					: ApplicationDateUtils
							.subtractNoOfDaysFromDateAsDDMMYYYY(new SimpleDateFormat("dd/MM/yyyy").parse(strToDate), 7);
			Date startDate = formatter.parse(strFromDate + " 00:00:00");
			Date endDate = formatter.parse(strToDate + " 23:59:59");
			List<VendorChecks> vendorChecks = vendorChecksRepository.getByDateRange(startDate, endDate);

			vendorChecks.forEach(temp -> log.info("Vendor check {}", temp));
			vendorChecks.forEach(temp -> {
				VendorUtilizationReportDto vendorUtilizationReportDto = new VendorUtilizationReportDto();
				if (temp.getCandidateName() != null) {
					vendorUtilizationReportDto.setCandidateName(temp.getCandidateName());
					vendorUtilizationReports.add(vendorUtilizationReportDto);
				}

			});
			svcSearchResult.setData(vendorUtilizationReports);
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage("Success");
		} catch (Exception e) {
			// TODO: handle exception
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}
	
	private void updateCandidateOverridedVerificationStatus(CandidateReportDTO candidateReportDTO, String overrideReportStatus) {
		CandidateVerificationState updateVerificationStatus = candidateVerificationStateRepository.findByCandidateCandidateId(candidateReportDTO.getCandidateId());
		if (overrideReportStatus.equalsIgnoreCase("RED")) {
//			log.info("updateCandidateOverridedVerificationStatus entry if------------");
			if(candidateReportDTO.getReportType().label.equals("Pre Offer")) {
				updateVerificationStatus.setPreApprovalColorCodeStatus(colorRepository.findByColorCode("RED"));
			}
			else if(candidateReportDTO.getReportType().label.equals("Interim")) {
				updateVerificationStatus.setInterimColorCodeStatus(colorRepository.findByColorCode("RED"));
				}
			else {	
				updateVerificationStatus.setFinalColorCodeStatus(colorRepository.findByColorCode("RED"));
				}		
				candidateReportDTO.setVerificationStatus(VerificationStatus.RED);
		} else if (overrideReportStatus.equalsIgnoreCase("AMBER") || overrideReportStatus.equalsIgnoreCase("MOONLIGHTING")) {
			if(candidateReportDTO.getReportType().label.equals("Pre Offer")) {
				updateVerificationStatus.setPreApprovalColorCodeStatus(colorRepository.findByColorCode("AMBER"));				
			}
			else if(candidateReportDTO.getReportType().label.equals("Interim")) {
				updateVerificationStatus.setInterimColorCodeStatus(colorRepository.findByColorCode("AMBER"));
				}
			else {
				updateVerificationStatus.setFinalColorCodeStatus(colorRepository.findByColorCode("AMBER"));
			}
			candidateReportDTO.setVerificationStatus(VerificationStatus.AMBER);
		} else {

			if(candidateReportDTO.getReportType().label.equals("Pre Offer")) {
				updateVerificationStatus.setPreApprovalColorCodeStatus(colorRepository.findByColorCode("GREEN"));				

			}
			else if(candidateReportDTO.getReportType().label.equals("Interim")) {
				updateVerificationStatus.setInterimColorCodeStatus(colorRepository.findByColorCode("GREEN"));
			}
			else {

				updateVerificationStatus.setFinalColorCodeStatus(colorRepository.findByColorCode("GREEN"));
			}
			candidateReportDTO.setVerificationStatus(VerificationStatus.GREEN);
		}
		candidateVerificationStateRepository.save(updateVerificationStatus);
	}

	@Override
	public ResponseEntity<byte[]> downloadCandidateStatusTrackerReport(ReportSearchDto reportSearchDto) {
		try {
			log.info("Downloading the candidate status tracker for ORG IDs::{}", reportSearchDto.getOrganizationIds());
			
			String strToDate = "";
			String strFromDate = "";

			strToDate = reportSearchDto.getToDate() != null ? reportSearchDto.getToDate()
					: ApplicationDateUtils.getStringTodayAsDDMMYYYY();
			strFromDate = reportSearchDto.getFromDate() != null ? reportSearchDto.getFromDate()
					: ApplicationDateUtils
							.subtractNoOfDaysFromDateAsDDMMYYYY(new SimpleDateFormat("dd/MM/yyyy").parse(strToDate), 7);
			Date startDate = formatter.parse(strFromDate + " 00:00:00");
			Date endDate = formatter.parse(strToDate + " 23:59:59");
			
			List<Candidate> candidatesListByCreatedOn= candidateRepository.getCandidatesByCreatedOnAndOrganization(reportSearchDto.getOrganizationIds(),startDate,endDate);
			
			//adding the interim generated candidates
			List<String> statusCodes = new ArrayList<>();
			statusCodes.addAll(statusMasterRepository.findAll().parallelStream().map(x -> x.getStatusCode()).toList());
			List<StatusMaster> statusMasterList = statusMasterRepository.findByStatusCodeIn(statusCodes);
			List<Long> statusIds = statusMasterList.stream().map(x -> x.getStatusMasterId()).toList();
//			StatusMaster statusMaster = statusMasterRepository.findByStatusCode("INTERIMREPORT");
//			List<Long> statusIds =new ArrayList<>();
//			
//			statusIds.add(statusMaster.getStatusMasterId());
			List<Candidate> interimCandidatesList= candidateRepository.getCandidateListByOrganizationIdAndStatusAndLastUpdated(reportSearchDto.getOrganizationIds().get(0)
					,statusIds,startDate,endDate);
			
			List<Candidate> mergedList = new ArrayList<>();
	        mergedList.addAll(candidatesListByCreatedOn);
	        mergedList.addAll(interimCandidatesList);
			
	        List<Candidate> uniqueCandidates =mergedList.stream().distinct().collect(Collectors.toList());
	        log.info("Downloading the No of candidates status tracker::{}", uniqueCandidates.size());
			//
			return excelUtil.downloadCandidateStatusTrackerExcel(uniqueCandidates);
			
		} catch (Exception e) {
			log.error("Exception occured in downloadCandidateStatusTrackerReport method in ReportServiceImpl-->", e);
		}
		return null;
	}

	@Override
	public ResponseEntity<byte[]> downloadCandidateEmploymentReport(ReportSearchDto reportSearchDto) {
		try {
			log.info("downloadCandidateEmploymentReport for ORG IDs::{}", reportSearchDto.getOrganizationIds());
			
			String strToDate = "";
			String strFromDate = "";

			strToDate = reportSearchDto.getToDate() != null ? reportSearchDto.getToDate()
					: ApplicationDateUtils.getStringTodayAsDDMMYYYY();
			strFromDate = reportSearchDto.getFromDate() != null ? reportSearchDto.getFromDate()
					: ApplicationDateUtils
							.subtractNoOfDaysFromDateAsDDMMYYYY(new SimpleDateFormat("dd/MM/yyyy").parse(strToDate), 7);
			Date startDate = formatter.parse(strFromDate + " 00:00:00");
			Date endDate = formatter.parse(strToDate + " 23:59:59");
			
			User currentUser = SecurityHelper.getCurrentUser();
			String user = currentUser.getUserFirstName();
			
			List<Candidate> candidatesListByCreatedOn= candidateRepository.getCandidatesByCreatedOnAndOrganization(reportSearchDto.getOrganizationIds(),startDate,endDate);
			List<UanSearchData> uanSearchFilterData = uanSearchDataRepository.uanSearchDashboardFilter(startDate,
					endDate, user);
			
			log.info("uanSearchFilterData before filter::{}",uanSearchFilterData.size());
			//filtering only failes response of UAN for FAILED_SHEET
			List<UanSearchData> failesUanFetched = new ArrayList<>();
			for (UanSearchData uanSearchData : uanSearchFilterData) {
				if(uanSearchData.getUan()!=null){
					String epsoResp = uanSearchData.getEPFOResponse();
					JSONObject epfoObj=null;
					 try {
				            // Try to create a JSON object from the string
						 epfoObj= new JSONObject(new JSONTokener(epsoResp));
						 
						 if(epfoObj.has("message") && epfoObj.get("message") instanceof JSONArray) {
							 log.info("VALID JSON-->::{}");
						 }else {
							 failesUanFetched.add(uanSearchData);
						 }
				        } catch (Exception e) {
				            // If an exception is thrown, it's not a valid JSON string
				        	log.error("NOT VALID JSON-->::{}",epsoResp);
				        	failesUanFetched.add(uanSearchData);
				        }
					 
				}else {
					failesUanFetched.add(uanSearchData);
				}
				
			}
			
			//get epfo data for each success candidates
			List<Candidate> successCandidates = new ArrayList<>();
			List<EpfoData> successCandidatesEpfoList = new ArrayList<>();
			for (Candidate candidate : candidatesListByCreatedOn) {
				if(candidate.getUan()!=null) {
					List<EpfoData> candEpfoList = epfoDataRepository.findAllByCandidateCandidateCode(candidate.getCandidateCode());
					
					// Check if EPFO data is present
				    if (candEpfoList!=null && !candEpfoList.isEmpty()) {
				    	successCandidates.add(candidate);
				    	successCandidatesEpfoList.addAll(candEpfoList);
				    }
				}
			}
			log.info("failesUanFetched after filter::{}",failesUanFetched.size());
			log.info("successCandidates success fetched::{}",successCandidates.size());
			
			return excelUtil.downloadCandidateEmploymentReportExcel(failesUanFetched,successCandidates, successCandidatesEpfoList);
		} catch (Exception e) {
			log.error("Exception occured in downloadCandidateEmploymentReport method in ReportServiceImpl-->", e);
		}
		return null;
	}
}
