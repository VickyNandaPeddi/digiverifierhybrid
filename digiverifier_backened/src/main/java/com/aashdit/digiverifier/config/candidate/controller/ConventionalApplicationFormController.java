/**
 * 
 */
package com.aashdit.digiverifier.config.candidate.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatusHistory;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateStatusHistory;
import com.aashdit.digiverifier.config.candidate.model.StatusMaster;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.ConventionalCandidateStatusHistoryRepository;
import com.aashdit.digiverifier.config.candidate.service.CandidateService;
import com.aashdit.digiverifier.config.candidate.service.ConventionalCandidateService;
import com.aashdit.digiverifier.config.superadmin.model.ServiceSourceMaster;
import com.aashdit.digiverifier.config.superadmin.service.ServiceSource;
import com.aashdit.digiverifier.globalConfig.EnvironmentVal;
import com.aashdit.digiverifier.utils.EmailSentTask;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Nambi
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/allowAll/conventional")
public class ConventionalApplicationFormController {
	
	@Autowired
	private CandidateService candidateService;
	
	@Autowired
	private ServiceSource serviceSource;
	
	@Autowired
	private EnvironmentVal environmentVal;
	
	@Autowired
    private CandidateRepository candidateRepository;

	@Autowired
	private EmailSentTask emailSentTask;
	
	@Autowired
	private ConventionalCandidateService conventionalCandidateService;
	
	@Autowired
	private ConventionalCandidateStatusHistoryRepository conventionalCandidateStatusHistoryRepository;

	
	
	@Operation(summary = "Getting the access code from Digilocker site")
	@GetMapping(value = "/checkMail/{candidateCode}")
    public void redirect(@PathVariable String candidateCode,HttpServletResponse res){
		try {
			Candidate findByCandidateCode = candidateRepository.findByCandidateCode(candidateCode);
            log.info("candidateBelongto::: {}",findByCandidateCode.getOrganization().getOrganizationName());
		if(candidateCode!=null) {
			ServiceOutcome<ConventionalCandidateStatus> candidate = conventionalCandidateService.conventionalGetCandidateStatusByCandidateCode(candidateCode);
			System.out.println("candidateStatus : "+candidate.getData().getStatusMaster().getStatusCode());
			ServiceOutcome<List<String>> configCodes = candidateService.getServiceConfigCodes(candidateCode, null);
			log.info("ORGANIZATION ALLOWED THE SERVICES LIST IN EMAIL CLICK::{}",configCodes.getData());

			if(candidate.getData().getCandidate().getConventionalCandidate() != null && candidate.getData().getCandidate().getConventionalCandidate()) {
				if(candidate.getData().getStatusMaster().getStatusCode().equals("CONVENTIONALINVITATIONSENT") || candidate.getData().getStatusMaster().getStatusCode().equals("CONVENTIONALREINVITE")) {			
//				if(candidate.getData().getCandidate().getConventionalStatusId().equals(21) || candidate.getData().getCandidate().getConventionalStatusId().equals(22)) {				
//					String responseString = environmentVal.getConventionalCandidateForm()+candidateCode;
					if(findByCandidateCode.getIsLoaAccepted() != null && !findByCandidateCode.getIsLoaAccepted()) {
						String responseString = environmentVal.getConventionalLOA()+candidateCode;
						System.out.println("responseString : "+responseString);
						res.sendRedirect(responseString);
					}else {
						String responseString = environmentVal.getConventionalCandidateForm()+candidateCode;
						System.out.println("responseString : "+responseString);
						res.sendRedirect(responseString);
					}
				}else {
					res.sendRedirect(environmentVal.getStaticPage()+"SUBMITTED");		
				}
			}
		}else {
			log.error("CANDIDATE CODE NAHI--->Candidate code is either empty or null-->"+candidateCode);
		}
		
		}catch(Exception e) {
			log.error("Something went wrong in redirect method-->"+candidateCode,e);
		}
    }
	
	
	@Operation(summary = "Create Access Code URI for self .")
	@PostMapping(value = "/conventionalCreateAccessCodeUriForSelf" )
	public ResponseEntity<ServiceOutcome<String>> createAccessCodeUri(@RequestBody String candidateObj,HttpServletResponse res) throws JsonProcessingException, IOException {
		ServiceOutcome<String> outcome = new ServiceOutcome<>();
		String response ="";
		if(candidateObj!=null) {
			String candidateCode = new JSONObject(candidateObj).getString("candidateCode");
		//	emailSentTask.loa(candidateCode);
			ServiceOutcome<ConventionalCandidateStatus> candidateStatus = conventionalCandidateService.conventionalGetCandidateStatusByCandidateCode(candidateCode);
			System.out.println(candidateStatus.getData().getStatusMaster().getStatusCode()+"status");
			
			Candidate findByCandidateCode = candidateRepository.findByCandidateCode(candidateCode);
			 log.info("candidateBelongto::: {}",findByCandidateCode.getOrganization().getOrganizationName());
			ServiceOutcome<List<String>> configCodes = candidateService.getServiceConfigCodes(candidateCode, null);
			log.info("ORGANIZATION ALLOWED THE SERVICES LIST ::{}",configCodes.getData());
			
			if(candidateStatus.getData().getStatusMaster().getStatusCode().equals("CONVENTIONALPROCESSDECLINED")) {
				outcome.setData(null);
				outcome.setMessage("You have already declined the process.");
				outcome.setOutcome(false);
			}else {
				//below condition for the case where some organization does not need digi locker.
//				if(Boolean.TRUE.equals(configCodes.getOutcome()) && configCodes.getData().contains("DIGILOCKER")) {
//					response = environmentVal.getRedirectAngularToDigilocker()+candidateCode;
//				}else {
					response=environmentVal.getConventionalCandidateForm()+candidateCode;
//				}
				Candidate candidate = candidateService.setIsLoaAccepted(candidateCode).getData();
				// String response = createAccessCodeUriForSelf(candidateCode);
				 outcome.setData(response);
				log.info("REDIRECT URL AFTER LOA ACCEPT::{}",response);
				outcome.setOutcome(true);
			}
			
		}else {
			outcome.setData(null);
			outcome.setMessage("Candidate not found.");
			outcome.setOutcome(false);
		}
		return new ResponseEntity<ServiceOutcome<String>>(outcome, HttpStatus.OK);
		
    }
	

}
