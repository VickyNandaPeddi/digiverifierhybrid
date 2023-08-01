package com.aashdit.digiverifier.config.superadmin.service;

import static com.aashdit.digiverifier.digilocker.service.DigilockerServiceImpl.DIGIVERIFIER_DOC_BUCKET_NAME;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.aashdit.digiverifier.common.ContentRepository;
import com.aashdit.digiverifier.common.enums.ContentCategory;
import com.aashdit.digiverifier.common.enums.ContentSubCategory;
import com.aashdit.digiverifier.common.enums.ContentType;
import com.aashdit.digiverifier.common.enums.FileType;
import com.aashdit.digiverifier.common.model.Content;
import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.admin.dto.VendorUploadChecksDto;
import com.aashdit.digiverifier.config.admin.dto.vendorChecksDto;
import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.admin.model.VendorChecks;
import com.aashdit.digiverifier.config.admin.model.VendorUploadChecks;
import com.aashdit.digiverifier.config.admin.repository.ConventionalAttributesMasterRepository;
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
import com.aashdit.digiverifier.config.candidate.dto.EmploymentDetailsDto;
import com.aashdit.digiverifier.config.candidate.dto.EmploymentTenureVerificationDto;
import com.aashdit.digiverifier.config.candidate.dto.EmploymentVerificationDto;
import com.aashdit.digiverifier.config.candidate.dto.EpfoDataResDTO;
import com.aashdit.digiverifier.config.candidate.dto.ExecutiveSummaryDto;
import com.aashdit.digiverifier.config.candidate.dto.FinalReportDto;
import com.aashdit.digiverifier.config.candidate.dto.IDVerificationDTO;
import com.aashdit.digiverifier.config.candidate.dto.ITRDataDto;
import com.aashdit.digiverifier.config.candidate.dto.ItrEpfoHeaderDetails;
import com.aashdit.digiverifier.config.candidate.dto.PanCardVerificationDto;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.CandidateAddComments;
import com.aashdit.digiverifier.config.candidate.model.CandidateCafExperience;
import com.aashdit.digiverifier.config.candidate.model.CandidateCaseDetails;
import com.aashdit.digiverifier.config.candidate.model.CandidateEmailStatus;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.CandidateVerificationState;
import com.aashdit.digiverifier.config.candidate.repository.CandidateAddCommentRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCafAddressRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCafExperienceRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCaseDetailsRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateEmailStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateIdItemsRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusHistoryRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.OrganisationScopeRepository;
import com.aashdit.digiverifier.config.candidate.service.CandidateService;
import com.aashdit.digiverifier.config.superadmin.Enum.ExecutiveName;
import com.aashdit.digiverifier.config.superadmin.Enum.ReportType;
import com.aashdit.digiverifier.config.superadmin.Enum.SourceEnum;
import com.aashdit.digiverifier.config.superadmin.Enum.VerificationStatus;
import com.aashdit.digiverifier.config.superadmin.dto.CandidateDetailsForReport;
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
import com.aashdit.digiverifier.config.superadmin.repository.SourceRepository;
import com.aashdit.digiverifier.email.dto.Email;
import com.aashdit.digiverifier.email.dto.EmailProperties;
import com.aashdit.digiverifier.epfo.model.CandidateEPFOResponse;
import com.aashdit.digiverifier.epfo.model.EpfoData;
import com.aashdit.digiverifier.epfo.repository.CandidateEPFOResponseRepository;
import com.aashdit.digiverifier.epfo.repository.EpfoDataRepository;
import com.aashdit.digiverifier.itr.model.ITRData;
import com.aashdit.digiverifier.itr.repository.CanditateItrEpfoResponseRepository;
import com.aashdit.digiverifier.itr.repository.ITRDataRepository;
import com.aashdit.digiverifier.utils.ApplicationDateUtils;
import com.aashdit.digiverifier.utils.AwsUtils;
import com.aashdit.digiverifier.utils.CommonUtils;
import com.aashdit.digiverifier.utils.DateUtil;
import com.aashdit.digiverifier.utils.EmailSentTask;
import com.aashdit.digiverifier.utils.FileUtil;
import com.aashdit.digiverifier.utils.PdfUtil;
import com.aashdit.digiverifier.utils.SecurityHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);

	public static final String NO_DATA_FOUND = "NO DATA FOUND";
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

	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

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
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='INVITATIONEXPIRED') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS invitationExpiredcount,\n");
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
						"COUNT( DISTINCT CASE WHEN (statusMaster.status_code ='INVITATIONEXPIRED') AND candidatestatushistory.candidate_status_change_timestamp BETWEEN ?1 AND ?2 THEN candidatestatushistory.candidate_id END) AS invitationExpiredcount,\n");
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
			if (resultList != null && resultList.size() > 0) {
				List<ReportResponseDto> pwdvMprReportDtoList = new ArrayList<ReportResponseDto>();
				for (Object[] result : resultList) {
					ReportResponseDto reportResponseDto = new ReportResponseDto(Long.valueOf(String.valueOf(result[0])),
							String.valueOf(result[1]), Integer.valueOf(String.valueOf(result[2])), "NEWUPLOAD",
							Integer.valueOf(String.valueOf(result[3])), "REINVITE",
							Integer.valueOf(String.valueOf(result[4])), "FINALREPORT",
							Integer.valueOf(String.valueOf(result[5])), "PENDINGAPPROVAL",
							Integer.valueOf(String.valueOf(result[6])), "PENDINGNOW",
							Integer.valueOf(String.valueOf(result[7])), "PROCESSDECLINED",
							Integer.valueOf(String.valueOf(result[8])), "INVITATIONEXPIRED",
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
				Date startDate = format.parse(reportSearchDto.getFromDate() + " 00:00:00");
				Date endDate = format.parse(reportSearchDto.getToDate() + " 23:59:59");
				List<String> statusList = null;
				if (reportSearchDto.getStatusCode().equals("NEWUPLOAD")) {
					if (reportSearchDto.getAgentIds() != null && !reportSearchDto.getAgentIds().isEmpty()) {
						candidateStatusList = candidateStatusRepository
								.findAllByCreatedOnBetweenAndCandidateOrganizationOrganizationIdInAndCreatedByUserIdIn(
										startDate, endDate, reportSearchDto.getOrganizationIds(),
										reportSearchDto.getAgentIds());
					} else {
						candidateStatusList = candidateStatusRepository
								.findAllByCreatedOnBetweenAndCandidateOrganizationOrganizationIdIn(startDate, endDate,
										reportSearchDto.getOrganizationIds());
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
				}
				if (reportSearchDto.getStatusCode().equals("REINVITE")
						|| reportSearchDto.getStatusCode().equals("FINALREPORT")
						|| reportSearchDto.getStatusCode().equals("PENDINGAPPROVAL")
						|| reportSearchDto.getStatusCode().equals("PROCESSDECLINED")
						|| reportSearchDto.getStatusCode().equals("INVITATIONEXPIRED")
						|| reportSearchDto.getStatusCode().equals("PENDINGNOW")) {
					query = new StringBuilder();
					query.append(
							"select distinct cb.candidate_id ,um.user_first_name ,COALESCE(um.user_last_name,'') as lastname , ");
					query.append(
							"cb.candidate_name ,cb.contact_number ,cb.email_id,coalesce(cb.pan_number,'') as pannumber, ");
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
							|| reportSearchDto.getStatusCode().equals("PENDINGNOW")) {
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
					squery.setParameter(1, startDate);
					squery.setParameter(2, endDate);
					squery.setParameter(3, reportSearchDto.getOrganizationIds());
					if (reportSearchDto.getStatusCode().equals("PENDINGNOW")) {
						statusList = new ArrayList<>();
						Collections.addAll(statusList, "INVITATIONSENT", "ITR", "EPFO", "DIGILOCKER",
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
							candidateDto.setExperienceInMonth(Integer.valueOf(String.valueOf(result[11])));
							candidateDto.setCurrentStatusDate(String.valueOf(result[12]));
							candidateDto.setColorName(
									result[13] != null
											? colorRepository.findById(Long.valueOf(String.valueOf(result[13]))).get()
													.getColorName()
											: "NA");
							candidateDto.setStatusName(String.valueOf(result[14]));
							candidateDto.setNumberofexpiredCount(Integer.valueOf(String.valueOf(result[15])));
							candidateDto.setReinviteCount(Integer.valueOf(String.valueOf(result[16])));
							candidateDto.setStatusDate(String.valueOf(result[17]));
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
			List<CandidateDetailsForReport> sortedList = candidateDetailsDtoList.stream()
					.sorted((o1, o2) -> o1.getCandidateName().compareTo(o2.getCandidateName()))
					.collect(Collectors.toList());
			reportSearchDtoObj.setCandidateDetailsDto(sortedList);
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
				Collections.addAll(statusList, "PENDINGAPPROVAL", "FINALREPORT", "INTERIMREPORT");
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
		if (reportType == ReportType.PRE_OFFER) {
			report_status_id = 7;
		} else if (reportType == ReportType.FINAL) {
			report_status_id = 8;
		} else if (reportType == ReportType.INTERIM) {
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
		if (validateCandidateStatus(candidate.getCandidateId())) {
			if (generatePdfFlag) {
				System.out.println("enter if *******************************");
				List<VendorUploadChecksDto> vendordocDtoList = new ArrayList<VendorUploadChecksDto>();
				VendorUploadChecksDto vendorUploadChecksDto = null;

				// candidate Basic detail
				CandidateReportDTO candidateReportDTO = new CandidateReportDTO();

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
				candidateReportDTO.setDob(candidate.getDateOfBirth());
				candidateReportDTO.setContactNo(candidate.getContactNumber());
				candidateReportDTO.setEmailId(candidate.getEmailId());
				candidateReportDTO.setExperience(candidate.getIsFresher() ? "Fresher" : "Experience");
				candidateReportDTO.setReportType(reportType);
				Organization organization = candidate.getOrganization();
				candidateReportDTO.setOrganizationName(organization.getOrganizationName());
				candidateReportDTO.setProject(organization.getOrganizationName());
//				candidateReportDTO.setOrganizationLocation(organization.getOrganizationLocation());
				candidateReportDTO.setOrganizationLocation(organization.getBillingAddress());
//				candidateReportDTO.setOrganizationLogo(organization.getLogoUrl());
				try {
					// Create a temporary file to store the Blob data
					Path tempFile = Files.createTempFile("data", ".dat");

					// Write the Blob data to the temporary file
					Files.copy(new ByteArrayInputStream(organization.getOrganizationLogo()), tempFile,
							StandardCopyOption.REPLACE_EXISTING);

					// Create a URL for the temporary file
					URL url = tempFile.toUri().toURL();

					candidateReportDTO.setOrganizationLogo(url.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
				candidateReportDTO.setAccountName(candidate.getAccountName());
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
//									educationVerificationDTO.setSource(SourceEnum.CANDIDATE);
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
						System.out.println("inside identity *******************************");
						// verify from digilocker and itr
						List<IDVerificationDTO> idVerificationDTOList = new ArrayList<>();
						IDVerificationDTO aadhaarIdVerificationDTO = new IDVerificationDTO();
						aadhaarIdVerificationDTO.setName(candidate.getAadharName());
//						aadhaarIdVerificationDTO.setName(candidate.getCandidateName());
						aadhaarIdVerificationDTO.setIDtype(IDtype.AADHAAR.label);
//                      aadhaarIdVerificationDTO.setIdNo(candidate.getAadharNumber());
						String aadharNumber = candidate.getAadharNumber();
                        String maskedAadharNumber = "XXXX-XXXX-" + aadharNumber.substring(aadharNumber.length() - 4);
                        aadhaarIdVerificationDTO.setIdNo(maskedAadharNumber);
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

						List<CandidateEPFOResponse> uanList = candidateEPFOResponseRepository
								.findByCandidateId(candidate.getCandidateId());
						EpfoData epfoDtls = epfoDataRepository
								.findFirstByCandidateCandidateId(candidate.getCandidateId());

						for (CandidateEPFOResponse candidateEPFOResponse : uanList) {
							IDVerificationDTO uanIdVerificationDTO = new IDVerificationDTO();

							if (epfoDtls != null) {
								uanIdVerificationDTO.setName(epfoDtls.getName());
								itrEpfoHeaderDetails.setEpfoName1(epfoDtls.getName());
//								itrEpfoHeaderDetails.setUanNo1(candidateEPFOResponse.getUan());
								if (candidateEPFOResponse.getUan() != null) {
									itrEpfoHeaderDetails.setUanNo1(candidateEPFOResponse.getUan());
									candidateReportDTO.setUanVerified("Yes");
								} else {
									candidateReportDTO.setUanVerified("No");
								}
							}

							uanIdVerificationDTO.setIDtype(IDtype.UAN.label);
							uanIdVerificationDTO.setIdNo(candidateEPFOResponse.getUan());
							uanIdVerificationDTO.setSourceEnum(SourceEnum.EPFO);
							uanIdVerificationDTO.setVerificationStatus(VerificationStatus.GREEN);
							idVerificationDTOList.add(uanIdVerificationDTO);
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
									cal.add(Calendar.DATE, 1);
									dateWith1Days = cal.getTime();
									System.out.println(dateWith1Days + "doj");

								}
								if (candidateCafExperience.getInputDateOfExit() != null) {
									Date doe = candidateCafExperience.getInputDateOfExit();
									Calendar cal = Calendar.getInstance();
									cal.setTime(doe);
									cal.add(Calendar.DATE, 1);
									doee = cal.getTime();
									System.out.println(doee + "doe");
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
												: "");
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

							int totalYears = 0;
							int totalMonths = 0;

							for (EmploymentTenureVerificationDto data : employmentTenureDtoList) {
//								String[] parts = data.getOutput().split(" ");
								String[] parts = data.getInput().split(" ");
								int years = Integer.parseInt(parts[0]);
								int months = Integer.parseInt(parts[2]);

								totalYears += years;
								totalMonths += months;
							}

							int extraYears = totalMonths / 12;
							int remainingMonths = totalMonths % 12;

							totalYears += extraYears;

							candidateReportDTO.setTotalTenure(totalYears + " years, " + remainingMonths + " months");
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
								} else if (s.getVerificationStatus().equals(VerificationStatus.AMBER)
										|| s.getVerificationStatus().equals(VerificationStatus.MOONLIGHTING)) {
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
							candidateCafExperienceList.sort(
									Comparator.comparing(CandidateCafExperience::getInputDateOfJoining).reversed());
							candidateReportDTO.setInputExperienceList(candidateCafExperienceList);
							EPFODataDto epfoDataDto = new EPFODataDto();

							Optional<CandidateEPFOResponse> canditateItrEpfoResponseOptional = candidateEPFOResponseRepository
									.findByCandidateId(candidate.getCandidateId()).stream().findFirst();

							// added for service history
							List<CandidateCafExperienceDto> cafExperiences = new ArrayList<>();

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

									// added for service history

									epfoDatas.stream().forEach(temp -> {
										CandidateCafExperienceDto cafExperience = new CandidateCafExperienceDto();
										cafExperience.setCandidateEmployerName(temp.getCompany());
										cafExperience.setInputDateOfJoining(temp.getDoj());
										cafExperience.setInputDateOfExit(temp.getDoe());
										cafExperience.setServiceName("EPFO");
										cafExperiences.add(cafExperience);
									});

//									int i = 0;
//									for (EpfoDataResDTO experience : epfoDatas) {
//										String inputTenure = "";
//										String outputTenure = "";
//										Date idoj = new Date(experience.getDoj());
//										Date idoe = new Date();
//										if (experience.getDoe() != null
//												&& !experience.getDoe().equalsIgnoreCase("NOT_AVAILABLE")) {
//											idoe = new Date(experience.getDoe());
//										}
//
//										String gap = "0y 0m";
//
//										if (idoj == null) {
//											inputTenure = 0 + "y " + 0 + "m";
//										} else {
//											LocalDate inputdoj = idoj.toInstant().atZone(ZoneId.systemDefault())
//													.toLocalDate();
//											LocalDate inputdoe = idoe == null ? LocalDate.now()
//													: idoe.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//											Period inputdiff = Period.between(inputdoj, inputdoe);
//
//											int years = inputdiff.getYears();
//											int months = inputdiff.getMonths();
//
//											if (inputdiff.getDays() > 0) {
//												months += 1;
//											}
//											inputTenure = years + "y " + months + "m";
//										}
//
//										Date odoj = new Date(experience.getDoj());
//										Date odoe = new Date();
//										if (experience.getDoe() != null
//												&& !experience.getDoe().equalsIgnoreCase("NOT_AVAILABLE"))
//											odoe = new Date(experience.getDoe());
//
//										if (odoj == null) {
//											outputTenure = 0 + "y " + 0 + "m";
//										} else {
//											LocalDate outputdoj = odoj.toInstant().atZone(ZoneId.systemDefault())
//													.toLocalDate();
//											LocalDate outputdoe = odoe == null ? LocalDate.now()
//													: odoe.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//											Period outputdiff = Period.between(outputdoj, outputdoe);
//											int oyears = outputdiff.getYears();
//											int omonths = outputdiff.getMonths();
//											if (outputdiff.getDays() > 0) {
//												omonths += 1;
//											}
//											outputTenure = oyears + "y " + omonths + "m";
//
//											if (i < epfoDatas.size()) {
//												EpfoDataResDTO experience1 = epfoDatas.get(i);
//												if (i + 1 < epfoDatas.size() && experience1.getDoj() != null) {
//													EpfoDataResDTO experience2 = epfoDatas.get(i + 1);
//													LocalDate experience1Inputdoj = new Date(experience1.getDoj())
//															.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//													LocalDate experience2Outputdoe = new Date(
//															experience2.getDoe()) == null
//																	? LocalDate.now()
//																	: new Date(experience2.getDoe()).toInstant()
//																			.atZone(ZoneId.systemDefault())
//																			.toLocalDate();
//													Period gapPeriod = Period.between(experience2Outputdoe,
//															experience1Inputdoj);
//													int gapYears = gapPeriod.getYears();
//													int gapMonths = gapPeriod.getMonths();
//													if (gapPeriod.getDays() > 0) {
//														gapMonths += 1;
//													}
//													gap = gapYears + "y " + gapMonths + "m";
//												}
//												i++;
//											}
//										}
//
//										for (EmploymentTenureVerificationDto employementDetails : candidateReportDTO
//												.getEmploymentTenureVerificationDtoList()) {
//											if (employementDetails.getEmployerName()
//													.equalsIgnoreCase(experience.getCompany())) {
//												employementDetails.setInput(inputTenure);
//											}
//										}
//
//									}

								} catch (JsonProcessingException e) {
									e.printStackTrace();
								}
							}

							List<ITRData> itrDataList = itrDataRepository
									.findAllByCandidateCandidateCodeOrderByFiledDateDesc(candidateCode);
							ITRDataDto itrDataDto = new ITRDataDto();
							itrDataDto.setItrDataList(itrDataList);
							candidateReportDTO.setItrData(itrDataDto);

							// start
							HashMap<String, List<ITRData>> map = new HashMap<String, List<ITRData>>();
							itrDataList.forEach(itr -> {
								if (!map.containsKey(itr.getDeductor())) {
									List<ITRData> list = new ArrayList<>();
									itrDataList.forEach(itrIndivisual -> {
										if (itr.getDeductor().equalsIgnoreCase(itrIndivisual.getDeductor())) {
											list.add(itrIndivisual);
										}
									});
									map.put(itr.getDeductor(), list);
								}
							});
							log.info("All Employer Names As Per ITR {}", map.keySet());
							map.keySet().forEach(itrMapKey -> {
								if (!containsObj(candidateReportDTO.getEmployementDetailsDTOlist(), itrMapKey)) {
									List<CandidateCafExperienceDto> employementDetails = candidateReportDTO
											.getEmployementDetailsDTOlist();
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

										employementDetails.add(employementDetail);
										candidateReportDTO.setEmployementDetailsDTOlist(employementDetails);
									}
								}
							});

							candidateReportDTO.getEmployementDetailsDTOlist().stream().forEach(temp -> {
								if (temp.getServiceName().equals("ITR")) {
									CandidateCafExperienceDto cafExperience = new CandidateCafExperienceDto();
									cafExperience = temp;

									cafExperiences.add(cafExperience);
								}
							});

							candidateReportDTO.setServiceHistory(cafExperiences);

							// end

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
							addressVerificationDto.setVerificationStatus(
									VerificationStatus.valueOf(candidateCafAddressDto.getColorColorCode()));
							addressVerificationDto.setSource(SourceEnum.AADHAR);
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
						candidateReportDTO.setAddressVerificationDtoList(collect);
						// System.out.println("candidateReportDTO**************"+candidateReportDTO);

						for (AddressVerificationDto addressVerificationDto : candidateReportDTO
								.getAddressVerificationDtoList()) {
							// System.out.println("inside
							// for"+employmentVerificationDto+"emppppp"+candidateReportDTO);
							executiveSummaryDtos.add(new ExecutiveSummaryDto(ExecutiveName.EMPLOYMENT,
									addressVerificationDto.getInput(), addressVerificationDto.getVerificationStatus()));
						}
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
					System.out.println("switch end *******************************");

				});
				// System.out.println("before
				// pdf*******************************"+candidateReportDTO);

				List<Source> sourceList = sourceRepository.findByIsActiveTrue();
				if (sourceList.size() > 0) {
					sourceList.forEach(source -> {
						List<VendorChecks> vendorChecks = vendorChecksRepository
								.findByCandidateCandidateIdAndSourceSourceId(candidate.getCandidateId(),
										source.getSourceId());

						if (vendorChecks.size() > 0) {
							VendorChecks vendorCheck = vendorChecks.get(vendorChecks.size() - 1);
							log.info("vendor check agent attributes {}", vendorCheck.getAgentAttirbuteValue());
							Map<String, String> checkMap = new LinkedHashMap<>();

							VendorUploadChecks vendorUploadChecks = vendorUploadChecksRepository
									.findByVendorChecksVendorcheckId(vendorCheck.getVendorcheckId());

							if (vendorUploadChecks != null) {
								vendorCheck.getAgentAttirbuteValue()
										.addAll(vendorUploadChecks.getVendorAttirbuteValue());
							}

							vendorCheck.getAgentAttirbuteValue().forEach(temp -> {
								String[] arr = temp.split("=");
								checkMap.put(arr[0], arr[1]);
							});

							if (source.getSourceId() == Long.valueOf(3))
								candidateReportDTO.setGlobalDatabaseCheck(checkMap);
							if (source.getSourceId() == Long.valueOf(6))
								candidateReportDTO.setCriminalCheck(checkMap);
							if (source.getSourceId() == Long.valueOf(2))
								candidateReportDTO.setEducationCheck(checkMap);
						}
					});
				}

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
								vendorChecksss.getAgentColor().getColorHexCode(), null);
						vendordocDtoList.add(vendorUploadChecksDto);
					}
				}

				candidateReportDTO.setVendorProofDetails(vendordocDtoList);
				candidateReportDTO.setOrganisationScope(
						organisationScopeRepository.findByCandidateId(candidate.getCandidateId()));

				updateCandidateVerificationStatus(candidateReportDTO);
				System.out.println("after*****************update**************");
				Date createdOn = candidate.getCreatedOn();
				System.out.println("after *****************date**************");

				// System.out.println("candidate Report dto : " +candidateReportDTO);
				File report = FileUtil.createUniqueTempFile("report", ".pdf");
				String htmlStr = null;
				if (reportType.toString() == "FINAL" && candidateReportDTO.getProject().contains("Wipro")) {
					htmlStr = pdfService.parseThymeleafTemplate("wipro-final", candidateReportDTO);
				} else
					htmlStr = pdfService.parseThymeleafTemplate("common-pdf", candidateReportDTO);

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

					CandidateCaseDetails candidateCaseDetails = candidateCaseDetailsRepository
							.findByCandidateCandidateCode(candidateCode);
					CandidateFileDto candidateFileDto = null;
					File caseDetailsDocument = null;
					File globalDatabaseDocument = null;

					if (candidateCaseDetails != null) {
						if (candidateCaseDetails.getCriminalVerificationisExist() != null) {
							candidateFileDto = new CandidateFileDto(candidateCaseDetails.getCandidateCaseDetailsId(),
									candidateCaseDetails.getCriminalVerificationDocument(),
									candidateCaseDetails.getCriminalVerificationisExist().getColorName());
							try {
								caseDetailsDocument = FileUtil.createUniqueTempFile(
										candidateCode + "_issued_" + candidateFileDto.getId().toString(), ".pdf");
								FileUtils.writeByteArrayToFile(caseDetailsDocument, candidateFileDto.getDocument());
								log.info("case details document {}", caseDetailsDocument);
								collect.add(FileUtil.convertToInputStream(caseDetailsDocument));
							} catch (IOException e) {
								System.out.println(e.getMessage());
							}
						}
						if (candidateCaseDetails.getGlobalDatabaseCaseDetailsIsExist() != null) {
							candidateFileDto = new CandidateFileDto(candidateCaseDetails.getCandidateCaseDetailsId(),
									candidateCaseDetails.getGlobalDatabaseCaseDetailsDocument(),
									candidateCaseDetails.getGlobalDatabaseCaseDetailsIsExist().getColorName());
							try {
								globalDatabaseDocument = FileUtil.createUniqueTempFile(
										candidateCode + "_issued_" + candidateFileDto.getId().toString(), ".pdf");
								FileUtils.writeByteArrayToFile(globalDatabaseDocument, candidateFileDto.getDocument());
								log.info("global database document {}", globalDatabaseDocument);
								collect.add(FileUtil.convertToInputStream(globalDatabaseDocument));
							} catch (IOException e) {
								System.out.println(e.getMessage());
							}
						}
					}

					if (reportType.toString() == "FINAL" && candidateReportDTO.getProject().contains("Wipro")) {
						List<InputStream> onlyReport = new ArrayList<>();
						onlyReport.add(FileUtil.convertToInputStream(report));

						PdfUtil.mergePdfFiles(onlyReport, new FileOutputStream(mergedFile.getPath()));
					} else
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
					if (reportType.name() == "PRE_OFFER") {
						content.setContentSubCategory(ContentSubCategory.PRE_APPROVAL);
					} else if (reportType.name() == "INTERIM") {
						content.setContentSubCategory(ContentSubCategory.INTERIM);
					} else if (reportType.name() == "FINAL") {
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
					e.printStackTrace();
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
		if (reportType == ReportType.PRE_OFFER) {
			report_status_id = 7;
		} else if (reportType == ReportType.FINAL) {
			report_status_id = 8;
		} else if (reportType == ReportType.INTERIM) {
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

						List<CandidateEPFOResponse> uanList = candidateEPFOResponseRepository
								.findByCandidateId(candidate.getCandidateId());
						EpfoData epfoDtls = epfoDataRepository
								.findFirstByCandidateCandidateId(candidate.getCandidateId());

						for (CandidateEPFOResponse candidateEPFOResponse : uanList) {
							IDVerificationDTO uanIdVerificationDTO = new IDVerificationDTO();

							if (epfoDtls != null) {
								uanIdVerificationDTO.setName(epfoDtls.getName());
								itrEpfoHeaderDetails.setEpfoName1(epfoDtls.getName());
								if (candidateEPFOResponse.getUan() != null) {
									itrEpfoHeaderDetails.setUanNo1(candidateEPFOResponse.getUan());
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
									e.printStackTrace();
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
								vendorChecksss.getAgentColor().getColorHexCode(), null);
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
					if (reportType.name() == "PRE_OFFER") {
						content.setContentSubCategory(ContentSubCategory.PRE_APPROVAL);
					} else if (reportType.name() == "FINAL") {
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
					e.printStackTrace();
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
				Date nextInputDateOfJoining = candidateCafExperienceList.get(i - 1).getInputDateOfJoining();
				LocalDateTime ldt = LocalDateTime.ofInstant(nextInputDateOfJoining.toInstant(), ZoneId.systemDefault());
				LocalDateTime exitDate = ldt.minusDays(1);
				Date out = Date.from(exitDate.atZone(ZoneId.systemDefault()).toInstant());
				candidateCafExperience.setInputDateOfExit(out);
			}
		}
	}

	private void updateCandidateVerificationStatus(CandidateReportDTO candidateReportDTO) {
		System.out.println("entry private------------" + candidateReportDTO.getExecutiveSummaryList());
		List<VerificationStatus> collect = candidateReportDTO.getExecutiveSummaryList().stream()
				.map(ExecutiveSummaryDto::getVerificationStatus).collect(Collectors.toList());
		System.out.println("private----inisde--------" + collect);
		if (collect.contains(VerificationStatus.RED)) {
			System.out.println("entry if------------");
			candidateReportDTO.setVerificationStatus(VerificationStatus.RED);
		} else if (collect.contains(VerificationStatus.AMBER) || collect.contains(VerificationStatus.MOONLIGHTING)) {
			System.out.println("entry else------if------");
			candidateReportDTO.setVerificationStatus(VerificationStatus.AMBER);
		} else {
			System.out.println("entry------else------");
			candidateReportDTO.setVerificationStatus(VerificationStatus.GREEN);
		}
	}

	private void updateCandidateVerificationStatus(FinalReportDto candidateReportDTO) {
		System.out.println("entry private------------" + candidateReportDTO.getExecutiveSummaryList());
		List<VerificationStatus> collect = candidateReportDTO.getExecutiveSummaryList().stream()
				.map(ExecutiveSummaryDto::getVerificationStatus).collect(Collectors.toList());
		System.out.println("private----inisde--------" + collect);
		if (collect.contains(VerificationStatus.RED)) {
			System.out.println("entry if------------");
			candidateReportDTO.setVerificationStatus(VerificationStatus.RED);
		} else if (collect.contains(VerificationStatus.AMBER)) {
			System.out.println("entry else------if------");
			candidateReportDTO.setVerificationStatus(VerificationStatus.AMBER);
		} else {
			System.out.println("entry------else------");
			candidateReportDTO.setVerificationStatus(VerificationStatus.GREEN);
		}
	}

	private List<EmploymentTenureVerificationDto> validateAndCompareExperienceTenure(
			List<EmploymentVerificationDto> candidateCafExperienceList,
			List<CandidateCafExperience> candidateExperienceFromItrEpfo, ToleranceConfig toleranceConfig,
			List<CandidateCafExperience> candidateCafExperienceMainList) {

		return candidateCafExperienceList.stream().map(employmentVerificationDto -> {
			EmploymentTenureVerificationDto employmentTenureVerificationDto = new EmploymentTenureVerificationDto();

			List<CandidateCafExperience> tempcandidateCafExperience = candidateCafExperienceMainList.stream()
					.filter(temp -> temp.getCandidateEmployerName().equalsIgnoreCase(employmentVerificationDto.getInput())).collect(Collectors.toList());
			
			long inputDifference1 = Long.valueOf(0);
			if(tempcandidateCafExperience.size() > 0) {
				
				inputDifference1 = DateUtil.differenceInMonths(tempcandidateCafExperience.get(0).getInputDateOfJoining(),
						employmentVerificationDto.getDoe());
			} else {
				inputDifference1 = DateUtil.differenceInMonths(employmentVerificationDto.getDoj(),
						employmentVerificationDto.getDoe());
			}

			employmentTenureVerificationDto.setDoj(employmentVerificationDto.getDoj());
			employmentTenureVerificationDto
					.setInput(inputDifference1 / 12 + " Years, " + inputDifference1 % 12 + " months");
//			employmentTenureVerificationDto
//					.setOutput(inputDifference1 / 12 + " Years, " + inputDifference1 % 12 + " months");
			employmentTenureVerificationDto.setSource(employmentVerificationDto.getSource());
			employmentTenureVerificationDto.setOutput("Data Not Found");
			employmentTenureVerificationDto.setVerificationStatus(VerificationStatus.AMBER);
			Integer tenure = toleranceConfig.getTenure();
			employmentTenureVerificationDto.setEmployerName(employmentVerificationDto.getInput());

			// added
			employmentTenureVerificationDto.setCustomRemark(employmentVerificationDto.getCustomRemark());
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
				if(tempcandidateCafExperience.size() > 0) {
					
					inputDifference = DateUtil.differenceInMonths(tempcandidateCafExperience.get(0).getInputDateOfJoining(),
							employmentVerificationDto.getDoe());
				} else {
					inputDifference = DateUtil.differenceInMonths(employmentVerificationDto.getDoj(),
							employmentVerificationDto.getDoe()); 
				}
				employmentTenureVerificationDto
						.setInput(inputDifference / 12 + " Years, " + inputDifference % 12 + " months");

				long outputDifference = DateUtil.differenceInMonths(candidateCafExperience.getInputDateOfJoining(),
						candidateCafExperience.getInputDateOfExit());
				employmentTenureVerificationDto
						.setOutput(outputDifference / 12 + " Years, " + outputDifference % 12 + " months");

				if (Math.abs(inputDifference - outputDifference) <= tenure) {
					employmentTenureVerificationDto.setSource(employmentVerificationDto.getSource());
					employmentTenureVerificationDto.setVerificationStatus(VerificationStatus.GREEN);
				}

			}

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
//					employmentVerificationDto.setSource(SourceEnum.CANDIDATE);
					employmentVerificationDto.setSource(null);
					employmentVerificationDto.setCustomRemark(candidateCafExperience.getCustomRemark());
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
				if (similarity >= 0.75) {
					employmentVerificationDto.setVerificationStatus(VerificationStatus.GREEN);
					employmentVerificationDto.setOutput(employerName1);
					employmentVerificationDto.setSource(SourceEnum.valueOf(
							candidateExperienceFromItrEpfo.get(index).getServiceSourceMaster().getServiceCode()));
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
				|| candidateStatusEnums.contains(CandidateStatusEnum.DIGILOCKER));
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
				System.out.println(query);
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
}
