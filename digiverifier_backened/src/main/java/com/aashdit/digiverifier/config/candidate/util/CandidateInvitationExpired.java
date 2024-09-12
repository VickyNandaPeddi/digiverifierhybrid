package com.aashdit.digiverifier.config.candidate.util;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.admin.service.UserService;
import com.aashdit.digiverifier.config.candidate.dto.CandidateInvitationSentDto;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.StatusMaster;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.StatusMasterRepository;
import com.aashdit.digiverifier.config.candidate.service.CandidateService;
import com.aashdit.digiverifier.config.candidate.service.CandidateServiceImpl;
import com.aashdit.digiverifier.config.superadmin.model.Organization;
import com.aashdit.digiverifier.config.superadmin.repository.OrganizationRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ServiceTypeConfigRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CandidateInvitationExpired {
	
	@Autowired
	private CandidateService candidateService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CandidateServiceImpl candidateServiceimpl;
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private CandidateRepository candidateRepository;
	
	@Autowired
	private ServiceTypeConfigRepository serviceTypeConfigRepository;
	
	@Autowired
	private StatusMasterRepository statusMasterRepository;
	
	@Value("${com.dgv.candidatesCountsForRefetchBySchedular}")
	private Integer candidatesCountsForRefetchBySchedular;
	
	@Scheduled(cron="${com.dgv.candidateSchedularTime}")
	public void expireInvitationForCandidate() {
		List<String> collect=null;
		try {		
			log.info("candidate invitation expired Schedular Started Successfully At " + new Date());	
			List<CandidateStatus> candidateStatusList=candidateService.expireInvitationForCandidate();
			if(!candidateStatusList.isEmpty()) {
				collect = candidateStatusList.stream().map(c -> c.getCandidate().getCandidateCode()).collect(Collectors.toList());
				log.info("Invitation expired for Candidates" + collect);	
			}
		} catch (Exception e) {
		 	log.error("Exception occured in expireInvitationForCandidate method in CandidateInvitationExpired-->",e);
		}
	
	}
	
//	@Scheduled(cron="${com.dgv.candidateSchedularTime}")
//	public void processDeclined() {
//		List<String> collect=null;
//		try {		
//			log.info("candidate process declined Started Successfully At " + new Date());	
//			List<CandidateStatus> candidateStatusList=candidateService.processDeclined();
//			if(!candidateStatusList.isEmpty()) {
//				collect = candidateStatusList.stream().map(c -> c.getCandidate().getCandidateCode()).collect(Collectors.toList());
//				log.info("Invitation expired for Candidates" + collect);	
//			}
//		} catch (Exception e) {
//		 	log.error("Exception occured in processDeclined method in CandidateInvitationExpired-->",e);
//		}
//	
//	}
	
	@Scheduled(cron="${com.dgv.logoutSchedularTime}")
	public void logoutUser() {
		try {		
			userService.logoutUserAfter5Mins();
		} catch (Exception e) {
		 	log.error("Exception occured in logoutUser method in CandidateInvitationExpired-->",e);
		}
	
	}
	
	@Scheduled(cron="${com.dgv.candidatesPurgeTime}")
	public void oldCandidatesPurge() {
		try {
			log.info("oldCandidatesPurge SCHEDULAR STARTED ON ::::{}", new Date());
			ServiceOutcome<List<Long>> purgedCandidatesList = candidateServiceimpl.oldCandidatesPurgeBGProcess(null);
			log.info("No of purgedCandidatesList By Schedular::::{}",purgedCandidatesList.getData()!=null ? purgedCandidatesList.getData().size() : 0);
			log.info("oldCandidatesPurge SCHEDULAR COMPLETED ON ::::{}", new Date());
		} catch (Exception e) {
		 	log.error("Exception occured in oldCandidatesPurge method in CandidateInvitationExpired-->",e);
		}
	
	}
	
	@Scheduled(cron="${com.dgv.candidatesUanRefetchScheduleTime}")
	public void candidateUanRefetchChron() {
		try {
			log.info("REFETCH UAN SCHEDULAR STARTED ON ::::{}", new Date());
			long orgId= 0;
			List<Organization> organizations = organizationRepository.findByOrganizationName("CAPGEMINI TECHNOLOGY SERVICES INDIA LIMITED");
			for(Organization organization : organizations) {
				List<String> orgServices = serviceTypeConfigRepository.getServiceSourceMasterByOrgId(organization.getOrganizationId());
				if(orgServices!=null && orgServices.contains("EPFO") && orgServices.contains("DNHDB")
			 			&& !orgServices.contains("DIGILOCKER") && !orgServices.contains("ITR")) {
					orgId = organization.getOrganizationId();
				}
			}
			
			StatusMaster statusMaster = statusMasterRepository.findByStatusCode("UANFETCHFAILED");
			List<String> candidateCodes = candidateRepository.findCandidateCodesForUanRefetchForSchedular(orgId,statusMaster.getStatusMasterId());
			log.info("total candidateCodes for refetching UAN data By Schedular::::{}",candidateCodes!= null ? candidateCodes.size() : 0);
			if (candidateCodes!= null && candidatesCountsForRefetchBySchedular > candidateCodes.size()) {
				candidatesCountsForRefetchBySchedular = candidateCodes.size();
	        }
			
			if(candidateCodes!= null) {
				candidateCodes = candidateCodes.subList(0, candidatesCountsForRefetchBySchedular);
				log.info("Sublist of candidateCodes for refetching UAN data By Schedular::::{}",candidateCodes);
				
				CandidateInvitationSentDto candidateInvitationSentDto = new CandidateInvitationSentDto();
				candidateInvitationSentDto.setCandidateReferenceNo(candidateCodes);
				
				ServiceOutcome<Boolean> outcome = candidateService.reFetchUANData(candidateInvitationSentDto);
				if(outcome.getData() && outcome.getOutcome()) {
					log.info("REFETCH UAN SCHEDULAR END WITH SUCCESS RESPONSE ON ::::{}", new Date());
				}else {
					log.info("REFETCH UAN SCHEDULAR END WITH FAILED RESPONSE ON ::::{}", new Date());
				}
				
			}else {
				log.info("No Candidates found for refetching UAN data By Schedular ::::{}");
			}
			
			
		} catch (Exception e) {
		 	log.error("Exception occured in candidateUanRefetchChron method in CandidateInvitationExpired-->",e);
		}	
	}
		
}
