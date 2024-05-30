package com.aashdit.digiverifier.config.superadmin.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.checkerframework.checker.units.qual.s;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.admin.repository.RoleRepository;
import com.aashdit.digiverifier.config.admin.repository.UserRepository;
import com.aashdit.digiverifier.config.candidate.dto.CandidateStatusCountDto;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatusHistory;
import com.aashdit.digiverifier.config.candidate.model.StatusMaster;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusHistoryRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.StatusMasterRepository;
import com.aashdit.digiverifier.config.superadmin.dto.DashboardDto;
import com.aashdit.digiverifier.config.superadmin.dto.OrganizationDto;
import com.aashdit.digiverifier.config.superadmin.dto.ServiceConfigdashboardDto;
import com.aashdit.digiverifier.config.superadmin.dto.SuperAdminDashboardDto;
import com.aashdit.digiverifier.config.superadmin.model.Organization;
import com.aashdit.digiverifier.config.superadmin.repository.OrganizationRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ServiceTypeConfigRepository;
import com.aashdit.digiverifier.utils.ApplicationDateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SuperAdminDashboardServiceImpl implements SuperAdminDashboardService,MessageSourceAware {
	
	private MessageSource messageSource;

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	@Autowired
	private CandidateStatusRepository candidateStatusRepository;
	
	@Autowired
	private CandidateStatusHistoryRepository candidateStatusHistoryRepository;
	
	@Autowired
	private StatusMasterRepository statusMasterRepository;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private OrganizationServiceImpl organizationServiceImpl;
	
	@Autowired
	private CandidateRepository candidateRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ServiceTypeConfigRepository serviceTypeConfigRepository;
	
	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	@Override
	public ServiceOutcome<SuperAdminDashboardDto> getPendingDetails(SuperAdminDashboardDto superAdminDashboardDto) {
		ServiceOutcome<SuperAdminDashboardDto> svcSearchResult = new ServiceOutcome<SuperAdminDashboardDto>();
		List<CandidateStatusCountDto> candidateStatusCountDtoList=new ArrayList<CandidateStatusCountDto>();
		String strToDate="";
		String strFromDate="";
		try {
			if(superAdminDashboardDto.getOrganizationId()!=null) {
				strToDate = superAdminDashboardDto.getToDate() != null?superAdminDashboardDto.getToDate():ApplicationDateUtils.getStringTodayAsDDMMYYYY();
				strFromDate = superAdminDashboardDto.getFromDate() != null?superAdminDashboardDto.getFromDate():ApplicationDateUtils.subtractNoOfDaysFromDateAsDDMMYYYY(new SimpleDateFormat("dd/MM/yyyy").parse(strToDate),7);
				Date startDate = format.parse(strFromDate+" 00:00:00");
				Date endDate = format.parse(strToDate+" 23:59:59");
				List<CandidateStatus> candidateStatusList = new ArrayList<CandidateStatus>();
				if(superAdminDashboardDto.getOrganizationId()!=0) {
					User orgAdmin=userRepository.findByOrganizationOrganizationIdAndRoleRoleIdAndIsActiveTrue(superAdminDashboardDto.getOrganizationId(),roleRepository.findByRoleName("Admin").getRoleId());
					if(orgAdmin!=null) {
						log.info("orgAdmin username::{}",orgAdmin.getUserName());
						startDate=orgAdmin.getCreatedOn();
						endDate=new Date();
						candidateStatusCountDtoList=getPendingDetailsCounts(superAdminDashboardDto.getOrganizationId(),startDate,endDate);
					}else {
						
						candidateStatusCountDtoList.add(0, new CandidateStatusCountDto("LOA Pending","INVITATIONSENT",0));
						candidateStatusCountDtoList.add(1, new CandidateStatusCountDto("ITR Pending","DIGILOCKER",0));
						candidateStatusCountDtoList.add(2, new CandidateStatusCountDto("EPFO Pending","ITR",0));
						candidateStatusCountDtoList.add(3, new CandidateStatusCountDto("PFR Pending","EPFO",0));
						//candidateStatusCountDtoList.add(4, new CandidateStatusCountDto("Relative Address Completed","RELATIVEADDRESS",0));
						candidateStatusCountDtoList.add(4, new CandidateStatusCountDto("Process Decline","PROCESSDECLINED",0));
						candidateStatusCountDtoList.add(5, new CandidateStatusCountDto("EPFO Skipped","EPFOSKIPPED",0));
						candidateStatusCountDtoList.add(6, new CandidateStatusCountDto("QC Pending","PENDINGAPPROVAL",0));
						
					}
//					candidateStatusList = candidateStatusRepository.findAllByOrganizationIdAndDateRange(superAdminDashboardDto.getOrganizationId(),startDate,endDate);
				}else {
					candidateStatusCountDtoList=getPendingDetailsCounts(superAdminDashboardDto.getOrganizationId(),startDate,endDate);
//					log.info("##########CHECKKKK##################");
//					candidateStatusList = candidateStatusRepository.findAllByDateRange(startDate,endDate);
				}
//				StatusMaster invStatusMaster = statusMasterRepository.findByStatusCode("INVITATIONSENT");
//				StatusMaster digiStatusMaster = statusMasterRepository.findByStatusCode("DIGILOCKER");
//				StatusMaster itrStatusMaster = statusMasterRepository.findByStatusCode("ITR");
//				StatusMaster epfoStatusMaster = statusMasterRepository.findByStatusCode("EPFO");
//				StatusMaster relStatusMaster = statusMasterRepository.findByStatusCode("RELATIVEADDRESS");
//				StatusMaster canStatusMaster = statusMasterRepository.findByStatusCode("PROCESSDECLINED");
//				
//				List<CandidateStatus> invitationSentList = candidateStatusList != null ?candidateStatusList.stream().filter(c -> c.getStatusMaster().getStatusCode().equals("INVITATIONSENT")).collect(Collectors.toList()) : null;
//				List<CandidateStatus> digiList = candidateStatusList != null ?candidateStatusList.stream().filter(c -> c.getStatusMaster().getStatusCode().equals("DIGILOCKER")).collect(Collectors.toList()) : null;
//				List<CandidateStatus> itrList = candidateStatusList != null ?candidateStatusList.stream().filter(c -> c.getStatusMaster().getStatusCode().equals("ITR")).collect(Collectors.toList()) : null;
//				List<CandidateStatus> epfoList = candidateStatusList != null ?candidateStatusList.stream().filter(c -> c.getStatusMaster().getStatusCode().equals("EPFO")).collect(Collectors.toList()) : null;
//				candidateStatusCountDtoList.add(new CandidateStatusCountDto(invStatusMaster.getStatusName().equalsIgnoreCase("Invitation Sent")?"LOA Pending":invStatusMaster.getStatusName()
//						,invStatusMaster.getStatusCode(),invitationSentList.size()));
//				candidateStatusCountDtoList.add(new CandidateStatusCountDto(digiStatusMaster.getStatusName().equalsIgnoreCase("Digilocker Completed")?"ITR Pending":digiStatusMaster.getStatusName()
//						,digiStatusMaster.getStatusCode(),digiList.size()));
//				candidateStatusCountDtoList.add(new CandidateStatusCountDto(itrStatusMaster.getStatusName().equalsIgnoreCase("ITR Completed")?"EPFO Pending":itrStatusMaster.getStatusName()
//						,itrStatusMaster.getStatusCode(),itrList.size()));
//				candidateStatusCountDtoList.add(new CandidateStatusCountDto(epfoStatusMaster.getStatusName().equalsIgnoreCase("EPFO Completed")?"Preoffer Pending":itrStatusMaster.getStatusName()
//						,epfoStatusMaster.getStatusCode(),epfoList.size()));
//				List<CandidateStatus> relList = candidateStatusList != null ?candidateStatusList.stream().filter(c -> c.getStatusMaster().getStatusCode().equals("RELATIVEADDRESS")).collect(Collectors.toList()) : null;
//				candidateStatusCountDtoList.add(new CandidateStatusCountDto(relStatusMaster.getStatusName(),relStatusMaster.getStatusCode(),relList.size()));
//				List<CandidateStatus> canList = candidateStatusList != null ?candidateStatusList.stream().filter(c -> c.getStatusMaster().getStatusCode().equals("PROCESSDECLINED")).collect(Collectors.toList()) : null;
//				candidateStatusCountDtoList.add(new CandidateStatusCountDto(canStatusMaster.getStatusName(),canStatusMaster.getStatusCode(),canList.size()));
//				List<CandidateStatus> epfoSkippedList = candidateStatusList != null ?candidateStatusList.stream().filter(c ->c.getCandidate().getIsUanSkipped() != null ? c.getStatusMaster().getStatusCode().equals("DIGILOCKER")&&c.getCandidate().getIsUanSkipped()||
//						c.getStatusMaster().getStatusCode().equals("ITR")&&c.getCandidate().getIsUanSkipped():false).collect(Collectors.toList()) : null;
//				candidateStatusCountDtoList.add(new CandidateStatusCountDto("EPFO Skipped","EPFOSKIPPED",epfoSkippedList.size()));
				
				SuperAdminDashboardDto superAdminDashboardDtoObj=new SuperAdminDashboardDto(strFromDate,strToDate,
						superAdminDashboardDto.getOrganizationId(),null,candidateStatusCountDtoList,null);
				svcSearchResult.setData(superAdminDashboardDtoObj);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));
			}else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("Please Specify Organization");
			}
		}
		catch(Exception ex)
		{
			log.error("Exception occured in getPendingDetails method in SuperAdminDashboardServiceImpl-->",ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public  ServiceOutcome<SuperAdminDashboardDto> getActivityDetails(SuperAdminDashboardDto superAdminDashboardDto) {
		ServiceOutcome<SuperAdminDashboardDto> svcSearchResult = new ServiceOutcome<SuperAdminDashboardDto>();
		List<CandidateStatusCountDto> candidateStatusCountDtoList=new ArrayList<CandidateStatusCountDto>();
		String strToDate="";
		String strFromDate="";
		try {
			if(superAdminDashboardDto.getOrganizationId()!=null) {
				List<String> orgServices =new ArrayList<>();
				if(superAdminDashboardDto.getOrganizationId()!=0) {
	                orgServices = serviceTypeConfigRepository.getServiceSourceMasterByOrgId(superAdminDashboardDto.getOrganizationId());
				}
				
				strToDate = superAdminDashboardDto.getToDate() != null?superAdminDashboardDto.getToDate():ApplicationDateUtils.getStringTodayAsDDMMYYYY();
				strFromDate = superAdminDashboardDto.getFromDate() != null?superAdminDashboardDto.getFromDate():ApplicationDateUtils.subtractNoOfDaysFromDateAsDDMMYYYY(new SimpleDateFormat("dd/MM/yyyy").parse(strToDate),7);
				Date startDate = format.parse(strFromDate+" 00:00:00");
				Date endDate = format.parse(strToDate+" 23:59:59");

//			 if(superAdminDashboardDto.getOrganizationId()==0) {
				StringBuilder query = new StringBuilder();
				query.append("select newupload,invalid,reinvites,interReport,finalReport,cancelled,invExpired,pendingNow\n");
				query.append("from \n");
				query.append("(select count(distinct tdcsh.candidate_id) as newupload from t_dgv_candidate_status_history tdcsh\n");
				query.append("join t_dgv_candidate_basic bas on bas.candidate_id = tdcsh.candidate_id \n");
				query.append("join t_dgv_organization_master org on bas.organization_id = org.organization_id \n");
				query.append("join t_dgv_status_master mas on mas.status_master_id = tdcsh.status_master_id \n");
				query.append("where mas.status_code IN ('NEWUPLOAD','INVALIDUPLOAD')\n");
				if(superAdminDashboardDto.getOrganizationId()!=0) {
					query.append("and org.organization_id =:orgId\n");
				}
				query.append("and tdcsh.candidate_status_change_timestamp between :startDate and :endDate) ne,\n");
				query.append("(select count(*) as invalid from t_dgv_candidate_status tdcsh\n");
				query.append("join t_dgv_candidate_basic bas on bas.candidate_id = tdcsh.candidate_id \n");
				query.append("join t_dgv_organization_master org on bas.organization_id = org.organization_id \n");
				query.append("join t_dgv_status_master mas on mas.status_master_id = tdcsh.status_master_id \n");
				if(orgServices!=null && orgServices.contains("EPFO") && orgServices.contains("DNHDB")
			 			&& !orgServices.contains("DIGILOCKER") && !orgServices.contains("ITR")) {
					query.append("where mas.status_code IN ('UANFETCHFAILED')\n");
				}else {
					query.append("where mas.status_code IN ('INVALIDUPLOAD')\n");
				}
				
				if(superAdminDashboardDto.getOrganizationId()!=0) {
					query.append("and org.organization_id =:orgId\n");
				}
				query.append("and tdcsh.last_updated_on between :startDate and :endDate) ie,\n");
				query.append("(select count(*) as reinvites from t_dgv_candidate_status tdcsh\n");
				query.append("join t_dgv_candidate_basic bas on bas.candidate_id = tdcsh.candidate_id \n");
				query.append("join t_dgv_organization_master org on bas.organization_id = org.organization_id \n");
				query.append("join t_dgv_status_master mas on mas.status_master_id = tdcsh.status_master_id \n");
				query.append("where mas.status_code ='REINVITE'\n");
				if(superAdminDashboardDto.getOrganizationId()!=0) {
					query.append("and org.organization_id =:orgId\n");
				}
				query.append("and tdcsh.last_updated_on between :startDate and :endDate) re,\n");
				query.append("(select count(*) as interReport from t_dgv_candidate_status tdcsh\n");
				query.append("join t_dgv_candidate_basic bas on bas.candidate_id = tdcsh.candidate_id \n");
				query.append("join t_dgv_organization_master org on bas.organization_id = org.organization_id \n");
				query.append("join t_dgv_status_master mas on mas.status_master_id = tdcsh.status_master_id \n");
//				query.append("where mas.status_code ='PENDINGAPPROVAL'\n");
				query.append("where mas.status_code ='INTERIMREPORT'\n");
				if(superAdminDashboardDto.getOrganizationId()!=0) {
					query.append("and org.organization_id =:orgId\n");
				}
				query.append("and tdcsh.last_updated_on between :startDate and :endDate) ir,\n");
				query.append("(select count(*) as finalReport from t_dgv_candidate_status tdcsh \n");
				query.append("join t_dgv_candidate_basic bas on bas.candidate_id = tdcsh.candidate_id \n");
				query.append("join t_dgv_organization_master org on bas.organization_id = org.organization_id \n");
				query.append("join t_dgv_status_master mas on mas.status_master_id = tdcsh.status_master_id \n");
				query.append("where mas.status_code ='FINALREPORT'\n");
				if(superAdminDashboardDto.getOrganizationId()!=0) {
					query.append("and org.organization_id =:orgId\n");
				}
				query.append("and tdcsh.last_updated_on between :startDate and :endDate) fr,\n");
				query.append("(select count(*) as cancelled from t_dgv_candidate_status tdcsh\n");
				query.append("join t_dgv_candidate_basic bas on bas.candidate_id = tdcsh.candidate_id \n");
				query.append("join t_dgv_organization_master org on bas.organization_id = org.organization_id \n");
				query.append("join t_dgv_status_master mas on mas.status_master_id = tdcsh.status_master_id \n");
//				query.append("where mas.status_code ='CANCELLED'\n");
				query.append("where mas.status_code ='PROCESSDECLINED'\n");
				if(superAdminDashboardDto.getOrganizationId()!=0) {
					query.append("and org.organization_id =:orgId\n");
				}
				query.append("and tdcsh.last_updated_on between :startDate and :endDate) can,\n");
				query.append("(select count(*) as invExpired from t_dgv_candidate_status tdcsh\n");
				query.append("join t_dgv_candidate_basic bas on bas.candidate_id = tdcsh.candidate_id \n");
				query.append("join t_dgv_organization_master org on bas.organization_id = org.organization_id \n");
				query.append("join t_dgv_status_master mas on mas.status_master_id = tdcsh.status_master_id \n");
				query.append("where mas.status_code ='INVITATIONEXPIRED'\n");
				if(superAdminDashboardDto.getOrganizationId()!=0) {
					query.append("and org.organization_id =:orgId\n");
				}
				query.append("and tdcsh.last_updated_on between :startDate and :endDate) inve,\n");
				query.append("(select count(*) as pendingNow from t_dgv_candidate_status tds\n");
				query.append("join t_dgv_candidate_basic bas on bas.candidate_id = tds.candidate_id \n");
				query.append("join t_dgv_organization_master org on bas.organization_id = org.organization_id \n");
				query.append("join t_dgv_status_master mas on mas.status_master_id = tds.status_master_id \n");
//				query.append("where mas.status_code in('INVITATIONSENT','ITR','EPFO','DIGILOCKER','RELATIVEADDRESS','PENDINGAPPROVAL')\n");
				query.append("where mas.status_code ='PENDINGAPPROVAL'\n");
				if(superAdminDashboardDto.getOrganizationId()!=0) {
					query.append("and org.organization_id =:orgId\n");
				}
				query.append("and tds.last_updated_on between :startDate and :endDate) pd\n");
				
				Query resultQuery = entityManager.createNativeQuery(query.toString());
				resultQuery.setParameter("startDate", startDate);
				resultQuery.setParameter("endDate", endDate);
				if(superAdminDashboardDto.getOrganizationId()!=0) {
					resultQuery.setParameter("orgId", superAdminDashboardDto.getOrganizationId());
				}
				
				
				
				List<Object[]> activityList = resultQuery.getResultList();
				for(Object[] activity : activityList) {
					candidateStatusCountDtoList.add(0, new CandidateStatusCountDto("New Upload","NEWUPLOAD",Integer.parseInt(activity[0].toString())));
					if(orgServices!=null && orgServices.contains("EPFO") && orgServices.contains("DNHDB")
				 			&& !orgServices.contains("DIGILOCKER") && !orgServices.contains("ITR")) {
						candidateStatusCountDtoList.add(1, new CandidateStatusCountDto("UAN Fetch Failed","UANFETCHFAILED",Integer.parseInt(activity[1].toString())));
					}else {
						candidateStatusCountDtoList.add(1, new CandidateStatusCountDto("Invalid Upload","INVALIDUPLOAD",Integer.parseInt(activity[1].toString())));
					}
					candidateStatusCountDtoList.add(2, new CandidateStatusCountDto("Re-Invites","REINVITE",Integer.parseInt(activity[2].toString())));
					candidateStatusCountDtoList.add(3, new CandidateStatusCountDto("Interim Report","PENDINGAPPROVAL",Integer.parseInt(activity[3].toString())));
					candidateStatusCountDtoList.add(4, new CandidateStatusCountDto("Final Report","FINALREPORT",Integer.parseInt(activity[4].toString())));
					candidateStatusCountDtoList.add(5, new CandidateStatusCountDto("Process Decline","CANCELLED",Integer.parseInt(activity[5].toString())));
					candidateStatusCountDtoList.add(6, new CandidateStatusCountDto("Invitations Expired","INVITATIONEXPIRED",Integer.parseInt(activity[6].toString())));
//					candidateStatusCountDtoList.add(7, new CandidateStatusCountDto("Pending Now","PENDINGNOW",Integer.parseInt(activity[7].toString())));
					if(orgServices!=null && orgServices.contains("EPFO") && orgServices.contains("DNHDB")
				 			&& !orgServices.contains("DIGILOCKER") && !orgServices.contains("ITR")) {
						candidateStatusCountDtoList.add(7, new CandidateStatusCountDto("QC Pending","PENDINGNOW",Integer.parseInt(activity[7].toString())));
					}else {
						candidateStatusCountDtoList.add(7, new CandidateStatusCountDto("CWF Completed","PENDINGNOW",Integer.parseInt(activity[7].toString())));
					}
				}
				
			//	List<CandidateStatus> candidateStatusList =new ArrayList<>();
				
//			 }else if(superAdminDashboardDto.getOrganizationId()!=0) {
//					//below commented lines are for new upload count (In case of any issue)
//					List<String> statusCodes = new ArrayList<>();
//					statusCodes.addAll(statusMasterRepository.findAll().parallelStream().map(x -> x.getStatusCode())
//							.collect(Collectors.toList()));
//					List<StatusMaster> statusMasterList = statusMasterRepository.findByStatusCodeIn(statusCodes);
//					List<Long> statusIds = statusMasterList.stream().map(x -> x.getStatusMasterId())
//							.collect(Collectors.toList());
//					List<Candidate> candidateList = candidateRepository.getCandidateListByOrganizationIdAndStatusAndCreatedOn(
//							superAdminDashboardDto.getOrganizationId(), statusIds, startDate, endDate);
//					log.info("NEW UPLOAD LIST ORGANIZATION WISE::{}",candidateList.size());
//					
//					List<CandidateStatus> candidateStatusList = candidateStatusRepository
//							.findAllByCandidateOrganizationOrganizationIdAndLastUpdatedOnBetween(
//									superAdminDashboardDto.getOrganizationId(), startDate, endDate);
//					log.info("candidateStatusList ORGANIZATION WISE::{}",candidateStatusList.size());
//					
//					//retriving all status list first for getting the size and counts
//					List<CandidateStatus> invitationexpiredList = candidateStatusList != null ? candidateStatusList.stream()
//							.filter(c -> c.getStatusMaster().getStatusCode().equals("INVITATIONEXPIRED"))
//							.collect(Collectors.toList()) : null;
//					List<CandidateStatus> invalidUploadList = candidateStatusList != null ? candidateStatusList.stream()
//							.filter(c -> c.getStatusMaster().getStatusCode().equals("INVALIDUPLOAD"))
//							.collect(Collectors.toList()) : null;
//					List<CandidateStatus> reinviteList = candidateStatusList != null ? candidateStatusList.stream()
//							.filter(c -> c.getStatusMaster().getStatusCode().equals("REINVITE"))
//							.collect(Collectors.toList()) : null;
////					List<CandidateStatus> pendingList = candidateStatusList != null ? candidateStatusList.stream()
////							.filter(c -> c.getStatusMaster().getStatusCode().equals("PENDINGAPPROVAL"))
////							.collect(Collectors.toList()) : null;
//					List<String> targetStatusCodes = Arrays.asList("INVITATIONSENT", "ITR", "EPFO","DIGILOCKER", "RELATIVEADDRESS", "PENDINGAPPROVAL");
//
//					List<CandidateStatus> pendingList = candidateStatusList != null ? candidateStatusList.stream()
//					        .filter(c -> targetStatusCodes.contains(c.getStatusMaster().getStatusCode()))
//					        .collect(Collectors.toList()) : null;
//					List<CandidateStatus> interimList = candidateStatusList != null ? candidateStatusList.stream()
//							.filter(c -> c.getStatusMaster().getStatusCode().equals("INTERIMREPORT"))
//							.collect(Collectors.toList()) : null;
//					List<CandidateStatus> processsdeclinedList = candidateStatusList != null ? candidateStatusList.stream()
//							.filter(c -> c.getStatusMaster().getStatusCode().equals("PROCESSDECLINED"))
//							.collect(Collectors.toList()) : null;
//					List<CandidateStatus> finalReportList = candidateStatusList != null ? candidateStatusList.stream()
//							.filter(c -> c.getStatusMaster().getStatusCode().equals("FINALREPORT"))
//							.collect(Collectors.toList()) : null;
//					
//					//addinng counts in response
//					candidateStatusCountDtoList.add(0, new CandidateStatusCountDto("New Upload","NEWUPLOAD",candidateList != null ? candidateList.size() : 0));
//					candidateStatusCountDtoList.add(1, new CandidateStatusCountDto("Invalid Upload","INVALIDUPLOAD",invalidUploadList != null ? invalidUploadList.size() : 0));
//					candidateStatusCountDtoList.add(2, new CandidateStatusCountDto("Re-Invites","REINVITE",reinviteList != null ? reinviteList.size() : 0));
//					candidateStatusCountDtoList.add(3, new CandidateStatusCountDto("Interim Report","PENDINGAPPROVAL",interimList != null ? interimList.size() : 0));
//					candidateStatusCountDtoList.add(4, new CandidateStatusCountDto("Final Report","FINALREPORT",finalReportList != null ? finalReportList.size() : 0));
//					candidateStatusCountDtoList.add(5, new CandidateStatusCountDto("Cancelled","CANCELLED",processsdeclinedList != null ? processsdeclinedList.size() : 0));
//					candidateStatusCountDtoList.add(6, new CandidateStatusCountDto("Invitations Expired","INVITATIONEXPIRED",invitationexpiredList != null ? invitationexpiredList.size() : 0));
//					candidateStatusCountDtoList.add(7, new CandidateStatusCountDto("Pending Now","PENDINGNOW",pendingList != null ? pendingList.size() : 0));
//					
//				}
				SuperAdminDashboardDto superAdminDashboardDtoObj=new SuperAdminDashboardDto(strFromDate,strToDate,
						superAdminDashboardDto.getOrganizationId(),null,candidateStatusCountDtoList,null);
				svcSearchResult.setData(superAdminDashboardDtoObj);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));
			}else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("Please Specify Organization");
			}
		}
		catch(Exception ex)
		{
			log.error("Exception occured in getActivityDetails method in SuperAdminDashboardServiceImpl-->",ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<SuperAdminDashboardDto> getUtilizationRatePerItem(SuperAdminDashboardDto superAdminDashboardDto) {
		ServiceOutcome<SuperAdminDashboardDto> svcSearchResult = new ServiceOutcome<SuperAdminDashboardDto>();
		List<ServiceConfigdashboardDto> configList=new ArrayList<ServiceConfigdashboardDto>();
		String strToDate="";
		String strFromDate="";
		try {
			if(superAdminDashboardDto.getSourceId()!=null) {
				strToDate = superAdminDashboardDto.getToDate() != null?superAdminDashboardDto.getToDate():ApplicationDateUtils.getStringTodayAsDDMMYYYY();
				strFromDate = superAdminDashboardDto.getFromDate() != null?superAdminDashboardDto.getFromDate():ApplicationDateUtils.subtractNoOfDaysFromDateAsDDMMYYYY(new SimpleDateFormat("dd/MM/yyyy").parse(strToDate),7);
				Date startDate = format.parse(strFromDate+" 00:00:00");
				Date endDate = format.parse(strToDate+" 23:59:59");
				StringBuilder query = new StringBuilder();
				query.append("select sm.organization_id,org.organization_name ,sum(sm.rate_per_item) as rateperitem \n");
				query.append("from t_dgv_service_master sm\n");
				query.append("join t_dgv_organization_master org on org.organization_id = sm.organization_id\n");
				if(superAdminDashboardDto.getSourceId()!=0) {
				query.append("join t_dgv_source src on src.source_id = sm.source_id\n");
				query.append("and sm.source_id =:sourceId \n");
				}
				//query.append("where org.is_active =true group by sm.organization_id,org.organization_name\n");
				query.append("where org.is_active =true ");
				query.append("and COALESCE(sm.last_updated_on ,sm.created_on ) between :startDate and :endDate ");
				query.append("group by sm.organization_id,org.organization_name ");
				Query result = entityManager.createNativeQuery(query.toString()); 
				if(superAdminDashboardDto.getSourceId()!=0) {
					 
					result.setParameter("sourceId", superAdminDashboardDto.getSourceId());
				}
				result.setParameter("startDate", startDate);
				result.setParameter("endDate", endDate);
				
				List<Object[]> configLists = result.getResultList();
				configList = configLists.stream().map(ServiceConfigdashboardDto::new).collect(Collectors.toList());
				SuperAdminDashboardDto superAdminDashboardDtoObj=new SuperAdminDashboardDto(strFromDate,strToDate,
						null,superAdminDashboardDto.getSourceId(),null,configList);
			svcSearchResult.setData(superAdminDashboardDtoObj);
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));
			}else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("Please Specify Source");
			}
		}
		catch(Exception ex)
		{
			log.error("Exception occured in getUtilizationRatePerItem method in SuperAdminDashboardServiceImpl-->",ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
		
	}

	@Override
	public ServiceOutcome<SuperAdminDashboardDto> getUtilizationRatePerReport(SuperAdminDashboardDto superAdminDashboardDto) {
		ServiceOutcome<SuperAdminDashboardDto> svcSearchResult = new ServiceOutcome<SuperAdminDashboardDto>();
		List<ServiceConfigdashboardDto> configList=new ArrayList<ServiceConfigdashboardDto>();
		String strToDate="";
		String strFromDate="";
		try {
			if(superAdminDashboardDto.getSourceId()!=null) {
				strToDate = superAdminDashboardDto.getToDate() != null?superAdminDashboardDto.getToDate():ApplicationDateUtils.getStringTodayAsDDMMYYYY();
				strFromDate = superAdminDashboardDto.getFromDate() != null?superAdminDashboardDto.getFromDate():ApplicationDateUtils.subtractNoOfDaysFromDateAsDDMMYYYY(new SimpleDateFormat("dd/MM/yyyy").parse(strToDate),7);
				Date startDate = format.parse(strFromDate+" 00:00:00");
				Date endDate = format.parse(strToDate+" 23:59:59");
				StringBuilder query = new StringBuilder();
				query.append("select sm.organization_id,org.organization_name ,sum(sm.rate_per_report) as rateperreport \n");
				query.append("from t_dgv_service_master sm\n");
				query.append("join t_dgv_organization_master org on org.organization_id = sm.organization_id\n");
				if(superAdminDashboardDto.getSourceId()!=0) {
				query.append("join t_dgv_source src on src.source_id = sm.source_id\n");
				query.append("and sm.source_id =:sourceId \n");
				}
				//query.append("where org.is_active =true group by sm.organization_id,org.organization_name\n");
				query.append("where org.is_active =true ");
				query.append("and COALESCE(sm.last_updated_on ,sm.created_on ) between :startDate and :endDate ");
				query.append("group by sm.organization_id,org.organization_name ");
				Query result = entityManager.createNativeQuery(query.toString()); 
				if(superAdminDashboardDto.getSourceId()!=0) {
					 
					result.setParameter("sourceId", superAdminDashboardDto.getSourceId());
				}
				result.setParameter("startDate", startDate);
				result.setParameter("endDate", endDate);
				List<Object[]> configLists = result.getResultList();
				configList = configLists.stream().map(ServiceConfigdashboardDto::new).collect(Collectors.toList());
				SuperAdminDashboardDto superAdminDashboardDtoObj=new SuperAdminDashboardDto(strFromDate,strToDate,
						null,superAdminDashboardDto.getSourceId(),null,configList);
			svcSearchResult.setData(superAdminDashboardDtoObj);
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));
		}else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("Please Specify Source");
			}
		}
		catch(Exception ex)
		{
			log.error("Exception occured in getUtilizationRatePerReport method in SuperAdminDashboardServiceImpl-->",ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<DashboardDto> getCompanyCountByActivity(DashboardDto dashboardDto) {
		ServiceOutcome<DashboardDto> svcSearchResult = new ServiceOutcome<DashboardDto>();
		List<CandidateStatusCountDto> candidateStatusCountDtoList=new ArrayList<CandidateStatusCountDto>();
		String strToDate="";
		String strFromDate="";
		List<Long> orgIds=new ArrayList<Long>();
		List<Long> ids=new ArrayList<Long>();
		List<String> activityCodes=new ArrayList<String>();
	
		try {
			ServiceOutcome<List<OrganizationDto>> svcoutcome=organizationServiceImpl.getOrganizationListAfterBilling();
			if(dashboardDto == null) {
				strToDate = ApplicationDateUtils.getStringTodayAsDDMMYYYY();
				strFromDate = ApplicationDateUtils.subtractNoOfDaysFromDateAsDDMMYYYY(new SimpleDateFormat("dd/MM/yyyy").parse(strToDate),7);
				orgIds= svcoutcome.getData().parallelStream().map(x -> x.getOrganizationId()).collect(Collectors.toList());
				activityCodes.add(0, "NEWUPLOAD");
				dashboardDto=new DashboardDto();
				dashboardDto.setActivityCode(activityCodes);
				ids.add(0, 0l);
				dashboardDto.setOrganizationIds(ids);
			}
			else {
				strToDate=dashboardDto.getToDate();
				strFromDate=dashboardDto.getFromDate();
				if(dashboardDto.getOrganizationIds()!=null && !dashboardDto.getOrganizationIds().isEmpty() ){
					if(dashboardDto.getOrganizationIds().get(0)!=0l) {
						orgIds.addAll(dashboardDto.getOrganizationIds());
					}
					else {
						orgIds.addAll(svcoutcome.getData().parallelStream().map(x -> x.getOrganizationId()).collect(Collectors.toList()));
					}
				}
				if(dashboardDto.getActivityCode()!=null && !dashboardDto.getActivityCode().isEmpty() ) {
					if(!dashboardDto.getActivityCode().get(0).equals("ALL")) {
						activityCodes.addAll(dashboardDto.getActivityCode());
					}else {
						activityCodes.addAll(statusMasterRepository.findAll().parallelStream().map(x->x.getStatusCode()).collect(Collectors.toList()));
					}
				}
			}
			Date startDate = format.parse(strFromDate+" 00:00:00");
			Date endDate = format.parse(strToDate+" 23:59:59");
			List<CandidateStatusHistory> candidateStatusList = new ArrayList<CandidateStatusHistory>();
			for (Long orgid : orgIds) {
				for (String statusCode : activityCodes) {
					List<String> statusList = new ArrayList<>();
					if(statusCode.equals("PENDINGAPPROVAL") || statusCode.equals("INTERIMREPORT")) {
						Collections.addAll(statusList, "PENDINGAPPROVAL");
					}else {
						Collections.addAll(statusList, statusCode);
					}
					candidateStatusList = candidateStatusHistoryRepository.findAllByOrganizationIdAndDateRangeAndStatusCode(orgid,startDate,endDate,statusList);
					if(candidateStatusList != null) {
						StatusMaster invStatusMaster = statusMasterRepository.findByStatusCode(statusCode);
						Organization organization=organizationRepository.findById(orgid).get();
						candidateStatusCountDtoList.add(new CandidateStatusCountDto(invStatusMaster.getStatusName(),invStatusMaster.getStatusCode(),candidateStatusList.size(),organization.getOrganizationId(),organization.getOrganizationName()));
					}
				}
				
			}
			List<CandidateStatusCountDto> sortedList = candidateStatusCountDtoList.parallelStream()
	                .sorted((o1,o2) -> o1.getOrganizationId().compareTo(o2.getOrganizationId()))
	                .collect(Collectors.toList());
			DashboardDto dashboardDtoObj=new DashboardDto(strFromDate,strToDate,
					dashboardDto.getOrganizationIds(),dashboardDto.getActivityCode(),sortedList,null,null,null,dashboardDto.getPageNumber());
			
			svcSearchResult.setData(dashboardDtoObj);
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage(messageSource.getMessage("msg.success", null, LocaleContextHolder.getLocale()));
		}
		catch(Exception ex)
		{
			log.error("Exception occured in getCompanyCountByActivity method in SuperAdminDashboardServiceImpl-->",ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage(messageSource.getMessage("ERROR.MESSAGE", null, LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}
	
	public List<CandidateStatusCountDto> getPendingDetailsCounts(Long organizationId,
			Date startDate,Date endDate) {
		List<CandidateStatusCountDto> candidateStatusCountDtoList=new ArrayList<>();
		try {
			StringBuilder query = new StringBuilder();
			query.append("SELECT loaPending, itrPending, epfoPending, preOfferPending, relAddress, processDecline, epfoSkipped, qcPending\n");
			query.append("FROM (\n");

			// LOA Pending
			query.append("(SELECT COUNT(*) AS loaPending FROM t_dgv_candidate_status tdcsh\n");
			query.append("JOIN t_dgv_candidate_basic bas ON bas.candidate_id = tdcsh.candidate_id\n");
			query.append("JOIN t_dgv_organization_master org ON bas.organization_id = org.organization_id\n");
			query.append("JOIN t_dgv_status_master mas ON mas.status_master_id = tdcsh.status_master_id\n");
			query.append("WHERE mas.status_code IN ('INVITATIONSENT','REINVITE')\n");
			if (organizationId != 0) {
			    query.append("AND org.organization_id = :orgId\n");
			    query.append("AND tdcsh.last_updated_on BETWEEN :startDate AND :endDate) ne,\n");
			} else {
			    query.append(") ne,\n");
			}

			// ITR Pending
			query.append("(SELECT COUNT(*) AS itrPending FROM t_dgv_candidate_status tdcsh\n");
			query.append("JOIN t_dgv_candidate_basic bas ON bas.candidate_id = tdcsh.candidate_id\n");
			query.append("JOIN t_dgv_organization_master org ON bas.organization_id = org.organization_id\n");
			query.append("JOIN t_dgv_status_master mas ON mas.status_master_id = tdcsh.status_master_id\n");
			query.append("WHERE mas.status_code IN ('DIGILOCKER')\n");
			if (organizationId != 0) {
			    query.append("AND org.organization_id = :orgId\n");
			    query.append("AND tdcsh.last_updated_on BETWEEN :startDate AND :endDate) ie,\n");
			} else {
			    query.append(") ie,\n");
			}

			// EPFO Pending
			query.append("(SELECT COUNT(*) AS epfoPending FROM t_dgv_candidate_status tdcsh\n");
			query.append("JOIN t_dgv_candidate_basic bas ON bas.candidate_id = tdcsh.candidate_id\n");
			query.append("JOIN t_dgv_organization_master org ON bas.organization_id = org.organization_id\n");
			query.append("JOIN t_dgv_status_master mas ON mas.status_master_id = tdcsh.status_master_id\n");
			query.append("WHERE mas.status_code = 'ITR'\n");
			if (organizationId != 0) {
			    query.append("AND org.organization_id = :orgId\n");
			    query.append("AND tdcsh.last_updated_on BETWEEN :startDate AND :endDate) re,\n");
			} else {
			    query.append(") re,\n");
			}

			// Pre-Offer Pending
			query.append("(SELECT COUNT(*) AS preOfferPending FROM t_dgv_candidate_status tdcsh\n");
			query.append("JOIN t_dgv_candidate_basic bas ON bas.candidate_id = tdcsh.candidate_id\n");
			query.append("JOIN t_dgv_organization_master org ON bas.organization_id = org.organization_id\n");
			query.append("JOIN t_dgv_status_master mas ON mas.status_master_id = tdcsh.status_master_id\n");
			query.append("WHERE mas.status_code = 'EPFO'\n");
			if (organizationId != 0) {
			    query.append("AND org.organization_id = :orgId\n");
			    query.append("AND tdcsh.last_updated_on BETWEEN :startDate AND :endDate) ir,\n");
			} else {
			    query.append(") ir,\n");
			}

			// Relative Address Pending
			query.append("(SELECT COUNT(*) AS relAddress FROM t_dgv_candidate_status tdcsh\n");
			query.append("JOIN t_dgv_candidate_basic bas ON bas.candidate_id = tdcsh.candidate_id\n");
			query.append("JOIN t_dgv_organization_master org ON bas.organization_id = org.organization_id\n");
			query.append("JOIN t_dgv_status_master mas ON mas.status_master_id = tdcsh.status_master_id\n");
			query.append("WHERE mas.status_code = 'RELATIVEADDRESS'\n");
			if (organizationId != 0) {
			    query.append("AND org.organization_id = :orgId\n");
			    query.append("AND tdcsh.last_updated_on BETWEEN :startDate AND :endDate) fr,\n");
			} else {
			    query.append(") fr,\n");
			}

			// Process Decline
			query.append("(SELECT COUNT(*) AS processDecline FROM t_dgv_candidate_status tdcsh\n");
			query.append("JOIN t_dgv_candidate_basic bas ON bas.candidate_id = tdcsh.candidate_id\n");
			query.append("JOIN t_dgv_organization_master org ON bas.organization_id = org.organization_id\n");
			query.append("JOIN t_dgv_status_master mas ON mas.status_master_id = tdcsh.status_master_id\n");
			query.append("WHERE mas.status_code = 'PROCESSDECLINED'\n");
			if (organizationId != 0) {
			    query.append("AND org.organization_id = :orgId\n");
			    query.append("AND tdcsh.last_updated_on BETWEEN :startDate AND :endDate) can,\n");
			} else {
			    query.append(") can,\n");
			}

			// EPFO Skipped
			query.append("(SELECT COUNT(distinct tdcsh.candidate_id) AS epfoSkipped FROM t_dgv_candidate_status tdcsh\n");
			query.append("JOIN t_dgv_candidate_basic bas ON bas.candidate_id = tdcsh.candidate_id\n");
			query.append("JOIN t_dgv_organization_master org ON bas.organization_id = org.organization_id\n");
			query.append("JOIN t_dgv_status_master mas ON mas.status_master_id = tdcsh.status_master_id\n");
			query.append("WHERE mas.status_code IN ('DIGILOCKER', 'ITR')\n");
			query.append("AND bas.is_uan_skipped = true\n");
			if (organizationId != 0) {
			    query.append("AND org.organization_id = :orgId\n");
			    query.append("AND tdcsh.last_updated_on BETWEEN :startDate AND :endDate) ep,\n");
			} else {
			    query.append(") ep,\n");
			}

			// QC Pending
			query.append("(SELECT COUNT(*) AS qcPending FROM t_dgv_candidate_status tdcsh\n");
			query.append("JOIN t_dgv_candidate_basic bas ON bas.candidate_id = tdcsh.candidate_id\n");
			query.append("JOIN t_dgv_organization_master org ON bas.organization_id = org.organization_id\n");
			query.append("JOIN t_dgv_status_master mas ON mas.status_master_id = tdcsh.status_master_id\n");
			query.append("WHERE mas.status_code = 'PENDINGAPPROVAL'\n");
			if (organizationId != 0) {
			    query.append("AND org.organization_id = :orgId\n");
			    query.append("AND org.is_active = true\n");
			    query.append("AND tdcsh.last_updated_on BETWEEN :startDate AND :endDate) qc\n");
			} else {
				query.append("AND org.is_active = true\n");
			    query.append(") qc\n");
			}

			query.append(")");


			
			Query resultQuery = entityManager.createNativeQuery(query.toString());
			
			if(organizationId!=0) {
				resultQuery.setParameter("orgId", organizationId);
				resultQuery.setParameter("startDate", startDate);
				resultQuery.setParameter("endDate", endDate);
			}
					
			List<Object[]> activityList = resultQuery.getResultList();
			for(Object[] activity : activityList) {
//				log.info("INVITATIONSENT ::{}",Integer.parseInt(activity[0].toString()));
//				log.info("DIGILOCKER::{}",Integer.parseInt(activity[1].toString()));
//				log.info("ITR::{}",Integer.parseInt(activity[2].toString()));
//				log.info("EPFO::{}",Integer.parseInt(activity[3].toString()));
//				log.info("RELATIVEADDRESS::{}",Integer.parseInt(activity[4].toString()));
//				log.info("PROCESSDECLINED::{}",Integer.parseInt(activity[5].toString()));
//				log.info("EPFOSKIPPED::{}",Integer.parseInt(activity[6].toString()));
//				log.info("PENDINGAPPROVAL::{}",Integer.parseInt(activity[7].toString()));
				candidateStatusCountDtoList.add(0, new CandidateStatusCountDto("LOA Pending","INVITATIONSENT",Integer.parseInt(activity[0].toString())));
				candidateStatusCountDtoList.add(1, new CandidateStatusCountDto("ITR Pending","DIGILOCKER",Integer.parseInt(activity[1].toString())));
				candidateStatusCountDtoList.add(2, new CandidateStatusCountDto("EPFO Pending","ITR",Integer.parseInt(activity[2].toString())));
				candidateStatusCountDtoList.add(3, new CandidateStatusCountDto("PFR Pending","EPFO",Integer.parseInt(activity[3].toString())));
				//candidateStatusCountDtoList.add(4, new CandidateStatusCountDto("Relative Address Completed","RELATIVEADDRESS",Integer.parseInt(activity[4].toString())));
				//candidateStatusCountDtoList.add(4, new CandidateStatusCountDto("Process Decline","PROCESSDECLINED",Integer.parseInt(activity[5].toString())));
				candidateStatusCountDtoList.add(4, new CandidateStatusCountDto("EPFO Skipped","EPFOSKIPPED",Integer.parseInt(activity[6].toString())));
				candidateStatusCountDtoList.add(5, new CandidateStatusCountDto("QC Pending","PENDINGAPPROVAL",Integer.parseInt(activity[7].toString())));
			}
			
		}catch(Exception e) {
			log.error("Error Occured in getPendingDetailsCounts::{}",e);		
		}
		return candidateStatusCountDtoList;
	}

}
