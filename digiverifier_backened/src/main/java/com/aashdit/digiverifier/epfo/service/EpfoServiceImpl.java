package com.aashdit.digiverifier.epfo.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.aashdit.digiverifier.config.candidate.dto.BulkUanDTO;
import com.aashdit.digiverifier.config.candidate.dto.CandidateDetailsDto;
import com.aashdit.digiverifier.config.candidate.dto.EpfoDto;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.CandidateCafExperience;
import com.aashdit.digiverifier.epfo.model.CandidateEPFOResponse;
import com.aashdit.digiverifier.epfo.model.EpfoData;
import com.aashdit.digiverifier.epfo.repository.CandidateEPFOResponseRepository;
import com.aashdit.digiverifier.epfo.repository.EpfoDataRepository;
import com.aashdit.digiverifier.globalConfig.EnvironmentVal;
import com.aashdit.digiverifier.utils.DateUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.aashdit.digiverifier.client.securityDetails.EPFOSecurityConfig;
import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCafExperienceRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.StatusMasterRepository;
import com.aashdit.digiverifier.config.candidate.service.CandidateService;
import com.aashdit.digiverifier.config.superadmin.repository.ColorRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ServiceSourceMasterRepository;
import com.aashdit.digiverifier.constants.EPFOConstants;
import com.aashdit.digiverifier.epfo.dto.EpfoDataFromApiDto;
import com.aashdit.digiverifier.epfo.dto.EpfoDetailsDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class EpfoServiceImpl implements EpfoService {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private EPFOSecurityConfig epfoSecurityConfig;
	
	@Autowired
	private CandidateRepository candidateRepository;
	
	@Autowired
	private CandidateCafExperienceRepository candidateCafExperienceRepository;
	
	@Autowired
	private ServiceSourceMasterRepository serviceSourceMasterRepository;
	
	@Autowired
	private CandidateStatusRepository candidateStatusRepository;
	
	@Autowired
	private StatusMasterRepository statusMasterRepository;
	
	@Autowired
	private ColorRepository colorRepository;
	
	@Autowired
	private CandidateService candidateService;
	
	@Autowired
	private CandidateEPFOResponseRepository candidateEPFOResponseRepository;
	
	@Autowired @Lazy
	private EpfoDataRepository epfoDataRepository;
	
	@Autowired
	private EnvironmentVal environmentVal;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	ResourceBundle rb = ResourceBundle.getBundle("application");
	
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
	public ServiceOutcome<EpfoDetailsDto> getEpfoCaptcha(String candidateCode) {
		// ResponseEntity<Map<String, Object>> epfoTokenResponse = null;
		ResponseEntity<String> epfoTokenResponse = null;
		HttpHeaders headers = new HttpHeaders();
        setHeaderDetails(headers);
        JSONObject request = new JSONObject();
        ServiceOutcome<EpfoDetailsDto> svcOutcome = new ServiceOutcome<EpfoDetailsDto>();
        try {
        	request.put(epfoSecurityConfig.getClientIdValue(),epfoSecurityConfig.getClientId());
			request.put(epfoSecurityConfig.getClientSecretValue(),epfoSecurityConfig.getClientSecret());
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
			
			ParameterizedTypeReference<Map<String, Object>> typeRef = new ParameterizedTypeReference<Map<String, Object>>() {};
			System.out.println("epfoSecurityConfig.getAccessTokenUrl() *************************"+epfoSecurityConfig.getAccessTokenUrl());
			epfoTokenResponse = restTemplate.exchange(epfoSecurityConfig.getAccessTokenUrl(), HttpMethod.POST, entity, String.class);
			log.info("Response from EPFO TOKEN API "+candidateCode+epfoTokenResponse);
			String message=epfoTokenResponse.getBody(); //.get("message").toString().replaceAll("=", ":")
			System.out.println("epfoTokenResponse  ************************* "+epfoTokenResponse.getBody());
			JSONObject obj1 = new JSONObject(message);
			log.info("Response from EPFO TOKEN API - message "+candidateCode+obj1);
			log.info("last message "+candidateCode+obj1.getJSONObject("message"));
			JSONObject obj = obj1.getJSONObject("message");
			String access_token = obj.getString("access_token");
    		System.out.println("access_token = "+access_token);
    		
    		// Call Next API
    		EpfoDetailsDto epfoDetails = new EpfoDetailsDto();
    		EpfoDetailsDto epfo = getEPFOTransactionIdString(access_token,candidateCode,epfoDetails);
    		epfo.setCandidateCode(candidateCode);
			System.out.print("\n"+access_token+"-------EpfoServicelmpl-------"+candidateCode);
			System.out.println("\n\nepfo in capache+++++++++++++++++++++++"+epfo);
        	if(epfoTokenResponse.getStatusCode() == HttpStatus.OK) {
        		String resMessageString = "Token Created Successfully. Response returned";
        		svcOutcome.setData(epfo);
        		if(epfo.getErrorMessage()==null) {
        			svcOutcome.setOutcome(true);
        			svcOutcome.setMessage(resMessageString);
        		}else {
        			svcOutcome.setOutcome(false);
        			svcOutcome.setMessage(epfo.getErrorMessage());
        		}
        	}else if(epfoTokenResponse.getStatusCode() == HttpStatus.UNAUTHORIZED){
        		svcOutcome.setData(null);
        		svcOutcome.setMessage("User is Unauthorized");
        		svcOutcome.setOutcome(false);
        	}else if(epfoTokenResponse.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT){
        		svcOutcome.setData(null);
        		svcOutcome.setMessage("Server response is slow, getting timeout");
        		svcOutcome.setOutcome(false);
        	}else if(epfoTokenResponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
        		svcOutcome.setData(null);
        		svcOutcome.setMessage("Server is down or Not responding at this Moment");
        		svcOutcome.setOutcome(false);
        	}
        	
        	
		} /*
			 * catch (JSONException jsn) {
			 * log.error("Json Exception occured., check the Json received ", jsn); // Add
			 * the Proper logging Message here svcOutcome.setData(null);
			 * svcOutcome.setMessage("Json Exception occured., check the Json received");
			 * svcOutcome.setOutcome(false); }catch(HttpClientErrorException e) {
			 * svcOutcome.setData(null);
			 * svcOutcome.setMessage("HttpClientErrorException occured...");
			 * svcOutcome.setOutcome(false);
			 * log.error("HttpClientErrorException occured...", e); // Add the Proper
			 * logging Message here }
			 */ catch(Exception ex) {
			svcOutcome.setData(null);
    		svcOutcome.setMessage("EPFO site is down, Please try after 7 PM or late night, If you don’t have UAN skip and complete the verification.");
    		svcOutcome.setOutcome(false);
			log.error("HttpServerErrorException occured...."+candidateCode, ex);	// Add the Proper logging Message here
		} /*
			 * catch (JsonProcessingException ex) { svcOutcome.setData(null);
			 * svcOutcome.setMessage("JsonProcessingException occured...");
			 * svcOutcome.setOutcome(false); log.error("JsonProcessingException occured...",
			 * ex); // Add the Proper logging Message here } catch (IOException ex) {
			 * svcOutcome.setData(null); svcOutcome.setMessage("IOException occured...");
			 * svcOutcome.setOutcome(false); log.error("IOException occured...", ex); // Add
			 * the Proper logging Message here }
			 */
		return svcOutcome;
	}
	
	/**
	 * 
	 * @param access_token
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	private EpfoDetailsDto getEPFOTransactionIdString(String access_token,String candidateId,EpfoDetailsDto epfoDetails)throws JsonProcessingException, IOException{
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
	        	response = restTemplate.exchange(epfoSecurityConfig.getTransactionIdUrl(), HttpMethod.GET, request, String.class);
			  	try {
					String message=response.getBody();
					log.info("Response from EPFO Transaction API : "+candidateId+message);
					JSONObject obj = new JSONObject(message);
					log.info("Response from EPFO Transaction API - obj: "+candidateId+obj);
			  		transactionId=obj.getString("message").toString();
			  		epfoDetails.setTransactionid(transactionId);
					log.info("Generated transactionId Id is "+candidateId+transactionId);
					System.out.println("transaction id-->"+transactionId);
				} catch (JSONException e) {
					log.error("Json Exception occured..",e);
				}
			  	if(response.getStatusCode() == HttpStatus.OK) {
	        		log.info("Transaction ID Created Successfully. Response returned : "+candidateId, transactionId);
	        		epfoDetails = generateCaptchaImageString(transactionId, candidateId,epfoDetails);
					log.error("---getEPFOTransactionIdString------epfoDetails----------"+candidateId, epfoDetails);
	        	}else if(response.getStatusCode() == HttpStatus.UNAUTHORIZED){
	        		log.error("User is Unauthorized"+candidateId);
	        	}else if(response.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT){
	        		log.error("Server response is slow, getting timeout"+candidateId);
	        	}else if(response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
	        		log.error("Server is down or Not responding at this Moment"+candidateId);
	        	}
			}catch(HttpClientErrorException e) {
			    log.error("HttpClientErrorException occured..."+candidateId, e);
			} catch(HttpServerErrorException ex) {
				log.error("HttpServerErrorException occured..."+candidateId, ex);
			}
		}else {
			log.error("transactionId could Not be generated."+candidateId);
		}
		return epfoDetails;
    }
	
	/**
	 * 
	 * @param transactionId
	 * @return
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	private EpfoDetailsDto generateCaptchaImageString(String transactionId,String candidateId,EpfoDetailsDto epfoDetails)throws JsonProcessingException, IOException{
		if(StringUtils.isNotEmpty(transactionId)) {
			ResponseEntity<String> response = null;
			
			HttpHeaders headers = new HttpHeaders();
			setHeaderDetails(headers);
			JSONObject request = new JSONObject();
			try {
				HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
				System.out.println("epfoSecurityConfig.getLoginPageSessionUrl() **********generateCaptchaImageString**********"+candidateId+epfoSecurityConfig.getLoginPageSessionUrl()+transactionId);
				response = restTemplate.exchange(epfoSecurityConfig.getLoginPageSessionUrl()+transactionId, HttpMethod.GET, entity, String.class);
				
				String message=response.getBody();
				JSONObject obj = new JSONObject(message);

				System.out.println("obj***************************"+obj);
				if(obj.getString("code").equals("fail")) {
					epfoDetails.setErrorMessage(obj.getString("message"));
				}else {
					String epfo_im = obj.getJSONObject("message").getString("epfo-im");
		    		log.info("epfo-im String received : "+candidateId+epfo_im);
		    		if(StringUtils.isNotEmpty(epfo_im)) {
		    			epfoDetails.setCaptcha(epfo_im);
		    			
		    			// generateCaptchaImage(epfo_im,candidateId,epfoDetails);
		    		}
				}
			}catch (JSONException e) {
				log.error("JSONException occured...."+candidateId, e);
			}
		}else {
			log.error("transactionId Id received as Blank Or Null "+candidateId);
		}
		return epfoDetails;
	}
	

	/**
	 * 
	 * @param captchaBase64String
	 */
	private EpfoDetailsDto generateCaptchaImage(String captchaBase64String,String candidateId, EpfoDetailsDto epfoDetails) {
		try {
			byte[] base64Val=convertToImg(captchaBase64String);
			String filepath = createDirectory();
			writeByteToImageFile(base64Val, filepath+File.separator+candidateId+".png");
			//epfoDetails.setCaptcha(filepath+File.separator+candidateId+".png");
			
		} catch (IOException e) {
			log.error("IOException occured during generating Captcha Image"+candidateId, e);
		}
		return epfoDetails;
	}
	
	
	/**
	 * 
	 * @return filePath
	 */
	private String createDirectory() {
		String filePath=null;
		File file = new File(rb.getString("EPFO.IMAGE.PATH")); // once it moves to server, change it to canonical Path
	    if (!file.exists()) {
	        if (file.mkdirs()) {
	           log.debug("Directory is created!");
	           filePath=file.getAbsolutePath();
	        } else {
	        	log.debug("Failed to create directory!");
	        }
	    }else {
	    	 filePath=file.getAbsolutePath();
	    }
	    return filePath;
	}

	/**
	 * 
	 * @param imgBytes
	 * @param imgFileName
	 * @throws IOException
	 */
	private static void writeByteToImageFile(byte[] imgBytes, String imgFileName) throws IOException {  
         File imgFile = new File(imgFileName);  
         BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgBytes));  
         ImageIO.write(img, "png", imgFile);  
    } 
	
	/**
	 * 
	 * @param base64
	 * @return
	 * @throws IOException
	 */
	private static byte[] convertToImg(String base64) throws IOException {  
         return Base64.decodeBase64(base64);  
    }

	@SuppressWarnings({ "rawtypes", "unchecked"})
	@Override
	public ServiceOutcome<String> getEpfodetail(EpfoDetailsDto epfoDetails) {
		ServiceOutcome<String> outcome = new ServiceOutcome<>();
		ResponseEntity<String> response = null;
		
		// && StringUtils.isNotEmpty(epfoDetails.getCaptcha())
		if(StringUtils.isNotEmpty(epfoDetails.getCandidateCode()) && StringUtils.isNotEmpty(epfoDetails.getUanusername())) { //&& StringUtils.isNotEmpty(epfoDetails.getUanpassword())
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
	        JSONObject request = new JSONObject();
	        
			JSONArray epfo_param_c_array=new JSONArray();
			String message="";
			Boolean outcomeBoolean= false;
			
	        try {
				System.out.println("____________________epfoDetails.getUanusername()"+epfoDetails);
	        	request.put(EPFOConstants.EPFO_USR,epfoDetails.getUanusername());
				
				request.put(EPFOConstants.EPFO_PWD,' ');
				request.put(EPFOConstants.EPFO_UAN,epfoDetails.getUanusername());
				//request.put(EPFOConstants.EPFO_PARAM_H,"string");
//				epfo_param_c_array.put(
//						new JSONObject()
//							.put(EPFOConstants.EPFO_DOMAIN, "string")
//							.put(EPFOConstants.EPFO_HTTP_ONLY,"true")
//							.put(EPFOConstants.EPFO_NAME, "string")
//							.put(EPFOConstants.EPFO_PATH, "string")
//							.put(EPFOConstants.EPFO_SECURE, "true")
//							.put(EPFOConstants.EPFO_VALUE, "string")
//						);
//				request.put(EPFOConstants.EPFO_PARAM_C,epfo_param_c_array);
//				request.put(EPFOConstants.EPFO_PARAM_CH,"string");
//				request.put(EPFOConstants.EPFO_PARAM_E,"string");
//				request.put(EPFOConstants.EPFO_PARAM_L,"string");
				
				HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
				System.out.println("\n------epfoSecurityConfig ------ "+epfoSecurityConfig.getFinalSubmitPostUrl());
				System.out.println("\n------epfoDetails ------ "+epfoDetails.getTransactionid());
				response = restTemplate.exchange(epfoSecurityConfig.getFinalSubmitPostUrl()+epfoDetails.getTransactionid(), HttpMethod.POST, entity, String.class);
				String responseBody=response.getBody();
				JSONObject obj = new JSONObject(responseBody);
				JSONArray messagee = obj.getBoolean("success") ? obj.getJSONArray("message") : new JSONArray();
				System.out.println("\n--------obj --------- "+epfoDetails.getCandidateCode()+obj);
				ServiceOutcome<CandidateDetailsDto> candidateByCandidateCode = candidateService.getCandidateByCandidateCode(
					epfoDetails.getCandidateCode());
				System.out.println("\n--------candidateByCandidateCode --------- "+candidateByCandidateCode);
				String resMsg = obj.toString();
				System.out.println("\n--------resMsg --------- "+resMsg);
				System.out.println("\n--------resMsg --------- "+epfoDetails.getUanusername());
				System.out.println("\n--------resMsg --------- "+candidateByCandidateCode.getData().getCandidateId());
				CandidateEPFOResponse candidateEPFOResponse = candidateEPFOResponseRepository
					.findByCandidateIdAndUan(candidateByCandidateCode.getData().getCandidateId(),epfoDetails.getUanusername())
					.orElse(new CandidateEPFOResponse());
				System.out.println("\n--------message --------- "+messagee);
				for(int i=0; i<messagee.length();i++) {
					JSONObject object = messagee.getJSONObject(i);
					if(object.length()!=0) {
						candidateEPFOResponse.setUanName (object.getString("name"));
					}
				}
				candidateEPFOResponse.setEPFOResponse(resMsg);
				candidateEPFOResponse.setUan(epfoDetails.getUanusername());
				candidateEPFOResponse.setCandidateId(candidateByCandidateCode.getData().getCandidateId());
				candidateEPFOResponse.setCreatedOn(new Date());
				candidateEPFOResponse.setLastUpdatedOn(new Date());
				System.out.println("\n____________________before candidateEPFOResponse"+candidateByCandidateCode.getData().getCandidateId()+candidateEPFOResponse);
				candidateEPFOResponseRepository.save(candidateEPFOResponse);
				System.out.println("\n____________________after candidateEPFOResponse "+candidateByCandidateCode.getData().getCandidateId()+candidateEPFOResponse);
				if(!obj.getString("code").equals("fail")){
					JSONArray epfoData = obj.getJSONArray("message");
			        final ObjectMapper objectMapper = new ObjectMapper();
			        EpfoDataFromApiDto[] epfoDataFromApiDto = objectMapper.readValue(epfoData.toString(), EpfoDataFromApiDto[].class);
			        List<EpfoDataFromApiDto> epfoList = new ArrayList(Arrays.asList(epfoDataFromApiDto));
					
			        List<EpfoData> epfoExperiencesList = new ArrayList<>();
			        if(epfoList !=null && epfoList.size()>0) {
		        		log.debug("Post Login Information retrieved successfully"+candidateByCandidateCode.getData().getCandidateId());
		        		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						Candidate candidate = candidateRepository.findByCandidateCode(
							epfoDetails.getCandidateCode());
						for(EpfoDataFromApiDto epfo : epfoList) {
						
//		        			CandidateCafExperience candidateCafExperience = new CandidateCafExperience();
//		        			candidateCafExperience.setUan(epfo.getUan());
//		        			candidateCafExperience.setCandidateEmployerName(epfo.getCompany());
//		        			candidateCafExperience.setOutputDateOfJoining(sdf.parse(epfo.getDoj()));
//		        			candidateCafExperience.setInputDateOfJoining(sdf.parse(epfo.getDoj()));
//		        			candidateCafExperience.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("EPFO"));
//		        			candidateCafExperience.setOutputDateOfExit(!epfo.getDoe().equals("NOT_AVAILABLE")?sdf.parse(epfo.getDoe()):null);
//		        			candidateCafExperience.setInputDateOfExit(!epfo.getDoe().equals("NOT_AVAILABLE")?sdf.parse(epfo.getDoe()):null);
//		        			candidateCafExperience.setCandidate(candidateRepository.findByCandidateCode(epfoDetails.getCandidateCode()));
//		        			candidateCafExperience.setCreatedOn(new Date());
//		        			candidateCafExperience.setColor(colorRepository.findByColorCode("GREEN"));
							EpfoData epfoData1 = new EpfoData();
							epfoData1.setCandidate(candidate);
							epfoData1.setCompany(epfo.getCompany());
							epfoData1.setName(epfo.getName());
							epfoData1.setUan(epfo.getUan());
							epfoData1.setDoj(DateUtil.getDate(epfo.getDoj(),"dd/MM/yyyy"));
							epfoData1.setDoe(DateUtil.getDate(epfo.getDoe(),"dd/MM/yyyy"));
							
							//adding member id(New column)
							epfoData1.setMemberId(epfo.getMemberId());
							
							epfoExperiencesList.add(epfoData1);
							
		        		}
		        		
		        		if(epfoExperiencesList!=null && epfoExperiencesList.size()>0) {
		        			
		        			List<EpfoData> existingEpfoList = epfoDataRepository.findAllByCandidateIdAndUan(candidateEPFOResponse.getCandidateId(), candidateEPFOResponse.getUan());
		        			if(existingEpfoList.size() > 0) {
		        				existingEpfoList.forEach(temp -> epfoDataRepository.deleteById(temp.getEpfoId()));
		        				log.info("Epfo Details deleted for {}{}",candidateEPFOResponse.getCandidateId(), candidateEPFOResponse.getUan());
		        			}
		        				
		        			
		        			epfoDataRepository.saveAll(epfoExperiencesList);
		        			
		        			if(epfoDetails.isUanSearch() || epfoDetails.isEnterUanInQcPending()) {
		        				
		        				System.out.println("ByPASS"+candidateByCandidateCode.getData().getCandidateId());
		        			}
		        			else {
		        				CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(epfoDetails.getCandidateCode());
			        			candidateStatus.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("EPFO"));
			        			candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("EPFO"));
			        			candidateStatus.setLastUpdatedOn(new Date());
			        			candidateStatusRepository.save(candidateStatus);
								if(candidateStatus.getCandidate().getOrganization().getCallBackUrl() != null)
									candidateService.postStatusToOrganization(candidateStatus.getCandidate().getCandidateCode());
			        			candidateService.createCandidateStatusHistory(candidateStatus,"CANDIDATE");
		        			}
		        			
		        			if(epfoDetails.isEnterUanInQcPending()) {
		        				candidateService.updateCandidateExperienceDetails(epfoDetails.getCandidateCode());
		        			}
		        			
		        			outcomeBoolean=true;
		        			
		        		}else {
		        			message = "Unable to save epfo details";
			        		outcomeBoolean=false;
		        		}
			        }
		        
	        	}else if(response.getStatusCode() == HttpStatus.OK && obj.getString("code").equals("fail")) {
	        		message = obj.getString("message");
	        		if(message.equals("Epfo site is Busy,pls make the request again")) {
	        			message = "EPFO site is down, Please try after 7 PM or late night, If you don’t have UAN skip and complete the verification.";
	        		}
	        		outcomeBoolean=false;
	        	}else if(response.getStatusCode() == HttpStatus.UNAUTHORIZED){
	        		log.error("User is Unauthorized"+candidateByCandidateCode.getData().getCandidateId());
	        		message = "User is Unauthorized";
	        		outcomeBoolean=false;
	        	}else if(response.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT){
	        		log.error("Server response is slow, getting timeout"+candidateByCandidateCode.getData().getCandidateId());
	        		message = "Server response is slow, getting timeout";
	        		outcomeBoolean=false;
	        	}else if(response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
	        		log.error("Server is down or Not responding at this Moment"+candidateByCandidateCode.getData().getCandidateId());
	        		message = "Server is down or Not responding at this Moment";
	        		outcomeBoolean=false;
	        	}
				outcome.setData(response.getStatusCode().toString());
				outcome.setOutcome(outcomeBoolean);
				outcome.setMessage(message);
				
	        }catch (Exception ex) {
	        	outcome.setData(response!=null?response.getStatusCode().toString():"");
				outcome.setOutcome(outcomeBoolean);
				outcome.setMessage("Unable to get epfo details.");
	        	log.error("Exception occured in getEpfodetail:",ex); // Add the Proper logging Message here
			}
		}
		return outcome;
	}


	@SuppressWarnings({ "rawtypes", "unchecked"})
	@Override
	public ServiceOutcome<String> getEpfodetailNew(EpfoDetailsDto epfoDetails) {
		ServiceOutcome<String> outcome = new ServiceOutcome<>();
		ResponseEntity<String> response = null;
		
		// && StringUtils.isNotEmpty(epfoDetails.getCaptcha())
		if(StringUtils.isNotEmpty(epfoDetails.getCandidateCode()) && StringUtils.isNotEmpty(epfoDetails.getUanusername())) { //&& StringUtils.isNotEmpty(epfoDetails.getUanpassword())
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
	        JSONObject request = new JSONObject();
	        
			JSONArray epfo_param_c_array=new JSONArray();
			String message="";
			Boolean outcomeBoolean= false;
			
	        try {
				System.out.println("____________________epfoDetails.getUanusername() new "+epfoDetails);
				System.out.println("____________________epfoDetails.getUanusername() new "+epfoDetails.getUanusername());
				System.out.println("____________________epfoDetails.getUanusername() new "+epfoDetails.getUanpassword());
				System.out.println("____________________epfoDetails.getUanusername() new "+epfoDetails.getCaptcha());
				request.put(EPFOConstants.EPFO_USR ,epfoDetails.getUanusername());
				request.put(EPFOConstants.EPFO_PWD ,epfoDetails.getUanpassword());
				request.put(EPFOConstants.EPFO_CAPTCHA ,epfoDetails.getCaptcha());
	        	// request.put(EPFOConstants.EPFO_USR,epfoDetails.getUanusername());
				
				// request.put(EPFOConstants.EPFO_PWD,' ');
				request.put(EPFOConstants.EPFO_UAN,epfoDetails.getUanusername());
				request.put(EPFOConstants.EPFO_PARAM_H,"string");
				epfo_param_c_array.put(
						new JSONObject()
							.put(EPFOConstants.EPFO_DOMAIN, "string")
							.put(EPFOConstants.EPFO_HTTP_ONLY,"true")
							.put(EPFOConstants.EPFO_NAME, "string")
							.put(EPFOConstants.EPFO_PATH, "string")
							.put(EPFOConstants.EPFO_SECURE, "true")
							.put(EPFOConstants.EPFO_VALUE, "string")
						);
				request.put(EPFOConstants.EPFO_PARAM_C,epfo_param_c_array);
				request.put(EPFOConstants.EPFO_PARAM_CH,"string");
				request.put(EPFOConstants.EPFO_PARAM_E,"string");
				request.put(EPFOConstants.EPFO_PARAM_L,"string");
				
				HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
				System.out.println("\n------entity--------------"+epfoDetails.getCandidateCode()+entity);
				System.out.println("\n------epfoSecurityConfig ------ new "+epfoSecurityConfig.getFinalSubmitPostUrl());
				System.out.println("\n------epfoDetails ------ new "+epfoDetails.getTransactionid());
				response = restTemplate.exchange(epfoSecurityConfig.getFinalSubmitPostUrl()+epfoDetails.getTransactionid(), HttpMethod.POST, entity, String.class);
				String responseBody=response.getBody();
				JSONObject obj = new JSONObject(responseBody);
				System.out.println("\n--------obj --------- new "+epfoDetails.getCandidateCode()+obj);
				ServiceOutcome<CandidateDetailsDto> candidateByCandidateCode = candidateService.getCandidateByCandidateCode(
					epfoDetails.getCandidateCode());
				System.out.println("\n--------candidateByCandidateCode --------- new "+candidateByCandidateCode);
				String resMsg = obj.toString();
				System.out.println("\n--------resMsg --------- new "+resMsg);
				System.out.println("\n--------resMsg --------- "+epfoDetails.getCandidateCode()+epfoDetails.getUanusername());
				CandidateEPFOResponse candidateEPFOResponse = candidateEPFOResponseRepository
					.findByCandidateIdAndUan(candidateByCandidateCode.getData().getCandidateId(),epfoDetails.getUanusername())
					.orElse(new CandidateEPFOResponse());
				System.out.println("\n--------resMsg --------- "+epfoDetails.getCandidateCode()+candidateEPFOResponse);
				candidateEPFOResponse.setEPFOResponse(resMsg);
				candidateEPFOResponse.setUan(epfoDetails.getUanusername());
				candidateEPFOResponse.setCandidateId(candidateByCandidateCode.getData().getCandidateId());
				candidateEPFOResponse.setCreatedOn(new Date());
				candidateEPFOResponse.setLastUpdatedOn(new Date());
				System.out.println("\n____________________before candidateEPFOResponse new "+candidateEPFOResponse);
				candidateEPFOResponseRepository.save(candidateEPFOResponse);
				System.out.println("\n____________________after candidateEPFOResponse  new "+epfoDetails.getCandidateCode()+candidateEPFOResponse);
				if(!obj.getString("code").equals("fail")){
					JSONArray epfoData = obj.getJSONArray("message");
			        final ObjectMapper objectMapper = new ObjectMapper();
			        EpfoDataFromApiDto[] epfoDataFromApiDto = objectMapper.readValue(epfoData.toString(), EpfoDataFromApiDto[].class);
			        List<EpfoDataFromApiDto> epfoList = new ArrayList(Arrays.asList(epfoDataFromApiDto));
					
			        List<EpfoData> epfoExperiencesList = new ArrayList<>();
			        if(epfoList !=null && epfoList.size()>0) {
		        		log.debug("Post Login Information retrieved successfully"+epfoDetails.getCandidateCode());
		        		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						Candidate candidate = candidateRepository.findByCandidateCode(
							epfoDetails.getCandidateCode());
						for(EpfoDataFromApiDto epfo : epfoList) {
						
//		        			CandidateCafExperience candidateCafExperience = new CandidateCafExperience();
//		        			candidateCafExperience.setUan(epfo.getUan());
//		        			candidateCafExperience.setCandidateEmployerName(epfo.getCompany());
//		        			candidateCafExperience.setOutputDateOfJoining(sdf.parse(epfo.getDoj()));
//		        			candidateCafExperience.setInputDateOfJoining(sdf.parse(epfo.getDoj()));
//		        			candidateCafExperience.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("EPFO"));
//		        			candidateCafExperience.setOutputDateOfExit(!epfo.getDoe().equals("NOT_AVAILABLE")?sdf.parse(epfo.getDoe()):null);
//		        			candidateCafExperience.setInputDateOfExit(!epfo.getDoe().equals("NOT_AVAILABLE")?sdf.parse(epfo.getDoe()):null);
//		        			candidateCafExperience.setCandidate(candidateRepository.findByCandidateCode(epfoDetails.getCandidateCode()));
//		        			candidateCafExperience.setCreatedOn(new Date());
//		        			candidateCafExperience.setColor(colorRepository.findByColorCode("GREEN"));
							EpfoData epfoData1 = new EpfoData();
							epfoData1.setCandidate(candidate);
							epfoData1.setCompany(epfo.getCompany());
							epfoData1.setName(epfo.getName());
							epfoData1.setUan(epfo.getUan());
							epfoData1.setDoj(DateUtil.getDate(epfo.getDoj(),"dd/MM/yyyy"));
							epfoData1.setDoe(DateUtil.getDate(epfo.getDoe(),"dd/MM/yyyy"));
							epfoExperiencesList.add(epfoData1);
		        		}
		        		
		        		if(epfoExperiencesList!=null && epfoExperiencesList.size()>0) {
		        			epfoDataRepository.saveAll(epfoExperiencesList);
		        			
		        			CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(epfoDetails.getCandidateCode());
		        			candidateStatus.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("EPFO"));
		        			candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("EPFO"));
		        			candidateStatus.setLastUpdatedOn(new Date());
		        			candidateStatusRepository.save(candidateStatus);
							if(candidateStatus.getCandidate().getOrganization().getCallBackUrl() != null)
								candidateService.postStatusToOrganization(candidateStatus.getCandidate().getCandidateCode());
		        			candidateService.createCandidateStatusHistory(candidateStatus,"CANDIDATE");
		        			
		        			outcomeBoolean=true;
		        		}else {
		        			message = "Unable to save epfo details";
			        		outcomeBoolean=false;
		        		}
			        }
		        
	        	}else if(response.getStatusCode() == HttpStatus.OK && obj.getString("code").equals("fail")) {
	        		message = obj.getString("message");
	        		if(message.equals("Epfo site is Busy,pls make the request again")) {
	        			message = "EPFO site is down, Please try after 7 PM or late night, If you don’t have UAN skip and complete the verification.";
	        		}
	        		outcomeBoolean=false;
	        	}else if(response.getStatusCode() == HttpStatus.UNAUTHORIZED){
	        		log.error("User is Unauthorized"+epfoDetails.getCandidateCode());
	        		message = "User is Unauthorized";
	        		outcomeBoolean=false;
	        	}else if(response.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT){
	        		log.error("Server response is slow, getting timeout"+epfoDetails.getCandidateCode());
	        		message = "Server response is slow, getting timeout";
	        		outcomeBoolean=false;
	        	}else if(response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
	        		log.error("Server is down or Not responding at this Moment"+epfoDetails.getCandidateCode());
	        		message = "Server is down or Not responding at this Moment";
	        		outcomeBoolean=false;
	        	}
				outcome.setData(response.getStatusCode().toString());
				outcome.setOutcome(outcomeBoolean);
				outcome.setMessage(message);
				
	        }catch (Exception ex) {
	        	outcome.setData(response!=null?response.getStatusCode().toString():"");
				outcome.setOutcome(outcomeBoolean);
				outcome.setMessage("Unable to get epfo details.");
	        	log.error("Exception occured in getEpfodetailNew:"+epfoDetails.getCandidateCode(),ex); // Add the Proper logging Message here
			}
		}
		return outcome;
	}

	@Transactional
	@Override
	public Boolean processEpfoDataForUANCandidate(ResponseEntity<String> response, BulkUanDTO bulkUanDTO) {
		
		Boolean outcomeBoolean= false;
		if(StringUtils.isNotEmpty(bulkUanDTO.getCandidateCode())) {
			
			
	        try {
				log.info("UAN CANDIDATE TO BE PROCESSING FOR EPFO RECORD::{}",bulkUanDTO.getCandidateCode());
				
				String responseBody=response.getBody();
				JSONObject obj = new JSONObject(responseBody);
				JSONArray messagee = obj.getBoolean("success") ? obj.getJSONArray("message") : new JSONArray();
				ServiceOutcome<CandidateDetailsDto> candidateByCandidateCode = candidateService.getCandidateByCandidateCode(bulkUanDTO.getCandidateCode());
				String resMsg = obj.toString();
				CandidateEPFOResponse candidateEPFOResponse = candidateEPFOResponseRepository
					.findByCandidateIdAndUan(candidateByCandidateCode.getData().getCandidateId(),bulkUanDTO.getUan())
					.orElse(new CandidateEPFOResponse());
				for(int i=0; i<messagee.length();i++) {
					JSONObject object = messagee.getJSONObject(i);
					if(object.length()!=0) {
						candidateEPFOResponse.setUanName (object.getString("name"));
					}
				}
				candidateEPFOResponse.setEPFOResponse(resMsg);
				candidateEPFOResponse.setUan(bulkUanDTO.getUan());
				candidateEPFOResponse.setCandidateId(candidateByCandidateCode.getData().getCandidateId());
				candidateEPFOResponse.setCreatedOn(new Date());
				candidateEPFOResponse.setLastUpdatedOn(new Date());
				candidateEPFOResponseRepository.save(candidateEPFOResponse);
				
				if(!obj.getString("code").equals("fail")){
					JSONArray epfoData = obj.getJSONArray("message");
			        final ObjectMapper objectMapper = new ObjectMapper();
			        EpfoDataFromApiDto[] epfoDataFromApiDto = objectMapper.readValue(epfoData.toString(), EpfoDataFromApiDto[].class);
			        List<EpfoDataFromApiDto> epfoList = new ArrayList(Arrays.asList(epfoDataFromApiDto));
					
			        List<EpfoData> epfoExperiencesList = new ArrayList<>();
			        if(epfoList !=null && !epfoList.isEmpty()) {
		        		log.debug("Post Login Information retrieved successfully"+candidateByCandidateCode.getData().getCandidateId());
						Candidate candidate = candidateRepository.findByCandidateCode(bulkUanDTO.getCandidateCode());
						for(EpfoDataFromApiDto epfo : epfoList) {
						
							EpfoData epfoData1 = new EpfoData();
							epfoData1.setCandidate(candidate);
							epfoData1.setCompany(epfo.getCompany());
							epfoData1.setName(epfo.getName());
							epfoData1.setUan(epfo.getUan());
							epfoData1.setDoj(DateUtil.getDate(epfo.getDoj(),"dd/MM/yyyy"));
							epfoData1.setDoe(DateUtil.getDate(epfo.getDoe(),"dd/MM/yyyy"));
							
							//adding member id(New column)
							epfoData1.setMemberId(epfo.getMemberId());
							
							epfoExperiencesList.add(epfoData1);
							candidate.setCandidateName(epfo.getName());
		        		}
						candidateRepository.save(candidate);
		        		
		        		if(epfoExperiencesList!=null && !epfoExperiencesList.isEmpty()) {
		        			
		        			List<EpfoData> existingEpfoList = epfoDataRepository.findAllByCandidateIdAndUan(candidateEPFOResponse.getCandidateId(), candidateEPFOResponse.getUan());
		        			if(existingEpfoList!=null && !existingEpfoList.isEmpty()) {
		        				existingEpfoList.forEach(temp -> epfoDataRepository.deleteById(temp.getEpfoId()));
		        				log.info("Epfo Details deleted for {}{}",candidateEPFOResponse.getCandidateId(), candidateEPFOResponse.getUan());
		        			}
		        				
		        			
		        			epfoDataRepository.saveAll(epfoExperiencesList);
		        			
		        		
//		        				CandidateStatus candidateStatus = candidateStatusRepository.findByCandidateCandidateCode(bulkUanDTO.getCandidateCode());
//			        			candidateStatus.setServiceSourceMaster(serviceSourceMasterRepository.findByServiceCode("EPFO"));
//			        			candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("EPFO"));
//			        			candidateStatus.setLastUpdatedOn(new Date());
//			        			candidateStatus= candidateStatusRepository.save(candidateStatus);
//			        			candidateService.createCandidateStatusHistory(candidateStatus,"CANDIDATE");
//			        			
//								candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("PENDINGAPPROVAL"));
//								candidateStatus = candidateStatusRepository.save(candidateStatus);
//								candidateService.createCandidateStatusHistory(candidateStatus, "CANDIDATE");
		        			
		        	//updating CAF table
								List<CandidateCafExperience> candidateCafExperiences = candidateService.getCandidateExperienceFromItrAndEpfoByCandidateId(
							        		candidate.getCandidateId(), false);
							        log.info("CAFEXPEROEINCE FOR BULK UAN ::{}", candidateCafExperiences.size());
							     // Reattach detached entities to the current session
							        List<CandidateCafExperience> attachedExperiences = new ArrayList<>();
							        for (CandidateCafExperience experience : candidateCafExperiences) {
							        	//checking suspect employer check
							        	ServiceOutcome<String> suspectResponse = candidateService.suspectEmpMasterCheck(experience.getCandidateEmployerName(), 
							        			                 candidate.getOrganization().getOrganizationId());
							        	if (Boolean.TRUE.equals(suspectResponse.getOutcome()) && suspectResponse.getData().equalsIgnoreCase("RED")) {
												experience.setColor(colorRepository.findByColorCode("AMBER"));
												experience.setServiceSourceMaster(
														serviceSourceMasterRepository.findByServiceCode("DNHDB"));
										}else {
											experience.setColor(colorRepository.findByColorCode("GREEN"));
										}
							        	//end suspect check
							            attachedExperiences.add(entityManager.merge(experience));
							        }

							        // Save all the attached entities
							        candidateCafExperienceRepository.saveAll(attachedExperiences);
								
		        			
		        			outcomeBoolean=true;
		        			
		        		}else {
			        		outcomeBoolean=false;
		        		}
			        }
		        
	        	}
				
	        }catch (Exception ex) {
	        	log.error("Exception occured in processEpfoDataForUANCandidate:",ex);
			}
		}
		return outcomeBoolean;
		
	}
	
	public String getEpfoTIDGeneral() {
		String tId="";
		ResponseEntity<String> epfoTokenResponse = null;
		HttpHeaders headers = new HttpHeaders();
        setHeaderDetails(headers);
        JSONObject request = new JSONObject();
        try {
        	request.put(epfoSecurityConfig.getClientIdValue(),epfoSecurityConfig.getClientId());
			request.put(epfoSecurityConfig.getClientSecretValue(),epfoSecurityConfig.getClientSecret());
			HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
			
			epfoTokenResponse = restTemplate.exchange(epfoSecurityConfig.getAccessTokenUrl(), HttpMethod.POST, entity, String.class);
			log.info("Response from EPFO TOKEN API ",epfoTokenResponse);
			String message=epfoTokenResponse.getBody(); 
			JSONObject obj1 = new JSONObject(message);
			log.info("Response from EPFO TOKEN API - message ",obj1);
			JSONObject obj = obj1.getJSONObject("message");
			String accessToken = obj.getString("access_token");
    		log.info("access_token::{}",accessToken);
    		if(StringUtils.isNotEmpty(accessToken)) {
    			ResponseEntity<String> response = null;
    			HttpHeaders tIdheaders = new HttpHeaders();
    			tIdheaders.setBearerAuth(accessToken);
    			tIdheaders.add("Bearer", accessToken);
    	        HttpEntity<String> tidrequest = new HttpEntity<String>(tIdheaders);
    	        try {
    	        	response = restTemplate.exchange(epfoSecurityConfig.getTransactionIdUrl(), HttpMethod.GET, tidrequest, String.class);
    			  	try {
    					String tidmessage=response.getBody();
    					log.info("Response from EPFO Transaction API : {}",tidmessage);
    					JSONObject tidobj = new JSONObject(tidmessage);
    					log.info("Response from EPFO Transaction API - obj:{} ",tidobj);
    			  		tId=tidobj.getString("message");
    					log.info("Generated transactionId Id is ::{}",tId);
    				} catch (JSONException e) {
    					log.error("Json Exception occured inn TID..",e);
    				}
    			  	
    			}catch(HttpClientErrorException e) {
    			    log.error("HttpClientErrorException occured in TID...::{}", e);
    			} catch(HttpServerErrorException ex) {
    				log.error("HttpServerErrorException occured...::{}",ex);
    			}
    		}else {
    			log.error("transactionId could Not be generated.");
    		}
    		return tId;
        }catch (Exception ex) {
        	log.error("Exception occured in getEpfoTIDGeneral:",ex); // Add the Proper logging Message here
		}
        return tId;
	}
}


