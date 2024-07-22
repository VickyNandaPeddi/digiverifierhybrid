package com.aashdit.digiverifier.epfo.remittance.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
import com.aashdit.digiverifier.epfo.model.EpfoData;
import com.aashdit.digiverifier.epfo.remittance.dto.RemittanceDataFromApiDto;
import com.aashdit.digiverifier.epfo.remittance.dto.RemittanceEmployerRequestDto;
import com.aashdit.digiverifier.epfo.remittance.model.RemittanceData;
import com.aashdit.digiverifier.epfo.remittance.repository.RemittanceRepository;
import com.aashdit.digiverifier.epfo.repository.EpfoDataRepository;
import com.aashdit.digiverifier.itr.repository.ITRDataRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.repository.query.Param;
import com.aashdit.digiverifier.config.candidate.model.CandidateCafExperience;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCafExperienceRepository;
import com.aashdit.digiverifier.config.candidate.service.CandidateService;
import com.aashdit.digiverifier.itr.model.ITRData;
import com.aashdit.digiverifier.utils.CommonUtils;

@Service
@Slf4j
public class RemittanceServiceImpl implements RemittanceService{
	
	private static SimpleDateFormat SDFMMM = new SimpleDateFormat("MMM");

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private ITRSecurityConfig itrSecurityConfig;
	
	@Autowired
	private CandidateRepository candidateRepository;
	
	@Autowired
	private ITRDataRepository itrDataRepository;
	
	
	@Autowired
	private CandidateStatusHistoryRepository candidateStatusHistoryRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ColorRepository colorRepository;
	
	
	@Autowired
	private StatusMasterRepository statusMasterRepository;
	
	
	@Autowired @Lazy
	private EpfoDataRepository epfoDataRepository;
	
	@Autowired
	private RemittanceRepository remittanceRepository;
	
	@Autowired
	private CandidateCafExperienceRepository candidateCafExperienceRepository;
	
	@Autowired
	private CandidateService candidateService;
	
	@Value("${REMITTANCE.BASE.URL}")
	private String remittanceBaseURL;
	
	private String remCatcheTID="";
	
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
	public ServiceOutcome<List<RemittanceDataFromApiDto>> getremittanceRecords(String candidateCode, String flow) {
		ServiceOutcome<List<RemittanceDataFromApiDto>> svcOutcome = new ServiceOutcome<>();
		List<RemittanceDataFromApiDto> dataDTOList = new ArrayList<>();
		try {
			List<EpfoData> allEpfoData= epfoDataRepository.findAllByCandidateCandidateCode(candidateCode);
			if(allEpfoData!=null && !allEpfoData.isEmpty()) {
				
				//Check if flow is agent and he wants to override (need to delete existing records of candidate flow)
				if (flow.equals("NOTCANDIDATE")) {
					log.info("Remittance call for  ::{}",flow);
					List<RemittanceData> remittances = remittanceRepository.findAllByCandidateCandidateCode(candidateCode);
					remittanceRepository.deleteAll(remittances);
				}
				
				//checking ITR records for each yeas of the employments
				for(EpfoData epfoData : allEpfoData) {
					List<String> yearsToFetchRemittance =new ArrayList<>();
//					List<String> yearsBetween =CommonUtils.getYearsBetweenDates(epfoData.getDoj(), epfoData.getDoe()!=null ? epfoData.getDoe(): new Date());
			       
					Date d= epfoData.getDoe()!=null ? epfoData.getDoe(): new Date();
					LocalDate localDate = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					Integer year = localDate.getYear();

					boolean remittanceApplied=false;
					int itrRecords = itrDataRepository.getCountByCandidateCandidateCodeFinYear(candidateCode,year.toString());
					if(itrRecords<2) {
						
						remittanceApplied=true;
					}
					
					
//					boolean remittanceApplied=false;
//					int i=0;
//					for(String year : yearsBetween) {
//						int itrRecords = itrDataRepository.getCountByCandidateCandidateCodeFinYear(candidateCode,year);
//						log.info("ITR Records FOR YEAR ::{}",year +"::"+itrRecords);
//						if(itrRecords<2) {
//							
//							remittanceApplied=true;
////								//retrieve months MMM
////								String monthOfDOJ = formatDate(epfoData.getDoj());
////								String monthOfDOE = formatDate(epfoData.getDoe());
////								
////								String monthYear= i==0 ? formatWithMonth(year, monthOfDOJ) : year;
////								monthYear= i==yearsBetween.size() - 1 && i>0 ? formatWithMonth(year, monthOfDOE) : monthYear;
////							
////							yearsToFetchRemittance.add(monthYear);
////							//below condition if candidate having DOJ and DOE in same YEAR, then added DOE record also
////							if(yearsBetween.size()==1) {
////								yearsToFetchRemittance.add(formatWithMonth(year, monthOfDOE));
////							}
//						}
//						i++;
//					}
					if(remittanceApplied) {
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM-yyyy");
						Calendar calendar = Calendar.getInstance();
						
						if(epfoData.getDoe()!=null) {
							Date doe = epfoData.getDoe();

					        // Convert Date to LocalDate
					        LocalDate localDateDOE = doe.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					        String monthYearDOE = localDateDOE.format(formatter);
					        yearsToFetchRemittance.add(monthYearDOE);
					        
					        
							// Get the next month
					        calendar.setTime(doe);
					        calendar.add(Calendar.MONTH, 1);
					        Date nextMonthDate = calendar.getTime();
					        
					        LocalDate localDateNextDoe = nextMonthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					        String nextMonthYearDOE= localDateNextDoe.format(formatter);
					        yearsToFetchRemittance.add(nextMonthYearDOE);
						}else {
							Date doe = new Date();

					        // Convert Date to LocalDate
					        LocalDate localDateDOE = doe.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					        String monthYearDOE = localDateDOE.format(formatter);
					        yearsToFetchRemittance.add(monthYearDOE);
					        
					        
							// Get the previous month
					        calendar.setTime(doe);
					        calendar.add(Calendar.MONTH, -1);
					        Date previousMonthDate = calendar.getTime();
					        
					        LocalDate localDatePreDoe = previousMonthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					        String preMonthYearDOE= localDatePreDoe.format(formatter);
					        yearsToFetchRemittance.add(preMonthYearDOE);
						}
					}
					
					log.info("yearsToFetchRemittance ::{}",yearsToFetchRemittance);
					
					if(yearsToFetchRemittance!=null && !yearsToFetchRemittance.isEmpty()) {
						//update flag in EPFO table for remittance check of that particular employer.
						epfoData.setIsRemittanceChecked(true);
						epfoDataRepository.save(epfoData);
						
						//for these years we need to call remittance API.
						//calling the remittance token for accessing the transaction id
						String remittanceToken =getRemittanceToken();
						
						//calling the remittance API to get transaction id
						if(!remittanceToken.equals("") && !remittanceToken.isEmpty()) {
							String remittanceTID = getRemittanceTransactionID(remittanceToken);
							
							if(!remittanceTID.equals("") && !remittanceTID.isEmpty()) {
		
								//call the remitance apis for getting images..
								
								String res = getRemittanceImagesData(epfoData,yearsToFetchRemittance,remittanceTID,null);
							//	log.info("Response of remitance Images ::{}",res);
								
								//save the records in database.
								List<RemittanceDataFromApiDto> dataList = saveRemittanceData(res, candidateCode, epfoData);
								dataDTOList.addAll(dataList);
							}
							
						}
					}
					
				}
				
				if(dataDTOList!=null && !dataDTOList.isEmpty()) {
					log.info("GOT REMITTANCE RESPONCE FOR ::{}",candidateCode);
					//updating the candidate status history table only
					if (flow.equals("CANDIDATE")) {
						Candidate candidate = candidateRepository.findByCandidateCode(candidateCode);
						CandidateStatusHistory candidateStatusHistoryObj = new CandidateStatusHistory();
						candidateStatusHistoryObj.setCandidate(candidate);
						candidateStatusHistoryObj.setStatusMaster(statusMasterRepository.findByStatusCode("REMITTANCE"));
						candidateStatusHistoryObj.setCreatedBy(candidate.getCreatedBy());
						candidateStatusHistoryObj.setCreatedOn(new Date());
						candidateStatusHistoryObj.setCandidateStatusChangeTimestamp(new Date());
						candidateStatusHistoryRepository.save(candidateStatusHistoryObj);
					}
					
					svcOutcome.setData(dataDTOList);
	        		svcOutcome.setMessage("Remittance Records Retrived Successfully..");
	        		svcOutcome.setOutcome(true);
				}else {
					log.info("No Remittance records found for this candidate ::{}",candidateCode);
					svcOutcome.setData(null);
	        		svcOutcome.setMessage("No Remittance Records available..!");
	        		svcOutcome.setOutcome(true);
					
				}
				
			}else {
				log.info("EPFO RECORDS NOT PRESENT FOR ::{}",candidateCode);
				svcOutcome.setData(null);
        		svcOutcome.setMessage("EPFO SKIPPED");
        		svcOutcome.setOutcome(true);
				
			}
		}catch(Exception e) {
			log.error("Exception occured in getremittanceRecords::{}",e);
		}
		return svcOutcome;
	}
	
	public String getRemittanceToken() {
		String token = "";
		try {
			HttpHeaders tokenHeaders = new HttpHeaders();
		    setHeaderDetails(tokenHeaders);
		    JSONObject tokenRequest = new JSONObject();
		    
		    tokenRequest.put(itrSecurityConfig.getClientIdValue(),itrSecurityConfig.getClientId());
		    tokenRequest.put(itrSecurityConfig.getClientSecretValue(),itrSecurityConfig.getClientSecret());
			HttpEntity<String> tokenEntity = new HttpEntity<>(tokenRequest.toString(), tokenHeaders);
			
			//calling remittance token API
			ResponseEntity<String> tokenResponse =restTemplate.exchange(remittanceBaseURL+"generate-post/", HttpMethod.POST, tokenEntity, String.class);
			String message = tokenResponse.getBody();
			
			if(message != null && !message.isEmpty()) {
				JSONObject obj = new JSONObject(message);
				token = obj!=null ? obj.getJSONObject("message").getString("access_token") : "";
			}
			log.info("REMITTANCE TOKEN in getRemittanceToken::{}",token);
			return token;
			
		}catch(JSONException jsn) {
  			log.error("JSON Exception occured in getRemittanceToken::{}",jsn);
		}catch(Exception e){
			log.error("Exception occured in getRemittanceToken::{}",e);
		}
		return token;
	}
	
	public String getRemittanceTransactionID(String remittanceToken) {
		String tID = "";
		try {
			HttpHeaders headers = new HttpHeaders();
			setHeaderDetails(headers);
	        headers.setBearerAuth(remittanceToken);
	        headers.add("Bearer", remittanceToken); 
	        HttpEntity<String> request = new HttpEntity<>(headers);
	        
	      //calling remittance transactionID API
	        ResponseEntity<String> response = restTemplate.exchange(remittanceBaseURL+"transaction-get/", HttpMethod.GET, request, String.class);
		  	String message=response.getBody();
		  		JSONObject obj = new JSONObject(message);
		  		tID = obj!=null ? obj.getString("message") : "";
		  		
		  		log.info("REMITTANCE TRANSACTION ID in getRemittanceTransactionID::{}",tID);
				return tID;
	  		
		}catch(JSONException jsn) {
  			log.error("JSON Exception occured in getRemittanceTransactionID::{}",jsn);
  		}catch(Exception e){
			log.error("Exception occured in getRemittanceTransactionID::{}",e);
		}
		return tID;
	}
	
	public String getRemittanceImagesData(EpfoData epfoData,List<String> yearsToFetchRemittance,String tID,
			 String captcha) {
		String message ="";
		try {
			HttpHeaders headers = new HttpHeaders();
			setHeaderDetails(headers);
			//request object
			JSONObject requestJson = new JSONObject();
			requestJson.put("name",epfoData.getName());
			requestJson.put("company_name",epfoData.getCompany());
			requestJson.put("years",yearsToFetchRemittance);
			requestJson.put("member_id",epfoData.getMemberId());//hard coded member id for testing
			
			requestJson.put("captcha",captcha);
			
			log.info("Request to fetch remitance Images ::{}",requestJson.toString());
			HttpEntity<String> requestEntity = new HttpEntity<>(requestJson.toString(), headers);
			
			//calling remittance API to get Images
			ResponseEntity<String> remResponse =restTemplate.exchange(remittanceBaseURL+"fetch-proofs?txnid="+tID, HttpMethod.POST, requestEntity, String.class);
			message = remResponse.getBody();
		
		}catch(HttpClientErrorException c) {
  			log.error("CLIENT Exception occured in getRemittanceImagesData::{}",c);
  			message=c.getResponseBodyAsString();
  		
		}catch(JSONException jsn) {
  			log.error("JSON Exception occured in getRemittanceImagesData::{}",jsn);
  		}catch(Exception e){
			log.error("Exception occured in getRemittanceImagesData::{}",e);
		}
		return message;
	}
	
	private List<RemittanceDataFromApiDto> saveRemittanceData(String remittanceResponce, String candidateCode, EpfoData epfoData) {
		
		List<RemittanceDataFromApiDto> dataDTOList = new ArrayList<>();
		try {
			Candidate candidate = epfoData.getCandidate();
			
			if(!remittanceResponce.equals("") && !remittanceResponce.isEmpty()) {
				JSONObject obj = new JSONObject(remittanceResponce);
				
				if(obj.getBoolean("success") && obj.opt("message") instanceof JSONObject) {
					JSONObject messageObject = (JSONObject) obj.opt("message");
					log.info("THE response of Remittance API is ::{}",messageObject.getString("color"));
					
//					String color = messageObject.getString("color");
					String name = messageObject.getString("name");
					String companyName = messageObject.getString("company_name");
					String memberId = messageObject.getString("member_id");
					JSONArray requestedYears =messageObject.getJSONArray("years");
					log.info("Retriving IMAGES FOR requestedYears::{}",requestedYears);
					JSONObject imagesJson = messageObject.getJSONObject("images");
//					Color colorObj = colorRepository.findByColorCode(color);
					List<RemittanceData> dataList = new ArrayList<>();
					for(String key : imagesJson.keySet()) {
						log.info("Retriving IMAGES FOR YEAR::{}",key);
						JSONObject yearsImagesArray =imagesJson.getJSONObject(key);
						//retriving the images base64 data for every year to store in DB
//						for(int j=0 ; j<yearsImagesArray.length(); j++) {
 
							log.info("Saving Remittance images");
							String base64ImageData = yearsImagesArray.getString("image");
							String base64Data = base64ImageData.replace("data:image/png;base64,", "");
							String color = yearsImagesArray.getString("Status");
							Color colorObj = colorRepository.findByColorCode(color);
 
							// Decode the Base64 string to bytes
				            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
				            String remark = "";
				            switch (color) {
							case "GREEN":
								remark = "Remittance Found for " + companyName + " in "  + key;
								break;
							case "AMBER":
								remark = "No Payment details found for " + companyName + " in "  + key;
								break;
							case "RED":
								remark = "No Remittance Found for " + companyName + " in "  + key;
								break;
							default:
								break;
							}
				            //set dto object list
				            RemittanceDataFromApiDto remittanceDataFromApiDto=new RemittanceDataFromApiDto();
				            remittanceDataFromApiDto.setCandidateCode(candidateCode);
				            remittanceDataFromApiDto.setColor(color);
				            remittanceDataFromApiDto.setCompany(companyName);
				            remittanceDataFromApiDto.setCreatedOn(new Date());
				            remittanceDataFromApiDto.setImage(base64Data);
				            remittanceDataFromApiDto.setMemberId(memberId);
				            remittanceDataFromApiDto.setName(name);
				            remittanceDataFromApiDto.setYear(key);
				            remittanceDataFromApiDto.setRemark(remark);
				            dataDTOList.add(remittanceDataFromApiDto);
				            //set the values in table
							RemittanceData remittanceData = new RemittanceData();
							remittanceData.setCandidate(candidate);
							remittanceData.setColor(colorObj);
							remittanceData.setCompany(companyName);
							remittanceData.setCreatedOn(new Date());
							remittanceData.setImage(imageBytes);
							remittanceData.setMemberId(memberId);
							remittanceData.setName(name);
							remittanceData.setYear(key);
							remittanceData.setRemark(remark);
							//prepare the list
							dataList.add(remittanceData);
//						}
					}
					
					//save all list of images for all year for single employer
					remittanceRepository.saveAll(dataList);
					
					//update flag in EPFO table for remittance check of that particular employer.
					epfoData.setIsRemittanceChecked(true);
					epfoDataRepository.save(epfoData);	
					
					return dataDTOList;
				}
				
			}else {
				return dataDTOList;
			}
			
		}catch(JSONException jsn) {
  			log.error("JSON Exception occured in saveRemittanceData::{}",jsn);
  		}catch(Exception e){
			log.error("Exception occured in saveRemittanceData::{}",e);
		}
		
		return dataDTOList;
	}
	
	private String formatDate(Date date) {
	    return date != null ? SDFMMM.format(date) : SDFMMM.format(new Date());
	}

	private String formatWithMonth(String yearString, String month) {
	    return month + "-" + yearString;
	}

	@Override
	public ServiceOutcome<List<RemittanceDataFromApiDto>> getremittanceRecordsForEmployer(
			RemittanceEmployerRequestDto remittanceEmployerRequestDto) {
		
		ServiceOutcome<List<RemittanceDataFromApiDto>> svcOutcome = new ServiceOutcome<>();
		List<RemittanceDataFromApiDto> dataDTOList = new ArrayList<>();
		try {
			if(remittanceEmployerRequestDto!=null) {
				String candidateCode = remittanceEmployerRequestDto.getCandidateCode();
				log.info("Request for REMITTANCE RECORD FOR CANDIDATE IN QC::{}",candidateCode);
				log.info("Request With REMITTANCE Captcha::{}",remittanceEmployerRequestDto.getRemittanceCaptchaText());
				
				//getting the epfo record for the employer 
				List<EpfoData> multiEpfoData= epfoDataRepository.findAllByCandidateCandidateCodeAndMemberId(candidateCode,remittanceEmployerRequestDto.getMemberId());
				EpfoData epfoData= multiEpfoData.get(0);  //unique record if multiple duplicate records are there.
				
				
				String dates = remittanceEmployerRequestDto.getRemittanceDates();
				 List<String> yearList = new ArrayList<>();
				if(dates.contains(",") || dates.contains(", ")) {
					String[] yearArray = dates.split(",");
					yearList=Arrays.asList(yearArray);
				}else {
					yearList.add(dates);
				}
				
				
				//trimming the years
				List<String> yearsToFetchRemittance =new ArrayList<>();
				for(String year : yearList) {
					yearsToFetchRemittance.add(year.trim());
					
					//delete existing remittance record for that employer
					List<RemittanceData> remittances = remittanceRepository.findAllByCandidateCandidateCodeAndMemberIdAndYear(candidateCode,
							                                                        remittanceEmployerRequestDto.getMemberId(),year.trim());
					remittanceRepository.deleteAll(remittances);
				}
				
				log.info("years To Fetch Remittance for QC::{}",yearsToFetchRemittance);
				if(yearsToFetchRemittance!=null && !yearsToFetchRemittance.isEmpty()) {
					log.info("remittance Process started..");
					
					//update flag in EPFO table for remittance check of that particular employer.
					epfoData.setIsRemittanceChecked(true);
					epfoDataRepository.save(epfoData);
					
					//for these years we need to call remittance API.
					    String remittanceToken ="";
					
						String remittanceTID = "";
							
							
						if(!remCatcheTID.equalsIgnoreCase("")) {
							log.info("REUSING TID::{}",remCatcheTID);
							remittanceTID=  remCatcheTID;
						}else {
							//calling the remittance token for accessing the transaction id
							 remittanceToken =getRemittanceToken();
							
							//calling the remittance API to get transaction id
							 remittanceTID = getRemittanceTransactionID(remittanceToken);
						}
						
						//call the remitance apis for getting images..
							String res = getRemittanceImagesData(epfoData,yearsToFetchRemittance,remittanceTID,
									                            remittanceEmployerRequestDto.getRemittanceCaptchaText());
						//	log.info("Response of remitance Images ::{}",res);
							JSONObject obj = new JSONObject(res);
							
							if(obj.getBoolean("success") && obj.getString("code").equalsIgnoreCase("498")) {
								log.info("REMITTANCE RESPONCE FAILED FOR CAPTCHA ::{}",candidateCode);
								
								svcOutcome.setData(null);
				        		svcOutcome.setMessage(obj.getString("code"));
				        		svcOutcome.setOutcome(false);
				        		return svcOutcome;
							}
							
							//reset 
							remCatcheTID="";
							
							//save the records in database.
							List<RemittanceDataFromApiDto> dataList = saveRemittanceData(res, candidateCode, epfoData);
							dataDTOList.addAll(dataList);
					
				}

				if(dataDTOList!=null && !dataDTOList.isEmpty()) {
					log.info("GOT REMITTANCE RESPONCE FOR ::{}",candidateCode);
					
					svcOutcome.setData(dataDTOList);
	        		svcOutcome.setMessage("Remittance Records Retrived Successfully..");
	        		svcOutcome.setOutcome(true);
				}else {
					log.info("Something Went Wrong during fetching records ::{}",candidateCode);
					svcOutcome.setData(null);
	        		svcOutcome.setMessage("Something Went Wrong While Fetching Proofs, Please check the input dates format..!");
	        		svcOutcome.setOutcome(false);
				}
				
			}
		}catch(Exception e) {
			log.error("Exception occured in getremittanceRecordsForEmployer::{}",e);
			svcOutcome.setData(null);
    		svcOutcome.setMessage("Something Went Wrong...!");
    		svcOutcome.setOutcome(false);
		}
		return svcOutcome;
	}

	@Override
	public ServiceOutcome<String> deletedRemittanceRecord(String candidateCode, String memberId, String year) {
		ServiceOutcome<String> svcOutcome = new ServiceOutcome<>();
		try {
			log.info("Delete Remittance for Candidate:{} and memberId:{} and Year:{}", candidateCode,memberId,year);
			
			List<RemittanceData> remittances = remittanceRepository.findAllByCandidateCandidateCodeAndMemberIdAndYear(candidateCode,memberId,year.trim());
			remittanceRepository.deleteAll(remittances);
			
			svcOutcome.setData(null);
    		svcOutcome.setMessage("Record Deleted Successfully..!");
    		svcOutcome.setOutcome(true);
			
		}catch(Exception e) {
			log.error("Exception occured in deletedRemittanceRecord::{}",e);
			svcOutcome.setData(null);
    		svcOutcome.setMessage("Something Went Wrong..!");
    		svcOutcome.setOutcome(false);
		}
		return svcOutcome;
	}

	@Override
	public ServiceOutcome<String> getRemittanceCaptcha(String candidateCode) {
		ServiceOutcome<String> svcOutcome = new ServiceOutcome<>();
		try {
			
			//calling the remittance token for accessing the transaction id
			String remittanceToken =getRemittanceToken();
			
			//calling the remittance API to get transaction id
			String remittanceTID = getRemittanceTransactionID(remittanceToken);
			
			//assigning to global level to use it again while fetching proof records
			remCatcheTID = remittanceTID;
			
			String captchaStringImageResponse =getRemittanceCaptchaImages(remittanceTID);
			
			if(!captchaStringImageResponse.equalsIgnoreCase("") && !captchaStringImageResponse.isEmpty()) {
				
				JSONObject obj = new JSONObject(captchaStringImageResponse);
				log.info("CAPCHA IMAGE RESPONSE FOR::{}",candidateCode+"::"+obj.getString("code"));
				String captchaStringImage =obj.getString("captcha");
				
				svcOutcome.setData(captchaStringImage);
	    		svcOutcome.setMessage("CAPTCHA IMAGE RETRIEVED..!");
	    		svcOutcome.setOutcome(true);
			}else {
				svcOutcome.setData(null);
	    		svcOutcome.setMessage("Something Went Wrong While Getting Captcha..!");
	    		svcOutcome.setOutcome(false);
			}
		}catch(Exception e) {
			log.error("Exception occured in getRemittanceCaptcha::{}",e);
			svcOutcome.setData(null);
    		svcOutcome.setMessage("Something Went Wrong..!");
    		svcOutcome.setOutcome(false);
		}
		return svcOutcome;
	}
	
	public String getRemittanceCaptchaImages(String tID) {
		String message ="";
		try {
			HttpHeaders headers = new HttpHeaders();
			setHeaderDetails(headers);
			HttpEntity<String> requestEntity = new HttpEntity<>(headers);
			
			//calling remittance API to get CAPTCHA Images
			ResponseEntity<String> response =restTemplate.exchange(remittanceBaseURL+"get-captcha?txnid="+tID, HttpMethod.GET, requestEntity, String.class);
			message = response.getBody();
			
			
		}catch(JSONException jsn) {
  			log.error("JSON Exception occured in getRemittanceCaptchaImages::{}",jsn);
  		}catch(Exception e){
			log.error("Exception occured in getRemittanceCaptchaImages::{}",e);
		}
		return message;
	}
	

	//need to update the logic based on LTIM requirement
		@Override
		public ServiceOutcome<List<RemittanceDataFromApiDto>> getLTIMRemittanceRecords(String candidateCode, String flow) {
			ServiceOutcome<List<RemittanceDataFromApiDto>> svcOutcome = new ServiceOutcome<>();
			List<RemittanceDataFromApiDto> dataDTOList = new ArrayList<>();
			try {
				Candidate candidate= candidateRepository.findByCandidateCode(candidateCode);
				List<EpfoData> allEpfoData= epfoDataRepository.findAllByCandidateCandidateCode(candidateCode);
				
				
				if(allEpfoData!=null && !allEpfoData.isEmpty()) {
					
					//Check if flow is agent and he wants to override (need to delete existing records of candidate flow)
					if (flow.equals("NOTCANDIDATE")) {
						log.info("Remittance call for  ::{}",flow);
						List<RemittanceData> remittances = remittanceRepository.findAllByCandidateCandidateCode(candidateCode);
						remittanceRepository.deleteAll(remittances);
					}
					
					List<CandidateCafExperience> candidateCafExperiences = candidateCafExperienceRepository
							.findAllByCandidateCandidateId(candidate.getCandidateId());
					//update the null dates
					cleanNullDates(candidateCafExperiences);
					List<ITRData> candidateItr= itrDataRepository.findAllByCandidateCandidateCodeOrderByFiledDateDesc(candidateCode);
					
					//checking ITR records for each yeas of the employments
					for(EpfoData epfoData : allEpfoData) {
						List<String> yearsToFetchRemittance =new ArrayList<>();
	 
						boolean remittanceApplied=false;
						
						//here needs to check tenuer gap or filing gap of same employer ITR records data.
					
						
						CandidateCafExperience candidateEPFOCafExperience=new CandidateCafExperience();
						for(CandidateCafExperience candidateCafExperience : candidateCafExperiences) {
							double similarity = CommonUtils.checkStringSimilarity(
									candidateCafExperience.getCandidateEmployerName(),
									epfoData.getCompany());
							if(similarity > 0.90 && candidateCafExperience.getServiceSourceMaster()!=null
									&& candidateCafExperience.getServiceSourceMaster().getServiceCode().equalsIgnoreCase("EPFO")) {
								log.info("SIMILARITY MATCHED FOR ::{}",epfoData.getCompany());
								candidateEPFOCafExperience = candidateCafExperience;
							}
							
						}
						
						if(candidateEPFOCafExperience!=null) {
							//check tenure gap here
							long tenureGapInMonths=candidateService.calculateTenuerGap(candidateEPFOCafExperience,candidateCafExperiences);
							log.info("CHECKING TENURE GAP for employer of the candidate ::{}{}",candidateCode ,"::GAP::" +tenureGapInMonths);
							if(tenureGapInMonths>6) {
								remittanceApplied = true;
							}else {
								//check the ITR filing gap
								
								// Filter EPFO employer ITR list
						        List<ITRData> filteredList = candidateItr.stream()
						                .filter(data -> epfoData.getCompany().equalsIgnoreCase(data.getDeductor()))
						                .collect(Collectors.toList());
						        
						        //checking filing gap
						        long itrFilingGapInMonths= candidateService.calculateITRFilingGap(filteredList);
						        log.info("CHECKING itrFilingGapInMonths GAP candidate ::{}{}",candidateCode ,"::GAP::" +itrFilingGapInMonths);
						        remittanceApplied = itrFilingGapInMonths > 6;
							}
							
						}
						
						if(remittanceApplied && candidateEPFOCafExperience!=null) {
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM-yyyy");
							Calendar calendar = Calendar.getInstance();
							
							if(candidateEPFOCafExperience.getInputDateOfJoining()!=null) {
								Date doj = candidateEPFOCafExperience.getInputDateOfJoining();
								// Convert Date to LocalDate
						        LocalDate localDateDOJ = doj.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						        String monthYearDOJ = localDateDOJ.format(formatter);
						        yearsToFetchRemittance.add(monthYearDOJ);
						        
						     // Get the next month
						        calendar.setTime(doj);
						        calendar.add(Calendar.MONTH, 1);
						        Date nextMonthDOJDate = calendar.getTime();
						        
						        LocalDate localDateNextDoj = nextMonthDOJDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						        String nextMonthYearDOJ= localDateNextDoj.format(formatter);
						        yearsToFetchRemittance.add(nextMonthYearDOJ);
							}
							
							if(candidateEPFOCafExperience.getInputDateOfExit()!=null) {
								Date doe = candidateEPFOCafExperience.getInputDateOfExit();
	 
								 // Convert Date to LocalDate
						        LocalDate localDateDOE = doe.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						        String monthYearDOE = localDateDOE.format(formatter);
						        yearsToFetchRemittance.add(monthYearDOE);
						        
						        
								// Get the previous month
						        calendar.setTime(doe);
						        calendar.add(Calendar.MONTH, -1);
						        Date previousMonthDate = calendar.getTime();
						        
						        LocalDate localDatePreDoe = previousMonthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						        String preMonthYearDOE= localDatePreDoe.format(formatter);
						        yearsToFetchRemittance.add(preMonthYearDOE);
							}
							
						}
						
						log.info("yearsToFetchRemittance ::{}",yearsToFetchRemittance);
						
						if(yearsToFetchRemittance!=null && !yearsToFetchRemittance.isEmpty()) {
							//update flag in EPFO table for remittance check of that particular employer.
							epfoData.setIsRemittanceChecked(true);
							epfoDataRepository.save(epfoData);
							
							//for these years we need to call remittance API.
							//calling the remittance token for accessing the transaction id
							String remittanceToken =getRemittanceToken();
							
							//calling the remittance API to get transaction id
							if(!remittanceToken.equals("") && !remittanceToken.isEmpty()) {
								String remittanceTID = getRemittanceTransactionID(remittanceToken);
								
								if(!remittanceTID.equals("") && !remittanceTID.isEmpty()) {
			
									//call the remitance apis for getting images..
									
									String res = getRemittanceImagesData(epfoData,yearsToFetchRemittance,remittanceTID,null);
								//	log.info("Response of remitance Images ::{}",res);
									
									//save the records in database.
									List<RemittanceDataFromApiDto> dataList = saveRemittanceData(res, candidateCode, epfoData);
									dataDTOList.addAll(dataList);
								}
								
							}
						}
						
					}
					
					if(dataDTOList!=null && !dataDTOList.isEmpty()) {
						log.info("GOT REMITTANCE RESPONCE FOR ::{}",candidateCode);
						//updating the candidate status history table only
						if (flow.equals("CANDIDATE")) {
							CandidateStatusHistory candidateStatusHistoryObj = new CandidateStatusHistory();
							candidateStatusHistoryObj.setCandidate(candidate);
							candidateStatusHistoryObj.setStatusMaster(statusMasterRepository.findByStatusCode("REMITTANCE"));
							candidateStatusHistoryObj.setCreatedBy(candidate.getCreatedBy());
							candidateStatusHistoryObj.setCreatedOn(new Date());
							candidateStatusHistoryObj.setCandidateStatusChangeTimestamp(new Date());
							candidateStatusHistoryRepository.save(candidateStatusHistoryObj);
						}
						
						svcOutcome.setData(dataDTOList);
		        		svcOutcome.setMessage("Remittance Records Retrived Successfully..");
		        		svcOutcome.setOutcome(true);
					}else {
						log.info("No Remittance records found for this candidate ::{}",candidateCode);
						svcOutcome.setData(null);
		        		svcOutcome.setMessage("No Remittance Records available..!");
		        		svcOutcome.setOutcome(true);
						
					}
					
				}else {
					log.info("EPFO RECORDS NOT PRESENT FOR ::{}",candidateCode);
					svcOutcome.setData(null);
	        		svcOutcome.setMessage("EPFO SKIPPED");
	        		svcOutcome.setOutcome(true);
					
				}
			}catch(Exception e) {
				log.error("Exception occured in getremittanceRecords::{}",e);
			}
			return svcOutcome;
		}
		
		private void cleanNullDates(List<CandidateCafExperience> candidateCafExperienceList) {
			
			//possible cases
			//1) if current employer having doj or doe null
			//logic : doj-> assigning previous employer doe+1day
			//        doe-> current date
			//2) if middle employers having doj or doe null
			//logic : doj-> assigning previous employer doe+1day
			//        doe-> assigning next employer doj-1day
			//3) if first employer having doj or doe null
			//logic : doj-> ??
			//        doe-> assigning next employer doj-1day
			
			//with single emp experience
			if (candidateCafExperienceList!=null && candidateCafExperienceList.size() == 1
					&& Objects.isNull(candidateCafExperienceList.get(0).getInputDateOfExit())) {
				entityManager.detach(candidateCafExperienceList.get(0));
				
				candidateCafExperienceList.get(0).setInputDateOfExit(new Date());
			}
	 
			if (candidateCafExperienceList!=null && candidateCafExperienceList.size() > 1) {
				for (int i = 0; i < candidateCafExperienceList.size(); i++) {
					entityManager.detach(candidateCafExperienceList.get(i));
					//setting doe to the current null doe employer
					if(i==0 && Objects.isNull(candidateCafExperienceList.get(0).getInputDateOfExit())) {
						candidateCafExperienceList.get(0).setInputDateOfExit(new Date());
					}
					//setting doj to all the null doj employer except first employer
					if(i!=(candidateCafExperienceList.size()-1) && Objects.isNull(candidateCafExperienceList.get(i).getInputDateOfJoining())) {  //if not the first employer
							if(Objects.isNull(candidateCafExperienceList.get(i+1).getInputDateOfExit())){
//								candidateCafExperienceList.get(i).setInputDateOfJoining(new Date());
							}else {
								Date preEmpDOE = candidateCafExperienceList.get(i+1).getInputDateOfExit();
								LocalDateTime ldt = LocalDateTime.ofInstant(preEmpDOE.toInstant(), ZoneId.systemDefault());
								LocalDateTime joinDate = ldt.plusDays(1);
								Date doj = Date.from(joinDate.atZone(ZoneId.systemDefault()).toInstant());
								
								candidateCafExperienceList.get(i).setInputDateOfJoining(doj);
							}
							
						
						
					}
					//setting doe to all the null doe employer except current employer
					if(i>0 && Objects.isNull(candidateCafExperienceList.get(i).getInputDateOfExit())) {
							Date curEmpDOJ = candidateCafExperienceList.get(i-1).getInputDateOfJoining();
							LocalDateTime ldt = LocalDateTime.ofInstant(curEmpDOJ.toInstant(), ZoneId.systemDefault());
							LocalDateTime exitDate = ldt.minusDays(1);
							Date doe = Date.from(exitDate.atZone(ZoneId.systemDefault()).toInstant());
							
							
							candidateCafExperienceList.get(i).setInputDateOfExit(doe);
						}
			 }
				
		 }
	  }
	 
}
