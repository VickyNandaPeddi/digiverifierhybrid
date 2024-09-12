package com.aashdit.digiverifier.api.service;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aashdit.digiverifier.api.model.ApiCandidate;
import com.aashdit.digiverifier.common.ContentRepository;
import com.aashdit.digiverifier.common.dto.EPFOResponseDto;
import com.aashdit.digiverifier.common.enums.ContentCategory;
import com.aashdit.digiverifier.common.enums.ContentSubCategory;
import com.aashdit.digiverifier.common.enums.ContentType;
import com.aashdit.digiverifier.common.enums.ContentViewType;
import com.aashdit.digiverifier.common.model.Content;
import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.common.service.ContentService;
import com.aashdit.digiverifier.common.util.RandomString;
import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.admin.repository.UserRepository;
import com.aashdit.digiverifier.config.candidate.dto.CandidateInvitationSentDto;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.CandidateEmailStatus;
import com.aashdit.digiverifier.config.candidate.model.CandidateResumeUpload;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatusHistory;
import com.aashdit.digiverifier.config.candidate.repository.CandidateEmailStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateResumeUploadRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusHistoryRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateVerificationStateRepository;
import com.aashdit.digiverifier.config.candidate.repository.OrganisationScopeRepository;
import com.aashdit.digiverifier.config.candidate.repository.StatusMasterRepository;
import com.aashdit.digiverifier.config.candidate.service.CandidateService;
import com.aashdit.digiverifier.config.candidate.util.ExcelUtil;
import com.aashdit.digiverifier.config.superadmin.model.Organization;
import com.aashdit.digiverifier.config.superadmin.repository.OrganizationRepository;
import com.aashdit.digiverifier.email.dto.EmailProperties;
import com.aashdit.digiverifier.epfo.model.CandidateEPFOResponse;
import com.aashdit.digiverifier.epfo.repository.CandidateEPFOResponseRepository;
import com.aashdit.digiverifier.utils.CommonValidation;
import com.aashdit.digiverifier.utils.EmailRateLimiter;
import com.aashdit.digiverifier.utils.SecurityHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApiServiceImpl implements ApiService {

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private CandidateRepository candidateRepository;

	@Autowired
	private ContentRepository contentRepository;

	@Autowired
	private CommonValidation commonValidation;

	@Autowired
	private StatusMasterRepository statusMasterRepository;

	@Autowired
	private CandidateStatusRepository candidateStatusRepository;

	@Autowired
	private CandidateVerificationStateRepository candidateVerificationStateRepository;
	
	@Autowired
	private OrganisationScopeRepository organisationScopeRepository;

	@Autowired
	private CandidateStatusHistoryRepository candidateStatusHistoryRepository;

	@Autowired
	private CandidateService candidateService;

	@Autowired
	private UserRepository userRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private EmailRateLimiter emailRateLimiter;

	@Autowired
	@Lazy
	private CandidateEPFOResponseRepository candidateEPFOResponseRepository;

	@Autowired
	private CandidateResumeUploadRepository candidateResumeUploadRepository;

	@Autowired
	private ExcelUtil excelUtil;

	@Autowired
	private ContentService contentService;
	
	@Autowired
	private CandidateEmailStatusRepository candidateEmailStatusRepository;
	
	@Autowired
	@Lazy
	private EmailProperties emailProperties;

	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
	
	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);

	private MessageSource messageSource;

	@Transactional
	@Override
	public ServiceOutcome<List> saveCandidateInformation(List<ApiCandidate> reqCandidateList) {
		ServiceOutcome<List> svcSearchResult = new ServiceOutcome<List>();
		try {
			User user = SecurityHelper.getCurrentUser();
			Organization organization = organizationRepository.findById(user.getOrganization().getOrganizationId())
					.get();
			RandomString rd = null;
			List<Candidate> candidates = null;
			List<Candidate> candidateList = null;
			List<CandidateStatus> candidateStatusList = new ArrayList<CandidateStatus>();

			if (reqCandidateList != null) {
				candidates = setCandidateObj(reqCandidateList, organization.getNoYearsToBeVerified());
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
					candidate.setCreatedBy(user);
				}
				candidateList = candidateRepository.saveAllAndFlush(candidates);
				log.info("saved candidate {}", candidateList);
				uploadResume(reqCandidateList, candidateList);
			}
			if (candidateList != null && !candidateList.isEmpty()) {

//				candidateList.forEach(candidateOBJ -> candidateOBJ.setCandidateSampleId(result));
				candidateRepository.saveAllAndFlush(candidateList);
				for (Candidate candidate : candidateList) {
					CandidateStatus candidateStatus = new CandidateStatus();
					candidateStatus.setCandidate(candidate);
					candidateStatus.setCreatedBy(user);
					candidateStatus.setCreatedOn(new Date());
					if (candidate.getCcEmailId() != null && !candidate.getCcEmailId().isEmpty()) {
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
			}
//			else if(candidateList.isEmpty()) {
//				svcSearchResult.setData(null);
//				svcSearchResult.setOutcome(false);
//				svcSearchResult.setMessage(originalFilename+" - filename already exists OR the file content is invalid.");
//			}
			else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage(
						"File Could not be Uploaded- filename already exists OR the file content is invalid.");
				svcSearchResult.setStatus("Success");
			}
		} catch (Exception e) {
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Candidate Could not be Uploaded.");
			svcSearchResult.setStatus("Fail");
			log.error("Exception occured in saveCandidateInformation method in ApiServiceImpl-->" + e);
			throw new RuntimeException("fail to store csv/xls data: " + e.getMessage());
		}
		return svcSearchResult;
	}

	private void uploadResume(List<ApiCandidate> reqCandidateList, List<Candidate> candidateList) {
		try {
			for (ApiCandidate reqCandidate : reqCandidateList) {
				candidateList.stream().forEach(temp -> {
					if (temp.getApplicantId().equals(reqCandidate.getAppId())) {
						CandidateResumeUpload candidateResumeUpload = new CandidateResumeUpload();
						candidateResumeUpload.setCandidate(temp);
						candidateResumeUpload
								.setCandidateResume(DatatypeConverter.parseHexBinary(reqCandidate.getResume()));
						candidateResumeUpload.setCreatedOn(new Date());
						candidateResumeUploadRepository.save(candidateResumeUpload);
					}
				});
			}
		} catch (Exception e) {
			log.info("Upload Resume ::" + e.getMessage());
		}
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

	public List<Candidate> setCandidateObj(List<ApiCandidate> reqCandidateList, String yearsToBeVerified) {
		try {
			if (yearsToBeVerified == null)
				yearsToBeVerified = "7";
			ArrayList<Candidate> candidateList = new ArrayList<Candidate>();

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Object principal = authentication.getPrincipal();
			String username = "";
			username = ((UserDetails) principal).getUsername();
			User findByUserName = userRepository.findByUserName(username);
			log.info("ORgID::{}", findByUserName.getOrganization().getOrganizationName());
			log.info(principal.toString());

			List<String> getFilenameFromCandidatebasic = candidateRepository.getFilename();
			try {
				for (ApiCandidate reqCandidate : reqCandidateList) {
					Candidate candidate = new Candidate();
					candidate.setCandidateName(reqCandidate.getCandidateName());
					candidate.setContactNumber(reqCandidate.getMobNo());
					candidate.setEmailId(reqCandidate.getEmailId());
					candidate.setRecruiterName(reqCandidate.getRecruiterName());
					candidate.setShowvalidation(false);
					candidate.setExperienceInMonth(Float.valueOf(yearsToBeVerified));

					SecureRandom secureRnd = new SecureRandom();
					int n = 100000 + secureRnd.nextInt(900000);

					if (!reqCandidate.getAppId().equals("")) {
						candidate.setApplicantId(reqCandidate.getAppId());
					} else {
						candidate.setApplicantId(String.valueOf(n));
					}

					if (emailRateLimiter.tryAcquire(candidate.getEmailId())) {
						candidateList.add(candidate);
					} else {
						log.info("Rate limit exceeded for email: " + candidate.getEmailId());
					}
				}
			} catch (Exception e) {
				log.info("ExcelUtils:::" + e.getMessage());
			}

			return candidateList;
		} catch (Exception e) {
			throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
		}
	}

	@Override
	public ServiceOutcome<String> getContentByCandidateCode(String CandidateCode) {
		ServiceOutcome<String> svcSearchResult = new ServiceOutcome<String>();
		try {
			Candidate candidate = candidateRepository.findByCandidateCode(CandidateCode);
			CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(CandidateCode);
			String statusCode = candidateStatus.getStatusMaster().getStatusCode();
			if (statusCode.equalsIgnoreCase("INTERIMREPORT")) {
				Optional<Content> contentList = contentRepository
						.findByCandidateIdAndContentTypeAndContentCategoryAndContentSubCategory(
								candidate.getCandidateId(), ContentType.GENERATED, ContentCategory.OTHERS,
								ContentSubCategory.INTERIM);
				if (contentList.isPresent()) {
					Content contentListObj1 = contentList.get();
					Long contentid = contentListObj1.getContentId();
					String url = contentService.getContentById(contentid, ContentViewType.VIEW);
					svcSearchResult.setData(url);
					svcSearchResult.setOutcome(true);
					svcSearchResult.setStatus("Success");
					svcSearchResult.setMessage("Successfully url received");
				} else {
					svcSearchResult.setMessage("Content Id Not Found");
					svcSearchResult.setOutcome(false);
					svcSearchResult.setStatus("Fail");
					svcSearchResult.setData(null);
				}
			} else {
				svcSearchResult.setMessage("Qc pending report not generated.");
				svcSearchResult.setOutcome(false);
				svcSearchResult.setStatus("Fail");
				svcSearchResult.setData(null);
			}

		} catch (Exception e) {
			log.error("Exception occured in getContentByCandidateCode method in ApiServiceImpl-->", e);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult
					.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ResponseEntity<byte[]> downloadCandidateStatusTrackerReport(String candidateCode) {
		try {

			Candidate candidate = candidateRepository.findByCandidateCode(candidateCode);
			log.info("Downloading the candidate status tracker for ::{}", candidate.getCandidateName());

			List<Candidate> list = new ArrayList<>();
			list.add(candidate);
			return excelUtil.downloadCandidateStatusTrackerExcel(list);

		} catch (Exception e) {
			log.error("Exception occured in downloadCandidateStatusTrackerReport method in ReportServiceImpl-->", e);
		}
		return null;
	}

	@Override
	public ServiceOutcome<EPFOResponseDto> getEPFODataAPI(String candidateCode) {
		ServiceOutcome<EPFOResponseDto> outcome = new ServiceOutcome<>();
		try {
			Candidate candidate = candidateRepository.findByCandidateCode(candidateCode);
			List<CandidateEPFOResponse> uanList = candidateEPFOResponseRepository
					.findByCandidateId(candidate.getCandidateId());
			if(!uanList.isEmpty()) {
		        ObjectMapper objectMapper = new ObjectMapper();
				EPFOResponseDto responseDTO = objectMapper.readValue(uanList.get(0).getEPFOResponse(), EPFOResponseDto.class);
				
				outcome.setData(responseDTO);
				outcome.setOutcome(true);
				outcome.setStatus("Success");
				outcome.setMessage("Fetch Successful");
			} else {
				outcome.setData(null);
				outcome.setOutcome(false);
				outcome.setStatus("Failure");
				outcome.setMessage("Data Not Found.");
			}
			
		} catch (Exception e) {
			outcome.setData(null);
			outcome.setOutcome(false);	
			outcome.setStatus("Failure");
			outcome.setMessage("Something went wrong.");

			log.error("Exception occured in getEPFODataAPI method in ApiServiceImpl-->" + e);
		}
		
		return outcome;
	}
	
	@Override
	public ServiceOutcome<List> getInvitaionLink(List<ApiCandidate> reqCandidateList) {
		ServiceOutcome<List> svcSearchResult = new ServiceOutcome<List>();
		try {
			User user = SecurityHelper.getCurrentUser();
			Organization organization = organizationRepository.findById(user.getOrganization().getOrganizationId())
					.get();
			RandomString rd = null;
			List<Candidate> candidates = null;
			List<Candidate> candidateList = null;
			List<CandidateStatus> candidateStatusList = new ArrayList<CandidateStatus>();
 
			if (reqCandidateList != null) {
				candidates = setCandidateObj(reqCandidateList, organization.getNoYearsToBeVerified());
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
					candidate.setCreatedBy(user);
				}
				candidateList = candidateRepository.saveAllAndFlush(candidates);
				uploadResume(reqCandidateList, candidateList);
			}
			if (candidateList != null && !candidateList.isEmpty()) {
 
//				candidateList.forEach(candidateOBJ -> candidateOBJ.setCandidateSampleId(result));
				candidateRepository.saveAllAndFlush(candidateList);
				for (Candidate candidate : candidateList) {
					CandidateStatus candidateStatus = new CandidateStatus();
					candidateStatus.setCandidate(candidate);
					candidateStatus.setCreatedBy(user);
					candidateStatus.setCreatedOn(new Date());
					if (candidate.getCcEmailId() != null && !candidate.getCcEmailId().isEmpty()) {
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
						.map(x -> emailProperties.getDigiverifierForwardUrllink() + x.getCandidate().getCandidateCode()).collect(Collectors.toList());
				for (Candidate candidate : candidateList) {
					CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(candidate.getCandidateCode());
					candidateStatus.setCandidate(candidate);
					candidateStatus.setCreatedBy(user);
					candidateStatus.setCreatedOn(new Date());
					candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("INVITATIONSENT"));
					candidateStatus = candidateStatusRepository.save(candidateStatus);
					createCandidateStatusHistory(candidateStatus, "NOTCANDIDATE");
					CandidateEmailStatus candidateEmailStatus = new CandidateEmailStatus();
					candidateEmailStatus.setCreatedBy(user);
					candidateEmailStatus.setCreatedOn(new Date());
					candidateEmailStatus.setCandidate(candidateStatus.getCandidate());
					candidateEmailStatus.setDateOfEmailInvite(new Date());
					candidateEmailStatus.setCandidateStatus(candidateStatus);
					candidateEmailStatusRepository.save(candidateEmailStatus);
					if (candidateStatus.getCandidate().getOrganization().getCallBackUrl() != null) {
						candidateService.postStatusToOrganization(candidateStatus.getCandidate().getCandidateCode());
					}
 
				}
 
				svcSearchResult.setData(referenceList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("File Uploaded Successfully");
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage(
						"File Could not be Uploaded- filename already exists OR the file content is invalid.");
			}
		} catch (Exception e) {
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("File Could not be Uploaded.");
			log.error("Exception occured in saveCandidateInformation method in CandidateServiceImpl-->" + e);
			throw new RuntimeException("fail to store csv/xls data: " + e.getMessage());
		}
		return svcSearchResult;
	}
}
