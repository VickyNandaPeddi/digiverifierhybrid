package com.aashdit.digiverifier.config.candidate.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import com.aashdit.digiverifier.common.util.RandomString;
import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.admin.repository.UserRepository;
import com.aashdit.digiverifier.config.candidate.dto.BulkUanDTO;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.SuspectEmpMaster;
import com.aashdit.digiverifier.config.candidate.model.UanSearchData;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.StatusMasterRepository;
import com.aashdit.digiverifier.config.candidate.repository.UanSearchDataRepository;
import com.aashdit.digiverifier.config.candidate.service.CandidateService;
import com.aashdit.digiverifier.config.superadmin.repository.OrganizationRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ServiceTypeConfigRepository;
import com.aashdit.digiverifier.utils.EmailRateLimiter;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CSVUtil {
	
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private CandidateRepository candidateRepository;
	
    @Autowired
    private EmailRateLimiter emailRateLimiter;

    @Autowired
	private UserRepository userRepository;
    
    @Autowired
	private UanSearchDataRepository uanSearchDataRepository;
    
    @Autowired
	private ServiceTypeConfigRepository serviceTypeConfigRepository;
	
	@Autowired
	private StatusMasterRepository statusMasterRepository;
	
	@Autowired
	private CandidateService candidateService;
	
	@Autowired
	private CandidateStatusRepository candidateStatusRepository;


	
	ResourceBundle rb = ResourceBundle.getBundle("application");
	
	// public static String TYPE = "application/vnd.ms-excel";
	public static String TYPE = "text/csv";
	  public static boolean hasCSVFormat(MultipartFile file) {
		System.out.println(file+"------inside util");
		System.out.println(file.getContentType()+"------type");
	    if (!TYPE.equals(file.getContentType())) {
	      return false;
	    }
	    return true;
	  }

	  @SuppressWarnings("unused")
		public List<Candidate> csvToCandidateList(InputStream is,String filename,String yearsToBeVerified) {
		  List<Candidate> candidateList = new ArrayList<>();
		    try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		        CSVParser csvParser = new CSVParser(fileReader,
		            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

		      
		      List<String> getFilenameFromCandidatebasic = candidateRepository.getFilename();
	          if (getFilenameFromCandidatebasic.contains(filename)) {
	             log.info("Filename already exists: {}" , filename);
	          }
	          else {
	        	  if(yearsToBeVerified == null)
	        		  yearsToBeVerified = "7";
	    	      Iterable<CSVRecord> csvRecords = csvParser.getRecords();
	    	      //getting headers
	    	      String[] header = csvParser.getHeaderMap().keySet().toArray(new String[0]);
	    	      
	    	      int candidateNoYExpIndex = -1;
	    	      int candidateCcEmailIndex = -1;
	    	      int candidateApplicantIdIndex = -1;
	    	      int candidatePanIndex = -1;
	    	      int candidateUanIndex = -1;
	    	      int recruiterNameIndex = -1;
	    	      int candidateInputSubmitDateIndex = -1;
	    	      int candidateInputSubmitTimeIndex = -1;

	              // Search for the "Recruiter Name" column in the header
	              for (int i = 0; i < header.length; i++) {
		              if (rb.getString("com.dgv.candidate.candidateNoYExp").equals(header[i].trim())) {
		            	  candidateNoYExpIndex = i;
		              }else if (rb.getString("com.dgv.candidate.candidateCcEmail").equals(header[i].trim())) {
		            	  candidateCcEmailIndex = i;
		              }else if (rb.getString("com.dgv.candidate.candidateApplicantId").equals(header[i].trim())) {
		            	  candidateApplicantIdIndex = i;
	                  }else if (rb.getString("com.dgv.candidate.candidatePan").equals(header[i].trim())) {
	                	  candidatePanIndex = i;
		              }else if (rb.getString("com.dgv.candidate.candidateUan").equals(header[i].trim())) {
		            	  candidateUanIndex = i;
	            	  
	                  }else if (rb.getString("com.dgv.candidate.candidateRecruiterName").equals(header[i].trim())) {
	                      recruiterNameIndex = i;
	                      
	                  }else if(rb.getString("com.dgv.candidate.candidateInputSubmitDate").equals(header[i].trim())) {
	                	  candidateInputSubmitDateIndex = i;
	                      
	                  }else if(rb.getString("com.dgv.candidate.candidateInputSubmitTime").equals(header[i].trim())) {
	                	  candidateInputSubmitTimeIndex = i;
	                     
	                  }
	              }
	              //now process records
	    	      try {
	    	    	  boolean invalidFormulaEntry=false;
	        	      for (CSVRecord csvRecord : csvRecords) {
	        	    	  if(!rb.getString("com.dgv.candidate.candidateName").equals("")) {
	        	    		  
	        	    		  if(( !csvRecord.get(rb.getString("com.dgv.candidate.candidateName")).equals("") && isCMDValidator(csvRecord.get(rb.getString("com.dgv.candidate.candidateName"))))
	        	    				&&  ( !csvRecord.get(rb.getString("com.dgv.candidate.candidateEmailId")).equals("") && isCMDValidator(csvRecord.get(rb.getString("com.dgv.candidate.candidateEmailId"))))
	        	    				&& ( !csvRecord.get(rb.getString("com.dgv.candidate.candidateContactNo")).equals("") && isCMDValidator(csvRecord.get(rb.getString("com.dgv.candidate.candidateContactNo"))))
	        	    				
	        	    				) {
	        	    			  String experienceFromFile= csvRecord.get(rb.getString("com.dgv.candidate.candidateNoYExp"));
	        	    			  Float floatExpFromFile=7f;
//	        	    			  log.info("experience form file::{}",experienceFromFile);
	        	    			  if (candidateNoYExpIndex != -1) {

	        	    				    if (!experienceFromFile.equals("") && isCMDValidator(experienceFromFile)) {
	        	    				    	floatExpFromFile= Float.parseFloat(experienceFromFile);
	        	    				    } else if (experienceFromFile.equals("")) {
	        	    				    	floatExpFromFile= Float.parseFloat(yearsToBeVerified);
	        	    				    }
	        	    				}
	        	    		  Candidate candidate = new Candidate(
	        		    			  csvRecord.get(rb.getString("com.dgv.candidate.candidateName")),
	        		    			  csvRecord.get(rb.getString("com.dgv.candidate.candidateEmailId")),
	        		    			  csvRecord.get(rb.getString("com.dgv.candidate.candidateContactNo")),
	        		    			  floatExpFromFile,
	        		    			//  candidateNoYExpIndex != -1 ? experienceFromFile.equals("")? Float.parseFloat(yearsToBeVerified):Float.parseFloat(experienceFromFile): null,
	        		    			  candidateCcEmailIndex != -1 && isCMDValidator(csvRecord.get(rb.getString("com.dgv.candidate.candidateCcEmail"))) ? csvRecord.get(rb.getString("com.dgv.candidate.candidateCcEmail")): null,
	        		    			  candidateApplicantIdIndex != -1 && isCMDValidator(csvRecord.get(rb.getString("com.dgv.candidate.candidateApplicantId"))) ? csvRecord.get(rb.getString("com.dgv.candidate.candidateApplicantId")): null,
	        		    			  candidatePanIndex != -1 && isCMDValidator(csvRecord.get(rb.getString("com.dgv.candidate.candidatePan"))) ? csvRecord.get(rb.getString("com.dgv.candidate.candidatePan")): null,
	        		    			  candidateUanIndex != -1 && isCMDValidator(csvRecord.get(rb.getString("com.dgv.candidate.candidateUan"))) ? csvRecord.get(rb.getString("com.dgv.candidate.candidateUan")): null,
	        		    			  recruiterNameIndex != -1 && isCMDValidator(csvRecord.get(rb.getString("com.dgv.candidate.candidateRecruiterName"))) ? csvRecord.get(rb.getString("com.dgv.candidate.candidateRecruiterName")) : null ,
	        		    			  candidateInputSubmitDateIndex != -1 && isCMDValidator(csvRecord.get(rb.getString("com.dgv.candidate.candidateInputSubmitDate"))) ? csvRecord.get(rb.getString("com.dgv.candidate.candidateInputSubmitDate")) : null ,
	        		    			  candidateInputSubmitTimeIndex != -1 && isCMDValidator(csvRecord.get(rb.getString("com.dgv.candidate.candidateInputSubmitTime"))) ? csvRecord.get(rb.getString("com.dgv.candidate.candidateInputSubmitTime")) :  null
	        		    	  );
	        	    		  String applicant = candidateApplicantIdIndex != -1 ? csvRecord.get(rb.getString("com.dgv.candidate.candidateApplicantId")): "";
	        	    		  candidate.setCandidateUploadFileName(filename);
	        	    		  if(!applicant.equals("")) {
	        	    			  candidate.setApplicantId(String.valueOf(applicant));
	        	    		  }

	        	    		  if(applicant.equals("")) {
	        	    			  SecureRandom secureRnd = new SecureRandom();
	        		    		  int n = 100000 + secureRnd.nextInt(900000);
	        		    		  candidate.setApplicantId(String.valueOf(n));
	        	    		  }
	        	    		  
	                		  if (emailRateLimiter.tryAcquire(candidate.getEmailId())) {
		                           candidateList.add(candidate);
	                		  } else {
	                			  log.info("Rate limit exceeded for email: "+ candidate.getEmailId());
	                		  } 
	        	    	  }else {
		        	    		 log.warn("CSV FILES CONTAINS INVALID DATA");
		        	    		 invalidFormulaEntry=true;
		        	    		 break;
		        	    	 }
	        	    	 }
	        	      }
	        	      
	        	      if(Boolean.TRUE.equals(invalidFormulaEntry)) {
	        	    	  //returning empty list for invalid formula insertion in file
	        	    	  log.info("CSV FILES CONTAINS INVALID DATAAND SENDING EMPTY LIST");
	        	    	  candidateList=null;
	        	    	  return candidateList;
	        	      }
					
				} catch (Exception e) {
				      log.error("fail to parse CSV file: {}" , e);
				}
	          }
		      

		      return candidateList;
		    } catch (IOException e) {
		    	log.error("fail to parse CSV file: {}" , e.getMessage());
		    	return candidateList;
		    }
		  }

	public List<User> csvToUserList(InputStream inputStream) {
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		        CSVParser csvParser = new CSVParser(fileReader,
		            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

		      List<User> userList = new ArrayList<User>();
		      Iterable<CSVRecord> csvRecords = csvParser.getRecords();
		      for (CSVRecord csvRecord : csvRecords) {
		    	  if(!rb.getString("com.dgv.agent.agentId").equals("")) {
		    		  User user = new User(
			    			  csvRecord.get(rb.getString("com.dgv.agent.agentId")),
			    			  csvRecord.get(rb.getString("com.dgv.agent.FirstName")),
			    			  csvRecord.get(rb.getString("com.dgv.agent.LastName")),
			    			  csvRecord.get(rb.getString("com.dgv.agent.EmailId")),
			    			  csvRecord.get(rb.getString("com.dgv.agent.location")),
			    			  csvRecord.get(rb.getString("com.dgv.agent.phoneNumber")),
			    			  csvRecord.get(rb.getString("com.dgv.agent.workNumber")),
			    			  csvRecord.get(rb.getString("com.dgv.agent.reportingEmailId")),
			    			  csvRecord.get(rb.getString("com.dgv.agent.EmailId").trim())
			    	  );
			    	  userList.add(user);
		    	  }
		    	 
		      }
		      return userList;
		    } catch (IOException e) {
		      throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
		    }
	}


	
//updated this all are commented
	 	public List<SuspectEmpMaster> csvToSuspectEmpMaster(InputStream is,Long organizationId) {
	     try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	         CSVParser csvParser = new CSVParser(fileReader,
	            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

	       List<SuspectEmpMaster> suspectEmpMasterList = new ArrayList<SuspectEmpMaster>();
	       Iterable<CSVRecord> csvRecords = csvParser.getRecords();
	       for (CSVRecord csvRecord : csvRecords) {
	     	  if(!rb.getString("com.dgv.suspect.suspectCompanyName").equals("")) {
	     		  SuspectEmpMaster suspectEmpMaster = new SuspectEmpMaster(
	 	    			  csvRecord.get(rb.getString("com.dgv.suspect.suspectCompanyName")),
	 	    			  csvRecord.get(rb.getString("com.dgv.suspect.address"))
	 	    	);
	     		  suspectEmpMaster.setIsActive(true);
					suspectEmpMaster.setOrganization(organizationRepository.findById(organizationId).get());
					suspectEmpMaster.setCreatedOn(new Date());

	     		suspectEmpMasterList.add(suspectEmpMaster);
	     	  }
	       }
	       return suspectEmpMasterList;
	     } catch (IOException e) {
	       throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
	     }
	   }
	 	
	 	public boolean isCMDValidator(String cellValue) {
	 		if(cellValue.equals("")&& cellValue.isEmpty()) {
	 			return true;
	 		}else {
//	 			String cmdRegex = "=cmd\\|'.*?'!'.*'";
	 			String cmdRegex = "(\\b(=|\\+|\\-|/|\\*|cmd|DDE)|\\b(AND|OR|IF|VLOOKUP|XLOOKUP|SUM|COUNT|AVERAGE|CONCAT|CONCATENATE|INDEX|MATCH|HLOOKUP|LOOKUP|OFFSET)\\b)";           
	            Pattern pattern = Pattern.compile(cmdRegex);
	            Matcher matcher = pattern.matcher(cellValue);
	            boolean containsInjection = matcher.find();
//	            log.info("Contains potential Excel formula injection: {}" + containsInjection);
	            return !containsInjection;
	 		}
	 		
	    }
	 	
	 	
	 	public List<BulkUanDTO> csvToBulkUanSearch(InputStream inputStream){

	 		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
	 				CSVParser csvParser = new CSVParser(fileReader,
	 						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

	 			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	 			Object principal = authentication.getPrincipal();
	 			String username = "";
	 			username = ((UserDetails) principal).getUsername();
	 			User findByUserName = userRepository.findByUserName(username);
	 			
	 			//checking organization configuration
                List<String> orgServices = serviceTypeConfigRepository.getServiceSourceMasterByOrgId(findByUserName.getOrganization().getOrganizationId());
	 			String getUserForUploadedBy = findByUserName.getUserFirstName();

	 			int min = 100000; 
	 			int max = 999999;
	 			SecureRandom secureRandom = new SecureRandom();
	 			int randomNum = secureRandom.nextInt(max - min + 1) + min;
	 			String bulkUanId = Integer.toString(randomNum);

	 			Date currentDate = new Date();
	 			Date uploadedOn = currentDate;
	 			ArrayList<BulkUanDTO> bulkUanSearchList = new ArrayList<BulkUanDTO>();

	 			Iterable<CSVRecord> csvRecords = csvParser.getRecords();
	 			for (CSVRecord csvRecord : csvRecords) {
	 				BulkUanDTO uanSearchData = new BulkUanDTO();
	 				if(!rb.getString("com.dgv.buklUanSearch.applicantId").equals("") && !rb.getString("com.dgv.bulkUanSearch.uanNumber").equals("") ) {
	 					String uanValue = csvRecord.get(rb.getString("com.dgv.bulkUanSearch.uanNumber"));
	 					Long uan = Long.parseLong(uanValue);
	 					int uanLength = String.valueOf(uan).length();
	 					if(uanLength == 12) {
	 						uanSearchData.setApplicantId(csvRecord.get(rb.getString("com.dgv.buklUanSearch.applicantId")));
	 						uanSearchData.setUan(csvRecord.get(rb.getString("com.dgv.bulkUanSearch.uanNumber")));
	 						uanSearchData.setUploadedBy(getUserForUploadedBy);
	 						uanSearchData.setBulkUanId(bulkUanId);				  

	 						UanSearchData uanSave = new UanSearchData(); 
	 						uanSave.setApplicantId(csvRecord.get(rb.getString("com.dgv.buklUanSearch.applicantId")));
	 						uanSave.setUan(csvRecord.get(rb.getString("com.dgv.bulkUanSearch.uanNumber")));	
	 						uanSave.setBulkUanId(bulkUanId);
	 						uanSave.setEPFOResponse("Search In Progress...");
	 						uanSave.setUploadedOn(uploadedOn);
	 						uanSave.setUploadedBy(getUserForUploadedBy);
//	 						bulkUanSearchList.add(uanSearchData);
	 						uanSearchDataRepository.save(uanSave);
	 						
	 						//saving candidates info for UAN search organization, who does not have ITR and DL services
	 					 	if(orgServices!=null && orgServices.contains("EPFO") && orgServices.contains("DNHDB")
	 					 			&& !orgServices.contains("DIGILOCKER") && !orgServices.contains("ITR")) {
	 					 		
	 					 		Candidate candidate = new Candidate();
	 					 		
	 					 		RandomString rd = new RandomString(12);
	 					 		Candidate findByCandidateCode = candidateRepository.findByCandidateCode(rd.nextString());
	 							if (findByCandidateCode != null) {
	 								rd = new RandomString(12);
	 								candidate.setCandidateCode(rd.nextString());
	 							} else {
	 								candidate.setCandidateCode(rd.nextString());
	 							}
	 							
	 							candidate.setOrganization(findByUserName.getOrganization());
	 							candidate.setCandidateName(csvRecord.get(rb.getString("com.dgv.bulkUanSearch.uanNumber")));
	 							candidate.setContactNumber(csvRecord.get(rb.getString("com.dgv.bulkUanSearch.uanNumber")));
	 							candidate.setEmailId("uan@gmail.com");
	 							candidate.setApplicantId(csvRecord.get(rb.getString("com.dgv.buklUanSearch.applicantId")));
	 							candidate.setUan(csvRecord.get(rb.getString("com.dgv.bulkUanSearch.uanNumber")));
	 							candidate.setCreatedOn(new Date());
	 							candidate.setCreatedBy(findByUserName);
	 							candidate = candidateRepository.save(candidate);
	 							
	 							
	 							CandidateStatus candidateStatus = new CandidateStatus();
	 							candidateStatus.setCandidate(candidate);
	 							candidateStatus.setCreatedBy(findByUserName);
	 							candidateStatus.setCreatedOn(new Date());
	 							candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("NEWUPLOAD"));
	 							candidateStatus = candidateStatusRepository.save(candidateStatus);
	 							candidateService.createCandidateStatusHistory(candidateStatus, "NOTCANDIDATE");
	 							
	 							candidateStatus.setLastUpdatedBy(findByUserName);
	 							candidateStatus.setLastUpdatedOn(new Date());
	 							candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("INVITATIONSENT"));
	 							candidateStatus = candidateStatusRepository.save(candidateStatus);
	 							candidateService.createCandidateStatusHistory(candidateStatus, "NOTCANDIDATE");
	 							
	 							uanSearchData.setCandidateCode(candidate.getCandidateCode());
	 					 	}
	 					 	bulkUanSearchList.add(uanSearchData);
	 					}
	 					else {
	 						log.warn("Invalid UAN: " + uanValue + ". UAN must have exactly 12 digits.");

	 					}
	 				}
	 			}
	 			return bulkUanSearchList;

	 		} catch (Exception e) {
	 			log.warn("fail to parse Excel file: " + e.getMessage());
	 			return null;
	 		}
	 	}
	 	
	 
	 	@SuppressWarnings("unused")
	 	public List<Candidate> csvToConventionalCandidateList(InputStream is,String filename,String yearsToBeVerified) {

	 		List<Candidate> candidateList = new ArrayList<>();

	 		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

	 				CSVParser csvParser = new CSVParser(fileReader,

	 						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {



	 			List<String> getFilenameFromCandidatebasic = candidateRepository.getFilename();

	 			if (getFilenameFromCandidatebasic.contains(filename)) {

	 				log.info("Filename already exists: {}" , filename);

	 			}

	 			else {

	 				if(yearsToBeVerified == null)

	 					yearsToBeVerified = "7";

	 				Iterable<CSVRecord> csvRecords = csvParser.getRecords();

	 				//getting headers

	 				String[] header = csvParser.getHeaderMap().keySet().toArray(new String[0]);

	 				int candidateNoYExpIndex = -1;

	 				int candidateCcEmailIndex = -1;

	 				int candidateApplicantIdIndex = -1;

	 				// Search for the "Recruiter Name" column in the header

	 				for (int i = 0; i < header.length; i++) {

	 					if (rb.getString("com.dgv.candidate.candidateCcEmail").equals(header[i].trim())) {

	 						candidateCcEmailIndex = i;

	 					}else if (rb.getString("com.dgv.candidate.candidateApplicantId").equals(header[i].trim())) {

	 						candidateApplicantIdIndex = i;

	 					}

	 				}

	 				//now process records

	 				try {

	 					boolean invalidFormulaEntry=false;

	 					for (CSVRecord csvRecord : csvRecords) {

	 						if(!rb.getString("com.dgv.candidate.candidateName").equals("")) {

	 							if(( !csvRecord.get(rb.getString("com.dgv.candidate.candidateName")).equals("") && isCMDValidator(csvRecord.get(rb.getString("com.dgv.candidate.candidateName"))))

	 									&&  ( !csvRecord.get(rb.getString("com.dgv.candidate.candidateEmailId")).equals("") && isCMDValidator(csvRecord.get(rb.getString("com.dgv.candidate.candidateEmailId"))))

	 									&& ( !csvRecord.get(rb.getString("com.dgv.candidate.candidateContactNo")).equals("") && isCMDValidator(csvRecord.get(rb.getString("com.dgv.candidate.candidateContactNo"))))

	 									) {

	 								//		        	    			  String experienceFromFile= csvRecord.get(rb.getString("com.dgv.candidate.candidateNoYExp"));

	 								String experienceFromFile= "";

	 								Float floatExpFromFile=7f;

//	 								log.info("experience form file::{}",experienceFromFile);

	 								if (candidateNoYExpIndex != -1) {

	 									if (!experienceFromFile.equals("") && isCMDValidator(experienceFromFile)) {

	 										floatExpFromFile= Float.parseFloat(experienceFromFile);

	 									} else if (experienceFromFile.equals("")) {

	 										floatExpFromFile= Float.parseFloat(yearsToBeVerified);

	 									}

	 								}

	 								Candidate candidate = new Candidate();

	 								candidate.setCandidateName(csvRecord.get(rb.getString("com.dgv.candidate.candidateName")));

	 								candidate.setEmailId(csvRecord.get(rb.getString("com.dgv.candidate.candidateEmailId")));

	 								candidate.setContactNumber(csvRecord.get(rb.getString("com.dgv.candidate.candidateContactNo")));

	 								candidate.setExperienceInMonth(floatExpFromFile);

	 								if(candidateCcEmailIndex != -1 && isCMDValidator(csvRecord.get(rb.getString("com.dgv.candidate.candidateCcEmail"))))

	 									candidate.setCcEmailId(csvRecord.get(rb.getString("com.dgv.candidate.candidateCcEmail")));

	 								if(candidateApplicantIdIndex != -1 && isCMDValidator(csvRecord.get(rb.getString("com.dgv.candidate.candidateApplicantId"))))

	 									candidate.setApplicantId(csvRecord.get(rb.getString("com.dgv.candidate.candidateApplicantId")));

	 								String applicant = candidateApplicantIdIndex != -1 ? csvRecord.get(rb.getString("com.dgv.candidate.candidateApplicantId")): "";

	 								candidate.setCandidateUploadFileName(filename);

	 								if(!applicant.equals("")) {

	 									candidate.setApplicantId(String.valueOf(applicant));

	 								}

	 								if(applicant.equals("")) {

	 									SecureRandom secureRnd = new SecureRandom();

	 									int n = 100000 + secureRnd.nextInt(900000);

	 									candidate.setApplicantId(String.valueOf(n));

	 								}

	 								candidate.setAccountName(csvRecord.get(rb.getString("com.dgv.candidate.accountName".trim())));

	 								candidate.setConventionalCandidate(true);

	 								if (emailRateLimiter.tryAcquire(candidate.getEmailId())) {

	 									candidateList.add(candidate);

	 								} else {

	 									log.info("Rate limit exceeded for email: "+ candidate.getEmailId());

	 								} 

	 							}else {

	 								log.warn("CSV FILES CONTAINS INVALID DATA");

	 								invalidFormulaEntry=true;

	 								break;

	 							}

	 						}

	 					}

	 					if(Boolean.TRUE.equals(invalidFormulaEntry)) {

	 						//returning empty list for invalid formula insertion in file

	 						log.info("CSV FILES CONTAINS INVALID DATAAND SENDING EMPTY LIST");

	 						candidateList=null;

	 						return candidateList;

	 					}

	 				} catch (Exception e) {

	 					log.error("fail to parse CSV file: {}" , e);

	 				}

	 			}

	 			return candidateList;
	 		} catch (IOException e) {
	 			log.error("fail to parse CSV file: {}" , e.getMessage());
	 			return candidateList;
	 		}

	 	}
}
