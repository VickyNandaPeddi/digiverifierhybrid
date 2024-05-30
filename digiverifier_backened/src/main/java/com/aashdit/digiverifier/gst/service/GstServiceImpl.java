package com.aashdit.digiverifier.gst.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.aashdit.digiverifier.client.securityDetails.ITRSecurityConfig;
import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatusHistory;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusHistoryRepository;
import com.aashdit.digiverifier.config.candidate.repository.StatusMasterRepository;
import com.aashdit.digiverifier.config.superadmin.model.Color;
import com.aashdit.digiverifier.config.superadmin.repository.ColorRepository;
import com.aashdit.digiverifier.gst.dto.GstDataFromApiDto;
import com.aashdit.digiverifier.gst.model.GstData;
import com.aashdit.digiverifier.gst.repository.GstRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GstServiceImpl implements GstService{

	private static SimpleDateFormat SDFMMM = new SimpleDateFormat("MMM");
	
	@Autowired
	private RestTemplate restTemplate; 
	
	@Autowired
	private ITRSecurityConfig itrSecurityConfig;
	
	@Autowired
	private CandidateRepository candidateRepository;
	
	@Autowired
	private CandidateStatusHistoryRepository candidateStatusHistoryRepository; 
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ColorRepository colorRepository;
	
	
	@Autowired
	private StatusMasterRepository statusMasterRepository;
	
	@Autowired
	private GstRepository gstRepository;
	
	@Value("${GST.BASE.URL}")
	private String gstBaseURL;
	
	private HttpHeaders setHeaderDetails (HttpHeaders headers) {
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
	
	@Override
	public ServiceOutcome<List<GstDataFromApiDto>> getGstRecords(String candidateCode, String flow) {
		ServiceOutcome<List<GstDataFromApiDto>> svcOutcome = new ServiceOutcome<>();
		List<GstDataFromApiDto> dataDTOList = new ArrayList<>();
		try {
			Candidate candidate=candidateRepository.findByCandidateCode(candidateCode);
			String gstToken =getGstToken();
			//calling the GST API to get transaction id
			if(!gstToken.equals("") && !gstToken.isEmpty()) {
				String gstTID = getGstTransactionID(gstToken);
				if(!gstTID.equals("") && !gstTID.isEmpty() && candidate.getItrPanNumber()!=null) {
					
					
					
					//call the GST API for getting images..
					
					String res =getGstImagesData(gstTID, candidate.getItrPanNumber());
					
					dataDTOList = saveGstData(res, candidate);
				}
			}
			
			if(dataDTOList!=null && !dataDTOList.isEmpty()) {
				log.info("GOT GST RESPONCE FOR ::{}",candidateCode);
				//updating the candidate status history table only
				if (flow.equals("CANDIDATE")) {
					CandidateStatusHistory candidateStatusHistoryObj = new CandidateStatusHistory();
					candidateStatusHistoryObj.setCandidate(candidate);
					candidateStatusHistoryObj.setStatusMaster(statusMasterRepository.findByStatusCode("GST"));
					candidateStatusHistoryObj.setCreatedBy(candidate.getCreatedBy());
					candidateStatusHistoryObj.setCreatedOn(new Date());
					candidateStatusHistoryObj.setCandidateStatusChangeTimestamp(new Date());
					candidateStatusHistoryRepository.save(candidateStatusHistoryObj);
				}
				
				svcOutcome.setData(dataDTOList);
        		svcOutcome.setMessage("GST Records Retrived Successfully..");
        		svcOutcome.setOutcome(true);
			}else {
				log.info("No GST records found for this candidate ::{}",candidateCode);
				svcOutcome.setData(null);
        		svcOutcome.setMessage("Something Went Wrong, Please Try Again..!");
        		svcOutcome.setOutcome(false);
				
			}
			
		}catch(Exception e) {
			log.error("Exception occured in getGstRecords::{}",e);
			svcOutcome.setData(null);
    		svcOutcome.setMessage("Something Went Wrong..!");
    		svcOutcome.setOutcome(false);
		}
		return svcOutcome;
	}
	
	public String getGstToken() {
		String token = "";
		try {
			HttpHeaders tokenHeaders = new HttpHeaders();
		    setHeaderDetails(tokenHeaders);
		    JSONObject tokenRequest = new JSONObject();
		    
		    tokenRequest.put(itrSecurityConfig.getClientIdValue(),itrSecurityConfig.getClientId());
		    tokenRequest.put(itrSecurityConfig.getClientSecretValue(),itrSecurityConfig.getClientSecret());
			HttpEntity<String> tokenEntity = new HttpEntity<>(tokenRequest.toString(), tokenHeaders);
			
			//calling remittance token API
			ResponseEntity<String> tokenResponse =restTemplate.exchange(gstBaseURL+"gen-access-token/", HttpMethod.POST, tokenEntity, String.class);
			String message = tokenResponse.getBody();
			
			if(message != null && !message.isEmpty()) {
				JSONObject obj = new JSONObject(message);
				token = obj!=null ? obj.getJSONObject("message").getString("access_token") : "";
			}
			log.info("GST TOKEN in getGstToken::{}",token);
			return token;
			
		}catch(JSONException jsn) {
  			log.error("JSON Exception occured in getGstToken::{}",jsn);
		}catch(Exception e){
			log.error("Exception occured in getGstToken::{}",e);
		}
		return token;
	}
	
	public String getGstTransactionID(String remittanceToken) {
		String tID = "";
		try {
			HttpHeaders headers = new HttpHeaders();
			setHeaderDetails(headers);
	        headers.setBearerAuth(remittanceToken);
	        headers.add("Bearer", remittanceToken); 
	        HttpEntity<String> request = new HttpEntity<>(headers);
	        
	      //calling remittance transactionID API
	        ResponseEntity<String> response = restTemplate.exchange(gstBaseURL+"gen-transaction-id/", HttpMethod.GET, request, String.class);
		  	String message=response.getBody();
		  		JSONObject obj = new JSONObject(message);
		  		tID = obj!=null ? obj.getString("message") : "";
		  		
		  		log.info("GST TRANSACTION ID in getGstTransactionID::{}",tID);
				return tID;
	  		
		}catch(JSONException jsn) {
  			log.error("JSON Exception occured in getGstTransactionID::{}",jsn);
  		}catch(Exception e){
			log.error("Exception occured in getGstTransactionID::{}",e);
		}
		return tID;
	}
	
	public String getGstImagesData(String tID, String panNumber) {
		String message ="";
		try {
			HttpHeaders headers = new HttpHeaders();
			setHeaderDetails(headers);
			headers.add("txnid",tID);
			//request object
			JSONObject requestJson = new JSONObject();
			requestJson.put("pan",panNumber);
			
			
			log.info("Request to fetch GST Images ::{}",requestJson.toString());
			HttpEntity<String> requestEntity = new HttpEntity<>(requestJson.toString(), headers);
			
			//calling remittance API to get Images
			ResponseEntity<String> gstResponse =restTemplate.exchange(gstBaseURL+"fetch-gst?txnid="+tID, HttpMethod.POST, requestEntity, String.class);
			// Check if the response is a redirect
//	        if (gstResponse.getStatusCode() == HttpStatus.FOUND) {
	            // Extract the redirect URL from the Location header
	            String redirectUrl = gstResponse.getHeaders().getLocation().toString();

	            // Make a new request to the redirect URL
	            ResponseEntity<String> redirectResponse = restTemplate.exchange(redirectUrl, HttpMethod.POST, requestEntity, String.class);
	            message = redirectResponse.getBody();
	            
//	        }
			
		
		}catch(HttpClientErrorException c) {
 			log.error("CLIENT Exception occured in getGstImagesData::{}",c);
 			message=c.getResponseBodyAsString();
 		
		}catch(JSONException jsn) {
 			log.error("JSON Exception occured in getGstImagesData::{}",jsn);
 		}catch(Exception e){
			log.error("Exception occured in getGstImagesData::{}",e);
		}
		return message;
	}
	
private List<GstDataFromApiDto> saveGstData(String gstResponce, Candidate candidate) {
		
		List<GstDataFromApiDto> dataDTOList = new ArrayList<>();
		try {
			
			if(!gstResponce.equals("") && !gstResponce.isEmpty()) {
//				log.info("THE response of GST API is ::{}",gstResponce);
				JSONObject obj = new JSONObject(gstResponce);
				
				if(obj.getBoolean("success") && obj.opt("message") instanceof JSONObject) {
					JSONObject messageObject = (JSONObject) obj.opt("message");
					JSONArray messagesArray = messageObject.getJSONArray("messages");
					List<GstData> dataList = new ArrayList<>();
					
					for(int i=0 ; i<messagesArray.length(); i++) {
						JSONObject message = messagesArray.getJSONObject(i);
						log.info("THE response Color of GST API is ::{}",message.getString("color"));
						String color = message.getString("color");
						String panNumber = message.getString("pan_num");
						String companyName = message.getString("company_name");
						String gstNumber = message.getString("gst");
						String status = message.getString("status");
						
						
						Color colorObj = colorRepository.findByColorCode(color);
	//					List<GstData> dataList = new ArrayList<>();
						JSONArray imagesJson =message.getJSONArray("images");
							
						//Retrieving the images base64 data for every year to store in DB
						for(int j=0 ; j<imagesJson.length(); j++) {
							log.info("Saving GST images");
							String base64ImageData = imagesJson.getString(j);
							String base64Data = base64ImageData.replace("data:image/png;base64,", "");
							// Decode the Base64 string to bytes
				            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
							
				            //set dto object list
				            GstDataFromApiDto gstDataFromApiDto=new GstDataFromApiDto();
				            gstDataFromApiDto.setCandidateCode(candidate.getCandidateCode());
				            gstDataFromApiDto.setColor(color);
				            gstDataFromApiDto.setCompany(companyName);
				            gstDataFromApiDto.setCreatedOn(new Date());
				            gstDataFromApiDto.setImage(base64Data);
				            gstDataFromApiDto.setPanNumber(panNumber);
				            gstDataFromApiDto.setGstNumber(gstNumber);
				            gstDataFromApiDto.setStatus(status);
				            
				            dataDTOList.add(gstDataFromApiDto);
				            
				            //set the values in table
							GstData gstData = new GstData();
							
							gstData.setCandidate(candidate);
							gstData.setColor(colorObj);
							gstData.setCompany(companyName);
							gstData.setCreatedOn(new Date());
							//gstData.setUpdatedOn(candidate.getLastUpdatedOn());
							gstData.setCreatedBy(candidate.getCreatedBy());
							//gstData.setLastUpdatedBy(candidate.getLastUpdatedBy());
							gstData.setImage(imageBytes);
							gstData.setPanNumber(panNumber);
							gstData.setGstNumber(gstNumber);
							gstData.setStatus(status);
							
							//prepare the list
							dataList.add(gstData);
						}
				     }
					
					//save all list of images for all year for single employer
					gstRepository.saveAll(dataList);
						
					
					return dataDTOList;
				}
				
			}else {
				return dataDTOList;
			}
			
		}catch(JSONException jsn) {
  			log.error("JSON Exception occured in saveGstData::{}",jsn);
  		}catch(Exception e){
			log.error("Exception occured in saveRemittanceData::{}",e);
		}
		
		return dataDTOList;
	}

@Override
public ServiceOutcome<String> deleteGstRecord(Long gstId) {
	ServiceOutcome<String> serviceOutcome = new ServiceOutcome<>();
	try {
		log.info("deleteGstRecord for ::{}",gstId);
		Optional<GstData> gstData = gstRepository.findById(gstId);
		if(gstData.isPresent() && gstData.get()!=null) {
			
			gstRepository.delete(gstData.get());
			log.info("gst record found and deleted for::{}",gstId);
			serviceOutcome.setData("GST Record Deleted Successfully..!");
			serviceOutcome.setMessage("GST Record Deleted Successfully..!");
			serviceOutcome.setOutcome(true);
			serviceOutcome.setStatus("Success");
		}else {
			log.info("gst record not found for::{}",gstId);
			serviceOutcome.setData("GST Record Not Found..!");
			serviceOutcome.setMessage("No Record Found To Delete..!");
			serviceOutcome.setOutcome(false);
			serviceOutcome.setStatus("Fail");
		}
		return serviceOutcome;
	}catch(Exception e){
		log.error("Exception occured in deleteGstRecord::{}",e);
	}
	return serviceOutcome;
}
	

}
