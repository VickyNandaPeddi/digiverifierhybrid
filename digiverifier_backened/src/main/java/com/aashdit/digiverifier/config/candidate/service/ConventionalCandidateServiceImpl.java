/**
 * 
 */
package com.aashdit.digiverifier.config.candidate.service;

import static com.aashdit.digiverifier.digilocker.service.DigilockerServiceImpl.DIGIVERIFIER_DOC_BUCKET_NAME;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.aashdit.digiverifier.common.dto.ContentDTO;
import com.aashdit.digiverifier.common.enums.ContentCategory;
import com.aashdit.digiverifier.common.enums.ContentSubCategory;
import com.aashdit.digiverifier.common.enums.ContentType;
import com.aashdit.digiverifier.common.enums.FileType;
import com.aashdit.digiverifier.common.model.Content;
import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.common.service.ContentService;
import com.aashdit.digiverifier.common.util.RandomString;
import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.admin.model.VendorChecks;
import com.aashdit.digiverifier.config.admin.repository.UserRepository;
import com.aashdit.digiverifier.config.admin.repository.VendorChecksRepository;
import com.aashdit.digiverifier.config.candidate.dto.CandidateDetailsDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateInvitationSentDto;
import com.aashdit.digiverifier.config.candidate.dto.CandidateStatusCountDto;
import com.aashdit.digiverifier.config.candidate.dto.SearchAllCandidateDTO;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.CandidateCaseDetails;
import com.aashdit.digiverifier.config.candidate.model.CandidateEmailStatus;
import com.aashdit.digiverifier.config.candidate.model.CandidateSampleCsvXlsMaster;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatusHistory;
import com.aashdit.digiverifier.config.candidate.model.CandidateVerificationState;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateEmailStatus;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateStatusHistory;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateVerificationState;
import com.aashdit.digiverifier.config.candidate.model.StatusMaster;
import com.aashdit.digiverifier.config.candidate.repository.CandidateEmailStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateSampleCsvXlsMasterRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusHistoryRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateVerificationStateRepository;
import com.aashdit.digiverifier.config.candidate.repository.ConventionalCandidateEmailStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.ConventionalCandidateStatusHistoryRepository;
import com.aashdit.digiverifier.config.candidate.repository.ConventionalCandidateStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.ConventionalCandidateVerificationStateRepository;
import com.aashdit.digiverifier.config.candidate.repository.StatusMasterRepository;
import com.aashdit.digiverifier.config.candidate.util.CSVUtil;
import com.aashdit.digiverifier.config.candidate.util.ExcelUtil;
import com.aashdit.digiverifier.config.superadmin.dto.DashboardDto;
import com.aashdit.digiverifier.config.superadmin.model.Organization;
import com.aashdit.digiverifier.config.superadmin.model.OrganizationConfig;
import com.aashdit.digiverifier.config.superadmin.model.ServiceTypeConfig;
import com.aashdit.digiverifier.config.superadmin.repository.OrganizationConfigRepository;
import com.aashdit.digiverifier.config.superadmin.repository.OrganizationRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ServiceTypeConfigRepository;
import com.aashdit.digiverifier.utils.ApplicationDateUtils;
import com.aashdit.digiverifier.utils.CommonValidation;
import com.aashdit.digiverifier.utils.EmailSentTask;
import com.aashdit.digiverifier.utils.SecurityHelper;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * Nambi
 */
@Service
@Slf4j
public class ConventionalCandidateServiceImpl implements ConventionalCandidateService, MessageSourceAware {

	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

	private MessageSource messageSource;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private CSVUtil cSVUtil;

	@Autowired
	private ExcelUtil excelUtil;

	@Autowired
	private CandidateRepository candidateRepository;

	@Autowired
	private CandidateSampleCsvXlsMasterRepository candidateSampleCsvXlsMasterRepository;

	@Autowired
	private StatusMasterRepository statusMasterRepository;

	@Autowired
	private CommonValidation commonValidation;
	
	@Autowired
	private VendorChecksRepository vendorCheckRepository;

	//	@Autowired
	//	private CandidateStatusRepository candidateStatusRepository;
	//
	//	@Autowired
	//	private CandidateStatusHistoryRepository candidateStatusHistoryRepository;

	@Autowired
	private EmailSentTask emailSentTask;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ServiceTypeConfigRepository serviceTypeConfigRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private OrganizationConfigRepository organizationConfigRepository;

	//	@Autowired
	//	private CandidateEmailStatusRepository candidateEmailStatusRepository;

	@Autowired
	private ConventionalCandidateEmailStatusRepository conventionalCandidateEmailStatusRepository;

	@Autowired
	private ConventionalCandidateStatusRepository conventionalCandidateStatusRepository;

	@Autowired
	private ConventionalCandidateStatusHistoryRepository conventionalCandidateStatusHistoryReposiory;

	@Autowired
	private ConventionalCandidateService conventionalCandidateService;

	@Autowired
	private ContentService contentService;

	@Autowired
	private CandidateVerificationStateRepository candidateVerificationStateRepository;

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private ConventionalCandidateVerificationStateRepository conventionalCandidateVerificationStateRepository;
	
	@Autowired
	private CandidateStatusHistoryRepository candidateStatusHistoryRepository;
	
	@Autowired
	private CandidateStatusRepository candidateStatusRepository;

	
	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;		
	}

	public Date getExpireDate(Long orgId) {
		Optional<OrganizationConfig> organizationConfig = organizationConfigRepository.findByOrganizationId(orgId);
		Integer inviteExpiryDays = 3;
		if(organizationConfig.isPresent() && organizationConfig.get().getInviteExpiryDays() != null && 
				organizationConfig.get().getInviteExpiryDays()!= 0) {
			inviteExpiryDays = organizationConfig.get().getInviteExpiryDays();
		}
		Date dateOfExpire = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(dateOfExpire);
		c.add(Calendar.DATE, inviteExpiryDays);
		c.add(Calendar.HOUR, 1);

		return c.getTime();

	}

	@Transactional
	@Override
	public ServiceOutcome<List> saveConventionalCandidateInformation(MultipartFile file,String candidateCode,boolean digitalToConventionalCandidateFlow,String accountName) {

		ServiceOutcome<List> svcSearchResult = new ServiceOutcome<List>();

		try {
			log.info("candidateCode : "+candidateCode);
			log.info("ACCOUNTNAME :"+accountName);

			User user = SecurityHelper.getCurrentUser();

			Organization organization = organizationRepository.findById(user.getOrganization().getOrganizationId()).get();
			Date linkExpireDate = getExpireDate(organization.getOrganizationId());
			RandomString rd = null;
			List<Candidate> candidates = null;
			List<Candidate> candidateList = null;
			List<ConventionalCandidateStatus> candidateStatusList = new ArrayList<ConventionalCandidateStatus>();
			CandidateSampleCsvXlsMaster candidateSampleCsvXlsMaster = null;


			if(file != null) {		
				String originalFilename = file.getOriginalFilename();
//				System.out.println("OrginalFileName::>>>" + originalFilename);
				if (CSVUtil.hasCSVFormat(file)) {
					candidates = cSVUtil.csvToConventionalCandidateList(file.getInputStream(), originalFilename,
							organization.getNoYearsToBeVerified());
					candidateSampleCsvXlsMaster = new CandidateSampleCsvXlsMaster();
					//				candidateSampleCsvXlsMaster.setCandidateSampleCsv(file.getBytes());

				}
				if (ExcelUtil.hasExcelFormat(file)) {
					candidates = excelUtil.excelToConventionalCandidate(file.getInputStream(), originalFilename,
							organization.getNoYearsToBeVerified());
					candidateSampleCsvXlsMaster = new CandidateSampleCsvXlsMaster();
					//				candidateSampleCsvXlsMaster.setCandidateSampleXls(file.getBytes());

				}
			}
			if (candidates != null && !candidates.isEmpty()) {
				for (Candidate candidate : candidates) {
					candidate.setOrganization(organization);
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
					candidate.setLinkExpireDate(linkExpireDate);
					candidate.setCreatedBy(user);
				}

				candidateList = candidateRepository.saveAllAndFlush(candidates);

			}

			if (candidateList != null && !candidateList.isEmpty()) {
				candidateSampleCsvXlsMaster.setOrganization(organizationRepository.findById(user.getOrganization().getOrganizationId()).get());
				candidateSampleCsvXlsMaster.setUploadedTimestamp(new Date());
				candidateSampleCsvXlsMaster.setCreatedBy(user);
				candidateSampleCsvXlsMaster.setCreatedOn(new Date());
				CandidateSampleCsvXlsMaster result = candidateSampleCsvXlsMasterRepository.save(candidateSampleCsvXlsMaster);
				candidateList.forEach(candidateOBJ -> candidateOBJ.setCandidateSampleId(result));

				candidateRepository.saveAllAndFlush(candidateList);
				for (Candidate candidate : candidateList) {
					ConventionalCandidateStatus conCandidateStatus = new ConventionalCandidateStatus();
					conCandidateStatus.setCandidate(candidate);
					conCandidateStatus.setCreatedBy(user);
					conCandidateStatus.setCreatedOn(new Date());
					if (candidate.getCcEmailId() != null && !candidate.getCcEmailId().isEmpty()) {
						if (commonValidation.validationEmail(candidate.getEmailId())) {
							conCandidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("CONVENTIONALNEWUPLOAD"));
							//							candidateStatus.setStatusMaster(null);
							//							candidateStatus.setConventionalStatusId(statusMasterRepository.findByStatusCode("CONVENTIONALNEWUPLOAD"));

						} else {
							conCandidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("CONVENTIONALINVALIDUPLOAD"));
							conCandidateStatus.setLastUpdatedOn(new Date());
							conCandidateStatus.setLastUpdatedBy(user);
						}
					} else {
						if (commonValidation.validationEmail(candidate.getEmailId())) {
							conCandidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("CONVENTIONALNEWUPLOAD"));
							//							candidateStatus.setStatusMaster(null);
							//							candidateStatus.setConventionalStatusId(statusMasterRepository.findByStatusCode("CONVENTIONALNEWUPLOAD"));
						} else {
							conCandidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("CONVENTIONALINVALIDUPLOAD"));
							conCandidateStatus.setLastUpdatedOn(new Date());
							conCandidateStatus.setLastUpdatedBy(user);
						}
					}
					conCandidateStatus = conventionalCandidateStatusRepository.save(conCandidateStatus);
					if (candidate.getOrganization().getCallBackUrl() != null)
						postStatusToOrganization(candidate.getCandidateCode());
					candidateStatusList.add(conCandidateStatus);
					createConventionalCandidateStatusHistory(conCandidateStatus, "NOTCANDIDATE");
					log.info("ConventionalCandidateStatus : CONVENTIONALNEWUPLOAD => candidateId : "+candidate.getCandidateId());

				}
				List<String> referenceList = candidateStatusList.stream()
						.filter(c -> c.getStatusMaster().getStatusCode().equals("CONVENTIONALNEWUPLOAD"))
						.map(x -> x.getCandidate().getCandidateCode()).collect(Collectors.toList());
				CandidateInvitationSentDto candidateInvitationSentDto = new CandidateInvitationSentDto();
				candidateInvitationSentDto.setCandidateReferenceNo(referenceList);
				//				System.out.println(referenceList + "referenceList");
				candidateInvitationSentDto.setStatuscode("CONVENTIONALINVITATIONSENT");
				//				candidateInvitationSentDto.setStatuscode(null);
				ServiceOutcome<Boolean> svcOutcome = conventionalCandidateService.conventionalInvitationSent(candidateInvitationSentDto);

				if (svcOutcome.getOutcome()) {
					svcSearchResult.setData(referenceList);
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage("File Uploaded Successfully");
				} else {
					svcSearchResult.setData(referenceList);
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage(svcOutcome.getMessage());
				}
			}


			else if(digitalToConventionalCandidateFlow) {
				Candidate byCandidateCode = candidateRepository.findByCandidateCode(candidateCode);

				byCandidateCode.setAccountName(accountName);
				byCandidateCode.setConventionalCandidate(true);
				candidateRepository.save(byCandidateCode);

//					Boolean sendEmail = emailSentTask.sendEmail(candidateCode,
//							byCandidateCode.getCandidateName(),
//							byCandidateCode.getEmailId(),
//							byCandidateCode.getCcEmailId());


//					if(sendEmail) {
					System.out.println("digitalToConventionalCandidateFlow is true::1234");
					ConventionalCandidateStatus conventionalCandidateStatus = new ConventionalCandidateStatus();
					conventionalCandidateStatus.setCandidate(byCandidateCode);
					conventionalCandidateStatus.setCreatedBy(user);
					conventionalCandidateStatus.setCreatedOn(byCandidateCode.getCreatedOn());
					conventionalCandidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("CONVENTIONALINVITATIONSENT"));
					//						candidateStatus.setStatusMaster(null);
					conventionalCandidateStatus = conventionalCandidateStatusRepository.save(conventionalCandidateStatus);
					if (byCandidateCode.getOrganization().getCallBackUrl() != null)
						postStatusToOrganization(byCandidateCode.getCandidateCode());
					candidateStatusList.add(conventionalCandidateStatus);
					createConventionalCandidateStatusHistory(conventionalCandidateStatus, "NOTCANDIDATE");

					//						List<String> referenceList = candidateStatusList.stream()
					//								.filter(c -> c.getStatusMaster().getStatusCode().equals("CONVENTIONALNEWUPLOAD"))
					//								.map(x -> x.getCandidate().getCandidateCode()).collect(Collectors.toList());
					List<String> referenceList = new ArrayList<>();
					referenceList.add(byCandidateCode.getCandidateCode());
					CandidateInvitationSentDto candidateInvitationSentDto = new CandidateInvitationSentDto();
					candidateInvitationSentDto.setCandidateReferenceNo(referenceList);
					//						System.out.println(referenceList + "referenceList");
					log.info("ConventionalCandidateStatus : DIGITAL CANIDATE TO CONVENTIONAL CANDIDATE CONVENTIONALINVITATION SENT => candidateId : "+byCandidateCode.getCandidateId());
					candidateInvitationSentDto.setStatuscode("CONVENTIONALINVITATIONSENT");
					//						candidateInvitationSentDto.setStatuscode(null);
					ServiceOutcome<Boolean> svcOutcome = conventionalCandidateService.conventionalInvitationSent(candidateInvitationSentDto);


					//						svcSearchResult.setData(referenceList);
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage("Invitation Send Successfully");
					if (svcOutcome.getOutcome()) {
						svcSearchResult.setData(referenceList);
						svcSearchResult.setOutcome(true);
						svcSearchResult.setMessage("Invitation Send Successfully");
					} else {
						svcSearchResult.setData(referenceList);
						svcSearchResult.setOutcome(true);
						svcSearchResult.setMessage(svcOutcome.getMessage());
					}
//					}
			}
			else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("File Could not be Uploaded- filename already exists OR the file content is invalid.");

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
	public ServiceOutcome<DashboardDto> conventionalGetReportDeliveryDetailsStatusAndCount(DashboardDto dashboardDto) {
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

				// calling the function to get counts for dashboard
				int pendingCount = 0;
				int interimCount = 0;
				int processDeclinedCount = 0;
				int finalReportCount = 0;
				int conventionalPendingCount = 0;
				int conventionalInterimReport = 0;
				int conventionalApprovedCount = 0;
				List<Object[]> activityList = getConventionalCountsForDashboard(strToDate, strFromDate, dashboardDto.getUserId());
				for (Object[] activity : activityList) {
					//				"select newupload,invalid,reinvites,interReport,finalReport,cancelled,invExpired,pendingNow\n");

					finalReportCount = Integer.parseInt(activity[4].toString());
					processDeclinedCount = Integer.parseInt(activity[5].toString());
					//					interimCount = Integer.parseInt(activity[3].toString());
					//					pendingCount = Integer.parseInt(activity[7].toString());
					conventionalPendingCount = Integer.parseInt(activity[7].toString());
					conventionalInterimReport = Integer.parseInt(activity[3].toString());
					conventionalApprovedCount = Integer.parseInt(activity[8].toString());

//					log.info("conventionalPendingCount :"+conventionalPendingCount);
//					log.info("conventionalInterimReport :"+conventionalInterimReport);
//					log.info("processDeclinedCount :"+processDeclinedCount);
//					log.info("finalReportCount :"+finalReportCount);

				}


				//				ServiceOutcome<List<CandidateStatus>> svcOutCome = getCandidateStatusList(strToDate, strFromDate,
				//						dashboardDto.getUserId());
				//				candidateStatusList = svcOutCome.getData();
				StatusMaster pending = statusMasterRepository.findByStatusCode("PENDINGAPPROVAL");
				StatusMaster interim = statusMasterRepository.findByStatusCode("INTERIMREPORT");
				StatusMaster processDeclined = statusMasterRepository.findByStatusCode("CONVENTIONALPROCESSDECLINED");
				StatusMaster finalReport = statusMasterRepository.findByStatusCode("CONVENTIONALFINALREPORT");
				StatusMaster conventionalPending = statusMasterRepository.findByStatusCode("CONVENTIONALPENDINGAPPROVAL");
				StatusMaster conventionInterim = statusMasterRepository.findByStatusCode("CONVENTIONALINTERIMREPORT");
				StatusMaster conventionalApprove = statusMasterRepository.findByStatusCode("CONVENTIONALCANDIDATEAPPROVE");
						
				// getting user and checking the organization for naming the status
				User user = userRepository.findById(dashboardDto.getUserId()).get();
				List<String> orgServices = serviceTypeConfigRepository.getServiceSourceMasterByOrgId(user.getOrganization().getOrganizationId());
				
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	              Object principal = authentication.getPrincipal();
//	              log.info("principal:: {}",principal.toString());
	              String username = "";
	                  username = ((UserDetails) principal).getUsername();
//	                  log.info("username 2::?? {}",username);
	                  User findByUserName = userRepository.findByUserName(username);
//	                  log.info("ORgID::{}",findByUserName.getOrganization().getOrganizationName());	                  
//	                  log.info(principal.toString());
	                  
	                  List<String> serviceSourceMasterByOrgId = serviceTypeConfigRepository.getServiceSourceMasterByOrgId(findByUserName.getOrganization().getOrganizationId());
						
						boolean clientApproval = serviceSourceMasterByOrgId.stream()
							    .anyMatch(serviceCode -> serviceCode.equals("CONVENTIONALCLIENTAPPROVAL"));
				
	                  if(clientApproval){
	                	  candidateStatusCountDtoList.add(0,
	                			  new CandidateStatusCountDto("Pending Approval", conventionalPending.getStatusCode(), conventionalPendingCount));
	                	  candidateStatusCountDtoList.add(1,
	                			  new CandidateStatusCountDto("Approval Completed", conventionalApprove.getStatusCode(), conventionalApprovedCount));	                	  
	                  }else {
	                	  candidateStatusCountDtoList.add(0,
	                			  new CandidateStatusCountDto("CWF Completed", conventionalPending.getStatusCode(), conventionalPendingCount));
	                  }
	                  
				//					}

				candidateStatusCountDtoList.add(1, new CandidateStatusCountDto("Interim Report",
						conventionInterim.getStatusCode(), conventionalInterimReport));
				candidateStatusCountDtoList.add(2, new CandidateStatusCountDto("Process Declined",
						processDeclined.getStatusCode(), processDeclinedCount));
				candidateStatusCountDtoList.add(3, new CandidateStatusCountDto("Final Report",
						finalReport.getStatusCode(), finalReportCount));
				//				}
				DashboardDto dashboardDtoObj = new DashboardDto(strFromDate, strToDate, null, null,
						candidateStatusCountDtoList, dashboardDto.getUserId(), null, null,
						dashboardDto.getPageNumber());
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
	public ServiceOutcome<DashboardDto> conventionalGetCandidateStatusAndCount(DashboardDto dashboardDto) {
		ServiceOutcome<DashboardDto> svcSearchResult = new ServiceOutcome<DashboardDto>();
		List<CandidateStatusCountDto> candidateStatusCountDtoList = new ArrayList<CandidateStatusCountDto>();
		List<CandidateStatus> candidateStatusList = null;
		String strToDate = "";
		String strFromDate = "";
		try {
			if (dashboardDto.getUserId() != null && dashboardDto.getUserId() != 0l) {
				User findByUserName = userRepository.findByUserId(dashboardDto.getUserId());
				List<String> orgServices = serviceTypeConfigRepository.getServiceSourceMasterByOrgId(findByUserName.getOrganization().getOrganizationId());
				strToDate = dashboardDto.getToDate() != null ? dashboardDto.getToDate()
						: ApplicationDateUtils.getStringTodayAsDDMMYYYY();
				strFromDate = dashboardDto.getFromDate() != null ? dashboardDto.getFromDate()
						: ApplicationDateUtils.subtractNoOfDaysFromDateAsDDMMYYYY(
								new SimpleDateFormat("dd/MM/yyyy").parse(strToDate), 7);

				// calling the function to get counts for dashboard
				int conventionalNewUploadCount = 0;
				int conventionalInvitationexpiredCount = 0;
				int conventionalInvaliduploadCount = 0;
				int conventionalReinviteCount = 0;
				//			"select newupload,invalid,reinvites,interReport,finalReport,cancelled,invExpired,pendingNow\n");

				List<Object[]> activityList = getConventionalCountsForDashboard(strToDate, strFromDate, dashboardDto.getUserId());
				for (Object[] activity : activityList) {
					conventionalNewUploadCount = Integer.parseInt(activity[0].toString());
					conventionalInvitationexpiredCount = Integer.parseInt(activity[6].toString());
					conventionalInvaliduploadCount = Integer.parseInt(activity[1].toString());
					conventionalReinviteCount = Integer.parseInt(activity[2].toString());

//					log.info("conventionalNewUploadCount :"+conventionalNewUploadCount);
//					log.info("conventionalInvitationexpiredCount :"+conventionalInvitationexpiredCount);
//					log.info("conventionalInvaliduploadCount :"+conventionalInvaliduploadCount);
//					log.info("conventionalReinviteCount :"+conventionalReinviteCount);
				}

				// finish
				StatusMaster newUploadStatusMaster = statusMasterRepository.findByStatusCode("CONVENTIONALNEWUPLOAD");
				candidateStatusCountDtoList.add(0, new CandidateStatusCountDto("New Upload",
						newUploadStatusMaster.getStatusCode(), conventionalNewUploadCount));
				//								dashboardDtoObj1 != null ? dashboardDtoObj1.getCandidateDtoList().size() : 0));
				// candidateStatusList != null ? candidateStatusList.size() : 0));
				// candidateStatusCountDtoList.add(1, new
				// CandidateStatusCountDto(statusMasterRepository.findByStatusCode("INVITATIONSENT").getStatusName(),statusMasterRepository.findByStatusCode("INVITATIONSENT").getStatusCode(),invitationSentList!=null?invitationSentList.size():0));
				StatusMaster invitExpStatusMaster = statusMasterRepository.findByStatusCode("CONVENTIONALINVITATIONEXPIRED");
				//				if(orgServices!=null && orgServices.contains("EPFO") && orgServices.contains("DNHDB")
				//			 			&& !orgServices.contains("DIGILOCKER") && !orgServices.contains("ITR")) {
				//					candidateStatusCountDtoList.add(1, new CandidateStatusCountDto("Upload expired",
				//							invitExpStatusMaster.getStatusCode(), invitationexpiredCount));
				//				}else {
				candidateStatusCountDtoList.add(1, new CandidateStatusCountDto("Invitation Expired",
						invitExpStatusMaster.getStatusCode(), conventionalInvitationexpiredCount));
				//				}

				//								invitationexpiredList != null ? invitationexpiredList.size() : 0));
				StatusMaster invalidUploadStatusMaster = statusMasterRepository.findByStatusCode("CONVENTIONALINVALIDUPLOAD");
				//				if(orgServices!=null && orgServices.contains("EPFO") && orgServices.contains("DNHDB")
				//			 			&& !orgServices.contains("DIGILOCKER") && !orgServices.contains("ITR")) {
				//					invalidUploadStatusMaster = statusMasterRepository.findByStatusCode("UANFETCHFAILED");
				//				}
				candidateStatusCountDtoList.add(2,
						new CandidateStatusCountDto("Invalid Upload",
								invalidUploadStatusMaster.getStatusCode(), conventionalInvaliduploadCount));
				//								invalidUploadList != null ? invalidUploadList.size() : 0));

				StatusMaster reInnviteStatusMaster = statusMasterRepository.findByStatusCode("CONVENTIONALREINVITE");
				candidateStatusCountDtoList.add(3, new CandidateStatusCountDto("Re Invite",
						reInnviteStatusMaster.getStatusCode(), conventionalReinviteCount));

				DashboardDto dashboardDtoObj = new DashboardDto(strFromDate, strToDate, null, null,
						candidateStatusCountDtoList, dashboardDto.getUserId(), null, null,
						dashboardDto.getPageNumber());
				svcSearchResult.setData(dashboardDtoObj);
				svcSearchResult.setOutcome(true);
				//				svcSearchResult
				//						.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("please specify user.");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getCandidateStatusAndCount method in ConventionalCandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			//			svcSearchResult
			//					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	public List<Object[]> getConventionalCountsForDashboard(String strToDate, String strFromDate, Long userId) {
		List<Object[]> activityList = new ArrayList<>();
		List<Long> agentIds = new ArrayList<>();
		Long orgId = 0L;
		try {
			Date startDate = formatter.parse(strFromDate + " 00:00:00");
			Date endDate = formatter.parse(strToDate + " 23:59:59");
			User user = userRepository.findById(userId).get();
			if (user.getRole().getRoleCode().equalsIgnoreCase("ROLE_ADMIN")
					|| user.getRole().getRoleCode().equalsIgnoreCase("ROLE_PARTNERADMIN") || user.getRole().getRoleCode().equalsIgnoreCase("ROLE_CLIENTAGENT") || user.getRole().getRoleCode().equalsIgnoreCase("ROLE_CLIENTSUPERVISOR") ) {

				orgId = user.getOrganization().getOrganizationId();
			}
			if (user.getRole().getRoleCode().equalsIgnoreCase("ROLE_AGENTSUPERVISOR")
					|| user.getRole().getRoleCode().equalsIgnoreCase("ROLE_AGENTHR")) {
				List<User> agentList = userRepository.findAllByAgentSupervisorUserId(user.getUserId());
				if (!agentList.isEmpty()) {
					agentIds = agentList.stream().map(x -> x.getUserId()).collect(Collectors.toList());
				}
				agentIds.add(user.getUserId());
			}

			//checking the organization scope here
			List<String> orgServices = serviceTypeConfigRepository.getServiceSourceMasterByOrgId(user.getOrganization()!=null ?
					user.getOrganization().getOrganizationId() : 0);

			// Query Start
			StringBuilder query = new StringBuilder();
			query.append(
					"select newupload,invalid,reinvites,interReport,finalReport,cancelled,invExpired,pendingNow,conventionalApprove\n");
			query.append("from \n");
			query.append(
					"(select count(distinct tdcsh.candidate_id) as newupload from t_dgv_conventional_candidate_status_history tdcsh\n");
			query.append("join t_dgv_candidate_basic bas on bas.candidate_id = tdcsh.candidate_id \n");
			query.append("join t_dgv_organization_master org on bas.organization_id = org.organization_id \n");
			query.append("join t_dgv_status_master mas on mas.status_master_id = tdcsh.status_master_id \n");
			query.append("where mas.status_code IN ('CONVENTIONALNEWUPLOAD','CONVENTIONALINVALIDUPLOAD')\n");
			if (orgId != 0) {
				query.append("and org.organization_id =:orgId\n");
			}
			if (agentIds != null && !agentIds.isEmpty()) {
				query.append("and bas.created_by IN (:agentIds)\n");
			}

			query.append("and tdcsh.candidate_status_change_timestamp between :startDate and :endDate) ne,\n");
			query.append("(select count(*) as invalid from t_dgv_conventional_candidate_status tdcsh\n");
			query.append("join t_dgv_candidate_basic bas on bas.candidate_id = tdcsh.candidate_id \n");
			query.append("join t_dgv_organization_master org on bas.organization_id = org.organization_id \n");
			query.append("join t_dgv_status_master mas on mas.status_master_id = tdcsh.status_master_id \n");
			query.append("where mas.status_code IN ('CONVENTIONALINVALIDUPLOAD')\n");
			if (orgId != 0) {
				query.append("and org.organization_id =:orgId\n");
			}
			if (agentIds != null && !agentIds.isEmpty()) {
				query.append("and bas.created_by IN (:agentIds)\n");
			}
			//			}
			query.append("and tdcsh.last_updated_on between :startDate and :endDate) ie,\n");
			query.append("(select count(*) as reinvites from t_dgv_conventional_candidate_status tdcsh\n");
			query.append("join t_dgv_candidate_basic bas on bas.candidate_id = tdcsh.candidate_id \n");
			query.append("join t_dgv_organization_master org on bas.organization_id = org.organization_id \n");
			query.append("join t_dgv_status_master mas on mas.status_master_id = tdcsh.status_master_id \n");
			query.append("where mas.status_code ='CONVENTIONALREINVITE'\n");
			if (orgId != 0) {
				query.append("and org.organization_id =:orgId\n");
			}
			if (agentIds != null && !agentIds.isEmpty()) {
				query.append("and bas.created_by IN (:agentIds)\n");
			}
			query.append("and tdcsh.last_updated_on between :startDate and :endDate) re,\n");
			query.append("(select count(*) as interReport from t_dgv_conventional_candidate_status tdcsh\n");
			query.append("join t_dgv_candidate_basic bas on bas.candidate_id = tdcsh.candidate_id \n");
			query.append("join t_dgv_organization_master org on bas.organization_id = org.organization_id \n");
			query.append("join t_dgv_status_master mas on mas.status_master_id = tdcsh.status_master_id \n");
			query.append("where mas.status_code ='CONVENTIONALINTERIMREPORT'\n");
			if (orgId != 0) {
				query.append("and org.organization_id =:orgId\n");
			}
			if (agentIds != null && !agentIds.isEmpty()) {
				query.append("and bas.created_by IN (:agentIds)\n");
			}
			query.append("and tdcsh.last_updated_on between :startDate and :endDate) ir,\n");
			query.append("(select count(*) as finalReport from t_dgv_conventional_candidate_status tdcsh \n");
			query.append("join t_dgv_candidate_basic bas on bas.candidate_id = tdcsh.candidate_id \n");
			query.append("join t_dgv_organization_master org on bas.organization_id = org.organization_id \n");
			query.append("join t_dgv_status_master mas on mas.status_master_id = tdcsh.status_master_id \n");
			query.append("where mas.status_code ='CONVENTIONALFINALREPORT'\n");
			if (orgId != 0) {
				query.append("and org.organization_id =:orgId\n");
			}
			if (agentIds != null && !agentIds.isEmpty()) {
				query.append("and bas.created_by IN (:agentIds)\n");
			}
			query.append("and tdcsh.last_updated_on between :startDate and :endDate) fr,\n");
			query.append("(select count(*) as cancelled from t_dgv_conventional_candidate_status tdcsh\n");
			query.append("join t_dgv_candidate_basic bas on bas.candidate_id = tdcsh.candidate_id \n");
			query.append("join t_dgv_organization_master org on bas.organization_id = org.organization_id \n");
			query.append("join t_dgv_status_master mas on mas.status_master_id = tdcsh.status_master_id \n");
			query.append("where mas.status_code ='CONVENTIONALPROCESSDECLINED'\n");
			if (orgId != 0) {
				query.append("and org.organization_id =:orgId\n");
			}
			if (agentIds != null && !agentIds.isEmpty()) {
				query.append("and bas.created_by IN (:agentIds)\n");
			}
			query.append("and tdcsh.last_updated_on between :startDate and :endDate) can,\n");
			query.append("(select count(*) as invExpired from t_dgv_conventional_candidate_status tdcsh\n");
			query.append("join t_dgv_candidate_basic bas on bas.candidate_id = tdcsh.candidate_id \n");
			query.append("join t_dgv_organization_master org on bas.organization_id = org.organization_id \n");
			query.append("join t_dgv_status_master mas on mas.status_master_id = tdcsh.status_master_id \n");
			query.append("where mas.status_code ='CONVENTIONALINVITATIONEXPIRED'\n");
			if (orgId != 0) {
				query.append("and org.organization_id =:orgId\n");
			}
			if (agentIds != null && !agentIds.isEmpty()) {
				query.append("and bas.created_by IN (:agentIds)\n");
			}
			query.append("and tdcsh.last_updated_on between :startDate and :endDate) inve,\n");
			query.append("(select count(*) as pendingNow from t_dgv_conventional_candidate_status tdcsh\n");
			query.append("join t_dgv_candidate_basic bas on bas.candidate_id = tdcsh.candidate_id \n");
			query.append("join t_dgv_organization_master org on bas.organization_id = org.organization_id \n");
			query.append("join t_dgv_status_master mas on mas.status_master_id = tdcsh.status_master_id \n");
			query.append("where mas.status_code ='CONVENTIONALPENDINGAPPROVAL'\n");
			if (orgId != 0) {
				query.append("and org.organization_id =:orgId\n");
			}
			if (agentIds != null && !agentIds.isEmpty()) {
				query.append("and bas.created_by IN (:agentIds)\n");
			}
			
			query.append("and tdcsh.last_updated_on between :startDate and :endDate) app,\n");
			query.append("(select count(*) as conventionalApprove from t_dgv_conventional_candidate_status tds\n");
			query.append("join t_dgv_candidate_basic bas on bas.candidate_id = tds.candidate_id \n");
			query.append("join t_dgv_organization_master org on bas.organization_id = org.organization_id \n");
			query.append("join t_dgv_status_master mas on mas.status_master_id = tds.status_master_id \n");
			query.append("where mas.status_code ='CONVENTIONALCANDIDATEAPPROVE'\n");
			if (orgId != 0) {
				query.append("and org.organization_id =:orgId\n");
			}
			if (agentIds != null && !agentIds.isEmpty()) {
				query.append("and bas.created_by IN (:agentIds)\n");
			}
			
			query.append("and tds.last_updated_on between :startDate and :endDate) pd\n");

			Query resultQuery = entityManager.createNativeQuery(query.toString());
			resultQuery.setParameter("startDate", startDate);
			resultQuery.setParameter("endDate", endDate);
			if (orgId != 0) {
				resultQuery.setParameter("orgId", orgId);
			}
			if (agentIds != null && !agentIds.isEmpty()) {
				resultQuery.setParameter("agentIds", agentIds);
			}

			activityList = resultQuery.getResultList();
		} catch (Exception ex) {
			log.error("Exception occured in getCountsForDashboard method in CandidateServiceImpl-->", ex);
		}
		return activityList;

	}

	private HttpHeaders setHeaderDetails(HttpHeaders headers) {
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	@Override
	public void postStatusToOrganization(String candidateCode) {
		try {
			Candidate findByCandidateCode = candidateRepository.findByCandidateCode(candidateCode);
			ConventionalCandidateStatus candidateStatus = conventionalCandidateStatusRepository.findByCandidateCandidateCode(candidateCode);
			Organization organization = findByCandidateCode.getOrganization();

			if (organization != null && organization.getCallBackUrl() != null
					&& !organization.getCallBackUrl().isEmpty()) {
				String orgCallBackUrl = organization.getCallBackUrl();
				log.info("Organization Callback Url for candidate::{}", candidateCode + "::::" + orgCallBackUrl);

				// Format the date
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
				String formattedDate = dateFormat.format(new Date());

				HttpHeaders headers = new HttpHeaders();
				setHeaderDetails(headers);
				// request object
				JSONObject requestJson = new JSONObject();
				requestJson.put("candidateCode", candidateCode);
				requestJson.put("status", candidateStatus.getStatusMaster().getStatusCode());
				requestJson.put("submittedOn", formattedDate);

				log.info("Request to post status to organization ::{}", requestJson.toString());
				HttpEntity<String> requestEntity = new HttpEntity<>(requestJson.toString(), headers);

				// calling callback URL To post the candidate status
				ResponseEntity<String> response = restTemplate.exchange(orgCallBackUrl, HttpMethod.POST, requestEntity,
						String.class);
				String message = response.getBody();
				log.info("Response after posting the status to organization ::{}", message);

			}
		} catch (HttpClientErrorException c) {
			log.info("Exception in Client call To send status for ::{}", candidateCode);
			log.error("CLIENT Exception occured while posting the candidate status to client::{}", c);
		} catch (Exception e) {
			log.info("Exception To send status for ::{}", candidateCode);
			log.info("Exception in postStatusToOrganization::{}", e.getMessage());
		}

	}

	@Transactional
	@Override
	public ConventionalCandidateStatusHistory createConventionalCandidateStatusHistory(ConventionalCandidateStatus candidateStatus, String who) {
		ConventionalCandidateStatusHistory conventionalcandidateStatusHistoryObj = new ConventionalCandidateStatusHistory();
		try {
			conventionalcandidateStatusHistoryObj.setCandidate(candidateStatus.getCandidate());
			conventionalcandidateStatusHistoryObj.setStatusMaster(candidateStatus.getStatusMaster());
			if (who.equals("NOTCANDIDATE")) {
				conventionalcandidateStatusHistoryObj.setCreatedBy(SecurityHelper.getCurrentUser());
			} else {
				conventionalcandidateStatusHistoryObj.setCreatedBy(candidateStatus.getCandidate().getCreatedBy());
			}
			conventionalcandidateStatusHistoryObj.setCreatedOn(new Date());
			conventionalcandidateStatusHistoryObj.setCandidateStatusChangeTimestamp(new Date());
			conventionalCandidateStatusHistoryReposiory.save(conventionalcandidateStatusHistoryObj);
		} catch (Exception ex) {
			log.error("Exception occured in createConventionalCandidateStatusHistory method in CandidateServiceImpl-->", ex);
		}
		return conventionalcandidateStatusHistoryObj;
	}


	@Transactional
	@Override
	public ServiceOutcome<Boolean> conventionalInvitationSent(CandidateInvitationSentDto candidateInvitationSentDto) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		try {
			User user = SecurityHelper.getCurrentUser();
			ConventionalCandidateStatus conventionalCandidateStatus = null;
			ConventionalCandidateEmailStatus conventionalCandidateEmailStatus = null;
			ConventionalCandidateStatus candidateStatusresult = null;
			if (candidateInvitationSentDto.getCandidateReferenceNo().size() > 0) {
				for (int i = 0; i < candidateInvitationSentDto.getCandidateReferenceNo().size(); i++) {
					conventionalCandidateStatus = conventionalCandidateStatusRepository
							.findByCandidateCandidateCode(candidateInvitationSentDto.getCandidateReferenceNo().get(i));
					//updating candidate expire date
					if (conventionalCandidateStatus != null && conventionalCandidateStatus.getStatusMaster().getStatusCode().equals("INVITATIONEXPIRED")
							&& candidateInvitationSentDto.getStatuscode().equalsIgnoreCase("CONVENTIONALREINVITE")) {
						log.info("updating candidate expire date on reinvite");
						Candidate candidate =conventionalCandidateStatus.getCandidate();

						Date currentDatePlusOne = getExpireDate(candidate.getOrganization().getOrganizationId());
						candidate.setLinkExpireDate(currentDatePlusOne);

						candidateRepository.save(candidate);
					}
					//end expire date update
					if (conventionalCandidateStatus != null) {
						Long organizationId = conventionalCandidateStatus.getCandidate().getOrganization().getOrganizationId();
						List<String> serviceSourceMasterByOrgId = serviceTypeConfigRepository.getServiceSourceMasterByOrgId(organizationId);
						System.out.println("serviceSourceMasterByOrgId : "+serviceSourceMasterByOrgId.toString());
						
						Boolean result = false;
						if(serviceSourceMasterByOrgId.contains("INVITATIONSENT")){
//							System.out.println("Conventional InvitationSent");
							 result = emailSentTask.sendEmail(conventionalCandidateStatus.getCandidate().getCandidateCode(),
									conventionalCandidateStatus.getCandidate().getCandidateName(),
									conventionalCandidateStatus.getCandidate().getEmailId(),
									conventionalCandidateStatus.getCandidate().getCcEmailId());
							conventionalCandidateEmailStatus = conventionalCandidateEmailStatusRepository
									.findByCandidateCandidateCode(conventionalCandidateStatus.getCandidate().getCandidateCode());
						}
						if (conventionalCandidateEmailStatus == null) {
							conventionalCandidateEmailStatus = new ConventionalCandidateEmailStatus();
							conventionalCandidateEmailStatus.setCreatedBy(user);
							conventionalCandidateEmailStatus.setCreatedOn(new Date());
							conventionalCandidateEmailStatus.setCandidate(conventionalCandidateStatus.getCandidate());
						} else {
							conventionalCandidateEmailStatus.setLastUpdatedBy(user);
							conventionalCandidateEmailStatus.setLastUpdatedOn(new Date());
						}
						
						if (!result) {
							conventionalCandidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("CONVENTIONALNEWUPLOAD"));
							conventionalCandidateEmailStatus.setDateOfEmailInvite(new Date());
						}
						else if (result && candidateInvitationSentDto.getStatuscode().equalsIgnoreCase("CONVENTIONALINVITATIONSENT")) {
							log.info("ConventionalcandidateStatus Moved to : CONVENTIONALINVITATIONSENT => candidateId : "+conventionalCandidateStatus.getCandidate().getCandidateId());
							conventionalCandidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("CONVENTIONALINVITATIONSENT"));
							conventionalCandidateEmailStatus.setDateOfEmailInvite(new Date());
						}
						else if (result && candidateInvitationSentDto.getStatuscode().equalsIgnoreCase("CONVENTIONALREINVITE")) {
							log.info("ConventionalcandidateStatus Moved to : CONVENTIONALREINVITE => candidateId : "+conventionalCandidateStatus.getCandidate().getCandidateId());

							// Start changes for get back to candidate on its previous state
							List<ConventionalCandidateStatusHistory> candidateStatusHistories = conventionalCandidateStatusHistoryReposiory
									.findAllByCandidateCandidateId(conventionalCandidateStatus.getCandidate().getCandidateId());

//							System.out.println("candidateStatusHistories>>>"+candidateStatusHistories.toString());

							if (!candidateStatusHistories.isEmpty() && "CONVENTIONALINVITATIONEXPIRED".equals(candidateStatusHistories.get(candidateStatusHistories.size() - 1).getStatusMaster().getStatusCode())) {
								log.info("ConventionalcandidateStatus Moved to : CONVENTIONALREINVITE => candidateId : "+conventionalCandidateStatus.getCandidate().getCandidateId());

								conventionalCandidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("CONVENTIONALREINVITE"));
							} else {
								//								// adding reinvite status only in history
								//								CandidateStatus candidateReinviteStatus = new CandidateStatus();
								//								candidateReinviteStatus.setCandidate(candidateStatus.getCandidate());
								//								candidateReinviteStatus.setServiceSourceMaster(candidateStatus.getServiceSourceMaster());
								//								candidateReinviteStatus.setStatusMaster(statusMasterRepository.findByStatusCode("REINVITE"));
								//								candidateReinviteStatus.setCreatedBy(user);
								//								candidateReinviteStatus.setCreatedOn(new Date());
								//								candidateReinviteStatus.setLastUpdatedBy(user);
								//								candidateReinviteStatus.setLastUpdatedOn(new Date());
								//								createCandidateStatusHistory(candidateReinviteStatus, "NOTCANDIDATE");

								// setting current status before the status of reinviting
								conventionalCandidateStatus.setStatusMaster(candidateStatusHistories
										.get(candidateStatusHistories.size() - 1).getStatusMaster());
							}
							// end
							// candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("REINVITE"));
							conventionalCandidateEmailStatus.setDateOfEmailReInvite(new Date());
						} else {
							conventionalCandidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("CONVENTIONALINVALIDUPLOAD"));
							conventionalCandidateEmailStatus.setDateOfEmailFailure(new Date());
						}
						conventionalCandidateStatus.setLastUpdatedBy(user);
						conventionalCandidateStatus.setLastUpdatedOn(new Date());
						candidateStatusresult = conventionalCandidateStatusRepository.save(conventionalCandidateStatus);
						if (conventionalCandidateStatus.getCandidate().getOrganization().getCallBackUrl() != null)
							postStatusToOrganization(conventionalCandidateStatus.getCandidate().getCandidateCode());
						createConventionalCandidateStatusHistory(candidateStatusresult, "NOTCANDIDATE");
						conventionalCandidateEmailStatus.setConventionalCandidateStatus(candidateStatusresult);
						conventionalCandidateEmailStatusRepository.save(conventionalCandidateEmailStatus);
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
	public ServiceOutcome<ConventionalCandidateStatus> conventionalGetCandidateStatusByCandidateCode(String code) {
		ServiceOutcome<ConventionalCandidateStatus> outcome = new ServiceOutcome<>();
		try {
			ConventionalCandidateStatus candidateStatus = conventionalCandidateStatusRepository.findByCandidateCandidateCode(code);
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
	public ServiceOutcome<DashboardDto> getAllConventionalCandidateList(DashboardDto dashboardDto) {
		ServiceOutcome<DashboardDto> svcSearchResult = new ServiceOutcome<DashboardDto>();
		List<Candidate> candidateList = new ArrayList<Candidate>();
		List<CandidateDetailsDto> candidateDtoList = new ArrayList<CandidateDetailsDto>();
		List<String> statusCodes = new ArrayList<String>();
		List<Long> agentIds = new ArrayList<Long>();
		String strToDate = "";
		String strFromDate = "";
		User currentUser = SecurityHelper.getCurrentUser();
		try {
			if (currentUser.getUserId() != null && currentUser.getUserId() != 0l
					&& StringUtils.isNotBlank(dashboardDto.getStatus())) {
				//				log.info("PAGE NUMBER::{}",dashboardDto.getPageNumber());
				Pageable pageable = null;
				if (dashboardDto.getPageNumber() != null) {
					pageable = PageRequest.of(dashboardDto.getPageNumber(), 10);
				}
				User user = userRepository.findById(currentUser.getUserId()).get();
				strToDate = dashboardDto.getToDate() != null ? dashboardDto.getToDate()
						: ApplicationDateUtils.getStringTodayAsDDMMYYYY();
				strFromDate = dashboardDto.getFromDate() != null ? dashboardDto.getFromDate()
						: ApplicationDateUtils.subtractNoOfDaysFromDateAsDDMMYYYY(
								new SimpleDateFormat("dd/MM/yyyy").parse(strToDate), 7);
				Date startDate = formatter.parse(strFromDate + " 00:00:00");
				Date endDate = formatter.parse(strToDate + " 23:59:59");
				String status = dashboardDto.getStatus();
//				log.info("Conventional Dashboard Status : "+status);
				if (status.equals("CONVENTIONALPENDINGAPPROVAL")) {
					status = "CONVENTIONALPENDINGAPPROVAL";
					statusCodes.add(0, status);

					//					status = "INTERIMREPORT";
					//					statusCodes.add(0, status);
				}
				if (status.equals("CONVENTIONALINTERIMREPORT")) {
					status = "CONVENTIONALINTERIMREPORT";
					statusCodes.add(0, status);
				} 
				//				else if (status.equals("CAFPENDING")) {
				//					status = "RELATIVEADDRESS";
				//					statusCodes.add(0, status);
				//				} else if (status.equals("EPFOSKIPPED")) {
				//					Collections.addAll(statusCodes, "ITR", "DIGILOCKER");
				//				} 
				else if (status.equals("CONVENTIONALNEWUPLOAD")) {
					statusCodes.addAll(statusMasterRepository.findAll().parallelStream().map(x -> x.getStatusCode())
							.collect(Collectors.toList()));
				} else {
					statusCodes.add(0, status);
				}
				List<StatusMaster> statusMasterList = statusMasterRepository.findByStatusCodeIn(statusCodes);
				List<Long> statusIds = statusMasterList.stream().map(x -> x.getStatusMasterId())
						.collect(Collectors.toList());
				if (user.getRole().getRoleCode().equalsIgnoreCase("ROLE_ADMIN")
						|| user.getRole().getRoleCode().equalsIgnoreCase("ROLE_PARTNERADMIN") || user.getRole().getRoleCode().equalsIgnoreCase("ROLE_CLIENTAGENT") || user.getRole().getRoleCode().equalsIgnoreCase("ROLE_CLIENTSUPERVISOR")) {
					if (status.equals("CONVENTIONALNEWUPLOAD")) {
						// System.out.println("IF NEWUPLOAD");
						// System.out.println("STATUS ID::: "+statusIds);
						if (pageable != null) {
							candidateList = candidateRepository
									.getPageCandidateListByOrganizationIdAndStatusAndCreatedOnConventional(
											user.getOrganization().getOrganizationId(), statusIds, startDate, endDate,
											pageable);
						} else {
							candidateList = candidateRepository.getCandidateListByOrganizationIdAndStatusAndCreatedOnConventional(
									user.getOrganization().getOrganizationId(), statusIds, startDate, endDate);
						}
						// System.out.println("CANIDATELIST IN NEWUPLOAD::"+candidateList.size());
					} else {
						// System.out.println("Now getting pagewise QC");
						if (pageable != null) {
							candidateList = candidateRepository
									.getPageCandidateListByOrganizationIdAndStatusAndCreatedOnConventional(
											user.getOrganization().getOrganizationId(), statusIds, startDate, endDate,
											pageable);
							//							log.info("PAGINATION CANDIDATE LIST SIZE::{}",candidateList.size());
						} else {
							candidateList = candidateRepository.getCandidateListByOrganizationIdAndStatusAndCreatedOnConventional(
									user.getOrganization().getOrganizationId(), statusIds, startDate, endDate);
						}
					}

				}
				if (user.getRole().getRoleCode().equalsIgnoreCase("ROLE_AGENTSUPERVISOR")
						|| user.getRole().getRoleCode().equalsIgnoreCase("ROLE_AGENTHR")) {
					List<User> agentList = userRepository.findAllByAgentSupervisorUserId(user.getUserId());
					if (!agentList.isEmpty()) {
						agentIds = agentList.stream().map(x -> x.getUserId()).collect(Collectors.toList());
					}
					agentIds.add(user.getUserId());

					if (pageable != null) {
						candidateList = candidateRepository.getPageCandidateListByUserIdAndStatusAndLastUpdatedConventional(
								agentIds, statusIds, startDate, endDate, pageable);
						log.info("PAGINATION CANDIDATE LIST SIZE::{}", candidateList.size());
					} else {
						candidateList = candidateRepository.getCandidateListByUserIdAndStatusAndLastUpdated(agentIds,
								statusIds, startDate, endDate);
					}
				}
				
				for (Candidate candidate : candidateList) {
//				if(candidate.getConventionalCandidate() != null && candidate.getConventionalCandidate()) {
					CandidateDetailsDto candidateDto = this.modelMapper.map(candidate, CandidateDetailsDto.class);
					candidateDto.setCreatedOn(formatter.format(candidate.getCreatedOn()));
					candidateDto.setSubmittedOn(
							candidate.getSubmittedOn() != null ? formatter.format(candidate.getSubmittedOn()) : null);
					ConventionalCandidateEmailStatus candidateEmailStatus = conventionalCandidateEmailStatusRepository
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
					ConventionalCandidateStatus candidateStatus = conventionalCandidateStatusRepository
							.findByCandidateCandidateCode(candidate.getCandidateCode());
//					ConventionalCandidateStatus candidateStatus = conventionalCandidateStatusRepository
//							.findByCandidateCandidateId(candidate.getCandidateId());
					Boolean uan = candidate.getIsUanSkipped() != null ? candidate.getIsUanSkipped() : false;
					//					if (candidateStatus.getStatusMaster().getStatusCode().equals("DIGILOCKER") && uan
					//							|| candidateStatus.getStatusMaster().getStatusCode().equals("ITR") && uan) {
					//						candidateDto.setCandidateStatusName("EPFO Skipped");
					//					} else {
					Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
					Object principal = authentication.getPrincipal();
//					log.info("principal:: {}",principal.toString());
					String username = "";
					username = ((UserDetails) principal).getUsername();
//					log.info("username 2::?? {}",username);
					User findByUserName = userRepository.findByUserName(username);
//					log.info("ORgID::{}",findByUserName.getOrganization().getOrganizationName());	                  
//					log.info(principal.toString());

					List<String> serviceSourceMasterByOrgId = serviceTypeConfigRepository.getServiceSourceMasterByOrgId(findByUserName.getOrganization().getOrganizationId());
					
					boolean clientApproval = serviceSourceMasterByOrgId.stream()
						    .anyMatch(serviceCode -> serviceCode.equals("CONVENTIONALCLIENTAPPROVAL"));
					
//					if(findByUserName.getOrganization().getOrganizationName().equalsIgnoreCase("Prodapt Solutions Private Limited")){
					if(clientApproval) {
						String statusName = candidateStatus.getStatusMaster().getStatusName();
						switch (statusName.toLowerCase()) {
						    case "conventional qc pending":
						        candidateDto.setCandidateStatusName("Pending Approval");
						        break;
						    case "conventional candidate approve":
						        candidateDto.setCandidateStatusName("Conventional QC Pending");
						        break;
						    default:
								candidateDto.setCandidateStatusName(candidateStatus.getStatusMaster().getStatusName());
						        break;
						}
					}
					else {					
						candidateDto.setCandidateStatusName(candidateStatus.getStatusMaster().getStatusName());
					}
					//					}
					candidateDto.setLastUploadedOn(candidateStatus.getLastUpdatedOn());
					List<ContentDTO> contentDTOList = contentService
							.getContentListByCandidateId(candidate.getCandidateId());
					//					System.out.println(contentDTOList + "--------contentdtolist-------");
					candidateDto.setContentDTOList(contentDTOList);

					ConventionalCandidateVerificationState updateVerificationStatus = conventionalCandidateVerificationStateRepository
							.findByCandidateCandidateId(candidate.getCandidateId());
					if (updateVerificationStatus != null) {
						candidateDto.setPreOfferVerificationColorCode(
								updateVerificationStatus.getPreApprovalColorCodeStatus());
						candidateDto
						.setInterimVerificationColorCode(updateVerificationStatus.getInterimColorCodeStatus());
						candidateDto.setFinalVerificationColorCode(updateVerificationStatus.getFinalColorCodeStatus());

						// adding report delivered dates
						candidateDto.setPreOfferReportDate(updateVerificationStatus.getPreApprovalTime() != null
								? formatter.format(Date.from(updateVerificationStatus.getPreApprovalTime().toInstant()))
										: null);
						candidateDto
						.setInterimReportDate(updateVerificationStatus.getInterimReportTime() != null
						? formatter.format(
								Date.from(updateVerificationStatus.getInterimReportTime().toInstant()))
								: null);
						candidateDto.setFinalReportDate(updateVerificationStatus.getFinalReportTime() != null
								? formatter.format(Date.from(updateVerificationStatus.getFinalReportTime().toInstant()))
										: null);
						candidateDto.setCaseInitiationDate(updateVerificationStatus.getCaseInitiationTime() != null
								? formatter
										.format(Date.from(updateVerificationStatus.getCaseInitiationTime().toInstant()))
										: null);
					}
					candidateDtoList.add(candidateDto);

//				}
			}

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
				Collections.sort(candidateDtoList, (s1, s2) -> {
					try {
						//						LocalDate date1 = LocalDate.parse(s1.getSubmittedOn() != null ? s1.getSubmittedOn() : s1.getCreatedOn(), formatter);
						//						LocalDate date2 = LocalDate.parse(s2.getSubmittedOn() != null ? s2.getSubmittedOn() : s2.getCreatedOn(), formatter);
						//						return date1.compareTo(date2);
						LocalDateTime dateTime1 = LocalDateTime.parse(
								s1.getSubmittedOn() != null ? s1.getSubmittedOn() : s1.getCreatedOn(), formatter);
						LocalDateTime dateTime2 = LocalDateTime.parse(
								s2.getSubmittedOn() != null ? s2.getSubmittedOn() : s2.getCreatedOn(), formatter);
						return dateTime2.compareTo(dateTime1);
					} catch (DateTimeParseException e) {
						log.error("Exception occured in getAllCandidateList method in CandidateServiceImpl-->", e);
						return 0; // Handle parsing error, e.g., consider them equal
					}
				});

				DashboardDto dashboardDtoObj = new DashboardDto(strFromDate, strToDate, null, null, null,
						dashboardDto.getUserId(), dashboardDto.getStatus(), candidateDtoList,
						dashboardDto.getPageNumber());
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
	public ServiceOutcome<Boolean> conventionalCandidateApplicationFormApproved(String candidateCode,
			MultipartFile criminalVerificationDocument, Long criminalVerificationColorId,
			MultipartFile globalDatabseCaseDetailsDocument, Long globalDatabseCaseDetailsColorId, String reportType) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		try {
			//			System.out.println("criminalVerificationDocument" + criminalVerificationDocument);
			Candidate candidate = candidateRepository.findByCandidateCode(candidateCode);
			User user = SecurityHelper.getCurrentUser();

			List<String> orgServices = serviceTypeConfigRepository.getServiceSourceMasterByOrgId(candidate.getOrganization().getOrganizationId());
			Long contentId = null;
			if (reportType.equals("INTERIMREPORT")) {
				candidate.setApprovalRequired(true);
			} else {
				candidate.setApprovalRequired(false);
			}
			candidateRepository.save(candidate);
//			ConventionalCandidateStatus candidateStatus = conventionalCandidateStatusRepository.findByCandidateCandidateCode(candidateCode);
			CandidateStatus digiCandidateStatus = candidateStatusRepository.findByCandidateCandidateCode(candidateCode);
			Boolean conventionalCandidate = candidate.getConventionalCandidate();
			System.out.println("conventionalCandidate : "+conventionalCandidate);
			if (reportType.equals("CONVENTIONALINTERIM")) {
				//			System.out.println("candidate.getConventionalCandidate() : "+candidate.getConventionalCandidate());
				if(candidate.getConventionalCandidate() == null || !candidate.getConventionalCandidate()) {					
					if(digiCandidateStatus != null) {
						ConventionalCandidateStatus conventionalCandidateStat = conventionalCandidateStatusRepository.findByCandidateCandidateCode(candidateCode);
						if(conventionalCandidateStat == null) {	
							ConventionalCandidateStatus conventionalCandidateStatus = new ConventionalCandidateStatus();

							conventionalCandidateStatus.setCandidate(digiCandidateStatus.getCandidate());
							conventionalCandidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("CONVENTIONALINTERIMREPORT"));
							conventionalCandidateStatus.setLastUpdatedOn(new Date());
							conventionalCandidateStatus.setLastUpdatedBy(user);
							conventionalCandidateStatus.setCreatedOn(new Date());
							conventionalCandidateStatus.setCreatedBy(user);
							conventionalCandidateStatusRepository.save(conventionalCandidateStatus);


							if (conventionalCandidateStatus.getCandidate().getOrganization().getCallBackUrl() != null)
								postStatusToOrganization(conventionalCandidateStatus.getCandidate().getCandidateCode());
							createConventionalCandidateStatusHistory(conventionalCandidateStatus, "NOTCANDIDATE");
						}
					}
					
					
//					}
				}
				else {
					ConventionalCandidateStatus candidateStatus = conventionalCandidateStatusRepository.findByCandidateCandidateCode(candidateCode);
					if (candidateStatus.getStatusMaster().getStatusCode().equals("CONVENTIONALINTERIMREPORT")) {
						ConventionalCandidateStatusHistory candidateStatusHistoryObj = conventionalCandidateStatusHistoryReposiory
								.findLastStatusHistorytRecord(candidate.getCandidateId());
//						log.info("LAST STATUS HISTORY IS ::{}",
//								candidateStatusHistoryObj.getStatusMaster().getStatusCode());
						candidateStatusHistoryObj.setCreatedOn(new Date());
						candidateStatusHistoryObj.setCandidateStatusChangeTimestamp(new Date());
						conventionalCandidateStatusHistoryReposiory.save(candidateStatusHistoryObj);

					} else {
						candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("CONVENTIONALINTERIMREPORT"));

						candidateStatus.setLastUpdatedOn(new Date());
						candidateStatus.setLastUpdatedBy(user);
						conventionalCandidateStatusRepository.save(candidateStatus);
						if (candidateStatus.getCandidate().getOrganization().getCallBackUrl() != null)
							postStatusToOrganization(candidateStatus.getCandidate().getCandidateCode());
						createConventionalCandidateStatusHistory(candidateStatus, "NOTCANDIDATE");
					}
				}
			} else {
				ConventionalCandidateStatus candidateStatus = conventionalCandidateStatusRepository.findByCandidateCandidateCode(candidateCode);
				candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("FINALREPORT"));

				candidateStatus.setLastUpdatedOn(new Date());
				candidateStatus.setLastUpdatedBy(user);
				conventionalCandidateStatusRepository.save(candidateStatus);
				if (candidateStatus.getCandidate().getOrganization().getCallBackUrl() != null)
					postStatusToOrganization(candidateStatus.getCandidate().getCandidateCode());
				createConventionalCandidateStatusHistory(candidateStatus, "NOTCANDIDATE");
			}

			//			candidateStatus.setLastUpdatedOn(new Date());
			//			candidateStatus.setLastUpdatedBy(user);
			//			candidateStatusRepository.save(candidateStatus);
			//			createCandidateStatusHistory(candidateStatus, "NOTCANDIDATE");
			svcSearchResult.setData(true);
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage("QC done successfully.");

			// 			if (reportType.equals("INTERIMREPORT")) {
			// //				CompletableFuture.runAsync(() -> {
			// //					reportService.generateDocument(candidateCode, "", ReportType.INTERIM);
			// //				});
			// 			} else {
			// 				CompletableFuture.runAsync(() -> {
			// 					reportService.generateDocument(candidateCode, "", ReportType.FINAL);
			// 				});			
			// 			}

		} catch (Exception ex) {
			log.error("Exception occured in conventionalCandidateApplicationFormApproved method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
			.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}


	@Override
	public ServiceOutcome<Boolean> conventionalCancelCandidate(String referenceNo) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		try {
			User user = SecurityHelper.getCurrentUser();
			ConventionalCandidateStatus result = null;
			if (StringUtils.isNotBlank(referenceNo)) {
				ConventionalCandidateStatus candidateStatus = conventionalCandidateStatusRepository
						.findByCandidateCandidateCode(referenceNo.trim());
				if (candidateStatus != null) {
					candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("CONVENTIONALPROCESSDECLINED"));
					candidateStatus.setLastUpdatedBy(user);
					candidateStatus.setLastUpdatedOn(new Date());
					result = conventionalCandidateStatusRepository.save(candidateStatus);
					if (candidateStatus.getCandidate().getOrganization().getCallBackUrl() != null)
						postStatusToOrganization(candidateStatus.getCandidate().getCandidateCode());
					createConventionalCandidateStatusHistory(result, "NOTCANDIDATE");
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

	@Override
	public ServiceOutcome<CandidateDetailsDto> conventionalUpdateCandidate(CandidateDetailsDto candidateDetails) {
		ServiceOutcome<CandidateDetailsDto> svcSearchResult = new ServiceOutcome<CandidateDetailsDto>();
		CandidateDetailsDto candidateDetailsDto = new CandidateDetailsDto();
		try {
			User user = SecurityHelper.getCurrentUser();
			Candidate result = null;
			ConventionalCandidateEmailStatus candidateEmailStatus = conventionalCandidateEmailStatusRepository
					.findByCandidateCandidateCode(candidateDetails.getCandidateCode());
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
					boolean sendMail = emailSentTask.sendEmail(candidateDetails.getCandidateCode(),
							candidate.getCandidateName(), candidate.getEmailId(), candidate.getCcEmailId());
					ConventionalCandidateStatus candidateStatus = conventionalCandidateStatusRepository
							.findByCandidateCandidateCode(candidateDetails.getCandidateCode());
					if (sendMail) {
						if (candidateEmailStatus == null) {
							candidateEmailStatus = new ConventionalCandidateEmailStatus();
							candidateEmailStatus.setCreatedBy(user);
							candidateEmailStatus.setCreatedOn(new Date());
							candidateEmailStatus.setCandidate(candidateStatus.getCandidate());
							candidateEmailStatus.setDateOfEmailInvite(new Date());
							conventionalCandidateEmailStatusRepository.save(candidateEmailStatus);

						}
						if (candidateStatus.getStatusMaster().getStatusCode().equalsIgnoreCase("CONVENTIONALINVALIDUPLOAD")) {
							candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("CONVENTIONALINVITATIONSENT"));
							conventionalCandidateStatusRepository.save(candidateStatus);
							if (candidateStatus.getCandidate().getOrganization().getCallBackUrl() != null)
								postStatusToOrganization(candidateStatus.getCandidate().getCandidateCode());
						}
						createConventionalCandidateStatusHistory(candidateStatus, "NOTCANDIDATE");
					}
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

	@Override
	public ConventionalCandidateVerificationState getConventionalCandidateVerificationStateByCandidateId(Long candidateId) {
		return conventionalCandidateVerificationStateRepository.findByCandidateCandidateId(candidateId);
	}
	
	@Override
	public ConventionalCandidateVerificationState addOrUpdateConventionalCandidateVerificationStateByCandidateId(Long candidateId,
			ConventionalCandidateVerificationState candidateVerificationState) {
		ConventionalCandidateVerificationState candidateVerificationState1 = conventionalCandidateVerificationStateRepository
				.findByCandidateCandidateId(candidateId);
		if (Objects.nonNull(candidateVerificationState1)) {
			candidateVerificationState
					.setCandidateVerificationStateId(candidateVerificationState1.getCandidateVerificationStateId());
		}
		return conventionalCandidateVerificationStateRepository.save(candidateVerificationState);
	}
	
	
	@Override
	public ServiceOutcome<DashboardDto> conventionalSearchAllCandidate(SearchAllCandidateDTO searchAllcandidate) {
		ServiceOutcome<DashboardDto> svcSearchResult = new ServiceOutcome<>();
		log.info("AgentName for Search All Candidate {}", searchAllcandidate.getAgentName());
		log.info("userSearchInput for Search All Candidate {}", searchAllcandidate.getUserSearchInput());
		log.info("ORGANISTATION ID:: {}", searchAllcandidate.getOrganisationId());
		List<CandidateDetailsDto> candidateDtoList = new ArrayList<CandidateDetailsDto>();
		List<Candidate> searchResult = null;

		Long userId = searchAllcandidate.getUserId();
		User findByUserId = userRepository.findByUserId(userId);
		Long orgId = searchAllcandidate.getOrganisationId();

		Date createdOnDate = findByUserId.getOrganization().getCreatedOn();

		log.info("CreatedOnDate {}", createdOnDate);

		LocalDate currentDate = LocalDate.now();

		LocalDateTime endOfDay = currentDate.atTime(LocalTime.MAX);
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
		String currentDateAndTime = endOfDay.format(formatter2);

		log.info("CURRENT DATE AND TIME: {}" + currentDateAndTime);

		if (searchAllcandidate.getRoleName().equalsIgnoreCase("agent")) {

			searchResult = candidateRepository.searchAllCandidateByAgent(userId,
					searchAllcandidate.getUserSearchInput(), createdOnDate, currentDateAndTime);

		} else {
			searchResult = candidateRepository.searchAllCandidateByAdmin(orgId, searchAllcandidate.getUserSearchInput(),
					createdOnDate, currentDateAndTime);
		}
		for (Candidate candidate : searchResult) {
			if(candidate.getConventionalCandidate() != null && candidate.getConventionalCandidate()) {
			CandidateDetailsDto candidateDto = this.modelMapper.map(candidate, CandidateDetailsDto.class);
			candidateDto.setCreatedOn(formatter.format(candidate.getCreatedOn()));
			candidateDto.setSubmittedOn(
					candidate.getSubmittedOn() != null ? formatter.format(candidate.getSubmittedOn()) : null);
			ConventionalCandidateEmailStatus candidateEmailStatus = conventionalCandidateEmailStatusRepository
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

			Long candidateId = candidate.getCandidateId();
			log.info("Candidate: {}", candidateId);

			ConventionalCandidateStatus candidateStatus = conventionalCandidateStatusRepository.findByCandidateCandidateCode(candidate.getCandidateCode());
//			candidateDto.setCandidateStatusName(candidateStatus.getStatusMaster().getStatusName());
			if (candidateStatus != null) {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				Object principal = authentication.getPrincipal();
				//	              log.info("principal:: {}",principal.toString());
				String username = "";
				username = ((UserDetails) principal).getUsername();
				User findByUserName = userRepository.findByUserName(username);


				List<String> serviceSourceMasterByOrgId = serviceTypeConfigRepository.getServiceSourceMasterByOrgId(findByUserName.getOrganization().getOrganizationId());

				boolean clientApproval = serviceSourceMasterByOrgId.stream()
						.anyMatch(serviceCode -> serviceCode.equals("CONVENTIONALCLIENTAPPROVAL"));

				if(clientApproval) {
					if(candidateStatus.getStatusMaster().getStatusName().equals("Conventional QC Pending")) {
						candidateDto.setCandidateStatusName("Pending Approval");
					}else if(candidateStatus.getStatusMaster().getStatusName().equals("Conventional Candidate Approve")) {
						candidateDto.setCandidateStatusName("Conventional QC Pending");
					}else {
						candidateDto.setCandidateStatusName(candidateStatus.getStatusMaster().getStatusName());
					}
				}
				else {
					candidateDto.setCandidateStatusName(candidateStatus.getStatusMaster().getStatusName());
				}
			}

			List<ContentDTO> contentDTOList = contentService.getContentListByCandidateId(candidate.getCandidateId());
//				System.out.println(contentDTOList + "--------contentdtolist-------");
			candidateDto.setContentDTOList(contentDTOList);

			// adding cadidate status related details for search functionalities
			ConventionalCandidateVerificationState updateVerificationStatus = conventionalCandidateVerificationStateRepository
					.findByCandidateCandidateId(candidate.getCandidateId());
			if (updateVerificationStatus != null) {
				candidateDto.setPreOfferVerificationColorCode(updateVerificationStatus.getPreApprovalColorCodeStatus());
				candidateDto.setInterimVerificationColorCode(updateVerificationStatus.getInterimColorCodeStatus());
				candidateDto.setFinalVerificationColorCode(updateVerificationStatus.getFinalColorCodeStatus());

				// adding report delivered dates
				candidateDto.setPreOfferReportDate(updateVerificationStatus.getPreApprovalTime() != null
						? formatter.format(Date.from(updateVerificationStatus.getPreApprovalTime().toInstant()))
						: null);
				candidateDto.setInterimReportDate(updateVerificationStatus.getInterimReportTime() != null
						? formatter.format(Date.from(updateVerificationStatus.getInterimReportTime().toInstant()))
						: null);
				candidateDto.setFinalReportDate(updateVerificationStatus.getFinalReportTime() != null
						? formatter.format(Date.from(updateVerificationStatus.getFinalReportTime().toInstant()))
						: null);
				candidateDto.setCaseInitiationDate(updateVerificationStatus.getCaseInitiationTime() != null
						? formatter.format(Date.from(updateVerificationStatus.getCaseInitiationTime().toInstant()))
						: null);
			}

			candidateDtoList.add(candidateDto);
//				log.info("candidateDateDTOLIST::::============== {}",candidateDtoList.toString());

		}
	}

		DashboardDto dashboardDtoObj = new DashboardDto(null, null, null, null, null, orgId, null, candidateDtoList, 0);
		if (!candidateDtoList.isEmpty()) {
			svcSearchResult.setData(dashboardDtoObj);
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage("Conventional Candidate list fetched successfully.");
		} else {
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("NO Candidate FOUND");
		}

		return svcSearchResult;
	}
	
	
	@Transactional
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
	public ServiceOutcome<?> clientApproval(String vendorCheckID) {
		ServiceOutcome<?> svcSearchResult = new ServiceOutcome<>();

		try {
			List<Long> vendorCheckIds;

			vendorCheckIds = Arrays.stream(vendorCheckID.split(","))
					.map(String::trim)
					.map(Long::parseLong)
					.collect(Collectors.toList());

			if (vendorCheckIds != null && !vendorCheckIds.isEmpty() && vendorCheckIds.size() > 1) {
				for (Long vendorCheckId : vendorCheckIds) {
//		            System.out.println("vendorCheckId : " + vendorCheckId);
		            VendorChecks byVendorcheckId = vendorCheckRepository.findByVendorcheckId(vendorCheckId);
//					System.out.println("byVendorcheckId :"+byVendorcheckId);
					ConventionalCandidateStatus byConventionalCandidateStatus = conventionalCandidateStatusRepository.findByCandidateCandidateId(byVendorcheckId.getCandidate().getCandidateId());
					if(byConventionalCandidateStatus.getStatusMaster().getStatusName().equals("Conventional QC Pending")) {
						byConventionalCandidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("CONVENTIONALCANDIDATEAPPROVE"));
//						System.out.println("byConventionalCandidateStatus : "+byConventionalCandidateStatus.getStatusMaster().getStatusMasterId());
						conventionalCandidateStatusRepository.save(byConventionalCandidateStatus);
						conventionalCandidateService.createConventionalCandidateStatusHistory(byConventionalCandidateStatus, "NOTCANDIDATE");
					}
					byVendorcheckId.setClientApproval(true);
					VendorChecks save = vendorCheckRepository.save(byVendorcheckId);
					if(save != null) {
						svcSearchResult.setMessage("Documents Approved");;
					}
		        }
			}
			else {
				if (vendorCheckIds != null && !vendorCheckIds.isEmpty()) {
					Long singleVendorCheckId = vendorCheckIds.get(0);
					VendorChecks byVendorcheckId = vendorCheckRepository.findByVendorcheckId(singleVendorCheckId);
					ConventionalCandidateStatus byConventionalCandidateStatus = conventionalCandidateStatusRepository.findByCandidateCandidateId(byVendorcheckId.getCandidate().getCandidateId());
					if(byConventionalCandidateStatus.getStatusMaster().getStatusName().equals("Conventional QC Pending")) {
						byConventionalCandidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("CONVENTIONALCANDIDATEAPPROVE"));
						conventionalCandidateStatusRepository.save(byConventionalCandidateStatus);
						conventionalCandidateService.createConventionalCandidateStatusHistory(byConventionalCandidateStatus, "NOTCANDIDATE");
					}
					byVendorcheckId.setClientApproval(true);
					VendorChecks save = vendorCheckRepository.save(byVendorcheckId);
					if(save != null) {
						svcSearchResult.setMessage("Document Approved");
					}
				}
				
			}

		} catch (Exception e) {
			log.info("clientApproval Exception : "+e);
		}

		return svcSearchResult;
	}




}
