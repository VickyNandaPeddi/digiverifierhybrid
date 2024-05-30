package com.aashdit.digiverifier.digilocker.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpServletResponse;
import com.aashdit.digiverifier.client.securityDetails.EPFOSecurityConfig;
import com.aashdit.digiverifier.common.ContentRepository;
import com.aashdit.digiverifier.common.enums.ContentCategory;
import com.aashdit.digiverifier.common.enums.ContentSubCategory;
import com.aashdit.digiverifier.common.enums.ContentType;
import com.aashdit.digiverifier.common.enums.FileType;
import com.aashdit.digiverifier.common.model.Content;
import com.aashdit.digiverifier.globalConfig.EnvironmentVal;
import com.aashdit.digiverifier.utils.AwsUtils;
import com.aashdit.digiverifier.utils.FileUtil;
import com.aashdit.digiverifier.utils.PdfUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.aashdit.digiverifier.client.securityDetails.DigilockerClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.repository.query.Param;

import com.aashdit.digiverifier.client.securityDetails.DigilockerClient;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.CandidateAdressVerification;
import com.aashdit.digiverifier.config.candidate.model.CandidateCafAddress;
import com.aashdit.digiverifier.config.candidate.model.CandidateCafEducation;
import com.aashdit.digiverifier.config.candidate.model.CandidateIdItems;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.repository.CandidateAdressVerificationRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCafAddressRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCafEducationRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateIdItemsRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.QualificationMasterRepository;
import com.aashdit.digiverifier.config.candidate.repository.StatusMasterRepository;
import com.aashdit.digiverifier.config.candidate.repository.SuspectClgMasterRepository;
import com.aashdit.digiverifier.config.candidate.service.CandidateService;
import com.aashdit.digiverifier.config.superadmin.model.Color;
import com.aashdit.digiverifier.config.superadmin.repository.ColorRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ServiceSourceMasterRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ServiceTypeConfigRepository;
import com.aashdit.digiverifier.constants.DigilockerConstants;
import com.aashdit.digiverifier.digilocker.dto.DigilockerTokenResponse;
import com.aashdit.digiverifier.digilocker.dto.IssuedDocumentsResponse;
import com.aashdit.digiverifier.digilocker.repository.IssuedDocumentsTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.aashdit.digiverifier.digilocker.dto.DigiLockerDetailsDto;
import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DigilockerServiceImpl implements DigilockerService {
	
    private static final Logger logger = LoggerFactory.getLogger(DigilockerServiceImpl.class);
	
	public static final String DIGIVERIFIER_DOC_BUCKET_NAME = "digiverifier-new";
	public static final String HOUSE = "house";
	public static final String UNABLE_TO_GET_DIGI_DETAILS = "Unable to get DIgi details.";
	public static final String ADDRESS = "Address";
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private EPFOSecurityConfig epfoSecurityConfig;

	@Autowired
	private DigilockerClient clientSecurityDetails;
	
	
	@Autowired
	private DigilockerTokenResponse digilockerTokenResponse;

	
	
	@Autowired
	private IssuedDocumentsTypeRepository issuedDocumentsTypeRepository;
	
	@Autowired
	private CandidateRepository candidateRepository;
	
	@Autowired
	private ColorRepository colorRepository;
	
	@Autowired
	private ServiceSourceMasterRepository serviceSourceMasterRepository;
	
	@Autowired
	private CandidateCafAddressRepository candidateCafAddressRepository;
	
	@Autowired
	private CandidateStatusRepository candidateStatusRepository;
	
	@Autowired
	private CandidateIdItemsRepository candidateIdItemsRepository;
	
	@Autowired
	private CandidateAdressVerificationRepository candidateAdressVerificationRepository;
	
	@Autowired
	private StatusMasterRepository statusMasterRepository;
	
	@Autowired
	private CandidateService candidateService;
	
	@Autowired
	private QualificationMasterRepository qualificationMasterRepository;
	
	@Autowired
	private SuspectClgMasterRepository suspectClgMasterRepository;
	
	@Autowired
	private CandidateCafEducationRepository candidateCafEducationRepository;
	
	@Autowired
	@Lazy
	private AwsUtils awsUtils;
	
	@Autowired
	@Lazy
	private PdfUtil pdfUtil;
	
	@Autowired @Lazy
	private ContentRepository contentRepository;
	
	@Autowired
	private EnvironmentVal environmentVal;
	
	@Autowired
	private ServiceTypeConfigRepository serviceTypeConfigRepository;
	
	@Override
	public ServiceOutcome<String> getDigilockerDetails(String code, String candidateCode, HttpServletResponse response, String action) {
		ServiceOutcome<String> outcome = new ServiceOutcome<>();
		String  tokenString="";
		String message = "";
		try {
			if(StringUtils.isNotBlank(code) && StringUtils.isNotBlank(candidateCode)) {
				clientSecurityDetails.setCode(code);
				clientSecurityDetails.setState(candidateCode);
				
			    String res = null;
			    
			    String credentials = action.equals("SELF")?clientSecurityDetails.getUsername()+DigilockerConstants._DELIMETER+clientSecurityDetails.getPassword():
			    										   clientSecurityDetails.getRelationUsername()+DigilockerConstants._DELIMETER+clientSecurityDetails.getRelationPassword();
				String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
				
				HttpHeaders headers = new HttpHeaders();
				setHeaderDetails(headers,encodedCredentials);
				LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
				logger.info("grant type : {} ", clientSecurityDetails.getGrantType());
			    params.add(DigilockerConstants._CODE,clientSecurityDetails.getCode());
			    params.add(DigilockerConstants._GRANT_TYPE,clientSecurityDetails.getGrantType());
			    params.add(DigilockerConstants._CLIENT_ID,action.equals("SELF")?clientSecurityDetails.getUsername():clientSecurityDetails.getRelationUsername());
			    params.add(DigilockerConstants._CLIENT_SECRET,action.equals("SELF")?clientSecurityDetails.getPassword():clientSecurityDetails.getRelationPassword());
			    params.add(DigilockerConstants._REDIRECT_URI,action.equals("SELF")?clientSecurityDetails.getRedirectUri():clientSecurityDetails.getRelationRedirectUri());
			    
			    HttpEntity<LinkedMultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);
					Gson gson=new Gson();
					logger.info("request url : {}", clientSecurityDetails.getAccessTokenUrl());
					logger.info(request.getBody().toString());
					res = restTemplate.postForObject(clientSecurityDetails.getAccessTokenUrl(),request, String.class);
					if(StringUtils.isNotBlank(res)) {
						digilockerTokenResponse =gson.fromJson(res, DigilockerTokenResponse.class);
						logger.info("DigilockerTokenResponse : {}", digilockerTokenResponse);
						tokenString = digilockerTokenResponse.getAccess_token();
						if(!tokenString.isEmpty()) {
							message = getUserDetails(tokenString,code,candidateCode,response,action);
							outcome.setOutcome(true);
							outcome.setMessage(tokenString);
							outcome.setData(message);
							outcome.setStatus(code);
						}
					}
			}
			logger.info("getDigilockerDetails :{}", message);
		} catch (Exception e) {
			  log.error("Exception occured in DigilockerServiceImpl in getDigilockerDetails method-->",e);
			 outcome.setData("FAILED");
			 outcome.setOutcome(false);
			 outcome.setMessage(UNABLE_TO_GET_DIGI_DETAILS);
		} 
		logger.info("=====================END OF getDigilockerDetails===============:{}", outcome);
		return outcome;
	}
	
	/**
	 * 
	 * @param headers
	 * @param encodedCedential
	 * @return
	 */
	private HttpHeaders setHeaderDetails (HttpHeaders headers, String encodedCedential) {
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add(clientSecurityDetails.getAuthorization(), clientSecurityDetails.getAuthorizationType() + encodedCedential);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return headers;
	}
	
	@Override
	public String getUserDetails(String accessToken,String code, String candidateCode,HttpServletResponse res, String action){
		// System.out.println(res+"--------------karthikaaa------------------------"+action);
		String result = "Access token is empty.";
		try {
			if(StringUtils.isNoneBlank(accessToken)) {
//				ResponseEntity<String> response = null;
//			    HttpHeaders headers = new HttpHeaders();
//			    headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
//			    headers.setBearerAuth(accessToken);
//			    HttpEntity<String> request = new HttpEntity<String>(headers);
//				  	response = (new RestTemplate()).exchange(clientSecurityDetails.getUserDetailsApi(), HttpMethod.GET, request, String.class);
				  	// System.out.println(response+"-------------------------------------response");
//					if(response.getStatusCode() == HttpStatus.OK) {
				  		result = getIssuedDocuments(accessToken,code, candidateCode,res,action);
						System.out.println(result+"============misnnsdn");
//			    	}else if(response.getStatusCode() == HttpStatus.UNAUTHORIZED){
//			    		result = "User is Unauthorized";
//			    	}else if(response.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT){
//			    		result = "Server response is slow, getting timeout";
//			    	}else if(response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
//			    		result = "Server is down or Not responding at this Moment";
//			    	}
			}
			// System.out.println(result+"getUserDetails");
		} catch (Exception e) {
			  log.error("Exception occured in DigilockerServiceImpl in getUserDetails method-->",e);
		}
		return result;
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	private String getIssuedDocuments(String accessToken,String code, String candidateCode,HttpServletResponse res, String action){
		String result="";
		String[] parts = candidateCode.split("_");
		String onlyCandidateCode=parts[1];
		 System.out.println("---inside getIssuedDocuments---"+"candidateCode:"+candidateCode+"--onlyCandidateCode:"+onlyCandidateCode);
		try {
			if(StringUtils.isNoneBlank(accessToken)) {
				ResponseEntity<String> response = null;
			    HttpHeaders headers = new HttpHeaders();
			    headers.setBearerAuth(accessToken);
			    HttpEntity<String> request = new HttpEntity<String>(headers);
				  	response = (new RestTemplate()).exchange(clientSecurityDetails.getUserFilesIssued(), HttpMethod.GET, request, String.class);
					// System.out.println(response+"--------------------inside response-------------------");
				  	if(response.getStatusCode() == HttpStatus.OK) {
						
				  		String message=response.getBody();
						System.out.println(message+"--------------------message-------------------");
						JSONObject obj = new JSONObject(message);
						JSONArray array = obj.getJSONArray("items");
						final ObjectMapper objectMapper = new ObjectMapper();
						IssuedDocumentsResponse[] issuedDocumentsResponses = objectMapper.readValue(array.toString(), IssuedDocumentsResponse[].class);
				        List<IssuedDocumentsResponse> issuedDocumentsList = new ArrayList(Arrays.asList(issuedDocumentsResponses));
				        List<String> issuedDocumentsType= issuedDocumentsTypeRepository.findAllDocumentNameByIsActiveTrue();
				        issuedDocumentsList = issuedDocumentsList.stream().filter(f->issuedDocumentsType.contains(f.getDoctype())).collect(Collectors.toList());
				        System.out.println(issuedDocumentsList+"--------------------inside issuedDocumentsList-------------------");
						
						Color color=colorRepository.findByColorCode("GREEN");
						List<File> issuedDocumentFiles =  new ArrayList<>();
						boolean isAdharavailable=false;
                        for(IssuedDocumentsResponse issuedDocument : issuedDocumentsList) {
                            String docType=issuedDocument.getDoctype();              
                            System.out.println("--------------------inside for-----------if--------");
                            String getFIleFromUriRes=getFIleFromUri(issuedDocument,accessToken,candidateCode,color,action);               
                            if(!getFIleFromUriRes.equalsIgnoreCase("FAILED") && docType.equalsIgnoreCase("ADHAR")) {
                                isAdharavailable=true;
                            }
                        }
                       log.info("Digilocker Contains ADHAR ::{}",isAdharavailable);
                       if(Boolean.FALSE.equals(isAdharavailable)){
                           log.info("Digilocker Does not Contains ISSUED DOCS.. ");
                           return "FAILED";
                       }
//						System.out.println(issuedDocumentFiles.size());
//						List<InputStream> collect = issuedDocumentFiles.stream().map(FileUtils::convertToInputStream).collect(Collectors.toList());
//						File mergedFile = FileUtils.createUniqueTempFile(candidateCode, ".pdf");
//						pdfUtil.mergePdfFiles(collect, new FileOutputStream(mergedFile.getPath()));
//						// move to s3
//						awsUtils.uploadFile("digiverifier-new",candidateCode.concat("/").concat(candidateCode+"_digilocker_issued_files"),mergedFile);
//
				        CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(onlyCandidateCode);
//	        			if(action.equals("SELF")) {
//							System.out.println("--------------------inside self if-------------------");
	        				candidateStatus.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("DIGILOCKER"));
		        			candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("DIGILOCKER"));
//	        			}else {
//							System.out.println("--------------------inside else-------------------");
//	        				candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("RELATIVEADDRESS"));
//	        			}
	        			candidateStatus.setLastUpdatedOn(new Date());
	        			candidateStatusRepository.save(candidateStatus);
						if(candidateStatus.getCandidate().getOrganization().getCallBackUrl() != null)
							candidateService.postStatusToOrganization(candidateStatus.getCandidate().getCandidateCode());
	        			candidateService.createCandidateStatusHistory(candidateStatus,"CANDIDATE");
	        			// if(action.equals("SELF")) {
						// 	System.out.println("inside restirect--------");
	        			// 	res.sendRedirect(environmentVal.getRedirectAngularAfterDigiLocker()+candidateCode);
	        			// }else {
	        			// 	 res.sendRedirect(environmentVal.getRedirectAngularToCandAppl());
	        			// }
				  		result ="Issued documents Information retrieved successfully";
						boolean retval = issuedDocumentsList.contains("DGCER"); 
	                    System.out.println(retval+"retvalretvalretvalretvalretval");
   						if (retval == false) {
   							Candidate candidate = candidateStatus.getCandidate();
   			                List<String> orgServices = serviceTypeConfigRepository.getServiceSourceMasterByOrgId(candidate.getOrganization().getOrganizationId());
	   			             if(orgServices!=null && orgServices.contains("DIGILOCKER")
	   					 			&& !orgServices.contains("EPFO") && !orgServices.contains("ITR")) {
	   			            	System.out.println("--------------------revel-----------if--------");
	   			            	candidate.setIsFresher(false);
	   			            	candidateRepository.save(candidate);
	   			            	result ="Issued documents Information retrieved successfully, dgree not in Issued documents";
	   			            	res.sendRedirect(environmentVal.getRedirectAngularToCandAppl()+"/"+onlyCandidateCode);
	   			             }else {
	   			            	System.out.println("--------------------revel-----------else--------");
								result ="Issued documents Information retrieved successfully, dgree not in Issued documents";
								res.sendRedirect(environmentVal.getIsFreshPage()+onlyCandidateCode);
	   			             }
							
						}
			    	}else if(response.getStatusCode() == HttpStatus.UNAUTHORIZED){
			    		result ="User is Unauthorized";
			    	}else if(response.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT){
			    		result ="Server response is slow, getting timeout";
			    	}else if(response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
			    		result ="Server is down or Not responding at this Moment";
			    	}
					
				
			}
			// System.out.println(result+"getIssuedDocuments---------------------------");
		} catch (Exception e) {
			  log.error("Exception occured in DigilockerServiceImpl in getIssuedDocuments method-->",e);
			  return "FAILED";
		}
		// System.out.println(result+"getIssuedDocuments");
		return result;
    }
	
	@Transactional
	private String getFIleFromUri(IssuedDocumentsResponse issuedDocument,String accessToken,String candidateCode,Color color, String action){
		System.out.println("------------------------------inside getFIleFromUri----------------");
		String[] parts = candidateCode.split("_");
		String inputAadhar=parts[0];
		candidateCode=parts[1];
	    System.out.println("----INPUT ADHAR----"+inputAadhar+"----separated candidate code-----"+candidateCode);
		System.out.println(issuedDocument.getDoctype()+"--------------gettype------------");
		log.info("DOC URI ::{}",issuedDocument.getUri());		
		if(StringUtils.isNoneBlank(accessToken)) {
			System.out.println("------------------------------inside if----------------");
			//DigilockerTokenResponse digilockerTokenResponse = new DigilockerTokenResponse();
			ResponseEntity<String> response = null;
			//System.out.println("digilockerTokenResponse ="+digilockerTokenResponse);
	        HttpHeaders headers = new HttpHeaders();
//	        headers.setContentType(issuedDocument.getDoctype().equals("LPGSV")?MediaType.APPLICATION_PDF:MediaType.APPLICATION_XML);
	        headers.setBearerAuth(accessToken);
	        HttpEntity<String> request = new HttpEntity<String>(headers);
			try {
				System.out.println("------------------------------inside try----------------");
				if(issuedDocument.getDoctype().equals("LPGSV") || issuedDocument.getDoctype().equals("UNCRD") ) {
					response = (new RestTemplate()).exchange(clientSecurityDetails.getUserFileFromUriAsPdf()+"/"+issuedDocument.getUri(), HttpMethod.GET, request, String.class);
					// System.out.println(response+"-------------------if-------------------response");
				}else {
					response = (new RestTemplate()).exchange(clientSecurityDetails.getUserFileFromUri()+"/"+issuedDocument.getUri(), HttpMethod.GET, request, String.class);
					// System.out.println(response+"-------------------else-------------------response");
				}
			  	Candidate candidate = candidateRepository.findByCandidateCode(candidateCode);
			  	String data = response.getBody();
			  	JSONObject json = new JSONObject();
			  	if(issuedDocument.getDoctype().equals("ADHAR")) {
//					System.out.println("-------------------ifaadhar-------------------response");
			  		if(action.equals("SELF")) {
//						System.out.println("-------------------ifself-------------------response");
 						CandidateCafAddress candidateCafAddress = candidateCafAddressRepository.findByCandidateCandidateCodeAndServiceSourceMasterServiceCode(candidateCode,"AADHARADDR");
				  		
				  			json = XML.toJSONObject(data);
				  			JSONObject cer = json.getJSONObject("Certificate");
							JSONObject cdata = cer.getJSONObject("CertificateData");
							JSONObject kyc = cdata.getJSONObject("KycRes");
					  		JSONObject uid = kyc.getJSONObject("UidData");
					  		JSONObject poa = uid.getJSONObject("Poa");
					  		JSONObject poi = uid.getJSONObject("Poi");
					  		CandidateCafAddress address = Objects.nonNull(candidateCafAddress) ? candidateCafAddress:new CandidateCafAddress();
					  		address.setCandidate(candidate);
					  		if(poa.has("pc"))
					  			address.setPinCode(Integer.parseInt(poa.get("pc").toString()));
					  		if(poa.has("state"))
					  			address.setState(poa.getString("state"));
							String candidateAddress = constructFullAddress(poa);
							address.setCandidateAddress(candidateAddress);//"+poa.getString("lm")+"
					  		address.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("AADHARADDR"));
					  		address.setColor(color);
					  		address.setCreatedOn(new Date());
					  		if(poi.has("name"))
					  			address.setName(poi.getString("name"));
					  		candidateCafAddressRepository.save(address);
					  	//Aadhar validation start 
					  		String adharFromDL= null;
					  		if(uid.has("uid"))
					  			adharFromDL = uid.getString("uid");
//					  		System.out.println("--------------adhar From DIGILOCKER in SELF------------"+adharFromDL);
					  	if(inputAadhar.substring(8, 12).equalsIgnoreCase(adharFromDL.substring(8, 12))){
//					  			System.out.println("-------AADHAR NUMBER MATCHED-------");
					  		    candidate.setAadharNumber(inputAadhar);
					  		if(poi.has("dob"))
					  			candidate.setAadharDob(poi.getString("dob"));
					  		if(poi.has("gender"))
					  			 candidate.setAadharGender(poi.getString("gender"));
					  		if(poi.has("name"))
					  			candidate.setAadharName(poi.getString("name"));
					  		if(poa.has("co"))
					  			candidate.setAadharFatherName(poa.getString("co"));
							// System.out.println(candidate+"--------------------------------------candidate");
					  		candidateRepository.save(candidate);
					  		CandidateIdItems item = new CandidateIdItems();
					  		item.setCandidate(candidate);
					  		item.setColor(color);
					  		if(uid.has("uid"))
					  			item.setIdNumber(uid.getString("uid"));
					  		if(poi.has("name"))
					  			item.setIdHolder(poi.getString("name"));
					  		if(poi.has("dob"))
					  			item.setIdHolderDob(poi.getString("dob"));
					  		item.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("AADHARID"));
					  		item.setCreatedOn(new Date());
							// System.out.println(item+"--------------------------------------item");
					  		candidateIdItemsRepository.save(item);
//							uploadFileToS3(ContentCategory.OTHERS, ContentSubCategory.AADHAR,issuedDocument,candidateCode,candidate.getCandidateId(),accessToken);
					  	}
		  		
			  		}else if(action.equals("RELATION")){
						// System.out.println(action+"--------------------------------------else if");
			  			json = XML.toJSONObject(data);
//			  			 log.info("Aadhar JSON::{}",json);
			  			JSONObject cer = null;
						JSONObject cdata = null;
						JSONObject kyc = null;
				  		JSONObject uid = null;
				  		JSONObject poa = null;
				  		JSONObject poi = null;
			  			 if(json.getJSONObject("Certificate")!=null &&
			  					json.getJSONObject("Certificate").getJSONObject("CertificateData")!=null &&
			  					json.getJSONObject("Certificate").getJSONObject("CertificateData").getJSONObject("KycRes")!=null &&
			  					json.getJSONObject("Certificate").getJSONObject("CertificateData").getJSONObject("KycRes").getJSONObject("UidData")!=null &&
			  					json.getJSONObject("Certificate").getJSONObject("CertificateData").getJSONObject("KycRes").getJSONObject("UidData").getJSONObject("Poa")!=null &&
			  					json.getJSONObject("Certificate").getJSONObject("CertificateData").getJSONObject("KycRes").getJSONObject("UidData").getJSONObject("Poi") !=null &&
			  							json.getJSONObject("Certificate").getJSONObject("CertificateData").getJSONObject("KycRes").getJSONObject("UidData").getJSONObject("Poi").has("name")) {

			  				 log.info("--------CANDIDATE HAVING ADHAR DATA RECORDS--------");
								 cer = json.getJSONObject("Certificate");
								 cdata = cer.getJSONObject("CertificateData");
								 kyc = cdata.getJSONObject("KycRes");
						  		 uid = kyc.getJSONObject("UidData");
						  		 poa = uid.getJSONObject("Poa");
						  		 poi = uid.getJSONObject("Poi");
			  			 }else {
			  				log.info("--------CANDIDATE NOT HAVING ADHAR DATA RECORDS--------");
			  				 return "FAILED";
			  			 }
//						JSONObject cer = json.getJSONObject("Certificate");
//						JSONObject cdata = cer.getJSONObject("CertificateData");
//						JSONObject kyc = cdata.getJSONObject("KycRes");
//				  		JSONObject uid = kyc.getJSONObject("UidData");
//				  		JSONObject poa = uid.getJSONObject("Poa");
//				  		JSONObject poi = uid.getJSONObject("Poi");
//				  		log.info("Aadhar POSTAL ADDRESS::{}",poa);
				  		
				  		CandidateAdressVerification verification = candidateAdressVerificationRepository.findByCandidateCandidateCode(candidateCode);
				  		CandidateCafAddress candidateCafAddress = candidateCafAddressRepository.findByCandidateCandidateCodeAndServiceSourceMasterServiceCode(candidateCode,"AADHARADDR");
				  		CandidateCafAddress address = Objects.nonNull(candidateCafAddress) ? candidateCafAddress:new CandidateCafAddress();
				  		//CandidateCafAddress address = new CandidateCafAddress();
				  		address.setCandidate(candidate);
				  		if(poa.has("pc"))
				  			address.setPinCode(Integer.parseInt(poa.get("pc").toString()));
				  		if(poa.has("state"))
				  			address.setState(poa.getString("state"));
						String candidateAddress = constructFullAddress(poa);
						
				  		address.setCandidateAddress(candidateAddress);//"+poa.getString("lm")+"
				  		address.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("AADHARADDR"));
				  		address.setColor(color);
				  		address.setCreatedOn(new Date());
				  		if(poi.has("name"))
				  			address.setName(poi.getString("name"));
				  		address.setAddressVerification(verification);
				  		candidateCafAddressRepository.save(address);
				  		
				  		//Aadhar validation start 
				  		String adharFromDL= null;
				  		if(uid.has("uid"))
				  			adharFromDL = uid.getString("uid");
//				  		System.out.println("--------------adhar From DIGILOCKER in RELATION------------"+adharFromDL);
				  		if(inputAadhar.substring(8, 12).equalsIgnoreCase(adharFromDL.substring(8, 12))){
//				  			System.out.println("-------AADHAR NUMBER MATCHED-------");
				  		    candidate.setAadharNumber(inputAadhar);
				  		    if(poi.has("dob"))
				  		    	candidate.setAadharDob(poi.getString("dob"));
				  		    if(poi.has("gender"))
				  		    	candidate.setAadharGender(poi.getString("gender"));
				  		    if(poi.has("name"))
				  		    	candidate.setAadharName(poi.getString("name"));
				  		    if(poa.has("co"))
				  		    	candidate.setAadharFatherName(poa.getString("co"));
							// System.out.println(candidate+"--------------------------------------candidate");
					  		candidateRepository.save(candidate);
					  		CandidateIdItems item = new CandidateIdItems();
					  		item.setCandidate(candidate);
					  		item.setColor(color);
					  		if(uid.has("uid"))
					  			item.setIdNumber(uid.getString("uid"));
					  		if(poi.has("name"))
					  			item.setIdHolder(poi.getString("name"));
					  		if(poi.has("dob"))
					  			item.setIdHolderDob(poi.getString("dob"));
					  		item.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("AADHARID"));
					  		item.setCreatedOn(new Date());
							// System.out.println(item+"--------------------------------------item");
					  		candidateIdItemsRepository.save(item);

				  		}else {
				  			log.info("INPUT ADHAR AND DIGILOCKER ADHAR IS NOT MATCHED..!");
				  			return "FAILED";
				  		}
			  		}
			  		log.info("START ADHAR uploadFileToS3 ");
			  		uploadFileToS3(ContentCategory.OTHERS, ContentSubCategory.AADHAR,issuedDocument,candidateCode,candidate.getCandidateId(),accessToken);

			  	}
			  	// if(issuedDocument.getDoctype().equals("PANCR") && action.equals("SELF")) {
				if(issuedDocument.getDoctype().equals("PANCR")) {
//					System.out.println(issuedDocument.getDoctype()+"--------------insidepan------------");
			  		json = XML.toJSONObject(data);
			  		JSONObject panData = json.getJSONObject("Certificate");
			  		Candidate candidateObj = null;
			  		if(panData.has("number"))
			  			candidateObj = candidateRepository.findByPanNumberAndCandidateCode(panData.getString("number"),candidateCode);
			  		if(candidateObj==null) {
			  			JSONObject issuedTo = panData.getJSONObject("IssuedTo");
				  		JSONObject dob = issuedTo.getJSONObject("Person");
				  		if(panData.has("number"))
				  			candidate.setPanNumber(panData.getString("number"));
				  		if(dob.has("dob"))
				  			candidate.setDateOfBirth(dob.getString("dob"));
				  		if(dob.has("dob"))
				  			candidate.setPanDob(dob.getString("dob"));
				  		if(dob.has("name"))
				  			candidate.setPanName(dob.getString("name"));
				  		candidateRepository.save(candidate);
				  		
				  		CandidateIdItems item = new CandidateIdItems();
				  		item.setCandidate(candidate);
				  		if(panData.has("number"))
				  			item.setIdNumber(panData.getString("number"));
				  		item.setColor(color);
				  		if(dob.has("name"))
				  			item.setIdHolder(dob.getString("name"));
				  		if(dob.has("dob"))
				  			item.setIdHolderDob(dob.getString("dob"));
				  		item.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("PAN"));
				  		item.setCreatedOn(new Date());
				  		candidateIdItemsRepository.save(item);
				  		
			  		}
					uploadFileToS3(ContentCategory.OTHERS, ContentSubCategory.PAN,issuedDocument,candidateCode,candidate.getCandidateId(),accessToken);
				 
				}
			  	if(issuedDocument.getDoctype().equals("LPGSV")) {
//			  		byte[] secretKey=clientSecurityDetails.getPassword().getBytes();
//			  		byte[] message = data.getBytes();
//			  		Mac mac = Mac.getInstance("HmacSHA256");
//			        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
//			        mac.init(secretKeySpec);
//			        byte[] hmacSha256 = mac.doFinal(message);
//			        String dataHex= String.format("Hex: %064x", new BigInteger(1, hmacSha256));
//			        System.out.println("dataHexa--->"+dataHex);
//			        byte[] base64HmacSha256 = Base64.decodeBase64(hmacSha256);
//			        System.out.println("Base64: " + base64HmacSha256);
//					FileOutputStream fos = new FileOutputStream(new File(rb.getString("GASBILL.UPLOAD") + candidateCode + ".pdf"));
//					fos.write(base64HmacSha256);
//					fos.close();
			  	  
			  	}
			  	
			  	if(issuedDocument.getDoctype().equals("DRVLC")) {
			  		
			  		//if(action.equals("SELF")) {
			  			json = XML.toJSONObject(data);
				  		JSONObject drvlcData = json.getJSONObject("Certificate");
				  		log.info("DRIVIG LICENCE CERTIFICATE FOR ::{}{}",candidateCode,drvlcData);
				  		JSONObject issuedTo = drvlcData.getJSONObject("IssuedTo");
			  			JSONObject person = issuedTo.getJSONObject("Person");
//			  			log.info("drivinng license person::{}",person);
			  			JSONObject drvAddress =null;
			  			if (person.has(ADDRESS) && person.get(ADDRESS) instanceof JSONObject) {
			  				drvAddress = person.getJSONObject(ADDRESS);
			  			}else {
			  				JSONArray drvAddressArray =person.getJSONArray(ADDRESS);
			  				drvAddress =(JSONObject) drvAddressArray.get(0);
			  			}
			  			
			  			CandidateCafAddress candidateCafAddress = candidateCafAddressRepository.findByCandidateCandidateCodeAndServiceSourceMasterServiceCode(candidateCode,"DLADDR");
				  		CandidateCafAddress address = Objects.nonNull(candidateCafAddress) ? candidateCafAddress:new CandidateCafAddress();
				  
				  	//	CandidateCafAddress address = new CandidateCafAddress();
				  		address.setCandidate(candidate);
				  		if(drvAddress.has("line1"))
				  			address.setCandidateAddress(drvAddress.getString("line1"));//"+poa.getString("lm")+"
				  		address.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("DLADDR"));
				  		address.setColor(color);
				  		if(person.has("name"))
				  			address.setName(person.getString("name"));
				  		address.setCreatedOn(new Date());
				  		candidateCafAddressRepository.save(address);
				  		
				  		CandidateIdItems item = new CandidateIdItems();
				  		item.setCandidate(candidate);
				  		item.setColor(color);
				  		if(drvlcData.has("number"))
				  			item.setIdNumber(drvlcData.getString("number"));
				  		item.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("DLID"));
				  		item.setCreatedOn(new Date());
				  		if(person.has("name"))
				  			item.setIdHolder(person.getString("name"));
				  		candidateIdItemsRepository.save(item);
					    uploadFileToS3(ContentCategory.OTHERS, ContentSubCategory.DRIVING_LICENSE,issuedDocument,candidateCode,candidate.getCandidateId(),accessToken);
				  
					//			  		}else {
//			  			
//			  			json = XML.toJSONObject(data);
//				  		JSONObject drvlcData = json.getJSONObject("Certificate");
//				  		JSONObject issuedTo = drvlcData.getJSONObject("IssuedTo");
//			  			JSONObject person = issuedTo.getJSONObject("Person");
//				  		JSONObject drvAddress = person.getJSONObject("Address");
//				  		
//				  		CandidateAdressVerification verification = candidateAdressVerificationRepository.findByCandidateCandidateCode(candidateCode);
//				  		
//				  		CandidateCafAddress address = new CandidateCafAddress();
//				  		address.setCandidate(candidate);
//				  		address.setCandidateAddress(drvAddress.getString("line1"));
//				  		address.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("DLADDR"));
//				  		address.setColor(color);
//				  		address.setCreatedOn(new Date());
//				  		address.setName(person.getString("name"));
//				  		address.setAddressVerification(verification);
//				  		candidateCafAddressRepository.save(address);
//			  		}
			  		
			  	}
			  	
			  	if(issuedDocument.getDoctype().equals("DGCER")){ //&& action.equals("SELF")) {
//					System.out.println(issuedDocument.getDoctype()+"--------------insidedegree------------");
			  		json = XML.toJSONObject(data);
//					System.out.println(json+"--------------json------------");
			  		CandidateCafEducation candidateCafEducation = candidateCafEducationRepository.findByCandidateAndQualificationCode(candidateCode,"BEBTECH");
//			  		CandidateCafEducation candidateCafEducation = new CandidateCafEducation();
			  		if(candidateCafEducation!=null) {
			  			log.info("candidateCafEducation already present for DEGREE");
			  		}else {
			  			candidateCafEducation = new CandidateCafEducation();
				  		JSONObject cerificate = json.getJSONObject("Certificate");
				  		log.info("DEGREE CERTIFICATE FOR ::{}{}",candidateCode,cerificate);
				  		JSONObject cerificateData = cerificate.getJSONObject("CertificateData");
						JSONObject examination = cerificateData.getJSONObject("Examination");
						Integer year = examination.has("year") ? examination.getInt("year") : null;
						String yearOfPassing =  Objects.nonNull(year) ? year.toString() : "";
				  		String course = cerificateData.getJSONObject("Course").getString("name");
				  		String courseName = cerificateData.getJSONObject("Course").getJSONObject("Streams").getJSONObject("Stream").getString("name");
				  		if(courseName.equals("") && cerificateData.getJSONObject("Course").has("name")
				  				&& !cerificateData.getJSONObject("Course").getString("name").equals("")) {
				  			courseName=cerificateData.getJSONObject("Course").getString("name");
				  		}
				  		String boardOrUniversityName = cerificate.getJSONObject("IssuedBy").getJSONObject("Organization").getString("name");
				  		String percentage= cerificateData.getJSONObject("Performance").getString("percentage");
						Object aObj = cerificateData.getJSONObject("Performance").get("cgpa");
						String cgpa=String.valueOf(aObj);
						Content content = uploadFileToS3(ContentCategory.EDUCATIONAL, ContentSubCategory.DEGREE_CERTIFICATE,
							issuedDocument, candidateCode,candidate.getCandidateId(), accessToken);
						if(Objects.nonNull(content)){
							candidateCafEducation.setContentId(content.getContentId());
						}
						candidateCafEducation.setCandidate(candidate);
				  		candidateCafEducation.setCandidateStatus(candidateStatusRepository.findByCandidateCandidateCode(candidate.getCandidateCode()));
						candidateCafEducation.setCreatedOn(new Date());
						candidateCafEducation.setIsHighestQualification(false);
						candidateCafEducation.setPercentage(cgpa.equals("")?percentage:cgpa);
						candidateCafEducation.setColor(colorRepository.findByColorCode("GREEN"));
						candidateCafEducation.setYearOfPassing(yearOfPassing);
						candidateCafEducation.setCourseName(courseName);
						candidateCafEducation.setBoardOrUniversityName(boardOrUniversityName);
						candidateCafEducation.setQualificationMaster(qualificationMasterRepository.findByQualificationCode("BEBTECH"));
						candidateCafEducation.setSuspectClgMaster(suspectClgMasterRepository.findById(0L).get());
						//added below line for setting the source of education
						candidateCafEducation.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("DIGILOCKER"));
						
						candidateCafEducationRepository.save(candidateCafEducation);
			  		}
				 
				}else if(issuedDocument.getDoctype().equals("UNCRD")) {// && action.equals("SELF")) {
					uploadFileToS3(ContentCategory.OTHERS, ContentSubCategory.UAN,issuedDocument,candidateCode,candidate.getCandidateId(),accessToken);
				}else if(issuedDocument.getDoctype().equals("PRVCR")) {
					log.info("START FETCHING PROVISIONAL DEGREE");	
					json = XML.toJSONObject(data);
					CandidateCafEducation candidateCafEducation = candidateCafEducationRepository.findByCandidateAndQualificationCode(candidateCode,"BEBTECH");
//					CandidateCafEducation candidateCafEducation = new CandidateCafEducation();
					if(candidateCafEducation!=null) {
			  			log.info("candidateCafEducation already present for PROVISIONAL DEGREE");
			  		}else {
			  			candidateCafEducation = new CandidateCafEducation();
				  		JSONObject cerificate = json.getJSONObject("Certificate");
				  		log.info("PROVISIONAL CERTIFICATE FOR ::{}{}",candidateCode,cerificate);
				  		JSONObject cerificateData = cerificate.getJSONObject("CertificateData");
						JSONObject examination = cerificateData.getJSONObject("Examination");
						Integer year = examination.has("year") ? examination.getInt("year") : null;
						String yearOfPassing =  Objects.nonNull(year) ? year.toString() : "";
	//			  		String course = cerificateData.getJSONObject("Course").getString("name");
				  		String courseName = cerificateData.getJSONObject("Course").getJSONObject("Streams").getJSONObject("Stream").getString("name");
				  		if(courseName.equals("") && cerificateData.getJSONObject("Course").has("name")
				  				&& !cerificateData.getJSONObject("Course").getString("name").equals("")) {
				  			courseName=cerificateData.getJSONObject("Course").getString("name");
				  		}
				  		String boardOrUniversityName = cerificate.getJSONObject("IssuedBy").getJSONObject("Organization").getString("name");
				  		String percentage= cerificateData.getJSONObject("Performance").getString("percentage");
						Object aObj = cerificateData.getJSONObject("Performance").get("cgpa");
						String cgpa=String.valueOf(aObj);
						Content content = uploadFileToS3(ContentCategory.EDUCATIONAL, ContentSubCategory.PROVISIONAL_DEGREE_CERTIFICATE,
							issuedDocument, candidateCode,candidate.getCandidateId(), accessToken);
						if(Objects.nonNull(content)){
							candidateCafEducation.setContentId(content.getContentId());
						}
						candidateCafEducation.setCandidate(candidate);
				  		candidateCafEducation.setCandidateStatus(candidateStatusRepository.findByCandidateCandidateCode(candidate.getCandidateCode()));
						candidateCafEducation.setCreatedOn(new Date());
						candidateCafEducation.setIsHighestQualification(false);
						candidateCafEducation.setPercentage(cgpa.equals("")?percentage:cgpa);
						candidateCafEducation.setColor(colorRepository.findByColorCode("GREEN"));
						candidateCafEducation.setYearOfPassing(yearOfPassing);
						candidateCafEducation.setCourseName(courseName);
						candidateCafEducation.setBoardOrUniversityName(boardOrUniversityName);
						candidateCafEducation.setQualificationMaster(qualificationMasterRepository.findByQualificationCode("BEBTECH"));
						candidateCafEducation.setSuspectClgMaster(suspectClgMasterRepository.findById(0L).get());
						//added below line for setting the source of education
						candidateCafEducation.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("DIGILOCKER"));
						
						candidateCafEducationRepository.save(candidateCafEducation);
			  		}
					log.info("END FETCHING PROVISIONAL DEGREE");
				}else if(issuedDocument.getDoctype().equals("SSCER")) {
					log.info("START FETCHING SSC");
					json = XML.toJSONObject(data);
					CandidateCafEducation candidateCafEducation = candidateCafEducationRepository.findByCandidateAndQualificationCode(candidateCode,"10TH");
//					CandidateCafEducation candidateCafEducation = new CandidateCafEducation();
					if(candidateCafEducation!=null) {
			  			log.info("candidateCafEducation already present for SSCER");
			  		}else {
			  			candidateCafEducation = new CandidateCafEducation();
				  		JSONObject cerificate = json.getJSONObject("Certificate");
				  		log.info("SSC CERTIFICATE FOR ::{}{}",candidateCode,cerificate);
				  		JSONObject cerificateData = cerificate.getJSONObject("CertificateData");
						JSONObject examination = cerificateData.getJSONObject("Examination");
						Integer year = examination.has("year") ? examination.getInt("year") : null;
						String courseName =examination.has("name") ? examination.getString("name") : null;
						if(courseName==null || courseName.equals("")) {
							
							courseName="Secondary Education Board Exam";
						}
						String yearOfPassing =  Objects.nonNull(year) ? year.toString() : "";
						String boardOrUniversityName = cerificate.getJSONObject("IssuedBy").getJSONObject("Organization").getString("name");
				  		Object percentage= null;
				  		Object aObj =null;
				  		if(cerificateData.has("Performance") ) {
				  			if(cerificateData.getJSONObject("Performance").has("percentage")) {
				  				percentage =cerificateData.getJSONObject("Performance").get("percentage");
				  			}else if(cerificateData.getJSONObject("Performance").has("result")) {
				  				percentage =cerificateData.getJSONObject("Performance").get("result");
				  			}
				  			
				  			if(cerificateData.getJSONObject("Performance").has("cgpa")) {
				  				aObj =cerificateData.getJSONObject("Performance").get("cgpa");
				  			}else if(cerificateData.getJSONObject("Performance").has("result")) {
				  				aObj =cerificateData.getJSONObject("Performance").get("result");
				  			}
				  		}else if(cerificateData.has("Result")) {
				  			percentage=cerificateData.getJSONObject("Result").get("gpaPoints");
				  			aObj=cerificateData.getJSONObject("Result").get("gpaPoints");
				  		}
						String cgpa=String.valueOf(aObj);
						Content content = uploadFileToS3(ContentCategory.EDUCATIONAL, ContentSubCategory.SSC_MARKSHEET,
							issuedDocument, candidateCode,candidate.getCandidateId(), accessToken);
						if(Objects.nonNull(content)){
							candidateCafEducation.setContentId(content.getContentId());
						}
						candidateCafEducation.setCandidate(candidate);
				  		candidateCafEducation.setCandidateStatus(candidateStatusRepository.findByCandidateCandidateCode(candidate.getCandidateCode()));
						candidateCafEducation.setCreatedOn(new Date());
						candidateCafEducation.setIsHighestQualification(false);
						candidateCafEducation.setPercentage(cgpa.equals("") && percentage !=null ?percentage.toString():cgpa);
						candidateCafEducation.setColor(colorRepository.findByColorCode("GREEN"));
						candidateCafEducation.setYearOfPassing(yearOfPassing);
						candidateCafEducation.setCourseName(courseName);
						candidateCafEducation.setBoardOrUniversityName(boardOrUniversityName);
						candidateCafEducation.setQualificationMaster(qualificationMasterRepository.findByQualificationCode("10TH"));
						candidateCafEducation.setSuspectClgMaster(suspectClgMasterRepository.findById(0L).get());
						//added below line for setting the source of education
						candidateCafEducation.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("DIGILOCKER"));
						
						candidateCafEducationRepository.save(candidateCafEducation);
			  		}
					log.info("END FETCHING SSC");
				}else if(issuedDocument.getDoctype().equals("HSCER")) {
					log.info("START FETCHING HSC");
					json = XML.toJSONObject(data);
					CandidateCafEducation candidateCafEducation = candidateCafEducationRepository.findByCandidateAndQualificationCode(candidateCode,"12TH");
//					CandidateCafEducation candidateCafEducation = new CandidateCafEducation();
					if(candidateCafEducation!=null) {
			  			log.info("candidateCafEducation already present for HSC::{}",candidateCode);
			  		}else {
			  			candidateCafEducation = new CandidateCafEducation();
				  		JSONObject cerificate = json.getJSONObject("Certificate");
				  		log.info("HSC CERTIFICATE FOR ::{}{}",candidateCode,cerificate);
				  		JSONObject cerificateData = cerificate.getJSONObject("CertificateData");
						JSONObject examination = cerificateData.getJSONObject("Examination");
						Integer year = examination.has("year") ? examination.getInt("year") : null;
						String courseName =examination.has("name") ? examination.getString("name") : null;
						if(courseName==null || courseName.equals("")) {
							
							courseName="Higher Secondary Education Board Exam";
						}
						String yearOfPassing =  Objects.nonNull(year) ? year.toString() : "";
						String boardOrUniversityName = cerificate.getJSONObject("IssuedBy").getJSONObject("Organization").getString("name");
						Object percentage= null;
				  		Object aObj =null;
				  		if(cerificateData.has("Performance") ) {
				  			if(cerificateData.getJSONObject("Performance").has("percentage")) {
				  				percentage =cerificateData.getJSONObject("Performance").get("percentage");
				  			}else if(cerificateData.getJSONObject("Performance").has("result")) {
				  				percentage =cerificateData.getJSONObject("Performance").get("result");
				  			}
				  			
				  			if(cerificateData.getJSONObject("Performance").has("cgpa")) {
				  				aObj =cerificateData.getJSONObject("Performance").get("cgpa");
				  			}else if(cerificateData.getJSONObject("Performance").has("result")) {
				  				aObj =cerificateData.getJSONObject("Performance").get("result");
				  			}
				  		}else if(cerificateData.has("Result")) {
				  			percentage=cerificateData.getJSONObject("Result").get("gpaPoints");
				  			aObj=cerificateData.getJSONObject("Result").get("gpaPoints");
				  		}
						String cgpa=String.valueOf(aObj);
						Content content = uploadFileToS3(ContentCategory.EDUCATIONAL, ContentSubCategory.HSC_MARKSHEET,
							issuedDocument, candidateCode,candidate.getCandidateId(), accessToken);
						if(Objects.nonNull(content)){
							candidateCafEducation.setContentId(content.getContentId());
						}
						candidateCafEducation.setCandidate(candidate);
				  		candidateCafEducation.setCandidateStatus(candidateStatusRepository.findByCandidateCandidateCode(candidate.getCandidateCode()));
						candidateCafEducation.setCreatedOn(new Date());
						candidateCafEducation.setIsHighestQualification(false);
						candidateCafEducation.setPercentage(cgpa.equals("") && percentage !=null ?percentage.toString():cgpa);
						candidateCafEducation.setColor(colorRepository.findByColorCode("GREEN"));
						candidateCafEducation.setYearOfPassing(yearOfPassing);
						candidateCafEducation.setCourseName(courseName);
						candidateCafEducation.setBoardOrUniversityName(boardOrUniversityName);
						candidateCafEducation.setQualificationMaster(qualificationMasterRepository.findByQualificationCode("12TH"));
						candidateCafEducation.setSuspectClgMaster(suspectClgMasterRepository.findById(0L).get());
						//added below line for setting the source of education
						candidateCafEducation.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("DIGILOCKER"));
						
						candidateCafEducationRepository.save(candidateCafEducation);
			  		}
					log.info("END FETCHING HSC");
				}
				// System.out.println(response+"getFIleFromUri");
			}catch(Exception e) {
			    log.error("Exception occured in DigilockerServiceImpl in getFIleFromUri method-->",e);
			    
			    return "FAILED";
			} 
//			if(response != null) {
//				System.out.println(response);
//			}
			return "SUCCESS";
		}else {
			return "";
		}
    }
	
	private String constructFullAddress(JSONObject poa) {

		Object aObj ;
		
		StringBuilder fullAddress = new
			StringBuilder("");
		if(poa.has("name")) {
			//fullAddress.append(poa.getString("name")+", ");
			String name = poa.optString("name", "");
			fullAddress.append(name+", ");
		}
		if(poa.has("co") && StringUtils.isNotEmpty(poa.getString("co"))) {
			//fullAddress.append(poa.getString("co")+", ");
			String co = poa.optString("co", "");
			fullAddress.append(co+", ");
		}
		if(poa.has(HOUSE)) { 
			aObj = poa.get(HOUSE);
		    String cgpach=String.valueOf(aObj);
			fullAddress.append(cgpach+", ");
		}else {
			fullAddress.append("");
		}
		if(poa.has("street")) {
			//fullAddress.append(poa.getString("street")+", ");
			String street = poa.optString("street", "");
			fullAddress.append(street+", ");
		}
		if(poa.has("lm")) {
			//fullAddress.append(poa.getString("lm")+", ");
			String lm = poa.optString("lm", "");
			fullAddress.append(lm+", ");
		}
		if(poa.has("loc")) {
			//fullAddress.append(poa.getString("loc")+", ");
			String loc = poa.optString("loc", "");
			fullAddress.append(loc+", ");
		}
		if(poa.has("vtc")) {
			//fullAddress.append(poa.getString("vtc")+", ");
			String vtc = poa.optString("vtc", "");
			fullAddress.append(vtc+", ");
		}
		if(poa.has("subdist")) {
			//fullAddress.append(poa.getString("subdist")+", ");
			String subdist = poa.optString("subdist", "");
			fullAddress.append(subdist+", ");
		}
		if(poa.has("dist")) {
			//fullAddress.append(poa.getString("dist")+", ");
			String dist = poa.optString("dist", "");
			fullAddress.append(dist+", ");
		}
		if(poa.has("state")) {
			//fullAddress.append(poa.getString("state")+", ");
			String state = poa.optString("state", "");
			fullAddress.append(state+", ");
		}
		if(poa.has("country")) {
			//fullAddress.append(poa.getString("country")+", ");
			String country = poa.optString("country", "");
			fullAddress.append(country+", ");
		}
		if(poa.has("pc")) {
			//fullAddress.append(poa.getInt("pc")+", ");
			String pc = poa.optString("pc", "");
			fullAddress.append(pc+", ");
		}
		if(poa.has("po")) {
			//fullAddress.append(poa.getString("po")+", ");
			String po = poa.optString("po", "");
			fullAddress.append(po+", ");
		}
		log.info("FULL ADDRESS AS PER ADHAR:: {} ",fullAddress.substring(0,fullAddress.length()-2));
		
		return fullAddress.substring(0,fullAddress.length()-2);
	}
	
	private Content uploadFileToS3(ContentCategory contentCategory, ContentSubCategory contentSubCategory,
		IssuedDocumentsResponse issuedDocument, String candidateCode,Long candidateId,String accessToken)throws IOException {
		File pdfFile = FileUtil.createUniqueTempFile(candidateCode, ".pdf");
		pdfFile = getPdfFromURI(issuedDocument, accessToken,
			pdfFile);
		 System.out.println("---------------------------inside uploadFileToS3");
		if(Objects.nonNull(pdfFile)){
			String path = "Candidate/".concat(
				candidateCode + "/Issued".concat("/").concat(issuedDocument.getName()).concat(".pdf"));
			awsUtils.uploadFile(DIGIVERIFIER_DOC_BUCKET_NAME, path,pdfFile);
			// pdfFile.delete();
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final InputStream in = new FileInputStream(pdfFile);
			final byte[] buffer = new byte[500];

			int read = -1;
			while ((read = in.read(buffer)) > 0) {
				baos.write(buffer, 0, read);
			}
			in.close();
			Content content = contentRepository
				.findByCandidateIdAndContentTypeAndContentCategoryAndContentSubCategory(candidateId,ContentType.ISSUED
					,contentCategory,contentSubCategory).orElse(new Content());
			content.setCandidateId(candidateId);
			content.setContentCategory(contentCategory);
			content.setContentSubCategory(contentSubCategory);
			content.setBucketName(DIGIVERIFIER_DOC_BUCKET_NAME);
			content.setPath(path);
			content.setDocument(baos.toByteArray());
			content.setContentType(ContentType.ISSUED);
			content.setFileType(FileType.PDF);
			// System.out.println(content+"----------------------content");
			contentRepository.save(content);
			return content;
		}
		return null;
	}
	
	private File getPdfFromURI(IssuedDocumentsResponse issuedDocument,String accessToken,File tempFile) {
		System.out.println("------------------------------inside private ------------");
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(accessToken);
			HttpEntity<String> request = new HttpEntity<String>(headers);
			ResponseEntity<byte[]> response = (new RestTemplate()).exchange(clientSecurityDetails.getUserFileFromUriAsPdf()+"/"+issuedDocument.getUri(), HttpMethod.GET, request, byte[].class);
			System.out.println(response.getStatusCode()+"--------------------responsestatuscode-------");
			if(response.getStatusCode().equals(HttpStatus.OK)){
				System.out.println("------------------------------privateif------------");
				Files.write(Paths.get(tempFile.getPath()), response.getBody());
				return tempFile;
			}else {
//				ResponseEntity<byte[]> response1 = (new RestTemplate()).exchange("https://api.digitallocker.gov.in/public/oauth2/3/xml/eaadhaar", HttpMethod.GET, request, byte[].class);
//				ResponseEntity<byte[]> response1 = (new RestTemplate()).exchange("https://api.digitallocker.gov.in/public/oauth2/1/xml"+"/"+issuedDocument.getUri(), HttpMethod.GET, request, byte[].class);
//
//				if(response1.getStatusCode().equals(HttpStatus.OK)){
//					System.out.println("----------------response1--------------privateif------------");
//					Files.write(Paths.get(tempFile.getPath()), response1.getBody());
//					return tempFile;
//				}else {
//					return null;
//				}
				return null;
			}
		}catch(Exception e){
			log.error("Exception occured in DigilockerServiceImpl in getFIleFromUri method-->",e);
			return null;
		}
	}

	@Override
	public ServiceOutcome<String> getDigiLockerAlldetail(DigiLockerDetailsDto digilockerDetails,HttpServletResponse res) {
		ServiceOutcome<String> outcome = new ServiceOutcome<>();
		ResponseEntity<String> response = null;
		String  tokenString="";
		String candidateCode="";
		String code="";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject request = new JSONObject();
		
		JSONArray epfo_param_c_array=new JSONArray();
		String message="";
		Boolean outcomeBoolean= false;
		try {
			System.out.println("____________________"+digilockerDetails);
			System.out.println("____________________"+digilockerDetails.getOtp());
			request.put(DigilockerConstants._DIGI_OTP ,digilockerDetails.getOtp());
							
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
//			System.out.println("\n------entity--------------"+entity);
//			System.out.println("\n------epfoDetails ------ new "+digilockerDetails.getTransactionid());
//			System.out.println("\n------epfoSecurityConfig ------ new "+clientSecurityDetails.getFinalSubmitPostOtp());
//			System.out.println("\n------epfoDetails ------ new "+digilockerDetails.getTransactionid());
			response = restTemplate.exchange(clientSecurityDetails.getFinalSubmitPostOtp()+digilockerDetails.getTransactionid(), HttpMethod.POST, entity, String.class);
			String responseBody=response.getBody();
			JSONObject obj = new JSONObject(responseBody);
			System.out.println("\n--------obj --------- new "+obj);
			String msg=obj.getString("message");
			Boolean status=obj.getBoolean("success");
//			System.out.println("\n--------obj --------- aadhar "+msg+"--------"+status);
//			System.out.println(responseBody+"--------------------------------digi-----karthika");
			if (responseBody!=null){
				outcome.setOutcome(status);
				outcome.setMessage(msg);
			}
			if(status==true){
				System.out.println(responseBody+"--------------------------------karthika");
				// System.out.println("Aadhaar details"+obj.getString("message")); 
				JSONObject aadhar=new JSONObject(obj.getString("message"));
//				System.out.println("\n--------obj --------- aadhar "+aadhar);
				JSONObject aadhardetails=aadhar.getJSONObject("aadhaar_details");
//				System.out.println("-----------------aadhar-------"+aadhardetails); 

				System.out.println("--------------------------------------------------------------------------------------------------------------"); 
				// System.out.println("digi_code"+aadhar.getString("digi_code")); 
				System.out.println("--------------------------------------------------------------------------------------------------------------");
				// System.out.println("digi_code"+aadhar.getString("access_token"));
				tokenString=aadhar.getString("access_token");
				String input_aadhar=aadhardetails.getString("input_aadhar");
//				System.out.println("-----------------aadhar"+input_aadhar); 
				code=aadhar.getString("digi_code");
				candidateCode=digilockerDetails.getCandidateCode();
				Candidate candidate = candidateRepository.findByCandidateCode(candidateCode);
				candidate.setAadharNumber(input_aadhar);
				candidateRepository.save(candidate);
				// System.out.println(candidateCode+"----------------------------------------candidateCode----------------------------------");
				// System.out.println("--------------------------------------------------------------------------------------------------------------");
				// Gson gson=new Gson();
				// digilockerTokenResponse =gson.fromJson(response, DigilockerTokenResponse.class);
				// System.out.println("DigilockerTokenResponse : "+digilockerTokenResponse.toString());
				// tokenString = digilockerTokenResponse.getAccess_token();
				// String res="response";
				outcome.setOutcome(status);
				outcome.setMessage(tokenString);
				String action="SELF";
				if(!tokenString.isEmpty()) {
					message = getUserDetails(tokenString,code,candidateCode,res,action);
					outcome.setData(message);
					outcome.setStatus(code);
				}
			}
			
			
		}catch (Exception ex) {
			outcome.setData(response!=null?response.getStatusCode().toString():"");
			outcome.setOutcome(outcomeBoolean);
			outcome.setMessage(UNABLE_TO_GET_DIGI_DETAILS);
			log.error("Exception occured in getEpfodetail:",ex); // Add the Proper logging Message here
		}

	
	System.out.println(outcome+"=========================otototorp===================");
	return outcome;
	
	}
	
	@Override
	public ServiceOutcome<String> getDigiLockerdetail(DigiLockerDetailsDto digilockerDetails) {
		ServiceOutcome<String> outcome = new ServiceOutcome<>();
		ResponseEntity<String> response = null;
		
		if(StringUtils.isNotEmpty(digilockerDetails.getCandidateCode()) && StringUtils.isNotEmpty(digilockerDetails.getAadhaar())) { //&& StringUtils.isNotEmpty(epfoDetails.getUanpassword())
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
	        JSONObject request = new JSONObject();
	        
			JSONArray epfo_param_c_array=new JSONArray();
			String message="";
			Boolean outcomeBoolean= false;
			try {
//				System.out.println("____________________epfoDetails.getUanusername() new "+digilockerDetails);
//				System.out.println("____________________epfoDetails.getUanusername() new "+digilockerDetails.getAadhaar());
				request.put(DigilockerConstants._DIGI_AADHAR ,digilockerDetails.getAadhaar());	
				request.put(DigilockerConstants._DIGI_SECURITY_PIN ,digilockerDetails.getSecuritypin());			
				HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
//				System.out.println("\n------entity--------------"+entity);
//				System.out.println("\n------epfoDetails ------ new "+digilockerDetails.getTransactionid());
//				System.out.println("\n------epfoSecurityConfig ------ new "+clientSecurityDetails.getFinalSubmitPostUrl());
//				System.out.println("\n------epfoDetails ------ new "+digilockerDetails.getTransactionid());
				response = restTemplate.exchange(clientSecurityDetails.getFinalSubmitPostUrl()+digilockerDetails.getTransactionid(), HttpMethod.POST, entity, String.class);
				String responseBody=response.getBody();
//				System.out.println("\n--------obj responseBody new "+responseBody);
				JSONObject obj = new JSONObject(responseBody);
//				System.out.println("\n--------obj --------- new "+obj);
				String msg=obj.getString("message");
				Boolean status=obj.getBoolean("success");
				System.out.println("\n--------obj --------- aadhar "+msg+"--------"+status);
				
				
				
				
				if (responseBody!=null){
					outcome.setMessage(msg);
					outcome.setOutcome(status);

				}
				
			}catch (Exception ex) {
				outcome.setData(response!=null?response.getStatusCode().toString():"");
				outcome.setOutcome(outcomeBoolean);
				outcome.setMessage(UNABLE_TO_GET_DIGI_DETAILS);
				log.error("Exception occured in getEpfodetail:",ex); // Add the Proper logging Message here
			}
	
		
		}
		return outcome;
	 
	}

	private HttpHeaders setHeaderDetails (HttpHeaders headers) {
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	/**
	 * 
	 * @param access_token
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	private DigiLockerDetailsDto getEPFOTransactionIdString(String access_token,String candidateId,DigiLockerDetailsDto digiDetails)throws JsonProcessingException, IOException{
		String transactionId = null;
		if(StringUtils.isNotEmpty(access_token)) {
			//ResponseEntity<Map<String, Object>> response = null;
			ResponseEntity<String> response = null;
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(access_token);
		    headers.add("Bearer", access_token);
	        HttpEntity<String> request = new HttpEntity<String>(headers);
	        try {
	        	ParameterizedTypeReference<Map<String, Object>> typeRef = new ParameterizedTypeReference<Map<String, Object>>() {};
	        	response = restTemplate.exchange(clientSecurityDetails.getTransactionIdUrl(), HttpMethod.GET, request, String.class);
			  	try {
					String message=response.getBody();
					log.info("Response from DIGI Transaction API : "+message);
					JSONObject obj = new JSONObject(message);
//					log.info("Response from DIGI Transaction API - obj: "+obj);
			  		transactionId=obj.getString("message").toString();
			  		digiDetails.setTransactionid(transactionId);
//					log.info("Generated transactionId Id is "+transactionId);
//					System.out.println("transaction id-->"+transactionId);
				} catch (JSONException e) {
					log.error("Json Exception occured..",e);
				}
			  	if(response.getStatusCode() == HttpStatus.OK) {
	        		log.info("Transaction ID Created Successfully. Response returned : ", transactionId);
					log.error("---getDIGITransactionIdString------digiDetails----------", digiDetails);
	        	}else if(response.getStatusCode() == HttpStatus.UNAUTHORIZED){
	        		log.error("User is Unauthorized");
	        	}else if(response.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT){
	        		log.error("Server response is slow, getting timeout");
	        	}else if(response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
	        		log.error("Server is down or Not responding at this Moment");
	        	}
			}catch(HttpClientErrorException e) {
			    log.error("HttpClientErrorException occured...", e);
			} catch(HttpServerErrorException ex) {
				log.error("HttpServerErrorException occured...", ex);
			}
		}else {
			log.error("transactionId could Not be generated.");
		}
		return digiDetails;
    }
	

	@Override
	public ServiceOutcome<DigiLockerDetailsDto> getDigiTansactionid(String candidateCode) {
		ResponseEntity<String> digiTokenResponse = null;
		HttpHeaders headers = new HttpHeaders();
        setHeaderDetails(headers);
        JSONObject request = new JSONObject();
        ServiceOutcome<DigiLockerDetailsDto> svcOutcome = new ServiceOutcome<DigiLockerDetailsDto>();
        try {
        	request.put(epfoSecurityConfig.getClientIdValue(),epfoSecurityConfig.getClientId());
			request.put(epfoSecurityConfig.getClientSecretValue(),epfoSecurityConfig.getClientSecret());
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
			System.out.println(entity+"---------------------------------");
			
			ParameterizedTypeReference<Map<String, Object>> typeRef = new ParameterizedTypeReference<Map<String, Object>>() {};
			System.out.println("getAccessTokenUrl *************************"+clientSecurityDetails.getNewAccessTokenUrl());
			digiTokenResponse = restTemplate.exchange(clientSecurityDetails.getNewAccessTokenUrl(), HttpMethod.POST, entity, String.class);
			log.info("Response from DIGI TOKEN API "+digiTokenResponse);
			String message=digiTokenResponse.getBody(); //.get("message").toString().replaceAll("=", ":")
			System.out.println("DigiTokenResponse  ************************* "+digiTokenResponse.getBody());
			JSONObject obj1 = new JSONObject(message);
//			log.info("Response from DIGI TOKEN API - message "+obj1);
			log.info("last message "+obj1.getJSONObject("message"));
			JSONObject obj = obj1.getJSONObject("message");
			String access_token = obj.getString("access_token");
    		System.out.println("access_token = "+access_token);
    		
    		// Call Next API
    		DigiLockerDetailsDto digiDetails = new DigiLockerDetailsDto();
    		DigiLockerDetailsDto digi = getEPFOTransactionIdString(access_token,candidateCode,digiDetails);
    		digi.setCandidateCode(candidateCode);
			System.out.print("\n"+access_token+"-------digiServicelmpl-------"+candidateCode);
			System.out.println("\n\ndigi in capache+++++++++++++++++++++++"+digi);
        	if(digiTokenResponse.getStatusCode() == HttpStatus.OK) {
        		String resMessageString = "Token Created Successfully. Response returned";
        		svcOutcome.setData(digi);
        		if(digi.getErrorMessage()==null) {
        			svcOutcome.setOutcome(true);
        			svcOutcome.setMessage(resMessageString);
        		}else {
        			svcOutcome.setOutcome(false);
        			svcOutcome.setMessage(digi.getErrorMessage());
        		}
        	}else if(digiTokenResponse.getStatusCode() == HttpStatus.UNAUTHORIZED){
        		svcOutcome.setData(null);
        		svcOutcome.setMessage("User is Unauthorized");
        		svcOutcome.setOutcome(false);
        	}else if(digiTokenResponse.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT){
        		svcOutcome.setData(null);
        		svcOutcome.setMessage("Server response is slow, getting timeout");
        		svcOutcome.setOutcome(false);
        	}else if(digiTokenResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
        		svcOutcome.setData(null);
        		svcOutcome.setMessage("Server is down or Not responding at this Moment");
        		svcOutcome.setOutcome(false);
        	}
        	
        	
		} 
			catch(Exception ex) {
			svcOutcome.setData(null);
    		svcOutcome.setMessage("Digi site is down, Please try after 7 PM or late night, If you don’t have UAN skip and complete the verification.");
    		svcOutcome.setOutcome(false);
			log.error("HttpServerErrorException occured....", ex);	// Add the Proper logging Message here
		} 
		return svcOutcome;
	}

	@Override
	public ServiceOutcome<Boolean> getDLEdudocument(String digidetails,HttpServletResponse res) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<>();
//		System.out.println(digidetails+"digidetails" );
		JSONObject digiInputparams = new JSONObject(digidetails);
		JSONObject finaldigiInputs = new JSONObject();
		String orgid=digiInputparams.get("orgid").toString();
		finaldigiInputs.put("orgid",orgid);
		String doctype=digiInputparams.get("doctype").toString();
		finaldigiInputs.put("doctype",doctype);
		String consent="Y";
		finaldigiInputs.put("consent",consent);
		String param1key=digiInputparams.get("param1key").toString();
		String param1=digiInputparams.get("param1").toString();
		finaldigiInputs.put(param1key,param1);
		if(digiInputparams.get("param2key")== null){
			System.out.println(digiInputparams.get("param2key")+"is empy");
		}
		else{
			String param2key=digiInputparams.get("param2key").toString();
			String param2=digiInputparams.get("param2").toString();
			finaldigiInputs.put(param2key,param2);
		}
		if(digiInputparams.get("param3key")== null){
			
			System.out.println((digiInputparams.get("param3key"))+"is empty");
		}
		else{
			String param3key=digiInputparams.get("param3key").toString();
			String param3=digiInputparams.get("param3").toString();
			finaldigiInputs.put(param3key,param3);
		}
		if(digiInputparams.get("param4key")== null){
		
		System.out.println((digiInputparams.get("param4key"))+"is empty");
		}
		else{
			String param4key=digiInputparams.get("param4key").toString();
			String param4=digiInputparams.get("param4").toString();
			finaldigiInputs.put(param4key,param4);
		}
		if(digiInputparams.get("param5key")== null){
		
		System.out.println((digiInputparams.get("param5key"))+"is empty");
	}
		else{
			String param5key=digiInputparams.get("param5key").toString();
			String param5=digiInputparams.get("param5").toString();
			finaldigiInputs.put(param5key,param5);
		}
		if(digiInputparams.get("param6key")== null){
		
		System.out.println((digiInputparams.get("param6key"))+"is empty");
		}
		else{
			String param6key=digiInputparams.get("param6key").toString();
			String param6=digiInputparams.get("param6").toString();
			finaldigiInputs.put(param6key,param6);
		}
		if(digiInputparams.get("param7key")== null){
		
		System.out.println((digiInputparams.get("param7key"))+"is empty");
		}
		else{
			String param7key=digiInputparams.get("param7key").toString();
			String param7=digiInputparams.get("param7").toString();
			finaldigiInputs.put(param7key,param7);
		}
		
		// finaldigiInputs.put("token",orgid);
		System.out.println(finaldigiInputs +"-------------");
		String token=digiInputparams.get("accesstoken").toString();
		System.out.println(token +"-------------");
		
		ResponseEntity<String> response = null;
		HttpHeaders headers = new HttpHeaders();	
		headers.setContentType(MediaType.APPLICATION_JSON);		
		HttpEntity<String> entity = new HttpEntity<String>(finaldigiInputs.toString(), headers);
		System.out.println("\n------entity--------------"+entity);
		System.out.println("\n------url--------------"+clientSecurityDetails.getdocumrntSubmitPostUrl());
		response = restTemplate.exchange(clientSecurityDetails.getdocumrntSubmitPostUrl()+token, HttpMethod.POST, entity, String.class);
		System.out.println("\n------response--------------"+response);
		String responseBody=response.getBody();
		System.out.println("\n------responseBody--------------"+responseBody);
		String code=digiInputparams.get("code").toString();
		String candidatecode=digiInputparams.get("candidatecode").toString();
		System.out.println(candidatecode+"candidatecode");
		// HttpServletResponse res="ertyuuu";
		boolean errorvalue = responseBody.contains("Error:"); 
      
   		if (errorvalue == false) {
		
			getUserDetails(token,code,candidatecode,res,"SELF");
		}
		else{
			svcSearchResult.setMessage(responseBody);
			svcSearchResult.setOutcome(false);
		}
		System.out.println(svcSearchResult+"svcSearchResult");
		return svcSearchResult;

	}
	

}
