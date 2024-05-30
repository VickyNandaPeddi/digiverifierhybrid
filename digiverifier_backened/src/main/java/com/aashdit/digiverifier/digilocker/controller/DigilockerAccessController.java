package com.aashdit.digiverifier.digilocker.controller;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.DatatypeConverter;

import com.aashdit.digiverifier.globalConfig.EnvironmentVal;
import com.aashdit.digiverifier.utils.EmailSentTask;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aashdit.digiverifier.client.securityDetails.DigilockerClient;
import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatusHistory;
import com.aashdit.digiverifier.config.candidate.model.StatusMaster;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusHistoryRepository;
import com.aashdit.digiverifier.config.candidate.service.CandidateService;
import com.aashdit.digiverifier.config.superadmin.model.ServiceSourceMaster;
import com.aashdit.digiverifier.config.superadmin.service.ServiceSource;
import com.aashdit.digiverifier.constants.DigilockerConstants;
import com.aashdit.digiverifier.digilocker.service.DigilockerService;
import com.aashdit.digiverifier.digilocker.dto.DigiLockerDetailsDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.http.MediaType;

//import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/allowAll")
public class DigilockerAccessController {
	
	@Autowired
	private DigilockerClient clientSecurityDetails;
	
	@Autowired
	private DigilockerService digilockerService;
	
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
	private CandidateStatusHistoryRepository candidateStatusHistoryRepository;

	@Operation(summary = "Getting the access code from Digilocker site")
	@GetMapping(value = "/checkAgnetMail/{employeid}")
    public void redirectAgent(@PathVariable String employeid,HttpServletResponse res){
		try {
			if(employeid!=null) {
				res.sendRedirect(environmentVal.getAgentcreatepasswrd()+employeid);
				System.out.println("now i am in digicontroller");

			}
		// 	else {
		// 		log.error("CANDIDATE CODE NAHI--->Candidate code is either empty or null-->"+employeid);
		// }
		
		
		}catch(Exception e) {
			log.error("Something went wrong in redirect method-->"+employeid,e);
		}
    }
	
	@Operation(summary = "Getting the access code from Digilocker site")
	@GetMapping(value = "/checkMail/{candidateCode}")
    public void redirect(@PathVariable String candidateCode,HttpServletResponse res){
		try {
			Candidate findByCandidateCode = candidateRepository.findByCandidateCode(candidateCode);
            log.info("candidateBelongto::: {}",findByCandidateCode.getOrganization().getOrganizationName());
		if(candidateCode!=null) {
			ServiceOutcome<CandidateStatus> candidate = candidateService.getCandidateStatusByCandidateCode(candidateCode);
			ServiceOutcome<List<String>> configCodes = candidateService.getServiceConfigCodes(candidateCode, null);
			log.info("ORGANIZATION ALLOWED THE SERVICES LIST IN EMAIL CLICK::{}",configCodes.getData());
			if(candidate.getData().getStatusMaster().getStatusCode().equals("REINVITE")) {
				List<CandidateStatusHistory> candidateStatusHistories = candidateStatusHistoryRepository
						.findAllByCandidateCandidateId(candidate.getData().getCandidate().getCandidateId());

				List<CandidateStatusHistory> filteredList = candidateStatusHistories.stream()
		                .filter(history -> !history.getStatusMaster().getStatusCode().equals("INVITATIONEXPIRED"))
		                .filter(history -> !history.getStatusMaster().getStatusCode().equals("REINVITE"))
		                .collect(Collectors.toList());
				
				 CandidateStatusHistory lastElement = filteredList.get(filteredList.size() - 1);
				
//				Optional<CandidateStatusHistory> statusBeforeInvitationExpire = candidateStatusHistories.stream()
//		                .filter(history -> history.getStatusMaster().getStatusCode().equals("INVITATIONEXPIRED"))
//		                .reduce((first, second) -> second) // Get the last occurrence
//		                .flatMap(invitationExpire -> {
//		                    int index = candidateStatusHistories.indexOf(invitationExpire);
//		                    if (index > 0) {
//		                        return Optional.of(candidateStatusHistories.get(index - 1)); // Return the previous record if available
//		                    }
//		                    return Optional.empty(); // If the INVITATIONEXPIRE record is the first one
//		                });
				
//				StatusMaster lastStatus = statusBeforeInvitationExpire.get().getStatusMaster();
				 StatusMaster lastStatus = lastElement.getStatusMaster();
				candidate.getData().setStatusMaster(lastStatus);
				log.info("REINVITE CASE ==>  REDIRECT FROM LINK TO::{}",lastStatus.getStatusCode());
			}
			if(candidate.getOutcome()) {
//				boolean restartSwitch = false;
//			 do {
				switch (candidate.getData().getStatusMaster().getStatusCode()) {
				case "INVITATIONSENT": //case "REINVITE":
					if(!candidate.getData().getCandidate().getIsLoaAccepted()) {
						if(findByCandidateCode.getOrganization().getOrganizationName().equalsIgnoreCase("kpmg")) {
                            String kpmgpath = environmentVal.getKpmgCrossOrigins()+"/#/candidate/letterAccept/"+candidateCode;
                            res.sendRedirect(kpmgpath);
                        }
                        else {
                            res.sendRedirect(environmentVal.getLetterAuthPage()+candidateCode);    
                        }					
					}else {
						if(findByCandidateCode.getOrganization().getOrganizationName().equalsIgnoreCase("kpmg")) {
                            String kpmgpath = environmentVal.getKpmgCrossOrigins()+"/#/candidate/itrlogin/"+candidateCode;
                            res.sendRedirect(kpmgpath);
						}
						else {
							//below condition for the case where some organization does not need digi locker.
							if(configCodes.getOutcome() && configCodes.getData().contains("DIGILOCKER")) {
								String responseString = environmentVal.getRedirectAngularToDigilocker()+candidateCode;
								System.out.println(responseString+"==================================");
								res.sendRedirect(responseString);
							}else {
								res.sendRedirect(environmentVal.getITRLogin()+candidateCode);
							}
						}
						
					}
					
					break;
					
				case "DIGILOCKER":
					if(candidate.getData().getCandidate().getIsFresher()==null) {
						if(findByCandidateCode.getOrganization().getOrganizationName().equalsIgnoreCase("kpmg")) {
                            String kpmgpath = environmentVal.getKpmgCrossOrigins()+"/#/candidate/cType/"+candidateCode;
                            res.sendRedirect(kpmgpath);
                        }
                        else {
                        	res.sendRedirect(environmentVal.getIsFreshPage()+candidateCode);
    						log.info("Is fresher page url {}", environmentVal.getIsFreshPage());                        }
						
					}else {
						if(!candidate.getData().getCandidate().getIsFresher()) {							
							if(configCodes.getOutcome()) {
								if(configCodes.getData().contains("ITR") && candidate.getData().getStatusMaster().getStatusCode().equals("DIGILOCKER")) {
									ServiceOutcome<ServiceSourceMaster> ssm = serviceSource.getServiceSourceMasterByServiceCode("DIGILOCKER");
//									res.sendRedirect(ssm.getData().getServiceApi()+candidateCode);
									res.sendRedirect(environmentVal.getITRLogin()+candidateCode);
								}else if(configCodes.getData().contains("EPFO") && !candidate.getData().getCandidate().getIsUanSkipped() && (candidate.getData().getStatusMaster().getStatusCode().equals("DIGILOCKER") || candidate.getData().getStatusMaster().getStatusCode().equals("ITR"))) {
									ServiceOutcome<ServiceSourceMaster> ssm = serviceSource.getServiceSourceMasterByServiceCode("ITR");
//									res.sendRedirect(ssm.getData().getServiceApi()+candidateCode);
									res.sendRedirect(environmentVal.getEPFOLogin()+candidateCode);
								}else if(configCodes.getData().contains("RELBILLTRUE") && (candidate.getData().getStatusMaster().getStatusCode().equals("DIGILOCKER") || candidate.getData().getStatusMaster().getStatusCode().equals("ITR")|| candidate.getData().getStatusMaster().getStatusCode().equals("EPFO"))) {
									res.sendRedirect(environmentVal.getRelativeBillPage()+candidateCode);
								}else {
									res.sendRedirect(environmentVal.getCafPage()+candidateCode);
								}
							}
						}else if(candidate.getData().getCandidate().getIsFresher() && configCodes.getData().contains("RELBILLTRUE")) {
							res.sendRedirect(environmentVal.getRelativeBillPage()+candidateCode);
						}else if(candidate.getData().getCandidate().getIsFresher() && configCodes.getData().contains("RELBILLFALSE")){
							res.sendRedirect(environmentVal.getCafPage()+candidateCode);
						}
					}
					
					
					break;
				case "ITR": 
					if(configCodes.getOutcome()) {
						if(configCodes.getData().contains("EPFO") && candidate.getData().getCandidate().getIsUanSkipped()==null) {
							if(findByCandidateCode.getOrganization().getOrganizationName().equalsIgnoreCase("kpmg")) {
								 String kpmgpath = environmentVal.getKpmgCrossOrigins()+"/#/candidate/cUanConfirm/"+candidateCode+"/1";
		                            res.sendRedirect(kpmgpath);
							}
							else {
								res.sendRedirect(environmentVal.getUanConfirmPage()+candidateCode+"/1");	
							}
						}else if(configCodes.getData().contains("EPFO") && !candidate.getData().getCandidate().getIsUanSkipped()) {
							ServiceOutcome<ServiceSourceMaster> ssm = serviceSource.getServiceSourceMasterByServiceCode("ITR");
//							res.sendRedirect(ssm.getData().getServiceApi()+candidateCode);
							if(findByCandidateCode.getOrganization().getOrganizationName().equalsIgnoreCase("kpmg")) {
								 String kpmgpath = environmentVal.getKpmgCrossOrigins()+"/#/candidate/epfologin/"+candidateCode;
		                            res.sendRedirect(kpmgpath);
							}
							else {
								res.sendRedirect(environmentVal.getEPFOLogin()+candidateCode);
							}
						}else if(configCodes.getData().contains("EPFO") && candidate.getData().getCandidate().getIsUanSkipped()) {
							//res.sendRedirect(environmentVal.getUanConfirmPage()+candidateCode+"/2");
							if(configCodes.getData().contains("RELBILLTRUE")) {
								res.sendRedirect(environmentVal.getRelativeBillPage()+candidateCode);
							}else if(findByCandidateCode.getShowvalidation()) {
								res.sendRedirect(environmentVal.getShowValidation()+candidateCode);
							}else {
								res.sendRedirect(environmentVal.getCafPage()+candidateCode);
							}
						}else {
							res.sendRedirect(environmentVal.getCafPage()+candidateCode);
						}
					}
					break;
				case "EPFO":
					
					if(configCodes.getOutcome()) {
						if(configCodes.getData().contains("RELBILLTRUE")) {
							res.sendRedirect(environmentVal.getRelativeBillPage()+candidateCode);
						}else if(findByCandidateCode.getShowvalidation()) {
							res.sendRedirect(environmentVal.getShowValidation()+candidateCode);
						}else {
							res.sendRedirect(environmentVal.getCafPage()+candidateCode);
						}
					}
					break;
				case "INVITATIONEXPIRED": case "PROCESSDECLINED":
					res.sendRedirect(environmentVal.getStaticPage()+candidate.getData().getStatusMaster().getStatusCode());
					break;
				case "PENDINGAPPROVAL": case "FINALREPORT": case "INTERIMREPORT":
					if(findByCandidateCode.getOrganization().getOrganizationName().equalsIgnoreCase("kpmg")) {
						 String kpmgpath = environmentVal.getKpmgCrossOrigins()+"/#/candidate/cStatusMessage/"+"SUBMITTED";
                         res.sendRedirect(kpmgpath);
					}
					else {
						res.sendRedirect(environmentVal.getStaticPage()+"SUBMITTED");
					}			
					break;
				case "CANCELLED":
					res.sendRedirect(environmentVal.getCafPage()+"CANCELLED");
					break;
				case "RELATIVEADDRESS":
					res.sendRedirect(environmentVal.getCafPage()+candidateCode);
					break;
//				case "REINVITE":
//					if(Boolean.FALSE.equals(candidate.getData().getCandidate().getIsLoaAccepted())) {
//						if(findByCandidateCode.getOrganization().getOrganizationName().equalsIgnoreCase("kpmg")) {
//                            String kpmgpath = environmentVal.getKpmgCrossOrigins()+"/#/candidate/letterAccept/"+candidateCode;
//                            res.sendRedirect(kpmgpath);
//                        }
//                        else {
//                            res.sendRedirect(environmentVal.getLetterAuthPage()+candidateCode);    
//                        }					
//					}else {
//						
//						List<CandidateStatusHistory> candidateStatusHistories = candidateStatusHistoryRepository
//								.findAllByCandidateCandidateId(candidate.getData().getCandidate().getCandidateId());
//						
//						Optional<CandidateStatusHistory> statusBeforeInvitationExpire = candidateStatusHistories.stream()
//				                .filter(history -> history.getStatusMaster().getStatusCode().equals("INVITATIONEXPIRED"))
//				                .reduce((first, second) -> second) // Get the last occurrence
//				                .flatMap(invitationExpire -> {
//				                    int index = candidateStatusHistories.indexOf(invitationExpire);
//				                    if (index > 0) {
//				                        return Optional.of(candidateStatusHistories.get(index - 1)); // Return the previous record if available
//				                    }
//				                    return Optional.empty(); // If the INVITATIONEXPIRE record is the first one
//				                });
//						
//						StatusMaster lastStatus = statusBeforeInvitationExpire.get().getStatusMaster();
//						candidate.getData().setStatusMaster(lastStatus);
//						log.info("REINVITE CASE ==>  REDIRECT FROM LINK TO::{}",lastStatus.getStatusCode());
//						restartSwitch = true;
//			            break;
//					}	
				}
//			 } while (restartSwitch);
			}else{
				log.error("CANDIDATE NIKHOJA--->Candidate not found-->"+candidateCode);
			}
		}else {
			log.error("CANDIDATE CODE NAHI--->Candidate code is either empty or null-->"+candidateCode);
		}
		
		}catch(Exception e) {
			log.error("Something went wrong in redirect method-->"+candidateCode,e);
		}
    }
	
	@Operation(summary = "Decline in letter of authorization")
	@PostMapping(value = "/declineAuthLetter")
    public ResponseEntity<ServiceOutcome<Boolean>> declineAuthLetter(@RequestBody String candidateObj){
		ServiceOutcome<Boolean> outcome=new ServiceOutcome<Boolean>();
		if(candidateObj!=null) {
			String candidateCode = new JSONObject(candidateObj).getString("candidateCode");
			outcome = candidateService.declineAuthLetter(candidateCode);
		}
		
		return new ResponseEntity<ServiceOutcome<Boolean>>(outcome, HttpStatus.OK);
    }
	
	
	/**
	 * createAccessCodeUri
	 * @param state
	 * @return
	 */
	private String createAccessCodeUriForSelf(String state) {
		String access_token_url = null;
		access_token_url = clientSecurityDetails.getAccessCodeUri();
		access_token_url += DigilockerConstants._RESPONSE_TYPE+clientSecurityDetails.getResponseType();
		access_token_url += DigilockerConstants._URI_CLIENT_ID+clientSecurityDetails.getUsername();
		access_token_url += DigilockerConstants._CLIENT_REDIRECT_URI+clientSecurityDetails.getAccessCodeRedirectUri();
		access_token_url += DigilockerConstants._CLIENT_STATE+state; // This Code needs to be replaced with  
		System.out.println(access_token_url+"--------------------");
		return access_token_url;
	}

	
	
	/**
	 * 
	 * @param code
	 * @param stateR
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	@Operation(summary = "generating an access token from the Digilocker site")
	@GetMapping(value = "/getDigilockerDetails")
	public ServiceOutcome<String> getDigilockerDetails(String code,String state,HttpServletResponse response) throws JsonProcessingException, IOException {
		System.out.println("code:"+code);
		//String message = digilockerService.getDigilockerDetails(code,state,response,"SELF");
		ServiceOutcome<String> outcome = digilockerService.getDigilockerDetails(code,state,response,"SELF");
		//return new ResponseEntity<>(message, HttpStatus.OK);
		return outcome;
    }
	
	public String decodeBase64(String encodedData){
		String decodedByteString = null;
		byte[] base64Decoded = DatatypeConverter.parseBase64Binary(encodedData);
        decodedByteString =(new String(base64Decoded));
        return decodedByteString;
	}
	
	@Operation(summary = "Getting the access code from Digilocker site for relation")
	@PostMapping(value = "/verifyRelation")
    public ResponseEntity<ServiceOutcome<String>> relationVerify(@RequestBody String candidateObj){
		ServiceOutcome<String> outcome = new ServiceOutcome<>();
		try {
		if(candidateObj!=null) {
			String candidateCode = new JSONObject(candidateObj).getString("candidateCode");
			ServiceOutcome<CandidateStatus> candidate = candidateService.getCandidateStatusByCandidateCode(candidateCode);
			if(candidate.getOutcome()) {
				String response = createAccessCodeUriForRelation(candidateCode);
				outcome.setData(candidateCode + "#" + candidate.getData().getStatusMaster().getStatusCode() + "#" + response);
				outcome.setMessage(candidate.getMessage());
				outcome.setOutcome(true);
			}else{
				outcome.setData("Candidate not found");
				outcome.setMessage("Candidate not found.");
				outcome.setOutcome(false);
			}
		}else {
			outcome.setData("CANDIDATE CODE not found");
			outcome.setMessage("Candidate code is either empty or null.");
			outcome.setOutcome(false);
		}
		
		}catch(Exception e) {
			outcome.setData("KICHI GOTE HELA AAU");
			outcome.setMessage("Something went wrong.");
			outcome.setOutcome(false);
			}
		
		return new ResponseEntity<ServiceOutcome<String>>(outcome, HttpStatus.OK);
    }
	
	@Operation(summary = "generating an access token from the Digilocker site and get relationship address details.")
	@GetMapping(value = "/getRelationDigilockerDetails" )
	public ServiceOutcome<String> getRelationDigilockerDetails(String code,String state,HttpServletRequest req,HttpServletResponse response) throws JsonProcessingException, IOException {
		System.out.println("req state param :"+ req.getParameter("state"));
		//String message = digilockerService.getDigilockerDetails(code,state,response,"RELATION");
		ServiceOutcome<String> outcome = digilockerService.getDigilockerDetails(code,state,response,"RELATION");
		String data=outcome.getData();
        log.info("DIGILOCKER ADHAR RESPONSE:: {}",data);
        if(data.equalsIgnoreCase("FAILED")) {
            String[] parts = state.split("_");
            String onlyCandidateCode=parts[1];
            String backToDigilockerReLogin=environmentVal.getRedirectAngularToDigilocker()+onlyCandidateCode;
            log.info("On Adhar FAILED, BACK TO LOGIN URL::{}",backToDigilockerReLogin);
            outcome.setOutcome(false);
            outcome.setMessage(backToDigilockerReLogin);
            outcome.setData("Adhar is missing in DIGILOCKER");
            outcome.setStatus(data);
            response.sendRedirect(environmentVal.getAadharRelogin()+onlyCandidateCode);
        }
		//return new ResponseEntity<>(message, HttpStatus.OK);
		return outcome;
    }
	
	private String createAccessCodeUriForRelation(String state) {
		String access_token_url = null;
		access_token_url = clientSecurityDetails.getAccessCodeUri();
		access_token_url += DigilockerConstants._RESPONSE_TYPE+clientSecurityDetails.getResponseType();
		access_token_url += DigilockerConstants._URI_CLIENT_ID+clientSecurityDetails.getRelationUsername();
		access_token_url += DigilockerConstants._CLIENT_REDIRECT_URI+clientSecurityDetails.getAccessCodeRelationRedirectUri();
		access_token_url += DigilockerConstants._CLIENT_STATE+state; // This Code needs to be replaced with  
		
		return access_token_url;
	}
	
	@Operation(summary = "Create Access Code URI for self .")
	@PostMapping(value = "/createAccessCodeUriForSelf" )
	public ResponseEntity<ServiceOutcome<String>> createAccessCodeUri(@RequestBody String candidateObj,HttpServletResponse res) throws JsonProcessingException, IOException {
		ServiceOutcome<String> outcome = new ServiceOutcome<>();
		String response ="";
		if(candidateObj!=null) {
			String candidateCode = new JSONObject(candidateObj).getString("candidateCode");
		//	emailSentTask.loa(candidateCode);
			ServiceOutcome<CandidateStatus> candidateStatus = candidateService.getCandidateStatusByCandidateCode(candidateCode);
			System.out.println(candidateStatus.getData().getStatusMaster().getStatusCode()+"status");
			
			Candidate findByCandidateCode = candidateRepository.findByCandidateCode(candidateCode);
			 log.info("candidateBelongto::: {}",findByCandidateCode.getOrganization().getOrganizationName());
			ServiceOutcome<List<String>> configCodes = candidateService.getServiceConfigCodes(candidateCode, null);
			log.info("ORGANIZATION ALLOWED THE SERVICES LIST ::{}",configCodes.getData());
			
			if(candidateStatus.getData().getStatusMaster().getStatusCode().equals("PROCESSDECLINED")) {
				outcome.setData(null);
				outcome.setMessage("You have already declined the process.");
				outcome.setOutcome(false);
			}else {
				//below condition for the case where some organization does not need digi locker.
				if(Boolean.TRUE.equals(configCodes.getOutcome()) && configCodes.getData().contains("DIGILOCKER")) {
					response = environmentVal.getRedirectAngularToDigilocker()+candidateCode;
				}else {
					response=environmentVal.getITRLogin()+candidateCode;
				}
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
	
	@GetMapping(value = "/temp" )
	public ResponseEntity<ServiceOutcome<String>> getAadhar(@RequestParam("token") String token,@RequestParam("candidateCode") String candidateCode,HttpServletResponse res){
		digilockerService.getUserDetails(token,"code",candidateCode,res,"SELF");
		return new ResponseEntity<ServiceOutcome<String>>(new ServiceOutcome<>(), HttpStatus.OK);
	}

	//New API FOR DIrecting user to digilocker original login screen  (23-06-2023)
	
		@Operation(summary = "New API for redirecting user to digilocker original login screen")
		@PostMapping(value = "/getDigiLockerdetail")
		public ServiceOutcome<String> getDigiLockerdetail(@RequestBody DigiLockerDetailsDto digilockerDetails,HttpServletResponse res){
			System.out.println(digilockerDetails+"digi request----------------details");
			ServiceOutcome<String> response = new ServiceOutcome();
			if(StringUtils.isNotEmpty(digilockerDetails.getCandidateCode()) && StringUtils.isNotEmpty(digilockerDetails.getAadhaar())) {
			 try {
		        String digilockerDetailsString =digilockerDetails.getAadhaar()+"_"+digilockerDetails.getCandidateCode();
		        System.out.println(digilockerDetailsString+"digi request----------------details in JsonString");
		        
		        //create the full authorize url for redirecting to digi login
		        String redirectToLogin = createAccessCodeUriForRelation(digilockerDetailsString);
		        System.out.println(redirectToLogin+"digi----------------Login Page URI");
		        
		        // res.sendRedirect(redirectToLogin);
				response.setData(redirectToLogin);
				log.info("response {}", response);
			 }catch(Exception e) {
				log.error("Something went wrong in getDigiLockerLogin redirect method-->"+e);
			 }
			}

			return response;
		}
	

	@Operation(summary = "Getting the digi details from Digilocker site")
	@PostMapping(value = "/getDigiLockerdetail_old")
	public ServiceOutcome<String> getDigiLockerdetail(@RequestBody DigiLockerDetailsDto digilockerDetails){
		System.out.println(digilockerDetails+"digi----------------details");
		ServiceOutcome<String> response =  digilockerService.getDigiLockerdetail(digilockerDetails);
		return response;
	}

	@Operation(summary = "Getting the digi all details from Digilocker site")
	@PostMapping(value = "/getDigiLockerAlldetail")
	public ServiceOutcome<String> getDigiLockerAlldetail(@RequestBody DigiLockerDetailsDto digilockerDetails,HttpServletResponse res){
		System.out.println(digilockerDetails+"digi----------------details");
		ServiceOutcome<String> response =  digilockerService.getDigiLockerAlldetail(digilockerDetails,res);
		return response;
	}


	@Operation(summary ="generating an access token from the DigiLocker site")
	@GetMapping(value = "/getDigiTansactionid/{candidateCode}")
	public ServiceOutcome<DigiLockerDetailsDto> getDigiTansactionid(@PathVariable String candidateCode) {
		System.out.println(candidateCode+"------------candidatecode-----------------");
		ServiceOutcome<DigiLockerDetailsDto> svcSearchResult = new ServiceOutcome<>();
		if(candidateCode!=null) {
			//svcSearchResult = digilockerService.getDigiTansactionid(candidateCode);
			DigiLockerDetailsDto digiDetails = new DigiLockerDetailsDto();
			digiDetails.setCandidateCode(candidateCode);
			svcSearchResult.setData(digiDetails);
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage("Candidate code is not null.");
		}else {
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Candidate code is null.");
		}
		
		return svcSearchResult;
    }
	

	@Operation(summary ="Get all Candidate digilocker details")
	@PostMapping(value = "/getDLEdudocument",consumes = {  MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<ServiceOutcome<Boolean>> getDLEdudocument(@RequestParam String digidetails,@RequestHeader("Authorization") String authorization,HttpServletResponse res) {
		System.out.println(digidetails+"============");
		ServiceOutcome<Boolean> svcSearchResult=digilockerService.getDLEdudocument(digidetails,res);
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);

	}
	
}
