package com.aashdit.digiverifier.config.candidate.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId; 
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.generic.DateTool;
import org.joda.time.DateTime;
import org.joda.time.Interval; 
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.aashdit.digiverifier.client.securityDetails.EPFOSecurityConfig;
import com.aashdit.digiverifier.common.ContentRepository;
import com.aashdit.digiverifier.common.dto.ContentDTO;
import com.aashdit.digiverifier.common.dto.EpfoItrResponseDTO;
import com.aashdit.digiverifier.common.enums.ContentCategory;
import com.aashdit.digiverifier.common.enums.ContentSubCategory;
import com.aashdit.digiverifier.common.enums.ContentType;
import com.aashdit.digiverifier.common.enums.FileType;
import com.aashdit.digiverifier.common.model.Content;
import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.common.service.ContentService;
import com.aashdit.digiverifier.common.util.RandomString;
import com.aashdit.digiverifier.config.admin.dto.UserDto;
import com.aashdit.digiverifier.config.admin.dto.VendorUploadChecksDto;
import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.admin.model.VendorChecks;
import com.aashdit.digiverifier.config.admin.model.VendorUploadChecks;
import com.aashdit.digiverifier.config.admin.repository.RoleRepository;
import com.aashdit.digiverifier.config.admin.repository.UserRepository;
import com.aashdit.digiverifier.config.admin.repository.VendorChecksRepository;
import com.aashdit.digiverifier.config.admin.repository.VendorUploadChecksRepository;
import com.aashdit.digiverifier.config.candidate.dto.ApprovalStatusRemarkDto;
import com.aashdit.digiverifier.config.candidate.dto.BulkUanDTO;
import com.aashdit.digiverifier.config.candidate.dto.CandidateCafAddressDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateCafEducationDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateCafExperienceDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateCafRelationshipDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateCaseDetailsDTO;
import com.aashdit.digiverifier.config.candidate.dto.CandidateDetailsDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateFileDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateInvitationSentDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateStatusCountDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidationApplicationFormDto;
import com.aashdit.digiverifier.config.candidate.dto.ContentFileDto;
import com.aashdit.digiverifier.config.candidate.dto.EmploymentDetailsDto;
import com.aashdit.digiverifier.config.candidate.dto.EmploymentTenureVerificationDto;
import com.aashdit.digiverifier.config.candidate.dto.ExecutiveSummaryDto;
import com.aashdit.digiverifier.config.candidate.dto.IdItemsDto;
import com.aashdit.digiverifier.config.candidate.dto.SuspectEmpMasterDto;
import com.aashdit.digiverifier.config.candidate.dto.UanSearchDataDTO;
import com.aashdit.digiverifier.config.candidate.dto.UanSearchEpfoDTO;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.CandidateAddComments;
import com.aashdit.digiverifier.config.candidate.model.CandidateAdressVerification;
import com.aashdit.digiverifier.config.candidate.model.CandidateCafAddress;
import com.aashdit.digiverifier.config.candidate.model.CandidateCafEducation;
import com.aashdit.digiverifier.config.candidate.model.CandidateCafExperience;
import com.aashdit.digiverifier.config.candidate.model.CandidateCafRelationship;
import com.aashdit.digiverifier.config.candidate.model.CandidateCaseDetails;
import com.aashdit.digiverifier.config.candidate.model.CandidateEmailStatus;
import com.aashdit.digiverifier.config.candidate.model.CandidateIdItems;
import com.aashdit.digiverifier.config.candidate.model.CandidateResumeUpload;
import com.aashdit.digiverifier.config.candidate.model.CandidateSampleCsvXlsMaster;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatusHistory;
import com.aashdit.digiverifier.config.candidate.model.CandidateVerificationState;
import com.aashdit.digiverifier.config.candidate.model.OrganisationScope;
import com.aashdit.digiverifier.config.candidate.model.QualificationMaster;
import com.aashdit.digiverifier.config.candidate.model.RemarkMaster;
import com.aashdit.digiverifier.config.candidate.model.StatusMaster;
import com.aashdit.digiverifier.config.candidate.model.SuspectClgMaster;
import com.aashdit.digiverifier.config.candidate.model.SuspectEmpMaster;
import com.aashdit.digiverifier.config.candidate.model.UanSearchData;
import com.aashdit.digiverifier.config.candidate.repository.CandidateAddCommentRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateAdressVerificationRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCafAddressRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCafEducationRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCafExperienceRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCafRelationshipRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCaseDetailsRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateEmailStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateIdItemsRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateResumeUploadRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateSampleCsvXlsMasterRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusHistoryRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateVerificationStateRepository;
import com.aashdit.digiverifier.config.candidate.repository.OrganisationScopeRepository;
import com.aashdit.digiverifier.config.candidate.repository.QualificationMasterRepository;
import com.aashdit.digiverifier.config.candidate.repository.RemarkMasterRepository;
import com.aashdit.digiverifier.config.candidate.repository.StatusMasterRepository;
import com.aashdit.digiverifier.config.candidate.repository.SuspectClgMasterRepository;
import com.aashdit.digiverifier.config.candidate.repository.SuspectEmpMasterRepository;
import com.aashdit.digiverifier.config.candidate.repository.UanSearchDataRepository;
import com.aashdit.digiverifier.config.candidate.util.CSVUtil;
import com.aashdit.digiverifier.config.candidate.util.ExcelUtil;
import com.aashdit.digiverifier.config.superadmin.Enum.ReportType;
import com.aashdit.digiverifier.config.superadmin.Enum.VerificationStatus;
import com.aashdit.digiverifier.config.superadmin.dto.DashboardDto;
import com.aashdit.digiverifier.config.superadmin.model.Color;
import com.aashdit.digiverifier.config.superadmin.model.Organization;
import com.aashdit.digiverifier.config.superadmin.repository.ColorRepository;
import com.aashdit.digiverifier.config.superadmin.repository.OrganizationRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ServiceSourceMasterRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ServiceTypeConfigRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ToleranceConfigRepository;
import com.aashdit.digiverifier.config.superadmin.service.ReportService;
import com.aashdit.digiverifier.constants.EPFOConstants;
import com.aashdit.digiverifier.epfo.dto.EpfoDataFromDetailsDto;
import com.aashdit.digiverifier.epfo.dto.EpfoDetailsDto;
import com.aashdit.digiverifier.epfo.model.EpfoData;
import com.aashdit.digiverifier.epfo.repository.CandidateEPFOResponseRepository;
import com.aashdit.digiverifier.epfo.repository.EpfoDataRepository;
import com.aashdit.digiverifier.epfo.service.EpfoServiceImpl;
import com.aashdit.digiverifier.itr.dto.ITRDataFromApiDto;
import com.aashdit.digiverifier.itr.model.ITRData;
import com.aashdit.digiverifier.itr.repository.CanditateItrEpfoResponseRepository;
import com.aashdit.digiverifier.itr.repository.ITRDataRepository;
import com.aashdit.digiverifier.utils.ApplicationDateUtils;
import com.aashdit.digiverifier.utils.CommonValidation;
import com.aashdit.digiverifier.utils.DateUtil;
import com.aashdit.digiverifier.utils.EmailSentTask;
import com.aashdit.digiverifier.utils.FileUtil;
import com.aashdit.digiverifier.utils.SecurityHelper; 
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CandidateServiceImpl implements CandidateService, MessageSourceAware {

	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
	private MessageSource messageSource;

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Autowired
	@Lazy
	private CandidateEPFOResponseRepository candidateEPFOResponseRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CandidateAddCommentRepository candidateAddCommentRepository;

	@Autowired
	private CandidateRepository candidateRepository;

	@Autowired
	private VendorChecksRepository vendorChecksRepository;

	@Autowired
	private VendorUploadChecksRepository vendorUploadChecksRepository;

	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private CSVUtil cSVUtil;

	@Autowired
	private ExcelUtil excelUtil;

	@Autowired
	private CandidateStatusRepository candidateStatusRepository;

	@Autowired
	private CandidateStatusHistoryRepository candidateStatusHistoryRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private EmailSentTask emailSentTask;

	@Autowired
	private CommonValidation commonValidation;

	@Autowired
	private CandidateSampleCsvXlsMasterRepository candidateSampleCsvXlsMasterRepository;

	@Autowired
	private StatusMasterRepository statusMasterRepository;

	@Autowired
	private CandidateEmailStatusRepository candidateEmailStatusRepository;

	@Autowired
	private RemarkMasterRepository remarkMasterRepository;

	@Autowired
	private SuspectEmpMasterRepository suspectEmpMasterRepository;

	@Autowired
	private SuspectClgMasterRepository suspectClgMasterRepository;

	@Autowired
	private CandidateCafEducationRepository candidateCafEducationRepository;

	@Autowired
	private QualificationMasterRepository qualificationMasterRepository;

	@Autowired
	private CandidateCafExperienceRepository candidateCafExperienceRepository;

	@Autowired
	private CandidateCafAddressRepository candidateCafAddressRepository;

	@Autowired
	private CandidateResumeUploadRepository candidateResumeUploadRepository;

	@Autowired
	private ColorRepository colorRepository;

	@Autowired
	private CandidateCaseDetailsRepository candidateCaseDetailsRepository;

	@Autowired
	private ITRDataRepository itrDataRepository;

	@Autowired
	private CandidateCafRelationshipRepository candidateCafRelationshipRepository;

	@Autowired
	private CandidateAdressVerificationRepository candidateAdressVerificationRepository;

	@Autowired
	private CandidateIdItemsRepository candidateIdItemsRepository;

	@Autowired
	private ServiceTypeConfigRepository serviceTypeConfigRepository;

	@Autowired
	private ToleranceConfigRepository toleranceConfigRepository;

	@Autowired
	private CandidateService candidateService;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private EpfoDataRepository epfoDataRepository;

	@Autowired
	private CanditateItrEpfoResponseRepository canditateItrEpfoResponseRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ServiceSourceMasterRepository serviceSourceMasterRepository;

	@Autowired
	private CandidateVerificationStateRepository candidateVerificationStateRepository;

	@Autowired
	@Lazy
	private ReportService reportService;

	@Autowired
	private ContentService contentService;

	@Autowired
	private OrganisationScopeRepository organisationScopeRepository;

	@Autowired
	private UanSearchDataRepository uanSearchDataRepository;

	@Autowired
	private EPFOSecurityConfig epfoSecurityConfig;

	@Autowired
	private EpfoServiceImpl epfoServiceImpl;

	public ServiceOutcome<Boolean> updateCandidateOrganisationScope(OrganisationScope organisationScope) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		OrganisationScope result = null;
		// CandidateCafExperience candidateCafExperience=null;
		User user = SecurityHelper.getCurrentUser();
		try {
			if (organisationScope.getOrgScopeId() != null) {
				Optional<OrganisationScope> orgScope = organisationScopeRepository
						.findById(organisationScope.getOrgScopeId());
				organisationScope.setOrgScopeId(orgScope.get().getOrgScopeId());
				result = organisationScopeRepository.save(organisationScope);
			} else {
				result = organisationScopeRepository.save(organisationScope);
			}

			if (result != null) {
				svcSearchResult.setMessage("Comment Update successfully.");

			} else {
				System.out.println("-------------candidate-----else------");
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult
						.setMessage(messageSource.getMessage("msg.error", null, LocaleContextHolder.getLocale()));
			}
		} catch (Exception e) {
			log.error("Exception occured in updateCandidateVendorProofColor method in CandidateServiceImpl-->", e);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Transactional
	@Override
	public ServiceOutcome<List> saveCandidateInformation(MultipartFile file) {
		ServiceOutcome<List> svcSearchResult = new ServiceOutcome<List>();
		try {
			User user = SecurityHelper.getCurrentUser();
			RandomString rd = null;
			List<Candidate> candidates = null;
			List<CandidateStatus> candidateStatusList = new ArrayList<CandidateStatus>();
			CandidateSampleCsvXlsMaster candidateSampleCsvXlsMaster = null;
			if (CSVUtil.hasCSVFormat(file)) {
				candidates = cSVUtil.csvToCandidateList(file.getInputStream());
				candidateSampleCsvXlsMaster = new CandidateSampleCsvXlsMaster();
				candidateSampleCsvXlsMaster.setCandidateSampleCsv(file.getBytes());
			}
			if (ExcelUtil.hasExcelFormat(file)) {
				candidates = excelUtil.excelToCandidate(file.getInputStream());
				candidateSampleCsvXlsMaster = new CandidateSampleCsvXlsMaster();
				candidateSampleCsvXlsMaster.setCandidateSampleXls(file.getBytes());
			}
			for (Candidate candidate : candidates) {
				candidate.setOrganization(
						organizationRepository.findById(user.getOrganization().getOrganizationId()).get());
				rd = new RandomString(12);
				Candidate findByCandidateCode = candidateRepository.findByCandidateCode(rd.nextString());
				if (findByCandidateCode != null) {
					rd = new RandomString(12);
					candidate.setCandidateCode(rd.nextString());
				} else {
					candidate.setCandidateCode(rd.nextString());
				}
				candidate.setIsLoaAccepted(false);
				candidate.setApprovalRequired(false);
				candidate.setIsActive(true);
				candidate.setCreatedOn(new Date());
				candidate.setCreatedBy(user);
			}
			List<Candidate> candidateList = candidateRepository.saveAllAndFlush(candidates);
			if (!candidateList.isEmpty()) {
				candidateSampleCsvXlsMaster.setOrganization(
						organizationRepository.findById(user.getOrganization().getOrganizationId()).get());
				candidateSampleCsvXlsMaster.setUploadedTimestamp(new Date());
				candidateSampleCsvXlsMaster.setCreatedBy(user);
				candidateSampleCsvXlsMaster.setCreatedOn(new Date());
				CandidateSampleCsvXlsMaster result = candidateSampleCsvXlsMasterRepository
						.save(candidateSampleCsvXlsMaster);
				candidateList.forEach(candidateOBJ -> candidateOBJ.setCandidateSampleId(result));
				candidateRepository.saveAllAndFlush(candidateList);
				for (Candidate candidate : candidateList) {
					CandidateStatus candidateStatus = new CandidateStatus();
					candidateStatus.setCandidate(candidate);
					candidateStatus.setCreatedBy(user);
					candidateStatus.setCreatedOn(new Date());
					if (!candidate.getCcEmailId().isEmpty()) {
						if (commonValidation.validationEmail(candidate.getEmailId())) {
							candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("NEWUPLOAD"));
						} else {
							candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("INVALIDUPLOAD"));
							candidateStatus.setLastUpdatedOn(new Date());
							candidateStatus.setLastUpdatedBy(user);
						}

					} else {
						if (commonValidation.validationEmail(candidate.getEmailId())) {
							candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("NEWUPLOAD"));
						} else {
							candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("INVALIDUPLOAD"));
							candidateStatus.setLastUpdatedOn(new Date());
							candidateStatus.setLastUpdatedBy(user);
						}
					}
					candidateStatus = candidateStatusRepository.save(candidateStatus);
					candidateStatusList.add(candidateStatus);
					createCandidateStatusHistory(candidateStatus, "NOTCANDIDATE");

				}
				List<String> referenceList = candidateStatusList.stream()
						.filter(c -> c.getStatusMaster().getStatusCode().equals("NEWUPLOAD"))
						.map(x -> x.getCandidate().getCandidateCode()).collect(Collectors.toList());
				CandidateInvitationSentDto candidateInvitationSentDto = new CandidateInvitationSentDto();
				candidateInvitationSentDto.setCandidateReferenceNo(referenceList);
				System.out.println(referenceList + "referenceList");
				candidateInvitationSentDto.setStatuscode("INVITATIONSENT");
				ServiceOutcome<Boolean> svcOutcome = candidateService.invitationSent(candidateInvitationSentDto);
				if (svcOutcome.getOutcome()) {
					svcSearchResult.setData(referenceList);
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage("File Uploaded Successfully");
				} else {
					svcSearchResult.setData(referenceList);
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage(svcOutcome.getMessage());
				}
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("File Could not be Uploaded.");
			}
		} catch (IOException e) {
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("File Could not be Uploaded.");
			log.error("Exception occured in saveCandidateInformation method in CandidateServiceImpl-->" + e);
			throw new RuntimeException("fail to store csv/xls data: " + e.getMessage());
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<DashboardDto> getAllCandidateList(DashboardDto dashboardDto) {
		ServiceOutcome<DashboardDto> svcSearchResult = new ServiceOutcome<DashboardDto>();
		List<Candidate> candidateList = new ArrayList<Candidate>();
		List<CandidateDetailsDto> candidateDtoList = new ArrayList<CandidateDetailsDto>();
		List<String> statusCodes = new ArrayList<String>();
		List<Long> agentIds = new ArrayList<Long>();
		String strToDate = "";
		String strFromDate = "";
		try {
			if (dashboardDto.getUserId() != null && dashboardDto.getUserId() != 0l
					&& StringUtils.isNotBlank(dashboardDto.getStatus())) {
				User user = userRepository.findById(dashboardDto.getUserId()).get();
				strToDate = dashboardDto.getToDate() != null ? dashboardDto.getToDate()
						: ApplicationDateUtils.getStringTodayAsDDMMYYYY();
				strFromDate = dashboardDto.getFromDate() != null ? dashboardDto.getFromDate()
						: ApplicationDateUtils.subtractNoOfDaysFromDateAsDDMMYYYY(
								new SimpleDateFormat("dd/MM/yyyy").parse(strToDate), 7);
				Date startDate = formatter.parse(strFromDate + " 00:00:00");
				Date endDate = formatter.parse(strToDate + " 23:59:59");
				String status = dashboardDto.getStatus();
				if (status.equals("PENDINGAPPROVAL")) {
					status = "PENDINGAPPROVAL";
					statusCodes.add(0, status);

					status = "INTERIMREPORT";
					statusCodes.add(0, status);
				}
				if (status.equals("INTERIMREPORT")) {
					status = "INTERIMREPORT";
					statusCodes.add(0, status);
				} else if (status.equals("CAFPENDING")) {
					status = "RELATIVEADDRESS";
					statusCodes.add(0, status);
				} else if (status.equals("EPFOSKIPPED")) {
					Collections.addAll(statusCodes, "ITR", "DIGILOCKER");
				} else if (status.equals("NEWUPLOAD")) {
					statusCodes.addAll(statusMasterRepository.findAll().parallelStream().map(x -> x.getStatusCode())
							.collect(Collectors.toList()));
				} else {
					statusCodes.add(0, status);
				}
				List<StatusMaster> statusMasterList = statusMasterRepository.findByStatusCodeIn(statusCodes);
				List<Long> statusIds = statusMasterList.stream().map(x -> x.getStatusMasterId())
						.collect(Collectors.toList());
				if (user.getRole().getRoleCode().equalsIgnoreCase("ROLE_ADMIN")
						|| user.getRole().getRoleCode().equalsIgnoreCase("ROLE_PARTNERADMIN")) {
					candidateList = candidateRepository.getCandidateListByOrganizationIdAndStatusAndLastUpdated(
							user.getOrganization().getOrganizationId(), statusIds, startDate, endDate);
				}
				if (user.getRole().getRoleCode().equalsIgnoreCase("ROLE_AGENTSUPERVISOR")
						|| user.getRole().getRoleCode().equalsIgnoreCase("ROLE_AGENTHR")) {
					List<User> agentList = userRepository.findAllByAgentSupervisorUserId(user.getUserId());
					if (!agentList.isEmpty()) {
						agentIds = agentList.stream().map(x -> x.getUserId()).collect(Collectors.toList());
					}
					agentIds.add(user.getUserId());
					candidateList = candidateRepository.getCandidateListByUserIdAndStatusAndLastUpdated(agentIds,
							statusIds, startDate, endDate);
				}
				if (dashboardDto.getStatus().equals("EPFOSKIPPED")) {
					candidateList = candidateList.parallelStream().filter(x -> x.getIsUanSkipped())
							.collect(Collectors.toList());
				}
				if (dashboardDto.getStatus().equals("DIGILOCKER") || dashboardDto.getStatus().equals("ITR")) {
					candidateList = candidateList.parallelStream()
							.filter(x -> x.getIsUanSkipped() != null ? !x.getIsUanSkipped() : true)
							.collect(Collectors.toList());
				}
				for (Candidate candidate : candidateList) {
					CandidateDetailsDto candidateDto = this.modelMapper.map(candidate, CandidateDetailsDto.class);
					candidateDto.setCreatedOn(formatter.format(candidate.getCreatedOn()));
					candidateDto.setSubmittedOn(
							candidate.getSubmittedOn() != null ? formatter.format(candidate.getSubmittedOn()) : null);
					CandidateEmailStatus candidateEmailStatus = candidateEmailStatusRepository
							.findByCandidateCandidateCode(candidate.getCandidateCode());
					if (candidateEmailStatus != null) {
						candidateDto.setDateOfEmailInvite(candidateEmailStatus.getDateOfEmailInvite() != null
								? formatter.format(candidateEmailStatus.getDateOfEmailInvite())
								: null);
						candidateDto.setDateOfEmailFailure(candidateEmailStatus.getDateOfEmailFailure() != null
								? formatter.format(candidateEmailStatus.getDateOfEmailFailure())
								: null);
						candidateDto.setDateOfEmailExpire(candidateEmailStatus.getDateOfEmailExpire() != null
								? formatter.format(candidateEmailStatus.getDateOfEmailExpire())
								: null);
						candidateDto.setDateOfEmailReInvite(candidateEmailStatus.getDateOfEmailReInvite() != null
								? formatter.format(candidateEmailStatus.getDateOfEmailReInvite())
								: null);
					}
					CandidateStatus candidateStatus = candidateStatusRepository
							.findByCandidateCandidateCode(candidate.getCandidateCode());
					Boolean uan = candidate.getIsUanSkipped() != null ? candidate.getIsUanSkipped() : false;
					if (candidateStatus.getStatusMaster().getStatusCode().equals("DIGILOCKER") && uan
							|| candidateStatus.getStatusMaster().getStatusCode().equals("ITR") && uan) {
						candidateDto.setCandidateStatusName("EPFO Skipped");
					} else {
						candidateDto.setCandidateStatusName(candidateStatus.getStatusMaster().getStatusName());
					}
					List<ContentDTO> contentDTOList = contentService
							.getContentListByCandidateId(candidate.getCandidateId());
					System.out.println(contentDTOList + "--------contentdtolist-------");
					candidateDto.setContentDTOList(contentDTOList);

					candidateDtoList.add(candidateDto);

				}
				DashboardDto dashboardDtoObj = new DashboardDto(strFromDate, strToDate, null, null, null,
						dashboardDto.getUserId(), dashboardDto.getStatus(), candidateDtoList);
				if (!candidateDtoList.isEmpty()) {
					svcSearchResult.setData(dashboardDtoObj);
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage("Candidate list fetched successfully.");
					svcSearchResult.setStatus(status);
				} else {
					svcSearchResult.setData(null);
					svcSearchResult.setOutcome(false);
					svcSearchResult.setMessage("NO Candidate FOUND");
				}
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("please specify user.");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getAllCandidateList method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Transactional
	@Override
	public ServiceOutcome<Boolean> invitationSent(CandidateInvitationSentDto candidateInvitationSentDto) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		try {
			User user = SecurityHelper.getCurrentUser();
			CandidateStatus candidateStatus = null;
			CandidateEmailStatus candidateEmailStatus = null;
			CandidateStatus candidateStatusresult = null;
			if (candidateInvitationSentDto.getCandidateReferenceNo().size() > 0) {
				for (int i = 0; i < candidateInvitationSentDto.getCandidateReferenceNo().size(); i++) {
					candidateStatus = candidateStatusRepository
							.findByCandidateCandidateCode(candidateInvitationSentDto.getCandidateReferenceNo().get(i));
					if (candidateStatus != null) {
						Boolean result = emailSentTask.sendEmail(candidateStatus.getCandidate().getCandidateCode(),
								candidateStatus.getCandidate().getCandidateName(),
								candidateStatus.getCandidate().getEmailId(),
								candidateStatus.getCandidate().getCcEmailId());
						candidateEmailStatus = candidateEmailStatusRepository
								.findByCandidateCandidateCode(candidateStatus.getCandidate().getCandidateCode());
						if (candidateEmailStatus == null) {
							candidateEmailStatus = new CandidateEmailStatus();
							candidateEmailStatus.setCreatedBy(user);
							candidateEmailStatus.setCreatedOn(new Date());
							candidateEmailStatus.setCandidate(candidateStatus.getCandidate());
						} else {
							candidateEmailStatus.setLastUpdatedBy(user);
							candidateEmailStatus.setLastUpdatedOn(new Date());
						}
						if (result && candidateInvitationSentDto.getStatuscode().equalsIgnoreCase("INVITATIONSENT")) {
							candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("INVITATIONSENT"));
							candidateEmailStatus.setDateOfEmailInvite(new Date());
						} else if (result && candidateInvitationSentDto.getStatuscode().equalsIgnoreCase("REINVITE")) {
							candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("REINVITE"));
							candidateEmailStatus.setDateOfEmailReInvite(new Date());
						} else {
							candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("INVALIDUPLOAD"));
							candidateEmailStatus.setDateOfEmailFailure(new Date());
						}
						candidateStatus.setLastUpdatedBy(user);
						candidateStatus.setLastUpdatedOn(new Date());
						candidateStatusresult = candidateStatusRepository.save(candidateStatus);
						createCandidateStatusHistory(candidateStatusresult, "NOTCANDIDATE");
						candidateEmailStatus.setCandidateStatus(candidateStatusresult);
						candidateEmailStatusRepository.save(candidateEmailStatus);
					}
				}
				svcSearchResult.setData(true);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("Invitations sent successfully.");
			} else {
				svcSearchResult.setData(false);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("No records found to send mail.");
			}
		} catch (Exception ex) {
			log.error("Exception occured in invitationSent method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<CandidateDetailsDto> updateCandidate(CandidateDetailsDto candidateDetails) {
		ServiceOutcome<CandidateDetailsDto> svcSearchResult = new ServiceOutcome<CandidateDetailsDto>();
		CandidateDetailsDto candidateDetailsDto = new CandidateDetailsDto();
		try {
			User user = SecurityHelper.getCurrentUser();
			Candidate result = null;
			if (StringUtils.isNotBlank(candidateDetails.getCandidateCode())) {
				Candidate candidate = candidateRepository
						.findByCandidateCode(candidateDetails.getCandidateCode().trim());
				if (candidate != null) {
					candidate.setCandidateName(candidateDetails.getCandidateName());
					candidate.setEmailId(candidateDetails.getEmailId());
					candidate.setContactNumber(candidateDetails.getContactNumber());
					candidate.setCcEmailId(candidateDetails.getCcEmailId());
					candidate.setApplicantId(candidateDetails.getApplicantId());
					candidate.setLastUpdatedBy(user);
					candidate.setLastUpdatedOn(new Date());
					result = candidateRepository.save(candidate);
					emailSentTask.sendEmail(candidateDetails.getCandidateCode(), candidate.getCandidateName(),
							candidate.getEmailId(), candidate.getCcEmailId());
					BeanUtils.copyProperties(result, candidateDetailsDto);
					svcSearchResult.setData(candidateDetailsDto);
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage("Candidate information Updated successfully");
				} else {
					svcSearchResult.setData(null);
					svcSearchResult.setOutcome(false);
					svcSearchResult.setMessage("NO CANDIDATE FOUND FOR THIS REFERENCE NO");
				}
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("PLEASE SPECIFY CANDIDATE REFERENCE NUMBER");
			}
		} catch (Exception ex) {
			log.error("Exception occured in updateCandidate method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	public ServiceOutcome<List<CandidateStatus>> getCandidateStatusList(String strToDate, String strFromDate,
			Long userId) {
		ServiceOutcome<List<CandidateStatus>> svcSearchResult = new ServiceOutcome<List<CandidateStatus>>();
		List<CandidateStatus> candidateStatusList = new ArrayList<CandidateStatus>();
		List<Long> agentIds = new ArrayList<Long>();
		try {
			Date startDate = formatter.parse(strFromDate + " 00:00:00");
			Date endDate = formatter.parse(strToDate + " 23:59:59");
			User user = userRepository.findById(userId).get();
			if (user.getRole().getRoleCode().equalsIgnoreCase("ROLE_ADMIN")
					|| user.getRole().getRoleCode().equalsIgnoreCase("ROLE_PARTNERADMIN")) {
				candidateStatusList = candidateStatusRepository
						.findAllByCandidateOrganizationOrganizationIdAndLastUpdatedOnBetween(
								user.getOrganization().getOrganizationId(), startDate, endDate);
			}
			if (user.getRole().getRoleCode().equalsIgnoreCase("ROLE_AGENTSUPERVISOR")
					|| user.getRole().getRoleCode().equalsIgnoreCase("ROLE_AGENTHR")) {
				List<User> agentList = userRepository.findAllByAgentSupervisorUserId(user.getUserId());
				if (!agentList.isEmpty()) {
					agentIds = agentList.stream().map(x -> x.getUserId()).collect(Collectors.toList());
				}
				agentIds.add(user.getUserId());
				candidateStatusList = candidateStatusRepository
						.findAllByCandidateCreatedByUserIdInAndLastUpdatedOnBetween(agentIds, startDate, endDate);
			}
			svcSearchResult.setData(candidateStatusList);
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));
		} catch (Exception ex) {
			log.error("Exception occured in getCandidateStatusList method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<DashboardDto> getCandidateStatusAndCount(DashboardDto dashboardDto) {
		ServiceOutcome<DashboardDto> svcSearchResult = new ServiceOutcome<DashboardDto>();
		List<CandidateStatusCountDto> candidateStatusCountDtoList = new ArrayList<CandidateStatusCountDto>();
		List<CandidateStatus> candidateStatusList = null;
		String strToDate = "";
		String strFromDate = "";
		try {
			if (dashboardDto.getUserId() != null && dashboardDto.getUserId() != 0l) {
				strToDate = dashboardDto.getToDate() != null ? dashboardDto.getToDate()
						: ApplicationDateUtils.getStringTodayAsDDMMYYYY();
				strFromDate = dashboardDto.getFromDate() != null ? dashboardDto.getFromDate()
						: ApplicationDateUtils.subtractNoOfDaysFromDateAsDDMMYYYY(
								new SimpleDateFormat("dd/MM/yyyy").parse(strToDate), 7);
				ServiceOutcome<List<CandidateStatus>> svcOutCome = getCandidateStatusList(strToDate, strFromDate,
						dashboardDto.getUserId());
				candidateStatusList = svcOutCome.getData();

				// List<CandidateStatus> invitationSentList = candidateStatusList != null
				// ?candidateStatusList.stream().filter(c ->
				// c.getStatusMaster().getStatusCode().equals("INVITATIONSENT")).collect(Collectors.toList())
				// : null;
				List<CandidateStatus> invitationexpiredList = candidateStatusList != null ? candidateStatusList.stream()
						.filter(c -> c.getStatusMaster().getStatusCode().equals("INVITATIONEXPIRED"))
						.collect(Collectors.toList()) : null;
				List<CandidateStatus> invalidUploadList = candidateStatusList != null ? candidateStatusList.stream()
						.filter(c -> c.getStatusMaster().getStatusCode().equals("INVALIDUPLOAD"))
						.collect(Collectors.toList()) : null;
				List<CandidateStatus> reinviteList = candidateStatusList != null ? candidateStatusList.stream()
						.filter(c -> c.getStatusMaster().getStatusCode().equals("REINVITE"))
						.collect(Collectors.toList()) : null;

				candidateStatusCountDtoList.add(0,
						new CandidateStatusCountDto(
								statusMasterRepository.findByStatusCode("NEWUPLOAD").getStatusName(),
								statusMasterRepository.findByStatusCode("NEWUPLOAD").getStatusCode(),
								candidateStatusList != null ? candidateStatusList.size() : 0));
				// candidateStatusCountDtoList.add(1, new
				// CandidateStatusCountDto(statusMasterRepository.findByStatusCode("INVITATIONSENT").getStatusName(),statusMasterRepository.findByStatusCode("INVITATIONSENT").getStatusCode(),invitationSentList!=null?invitationSentList.size():0));
				candidateStatusCountDtoList.add(1,
						new CandidateStatusCountDto(
								statusMasterRepository.findByStatusCode("INVITATIONEXPIRED").getStatusName(),
								statusMasterRepository.findByStatusCode("INVITATIONEXPIRED").getStatusCode(),
								invitationexpiredList != null ? invitationexpiredList.size() : 0));
				candidateStatusCountDtoList.add(2,
						new CandidateStatusCountDto(
								statusMasterRepository.findByStatusCode("INVALIDUPLOAD").getStatusName(),
								statusMasterRepository.findByStatusCode("INVALIDUPLOAD").getStatusCode(),
								invalidUploadList != null ? invalidUploadList.size() : 0));
				candidateStatusCountDtoList.add(3,
						new CandidateStatusCountDto(statusMasterRepository.findByStatusCode("REINVITE").getStatusName(),
								statusMasterRepository.findByStatusCode("REINVITE").getStatusCode(),
								reinviteList != null ? reinviteList.size() : 0));

				DashboardDto dashboardDtoObj = new DashboardDto(strFromDate, strToDate, null, null,
						candidateStatusCountDtoList, dashboardDto.getUserId(), null, null);
				svcSearchResult.setData(dashboardDtoObj);
				svcSearchResult.setOutcome(true);
				svcSearchResult
						.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("please specify user.");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getCandidateStatusAndCount method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<CandidateDetailsDto> getCandidateByCandidateCode(String referenceNo) {
		ServiceOutcome<CandidateDetailsDto> svcSearchResult = new ServiceOutcome<>();
		try {
			Candidate candidate = candidateRepository.findByCandidateCode(referenceNo);
			if (candidate != null) {
				CandidateDetailsDto candidateDetailsDto = this.modelMapper.map(candidate, CandidateDetailsDto.class);
				svcSearchResult.setData(candidateDetailsDto);
				svcSearchResult.setOutcome(true);
				svcSearchResult
						.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("No candidate found");
			}
		} catch (Exception ex) {
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(true);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
			log.error("Exception occured in getCandidate method in CandidateServiceImpl-->", ex);
		}
		return svcSearchResult;
	}

	@Override
	public Candidate findCandidateByCandidateCode(String candidateCode) {
		Candidate byCandidateCode = candidateRepository.findByCandidateCode(candidateCode);
		if (Objects.isNull(byCandidateCode)) {
			throw new RuntimeException("invalid candidate code");
		}
		return byCandidateCode;
	}

	@Override
	public ServiceOutcome<Boolean> cancelCandidate(String referenceNo) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		try {
			User user = SecurityHelper.getCurrentUser();
			CandidateStatus result = null;
			if (StringUtils.isNotBlank(referenceNo)) {
				CandidateStatus candidateStatus = candidateStatusRepository
						.findByCandidateCandidateCode(referenceNo.trim());
				if (candidateStatus != null) {
					candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("PROCESSDECLINED"));
					candidateStatus.setLastUpdatedBy(user);
					candidateStatus.setLastUpdatedOn(new Date());
					result = candidateStatusRepository.save(candidateStatus);
					createCandidateStatusHistory(result, "NOTCANDIDATE");
					if (result != null) {
						svcSearchResult.setData(true);
						svcSearchResult.setOutcome(true);
						svcSearchResult.setMessage("Verification process declined successfully.");
					}
				} else {
					svcSearchResult.setData(null);
					svcSearchResult.setOutcome(false);
					svcSearchResult.setMessage("NO CANDIDATE FOUND");
				}
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("PLEASE SPECIFY CANDIDATE REFERENCE NUMBER");
			}
		} catch (Exception ex) {
			log.error("Exception occured in cancelCandidate method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Transactional
	@Override
	public List<CandidateStatus> expireInvitationForCandidate() {
		List<CandidateStatus> candidateList = new ArrayList<CandidateStatus>();
		try {
			User user = SecurityHelper.getCurrentUser();
			List<CandidateStatus> candidateStatusList = candidateStatusRepository
					.findAllByStatusMasterStatusCode("INVITATIONSENT");
			if (candidateStatusList != null && candidateStatusList.size() > 0) {
				for (CandidateStatus candidateStatus : candidateStatusList) {
					Timestamp currentTimeInTimestamp = new Timestamp(System.currentTimeMillis());
					Timestamp savedTimestamp = (Timestamp) candidateStatus.getLastUpdatedOn();
					Long timeDifferenceInMillis = currentTimeInTimestamp.getTime() - savedTimestamp.getTime();
					Long timeDifferenceInSecond = TimeUnit.MILLISECONDS.toSeconds(timeDifferenceInMillis);
					if (timeDifferenceInSecond > 259200) {
						candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("INVITATIONEXPIRED"));
						candidateStatus.setLastUpdatedOn(new Date());
						CandidateStatus candidateStatusObj = candidateStatusRepository.save(candidateStatus);
						createCandidateStatusHistory(candidateStatusObj, "CANDIDATE");
						CandidateEmailStatus candidateEmailStatus = candidateEmailStatusRepository
								.findByCandidateCandidateCode(candidateStatus.getCandidate().getCandidateCode());
						candidateEmailStatus.setDateOfEmailExpire(new Date());
						candidateEmailStatus.setLastUpdatedOn(new Date());
						candidateEmailStatus.setLastUpdatedBy(user);
						candidateEmailStatusRepository.save(candidateEmailStatus);
						candidateList.add(candidateStatusObj);
					}
				}
			}
		} catch (Exception ex) {
			log.error("Exception occured in expireInvitationForCandidate method in CandidateServiceImpl-->", ex);
		}
		return candidateList;
	}

	@Override
	public ServiceOutcome<CandidateStatus> getCandidateStatusByCandidateCode(String code) {
		ServiceOutcome<CandidateStatus> outcome = new ServiceOutcome<>();
		try {
			CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(code);
			if (candidateStatus != null) {
				outcome.setData(candidateStatus);
				outcome.setMessage("Record fetched successfully.");
				outcome.setOutcome(true);
			} else {
				outcome.setData(null);
				outcome.setMessage("");
				outcome.setOutcome(false);
			}

		} catch (Exception ex) {
			outcome.setData(null);
			outcome.setMessage("");
			outcome.setOutcome(false);
			log.error("Exception occured in getCandidateStatusByCandidateCode method in CandidateServiceImpl-->", ex);
		}
		return outcome;
	}

	@Override
	public ServiceOutcome<DashboardDto> getReportDeliveryDetailsStatusAndCount(DashboardDto dashboardDto) {
		ServiceOutcome<DashboardDto> svcSearchResult = new ServiceOutcome<DashboardDto>();
		List<CandidateStatusCountDto> candidateStatusCountDtoList = new ArrayList<CandidateStatusCountDto>();
		List<CandidateStatus> candidateStatusList = null;
		String strToDate = "";
		String strFromDate = "";
		try {
			if (dashboardDto.getUserId() != null && dashboardDto.getUserId() != 0l) {
				strToDate = dashboardDto.getToDate() != null ? dashboardDto.getToDate()
						: ApplicationDateUtils.getStringTodayAsDDMMYYYY();
				strFromDate = dashboardDto.getFromDate() != null ? dashboardDto.getFromDate()
						: ApplicationDateUtils.subtractNoOfDaysFromDateAsDDMMYYYY(
								new SimpleDateFormat("dd/MM/yyyy").parse(strToDate), 7);
				ServiceOutcome<List<CandidateStatus>> svcOutCome = getCandidateStatusList(strToDate, strFromDate,
						dashboardDto.getUserId());
				candidateStatusList = svcOutCome.getData();
				StatusMaster pending = statusMasterRepository.findByStatusCode("PENDINGAPPROVAL");
				StatusMaster interim = statusMasterRepository.findByStatusCode("INTERIMREPORT");
				StatusMaster processDeclined = statusMasterRepository.findByStatusCode("PROCESSDECLINED");
				StatusMaster finalReport = statusMasterRepository.findByStatusCode("FINALREPORT");
				List<CandidateStatus> pendingList = candidateStatusList != null ? candidateStatusList.stream()
						.filter(c -> c.getStatusMaster().getStatusCode().equals("PENDINGAPPROVAL"))
						.collect(Collectors.toList()) : null;
				List<CandidateStatus> interimList = candidateStatusList != null ? candidateStatusList.stream()
						.filter(c -> c.getStatusMaster().getStatusCode().equals("INTERIMREPORT"))
						.collect(Collectors.toList()) : null;
				List<CandidateStatus> processsdeclinedList = candidateStatusList != null ? candidateStatusList.stream()
						.filter(c -> c.getStatusMaster().getStatusCode().equals("PROCESSDECLINED"))
						.collect(Collectors.toList()) : null;
				List<CandidateStatus> finalReportList = candidateStatusList != null ? candidateStatusList.stream()
						.filter(c -> c.getStatusMaster().getStatusCode().equals("FINALREPORT"))
						.collect(Collectors.toList()) : null;
				int pendingCount = pendingList.size() + interimList.size();
				candidateStatusCountDtoList.add(0, new CandidateStatusCountDto(pending.getStatusName(),
						pending.getStatusCode(), pendingList != null ? pendingCount : 0));
				candidateStatusCountDtoList.add(1, new CandidateStatusCountDto(interim.getStatusName(),
						interim.getStatusCode(), pendingList != null ? interimList.size() : 0));
				candidateStatusCountDtoList.add(2,
						new CandidateStatusCountDto(processDeclined.getStatusName(), processDeclined.getStatusCode(),
								processsdeclinedList != null ? processsdeclinedList.size() : 0));
				candidateStatusCountDtoList.add(3, new CandidateStatusCountDto(finalReport.getStatusName(),
						finalReport.getStatusCode(), finalReportList != null ? finalReportList.size() : 0));
				DashboardDto dashboardDtoObj = new DashboardDto(strFromDate, strToDate, null, null,
						candidateStatusCountDtoList, dashboardDto.getUserId(), null, null);
				svcSearchResult.setData(dashboardDtoObj);
				svcSearchResult.setOutcome(true);
				svcSearchResult
						.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("please specify user.");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getReportDeliveryDetailsStatusAndCount method in CandidateServiceImpl-->",
					ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<List<RemarkMaster>> getAllRemark(String remarkType) {
		ServiceOutcome<List<RemarkMaster>> svcSearchResult = new ServiceOutcome<List<RemarkMaster>>();
		try {
			List<RemarkMaster> remarkList = remarkMasterRepository.findAllByRemarkType(remarkType.toUpperCase());
			if (!remarkList.isEmpty()) {
				svcSearchResult.setData(remarkList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NOT FOUND");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getAllRemark method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	public List<CandidateStatus> processDeclined() {
		List<CandidateStatus> candidateList = new ArrayList<CandidateStatus>();
		try {
			List<String> statusList = new ArrayList<String>();
			statusList.add("DIGILOCKER");
			statusList.add("ITR");
			statusList.add("EPFO");
			statusList.add("RELATIVEADDRESS");
			List<CandidateStatus> candidateStatusList = candidateStatusRepository
					.findAllByStatusMasterStatusCodeIn(statusList);
			if (candidateStatusList != null && candidateStatusList.size() > 0) {
				for (CandidateStatus candidateStatus : candidateStatusList) {
					Timestamp currentTimeInTimestamp = new Timestamp(System.currentTimeMillis());
					Timestamp savedTimestamp = (Timestamp) candidateStatus.getLastUpdatedOn();
					Long timeDifferenceInMillis = currentTimeInTimestamp.getTime() - savedTimestamp.getTime();
					Long timeDifferenceInSecond = TimeUnit.MILLISECONDS.toSeconds(timeDifferenceInMillis);
					if (timeDifferenceInSecond > 604800) {
						candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("PROCESSDECLINED"));
						candidateStatus.setLastUpdatedOn(new Date());
						CandidateStatus candidateStatusObj = candidateStatusRepository.save(candidateStatus);
						createCandidateStatusHistory(candidateStatusObj, "CANDIDATE");
						candidateList.add(candidateStatusObj);
					}
				}
			}
		} catch (Exception ex) {
			log.error("Exception occured in processDeclined method in CandidateServiceImpl-->", ex);
		}
		return candidateList;
	}

	@Override
	public ServiceOutcome<Boolean> declineAuthLetter(String candidateCode) {
		ServiceOutcome<Boolean> svcOutcome = new ServiceOutcome<>();
		try {
			CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(candidateCode);
			candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("PROCESSDECLINED"));
			candidateStatus.setLastUpdatedOn(new Date());
			candidateStatus = candidateStatusRepository.save(candidateStatus);
			createCandidateStatusHistory(candidateStatus, "CANDIDATE");
			svcOutcome.setData(true);
			svcOutcome.setOutcome(true);
			svcOutcome.setMessage("Your process was declined successfully.");

		} catch (Exception e) {
			svcOutcome.setData(true);
			svcOutcome.setOutcome(true);
			svcOutcome.setMessage("Unable to decline process.");
			log.error("Exception occured in declineAuthLetter method.");
		}
		return svcOutcome;
	}

	@Override
	public ServiceOutcome<List<QualificationMaster>> getQualificationList() {
		ServiceOutcome<List<QualificationMaster>> svcSearchResult = new ServiceOutcome<List<QualificationMaster>>();
		try {
			List<QualificationMaster> qualifiacationList = qualificationMasterRepository.findAll();
			if (!qualifiacationList.isEmpty()) {

				svcSearchResult.setData(qualifiacationList);
				svcSearchResult.setOutcome(true);
				svcSearchResult
						.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("No Qualification List found");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getQualificationList method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<Boolean> saveNUpdateCandidateEducation(String candidateCafEducationObject,
			MultipartFile certificate) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		CandidateCafEducation candidateCafEducation = null;
		CandidateCafEducation candidateCafEducationObj = null;
		try {
			CandidateCafEducationDto candidateCafEducationDto = new ObjectMapper()
					.readValue(candidateCafEducationObject, CandidateCafEducationDto.class);
			if (candidateCafEducationDto.getCandidateCafEducationId() != null) {
				Optional<CandidateCafEducation> candidateCafEducationObj1 = candidateCafEducationRepository
						.findById(candidateCafEducationDto.getCandidateCafEducationId());
				if (candidateCafEducationObj1.isPresent()) {
					candidateCafEducation = candidateCafEducationObj1.get();
					candidateCafEducation.setLastUpdatedOn(new Date());
					svcSearchResult.setMessage("Education details updated successfully.");
				}
			} else {
				candidateCafEducation = new CandidateCafEducation();
				candidateCafEducation.setCandidate(
						candidateRepository.findByCandidateCode(candidateCafEducationDto.getCandidateCode()));
				candidateCafEducation.setCandidateStatus(candidateStatusRepository
						.findByCandidateCandidateCode(candidateCafEducationDto.getCandidateCode()));
				candidateCafEducation.setCreatedOn(new Date());
				candidateCafEducation.setIsHighestQualification(false);
				svcSearchResult.setMessage("Education details added successfully.");
			}
			BeanUtils.copyProperties(candidateCafEducationDto, candidateCafEducation);
			// todo upload the file to s3
			candidateCafEducation.setCertificate(certificate != null ? certificate.getBytes() : null);
			candidateCafEducation.setQualificationMaster(
					qualificationMasterRepository.findById(candidateCafEducationDto.getQualificationId()).get());
			SuspectClgMaster suspectClgMasterObj = suspectClgMasterRepository
					.findById(candidateCafEducationDto.getSuspectClgMasterId()).get();
			candidateCafEducation.setSuspectClgMaster(suspectClgMasterObj);

			// removed DNH logic
//			if(suspectClgMasterObj.getSuspectClgMasterId()!=0) {
//				candidateCafEducation.setBoardOrUniversityName(suspectClgMasterObj.getSuspectInstitutionName());
//				candidateCafEducation.setCourseName(candidateCafEducationDto.getCourseName());
//				candidateCafEducation.setColor(colorRepository.findByColorCode("RED"));
//				candidateCafEducation.setRemarkMaster(remarkMasterRepository.findByRemarkCode("EDUCATION-1"));
//			}
//			else {
			candidateCafEducation.setBoardOrUniversityName(candidateCafEducationDto.getBoardOrUniversityName());
			candidateCafEducation.setCourseName(candidateCafEducationDto.getCourseName());
			candidateCafEducation.setColor(colorRepository.findByColorCode("AMBER"));
			candidateCafEducation.setRemarkMaster(remarkMasterRepository.findByRemarkCode("ALL"));
//			}
			candidateCafEducationObj = candidateCafEducationRepository.save(candidateCafEducation);
			if (candidateCafEducationObj != null) {
				svcSearchResult.setData(true);
				svcSearchResult.setOutcome(true);

			} else {
				svcSearchResult.setData(false);
				svcSearchResult.setOutcome(false);
				svcSearchResult
						.setMessage(messageSource.getMessage("msg.error", null, LocaleContextHolder.getLocale()));
			}
		} catch (Exception ex) {
			log.error("Exception occured in saveNUpdateEducation method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(false);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<CandidateCafEducationDto> getCandidateEducationById(Long candidateCafEducationId) {
		ServiceOutcome<CandidateCafEducationDto> svcSearchResult = new ServiceOutcome<CandidateCafEducationDto>();
		try {
			Optional<CandidateCafEducation> candidateCafEducation = candidateCafEducationRepository
					.findById(candidateCafEducationId);
			if (candidateCafEducation.isPresent()) {
				CandidateCafEducationDto candidateCafEducationDto = this.modelMapper.map(candidateCafEducation.get(),
						CandidateCafEducationDto.class);
				svcSearchResult.setData(candidateCafEducationDto);
				svcSearchResult.setOutcome(true);
				svcSearchResult
						.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("No Candidate Education Details Found");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getCandidateEducationById method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<Boolean> saveNUpdateCandidateExperience(String candidateCafExperienceDtoObj,
			MultipartFile certificate) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		CandidateCafExperience candidateCafExperience = null;
		CandidateCafExperience candidateCafExperienceObj = null;
		try {
			CandidateCafExperienceDto candidateCafExperienceDto = new ObjectMapper()
					.readValue(candidateCafExperienceDtoObj, CandidateCafExperienceDto.class);

			if (candidateCafExperienceDto.getSuspectEmpMasterId() == null) {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("Please select employer.");
				return svcSearchResult;
			} else if (candidateCafExperienceDto.getSuspectEmpMasterId() == 0
					&& candidateCafExperienceDto.getCandidateEmployerName().equals("")) {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("Please enter candidate employer name.");
				return svcSearchResult;
			} else if (candidateCafExperienceDto.getInputDateOfJoining() == null) {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("Please select date of joining.");
				return svcSearchResult;

			} else if (candidateCafExperienceDto.getInputDateOfExit() == null) {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("Please select date of exit.");
				return svcSearchResult;
			} else if (certificate == null) {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("Please upload certificate.");
				return svcSearchResult;
			}
			if (candidateCafExperienceDto.getCandidateCafExperienceId() != null) {
				Optional<CandidateCafExperience> candidateCafExperienceObj1 = candidateCafExperienceRepository
						.findById(candidateCafExperienceDto.getCandidateCafExperienceId());
				if (candidateCafExperienceObj1.isPresent()) {
					candidateCafExperience = candidateCafExperienceObj1.get();
					candidateCafExperience.setLastUpdatedOn(new Date());
					candidateCafExperienceObj = candidateCafExperienceRepository.save(candidateCafExperience);
					svcSearchResult.setMessage("Experience updated successfully.");
				}
			} else {
				candidateCafExperience = new CandidateCafExperience();
				candidateCafExperience.setCandidate(
						candidateRepository.findByCandidateCode(candidateCafExperienceDto.getCandidateCode()));
				candidateCafExperience.setCandidateStatus(candidateStatusRepository
						.findByCandidateCandidateCode(candidateCafExperienceDto.getCandidateCode()));
				candidateCafExperience.setCreatedOn(new Date());
				svcSearchResult.setMessage("Experience saved successfully.");
			}
			SuspectEmpMaster suspectEmpMaster = suspectEmpMasterRepository
					.findById(candidateCafExperienceDto.getSuspectEmpMasterId()).get();
			candidateCafExperience.setSuspectEmpMaster(suspectEmpMaster);
			if (suspectEmpMaster.getSuspectEmpMasterId() != 0) {
				candidateCafExperience.setCandidateEmployerName(suspectEmpMaster.getSuspectCompanyName());
				candidateCafExperience.setColor(colorRepository.findByColorCode("RED"));
				candidateCafExperience.setRemarkMaster(remarkMasterRepository.findByRemarkCode("EMPLOYMENT-1"));
			} else {
				candidateCafExperience.setCandidateEmployerName(candidateCafExperienceDto.getCandidateEmployerName());
				candidateCafExperience.setColor(colorRepository.findByColorCode("AMBER"));
				candidateCafExperience.setRemarkMaster(remarkMasterRepository.findByRemarkCode("ALL"));
			}
			candidateCafExperience.setInputDateOfJoining(candidateCafExperienceDto.getInputDateOfJoining() != null
					? sdf.parse(candidateCafExperienceDto.getInputDateOfJoining())
					: null);
			candidateCafExperience.setInputDateOfExit(candidateCafExperienceDto.getInputDateOfExit() != null
					? sdf.parse(candidateCafExperienceDto.getInputDateOfExit())
					: null);
			candidateCafExperience.setExperienceCertificate(certificate != null ? certificate.getBytes() : null);
			candidateCafExperienceObj = candidateCafExperienceRepository.save(candidateCafExperience);
			if (candidateCafExperienceObj != null) {
				svcSearchResult.setData(true);
				svcSearchResult.setOutcome(true);
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(true);
				svcSearchResult
						.setMessage(messageSource.getMessage("msg.error", null, LocaleContextHolder.getLocale()));
			}
		} catch (Exception ex) {
			log.error("Exception occured in saveNUpdateCandidateExperience method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<CandidateCafExperienceDto> getCandidateExperienceById(Long candidateCafExperienceId) {
		ServiceOutcome<CandidateCafExperienceDto> svcSearchResult = new ServiceOutcome<CandidateCafExperienceDto>();
		try {
			Optional<CandidateCafExperience> candidateCafExperienceObj = candidateCafExperienceRepository
					.findById(candidateCafExperienceId);
			if (candidateCafExperienceObj.isPresent()) {
				CandidateCafExperience candidateCafExperience = candidateCafExperienceObj.get();
				CandidateCafExperienceDto candidateCafExperienceDto = this.modelMapper.map(candidateCafExperience,
						CandidateCafExperienceDto.class);
				candidateCafExperienceDto.setInputDateOfJoining(candidateCafExperience.getInputDateOfJoining() != null
						? sdf.format(candidateCafExperience.getInputDateOfJoining())
						: null);
				candidateCafExperienceDto.setOutputDateOfJoining(candidateCafExperience.getOutputDateOfJoining() != null
						? sdf.format(candidateCafExperience.getOutputDateOfJoining())
						: null);
				candidateCafExperienceDto.setInputDateOfExit(candidateCafExperience.getInputDateOfExit() != null
						? sdf.format(candidateCafExperience.getInputDateOfExit())
						: null);
				candidateCafExperienceDto.setOutputDateOfExit(candidateCafExperience.getOutputDateOfExit() != null
						? sdf.format(candidateCafExperience.getOutputDateOfExit())
						: null);
				svcSearchResult.setData(candidateCafExperienceDto);
				svcSearchResult.setOutcome(true);
				svcSearchResult
						.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("No Candidate Experience Details Found");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getCandidateExperienceById method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	// Candidate details
	@Override
	public ServiceOutcome<CandidationApplicationFormDto> candidateApplicationFormDetails(String candidateCode) {
		ServiceOutcome<CandidationApplicationFormDto> svcSearchResult = new ServiceOutcome<CandidationApplicationFormDto>();
		CandidationApplicationFormDto candidationApplicationFormDto = new CandidationApplicationFormDto();
		List<CandidateCafEducationDto> candidateCafEducationDtoList = new ArrayList<CandidateCafEducationDto>();
		List<CandidateCafExperienceDto> candidateCafExperienceDtoList = new ArrayList<CandidateCafExperienceDto>();
		List<CandidateCafAddressDto> candidateCafAddressDtoList = new ArrayList<CandidateCafAddressDto>();
		// List<ITRDataFromApiDto> iTRDataFromApiDtoList=new
		// ArrayList<ITRDataFromApiDto>();
		CandidateFileDto candidateFileDto = null;
		try {
			if (StringUtils.isNotEmpty(candidateCode)) {
				Candidate candidate = candidateRepository.findByCandidateCode(candidateCode);
				IdItemsDto candidateIdItemPan = candidateIdItemsRepository
						.findByCandidateCandidateCodeAndServiceSourceMasterServiceCode(candidateCode, "PAN");
				if (candidateIdItemPan != null && candidateIdItemPan.getIdHolder() != null) {
					candidate.setCandidateName(candidateIdItemPan.getIdHolder());
				} else {
					IdItemsDto candidateIdItemAadhar = candidateIdItemsRepository
							.findByCandidateCandidateCodeAndServiceSourceMasterServiceCode(candidateCode, "AADHARID");
					if (Objects.nonNull(candidateIdItemAadhar)) {
						candidate.setCandidateName(candidateIdItemAadhar.getIdHolder());
					}
				}
				candidationApplicationFormDto.setCandidate(candidate);
				List<CandidateCafEducation> candidateCafEducationList = candidateCafEducationRepository
						.findAllByCandidateCandidateCode(candidateCode);
				if (!candidateCafEducationList.isEmpty()) {
					candidateCafEducationDtoList = candidateCafEducationList.stream()
							.map(candidateCafEducation -> modelMapper.map(candidateCafEducation,
									CandidateCafEducationDto.class))
							.collect(Collectors.toList());
					candidationApplicationFormDto.setCandidateCafEducationDto(candidateCafEducationDtoList);
				}
//				List<CandidateCafExperience> candidateCafExperienceList=candidateCafExperienceRepository.findAllByCandidateCandidateCodeOrderByInputDateOfJoiningDesc(candidateCode);
//				List<CandidateCafExperience> efpolist = candidateCafExperienceList != null ?candidateCafExperienceList.stream().filter(c -> c.getServiceSourceMaster()!=null && c.getServiceSourceMaster().getServiceCode().equals("EPFO")).collect(Collectors.toList()) : new ArrayList<>();
//				List<Long> itrIds = candidateCafExperienceList != null ?candidateCafExperienceList.stream().filter(c -> c.getServiceSourceMaster()!=null && c.getServiceSourceMaster().getServiceCode().equals("ITR")).map(f->f.getCandidateCafExperienceId()).collect(Collectors.toList()) : new ArrayList<>();
//
//				for(CandidateCafExperience efpo: efpolist) {
//					Date inputJoiningDate = efpo.getInputDateOfJoining();
//					Date inputExitDate = efpo.getInputDateOfExit();
//					List<Long> itrRemoveIdsList = candidateCafExperienceRepository.findByCandidateCodeAndServiceSourceMasterCodeAndDates(candidateCode, "ITR",inputJoiningDate,inputExitDate);
//					itrIds.removeAll(itrRemoveIdsList);
//				}
//
//				List<CandidateCafExperience> itrList  = candidateCafExperienceList.stream().filter(c-> itrIds.contains(c.getCandidateCafExperienceId())).collect(Collectors.toList());
//				efpolist.addAll(itrList);
//				List<CandidateCafExperience> newList  = candidateCafExperienceList.stream().filter(c -> c.getServiceSourceMaster()==null).collect(Collectors.toList());
//				efpolist.addAll(newList);
//				efpolist.sort(Comparator.comparing(CandidateCafExperience::getInputDateOfJoining));
//				Collections.reverse(efpolist);
//				if(!efpolist.isEmpty()) {
//					for (CandidateCafExperience candidateCafExperience : efpolist) {
//						CandidateCafExperienceDto candidateCafExperienceDto = this.modelMapper.map(candidateCafExperience, CandidateCafExperienceDto.class);
//						candidateCafExperienceDto.setInputDateOfJoining(candidateCafExperience.getInputDateOfJoining()!=null?sdf.format(candidateCafExperience.getInputDateOfJoining()):null);
//						candidateCafExperienceDto.setInputDateOfExit(candidateCafExperience.getInputDateOfExit()!=null?sdf.format(candidateCafExperience.getInputDateOfExit()):null);
//						candidateCafExperienceDto.setOutputDateOfJoining(candidateCafExperience.getOutputDateOfJoining()!=null?sdf.format(candidateCafExperience.getOutputDateOfJoining()):null);
//						candidateCafExperienceDto.setOutputDateOfExit(candidateCafExperience.getOutputDateOfExit()!=null?sdf.format(candidateCafExperience.getOutputDateOfExit()):null);
//						candidateCafExperienceDto.setServiceName(candidateCafExperience.getServiceSourceMaster()!=null?candidateCafExperience.getServiceSourceMaster().getServiceName():"Candidate");
//						candidateCafExperienceDtoList.add(candidateCafExperienceDto);
//					}
//
//					candidationApplicationFormDto.setCandidateCafExperienceDto(candidateCafExperienceDtoList);
//					List<String> uanNUmberList=candidateCafExperienceRepository.getCandidateUan(candidate.getCandidateId());
//					String uanNumber = uanNUmberList.stream().map(uan -> uan.toString()).collect(Collectors.joining("/"));
//					candidationApplicationFormDto.setCandidateUan(uanNumber);
//				}
				List<CandidateCafExperience> candidateCafExperiences = candidateCafExperienceRepository
						.findAllByCandidateCandidateId(candidate.getCandidateId());
				if (candidateCafExperiences.isEmpty()) {
					candidateCafExperiences = getCandidateExperienceFromItrAndEpfoByCandidateId(
							candidate.getCandidateId(), false);
					System.out.println(candidateCafExperiences.size() + "getting");
					candidateCafExperienceRepository.saveAll(candidateCafExperiences);
				}
				List<CandidateCafExperienceDto> collect = candidateCafExperiences.stream()
						.map(candidateCafExperience -> {
							CandidateCafExperienceDto candidateCafExperienceDto = this.modelMapper
									.map(candidateCafExperience, CandidateCafExperienceDto.class);
							candidateCafExperienceDto
									.setInputDateOfJoining(candidateCafExperience.getInputDateOfJoining() != null
											? sdf.format(candidateCafExperience.getInputDateOfJoining())
											: null);
							candidateCafExperienceDto
									.setInputDateOfExit(candidateCafExperience.getInputDateOfExit() != null
											? sdf.format(candidateCafExperience.getInputDateOfExit())
											: null);
							candidateCafExperienceDto
									.setOutputDateOfJoining(candidateCafExperience.getOutputDateOfJoining() != null
											? sdf.format(candidateCafExperience.getOutputDateOfJoining())
											: null);
							candidateCafExperienceDto
									.setOutputDateOfExit(candidateCafExperience.getOutputDateOfExit() != null
											? sdf.format(candidateCafExperience.getOutputDateOfExit())
											: null);
							candidateCafExperienceDto
									.setServiceName(candidateCafExperience.getServiceSourceMaster() != null
											? candidateCafExperience.getServiceSourceMaster().getServiceName()
											: "Candidate");
							return candidateCafExperienceDto;
						}).collect(Collectors.toList());
				candidateCafExperienceDtoList.addAll(collect);
				candidationApplicationFormDto.setCandidateCafExperienceDto(candidateCafExperienceDtoList);
				List<CandidateCafAddress> candidateCafAddressList = candidateCafAddressRepository
						.findAllByCandidateCandidateCode(candidateCode);
				if (candidateCafAddressList != null) {
					candidateCafAddressDtoList = candidateCafAddressList.stream().map(
							candidateCafAddress -> modelMapper.map(candidateCafAddress, CandidateCafAddressDto.class))
							.collect(Collectors.toList());
					candidationApplicationFormDto.setCandidateCafAddressDto(candidateCafAddressDtoList);
				}

//				List<ITRData> iTRDataList=itrDataRepository.findAllByCandidateCandidateCodeOrderByDateDesc(candidateCode);
//				if(!iTRDataList.isEmpty()) {
//					iTRDataFromApiDtoList= iTRDataList.stream().map(itr -> modelMapper.map(itr,ITRDataFromApiDto.class)).collect(Collectors.toList());
//					candidationApplicationFormDto.setITRDataFromApiDto(iTRDataFromApiDtoList);
//				}
				CandidateResumeUpload candidateResume = candidateResumeUploadRepository
						.findByCandidateCandidateCode(candidateCode);
				if (candidateResume != null && Objects.nonNull(candidateResume.getContentId())) {
					String resumeUrl = contentService.getFileUrlFromContentId(candidateResume.getContentId());
					candidationApplicationFormDto.setCandidateResumeUrl(resumeUrl);
				} else if (candidateResume != null) {
					candidateFileDto = new CandidateFileDto(candidateResume.getCandidateResumeUploadId(),
							candidateResume.getCandidateResume(), null);
					candidationApplicationFormDto.setCandidateResume(candidateFileDto);
				}
//				CandidateCaseDetails candidateCaseDetails=candidateCaseDetailsRepository.findByCandidateCandidateCode(candidateCode);
//				if(candidateCaseDetails!=null) {
//					candidateFileDto=new CandidateFileDto(candidateCaseDetails.getCandidateCaseDetailsId(), candidateCaseDetails.getCriminalVerificationDocument(),candidateCaseDetails.getCriminalVerificationisExist().getColorName());
//					candidationApplicationFormDto.setCaseDetails(candidateFileDto);
//					candidateFileDto=new CandidateFileDto(candidateCaseDetails.getCandidateCaseDetailsId(), candidateCaseDetails.getGlobalDatabaseCaseDetailsDocument(),candidateCaseDetails.getGlobalDatabaseCaseDetailsIsExist().getColorName());
//					candidationApplicationFormDto.setGlobalDatabaseCaseDetails(candidateFileDto);
//				}

				CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(candidateCode);
				candidationApplicationFormDto.setCandidateStatus(candidateStatus);

			}
			svcSearchResult.setData(candidationApplicationFormDto);
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));
		} catch (Exception ex) {
			log.error("Exception occured in candidateApplicationFormDetails method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Transactional
	@Override
	public ServiceOutcome<Boolean> saveCandidateApplicationForm(String candidateCafEducationId,
			JSONArray candidateCafAddressDto, MultipartFile resume, String candidateCode) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		try {
			Candidate candidate = candidateRepository.findByCandidateCode(candidateCode);

			if (resume != null && !candidateCode.isEmpty()) {
				CandidateResumeUpload candidateResume = candidateResumeUploadRepository
						.findByCandidateCandidateCode(candidateCode);
				if (candidateResume != null) {
					candidateResume.setLastUpdatedOn(new Date());
				} else {
					candidateResume = new CandidateResumeUpload();
					candidateResume.setCandidate(candidateRepository.findByCandidateCode(candidateCode));
					candidateResume.setCreatedOn(new Date());
				}
				ContentDTO contentDTO = new ContentDTO();
				File resumeFile = FileUtil.createUniqueTempFile(candidateCode, ".pdf");
				resume.transferTo(resumeFile);
				contentDTO.setFile(resumeFile);
				contentDTO.setCandidateCode(candidateCode);
				contentDTO.setCandidateId(candidate.getCandidateId());
				contentDTO.setContentType(ContentType.CANDIDATE_UPLOADED);
				contentDTO.setFileType(FileType.PDF);
				contentDTO.setContentCategory(ContentCategory.OTHERS);
				contentDTO.setContentSubCategory(ContentSubCategory.RESUME);
				ContentDTO contentDTO1 = contentService.uploadFile(contentDTO);
//				candidateResume.setCandidateResume(resume!=null?resume.getBytes():null);
				candidateResume.setContentId(contentDTO1.getContentId());
				candidateResumeUploadRepository.save(candidateResume);
			}
			if (!candidateCafEducationId.isEmpty() && !candidateCafEducationId.equals("")
					&& candidateCafEducationId != null) {
				Optional<CandidateCafEducation> candidateCafEducationObj1 = candidateCafEducationRepository
						.findById(Long.parseLong(candidateCafEducationId));
				if (candidateCafEducationObj1.isPresent()) {
					CandidateCafEducation candidateCafEducation = candidateCafEducationObj1.get();
					candidateCafEducation.setIsHighestQualification(true);
					candidateCafEducationRepository.save(candidateCafEducation);
				}
			}
			if (!candidateCafAddressDto.isEmpty()) {
				for (int i = 0; i < candidateCafAddressDto.length(); i++) {
					JSONObject object = candidateCafAddressDto.getJSONObject(i);
					CandidateCafAddressDto candidateCafAddressDtoObj1 = new ObjectMapper().readValue(object.toString(),
							CandidateCafAddressDto.class);
					if (candidateCafAddressDtoObj1.getCandidateCafAddressId() != null) {
						Optional<CandidateCafAddress> candidateCafAddress = candidateCafAddressRepository
								.findById(candidateCafAddressDtoObj1.getCandidateCafAddressId());
						if (candidateCafAddress.isPresent()) {
							CandidateCafAddress candidateCafAddressobj = candidateCafAddress.get();
							candidateCafAddressobj.setIsAssetDeliveryAddress(
									candidateCafAddressDtoObj1.getIsAssetDeliveryAddress() != null
											? candidateCafAddressDtoObj1.getIsAssetDeliveryAddress()
											: candidateCafAddressobj.getIsAssetDeliveryAddress());
							candidateCafAddressobj
									.setIsPermanentAddress(candidateCafAddressDtoObj1.getIsPermanentAddress() != null
											? candidateCafAddressDtoObj1.getIsPermanentAddress()
											: candidateCafAddressobj.getIsAssetDeliveryAddress());
							candidateCafAddressobj
									.setIsPresentAddress(candidateCafAddressDtoObj1.getIsPresentAddress() != null
											? candidateCafAddressDtoObj1.getIsPresentAddress()
											: candidateCafAddressobj.getIsAssetDeliveryAddress());
							candidateCafAddressRepository.save(candidateCafAddressobj);
						}
					}
				}

			}
			candidate.setSubmittedOn(new Date());
			candidate.setApprovalRequired(true);
			candidateRepository.save(candidate);
			CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(candidateCode);
			candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("PENDINGAPPROVAL"));
			candidateStatus.setLastUpdatedOn(new Date());
			candidateStatus = candidateStatusRepository.save(candidateStatus);
			createCandidateStatusHistory(candidateStatus, "CANDIDATE");
			svcSearchResult.setData(true);
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage("Thank you for submitting your response.");

			CompletableFuture.runAsync(() -> {
				reportService.generateDocument(candidateCode, "", ReportType.PRE_OFFER);
			});

		} catch (Exception ex) {
			log.error("Exception occured in saveCandidateApplicationForm method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(false);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<Boolean> updateCandidateEducationStatusAndRemark(
			ApprovalStatusRemarkDto approvalStatusRemarkDto) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		CandidateCafEducation candidateCafEducation = null;
		// System.out.println("\n__________approvalStatusRemarkDto________"+approvalStatusRemarkDto);
		try {
			User user = SecurityHelper.getCurrentUser();
			if (approvalStatusRemarkDto.getId() != null) {
				Optional<CandidateCafEducation> candidateCafEducationObj1 = candidateCafEducationRepository
						.findById(approvalStatusRemarkDto.getId());
				if (candidateCafEducationObj1.isPresent()) {
					Optional<QualificationMaster> qualificationMasterobj = qualificationMasterRepository
							.findById(approvalStatusRemarkDto.getQualificationId());
					System.out.println("qualificationMasterobj====================" + qualificationMasterobj);
					CandidateCafEducation candidateCafEducationobj = candidateCafEducationObj1.get();
					candidateCafEducationobj.setQualificationMaster(
							qualificationMasterRepository.findById(approvalStatusRemarkDto.getQualificationId()).get());
					if (qualificationMasterobj.isPresent()) {
						QualificationMaster qualificationMasterobj1 = qualificationMasterobj.get();
						String qualificationName = qualificationMasterobj1.getQualificationName();
						candidateCafEducationobj.setCourseName(qualificationName);
					}
					candidateCafEducationobj.setSchoolOrCollegeName(approvalStatusRemarkDto.getSchoolOrCollegeName());
					candidateCafEducationobj
							.setBoardOrUniversityName(approvalStatusRemarkDto.getBoardOrUniversityName());
					candidateCafEducationobj.setYearOfPassing(approvalStatusRemarkDto.getYearOfPassing());
					candidateCafEducationobj.setPercentage(approvalStatusRemarkDto.getPercentage());
					candidateCafEducationobj
							.setColor(colorRepository.findById(approvalStatusRemarkDto.getColorId()).get());
					if (approvalStatusRemarkDto.getRemarkId() != null)
						candidateCafEducationobj.setRemarkMaster(
								remarkMasterRepository.findById(approvalStatusRemarkDto.getRemarkId()).get());
					candidateCafEducationobj.setCustomRemark(approvalStatusRemarkDto.getCustomRemark());
					candidateCafEducationobj.setLastUpdatedOn(new Date());
					candidateCafEducationobj.setLastUpdatedBy(user);
					candidateCafEducation = candidateCafEducationRepository.save(candidateCafEducationobj);
					if (candidateCafEducation != null) {
						svcSearchResult.setData(true);
						svcSearchResult.setOutcome(true);
						svcSearchResult.setMessage("Education remarks saved successfully.");
					} else {
						svcSearchResult.setData(true);
						svcSearchResult.setOutcome(false);
						svcSearchResult.setMessage(
								messageSource.getMessage("msg.error", null, LocaleContextHolder.getLocale()));
					}
				}
			} else {
				Optional<QualificationMaster> qualificationMasterobj = qualificationMasterRepository
						.findById(approvalStatusRemarkDto.getQualificationId());
				System.out.println("qualificationMasterobj====================" + qualificationMasterobj);

				candidateCafEducation = new CandidateCafEducation();
				candidateCafEducation.setCandidate(
						candidateRepository.findByCandidateCode(approvalStatusRemarkDto.getCandidateCode()));
				candidateCafEducation.setCandidateStatus(candidateStatusRepository
						.findByCandidateCandidateCode(approvalStatusRemarkDto.getCandidateCode()));
				candidateCafEducation.setCreatedOn(new Date());
				candidateCafEducation.setIsHighestQualification(false);
				if (qualificationMasterobj.isPresent()) {
					QualificationMaster qualificationMasterobj1 = qualificationMasterobj.get();
					String qualificationName = qualificationMasterobj1.getQualificationName();
					candidateCafEducation.setCourseName(qualificationName);
				}

				BeanUtils.copyProperties(approvalStatusRemarkDto, candidateCafEducation);
				candidateCafEducation.setSchoolOrCollegeName(approvalStatusRemarkDto.getSchoolOrCollegeName());
				candidateCafEducation.setBoardOrUniversityName(approvalStatusRemarkDto.getBoardOrUniversityName());
				// candidateCafEducation.setQualificationMaster(approvalStatusRemarkDto.getQualificationMaster());
				candidateCafEducation.setQualificationMaster(
						qualificationMasterRepository.findById(approvalStatusRemarkDto.getQualificationId()).get());
				// candidateCafEducation.setColor(colorRepository.findByColorCode("AMBER"));
				candidateCafEducation.setColor(colorRepository.findById(approvalStatusRemarkDto.getColorId()).get());
				candidateCafEducation.setRemarkMaster(remarkMasterRepository.findByRemarkCode("ALL"));
				candidateCafEducationRepository.save(candidateCafEducation);
				svcSearchResult.setMessage("Education details added successfully.");
				// System.out.println("***********************candidateCafEducation"+candidateCafEducation);

			}
		} catch (Exception ex) {
			log.error("Exception occured in updateCandidateEducationStatusAndRemark method in CandidateServiceImpl-->",
					ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<Boolean> updateCandidateExperienceStatusAndRemark(
			ApprovalStatusRemarkDto approvalStatusRemarkDto) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		CandidateCafExperience candidateCafExperience = null;
		System.out.println("...............updateCandidateExperienceStatusAndRemark" + approvalStatusRemarkDto);
		try {
			User user = SecurityHelper.getCurrentUser();
			if (approvalStatusRemarkDto.getId() != null) {
				Optional<CandidateCafExperience> candidateCafExperienceObj1 = candidateCafExperienceRepository
						.findById(approvalStatusRemarkDto.getId());
				if (candidateCafExperienceObj1.isPresent()) {
					CandidateCafExperience candidateCafExperienceobj = candidateCafExperienceObj1.get();

					candidateCafExperienceobj
							.setCandidateEmployerName(approvalStatusRemarkDto.getCandidateEmployerName());
					candidateCafExperienceobj.setInputDateOfJoining(
							DateUtil.getDate(approvalStatusRemarkDto.getInputDateOfJoining(), "yyyy-MM-dd"));
					candidateCafExperienceobj.setInputDateOfExit(
							DateUtil.getDate(approvalStatusRemarkDto.getInputDateOfExit(), "yyyy-MM-dd"));

					// candidateCafExperienceobj.setColor("GREEN");
					// candidateCafExperienceobj.setRemarkMaster(remarkMasterRepository.findById(approvalStatusRemarkDto.getRemarkId()).get());
					// candidateCafExperienceobj.setColor(colorRepository.findById(approvalStatusRemarkDto.getColorId()).get());
					// candidateCafExperienceobj.setRemarkMaster(remarkMasterRepository.findById(approvalStatusRemarkDto.getRemarkId()).get());
					candidateCafExperienceobj.setCustomRemark(approvalStatusRemarkDto.getCustomRemark());
					candidateCafExperienceobj.setLastUpdatedOn(new Date());
					candidateCafExperienceobj.setLastUpdatedBy(user);
					if (approvalStatusRemarkDto.getColorId() != null) {
						candidateCafExperienceobj
								.setColor((colorRepository.findById(approvalStatusRemarkDto.getColorId())).get());
					}
					candidateCafExperience = candidateCafExperienceRepository.save(candidateCafExperienceobj);
					if (candidateCafExperience != null) {
						svcSearchResult.setData(true);
						svcSearchResult.setOutcome(true);
						svcSearchResult.setMessage("Experience remarks update successfully.");

					} else {
						svcSearchResult.setData(true);
						svcSearchResult.setOutcome(false);
						svcSearchResult.setMessage(
								messageSource.getMessage("msg.error", null, LocaleContextHolder.getLocale()));
					}
				}

			} else {
				Optional<Organization> organization = organizationRepository
						.findById(approvalStatusRemarkDto.getOrganizationid());
				if (organization.isPresent()) {
					Organization orgObj = organization.get();
					Long orgid = orgObj.getOrganizationId();
					System.out.println(orgObj.getOrganizationId());
					List<SuspectEmpMaster> suspectEmpMaster = suspectEmpMasterRepository
							.findAllByOrganizationOrganizationIdAndIsActiveTrue(orgid);
					if (!suspectEmpMaster.isEmpty()) {
						int i = 0;
						int j = 0;
						String[] arrOfStr = null;
						String[] giveemploy = null;
						for (SuspectEmpMaster SuspectEmp : suspectEmpMaster) {
							String removenumbers = approvalStatusRemarkDto.getCandidateEmployerName().toLowerCase();
							String employnumbers = SuspectEmp.getSuspectCompanyName().toLowerCase();
							arrOfStr = employnumbers.split(" ", 8);
							giveemploy = removenumbers.split(" ", 8);
							System.out.println(removenumbers + "===========" + employnumbers);
							if (arrOfStr[0].equals(giveemploy[0])) {
								System.out.println("first string matching");
								i++;

							}
							if (giveemploy.length != 1 && arrOfStr.length != 1) {
								if (arrOfStr[1].equals(giveemploy[1]) && arrOfStr[0].equals(giveemploy[0])) {
									System.out.println("second string matching");
									j++;
								}
							}
							System.out.println("length" + giveemploy.length);

						}
						System.out.println(i + "===========" + j);
						candidateCafExperience = new CandidateCafExperience();
						candidateCafExperience.setCandidate(
								candidateRepository.findByCandidateCode(approvalStatusRemarkDto.getCandidateCode()));
						candidateCafExperience.setCandidateStatus(candidateStatusRepository
								.findByCandidateCandidateCode(approvalStatusRemarkDto.getCandidateCode()));
						candidateCafExperience.setCreatedOn(new Date());
						candidateCafExperience.setCreatedBy(user);
						candidateCafExperience
								.setCandidateEmployerName(approvalStatusRemarkDto.getCandidateEmployerName());
						candidateCafExperience.setCustomRemark(approvalStatusRemarkDto.getCustomRemark());
						if (giveemploy.length != 1 && arrOfStr.length != 1) {
							if (i >= 1 && j >= 1) {
								candidateCafExperience.setColor(colorRepository.findByColorCode("RED"));

							} else {
								candidateCafExperience.setColor(colorRepository.findByColorCode("AMBER"));
							}
						} else {
							if (i >= 1) {
								candidateCafExperience.setColor(colorRepository.findByColorCode("RED"));

							} else {
								candidateCafExperience.setColor(colorRepository.findByColorCode("AMBER"));
							}

						}

						candidateCafExperience.setRemarkMaster(remarkMasterRepository.findByRemarkCode("ALL"));
						candidateCafExperience.setInputDateOfJoining(
								DateUtil.getDate(approvalStatusRemarkDto.getInputDateOfJoining(), "yyyy-MM-dd"));
						candidateCafExperience.setInputDateOfExit(
								DateUtil.getDate(approvalStatusRemarkDto.getInputDateOfExit(), "yyyy-MM-dd"));

						if (approvalStatusRemarkDto.getColorId() != null) {
							candidateCafExperience
									.setColor((colorRepository.findById(approvalStatusRemarkDto.getColorId())).get());
						}

						candidateCafExperienceRepository.save(candidateCafExperience);
						if (candidateCafExperience != null) {
							svcSearchResult.setData(true);
							svcSearchResult.setOutcome(true);
							svcSearchResult.setMessage("Experience saved successfully.");

						} else {
							svcSearchResult.setData(true);
							svcSearchResult.setOutcome(false);
							svcSearchResult.setMessage(
									messageSource.getMessage("msg.error", null, LocaleContextHolder.getLocale()));
						}

					} else {
						candidateCafExperience = new CandidateCafExperience();
						candidateCafExperience.setCandidate(
								candidateRepository.findByCandidateCode(approvalStatusRemarkDto.getCandidateCode()));
						candidateCafExperience.setCandidateStatus(candidateStatusRepository
								.findByCandidateCandidateCode(approvalStatusRemarkDto.getCandidateCode()));
						candidateCafExperience.setCreatedOn(new Date());
						candidateCafExperience.setCreatedBy(user);
						candidateCafExperience
								.setCandidateEmployerName(approvalStatusRemarkDto.getCandidateEmployerName());
						candidateCafExperience.setColor(colorRepository.findByColorCode("AMBER"));
						candidateCafExperience.setRemarkMaster(remarkMasterRepository.findByRemarkCode("ALL"));
						candidateCafExperience.setInputDateOfJoining(
								DateUtil.getDate(approvalStatusRemarkDto.getInputDateOfJoining(), "yyyy-MM-dd"));
						candidateCafExperience.setInputDateOfExit(
								DateUtil.getDate(approvalStatusRemarkDto.getInputDateOfExit(), "yyyy-MM-dd"));

						if (approvalStatusRemarkDto.getColorId() != null) {
							candidateCafExperience
									.setColor((colorRepository.findById(approvalStatusRemarkDto.getColorId())).get());
						}

						candidateCafExperienceRepository.save(candidateCafExperience);
						if (candidateCafExperience != null) {
							svcSearchResult.setData(true);
							svcSearchResult.setOutcome(true);
							svcSearchResult.setMessage("Organization Not Found, Experience saved successfully.");

						} else {
							svcSearchResult.setData(true);
							svcSearchResult.setOutcome(false);
							svcSearchResult.setMessage(
									messageSource.getMessage("msg.error", null, LocaleContextHolder.getLocale()));
						}

					}
				}

			}
		} catch (Exception ex) {
			log.error("Exception occured in updateCandidateExperienceStatusAndRemark method in CandidateServiceImpl-->",
					ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<Boolean> updateCandidateAddressStatusAndRemark(
			ApprovalStatusRemarkDto approvalStatusRemarkDto) {
		System.out.println("***********************approvalStatusRemarkDto" + approvalStatusRemarkDto);
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		CandidateCafAddress candidateCafAddress = null;
		try {
			User user = SecurityHelper.getCurrentUser();
			if (approvalStatusRemarkDto.getId() != null) {
				System.out.println("*********%%%%%%%%%%%%%%%%%%%%%%%%%%%%" + approvalStatusRemarkDto.getId());
				Optional<CandidateCafAddress> candidateCafAddressObj1 = candidateCafAddressRepository
						.findById(approvalStatusRemarkDto.getId());
				if (candidateCafAddressObj1.isPresent()) {
					System.out.println("*********%%%%%%%%%%%%%%%%");
					CandidateCafAddress candidateCafAddressObj = candidateCafAddressObj1.get();
					// candidateCafAddressObj.setCandidateCafAddressId(candidateCafAddressRepository.findById(approvalStatusRemarkDto.getCandidateCafAddressId()).get());
					candidateCafAddressObj
							.setColor(colorRepository.findById(approvalStatusRemarkDto.getColorId()).get());
					if (approvalStatusRemarkDto.getRemarkId() != null)
						candidateCafAddressObj.setRemarkMaster(
								remarkMasterRepository.findById(approvalStatusRemarkDto.getRemarkId()).get());

					candidateCafAddressObj
							.setIsAssetDeliveryAddress(approvalStatusRemarkDto.getIsAssetDeliveryAddress() != null
									? approvalStatusRemarkDto.getIsAssetDeliveryAddress()
									: candidateCafAddressObj.getIsAssetDeliveryAddress());
					candidateCafAddressObj.setIsPermanentAddress(approvalStatusRemarkDto.getIsPermanentAddress() != null
							? approvalStatusRemarkDto.getIsPermanentAddress()
							: candidateCafAddressObj.getIsAssetDeliveryAddress());
					candidateCafAddressObj.setIsPresentAddress(approvalStatusRemarkDto.getIsPresentAddress() != null
							? approvalStatusRemarkDto.getIsPresentAddress()
							: candidateCafAddressObj.getIsAssetDeliveryAddress());
					candidateCafAddressObj.setCustomRemark(approvalStatusRemarkDto.getCustomRemark());
					candidateCafAddressObj.setLastUpdatedOn(new Date());
					candidateCafAddressObj.setLastUpdatedBy(user);
					System.out.println("***********************candidateCafAddressObj" + candidateCafAddressObj);
					candidateCafAddress = candidateCafAddressRepository.save(candidateCafAddressObj);

					if (candidateCafAddress != null) {
						svcSearchResult.setData(true);
						svcSearchResult.setOutcome(true);
						svcSearchResult.setMessage("Address remarks saved successfully.");
					} else {
						svcSearchResult.setData(true);
						svcSearchResult.setOutcome(false);
						svcSearchResult.setMessage(
								messageSource.getMessage("msg.error", null, LocaleContextHolder.getLocale()));
					}
				}
			}
		} catch (Exception ex) {
			log.error("Exception occured in updateCandidateAddressStatusAndRemark method in CandidateServiceImpl-->",
					ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Transactional
	@Override
	public ServiceOutcome<Boolean> candidateApplicationFormApproved(String candidateCode,
			MultipartFile criminalVerificationDocument, Long criminalVerificationColorId,
			MultipartFile globalDatabseCaseDetailsDocument, Long globalDatabseCaseDetailsColorId, String reportType) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		try {
			System.out.println("criminalVerificationDocument" + criminalVerificationDocument);
			Candidate candidate = candidateRepository.findByCandidateCode(candidateCode);
			User user = SecurityHelper.getCurrentUser();
			if (criminalVerificationDocument != null || globalDatabseCaseDetailsDocument != null) {
				System.out.println("criminalVerificationDocument" + criminalVerificationDocument);
				CandidateCaseDetails existingCaseDetails = candidateCaseDetailsRepository
						.findByCandidateCandidateCode(candidateCode);

				CandidateCaseDetails candidateCaseDetails = new CandidateCaseDetails();
				if (existingCaseDetails != null) {
					candidateCaseDetails = existingCaseDetails;
				}

				candidateCaseDetails.setCandidate(candidate);

				if (criminalVerificationDocument != null) {
					candidateCaseDetails.setCriminalVerificationDocument(
							criminalVerificationDocument != null ? criminalVerificationDocument.getBytes() : null);
					candidateCaseDetails.setCriminalVerificationisExist(criminalVerificationColorId != null
							? colorRepository.findById(criminalVerificationColorId).get()
							: null);
				}

				if (globalDatabseCaseDetailsDocument != null) {
					candidateCaseDetails.setGlobalDatabaseCaseDetailsDocument(
							globalDatabseCaseDetailsDocument != null ? globalDatabseCaseDetailsDocument.getBytes()
									: null);
					candidateCaseDetails.setGlobalDatabaseCaseDetailsIsExist(globalDatabseCaseDetailsColorId != null
							? colorRepository.findById(globalDatabseCaseDetailsColorId).get()
							: null);
				}

				candidateCaseDetails.setCreatedOn(new Date());
				candidateCaseDetails.setCreatedBy(user);
				CandidateCaseDetails result = candidateCaseDetailsRepository.save(candidateCaseDetails);
			}

			if (reportType.equals("INTERIMREPORT")) {
				candidate.setApprovalRequired(true);
			} else {
				candidate.setApprovalRequired(false);
			}
			candidateRepository.save(candidate);
			CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(candidateCode);
			if (reportType.equals("INTERIMREPORT")) {
				candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("INTERIMREPORT"));
			} else {
				candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("FINALREPORT"));
			}

			candidateStatus.setLastUpdatedOn(new Date());
			candidateStatus.setLastUpdatedBy(user);
			candidateStatusRepository.save(candidateStatus);
			createCandidateStatusHistory(candidateStatus, "NOTCANDIDATE");
			svcSearchResult.setData(true);
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage("Candidate application form approved successfully.");
			CompletableFuture.runAsync(() -> {

				reportService.generateDocument(candidateCode, "", ReportType.FINAL);

			});

		} catch (Exception ex) {
			log.error("Exception occured in candidateApplicationFormApproved method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Transactional
	@Override
	public ServiceOutcome<Boolean> saveFakeCompanyDetails(MultipartFile file, Long organizationId) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		try {
			List<SuspectEmpMaster> suspectEmpMasterListObj = null;

			// updated
			if (CSVUtil.hasCSVFormat(file)) {
				suspectEmpMasterListObj = cSVUtil.csvToSuspectEmpMaster(file.getInputStream(), organizationId);

			}

			if (ExcelUtil.hasExcelFormat(file)) {
				suspectEmpMasterListObj = excelUtil.excelToSuspectEmpMaster(file.getInputStream(), organizationId);
			}

			// TODO need to check here!!!

			List<SuspectEmpMaster> suspectEmpMasterList = suspectEmpMasterRepository
					.saveAllAndFlush(suspectEmpMasterListObj);
			if (!suspectEmpMasterList.isEmpty()) {
				svcSearchResult.setData(true);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("Uploaded the file successfully: " + file.getOriginalFilename());
			}
		} catch (IOException e) {
			log.error("Exception occured in saveFakeCompanyDetails method in CandidateServiceImpl-->" + e);
			throw new RuntimeException("fail to store csv/xls data: " + e.getMessage());
		}
		return svcSearchResult;
	}

	@Transactional
	@Override
	public ServiceOutcome<Boolean> saveFakeCollegeDetails(MultipartFile file) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		try {
			List<SuspectClgMaster> suspectClgMasterListObj = null;
			if (ExcelUtil.hasExcelFormat(file)) {
				suspectClgMasterListObj = excelUtil.excelToSuspectClgMaster(file.getInputStream());
			}
			List<SuspectClgMaster> suspectClgMasterList = suspectClgMasterRepository
					.saveAllAndFlush(suspectClgMasterListObj);
			if (!suspectClgMasterList.isEmpty()) {
				svcSearchResult.setData(true);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("Uploaded the file successfully: " + file.getOriginalFilename());
			}
		} catch (IOException e) {
			log.error("Exception occured in saveFakeCollegeDetails method in CandidateServiceImpl-->" + e);
			throw new RuntimeException("fail to store csv/xls data: " + e.getMessage());
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<List<SuspectClgMaster>> getAllSuspectClgList() {
		ServiceOutcome<List<SuspectClgMaster>> svcSearchResult = new ServiceOutcome<List<SuspectClgMaster>>();
		try {
			SuspectClgMaster suspectClgMaster = suspectClgMasterRepository.findById(0L).get();
			List<SuspectClgMaster> suspectClgMasterList = suspectClgMasterRepository
					.findAllByIsActiveTrueOrderBySuspectInstitutionNameAsc();
			if (!suspectClgMasterList.isEmpty() && suspectClgMaster != null) {
				List<SuspectClgMaster> newList = new ArrayList<SuspectClgMaster>();
				newList.add(suspectClgMaster);
				newList.addAll(suspectClgMasterList);
				svcSearchResult.setData(newList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NOT FOUND");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getAllSuspectClgList method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<List<SuspectEmpMaster>> getAllSuspectEmpList(Long organizationId) {
		System.out.println("--------------++++++===---------IMPPPorganizationId" + organizationId);
		ServiceOutcome<List<SuspectEmpMaster>> svcSearchResult = new ServiceOutcome<List<SuspectEmpMaster>>();
		try {
			System.out.println("+++++++++++++++++++++++++TRY" + organizationId);
			Optional<Organization> organization = organizationRepository.findById(organizationId);
			System.out.println("=====================LIST" + organization);
			// System.out.println("--------**********-------------suspectEmpMasterList"+suspectEmpMasterList);
			if (organization.isPresent()) {

				Organization orgObj = organization.get();
				Long orgid = orgObj.getOrganizationId();
				System.out.println(orgObj.getOrganizationId());
				List<SuspectEmpMaster> suspectEmpMaster = suspectEmpMasterRepository
						.findAllByOrganizationOrganizationId(orgid);
				System.out.println("===================***********==LISTsuspectEmpMaster" + suspectEmpMaster);

				if (!suspectEmpMaster.isEmpty()) {

					List<SuspectEmpMaster> newList = new ArrayList<SuspectEmpMaster>();
					newList.addAll(suspectEmpMaster);
					svcSearchResult.setData(newList);
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage("SUCCESS");
				} else {
					svcSearchResult.setData(null);
					svcSearchResult.setOutcome(false);
					svcSearchResult.setMessage("NOT FOUND");
				}
			}

		} catch (Exception ex) {
			log.error("Exception occured in getAllSuspectEmpList method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Transactional
	@Override
	public ServiceOutcome<Boolean> relationshipAddressVerification(String candidateCafRelation,
			MultipartFile document) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		CandidateCafRelationship candidateCafRelationshipObj = null;

		try {
			CandidateCafRelationshipDto candidateCafRelationDto = new ObjectMapper().readValue(candidateCafRelation,
					CandidateCafRelationshipDto.class);
			if (StringUtils.isNotBlank(candidateCafRelationDto.getCandidateCode())) {
				Candidate candidate = candidateRepository
						.findByCandidateCode(candidateCafRelationDto.getCandidateCode());
				CandidateCafRelationship candidateCafRelationship = new CandidateCafRelationship();
				CandidateStatus candidateStatus = candidateStatusRepository
						.findByCandidateCandidateCode(candidateCafRelationDto.getCandidateCode());
				candidateCafRelationship.setCandidateStatus(candidateStatus);
				candidateCafRelationship.setCandidateRelationship(candidateCafRelationDto.getCandidateRelationship());
				candidateCafRelationship.setCreatedOn(new Date());
				if (candidateCafRelationDto.getCandidateRelationship().equals("LANDLORD")) {
					// TODO upload file to s3
					candidateCafRelationship.setDocumentUploaded(document != null ? document.getBytes() : null);
					candidateCafRelationship.setRentType(candidateCafRelationDto.getRentType());
					candidateCafRelationshipObj = candidateCafRelationshipRepository.save(candidateCafRelationship);
				} else {
					Boolean result = emailSentTask.sendRelationshipEmail(candidateCafRelationDto.getCandidateCode(),
							candidate.getCandidateName(), candidateCafRelationDto.getCandidateRelationshipEmail());
					if (result) {
						candidateCafRelationshipObj = candidateCafRelationshipRepository.save(candidateCafRelationship);
					}
				}
				if (candidateCafRelationshipObj != null) {
					CandidateAdressVerification candidateAdressVerification = new CandidateAdressVerification();
					candidateAdressVerification.setCandidateCafRelationship(candidateCafRelationshipObj);
					candidateAdressVerification
							.setVerificationEmailId(candidateCafRelationDto.getCandidateRelationshipEmail());
					candidateAdressVerification.setDateOfAdressVerification(new Date());
					candidateAdressVerification.setCandidate(candidate);
					candidateAdressVerification.setCandidateStatus(candidateStatus);
					candidateAdressVerification.setCreatedOn(new Date());
					candidateAdressVerification = candidateAdressVerificationRepository
							.save(candidateAdressVerification);

					if (candidateCafRelationDto.getCandidateRelationship().equals("LANDLORD")) {
						CandidateCafAddress address = new CandidateCafAddress();
						address.setCandidate(candidate);
						address.setColor(colorRepository.findByColorCode("AMBER"));
						address.setCreatedOn(new Date());
						address.setName("LANDLORD");
						address.setAddressVerification(candidateAdressVerification);
						candidateCafAddressRepository.save(address);
					}
					candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("RELATIVEADDRESS"));
					candidateStatus.setLastUpdatedOn(new Date());
					CandidateStatus candidatestatus = candidateStatusRepository.save(candidateStatus);
					createCandidateStatusHistory(candidatestatus, "CANDIDATE");
					svcSearchResult.setData(true);
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage("Relationship address details saved successfully.");
				} else {
					svcSearchResult.setData(null);
					svcSearchResult.setOutcome(false);
					svcSearchResult
							.setMessage(messageSource.getMessage("msg.error", null, LocaleContextHolder.getLocale()));
				}
			}

		} catch (Exception ex) {
			log.error("Exception occured in relationshipAddressVerification method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<DashboardDto> getPendingDetailsStatusAndCount(DashboardDto dashboardDto) {
		ServiceOutcome<DashboardDto> svcSearchResult = new ServiceOutcome<DashboardDto>();
		List<CandidateStatusCountDto> candidateStatusCountDtoList = new ArrayList<CandidateStatusCountDto>();
		List<CandidateStatus> candidateStatusList = null;
		String strToDate = "";
		String strFromDate = "";
		try {
			if (dashboardDto.getUserId() != null) {
				User user = userRepository.findById(dashboardDto.getUserId()).get();
				strToDate = dashboardDto.getToDate() != null ? dashboardDto.getToDate()
						: ApplicationDateUtils.getStringTodayAsDDMMYYYY();
				strFromDate = dashboardDto.getFromDate() != null ? dashboardDto.getFromDate()
						: ApplicationDateUtils.subtractNoOfDaysFromDateAsDDMMYYYY(
								new SimpleDateFormat("dd/MM/yyyy").parse(strToDate), 7);
				ServiceOutcome<List<CandidateStatus>> svcOutCome = getCandidateStatusList(strToDate, strFromDate,
						dashboardDto.getUserId());
				candidateStatusList = svcOutCome.getData();

				ServiceOutcome<List<String>> configCodes = candidateService.getServiceConfigCodes(null,
						user.getOrganization().getOrganizationId());

				StatusMaster invStatusMaster = statusMasterRepository.findByStatusCode("INVITATIONSENT");
				StatusMaster digiStatusMaster = statusMasterRepository.findByStatusCode("DIGILOCKER");
				StatusMaster itrStatusMaster = statusMasterRepository.findByStatusCode("ITR");
				StatusMaster epfoStatusMaster = statusMasterRepository.findByStatusCode("EPFO");
				StatusMaster relStatusMaster = statusMasterRepository.findByStatusCode("RELATIVEADDRESS");
				StatusMaster canStatusMaster = statusMasterRepository.findByStatusCode("PROCESSDECLINED");

				List<CandidateStatus> invitationSentList = candidateStatusList != null ? candidateStatusList.stream()
						.filter(c -> c.getStatusMaster().getStatusCode().equals("INVITATIONSENT"))
						.collect(Collectors.toList()) : null;
				List<CandidateStatus> digiList = candidateStatusList != null ? candidateStatusList.stream()
						.filter(c -> c.getCandidate().getIsUanSkipped() != null
								? c.getStatusMaster().getStatusCode().equals("DIGILOCKER")
										&& !c.getCandidate().getIsUanSkipped()
								: c.getStatusMaster().getStatusCode().equals("DIGILOCKER"))
						.collect(Collectors.toList()) : null;
				candidateStatusCountDtoList.add(new CandidateStatusCountDto(invStatusMaster.getStatusName(),
						invStatusMaster.getStatusCode(), invitationSentList.size()));
				candidateStatusCountDtoList.add(new CandidateStatusCountDto(digiStatusMaster.getStatusName(),
						digiStatusMaster.getStatusCode(), digiList.size()));
				if (configCodes.getOutcome()) {
					if (configCodes.getData().contains("ITR")) {
						List<CandidateStatus> itrList = candidateStatusList != null ? candidateStatusList.stream()
								.filter(c -> c.getCandidate().getIsUanSkipped() != null
										? c.getStatusMaster().getStatusCode().equals("ITR")
												&& !c.getCandidate().getIsUanSkipped()
										: c.getStatusMaster().getStatusCode().equals("ITR"))
								.collect(Collectors.toList()) : null;
						candidateStatusCountDtoList.add(new CandidateStatusCountDto(itrStatusMaster.getStatusName(),
								itrStatusMaster.getStatusCode(), itrList.size()));
					}
					if (configCodes.getData().contains("EPFO")) {
						List<CandidateStatus> epfoList = candidateStatusList != null ? candidateStatusList.stream()
								.filter(c -> c.getStatusMaster().getStatusCode().equals("EPFO"))
								.collect(Collectors.toList()) : null;
						candidateStatusCountDtoList.add(new CandidateStatusCountDto(epfoStatusMaster.getStatusName(),
								epfoStatusMaster.getStatusCode(), epfoList.size()));
						List<CandidateStatus> epfoSkippedList = candidateStatusList != null
								? candidateStatusList.stream()
										.filter(c -> c.getCandidate().getIsUanSkipped() != null
												? c.getStatusMaster().getStatusCode().equals("DIGILOCKER")
														&& c.getCandidate().getIsUanSkipped()
														|| c.getStatusMaster().getStatusCode().equals("ITR")
																&& c.getCandidate().getIsUanSkipped()
												: false)
										.collect(Collectors.toList())
								: null;
						candidateStatusCountDtoList.add(
								new CandidateStatusCountDto("EPFO Skipped", "EPFOSKIPPED", epfoSkippedList.size()));
					}
					if (configCodes.getData().contains("RELBILLTRUE")) {
						List<CandidateStatus> relList = candidateStatusList != null
								? candidateStatusList.stream()
										.filter(c -> c.getStatusMaster().getStatusCode().equals("RELATIVEADDRESS"))
										.collect(Collectors.toList())
								: null;
						List<CandidateStatus> newRelList = new ArrayList<>();
						for (CandidateStatus rel : relList) {
							String code = rel.getCandidate().getCandidateCode();
							Long count = candidateCafAddressRepository
									.findCountByCandidateCodeAndRelAddrVerification(code);
							if (count > 0) {
								newRelList.add(rel);
							}
						}
						candidateStatusCountDtoList.add(new CandidateStatusCountDto(relStatusMaster.getStatusName(),
								relStatusMaster.getStatusCode(), newRelList.size()));
					}
				}
				if (configCodes.getData().contains("RELBILLTRUE")) {
					List<CandidateStatus> cafList = candidateStatusList != null ? candidateStatusList.stream()
							.filter(c -> c.getStatusMaster().getStatusCode().equals("RELATIVEADDRESS"))
							.collect(Collectors.toList()) : null;
					List<CandidateStatus> newRelList = new ArrayList<>();
					for (CandidateStatus rel : cafList) {
						String code = rel.getCandidate().getCandidateCode();
						Long count = candidateCafAddressRepository.findCountByCandidateCodeAndRelAddrVerification(code);
						if (count > 0) {
							newRelList.add(rel);
						}
					}
					candidateStatusCountDtoList.add(new CandidateStatusCountDto("CAF Pending", "CAFPENDING",
							cafList != null ? newRelList.size() : 0));
				} else if (configCodes.getData().contains("RELBILLFALSE") && configCodes.getData().contains("EPFO")) {
					List<CandidateStatus> cafList = candidateStatusList != null ? candidateStatusList.stream()
							.filter(c -> c.getStatusMaster().getStatusCode().equals("EPFO"))
							.collect(Collectors.toList()) : null;
					candidateStatusCountDtoList.add(new CandidateStatusCountDto("CAF Pending", "CAFPENDING",
							cafList != null ? cafList.size() : 0));
				} else if (configCodes.getData().contains("RELBILLFALSE") && !configCodes.getData().contains("EPFO")
						&& configCodes.getData().contains("ITR")) {
					List<CandidateStatus> cafList = candidateStatusList != null ? candidateStatusList.stream()
							.filter(c -> c.getStatusMaster().getStatusCode().equals("ITR")).collect(Collectors.toList())
							: null;
					candidateStatusCountDtoList.add(new CandidateStatusCountDto("CAF Pending", "CAFPENDING",
							cafList != null ? cafList.size() : 0));
				} else if (configCodes.getData().contains("RELBILLFALSE") && !configCodes.getData().contains("EPFO")
						&& !configCodes.getData().contains("ITR")) {
					List<CandidateStatus> cafList = candidateStatusList != null ? candidateStatusList.stream()
							.filter(c -> c.getStatusMaster().getStatusCode().equals("DIGILOCKER"))
							.collect(Collectors.toList()) : null;
					candidateStatusCountDtoList.add(new CandidateStatusCountDto("CAF Pending", "CAFPENDING",
							cafList != null ? cafList.size() : 0));
				}

				List<CandidateStatus> canList = candidateStatusList != null ? candidateStatusList.stream()
						.filter(c -> c.getStatusMaster().getStatusCode().equals("PROCESSDECLINED"))
						.collect(Collectors.toList()) : null;
				candidateStatusCountDtoList.add(new CandidateStatusCountDto(canStatusMaster.getStatusName(),
						canStatusMaster.getStatusCode(), canList.size()));
				DashboardDto dashboardDtoObj = new DashboardDto(strFromDate, strToDate, null, null,
						candidateStatusCountDtoList, dashboardDto.getUserId(), null, null);
				svcSearchResult.setData(dashboardDtoObj);
				svcSearchResult.setOutcome(true);
				svcSearchResult
						.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("please specify user.");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getPendingDetailsStatusAndCount method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<Candidate> saveIsFresher(String candidateCode, Boolean isFresher) {
		ServiceOutcome<Candidate> svcSearchResult = new ServiceOutcome<Candidate>();
		try {
			Candidate candidate = candidateRepository.findByCandidateCode(candidateCode);
			candidate.setIsFresher(isFresher);
			candidateRepository.save(candidate);

			svcSearchResult.setData(candidate);
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage("Data saved successfully.");

		} catch (Exception e) {
			log.error("Exception occured in saveIsFresher method in CandidateServiceImpl-->", e);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<CandidationApplicationFormDto> candidateApplicationFormDetailsExceptCandidate(
			String candidateCode) {
		ServiceOutcome<CandidationApplicationFormDto> svcSearchResult = new ServiceOutcome<CandidationApplicationFormDto>();
		CandidationApplicationFormDto candidationApplicationFormDto = new CandidationApplicationFormDto();
		List<CandidateCafEducationDto> candidateCafEducationDtoList = new ArrayList<CandidateCafEducationDto>();
		List<CandidateCafExperienceDto> candidateCafExperienceDtoList = new ArrayList<CandidateCafExperienceDto>();
		List<CandidateCafAddressDto> candidateCafAddressDtoList = new ArrayList<CandidateCafAddressDto>();
		List<ITRDataFromApiDto> iTRDataFromApiDtoList = new ArrayList<ITRDataFromApiDto>();
		List<ContentFileDto> docDtoList = new ArrayList<ContentFileDto>();
		List<VendorUploadChecksDto> vendordocDtoList = new ArrayList<VendorUploadChecksDto>();
		CandidateFileDto candidateFileDto = null;
		ContentFileDto contentFileDto = null;
		VendorUploadChecksDto vendorUploadChecksDto = null;
		CandidateCaseDetailsDTO candidateCaseDetailsDTO = null;
		try {
			if (StringUtils.isNotEmpty(candidateCode)) {
				Candidate candidate = candidateRepository.findByCandidateCode(candidateCode);
				Long candidateId = candidate.getCandidateId();

				IdItemsDto candidateIdItemPan = candidateIdItemsRepository
						.findByCandidateCandidateCodeAndServiceSourceMasterServiceCode(candidateCode, "PAN");
				if (candidateIdItemPan != null) {
					candidate.setCandidateName(candidateIdItemPan.getIdHolder());
				} else {
					IdItemsDto candidateIdItemAadhar = candidateIdItemsRepository
							.findByCandidateCandidateCodeAndServiceSourceMasterServiceCode(candidateCode, "AADHARID");
					candidate.setCandidateName(candidateIdItemAadhar.getIdHolder());
				}
				candidationApplicationFormDto.setCandidate(candidate);
				List<CandidateCafEducation> candidateCafEducationList = candidateCafEducationRepository
						.findAllByCandidateCandidateCode(candidateCode);
				if (!candidateCafEducationList.isEmpty()) {
					candidateCafEducationDtoList = candidateCafEducationList.stream()
							.map(candidateCafEducation -> modelMapper.map(candidateCafEducation,
									CandidateCafEducationDto.class))
							.collect(Collectors.toList());
					candidationApplicationFormDto.setCandidateCafEducationDto(candidateCafEducationDtoList);
				}
				List<CandidateCafExperience> candidateCafExperienceList = candidateCafExperienceRepository
						.findAllByCandidateCandidateCodeOrderByInputDateOfJoiningDesc(candidateCode);

				List<CandidateCafExperience> efpolist = candidateCafExperienceList != null
						? candidateCafExperienceList.stream()
								.filter(c -> c.getServiceSourceMaster() != null
										&& c.getServiceSourceMaster().getServiceCode().equals("EPFO"))
								.collect(Collectors.toList())
						: new ArrayList<>();
				List<Long> itrIds = candidateCafExperienceList != null
						? candidateCafExperienceList.stream()
								.filter(c -> c.getServiceSourceMaster() != null
										&& c.getServiceSourceMaster().getServiceCode().equals("ITR"))
								.map(f -> f.getCandidateCafExperienceId()).collect(Collectors.toList())
						: new ArrayList<>();

				Boolean dojNull = false; // added to avoid null doj value while sorting
				for (CandidateCafExperience efpo : efpolist) {
					System.out.println(efpo.getInputDateOfJoining() + "datessss");
					if (efpo.getInputDateOfJoining() == null) {
						dojNull = true;
					}
					Date inputJoiningDate = efpo.getInputDateOfJoining();
					Date inputExitDate = efpo.getInputDateOfExit() == null ? new Date() : efpo.getInputDateOfExit();
					List<Long> itrRemoveIdsList = candidateCafExperienceRepository
							.findByCandidateCodeAndServiceSourceMasterCodeAndDates(candidateCode, "ITR",
									inputJoiningDate, inputExitDate);
					itrIds.removeAll(itrRemoveIdsList);
				}

				List<CandidateCafExperience> itrList = candidateCafExperienceList.stream()
						.filter(c -> itrIds.contains(c.getCandidateCafExperienceId())).collect(Collectors.toList());
				efpolist.addAll(itrList);
				List<CandidateCafExperience> newList = candidateCafExperienceList.stream()
						.filter(c -> c.getServiceSourceMaster() == null).collect(Collectors.toList());
				efpolist.addAll(newList);
				if (!dojNull) {
					efpolist.sort(Comparator.comparing(CandidateCafExperience::getInputDateOfJoining));
				}
				Collections.reverse(efpolist);
				if (!efpolist.isEmpty()) {
					Date dateWith1Days = null;
					Date doee = null;
					for (CandidateCafExperience candidateCafExperience : efpolist) {
						System.out.println(candidateCafExperience.getInputDateOfJoining() + "dateeeeeeee");
						CandidateCafExperienceDto candidateCafExperienceDto = this.modelMapper
								.map(candidateCafExperience, CandidateCafExperienceDto.class);
						if (candidateCafExperience.getInputDateOfJoining() != null) {
							Date doj = candidateCafExperience.getInputDateOfJoining();
							Calendar cal = Calendar.getInstance();
							cal.setTime(doj);
							cal.add(Calendar.DATE, 1);
							dateWith1Days = cal.getTime();
							System.out.println(dateWith1Days + "doj");
							candidateCafExperienceDto
									.setInputDateOfJoining(dateWith1Days != null ? sdf.format(dateWith1Days) : null);
							candidateCafExperienceDto
									.setOutputDateOfJoining(dateWith1Days != null ? sdf.format(dateWith1Days) : null);

						}
						if (candidateCafExperience.getInputDateOfExit() != null) {
							Date doe = candidateCafExperience.getInputDateOfExit();
							Calendar cal = Calendar.getInstance();
							cal.setTime(doe);
							cal.add(Calendar.DATE, 1);
							doee = cal.getTime();
							System.out.println(doee + "doe");
							candidateCafExperienceDto.setInputDateOfExit(doee != null ? sdf.format(doee) : null);

							candidateCafExperienceDto.setOutputDateOfExit(doee != null ? sdf.format(doee) : null);
						}

						candidateCafExperienceDto.setServiceName(candidateCafExperience.getServiceSourceMaster() != null
								? candidateCafExperience.getServiceSourceMaster().getServiceName()
								: "NA");

						candidateCafExperienceDtoList.add(candidateCafExperienceDto);
					}

					candidationApplicationFormDto.setCandidateCafExperienceDto(candidateCafExperienceDtoList);
					List<String> uanNUmberList = candidateCafExperienceRepository
							.getCandidateUan(candidate.getCandidateId());
					String uanNumber = uanNUmberList.stream().map(uan -> uan.toString())
							.collect(Collectors.joining("/"));
					candidationApplicationFormDto.setCandidateUan(uanNumber);
				}
				List<CandidateCafAddress> candidateCafAddressList = candidateCafAddressRepository
						.findAllByCandidateCandidateCode(candidateCode);
				if (candidateCafAddressList != null) {
					candidateCafAddressDtoList = candidateCafAddressList.stream().map(
							candidateCafAddress -> modelMapper.map(candidateCafAddress, CandidateCafAddressDto.class))
							.collect(Collectors.toList());
					candidationApplicationFormDto.setCandidateCafAddressDto(candidateCafAddressDtoList);
				}
				List<ITRData> iTRDataList = itrDataRepository
						.findAllByCandidateCandidateCodeOrderByFiledDateDesc(candidateCode);
				if (!iTRDataList.isEmpty()) {
					iTRDataFromApiDtoList = iTRDataList.stream()
							.map(itr -> modelMapper.map(itr, ITRDataFromApiDto.class)).collect(Collectors.toList());
					candidationApplicationFormDto.setITRDataFromApiDto(iTRDataFromApiDtoList);
				}
				CandidateResumeUpload candidateResume = candidateResumeUploadRepository
						.findByCandidateCandidateCode(candidateCode);
				if (candidateResume != null && Objects.nonNull(candidateResume.getContentId())) {
					String resumeUrl = contentService.getFileUrlFromContentId(candidateResume.getContentId());
					candidationApplicationFormDto.setCandidateResumeUrl(resumeUrl);
				} else if (candidateResume != null) {
					candidateFileDto = new CandidateFileDto(candidateResume.getCandidateResumeUploadId(),
							candidateResume.getCandidateResume(), null);
					candidationApplicationFormDto.setCandidateResume(candidateFileDto);
				}
				CandidateCaseDetails candidateCaseDetails = candidateCaseDetailsRepository
						.findByCandidateCandidateCode(candidateCode);
				if (candidateCaseDetails != null) {
					if (candidateCaseDetails.getCriminalVerificationisExist() != null) {
						candidateFileDto = new CandidateFileDto(candidateCaseDetails.getCandidateCaseDetailsId(),
								candidateCaseDetails.getCriminalVerificationDocument(),
								candidateCaseDetails.getCriminalVerificationisExist().getColorName());
						candidationApplicationFormDto.setCaseDetails(candidateFileDto);
					}
					if (candidateCaseDetails.getGlobalDatabaseCaseDetailsIsExist() != null) {
						candidateFileDto = new CandidateFileDto(candidateCaseDetails.getCandidateCaseDetailsId(),
								candidateCaseDetails.getGlobalDatabaseCaseDetailsDocument(),
								candidateCaseDetails.getGlobalDatabaseCaseDetailsIsExist().getColorName());
						candidationApplicationFormDto.setGlobalDatabaseCaseDetails(candidateFileDto);
					}
				}
				List<VendorChecks> vendorList = vendorChecksRepository.findAllByCandidateCandidateId(candidateId);
				for (VendorChecks vendorChecks : vendorList) {
					User user = userRepository.findByUserId(vendorChecks.getVendorId());
					VendorUploadChecks vendorChecksss = vendorUploadChecksRepository
							.findByVendorChecksVendorcheckId(vendorChecks.getVendorcheckId());
					if (vendorChecksss != null) {
						vendorUploadChecksDto = new VendorUploadChecksDto(user.getUserFirstName(),
								vendorChecksss.getVendorChecks().getVendorcheckId(),
								vendorChecksss.getVendorUploadedDocument(), vendorChecksss.getDocumentname(),
								vendorChecksss.getAgentColor().getColorName(),
								vendorChecksss.getAgentColor().getColorHexCode(), null);
						vendordocDtoList.add(vendorUploadChecksDto);

					}
					candidationApplicationFormDto.setVendorProofDetails(vendordocDtoList);

				}

				List<Content> contentDetails = contentRepository.findAllByCandidateId(candidateId);
				byte[] document;

				if (contentDetails != null) {
					for (Content content : contentDetails) {
						contentFileDto = new ContentFileDto(content.getContentId(), content.getDocument(),
								content.getContentSubCategory());
						docDtoList.add(contentFileDto);

					}
					candidationApplicationFormDto.setDocument(docDtoList);

				}
				CandidateAddComments candidateAddComments = candidateAddCommentRepository
						.findByCandidateCandidateId(candidateId);
				if (candidateAddComments != null) {
					candidationApplicationFormDto.setCandidateAddComments(candidateAddComments);
				}

				CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(candidateCode);
				candidationApplicationFormDto.setCandidateStatus(candidateStatus);

				List<CandidateIdItems> candidateIdItems = candidateIdItemsRepository
						.findByCandidateCandidateCode(candidateCode);
				candidationApplicationFormDto.setCandidateIdItems(candidateIdItems);

				CandidateEmailStatus candidateEmailStatus = candidateEmailStatusRepository
						.findByCandidateCandidateCode(candidate.getCandidateCode());
				candidationApplicationFormDto.setEmailStatus(candidateEmailStatus);

				StringBuilder query = new StringBuilder();

				query.append(
						"select distinct srcId,srcName,count(distinct case when svcName !='' then  item end) as servicecount,\n");
				query.append("REPLACE(GROUP_CONCAT(DISTINCT(svcName)),',','/'), ");
				query.append(
						"count(distinct smm1.source_service_id) as totalcount, count(distinct case when svcName ='' then  item1 end) as nullcount\n");
				query.append("from\n");
				query.append("(\n");
				query.append(
						"select distinct tds.source_id as srcId,tds.source_name as srcName,service_name as svcName,\n");
				query.append("cii.items_id as item,0 as item1\n");
				query.append("from t_dgv_candidate_id_items cii,\n");
				query.append(
						"t_dgv_source_service_master ssm,t_dgv_service_type_config_master tstcm,t_dgv_source tds,\n");
				query.append("t_dgv_service_master tdsm\n");
				query.append(
						"where cii.service_source_master_id=ssm.source_service_id and cii.candidate_id =:candidateId\n");
				query.append("and ssm.source_id =tds.source_id \n");
				query.append("and tds.source_id =ssm.source_id and ssm.source_service_id =tstcm.source_service_id \n");
				query.append("and tdsm.organization_id =tstcm.organization_id and tdsm.source_id=tds.source_id \n");
				query.append("and tdsm.organization_id=:organizationId\n");
				query.append("and tstcm.organization_id =:organizationId\n");
				query.append("union \n");
				query.append(
						"select distinct tds.source_id as srcId,tds.source_name as srcName,service_name as svcName,\n");
				query.append("caa.candidate_caf_address_id as item,0 as item1\n");
				query.append("from  t_dgv_candidate_caf_address caa,\n");
				query.append(
						"t_dgv_source_service_master ssm,t_dgv_service_type_config_master tstcm,t_dgv_source tds,\n");
				query.append("t_dgv_service_master tdsm\n");
				query.append("where caa.source_service_id =ssm.source_service_id\n");
				query.append("and caa.candidate_id =:candidateId\n");
				query.append("and ssm.source_id =tds.source_id \n");
				query.append("and tds.source_id =ssm.source_id and ssm.source_service_id =tstcm.source_service_id \n");
				query.append("and tdsm.organization_id =tstcm.organization_id and tdsm.source_id=tds.source_id \n");
				query.append("and tdsm.organization_id=:organizationId\n");
				query.append("and tstcm.organization_id =:organizationId\n");
				query.append("and caa.source_service_id is not null\n");
				query.append("union \n");
				query.append("select tds.source_id as srcId,tds.source_name as srcName,'' as svcName,\n");
				query.append("0 as item, ca.candidate_caf_address_id as item1\n");
				query.append("from  t_dgv_candidate_caf_address ca, t_dgv_source tds\n");
				query.append("where (ca.source_service_id is null or ca.source_service_id=null)\n");
				query.append("and ca.candidate_id =:candidateId and source_code='ADDRESS'\n");
				query.append("union \n");
				query.append(
						"select distinct tds.source_id as srcId,tds.source_name as srcName,service_name as svcName,\n");
				query.append("ce.candidate_caf_education_id as item,0 as item1\n");
				query.append("from  t_dgv_candidate_caf_education ce,\n");
				query.append(
						"t_dgv_source_service_master ssm,t_dgv_service_type_config_master tstcm,t_dgv_source tds,\n");
				query.append("t_dgv_service_master tdsm\n");
				query.append("where ce.source_service_id =ssm.source_service_id\n");
				query.append("and ce.candidate_id =:candidateId\n");
				query.append("and ssm.source_id =tds.source_id \n");
				query.append("and ssm.source_service_id =tstcm.source_service_id \n");
				query.append("and tdsm.organization_id =tstcm.organization_id and tdsm.source_id=tds.source_id \n");
				query.append("and tdsm.organization_id=:organizationId\n");
				query.append("and tstcm.organization_id =:organizationId\n");
				query.append("and ce.source_service_id is not null\n");
				query.append("Union\n");
				query.append("select tds.source_id as srcId,tds.source_name as srcName,'' as svcName,\n");
				query.append("0 as item,cce.candidate_caf_education_id as item1  \n");
				query.append("from     t_dgv_candidate_caf_education cce , t_dgv_source tds\n");
				query.append("where (cce.source_service_id is null or cce.source_service_id=null)\n");
				query.append("and cce.candidate_id =:candidateId and source_code='EDUCATION'\n");
				query.append("union \n");
				query.append(
						"select distinct tds.source_id as srcId,tds.source_name as srcName,service_name as svcName,\n");
				query.append("ce.candidate_caf_education_id as item,0 as item1\n");
				query.append("from  t_dgv_candidate_caf_education ce,\n");
				query.append(
						"t_dgv_source_service_master ssm,t_dgv_service_type_config_master tstcm,t_dgv_source tds,\n");
				query.append("t_dgv_service_master tdsm\n");
				query.append("where ce.source_service_id =ssm.source_service_id\n");
				query.append("and ce.candidate_id =:candidateId\n");
				query.append("and ssm.source_id =tds.source_id \n");
				query.append("and ssm.source_service_id =tstcm.source_service_id \n");
				query.append("and tdsm.organization_id =tstcm.organization_id and tdsm.source_id=tds.source_id \n");
				query.append("and tdsm.organization_id=:organizationId\n");
				query.append("and tstcm.organization_id =:organizationId\n");
				query.append("and ce.source_service_id is not null\n");
				query.append("union \n");
				query.append(
						"select distinct tds.source_id as srcId,tds.source_name as srcName,service_name as svcName,\n");
				query.append("cx.candidate_caf_experience_id as item,0 as item1\n");
				query.append("from  t_dgv_candidate_caf_experience cx,\n");
				query.append(
						"t_dgv_source_service_master ssm,t_dgv_service_type_config_master tstcm,t_dgv_source tds,\n");
				query.append("t_dgv_service_master tdsm\n");
				query.append("where cx.service_source_master_id =ssm.source_service_id\n");
				query.append("and cx.candidate_id =:candidateId\n");
				query.append("and ssm.source_id =tds.source_id \n");
				query.append("and ssm.source_service_id =tstcm.source_service_id \n");
				query.append("and tdsm.organization_id =tstcm.organization_id and tdsm.source_id=tds.source_id \n");
				query.append("and tdsm.organization_id=:organizationId\n");
				query.append("and tstcm.organization_id =:organizationId\n");
				query.append("and cx.service_source_master_id is not null\n");
				query.append("union \n");
				query.append("select tds.source_id as srcId,tds.source_name as srcName,'' as svcName,\n");
				query.append("0 as item,cex.candidate_caf_experience_id as item1  \n");
				query.append("from    t_dgv_candidate_caf_experience  cex , t_dgv_source tds\n");
				query.append("where (cex.service_source_master_id is null or cex.service_source_master_id=null)\n");
				query.append("and cex.candidate_id =:candidateId and source_code='EMPLOYMENTS'\n");
				query.append(") t, t_dgv_source_service_master smm1\n");
				query.append("where smm1.source_id =t.srcId\n");
				query.append("group  by srcId,srcName\n");

				Query resultQuery = entityManager.createNativeQuery(query.toString());
				resultQuery.setParameter("candidateId", candidate.getCandidateId());
				resultQuery.setParameter("organizationId", candidate.getOrganization().getOrganizationId());

				List<Object[]> executiveSummaryList = resultQuery.getResultList();
				List<ExecutiveSummaryDto> executiveSummaryDtoList = new ArrayList<ExecutiveSummaryDto>();
				Color colGreen = colorRepository.findByColorCode("GREEN");
				Color colAmber = colorRepository.findByColorCode("AMBER");
				Color colRed = colorRepository.findByColorCode("RED");
				List<String> configCodes = getServiceConfigCodes(candidateCode, null).getData();

				for (Object[] executiveSummary : executiveSummaryList) {
					ExecutiveSummaryDto executiveSummaryDto = new ExecutiveSummaryDto();
					executiveSummaryDto.setVerificationItem(executiveSummary[1].toString());
					System.out.println("caseDetails");
					CandidateCaseDetails caseDetails = candidateCaseDetailsRepository
							.findByCandidateCandidateCode(candidateCode);
					System.out.println("caseDetailsssssssssss" + caseDetails);
					switch (executiveSummary[1].toString()) {
					case "Employments":
						if (!executiveSummary[3].toString().equals("")
								&& executiveSummary[3].toString().indexOf("/") == 0) {
							executiveSummaryDto.setSource(executiveSummary[3].toString().substring(1));
						} else if (!executiveSummary[3].toString().equals("")
								&& executiveSummary[3].toString().indexOf("/") != 0) {
							executiveSummaryDto.setSource(executiveSummary[3].toString());
						} else {
							executiveSummaryDto.setSource("Not-Available");
						}

						List<CandidateCafExperience> redColorList = candidateCafExperienceList.stream()
								.filter(c -> c.getColor().getColorCode().equals("RED")).collect(Collectors.toList());
						if (redColorList != null && redColorList.size() > 0) {
							executiveSummaryDto.setResult(colRed.getColorName());
							executiveSummaryDto.setColorCode(colRed.getColorCode());
							executiveSummaryDto.setColorHexCode(colRed.getColorHexCode());
						} else {
							List<CandidateCafExperience> amberColorList = candidateCafExperienceList.stream()
									.filter(c -> c.getColor().getColorCode().equals("AMBER"))
									.collect(Collectors.toList());
							if (amberColorList != null && amberColorList.size() > 0) {
								executiveSummaryDto.setResult(colAmber.getColorName());
								executiveSummaryDto.setColorCode(colAmber.getColorCode());
								executiveSummaryDto.setColorHexCode(colAmber.getColorHexCode());
							} else {
								executiveSummaryDto.setResult(colGreen.getColorName());
								executiveSummaryDto.setColorCode(colGreen.getColorCode());
								executiveSummaryDto.setColorHexCode(colGreen.getColorHexCode());
							}
						}
						break;
					case "Education":
						if (!executiveSummary[3].toString().equals("")
								&& executiveSummary[3].toString().indexOf("/") == 0) {
							executiveSummaryDto.setSource(executiveSummary[3].toString().substring(1));
						} else if (!executiveSummary[3].toString().equals("")
								&& executiveSummary[3].toString().indexOf("/") != 0) {
							executiveSummaryDto.setSource(executiveSummary[3].toString());
						} else {
							executiveSummaryDto.setSource("Not-Available");
						}
						List<CandidateCafEducation> redColorEduList = candidateCafEducationList.stream()
								.filter(c -> c.getColor().getColorCode().equals("RED")).collect(Collectors.toList());
						if (redColorEduList != null && redColorEduList.size() > 0) {
							executiveSummaryDto.setResult(colRed.getColorName());
							executiveSummaryDto.setColorCode(colRed.getColorCode());
							executiveSummaryDto.setColorHexCode(colRed.getColorHexCode());
						} else {
							List<CandidateCafEducation> amberColorEduList = candidateCafEducationList.stream()
									.filter(c -> c.getColor().getColorCode().equals("AMBER"))
									.collect(Collectors.toList());
							if (amberColorEduList != null && amberColorEduList.size() > 0) {
								executiveSummaryDto.setResult(colAmber.getColorName());
								executiveSummaryDto.setColorCode(colAmber.getColorCode());
								executiveSummaryDto.setColorHexCode(colAmber.getColorHexCode());
							} else {
								executiveSummaryDto.setResult(colGreen.getColorName());
								executiveSummaryDto.setColorCode(colGreen.getColorCode());
								executiveSummaryDto.setColorHexCode(colGreen.getColorHexCode());
							}
						}
						break;
					case "Global Database check":
						System.out.println("Database");
						if (caseDetails != null && caseDetails.getGlobalDatabaseCaseDetailsIsExist() != null) {
							executiveSummaryDto
									.setResult(caseDetails.getGlobalDatabaseCaseDetailsIsExist().getColorName());
							executiveSummaryDto
									.setColorCode(caseDetails.getGlobalDatabaseCaseDetailsIsExist().getColorCode());
							executiveSummaryDto.setColorHexCode(
									caseDetails.getGlobalDatabaseCaseDetailsIsExist().getColorHexCode());
						} else {
							executiveSummaryDto.setResult(colAmber.getColorName());
							executiveSummaryDto.setColorCode(colAmber.getColorCode());
							executiveSummaryDto.setColorHexCode(colAmber.getColorHexCode());
						}
						System.out.println("Databaseend");
						break;
					case "Criminal":
						System.out.println("Criminal");
						if (caseDetails != null && caseDetails.getCriminalVerificationisExist() != null) {
							executiveSummaryDto.setResult(caseDetails.getCriminalVerificationisExist().getColorName());
							executiveSummaryDto
									.setColorCode(caseDetails.getCriminalVerificationisExist().getColorCode());
							executiveSummaryDto
									.setColorHexCode(caseDetails.getCriminalVerificationisExist().getColorHexCode());
						} else {
							executiveSummaryDto.setResult(colAmber.getColorName());
							executiveSummaryDto.setColorCode(colAmber.getColorCode());
							executiveSummaryDto.setColorHexCode(colAmber.getColorHexCode());
						}
						System.out.println("Criminalend");
						break;
					case "Address":
						if (!executiveSummary[3].toString().equals("")
								&& executiveSummary[3].toString().indexOf("/") == 0) {
							executiveSummaryDto.setSource(executiveSummary[3].toString().substring(1));
						} else if (!executiveSummary[3].toString().equals("")
								&& executiveSummary[3].toString().indexOf("/") != 0) {
							executiveSummaryDto.setSource(executiveSummary[3].toString());
						} else {
							executiveSummaryDto.setSource("Not-Available");
						}
						List<CandidateCafAddress> redColorAddrList = candidateCafAddressList.stream()
								.filter(c -> c.getColor().getColorCode().equals("RED")).collect(Collectors.toList());
						if (redColorAddrList != null && redColorAddrList.size() > 0) {
							executiveSummaryDto.setResult(colRed.getColorName());
							executiveSummaryDto.setColorCode(colRed.getColorCode());
							executiveSummaryDto.setColorHexCode(colRed.getColorHexCode());
						} else {
							List<CandidateCafAddress> amberColorAddrList = candidateCafAddressList.stream()
									.filter(c -> c.getColor().getColorCode().equals("AMBER"))
									.collect(Collectors.toList());
							if (amberColorAddrList != null && amberColorAddrList.size() > 0) {
								executiveSummaryDto.setResult(colAmber.getColorName());
								executiveSummaryDto.setColorCode(colAmber.getColorCode());
								executiveSummaryDto.setColorHexCode(colAmber.getColorHexCode());
							} else {
								executiveSummaryDto.setResult(colGreen.getColorName());
								executiveSummaryDto.setColorCode(colGreen.getColorCode());
								executiveSummaryDto.setColorHexCode(colGreen.getColorHexCode());
							}
						}
						break;
					case "ID Items":

						if (!executiveSummary[3].toString().equals("")
								&& executiveSummary[3].toString().indexOf("/") == 0) {

							executiveSummaryDto.setSource(executiveSummary[3].toString().substring(1));
						} else if (!executiveSummary[3].toString().equals("")
								&& executiveSummary[3].toString().indexOf("/") != 0) {
							executiveSummaryDto.setSource(executiveSummary[3].toString());
						} else {
							executiveSummaryDto.setSource("Not-Available");
						}
						if (Long.parseLong(executiveSummary[5].toString()) == 0) {
							executiveSummaryDto.setResult(colGreen.getColorName());
							executiveSummaryDto.setColorCode(colGreen.getColorCode());
							executiveSummaryDto.setColorHexCode(colGreen.getColorHexCode());
						} else {
							executiveSummaryDto.setResult(colAmber.getColorName());
							executiveSummaryDto.setColorCode(colAmber.getColorCode());
							executiveSummaryDto.setColorHexCode(colAmber.getColorHexCode());
						}
						break;
					}

					executiveSummaryDtoList.add(executiveSummaryDto);

				}
				if (configCodes.contains("CRIMINAL")) {
					if (candidateCaseDetails != null) {
						ExecutiveSummaryDto executiveSummaryDto = new ExecutiveSummaryDto();
						executiveSummaryDto.setVerificationItem("Criminal");

						if (candidateCaseDetails.getCreatedBy() != null)
							if (candidateCaseDetails.getCreatedBy().getRole() != null)
								executiveSummaryDto
										.setSource(candidateCaseDetails.getCreatedBy().getRole().getRoleName());

						if (candidateCaseDetails.getCriminalVerificationisExist() != null)
							executiveSummaryDto
									.setResult(candidateCaseDetails.getCriminalVerificationisExist().getColorName());

						if (candidateCaseDetails.getCriminalVerificationisExist() != null)
							executiveSummaryDto
									.setColorCode(candidateCaseDetails.getCriminalVerificationisExist().getColorCode());

						if (candidateCaseDetails.getCriminalVerificationisExist() != null)
							executiveSummaryDto.setColorHexCode(
									candidateCaseDetails.getCriminalVerificationisExist().getColorHexCode());

						executiveSummaryDtoList.add(executiveSummaryDto);
					} else {
						ExecutiveSummaryDto executiveSummaryDto = new ExecutiveSummaryDto();
						executiveSummaryDto.setVerificationItem("Criminal");
						executiveSummaryDto.setSource("Pending");
						executiveSummaryDto.setResult("");// colAmber.getColorName()
						executiveSummaryDto.setColorCode("");// colAmber.getColorCode()
						executiveSummaryDto.setColorHexCode("");// colAmber.getColorHexCode()
						executiveSummaryDtoList.add(executiveSummaryDto);
					}
				}
				if (configCodes.contains("GLOBAL")) {
					if (candidateCaseDetails != null) {
						ExecutiveSummaryDto executiveSummaryDto = new ExecutiveSummaryDto();
						executiveSummaryDto.setVerificationItem("Global Database check");

						if (candidateCaseDetails.getCreatedBy() != null)
							if (candidateCaseDetails.getCreatedBy().getRole() != null)
								executiveSummaryDto
										.setSource(candidateCaseDetails.getCreatedBy().getRole().getRoleName());

						if (candidateCaseDetails.getGlobalDatabaseCaseDetailsIsExist() != null)
							executiveSummaryDto.setResult(
									candidateCaseDetails.getGlobalDatabaseCaseDetailsIsExist().getColorName());

						if (candidateCaseDetails.getGlobalDatabaseCaseDetailsIsExist() != null)
							executiveSummaryDto.setColorCode(
									candidateCaseDetails.getGlobalDatabaseCaseDetailsIsExist().getColorCode());

						if (candidateCaseDetails.getGlobalDatabaseCaseDetailsIsExist() != null)
							executiveSummaryDto.setColorHexCode(
									candidateCaseDetails.getGlobalDatabaseCaseDetailsIsExist().getColorHexCode());

						executiveSummaryDtoList.add(executiveSummaryDto);
					} else {
						ExecutiveSummaryDto executiveSummaryDto = new ExecutiveSummaryDto();
						executiveSummaryDto.setVerificationItem("Global Database check");
						executiveSummaryDto.setSource("Pending");
						executiveSummaryDto.setResult("");// colAmber.getColorName()
						executiveSummaryDto.setColorCode("");// colAmber.getColorCode()
						executiveSummaryDto.setColorHexCode("");// colAmber.getColorHexCode()
						executiveSummaryDtoList.add(executiveSummaryDto);
					}
				}
				List<EmploymentDetailsDto> employmentDetailsDto = new ArrayList<EmploymentDetailsDto>();
				int i = 0;
				for (CandidateCafExperience experience : efpolist) {
					String inputTenure = "";
					String outputTenure = "";
					Date idoj = experience.getInputDateOfJoining();
					Date idoe = experience.getInputDateOfExit();

					String gap = "0y 0m";

					if (idoj == null) {
						inputTenure = 0 + "y " + 0 + "m";
					} else {
						LocalDate inputdoj = idoj.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						LocalDate inputdoe = idoe == null ? LocalDate.now()
								: idoe.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						Period inputdiff = Period.between(inputdoj, inputdoe);

						int years = inputdiff.getYears();
						int months = inputdiff.getMonths();

						if (inputdiff.getDays() > 0) {
							months += 1;
						}
						inputTenure = years + "y " + months + "m";
					}

					Date odoj = experience.getOutputDateOfJoining();
					Date odoe = experience.getOutputDateOfExit();

					if (odoj == null) {
						outputTenure = 0 + "y " + 0 + "m";
					} else {
						LocalDate outputdoj = odoj.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						LocalDate outputdoe = odoe == null ? LocalDate.now()
								: odoe.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						Period outputdiff = Period.between(outputdoj, outputdoe);
						int oyears = outputdiff.getYears();
						int omonths = outputdiff.getMonths();
						if (outputdiff.getDays() > 0) {
							omonths += 1;
						}
						outputTenure = oyears + "y " + omonths + "m";

						if (i < efpolist.size()) {
							CandidateCafExperience experience1 = efpolist.get(i);
							if (i + 1 < efpolist.size() && experience1.getInputDateOfJoining() != null) {
								CandidateCafExperience experience2 = efpolist.get(i + 1);
								LocalDate experience1Inputdoj = experience1.getInputDateOfJoining().toInstant()
										.atZone(ZoneId.systemDefault()).toLocalDate();
								LocalDate experience2Outputdoe = experience2.getOutputDateOfExit() == null
										? LocalDate.now()
										: experience2.getOutputDateOfExit().toInstant().atZone(ZoneId.systemDefault())
												.toLocalDate();
								Period gapPeriod = Period.between(experience2Outputdoe, experience1Inputdoj);
								int gapYears = gapPeriod.getYears();
								int gapMonths = gapPeriod.getMonths();
								if (gapPeriod.getDays() > 0) {
									gapMonths += 1;
								}
								gap = gapYears + "y " + gapMonths + "m";
							}
							i++;
						}
					}

					EmploymentDetailsDto employmentDetails = new EmploymentDetailsDto(experience, inputTenure,
							outputTenure, gap);
					employmentDetailsDto.add(employmentDetails);

				}
				candidationApplicationFormDto.setEmploymentDetails(employmentDetailsDto);
				tenureSum(employmentDetailsDto, candidationApplicationFormDto);
				gapSum(employmentDetailsDto, candidationApplicationFormDto);
				candidationApplicationFormDto.setExecutiveSummary(executiveSummaryDtoList);

				candidationApplicationFormDto.setOrganisationScope(
						organisationScopeRepository.findByCandidateId(candidate.getCandidateId()));

				svcSearchResult.setData(candidationApplicationFormDto);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("Data retrieved successfully.");
			}
		} catch (Exception e) {
			log.error(
					"Exception occured in candidateApplicationFormDetailsExceptCandidate method in CandidateServiceImpl-->",
					e);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<CandidateCafExperience> updateCandidateExperience(
			CandidateCafExperienceDto candidateCafExperienceDto) {
		ServiceOutcome<CandidateCafExperience> outcome = new ServiceOutcome<CandidateCafExperience>();
		try {
			Candidate candidate = candidateRepository.findByCandidateCode(candidateCafExperienceDto.getCandidateCode());
			if (candidate != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				CandidateCafExperience experience = candidateCafExperienceRepository
						.findById(candidateCafExperienceDto.getCandidateCafExperienceId()).get();
				experience.setInputDateOfJoining(candidateCafExperienceDto.getInputDateOfJoining() != null
						? sdf.parse(candidateCafExperienceDto.getInputDateOfJoining())
						: experience.getInputDateOfJoining());
				experience.setInputDateOfExit(candidateCafExperienceDto.getInputDateOfExit() != null
						? sdf.parse(candidateCafExperienceDto.getInputDateOfExit())
						: experience.getInputDateOfExit());
				experience.setColor(colorRepository.findByColorCode("AMBER"));
				candidateCafExperienceRepository.save(experience);

				outcome.setData(experience);
				outcome.setOutcome(true);
				outcome.setMessage("Experience updated successfully.");
			} else {
				outcome.setData(null);
				outcome.setOutcome(false);
				outcome.setMessage("Candidate not found.");
			}
		} catch (Exception e) {
			log.error("Exception occured in updateCandidateExperience method in CandidateServiceImpl-->", e);
			outcome.setData(null);
			outcome.setOutcome(false);
			outcome.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return outcome;
	}

	@Override
	public ServiceOutcome<CandidateCafAddress> saveCandidateAddress(CandidateCafAddressDto candidateCafAddressDto) {
		ServiceOutcome<CandidateCafAddress> outcome = new ServiceOutcome<CandidateCafAddress>();
		try {
			Candidate candidate = candidateRepository.findByCandidateCode(candidateCafAddressDto.getCandidateCode());
			if (candidate != null) {
				if (candidateCafAddressDto.getCandidateCafAddressId() == null) {
					CandidateCafAddress candidateCafAddress = new CandidateCafAddress();
					BeanUtils.copyProperties(candidateCafAddressDto, candidateCafAddress);
					candidateCafAddress.setCandidate(candidate);
					candidateCafAddress.setName(candidate.getCandidateName());
					candidateCafAddress.setCreatedOn(new Date());
					candidateCafAddress.setColor(colorRepository.findByColorCode("AMBER"));
					candidateCafAddress = candidateCafAddressRepository.save(candidateCafAddress);
					outcome.setData(candidateCafAddress);
					outcome.setOutcome(true);
					outcome.setMessage("Address saved successfully.");

				} else {
					CandidateCafAddress candidateCafAddress = candidateCafAddressRepository
							.findById(candidateCafAddressDto.getCandidateCafAddressId()).get();
					candidateCafAddress.setCandidate(candidate);
					candidateCafAddress.setCandidateAddress(candidateCafAddressDto.getCandidateAddress());
					candidateCafAddress.setPinCode(candidateCafAddressDto.getPinCode());
					candidateCafAddress.setCity(candidateCafAddressDto.getCity());
					candidateCafAddress.setName(candidate.getCandidateName());
					candidateCafAddress.setLastUpdatedOn(new Date());
					candidateCafAddress = candidateCafAddressRepository.save(candidateCafAddress);
					outcome.setData(candidateCafAddress);
					outcome.setOutcome(true);
					outcome.setMessage("Address updated successfully.");
				}

			} else {
				outcome.setData(null);
				outcome.setOutcome(false);
				outcome.setMessage("Candidate not found.");
			}

		} catch (Exception e) {
			log.error("Exception occured in saveCandidateAddress method in CandidateServiceImpl-->", e);
			outcome.setData(null);
			outcome.setOutcome(false);
			outcome.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return outcome;
	}

	@Override
	public ServiceOutcome<List<String>> getServiceConfigCodes(String candidateCode, Long orgId) {
		ServiceOutcome<List<String>> outcome = new ServiceOutcome<List<String>>();
		try {
			if (candidateCode != null && !candidateCode.equals("")) {
				Candidate candidate = candidateRepository.findByCandidateCode(candidateCode);
				orgId = candidate.getOrganization().getOrganizationId();
			}

			List<String> serviceTypeConfig = serviceTypeConfigRepository.getServiceSourceMasterByOrgId(orgId);
			String relutilityString = toleranceConfigRepository.findByOrganizationOrganizationId(orgId)
					.getAccessToRelativesBill() ? "RELBILLTRUE" : "RELBILLFALSE";
			serviceTypeConfig.add(relutilityString);
			outcome.setData(serviceTypeConfig);
			outcome.setOutcome(true);
			outcome.setMessage("List of services.");
		} catch (Exception e) {
			log.error("Exception occured in getServiceConfigCodes method in CandidateServiceImpl-->", e);
			outcome.setData(null);
			outcome.setOutcome(false);
			outcome.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return outcome;
	}

	@Transactional
	@Override
	public CandidateStatusHistory createCandidateStatusHistory(CandidateStatus candidateStatus, String who) {
		CandidateStatusHistory candidateStatusHistoryObj = new CandidateStatusHistory();
		try {
			candidateStatusHistoryObj.setCandidate(candidateStatus.getCandidate());
			candidateStatusHistoryObj.setStatusMaster(candidateStatus.getStatusMaster());
			if (who.equals("NOTCANDIDATE")) {
				candidateStatusHistoryObj.setCreatedBy(SecurityHelper.getCurrentUser());
			} else {
				candidateStatusHistoryObj.setCreatedBy(candidateStatus.getCandidate().getCreatedBy());
			}
			candidateStatusHistoryObj.setCreatedOn(new Date());
			candidateStatusHistoryObj.setCandidateStatusChangeTimestamp(new Date());
			candidateStatusHistoryRepository.save(candidateStatusHistoryObj);
		} catch (Exception ex) {
			log.error("Exception occured in createCandidateStatusHistory method in CandidateServiceImpl-->", ex);
		}
		return candidateStatusHistoryObj;
	}

	@Override
	public ServiceOutcome<Candidate> setIsLoaAccepted(String candidateCode) {
		ServiceOutcome<Candidate> svcSearchResult = new ServiceOutcome<Candidate>();
		try {
			Candidate candidate = candidateRepository.findByCandidateCode(candidateCode);

			if (candidate != null) {
				candidate.setIsLoaAccepted(true);
				candidateRepository.save(candidate);
				svcSearchResult.setData(candidate);
				svcSearchResult.setOutcome(true);
				svcSearchResult
						.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("No candidate found");
			}
		} catch (Exception ex) {
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(true);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
			log.error("Exception occured in findByCandidateCode method in CandidateServiceImpl-->", ex);
		}
		return svcSearchResult;
	}

	public CandidationApplicationFormDto tenureSum(List<EmploymentDetailsDto> employmentDetailsDto,
			CandidationApplicationFormDto candidationApplicationFormDto) {
		Integer opTenureYears = 0;
		Integer opTenureMonths = 0;
		Integer ipTenureYears = 0;
		Integer ipTenureMonths = 0;
		for (EmploymentDetailsDto employmentDetails : employmentDetailsDto) {
			if (!employmentDetails.getOutputTenure().equals("Not-Available")) {
				String[] data = employmentDetails.getOutputTenure().split(" ");
				String yearString = data[0];
				String monthString = data[1];
				Integer years = Integer.parseInt(yearString.replace("y", ""));
				Integer months = Integer.parseInt(monthString.replace("m", ""));
				opTenureYears = opTenureYears + years;
				opTenureMonths = opTenureMonths + months;
			}
		}

		for (EmploymentDetailsDto employmentDetails : employmentDetailsDto) {
			if (!employmentDetails.getInputTenure().equals("Not-Available")) {
				String[] data = employmentDetails.getInputTenure().split(" ");
				String yearString = data[0];
				String monthString = data[1];
				Integer years = Integer.parseInt(yearString.replace("y", ""));
				Integer months = Integer.parseInt(monthString.replace("m", ""));
				ipTenureYears = ipTenureYears + years;
				ipTenureMonths = ipTenureMonths + months;
			}
		}

		if (opTenureMonths >= 12) {
			Period period = Period.ofMonths(opTenureMonths).normalized();
			int years = period.getYears();
			int months = period.getMonths();
			opTenureYears = opTenureYears + years;
			opTenureMonths = months;

		}

		if (ipTenureMonths >= 12) {
			Period period = Period.ofMonths(ipTenureMonths).normalized();
			int years = period.getYears();
			int months = period.getMonths();
			ipTenureYears = ipTenureYears + years;
			ipTenureMonths = months;

		}

		String outputTenureSum = opTenureYears.toString() + "y " + opTenureMonths.toString() + "m";
		String inputTenureSum = ipTenureYears.toString() + "y " + ipTenureMonths.toString() + "m";
		candidationApplicationFormDto.setOutputTenureSum(outputTenureSum);
		candidationApplicationFormDto.setInputTenureSum(inputTenureSum);
		return candidationApplicationFormDto;
	}

	public CandidationApplicationFormDto gapSum(List<EmploymentDetailsDto> employmentDetailsDto,
			CandidationApplicationFormDto candidationApplicationFormDto) {
		Integer gapYears = 0;
		Integer gapMonths = 0;

		for (EmploymentDetailsDto employmentDetails : employmentDetailsDto) {
			if (!employmentDetails.getGap().equals("Not-Available")) {
				String[] data = employmentDetails.getGap().split(" ");
				String yearString = data[0];
				String monthString = data[1];
				Integer years = Integer.parseInt(yearString.replace("y", ""));
				Integer months = Integer.parseInt(monthString.replace("m", ""));
				gapYears = gapYears + years;
				gapMonths = gapMonths + months;
			}
		}

		if (gapMonths >= 12) {
			Period period = Period.ofMonths(gapMonths).normalized();
			int years = period.getYears();
			int months = period.getMonths();
			gapYears = gapYears + years;
			gapMonths = months;

		}

		String gapSum = gapYears.toString() + "y " + gapMonths.toString() + "m";
		candidationApplicationFormDto.setGapSum(gapSum);
		return candidationApplicationFormDto;
	}

	@Override
	public ServiceOutcome<List<StatusMaster>> getAllStatus() {
		ServiceOutcome<List<StatusMaster>> svcSearchResult = new ServiceOutcome<List<StatusMaster>>();
		try {
			List<StatusMaster> statusMasterList = statusMasterRepository.findAll();
			if (!statusMasterList.isEmpty()) {
				svcSearchResult.setData(statusMasterList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NOT FOUND");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getAllStatus method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<String> generateInterimReport(String candidateCode)
			throws FileNotFoundException, IOException {
		ServiceOutcome<String> svcSearchResult = new ServiceOutcome<String>();
		ServiceOutcome<CandidationApplicationFormDto> applicationForm = candidateApplicationFormDetailsExceptCandidate(
				candidateCode);

		VelocityEngine ve = new VelocityEngine();

		/* next, get the Template */
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.init();
		Template t = ve.getTemplate("templates/interimReport.vm");
		/* create a context and add data */
		VelocityContext context = new VelocityContext();

		List<ExecutiveSummaryDto> executiveSummaryDto = applicationForm.getData().getExecutiveSummary();
		String executiveSummary_stat = "";
		if (!executiveSummaryDto.isEmpty()) {
			List<String> colorArray = new ArrayList<>();
			for (int index = 0; index < executiveSummaryDto.size(); index++) {
				colorArray.add(executiveSummaryDto.get(index).getColorCode());
			}
			if (colorArray.contains("RED")) {
				executiveSummary_stat = "Red";
			} else if (colorArray.contains("AMBER")) {
				executiveSummary_stat = "Amber";
			} else {
				executiveSummary_stat = "Green";
			}

		}

		List<CandidateIdItems> idItems = applicationForm.getData().getCandidateIdItems();
		String candidateIdItems_stat = "";
		if (!idItems.isEmpty()) {
			List<String> colorArray = new ArrayList<>();
			for (int index = 0; index < idItems.size(); index++) {
				colorArray.add(idItems.get(index).getColor().getColorCode());
			}
			if (colorArray.contains("RED")) {
				candidateIdItems_stat = "Red";
			} else if (colorArray.contains("AMBER")) {
				candidateIdItems_stat = "Amber";
			} else {
				candidateIdItems_stat = "Green";
			}

		}

		List<CandidateCafEducationDto> education = applicationForm.getData().getCandidateCafEducationDto();
		String candidateEducation_stat = "";
		if (!education.isEmpty()) {
			List<String> colorArray = new ArrayList<>();
			for (int index = 0; index < education.size(); index++) {
				colorArray.add(education.get(index).getColorColorCode());
			}
			if (colorArray.contains("RED")) {
				candidateEducation_stat = "Red";
			} else if (colorArray.contains("AMBER")) {
				candidateEducation_stat = "Amber";
			} else {
				candidateEducation_stat = "Green";
			}

		}

		List<EmploymentDetailsDto> employmentDetails = applicationForm.getData().getEmploymentDetails();
		String candidateEmployment_stat = "";
		if (!employmentDetails.isEmpty()) {
			List<String> colorArray = new ArrayList<>();
			for (int index = 0; index < employmentDetails.size(); index++) {
				colorArray.add(employmentDetails.get(index).getColorCode());
			}
			if (colorArray.contains("RED")) {
				candidateEmployment_stat = "Red";
			} else if (colorArray.contains("AMBER")) {
				candidateEmployment_stat = "Amber";
			} else {
				candidateEmployment_stat = "Green";
			}

		}

		List<CandidateCafAddressDto> candidateCafAddressDto = applicationForm.getData().getCandidateCafAddressDto();
		String candidateAddress_stat = "";
		if (!candidateCafAddressDto.isEmpty()) {
			List<String> colorArray = new ArrayList<>();
			for (int index = 0; index < candidateCafAddressDto.size(); index++) {
				colorArray.add(candidateCafAddressDto.get(index).getColorColorCode());
			}
			if (colorArray.contains("RED")) {
				candidateAddress_stat = "Red";
			} else if (colorArray.contains("AMBER")) {
				candidateAddress_stat = "Amber";
			} else {
				candidateAddress_stat = "Green";
			}

		}
		CandidateFileDto caseDetails = applicationForm.getData().getCaseDetails();
		String criminal_stat = "";
		if (Optional.ofNullable(caseDetails).isPresent()) {
			criminal_stat = caseDetails.getColorName();
		}
		CandidateFileDto globalDatabaseCaseDetails = applicationForm.getData().getGlobalDatabaseCaseDetails();
		String globalDatabaseCase_stat = "";
		if (Optional.ofNullable(globalDatabaseCaseDetails).isPresent()) {
			globalDatabaseCase_stat = globalDatabaseCaseDetails.getColorName();
		}
		String candidateFinalReport_stat = "";
		if (executiveSummary_stat == "Red" || candidateAddress_stat == "Red" || candidateEducation_stat == "Red"
				|| candidateIdItems_stat == "Red" || candidateEmployment_stat == "Red"
				|| globalDatabaseCase_stat == "Red" || criminal_stat == "Red") {
			candidateFinalReport_stat = "Red";
		} else if (executiveSummary_stat == "Amber" || candidateAddress_stat == "Amber"
				|| candidateEducation_stat == "Amber" || candidateIdItems_stat == "Amber"
				|| candidateEmployment_stat == "Amber" || globalDatabaseCase_stat == "Amber"
				|| criminal_stat == "Amber") {
			candidateFinalReport_stat = "Amber";
		} else if (executiveSummary_stat == "Green" || candidateAddress_stat == "Green"
				|| candidateEducation_stat == "Green" || candidateIdItems_stat == "Green"
				|| candidateEmployment_stat == "Green" || globalDatabaseCase_stat == "Green"
				|| criminal_stat == "Green") {
			candidateFinalReport_stat = "Green";
		}

		context.put("data", applicationForm.getData());
		context.put("report", "Interim Report");
		context.put("date", new DateTool());
		context.put("executiveSummary_stat", executiveSummary_stat);
		context.put("candidateIdItems_stat", candidateIdItems_stat);
		context.put("candidateEducation_stat", candidateEducation_stat);
		context.put("candidateEmployment_stat", candidateEmployment_stat);
		context.put("candidateAddress_stat", candidateAddress_stat);
		context.put("candidateFinalReport_stat", candidateFinalReport_stat);
		context.put("criminal_stat", criminal_stat);
		context.put("globalDatabaseCase_stat", globalDatabaseCase_stat);
		/* now render the template into a StringWriter */
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		/* show the World */

		HtmlConverter.convertToPdf(writer.toString(), new FileOutputStream("E://interimLatest.pdf"));

		File file = new File("E://interimLatest.pdf");
		FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(file.toPath()), false, file.getName(),
				(int) file.length(), file.getParentFile());

		try {
			IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
		} catch (IOException ex) {
			// do something.
		}

		MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//		baos = generatePdf(writer.toString());
//		try 
//		OutputStream out = new FileOutputStream("E://out.pdf");
//		
//			out.write(baos.toByteArray());
//			out.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally {
//			
//		}
		svcSearchResult.setData("PDF generated");
		svcSearchResult.setOutcome(true);
		svcSearchResult.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));

		return svcSearchResult;
	}

	public ByteArrayOutputStream generatePdf(String html) {

		PdfWriter pdfWriter = null;

		// create a new document
		Document document = new Document();
		try {

			document = new Document();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);

			// open document
			document.open();

			XMLWorkerHelper xmlWorkerHelper = XMLWorkerHelper.getInstance();
			xmlWorkerHelper.getDefaultCssResolver(true);
			xmlWorkerHelper.parseXHtml(pdfWriter, document, new StringReader(html));
			// close the document
			document.close();
			System.out.println("PDF generated successfully");

			return baos;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public ServiceOutcome<Candidate> saveIsUanSkipped(String candidateCode, String isUanSkipped) {
		ServiceOutcome<Candidate> svcSearchResult = new ServiceOutcome<Candidate>();
		try {
			Candidate candidate = candidateRepository.findByCandidateCode(candidateCode);
			if (candidate != null) {
				if (isUanSkipped.equals("yes")) {
					candidate.setIsUanSkipped(false);
				} else {
					candidate.setIsUanSkipped(true);
				}
				candidate.setLastUpdatedOn(new Date());
				candidateRepository.save(candidate);
				svcSearchResult.setData(candidate);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("UAN check saved successfully.");
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("Candidate not found.");
			}

		} catch (Exception e) {
			log.error("Exception occured in saveIsUanSkipped method in CandidateServiceImpl-->", e);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public List<CandidateCafExperience> getCandidateExperienceFromItrAndEpfoByCandidateId_old(Long candidateId,
			Boolean formatEpfoDate) {
		System.out.println("enter to node js server" + formatEpfoDate);
		Candidate candidate = candidateRepository.findById(candidateId)
				.orElseThrow(() -> new RuntimeException("invalid candidate id"));
		String nodeServerUrl = "http://ec2-35-154-251-102.ap-south-1.compute.amazonaws.com:9090/v1.0/candidate/get-itr-epfo/"
				+ candidateId + "?formatEpfoDate=" + (formatEpfoDate ? "1" : "0");
		ResponseEntity<EpfoItrResponseDTO> epfoItrResponseDTOResponseEntity = restTemplate.getForEntity(nodeServerUrl,
				EpfoItrResponseDTO.class);
		List<CandidateCafExperience> candidateCafExperiences = new ArrayList<>();
		if (epfoItrResponseDTOResponseEntity.getStatusCode().equals(HttpStatus.OK)) {
			EpfoItrResponseDTO epfoItrResponseDTO = epfoItrResponseDTOResponseEntity.getBody();
			epfoItrResponseDTO.getData().getEmployerList().stream().forEach(employerData -> {
				System.out.println("enter to node js server" + employerData.getDoj());
				CandidateCafExperience candidateCafExperience = new CandidateCafExperience();
				candidateCafExperience.setCandidateEmployerName(employerData.getEmployerName());
				candidateCafExperience.setOutputDateOfJoining(DateUtil.getDate(employerData.getDoj(), "dd-MM-yyyy"));
				candidateCafExperience.setInputDateOfJoining(DateUtil.getDate(employerData.getDoj(), "dd-MM-yyyy"));
				candidateCafExperience.setOutputDateOfExit(DateUtil.getDate(employerData.getDoe(), "dd-MM-yyyy"));
				candidateCafExperience.setInputDateOfExit(DateUtil.getDate(employerData.getDoe(), "dd-MM-yyyy"));
				candidateCafExperience.setServiceSourceMaster(
						serviceSourceMasterRepository.findByServiceCode(employerData.getSource()));
				System.out.println(candidateCafExperience + "candidateCafExperience");
				candidateCafExperience.setCandidate(candidate);
				candidateCafExperience.setCreatedOn(new Date());
				candidateCafExperience.setColor(colorRepository.findByColorCode("GREEN"));
				candidateCafExperience.setUan(epfoItrResponseDTO.getData().getUan());
				candidateCafExperiences.add(candidateCafExperience);
			});
			return candidateCafExperiences;

		}
		return candidateCafExperiences;

	}

	@Override
	public List<CandidateCafExperience> getCandidateExperienceFromItrAndEpfoByCandidateId(Long candidateId,
			Boolean formatEpfoDate) {
		System.out.println("enter new" + formatEpfoDate);
		Candidate candidate = candidateRepository.findById(candidateId)
				.orElseThrow(() -> new RuntimeException("invalid candidate id"));
		List<CandidateCafExperience> candidateCafExperiences = new ArrayList<>();
		List<ITRData> iTRDataList = itrDataRepository
				.findAllByCandidateCandidateCodeOrderByFiledDateDesc(candidate.getCandidateCode());
		List<EpfoData> uan = epfoDataRepository.findAllByCandidateCandidateCode(candidate.getCandidateCode());
		// System.out.println(uan+"uan");
		Date doe = null;
		if (uan.isEmpty()) {
			if (!iTRDataList.isEmpty()) {
				int j = 0;
				Collections.reverse(iTRDataList);
				for (ITRData itrData : iTRDataList) {
					ITRDataFromApiDto itrDataFromApiDto = this.modelMapper.map(itrData, ITRDataFromApiDto.class);

					System.out.println(itrDataFromApiDto + "iTRDataList" + iTRDataList.size());
					CandidateCafExperience candidateCafExperience = new CandidateCafExperience();
					if (j == 0) {

						candidateCafExperience.setCandidateEmployerName(itrDataFromApiDto.getDeductor());
						candidateCafExperience
								.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("ITR"));
						candidateCafExperience.setCandidate(candidate);
						candidateCafExperience.setCreatedOn(new Date());
						candidateCafExperience.setColor(colorRepository.findByColorCode("GREEN"));
						System.out.println(itrDataFromApiDto.getFiledDate() + "date1");
						candidateCafExperience.setOutputDateOfJoining(itrDataFromApiDto.getFiledDate());
						candidateCafExperience.setInputDateOfJoining(itrDataFromApiDto.getFiledDate());
						candidateCafExperiences.add(candidateCafExperience);
						return candidateCafExperiences;

					}
					j++;

				}
				return candidateCafExperiences;

			}
		} else {
			for (EpfoData epfoData : uan) {
				EpfoDataFromDetailsDto epfoDataFromApiDto = this.modelMapper.map(epfoData,
						EpfoDataFromDetailsDto.class);
				System.out.println(uan.size() + "iTRDataList");
				System.out.println(epfoDataFromApiDto + "uan");
				System.out.println(epfoDataFromApiDto.getDoj());
				CandidateCafExperience candidateCafExperience = new CandidateCafExperience();
				candidateCafExperience.setCandidateEmployerName(epfoDataFromApiDto.getCompany());
				candidateCafExperience.setOutputDateOfJoining(epfoDataFromApiDto.getDoj());
				candidateCafExperience.setInputDateOfJoining(epfoDataFromApiDto.getDoj());
				if (epfoDataFromApiDto.getDoe() != null) {
					candidateCafExperience.setOutputDateOfExit(epfoDataFromApiDto.getDoe());
					candidateCafExperience.setInputDateOfExit(epfoDataFromApiDto.getDoe());
				}
				candidateCafExperience.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("EPFO"));
				candidateCafExperience.setCandidate(candidate);
				candidateCafExperience.setCreatedOn(new Date());
				candidateCafExperience.setColor(colorRepository.findByColorCode("GREEN"));
				candidateCafExperience.setUan(epfoDataFromApiDto.getUan());
				candidateCafExperiences.add(candidateCafExperience);

			}
			
			for(int i=0;i<uan.size()-1;i++) {
				
				EpfoDataFromDetailsDto epfoData1 = this.modelMapper.map(uan.get(i),
						EpfoDataFromDetailsDto.class);
				
				EpfoDataFromDetailsDto epfoData2 = this.modelMapper.map(uan.get(i+1),
						EpfoDataFromDetailsDto.class);
				if(epfoData1.getDoj() != null && epfoData2.getDoj() != null) { 

					DateTime start1 = new DateTime(epfoData1.getDoj()); 
					DateTime end1 = null;
					if(epfoData1.getDoe() == null) {
						end1 = DateTime.now();
					}else {
						end1 = new DateTime(epfoData1.getDoe());
					}

					DateTime start2 = new DateTime(epfoData2.getDoj());
					DateTime end2 = null;
					if(epfoData2.getDoe() == null) {
						end2 = DateTime.now();
					}else {
						end2 = new DateTime(epfoData2.getDoe());
					}

					Interval interval = new Interval( start1, end1 );
					Interval interval2 = new Interval( start2, end2 );
					
					log.info("result {}", interval.overlaps( interval2 ));
					if(interval.overlaps( interval2 )) { 
						for(CandidateCafExperience candidateCafExperience  : candidateCafExperiences) {
							if(candidateCafExperience.getCandidateEmployerName().equalsIgnoreCase(epfoData1.getCompany())) { 
								candidateCafExperience.setColor(colorRepository.findByColorCode("MOONLIGHTING")); 
							}
							if(candidateCafExperience.getCandidateEmployerName().equalsIgnoreCase(epfoData2.getCompany())) {  
								candidateCafExperience.setColor(colorRepository.findByColorCode("MOONLIGHTING"));
							}
						} 
					}
				}
			}

			return candidateCafExperiences;
		}

		return candidateCafExperiences;
	}

	@Override
	public List<CandidateCafExperience> getCandidateExperienceByCandidateId(Long candidateId) {
		return candidateCafExperienceRepository.findAllByCandidateCandidateId(candidateId);
	}

	@Override
	public CandidateVerificationState getCandidateVerificationStateByCandidateId(Long candidateId) {
		return candidateVerificationStateRepository.findByCandidateCandidateId(candidateId);
	}

	@Override
	public CandidateVerificationState addOrUpdateCandidateVerificationStateByCandidateId(Long candidateId,
			CandidateVerificationState candidateVerificationState) {
		CandidateVerificationState candidateVerificationState1 = candidateVerificationStateRepository
				.findByCandidateCandidateId(candidateId);
		if (Objects.nonNull(candidateVerificationState1)) {
			candidateVerificationState
					.setCandidateVerificationStateId(candidateVerificationState1.getCandidateVerificationStateId());
		}
		return candidateVerificationStateRepository.save(candidateVerificationState);
	}

	@Override
	public List<CandidateCafEducationDto> getAllCandidateEducationByCandidateId(Long candidateId) {
		return candidateCafEducationRepository.findAllByCandidateCandidateId(candidateId).stream().map(
				candidateCafEducation -> this.modelMapper.map(candidateCafEducation, CandidateCafEducationDto.class))
				.collect(Collectors.toList());
	}

	@Override
	public List<CandidateCafAddressDto> getCandidateAddress(Candidate candidate) {
		return candidateCafAddressRepository.findAllByCandidateCandidateCode(candidate.getCandidateCode()).stream()
				.map(candidateCafAddress -> this.modelMapper.map(candidateCafAddress, CandidateCafAddressDto.class))
				.collect(Collectors.toList());
	}

	@Transactional
	@Override
	public ServiceOutcome<Boolean> qcPendingstatus(String candidateCode) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		System.out.println(candidateCode + "candidtaecoe");
		try {
			System.out.println("inside try");
			Candidate candidate = candidateRepository.findByCandidateCode(candidateCode);
			candidate.setSubmittedOn(new Date());
			candidate.setApprovalRequired(true);
			candidateRepository.save(candidate);
			CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(candidateCode);
			candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("PENDINGAPPROVAL"));
			candidateStatus.setLastUpdatedOn(new Date());
			// System.out.println(candidateStatus+"candidtaecoestatus");
			candidateStatus = candidateStatusRepository.save(candidateStatus);

			createCandidateStatusHistory(candidateStatus, "CANDIDATE");
			svcSearchResult.setData(true);
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage("Thank you for submitting your response.");

			CompletableFuture.runAsync(() -> {
				reportService.generateDocument(candidateCode, "", ReportType.PRE_OFFER);
			});
		} catch (Exception ex) {
			log.error("Exception occured in qcPendingstatus method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(false);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;

	}

	@Override
	public ServiceOutcome<CandidateCafExperience> deletecandidateExpById(Long id) {
		ServiceOutcome<CandidateCafExperience> svcSearchResult = new ServiceOutcome<>();
		try {
			CandidateCafExperience result = null;
			if (id == null || id.equals(0l)) {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("Please specify User");
			} else {
				Optional<CandidateCafExperience> candidateCafExperienceObj = candidateCafExperienceRepository
						.findById(id);
				if (candidateCafExperienceObj.isPresent()) {
					CandidateCafExperience candidateCafExperienceObj1 = candidateCafExperienceObj.get();
					candidateCafExperienceRepository.deleteById(id);
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage("Experience Details Deleted Successfully");
				} else {
					svcSearchResult.setData(null);
					svcSearchResult.setOutcome(false);
					svcSearchResult.setMessage("No User Found");
				}
			}
		} catch (Exception ex) {
			log.error("Exception occured in deletecandidateExp method in UserServiceImpl-->" + ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<CandidateCafEducation> deletecandidateEducationById(Long id) {
		ServiceOutcome<CandidateCafEducation> svcSearchResult = new ServiceOutcome<>();
		try {
			CandidateCafEducation result = null;
			if (id == null || id.equals(0l)) {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("Please specify User");
			} else {
				Optional<CandidateCafEducation> candidateCafEducationObj = candidateCafEducationRepository.findById(id);
				if (candidateCafEducationObj.isPresent()) {
					CandidateCafEducation candidateCafEducationObj1 = candidateCafEducationObj.get();
					candidateCafEducationRepository.deleteById(id);
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage("Education Details Deleted Successfully");
				} else {
					svcSearchResult.setData(null);
					svcSearchResult.setOutcome(false);
					svcSearchResult.setMessage("No User Found");
				}
			}
		} catch (Exception ex) {
			log.error("Exception occured in deletecandidateEducationById method in UserServiceImpl-->" + ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	// get digilocker details name and dateof birth
	@Override
	public ServiceOutcome<CandidateDetailsDto> candidateDLdata(String candidateCode) {
		ServiceOutcome<CandidateDetailsDto> svcSearchResult = new ServiceOutcome<CandidateDetailsDto>();
		System.out.println(candidateCode + "candidate=====================================");
		try {
			System.out.println("iside========try=============================");
			Candidate candidate = candidateRepository.findByCandidateCode(candidateCode);
			if (candidate != null) {
				CandidateDetailsDto candidateDetailsDto = this.modelMapper.map(candidate, CandidateDetailsDto.class);
				candidateDetailsDto.setAadharName(candidate.getAadharName());
				candidateDetailsDto.setAadharDob(candidate.getAadharDob());
				System.out.println("iside========try=============================" + candidateDetailsDto);
				svcSearchResult.setData(candidateDetailsDto);

				svcSearchResult.setOutcome(true);
				svcSearchResult
						.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("No candidate found");
			}

		} catch (Exception e) {
			log.error("Exception occured in candidateDLdata method in CandidateServiceImpl-->", e);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}

		return svcSearchResult;
	}

// update the vendor proof color by agent ///
	@Override
	public ServiceOutcome<Boolean> updateCandidateVendorProofColor(VendorUploadChecksDto vendorUploadChecksDto) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		VendorUploadChecks result = null;
		// CandidateCafExperience candidateCafExperience=null;
		User user = SecurityHelper.getCurrentUser();
		try {
			VendorUploadChecks vendorChecks = vendorUploadChecksRepository
					.findByVendorChecksVendorcheckId(vendorUploadChecksDto.getVendorChecks());
			System.out.println("...............updateCandidateVendorProofColor" + vendorUploadChecksDto);
			if (vendorChecks != null) {
				vendorChecks.setAgentColor(colorRepository.findById(vendorUploadChecksDto.getColorId()).get());
				vendorChecks.setCreatedOn(new Date());
				vendorChecks.setCreatedBy(user);
				result = vendorUploadChecksRepository.save(vendorChecks);
				if (result != null) {
					svcSearchResult.setMessage("vendorProofs Update successfully.");

				} else {
					System.out.println("-------------candidate-----else------");
					svcSearchResult.setData(null);
					svcSearchResult.setOutcome(false);
					svcSearchResult
							.setMessage(messageSource.getMessage("msg.error", null, LocaleContextHolder.getLocale()));
				}

			} else {
				System.out.println("-------------candvendorChecksidate-----else------");
			}
		} catch (Exception e) {
			log.error("Exception occured in updateCandidateVendorProofColor method in CandidateServiceImpl-->", e);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<Long> getContentById(String CandidateCode) {
		ServiceOutcome<Long> svcSearchResult = new ServiceOutcome<Long>();
		try {
			Candidate candidate = candidateRepository.findByCandidateCode(CandidateCode);
			Optional<Content> contentList = contentRepository
					.findByCandidateIdAndContentTypeAndContentCategoryAndContentSubCategory(candidate.getCandidateId(),
							ContentType.GENERATED, ContentCategory.OTHERS, ContentSubCategory.PRE_APPROVAL);
			if (contentList.isPresent()) {
				Content contentListObj1 = contentList.get();
				System.out.println(contentListObj1 + "content list");
				Long contentid = contentListObj1.getContentId();
				System.out.println(contentid + "content id");
				svcSearchResult.setData(contentid);
				svcSearchResult.setOutcome(true);

			} else {
				svcSearchResult.setMessage("Content Id Not Found");
				svcSearchResult.setOutcome(false);
			}
		} catch (Exception e) {
			log.error("Exception occured in getContentById method in CandidateServiceImpl-->", e);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	/// comments in qc /////
	@Override
	public ServiceOutcome<Boolean> AddCommentsReports(CandidateCaseDetailsDTO candidateCaseDetailsDTO) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		CandidateAddComments candidateAddCommentsObj = null;
		CandidateAddComments result = null;

		try {
			User user = SecurityHelper.getCurrentUser();
			Candidate candidate = candidateRepository.findByCandidateCode(candidateCaseDetailsDTO.getCandidateCode());
			Long candidateId = candidate.getCandidateId();
			CandidateAddComments candidateAddComments = candidateAddCommentRepository
					.findByCandidateCandidateId(candidateId);
			if (candidateAddComments != null) {
				candidateAddComments.setComments(candidateCaseDetailsDTO.getAddComments());
				candidateAddComments.setCreatedOn(new Date());
				candidateAddComments.setCreatedBy(user);
				result = candidateAddCommentRepository.save(candidateAddComments);
				if (result != null) {
					svcSearchResult.setMessage("Addcomments Update Sucussfully.");

				} else {
					svcSearchResult.setData(null);
					svcSearchResult.setOutcome(false);
					svcSearchResult
							.setMessage(messageSource.getMessage("msg.error", null, LocaleContextHolder.getLocale()));
				}
			} else {
				candidateAddCommentsObj = new CandidateAddComments();
				candidateAddCommentsObj.setCandidate(candidate);
				candidateAddCommentsObj.setComments(candidateCaseDetailsDTO.getAddComments());
				candidateAddCommentsObj.setCreatedOn(new Date());
				candidateAddCommentsObj.setCreatedBy(user);
				result = candidateAddCommentRepository.save(candidateAddCommentsObj);
				if (result != null) {
					svcSearchResult.setMessage("Addcomments Saved Sucussfully.");

				} else {
					svcSearchResult.setData(null);
					svcSearchResult.setOutcome(false);
					svcSearchResult
							.setMessage(messageSource.getMessage("msg.error", null, LocaleContextHolder.getLocale()));
				}
			}

		} catch (Exception ex) {
			log.error("Exception occured in AddCommentsReports method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<SuspectEmpMaster> deleteSuspectExpById(Long id) {
		ServiceOutcome<SuspectEmpMaster> svcSearchResult = new ServiceOutcome<>();
		try {
			SuspectEmpMaster result = null;
			if (id == null || id.equals(0l)) {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("Please specify User");
			} else {
				Optional<SuspectEmpMaster> suspectEmpMastereObj = suspectEmpMasterRepository.findById(id);
				if (suspectEmpMastereObj.isPresent()) {
					SuspectEmpMaster suspectEmpMastereObj1 = suspectEmpMastereObj.get();
					suspectEmpMasterRepository.deleteById(id);
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage("suspectEmploye Details Deleted Successfully");
				} else {
					svcSearchResult.setData(null);
					svcSearchResult.setOutcome(false);
					svcSearchResult.setMessage("No User Found");
				}
			}
		} catch (Exception ex) {
			log.error("Exception occured in deletesuspectEmp method in UserServiceImpl-->" + ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<Boolean> updateSpectEMPloyee(SuspectEmpMasterDto suspectEmpMasterDto) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		User user = SecurityHelper.getCurrentUser();
		SuspectEmpMaster result = null;
		try {
			System.out.println("...............updateCandidateVendorProofColor" + suspectEmpMasterDto);
			Optional<SuspectEmpMaster> suspectEmpMastereObj = suspectEmpMasterRepository
					.findById(suspectEmpMasterDto.getId());
			if (suspectEmpMastereObj.isPresent()) {
				SuspectEmpMaster suspectEmpMastereObj1 = suspectEmpMastereObj.get();
				suspectEmpMastereObj1.setSuspectCompanyName(suspectEmpMasterDto.getSuspectCompanyName());
				suspectEmpMastereObj1.setAddress(suspectEmpMasterDto.getAddress());

				suspectEmpMastereObj1.setIsActive(suspectEmpMasterDto.getIsActive());
				svcSearchResult.setOutcome(true);
				result = suspectEmpMasterRepository.save(suspectEmpMastereObj1);
				if (result != null) {
					svcSearchResult.setMessage("suspectEmploye Details Updated Successfully");

				} else {
					svcSearchResult.setData(null);
					svcSearchResult.setOutcome(false);
					svcSearchResult
							.setMessage(messageSource.getMessage("msg.error", null, LocaleContextHolder.getLocale()));
				}

			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("No User Found");
			}

		} catch (Exception e) {
			log.error("Exception occured in updateSpectEMPloyee method in CandidateServiceImpl-->", e);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Transactional
	@Override
	public ServiceOutcome<UserDto> agentCreatepasswrd(UserDto user) {
		ServiceOutcome<UserDto> svcSearchResult = new ServiceOutcome<>();
		System.out.println(user + "user");
		UserDto userDto = new UserDto();
		try {
			User result = null;
			// if (user.getUserId() != null && !user.getUserId().equals(0l) ) {
			// System.out.println(user+"inside_user");
			// User findUserEmail = userRepository.findByUserEmailId(user.getUserEmailId());

			User userObj = userRepository.findByEmployeeId(user.getEmployeeId());
			if (userObj != null) {
				System.out.println(user + "inside_if");
				// User userObj1 = userObj.get();
				String passwoString = userObj.getPassword();
				if (!user.getPassword().equals("")) {
					userObj.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
					userObj.setAddlPassword(user.getPassword());

				} else {
					userObj.setPassword(passwoString);
					userObj.setAddlPassword(userObj.getAddlPassword());

				}

				result = userRepository.save(userObj);

				BeanUtils.copyProperties(result, userDto);

				// setSomeUserDataInDTO(userDto,result);

				svcSearchResult.setData(userDto);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("User information Updated successfully");
			}

		} catch (Exception ex) {
			log.error("Exception occured in saveUser method in UserServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes");
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<String> suspectEmpMasterCheck(String companyName, Long oganizationId) {
		ServiceOutcome<String> svcSearchResult = new ServiceOutcome<String>();
		Optional<Organization> organization = organizationRepository.findById(oganizationId);
		if (organization.isPresent()) {
			Organization orgObj = organization.get();
			Long orgid = orgObj.getOrganizationId();
			System.out.println(orgObj.getOrganizationId());
			List<SuspectEmpMaster> suspectEmpMaster = suspectEmpMasterRepository
					.findAllByOrganizationOrganizationIdAndIsActiveTrue(orgid);
			if (!suspectEmpMaster.isEmpty()) {
				int i = 0;
				int j = 0;
				String[] arrOfStr = null;
				String[] giveemploy = null;
				for (SuspectEmpMaster SuspectEmp : suspectEmpMaster) {
					String removenumbers = companyName.toLowerCase();
					String employnumbers = SuspectEmp.getSuspectCompanyName().toLowerCase();
					arrOfStr = employnumbers.split(" ", 8);
					giveemploy = removenumbers.split(" ", 8);
					System.out.println(removenumbers + "===========" + employnumbers);
					if (arrOfStr[0].equals(giveemploy[0])) {
						System.out.println("first string matching");
						i++;

					}
					if (giveemploy.length != 1 && arrOfStr.length != 1) {
						if (arrOfStr[1].equals(giveemploy[1]) && arrOfStr[0].equals(giveemploy[0])) {
							System.out.println("second string matching");
							j++;
						}
					}
					System.out.println("length" + giveemploy.length);

				}
				System.out.println(i + "===========" + j);

				if (giveemploy.length != 1 && arrOfStr.length != 1) {
					if (i >= 1 && j >= 1) {
						svcSearchResult.setData("RED");

					} else {
						svcSearchResult.setData("AMBER");
					}
				} else {
					if (i >= 1) {
						svcSearchResult.setData("RED");

					} else {
						svcSearchResult.setData("AMBER");
					}

				}
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("Number of words matched " + i);

			} else {
				svcSearchResult.setData("GREEN");
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("Suspect Emp Master is empty");
			}
		}

		return svcSearchResult;
	}

	private HttpHeaders setHeaderDetails(HttpHeaders headers) {
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	@Override
	public ServiceOutcome<UanSearchData> saveUan(UanSearchDataDTO uanDTO) {

		System.out.println("ApplicantId::" + uanDTO.getApplicantId());
		System.out.println("uanSave::" + uanDTO);

		ServiceOutcome<UanSearchData> svcSearchResult = new ServiceOutcome<>();

		try {

			Date date = new Date();
			String applicantId = uanDTO.getApplicantId();
			String uanNumber = uanDTO.getUanusername();
			String uploadedBy = uanDTO.getUploadedBy();
			String message = uanDTO.getMsg();

			System.out.println("ApplicantID::1:::" + applicantId);
			System.out.println("UAN_NUMBER::1" + uanNumber);
			System.out.println("UPLOADEDBY::1" + uploadedBy);
			System.out.println("MSG:::" + message);

			UanSearchData uanSave = new UanSearchData();

			Candidate findByApplicantId = candidateRepository.findByapplicantId(uanDTO.getApplicantId());
			System.out.println("ApplicantID IN SERVICE:::>>>" + findByApplicantId);
			String transactionId = null;

			if (findByApplicantId == null) {
				System.out.println("===========APPLICANTID DOES NOT EXIST=================");

				ResponseEntity<String> epfoTokenResponse = null;
				HttpHeaders headers = new HttpHeaders();
				setHeaderDetails(headers);
				JSONObject request = new JSONObject();

				request.put(epfoSecurityConfig.getClientIdValue(), epfoSecurityConfig.getClientId());
				request.put(epfoSecurityConfig.getClientSecretValue(), epfoSecurityConfig.getClientSecret());
				HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);

				ParameterizedTypeReference<Map<String, Object>> typeRef = new ParameterizedTypeReference<Map<String, Object>>() {
				};
				System.out.println("epfoSecurityConfig.getAccessTokenUrl() *************************"
						+ epfoSecurityConfig.getAccessTokenUrl());
				epfoTokenResponse = restTemplate.exchange(epfoSecurityConfig.getAccessTokenUrl(), HttpMethod.POST,
						entity, String.class);
				log.info("Response from EPFO TOKEN API " + epfoTokenResponse);
				String message1 = epfoTokenResponse.getBody(); // .get("message").toString().replaceAll("=", ":")
				System.out.println("epfoTokenResponse  ************************* " + epfoTokenResponse.getBody());
				JSONObject obj1 = new JSONObject(message1);
				log.info("Response from EPFO TOKEN API - message " + obj1);
				log.info("last message " + obj1.getJSONObject("message"));
				JSONObject obj = obj1.getJSONObject("message");
				String access_token = obj.getString("access_token");
				System.out.println("access_token = " + access_token);

				//
				ResponseEntity<String> response = null;
				headers.setBearerAuth(access_token);
				headers.add("Bearer", access_token);
				HttpEntity<String> request1 = new HttpEntity<String>(headers);

				EpfoDetailsDto epfoDetails = new EpfoDetailsDto();
				ParameterizedTypeReference<Map<String, Object>> typeRef1 = new ParameterizedTypeReference<Map<String, Object>>() {
				};
				response = restTemplate.exchange(epfoSecurityConfig.getTransactionIdUrl(), HttpMethod.GET, request1,
						String.class);
				String message2 = response.getBody();
				log.info("Response from EPFO Transaction API : " + message2);
				JSONObject obj2 = new JSONObject(message2);
				log.info("Response from EPFO Transaction API - obj: " + obj2);
				transactionId = obj2.getString("message").toString();
				epfoDetails.setTransactionid(transactionId);
				log.info("Generated transactionId Id is " + transactionId);
				System.out.println("transaction 123 id--> " + transactionId);

				//
				JSONObject request2 = new JSONObject();
				System.out.println("____________________epfoDetails.getUanusername()" + uanNumber);
				request.put(EPFOConstants.EPFO_USR, uanNumber);

				request.put(EPFOConstants.EPFO_PWD, ' ');
				request.put(EPFOConstants.EPFO_UAN, uanNumber);

				HttpEntity<String> entity1 = new HttpEntity<String>(request.toString(), headers);
				System.out.println("\n------epfoSecurityConfig ------ " + epfoSecurityConfig.getFinalSubmitPostUrl());
				System.out.println("\n------epfoDetails ------ " + epfoDetails.getTransactionid());
				response = restTemplate.exchange(
						epfoSecurityConfig.getFinalSubmitPostUrl() + epfoDetails.getTransactionid(), HttpMethod.POST,
						entity1, String.class);
				String responseBody = response.getBody();
				JSONObject obj3 = new JSONObject(responseBody);
				JSONArray messagee = obj3.getJSONArray("message");
				System.out.println("\n--------obj --------- " + obj3);
				String resMsg = obj3.toString();
				System.out.println("\n--------resMsg --------- " + resMsg);

//				System.out.println("UANSAVE::: If Not Exists::"+uanSave);
				System.out.println("ApplicantID:: If Not Exist::" + applicantId);
				System.out.println("UAN NUMBER:: If Not Exist::" + uanNumber);

				uanSave.setApplicantId(applicantId);
				uanSave.setUan(uanNumber);
				uanSave.setUploadedBy(uploadedBy);
				uanSave.setUploadedOn(date);
				if (uanDTO.isBulkUanSearch()) {
					System.out.println("MULTIPLERECODS:::" + uanDTO.getTotalRecordUploaded());
					uanSave.setTotalRecordUploaded(uanDTO.getTotalRecordUploaded());
					uanSave.setBulkUanId(uanDTO.getBulkUanId());
				} else {
					uanSave.setTotalRecordUploaded(1);

				}
				uanSave.setEPFOResponse(resMsg);

				if ("success".equals(uanDTO.getMsg())) {
					System.out.println("SUCCESS>>>>>>>>>>>>>>>");
					List<UanSearchData> uanSearchDataList = uanSearchDataRepository.findByApplicantId(applicantId);
					System.out.println("success ==== ");
					for (UanSearchData uanSearchData : uanSearchDataList) {
						uanSearchData.setTotalRecordFetched(1);
						uanSearchData.setTotalRecordFailed(0);
						uanSearchDataRepository.save(uanSearchData);
					}
				}

				else {
					System.out.println("Empty ====>>>>> ");
					if (uanDTO.isBulkUanSearch()) {
						uanSave.setTotalRecordFetched(uanDTO.getTotalRecordUploaded());
					} else {
						uanSave.setTotalRecordFetched(1);

					}
					uanSave.setTotalRecordFailed(0);

					uanSearchDataRepository.save(uanSave);
					svcSearchResult.setMessage(null);

				}

//				else {	
//					System.out.println("Fail ==== ");
//
//					uanSave.setTotalRecordFetched(0);
//					uanSave.setTotalRecordFailed(1);
//					
//					uanSearchDataRepository.save(uanSave);
//					
//				}

				System.out.println("==============================:FINSIHED:===========================");
			} else {
				String candidateCode = findByApplicantId.getCandidateCode();
				System.out.println("getCandidateCode::" + candidateCode);

				uanSave.setApplicantId(applicantId);
				uanSave.setUan(uanNumber);
				uanSave.setUploadedBy(uploadedBy);
				uanSave.setUploadedOn(date);

				String bulkUanId = uanDTO.getBulkUanId();
				System.out.println("BULKUAN_ID:::>>>>" + bulkUanId);
				if (uanDTO.isBulkUanSearch()) {
					System.out.println("MULTIPLERECODS:::" + uanDTO.getTotalRecordUploaded());
					uanSave.setTotalRecordUploaded(uanDTO.getTotalRecordUploaded());
					uanSave.setBulkUanId(uanDTO.getBulkUanId());
				}

				else {
					uanSave.setTotalRecordUploaded(1);

				}

				if ("success".equals(uanDTO.getMsg())) {
					System.out.println("SUCCESS>>>>>>>>>>>>>>>");
					List<UanSearchData> uanSearchDataList = uanSearchDataRepository.findByApplicantId(applicantId);
					System.out.println("success ==== ");
					for (UanSearchData uanSearchData : uanSearchDataList) {
						uanSearchData.setTotalRecordFetched(1);
						uanSearchData.setTotalRecordFailed(0);
						uanSearchDataRepository.save(uanSearchData);
					}
				} else if ("".equals(uanDTO.getMsg())) {
					System.out.println("Empty ====>>>>> ");

					uanSave.setTotalRecordFetched(1);
					uanSave.setTotalRecordFailed(0);

					uanSearchDataRepository.save(uanSave);

				} else {
					if (uanDTO.isBulkUanSearch()) {
						uanSave.setTotalRecordFetched(uanDTO.getTotalRecordUploaded());
					} else {
						System.out.println("Fail ==== ");

						uanSave.setTotalRecordFetched(0);
						uanSave.setTotalRecordFailed(1);
					}

					uanSearchDataRepository.save(uanSave);

				}

				svcSearchResult.setMessage(candidateCode);

			}

			System.out.println("UANSAVE:::" + uanSave);

			svcSearchResult.setData(uanSave);
		} catch (Exception ex) {
			// TODO: handle exception
			log.error("Exception occured in saveUan method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes");

		}

		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<List<UanSearchEpfoDTO>> getEpfoData(Map<String, String> data) {

		ServiceOutcome<List<UanSearchEpfoDTO>> svcSearchResult = new ServiceOutcome<>();
//		ServiceOutcome<List<UanSearchData>> svcSearchResult2 = new ServiceOutcome<>();

		String candidateCode = data.get("candidateCode");
		String uan = data.get("uanusername");
		String applicantId = data.get("applicantId");
		String bulkUanId = data.get("bulkUanId");

		System.out.println("CCODE::" + candidateCode);
		System.out.println("UAN::" + uan);
		System.out.println("APPI:::" + applicantId);
		System.out.println("BulkUanId::>>"+bulkUanId);

		try {

			if (candidateCode == null) {

				if (!(bulkUanId == null)) {
					List<UanSearchData> uanSearchDataList = uanSearchDataRepository.findByBulkUanId(bulkUanId);
					System.out.println("NoT exist applicant And Id is not null");
					List<String> applicantIdsAndUans = new ArrayList<>();
					List<Candidate> candidateList = new ArrayList<>();
					List<UanSearchEpfoDTO> dtoList = new ArrayList<>();
					List<UanSearchEpfoDTO> epfoDataList = new ArrayList<>();
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

					for (UanSearchData uanSearchData : uanSearchDataList) {
						uan = uanSearchData.getUan();
						String BulkApplicantId = uanSearchData.getApplicantId();
						System.out.println("BulkUANNumber::: " + uan);
						System.out.println("BulkApplicantId::: " + BulkApplicantId);

						applicantIdsAndUans.add("BulkApplicantId: " + BulkApplicantId + ", UAN: " + uan);

						List<UanSearchData> findByUanAndApplicantID = uanSearchDataRepository
								.findByApplicantIdAndUan(BulkApplicantId, uan);
						System.out.println("BULKDOWNLOAD PROCESS >>>>"+findByUanAndApplicantID);

						for (UanSearchData searchData : findByUanAndApplicantID) {
							String epfoResponse = searchData.getEPFOResponse();
							JsonObject epfoJson = new Gson().fromJson(epfoResponse, JsonObject.class);
							JsonArray messageArray = epfoJson.getAsJsonArray("message");

							for (JsonElement messageElement : messageArray) {
								JsonObject messageObj = messageElement.getAsJsonObject();

								// Convert JsonObject to EpfoDataFromDetailsDto
								UanSearchEpfoDTO epfoData = new UanSearchEpfoDTO();
								epfoData.setUan(messageObj.get("uan").getAsString());
								epfoData.setName(messageObj.get("name").getAsString());
								epfoData.setCompany(messageObj.get("company").getAsString());
//							        epfoData.setDoe(messageObj.get("doe").getAsString());
//							        epfoData.setDoj(messageObj.get("doj").getAsString());

								if (messageObj.has("doe")) {
									String doeString = messageObj.get("doe").getAsString();
									if (!doeString.equals("NOT_AVAILABLE")) {
										try {
											Date doe = dateFormat.parse(doeString);
											epfoData.setDoe(doe);
										} catch (ParseException e) {
											e.printStackTrace();
											epfoData.setDoe(null); // Set as null if parsing fails
										}
									}
								}

								String dojString = messageObj.get("doj").getAsString();
								Date doj = dateFormat.parse(dojString);
								epfoData.setDoj(doj);
								epfoData.setApplicantId(BulkApplicantId);
								
								    epfoData.setBulkId(bulkUanId);
								
								epfoDataList.add(epfoData);
							}

						}

					}
					svcSearchResult.setData(epfoDataList);

				}

				else {
					System.out.println("Candidate_Code is Null:::");
					List<UanSearchData> findByUanAndApplicantID = uanSearchDataRepository
							.findByApplicantIdAndUan(applicantId, uan);
					System.out.println("FINDBYUAN>>>> DATA::>>" + findByUanAndApplicantID.toString());
//						svcSearchResult.setData(findByUanAndApplicantID);
					List<UanSearchEpfoDTO> epfoDataList = new ArrayList<>();
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

					for (UanSearchData searchData : findByUanAndApplicantID) {
						String epfoResponse = searchData.getEPFOResponse();
						JsonObject epfoJson = new Gson().fromJson(epfoResponse, JsonObject.class);
						JsonArray messageArray = epfoJson.getAsJsonArray("message");

						for (JsonElement messageElement : messageArray) {
							JsonObject messageObj = messageElement.getAsJsonObject();

							// Convert JsonObject to EpfoDataFromDetailsDto
							UanSearchEpfoDTO epfoData = new UanSearchEpfoDTO();
							epfoData.setUan(messageObj.get("uan").getAsString());
							epfoData.setName(messageObj.get("name").getAsString());
							epfoData.setCompany(messageObj.get("company").getAsString());
//						        epfoData.setDoe(messageObj.get("doe").getAsString());
//						        epfoData.setDoj(messageObj.get("doj").getAsString());

							if (messageObj.has("doe")) {
								String doeString = messageObj.get("doe").getAsString();
								if (!doeString.equals("NOT_AVAILABLE")) {
									try {
										Date doe = dateFormat.parse(doeString);
										epfoData.setDoe(doe);
									} catch (ParseException e) {
										e.printStackTrace();
										epfoData.setDoe(null); // Set as null if parsing fails
									}
								}
							}

							String dojString = messageObj.get("doj").getAsString();
							Date doj = dateFormat.parse(dojString);
							epfoData.setDoj(doj);

							epfoDataList.add(epfoData);
						}
						svcSearchResult.setData(epfoDataList);

					}
				}

				// Set the converted data in svcSearchResult
				System.out.println("===================FINSIHED=================GETEPFODATA========");
			}

			else {
				if (!(bulkUanId == null)) {
					System.out.println("BulkUanID is Exist::::>>>" + bulkUanId);
					List<UanSearchData> uanSearchDataList = uanSearchDataRepository.findByBulkUanId(bulkUanId);

					List<String> applicantIdsAndUans = new ArrayList<>();
					List<Candidate> candidateList = new ArrayList<>();
					List<UanSearchEpfoDTO> dtoList = new ArrayList<>();

					for (UanSearchData uanSearchData : uanSearchDataList) {
						uan = uanSearchData.getUan();
						String BulkApplicantId = uanSearchData.getApplicantId();
						String bulkId = uanSearchData.getBulkUanId();
						System.out.println("BulkID::: " + bulkId);
						System.out.println("BulkUANNumber::: " + uan);
						System.out.println("BulkApplicantId::: " + BulkApplicantId);

						applicantIdsAndUans.add("BulkApplicantId: " + BulkApplicantId + ", UAN: " + uan);

						Candidate findCandidateId = candidateRepository.findByapplicantId(BulkApplicantId);
						System.out.println("FindCandidateId::>>>>>" + findCandidateId);
						candidateList.add(findCandidateId);

						for (String value : applicantIdsAndUans) {
							if (value.contains("BulkApplicantId")) {
								System.out.println("Appliacnt>>>>>>>>" + value);
							}
						}
						Long candidateId = null;
						for (Candidate candidate : candidateList) {
							System.out.println("FindCandidateId::>>>>>" + candidate);
							candidateId = candidate.getCandidateId();
							System.out.println("CandID>>>>>>>" + candidateId);

						}
						List<Object[]> findAllCandidateByCandidateIdAndUan = epfoDataRepository
								.findAllCandidateByCandidateIdAndUan(candidateId, uan);

						System.out.println("findAllCandidateByCandidateIdAndUan::::>>>>"
								+ findAllCandidateByCandidateIdAndUan.toString());

						boolean isLoopCompleted = false;

						for (Object[] result : findAllCandidateByCandidateIdAndUan) {
							UanSearchEpfoDTO dto = new UanSearchEpfoDTO();
							dto.setUan((String) result[0]);
							dto.setName((String) result[1]);
							dto.setCompany((String) result[2]);
							dto.setDoj((Date) result[3]);
							dto.setDoe((Date) result[4]);
							dto.setApplicantId(BulkApplicantId);
//							dto.setBulkId(bulkId);
							dtoList.add(dto);

							if (result == findAllCandidateByCandidateIdAndUan
									.get(findAllCandidateByCandidateIdAndUan.size() - 1)) {
								isLoopCompleted = true;
							}
						}

						for (Object[] result : findAllCandidateByCandidateIdAndUan) {
							for (Object obj : result) {
								System.out.print(obj + " ");
							}
							System.out.println();
						}

						System.out.println("DTOLIST:::" + dtoList);
//							svcSearchResult.setData(dtoList);

						if (isLoopCompleted) {
							System.out.println("DTOLIST:::" + dtoList);
							svcSearchResult.setData(dtoList);
//							    svcSearchResult.setMessage(BulkApplicantId)	;
						}

//					        List<EpfoData> findByUan = epfoDataRepository.findByUan(uan);

//					        System.out.println("FINDBYUAN:::::>>>>"+findByUan.size());
//					        System.out.println("FINDBYUAN:::::>>>>"+findByUan.toString());

						List<EpfoDataFromDetailsDto> dtoList2 = new ArrayList<>();

					}

					System.out.println("BulkUANNumber::: 2 " + uan);
//				        System.out.println("BulkApplicantId:::2 " + BulkApplicantId);

				}

				else {

					System.out.println("BulkUANNumber:::3 " + uan);
//					        System.out.println("BulkApplicantId:::3 " + BulkApplicantId);

					Candidate findByCandidateCode = candidateRepository.findByCandidateCode(candidateCode);
					Long candidateId = findByCandidateCode.getCandidateId();

					System.out.println("findByCandidateCode IS NULLL::: OUT.." + findByCandidateCode);

					System.out.println("CandidateID::" + candidateId);

					List<Object[]> findAllCandidateByCandidateIdAndUan = epfoDataRepository
							.findAllCandidateByCandidateIdAndUan(candidateId, uan);

					System.out.println(
							"findAllCandidateByCandidateIdAndUan::::" + findAllCandidateByCandidateIdAndUan.toString());

					List<UanSearchEpfoDTO> dtoList = new ArrayList<>();

					for (Object[] result : findAllCandidateByCandidateIdAndUan) {
						UanSearchEpfoDTO dto = new UanSearchEpfoDTO();
						dto.setUan((String) result[0]);
						dto.setName((String) result[1]);
						dto.setCompany((String) result[2]);
						dto.setDoj((Date) result[3]);
						dto.setDoe((Date) result[4]);
						// Set other columns based on the order in the result array
						dtoList.add(dto);
					}

					for (Object[] result : findAllCandidateByCandidateIdAndUan) {
						for (Object obj : result) {
							System.out.print(obj + " ");
						}
						System.out.println();
					}
					System.out.println("DTOLIST:::" + dtoList);
					svcSearchResult.setData(dtoList);

				}

			}
			svcSearchResult.setMessage("SUCCESS::");
			;

		} catch (Exception ex) {
			// TODO: handle exception
			log.error("Exception occured in getEpfoData method in UserServiceImpl-->", ex);
		}

		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<List<UanSearchData>> bulkUan(List<BulkUanDTO> bulkUan) {
		
//		System.out.println("ApplicantId::" + bulkUan.getApplicantId());
		System.out.println("uanSave::" + bulkUan);
		
		ServiceOutcome<List<UanSearchData>> svcSearchResult = new ServiceOutcome<>();

		
		try {
			
			Date date = new Date();
			String applicantId = null;
			String uanNumber = null;
			String uploadedBy = null;
			String message = null;
			String bulkUanId = null;
			String uploadedOn = null;
			int totalRecordUploaded;
			int totalRecordFetched;
			String epfoResponse = null;
			
	        List<BulkUanDTO> dataList = new ArrayList<>(); // Create a list to store the data

			
			for (BulkUanDTO dto : bulkUan) {
//		        System.out.println("ApplicantiD::>"+dto.getApplicantId());
//		        System.out.println("UANNUMBER::>"+dto.getUan());
//		        System.out.println("RandomID::>"+dto.getRandomId());
				BulkUanDTO bulkUanDTO = new BulkUanDTO();
				bulkUanDTO.setApplicantId(dto.getApplicantId());
				bulkUanDTO.setBulkUanId(dto.getRandomId());
				bulkUanDTO.setUan(dto.getUan());
				bulkUanDTO.setUploadedBy(dto.getUploadedBy());
				bulkUanDTO.setTotalRecordUploaded(dto.getTotalRecordUploaded());
				bulkUanDTO.setBulkUanSearch(true);
				
	    
		        dataList.add(bulkUanDTO);	        
		        System.out.println("TotalRecords:::>"+dto.getTotalRecordUploaded());
		        // Add any other processing logic for each UanSearchDataDTO element here.
		    }
			
			System.out.println("BULKDATALIST:::>>>"+dataList.toString());	
			String transactionId = null;
			UanSearchData uanSave;
			List<UanSearchData> bulkData = new ArrayList<>();
			
 			ResponseEntity<String> epfoTokenResponse = null;
 			HttpHeaders headers = new HttpHeaders();
 			setHeaderDetails(headers);
 			JSONObject request = new JSONObject();

 			request.put(epfoSecurityConfig.getClientIdValue(), epfoSecurityConfig.getClientId());
 			request.put(epfoSecurityConfig.getClientSecretValue(), epfoSecurityConfig.getClientSecret());
 			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);

 			ParameterizedTypeReference<Map<String, Object>> typeRef = new ParameterizedTypeReference<Map<String, Object>>() {
 			};
 			System.out.println("epfoSecurityConfig.getAccessTokenUrl() *************************"
 					+ epfoSecurityConfig.getAccessTokenUrl());
 			epfoTokenResponse = restTemplate.exchange(epfoSecurityConfig.getAccessTokenUrl(), HttpMethod.POST,
 					entity, String.class);
 			log.info("Response from EPFO TOKEN API " + epfoTokenResponse);
 			String message1 = epfoTokenResponse.getBody(); // .get("message").toString().replaceAll("=", ":")
 			System.out.println("epfoTokenResponse  ************************* " + epfoTokenResponse.getBody());
 			JSONObject obj1 = new JSONObject(message1);
 			log.info("Response from EPFO TOKEN API - message " + obj1);
 			log.info("last message " + obj1.getJSONObject("message"));
 			JSONObject obj = obj1.getJSONObject("message");
 			String access_token = obj.getString("access_token");
 			System.out.println("access_token = " + access_token);

 			//
 			ResponseEntity<String> response = null;
 			headers.setBearerAuth(access_token);
 			headers.add("Bearer", access_token);
 			HttpEntity<String> request1 = new HttpEntity<String>(headers);

 			EpfoDetailsDto epfoDetails = new EpfoDetailsDto();
 			ParameterizedTypeReference<Map<String, Object>> typeRef1 = new ParameterizedTypeReference<Map<String, Object>>() {
 			};
 			response = restTemplate.exchange(epfoSecurityConfig.getTransactionIdUrl(), HttpMethod.GET, request1,
 					String.class);
 			String message2 = response.getBody();
 			log.info("Response from EPFO Transaction API : " + message2);
 			JSONObject obj2 = new JSONObject(message2);
 			log.info("Response from EPFO Transaction API - obj: " + obj2);
 			transactionId = obj2.getString("message").toString();
 			epfoDetails.setTransactionid(transactionId);
 			log.info("Generated transactionId Id is " + transactionId);
 			System.out.println("transaction 123 id--> " + transactionId);

			

			for (BulkUanDTO details : dataList) {
	            System.out.println(details);
	             uanNumber = details.getUan();
	             applicantId = details.getApplicantId();
	             bulkUanId = details.getBulkUanId();
	             uploadedBy = details.getUploadedBy();
	             uploadedOn = date.toGMTString();
	             totalRecordUploaded = details.getTotalRecordUploaded();
	             totalRecordFetched = details.getTotalRecordUploaded();
	             
	             
	             System.out.println("APPLICANT_ID::>>"+applicantId);

	             System.out.println("UANNUMBER::>>"+uanNumber);
	             System.out.println("BulkUaNID::>>"+bulkUanId);
	             System.out.println("UploadedBy::>>"+uploadedBy);


	 			//
	 			JSONObject request2 = new JSONObject();
	 			System.out.println("____________________epfoDetails.getUanusername()" + uanNumber);
	 			request.put(EPFOConstants.EPFO_USR, uanNumber);

	 			request.put(EPFOConstants.EPFO_PWD, ' ');
	 			request.put(EPFOConstants.EPFO_UAN, uanNumber);

	 			HttpEntity<String> entity1 = new HttpEntity<String>(request.toString(), headers);
	 			System.out.println("\n------epfoSecurityConfig ------ " + epfoSecurityConfig.getFinalSubmitPostUrl());
	 			System.out.println("\n------epfoDetails ------ " + epfoDetails.getTransactionid());
	 			response = restTemplate.exchange(
	 					epfoSecurityConfig.getFinalSubmitPostUrl() + epfoDetails.getTransactionid(), HttpMethod.POST,
	 					entity1, String.class);
	 			String responseBody = response.getBody();
	 			JSONObject obj3 = new JSONObject(responseBody);
//	 			JSONArray messagee = obj3.getJSONArray("message");
	 			System.out.println("\n--------obj --------- " + obj3);
	 			String resMsg = obj3.toString();
	 			System.out.println("\n--------resMsg --------- " + resMsg);

	 		    uanSave = new UanSearchData();

	 			uanSave.setApplicantId(applicantId);
	 			uanSave.setBulkUanId(bulkUanId);
	 			uanSave.setUan(uanNumber);
	 			uanSave.setTotalRecordUploaded(totalRecordUploaded);
	 			uanSave.setTotalRecordFetched(totalRecordUploaded);
	 			uanSave.setUploadedBy(uploadedBy);
	 			uanSave.setUploadedOn(date);
	 			uanSave.setEPFOResponse(resMsg);
	 			
	 			
	 			bulkData.add(uanSave);

	 			uanSearchDataRepository.save(uanSave);

	 			
	        }
			
//		 UanSearchData findByBulkUanId = uanSearchDataRepository.findForBulk(bulkUanId);
//			System.out.println("UANSEARCHDATALIST:::"+findByBulkUanId.toString());
			System.out.println("BULKUANID::"+bulkUanId);
			
			 TypedQuery<UanSearchData> query = entityManager.createQuery(
			            "SELECT e FROM UanSearchData e WHERE e.bulkUanId = :bulkUanId", UanSearchData.class);
			    query.setParameter("bulkUanId", bulkUanId);
			    query.setMaxResults(1); // Limit the result to one record
			    List<UanSearchData> resultList = query.getResultList();

			    System.out.println("RESULTLIST::::>>>"+resultList);
			    System.out.println("RESULTLIST::::>>>"+resultList.get(0));

			
			List<UanSearchData> findByApplicantIdAndUan = uanSearchDataRepository.findByApplicantIdAndUan(applicantId, uanNumber);
			
			System.out.println("BULKDATA::>>>"+bulkData.toString());
		  
			svcSearchResult.setData(resultList);
			svcSearchResult.setMessage(bulkData.toString());			
		} catch (Exception ex) {
			// TODO: handle exception
			log.error("Exception occured in BulkUan method in CandidateServiceImpl-->", ex);
//			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes");


		}
		
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<List<UanSearchData>> retriveBulkUanData(String bulkUanId) {
		
		System.out.println("BULKUANID::"+bulkUanId);
		ServiceOutcome<List<UanSearchData>> svcSearchResult = new ServiceOutcome<>();

		
		try {

			TypedQuery<UanSearchData> query = entityManager.createQuery(
		            "SELECT e FROM UanSearchData e WHERE e.bulkUanId = :bulkUanId", UanSearchData.class);
		    query.setParameter("bulkUanId", bulkUanId);
		    query.setMaxResults(1); // Limit the result to one record
		    List<UanSearchData> resultList = query.getResultList();

		    System.out.println("RESULTLIST::::>>>"+resultList);
		    System.out.println("RESULTLIST::::>>>"+resultList.get(0));
		    
			svcSearchResult.setData(resultList);

			
		} catch (Exception ex) {
			// TODO: handle exception
			log.error("Exception occured in BulkUanRetrive method in CandidateServiceImpl-->", ex);
//			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes");

		}
		
		return svcSearchResult;
	}

}
