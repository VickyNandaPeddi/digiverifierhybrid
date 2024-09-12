package com.aashdit.digiverifier.config.candidate.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.admin.repository.UserRepository;
import com.aashdit.digiverifier.config.candidate.dto.BulkPanToUanDTO;
import com.aashdit.digiverifier.config.candidate.dto.BulkUanDTO;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.CandidateVerificationState;
import com.aashdit.digiverifier.config.candidate.model.OrganisationScope;
import com.aashdit.digiverifier.config.candidate.model.SuspectClgMaster;
import com.aashdit.digiverifier.config.candidate.model.SuspectEmpMaster;
import com.aashdit.digiverifier.config.candidate.model.UanSearchData;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateVerificationStateRepository;
import com.aashdit.digiverifier.config.candidate.repository.OrganisationScopeRepository;
import com.aashdit.digiverifier.config.candidate.repository.StatusMasterRepository;
import com.aashdit.digiverifier.config.candidate.repository.UanSearchDataRepository;
import com.aashdit.digiverifier.config.candidate.service.CandidateService;
import com.aashdit.digiverifier.config.superadmin.model.ToleranceConfig;
import com.aashdit.digiverifier.config.superadmin.repository.OrganizationRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ServiceTypeConfigRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ToleranceConfigRepository;
import com.aashdit.digiverifier.epfo.model.EpfoData;
import com.aashdit.digiverifier.utils.EmailRateLimiter;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.util.Date;
// import java.util.concurrent.ThreadLocalRandom; 
import com.aashdit.digiverifier.common.util.RandomString;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ExcelUtil {
	
	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private CandidateRepository candidateRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UanSearchDataRepository uanSearchDataRepository;
	
	@Autowired
	private CandidateStatusRepository candidateStatusRepository;
	
	@Autowired
	private CandidateVerificationStateRepository candidateVerificationStateRepository;
	
	@Autowired
	private OrganisationScopeRepository organisationScopeRepository;
	
	@Autowired
	private ServiceTypeConfigRepository serviceTypeConfigRepository;
	
	@Autowired
	private StatusMasterRepository statusMasterRepository;
	
	@Autowired
	private CandidateService candidateService;
	
    @Autowired
    private EmailRateLimiter emailRateLimiter;
    
    @Autowired
	private ToleranceConfigRepository toleranceConfigRepository;
	
	 public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	  public static boolean hasExcelFormat(MultipartFile file) {
	    if (!TYPE.equals(file.getContentType())) {
	      return false;
	    }
	    return true;
	  }
	  
	  static {
	    // Adjust the min inflate ratio
	    ZipSecureFile.setMinInflateRatio(0);  // Adjust this value as needed
	  }
      
	  public  List<Candidate> excelToCandidate(InputStream is,String filename,String yearsToBeVerified) {
	        try {
	        	log.info("FILENAME::>>>"+filename);
	        	  if(yearsToBeVerified == null)
	        		  yearsToBeVerified = "7";
	              ArrayList<Candidate> candidateList = new ArrayList<Candidate>();
	              XSSFWorkbook workbook = new XSSFWorkbook(is);
	              XSSFSheet worksheet = workbook.getSheetAt(0);
	              
	              Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	              Object principal = authentication.getPrincipal();
	              log.info("principal:: {}",principal.toString());
	              String username = "";
	                  username = ((UserDetails) principal).getUsername();
	                  log.info("username 2::?? {}",username);
	                  User findByUserName = userRepository.findByUserName(username);
	                  log.info("ORgID::{}",findByUserName.getOrganization().getOrganizationName());	                  
	                  log.info(principal.toString());
	             
	                  if(findByUserName.getOrganization().getOrganizationName().equalsIgnoreCase("Accolite Digital India Pvt Ltd")){
		            	  log.info("accolite is true::");
		            	  try {
		            		  
			                  for(int i=1;i<worksheet.getPhysicalNumberOfRows() ;i++) {
			                      Candidate candidate = new Candidate();
			                      XSSFRow row = worksheet.getRow(i);
			                        XSSFRow header = worksheet.getRow(0);
			                        log.info("xls heading row {}", header.getLastCellNum());
			                        if (getCellValue(row, 0) != null && !getCellValue(row, 0).equals("") &&
				                    	    getCellValue(row, 1) != null && !getCellValue(row, 1).equals("")){
			                            candidate.setCandidateName(getCellValue(row, 0));
			                            candidate.setContactNumber(getCellValue(row, 1).trim());
				                        candidate.setEmailId(getCellValue(row, 2).trim());


			                            if(!getCellValue(row, 7).trim().isEmpty()|| getCellValue(row, 7).trim().equals("")) {

			                                candidate.setAccountName(null);
			                                log.info("AccountName is null= {}"+candidate.getAccountName());

			                            }
			                                if(getCellValue(row, 7).isEmpty()) {
			                                candidate.setAccountName(getCellValue(row, 6));
			                                candidate.setShowvalidation(false);

			                                 }

			                                if (getCellValue(row, 7) != null && getCellValue(row, 6) != null) {

			                                         log.info("getCEllValue::{}"+getCellValue(row, 6));
			                                        String cellValue = getCellValue(row, 6);
			                                        log.info("CELLVALUE:: {}"+cellValue);
			                                        if(cellValue.trim().equalsIgnoreCase("true")|| cellValue.trim().equalsIgnoreCase("TRUE()")) {
			                                        candidate.setShowvalidation(true);
			                                        log.info("True:::::: {}");
			                                        }
			                                        
			                                     if(cellValue.trim().equalsIgnoreCase("false") || cellValue.trim().equalsIgnoreCase("")
			                                    		 || cellValue==null || cellValue.trim().equalsIgnoreCase("FALSE()")) {
			                                                candidate.setShowvalidation(false);
			                                                log.info("False::::::");
			                                         }
			                                                candidate.setAccountName(getCellValue(row, 7));

			                                    
			                                }

			                            SecureRandom secureRnd = new SecureRandom();
			                            int n = 100000 + secureRnd.nextInt(900000);

			                            if(header.getLastCellNum() == 7 && getCellValue(header, 3).equals("Applicant Id")) {
			                                if(!getCellValue(row, 3).equals("")) {
			                                    candidate.setApplicantId(getCellValue(row, 3));
			                                }
			                                else {
			                                    candidate.setApplicantId(String.valueOf(n));
			                                }

			                                //candidate.setExperienceInMonth(!getCellValue(row, 4).equals("")?Integer.valueOf(getCellValue(row, 4)):null);
			                                candidate.setExperienceInMonth(!getCellValue(row, 4).equals("") ? Float.parseFloat(getCellValue(row, 4)) : Float.valueOf(yearsToBeVerified));
				                            candidate.setCcEmailId(getCellValue(row, 5));
			                            } else {

			                                candidate.setApplicantId(String.valueOf(n));
			                               // candidate.setExperienceInMonth(!getCellValue(row, 4).equals("")?Integer.valueOf(getCellValue(row, 4)):null);
			                                candidate.setExperienceInMonth(!getCellValue(row, 4).equals("") ? Float.parseFloat(getCellValue(row, 4)) : Float.valueOf(yearsToBeVerified));
				                            candidate.setCcEmailId(getCellValue(row, 5).trim());
			                            }
			                            if(getCellValue(row,8) != null || getCellValue(row, 9)!= null) {		                            
				                        	candidate.setItrPanNumber(getCellValue(row, 8));
				                        	candidate.setUan(getCellValue(row, 9));
				                        }
			                            
			                            if(getCellValue(row, 10) != null || getCellValue(row, 11)!= null
				                        		|| getCellValue(row,12) != null) {		                            
				                        	candidate.setRecruiterName(getCellValue(row, 10));
				                        	candidate.setInputSubmitDate(getCellValue(row, 11));
				                        	candidate.setInputSubmitTime(getCellValue(row, 12));
				                        }

			                			if (emailRateLimiter.tryAcquire(candidate.getEmailId())) {
				                            candidateList.add(candidate);
			                			} else {
			                				log.info("Rate limit exceeded for email: "+ candidate.getEmailId());
			                			}
			                        }
			                    }
							
						} catch (Exception e) {
							log.info("ExcelUtils:::"+e.getMessage());
						}

		              }        
	              
	              else {
	            	  
	            	  List<String> getFilenameFromCandidatebasic = candidateRepository.getFilename();
		              if (getFilenameFromCandidatebasic.contains(filename)) {
		                  log.info("Filename already exists: " + filename);
		              }
	            	  
		              else {
		            	  try {
				              for(int i=1;i<worksheet.getPhysicalNumberOfRows() ;i++) {
				                  Candidate candidate = new Candidate();
				                  candidate.setCandidateUploadFileName(filename);
				                  XSSFRow row = worksheet.getRow(i);
				                    XSSFRow header = worksheet.getRow(0);
					                  System.out.println("Pan NUMBER ::"+getCellValue(row, 8));
					                  System.out.println("Uan Number ::"+getCellValue(row, 9));
				                    log.info("xls heading row {}", header.getLastCellNum());
				                    if (getCellValue(row, 0) != null && !getCellValue(row, 0).equals("") &&
				                    	    getCellValue(row, 1) != null && !getCellValue(row, 1).equals("")){
				                        candidate.setCandidateName(getCellValue(row, 0));
				                        candidate.setContactNumber(getCellValue(row, 1).trim());
				                        candidate.setEmailId(getCellValue(row, 2).trim());
//				                        candidate.setConventionalCandidate(false);


				                        if(!getCellValue(row, 7).trim().isEmpty()|| getCellValue(row, 7).trim().equals("")) {

				                            candidate.setAccountName(null);
				                            log.info("AccountName is null= "+candidate.getAccountName());

				                        }

				                            if(getCellValue(row, 7).isEmpty()) {
				                            candidate.setAccountName(getCellValue(row, 6));
				                            candidate.setShowvalidation(false);

				                             }

				                            if (getCellValue(row, 7) != null && getCellValue(row, 6) != null) {

				                                     log.info("getCEllValue::"+getCellValue(row, 6));
				                                    String cellValue = getCellValue(row, 6);
				                                    log.info("CELLVALUE::"+cellValue);
				                                    log.info("Formula: {}" , row.getCell(6).getCellType());
				                                    if(cellValue.trim().equalsIgnoreCase("true")|| cellValue.trim().equalsIgnoreCase("TRUE()")) {
				                                    candidate.setShowvalidation(true);
				                                    log.info("True::::::");
				                                    }
				                                    
				                                 if(cellValue.trim().equalsIgnoreCase("false") || cellValue.trim().equalsIgnoreCase("")
				                                		|| cellValue==null || cellValue.trim().equalsIgnoreCase("FALSE()")) {
				                                            candidate.setShowvalidation(false);
				                                            log.info("False::::::");
				                                     }
				                                            candidate.setAccountName(getCellValue(row, 7));  
				                            }

				                        SecureRandom secureRnd = new SecureRandom();
				                        int n = 100000 + secureRnd.nextInt(900000);

				                        if(header.getLastCellNum() == 7 && getCellValue(header, 3).equals("Applicant Id")) {
				                            if(!getCellValue(row, 3).equals("")) {
				                                candidate.setApplicantId(getCellValue(row, 3));
				                            }
				                            else {
				                                candidate.setApplicantId(String.valueOf(n));
				                            }

				                            //candidate.setExperienceInMonth(!getCellValue(row, 4).equals("")?Integer.valueOf(getCellValue(row, 4)):null);
				                            candidate.setExperienceInMonth(!getCellValue(row, 4).equals("") ? Float.parseFloat(getCellValue(row, 4)) : Float.valueOf(yearsToBeVerified));
				                            candidate.setCcEmailId(getCellValue(row, 5));

				                        } else {
				                            candidate.setApplicantId(String.valueOf(getCellValue(row, 3)));
				                            
				                        if (getCellValue(row, 3).equals("")) {
				                        	candidate.setApplicantId(String.valueOf(n));
				                        }
				                        //candidate.setExperienceInMonth(!getCellValue(row, 4).equals("")?Integer.valueOf(getCellValue(row, 4)):null);
				                        candidate.setExperienceInMonth(!getCellValue(row, 4).equals("") ? Float.parseFloat(getCellValue(row, 4)) : Float.valueOf(yearsToBeVerified));
				                        candidate.setCcEmailId(getCellValue(row, 5).trim());


				                        }
				                        
				                        if(getCellValue(row,8) != null || getCellValue(row, 9)!= null) {		                            
				                        	candidate.setItrPanNumber(getCellValue(row, 8));
				                        	candidate.setUan(getCellValue(row, 9));;
				                        }
				                        
				                        if(getCellValue(row, 10) != null || getCellValue(row, 11)!= null
				                        		|| getCellValue(row,12) != null) {		                            
				                        	candidate.setRecruiterName(getCellValue(row, 10));
				                        	candidate.setInputSubmitDate(getCellValue(row, 11));
				                        	candidate.setInputSubmitTime(getCellValue(row, 12));
				                        }
				                        
				                        if(getCellValue(row,13) != null && findByUserName.getOrganization().getOrganizationName().equalsIgnoreCase("CAPGEMINI TECHNOLOGY SERVICES INDIA LIMITED")) {
											log.info("getCellValue(row, 13) for CANDIDATE EXCEL::{}",getCellValue(row, 13));
											candidate.setCgSfCandidateId(getCellValue(row, 13));
										}

			                			if (emailRateLimiter.tryAcquire(candidate.getEmailId())) {
				                            candidateList.add(candidate);
			                			} else {
			                				log.info("Rate limit exceeded for email: "+ candidate.getEmailId());
			                			}				                    }
				                }
						} catch (Exception e) {
							log.info("ExcelUtils:::"+e.getMessage());;
						}

		              }
	              }
	              
	              return candidateList;
	            }

	              catch (IOException e) {
	              throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
	            }
	      }
	  
	  private String getCellValue(Row row, int cellNo) {
		  String cellValue="";
		  try {
	        DataFormatter formatter = new DataFormatter();
	        Cell cell = row.getCell(cellNo);
	        cellValue=formatter.formatCellValue(cell);
		  }
		  catch(Exception ex) {
			  log.error("Exception occured in getCellValue method in ExcelUtil-->"+ex);
		  }
		  return cellValue;
	  }

	public List<User> excelToUserList(InputStream inputStream) {
		 try {
	    	  ArrayList<User> userList = new ArrayList<User>();
	    	  XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
	          XSSFSheet worksheet = workbook.getSheetAt(0);
			  for(int i=1;i<worksheet.getPhysicalNumberOfRows() ;i++) {
				  	User user = new User();
		            XSSFRow row = worksheet.getRow(i);
		            if(!getCellValue(row, 0).equals("")){
		            	user.setEmployeeId(getCellValue(row, 0));
			            user.setUserFirstName(getCellValue(row, 1));
			            user.setUserLastName(getCellValue(row, 2));
			            user.setUserEmailId(getCellValue(row, 3));
			            user.setUserName(getCellValue(row, 3).trim());
			            user.setLocation(getCellValue(row, 4));
			            user.setUserMobileNum(getCellValue(row, 5));
			            user.setUserLandlineNum(getCellValue(row, 6));
			            user.setReportingEmailId(getCellValue(row, 7));
			            userList.add(user);
		            }
		            
		        }
		      return userList;
		    } catch (IOException e) {
		      throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
		    }
	}

	public List<SuspectEmpMaster> excelToSuspectEmpMaster(InputStream inputStream,Long organizationId) {
		try {
	    	  ArrayList<SuspectEmpMaster> suspectEmpMasterList = new ArrayList<SuspectEmpMaster>();
	    	  XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
	          XSSFSheet worksheet = workbook.getSheetAt(0);
			  for(int i=1;i<worksheet.getPhysicalNumberOfRows() ;i++) {
				  SuspectEmpMaster suspectEmpMaster = new SuspectEmpMaster();
		            XSSFRow row = worksheet.getRow(i);
		            if(!getCellValue(row, 0).equals("")) {
						 
		            	suspectEmpMaster.setSuspectCompanyName(getCellValue(row, 0));
			            suspectEmpMaster.setAddress(getCellValue(row, 1));
						
			            suspectEmpMaster.setIsActive(true);
						suspectEmpMaster.setOrganization(organizationRepository.findById(organizationId).get());
						suspectEmpMaster.setCreatedOn(new Date());
						
			            suspectEmpMasterList.add(suspectEmpMaster);
						
		            }
		            
		        }
		      return suspectEmpMasterList;
		    } catch (IOException e) {
		      throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
		   }
	}

	public List<SuspectClgMaster> excelToSuspectClgMaster(InputStream inputStream) {
		try {
	    	  ArrayList<SuspectClgMaster> suspectClgMasterList = new ArrayList<SuspectClgMaster>();
	    	  XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
	          XSSFSheet worksheet = workbook.getSheetAt(1);
			  for(int i=1;i<worksheet.getPhysicalNumberOfRows() ;i++) {
				  SuspectClgMaster suspectClgMaster = new SuspectClgMaster();
		            XSSFRow row = worksheet.getRow(i);
		            if(!getCellValue(row, 0).equals("")) {
		            	suspectClgMaster.setSuspectInstitutionName(getCellValue(row, 0));
			            suspectClgMaster.setAssociatedInstitution(getCellValue(row, 1));
			            suspectClgMaster.setAddress(getCellValue(row, 2));
			            suspectClgMaster.setSource(getCellValue(row, 3));
			            suspectClgMaster.setClassifiedAs(getCellValue(row, 4));
			            suspectClgMaster.setDateModified(getCellValue(row, 5));
			            suspectClgMaster.setVendor(getCellValue(row, 6));
			            suspectClgMaster.setIsActive(true);
			            suspectClgMasterList.add(suspectClgMaster);
		            }
		        }
		      return suspectClgMasterList;
		    } catch (IOException e) {
		      throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
		   }
	}
	
	public List<BulkUanDTO> excelToBulkUanSearch(InputStream inputStream){
		ArrayList<BulkUanDTO> bulkUanSearchList = new ArrayList<>();
		try {
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
			
	    	  XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
	          XSSFSheet worksheet = workbook.getSheetAt(0);
	          
	          //checking header values null
	          XSSFRow headerRow = worksheet.getRow(0); //header row
	          if(getCellValue(headerRow, 0).equals("") || getCellValue(headerRow, 1).equals("") || 
	        		  getCellValue(headerRow, 2).equals("")) {
	        	  log.info("LOOKS LIKE OLD UPLOAD FILE IS USING..!");
	        	  return bulkUanSearchList;
	          }
	          
			  for(int i=1;i<worksheet.getPhysicalNumberOfRows() ;i++) {
				  BulkUanDTO uanSearchData = new BulkUanDTO();
		            XSSFRow row = worksheet.getRow(i);
				  if(!getCellValue(row, 0).equals("")) {
					  uanSearchData.setApplicantId(getCellValue(row, 0));
					  uanSearchData.setUan(getCellValue(row, 2));
					  uanSearchData.setUploadedBy(getUserForUploadedBy);
					  uanSearchData.setBulkUanId(bulkUanId);
					  uanSearchData.setTotalRecordFetched(0);
					  uanSearchData.setTotalRecordFailed(0);
					  
			          UanSearchData uanSave = new UanSearchData(); 
					  uanSave.setApplicantId(getCellValue(row, 0));
					  uanSave.setUan(getCellValue(row, 2));	
					  uanSave.setBulkUanId(bulkUanId);
					  uanSave.setEPFOResponse("Search In Progress...");
					  uanSave.setUploadedOn(uploadedOn);
					  uanSave.setUploadedBy(getUserForUploadedBy);
//					  bulkUanSearchList.add(uanSearchData);
					  
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
						
						if(!getCellValue(row, 1).equals("") && StringUtils.isNumeric(getCellValue(row, 1))) {
							log.info("getCellValue(row, 1) for SFCANDIDATEID::{}",getCellValue(row, 1));
							candidate.setCgSfCandidateId(getCellValue(row, 1));
						}
						candidate.setOrganization(findByUserName.getOrganization());
						candidate.setCandidateName(getCellValue(row, 2));
						candidate.setContactNumber(getCellValue(row, 2));
						candidate.setEmailId("uan@gmail.com");
						candidate.setApplicantId(getCellValue(row, 0));
						candidate.setUan(getCellValue(row, 2));
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

			  }
			  
			  for(BulkUanDTO bulkUanDTO :bulkUanSearchList) {
				  bulkUanDTO.setTotalRecordUploaded(bulkUanSearchList.size());
			  }

			  return bulkUanSearchList;
		} catch (IOException e) {
		      log.info("fail to parse Excel file: " + e.getMessage());
		}
		return bulkUanSearchList;	
	}
	
	public ResponseEntity<byte[]> downloadCandidateStatusTrackerExcel(List<Candidate> candidatesList) throws IOException {
		try {
			// Create Excel Workbook
	        Workbook workbook = new XSSFWorkbook();
	        Sheet sheet = workbook.createSheet("Candidate_Status_Tracker");
	        
	     // Create header row
	        Row headerRow = sheet.createRow(0);
	     // Set font style to bold
	        Font font = workbook.createFont();
	        font.setBold(true);
	        font.setFontHeightInPoints((short) 14);

	        CellStyle style = workbook.createCellStyle();
	        style.setFont(font);
	        
	        Cell cell0 =headerRow.createCell(0);
	        cell0.setCellValue("Sl.no");
	        cell0.setCellStyle(style);
	        sheet.setColumnWidth(0, 4000);
	        Cell cell1 =headerRow.createCell(1);
	        cell1.setCellValue("Applicant ID / RID");
	        cell1.setCellStyle(style);
	        sheet.setColumnWidth(1, 4000);
	        Cell cell2 =headerRow.createCell(2);
	        cell2.setCellValue("Applicant Name");
	        cell2.setCellStyle(style);
	        sheet.setColumnWidth(2, 5000);
	        Cell cell3 =headerRow.createCell(3);
	        cell3.setCellValue("Mobile Number");
	        cell3.setCellStyle(style);
	        sheet.setColumnWidth(3, 5000);
	        Cell cell4 =headerRow.createCell(4);
	        cell4.setCellValue("Email ID");
	        cell4.setCellStyle(style);
	        sheet.setColumnWidth(4, 10000);
	        Cell cell5 =headerRow.createCell(5);
	        cell5.setCellValue("Recruiter Name");
	        cell5.setCellStyle(style);
	        sheet.setColumnWidth(5, 5000);
	        Cell cell6 =headerRow.createCell(6);
	        cell6.setCellValue("Profile Submitted ( Yes/No )");
	        cell6.setCellStyle(style);
	        sheet.setColumnWidth(6, 4000);
	        Cell cell7 =headerRow.createCell(7);
	        cell7.setCellValue("Input Submission Date");
	        cell7.setCellStyle(style);
	        sheet.setColumnWidth(7, 5000);
	        Cell cell8 =headerRow.createCell(8);
	        cell8.setCellValue("Input Submission Time");
	        cell8.setCellStyle(style);
	        sheet.setColumnWidth(8, 4000);
	        Cell cell9 =headerRow.createCell(9);
	        cell9.setCellValue("Workflow Status");
	        cell9.setCellStyle(style);
	        sheet.setColumnWidth(9, 6000);
	        Cell cell10 =headerRow.createCell(10);
	        cell10.setCellValue("Workflow Status Timestamp");
	        cell10.setCellStyle(style);
	        sheet.setColumnWidth(10, 7000);
	        Cell cell11 =headerRow.createCell(11);
	        cell11.setCellValue("Comments");
	        cell11.setCellStyle(style);
	        sheet.setColumnWidth(11, 10000);
	        Cell cell12 =headerRow.createCell(12);
	        cell12.setCellValue("Case Initaited On");
	        cell12.setCellStyle(style);
	        sheet.setColumnWidth(12, 7000);
	        Cell cell13 =headerRow.createCell(13);
	        cell13.setCellValue("CWF Completed On");
	        cell13.setCellStyle(style);
	        sheet.setColumnWidth(13, 7000);
	        Cell cell14 =headerRow.createCell(14);
	        cell14.setCellValue("Report Delivered On");
	        cell14.setCellStyle(style);
	        sheet.setColumnWidth(14, 7000);
	        
	     // Create data rows
	        int rowNum = 1;
	        int srNum = 1;
	        for (Candidate candidate : candidatesList) {
	            
	            
	            CandidateVerificationState verificationStatus = candidateVerificationStateRepository
	            		                                                .findByCandidateCandidateId(candidate.getCandidateId());
	            CandidateStatus candidateStatus = candidateStatusRepository
						                                             .findByCandidateCandidateCode(candidate.getCandidateCode());
	            OrganisationScope organisationScope = organisationScopeRepository.findByCandidateId(candidate.getCandidateId());
	            
	            String caseInitiatedDate = formatter.format(candidate.getCreatedOn());
	            String submittedDate = candidate.getSubmittedOn()!=null ? formatter.format(candidate.getSubmittedOn()) : null;
	            String reportDeliveredDate = verificationStatus!=null && verificationStatus.getInterimReportTime() != null ? formatter.format(Date.from(verificationStatus.getInterimReportTime().toInstant())) : null;
	            		
	            String workflowStatus="";
	            Boolean uan = candidate.getIsUanSkipped() != null ? candidate.getIsUanSkipped() : false;
	            Boolean loaAccepted = candidate.getIsLoaAccepted();
				if (Boolean.TRUE.equals(candidateStatus.getStatusMaster().getStatusCode().equals("DIGILOCKER") && uan)
						|| Boolean.TRUE.equals(candidateStatus.getStatusMaster().getStatusCode().equals("ITR") && uan)) {
					workflowStatus="EPFO Skipped";
				} else if(Boolean.TRUE.equals(candidateStatus.getStatusMaster().getStatusCode().equals("INVITATIONSENT") && loaAccepted)
						|| Boolean.TRUE.equals(candidateStatus.getStatusMaster().getStatusCode().equals("REINVITE") && loaAccepted)){
					workflowStatus="LOA Completed";
				}else if(Boolean.TRUE.equals(candidateStatus.getStatusMaster().getStatusCode().equals("INTERIMREPORT"))
						       && verificationStatus!=null && verificationStatus.getInterimColorCodeStatus()!=null){
					
					workflowStatus=verificationStatus.getInterimColorCodeStatus().getColorName();
				}else if(Boolean.TRUE.equals(candidateStatus.getStatusMaster().getStatusCode().equals("PENDINGAPPROVAL"))
					       && verificationStatus!=null && verificationStatus.getPreApprovalColorCodeStatus()!=null) {
					
					workflowStatus=verificationStatus.getPreApprovalColorCodeStatus().getColorName();
					
				}else {
					workflowStatus=candidateStatus.getStatusMaster().getStatusName();
				}
				
				String workflowStatusDate = formatter.format(candidateStatus.getLastUpdatedOn());
				
				String comments="";
				if(Boolean.TRUE.equals(candidateStatus.getStatusMaster().getStatusCode().equals("NEWUPLOAD"))
					||	Boolean.TRUE.equals(candidateStatus.getStatusMaster().getStatusCode().equals("INVITATIONSENT"))
					|| Boolean.TRUE.equals(candidateStatus.getStatusMaster().getStatusCode().equals("INVITATIONSENT") && loaAccepted)
					||	Boolean.TRUE.equals(candidateStatus.getStatusMaster().getStatusCode().equals("DIGILOCKER"))
					||	Boolean.TRUE.equals(candidateStatus.getStatusMaster().getStatusCode().equals("ITR"))
					||	Boolean.TRUE.equals(candidateStatus.getStatusMaster().getStatusCode().equals("EPFO"))) {
					
					comments= "Recruiter to follow Up";
				}else if(Boolean.TRUE.equals(candidateStatus.getStatusMaster().getStatusCode().equals("PENDINGAPPROVAL"))){
					comments= "DV Task to be completed";
				}
				
				if(organisationScope!=null && 
						((organisationScope.getDualEmployment() != null && !organisationScope.getDualEmployment().isEmpty())
							|| (organisationScope.getUndisclosed() != null && !organisationScope.getUndisclosed().isEmpty())
							|| (organisationScope.getDataNotFound() != null && !organisationScope.getDataNotFound().isEmpty())
							|| (organisationScope.getDNHDB() != null && !organisationScope.getDNHDB().isEmpty())
							|| (organisationScope.getTenureMismatch() != null && !organisationScope.getTenureMismatch().isEmpty())
							|| (organisationScope.getOverseasEmployment() != null && !organisationScope.getOverseasEmployment().isEmpty())
							|| (organisationScope.getOthers() != null && !organisationScope.getOthers().isEmpty())
								)) {
					comments="";
					String dualEmploymentComment= organisationScope.getDualEmployment();
					String undisclosedComment= organisationScope.getUndisclosed();
					String dataNotFoundComment= organisationScope.getDataNotFound();
					String dnhdbComment= organisationScope.getDNHDB();
					String tenuerMismatchComment= organisationScope.getTenureMismatch();
					String overseaseEmploymentComment= organisationScope.getOverseasEmployment();
					String otherComment= organisationScope.getOthers();

					if (dualEmploymentComment != null && !dualEmploymentComment.isEmpty()) {
					    comments = "Dual Employment (Moonlighting): "+dualEmploymentComment;
					}
					comments += "\n";

					if (undisclosedComment != null && !undisclosedComment.isEmpty()) {
					    comments += "Undisclosed: "+undisclosedComment;
					}
					comments += "\n";

					if (dataNotFoundComment != null && !dataNotFoundComment.isEmpty()) {
					    comments += "Data Not Found: "+dataNotFoundComment;
					}
					comments += "\n";

					if (dnhdbComment != null && !dnhdbComment.isEmpty()) {
					    comments += "DNH DB: "+dnhdbComment;
					}
					comments += "\n";

					if (tenuerMismatchComment != null && !tenuerMismatchComment.isEmpty()) {
					    comments += "Tenure Mismatch: "+tenuerMismatchComment;
					}
					comments += "\n";

					if (overseaseEmploymentComment != null && !overseaseEmploymentComment.isEmpty()) {
					    comments += "Overseas Employment: "+overseaseEmploymentComment;
					}
					comments += "\n";

					if (otherComment != null && !otherComment.isEmpty()) {
					    comments += "Others: "+otherComment;
					}
				}
				
                if(Boolean.TRUE.equals(candidateStatus.getStatusMaster().getStatusCode().equals("INTERIMREPORT"))
                		&& verificationStatus!=null && verificationStatus.getInterimColorCodeStatus()!=null
                		&& verificationStatus.getInterimColorCodeStatus().getColorName().equalsIgnoreCase("Green")){
					
                	comments="";
				}
                comments = !comments.equals("") ? comments.trim() :"";
                		
				 Font rfont = workbook.createFont();
				 rfont.setFontHeightInPoints((short) 13);
                 CellStyle rstyle = workbook.createCellStyle();
                 rstyle.setFont(rfont);
			     
				Row row = sheet.createRow(rowNum++);
				Cell rcell0 = row.createCell(0);
				rcell0.setCellValue(srNum);
				rcell0.setCellStyle(rstyle);
				
				Cell rcell1 = row.createCell(1);
				rcell1.setCellValue(candidate.getApplicantId());
				rcell1.setCellStyle(rstyle);
				
				Cell rcell2 = row.createCell(2);
				rcell2.setCellValue(candidate.getCandidateName());
				rcell2.setCellStyle(rstyle);
				
				Cell rcell3 = row.createCell(3);
				rcell3.setCellValue(candidate.getContactNumber());
				rcell3.setCellStyle(rstyle);
				
				Cell rcell4 = row.createCell(4);
				rcell4.setCellValue(candidate.getEmailId());
				rcell4.setCellStyle(rstyle);
				
				Cell rcell5 = row.createCell(5);
				rcell5.setCellValue(candidate.getRecruiterName());
				rcell5.setCellStyle(rstyle);
				
				Cell rcell6 = row.createCell(6);
				rcell6.setCellValue("Yes");
				rcell6.setCellStyle(rstyle);
				
				Cell rcell7 = row.createCell(7);
				rcell7.setCellValue(candidate.getInputSubmitDate());
				rcell7.setCellStyle(rstyle);
				
				Cell rcell8 = row.createCell(8);
				rcell8.setCellValue(candidate.getInputSubmitTime());
				rcell8.setCellStyle(rstyle);
				
				Cell rcell9 = row.createCell(9);
				rcell9.setCellValue(workflowStatus);
				rcell9.setCellStyle(rstyle);
				
				Cell rcell10 = row.createCell(10);
				rcell10.setCellValue(workflowStatusDate);
				rcell10.setCellStyle(rstyle);
				
				Cell rcell11 = row.createCell(11);
				rcell11.setCellValue(comments);
				rcell11.setCellStyle(rstyle);
				
				Cell rcell12 = row.createCell(12);
				rcell12.setCellValue(caseInitiatedDate);
				rcell12.setCellStyle(rstyle);
				
				Cell rcell13 = row.createCell(13);
				rcell13.setCellValue(submittedDate);
				rcell13.setCellStyle(rstyle);
				
				Cell rcell14 = row.createCell(14);
				rcell14.setCellValue(reportDeliveredDate);
				rcell14.setCellStyle(rstyle);
				
	            
	            srNum++;
	        }
	        
	     // Convert Workbook to byte array
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        workbook.write(outputStream);
	        workbook.close();
	        
	     // Set response headers
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
	        headers.setContentDispositionFormData("attachment", "Candidate_Status_Tracker.xlsx");

			// Return the byte array as a ResponseEntity
	        return ResponseEntity.ok()
	                .headers(headers)
	                .body(outputStream.toByteArray());
		} catch (IOException e) {
			
		      log.error("fail to downloadCandidateStatusTrackerExcel file:{} ", e.getMessage());
		      return null;
		}
	}
	
	public List<BulkPanToUanDTO> excelToBulkPANToUAN(InputStream inputStream){
		ArrayList<BulkPanToUanDTO> bulkPanToUanDTOList = new ArrayList<>();
		try {
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
			
	    	  XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
	          XSSFSheet worksheet = workbook.getSheetAt(0);
			  for(int i=1;i<worksheet.getPhysicalNumberOfRows() ;i++) {
				  BulkPanToUanDTO bulkPanToUanDTO = new BulkPanToUanDTO();
		            XSSFRow row = worksheet.getRow(i);
				  if(getCellValue(row, 1) != null && !getCellValue(row, 1).equals("")
						  && getCellValue(row, 2) != null && !getCellValue(row, 2).equals("") 
						  && getCellValue(row, 3) != null && !getCellValue(row, 3).equals("")) {
					  String dob = getCellValue(row, 2).trim();
					  String pan = getCellValue(row, 3).trim();
					  String uan =getCellValue(row, 4).trim();
					  String appId ="";
					  if(getCellValue(row, 0) != null && !getCellValue(row, 0).equals("")) {
						  appId= getCellValue(row, 0);
					  }else {
						  SecureRandom secureRnd = new SecureRandom();
                          int n = 100000 + secureRnd.nextInt(900000);
                          appId = String.valueOf(n);
					  }
					  log.info("PAN NUMBER::: {} {}",pan,dob);
					  if(orgServices!=null && orgServices.contains("PANTOUAN") 
							  && dob.contains("/") && pan.length()==10) {
						  bulkPanToUanDTO.setApplicantId(appId.trim());
						  bulkPanToUanDTO.setCandidateName(getCellValue(row, 1).trim());
						  bulkPanToUanDTO.setDob(getCellValue(row, 2).trim());
						  bulkPanToUanDTO.setPan(getCellValue(row, 3).trim());
						  bulkPanToUanDTO.setUan(uan.equals("")?null:uan);
						  bulkPanToUanDTO.setUploadedBy(getUserForUploadedBy);
						  bulkPanToUanDTO.setBulkUanId(bulkUanId);				  
						  
				          UanSearchData uanSave = new UanSearchData(); 
						  uanSave.setApplicantId(appId.trim());
						  uanSave.setPan(getCellValue(row, 3).trim());
						  uanSave.setUan(uan.equals("")?null:uan);	
						  uanSave.setBulkUanId(bulkUanId);
						  uanSave.setEPFOResponse("Search In Progress...");
						  uanSave.setUploadedOn(uploadedOn);
						  uanSave.setUploadedBy(getUserForUploadedBy);
	//					  bulkUanSearchList.add(uanSearchData);
						  
//					 	uanSearchDataRepository.save(uanSave);
					 	//saving candidates info for UAN search organization, who does not have ITR and DL services
//					 	if(orgServices!=null && orgServices.contains("EPFO") && orgServices.contains("DNHDB")
//					 			&& !orgServices.contains("DIGILOCKER") && !orgServices.contains("ITR")) {
					 		
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
							candidate.setCandidateName(getCellValue(row, 1).trim());
							candidate.setContactNumber(getCellValue(row, 3).trim());
							candidate.setEmailId("pantouan@gmail.com");
							candidate.setApplicantId(appId.trim());
							candidate.setDateOfBirth(dob.replace("/", "-"));
							candidate.setPanNumber(getCellValue(row, 3).trim());
							candidate.setUan(uan.equals("")?null:uan);
							candidate.setCreatedOn(new Date());
							candidate.setCreatedBy(findByUserName);
							candidate = candidateRepository.save(candidate);
							
							uanSave.setCandidateId(candidate.getCandidateId());
	 						
							uanSearchDataRepository.save(uanSave);
							
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
							
							bulkPanToUanDTO.setCandidateCode(candidate.getCandidateCode());
//					 	}
					 	
					 	bulkPanToUanDTOList.add(bulkPanToUanDTO);

				     }else {
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
							candidate.setCandidateName(getCellValue(row, 1).trim());
							candidate.setContactNumber(getCellValue(row, 3).trim());
							candidate.setEmailId("pantouan@gmail.com");
							candidate.setApplicantId(appId.trim());
							candidate.setDateOfBirth(dob.replace("/", "-"));
							candidate.setPanNumber(getCellValue(row, 3).trim());
							candidate.setUan(getCellValue(row, 4).trim());
							candidate.setCreatedOn(new Date());
							candidate.setCreatedBy(findByUserName);
							candidate = candidateRepository.save(candidate);
							
							
							CandidateStatus candidateStatus = new CandidateStatus();
							candidateStatus.setCandidate(candidate);
							candidateStatus.setCreatedBy(findByUserName);
							candidateStatus.setCreatedOn(new Date());
							candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("INVALIDUPLOAD"));
							candidateStatus = candidateStatusRepository.save(candidateStatus);
							candidateService.createCandidateStatusHistory(candidateStatus, "NOTCANDIDATE");
				    	 
				     }
				  }

			  }

			  return bulkPanToUanDTOList;
		} catch (IOException e) {
		      log.info("fail to parse Excel file: " + e.getMessage());
		}
		return bulkPanToUanDTOList;	
	}
	
	public ResponseEntity<byte[]> downloadCandidateEmploymentReportExcel(List<UanSearchData> uanSearchFilterData,
			List<Candidate> successCandidates, List<EpfoData> successCandidatesEpfoList) throws IOException {
		try {
			// Create Excel Workbook
	        Workbook workbook = new XSSFWorkbook();
	        Sheet sheet = workbook.createSheet("Success_Response");
	        Sheet failResponseSheet = workbook.createSheet("Fail_Response");
	        
	     // Create header row
	        Row headerRow = sheet.createRow(0);
	     // Create header row for failes
	        Row failheaderRow = failResponseSheet.createRow(0);
	     // Set font style to bold
	        Font font = workbook.createFont();
	        font.setBold(true);
	        font.setFontHeightInPoints((short) 12);

	        CellStyle style = workbook.createCellStyle();
	        style.setFont(font);
	        
	        Cell failcell0 =failheaderRow.createCell(0);
	        failcell0.setCellValue("Sl_NO");
	        failcell0.setCellStyle(style);
	        failResponseSheet.setColumnWidth(0, 4000);
	        Cell failcell1 =failheaderRow.createCell(1);
	        failcell1.setCellValue("App_ID");
	        failcell1.setCellStyle(style);
	        failResponseSheet.setColumnWidth(1, 4000);
	        Cell failcell2 =failheaderRow.createCell(2);
	        failcell2.setCellValue("PAN");
	        failcell2.setCellStyle(style);
	        failResponseSheet.setColumnWidth(2, 5000);
	        Cell failcell3 =failheaderRow.createCell(3);
	        failcell3.setCellValue("FAIL_RESPONSE");
	        failcell3.setCellStyle(style);
	        failResponseSheet.setColumnWidth(3, 8000);
	        
	        Cell cell0 =headerRow.createCell(0);
	        cell0.setCellValue("Sl_NO");
	        cell0.setCellStyle(style);
	        sheet.setColumnWidth(0, 4000);
	        Cell cell1 =headerRow.createCell(1);
	        cell1.setCellValue("App_ID");
	        cell1.setCellStyle(style);
	        sheet.setColumnWidth(1, 4000);
	        Cell cell2 =headerRow.createCell(2);
	        cell2.setCellValue("UAN");
	        cell2.setCellStyle(style);
	        sheet.setColumnWidth(2, 5000);
	        Cell cell3 =headerRow.createCell(3);
	        cell3.setCellValue("NAME");
	        cell3.setCellStyle(style);
	        sheet.setColumnWidth(3, 8000);
	        
	        
	     // Create data rows
	        int rowNum = 1;
	        int srNum = 1;
	        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	        for (Candidate candidate : successCandidates) {
	        	
	        	List<EpfoData> epfoDataList = successCandidatesEpfoList.stream()
							                .filter(data -> data.getCandidate().getCandidateId().equals(candidate.getCandidateId()))
							                .collect(Collectors.toList());
	        	String uan = epfoDataList.get(0).getUan();
	        	String candidateName = epfoDataList.get(0).getName();
	        	log.info("epfoDataList for candidate::{}:{}",candidate.getCandidateCode(),epfoDataList.size());
	        	Font rfont = workbook.createFont();
				 rfont.setFontHeightInPoints((short) 12);
                CellStyle rstyle = workbook.createCellStyle();
                rstyle.setFont(rfont);
			     
				Row row = sheet.createRow(rowNum++);
				Cell rcell0 = row.createCell(0);
				rcell0.setCellValue(srNum);
				rcell0.setCellStyle(rstyle);
				
				Cell rcell1 = row.createCell(1);
				rcell1.setCellValue(candidate.getApplicantId());
				rcell1.setCellStyle(rstyle);
				
				Cell rcell2 = row.createCell(2);
				rcell2.setCellValue(uan);
				rcell2.setCellStyle(rstyle);
				
				Cell rcell3 = row.createCell(3);
				rcell3.setCellValue(candidateName);
				rcell3.setCellStyle(rstyle);
	        	
				
				//checking the employment to be verified for this candidate
				ToleranceConfig toleranceConfigObj= toleranceConfigRepository.findByOrganizationOrganizationId(candidate.getOrganization().getOrganizationId());
				 // Create headers dynamically based on the number of epfoDataLists
			    for (int i = 0; i < epfoDataList.size(); i++) {
			    	if(toleranceConfigObj!=null && toleranceConfigObj.getNumberOfEmployment()!=null && i< toleranceConfigObj.getNumberOfEmployment()) {
				        Cell headerCell4 = sheet.getRow(0).getCell(4 + i * 3); // Increment column index by 3 for each new epfoDataList
				        if (headerCell4 == null) {
				            headerCell4 = sheet.getRow(0).createCell(4 + i * 3);
				            headerCell4.setCellValue("COMPANY_" + (i + 1));
				            headerCell4.setCellStyle(style);
				            sheet.setColumnWidth(4 + i * 3, 15000);
				        }
	
				        Cell headerCell5 = sheet.getRow(0).getCell(5 + i * 3); // Increment column index by 3 for each new epfoDataList
				        if (headerCell5 == null) {
				            headerCell5 = sheet.getRow(0).createCell(5 + i * 3);
				            headerCell5.setCellValue("DOJ_" + (i + 1));
				            headerCell5.setCellStyle(style);
				            sheet.setColumnWidth(5 + i * 3, 5000);
				        }
	
				        Cell headerCell6 = sheet.getRow(0).getCell(6 + i * 3); // Increment column index by 3 for each new epfoDataList
				        if (headerCell6 == null) {
				            headerCell6 = sheet.getRow(0).createCell(6 + i * 3);
				            headerCell6.setCellValue("DOE_" + (i + 1));
				            headerCell6.setCellStyle(style);
				            sheet.setColumnWidth(6 + i * 3, 4000);
				        }
	
				        // Populate EpfoData
				        EpfoData epfoData = epfoDataList.get(i);
				        Cell rcell4 = row.createCell(4 + i * 3); // Increment column index by 3 for each new epfoDataList
				        rcell4.setCellValue(epfoData.getCompany());
				        rcell4.setCellStyle(rstyle);
	
				        String doj="NOT_AVAILABLE";
				        if(epfoData.getDoj()!=null) {
								doj = dateFormat.format(epfoData.getDoj());
								Cell rcell5 = row.createCell(5 + i * 3); // Increment column index by 3 for each new epfoDataList
						        rcell5.setCellValue(doj);
						        rcell5.setCellStyle(rstyle);
							
				        }else {
				        	Cell rcell5 = row.createCell(5 + i * 3); // Increment column index by 3 for each new epfoDataList
					        rcell5.setCellValue(doj);
					        rcell5.setCellStyle(rstyle);
				        }
				        String doe="NOT_AVAILABLE";
				        if(epfoData.getDoe()!=null) {
				        		doe = dateFormat.format(epfoData.getDoe());
								Cell rcell6 = row.createCell(6 + i * 3); // Increment column index by 3 for each new epfoDataList
						        rcell6.setCellValue(doe);
						        rcell6.setCellStyle(rstyle);
							
				        }else {
				        	Cell rcell6 = row.createCell(6 + i * 3); // Increment column index by 3 for each new epfoDataList
					        rcell6.setCellValue(doe);
					        rcell6.setCellStyle(rstyle);
				        }

			    	} 
			    }
				
	            srNum++;
	        }
	        
	     // Create data rows
	        int failRowNum = 1;
	        int failSrNum = 1;
            for (UanSearchData uanSearchData : uanSearchFilterData) {
                		
				 Font rfont = workbook.createFont();
				 rfont.setFontHeightInPoints((short) 12);
                 CellStyle rstyle = workbook.createCellStyle();
                 rstyle.setFont(rfont);
			     
				Row row = failResponseSheet.createRow(failRowNum++);
				Cell rcell0 = row.createCell(0);
				rcell0.setCellValue(failSrNum);
				rcell0.setCellStyle(rstyle);
				
				Cell rcell1 = row.createCell(1);
				rcell1.setCellValue(uanSearchData.getApplicantId());
				rcell1.setCellStyle(rstyle);
				
				Cell rcell2 = row.createCell(2);
				rcell2.setCellValue(uanSearchData.getPan());
				rcell2.setCellStyle(rstyle);
				
				Cell rcell3 = row.createCell(3);
				rcell3.setCellValue(uanSearchData.getEPFOResponse());
				rcell3.setCellStyle(rstyle);
				
				failSrNum++;
	        }
	        
	     // Convert Workbook to byte array
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        workbook.write(outputStream);
	        workbook.close();
	        
	     // Set response headers
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
	        headers.setContentDispositionFormData("attachment", "Candidate_Emplyment_Report.xlsx");

			// Return the byte array as a ResponseEntity
	        return ResponseEntity.ok()
	                .headers(headers)
	                .body(outputStream.toByteArray());
		} catch (IOException e) {
			
		      log.error("fail to downloadCandidateEmploymentReportExcel file:{} ", e.getMessage());
		      return null;
		}
	}

	public  List<Candidate> excelToConventionalCandidate(InputStream is,String filename,String yearsToBeVerified) {

	        try {
	        	log.info("FILENAME::>>>"+filename);
	        	  if(yearsToBeVerified == null)
	        		  yearsToBeVerified = "7";
	              ArrayList<Candidate> candidateList = new ArrayList<Candidate>();
	              XSSFWorkbook workbook = new XSSFWorkbook(is);
	              XSSFSheet worksheet = workbook.getSheetAt(0);
	              Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	              Object principal = authentication.getPrincipal();
	              log.info("principal:: {}",principal.toString());
	              String username = "";
	                  username = ((UserDetails) principal).getUsername();
	                  log.info("username 2::?? {}",username);
	                  User findByUserName = userRepository.findByUserName(username);
	                  log.info("ORgID::{}",findByUserName.getOrganization().getOrganizationName());	                  
	                  log.info(principal.toString());
	                  try {
			                  for(int i=1;i<worksheet.getPhysicalNumberOfRows() ;i++) {
			                      Candidate candidate = new Candidate();
			                      XSSFRow row = worksheet.getRow(i);
			                        XSSFRow header = worksheet.getRow(0);
			                        log.info("xls heading row {}", header.getLastCellNum());
			                        if (getCellValue(row, 0) != null && !getCellValue(row, 0).equals("") &&
				                    	    getCellValue(row, 1) != null && !getCellValue(row, 1).equals("")){
			                            candidate.setCandidateName(getCellValue(row, 0));
			                            candidate.setContactNumber(getCellValue(row, 1).trim());
				                        candidate.setEmailId(getCellValue(row, 2).trim());
			                            SecureRandom secureRnd = new SecureRandom();
			                            int n = 100000 + secureRnd.nextInt(900000);

			                            if(!getCellValue(row, 3).equals("")) {

			                            	candidate.setApplicantId(getCellValue(row, 3));

			                            } else {

			                            	candidate.setApplicantId(String.valueOf(n));

			                            }
			                            candidate.setCcEmailId(getCellValue(row, 4));
		                                candidate.setAccountName(getCellValue(row, 5).trim());
		                                candidate.setExperienceInMonth(Float.valueOf(yearsToBeVerified));
		                                candidate.setConventionalCandidate(true);
//		                                candidate.setConventionalStatusId(20);
			                			if (emailRateLimiter.tryAcquire(candidate.getEmailId())) {
				                            candidateList.add(candidate);
			                			} else {

			                				log.info("Rate limit exceeded for email: "+ candidate.getEmailId());

			                			}
			                        }

			                    }

						} catch (Exception e) {

							log.info("ExcelUtils:::"+e.getMessage());

						}

	              return candidateList;

	            }

	              catch (IOException e) {

	              throw new RuntimeException("fail to parse Excel file: " + e.getMessage());

	            }

	  }

	
}
