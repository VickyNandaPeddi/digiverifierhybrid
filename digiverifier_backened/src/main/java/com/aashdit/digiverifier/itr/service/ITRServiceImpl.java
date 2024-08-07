package com.aashdit.digiverifier.itr.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.aashdit.digiverifier.globalConfig.EnvironmentVal;
import com.aashdit.digiverifier.itr.model.CanditateItrResponse;
import com.aashdit.digiverifier.itr.repository.CanditateItrEpfoResponseRepository;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.aashdit.digiverifier.client.securityDetails.ITRSecurityConfig;
import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCafExperienceRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.StatusMasterRepository;
import com.aashdit.digiverifier.config.candidate.service.CandidateService;
import com.aashdit.digiverifier.config.superadmin.repository.ColorRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ServiceSourceMasterRepository;
import com.aashdit.digiverifier.itr.dto.ITRDataFromApiDto;
import com.aashdit.digiverifier.itr.dto.ITRDetailsDto;
import com.aashdit.digiverifier.itr.model.ITRData;
import com.aashdit.digiverifier.itr.repository.ITRDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aashdit.digiverifier.config.candidate.model.CandidateCafAddress;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCafAddressRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ServiceTypeConfigRepository;


import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ITRServiceImpl implements ITRService {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ITRSecurityConfig itrSecurityConfig;
	
	@Autowired
	private CandidateRepository candidateRepository;
	
	@Autowired
	private ITRDataRepository itrDataRepository;
	
	@Autowired
	private ServiceSourceMasterRepository serviceSourceMasterRepository;
	
	@Autowired
	private CandidateStatusRepository candidateStatusRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ColorRepository colorRepository;
	
	@Autowired
	private CandidateCafExperienceRepository candidateCafExperienceRepository;
	
	@Autowired
	private StatusMasterRepository statusMasterRepository;
	
	@Autowired
	private CandidateService candidateService;
	
	@Autowired
	private CanditateItrEpfoResponseRepository canditateItrEpfoResponseRepository;
	
	@Autowired
	private EnvironmentVal environmentVal;

  	@Autowired
	private ServiceTypeConfigRepository serviceTypeConfigRepository;
	
	@Autowired
	private CandidateCafAddressRepository candidateCafAddressRepository;
	
	/**
	 * 
	 * @param headers
	 * @param encodedCedential
	 * @return
	 */
	private HttpHeaders setHeaderDetails (HttpHeaders headers) {
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
	
	@Override
	public ServiceOutcome<String> getITRDetailsFromITRSite(ITRDetailsDto iTRDetails) {
		ResponseEntity<String> itrTokenResponse = null;
      HttpHeaders headers = new HttpHeaders();
      setHeaderDetails(headers);
      JSONObject request = new JSONObject();
      ServiceOutcome<String> result = new ServiceOutcome<String>();
      try {
      	request.put(itrSecurityConfig.getClientIdValue(),itrSecurityConfig.getClientId());
			request.put(itrSecurityConfig.getClientSecretValue(),itrSecurityConfig.getClientSecret());
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
      	
			itrTokenResponse = restTemplate.exchange(itrSecurityConfig.getAccessTokenUrl(), HttpMethod.POST, entity, String.class);
			
			String message=itrTokenResponse.getBody();
			JSONObject obj = new JSONObject(message);
			String token = null;
			if(obj != null) {
				token = obj.getJSONObject("message").getString("access_token");
			}
      	if(itrTokenResponse.getStatusCode() == HttpStatus.OK) {
      		log.info("Token Created Successfully. Calling ITR Transaction Id API"+iTRDetails.getCandidateCode());
      		result = getTransactionId(token, iTRDetails);
      		
      	}else if(itrTokenResponse.getStatusCode() == HttpStatus.UNAUTHORIZED){
      		result.setData("User is Unauthorized to access the ITR");
      		result.setOutcome(false);
      		result.setMessage("User is Unauthorized to access the ITR.");
      		log.error("User is Unauthorized to access the ITR"+iTRDetails.getCandidateCode());
      	}else if(itrTokenResponse.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT){
      		result.setData("Server response is slow, getting timeout.");
      		result.setOutcome(false);
      		result.setMessage("Server response is slow, getting timeout.");
      		log.error("Server response is slow, getting timeout"+iTRDetails.getCandidateCode());
      	}else if(itrTokenResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
      		result.setData("Server is down or Not responding at this Moment.");
      		result.setOutcome(false);
      		result.setMessage("Server is down or Not responding at this Moment.");
      		log.error("Server is down or Not responding at this Moment"+iTRDetails.getCandidateCode());
      	}
      }catch (Exception jsn) {
      	log.error("Exception occured: "+iTRDetails.getCandidateCode(),jsn); // Add the Proper logging Message here
      	result.setData("Something went wrong.");
  		result.setOutcome(false);
  		result.setMessage("Something went wrong.");
		}
	return result;
  }
	
	/**
	 * 
	 * @param accessToken
	 * @param candidateId
	 * @param itrUserName
	 * @param itrPassword
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	public ServiceOutcome<String> getTransactionId(String accessToken,ITRDetailsDto iTRDetails) throws JsonProcessingException, IOException{
		ServiceOutcome<String> result = new ServiceOutcome<String>();
		if(StringUtils.isNoneBlank(accessToken) || StringUtils.isNoneBlank(iTRDetails.getCandidateCode())
				|| StringUtils.isNoneBlank(iTRDetails.getUserName()) || StringUtils.isNoneBlank(iTRDetails.getPassword())) {
			
			Candidate candidate = candidateRepository.findByCandidateCode(iTRDetails.getCandidateCode());
			candidate.setItrPanNumber(iTRDetails.getUserName());
			candidateRepository.save(candidate);
			
			ResponseEntity<String> response = null;
			HttpHeaders headers = new HttpHeaders();
			setHeaderDetails(headers);
	        headers.setBearerAuth(accessToken);
	        headers.add("Bearer", accessToken); // This is required as by Simply adding the 
	        HttpEntity<String> request = new HttpEntity<String>(headers);
	        try {
			  	response = restTemplate.exchange(itrSecurityConfig.getTransactionIdUrl(), HttpMethod.GET, request, String.class);
			  	String message=response.getBody();
			  	try {
			  		JSONObject obj = new JSONObject(message);
			  		String transactionId = obj.getString("message");
			  		if(response.getStatusCode() == HttpStatus.OK) {
			  			log.info("Transaction Id Created Successfully. Response returned"+iTRDetails.getCandidateCode());
			  			result = getPostLogInInfo(accessToken,iTRDetails.getCandidateCode(), transactionId, iTRDetails.getUserName(), iTRDetails.getPassword());
			  		}else if(response.getStatusCode() == HttpStatus.UNAUTHORIZED){
			      		result.setData("User is Unauthorized to access the ITR");
			      		result.setOutcome(false);
			      		result.setMessage("User is Unauthorized to access the ITR.");
			      		log.error("User is Unauthorized to access the ITR"+iTRDetails.getCandidateCode());
			      	}else if(response.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT){
			      		result.setData("Server response is slow, getting timeout.");
			      		result.setOutcome(false);
			      		result.setMessage("Server response is slow, getting timeout.");
			      		log.error("Server response is slow, getting timeout"+iTRDetails.getCandidateCode());
			      	}else if(response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
			      		result.setData("Server is down or Not responding at this Moment.");
			      		result.setOutcome(false);
			      		result.setMessage("Server is down or Not responding at this Moment.");
			      		log.error("Server is down or Not responding at this Moment"+iTRDetails.getCandidateCode());
			      	}
		  		}catch(JSONException jsn) {
		  			result.setData("Something went wrong.");
		      		result.setOutcome(false);
		      		result.setMessage("Something went wrong.");
		  			log.error("JSON Exception occured"+iTRDetails.getCandidateCode(),jsn);
		  		}
			}catch(HttpClientErrorException e) {
				result.setData("Something went wrong.");
	      		result.setOutcome(false);
	      		result.setMessage("Something went wrong.");
	  			log.error("HttpClientErrorException occured in getTransactionId in ITRServiceImpl-->"+iTRDetails.getCandidateCode(),e);
			} catch(HttpServerErrorException ex) {
				result.setData("Something went wrong.");
	      		result.setOutcome(false);
	      		result.setMessage("Something went wrong.");
	  			log.error("HttpServerErrorException occured in getTransactionId in ITRServiceImpl-->"+iTRDetails.getCandidateCode(),ex);
			}
		}else {
			log.error("Invalid ITR Token generated Or Token is null, Please Check the ITR server might be down Or Not Responding"+iTRDetails.getCandidateCode());
		}
		return result;
  }
	
	/**
	 * 
	 * @param access_token
	 * @param transactionId
	 * @param candidateId
	 * @param itrUserName
	 * @param itrPassword
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	// This method should accept the details with the below parameters as @RequestParam.
	public ServiceOutcome<String> getPostLogInInfo(String access_token, String candidateId, String transactionId,  String itrUserName, String itrPassword)throws JsonProcessingException, IOException{
		 ServiceOutcome<String> outcome = new ServiceOutcome<String>();	
		if(StringUtils.isNotEmpty(transactionId) && StringUtils.isNotEmpty(access_token) && StringUtils.isNotEmpty(candidateId)) {
			Candidate candidate= candidateRepository.findByCandidateCode(candidateId);
      List<String> orgServices = serviceTypeConfigRepository.getServiceSourceMasterByOrgId(candidate.getOrganization().getOrganizationId());

				ResponseEntity<String> response = null;
				HttpHeaders headers = new HttpHeaders();
		        headers.add("Bearer", access_token); 
		        headers.setContentType(MediaType.APPLICATION_JSON);
		        JSONObject request = new JSONObject();
		        SimpleDateFormat sdfp = new SimpleDateFormat("yyyy-MM-dd");
		        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		        try {
		        	request.put("itr-user",itrUserName);
					request.put("itr-pwd",itrPassword);
					HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
					response = restTemplate.exchange(itrSecurityConfig.getPostLoginInfoUrl()+transactionId, HttpMethod.POST, entity, String.class);
					String message=response.getBody();
					String resMsg = message;
					JSONObject obj = new JSONObject(message);
				
					if(!obj.getBoolean("success")) {
						outcome.setData(obj.getString("message"));
						outcome.setOutcome(false);
						outcome.setMessage(obj.getString("message"));
					}else {
						String itrDetails = obj.getString("message");
						System.out.println(itrDetails+"itrDetails "+candidateId);
						if(response.getStatusCode() == HttpStatus.OK && !itrDetails.equals("") && itrDetails.contains("Form26ASInfo")) {
			        		log.info("Post Login Information retrieved successfully "+candidateId);
			        		JSONObject form26ASInfo = new JSONObject(itrDetails).getJSONObject("Form26ASInfo");
							resMsg = form26ASInfo.toString();
			        		JSONArray tDSDetails = form26ASInfo.getJSONArray("TDSDetails");	
							System.out.println(tDSDetails+"tDSDetailsssss "+candidateId);
			        		List<ITRDataFromApiDto> finalItrList = new ArrayList<ITRDataFromApiDto>();
			        		for(int i=0; i<tDSDetails.length();i++) {
			        			JSONObject object = tDSDetails.getJSONObject(i);
			        			if(object.length()!=0) {
			        				JSONObject year = object.getJSONObject("$");
				        			JSONArray tdss = object.getJSONArray("TDSs");
				        			for(int j=0; j<tdss.length();j++) {
				        				JSONObject preFinalObject = tdss.getJSONObject(j);
				        				JSONArray tds = preFinalObject.getJSONArray("TDS");
				        				for(int k=0; k<tds.length();k++) {
											JSONObject finalObject = tds.getJSONObject(k);
											JSONObject tdsData = finalObject.getJSONObject("$");
											final ObjectMapper objectMapper = new ObjectMapper();
											ITRDataFromApiDto itr = objectMapper.readValue(tdsData.toString(), ITRDataFromApiDto.class);
											itr.setAssesmentYear(year.getString("ay"));
											itr.setFinancialYear(year.getString("fy"));
											itr.setFiledDate(dateFormat.parse(itr.getDate()));
											finalItrList.add(itr);
				        				}
				        			}
			        			}
			        		}
			        		List<ITRData> itrDataList = new ArrayList<ITRData>();
			        		for(ITRDataFromApiDto itr: finalItrList) {
			        			ITRData itrData = new ITRData();
			        			BeanUtils.copyProperties(itr, itrData);
			        			itrData.setCandidate(candidate);
			        			itrData.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("ITR"));
			        			itrDataList.add(itrData);
			        		}
			        		
			        		List<ITRData> alreadyExistingData = itrDataRepository.findAllByCandidateCandidateCodeOrderByFiledDateDesc(candidate.getCandidateCode());
			        		if(itrDataList!=null && itrDataList.size()>0 && alreadyExistingData.size() == 0) {
			        			itrDataRepository.saveAll(itrDataList);
			        			
//			        			StringBuilder query = new StringBuilder();
//			        			query.append("select itr.tan_no,itr.deductor,max(itr.filed_date),min(itr.filed_date) from t_dgv_candidate_itr itr\n");
//			        			query.append("where itr.candidate_id =:candidateId\n");
//			        			query.append("group by itr.deductor,itr.tan_no\n");
//
//			        			Query resultQuery = entityManager.createNativeQuery(query.toString());
//								resultQuery.setParameter("candidateId", candidate.getCandidateId());
//								 List<CandidateCafExperience> experiencesList = new ArrayList<CandidateCafExperience>();
//								List<Object[]> itrTenureList = resultQuery.getResultList();
//								for(Object[] itrTenure:itrTenureList) {
//									CandidateCafExperience candidateCafExperience = new CandidateCafExperience();
//				        			candidateCafExperience.setCandidateEmployerName(itrTenure[1].toString());
//				        			candidateCafExperience.setOutputDateOfJoining(sdfp.parse(itrTenure[3].toString()));
//				        			candidateCafExperience.setInputDateOfJoining(sdfp.parse(itrTenure[3].toString()));
//				        			candidateCafExperience.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("ITR"));
//				        			candidateCafExperience.setOutputDateOfExit(sdfp.parse(itrTenure[2].toString()));
//				        			candidateCafExperience.setInputDateOfExit(sdfp.parse(itrTenure[2].toString()));
//				        			candidateCafExperience.setCandidate(candidate);
//				        			candidateCafExperience.setCreatedOn(new Date());
//				        			candidateCafExperience.setColor(colorRepository.findByColorCode("GREEN"));
//				        			candidateCafExperience.setTanNo(itrTenure[0].toString());
//				        			experiencesList.add(candidateCafExperience);
//								}
//								if(experiencesList!=null && experiencesList.size()>0) {
//				        			candidateCafExperienceRepository.saveAll(experiencesList);
//

			        			//updating status in last
//				        			CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(candidateId);
//				        			candidateStatus.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("ITR"));
//				        			candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("ITR"));
//				        			candidateStatus.setLastUpdatedOn(new Date());
//				        			candidateStatusRepository.save(candidateStatus);
//				        			candidateService.createCandidateStatusHistory(candidateStatus,"CANDIDATE");

				        			
//				        		}
			        			

			        			outcome.setData("ITR data recieved successfully.");
					      		outcome.setOutcome(true);
					      		outcome.setMessage("ITR data recieved successfully.");
					      		log.info("ITR data recieved successfully "+candidateId);
			        		}else {
			        			CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(candidateId);
			        			candidateStatus.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("ITR"));
			        			candidateStatus.setLastUpdatedOn(new Date());
			        			candidateStatusRepository.save(candidateStatus);
								if(candidateStatus.getCandidate().getOrganization().getCallBackUrl() != null)
									candidateService.postStatusToOrganization(candidateStatus.getCandidate().getCandidateCode());
			        			candidateService.createCandidateStatusHistory(candidateStatus,"CANDIDATE");
			        			log.info("ITR else "+candidateId);
			        			outcome.setData("No Data Found.");
					      		outcome.setOutcome(true);
					      		outcome.setMessage("No Data Found.");
			        		}
			        	}else if(response.getStatusCode() == HttpStatus.OK && !itrDetails.equals("") && !itrDetails.contains("Form26ASInfo")) {
			        		log.error("Invalid credentials"+candidateId);
			        		outcome.setData("Something Went Wrong, Please Check The Credentials..!");
				      		outcome.setOutcome(false);
				      		outcome.setMessage("Something Went Wrong, Please Check The Credentials..!");
				  			
					    }else if(response.getStatusCode() == HttpStatus.UNAUTHORIZED){
				      		outcome.setData("User is Unauthorized to access the ITR");
				      		outcome.setOutcome(false);
				      		outcome.setMessage("User is Unauthorized to access the ITR.");
				      		log.error("User is Unauthorized to access the ITR "+candidateId);
				      	}else if(response.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT){
				      		outcome.setData("Server response is slow, getting timeout.");
				      		outcome.setOutcome(false);
				      		outcome.setMessage("Server response is slow, getting timeout.");
				      		log.error("Server response is slow, getting timeout "+candidateId);
				      	}else if(response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
				      		outcome.setData("Server is down or Not responding at this Moment.");
				      		outcome.setOutcome(false);
				      		outcome.setMessage("Server is down or Not responding at this Moment.");
				      		log.error("Server is down or Not responding at this Moment"+candidateId);
				      	}
					}
					CanditateItrResponse canditateItrEpfoResponse = canditateItrEpfoResponseRepository
						.findByCandidateId(candidate.getCandidateId()).orElse(new CanditateItrResponse());
					canditateItrEpfoResponse.setForm26AsResponse(resMsg);
					canditateItrEpfoResponse.setCandidateId(candidate.getCandidateId());
					canditateItrEpfoResponse.setCandidate(candidate);
					canditateItrEpfoResponse.setCreatedOn(new Date());
					canditateItrEpfoResponse.setLastUpdatedOn(new Date());
					canditateItrEpfoResponseRepository.save(canditateItrEpfoResponse);
					String itrDetails = obj.getString("message");
					if(itrDetails.contains("Form26ASInfo")){
						JSONObject itrDetailsObj = new JSONObject(itrDetails); 

						JSONObject form26ASInfo = itrDetailsObj.getJSONObject("Form26ASInfo");
						JSONArray personalDetails = form26ASInfo.getJSONArray("PersonalDetails"); 
						for(int i=0; i<personalDetails.length();i++) {
							JSONObject object = personalDetails.getJSONObject(i);
							if(object.length()!=0) {
								JSONObject name = object.getJSONObject("$");
								candidate.setItrPanNumber(name.getString("pan"));
								candidate.setPanDob(name.getString("dob")); 
								candidate.setPanName(name.getString("name"));
								candidate.setMaskedAadhar(name.getString("masked_aadhar"));
								candidate.setAadharLinked(name.getBoolean("aadhar_linked"));
								
								if(!orgServices.contains("DIGILOCKER")) {
									if(name.has("gender"))
										candidate.setAadharGender(name.getString("gender"));
									if(name.has("address")) {
								  		CandidateCafAddress address = new CandidateCafAddress();
								  		address.setCandidate(candidate);
										address.setCandidateAddress(name.getString("address"));
								  		address.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("PAN"));
								  		address.setColor(colorRepository.findByColorCode("GREEN"));
								  		address.setCreatedOn(new Date());
								  		if(name.has("name"))
								  			address.setName(name.getString("name"));
								  		
								  		CandidateCafAddress candidateCafAddress = candidateCafAddressRepository.findByCandidateCandidateCodeAndServiceSourceMasterServiceCode(candidate.getCandidateCode(),"PAN");
				 						if(candidateCafAddress != null)
				 							address.setCandidateCafAddressId(candidateCafAddress.getCandidateCafAddressId());
				 						
								  		candidateCafAddressRepository.save(address);
									}	
								}

								candidateRepository.save(candidate);
							}
						}
						
						//updating status in last of the ITR flow
						CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(candidateId);
	        			candidateStatus.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("ITR"));
	        			candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("ITR"));
	        			candidateStatus.setLastUpdatedOn(new Date());
	        			candidateStatusRepository.save(candidateStatus);
	        			if(candidateStatus.getCandidate().getOrganization().getCallBackUrl() != null) {
							candidateService.postStatusToOrganization(candidateStatus.getCandidate().getCandidateCode());
	        			}
	        			candidateService.createCandidateStatusHistory(candidateStatus,"CANDIDATE");
					}
					
					
					// String name = personalDetails.getString("$");
					// candidate.setPanDob(name.getString("dob"));
					// candidate.setPanName(name.getString("name"));
					// System.out.println(name.getString("dob")+"****"+name.getString("name")+"personal data");
					// candidateRepository.save(candidate);
					return outcome;
		        }catch (Exception jsn) {
		        	log.error("Exception occured in itr: "+candidateId,jsn); 
		        	outcome.setData("Something went wrong.");
		      		outcome.setOutcome(false);
		      		outcome.setMessage("Something went wrong.");
		  			log.error("JSON Exception occured"+candidateId,jsn);
		  			return outcome;
				}
				
			}else {
				log.error("Either ITR Token Or TransactionId Or candidateId Or UserName Or Password is not provided / Missing"+candidateId);
				outcome.setData("Either ITR Token Or TransactionId Or candidateId Or UserName Or Password is not provided / Missing");
	      		outcome.setOutcome(false);
	      		outcome.setMessage("Something went wrong.");
				return  outcome;
			}
		
	}

}
